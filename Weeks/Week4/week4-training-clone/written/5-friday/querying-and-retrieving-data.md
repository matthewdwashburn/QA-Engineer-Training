# Querying and Retrieving Data with sqlite3

## Learning Objectives

- Fetch rows using **`fetchone`**, **`fetchall`**, **`fetchmany`**, and **iteration**.
- Configure **`row_factory`** for dict-like or named column access.
- Apply **pagination** with `LIMIT` / `OFFSET` for large result sets.
- Choose fetch strategies for memory efficiency vs convenience.
- Recognize common retrieval pitfalls and how to avoid them.

## Why This Matters

Reading data powers reports, APIs, and automated tests. Understanding fetch APIs prevents accidentally loading **millions of rows** into RAM, and using `row_factory` removes brittle positional indexing so that code reads closer to intent. These habits directly transfer to production ORMs and JDBC `ResultSet` iteration patterns you saw on Thursday.

## The Concept

### Executing a SELECT

A `SELECT` statement is executed through a `Cursor`. The cursor holds the result set **server-side** (in SQLite's case, in memory) until you fetch rows.

```python
import sqlite3

conn = sqlite3.connect("app.db")
cur = conn.cursor()
cur.execute("SELECT id, name, email FROM contact WHERE active = ?", (1,))
```

> **Always parameterize `WHERE` clause values** — even for reads. Never use f-strings with user-supplied input.

---

### fetchone — Single Row Lookup

Returns the **next** row as a tuple, or `None` if no rows remain. Ideal when you expect exactly one result (e.g., look up by primary key or unique field).

```python
cur.execute("SELECT id, name FROM contact WHERE email = ?", ("ada@example.com",))
row = cur.fetchone()   # None if not found

if row is not None:
    pk, name = row[0], row[1]
    print(f"Found: {name} (id={pk})")
else:
    print("No contact found.")
```

---

### fetchall — All Rows at Once

Returns every remaining row as a **list of tuples**. Convenient for small result sets (reports, config tables, test assertions). **Avoid** on tables that may grow to thousands of rows — all data is materialized in Python RAM.

```python
cur.execute("SELECT id, name FROM contact ORDER BY name")
all_rows = cur.fetchall()   # list[tuple]

for row in all_rows:
    print(row[0], row[1])
```

---

### fetchmany — Streaming in Batches

Fetches up to `size` rows per call. Returns an **empty list** when exhausted — use that as your loop sentinel. This is the right tool when processing large tables without loading everything at once.

```python
cur.execute("SELECT id, payload FROM events ORDER BY created_at")

BATCH = 500
while True:
    batch = cur.fetchmany(BATCH)
    if not batch:
        break
    for row in batch:
        process(row)
```

Alternatively, using the walrus operator (Python ≥ 3.8):

```python
while batch := cur.fetchmany(500):
    process_batch(batch)
```

---

### Iterator Protocol — Row-by-Row Streaming

Cursors implement Python's **iterator protocol**, so you can loop directly. This is memory-efficient and the most Pythonic style for sequential processing — rows are yielded one at a time from SQLite's internal pager.

```python
for row in cur.execute("SELECT id, name FROM contact"):
    print(row)
```

---

### row_factory — Named Column Access

By default, rows are plain **tuples** accessed by position. Setting `conn.row_factory = sqlite3.Row` wraps each row in an object that supports both **positional** and **named** (string key) access. This makes code more readable and resilient to column reordering.

```python
conn = sqlite3.connect("app.db")
conn.row_factory = sqlite3.Row      # set before creating cursors

cur = conn.cursor()
cur.execute("SELECT id, name, email FROM contact WHERE id = ?", (1,))
row = cur.fetchone()

if row:
    print(row["name"])          # named access
    print(row["email"])
    print(row[0])               # positional still works
    print(dict(row))            # convert to plain dict for serialization
```

> **`sqlite3.Row` is read-only.** To mutate, convert with `dict(row)` first.

You can also set a custom factory for full dict rows:

```python
conn.row_factory = lambda cursor, row: dict(
    zip([col[0] for col in cursor.description], row)
)
cur = conn.cursor()
cur.execute("SELECT id, name FROM contact LIMIT 3")
rows = cur.fetchall()   # list[dict]
print(rows[0]["name"])
```

---

### Pagination with LIMIT and OFFSET

When an endpoint or report must page through large result sets, use SQL `LIMIT` and `OFFSET`:

```python
def get_page(conn: sqlite3.Connection, page: int, page_size: int = 20) -> list:
    offset = (page - 1) * page_size
    cur = conn.cursor()
    cur.execute(
        "SELECT id, name FROM contact ORDER BY id LIMIT ? OFFSET ?",
        (page_size, offset),
    )
    return cur.fetchall()

page_1 = get_page(conn, page=1)
page_2 = get_page(conn, page=2)
```

> **Performance note:** `OFFSET` requires SQLite to scan and discard rows up to the offset point. For very large tables, **keyset pagination** (cursor-based: `WHERE id > last_seen_id`) is more efficient.

---

### Fetch Strategy Comparison

| Method | Returns | Memory | Use When |
|---|---|---|---|
| `fetchone()` | tuple \| None | Minimal | Single-row lookup (PK, unique key) |
| `fetchall()` | list[tuple] | All rows in RAM | Small/bounded result sets |
| `fetchmany(n)` | list[tuple] | n rows at a time | Streaming large tables in chunks |
| Iteration (`for row in cur`) | tuple (lazy) | One row at a time | Sequential processing, large tables |

---

## Common Pitfalls

| Pitfall | Problem | Fix |
|---|---|---|
| Calling `fetchall()` on a large table | OOM or severe slowdown | Use `fetchmany` or iteration |
| Positional index on column reorder | Silent wrong-field bugs | Use `row_factory = sqlite3.Row` |
| Forgetting `WHERE` clause | Scans entire table | Always parameterize and filter |
| Reusing cursor mid-iteration | Corrupts result set pointer | Open a fresh cursor per query |
| Ignoring `fetchone()` returning `None` | `AttributeError` or `TypeError` | Always guard with `if row is not None:` |

---

## Summary

- **`fetchone`** — single row or `None`; ideal for PK/unique lookups.
- **`fetchall`** — all rows as a list; only for small, bounded result sets.
- **`fetchmany(n)`** / **iteration** — streaming patterns for large data; keep RAM flat.
- **`row_factory`** — enables named column access, eliminating brittle positional indexing.
- **`LIMIT / OFFSET`** — paginate results; prefer keyset pagination for performance-critical paths.

## Additional Resources

- [Python sqlite3 – Row objects](https://docs.python.org/3/library/sqlite3.html#sqlite3.Row)
- [Python sqlite3 – Cursor](https://docs.python.org/3/library/sqlite3.html#sqlite3.Cursor)
- [SQLite SELECT syntax](https://www.sqlite.org/lang_select.html)
- [SQLite LIMIT / OFFSET](https://www.sqlite.org/lang_select.html#limitoffset)
