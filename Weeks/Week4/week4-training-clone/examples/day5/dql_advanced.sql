SET SEARCH_PATH TO test2;

SELECT * FROM order_line;
SELECT * FROM order_header;

-- Scalar subquery: customer with highest total line quantity

SELECT c.email 
FROM customer c
WHERE c.customer_id IN (
	SELECT oh.customer_id
	FROM order_line ol
	JOIN order_header oh ON oh.order_id = ol.order_id
	GROUP BY oh.customer_id
	ORDER BY SUM(ol.qty) DESC
);

-- or instead if you want the total quantity returned
SELECT c.email,
	SUM(ol.qty) AS total_qty
FROM customer c
JOIN order_header oh on c.customer_id = oh.customer_id
JOIN order_line ol ON oh.order_id = ol.order_id
GROUP BY c.customer_id
ORDER BY total_qty DESC;

-- Aggregates + GROUP BY + HAVING: revenue per customer above a threshold

SELECT c.email,
	SUM(ol.qty * ol.unit_price) as REVENUE,
	COUNT(DISTINCT oh.order_id) AS order_count
FROM customer c
JOIN order_header oh on c.customer_id = oh.customer_id
JOIN order_line ol ON oh.order_id = ol.order_id
GROUP BY c.customer_id
HAVING SUM(ol.qty * ol.unit_price) >1000
ORDER BY REVENUE DESC;

-- to show difference of WHERE vs HAVING 

SELECT c.email,
	SUM(ol.qty * ol.unit_price) as REVENUE,
	COUNT(DISTINCT oh.order_id) AS order_count
FROM customer c
JOIN order_header oh on c.customer_id = oh.customer_id
JOIN order_line ol ON oh.order_id = ol.order_id
WHERE ol.qty>=2
GROUP BY c.customer_id
HAVING SUM(ol.qty * ol.unit_price) >500
ORDER BY REVENUE DESC;

-- subquery in FROM: per-order line counts (foundation for reporting)
SELECT oh.order_id,
	c.email,
	lc.line_count,
	SUM(ol.qty * ol.unit_price) AS order_revenue
FROM order_header oh
JOIN customer c ON c.customer_id = oh.customer_id
JOIN order_line ol ON ol.order_id = oh.order_id
JOIN( SELECT order_id, COUNT(*) AS line_count
	FROM order_line
	GROUP BY order_id
	) lc ON lc.order_id = oh.order_id
GROUP BY oh.order_id, c.email, lc.line_count;





