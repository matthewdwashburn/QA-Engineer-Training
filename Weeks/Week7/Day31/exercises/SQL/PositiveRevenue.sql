/*	
You are given a table from a database, and it's your job to find out which departments 
have had a total positive revenue throughout all the years reported.
*/
SELECT department_id, FORM department_revenue
    GROUP BY id
    HAVING SUM(revenue) > 0;
    