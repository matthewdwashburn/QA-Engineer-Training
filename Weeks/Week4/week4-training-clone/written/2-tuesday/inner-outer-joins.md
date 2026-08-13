# INNER JOIN and FULL OUTER JOIN

## Learning Objectives

- Explain how an **INNER JOIN** keeps only **matching** rows from both sides.
- Describe **FULL OUTER JOIN** as preserving **all** rows from **both** tables with NULLs where no match exists.
- Choose between them based on whether unmatched rows matter to the question.
- Define a **join key** and a **join condition**, and predict row-count changes.

## Why This Matters

Joins are how relational databases **assemble** a normalized model back into useful shapes. Picking the wrong join type silently drops rows or duplicates them—classic sources of bugs in reporting and application queries. This sits at the center of **querying related data** in the weekly epic.

## The Concept

### Definitions you need before writing joins

- **Join key**: the column(s) used to relate rows (e.g., `employee.department_id` ↔ `department.id`).
- **Join condition**: the boolean expression that defines a match (usually equality on key columns): `ON e.department_id = d.id`.
- **Match**: a pair of rows (one from each table) that satisfies the join condition.

Joins can **multiply rows**. If one department matches 10 employees, the department row appears 10 times in the result (once per match). That is correct, but it surprises people who expected “one row per department.”

### INNER JOIN

Returns rows where the **join condition** is true in **both** tables. Unmatched rows from either side are **discarded**.

*Analogy:* Only guests who actually RSVP’d **and** appear on the door list get into the event—everyone else is filtered out.

```sql
SELECT e.name, d.name AS department
FROM employee e
INNER JOIN department d ON e.department_id = d.id;
```

Employees without a valid `department_id` (or with a nonexistent id) **do not appear**. Departments with **no** employees **do not appear**.

### FULL OUTER JOIN

Returns **all** rows from **left** and **right**. Where a match exists, columns are filled from both sides. Where no match exists, the missing side’s columns are **NULL**.

```sql
SELECT e.name, d.name AS department
FROM employee e
FULL OUTER JOIN department d ON e.department_id = d.id;
```

You might see employees with `NULL` department (bad data or optional link) and departments with `NULL` employee (empty department).

**Portability note:** MySQL does not support `FULL OUTER JOIN` natively; teams commonly emulate it by `UNION`-ing a left join and a right join (or two left joins with swapped table order).

Example emulation pattern:

```sql
-- FULL OUTER JOIN emulation (conceptual)
SELECT e.name, d.name AS department
FROM employee e
LEFT JOIN department d ON e.department_id = d.id

UNION

SELECT e.name, d.name AS department
FROM employee e
RIGHT JOIN department d ON e.department_id = d.id;
```

*(Exact syntax and performance characteristics vary by engine; this is for understanding, not a universal “best practice.”)*

## When to Use Which

| Need | Join |
|------|------|
| Only facts that exist on **both** sides | `INNER JOIN` |
| Include **orphans** on either side in one result | `FULL OUTER JOIN` |
| Typical app queries for valid relationships | Usually `INNER JOIN` |

## Summary

- **INNER JOIN** is intersection on the join key—strict matching.
- **FULL OUTER JOIN** is union with pairing—unmatched sides appear with NULLs.
- Wrong join choice changes **row counts** and **business meaning**; validate with small test data.
- Know your join key and expect row multiplication on 1-to-many relationships.

## Additional Resources

- [PostgreSQL Joins](https://www.postgresql.org/docs/current/queries-table-expressions.html#QUERIES-JOIN)
- [SQL Server JOIN types](https://learn.microsoft.com/en-us/sql/t-sql/queries/from-transact-sql)
- [MySQL JOIN documentation](https://dev.mysql.com/doc/refman/8.0/en/join.html)
