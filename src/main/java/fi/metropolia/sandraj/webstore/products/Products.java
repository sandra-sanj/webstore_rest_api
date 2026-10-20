package fi.metropolia.sandraj.webstore.products;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.metropolia.sandraj.webstore.productcategories.ProductCategories;
import fi.metropolia.sandraj.webstore.suppliers.Suppliers;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "products")
@DynamicUpdate
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "row_id")
    private Long rowId;

    @Column(nullable = false)
    private int id;

    @Column(length = 255, nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity = 0;

    private LocalDate startdate = LocalDate.now();
    private LocalDate enddate = LocalDate.of(2100, 12, 31);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnore
    private ProductCategories category;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinTable(
            name = "productsuppliers",
            joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "supplier_id"))
    private Set<Suppliers> suppliers = new HashSet<>();

    public Products() {

    }

    public Products(String name, String description, BigDecimal price, int stockQuantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public LocalDate getStartdate() {
        return startdate;
    }

    public LocalDate getEnddate() {
        return enddate;
    }

    public Integer getCategoryId() {
        return category != null ? category.getId() : null;
    }

    public Set<Suppliers> getSuppliers() {
        return suppliers;
    }

    public void addSupplier(Suppliers supplier) {
        suppliers.add(supplier);
    }

    public void removeSupplier(int supplierId) {
        suppliers.removeIf(supplier -> supplier.getId() == supplierId);
    }
}
