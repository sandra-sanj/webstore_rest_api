package fi.metropolia.sandraj.webstore.products;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface ProductsRepository extends JpaRepository<Products, Long>, JpaSpecificationExecutor<Products> {
    @Query("""
            SELECT product FROM Products product
            WHERE product.id = :id AND product.startdate <= CURRENT_DATE AND product.enddate > CURRENT_DATE
            """)
    Optional<Products> findCurrentById(int id);

    default List<Products> searchCurrent(String name, Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice,
                                         boolean inStock) {
        return findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.lessThanOrEqualTo(root.get("startdate"), cb.localDate()));
            predicates.add(cb.greaterThan(root.get("enddate"), cb.localDate()));
            if (name != null) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            if (inStock) {
                predicates.add(cb.greaterThan(root.get("stockQuantity"), 0));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        });
    }

    @Query("""
            SELECT product FROM Products product
            WHERE product.category.id = :categoryId AND product.startdate <= CURRENT_DATE AND product.enddate > CURRENT_DATE
            """)
    List<Products> findCurrentByCategoryId(int categoryId);

    @Transactional
    @Modifying
    @Query("""
            UPDATE Products product SET product.stockQuantity = product.stockQuantity - :quantity
            WHERE product.id = :id AND product.startdate <= CURRENT_DATE AND product.enddate > CURRENT_DATE
                AND product.stockQuantity >= :quantity
            """)
    int decreaseCurrentStock(int id, int quantity);

    @Query("""
            SELECT product FROM Products product JOIN product.suppliers supplier
            WHERE supplier.id = :supplierId AND product.startdate <= CURRENT_DATE AND product.enddate > CURRENT_DATE
            """)
    List<Products> findCurrentBySupplierId(int supplierId);

    default int updateCurrentPrice(int id, BigDecimal price) {
        return findCurrentById(id)
                .map(product -> updatePriceFromToday(id, price, product.getEnddate()))
                .orElse(0);
    }

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = """
            UPDATE products FOR PORTION OF valid_time FROM CURDATE() TO :until
            SET price = :price
            WHERE id = :id
            """, nativeQuery = true)
    int updatePriceFromToday(int id, BigDecimal price, LocalDate until);
}
