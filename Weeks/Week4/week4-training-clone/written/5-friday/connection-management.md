# Connection Management with sqlite3

## Learning Objectives

- Use **context managers** (`with`) and `try/finally` to ensure connections are always closed.
- Understand SQLite's **locking model** and why keeping transactions short matters.
- Explain the `check_same_thread` parameter and **thread safety** rules.
- Configure **timeouts** to handle lock contention gracefully.
- Contrast SQLite's connection model with **connection pooling** in server databases.

## Why This Matters

Leaked connections or missing commits cause **flaky tests**, **corrupted expectations**, and **`OperationalError: database is locked`** errors that are painful to diagnose. Even with SQLite's simplicity, disciplined **open/close** and **transaction** scoping mirror production habits for PostgreSQL/MySQL connection pools. Every connection you manage correctly in Python prepares you to reason about pooling in frameworks like FastAPI and Spring Boot.

## The Concept

### The Default Isolation Behavior

Python's `sqlite3` module operates with **implicit transaction management** by default:

- DML statements (`INSERT`, `UPDATE`, `DELETE`) automatically open a transaction if one is not already active.
- DDL statements (`CREATE TABLE`, `DROP TABLE`) implicitly commit any pending transaction before executing.
- You must call `conn.commit()` explicitly — or changes **will not be persisted**.

If you close a connection without committing, `sqlite3` will issue an **implicit rollback** on the pending transaction. This is a common source of "my writes disappeared" bugs.

---

### Connection via Context Manager (Python 3.12+)

Modern `sqlite3.Connection` supports the context manager protocol:

```python
import sqlite3

with sqlite3.connect("app.db") as conn:
    conn.execute("PRAGMA foreign_keys = ON")
    conn.execute("INSERT INTO contact (name) VALUES (?)", ("Ada",))
    conn.commit()
# conn is automatically closed when the block exits
```

> **Important nuance:** The `with` block in `sqlite3` does **not** automatically commit on exit. In Python < 3.12, exiting the block with no exception calls `commit()`; with an exception it calls `rollback()`. In Python ≥ 3.12 the behavior changed. **Always call `conn.commit()` explicitly** before the block exits — do not rely on implicit commit behavior across Python versions.

---

### Connection via try/finally — Explicit and Version-Safe

For clarity and cross-version compatibility, `try/finally` makes intent unambiguous:

```python
conn = sqlite3.connect("app.db")
try:
    conn.execute("PRAGMA foreign_keys = ON")
    conn.execute("INSERT INTO contact (name, email) VALUES (?, ?)", ("Ada", "ada@example.com"))
    conn.commit()
except sqlite3.IntegrityError as e:
    conn.rollback()
    print(f"Constraint violation: {e}")
except Exception:
    conn.rollback()
    raise
finally:
    conn.close()    # always runs — connection always released
```

**Checklist for every connection:**
- [ ] Enable `PRAGMA foreign_keys = ON` if schema uses FKs.
- [ ] Commit after every logical unit of work.
- [ ] Rollback in the `except` branch before re-raising.
- [ ] Close in `finally` — always.

---

### In-Memory Connections — Test Pattern

For unit tests, use `":memory:"` to create an isolated, zero-cost database per test:

```python
def make_test_db() -> sqlite3.Connection:
    conn = sqlite3.connect(":memory:")
    conn.execute("PRAGMA foreign_keys = ON")
    conn.execute("CREATE TABLE contact (id INTEGER PRIMARY KEY, name TEXT NOT NULL)")
    conn.commit()
    return conn

def test_insert_contact():
    conn = make_test_db()
    try:
        conn.execute("INSERT INTO contact (name) VALUES (?)", ("Test User",))
        conn.commit()
        row = conn.execute("SELECT name FROM contact WHERE id = 1").fetchone()
        assert row[0] == "Test User"
    finally:
        conn.close()
```

---

### SQLite's Locking Model

SQLite uses **file-level locking** with five lock states:

| Lock State | Description |
|---|---|
| `UNLOCKED` | No lock held; database open but inactive |
| `SHARED` | One or more readers hold a shared lock; multiple allowed |
| `RESERVED` | A writer intends to write; readers still allowed |
| `PENDING` | Writer waiting for all current readers to finish |
| `EXCLUSIVE` | Writer has exclusive access; no readers or other writers |

**Key implication:** SQLite allows **many concurrent readers** but only **one writer at a time**. While a write transaction is in progress (EXCLUSIVE lock held), all other write attempts wait (or timeout).

**Rule of thumb:** Keep write transactions as **short as possible** — read all needed data first, perform calculations, then write in a tight transaction block.

---

### Timeout — Handling Lock Contention

When SQLite cannot acquire a lock (another connection is writing), it retries for the duration specified by `timeout` (default: 5 seconds). If the lock is not released in time, it raises `OperationalError: database is locked`.

```python
conn = sqlite3.connect("app.db", timeout=10.0)  # wait up to 10 seconds on lock
```

Tune `timeout` based on expected write latency. For scripts running sequentially, the default is usually fine. For long-running background writers, increase it — or switch to WAL mode for better read/write concurrency.

---

### Thread Safety — `check_same_thread`

By default, `sqlite3` connections are **not thread-safe** and will raise `ProgrammingError` if used from a thread other than the one that created them. For multi-threaded applications:

```python
# Option A: disable the check + use a threading.Lock for serialization
import threading

lock = threading.Lock()
conn = sqlite3.connect("app.db", check_same_thread=False)

def write_safely(sql, params):
    with lock:
        conn.execute(sql, params)
        conn.commit()
```

```python
# Option B (preferred): one connection per thread
import threading

_local = threading.local()

def get_conn() -> sqlite3.Connection:
    if not hasattr(_local, "conn"):
        _local.conn = sqlite3.connect("app.db")
        _local.conn.execute("PRAGMA foreign_keys = ON")
    return _local.conn
```

> **Recommendation:** Prefer **one connection per thread** (`threading.local`). Sharing a connection with `check_same_thread=False` is only safe if access is properly serialized.

---

### Connection Pooling — SQLite vs Server Databases

**Connection pooling** (SQLAlchemy's `QueuePool`, JDBC `HikariCP`) addresses the cost of **opening network connections** to remote database servers. Since SQLite connections involve no network — just a file handle — pooling provides minimal benefit.

| Database | Connection cost | Pool benefit |
|---|---|---|
| PostgreSQL / MySQL | High (TCP, auth, SSL) | Significant |
| SQLite (file) | Very low (open file) | Minimal |
| SQLite (`:memory:`) | Near zero | Not applicable |

For SQLite in multi-threaded apps, the practical choice is **one connection per thread** (or one connection per process for single-threaded scripts) — not a pool. SQLAlchemy's SQLite dialect uses `StaticPool` or `NullPool` for in-memory databases precisely for this reason.

---

## Common Pitfalls

| Pitfall | Problem | Fix |
|---|---|---|
| No `conn.close()` or `try/finally` | File lock held; next run may get `database is locked` | Always close in `finally` or use context manager |
| Relying on implicit commit via `with` | Behavior varies by Python version | Always call `conn.commit()` explicitly |
| Sharing a connection across threads without a lock | `ProgrammingError` or data corruption | Use `threading.local` or explicit lock |
| Very long-running write transactions | Other writers starved; `database is locked` | Keep transactions short; commit early |
| Not calling rollback on exception | Dirty transaction state on reuse | Always rollback before `raise` in except block |
| Ignoring `timeout` configuration | Silent hangs under contention | Set a timeout appropriate to expected write latency |

---

## Summary

- Prefer structured scopes (`try/finally` or `with`) to guarantee **`close()`** and explicit **`commit()`**/**`rollback()`**.
- Understand SQLite's **writer-exclusive** lock model; keep write transactions **tight and short**.
- Set `timeout` to prevent indefinite blocking under write contention; consider **WAL mode** for better concurrency.
- For multi-threaded access, use **one connection per thread** (`threading.local`) instead of sharing with `check_same_thread=False`.
- **Connection pooling** is a server-database concern; SQLite uses a single connection per process/thread.

## Additional Resources

- [Python sqlite3 – sqlite3.connect](https://docs.python.org/3/library/sqlite3.html#sqlite3.connect)
- [SQLite Locking and Concurrency](https://www.sqlite.org/lockingv3.html)
- [SQLite WAL mode](https://www.sqlite.org/wal.html)
- [SQLAlchemy Connection Pooling (for contrast)](https://docs.sqlalchemy.org/en/20/core/pooling.html)
