# Day 16 - SQLAlchemy & PostgreSQL

---

## SQLAlchemy — Python DB Toolkit

### Setup

```python
from os import getenv
from dotenv import load_dotenv
from sqlalchemy import create_engine, text

load_dotenv()
CS = getenv("CS")           # reads connection string from .env
engine = create_engine(CS)
```

**.env format** — keep credentials out of code:
```
CS = "postgresql://user:password@localhost:5432/dbname"
```

### Read SQL → DataFrame

```python
import pandas as pd

df = pd.read_sql("SELECT * FROM employees;", engine)
```

### Write DataFrame → Table

```python
df.to_sql(
    name="processed",      # target table name
    con=engine,
    if_exists='replace',   # 'replace' drops+recreates; 'append' adds rows; 'fail' errors
    index=False            # don't write the DataFrame index as a column
)
```

### Parameterized INSERT with `text()` + `engine.begin()`

```python
with engine.begin() as conn:   # transaction — auto-commits on success, rolls back on exception
    results = conn.execute(
        text("""
            INSERT INTO employees(first_name, last_name, email, hire_date, salary)
            VALUES (:first_name, :last_name, :email, :hire_date, :salary)
            RETURNING employees.employee_id
        """),
        {
            "first_name": first_name,
            "last_name": last_name,
            "email": email,
            "hire_date": hire_date,
            "salary": salary
        }
    )
    employee_id = results.scalar()   # extract single value from result
```

- `text()` — wraps raw SQL for SQLAlchemy; enables named params (`:param_name`)
- `engine.begin()` — context manager: commits on exit, rolls back on exception
- `results.scalar()` — returns first column of first row as a single value
- `RETURNING col` — PostgreSQL: returns generated values (e.g. auto-increment PK) after INSERT

---

## SQL Exercises

### `EXTRACT()` — pull part of a date

```sql
EXTRACT(YEAR FROM inspection_date) AS inspection_year
EXTRACT(MONTH FROM time_id) AS month

-- Combine year + month as a string
EXTRACT(YEAR FROM shipment_date) || '-' || EXTRACT(MONTH FROM shipment_date) AS year_month
```

`||` — string concatenation in PostgreSQL.

### `WHERE` with `OR`

```sql
SELECT * FROM lyft_drivers WHERE yearly_salary <= 30000 OR yearly_salary >= 70000;
```

### `COUNT(*)` + `GROUP BY` + `ORDER BY`

```sql
SELECT EXTRACT(YEAR FROM inspection_date) AS inspection_year, COUNT(*) AS n_inspections
FROM sf_restaurant_health_violations
WHERE business_id = 500
GROUP BY inspection_year
ORDER BY inspection_year;
```

---

## PostgreSQL Schema Patterns

```sql
CREATE TABLE customers (
    customer_id SERIAL PRIMARY KEY,           -- auto-increment PK
    email       VARCHAR(100) UNIQUE NOT NULL,
    created_at  TIMESTAMP DEFAULT NOW()       -- default to current time
);

CREATE TABLE products (
    price          DECIMAL(10, 2) NOT NULL CHECK (price >= 0),    -- check constraint
    stock_quantity INTEGER DEFAULT 0 CHECK (stock_quantity >= 0)
);

CREATE TABLE orders (
    customer_id INTEGER NOT NULL REFERENCES customers(customer_id)  -- FK
);

CREATE TABLE order_items (
    order_id INTEGER NOT NULL REFERENCES orders(order_id) ON DELETE CASCADE
);

CREATE INDEX idx_orders_customer ON orders(customer_id);   -- index FK columns for performance
```

### Multi-table JOIN with string concat

```sql
SELECT c.first_name || ' ' || c.last_name AS customer,
       o.order_id, o.status,
       p.name AS product,
       oi.quantity * oi.unit_price AS line_total
FROM customers c
JOIN orders o     ON c.customer_id = o.customer_id
JOIN order_items oi ON o.order_id = oi.order_id
JOIN products p   ON oi.product_id = p.product_id
ORDER BY o.order_id;
```

### `GROUP BY` with aggregates — all non-aggregated SELECT columns must appear in GROUP BY

```sql
SELECT o.order_id,
       COUNT(oi.item_id) AS items,
       o.total_amount
FROM orders o
JOIN order_items oi ON o.order_id = oi.order_id
GROUP BY o.order_id, o.total_amount;
```

### `SET search_path TO public`

Sets the default schema so table names don't need a prefix:

```sql
SET search_path TO public;
```
