# What Is a View? Advantages of Views

## Learning Objectives

- Define a **view** as a named, stored query (virtual table).
- List benefits: simplification, consistency, security, and abstraction.
- Name performance considerations (views are not magic accelerators by themselves).

## Why This Matters

Views are a standard way to **package** complex joins and filters behind a simple name—so applications and analysts share the same definition of “active customer” or “order total.” They bridge **raw tables** (Monday/Tuesday) and **controlled access** patterns teams use before ORMs on Friday.

## The Concept

A **view** does not usually store data independently (unless **materialized**—often covered as an advanced topic). Instead, the database stores the **view definition** and merges it into queries that reference the view (**view expansion**).

### Key definitions

- **View**: a named query that presents data as a virtual table.
- **Materialized view** (where supported): a view whose results are stored physically and refreshed; trades freshness for speed.
- **View expansion**: the engine rewrites a query that references a view into a query on the underlying tables (so performance still depends on the underlying query and indexes).

```sql
CREATE VIEW v_active_customer AS
SELECT id, email
FROM customer
WHERE is_active = TRUE;
```

Selecting from `v_active_customer` behaves like selecting from a table whose rows are computed on demand.

### Advantages

1. **Simplify queries:** Hide multi-table joins and expressions behind one name.
2. **Consistency:** Consumers cannot accidentally diverge on the definition of a metric or filter.
3. **Security:** Grant `SELECT` on a view exposing a **subset** of columns/rows without granting base-table access (with caveats depending on privileges and updatability).
4. **API stability:** Refactor underlying tables while keeping the view interface stable (until breaking changes are unavoidable).

### Common pitfalls

- **Views don’t automatically speed up queries**: plain views are mainly about reuse and governance.
- **Security is subtle**: granting access to a view may still require underlying table privileges depending on engine and configuration. Always test with the intended role/user.
- **Hidden complexity**: views can make it harder to see which base tables are being hit; use `EXPLAIN` when debugging performance.

### Performance considerations

Plain views inherit the performance of the **underlying query**. They do **not** automatically cache results. Heavy views still need good indexes on base tables. **Materialized views** (where available) precompute storage—trade freshness for speed.

## Summary

- A view is a **reusable, named query** that looks like a table to clients.
- Views improve **readability**, **governance**, and **access patterns**; they are not a substitute for indexing or query tuning.
- We cover **creating and altering** views in the companion reading for today.

## Additional Resources

- [PostgreSQL Views](https://www.postgresql.org/docs/current/sql-createview.html)
- [MySQL Views Overview](https://dev.mysql.com/doc/refman/8.0/en/views.html)
- [SQLite CREATE VIEW](https://www.sqlite.org/lang_createview.html)
