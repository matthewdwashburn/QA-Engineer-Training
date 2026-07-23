/*
You are given a table from a database, and it's your job to find the 5 
best selling products from that table in order by most to least amount sold.
*/
SELECT name, amount_sold FROM products
    ORDER BY amount_sold DESC
    LIMIT 5;