# Set Operations: UNION, UNION ALL, INTERSECT, EXCEPT

## Learning Objectives

- Combine query results with `UNION`, `UNION ALL`, `INTERSECT`, and `EXCEPT` (or `MINUS` on some engines).
- Explain duplicate handling and column compatibility rules.
- Choose `UNION ALL` when duplicates are acceptable for performance.
- Contrast set operations with joins (vertical vs horizontal combination).

## Why This Matters

Set operations let you **merge** or **compare** result sets from different queries—useful for migrations, audits, feature flags across tables, and reporting. They differ from joins: joins **widen** rows; set ops **stack** or **filter** rows **vertically** (same column shape).

## The Concept

### Quick definition (and the key mental model)

Set operations combine results **vertically**: they **stack** rows from two queries that have the same “shape” (same number of columns and compatible types).

By contrast, a **join** combines results **horizontally**: it **adds columns** from another table based on a match condition.

If you remember just one line:

- **Join** = widen rows (more columns)
- **Set op** = stack rows (more rows)

All operands must have **compatible columns**: same count, usually compatible types, same order.

### UNION vs UNION ALL

- **`UNION`:** Concatenates results and **removes duplicates** (often via sort/hash—can be expensive).
- **`UNION ALL`:** Concatenates **without** deduplication—faster when duplicates are impossible or acceptable.

```sql
SELECT email FROM archived_customer
UNION
SELECT email FROM customer;
```

Tip: if you only need deduplicated values, consider `SELECT DISTINCT ...` on a `UNION ALL` in some analytic patterns, but in most cases plain `UNION` is the clearest statement of intent.

### INTERSECT

Rows present in **both** queries.

```sql
SELECT product_id FROM wishlist
INTERSECT
SELECT product_id FROM purchase_line;
```

### EXCEPT / MINUS

Rows in the **first** query **not** present in the second (set difference).

```sql
SELECT email FROM newsletter_subscribers
EXCEPT
SELECT email FROM customer;
```

Oracle uses **`MINUS`**; standard and PostgreSQL use **`EXCEPT`**.

### NULL and “row equality” nuance

Set operators compare entire rows for equality/deduplication. How NULLs participate can be engine-specific in edge cases, but the safe assumption for learners is:

- Two rows are considered duplicates only when all corresponding columns are considered equal by the engine’s set semantics.
- Avoid relying on tricky NULL equality behavior; normalize your data (e.g., `COALESCE`) if you must compare rows containing nullable columns.

### Ordering and limits

Apply `ORDER BY` / `LIMIT` on the **outer** combined query (with parentheses as required by your dialect).

```sql
(SELECT id FROM a)
UNION ALL
(SELECT id FROM b)
ORDER BY id;
```

## Code Example

```sql
SELECT 'online' AS channel, COUNT(*) AS cnt FROM web_order
UNION ALL
SELECT 'retail', COUNT(*) FROM store_order;
```

## Summary

- **UNION** deduplicates; **UNION ALL** is the fast path when safe.
- **INTERSECT** finds common rows; **EXCEPT** finds rows only in the first set.
- Column lists must **align** in count and types; verify with small samples first.
- Use set ops to combine similar result sets; use joins to enrich rows with related columns.

## Additional Resources

- [PostgreSQL UNION/INTERSECT/EXCEPT](https://www.postgresql.org/docs/current/typeconv-union-case.html)
- [SQL Server UNION](https://learn.microsoft.com/en-us/sql/t-sql/language-elements/set-operators-union-transact-sql)
- [SQLite Compound SELECT](https://www.sqlite.org/lang_select.html#compound)
