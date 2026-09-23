package fi.metropolia.sandraj.webstore.customeraddresses;

import fi.metropolia.sandraj.webstore.customers.Customers;
import jakarta.persistence.*;

@Entity
@Table(name = "customeraddresses")
public class CustomerAddresses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customers customer;

    @Column(name = "street_address", length = 255, nullable = false)
    private String streetAddress;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(length = 100, nullable = false)
    private String city;

    @Column(length = 100)
    private String country;

    public CustomerAddresses() {}

    public CustomerAddresses(Customers customer, String streetAddress, String postalCode, String city, String country) {
        this.customer = customer;
        this.streetAddress = streetAddress;
        this.postalCode = postalCode;
        this.city = city;
        this.country = country;
    }

    public int getId() {
        return id;
    }

    public Customers getCustomer() {
        return customer;
    }

    public void setCustomer(Customers customer) {
        this.customer = customer;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
