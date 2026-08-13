# Defining and Creating Schemas with sqlite3

## Learning Objectives

- Create tables and constraints from Python using `sqlite3` DDL statements.
- Map logical SQL types to **SQLite's dynamic typing** (affinity rules) and avoid common affinity bugs.
- Create **indexes** programmatically to improve query performance.
- Enable and use **`FOREIGN KEY`** constraints correctly with the required PRAGMA.
- Outline basic **migration** strategies for evolving schema over time.

## Why This Matters

Your Python code owns the **lifecycle** of a local SQLite database: creating tables on first run, adding columns or rebuilding tables between application versions. Unlike a DBA-managed PostgreSQL instance, there is no external schema owner — your Python bootstrapping code *is* the migration system. Understanding SQLite's type affinity prevents subtle bugs that only surface when reading data back. Knowing how to index columns correctly is the difference between a fast CLI tool and one that becomes unusably slow as data grows.

## The Concept

### Creating Tables — `CREATE TABLE IF NOT EXISTS`

Use the `IF NOT EXISTS` guard on every `CREATE TABLE` you run at startup. This makes the statement **idempotent** — safe to call multiple times without error, regardless of whether the table already exists.

```python
import sqlite3

conn = sqlite3.connect("app.db")
cur = conn.cursor()

cur.execute("""
    CREATE TABLE IF NOT EXISTS task (
        id        INTEGER PRIMARY KEY,
        title     TEXT    NOT NULL,
        due_date  TEXT,
        priority  INTEGER NOT NULL DEFAULT 1 CHECK (priority BETWEEN 1 AND 5),
        done      INTEGER NOT NULL DEFAULT 0 CHECK (done IN (0, 1))
    )
""")
conn.commit()
```

**Column anatomy:**
- `INTEGER PRIMARY KEY` — SQLite alias for the internal `rowid`; auto-assigned if you insert with `id = NULL` or omit it.
- `NOT NULL` — rejects `NULL` values at the driver level (before Python sees it).
- `DEFAULT` — used when no value is supplied in `INSERT`.
- `CHECK (expr)` — evaluated per-row; raises `IntegrityError` on violation.

---

### SQLite Type Affinity — The Dynamic Typing Model

SQLite uses **dynamic typing**: the *value* carries a type, not the column. Columns declare an **affinity** that SQLite uses as a preference when coercing stored values. This differs fundamentally from PostgreSQL's strict typing.

| Affinity | Storage Preference | Typical SQL Column Declarations |
|---|---|---|
| `INTEGER` | Signed integer (1–8 bytes) | `INT`, `INTEGER`, `BIGINT`, `SMALLINT` |
| `TEXT` | UTF-8 string | `TEXT`, `VARCHAR(n)`, `CHAR`, `CLOB` |
| `REAL` | 8-byte IEEE 754 float | `REAL`, `FLOAT`, `DOUBLE` |
| `BLOB` | Raw bytes, no conversion | `BLOB` |
| `NUMERIC` | Integer if lossless, else REAL | `DECIMAL`, `NUMERIC`, `BOOLEAN`, `DATE` |

**Practical implications:**

```python
# SQLite will store this as INTEGER, not TEXT — even if column is TEXT
cur.execute("INSERT INTO task (id, title) VALUES (?, ?)", (1, 42))
# Reading back gives integer 42, not string "42" — may surprise you
```

**Datetime handling** — SQLite has **no native datetime type**. Use one of these consistent strategies:

| Strategy | Column type | Example value | Pros / Cons |
|---|---|---|---|
| ISO-8601 string | `TEXT` | `"2025-06-01T14:30:00"` | Human-readable; sorts correctly; easy in Python |
| Unix timestamp | `INTEGER` | `1748785800` | Compact; timezone-agnostic; less readable |
| Julian day | `REAL` | `2461048.1041...` | SQLite built-in functions use this format |

**Recommendation:** Use `TEXT` with ISO-8601 (`datetime.isoformat()`) unless you have a specific reason for integers.

---

### Constraints

Constraints enforce **data integrity rules** at the database level — they fire even when data is inserted via a DB browser or migration tool, not just your Python code.

| Constraint | Syntax | Behavior |
|---|---|---|
| `PRIMARY KEY` | Column or table level | Unique + NOT NULL; auto-assigns `rowid` |
| `UNIQUE` | `column UNIQUE` or `UNIQUE(a, b)` | Raises `IntegrityError` on duplicate |
| `NOT NULL` | `column TEXT NOT NULL` | Rejects `NULL` inserts |
| `CHECK` | `CHECK (expr)` | Validates expression per row |
| `FOREIGN KEY` | `REFERENCES other_table(col)` | Enforces referential integrity (requires PRAGMA) |

---

### Foreign Keys — Require a PRAGMA

SQLite **does not enforce foreign keys by default** — you must enable them each time you open a connection:

```python
conn = sqlite3.connect("app.db")
conn.execute("PRAGMA foreign_keys = ON")    # must be before any DML
```

**FK definition with cascade:**

```python
cur.execute("""
    CREATE TABLE IF NOT EXISTS task_comment (
        id      INTEGER PRIMARY KEY,
        task_id INTEGER NOT NULL REFERENCES task(id) ON DELETE CASCADE,
        body    TEXT    NOT NULL
    )
""")
conn.commit()
```

`ON DELETE CASCADE` automatically removes all `task_comment` rows when their parent `task` row is deleted. Other options: `SET NULL`, `RESTRICT`, `NO ACTION`.

> **Tip:** Create a helper to always enable FK enforcement consistently:
> ```python
> def get_connection(path: str) -> sqlite3.Connection:
>     conn = sqlite3.connect(path)
>     conn.execute("PRAGMA foreign_keys = ON")
>     return conn
> ```

---

### Indexes — Speeding Up Queries

SQLite automatically creates an index for `PRIMARY KEY` and `UNIQUE` columns. For all other frequently-queried columns (especially those used in `WHERE`, `JOIN`, and `ORDER BY`), create explicit indexes:

```python
# Single-column index
cur.execute("CREATE INDEX IF NOT EXISTS idx_task_due_date ON task(due_date)")

# Composite index for multi-column filters
cur.execute(
    "CREATE INDEX IF NOT EXISTS idx_task_priority_done ON task(priority, done)"
)

conn.commit()
```

> **When to index:** Any column appearing in a `WHERE`, `JOIN ON`, or `ORDER BY` clause is a candidate. Over-indexing slows `INSERT`/`UPDATE` because every write must update all affected indexes.

---

### Migrations — Evolving the Schema

SQLite has limited `ALTER TABLE` support compared to PostgreSQL. Supported:

- `ALTER TABLE t RENAME TO new_name`
- `ALTER TABLE t ADD COLUMN col type`

**Not supported directly:** drop column, rename column, change column type (requires a rebuild).

**Common migration patterns for Python apps:**

#### 1. Version table (simple apps)

```python
def get_schema_version(conn):
    conn.execute("CREATE TABLE IF NOT EXISTS schema_version (version INTEGER)")
    row = conn.execute("SELECT version FROM schema_version").fetchone()
    return row[0] if row else 0

def migrate(conn):
    v = get_schema_version(conn)
    if v < 1:
        conn.execute("ALTER TABLE task ADD COLUMN priority INTEGER DEFAULT 1")
        conn.execute("UPDATE schema_version SET version = 1")
        conn.commit()
```

#### 2. Table rebuild (for structural changes)

```python
# To drop a column or change a type:
conn.execute("ALTER TABLE task RENAME TO task_old")
conn.execute("CREATE TABLE task (id INTEGER PRIMARY KEY, title TEXT NOT NULL, priority INTEGER DEFAULT 1)")
conn.execute("INSERT INTO task SELECT id, title, 1 FROM task_old")
conn.execute("DROP TABLE task_old")
conn.commit()
```

> **For training apps:** `CREATE IF NOT EXISTS` with static DDL is sufficient. For production, adopt **Alembic** (covered briefly in the SQLAlchemy readings).

---

## Common Pitfalls

| Pitfall | Problem | Fix |
|---|---|---|
| Omitting `IF NOT EXISTS` on CREATE | `OperationalError` if table already exists | Always use `IF NOT EXISTS` at startup |
| Forgetting `PRAGMA foreign_keys = ON` | FK violations silently accepted | Enable in every new connection |
| Using `NUMERIC` affinity for booleans | Python `True`/`False` stored as `1`/`0` OK, but reads back as int | Store as `INTEGER 0/1` explicitly; map in application code |
| Assuming column order is stable | Positional index bugs after `ALTER TABLE ADD COLUMN` | Use `row_factory = sqlite3.Row` and named access |
| Indexing every column by default | Slower inserts; wasted disk | Index only query-critical columns |
| Using SQLite datetime functions without ISO-8601 strings | `strftime` / `date` functions may fail | Store datetimes consistently in ISO-8601 TEXT format |

---

## Summary

- Execute DDL through cursors like any SQL; use `IF NOT EXISTS` to keep startup code idempotent.
- Respect SQLite **affinity** — store datetimes as ISO-8601 `TEXT` strings unless you have a specific reason otherwise.
- Enable **`PRAGMA foreign_keys = ON`** in every new connection if your schema uses FK constraints; they are silently ignored otherwise.
- Create **indexes** on frequently-filtered, joined, or sorted columns to maintain performance as data grows.
- Migrations in SQLite: `ADD COLUMN` via `ALTER TABLE` for simple additions; full table rebuild for structural changes.

## Additional Resources

- [SQLite CREATE TABLE](https://www.sqlite.org/lang_createtable.html)
- [SQLite Type Affinity](https://www.sqlite.org/datatype3.html)
- [SQLite ALTER TABLE](https://www.sqlite.org/lang_altertable.html)
- [SQLite Foreign Keys](https://www.sqlite.org/foreignkeys.html)
- [SQLite CREATE INDEX](https://www.sqlite.org/lang_createindex.html)
