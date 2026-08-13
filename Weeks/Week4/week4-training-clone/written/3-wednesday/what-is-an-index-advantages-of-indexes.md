# What Is an Index? Advantages of Indexes

## Learning Objectives

- Explain an **index** as a separate structure that speeds lookups on columns.
- Describe **B-tree** indexes as the default mental model in relational engines.
- Balance faster **reads** against **write** overhead and storage costs.

## Why This Matters

Slow queries drive outages and cloud bills. **Indexes** are the first lever DBAs and developers pull after fixing SQL shape. They complete the performance story after you have modeled and queried data earlier in the week.

## The Concept

Tables are often stored as **heap** or **clustered** structures; without an index, finding rows by a predicate may require **scanning** many rows (**full table scan**).

An **index** stores a mapping from **key values** to row locations (row IDs, heap pointers), usually organized for **logarithmic** lookup—classic **B-tree** (often B+ tree in practice).

### A few key definitions (high leverage)

- **Selectivity**: how strongly a predicate narrows rows. A highly selective predicate (few rows) is a great index candidate.
- **Seek vs scan**: a **seek** jumps to matching keys; a **scan** reads many entries/rows to find matches.
- **Covering index**: an index that contains enough columns to satisfy a query without reading the table rows (vendor features like `INCLUDE` help).
- **SARGable predicate**: a filter the engine can use with an index efficiently (e.g., `created_at >= ?`) rather than wrapping the column in a function (often breaks index usage).

### Advantages

- **Faster point lookups:** `WHERE id = ?` with a primary key index.
- **Faster range scans:** `WHERE created_at BETWEEN ? AND ?` with a suitable index.
- **Faster joins:** when join keys are indexed.
- **Enforcing uniqueness:** unique indexes implement `UNIQUE` constraints.
- **Faster sorting/grouping (sometimes):** `ORDER BY` / `GROUP BY` can use index order when it matches the query pattern.

### Costs

- **Slower writes:** each `INSERT`/`UPDATE`/`DELETE` may need to update multiple indexes.
- **More storage:** indexes duplicate key data and pointers.
- **Wrong indexes hurt:** unused indexes waste space and slow writes; missing indexes hurt reads.
- **Maintenance overhead:** stats, fragmentation/page splits, and vacuum/cleanup behavior vary by engine.

### Query plans

Engines choose **index seek** vs **scan** based on statistics, selectivity, and cost models. You will use **`EXPLAIN`** (today’s demos) to see decisions.

### When an index won’t help (common “skeletal” missing piece)

- Predicates that match a large fraction of the table (low selectivity).
- Very small tables (scan is cheaper).
- Filters that wrap the column in a function, e.g. `WHERE LOWER(email) = 'a@b.com'` (unless you use a functional/expression index where supported).
- Leading-wildcard patterns like `LIKE '%term%'` on plain B-tree indexes (use full-text or trigram indexes where available).

## Code Example

```sql
CREATE INDEX idx_customer_email ON customer (email);
```

## Summary

- Indexes accelerate **search, join, and sort** operations on keyed columns.
- Default mental model: **B-tree** balanced structure with log-time lookups.
- Treat indexes as a **trade-off**: read latency vs write throughput and disk.

## Additional Resources

- [PostgreSQL Indexes](https://www.postgresql.org/docs/current/indexes.html)
- [MySQL InnoDB Indexes](https://dev.mysql.com/doc/refman/8.0/en/innodb-index-types.html)
- [Use The Index, Luke](https://use-the-index-luke.com/)
