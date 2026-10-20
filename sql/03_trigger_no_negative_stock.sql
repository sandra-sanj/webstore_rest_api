DELIMITER //

CREATE TRIGGER trigger_prevent_negative_stock_quantity
BEFORE UPDATE ON products
FOR EACH ROW
BEGIN
    IF NEW.stock_quantity < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: Product stock quantity cannot be negative.';
    END IF;
END //

DELIMITER ;
