SELECT * FROM product;


--Rollback
BEGIN;

UPDATE product SET stock_qty = stock_qty -1 WHERE sku = 'BASE-A' AND stock_qty >=1;

ROLLBACK;

COMMIT;

--COMMIT persists a multi-step business action

SELECT * FROM customer;

BEGIN;
INSERT INTO customer (email, full_name) VALUES ('txn-demo@example.com','Txn Demo') ON 
CONFLICT (email) DO NOTHING;

UPDATE product
SET stock_qty =stock_qty -1
WHERE sku='BASE-A' AND stock_qty>=1;

COMMIT;

SELECT * FROM order_header;
SELECT * FROM order_line;

--SAVEPOINT
BEGIN;
SAVEPOINT before_insert;

INSERT INTO order_header(customer_id, status)
SELECT customer_id, 'OPEN' FROM customer WHERE email = 'txn-demo@example.com';

-- ROLLBACK TO SAVEPOINT before_insert;

SAVEPOINT before_order_line_insert;

INSERT INTO order_line (order_id, line_no, product_id, qty, unit_price)
SELECT oh.order_id, 99, p.product_id, 1, p.unit_price
FROM order_header oh
JOIN customer c ON c.customer_id = oh.customer_id
JOIN product p ON p.sku = 'BASE-A'
WHERE c.email = 'txn-demo@example.com'
ORDER BY oh.order_id DESC
LIMIT 1;

ROLLBACK TO SAVEPOINT before_order_line_insert;

COMMIT;



