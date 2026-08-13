# Types of Indexes

## Learning Objectives

- Contrast **clustered** vs **non-clustered** indexes.
- Explain **unique**, **composite**, and **full-text** indexes at a high level.
- Recognize vendor-specific index types (hash, GiST, GIN) as extensions beyond the basics.

## Why This Matters

Interviewers and architects name index types when discussing **primary keys**, **covering indexes**, and **search features**. Knowing the vocabulary prevents talking past each other when tuning production systems.

## The Concept

### Clustered index

The **table rows** are stored **in the order** of the clustered key (conceptually—the physical story varies). **SQL Server** and **MySQL InnoDB** cluster the primary key with the row. **PostgreSQL** uses heap tables with a separate clustered-like option via `CLUSTER` command (reorders physically but not continuously maintained).

- **Implication:** Range scans on the clustered key can be very efficient.

### Non-clustered (secondary) index

A separate structure pointing into the table’s storage. Most indexes you add explicitly are **non-clustered**.

### Covering index (concept)

A **covering index** is an index that contains all columns needed by a query (either as key columns or as included/stored columns), allowing the engine to answer the query without fetching table rows.

- **Why it matters:** fewer random reads and faster response times for read-heavy queries.
- **How it’s achieved:** depends on engine (e.g., SQL Server supports `INCLUDE`; PostgreSQL can use multi-column indexes or “index-only scans” when visibility rules allow).

### Unique index

Enforces that no two rows share the same key (NULL handling per dialect). Often created automatically for **PRIMARY KEY** and **UNIQUE** constraints.

```sql
CREATE UNIQUE INDEX uq_customer_email ON customer (email);
```

### Composite index

Index on **multiple columns**; order matters for predicates. Good for queries filtering on `(last_name, first_name)` or sorting along that sequence.

```sql
CREATE INDEX idx_order_customer_date ON order_header (customer_id, placed_at DESC);
```

Order matters rule of thumb:

- Works best when queries filter by the **leftmost** (leading) column(s) in the index.
- A composite index on `(a, b)` can help `WHERE a = ?` and `WHERE a = ? AND b = ?`, but usually not `WHERE b = ?` alone.

### Full-text index

Specialized structure for **token search**, stemming, ranking—distinct from simple `LIKE '%word%'` scans. SQL Server **full-text catalogs**, PostgreSQL **GIN/GiST** with `tsvector`, MySQL **FULLTEXT**—all differ in syntax.

### Other types (awareness)

- **Hash indexes:** fast equality only; limited in some engines or storage engines.
- **GiST/GIN/BRIN (PostgreSQL):** specialized for geometry, arrays, JSON, very large tables, etc.
- **Bitmap indexes:** common in some enterprise engines for analytics/low-cardinality columns (rare in OLTP defaults; engine-specific).

## Summary

- **Clustered** indexes define/table-store row order in some products; **non-clustered** indexes are separate lookup structures.
- **Unique** indexes enforce uniqueness; **composite** indexes optimize multi-column access paths.
- **Full-text** indexes power search features beyond B-tree `LIKE` patterns.

## Additional Resources

- [PostgreSQL Index Types](https://www.postgresql.org/docs/current/indexes-types.html)
- [MySQL CREATE INDEX](https://dev.mysql.com/doc/refman/8.0/en/create-index.html)
- [SQL Server Index Architecture](https://learn.microsoft.com/en-us/sql/relational-databases/sql-server-index-design-guide)
