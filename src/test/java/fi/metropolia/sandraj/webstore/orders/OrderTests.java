package fi.metropolia.sandraj.webstore.orders;

import com.jayway.jsonpath.JsonPath;
import fi.metropolia.sandraj.webstore.customeraddresses.CustomerAddresses;
import fi.metropolia.sandraj.webstore.customeraddresses.CustomerAddressesRepository;
import fi.metropolia.sandraj.webstore.customers.Customers;
import fi.metropolia.sandraj.webstore.customers.CustomersRepository;
import fi.metropolia.sandraj.webstore.products.ProductsRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderTests {

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private CustomerAddressesRepository addressesRepository;

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private MockMvc mockMvc;

    @PersistenceContext
    private EntityManager entityManager;

    private Customers customer;
    private CustomerAddresses address;

    @BeforeEach
    void setUp() {
        customer = newCustomer();
        address = addressesRepository.saveAndFlush(
                new CustomerAddresses(customer, "Testikatu 1", "00100", "Helsinki", "Finland"));
    }

    private Customers newCustomer() {
        return customersRepository.saveAndFlush(
                new Customers("Test", "Tester", "test@test.com", "0123456789"));
    }

    private int stock(int productId) {
        return ((Number) entityManager.createNativeQuery("""
                        SELECT stock_quantity FROM products
                        WHERE id = ?1 AND startdate <= CURDATE() AND enddate > CURDATE()
                        """)
                .setParameter(1, productId)
                .getSingleResult()).intValue();
    }

    private ResultActions placeOrder(int shippingAddressId, String items) throws Exception {
        return mockMvc.perform(post("/customers/" + customer.getId() + "/orders")
                .param("shippingAddressId", String.valueOf(shippingAddressId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(items));
    }

    private int placeOrder(String items) throws Exception {
        String response = placeOrder(address.getId(), items)
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(response, "$.id");
    }

    private ResultActions changeQuantity(int orderId, int productId, int quantity) throws Exception {
        return mockMvc.perform(put("/orders/" + orderId + "/items/" + productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"quantity\":" + quantity + "}"));
    }

    @Test
    void placingOrderSavesLinesAtCurrentPriceAndTakesStock() throws Exception {
        int stockBefore = stock(2);
        BigDecimal price = productsRepository.findCurrentById(2).orElseThrow().getPrice();

        int orderId = placeOrder("""
                [{"productId":2,"quantity":2}]
                """);

        assertEquals(stockBefore - 2, stock(2));
        mockMvc.perform(get("/orders/" + orderId + "/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].orderId").value(orderId))
                .andExpect(jsonPath("$[0].productId").value(2))
                .andExpect(jsonPath("$[0].quantity").value(2))
                .andExpect(jsonPath("$[0].unitPrice").value(price.doubleValue()));
    }

    @Test
    void orderTotalsSumTheLinesOfEachOrder() throws Exception {
        BigDecimal price2 = productsRepository.findCurrentById(2).orElseThrow().getPrice();
        BigDecimal price3 = productsRepository.findCurrentById(3).orElseThrow().getPrice();

        int firstOrderId = placeOrder("""
                [{"productId":2,"quantity":2},{"productId":3,"quantity":1}]
                """);
        int secondOrderId = placeOrder("""
                [{"productId":2,"quantity":1}]
                """);

        mockMvc.perform(get("/customers/" + customer.getId() + "/orders/totals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].orderId").value(firstOrderId))
                .andExpect(jsonPath("$[0].status").value("NEW"))
                .andExpect(jsonPath("$[0].total")
                        .value(price2.multiply(BigDecimal.TWO).add(price3).doubleValue()))
                .andExpect(jsonPath("$[1].orderId").value(secondOrderId))
                .andExpect(jsonPath("$[1].total").value(price2.doubleValue()));
    }

    @Test
    void listCustomerOrders() throws Exception {
        int orderId = placeOrder("""
                [{"productId":2,"quantity":1}]
                """);

        mockMvc.perform(get("/customers/" + customer.getId() + "/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(orderId));
    }

    @Test
    void orderShowsIdsInsteadOfNestedObjects() throws Exception {
        int orderId = placeOrder("""
                [{"productId":2,"quantity":1}]
                """);

        mockMvc.perform(get("/orders/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.customerId").value(customer.getId()))
                .andExpect(jsonPath("$.shippingAddressId").value(address.getId()))
                .andExpect(jsonPath("$.shippingAddress").doesNotExist());
    }

    @Test
    void shippingAddressOfAnotherCustomerIsRejected() throws Exception {
        CustomerAddresses otherAddress = addressesRepository.saveAndFlush(
                new CustomerAddresses(newCustomer(), "Muukatu 2", "00200", "Espoo", "Finland"));

        placeOrder(otherAddress.getId(), """
                [{"productId":2,"quantity":1}]
                """).andExpect(status().isBadRequest());
    }

    @Test
    void sameProductTwiceIsRejected() throws Exception {
        placeOrder(address.getId(), """
                [{"productId":2,"quantity":1},{"productId":2,"quantity":1}]
                """).andExpect(status().isBadRequest());
    }

    @Test
    void changingQuantityMovesStockByTheDifference() throws Exception {
        int stockBefore = stock(2);
        int orderId = placeOrder("""
                [{"productId":2,"quantity":2}]
                """);

        changeQuantity(orderId, 2, 5).andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(5));
        assertEquals(stockBefore - 5, stock(2));

        changeQuantity(orderId, 2, 1).andExpect(status().isOk());
        assertEquals(stockBefore - 1, stock(2));
    }

    @Test
    void quantityOfShippedOrderCannotChange() throws Exception {
        Object[] line = (Object[]) entityManager.createNativeQuery("""
                        SELECT orderitems.order_id, orderitems.product_id FROM orderitems
                        JOIN orders ON orders.id = orderitems.order_id
                        WHERE orders.status = 'SHIPPED' LIMIT 1
                        """)
                .getSingleResult();

        changeQuantity(((Number) line[0]).intValue(), ((Number) line[1]).intValue(), 1)
                .andExpect(status().isConflict());
    }
}
