SET search_path to public;

CREATE TABLE employees (
	employee_id SERIAL PRIMARY KEY,
	first_name VARCHAR(50) NOT NULL,
	last_name VARCHAR(50) NOT NULL,
	email VARCHAR(100) UNIQUE,
	hire_date DATE DEFAULT CURRENT_DATE,
	salary DECIMAL(10,2)
);

INSERT INTO employees (first_name, last_name, email, salary) VALUES ('Bob','Johnson','bob@company.com',65000.00),
('Carol','Williams','carol@company.com',80_000.00),
('David','Brown','david@company.com',70_000.00);

-- SELECT all employees;
SELECT * FROM employees;

-- SELECT specific columns
SELECT first_name, last_name, salary FROM employees;

-- Filter with WHERE
SELECT * FROM employees WHERE salary > 70000;

--Order results
SELECT * FROM employees ORDER BY salary DESC;


