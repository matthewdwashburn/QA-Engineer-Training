-- Only call with once, make sure in each company code group by bucket each worker code only appears once with distinct, careful when copy pasting
WITH l_table as (
    SELECT company_code, COUNT(DISTINCT lead_manager_code) AS cnt FROM lead_manager
    GROUP BY company_code
), s_table as (
    SELECT company_code, COUNT(DISTINCT senior_manager_code) AS cnt FROM senior_manager
    GROUP BY company_code
), m_table as (
    SELECT company_code, COUNT(DISTINCT manager_code) AS cnt FROM manager
    GROUP BY company_code
), e_table as (
    SELECT company_code, COUNT(DISTINCT employee_code) AS cnt FROM employee
    GROUP BY company_code
)
SELECT c.company_code, c.founder, l.cnt, s.cnt, m.cnt, e.cnt
    FROM company c
    JOIN l_table l ON l.company_code = c.company_code
    JOIN s_table s ON s.company_code = c.company_code
    JOIN m_table m ON m.company_code = c.company_code
    JOIN e_table e ON e.company_code = c.company_code
    ORDER BY c.company_code;


