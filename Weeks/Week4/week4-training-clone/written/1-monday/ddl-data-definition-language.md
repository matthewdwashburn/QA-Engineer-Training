# DDL: Data Definition Language

## Learning Objectives

- Use `CREATE`, `ALTER`, `DROP`, and `TRUNCATE` for schema and object lifecycle.
- Explain the difference between removing structure vs removing rows.
- Apply each statement to realistic design tasks without mixing semantics.

## Why This Matters

**DDL** is how you **shape** the database: tables, indexes, views, constraints. Application code depends on that shape. Mistakes in DDL propagate to every query and integration point in the weekly epic—from raw SQL through JDBC and ORMs.

## The Concept

### CREATE

**CREATE** defines new objects: databases/schemas, tables, indexes, views, procedures, etc.

```sql
CREATE TABLE customer (
  id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  email VARCHAR(255) NOT NULL
);
```

Use `CREATE` when bootstrapping environments, migrations, or new features.

### ALTER

**ALTER** changes existing objects **without** dropping them (when possible): add/drop columns, change types (with care), add constraints.

```sql
ALTER TABLE customer
  ADD COLUMN phone VARCHAR(32);

ALTER TABLE customer
  DROP COLUMN phone;
```

**Caution:** Type changes and constraint additions may **lock** tables or **fail** if existing data violates the new rule. Plan for maintenance windows and validation scripts.

### DROP

**DROP** removes an object from the catalog (table, view, index, etc.). This is **structural** removal.

```sql
DROP TABLE customer;
```

**Effect:** Metadata and usually **data** are gone (unless your product supports recycle-bin style features—non-standard). Dependencies (foreign keys, views) may block `DROP` until removed.

### TRUNCATE

**TRUNCATE** removes **all rows** from a table quickly, typically **without** row-by-row logging semantics of `DELETE` (implementation-dependent). It usually **resets** storage for the table more aggressively than `DELETE`.

```sql
TRUNCATE TABLE staging_import;
```

**Contrast with `DELETE`:** `DELETE` can have a `WHERE` clause and is DML; `TRUNCATE` targets the whole table and is classified as **DDL** in many systems. **Foreign keys** referencing the table often prevent `TRUNCATE` until dependencies are handled.

## When to Use Which

| Goal | Statement |
|------|-----------|
| New table | `CREATE TABLE` |
| Add/rename column, add FK | `ALTER TABLE` |
| Remove table entirely | `DROP TABLE` |
| Empty table fast, keep structure | `TRUNCATE TABLE` |
| Remove some rows | `DELETE` (DML—covered with DML readings) |

## Summary

- **CREATE** defines objects; **ALTER** evolves them; **DROP** removes objects.
- **TRUNCATE** clears data while keeping the table—fast, whole-table, DDL-class in most engines.
- Always consider **dependencies**, **locks**, and **data loss** before running DDL in shared environments.

## Additional Resources

- [PostgreSQL DDL statements](https://www.postgresql.org/docs/current/ddl.html)
- [MySQL CREATE TABLE](https://dev.mysql.com/doc/refman/8.0/en/create-table.html)
- [SQLite CREATE TABLE](https://www.sqlite.org/lang_createtable.html)
