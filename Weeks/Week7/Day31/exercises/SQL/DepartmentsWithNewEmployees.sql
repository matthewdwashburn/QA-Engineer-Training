/*
For each department with 5 or more employees hired after 2020, 
return the name, headcount, total payroll, and average salary.
*/

SELECT department AS name, 
    COUNT(DISTINCT id) AS headcount, 
    SUM(salary) AS total_payroll,
    AVG(salary) AS average_salary
    FROM techcorp_workforce
    GROUP BY department
    HAVING COUNT(CASE WHEN YEAR(joining_date) >= 2020 THEN 1 ELSE NULL END) >= 5;