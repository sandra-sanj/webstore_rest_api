SET GLOBAL event_scheduler = ON;

CREATE EVENT cancel_old_invalid_orders
ON SCHEDULE EVERY 1 DAY
DO
    UPDATE orders SET status = 'CANCELLED'
    WHERE status = 'NEW' AND order_date < NOW() - INTERVAL 14 DAY;
