CREATE INDEX idx_customeraddress_country ON customeraddresses (country, city, postal_code, street_address);
CREATE INDEX idx_customers_fullname ON customers (first_name, last_name);
