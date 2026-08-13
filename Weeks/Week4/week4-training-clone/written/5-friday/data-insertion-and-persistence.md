# Data Insertion and Persistence with sqlite3

## Learning Objectives

- Insert rows with **parameterized** SQL (`?` placeholders) to prevent SQL injection.
- Use **`executemany`** for efficient bulk inserts.
- Read **`lastrowid`** and **`rowcount`** to inspect DML results.
- Apply **`commit`** and **`rollback`** patterns for durable, atomic writes.
- Understand **`UPSERT`** (`INSERT OR REPLACE` / `ON CONFLICT`) for idempotent writes.
- Handle **`IntegrityError`** when constraints are violated.

## Why This Matters

String interpolation in SQL is a **critical security vulnerability** — it is the root cause of SQL injection attacks (OWASP Top 10). Parameter binding is non-negotiable and exactly mirrors the `PreparedStatement` pattern from Thursday's JDBC module. Additionally, understanding transaction scoping and batch APIs directly impacts the **correctness** and **performance** of data-heavy QA automation scripts.

## The Concept

### Why Parameter Binding? — Injection Contrast

**Never** build SQL by concatenating or formatting user-supplied strings:

```python
# ❌ DANGEROUS — SQL Injection vulnerability
username = "admin' OR '1'='1"
cur.execute(f"SELECT * FROM users WHERE name = '{username}'")
# Becomes: SELECT * FROM users WHERE name = 'admin' OR '1'='1'
# This returns ALL rows — authentication bypassed!
```

**Always** use `?` placeholders — the driver handles escaping and quoting:

```python
# ✅ SAFE — parameterized query
cur.execute("SELECT * FROM users WHERE name = ?", (username,))
# SQLite receives name as a bound value, never as SQL text
```

---

### Parameterized INSERT

Pass values as a **tuple** (or list) as the second argument to `execute()`. SQLite's driver substitutes each `?` in order, safely escaping the values.

```python
import sqlite3

conn = sqlite3.connect("app.db")
cur = conn.cursor()

cur.execute(
    "INSERT INTO contact (name, email) VALUES (?, ?)",
    ("Ada Lovelace", "ada@example.com"),
)
conn.commit()

print(f"New row id: {cur.lastrowid}")   # integer PK of the inserted row
```

> **`cur.lastrowid`** — the `rowid` (or `INTEGER PRIMARY KEY`) of the most recently inserted row. Returns `None` if no insert was performed on this cursor.

---

### executemany — Bulk Inserts

`executemany` accepts an **iterable of parameter sequences** and executes the same statement once per item. This is significantly faster than looping `execute()` because it reduces Python–SQLite round-trips and can be wrapped in a single transaction.

```python
rows = [
    ("Bob Smith",  "bob@example.com"),
    ("Chen Wei",   "chen@example.com"),
    ("Diana Ramos","diana@example.com"),
]

cur.executemany(
    "INSERT INTO contact (name, email) VALUES (?, ?)",
    rows,
)
conn.commit()

print(f"Rows affected: {cur.rowcount}")  # total rows inserted
```

> **`cur.rowcount`** — number of rows affected by the last `execute` / `executemany`. Useful for assertions in test code (e.g., verify exactly 1 row was updated).

---

### Transactions — Atomic Multi-Statement Operations

SQLite wraps every statement in an implicit transaction when `autocommit` is off (the default in Python's `sqlite3`). For multi-step logic that must **succeed together or fail together** (e.g., a funds transfer), open an explicit transaction:

```python
conn.execute("BEGIN")
try:
    conn.execute(
        "UPDATE account SET balance = balance - ? WHERE id = ?",
        (100, sender_id),
    )
    conn.execute(
        "UPDATE account SET balance = balance + ? WHERE id = ?",
        (100, receiver_id),
    )
    conn.commit()       # both updates persisted atomically
except Exception:
    conn.rollback()     # neither update takes effect
    raise
```

**Why this matters:** If the process crashes between the two `UPDATE` statements, the `rollback` (or SQLite's automatic journal recovery) ensures the database is never left in a half-modified state — a core ACID property.

---

### UPSERT — Insert or Update on Conflict

When you need an idempotent write (insert if absent, update if present), use SQLite's `ON CONFLICT` clause instead of a separate SELECT + branch:

```python
# INSERT OR REPLACE — deletes old row and inserts new one (resets all fields)
cur.execute(
    "INSERT OR REPLACE INTO contact (id, name, email) VALUES (?, ?, ?)",
    (1, "Ada Lovelace", "ada@new-email.com"),
)
conn.commit()
```

For a partial update (preserving columns not mentioned), use `ON CONFLICT DO UPDATE`:

```python
cur.execute(
    """
    INSERT INTO contact (name, email) VALUES (?, ?)
    ON CONFLICT(email) DO UPDATE SET name = excluded.name
    """,
    ("Ada L.", "ada@example.com"),
)
conn.commit()
```

> `excluded.name` refers to the value that *would have been* inserted — a standard SQL concept.

---

### Handling Constraint Violations — IntegrityError

When a constraint (`UNIQUE`, `NOT NULL`, `FOREIGN KEY`, `CHECK`) is violated, `sqlite3` raises **`sqlite3.IntegrityError`**. Always catch it at the appropriate boundary:

```python
try:
    cur.execute(
        "INSERT INTO contact (name, email) VALUES (?, ?)",
        ("Duplicate", "ada@example.com"),   # email already exists (UNIQUE)
    )
    conn.commit()
except sqlite3.IntegrityError as e:
    conn.rollback()
    print(f"Constraint violation: {e}")
    # Application decides: skip, report, or re-raise
```

---

### DML Result Inspection Summary

| Attribute | Type | Meaning |
|---|---|---|
| `cur.lastrowid` | `int \| None` | PK of last inserted row |
| `cur.rowcount` | `int` | Rows affected by last DML statement |

---

## Common Pitfalls

| Pitfall | Problem | Fix |
|---|---|---|
| Using f-strings for SQL values | SQL injection vulnerability | Always use `?` placeholders |
| Forgetting `conn.commit()` | Changes invisible to other connections; lost on disconnect | Commit explicitly after every logical unit |
| Calling `execute()` in a loop instead of `executemany` | Slow — many round-trips | Use `executemany` for batch DML |
| Not catching `IntegrityError` | Unhandled exception crashes the app | Wrap in try/except at service boundary |
| Using `INSERT OR REPLACE` without understanding side effects | Resets auto-increment ID and cascades deletes | Prefer `ON CONFLICT DO UPDATE` for partial updates |
| Missing `conn.rollback()` on exception | Leaves transaction open / dirty state | Always rollback in the `except` branch |

---

## Summary

- **Parameterized queries** (`?`) are mandatory — they prevent SQL injection and handle quoting correctly.
- **`executemany`** is the right tool for batch inserts; reduces round-trips and is faster than looping.
- **`cur.lastrowid`** and **`cur.rowcount`** let you inspect DML results without an extra `SELECT`.
- Wrap multi-statement logic in explicit **`BEGIN` / `commit` / `rollback`** for atomicity.
- Use **`ON CONFLICT DO UPDATE`** for partial upserts; `INSERT OR REPLACE` for full row replacement.
- Catch **`sqlite3.IntegrityError`** at service boundaries to handle constraint violations gracefully.

## Additional Resources

- [Python sqlite3 – Cursor.execute](https://docs.python.org/3/library/sqlite3.html#sqlite3.Cursor.execute)
- [Python sqlite3 – Cursor.executemany](https://docs.python.org/3/library/sqlite3.html#sqlite3.Cursor.executemany)
- [SQLite Transaction](https://www.sqlite.org/lang_transaction.html)
- [SQLite ON CONFLICT clause](https://www.sqlite.org/lang_conflict.html)
- [OWASP SQL Injection Prevention Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/SQL_Injection_Prevention_Cheat_Sheet.html)
