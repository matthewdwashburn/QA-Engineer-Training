-- Week 4 Monday — Instructor demo: multi-table DDL with constraints (PostgreSQL)
-- Theory tie-in: written/ddl-data-definition-language.md, constraints.md, referential-integrity.md
-- Run in a scratch database: CREATE DATABASE week4_demo; \c week4_demo;

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

COMMIT;
