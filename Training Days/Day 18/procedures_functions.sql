SET SEARCH_PATH TO test1;

DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS customer CASCADE;

CREATE TABLE customer (
	customer_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	customer_name VARCHAR(100) NOT NULL,
	current_discount_rate NUMERIC(5,2) DEFAULT 0.0,
	discount_updated_at TIMESTAMP
);

CREATE TABLE orders (
	order_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	customer_id INT NOT NULL REFERENCES customer(customer_id) ON DELETE CASCADE,
	order_date DATE NOT NULL DEFAULT CURRENT_DATE,
	total_amount NUMERIC(10,2) NOT NULL CHECK (total_amount>0)
);

INSERT INTO customer(customer_name)
VALUES
('Alice Johnson'),
('Bob Smith'),
('Carol Davis');

INSERT INTO orders(customer_id, total_amount)
VALUES
(1,2500.00),
(1, 3500.00),
(2,5000.00),
(2,7500.00),
(3,300.00),
(3,300.00);

SELECT * FROM orders;
SELECT * FROM customer;

-- FUNCTION: CALCULATE DISCOUNT
CREATE OR REPLACE FUNCTION calculate_discount_rate(
	p_customer_id INT)
RETURNS NUMERIC(5,2)
LANGUAGE plpgsql
AS $$
DECLARE
	v_total_spent NUMERIC(10,2);
BEGIN
	SELECT COALESCE(SUM(total_amount),0)
	INTO v_total_spent
	FROM orders
	WHERE customer_id = p_customer_id;

	RETURN CASE
		WHEN v_total_spent >=10000 THEN 0.20
		WHEN v_total_spent >=5000 THEN 0.10
		WHEN v_total_spent >=1000 THEN 0.05
		ELSE 0.00
	END;
END;
$$;

SELECT calculate_discount_rate(2);

-- PROCEDURE: APPLY DISCOUNT TO CUSTOMER
CREATE OR REPLACE PROCEDURE apply_customer_discount(
	p_customer_id INT
)
LANGUAGE plpgsql
AS $$
DECLARE 
	v_discount_rate NUMERIC(5,2);
	v_exists BOOLEAN;
BEGIN
	-- check customer exists
	SELECT EXISTS (SELECT 1 FROM customer WHERE customer_id=p_customer_id)
	INTO v_exists;

	IF NOT v_exists THEN
		RAISE NOTICE 'Customer % not found', p_customer_id;
		RETURN;
	END IF;

	--calculate discount
	v_discount_rate := calculate_discount_rate(p_customer_id);

	--update customer
	UPDATE customer
	SET current_discount_rate = v_discount_rate,
		discount_updated_at = CURRENT_TIMESTAMP
	WHERE customer_id = p_customer_id;

	RAISE NOTICE
		'Customer % discount updated to %',
		p_customer_id,
		v_discount_rate *100;
END;
$$;

CALL apply_customer_discount(1);
CALL apply_customer_discount(2);
CALL apply_customer_discount(3);
SELECT * FROM customer;