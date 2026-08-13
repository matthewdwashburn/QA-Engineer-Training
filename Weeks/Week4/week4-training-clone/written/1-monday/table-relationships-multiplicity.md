# Table Relationships and Multiplicity

## Learning Objectives

- Model **one-to-one**, **one-to-many**, and **many-to-many** relationships in SQL.
- Use **junction** (associative) tables for many-to-many cases.
- Represent optional vs mandatory participation at the schema level.
- Map relationship rules to concrete schema tools (`FOREIGN KEY`, `NOT NULL`, `UNIQUE`).

## Why This Matters

Multiplicity errors in schema design cause duplicated data, impossible queries, or broken ORM mappings. The epic’s path—from normalized tables to joins and later ORM relationships—assumes you can **draw** and **implement** cardinality correctly.

## The Concept

### Quick definition

**Multiplicity/cardinality** describes “how many” rows on one side can relate to rows on the other side (1:1, 1:N, M:N). **Participation/optionality** describes whether a relationship is required (mandatory) or can be missing (optional).

In SQL, you encode these rules using:

- **FOREIGN KEY**: enforces that a reference (when present) points to a real parent row.
- **NOT NULL** on FK: makes the relationship mandatory for the child row.
- **UNIQUE** on FK (or shared PK): enforces at-most-one child per parent (1:1).

### One-to-many (1:N)

The most common pattern: one parent row relates to **many** child rows; each child points to **one** parent via a foreign key on the **many** side.

*Example:* One `department` has many `employees`; each `employee` has one `department_id`.

```sql
CREATE TABLE department (id INT PRIMARY KEY);
CREATE TABLE employee (
  id INT PRIMARY KEY,
  department_id INT NOT NULL REFERENCES department(id)
);
```

Interpretation:

- A department can have 0..N employees (nothing forces “at least one employee”).
- An employee must have exactly 1 department because `department_id` is `NOT NULL`.

### One-to-one (1:1)

Each row on side A matches at most one row on side B. Often modeled by **sharing the same primary key** or a **unique foreign key**.

*Example:* `user` and `user_profile`—profile is optional extension data.

```sql
CREATE TABLE app_user (
  id INT PRIMARY KEY
);

CREATE TABLE user_profile (
  user_id INT PRIMARY KEY REFERENCES app_user(id),
  bio TEXT
);
```

`user_id` as **both** PK and FK enforces 1:1 from profile → user. Add `UNIQUE` on FK in other patterns when the FK lives on the “optional” side.

Alternative 1:1 pattern (unique FK):

```sql
CREATE TABLE app_user (
  id INT PRIMARY KEY
);

CREATE TABLE user_profile (
  id INT PRIMARY KEY,
  user_id INT NOT NULL UNIQUE REFERENCES app_user(id),
  bio TEXT
);
```

### Many-to-many (M:N)

Neither table can hold the FK alone without repetition. Use a **junction table** with two foreign keys, often forming a **composite primary key** or a surrogate key plus **unique** pair constraint.

*Example:* `student` **enrolled in** `course`.

```sql
CREATE TABLE student (id INT PRIMARY KEY);
CREATE TABLE course (id INT PRIMARY KEY);

CREATE TABLE enrollment (
  student_id INT NOT NULL REFERENCES student(id),
  course_id INT NOT NULL REFERENCES course(id),
  enrolled_at DATE NOT NULL,
  PRIMARY KEY (student_id, course_id)
);
```

Attributes of the relationship itself (e.g., `enrolled_at`, grade) belong on the junction table.

### Optional vs mandatory

- **Mandatory child link:** `NOT NULL` FK (child must point to a parent).
- **Optional child link:** nullable FK (child may point to a parent, but isn’t required to).
- **Optional 1:1 extension:** omit the extension row entirely (profile table row exists only when needed).

#### “Mandatory parent participation” (advanced note)

SQL constraints easily enforce “every child has a parent,” but “every parent must have at least one child” is not enforced by a simple FK alone. That rule typically requires:

- application logic,
- triggers, or
- periodic checks (reports/tests),

depending on how strict your domain must be.

## Summary

- **1:N:** FK on the “many” table pointing to the “one” table.
- **1:1:** Shared PK or `UNIQUE` FK to enforce single pairing.
- **M:N:** Junction table with two FKs (and optional payload columns for the association).
- Optionality is primarily expressed via FK nullability (`NOT NULL` vs nullable).

## Additional Resources

- [Database Design — Relationships (IBM)](https://www.ibm.com/docs/en/i/7.4?topic=design-database-relationships)
- [PostgreSQL Tutorial: Joins (context for modeling)](https://www.postgresql.org/docs/current/tutorial-join.html)
- [Use The Index, Luke: Analogy of junction tables](https://use-the-index-luke.com/sql/join)
