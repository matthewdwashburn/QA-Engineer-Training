-- Week 4 Monday — Instructor demo: multi-table DDL with constraints (PostgreSQL)
-- Theory tie-in: written/ddl-data-definition-language.md, constraints.md, referential-integrity.md
-- Run in a scratch database: CREATE DATABASE week4_demo; \c week4_demo;
SET SEARCH_PATH to test2;

BEGIN;

DROP TABLE IF EXISTS order_line CASCADE;
DROP TABLE IF EXISTS order_header CASCADE;
DROP TABLE IF EXISTS product CASCADE;
DROP TABLE IF EXISTS customer CASCADE;

-- CUSTOMER: identity, uniqueness, domain check
CREATE TABLE customer (
    customer_id   INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email         VARCHAR(255) NOT NULL,
    full_name     VARCHAR(200) NOT NULL,
    country_code  CHAR(2) NOT NULL DEFAULT 'US'
        CHECK (country_code ~ '^[A-Z]{2}$'),
    is_active     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_customer_email UNIQUE (email)
);

-- PRODUCT: SKU uniqueness, non-negative stock and price
CREATE TABLE product (
    product_id    INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sku           VARCHAR(32) NOT NULL,
    name          VARCHAR(200) NOT NULL,
    unit_price    NUMERIC(12, 2) NOT NULL CHECK (unit_price >= 0),
    stock_qty     INTEGER NOT NULL DEFAULT 0 CHECK (stock_qty >= 0),
    CONSTRAINT uq_product_sku UNIQUE (sku)
);

-- ORDER_HEADER: FK to customer, status domain
CREATE TABLE order_header (
    order_id      INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    customer_id   INTEGER NOT NULL REFERENCES customer (customer_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    placed_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    status        VARCHAR(20) NOT NULL DEFAULT 'OPEN'
        CHECK (status IN ('OPEN', 'PAID', 'SHIPPED', 'CANCELLED'))
);

-- ORDER_LINE: composite PK, FKs with CASCADE delete of lines when order removed
CREATE TABLE order_line (
    order_id      INTEGER NOT NULL REFERENCES order_header (order_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    line_no       INTEGER NOT NULL CHECK (line_no > 0),
    product_id    INTEGER NOT NULL REFERENCES product (product_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    qty           INTEGER NOT NULL CHECK (qty > 0),
    unit_price    NUMERIC(12, 2) NOT NULL CHECK (unit_price >= 0),
    PRIMARY KEY (order_id, line_no)
);



------------------------------------------------------------
-- Customers
------------------------------------------------------------
INSERT INTO customer (email, full_name, country_code)
VALUES
('alice@example.com',   'Alice Johnson',    'US'),
('bob@example.com',     'Bob Smith',        'US'),
('carol@example.com',   'Carol Davis',      'CA'),
('david@example.com',   'David Wilson',     'US'),
('emma@example.com',    'Emma Brown',       'GB'),
('frank@example.com',   'Frank Miller',     'US'),
('grace@example.com',   'Grace Taylor',     'AU'),
('henry@example.com',   'Henry Moore',      'US'),
('isabella@example.com','Isabella Thomas',  'US'),
('jack@example.com',    'Jack Anderson',    'CA');

------------------------------------------------------------
-- Products
------------------------------------------------------------
INSERT INTO product (sku, name, unit_price, stock_qty)
VALUES
('SKU-1001','Laptop',           999.99,15),
('SKU-1002','Monitor',          249.99,30),
('SKU-1003','Keyboard',          49.99,80),
('SKU-1004','Mouse',             29.99,100),
('SKU-1005','USB-C Cable',       12.99,200),
('SKU-1006','Docking Station',  149.99,25),
('SKU-1007','Headset',           89.99,40),
('SKU-1008','Webcam',            79.99,35),
('SKU-1009','Desk Lamp',         39.99,50),
('SKU-1010','Office Chair',     299.99,12),
('SKU-1011','External SSD',     159.99,45),
('SKU-1012','Printer',          219.99,18);

------------------------------------------------------------
-- Order Headers (25 Orders)
------------------------------------------------------------
INSERT INTO order_header (customer_id,status)
VALUES
(1,'PAID'),
(2,'SHIPPED'),
(3,'OPEN'),
(4,'PAID'),
(5,'CANCELLED'),
(6,'OPEN'),
(7,'SHIPPED'),
(8,'PAID'),
(9,'OPEN'),
(10,'SHIPPED'),
(1,'OPEN'),
(2,'PAID'),
(3,'SHIPPED'),
(4,'OPEN'),
(5,'PAID'),
(6,'SHIPPED'),
(7,'OPEN'),
(8,'PAID'),
(9,'CANCELLED'),
(10,'OPEN'),
(2,'OPEN'),
(4,'SHIPPED'),
(6,'PAID'),
(8,'OPEN'),
(10,'PAID');

------------------------------------------------------------
-- Order Lines
------------------------------------------------------------
INSERT INTO order_line
(order_id,line_no,product_id,qty,unit_price)
VALUES

(1,1,1,1,999.99),
(1,2,3,1,49.99),

(2,1,2,2,249.99),
(2,2,4,1,29.99),

(3,1,5,3,12.99),

(4,1,10,1,299.99),
(4,2,9,2,39.99),

(5,1,6,1,149.99),

(6,1,3,2,49.99),
(6,2,4,2,29.99),

(7,1,7,1,89.99),
(7,2,5,4,12.99),

(8,1,1,1,999.99),
(8,2,11,1,159.99),

(9,1,8,2,79.99),

(10,1,2,1,249.99),
(10,2,12,1,219.99),

(11,1,5,5,12.99),

(12,1,6,1,149.99),
(12,2,3,1,49.99),

(13,1,1,1,999.99),
(13,2,4,1,29.99),

(14,1,10,1,299.99),

(15,1,9,3,39.99),
(15,2,5,2,12.99),

(16,1,11,2,159.99),

(17,1,7,2,89.99),

(18,1,8,1,79.99),
(18,2,2,1,249.99),

(19,1,12,1,219.99),

(20,1,3,4,49.99),
(20,2,4,2,29.99),

(21,1,6,1,149.99),

(22,1,1,1,999.99),
(22,2,10,1,299.99),

(23,1,5,10,12.99),

(24,1,9,2,39.99),
(24,2,11,1,159.99),

(25,1,2,2,249.99),
(25,2,7,1,89.99);


COMMIT;
