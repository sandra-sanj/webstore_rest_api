package fi.metropolia.sandraj.webstore.orderitems;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.metropolia.sandraj.webstore.orders.Orders;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;

@Entity
@Table(name = "orderitems")
@IdClass(OrderItemsId.class)
@DynamicUpdate
public class OrderItems {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Orders order;

    @Id
    @Column(name = "product_id", nullable = false)
    private int productId;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    public OrderItems() {}

    public Orders order() {
        return order;
    }

    public void setOrder(Orders order) {
        this.order = order;
    }

    public Integer getOrderId() {
        return order != null ? order.getId() : null;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
