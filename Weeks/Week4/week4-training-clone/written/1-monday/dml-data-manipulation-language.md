# DML: Data Manipulation Language

## Learning Objectives

- Write `INSERT`, `UPDATE`, and `DELETE` with clear, safe patterns.
- Use bulk insert techniques appropriate to your platform.
- Explain why a **precise `WHERE` clause** is critical on `UPDATE` and `DELETE`.

## Why This Matters

DML is how applications **persist** business events: new users, changed addresses, removed carts. Mistakes affect **production data**. This ties directly to the week’s epic—after DDL defines shape, DML **fills and maintains** the story the database tells, before you query it with DQL on Tuesday.

## The Concept

### INSERT

Adds new rows. Specify columns explicitly for clarity and resilience to schema changes.

```sql
INSERT INTO customer (email, name)
VALUES ('a@example.com', 'Ada');

-- Multiple rows (standard SQL)
INSERT INTO customer (email, name)
VALUES ('b@example.com', 'Bob'),
       ('c@example.com', 'Chen');
```

**Bulk loads:** Use vendor tools (`COPY` in PostgreSQL, `LOAD DATA` in MySQL, `bcp` in SQL Server) for large files—much faster than row-by-row inserts from app code when appropriate.

### UPDATE

Changes existing rows. **Always** constrain with `WHERE` unless you truly intend to touch every row.

```sql
UPDATE customer
SET name = 'Ada Lovelace'
WHERE email = 'a@example.com';
```

Without `WHERE`, you update **all** rows—a common incident during onboarding.

### DELETE

Removes rows. Same rule: **`WHERE` is mandatory** for targeted deletes.

```sql
DELETE FROM customer
WHERE id = 42;
```

For removing **all** rows while keeping the table, prefer **`TRUNCATE`** (DDL) when FK rules allow—faster and clearer intent.

### WHERE clause importance

- **Precision:** Limits blast radius.
- **Index use:** Predictable predicates help performance (indexes covered later in the week).
- **Safety:** In production, run `SELECT` with the same `WHERE` first to preview affected rows; use transactions (TCL—Tuesday) to allow `ROLLBACK`.

### Subqueries and joins in DML

Some systems allow `UPDATE`/`DELETE` with joins/subqueries; syntax varies. Prefer **simple** patterns or `WITH` CTEs for readability and portability.

## Code Example

```sql
BEGIN;
  UPDATE inventory
  SET qty = qty - 1
  WHERE sku = 'WIDGET-01' AND qty >= 1;

  INSERT INTO shipment_log (sku, qty, shipped_at)
  VALUES ('WIDGET-01', 1, CURRENT_TIMESTAMP);
COMMIT;
```

*(Transaction keywords are TCL; we use them here only to show a safe unit of work—you will study TCL in depth on Tuesday.)*

## Summary

- **INSERT** adds rows; list columns explicitly; use bulk tools for large data.
- **UPDATE** and **DELETE** must usually include a **`WHERE`**; verify with `SELECT` first in sensitive environments.
- **TRUNCATE** vs **DELETE** differs in scope (whole table vs filtered rows) and mechanics (DDL vs DML).

## Additional Resources

- [PostgreSQL INSERT/UPDATE/DELETE](https://www.postgresql.org/docs/current/dml.html)
- [MySQL DML syntax](https://dev.mysql.com/doc/refman/8.0/en/sql-data-manipulation-statements.html)
- [SQLite Data Manipulation](https://www.sqlite.org/lang.html)
