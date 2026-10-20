CREATE VIEW order_details AS
SELECT orders.id AS order_id, customers.first_name, customers.last_name,
       products.name AS product_name, orders.status, orders.order_date
FROM customers
INNER JOIN orders ON customers.id = orders.customer_id
INNER JOIN orderitems ON orders.id = orderitems.order_id
INNER JOIN products ON orderitems.product_id = products.id;

CREATE VIEW product_category_count AS
SELECT productcategories.name AS category_name, COUNT(*) AS product_count
FROM products
INNER JOIN productcategories ON products.category_id = productcategories.id
GROUP BY productcategories.name;
