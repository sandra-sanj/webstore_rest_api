package fi.metropolia.sandraj.webstore.products;

import fi.metropolia.sandraj.webstore.suppliers.Suppliers;
import fi.metropolia.sandraj.webstore.suppliers.SuppliersRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/products")
public class ProductsController {
    private final ProductsRepository repository;
    private final SuppliersRepository suppliersRepository;

    public ProductsController(ProductsRepository repository, SuppliersRepository suppliersRepository) {
        this.repository = repository;
        this.suppliersRepository = suppliersRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Products> getProductById(@PathVariable Integer id) {
        return repository.findCurrentById(id)
                .map(product -> ResponseEntity.ok(product))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/suppliers")
    public ResponseEntity<Set<Suppliers>> getSuppliersForProduct(@PathVariable Integer id) {
        return repository.findCurrentById(id)
                .map(product -> ResponseEntity.ok(product.getSuppliers()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{productId}/suppliers/{supplierId}")
    public ResponseEntity<Products> addSupplier(@PathVariable Integer productId, @PathVariable Integer supplierId) {
        Optional<Products> product = repository.findCurrentById(productId);
        Optional<Suppliers> supplier = suppliersRepository.findById(supplierId);
        if (product.isEmpty() || supplier.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        product.get().addSupplier(supplier.get());
        return ResponseEntity.ok(repository.save(product.get()));
    }

    @DeleteMapping("/{productId}/suppliers/{supplierId}")
    public ResponseEntity<Products> removeSupplier(@PathVariable Integer productId, @PathVariable Integer supplierId) {
        return repository.findCurrentById(productId)
                .map(product -> {
                    product.removeSupplier(supplierId);
                    return ResponseEntity.ok(repository.save(product));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
