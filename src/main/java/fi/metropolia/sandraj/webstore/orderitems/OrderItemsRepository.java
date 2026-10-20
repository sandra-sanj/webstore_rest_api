package fi.metropolia.sandraj.webstore.orderitems;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderItemsRepository extends JpaRepository<OrderItems, OrderItemsId> {
    List<OrderItems> findByOrder_Id(int orderId);

    Optional<OrderItems> findByOrder_IdAndProductId(int orderId, int productId);
}
