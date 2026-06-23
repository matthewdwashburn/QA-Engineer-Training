-- https://platform.stratascratch.com/coding/9728-inspections-that-resulted-in-violations?code_type=1

SELECT EXTRACT(YEAR FROM inspection_date) AS inspection_year, COUNT(*) AS n_inspections
FROM sf_restaurant_health_violations
WHERE business_id = 500
GROUP BY inspection_year
ORDER BY inspection_year;