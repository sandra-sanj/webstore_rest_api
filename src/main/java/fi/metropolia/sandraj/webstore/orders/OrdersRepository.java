package fi.metropolia.sandraj.webstore.orders;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {
    List<Orders> findByCustomer_Id(int customerId);

    @Query("""
            SELECT order.id AS orderId, order.orderDate AS orderDate, order.status AS status,
                SUM(item.quantity * item.unitPrice) AS total
            FROM Orders order JOIN order.items item
            WHERE order.customer.id = :customerId
            GROUP BY order.id, order.orderDate, order.status
            ORDER BY order.id
            """)
    List<OrderTotal> findTotalsByCustomerId(int customerId);
}
