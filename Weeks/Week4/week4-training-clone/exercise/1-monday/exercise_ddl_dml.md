# Exercise: Implement your e-commerce schema (DDL + DML)

## Prerequisites

- Complete **exercise_schema_design.md** (your design is the blueprint).
- Same RDBMS you planned for (PostgreSQL recommended).

## Part A — DDL

1. Write **`CREATE TABLE`** statements for every table in your design.
2. Include:
   - `PRIMARY KEY` on every table.
   - `NOT NULL` where business requires a value.
   - `UNIQUE` on natural keys (e.g. email, SKU).
   - `FOREIGN KEY` with explicit **`ON DELETE` / `ON UPDATE`** actions where it matters; explain mismatches in comments.
   - At least one **`CHECK`** constraint (e.g. non-negative price or qty, or allowed status values).
3. Optional stretch: one `ALTER TABLE` that adds a column **after** initial create (comment why).

## Part B — DML (seed + updates)

Write statements that demonstrate:

1. **`INSERT`** at least:
   - 2 customers
   - 3 products
   - 2 orders for **different** customers
   - Multiple **order lines** per order (at least one order with ≥2 lines)
2. **`UPDATE`** that changes a **product** price **after** an order exists — show that **historical line prices** stay correct (they should not change retroactively if you modeled line price correctly).
3. **`DELETE`** or **`UPDATE`** guarded by a **`WHERE`** clause that affects **only one** logical row (e.g. cancel a single `OPEN` order by id).
4. One **multi-row** `INSERT` (`VALUES (...), (...)`) or `INSERT ... SELECT`.

## Safety rules

- Every **`UPDATE`/`DELETE`** in your script must include a **`WHERE`** (no “wide open” updates).
- Prefer **explicit column lists** on `INSERT`.

## Deliverable

- Submit `ecommerce_schema.sql` (DDL) and `ecommerce_seed.sql` (DML), **or** a single `ecommerce_all.sql` with clear section comments.

## Self-check

- Script runs twice? Either use `DROP ... IF EXISTS` in dev-only scripts **or** document “run on empty DB”.
- Foreign keys: try inserting an **invalid** `customer_id` on an order — should **fail**.
