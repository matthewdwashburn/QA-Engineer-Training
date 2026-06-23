-- https://platform.stratascratch.com/coding/9847-find-the-number-of-workers-by-department?code_type=1

select department, COUNT(*) AS worker_count from worker
WHERE joining_date >= '20140401'
GROUP BY department
ORDER BY worker_count DESC;