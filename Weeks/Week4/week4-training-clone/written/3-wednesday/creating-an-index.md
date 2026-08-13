# Creating an Index

## Learning Objectives

- Write `CREATE INDEX` with common options (unique, composite, sort direction).
- Choose sensible columns to index based on predicates and join keys.
- Use **`EXPLAIN`** (or your engine’s plan command) to verify index use.

## Why This Matters

Creating an index is easy; creating the **right** indexes requires understanding **workloads**. This reading ties the week’s query patterns (Tuesday) to **physical design** decisions you will practice in exercises and demos.

## The Concept

### Basic syntax

```sql
CREATE INDEX idx_name ON table_name (column_name);
```

Naming convention tip (team readability)

- Use consistent prefixes like `idx_` (non-unique), `uq_` (unique), and include table + column(s) in the name.
- Example: `idx_order_header_customer_id`.

### Composite and ordering

```sql
CREATE INDEX idx_orders_cust_date ON order_header (customer_id, placed_at DESC);
```

Leading column `customer_id` supports filters on `customer_id` alone; adding `placed_at` helps range/sort within a customer.

### Unique indexes

```sql
CREATE UNIQUE INDEX uq_line ON order_line (order_id, line_no);
```

### Partial indexes (PostgreSQL example)

Index only hot subsets:

```sql
CREATE INDEX idx_open_orders ON order_header (customer_id)
WHERE status = 'OPEN';
```

(MySQL/SQL Server have filtered index equivalents with different syntax.)

### Choosing columns

Strong candidates:

- **Primary** and **foreign keys** used in joins.
- Columns in frequent **`WHERE`**, **`ORDER BY`**, **`GROUP BY`** clauses.
- Columns used for **uniqueness** rules.

Weak candidates:

- Low-cardinality flags alone (sometimes still useful in combinations).
- Columns rarely filtered.
- **Wide** values (long text) unless using specialized indexes.

### Don’t forget: indexes can change write behavior

- `UPDATE` on an indexed column is often more expensive than updating a non-indexed column (the index entry must move/change).
- Too many indexes can cause slow inserts and lock contention during bulk loads.

### Analyzing query plans

After creating an index, run `EXPLAIN` on representative queries. Look for **index scans/seeks** vs **sequential scans**. Plans depend on **table size** and **statistics**—small tables may still scan because it is cheaper.

```sql
EXPLAIN SELECT * FROM order_header WHERE customer_id = 123;
```

Exact command varies: `EXPLAIN ANALYZE` (PostgreSQL), `EXPLAIN FORMAT=TREE` (MySQL 8+), etc.

### Dropping an index (cleanup)

If an index is unused or harmful, remove it deliberately:

```sql
DROP INDEX idx_customer_email;
```

Syntax can vary (some engines require `DROP INDEX idx_name ON table_name`). Always verify in your target DB.

## Summary

- **`CREATE INDEX`** adds secondary access paths; **composite** column order must match common predicates.
- **Unique** and **partial/filtered** indexes solve specific integrity and performance niches.
- Always **validate** with execution plans on realistic data volumes.

## Additional Resources

- [PostgreSQL CREATE INDEX](https://www.postgresql.org/docs/current/sql-createindex.html)
- [MySQL CREATE INDEX](https://dev.mysql.com/doc/refman/8.0/en/create-index.html)
- [SQLite EXPLAIN QUERY PLAN](https://www.sqlite.org/eqp.html)
