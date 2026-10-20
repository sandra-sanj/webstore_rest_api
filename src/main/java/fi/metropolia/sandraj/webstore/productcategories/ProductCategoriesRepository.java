package fi.metropolia.sandraj.webstore.productcategories;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoriesRepository extends JpaRepository<ProductCategories, Integer> {
}
