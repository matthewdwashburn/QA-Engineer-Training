# Creating, Modifying, and Dropping Views

## Learning Objectives

- Create and remove views with `CREATE VIEW` and `DROP VIEW`.
- Understand `CREATE OR REPLACE` (where supported) vs explicit drop/recreate.
- Distinguish **updatable** vs **read-only** views and why updates are restricted.

## Why This Matters

Views appear in migrations, reporting layers, and security designs. Knowing **how** to evolve a view—and when updates through a view are allowed—prevents broken dependencies and unsafe write paths.

## The Concept

### CREATE VIEW

```sql
CREATE VIEW v_order_totals AS
SELECT order_id, SUM(line_total) AS total
FROM order_line
GROUP BY order_id;
```

Some engines support:

```sql
CREATE OR REPLACE VIEW v_order_totals AS
SELECT order_id, SUM(line_total) AS total
FROM order_line
GROUP BY order_id;
```

`OR REPLACE` avoids a separate `DROP` when the shape is compatible enough for your tooling.

### ALTER VIEW

Vendor-dependent. PostgreSQL uses `ALTER VIEW` for renaming or changing owner; changing the query often uses `CREATE OR REPLACE`. SQL Server uses `ALTER VIEW` as the primary evolution mechanism. **Check your DB docs** before assuming portability.

### DROP VIEW

```sql
DROP VIEW IF EXISTS v_order_totals;
```

Dropping a view **does not** delete base-table data. Dependent objects may block the drop unless `CASCADE` is specified (PostgreSQL style).

### Updatable vs read-only views

A view is **updatable** when the engine can map `INSERT`/`UPDATE`/`DELETE` on the view to **unambiguous** changes on **one** base table—rules vary. Views with:

- aggregates (`GROUP BY`, `SUM`),
- `DISTINCT`,
- joins (in many products),
- unions,

are typically **not** updatable. Some systems offer **INSTEAD OF** triggers on views to custom-handle writes.

```sql
-- Often updatable (simple projection on one table)
CREATE VIEW v_customer_email AS
SELECT id, email FROM customer;

UPDATE v_customer_email SET email = 'new@example.com' WHERE id = 1;
```

### WITH CHECK OPTION (concept)

Some databases support `WITH CHECK OPTION` to ensure updates through a view cannot create rows that no longer satisfy the view’s filter.

```sql
-- Example concept (syntax support varies by engine)
CREATE VIEW v_us_customer AS
SELECT id, email, country_code
FROM customer
WHERE country_code = 'US'
WITH CHECK OPTION;
```

Without a check option, some engines may allow an update via the view that changes `country_code` to a non-US value, causing the row to “disappear” from the view after the update—surprising behavior.

## Summary

- **CREATE VIEW** names a stored query; **DROP VIEW** removes the definition only.
- **CREATE OR REPLACE** / **ALTER VIEW** patterns are **vendor-specific**—verify migrations on target engines.
- **Updatability** is limited for complex views; prefer updating **base tables** or explicit triggers/procedures when in doubt.

## Additional Resources

- [PostgreSQL CREATE VIEW](https://www.postgresql.org/docs/current/sql-createview.html)
- [SQL Server CREATE VIEW](https://learn.microsoft.com/en-us/sql/t-sql/statements/create-view-transact-sql)
- [MySQL View Updatability](https://dev.mysql.com/doc/refman/8.0/en/view-updatability.html)
