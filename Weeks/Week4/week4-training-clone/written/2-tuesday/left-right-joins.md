# LEFT JOIN and RIGHT JOIN

## Learning Objectives

- Contrast **LEFT OUTER JOIN** and **RIGHT OUTER JOIN** with inner joins.
- Predict **NULL** placement in result columns for unmatched rows.
- Prefer **LEFT JOIN** as the default outer pattern for consistent readability.
- Explain the difference between filtering in `ON` vs filtering in `WHERE` for outer joins.

## Why This Matters

Outer joins answer questions like “**all** customers and their last order, **including** customers who never ordered.” Those shapes appear constantly in CRM, ecommerce, and operational dashboards. Mastering **which side is preserved** prevents flipped logic bugs.

## The Concept

### Key definition: “preserved” (driving) side

In an outer join, one side is **preserved**: those rows appear even if no match exists. That preserved side is your **driving set** (the base population you’re reporting on).

### LEFT OUTER JOIN (LEFT JOIN)

Preserves **every row** from the **left** table. For each left row, matching right rows are attached; if **none** match, right-side columns are **NULL**.

```sql
SELECT c.id, c.email, o.id AS order_id
FROM customer c
LEFT JOIN order_header o ON o.customer_id = c.id;
```

Every customer appears at least once; `order_id` is NULL for customers with no orders.

### Filtering: `ON` vs `WHERE` (common outer join bug)

With outer joins, **where you put a condition matters**:

- Conditions in the `ON` clause affect **matching**, but still keep preserved-side rows.
- Conditions in the `WHERE` clause run **after** the join and can accidentally remove the preserved-side rows (turning a left join into an inner join for that condition).

Example: “All customers, and only their completed orders (if any).”

```sql
-- Correct: filter orders in ON so customers with no completed orders remain
SELECT c.id, o.id AS order_id
FROM customer c
LEFT JOIN order_header o
  ON o.customer_id = c.id
 AND o.status = 'COMPLETED';
```

```sql
-- Bug-prone: WHERE filters out rows where o.status is NULL,
-- eliminating customers with no orders (or no completed orders)
SELECT c.id, o.id AS order_id
FROM customer c
LEFT JOIN order_header o ON o.customer_id = c.id
WHERE o.status = 'COMPLETED';
```

### RIGHT OUTER JOIN (RIGHT JOIN)

Symmetric: preserves **every row** from the **right** table; left columns NULL when unmatched.

```sql
SELECT c.id, c.email, o.id AS order_id
FROM customer c
RIGHT JOIN order_header o ON o.customer_id = c.id;
```

Here, every order appears; a customer with no orders would **not** create a row unless they have an order referencing them.

### NULL handling

Unmatched outer side columns are **NULL**. Application code and reports must handle NULLs (COALESCE, filters, UI defaults).

### Style convention

**RIGHT JOIN** is less common in production SQL because it forces readers to mentally swap perspective. Typical pattern: reorder tables so the **preserved** side is on the **left**, and use **LEFT JOIN** only—improves team consistency.

## Code Example

```sql
-- All products and stock level rows if they exist
SELECT p.sku, p.name, i.qty
FROM product p
LEFT JOIN inventory i ON i.sku = p.sku;
```

## Summary

- **LEFT JOIN** keeps all **left** rows; **RIGHT JOIN** keeps all **right** rows.
- Unmatched join partner columns are **NULL**.
- Prefer **LEFT JOIN** with table order chosen so the driving set is on the left.
- For outer joins, filter the non-preserved table in `ON` when you want to keep preserved rows.

## Additional Resources

- [PostgreSQL Outer Joins](https://www.postgresql.org/docs/current/queries-table-expressions.html#QUERIES-JOIN)
- [Use The Index, Luke: Left join bias](https://use-the-index-luke.com/sql/join)
- [MySQL LEFT JOIN](https://dev.mysql.com/doc/refman/8.0/en/join.html)
