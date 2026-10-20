# webstore_rest_api

A REST API for a fake webstore. API focuses on customer perspective, allowing website customers to register, manage their own details and addresses, browse products and place orders.

**Technologies:** MariaDB, Java 21, Spring Boot, Spring Data JPA

## Setup

The database is built from the original dump `sql/schema_populated_dump.sql`, the scripts in `sql/` and Spring Boot (`dev` profile), which creates the tables of the new entities.

1. Copy `sql/00_users.sql` as `sql/00_users.local.sql` and set the passwords for admin, user and employee.

2. Copy `.env.example` as `.env` and set the same passwords. `WEBSTORE_DB` is the database name, used by `setup.sh` and Spring Boot.

3. Run `./sql/setup.sh`. It creates the database, loads the dump and runs first batch of sql queries.

4. Run Spring Boot in dev profile. It has update permissions and creates `productsuppliers` and `companycustomers`. Stop it once it has started.

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

5. Run `./sql/setup_after_dev.sh`. It runs second batch of sql queries, which depend on the previous step.

6. Done. After this, `./mvnw spring-boot:run` runs the API as a website customer in http://localhost:8080.

## Database modifications

The original dump is changed in three ways: the numbered scripts in `sql/`, the new tables that Spring Boot creates, and the API code. The scripts run in number order, through the two setup scripts in Setup.

### Users and rights

`00_users.sql` creates three DB users: admin, user and employee. The admin gets all rights first, because `dev` needs them to create tables. `08_permissions.sql` then gives each user only what it needs, so the customer API can't delete rows or change tables. It runs after `dev`, because rights can't be given on tables that don't exist yet. Real passwords go in the gitignored copy `00_users.local.sql`.

### Keeping the data valid

`03_trigger_no_negative_stock.sql` blocks any update that would make a product's stock go below zero. `04_event_cancel_old_orders.sql` cancels orders that have waited for payment for over 14 days, so abandoned orders don't stay open forever. The API also trims emails and converts them to lowercase before saving, so the same address is always stored the same way.

### Faster queries

`01_indexes.sql` adds indexes for searching addresses by country and customers by name. During testing these two queries were slower than other tests, so these were indexed.

Two API queries were also improved. Total price of order is calculated in one database query, instead of loading every order and adding them up after. Product search puts only the filters the customer used into the query instead of checking every filter each time.

### Products with price history

`05_products_price_history.sql` gives each product row a period when it's valid, and the database also keeps old versions automatically. One product can now have several rows, so a new `row_id` replaces `id` as the primary key. The API shows only the version valid today. `07_products_price_history_data.sql` adds price history for a few products, so this can be tested through API calls.

### Views

`02_views.sql` adds two read-only views that replace long queries. `order_details` shows each ordered product with its order and customer, and `product_category_count` counts products per category. `06_views_current_products.sql` makes both use only current product versions, so a product with several prices isn't counted twice.

### New tables

Spring Boot creates two new tables. `productsuppliers` lets a product have many suppliers, and `09_productsuppliers_data.sql` fills it from the old supplier column. `companycustomers` adds a company name and business ID to a customer, so the large `customers` table didn't have to change.

## Profiles

The profile decides which database user the app connects as. The default profile is the website customer (`webstore_user`), so the app can only do what that user's permissions allow. `dev` (`webstore_admin`) is used during setup.

Run the API as customer:

```bash
./mvnw spring-boot:run
```

Run the API as admin:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## Tests

Run tests with `./mvnw test` after setup. They test the customer endpoints, including what a customer can or cannot do. They use the real database and roll back the test changes.

## Customer API

The endpoints cover what a shop customer does on the website. Every endpoint is described with a request and a response in [docs/api-results.md](docs/api-results.md), and the [http](http/) folder has requests that can be run.

| Method | Endpoint                                                   | Function                               |
| ------ | ---------------------------------------------------------- | -------------------------------------- |
| POST   | `/customers`, `/customers/company`                         | Create customer                        |
| GET    | `/customers/{id}`                                          | Get customer details                   |
| PUT    | `/customers/{id}`, `/customers/company/{id}`               | Edit customer details                  |
| POST   | `/customers/{id}/addresses`                                | Add customer address                   |
| GET    | `/customers/{id}/addresses`, `/addresses/{id}`             | Get customer addresses                 |
| GET    | `/customers/{id}/address`                                  | Get customer's first address           |
| PUT    | `/addresses/{id}`                                          | Edit address details                   |
| GET    | `/categories`, `/categories/{id}`                          | Get all/specific product categories    |
| GET    | `/categories/{id}/products`, `/products`, `/products/{id}` | Get products                           |
| GET    | `/products?name=&categoryId=&minPrice=&maxPrice=&inStock=` | Search products (all filters optional) |
| POST   | `/customers/{id}/orders?shippingAddressId=`                | Create order                           |
| GET    | `/customers/{id}/orders`                                   | Get customer orders                    |
| GET    | `/customers/{id}/orders/totals`                            | Get the total of each customer order   |
| GET    | `/orders/{id}`                                             | Get customer order                     |
| GET    | `/orders/{id}/items`                                       | Get order items                        |
| PUT    | `/orders/{id}/items/{productId}`                           | Update order product quantity          |
