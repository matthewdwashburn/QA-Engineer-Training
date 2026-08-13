# CROSS JOIN: The Cartesian Product

## Learning Objectives

- Define **CROSS JOIN** as the Cartesian product of two row sets.
- Write portable `CROSS JOIN` syntax and recognize comma-join equivalents.
- Identify practical, safe use cases vs accidental explosions.

## Why This Matters

A **cross join** multiplies rows: if you cross 1,000 rows with 1,000 rows, you get **one million** rows. Used intentionally, it generates combinations (test matrices, calendars, allocation grids). Used accidentally—often via a missing join condition—it becomes a **performance and correctness** incident.

## The Concept

### Definition

For every row in table A, pair it with **every** row in table B.

```sql
SELECT a.id AS a_id, b.id AS b_id
FROM a
CROSS JOIN b;
```

Some dialects allow the legacy comma form:

```sql
SELECT a.id, b.id
FROM a, b;
```

The comma form is easy to misuse when adding tables—one forgotten `ON` clause silently becomes a cross join.

### Row count

\[
|A \times B| = |A| \times |B|
\]

Always estimate cardinalities before running ad hoc cross joins on large tables.

### Practical use cases

- **Enumerate combinations:** all stores × all products for a default shelf map (then filter).
- **Generate sequences:** numbers/dates expanded by crossing a small dimension table with a generator pattern (engine-specific).
- **Tests:** small synthetic datasets.

### Pitfalls

- **Missing join predicate** in inner join style queries—classic bug producing duplicated unrelated rows.
- **Large intermediate results** exhausting memory or temp space.

## Code Example

```sql
-- All role–permission pairs to seed a matrix (small sets only)
SELECT r.code AS role, p.code AS permission
FROM role r
CROSS JOIN permission p;
```

## Summary

- **CROSS JOIN** returns every combination of left and right rows.
- Prefer explicit `CROSS JOIN` syntax for clarity; beware comma joins without predicates.
- Use only when the **product set** is intentional and size-bounded.

## Additional Resources

- [PostgreSQL CROSS JOIN](https://www.postgresql.org/docs/current/queries-table-expressions.html#QUERIES-JOIN)
- [SQL Server CROSS JOIN](https://learn.microsoft.com/en-us/sql/t-sql/queries/from-transact-sql)
- [Stack Overflow: When is CROSS JOIN useful?](https://stackoverflow.com/questions/2197160/what-are-the-uses-of-cross-join)
