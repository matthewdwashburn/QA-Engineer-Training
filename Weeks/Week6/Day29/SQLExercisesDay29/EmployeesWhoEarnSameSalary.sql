WITH salary_groups AS (SELECT salary FROM worker
    GROUP BY salary
    HAVING count(*) >= 2)
    SELECT w.worker_id, w.first_name, w.last_name, s.salary FROM worker w
        JOIN salary_groups s ON s.salary = w.salary;