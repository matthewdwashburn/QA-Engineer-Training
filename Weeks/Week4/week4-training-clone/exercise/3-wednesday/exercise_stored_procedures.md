# Exercise: Stored procedures

## Goal

Encapsulate **inventory adjustment** and **order total lookup** behind PostgreSQL **`PROCEDURE`** routines with parameters.

## Prerequisites

- PostgreSQL with **PL/pgSQL** enabled (default on standard installs).
- Baseline ecommerce schema with `product`, `order_header`, `order_line`.

## Tasks

Write **`procedures.sql`** containing:

### Procedure 1 — `adjust_stock`

**Signature (you may rename slightly but keep meaning):**

- **`p_sku TEXT`**
- **`p_delta INT`** — positive to restock, negative to sell/remove stock
- Rules:
  - If resulting `stock_qty` would be **negative**, **raise** an exception (`RAISE EXCEPTION`) and change **nothing**.
  - Otherwise update `product.stock_qty` in one statement.

Document with a comment **why** a procedure might be preferable to scattering the `UPDATE` across apps (one sentence is enough).

### Procedure 2 — `fetch_order_total` (choose one style)

Either:

- **A)** Procedure with **`INOUT`** parameter: input `p_order_id`, output **`NUMERIC`** total line sum; **or**
- **B)** A **`FUNCTION`** that `RETURNS NUMERIC` (if your class allows functions this week — ask instructor).

Pick **one** style and note portability trade-offs in a comment.

### Calls

At the bottom of the file, include **`CALL`** / `SELECT` examples that:

- Successfully adjust stock.
- Fail on insufficient stock (show the error text you expect).
- Return the total for an existing order.

## Definition of done

- Script is re-runnable (`CREATE OR REPLACE` or `DROP` guards).
- No procedure leaves the database in a **partial** inconsistent state when validation fails.

## Reference

- `written/3-wednesday/stored-procedures.md`
- `demos/3-wednesday/code/demo_stored_procedures.sql`
