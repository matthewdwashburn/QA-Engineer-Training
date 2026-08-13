# DQL: Data Query Language

## Learning Objectives

- Build queries with `SELECT`, `WHERE`, `ORDER BY`, `GROUP BY`, and `HAVING`.
- Use aggregate functions `COUNT`, `SUM`, `AVG`, `MIN`, and `MAX` correctly with grouping rules.
- Apply column and table **aliases** for readability and self-joins.
- Explain the logical order of query evaluation and why it matters (`WHERE` vs `HAVING`, alias scope).

## Why This Matters

**Reading** data is how you validate schemas, debug applications, and power reports. DQL is the busiest SQL category in analytics and application services. It connects directly to the week’s epic: after Monday’s schema and data, **joins** (today) and later **views/indexes** optimize how you retrieve it.

## The Concept

### Quick definition

**DQL** is the part of SQL used to **read** data. In practice it is mostly `SELECT` plus its clauses (joins, filtering, grouping, ordering, limiting).

### Logical query processing order (how the database “thinks”)

Even though you *write* `SELECT ... FROM ... WHERE ...`, the database conceptually evaluates in this order:

1) `FROM` / `JOIN` (build the row set)
2) `WHERE` (filter rows)
3) `GROUP BY` (form groups)
4) `HAVING` (filter groups)
5) `SELECT` (compute output columns/expressions)
6) `ORDER BY` (sort)
7) `LIMIT` / `OFFSET` (or vendor equivalents like `TOP`)

This explains common rules:

- `WHERE` cannot reference aggregates like `COUNT(*)` (they don’t exist until grouping happens).
- Some engines don’t let `WHERE` reference a `SELECT` alias (the alias is created later).

### SELECT and WHERE

`SELECT` projects columns (or expressions). `WHERE` filters **rows before** aggregation.

```sql
SELECT id, email
FROM customer
WHERE country_code = 'US';
```

### ORDER BY

Sorts the result. Use explicit `ASC`/`DESC`; multiple keys for stable business ordering.

```sql
SELECT * FROM order_header
ORDER BY placed_at DESC, id ASC;
```

### GROUP BY and aggregates

**Aggregates** collapse rows into summaries. Non-aggregated columns in `SELECT` must appear in `GROUP BY` (standard SQL) or be functionally dependent—rules vary slightly by vendor and `ONLY_FULL_GROUP_BY` modes.

```sql
SELECT department_id, COUNT(*) AS headcount
FROM employee
GROUP BY department_id;
```

#### COUNT(*) vs COUNT(column)

- `COUNT(*)` counts rows (including rows where some columns are NULL).
- `COUNT(column)` counts only rows where that column is **not NULL**.

### HAVING

Filters **groups** after aggregation—use for conditions on aggregates. `WHERE` cannot reference `COUNT(*)` in standard SQL.

```sql
SELECT department_id, COUNT(*) AS headcount
FROM employee
GROUP BY department_id
HAVING COUNT(*) >= 5;
```

### Aliases

```sql
SELECT c.email AS customer_email
FROM customer AS c;
```

### DISTINCT (deduplication)

`DISTINCT` removes duplicate rows from the result set:

```sql
SELECT DISTINCT country_code
FROM customer;
```

Be careful: `DISTINCT` applies to the **entire selected row** (all selected columns), not just one column unless you select only that column.

### NULL basics in query results

- `NULL` means “missing/unknown,” not zero and not empty string.
- Comparisons with `NULL` use `IS NULL` / `IS NOT NULL` (not `= NULL`).

```sql
SELECT *
FROM customer
WHERE phone IS NULL;
```

## Code Example

```sql
SELECT date_trunc('month', placed_at) AS month,
       COUNT(*) AS orders,
       SUM(total_amount) AS revenue
FROM order_header
WHERE status = 'COMPLETED'
GROUP BY date_trunc('month', placed_at)
HAVING SUM(total_amount) > 10000
ORDER BY month;
```

*(Note: `date_trunc` is PostgreSQL-style; use your engine’s equivalent for production portability.)*

## Summary

- **WHERE** filters rows; **HAVING** filters grouped results.
- Aggregates summarize per **GROUP BY** key; know your engine’s rules for selected columns.
- **Aliases** clarify queries and enable concise join syntax.
- Remember the logical order: `FROM` → `WHERE` → `GROUP BY` → `HAVING` → `SELECT` → `ORDER BY` → `LIMIT`.

## Additional Resources

- [PostgreSQL SELECT reference](https://www.postgresql.org/docs/current/sql-select.html)
- [MySQL SELECT syntax](https://dev.mysql.com/doc/refman/8.0/en/select.html)
- [SQLite SELECT](https://www.sqlite.org/lang_select.html)
