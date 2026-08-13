# Introduction to Python's sqlite3 Module

## Learning Objectives

- Describe **SQLite** as an embedded, file-based relational engine and when it is the right tool.
- Use the **`sqlite3`** standard library module to open databases, create cursors, and execute SQL.
- Distinguish **file-backed**, **in-memory**, and **WAL-mode** SQLite databases.
- Identify the cursor lifecycle and the role of `commit` in persisting changes.
- Recognize good use cases (local tools, tests, mobile/desktop apps) vs client/server databases.

## Why This Matters

The weekly epic closes by connecting SQL to **Python**. `sqlite3` ships with Python — no extra server, no installation — so you can practice persistence immediately. The concepts mirror Thursday's JDBC module: connections, cursors, parameterized SQL, and transactions. Mastering `sqlite3` also establishes the foundation for SQLAlchemy, which wraps it (and other databases) with a higher-level API.

## The Concept

### What is SQLite?

SQLite is a **C library** that implements a complete SQL engine **inside your application's process**. Unlike PostgreSQL or MySQL, there is no separate database service, no TCP socket, and no network round-trip. The entire database (schema, data, and indexes) is stored in a **single cross-platform file** on disk.

Key architectural facts:

| Property | SQLite | Client/Server RDBMS (PostgreSQL, MySQL) |
|---|---|---|
| Deployment | Library linked into your app | Separate server process |
| Storage | Single file | Server-managed data directory |
| Network overhead | None (in-process) | TCP/IP round-trips |
| Concurrent writers | One writer at a time | Multiple concurrent writers |
| Max database size | ~281 TB (theoretical) | Depends on storage |
| Setup required | None (stdlib) | Install + configure server |

---

### sqlite3 Module — Core API

Python's built-in `sqlite3` module is the official interface. It exposes:

| Object / Function | Purpose |
|---|---|
| `sqlite3.connect(path)` | Opens (or creates) a database file; returns a `Connection` |
| `Connection.cursor()` | Creates a `Cursor` for executing SQL statements |
| `Cursor.execute(sql, params)` | Executes a single SQL statement with optional bound parameters |
| `Cursor.executemany(sql, seq)` | Executes a statement once per item in an iterable |
| `Cursor.fetchone()` | Returns the next result row, or `None` |
| `Cursor.fetchall()` | Returns all remaining result rows as a list |
| `Connection.commit()` | Persists the current transaction to disk |
| `Connection.rollback()` | Reverts uncommitted changes |
| `Connection.close()` | Releases the file lock and flushes buffers |

```python
import sqlite3

conn = sqlite3.connect("app.db")          # creates app.db if it doesn't exist
cur = conn.cursor()

cur.execute(
    "CREATE TABLE IF NOT EXISTS contact (id INTEGER PRIMARY KEY, name TEXT NOT NULL)"
)
conn.commit()    # DDL is also a transaction in SQLite
conn.close()
```

---

### Cursor Lifecycle

The **cursor** is the object that holds query state: the SQL text, bound parameters, and the internal pointer to the result set. Key lifecycle rules:

1. **Create** a cursor from a connection: `cur = conn.cursor()`.
2. **Execute** SQL on it: `cur.execute(...)`.
3. **Fetch** results (for SELECT): `cur.fetchone()`, `cur.fetchall()`, or iterate.
4. **Commit or rollback** via the *connection* (not the cursor).
5. **Re-use** the cursor for another query, or create a new one — but do not interleave concurrent queries on the same cursor.

> **`Connection.execute()`** is a shortcut that creates an implicit cursor — fine for one-off statements where you do not need to fetch results.

---

### In-Memory Databases

Pass `":memory:"` instead of a file path to create a database that lives **entirely in RAM**. This is the standard pattern for **unit tests** — every test gets a pristine, empty database with zero disk I/O:

```python
conn = sqlite3.connect(":memory:")
conn.execute("CREATE TABLE task (id INTEGER PRIMARY KEY, title TEXT)")
conn.execute("INSERT INTO task (title) VALUES (?)", ("Write tests",))
conn.commit()

row = conn.execute("SELECT title FROM task WHERE id = 1").fetchone()
print(row[0])   # "Write tests"
conn.close()    # database is destroyed — no file left behind
```

---

### WAL Mode — Concurrent Reads During Writes

By default, SQLite uses a **rollback journal**. An alternative is **Write-Ahead Logging (WAL)**, which allows **readers to proceed concurrently while a write is in progress**. Enable it per-connection:

```python
conn = sqlite3.connect("app.db")
conn.execute("PRAGMA journal_mode=WAL")
```

WAL is recommended for applications with multiple readers and occasional writes (e.g., a desktop app with a background sync thread). It is **not** a substitute for a client/server RDBMS under high write concurrency.

---

### Error Types

| Exception | When Raised |
|---|---|
| `sqlite3.OperationalError` | SQL syntax error, missing table, locked database |
| `sqlite3.IntegrityError` | Constraint violation (UNIQUE, NOT NULL, FOREIGN KEY) |
| `sqlite3.ProgrammingError` | Misuse of the API (bad parameter count, closed connection) |
| `sqlite3.DatabaseError` | Base class for all database-related exceptions |

---

### When to Use SQLite

**SQLite is the right choice when:**

- Developing **prototypes**, CLI utilities, or **test fixtures** (`:memory:`).
- Building **desktop** or **mobile** apps with embedded local storage.
- Running **automated tests** that need a real database without a server.
- Serving **low-to-moderate read traffic** with infrequent writes (single writer model).

**Prefer a client/server RDBMS (PostgreSQL, MySQL) when:**

- Many concurrent writers are expected.
- The database is shared between multiple application servers.
- You need advanced HA, replication, or centralized DBA administration.
- Dataset size and write throughput exceed SQLite's single-writer model.

---

## Common Pitfalls

| Pitfall | Problem | Fix |
|---|---|---|
| Forgetting `conn.commit()` after writes | Changes vanish when connection closes | Always commit after every logical unit of work |
| Not closing the connection | File lock held; other processes may be blocked | Use `try/finally` or context manager |
| String formatting SQL values | SQL injection vulnerability | Use `?` parameterized placeholders |
| Sharing a connection across threads | Default: raises `ProgrammingError` | Use `check_same_thread=False` + synchronization, or one connection per thread |
| Treating SQLite like PostgreSQL for concurrency | Deadlocks or `OperationalError: database is locked` | Keep transactions short; consider WAL mode |

---

## Summary

- **`sqlite3`** is built into Python and requires no server — open a file or `":memory:"` and start working.
- The core pattern: `connect` → `cursor` → `execute` → `commit` → `close`.
- **In-memory** databases are ideal for unit tests; **WAL mode** improves read concurrency.
- The same SQL discipline — constraints, parameterized queries, transactions — applies as in production engines.
- SQLite trades **concurrent writes** for **simplicity**; switch to PostgreSQL/MySQL when that trade-off no longer fits.

## Additional Resources

- [Python sqlite3 documentation](https://docs.python.org/3/library/sqlite3.html)
- [SQLite official documentation](https://www.sqlite.org/docs.html)
- [SQLite When to Use](https://www.sqlite.org/whentouse.html)
- [SQLite WAL mode](https://www.sqlite.org/wal.html)
