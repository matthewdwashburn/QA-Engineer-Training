-- The marketing team wants to identify high-value customers for a premium loyalty program. 
-- Find all customers who have placed at least one order over $100. Return customer ID and name.
-- Consider all orders regardless of their payment or fulfillment status.

SELECT DISTINCT c.customer_id, c.customer_name from online_store_customers c
    JOIN online_store_orders o ON c.customer_id = o.customer_id
    WHERE o.amount > 100;