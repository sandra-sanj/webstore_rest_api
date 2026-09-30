-- Replace CHANGE_ME with the real password before running, to keep the real value out of git
CREATE USER 'webstore_admin'@'localhost' IDENTIFIED BY 'CHANGE_ME';
CREATE USER 'webstore_user'@'localhost' IDENTIFIED BY 'CHANGE_ME';
CREATE USER 'webstore_employee'@'localhost' IDENTIFIED BY 'CHANGE_ME';

-- webstore_admin permissions
GRANT ALL PRIVILEGES ON webstore.* TO 'webstore_admin'@'localhost';

-- webstore_user permissions
GRANT SELECT, INSERT, UPDATE ON webstore.customers TO 'webstore_user'@'localhost';
GRANT SELECT, INSERT, UPDATE ON webstore.customeraddresses TO 'webstore_user'@'localhost';
GRANT SELECT, UPDATE (stock_quantity) ON webstore.products TO 'webstore_user'@'localhost';
GRANT SELECT ON webstore.productcategories TO 'webstore_user'@'localhost';
GRANT SELECT, INSERT ON webstore.orders TO 'webstore_user'@'localhost';
GRANT SELECT, INSERT, UPDATE (quantity) ON webstore.orderitems TO 'webstore_user'@'localhost';
GRANT SELECT, INSERT, UPDATE ON webstore.companycustomers TO 'webstore_user'@'localhost';

-- webstore_employee permissions
GRANT SELECT ON webstore.customers TO 'webstore_employee'@'localhost';
GRANT SELECT ON webstore.customeraddresses TO 'webstore_employee'@'localhost';
GRANT SELECT, INSERT, UPDATE (stock_quantity, price) ON webstore.products TO 'webstore_employee'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON webstore.productcategories TO 'webstore_employee'@'localhost';
GRANT SELECT, INSERT, UPDATE (status, delivery_date) ON webstore.orders TO 'webstore_employee'@'localhost';
GRANT SELECT, INSERT, UPDATE (quantity) ON webstore.orderitems TO 'webstore_employee'@'localhost';
GRANT SELECT, INSERT, UPDATE ON webstore.suppliers TO 'webstore_employee'@'localhost';
GRANT SELECT, INSERT, UPDATE ON webstore.supplieraddresses TO 'webstore_employee'@'localhost';
GRANT SELECT ON webstore.companycustomers TO 'webstore_employee'@'localhost';
GRANT SELECT, INSERT, DELETE ON webstore.productsuppliers TO 'webstore_employee'@'localhost';

-- Show user access
-- SHOW GRANTS FOR 'webstore_user'@'localhost';

-- Remove all access from user
-- REVOKE ALL PRIVILEGES, GRANT OPTION FROM 'webstore_user'@'localhost';
