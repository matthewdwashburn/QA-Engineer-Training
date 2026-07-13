PRAGMA foreign_keys = ON;

DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS roles;

CREATE TABLE IF NOT EXISTS roles (
role_id INTEGER PRIMARY KEY AUTOINCREMENT,
role_title TEXT NOT NULL UNIQUE,
role_salary INTEGER NOT NULL);


INSERT INTO roles (role_title,role_salary) VALUES 
("Employee", 85000),
("Manager", 100000);


CREATE TABLE IF NOT EXISTS employees(
employee_id INTEGER PRIMARY KEY AUTOINCREMENT,
first_name TEXT NOT NULL,
last_name TEXT NOT NULL,
role_id_fk INTEGER NOT NULL,
FOREIGN KEY (role_id_fk) REFERENCES roles(role_id)
);

SELECT * FROM employees;

SELECT * FROM roles;
