package fi.metropolia.sandraj.webstore.orders;

import fi.metropolia.sandraj.webstore.customers.CustomersRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrdersController {
    private final OrdersRepository repository;
    private final CustomersRepository customersRepository;

    public OrdersController(OrdersRepository repository, CustomersRepository customersRepository) {
        this.repository = repository;
        this.customersRepository = customersRepository;
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Orders> getOrderById(@PathVariable Integer id) {
        return this.repository.findById(id)
                .map(order -> ResponseEntity.ok(order))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/customers/{customerId}/orders")
    public ResponseEntity<Orders> createOrder(
            @PathVariable Integer customerId, @RequestBody Orders order)
    {
        return customersRepository.findById(customerId)
                .map(customer -> {
                    order.setCustomer(customer);
                    Orders saved = repository.save(order);
                    return ResponseEntity.status(HttpStatus.CREATED).body(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customers/{customerId}/orders")
    public ResponseEntity<List<Orders>> getOrdersForCustomer(@PathVariable Integer customerId) {
        if (!customersRepository.existsById(customerId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(repository.findByCustomer_Id(customerId));
    }
}
