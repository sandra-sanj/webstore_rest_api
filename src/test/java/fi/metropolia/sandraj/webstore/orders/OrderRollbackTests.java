package fi.metropolia.sandraj.webstore.orders;

import fi.metropolia.sandraj.webstore.products.ProductsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderRollbackTests {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private MockMvc mockMvc;

    private int stock(int productId) {
        return productsRepository.findCurrentById(productId).orElseThrow().getStockQuantity();
    }

    @Test
    void lineWithoutEnoughStockRollsBackTheWholeOrder() throws Exception {
        int stockBefore = stock(2);
        int ordersBefore = ordersRepository.findByCustomer_Id(1).size();

        mockMvc.perform(post("/customers/1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [{"productId":2,"quantity":1},{"productId":3,"quantity":100000000}]
                                """))
                .andExpect(status().isConflict());

        assertEquals(stockBefore, stock(2));
        assertEquals(ordersBefore, ordersRepository.findByCustomer_Id(1).size());
    }
}
