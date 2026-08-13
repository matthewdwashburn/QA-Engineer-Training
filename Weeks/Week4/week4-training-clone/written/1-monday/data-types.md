# SQL Data Types

## Learning Objectives

- Choose appropriate numeric types (`INT`, floating point, `DECIMAL`).
- Use string types (`CHAR`, `VARCHAR`, `TEXT`) effectively.
- Model dates and times with clarity across time zones and precision.
- Use boolean types where the dialect supports them, with portable alternatives.
- Explain NULL semantics and how they affect comparisons and aggregates.

## Why This Matters

Types define **valid values**, **storage**, **comparison behavior**, and **index effectiveness**. Wrong types cause silent bugs (floating money), slow queries (oversized columns), or failed inserts. Schema design is the foundation of the week’s epic; types are its building blocks.

## The Concept

### Quick definition

A **data type** defines what values a column can hold and how the database stores, compares, and indexes those values. Type choice impacts correctness (money and time zones), not just storage.

### Numeric types

- **INTEGER / INT / BIGINT / SMALLINT:** Whole numbers. Pick the smallest range that fits your domain to save space; use **BIGINT** for large counts or IDs if needed.
- **FLOAT / REAL / DOUBLE:** Approximate binary floating point. Good for scientific magnitudes; **poor for currency** because `0.1 + 0.2` style errors appear.
- **DECIMAL(p, s) / NUMERIC(p, s):** Fixed-point exact arithmetic. **Preferred for money** and any quantity requiring exact decimal representation. `p` = precision (total digits), `s` = scale (digits after decimal).

```sql
CREATE TABLE invoice_line (
  line_id BIGINT PRIMARY KEY,
  quantity INT NOT NULL CHECK (quantity > 0),
  unit_price DECIMAL(12, 2) NOT NULL
);
```

### String types

- **CHAR(n):** Fixed length, padded with spaces (in SQL standard behavior). Use for codes of fixed width when storage predictability matters.
- **VARCHAR(n):** Variable length up to `n` **characters** (in standard SQL; some systems count bytes—check your DB). Default choice for names, descriptions with a sensible max.
- **TEXT / CLOB:** Large or unbounded text. May have indexing and sorting limitations compared to `VARCHAR`; still right for articles, JSON strings, logs.

#### Collation and case sensitivity (string comparison rules)

Databases use **collations** to decide how strings compare and sort (case sensitivity, accents, locale rules). This is vendor- and configuration-dependent.

- If you need case-insensitive uniqueness (e.g., emails), a plain `UNIQUE` on `VARCHAR` may not behave the same across engines/collations.
- Always test string rules in your target engine.

### Date and time

Common types (names vary slightly by vendor):

- **DATE:** Calendar date (no time of day).
- **TIME:** Time of day (no date).
- **TIMESTAMP / DATETIME:** Date + time; often **without** time zone unless you use `TIMESTAMP WITH TIME ZONE` (PostgreSQL) or equivalent.

**Practice:** Store **UTC** in the database when events cross zones; convert at the application edge. Document whether a column is “local wall time” vs UTC—mixing them causes painful bugs.

### Boolean

- **BOOLEAN** in PostgreSQL; **BIT** or **TINYINT(1)** patterns in MySQL; SQL Server uses **BIT**. Portable pattern: `SMALLINT` with `CHECK (col IN (0,1))` or a **lookup** table if you need extensibility.

```sql
-- PostgreSQL-style
is_active BOOLEAN NOT NULL DEFAULT TRUE;
```

### NULL (missing/unknown) and why it matters

`NULL` is not a value like 0 or empty string—it represents **missing/unknown**. This affects:

- **Comparisons**: use `IS NULL` / `IS NOT NULL` (not `= NULL`).
- **Aggregates**: `COUNT(column)` ignores NULLs; `COUNT(*)` counts rows.
- **Logic**: SQL has three-valued logic (TRUE/FALSE/UNKNOWN) when NULL is involved.

```sql
SELECT *
FROM customer
WHERE phone IS NULL;
```

## Code Example

```sql
CREATE TABLE event_log (
  id BIGSERIAL PRIMARY KEY,
  event_name VARCHAR(200) NOT NULL,
  payload TEXT,
  occurred_at TIMESTAMP NOT NULL,
  success BOOLEAN NOT NULL DEFAULT TRUE
);
```

## Summary

- Use **INTEGER** family for counts/IDs; **DECIMAL** for money; avoid **FLOAT** for exact decimals.
- Prefer **VARCHAR** with realistic limits; use **TEXT** when length is highly variable or large.
- Be explicit about **date vs timestamp** and **time zone** semantics.
- Boolean support is dialect-specific; know your target engine or use portable constraints.
- Know `NULL` semantics—especially for comparisons and `COUNT`.

## Additional Resources

- [PostgreSQL Data Types](https://www.postgresql.org/docs/current/datatype.html)
- [MySQL Data Types](https://dev.mysql.com/doc/refman/8.0/en/data-types.html)
- [SQLite Datatypes](https://www.sqlite.org/datatype3.html)
