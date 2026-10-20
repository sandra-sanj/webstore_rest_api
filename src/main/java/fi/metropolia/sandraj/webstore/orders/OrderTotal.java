package fi.metropolia.sandraj.webstore.orders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface OrderTotal {
    int getOrderId();

    LocalDateTime getOrderDate();

    Orders.Status getStatus();

    BigDecimal getTotal();
}
