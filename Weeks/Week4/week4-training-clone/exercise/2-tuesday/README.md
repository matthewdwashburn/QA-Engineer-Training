# Week 4 — Tuesday exercises (queries + transactions)

**Epic thread:** Read and summarize relational data with **joins** and **aggregates**; control **multi-step updates** with transactions.

## Time box

Roughly **2–3 hours** total.

## Setup

1. Load **`starter_code/baseline_ecommerce.sql`** into a scratch database **or** use your **Monday** schema if it includes the same core entities (`customer`, `product`, `order_header`, `order_line`).
2. If your Monday schema differs, adjust query table/column names — the **requirements** still apply.

## Exercises

| File | Focus |
|------|--------|
| [exercise_queries_joins.md](exercise_queries_joins.md) | Joins, aggregates, `HAVING` |
| [exercise_transactions.md](exercise_transactions.md) | `COMMIT` / `ROLLBACK` / `SAVEPOINT` |

## Deliverables

- `queries.sql` — numbered queries with brief comments.
- `transactions.sql` — one script demonstrating the transaction story in the exercise.

## Definition of done

- Every required join type appears at least once (where supported by your engine).
- At least one query uses **`GROUP BY`** + **`HAVING`**.
- Transaction script shows **successful commit** and **rollback** paths without leaving the database inconsistent.
