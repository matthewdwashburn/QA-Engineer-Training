-- Unnormalized / non - 1NF : "tags" is a list is one cell (repeating group)
DROP TABLE IF EXISTS tmp_orders_denorm;
CREATE TEMP TABLE tmp_orders_denorm (
	order_ref TEXT PRIMARY KEY,
	customer TEXT NOT NULL,
	tags TEXT NOT NULL,  -- e.g 'retail;priority'
	product_a TEXT,
	qty_a INTEGER,
	product_b TEXT,
	qty_b INTEGER,
	customer_email TEXT,
	name TEXT
	);

INSERT INTO tmp_orders_denorm VALUES 
	('SO-100','ada@example.com','retail;priority','SKU-1',2,'SKU-2',1,'ada@example.com','Ada'),
	('SO-101','bob@example.com','wholesale','SKU-3',5,NULL, NULL, 'bob@example.com','Bob');

SELECT * FROM tmp_orders_denorm;

-- 1NF: atomic tags (one tag per fow) an line-level grain for products
DROP TABLE IF EXISTS tmp_order_tags_1nf;
CREATE TEMP TABLE tmp_order_tags_1nf(
	order_ref TEXT NOT NULL,
	tag TEXT NOT NULL,
	PRIMARY KEY (order_ref, tag)
);

INSERT INTO tmp_order_tags_1nf VALUES
('SO-100','retail'),
('SO-100','priority'),
('SO-101','wholesale');

DROP TABLE IF EXISTS tmp_order_lines_1nf;
CREATE TEMP TABLE tmp_order_lines_1nf (
	order_ref TEXT NOT NULL,
	line_no INTEGER NOT NULL,
	sku TEXT NOT NULL,
	qty INTEGER NOT NULL,
	PRIMARY KEY (order_ref,line_no),
	email TEXT,
	name TEXT
	);

INSERT INTO tmp_order_lines_1nf VALUES
('SO-100',1,'SKU-1',2, 'ada@example.com','Ada'),
('SO-100',2,'SKU-2',1,'ada@example.com','Ada'),
('SO-101',1,'SKU-3',5, 'bob@example.com','Bob');

SELECT * FROM tmp_order_tags_1nf;
SELECT * FROM tmp_order_lines_1nf;
--in 1nf now

--2NF: remove partial dependency -tag should not depend on only part 
-- of a composite key, if we have (order_ref,line_no) ->sku->product_name

DROP TABLE IF EXISTS tmp_product_2nf CASCADE;
CREATE TEMP TABLE tmp_product_2nf (
	sku TEXT PRIMARY KEY,
	tag TEXT NOT NULL
);

INSERT INTO tmp_product_2nf VALUES
	('SKU-1','retail'),
	('SKU-2','priority'),
	('SKU-3','wholesale');

--lines now reference SKU; product name lives only on product (not duplicated per line row conceptually)
DROP TABLE IF EXISTS tmp_order_lines_2nf CASCADE;
CREATE TEMP TABLE tmp_order_lines_2nf (
	order_ref TEXT NOT NULL,
	line_no INTEGER NOT NULL,
	sku TEXT NOT NULL REFERENCES tmp_product_2nf (sku),
	qty INTEGER NOT NULL,
	PRIMARY KEY (order_ref, line_no),
	email TEXT,
	customer_name TEXT
	);

INSERT INTO tmp_order_lines_2nf VALUES
('SO-100',1,'SKU-1',2,'ada@example.com','Ada'),
('SO-100',2,'SKU-2',1,'ada@example.com','Ada'),
('SO-101',1,'SKU-3',5,'bob@example.com','Bob');

SELECT * FROM tmp_product_2nf;
SELECT * FROM tmp_order_lines_2nf;

-- 3NF: Remove transitive dependency
-- order_ref -> email -> name
-- Therefore name belongs with the customer, not the order.

DROP TABLE IF EXISTS tmp_customer_3nf CASCADE;
CREATE TEMP TABLE tmp_customer_3nf (
    customer_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email TEXT NOT NULL UNIQUE,
    name TEXT NOT NULL
);

INSERT INTO tmp_customer_3nf (email, name) VALUES
('ada@example.com', 'Ada'),
('bob@example.com', 'Bob');


DROP TABLE IF EXISTS tmp_order_header_3nf CASCADE;
CREATE TEMP TABLE tmp_order_header_3nf (
    order_ref TEXT PRIMARY KEY,
    customer_id INTEGER NOT NULL
        REFERENCES tmp_customer_3nf(customer_id)
);

INSERT INTO tmp_order_header_3nf (order_ref, customer_id)
SELECT 'SO-100', customer_id
FROM tmp_customer_3nf
WHERE email = 'ada@example.com'

UNION ALL

SELECT 'SO-101', customer_id
FROM tmp_customer_3nf
WHERE email = 'bob@example.com';


DROP TABLE IF EXISTS tmp_order_lines_3nf CASCADE;
CREATE TEMP TABLE tmp_order_lines_3nf (
    order_ref TEXT NOT NULL
        REFERENCES tmp_order_header_3nf(order_ref),
    sku TEXT NOT NULL
        REFERENCES tmp_product_2nf(sku),
    qty INTEGER NOT NULL,
    PRIMARY KEY (order_ref, sku)
);

INSERT INTO tmp_order_lines_3nf VALUES
('SO-100', 'SKU-1', 2),
('SO-100', 'SKU-2', 1),
('SO-101', 'SKU-3', 5);


SELECT * FROM tmp_customer_3nf;
SELECT * FROM tmp_order_header_3nf;
SELECT * FROM tmp_order_lines_3nf;
SELECT * FROM tmp_product_2nf;


SELECT
    oh.order_ref,
    c.email,
    c.name,
    ol.sku,
    ol.qty
FROM tmp_order_header_3nf oh
JOIN tmp_customer_3nf c
    ON c.customer_id = oh.customer_id
JOIN tmp_order_lines_3nf ol
    ON ol.order_ref = oh.order_ref
ORDER BY oh.order_ref, ol.sku;
	