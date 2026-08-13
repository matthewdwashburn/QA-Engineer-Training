# Week 4 — Friday exercises (sqlite3 + SQLAlchemy)

**Epic thread:** Persist and query from **Python** — first with **`sqlite3`**, then refactor to **SQLAlchemy ORM** with a **relationship**.

## Time box

Roughly **2–4 hours** total (Part 2 builds on Part 1).

## Prerequisites

- Python **3.10+**
- `pip install sqlalchemy` for Part 2

## Exercises

| File | Focus |
|------|--------|
| [exercise_sqlite3.md](exercise_sqlite3.md) | CLI contact app, stdlib only |
| [exercise_sqlalchemy.md](exercise_sqlalchemy.md) | ORM models + `Session` |

## Starter code

`starter_code/contact_app_starter.py` — optional scaffold; you may replace entirely.

## Definition of done

- Part 1: script runs **`--help`** and supports **list/add/get/update/delete** against a **`.db`** file.
- Part 2: models include **`Contact`** + **`PhoneNumber`** (1:N); CRUD works without raw SQL strings in the service layer (ORM queries OK).
