package fi.metropolia.sandraj.webstore.customers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Customers> createCustomer(@RequestBody Customers customer) {
        Customers saved = this.repository.save(customer);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customers> updateCustomer(@PathVariable Integer id, @RequestBody Customers update) {
        return this.repository.findById(id)
                .map(customer -> {
                    customer.setFirstName(update.getFirstName());
                    customer.setLastName(update.getLastName());
                    customer.setEmail(update.getEmail());
                    customer.setPhone(update.getPhone());
                    return ResponseEntity.ok(this.repository.save(customer));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/company")
    public ResponseEntity<CompanyCustomers> createCompanyCustomer(@RequestBody CompanyCustomers customer) {
        return ResponseEntity.status(201).body(this.repository.save(customer));
    }

    @PutMapping("/company/{id}")
    public ResponseEntity<CompanyCustomers> updateCompanyCustomer(
            @PathVariable Integer id, @RequestBody CompanyCustomers update)
    {
        return this.repository.findById(id)
                .filter(customer -> customer instanceof CompanyCustomers)
                .map(customer -> {
                    CompanyCustomers company = (CompanyCustomers) customer;
                    company.setFirstName(update.getFirstName());
                    company.setLastName(update.getLastName());
                    company.setEmail(update.getEmail());
                    company.setPhone(update.getPhone());
                    company.setCompanyName(update.getCompanyName());
                    company.setCompanyId(update.getCompanyId());
                    return ResponseEntity.ok(this.repository.save(company));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
