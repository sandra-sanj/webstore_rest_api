-- webstore_user permissions
GRANT SELECT, INSERT, UPDATE ON customers TO 'webstore_user'@'localhost';
GRANT SELECT, INSERT, UPDATE ON customeraddresses TO 'webstore_user'@'localhost';
GRANT SELECT, UPDATE (stock_quantity) ON products TO 'webstore_user'@'localhost';
GRANT SELECT ON productcategories TO 'webstore_user'@'localhost';
GRANT SELECT, INSERT ON orders TO 'webstore_user'@'localhost';
GRANT SELECT, INSERT, UPDATE (quantity) ON orderitems TO 'webstore_user'@'localhost';
GRANT SELECT, INSERT, UPDATE ON companycustomers TO 'webstore_user'@'localhost';

-- webstore_employee permissions (employee DB user, not used by the customer API)
GRANT SELECT ON customers TO 'webstore_employee'@'localhost';
GRANT SELECT ON customeraddresses TO 'webstore_employee'@'localhost';
GRANT SELECT, INSERT, UPDATE (stock_quantity, price) ON products TO 'webstore_employee'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON productcategories TO 'webstore_employee'@'localhost';
GRANT SELECT, INSERT, UPDATE (status, delivery_date) ON orders TO 'webstore_employee'@'localhost';
GRANT SELECT, INSERT, UPDATE (quantity) ON orderitems TO 'webstore_employee'@'localhost';
GRANT SELECT, INSERT, UPDATE ON suppliers TO 'webstore_employee'@'localhost';
GRANT SELECT, INSERT, UPDATE ON supplieraddresses TO 'webstore_employee'@'localhost';
GRANT SELECT, INSERT, DELETE ON productsuppliers TO 'webstore_employee'@'localhost';
GRANT SELECT ON companycustomers TO 'webstore_employee'@'localhost';

-- Show user access
-- SHOW GRANTS FOR 'webstore_user'@'localhost';

-- Remove all access from user
-- REVOKE ALL PRIVILEGES, GRANT OPTION FROM 'webstore_user'@'localhost';
