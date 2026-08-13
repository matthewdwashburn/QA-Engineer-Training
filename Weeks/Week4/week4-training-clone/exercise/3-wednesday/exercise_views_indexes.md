# Exercise: Views and indexes

## Part 1 — Views

Using your ecommerce tables (`customer`, `order_header`, `order_line`, `product`):

1. Create a view **`v_order_line_detail`** that exposes: `order_id`, customer email, `sku`, `qty`, `line_total` (`qty * unit_price`), and order `status`.
2. Create a view **`v_customer_spend`** that returns **one row per customer**: email, `order_count`, `lifetime_spend` (sum of all line totals). Use **`LEFT JOIN`** so customers with **zero** orders still appear with zeros.
3. Query both views and paste **one sample row** from each into your notes (or comment in SQL file).

**Discussion question (write 2–3 sentences in comments):** Why is `v_customer_spend` **not** a substitute for indexes on base tables?

## Part 2 — Indexes and plans

On table **`product_event`** from `starter_code/wednesday_prep.sql`:

1. Run **`EXPLAIN (ANALYZE, BUFFERS)`** (PostgreSQL) on:

   ```sql
   SELECT COUNT(*) FROM product_event WHERE product_id = 1;
   ```

   Record whether you see **Seq Scan** or an **index** path.

2. Create an index you believe will help that predicate. Re-run **`ANALYZE product_event;`** if your engine needs fresh stats.

3. Re-run the same **`EXPLAIN (ANALYZE, BUFFERS)`** and describe what changed in **one short paragraph**.

4. Add a **second** index that helps a query filtering on **`(product_id, event_type)`** together. Show `EXPLAIN` before/after for that query.

## Stretch

- Create a **`UNIQUE`** index that enforces business meaning (e.g. prevent duplicate `sku` if not already unique — if already unique, pick another valid case with instructor approval).

## Definition of done

- `views_indexes.sql` contains `CREATE VIEW`, `CREATE INDEX`, and annotated `EXPLAIN` output (paste as comments if your client clears output).

## Reference

- `written/3-wednesday/what-is-a-view-advantages-of-views.md`
- `written/3-wednesday/creating-an-index.md`
- `demos/3-wednesday/code/demo_views.sql`, `demo_indexes.sql`
