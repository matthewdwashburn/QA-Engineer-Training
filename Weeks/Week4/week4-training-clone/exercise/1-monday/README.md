# Week 4 — Monday exercises (schema design + DDL/DML)

**Epic thread:** Model an e-commerce database, then implement it with SQL.

## Time box

Roughly **2–3 hours** total (both parts).

## What you need

- Pen/paper or a diagram tool (Mermaid template provided).
- A SQL client for your chosen RDBMS (**PostgreSQL** recommended to match demos; SQLite acceptable if you adjust types).
- Monday written topics and instructor demos under `written/1-monday/` and `demos/1-monday/`.

## Exercises

| File | Mode | Focus |
|------|------|--------|
| [exercise_schema_design.md](exercise_schema_design.md) | Design (B) | Normalized ERD + cardinality |
| [exercise_ddl_dml.md](exercise_ddl_dml.md) | Implementation (A) | `CREATE TABLE` + `INSERT`/`UPDATE`/`DELETE` |

## Submit / show

- Your completed **Mermaid** (or exported image) for Part 1.
- **One `.sql` file** (or two: `schema.sql` + `seed.sql`) for Part 2 that runs cleanly top-to-bottom.

## Definition of done

- Part 1: ERD shows **customers, products, orders, line items**, addresses **M:N** where needed, notes **PK/FK** and **ON DELETE** choices.
- Part 2: Script creates schema with **at least** `NOT NULL`, `UNIQUE`, `PRIMARY KEY`, `FOREIGN KEY`, and one **`CHECK`**. Seed data proves relationships (at least 2 customers, 3 products, 2 orders, multiple lines).
