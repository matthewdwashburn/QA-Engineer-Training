-- Instructor reference — PostgreSQL. Adapt for other engines as needed.
-- One possible shape; trainees' designs may differ if they meet requirements.

BEGIN;

DROP TABLE IF EXISTS order_line CASCADE;
DROP TABLE IF EXISTS order_header CASCADE;
DROP TABLE IF EXISTS customer_address CASCADE;
DROP TABLE IF EXISTS product CASCADE;
DROP TABLE IF EXISTS customer CASCADE;

CREATE TABLE customer (
    customer_id   INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    full_name     VARCHAR(200) NOT NULL,
    phone         VARCHAR(32),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE customer_address (
    address_id    INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    customer_id   INTEGER NOT NULL REFERENCES customer (customer_id) ON DELETE CASCADE,
    line1         VARCHAR(200) NOT NULL,
    city          VARCHAR(100) NOT NULL,
    postal_code   VARCHAR(20) NOT NULL,
    is_default    BOOLEAN NOT NULL DEFAULT FALSE
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
    customer_id   INTEGER NOT NULL REFERENCES customer (customer_id) ON DELETE RESTRICT,
    ship_address_id INTEGER NOT NULL REFERENCES customer_address (address_id) ON DELETE RESTRICT,
    placed_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    status        VARCHAR(20) NOT NULL DEFAULT 'OPEN'
        CHECK (status IN ('OPEN', 'PAID', 'SHIPPED', 'CANCELLED'))
);

CREATE TABLE order_line (
    order_id      INTEGER NOT NULL REFERENCES order_header (order_id) ON DELETE CASCADE,
    line_no       INTEGER NOT NULL CHECK (line_no > 0),
    product_id    INTEGER NOT NULL REFERENCES product (product_id) ON DELETE RESTRICT,
    qty           INTEGER NOT NULL CHECK (qty > 0),
    unit_price    NUMERIC(12, 2) NOT NULL CHECK (unit_price >= 0),
    PRIMARY KEY (order_id, line_no)
);

COMMIT;
