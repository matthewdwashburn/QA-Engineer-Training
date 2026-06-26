SET SEARCH_PATH TO test1;

-- Clean up
DROP TABLE IF EXISTS order_line CASCADE ;
DROP TABLE IF EXISTS order_header CASCADE;
DROP TABLE IF EXISTS product CASCADE;
DROP TABLE IF EXISTS customer CASCADE;

-- Customer
CREATE TABLE customer (
    customer_id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL
);

-- Product
CREATE TABLE product (
    product_id SERIAL PRIMARY KEY,
    sku VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    unit_price NUMERIC(10,2) NOT NULL,
    stock_qty INTEGER NOT NULL DEFAULT 0
);

-- Order Header
CREATE TABLE order_header (
    order_id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_order_customer
        FOREIGN KEY (customer_id)
        REFERENCES customer(customer_id)
);

-- Order Line
CREATE TABLE order_line (
    order_id INTEGER NOT NULL,
    line_no INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    qty INTEGER NOT NULL,
    unit_price NUMERIC(10,2) NOT NULL,

    PRIMARY KEY (order_id, line_no),

    CONSTRAINT fk_line_order
        FOREIGN KEY (order_id)
        REFERENCES order_header(order_id),

    CONSTRAINT fk_line_product
        FOREIGN KEY (product_id)
        REFERENCES product(product_id)
);

DELETE FROM order_line;
DELETE FROM order_header;
DELETE FROM product;
DELETE FROM customer;

INSERT INTO customer (email, full_name) VALUES
    ('join-a@example.com', 'Join Alpha'),
    ('join-b@example.com', 'Join Beta'),
    ('join-c@example.com', 'Join Gamma');

INSERT INTO product (sku, name, unit_price, stock_qty) VALUES
    ('J-SKU-1', 'Demo Item 1', 10.00, 50),
    ('J-SKU-2', 'Demo Item 2', 20.00, 50);

INSERT INTO order_header (customer_id, status)
SELECT customer_id, 'PAID'
FROM customer
WHERE email = 'join-a@example.com';

INSERT INTO order_line (order_id, line_no, product_id, qty, unit_price)
SELECT oh.order_id, 1, p.product_id, 1, p.unit_price
FROM order_header oh
JOIN customer c ON c.customer_id = oh.customer_id
JOIN product p ON p.sku = 'J-SKU-1'
WHERE c.email = 'join-a@example.com';

INSERT INTO order_header (customer_id, status)
SELECT customer_id, 'OPEN'
FROM customer
WHERE email = 'join-b@example.com';

---------------------------------------------------------
SELECT * FROM customer;
SELECT * FROM product;
SELECT * FROM order_header;
SELECT * FROM order_line;

-- INNER: Customers who have at least one order
SELECT c.email, oh.order_id, oh.status
FROM customer c
INNER JOIN order_header oh ON oh.customer_id = c.customer_id
ORDER BY c.email, oh.order_id;

-- LEFT: All Customers, orders if any
SELECT c.email, oh.order_id, oh.status
FROM customer c
LEFT JOIN order_header oh ON oh.customer_id = c.customer_id
ORDER BY c.email, oh.order_id;

--RIGHT: all orders, customer columns even if missing (with our FK, every order has a customer)
SELECT c.email, oh.order_id, oh.status
FROM customer c
RIGHT JOIN order_header oh ON oh.customer_id = c.customer_id
ORDER BY c.email, oh.order_id;

--FULL OUTER: customers without orders AND (conceptually) orders without customers
-- with FK enforced, unmatched orders won't exist
SELECT c.email, oh.order_id, oh.status
FROM customer c
FULL OUTER JOIN order_header oh ON oh.customer_id = c.customer_id
ORDER BY c.email NULLS LAST, oh.order_id NULLS LAST;

-- CROSS JOIN: our case small cardinality - customers x products
SELECT c.email, p.sku
FROM customer c
CROSS JOIN product p
ORDER BY c.email, p.sku;





