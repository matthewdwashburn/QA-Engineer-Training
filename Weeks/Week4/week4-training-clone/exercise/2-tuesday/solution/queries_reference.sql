-- Instructor reference — PostgreSQL. Adjust names if trainees use Monday schema variants.

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

-- 2 LEFT: all customers + latest order id
SELECT c.email,
       recent.order_id
FROM customer c
LEFT JOIN LATERAL (
    SELECT oh.order_id
    FROM order_header oh
    WHERE oh.customer_id = c.customer_id
    ORDER BY oh.placed_at DESC, oh.order_id DESC
    LIMIT 1
) recent ON TRUE
ORDER BY c.email;

-- 3 RIGHT: equivalent to left with flip
SELECT c.email,
       oh.order_id
FROM order_header oh
RIGHT JOIN customer c ON c.customer_id = oh.customer_id;

-- 4 FULL OUTER
SELECT c.email, oh.order_id, oh.status
FROM customer c
FULL OUTER JOIN order_header oh ON oh.customer_id = c.customer_id
ORDER BY c.email NULLS LAST, oh.order_id NULLS LAST;

-- 5 CROSS — example with tiny VALUES dimension (trainees may vary)
SELECT p.sku, d.flag
FROM product p
CROSS JOIN (VALUES ('LISTED'), ('FEATURED')) AS d(flag);

-- 6 HAVING
SELECT c.email,
       COUNT(DISTINCT oh.order_id) AS order_count,
       SUM(ol.qty * ol.unit_price) AS total_spend
FROM customer c
JOIN order_header oh ON oh.customer_id = c.customer_id
JOIN order_line ol ON ol.order_id = oh.order_id
GROUP BY c.customer_id, c.email
HAVING SUM(ol.qty * ol.unit_price) > 25
ORDER BY total_spend DESC;
