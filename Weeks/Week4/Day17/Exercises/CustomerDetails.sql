-- https://platform.stratascratch.com/coding/9891-customer-details?code_type=1

SELECT first_name, last_name, city, order_details FROM customers c   
    LEFT JOIN orders o ON c.id = o.cust_id
    ORDER BY first_name, order_details;