package fi.metropolia.sandraj.webstore.permissions;

import fi.metropolia.sandraj.webstore.customeraddresses.CustomerAddresses;
import fi.metropolia.sandraj.webstore.customeraddresses.CustomerAddressesRepository;
import fi.metropolia.sandraj.webstore.customers.CompanyCustomers;
import fi.metropolia.sandraj.webstore.customers.Customers;
import fi.metropolia.sandraj.webstore.customers.CustomersRepository;
import fi.metropolia.sandraj.webstore.orders.Orders;
import fi.metropolia.sandraj.webstore.orders.OrdersRepository;
import fi.metropolia.sandraj.webstore.products.Products;
import fi.metropolia.sandraj.webstore.products.ProductsRepository;
import fi.metropolia.sandraj.webstore.suppliers.Suppliers;
import fi.metropolia.sandraj.webstore.suppliers.SuppliersRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("employee")
@Transactional
class EmployeeProfileTests {

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

    private int countRows(String sql, Object... parameters) {
        var query = entityManager.createNativeQuery(sql);
        for (int i = 0; i < parameters.length; i++) {
            query.setParameter(i + 1, parameters[i]);
        }
        return ((Number) query.getSingleResult()).intValue();
    }

    @Nested
    class CustomerTests {
        @Test
        void canRead() {
            assertTrue(customersRepository.findById(1).isPresent());
        }

        @Test
        void cannotCreate() {
            assertDenied(() -> customersRepository.save(
                    new Customers("Test", "Tester", "test@test.com", "0123456789")));
        }
    }

    @Nested
    class CompanyCustomerTests {
        @Test
        void canRead() {
            assertDoesNotThrow(() -> entityManager
                    .createQuery("SELECT c FROM CompanyCustomers c", CompanyCustomers.class)
                    .setMaxResults(1)
                    .getResultList());
        }

        @Test
        void cannotCreate() {
            assertDenied(() -> customersRepository.save(new CompanyCustomers(
                    "Test", "Tester", "test@test.com", "0123456789", "Test Oy", "1234567-8")));
        }
    }

    @Nested
    class CustomerAddressTests {
        @Test
        void canRead() {
            assertTrue(addressesRepository.findById(1).isPresent());
        }

        @Test
        void cannotCreate() {
            Customers customer = customersRepository.findById(1).orElseThrow();
            assertDenied(() -> addressesRepository.save(
                    new CustomerAddresses(customer, "Testikatu 1", "00100", "Helsinki", "Finland")));
        }
    }

    @Nested
    class OrderTests {
        @Test
        void canChangeStatus() {
            Orders order = ordersRepository.findById(1).orElseThrow();
            order.setStatus("CHANGED_BY_TEST");
            ordersRepository.flush();
            entityManager.clear();

            assertEquals("CHANGED_BY_TEST", ordersRepository.findById(1).orElseThrow().getStatus());
        }

        @Test
        void cannotDelete() {
            Orders order = ordersRepository.findById(1).orElseThrow();
            assertDenied(() -> ordersRepository.delete(order));
        }
    }

    @Nested
    class ProductTests {
        @Test
        void canChangePriceAndOldPriceStaysInHistory() {
            BigDecimal oldPrice = productsRepository.findCurrentById(2).orElseThrow().getPrice();
            BigDecimal newPrice = oldPrice.add(BigDecimal.ONE);

            assertEquals(1, productsRepository.updateCurrentPrice(2, newPrice));

            assertEquals(newPrice, productsRepository.findCurrentById(2).orElseThrow().getPrice());
            assertEquals(1, countRows(
                    "SELECT COUNT(*) FROM products WHERE id = 2 AND price = ?1 AND enddate = CURDATE()",
                    oldPrice));
        }

        @Test
        void cannotDelete() {
            Products product = productsRepository.findCurrentById(2).orElseThrow();
            assertDenied(() -> productsRepository.delete(product));
        }
    }

    @Nested
    class SupplierTests {
        @Test
        void canAddLinkToProduct() {
            Suppliers supplier = suppliersRepository.saveAndFlush(new Suppliers("Test Supplier"));
            Products product = productsRepository.findCurrentById(2).orElseThrow();

            product.addSupplier(supplier);
            productsRepository.flush();

            assertEquals(1, countRows(
                    "SELECT COUNT(*) FROM productsuppliers WHERE product_id = 2 AND supplier_id = ?1",
                    supplier.getId()));
        }

        @Test
        void cannotDelete() {
            Suppliers supplier = suppliersRepository.saveAndFlush(new Suppliers("Test Supplier"));
            assertDenied(() -> suppliersRepository.delete(supplier));
        }
    }
}
