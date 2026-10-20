package fi.metropolia.sandraj.webstore.orderitems;

import fi.metropolia.sandraj.webstore.orders.Orders;
import fi.metropolia.sandraj.webstore.orders.OrdersRepository;
import fi.metropolia.sandraj.webstore.products.ProductsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class OrderItemsController {
    private final OrderItemsRepository repository;
    private final OrdersRepository ordersRepository;
    private final ProductsRepository productsRepository;

    public OrderItemsController(
            OrderItemsRepository repository, OrdersRepository ordersRepository,
            ProductsRepository productsRepository
    ) {
        this.repository = repository;
        this.ordersRepository = ordersRepository;
        this.productsRepository = productsRepository;
    }

    @GetMapping("/orders/{orderId}/items")
    public ResponseEntity<List<OrderItems>> getItemsForOrder(@PathVariable Integer orderId) {
        if (!ordersRepository.existsById(orderId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(repository.findByOrder_Id(orderId));
    }

    @PutMapping("/orders/{orderId}/items/{productId}")
    @Transactional
    public ResponseEntity<OrderItems> updateQuantity(
            @PathVariable Integer orderId, @PathVariable Integer productId, @RequestBody OrderItems update)
    {
        if (update.getQuantity() <= 0) {
            return ResponseEntity.badRequest().build();
        }

        Optional<OrderItems> item = repository.findByOrder_IdAndProductId(orderId, productId);
        if (item.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (item.get().order().getStatus() != Orders.Status.NEW) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        int change = update.getQuantity() - item.get().getQuantity();
        if (change != 0 && productsRepository.decreaseCurrentStock(productId, change) == 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        item.get().setQuantity(update.getQuantity());
        return ResponseEntity.ok(repository.save(item.get()));
    }
}
