-- https://platform.stratascratch.com/coding/10308-salaries-differences?code_type=1
WITH max_marketing AS (
    SELECT MAX(e.salary) as max_m FROM db_employee e
        JOIN db_dept d ON d.id = e.department_id
        GROUP BY d.id, d.department
        HAVING d.department = 'marketing'
), max_engineering AS (
    SELECT MAX(e.salary) as max_e FROM db_employee e
        JOIN db_dept d ON d.id = e.department_id
        GROUP BY d.id, d.department
        HAVING d.department = 'engineering'
)

SELECT ABS(e.max_e - m.max_m) FROM max_marketing m
    CROSS JOIN max_engineering e;
