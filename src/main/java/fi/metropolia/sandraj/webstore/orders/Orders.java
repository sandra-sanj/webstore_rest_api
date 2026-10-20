package fi.metropolia.sandraj.webstore.orders;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.metropolia.sandraj.webstore.customeraddresses.CustomerAddresses;
import fi.metropolia.sandraj.webstore.customers.Customers;
import fi.metropolia.sandraj.webstore.orderitems.OrderItems;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@DynamicUpdate
public class Orders {

    public enum Status { NEW, SHIPPED, CANCELLED }

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
    @JsonIgnore
    private CustomerAddresses shippingAddress;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Status status = Status.NEW;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<OrderItems> items = new ArrayList<>();

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

    public Integer getShippingAddressId() {
        return shippingAddress != null ? shippingAddress.getId() : null;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<OrderItems> items() {
        return items;
    }

    public void addItem(OrderItems item) {
        this.items.add(item);
        item.setOrder(this);
    }
}
