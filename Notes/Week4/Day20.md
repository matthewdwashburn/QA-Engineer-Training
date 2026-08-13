# Day 20 - Advanced SQL: DDL Constraints, DQL Subqueries & Transaction Isolation

---

## DDL — Advanced Constraints (PostgreSQL)

### `GENERATED ALWAYS AS IDENTITY` — auto-increment PK (PostgreSQL-native)

```sql
customer_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
-- preferred over SERIAL in modern PostgreSQL
```

### `NUMERIC(precision, scale)` — exact fixed-point number

```sql
unit_price NUMERIC(12, 2) NOT NULL CHECK (unit_price >= 0)
-- 12 total digits, 2 after decimal — good for money
```

### `TIMESTAMPTZ` — timestamp with time zone

```sql
created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
```

### `CHECK` constraint with regex `~`

```sql
country_code CHAR(2) NOT NULL DEFAULT 'US'
    CHECK (country_code ~ '^[A-Z]{2}$')
-- ~ is PostgreSQL regex match operator
```

### Named constraints

```sql
CONSTRAINT uq_customer_email UNIQUE (email)
-- naming makes error messages more readable
```

### Composite PRIMARY KEY

```sql
PRIMARY KEY (order_id, line_no)
-- two columns together form the unique row identifier
```

### Referential integrity actions on FK

```sql
customer_id INTEGER NOT NULL REFERENCES customer (customer_id)
    ON UPDATE CASCADE    -- if PK changes, FK rows update automatically
    ON DELETE RESTRICT;  -- prevent deleting a customer who has orders

order_id INTEGER NOT NULL REFERENCES order_header (order_id)
    ON DELETE CASCADE    -- deleting an order auto-deletes its lines
```

| Action | Behavior |
|---|---|
| `CASCADE` | propagate the change to child rows |
| `RESTRICT` | block the operation if child rows exist |
| `SET NULL` | set FK column to NULL in child rows |

### `SET search_path TO "schema"` — target a specific schema

```sql
SET search_path TO "public 2";
```

### Transaction block

```sql
BEGIN;
-- statements
COMMIT;   -- or ROLLBACK; to undo
```

---

## DQL — Subqueries & Multi-Table Aggregation

### Subquery in `WHERE … IN (…)` — filter by a derived set

```sql
SELECT c.email
FROM customer c
WHERE c.customer_id IN (
    SELECT oh.customer_id
    FROM order_line ol
    JOIN order_header oh ON oh.order_id = ol.order_id
    GROUP BY oh.customer_id
    ORDER BY SUM(ol.qty) DESC
);
```

### Subquery in `FROM` — inline derived table (no CTE needed)

```sql
SELECT oh.order_id, lc.line_count
FROM order_header oh
JOIN (
    SELECT order_id, COUNT(*) AS line_count
    FROM order_line
    GROUP BY order_id
) lc ON lc.order_id = oh.order_id;
-- the subquery acts like a named temp table
```

### `WHERE` vs `HAVING`

- `WHERE` filters **before** grouping — operates on individual rows
- `HAVING` filters **after** grouping — operates on aggregate results

```sql
SELECT c.email, SUM(ol.qty * ol.unit_price) AS revenue
FROM customer c
JOIN order_header oh ON c.customer_id = oh.customer_id
JOIN order_line ol ON oh.order_id = ol.order_id
WHERE ol.qty >= 2                          -- row-level filter (pre-group)
GROUP BY c.customer_id
HAVING SUM(ol.qty * ol.unit_price) > 500  -- aggregate filter (post-group)
ORDER BY revenue DESC;
```

### `COUNT(DISTINCT col)` across multiple CTEs — avoid double-counting

```sql
WITH l_table AS (
    SELECT company_code, COUNT(DISTINCT lead_manager_code) AS cnt
    FROM lead_manager GROUP BY company_code
), e_table AS (
    SELECT company_code, COUNT(DISTINCT employee_code) AS cnt
    FROM employee GROUP BY company_code
)
SELECT c.company_code, l.cnt, e.cnt
FROM company c
JOIN l_table l ON l.company_code = c.company_code
JOIN e_table e ON e.company_code = c.company_code;
-- DISTINCT inside each CTE prevents duplicates from hierarchical joins
```

### `CASE` in SELECT + `CASE` in ORDER BY

```sql
SELECT
    CASE WHEN g.grade >= 8 THEN s.name ELSE NULL END AS name,
    g.grade,
    s.marks
FROM students s
JOIN grades g ON s.marks BETWEEN g.min_mark AND g.max_mark
ORDER BY
    g.grade DESC,
    CASE WHEN g.grade >= 8 THEN s.name END,       -- sort by name for A grades
    CASE WHEN g.grade < 8  THEN s.marks END;      -- sort by marks for low grades
-- separate CASE expressions in ORDER BY for branching sort logic
```

### `JOIN … ON … BETWEEN` — range-based join (no explicit FK)

```sql
JOIN grades g ON s.marks BETWEEN g.min_mark AND g.max_mark
```

---

## Transaction Isolation Levels

Isolation levels control what concurrent transactions can see from each other. Higher isolation = safer reads, lower throughput.

### Read Phenomena

| Phenomenon | Description |
|---|---|
| **Dirty Read** | Read uncommitted changes from another transaction (PostgreSQL never allows this — min level is READ COMMITTED) |
| **Non-repeatable Read** | Same row read twice in one transaction returns different values because another transaction committed between reads |
| **Phantom Read** | Same query run twice returns different rows because another transaction inserted/deleted rows between reads |

### Isolation Levels (least → most isolated)

| Level | Dirty Read | Non-repeatable Read | Phantom Read |
|---|---|---|---|
| `READ UNCOMMITTED` | possible | possible | possible |
| `READ COMMITTED` *(Postgres default)* | blocked | possible | possible |
| `REPEATABLE READ` | blocked | blocked | possible |
| `SERIALIZABLE` | blocked | blocked | blocked |

### Setting isolation level

```sql
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;
BEGIN;
SELECT balance FROM accounts WHERE account_id = 1;
-- another transaction can't change this row until we COMMIT
COMMIT;

SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
BEGIN;
SELECT * FROM accounts WHERE balance > 600;
-- no phantom rows can appear; but may cause transaction failures requiring retry
COMMIT;
```

- `SERIALIZABLE` is the safest but has a performance cost and can cause transactions to fail and need to be re-run.

---

## Notes

- Multiple filter conditions: `WHERE col1 = x AND col2 = y`, `HAVING agg1 > x AND agg2 > y`
- Code iteratively — check each new statement works before adding more; when it fails, don't panic, could just be a missed semicolon
- Keep going back to the requirements — you probably missed something
