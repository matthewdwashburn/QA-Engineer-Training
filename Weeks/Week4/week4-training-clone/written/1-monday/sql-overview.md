# SQL Overview

## Learning Objectives

- Describe what SQL is and how it relates to relational databases.
- Explain the relational model in plain terms (tables, rows, columns, keys).
- Recognize ANSI SQL standards and why portability matters.
- Classify SQL statements into DDL, DML, DQL, DCL, and TCL.

## Why This Matters

Almost every business application stores data in a relational database. SQL is the **lingua franca** for defining that data, changing it, and asking questions of it. This week’s epic—**relational data mastery from schema to code**—starts here: you cannot integrate applications with databases until you speak SQL fluently at the level of standards and categories.

## The Concept

**SQL** (Structured Query Language) is a declarative language for managing **relational** data. You describe *what* you want (e.g., “rows where status is active”), not *how* the engine should scan storage—though optimization (indexes, plans) becomes important later in the week.

The **relational model** (Codd) organizes data in **relations** (tables). Each **row** (tuple) is one fact or entity instance; each **column** (attribute) is a named field with a defined type. **Primary keys** identify rows; **foreign keys** link tables. This structure reduces duplication and keeps updates consistent when designed well.

**ANSI/ISO SQL** defines a portable core. Vendors (MySQL, PostgreSQL, Oracle, SQL Server, SQLite) implement the standard plus **extensions** (syntax, functions, types). Writing toward standard SQL improves portability; using vendor features trades portability for power.

SQL statements are grouped by purpose:

| Category | Meaning | Typical statements |
|----------|---------|--------------------|
| **DDL** (Data Definition Language) | Define or change structure | `CREATE`, `ALTER`, `DROP`, `TRUNCATE` |
| **DML** (Data Manipulation Language) | Change or load data | `INSERT`, `UPDATE`, `DELETE` |
| **DQL** (Data Query Language) | Read data | Primarily `SELECT` (often taught with DML) |
| **DCL** (Data Control Language) | Permissions | `GRANT`, `REVOKE` |
| **TCL** (Transaction Control Language) | Boundaries of work units | `COMMIT`, `ROLLBACK`, `SAVEPOINT` |

*Note:* Some materials fold `SELECT` under DML; separating **DQL** highlights read vs write operations, which matters for security, replication, and performance tuning.

## Code Example

```sql
-- DDL: define structure
CREATE TABLE department (
  id INT PRIMARY KEY,
  name VARCHAR(100) NOT NULL
);

-- DML: add and change data
INSERT INTO department (id, name) VALUES (1, 'Engineering');
UPDATE department SET name = 'Eng' WHERE id = 1;

-- DQL: read data
SELECT id, name FROM department WHERE id = 1;

-- TCL: finish or undo a unit of work (exact syntax may vary by vendor)
COMMIT;
-- ROLLBACK;
```

DCL example (permissions vary by product):

```sql
-- Conceptual: grant read on a table to a role
-- GRANT SELECT ON department TO analyst_role;
```

## Summary

- SQL is the standard way to work with relational databases: structure, data, queries, permissions, and transactions.
- The relational model uses tables, rows, columns, and keys to represent entities and relationships.
- ANSI SQL is the portability baseline; each vendor adds features you should label when you use them.
- **DDL** shapes the database; **DML** and **DQL** manipulate and read data; **DCL** controls access; **TCL** groups changes into atomic units.

## Additional Resources

- [ISO/IEC SQL standard overview (Wikipedia)](https://en.wikipedia.org/wiki/SQL) — high-level history and standard editions.
- [PostgreSQL SQL commands (official)](https://www.postgresql.org/docs/current/sql-commands.html) — categorized command list with links.
- [SQLite SQL syntax (official)](https://www.sqlite.org/lang.html) — compact reference for learning core syntax.
