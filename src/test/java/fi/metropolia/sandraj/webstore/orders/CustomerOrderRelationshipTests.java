package fi.metropolia.sandraj.webstore.orders;

import fi.metropolia.sandraj.webstore.customers.Customers;
import fi.metropolia.sandraj.webstore.customers.CustomersRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CustomerOrderRelationshipTests {

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void savingCustomerCascadesToNewOrder() {
        Customers customer = new Customers("Test", "Tester", "test@test.com", "0123456789");
        Orders order = new Orders(customer, null);
        customer.addOrders(order);
        customersRepository.saveAndFlush(customer);

        assertTrue(ordersRepository.findById(order.getId()).isPresent());
    }

    @Test
    void deletingCustomerWithOrdersFailsInsteadOfDeletingTheirOrders() {
        Customers customer = new Customers("Test", "Tester", "test@test.com", "0123456789");
        customer.addOrders(new Orders(customer, null));
        customersRepository.saveAndFlush(customer);

        DataAccessException thrown = assertThrows(DataAccessException.class, () -> {
            customersRepository.delete(customer);
            customersRepository.flush();
        });

        System.out.println(thrown);
    }

    @Test
    void ordersAreLoadedLazilyOnlyWhenAsked() {
        Customers customer = new Customers("Test", "Tester", "test@test.com", "0123456789");
        customer.addOrders(new Orders(customer, null));
        customersRepository.saveAndFlush(customer);
        Integer customerId = customer.getId();

        entityManager.clear();

        Customers reloaded = customersRepository.findById(customerId).orElseThrow();

        List<Orders> orders = reloaded.orders();

        assertEquals(1, orders.size());
    }
}
