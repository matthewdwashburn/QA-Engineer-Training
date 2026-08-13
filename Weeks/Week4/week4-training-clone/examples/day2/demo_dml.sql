DELETE FROM customer;
-- TRUNCATE TABLE customer;
DELETE FROM product;

SELECT * FROM customer;

-- INSERT: explicit columns, multi-row
INSERT INTO customer (email, full_name, country_code)
VALUES 
	('ada@example.com','Ada Lovelace','US'),
	('bob@example.com', 'Bob Noyce','US');

INSERT INTO product (sku, name, unit_price, stock_qty)
VALUES
	('MUG-01','Ceramic Mug',12.50,100),
	('TEE-01','TRaining T-Shirt',24.00,40);

INSERT INTO order_header(customer_id, status)
	SELECT customer_id,'OPEN'
	FROM customer
	WHERE email = 'ada@example.com';

-- INSERT lines for the order we just created (CTE keeps intent obvious)
WITH recent_order AS (
	SELECT oh.order_id
	FROM order_header oh
	JOIN customer c on c.customer_id = oh.customer_id
	WHERE c.email = 'ada@example.com' 
	AND oh.status= 'OPEN' 
	ORDER BY oh.order_id DESC
),
mug AS (
	SELECT product_id, unit_price FROM product WHERE sku='MUG-01'
)
INSERT INTO order_line (order_id, line_no, product_id, qty, unit_price)
SELECT recent_order.order_id, 1, mug.product_id,2, mug.unit_price
FROM recent_order, mug;

-- UPDATE: always constrain - bump price for one SKU only
UPDATE product
SET unit_price = 13.00,
	stock_qty = stock_qty-2
WHERE sku = 'MUG-01'
	AND stock_qty>=2;

-- UPDATE with join-like predicate via subquery (common pattern)
UPDATE order_header
SET status = 'CANCELLED'
	WHERE order_id = (
	SELECT order_id FROM order_header oh
	JOIN customer c ON c.customer_id = oh.customer_id
	WHERE c.email='ada@example.com'
	ORDER BY oh.order_id DESC
	LIMIT 1
	);

-- DELETE: dangerous without WHERE 
DELETE FROM order_line
WHERE order_id = (
	SELECT order_id FROM order_header oh
	JOIN customer c ON c.customer_id = oh.customer_id
	WHERE c.email = 'ada@example.com'
	AND oh.status='CANCELLED'
);


SELECT * FROM customer;
SELECT * FROM product;
SELECT * FROM order_header;
SELECT * FROM order_line;



