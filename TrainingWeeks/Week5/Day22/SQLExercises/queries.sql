SET search_path TO exercises;

SELECT * FROM order_line;

WITH order_line_revenue AS (
	SELECT order_id, SUM(qty * unit_price) AS revenue
	FROM order_line
	GROUP BY order_id
)

-- INNER JOIN
SELECT c.email, h.order_id, h.placed_at, r.revenue
	FROM order_header h
	JOIN customer c ON c.customer_id = h.customer_id
	JOIN order_line_revenue r ON r.order_id = h.order_id
	WHERE h.status = 'PAID';

-- 1 INNER: paid orders with line revenue
SELECT c.email,
       oh.order_id,
       oh.placed_at,
       SUM(ol.qty * ol.unit_price) AS line_revenue
FROM customer c
JOIN order_header oh ON oh.customer_id = c.customer_id
JOIN order_line ol ON ol.order_id = oh.order_id
WHERE oh.status = 'PAID'
GROUP BY c.email, oh.order_id, oh.placed_at
ORDER BY oh.order_id;

-- LEFT JOIN
SELECT c.*, recent.order_id FROM customer c
	LEFT JOIN LATERAL (
		SELECT h.order_id 
			FROM order_header h
			WHERE h.customer_id = c.customer_id
			ORDER BY h.placed_at DESC, h.order_id DESC
			LIMIT 1
	) recent ON TRUE
	ORDER BY c.email;

-- RIGHT JOIN all customers
SELECT c.email, oh.order_id FROM order_header oh
	RIGHT JOIN customer c ON c.customer_id = oh.customer_id;

-- FULL OUTER JOIN
SELECT c.*, oh.* FROM customer c
	FULL OUTER JOIN order_header oh ON c.customer_id = oh.customer_id;

-- CROSS JOIN
SELECT * FROM customer
	CROSS JOIN (VALUES ('STOCK_GOOD'), ('STOCK_OK'), ('STOCK_BAD')) AS status_table(status_desc);

-- Aggregate, HAVING
SELECT c.email, oh.customer_id, COUNT(DISTINCT oh.order_id) AS order_count, SUM(ol.qty * ol.unit_price) AS total_spend
	FROM order_line ol
	JOIN order_header oh ON ol.order_id = oh.order_id
	JOIN customer c ON c.customer_id = oh.customer_id
	GROUP BY oh.customer_id, c.email
	HAVING SUM(ol.qty * ol.unit_price) > 25;

	
