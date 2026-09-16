package fi.metropolia.sandraj.webstore.customers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
@RequestMapping("/customers")
public class CustomersController {
    private final CustomersRepository repository;

    public CustomersController(CustomersRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customers> getCustomerById(@PathVariable Integer id) {
        return this.repository.findById(id)
        .map(customer -> ResponseEntity.ok(customer))
        .orElse(ResponseEntity.notFound().build());
    }
}
