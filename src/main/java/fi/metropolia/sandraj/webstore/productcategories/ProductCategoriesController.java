package fi.metropolia.sandraj.webstore.productcategories;

import fi.metropolia.sandraj.webstore.products.Products;
import fi.metropolia.sandraj.webstore.products.ProductsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class ProductCategoriesController {
    private final ProductCategoriesRepository repository;
    private final ProductsRepository productsRepository;

    public ProductCategoriesController(
            ProductCategoriesRepository repository, ProductsRepository productsRepository
    ) {
        this.repository = repository;
        this.productsRepository = productsRepository;
    }

    @GetMapping
    public ResponseEntity<List<ProductCategories>> getCategories() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductCategories> getCategoryById(@PathVariable Integer id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<List<Products>> getProductsForCategory(@PathVariable Integer id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productsRepository.findCurrentByCategoryId(id));
    }
}
