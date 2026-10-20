INSERT INTO productsuppliers (product_id, supplier_id)
SELECT DISTINCT id, supplier_id FROM products WHERE supplier_id IS NOT NULL;
