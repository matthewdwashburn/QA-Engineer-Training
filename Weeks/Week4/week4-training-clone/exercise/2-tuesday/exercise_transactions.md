# Exercise: Transaction control (TCL)

## Goal

Use **`BEGIN`**, **`COMMIT`**, **`ROLLBACK`**, and **`SAVEPOINT`** to make a **multi-step inventory + order** update **atomic** where intended.

## Scenario

You sell physical products. When an order is **confirmed**, you must:

1. Insert a new **`order_header`** row (`status = 'PAID'`).
2. Insert **`order_line`** rows for that order.
3. **Decrement** `product.stock_qty` for each line’s product (must not go negative).

If **any** step fails, **none** of the inventory or order rows should persist (for the failure scenario).

## Tasks

Write **`transactions.sql`** with **three labeled sections**:

### Section A — Happy path

- `BEGIN;`
- Perform steps 1–3 for a **valid** order (choose customer + products from baseline data).
- `COMMIT;`
- Add **`SELECT`** statements after commit to prove stock decreased and order exists.

### Section B — Rollback on rule violation

- `BEGIN;`
- Attempt an order line with **qty greater than available** `stock_qty` **or** an invalid `product_id`.
- Show that you **`ROLLBACK`** and verify with **`SELECT`** that **no** partial order or stock change remains.

### Section C — Savepoint

- `BEGIN;`
- `SAVEPOINT after_header;`
- Insert `order_header`.
- `SAVEPOINT after_lines;`
- Insert lines, then **intentionally** run a statement that violates a constraint or business rule **after** `after_lines`.
- Use **`ROLLBACK TO SAVEPOINT after_lines;`** (or another savepoint you name) to undo only the bad part.
- Finish with a **valid** line insert (or corrective action) and **`COMMIT;`**.

## Requirements

- Every `BEGIN` has a matching **`COMMIT`** or **`ROLLBACK`** on all code paths you demonstrate.
- Comment **which failure** you simulated in Section B and C.

## Definition of done

- Running the script section-by-section behaves as described (instructors may run in separate sessions).
- You can explain how this maps to **ACID** (**Atomicity** especially).

## Reference

- `written/2-tuesday/tcl-transaction-control-language.md`
- `written/2-tuesday/atomicity-consistency-isolation-durability.md`
- `demos/2-tuesday/code/demo_transactions.sql`
