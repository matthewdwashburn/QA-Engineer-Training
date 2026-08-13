# TCL: Transaction Control Language

## Learning Objectives

- Use `COMMIT` and `ROLLBACK` to end transactions successfully or abort.
- Apply `SAVEPOINT` for partial rollback within a transaction.
- Understand **auto-commit** mode and how APIs (JDBC, ORMs) expose transaction boundaries.
- Define what a transaction boundary is and why “implicit transactions” surprise people.

## Why This Matters

**TCL** turns a sequence of statements into a **business unit**. Misunderstanding auto-commit causes “half done” money transfers or orphaned rows. Thursday’s JDBC material builds directly on these concepts—`setAutoCommit`, `commit`, and `rollback` map to what you learn here.

## The Concept

### Quick definition

A **transaction** is a unit of work the database treats as a single “all-or-nothing” operation. A **transaction boundary** is where that unit begins and ends (begin → commit/rollback).

Different clients hide boundaries differently. If you don’t explicitly manage them, you can accidentally run each statement as its own transaction.

### COMMIT

Makes all changes in the current transaction **permanent** (subject to durability guarantees). Locks release according to isolation rules.

```sql
COMMIT;
```

### ROLLBACK

Undoes all changes in the current transaction **since the last commit** (or since session start), discarding uncommitted work.

```sql
ROLLBACK;
```

### SAVEPOINT

Marks a point inside a transaction; you can **`ROLLBACK TO SAVEPOINT`** without ending the whole transaction.

```sql
BEGIN;
  INSERT INTO order_header (id) VALUES (100);
  SAVEPOINT after_header;
  INSERT INTO order_line (order_id, sku) VALUES (100, 'BAD-SKU'); -- fails business rule
  ROLLBACK TO SAVEPOINT after_header;
  INSERT INTO order_line (order_id, sku) VALUES (100, 'GOOD-SKU');
COMMIT;
```

Support and syntax for `SAVEPOINT` are widespread in major engines; verify edge cases with nested savepoints on your platform.

### BEGIN / START TRANSACTION (explicit start)

Some engines use:

- `BEGIN;` (PostgreSQL, SQLite)
- `START TRANSACTION;` (MySQL)
- SQL Server often uses implicit transactions unless configured; `BEGIN TRANSACTION;` is common T-SQL

Core idea: make the boundary explicit when multiple statements must succeed or fail together.

### SET TRANSACTION

Some systems allow declaring **isolation level**, **read-only**, or **deferrable** modes at transaction start:

```sql
-- Illustrative; exact options vary
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
```

### Auto-commit mode

Many clients default to **one statement = one transaction** (auto-commit **on**). For multi-step operations, disable auto-commit, run statements, then **commit** once—this is how JDBC and other drivers model explicit transactions.

### Errors, retries, and “transaction aborted” behavior

Two practical rules:

- If a statement fails inside a transaction, you often need to decide: **fix and continue** (possibly using a `SAVEPOINT`) or **ROLLBACK** the whole transaction.
- Under high isolation (especially `SERIALIZABLE`) some engines may abort a transaction due to concurrency conflicts (e.g., serialization failure). Correct application behavior is typically to **retry** the transaction, not to “half-retry” one statement.

### Locking and duration (performance + correctness)

- Transactions hold locks or snapshots. **Long transactions** increase contention and can block others.
- Keep transactions **short** and avoid user input “inside” a transaction (e.g., don’t start a transaction, wait for a user to click “confirm,” then commit).

## Summary

- **COMMIT** persists work; **ROLLBACK** discards uncommitted work.
- **SAVEPOINT** enables **partial** undo while keeping the outer transaction open.
- **SET TRANSACTION** configures isolation/read-only behavior where supported; **auto-commit** hides boundaries unless you manage them in code.
- Explicit boundaries + short transactions prevent “half done” workflows and reduce locking pain.

## Additional Resources

- [PostgreSQL TRANSACTION](https://www.postgresql.org/docs/current/sql-begin.html)
- [MySQL COMMIT / ROLLBACK](https://dev.mysql.com/doc/refman/8.0/en/commit.html)
- [SQLite Transaction Control](https://www.sqlite.org/lang_transaction.html)
