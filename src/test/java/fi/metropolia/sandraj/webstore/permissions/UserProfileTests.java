package fi.metropolia.sandraj.webstore.permissions;

import fi.metropolia.sandraj.webstore.customeraddresses.CustomerAddresses;
import fi.metropolia.sandraj.webstore.customeraddresses.CustomerAddressesRepository;
import fi.metropolia.sandraj.webstore.customers.CompanyCustomers;
import fi.metropolia.sandraj.webstore.customers.Customers;
import fi.metropolia.sandraj.webstore.customers.CustomersRepository;
import fi.metropolia.sandraj.webstore.orders.Orders;
import fi.metropolia.sandraj.webstore.orders.OrdersRepository;
import fi.metropolia.sandraj.webstore.products.ProductsRepository;
import fi.metropolia.sandraj.webstore.suppliers.SuppliersRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
    private ProductsRepository productsRepository;

    @Autowired
    private SuppliersRepository suppliersRepository;

    @PersistenceContext
    private EntityManager entityManager;

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
        void canCreate() {
            assertTrue(newCustomer().getId() > 0);
        }

        @Test
        void cannotDelete() {
            Customers customer = newCustomer();
            assertDenied(() -> customersRepository.delete(customer));
        }
    }

    @Nested
    class CompanyCustomerTests {
        @Test
        void canCreateAndReadBack() {
            CompanyCustomers company = customersRepository.saveAndFlush(new CompanyCustomers(
                    "Test", "Tester", "test@test.com", "0123456789", "Test Oy", "1234567-8"));
            entityManager.clear();

            Customers loaded = customersRepository.findById(company.getId()).orElseThrow();
            assertInstanceOf(CompanyCustomers.class, loaded);
        }

        @Test
        void cannotDelete() {
            CompanyCustomers company = customersRepository.saveAndFlush(new CompanyCustomers(
                    "Test", "Tester", "test@test.com", "0123456789", "Test Oy", "1234567-8"));
            assertDenied(() -> customersRepository.delete(company));
        }
    }

    @Nested
    class CustomerAddressTests {
        @Test
        void canCreate() {
            CustomerAddresses address = addressesRepository.saveAndFlush(
                    new CustomerAddresses(newCustomer(), "Testikatu 1", "00100", "Helsinki", "Finland"));
            assertTrue(address.getId() > 0);
        }

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
        void canCreate() {
            Orders order = ordersRepository.saveAndFlush(new Orders(newCustomer(), null));
            assertTrue(order.getId() > 0);
        }

        @Test
        void cannotChangeStatus() {
            Orders order = ordersRepository.findById(1).orElseThrow();
            assertDenied(() -> order.setStatus("CANCELLED_BY_TEST"));
        }
    }

    @Nested
    class ProductTests {
        @Test
        void canReadWithoutSupplierAccess() {
            assertTrue(productsRepository.findCurrentById(2).isPresent());
        }

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
