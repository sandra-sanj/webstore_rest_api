# API results

This file lists all customer API endpoints at `http://localhost:8080`.

## Customers

### Create customer

Request:

```http
POST /customers

{
  "firstName": "Test",
  "lastName": "Customer",
  "email": "test.customer@example.com",
  "phone": "0401234567"
}
```

Response: `201 Created`

```json
{
  "firstName": "Test",
  "lastName": "Customer",
  "email": "test.customer@example.com",
  "phone": "0401234567",
  "id": 100031
}
```

### Create company customer

Request:

```http
POST /customers/company

{
  "firstName": "Test",
  "lastName": "Contact",
  "email": "test.contact@example.com",
  "phone": "0401112233",
  "companyName": "Test Company Oy",
  "companyId": "1234567-8"
}
```

Response: `201 Created`

```json
{
  "firstName": "Test",
  "lastName": "Contact",
  "email": "test.contact@example.com",
  "phone": "0401112233",
  "companyName": "Test Company Oy",
  "companyId": "1234567-8",
  "id": 100032
}
```

### Get customer

Request:

```http
GET /customers/{{customerId}}
```

Response: `200 OK`

```json
{
  "firstName": "Test",
  "lastName": "Customer",
  "email": "test.customer@example.com",
  "phone": "0401234567",
  "id": 100031
}
```

Company customer request:

```http
GET /customers/{{companyCustomerId}}
```

Response: `200 OK`

```json
{
  "firstName": "Test",
  "lastName": "Contact",
  "email": "test.contact@example.com",
  "phone": "0401112233",
  "companyName": "Test Company Oy",
  "companyId": "1234567-8",
  "id": 100032
}
```

### Update customer

Request:

```http
PUT /customers/{{customerId}}

{
  "firstName": "Updated",
  "lastName": "Customer",
  "email": "updated.customer@example.com",
  "phone": "0507654321"
}
```

Response: `200 OK`

```json
{
  "firstName": "Updated",
  "lastName": "Customer",
  "email": "updated.customer@example.com",
  "phone": "0507654321",
  "id": 100031
}
```

### Update company customer

Request:

```http
PUT /customers/company/{{companyCustomerId}}

{
  "firstName": "Updated",
  "lastName": "Contact",
  "email": "test.contact@example.com",
  "phone": "0509998877",
  "companyName": "Updated Company Oy",
  "companyId": "1234567-8"
}
```

Response: `200 OK`

```json
{
  "firstName": "Updated",
  "lastName": "Contact",
  "email": "test.contact@example.com",
  "phone": "0509998877",
  "companyName": "Updated Company Oy",
  "companyId": "1234567-8",
  "id": 100032
}
```

## Customer addresses

### Add address

Request:

```http
POST /customers/{{customerId}}/addresses

{
  "streetAddress": "Test Street 1",
  "postalCode": "00100",
  "city": "Helsinki",
  "country": "Finland"
}
```

Response: `201 Created`

```json
{
  "streetAddress": "Test Street 1",
  "postalCode": "00100",
  "city": "Helsinki",
  "country": "Finland",
  "customerId": 1,
  "id": 100012
}
```

### Get customer addresses

Request:

```http
GET /customers/{{customerId}}/addresses
```

Response: `200 OK`

```json
[
  {
    "streetAddress": "39650 Harrington Plains Suite 474",
    "postalCode": "95942",
    "city": "Larryview",
    "country": "Guadeloupe",
    "customerId": 1,
    "id": 1
  },
  {
    "streetAddress": "Test Street 1",
    "postalCode": "00100",
    "city": "Helsinki",
    "country": "Finland",
    "customerId": 1,
    "id": 100012
  }
]
```

### Get customer's first address

Request:

```http
GET /customers/{{customerId}}/address
```

Response: `200 OK`

```json
{
  "streetAddress": "39650 Harrington Plains Suite 474",
  "postalCode": "95942",
  "city": "Larryview",
  "country": "Guadeloupe",
  "customerId": 1,
  "id": 1
}
```

### Get address by id

Request:

```http
GET /addresses/{{addressId}}
```

Response: `200 OK`

```json
{
  "streetAddress": "Test Street 1",
  "postalCode": "00100",
  "city": "Helsinki",
  "country": "Finland",
  "customerId": 1,
  "id": 100012
}
```

### Update address

Request:

```http
PUT /addresses/{{addressId}}

{
  "streetAddress": "Test Street 2",
  "postalCode": "00200",
  "city": "Espoo",
  "country": "Finland"
}
```

Response: `200 OK`

```json
{
  "streetAddress": "Test Street 2",
  "postalCode": "00200",
  "city": "Espoo",
  "country": "Finland",
  "customerId": 1,
  "id": 100012
}
```

## Product categories

### Get categories

Request:

```http
GET /categories
```

Response: `200 OK`

```json
[
  {
    "description": "Sähkölaitteet, komponentit ja elektroniikkatuotteet",
    "id": 1,
    "name": "Elektroniikka"
  },
  {
    "description": "Kodin ja arjen tarvikkeet, kodintekstiilit",
    "id": 2,
    "name": "Kodin tarvikkeet"
  }
]
```

### Get category by id

Request:

```http
GET /categories/{{categoryId}}
```

Response: `200 OK`

```json
{
  "description": "Sähkölaitteet, komponentit ja elektroniikkatuotteet",
  "id": 1,
  "name": "Elektroniikka"
}
```

### Get category products

Request:

```http
GET /categories/{{categoryId}}/products
```

Response: `200 OK`

```json
[
  {
    "name": "Magic Bit 840",
    "description": "Focus test but out catch million with see view.",
    "price": 352.35,
    "stockQuantity": 378,
    "categoryId": 1,
    "enddate": "2100-12-31",
    "id": 7,
    "startdate": "1970-01-01"
  },
  {
    "name": "Shiny Planet 299",
    "description": "Us really personal yes into yourself task happen.",
    "price": 697.49,
    "stockQuantity": 179,
    "categoryId": 1,
    "enddate": "2100-12-31",
    "id": 8,
    "startdate": "1970-01-01"
  }
]
```

## Products

### Get all products

Request:

```http
GET /products
```

Response: `200 OK`

```json
[
  {
    "name": "Happy Pack 382",
    "description": "Score million throw thing instead ball line think.",
    "price": 546.08,
    "stockQuantity": 398,
    "categoryId": 10,
    "enddate": "2100-12-31",
    "id": 2,
    "startdate": "1970-01-01"
  },
  {
    "name": "Quantum Widget 457",
    "description": "Ask decide need next very capital.",
    "price": 849.66,
    "stockQuantity": 312,
    "categoryId": 3,
    "enddate": "2100-12-31",
    "id": 3,
    "startdate": "1970-01-01"
  }
]
```

### Search products

All filters are optional and can be used alone or together.

Request:

```http
GET /products?name=widget&categoryId=3&minPrice=100&maxPrice=500&inStock=true
```

Response: `200 OK`

```json
[
  {
    "name": "Peak Widget 907",
    "description": "Skill everyone have player early value herself.",
    "price": 294.13,
    "stockQuantity": 475,
    "categoryId": 3,
    "enddate": "2100-12-31",
    "id": 384,
    "startdate": "1970-01-01"
  },
  {
    "name": "Bliss Widget 412",
    "description": "Doctor simply medical foot woman this protect unit.",
    "price": 210.35,
    "stockQuantity": 216,
    "categoryId": 3,
    "enddate": "2100-12-31",
    "id": 862,
    "startdate": "1970-01-01"
  }
]
```

### Get product by id

Request:

```http
GET /products/{{productId}}
```

Response: `200 OK`

```json
{
  "name": "Happy Pack 382",
  "description": "Score million throw thing instead ball line think.",
  "price": 546.08,
  "stockQuantity": 398,
  "categoryId": 10,
  "enddate": "2100-12-31",
  "id": 2,
  "startdate": "1970-01-01"
}
```

## Orders

### Create order

Request:

```http
POST /customers/{{customerId}}/orders?shippingAddressId={{addressId}}

[
  {"productId": 2, "quantity": 2},
  {"productId": 3, "quantity": 1}
]
```

Response: `201 Created`

```json
{
  "customerId": 1,
  "deliveryDate": null,
  "id": 200001,
  "orderDate": "2026-10-10T17:16:46.141071",
  "shippingAddressId": 1,
  "status": "NEW"
}
```

### Get order by id

Request:

```http
GET /orders/{{orderId}}
```

Response: `200 OK`

```json
{
  "customerId": 1,
  "deliveryDate": null,
  "id": 200001,
  "orderDate": "2026-10-10T17:16:46",
  "shippingAddressId": 1,
  "status": "NEW"
}
```

### Get customer orders

Request:

```http
GET /customers/{{customerId}}/orders
```

Response: `200 OK`

```json
[
  {
    "customerId": 1,
    "deliveryDate": "2024-05-08T04:18:07",
    "id": 181269,
    "orderDate": "2024-05-02T00:20:53",
    "shippingAddressId": 1,
    "status": "CANCELLED"
  },
  {
    "customerId": 1,
    "deliveryDate": "2023-11-26T16:06:51",
    "id": 182014,
    "orderDate": "2023-11-21T02:38:02",
    "shippingAddressId": 1,
    "status": "CANCELLED"
  },
  {
    "customerId": 1,
    "deliveryDate": null,
    "id": 200001,
    "orderDate": "2026-10-10T17:16:46",
    "shippingAddressId": 1,
    "status": "NEW"
  }
]
```

### Get customer order totals

Request:

```http
GET /customers/{{customerId}}/orders/totals
```

Response: `200 OK`

```json
[
  {
    "orderDate": "2024-05-02T00:20:53",
    "orderId": 181269,
    "status": "CANCELLED",
    "total": 7023.97
  },
  {
    "orderDate": "2023-11-21T02:38:02",
    "orderId": 182014,
    "status": "CANCELLED",
    "total": 1495.24
  },
  {
    "orderDate": "2026-10-10T17:16:46",
    "orderId": 200001,
    "status": "NEW",
    "total": 1941.82
  }
]
```

## Order items

### Get order items

Request:

```http
GET /orders/{{orderId}}/items
```

Response: `200 OK`

```json
[
  {
    "orderId": 200001,
    "productId": 2,
    "quantity": 2,
    "unitPrice": 546.08
  },
  {
    "orderId": 200001,
    "productId": 3,
    "quantity": 1,
    "unitPrice": 849.66
  }
]
```

### Change product quantity

Request:

```http
PUT /orders/{{orderId}}/items/{{productId}}

{"quantity": 10}
```

Response: `200 OK`

```json
{
  "orderId": 200001,
  "productId": 2,
  "quantity": 10,
  "unitPrice": 546.08
}
```
