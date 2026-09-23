package fi.metropolia.sandraj.webstore.orders;

import fi.metropolia.sandraj.webstore.customeraddresses.CustomerAddresses;
import fi.metropolia.sandraj.webstore.customers.Customers;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customers customer;

    @CreationTimestamp
    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_address_id")
    private CustomerAddresses shippingAddress;

    @Column(length = 50)
    private String status = "NEW";

    public Orders () {}

    public Orders(Customers customer, CustomerAddresses shippingAddress) {
        this.customer = customer;
        this.shippingAddress = shippingAddress;
    }

    public int getId() {
        return id;
    }

    public Customers customer() {
        return customer;
    }

    public void setCustomer(Customers customer) {
        this.customer = customer;
    }

    public Integer getCustomerId() {
        return customer != null ? customer.getId() : null;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public CustomerAddresses getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(CustomerAddresses shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
