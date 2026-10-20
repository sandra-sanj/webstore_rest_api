package fi.metropolia.sandraj.webstore.orders;

import fi.metropolia.sandraj.webstore.customeraddresses.CustomerAddresses;
import fi.metropolia.sandraj.webstore.customeraddresses.CustomerAddressesRepository;
import fi.metropolia.sandraj.webstore.customers.Customers;
import fi.metropolia.sandraj.webstore.customers.CustomersRepository;
import fi.metropolia.sandraj.webstore.orderitems.OrderItems;
import fi.metropolia.sandraj.webstore.products.Products;
import fi.metropolia.sandraj.webstore.products.ProductsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
public class OrdersController {
    private final OrdersRepository repository;
    private final CustomersRepository customersRepository;
    private final CustomerAddressesRepository addressesRepository;
    private final ProductsRepository productsRepository;

    public OrdersController(
            OrdersRepository repository, CustomersRepository customersRepository,
            CustomerAddressesRepository addressesRepository, ProductsRepository productsRepository
    ) {
        this.repository = repository;
        this.customersRepository = customersRepository;
        this.addressesRepository = addressesRepository;
        this.productsRepository = productsRepository;
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Orders> getOrderById(@PathVariable Integer id) {
        return this.repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/customers/{customerId}/orders")
    @Transactional
    public ResponseEntity<Orders> createOrder(
            @PathVariable Integer customerId,
            @RequestParam(required = false) Integer shippingAddressId,
            @RequestBody List<OrderItems> items)
    {
        Optional<Customers> customer = customersRepository.findById(customerId);
        if (customer.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (items.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Set<Integer> productIds = new HashSet<>();
        for (OrderItems item : items) {
            if (item.getQuantity() <= 0 || !productIds.add(item.getProductId())) {
                return ResponseEntity.badRequest().build();
            }
        }

        CustomerAddresses shippingAddress = null;
        if (shippingAddressId != null) {
            shippingAddress = addressesRepository.findById(shippingAddressId)
                    .filter(address -> customerId.equals(address.getCustomerId()))
                    .orElse(null);
            if (shippingAddress == null) {
                return ResponseEntity.badRequest().build();
            }
        }

        Orders order = new Orders(customer.get(), shippingAddress);
        for (OrderItems item : items) {
            Products product = productsRepository.findCurrentById(item.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
            if (productsRepository.decreaseCurrentStock(item.getProductId(), item.getQuantity()) == 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT);
            }
            item.setUnitPrice(product.getPrice());
            order.addItem(item);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(order));
    }

    @GetMapping("/customers/{customerId}/orders")
    public ResponseEntity<List<Orders>> getOrdersForCustomer(@PathVariable Integer customerId) {
        if (!customersRepository.existsById(customerId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(repository.findByCustomer_Id(customerId));
    }

    @GetMapping("/customers/{customerId}/orders/totals")
    public ResponseEntity<List<OrderTotal>> getOrderTotalsForCustomer(@PathVariable Integer customerId) {
        if (!customersRepository.existsById(customerId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(repository.findTotalsByCustomerId(customerId));
    }
}
