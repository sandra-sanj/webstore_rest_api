package fi.metropolia.sandraj.webstore.suppliers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.metropolia.sandraj.webstore.products.Products;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "suppliers")
public class Suppliers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 255, nullable = false)
    private String name;

    @ManyToMany(mappedBy = "suppliers")
    @SQLRestriction("startdate <= CURDATE() AND enddate > CURDATE()")
    @JsonIgnore
    private Set<Products> products = new HashSet<>();

    public Suppliers() {
    }

    public Suppliers(String name) {
        this.name = name;
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

    public Set<Products> getProducts() {
        return products;
    }
}
