# Referential Integrity and Foreign Key Actions

## Learning Objectives

- Explain how foreign keys enforce relationships between tables.
- Choose appropriate `ON DELETE` and `ON UPDATE` actions: `CASCADE`, `SET NULL`, `RESTRICT` / `NO ACTION`, and others where supported.
- Anticipate operational impact (cascading deletes, nullability requirements).
- Use safe patterns for deletes/updates in the presence of foreign keys.

## Why This Matters

Without referential integrity, databases accumulate **orphan rows**—order lines without orders, employees without departments. Applications then break in subtle ways. Foreign keys are the database’s contract that relationships stay consistent, which matters whether you access the DB from SQL scripts, JDBC, or ORMs.

## The Concept

A **foreign key (FK)** declares that values in child column(s) must match existing values in parent **primary** or **unique** key column(s). Inserts/updates on the child that violate this fail; behavior on parent changes is defined by **referential actions**.

### Parent/child vocabulary

- **Parent table**: the referenced table (the one with the PK/UNIQUE key).
- **Child table**: the referencing table (the one with the FK column(s)).

Example: `order_header` is the parent; `order_line(order_id)` is the child.

### Common actions

| Action | On parent delete/update | Typical use |
|--------|-------------------------|-------------|
| **RESTRICT** / **NO ACTION** | Reject if child rows exist | Protect parent from accidental removal |
| **CASCADE** | Propagate delete/update to children | Strong ownership (line items belong to order) |
| **SET NULL** | Set child FK columns to NULL | Optional relationship; child can survive without parent |
| **SET DEFAULT** | Set to a default value (if supported) | Less common; requires DEFAULT on FK column |

**SET NULL** requires the FK column to be **nullable**. **CASCADE** deletes can remove large subtrees—use with care and backups.

### ON DELETE vs ON UPDATE

- **ON DELETE CASCADE:** Deleting a parent deletes children—classic for `order` → `order_line`.
- **ON UPDATE CASCADE:** If parent key changes (rare for surrogate keys), children update—more relevant to natural keys.

Surrogate **integer/bigint** keys with `IDENTITY`/`SERIAL` rarely change; natural keys (e.g., country code rename) may need `ON UPDATE CASCADE`.

### RESTRICT vs NO ACTION (practical note)

In many databases, **RESTRICT** and **NO ACTION** behave similarly for immediate constraints: they prevent deleting/updating the parent when children exist. Some systems distinguish them when constraints can be **deferred** (checked at transaction end). If your engine supports **DEFERRABLE** constraints (not universal), `NO ACTION` can allow temporarily breaking the relationship inside a transaction as long as it is fixed before `COMMIT`.

### Safe “delete” patterns (often better than CASCADE)

When business rules say “don’t really delete,” consider a **soft delete**:

```sql
ALTER TABLE customer
  ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

-- Instead of DELETE FROM customer WHERE id = ...;
UPDATE customer
SET is_deleted = TRUE
WHERE id = 42;
```

Soft deletes avoid accidental cascades and preserve history, but require filtering in queries (and sometimes partial indexes).

Another safe pattern is “delete children first, then parent” inside a transaction:

```sql
BEGIN;
  DELETE FROM order_line WHERE order_id = 10;
  DELETE FROM order_header WHERE id = 10;
COMMIT;
```

This keeps control in your hands and makes the intent explicit.

## Code Example

```sql
CREATE TABLE department (
  id INT PRIMARY KEY,
  name VARCHAR(100) NOT NULL
);

CREATE TABLE employee (
  id INT PRIMARY KEY,
  name VARCHAR(200) NOT NULL,
  dept_id INT,
  CONSTRAINT fk_employee_dept
    FOREIGN KEY (dept_id) REFERENCES department(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE
);
```

Stricter example for order lines:

```sql
CREATE TABLE order_header (
  id INT PRIMARY KEY
);

CREATE TABLE order_line (
  id INT PRIMARY KEY,
  order_id INT NOT NULL,
  CONSTRAINT fk_line_order
    FOREIGN KEY (order_id) REFERENCES order_header(id)
    ON DELETE CASCADE
);
```

## Summary

- Foreign keys enforce **valid references** from child to parent.
- **RESTRICT** protects parents; **CASCADE** maintains dependent rows automatically; **SET NULL** preserves children when the link is optional.
- Design actions from **business rules**: is the child owned by the parent, or merely associated?
- Prefer explicit, safe patterns (soft delete or delete-children-then-parent) when cascades are risky.

## Additional Resources

- [PostgreSQL Foreign Keys](https://www.postgresql.org/docs/current/ddl-constraints.html#DDL-CONSTRAINTS-FK)
- [MySQL FOREIGN KEY Constraints](https://dev.mysql.com/doc/refman/8.0/en/create-table-foreign-keys.html)
- [SQLite Foreign Keys](https://www.sqlite.org/foreignkeys.html)
