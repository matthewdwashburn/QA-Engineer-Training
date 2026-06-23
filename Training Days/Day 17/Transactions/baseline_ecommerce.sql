-- Baseline data for Tuesday exercises (PostgreSQL).
-- Creates a minimal ecommerce schema + seed if you do not have Monday's DB handy.

BEGIN;

DROP TABLE IF EXISTS order_line CASCADE;
DROP TABLE IF EXISTS order_header CASCADE;
DROP TABLE IF EXISTS product CASCADE;
DROP TABLE IF EXISTS customer CASCADE;

CREATE TABLE customer (
    customer_id   INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    full_name     VARCHAR(200) NOT NULL
);

CREATE TABLE product (
    product_id    INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sku           VARCHAR(32) NOT NULL UNIQUE,
    name          VARCHAR(200) NOT NULL,
    unit_price    NUMERIC(12, 2) NOT NULL CHECK (unit_price >= 0),
    stock_qty     INTEGER NOT NULL DEFAULT 0 CHECK (stock_qty >= 0)
);

CREATE TABLE order_header (
    order_id      INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    customer_id   INTEGER NOT NULL REFERENCES customer (customer_id),
    placed_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    status        VARCHAR(20) NOT NULL DEFAULT 'OPEN'
);

CREATE TABLE order_line (
    order_id      INTEGER NOT NULL REFERENCES order_header (order_id) ON DELETE CASCADE,
    line_no       INTEGER NOT NULL,
    product_id    INTEGER NOT NULL REFERENCES product (product_id),
    qty           INTEGER NOT NULL CHECK (qty > 0),
    unit_price    NUMERIC(12, 2) NOT NULL CHECK (unit_price >= 0),
    PRIMARY KEY (order_id, line_no)
);

INSERT INTO customer (email, full_name) VALUES
    ('alpha@example.com', 'Customer Alpha'),
    ('beta@example.com', 'Customer Beta'),
    ('gamma@example.com', 'Customer Gamma');

INSERT INTO product (sku, name, unit_price, stock_qty) VALUES
    ('BASE-A', 'Item A', 10.00, 100),
    ('BASE-B', 'Item B', 20.00, 50),
    ('BASE-C', 'Item C', 5.00, 200);

-- Alpha: two orders; Beta: one order; Gamma: no orders
INSERT INTO order_header (customer_id, status)
SELECT customer_id, 'PAID' FROM customer WHERE email = 'alpha@example.com';
INSERT INTO order_line (order_id, line_no, product_id, qty, unit_price)
SELECT oh.order_id, 1, p.product_id, 2, p.unit_price
FROM order_header oh
JOIN customer c ON c.customer_id = oh.customer_id
JOIN product p ON p.sku = 'BASE-A'
WHERE c.email = 'alpha@example.com'
ORDER BY oh.order_id DESC LIMIT 1;

INSERT INTO order_header (customer_id, status)
SELECT customer_id, 'OPEN' FROM customer WHERE email = 'alpha@example.com';
INSERT INTO order_line (order_id, line_no, product_id, qty, unit_price)
SELECT oh.order_id, 1, p.product_id, 1, p.unit_price
FROM order_header oh
JOIN customer c ON c.customer_id = oh.customer_id
JOIN product p ON p.sku = 'BASE-B'
WHERE c.email = 'alpha@example.com'
ORDER BY oh.order_id DESC LIMIT 1;

INSERT INTO order_header (customer_id, status)
SELECT customer_id, 'PAID' FROM customer WHERE email = 'beta@example.com';
INSERT INTO order_line (order_id, line_no, product_id, qty, unit_price)
SELECT oh.order_id, 1, p.product_id, 3, p.unit_price
FROM order_header oh
JOIN customer c ON c.customer_id = oh.customer_id
JOIN product p ON p.sku = 'BASE-C'
WHERE c.email = 'beta@example.com'
ORDER BY oh.order_id DESC LIMIT 1;

COMMIT;
