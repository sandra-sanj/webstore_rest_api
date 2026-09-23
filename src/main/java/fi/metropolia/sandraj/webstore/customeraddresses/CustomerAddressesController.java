package fi.metropolia.sandraj.webstore.customeraddresses;

import fi.metropolia.sandraj.webstore.customers.CustomersRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CustomerAddressesController {
    private final CustomerAddressesRepository repository;
    private final CustomersRepository customersRepository;

    public CustomerAddressesController(
            CustomerAddressesRepository repository, CustomersRepository customersRepository
    ) {
        this.repository = repository;
        this.customersRepository = customersRepository;
    }

    @PostMapping("/customers/{customerId}/addresses")
    public ResponseEntity<CustomerAddresses> addAddress(
            @PathVariable Integer customerId, @RequestBody CustomerAddresses address)
    {
        return customersRepository.findById(customerId)
                .map(customer -> {
                    address.setCustomer(customer);
                    CustomerAddresses saved = repository.save(address);
                    return ResponseEntity.status(HttpStatus.CREATED).body(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customers/{customerId}/addresses")
    public ResponseEntity<List<CustomerAddresses>> getAddressesForCustomer(@PathVariable Integer customerId) {
        if (!customersRepository.existsById(customerId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(repository.findByCustomer_Id(customerId));
    }

    @GetMapping("/addresses/{id}")
    public ResponseEntity<CustomerAddresses> getAddressById(@PathVariable Integer id) {
        return repository.findById(id)
                .map(address -> ResponseEntity.ok(address))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/addresses/{id}")
    public ResponseEntity<CustomerAddresses> updateAddress(
            @PathVariable Integer id, @RequestBody CustomerAddresses update)
    {
        return repository.findById(id)
                .map(address -> {
                    address.setStreetAddress(update.getStreetAddress());
                    address.setPostalCode(update.getPostalCode());
                    address.setCity(update.getCity());
                    address.setCountry(update.getCountry());
                    return ResponseEntity.ok(repository.save(address));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Integer id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        try {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
