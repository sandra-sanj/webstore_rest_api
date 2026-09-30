package fi.metropolia.sandraj.webstore.products;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProductsRepository extends JpaRepository<Products, Long> {
    @Query("""
            SELECT p FROM Products p
            WHERE p.id = :id AND p.startdate <= CURRENT_DATE AND p.enddate > CURRENT_DATE
            """)
    Optional<Products> findCurrentById(int id);

    @Query("""
            SELECT p FROM Products p JOIN p.suppliers s
            WHERE s.id = :supplierId AND p.startdate <= CURRENT_DATE AND p.enddate > CURRENT_DATE
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
