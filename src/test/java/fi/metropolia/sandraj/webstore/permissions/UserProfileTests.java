package fi.metropolia.sandraj.webstore.permissions;

import fi.metropolia.sandraj.webstore.customeraddresses.CustomerAddresses;
import fi.metropolia.sandraj.webstore.customeraddresses.CustomerAddressesRepository;
import fi.metropolia.sandraj.webstore.customers.Customers;
import fi.metropolia.sandraj.webstore.customers.CustomersRepository;
import fi.metropolia.sandraj.webstore.orderitems.OrderItems;
import fi.metropolia.sandraj.webstore.orderitems.OrderItemsRepository;
import fi.metropolia.sandraj.webstore.orders.Orders;
import fi.metropolia.sandraj.webstore.orders.OrdersRepository;
import fi.metropolia.sandraj.webstore.products.ProductsRepository;
import fi.metropolia.sandraj.webstore.suppliers.SuppliersRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserProfileTests {

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private CustomerAddressesRepository addressesRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrderItemsRepository orderItemsRepository;

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private SuppliersRepository suppliersRepository;

    private void assertDenied(Executable action) {
        DataAccessException thrown = assertThrows(DataAccessException.class, () -> {
            action.execute();
            customersRepository.flush();
        });
        assertTrue(thrown.getMostSpecificCause().getMessage().contains("denied"), thrown.getMessage());
    }

    private Customers newCustomer() {
        return customersRepository.saveAndFlush(
                new Customers("Test", "Tester", "test@test.com", "0123456789"));
    }

    @Nested
    class CustomerTests {
        @Test
        void cannotDelete() {
            Customers customer = newCustomer();
            assertDenied(() -> customersRepository.delete(customer));
        }
    }

    @Nested
    class CustomerAddressTests {
        @Test
        void cannotDelete() {
            CustomerAddresses address = addressesRepository.saveAndFlush(
                    new CustomerAddresses(newCustomer(), "Testikatu 1", "00100", "Helsinki", "Finland"));
            assertDenied(() -> addressesRepository.delete(address));
        }
    }

    @Nested
    class OrderTests {
        @Test
        void cannotChangeStatus() {
            Orders order = ordersRepository.findById(1).orElseThrow();
            Orders.Status other = order.getStatus() == Orders.Status.CANCELLED
                    ? Orders.Status.NEW : Orders.Status.CANCELLED;
            assertDenied(() -> order.setStatus(other));
        }
    }

    @Nested
    class OrderItemTests {
        @Test
        void canChangeQuantity() {
            OrderItems item = orderItemsRepository.findByOrder_Id(1).get(0);
            item.setQuantity(item.getQuantity() + 1);
            assertDoesNotThrow(() -> orderItemsRepository.flush());
        }

        @Test
        void cannotDelete() {
            OrderItems item = orderItemsRepository.findByOrder_Id(1).get(0);
            assertDenied(() -> orderItemsRepository.delete(item));
        }
    }

    @Nested
    class ProductTests {
        @Test
        void cannotChangePrice() {
            assertDenied(() -> productsRepository.updateCurrentPrice(2, new BigDecimal("1.00")));
        }
    }

    @Nested
    class SupplierTests {
        @Test
        void cannotReadSupplier() {
            assertDenied(() -> suppliersRepository.findById(1));
        }

        @Test
        void cannotListSupplierProducts() {
            assertDenied(() -> productsRepository.findCurrentBySupplierId(1));
        }
    }
}
