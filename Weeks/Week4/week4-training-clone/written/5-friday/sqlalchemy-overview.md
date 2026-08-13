# SQLAlchemy Overview

## Learning Objectives

- Define **ORM** and explain how it maps relational tables to Python classes.
- Distinguish **SQLAlchemy Core** (SQL expression layer) from the **ORM** layer, and know when to use each.
- Describe the **declarative mapping** style and what the **Base** class provides.
- Explain the **Session** as a unit-of-work and its role in tracking object state.
- Recognize how SQLAlchemy relates to JPA/Hibernate from Thursday's Java content.

## Why This Matters

Raw `sqlite3` SQL strings work for scripts and utilities, but larger Python applications need **structured**, testable data access patterns. SQLAlchemy is the **de facto standard** for Python database access in web frameworks (FastAPI, Flask, Django with SQLAlchemy extensions), data pipelines, and QA tooling alike. Understanding it at a conceptual level makes you immediately productive in most professional Python codebases — the same way knowing JPA concepts readies you for Spring Boot projects.

## The Concept

### What is an ORM?

An **Object-Relational Mapper (ORM)** bridges two fundamentally different paradigms:

| Relational World | Object-Oriented World |
|---|---|
| Table | Python class |
| Row | Instance of that class |
| Column | Attribute / field |
| Foreign key join | Object reference / relationship attribute |
| `INSERT` / `UPDATE` / `DELETE` | Create / modify / delete an object, then flush |

The ORM **generates SQL automatically** from object operations, so you work primarily in Python while the ORM handles the translation. This centralizes SQL generation, making it easier to switch databases and to test data-access logic.

> **Analogy:** If JDBC is Python's `sqlite3` (raw SQL, explicit result mapping), then **JPA/Hibernate** is SQLAlchemy ORM. Both solve the same impedance mismatch — just in Java vs Python.

---

### SQLAlchemy's Two Layers

SQLAlchemy is **not** just an ORM. It is a toolkit with two distinct, composable layers:

#### 1. Core — SQL Expression Language

The Core layer provides a **Pythonic API for composing SQL** — without mapping to objects. You build `select()`, `insert()`, `update()` constructs that render to dialect-specific SQL. Core is appropriate when:

- You need **full SQL control** (complex joins, window functions, CTEs).
- You are writing reporting queries or ETL scripts without needing model objects.
- You want the **safety** of parameterized SQL without the overhead of the ORM.

```python
from sqlalchemy import text, create_engine

engine = create_engine("sqlite:///app.db")
with engine.connect() as conn:
    result = conn.execute(text("SELECT id, name FROM contact WHERE active = :active"), {"active": 1})
    for row in result:
        print(row.name)
```

#### 2. ORM — Declarative Mapping + Session

The ORM layer adds **class-to-table mapping**, **relationship navigation**, and the **unit-of-work Session**. It is appropriate when:

- Your app has a **domain model** (entities with behavior, not just data bags).
- You need **relationship traversal** (e.g., `order.customer.name`).
- You want **change tracking** so you do not need to write every `UPDATE` manually.

---

### Core vs ORM Comparison

| Dimension | Core | ORM |
|---|---|---|
| Abstraction level | SQL expressions in Python | Python classes ↔ tables |
| Query style | `select(table).where(...)` | `session.scalars(select(Model)...)` |
| Result type | `Row` objects | Mapped model instances |
| Change tracking | Manual `INSERT`/`UPDATE` | Automatic via `Session` |
| Best for | ETL, reporting, complex SQL | Application domain models |
| Can be mixed | ✅ Yes — ORM sessions can run Core queries | ✅ Yes |

---

### Declarative Mapping

In the **declarative** style, you define a Python class that inherits from a `Base` (a registry object). Attributes annotated with `Mapped` types declare columns. SQLAlchemy reads these definitions to:

1. Know the table name and column schema.
2. Generate `CREATE TABLE` DDL when asked.
3. Map SELECT result rows back to instances of the class.

```python
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column

class Base(DeclarativeBase):
    pass

class Contact(Base):
    __tablename__ = "contact"
    id:    Mapped[int]       = mapped_column(primary_key=True, autoincrement=True)
    name:  Mapped[str]       = mapped_column(nullable=False)
    email: Mapped[str | None] = mapped_column(unique=True)
```

The `Base` also holds **`metadata`** — a registry of all mapped tables — used to create or introspect the schema.

---

### The Session — Unit of Work

The **`Session`** is the ORM's central work tracker. It implements the **Unit of Work** pattern:

1. **Identity Map:** Every object loaded through a Session is tracked by its primary key. Loading the same row twice within a Session returns **the same Python object** — not two copies.
2. **Change Tracking:** When you modify an attribute on a tracked object (`contact.name = "New Name"`), the Session marks it as **dirty**.
3. **Flush:** Before committing (or on explicit `session.flush()`), the Session inspects all new/dirty/deleted objects and emits the required SQL (`INSERT`, `UPDATE`, `DELETE`) in dependency order.
4. **Commit:** Persists the transaction; resets tracking state.
5. **Rollback:** Reverts all tracked changes; objects return to their last committed state.

```
User code         Session              Database
─────────         ───────              ────────
add(contact)  →   marks as "new"
flush()       →                    →  INSERT INTO contact ...
commit()      →                    →  COMMIT
```

> **Sessions should be short-lived** — typically scoped to a single request (web) or a single logical operation (script). Never keep one open indefinitely.

---

### Relationship to Other Tools

| Tool | Role |
|---|---|
| **`sqlite3`** | Raw SQL, direct control, no mapping |
| **SQLAlchemy Core** | Pythonic SQL builder, parameterized, no mapping |
| **SQLAlchemy ORM** | Full class-to-table mapping, change tracking |
| **Alembic** | Migration tool built on SQLAlchemy — handles schema evolution |
| **FastAPI + SQLAlchemy** | Common production stack for Python APIs |

---

## Summary

- **ORM** maps tables → classes and rows → instances, eliminating repetitive `SELECT` → manual mapping boilerplate.
- **SQLAlchemy Core** gives you Python-composed SQL expressions without object mapping — useful for complex queries and scripts.
- **Declarative mapping** defines tables as Python classes with typed `Mapped` attributes, read by SQLAlchemy's metadata system.
- The **Session** implements unit-of-work: it tracks new/dirty/deleted objects and flushes the minimum required SQL on commit.
- SQLAlchemy mirrors the JPA/Hibernate conceptual model — if you know one, the other is approachable.

## Additional Resources

- [SQLAlchemy Documentation Home](https://docs.sqlalchemy.org/en/20/)
- [SQLAlchemy ORM Quick Start](https://docs.sqlalchemy.org/en/20/orm/quickstart.html)
- [SQLAlchemy Core Expression Tutorial](https://docs.sqlalchemy.org/en/20/core/tutorial.html)
- [Unit of Work pattern (Martin Fowler)](https://martinfowler.com/eaaCatalog/unitOfWork.html)
- [SQLAlchemy vs raw SQL – when to use what](https://docs.sqlalchemy.org/en/20/faq/performance.html)
