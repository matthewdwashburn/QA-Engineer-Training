# Exercise: Queries and joins

## Goal

Practice **DQL** on a multi-table schema: joins, aggregates, and filters on grouped data.

## Dataset

Use **`starter_code/baseline_ecommerce.sql`** or your Monday database with equivalent tables.

## Required queries (write `queries.sql`)

Number each query and include a one-line comment stating the business question.

1. **INNER JOIN:** List every **paid** order: customer email, `order_id`, `placed_at`, line revenue sum for that order (`SUM(qty * unit_price)`).
2. **LEFT JOIN:** List **all customers** and their **most recent** order id (if any). Include customers with **no** orders (`order_id` NULL).
3. **RIGHT JOIN:** Same logical data as (2) but implement with **`RIGHT JOIN`** (hint: flip table order so you still “start” from customers mentally — comment why you ordered tables that way).
4. **FULL OUTER JOIN:** Produce a report showing **all customers** and **all orders**, pairing where `customer_id` matches; include **unmatched** rows on either side with NULLs.  
   - *Note:* With referential integrity, “orphan orders” may not exist — explain in a comment what the NULL side would mean if a bad row slipped in.
5. **CROSS JOIN (controlled):** Build a **small** Cartesian product: each **product** paired with a **literal** status dimension you create via an inline `VALUES` list (e.g. `('STOCK_OK','STOCK_LOW')` thresholds are optional). **Cap** the result: `WHERE` or small dimension so you do not explode row counts.
6. **Aggregate + `HAVING`:** Per **customer**, show `order_count` and **total spend**; include only customers with **total spend > 25**.
7. **Subquery:** Products that appear on **more than one distinct order** (by `order_id`).
8. **Set operation:** Use **`UNION ALL`** to combine two `SELECT`s with compatible columns (e.g. “active customers” vs “customers with paid orders” — define the sets clearly in comments).

## Stretch

- Add one **`INTERSECT`** or **`EXCEPT`** query with a sentence explaining the business meaning.

## Definition of done

- `queries.sql` runs without error on your engine.
- You can verbally explain **why** `LEFT` vs `INNER` changes row count on this dataset (use **Gamma** customer with no orders).

## Reference

- `written/2-tuesday/dql-data-query-language.md`
- `written/2-tuesday/inner-outer-joins.md`, `left-right-joins.md`, `cross-join.md`, `set-operations.md`
- `demos/2-tuesday/code/demo_joins.sql`, `demo_dql_advanced.sql`
