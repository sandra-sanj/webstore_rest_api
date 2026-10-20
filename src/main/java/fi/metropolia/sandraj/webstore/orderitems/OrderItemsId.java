package fi.metropolia.sandraj.webstore.orderitems;

import java.io.Serializable;
import java.util.Objects;

public class OrderItemsId implements Serializable {

    private int order;
    private int productId;

    public OrderItemsId() {}

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof OrderItemsId other)) {
            return false;
        }
        return order == other.order && productId == other.productId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, productId);
    }
}
