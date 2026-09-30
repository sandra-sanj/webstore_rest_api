package fi.metropolia.sandraj.webstore.suppliers;

import fi.metropolia.sandraj.webstore.products.Products;
import fi.metropolia.sandraj.webstore.products.ProductsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SuppliersController {
    private final SuppliersRepository repository;
    private final ProductsRepository productsRepository;

    public SuppliersController(SuppliersRepository repository, ProductsRepository productsRepository) {
        this.repository = repository;
        this.productsRepository = productsRepository;
    }

    @GetMapping("/suppliers/{id}/products")
    public ResponseEntity<List<Products>> getProductsForSupplier(@PathVariable Integer id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productsRepository.findCurrentBySupplierId(id));
    }
}
