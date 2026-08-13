# SQLAlchemy Setup

## Learning Objectives

- Install SQLAlchemy and verify the installation.
- Create an **Engine** with a **connection string (URL)** for SQLite and understand URL patterns for other databases.
- Configure engine options: `echo`, `pool_pre_ping`, and SQLite-specific flags.
- Define **declarative models** using the modern SQLAlchemy 2.0 `Mapped` / `mapped_column` style.
- Bootstrap a development database with **`Base.metadata.create_all`**.

## Why This Matters

Correct **engine configuration** and **model definitions** are prerequisites for every ORM operation. Getting setup right the first time — using the SQLAlchemy 2.0 declarative API rather than deprecated 1.x patterns — avoids hours of migration headaches later. Understanding connection URLs is also essential because the same pattern applies to PostgreSQL, MySQL, and other production databases your teams will use.

## The Concept

### Installation

Install SQLAlchemy into your project's virtual environment:

```bash
pip install sqlalchemy
```

Verify the installed version:

```bash
python -c "import sqlalchemy; print(sqlalchemy.__version__)"
# Should print 2.x.x
```

> **SQLAlchemy 1.x vs 2.x:** Many tutorials online use the older 1.x API (`Session.query()`, `declarative_base()` from `sqlalchemy.ext.declarative`). These still work but are **legacy** — this module uses the **2.0 style** (`DeclarativeBase`, `Mapped`, `session.scalars(select(...))`). If you see `Session.query(Model)` in online examples, it is 1.x style.

---

### Engine and Connection URL

The **Engine** is the root SQLAlchemy object. It manages the **connection pool** and knows how to translate Python SQL constructs into dialect-specific SQL. You create it once, at application startup, and share it across the app.

```python
from sqlalchemy import create_engine

engine = create_engine(
    "sqlite:///app.db",    # connection URL
    echo=False,            # set True to log all SQL to stdout
    future=True,           # 2.0-mode behavior (default in SQLAlchemy 2.0+)
)
```

#### Connection URL Patterns

| Database | URL format | Notes |
|---|---|---|
| SQLite (relative file) | `sqlite:///relative.db` | Relative to process working directory |
| SQLite (absolute path) | `sqlite:////absolute/path/app.db` | Note: 4 slashes |
| SQLite (in-memory) | `sqlite:///:memory:` | Isolated per-engine; use `StaticPool` for shared in-memory |
| PostgreSQL | `postgresql+psycopg2://user:pass@host:5432/dbname` | Requires `psycopg2` driver |
| MySQL | `mysql+pymysql://user:pass@host/dbname` | Requires `pymysql` driver |
| SQL Server | `mssql+pyodbc://user:pass@host/db?driver=...` | Requires `pyodbc` |

#### Key Engine Parameters

| Parameter | Default | Purpose |
|---|---|---|
| `echo=True` | `False` | Logs every SQL statement and parameter — invaluable when learning |
| `echo="debug"` | `False` | Logs SQL + result rows |
| `pool_pre_ping=True` | `False` | Tests connections before use; recovers from stale pool connections |
| `pool_size` | 5 | Max open connections (not applicable to SQLite) |
| `connect_args` | `{}` | Driver-specific arguments (see below) |

#### SQLite-Specific Engine Configuration

For multithreaded applications using SQLite, pass `check_same_thread=False` via `connect_args`:

```python
engine = create_engine(
    "sqlite:///app.db",
    connect_args={"check_same_thread": False},  # needed for multithreaded apps (e.g., FastAPI)
    echo=False,
)
```

For **in-memory SQLite** shared across tests, use `StaticPool` so all connections reuse the same in-memory database:

```python
from sqlalchemy import create_engine
from sqlalchemy.pool import StaticPool

engine = create_engine(
    "sqlite:///:memory:",
    connect_args={"check_same_thread": False},
    poolclass=StaticPool,
)
```

---

### Declarative Base (SQLAlchemy 2.0 Style)

In the 2.0 API, define a `Base` class by subclassing `DeclarativeBase`. This `Base` holds the **metadata** (the schema registry) for all models registered under it.

```python
from sqlalchemy.orm import DeclarativeBase

class Base(DeclarativeBase):
    pass
```

> **One Base per application.** All models for a given database must inherit from the same `Base` so `Base.metadata.create_all(engine)` knows about them.

---

### Defining Models — `Mapped` and `mapped_column`

Each mapped class represents one database table. Annotate attributes with `Mapped[type]` to declare columns. This style uses Python's type hints — providing both ORM metadata and IDE type-checking.

```python
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column
from sqlalchemy import String, Integer

class Base(DeclarativeBase):
    pass

class Contact(Base):
    __tablename__ = "contact"

    id:    Mapped[int]        = mapped_column(primary_key=True, autoincrement=True)
    name:  Mapped[str]        = mapped_column(String(100), nullable=False)
    email: Mapped[str | None] = mapped_column(String(200), unique=True)
    active: Mapped[bool]      = mapped_column(default=True)

    def __repr__(self) -> str:
        return f"<Contact id={self.id} name={self.name!r}>"
```

**Column annotation rules:**

| Python type hint | Nullable? | Notes |
|---|---|---|
| `Mapped[str]` | `NOT NULL` | Required field |
| `Mapped[str \| None]` | `NULL` allowed | Optional field |
| `Mapped[int]` | `NOT NULL` | Non-nullable integer |
| `Mapped[bool]` | `NOT NULL` | Stored as `INTEGER` in SQLite |

> **`__repr__`** — always define it for models; it makes debugging sessions much easier when printing objects.

---

### Relationships — Declaring Associations

Relationships link two mapped classes. They must align with a `ForeignKey` column:

```python
from sqlalchemy import ForeignKey
from sqlalchemy.orm import relationship

class Order(Base):
    __tablename__ = "order"

    id:          Mapped[int]       = mapped_column(primary_key=True, autoincrement=True)
    customer_id: Mapped[int]       = mapped_column(ForeignKey("contact.id"), nullable=False)
    total:       Mapped[float]     = mapped_column()

    customer: Mapped["Contact"] = relationship(back_populates="orders")

# Add to Contact:
# orders: Mapped[list["Order"]] = relationship(back_populates="customer")
```

Relationships are covered in depth in the SQLAlchemy Usage module.

---

### Creating Tables — `create_all` (Development Only)

```python
Base.metadata.create_all(engine)
```

This inspects all classes that inherit from `Base` and emits `CREATE TABLE IF NOT EXISTS` DDL for any table not already present in the database. Safe to call on every startup during development.

**What it does NOT do:**
- It does **not** drop existing tables.
- It does **not** migrate existing tables (add columns, change types).
- It does **not** replace **Alembic** for production deployments.

> **Production schema management:** Use [Alembic](https://alembic.sqlalchemy.org/) for version-controlled migrations. `create_all` is a convenience for development and testing.

---

### `echo=True` — Understanding What SQLAlchemy Generates

When learning SQLAlchemy, enable `echo=True` to see the SQL it generates:

```python
engine = create_engine("sqlite:///app.db", echo=True)
Base.metadata.create_all(engine)
```

Output will include:
```sql
CREATE TABLE IF NOT EXISTS contact (
    id INTEGER NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(200),
    active BOOLEAN NOT NULL,
    PRIMARY KEY (id)
)
```

This is an excellent learning tool — compare what SQLAlchemy generates to the raw SQL you wrote earlier in the week.

---

## Common Pitfalls

| Pitfall | Problem | Fix |
|---|---|---|
| Using 1.x `declarative_base()` from `sqlalchemy.ext.declarative` | Deprecated; incompatible with 2.0 query style | Use `class Base(DeclarativeBase): pass` |
| Using `Session.query(Model)` | Legacy 1.x API — still works but not 2.0 style | Use `session.scalars(select(Model))` |
| Multiple `Base` classes for models in the same DB | `create_all` only sees models registered on that `Base` | All models must share one `Base` |
| Forgetting `check_same_thread=False` for FastAPI/Flask + SQLite | `ProgrammingError` under concurrent requests | Pass via `connect_args` |
| Using `create_all` in production without Alembic | Missing migrations; schema drift | Use Alembic for production |
| Not defining `__repr__` | Debugging sessions are painful | Always define `__repr__` for model classes |

---

## Summary

- **`create_engine`** connects SQLAlchemy to the database via a URL string; configure `echo=True` while learning.
- Connection URLs follow a standard pattern — `dialect+driver://user:pass@host/dbname`; for SQLite it is `sqlite:///path.db`.
- **`DeclarativeBase`** subclasses provide the metadata registry; all models inherit from a single shared `Base`.
- **`Mapped[type]` / `mapped_column`** is the 2.0-style way to declare columns with type safety.
- **`create_all`** bootstraps development databases; use Alembic for production schema evolution.

## Additional Resources

- [SQLAlchemy Engine Configuration](https://docs.sqlalchemy.org/en/20/core/engines.html)
- [SQLAlchemy ORM Declarative Mapping (2.0)](https://docs.sqlalchemy.org/en/20/orm/declarative_styles.html)
- [SQLAlchemy 2.0 Migration Guide](https://docs.sqlalchemy.org/en/20/changelog/migration_20.html)
- [Alembic migrations](https://alembic.sqlalchemy.org/en/latest/)
