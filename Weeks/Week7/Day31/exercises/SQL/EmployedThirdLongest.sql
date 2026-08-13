/*
You are given a table from a database and you have to find out which 
employee has been employed with the company the 3rd longest.
*/
SELECT * FROM employees
    ORDER BY months_employed DESC
    LIMIT 1 OFFSET 2; -- Offset important new DQL statement