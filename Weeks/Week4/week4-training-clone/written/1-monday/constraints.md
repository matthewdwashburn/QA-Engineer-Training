# SQL Constraints

## Learning Objectives

- Apply `NOT NULL`, `UNIQUE`, `CHECK`, and `DEFAULT` to enforce column rules.
- Define **primary** and **foreign** keys and explain their roles in relational design.
- Predict how constraint violations surface in applications and migrations.
- Distinguish constraints from indexes (and how they relate).

## Why This Matters

Constraints move **business rules** closer to the data: the database rejects impossible states even if an application bug ships. That is essential for **integrity** across JDBC, Python, and multiple services talking to one database—the core of reliable relational systems.

## The Concept

### A quick definition

A **constraint** is a rule the database enforces so invalid data cannot be stored. When a statement violates a constraint, the DB rejects the statement (typically with an error). This is different from “validation in the UI,” which can be bypassed by another client or a bug.

### Constraints vs indexes (common confusion)

- **Constraint**: correctness rule (e.g., “email must be unique”, “qty must be > 0”).
- **Index**: performance structure that speeds lookups/joins/sorts.

Important relationship:

- Many databases implement **PRIMARY KEY** and **UNIQUE** constraints by creating a **unique index** behind the scenes.
- A unique index can sometimes exist without being declared as a constraint (dialect-specific), but for schema clarity and portability, prefer expressing **business identity rules as constraints**.

### NOT NULL

Requires a value on every insert/update (unless a `DEFAULT` supplies one).

```sql
CREATE TABLE product (
  sku VARCHAR(32) NOT NULL,
  name VARCHAR(200) NOT NULL
);
```

### UNIQUE

Ensures no two rows share the same value (or combination) in the constrained column(s). **NULL** handling varies: in standard SQL, multiple NULLs are often allowed in unique columns.

```sql
CREATE TABLE app_user (
  id INT PRIMARY KEY,
  email VARCHAR(255) UNIQUE
);
```

Composite (multi-column) uniqueness example:

```sql
-- One employee number per department (but numbers can repeat across departments)
CREATE TABLE employee (
  id INT PRIMARY KEY,
  dept_id INT NOT NULL,
  employee_no INT NOT NULL,
  CONSTRAINT uq_employee_dept_no UNIQUE (dept_id, employee_no)
);
```

### CHECK

Arbitrary boolean predicate per row (or table-level in some engines).

```sql
CREATE TABLE order_line (
  qty INT NOT NULL CHECK (qty > 0),
  discount_pct DECIMAL(5,2) NOT NULL CHECK (discount_pct BETWEEN 0 AND 100)
);
```

### DEFAULT

Value used when insert omits the column.

```sql
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
```

### PRIMARY KEY

Uniquely identifies a row; implies **UNIQUE** + **NOT NULL** (typically one per table). Underpins joins and ORM identity.

```sql
CREATE TABLE department (
  dept_id INT PRIMARY KEY,
  name VARCHAR(100) NOT NULL
);
```

### FOREIGN KEY

Links a column to a **primary** or **unique** key in another (or same) table, enforcing **referential integrity**. Deletion/update behavior is controlled with `ON DELETE` / `ON UPDATE` actions (covered in the referential integrity reading).

```sql
CREATE TABLE employee (
  id INT PRIMARY KEY,
  dept_id INT NOT NULL REFERENCES department(dept_id)
);
```

### What constraint failures look like in practice

- **On INSERT**: “duplicate key value violates unique constraint …” (UNIQUE/PK), “null value in column … violates not-null constraint …” (NOT NULL), “violates check constraint …” (CHECK), “violates foreign key constraint …” (FK).
- **On UPDATE**: same as above, plus FK issues when changing the parent key (if allowed) or the child FK value.
- **During migrations** (`ALTER TABLE ... ADD CONSTRAINT`): adding a constraint can fail if existing data already violates the rule. Safer approach is often: clean data first → add constraint second.

## Code Example

```sql
CREATE TABLE customer (
  id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  email VARCHAR(255) NOT NULL UNIQUE,
  country_code CHAR(2) NOT NULL DEFAULT 'US' CHECK (char_length(country_code) = 2)
);
```

## Summary

- **NOT NULL** and **DEFAULT** shape required vs optional fields.
- **UNIQUE** and **PRIMARY KEY** enforce identity and alternate keys.
- **CHECK** encodes simple domain rules in the database.
- **FOREIGN KEY** connects tables and prevents dangling references (with actions defining cascade behavior).
- Constraints enforce correctness; indexes primarily improve performance (though UNIQUE/PK often use indexes internally).

## Additional Resources

- [PostgreSQL Constraints](https://www.postgresql.org/doc/current/ddl-constraints.html)
- [MySQL CREATE TABLE constraints](https://dev.mysql.com/doc/refman/8.0/en/create-table.html)
- [SQLite Constraints](https://www.sqlite.org/lang_createtable.html)
