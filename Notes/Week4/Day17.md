# Day 17 - PostgreSQL DDL, DML, Transactions & Normalization

---

## DDL — Data Definition Language

### Identity Column (modern PG auto-increment)

```sql
customer_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
-- prefer over SERIAL; DB always controls the value
```

### Data Types

```sql
TIMESTAMPTZ       -- timestamp with timezone (prefer over TIMESTAMP)
CHAR(2)           -- fixed-length string (pads with spaces if shorter)
NUMERIC(12, 2)    -- exact decimal; use for money
VARCHAR(255)      -- variable-length string
BOOLEAN           -- TRUE / FALSE
```

### Constraints

```sql
NOT NULL
UNIQUE
DEFAULT NOW()
DEFAULT TRUE
CHECK (unit_price >= 0)
CHECK (status IN ('OPEN', 'PAID', 'SHIPPED', 'CANCELLED'))
CHECK (country_code ~ '^[A-Z]{2}$')            -- regex check constraint (PostgreSQL only)
CONSTRAINT uq_customer_email UNIQUE (email)    -- named constraint (easier to DROP later)
```

### Foreign Keys & Referential Integrity

```sql
customer_id INTEGER NOT NULL REFERENCES customer(customer_id)
    ON UPDATE CASCADE   -- if PK changes, FK rows update automatically
    ON DELETE RESTRICT  -- block delete of parent if children exist
    ON DELETE CASCADE   -- delete children when parent is deleted
```

### Composite Primary Key

```sql
PRIMARY KEY (order_id, line_no)  -- two columns together uniquely identify the row
```

### Wrapping DDL in a Transaction

```sql
BEGIN;
DROP TABLE IF EXISTS order_line CASCADE;
CREATE TABLE ...;
COMMIT;   -- if anything fails mid-script, ROLLBACK undoes all DDL
```

---

## DML — Data Manipulation Language

### `DELETE` vs `TRUNCATE`

```sql
DELETE FROM customer;     -- logged row-by-row; supports WHERE; slower
TRUNCATE TABLE customer;  -- minimal logging; no WHERE; resets sequences; much faster
```

### `INSERT … SELECT`

```sql
INSERT INTO order_header(customer_id, status)
SELECT customer_id, 'OPEN'
FROM customer
WHERE email = 'ada@example.com';
```

### CTE in DML

```sql
WITH recent_order AS (
    SELECT oh.order_id FROM order_header oh
    JOIN customer c ON c.customer_id = oh.customer_id
    WHERE c.email = 'ada@example.com' AND oh.status = 'OPEN'
    ORDER BY oh.order_id DESC
),
mug AS (SELECT product_id, unit_price FROM product WHERE sku = 'MUG-01')
INSERT INTO order_line(order_id, line_no, product_id, qty, unit_price)
SELECT recent_order.order_id, 1, mug.product_id, 2, mug.unit_price
FROM recent_order, mug;
```

### `UPDATE` — always constrain with `WHERE`

```sql
UPDATE product
SET unit_price = 13.00,
    stock_qty  = stock_qty - 2          -- arithmetic in SET
WHERE sku = 'MUG-01' AND stock_qty >= 2;

-- UPDATE via subquery
UPDATE order_header
SET status = 'CANCELLED'
WHERE order_id = (
    SELECT order_id FROM order_header oh
    JOIN customer c ON c.customer_id = oh.customer_id
    WHERE c.email = 'ada@example.com'
    ORDER BY oh.order_id DESC LIMIT 1
);
```

### `ON CONFLICT … DO NOTHING` — Upsert

```sql
INSERT INTO customer (email, full_name)
VALUES ('txn-demo@example.com', 'Txn Demo')
ON CONFLICT (email) DO NOTHING;   -- silently skip if email already exists
```

### `UNION ALL`

```sql
SELECT 'SO-100', customer_id FROM tmp_customer WHERE email = 'ada@example.com'
UNION ALL
SELECT 'SO-101', customer_id FROM tmp_customer WHERE email = 'bob@example.com';
-- UNION removes duplicates; UNION ALL keeps them (faster)
```

---

## Transactions

```sql
BEGIN;
-- statements
COMMIT;    -- persist all changes atomically

BEGIN;
-- something goes wrong
ROLLBACK;  -- undo everything since BEGIN
```

### SAVEPOINT — partial rollback

```sql
BEGIN;
SAVEPOINT before_insert;
INSERT INTO order_header ...;

SAVEPOINT before_line_insert;
INSERT INTO order_line ...;
ROLLBACK TO SAVEPOINT before_line_insert;  -- undo only the line insert
                                            -- order_header insert is still pending
COMMIT;
```

Transactions are **ACID**:
- **A**tomic — all or nothing
- **C**onsistent — DB stays valid after transaction
- **I**solated — concurrent transactions don't see each other's partial work
- **D**urable — committed data survives crashes

---

## Data Normalization

### 1NF — Atomic Values

- No comma-separated lists in a cell (`'retail;priority'` → separate rows)
- Every row must be identifiable by a primary key

### 2NF — No Partial Dependencies *(only matters with a composite PK)*

- Every non-key column must depend on the **whole** composite PK, not just part of it
- e.g. PK is `(order_id, line_no)` — a `customer_name` that depends only on `order_id` is a partial dependency → extract it to an orders table

### 3NF — No Transitive Dependencies

- Non-key columns must depend directly on the PK, not on another non-key column
- e.g. `order_ref → email → name`: `name` depends on `email` (another non-key), not the PK → move `name` and `email` to a separate customer table, store only `customer_id` on the order

---

## SQL Exercises

### `LEFT JOIN` — keep all rows from left table

```sql
SELECT first_name, last_name, city, order_details
FROM customers c
LEFT JOIN orders o ON c.id = o.cust_id  -- customers with no orders still appear (NULL order cols)
ORDER BY first_name, order_details;
```

### `COUNT(DISTINCT col)` + `EXTRACT(MONTH …)`

```sql
SELECT client_id,
       COUNT(DISTINCT user_id) AS unique_users,
       EXTRACT(MONTH FROM time_id) AS month
FROM fact_events
GROUP BY month, client_id;
```

### Filter + `GROUP BY` + `ORDER BY`

```sql
SELECT department, COUNT(*) AS worker_count
FROM worker
WHERE joining_date >= '20140401'
GROUP BY department
ORDER BY worker_count DESC;
```
