SET search_path TO exercises;

DROP TABLE IF EXISTS customer CASCADE;
DROP TABLE IF EXISTS product CASCADE;
DROP TABLE IF EXISTS order_header CASCADE;
DROP TABLE IF EXISTS order_line CASCADE;
DROP TABLE IF EXISTS customer_address CASCADE;

CREATE TABLE customer (
    customer_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(32)
);

CREATE TABLE product (
    product_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	sku VARCHAR(32) NOT NULL UNIQUE,
    product_name VARCHAR(200) NOT NULL,
    unit_price NUMERIC(12, 2) NOT NULL CHECK (unit_price >= 0),
    stock_qty INT NOT NULL CHECK (stock_qty >= 0)
);

CREATE TABLE customer_address (
    address_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	-- If customer that has addresses is deleted, system will restrict until all addresses accociated 
	-- with this customer id are deleted.
    customer_id INT NOT NULL REFERENCES customer (customer_id) ON DELETE RESTRICT,
    line1 VARCHAR(200) NOT NULL,
    city VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
	is_default BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE order_header (
    order_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    customer_id INT NOT NULL REFERENCES customer (customer_id) ON DELETE RESTRICT,
    ship_address_id INT NOT NULL REFERENCES customer_address (address_id) ON DELETE RESTRICT,
    order_date TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    order_status VARCHAR(20) NOT NULL DEFAULT 'OPEN'
);

CREATE TABLE order_line (
	order_id INT NOT NULL REFERENCES order_header (order_id) ON DELETE CASCADE,
	line_no INT NOT NULL CHECK (line_no > 0),
	product_id INT NOT NULL REFERENCES product (product_id) ON DELETE RESTRICT,
	qty INT NOT NULL CHECK (qty > 0),
	unit_price NUMERIC(12, 2) NOT NULL CHECK (unit_price >= 0),
	PRIMARY KEY (order_id, line_no) -- Combination of order_id and line_no must be unique
);

-- Customer inserts test

INSERT INTO customer (first_name, last_name, email, phone) VALUES (
	'Matthew', 'Washburn', 'matthew@gmail.com', '888-8888-8888'
);

INSERT INTO customer (first_name, last_name, email, phone) VALUES (
	'Jack', 'Frost', 'jack@gmail.com', '888-8888-8888'
);

INSERT INTO customer (first_name, last_name, email, phone) VALUES (
	'Emily', 'Frost', 'emily@gmail.com', '888-8888-8888'
);

SELECT * FROM customer;

-- Product inserts test
INSERT INTO product (sku, product_name, unit_price, stock_qty) VALUES (
	'SKU-001', 'Gorilla Couch', 676.76, 1500
);

INSERT INTO product (sku, product_name, unit_price, stock_qty) VALUES (
	'SKU-002', 'Gorilla Chair', 67.67, 1000
);

INSERT INTO product (sku, product_name, unit_price, stock_qty) VALUES (
	'SKU-003', 'Gorilla Bed', 6767.67, 750
);

SELECT * FROM product;

-- Insert address test
INSERT INTO customer_address (customer_id, line1, city, postal_code) VALUES (
	1, '401 Gorilla Rd', 'San Francisco', 90001
);

SELECT * FROM customer_address;

-- Order header insert test
INSERT INTO order_header (customer_id, ship_address_id) VALUES (1, 1), (1, 1);

SELECT * FROM order_header;

-- Order line insert test
INSERT INTO order_line (order_id, line_no, product_id, qty, unit_price) VALUES (
	1, 1, 1, 25, 676.76
);

INSERT INTO order_line (order_id, line_no, product_id, qty, unit_price) VALUES (
	1, 2, 2, 30, 676.76
);

INSERT INTO order_line (order_id, line_no, product_id, qty, unit_price) VALUES (
	1, 3, 3, 30, 676.76
);

INSERT INTO order_line (order_id, line_no, product_id, qty, unit_price) VALUES 
	(2, 1, 1, 50, 7000.00), 
	(2, 2, 2, 60, 7000.00), 
	(2, 3, 3, 60, 7000.00);

SELECT * FROM order_line;

UPDATE product
	SET unit_price = 67.00
	WHERE product_id = 1;

SELECT * FROM product;

DELETE FROM order_header
	WHERE order_id = 1 OR order_id = 2;

SELECT * FROM order_header;
SELECT * FROM order_line;

DELETE FROM product
	WHERE product_id = 1;
	
SELECT * FROM product;