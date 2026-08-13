# SQLAlchemy ORM: CRUD, Queries, and Relationships

## Learning Objectives

- Perform full **CRUD** operations with **`Session`**: `add`, `get`, `delete`, `commit`.
- Write **`select`** queries with `where`, `order_by`, `limit`, and compound conditions using the 2.0 style.
- Understand **session state lifecycle**: `transient`, `pending`, `persistent`, `detached`.
- Declare and navigate **relationships** with `ForeignKey` and `relationship`.
- Compare **eager** (`selectinload`, `joinedload`) and **lazy** loading strategies, and recognize the **N+1 problem**.
- Use `session.refresh()` and understand `expire_on_commit`.

## Why This Matters

ORM usage is a daily skill in Python web services, QA tooling, and data pipelines. Knowing **session boundaries** and **loading strategies** prevents the two most common ORM performance bugs: **N+1 query storms** and **stale object reads** after a commit. These patterns are directly analogous to JPA/Hibernate session and lazy-loading behavior from the Java track.

## The Concept

### Assumed Setup

All examples below assume the following models and engine exist (from the Setup reading):

```python
from sqlalchemy import create_engine, select, ForeignKey
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column, relationship, Session

engine = create_engine("sqlite:///app.db", echo=False)

class Base(DeclarativeBase):
    pass

class Contact(Base):
    __tablename__ = "contact"
    id:    Mapped[int]        = mapped_column(primary_key=True, autoincrement=True)
    name:  Mapped[str]        = mapped_column(nullable=False)
    email: Mapped[str | None] = mapped_column(unique=True)
    orders: Mapped[list["Order"]] = relationship(back_populates="customer", cascade="all, delete-orphan")

class Order(Base):
    __tablename__ = "order"
    id:          Mapped[int]   = mapped_column(primary_key=True, autoincrement=True)
    customer_id: Mapped[int]   = mapped_column(ForeignKey("contact.id"), nullable=False)
    total:       Mapped[float] = mapped_column()
    customer:    Mapped["Contact"] = relationship(back_populates="orders")

Base.metadata.create_all(engine)
```

---

### Session Lifecycle — Object States

Every mapped object passes through four states:

| State | Description | How to get here |
|---|---|---|
| **Transient** | Created in Python; not known to any Session | `obj = Contact(name="Ada")` |
| **Pending** | Added to Session; not yet flushed to DB | `session.add(obj)` |
| **Persistent** | In Session + exists in DB (has a PK) | After `flush()` or `commit()` |
| **Detached** | Had a Session; now disconnected | After `session.close()` or `session.expunge(obj)` |

Understanding these states explains why accessing a relationship attribute on a **detached** object raises `DetachedInstanceError`.

---

### CREATE — Adding Objects

```python
with Session(engine) as session:
    ada = Contact(name="Ada Lovelace", email="ada@example.com")
    session.add(ada)
    session.flush()    # optional: assigns ada.id without committing
    print(f"Assigned id: {ada.id}")
    session.commit()   # persists INSERT; ada is now Persistent
```

**`session.flush()`** sends SQL to the database but does **not** commit the transaction. Use it when you need the generated PK before the transaction ends (e.g., to insert related rows).

To add multiple objects at once:

```python
with Session(engine) as session:
    session.add_all([
        Contact(name="Bob Smith", email="bob@example.com"),
        Contact(name="Chen Wei", email="chen@example.com"),
    ])
    session.commit()
```

---

### READ — Querying with `select`

The 2.0-style query API uses `select()` constructs passed to `session.scalars()` or `session.execute()`.

#### Get by Primary Key

```python
with Session(engine) as session:
    contact = session.get(Contact, 1)   # returns None if not found
    if contact:
        print(contact.name)
```

#### Filter with `where`

```python
with Session(engine) as session:
    stmt = select(Contact).where(Contact.email == "ada@example.com")
    ada = session.scalar(stmt)          # returns first result or None
```

#### Multiple filters, ordering, and limiting

```python
with Session(engine) as session:
    stmt = (
        select(Contact)
        .where(Contact.name.like("A%"))   # starts with A
        .where(Contact.email.is_not(None))
        .order_by(Contact.name)
        .limit(10)
    )
    contacts = session.scalars(stmt).all()   # list[Contact]
    for c in contacts:
        print(c.name, c.email)
```

#### `scalar` vs `scalars` vs `execute`

| Method | Returns | Use when |
|---|---|---|
| `session.scalar(stmt)` | Single object or `None` | Expect exactly 0 or 1 result |
| `session.scalars(stmt).all()` | `list` of objects | Expect multiple results |
| `session.scalars(stmt).first()` | First object or `None` | Want first of potentially many |
| `session.execute(stmt)` | `Result` rows (Core style) | Need multiple columns not on one model |

---

### UPDATE — Modify and Commit

The Session **tracks attribute changes** on persistent objects. Assigning a new value marks the object as **dirty**; the `UPDATE` is emitted on the next flush/commit.

```python
with Session(engine) as session:
    ada = session.scalar(select(Contact).where(Contact.email == "ada@example.com"))
    if ada:
        ada.name = "Ada L. Lovelace"   # Session marks as dirty
        session.commit()                # emits: UPDATE contact SET name=? WHERE id=?
```

For bulk updates without loading objects:

```python
from sqlalchemy import update

with Session(engine) as session:
    session.execute(
        update(Contact).where(Contact.name.like("A%")).values(name="Updated")
    )
    session.commit()
```

---

### DELETE — Remove Objects

```python
with Session(engine) as session:
    ada = session.get(Contact, 1)
    if ada:
        session.delete(ada)   # marks for deletion
        session.commit()      # emits: DELETE FROM contact WHERE id=?
```

For bulk deletes:

```python
from sqlalchemy import delete

with Session(engine) as session:
    session.execute(delete(Contact).where(Contact.name == "Test User"))
    session.commit()
```

---

### `expire_on_commit` and `session.refresh`

After `session.commit()`, SQLAlchemy **expires** all attributes on persistent objects by default (`expire_on_commit=True`). The next access to any attribute triggers a fresh `SELECT` — ensuring you never read stale data.

```python
with Session(engine) as session:
    ada = session.get(Contact, 1)
    session.commit()
    # ada.name access here triggers a SELECT — attributes were expired on commit
    print(ada.name)   # fresh from DB
```

If you need to explicitly reload an object (e.g., after a bulk update):

```python
with Session(engine) as session:
    ada = session.get(Contact, 1)
    session.execute(update(Contact).where(Contact.id == 1).values(name="Refreshed"))
    session.commit()
    session.refresh(ada)     # forces a SELECT to reload ada's attributes
    print(ada.name)          # "Refreshed"
```

---

### Relationships

Relationships let you traverse from one object to its related objects in Python — SQLAlchemy generates the `JOIN` or sub-select automatically.

```python
# Insert related data
with Session(engine) as session:
    ada = session.get(Contact, 1)
    order = Order(customer=ada, total=49.99)
    session.add(order)
    session.commit()

# Navigate relationship
with Session(engine) as session:
    ada = session.get(Contact, 1)
    for order in ada.orders:           # triggers a SELECT on first access (lazy load)
        print(f"Order {order.id}: ${order.total}")
```

---

### Eager vs Lazy Loading — and the N+1 Problem

#### Lazy Loading (default)

Related objects are loaded **on first access** via a separate `SELECT`. This is convenient but causes the **N+1 problem** when iterating:

```python
# ❌ N+1 problem — 1 SELECT for contacts + N SELECTs for orders
with Session(engine) as session:
    contacts = session.scalars(select(Contact)).all()
    for c in contacts:
        print(c.orders)   # each access fires a new SELECT to load orders
```

#### Eager Loading with `selectinload` (Recommended)

`selectinload` issues a **second query** (using `WHERE id IN (...)`) to load all related objects in a single round-trip — eliminating N+1:

```python
from sqlalchemy.orm import selectinload

# ✅ 2 total queries: 1 for contacts, 1 for all their orders
with Session(engine) as session:
    stmt = select(Contact).options(selectinload(Contact.orders))
    contacts = session.scalars(stmt).all()
    for c in contacts:
        print(c.orders)   # already loaded — no extra SELECT
```

#### Eager Loading with `joinedload`

`joinedload` uses a SQL `LEFT OUTER JOIN` to load related objects in **a single query**. Best when loading a small number of objects with their relationships:

```python
from sqlalchemy.orm import joinedload

with Session(engine) as session:
    stmt = select(Contact).options(joinedload(Contact.orders)).where(Contact.id == 1)
    ada = session.scalar(stmt)
    print(ada.orders)   # loaded via JOIN — one query total
```

#### Loading Strategy Comparison

| Strategy | SQL issued | Best for |
|---|---|---|
| Lazy (default) | 1 + N queries | Accessing relationships rarely / on a single object |
| `selectinload` | 2 queries (main + IN clause) | Iterating a collection with relationships |
| `joinedload` | 1 query (JOIN) | Single object + its relationships |

> **Rule of thumb:** If you are iterating over a list and accessing a relationship attribute inside the loop, use `selectinload`. If you are loading one object and want its relationships, use `joinedload`.

---

## Common Pitfalls

| Pitfall | Problem | Fix |
|---|---|---|
| Accessing relationship after `session.close()` | `DetachedInstanceError` | Load relationships eagerly before session closes, or use `expire_on_commit=False` |
| Iterating and accessing lazy relationship | N+1 query storm | Use `selectinload` or `joinedload` |
| Forgetting `session.commit()` after add/delete | Changes not persisted | Always commit at logical unit boundaries |
| Using `Session.query(Model)` | 1.x legacy API | Use `session.scalars(select(Model))` |
| Using `session.get` and ignoring `None` | `AttributeError` when row absent | Always guard: `if obj is None: raise / return` |
| Modifying an object after `session.close()` | Changes silently ignored or error | Modify within the session's `with` block |

---

## Summary

- **`Session`** tracks the state of objects: transient → pending → persistent → detached.
- **CRUD:** `session.add()` + `commit()` for insert; attribute assignment + `commit()` for update; `session.delete()` + `commit()` for delete.
- **Queries** use `select(Model).where(...).order_by(...).limit(...)` passed to `session.scalars()` or `session.scalar()`.
- After `commit()`, attributes are **expired** — next access re-fetches from DB. Use `session.refresh(obj)` to reload manually.
- **Relationships** allow Python-level traversal; default is **lazy** — switch to `selectinload` or `joinedload` to prevent N+1 problems.

## Additional Resources

- [SQLAlchemy ORM Session Basics](https://docs.sqlalchemy.org/en/20/orm/session_basics.html)
- [SQLAlchemy Relationship Loading Techniques](https://docs.sqlalchemy.org/en/20/orm/loading_relationships.html)
- [SQLAlchemy SELECT statements (2.0 tutorial)](https://docs.sqlalchemy.org/en/20/tutorial/data_select.html)
- [SQLAlchemy ORM Cascades](https://docs.sqlalchemy.org/en/20/orm/cascades.html)
- [N+1 query problem explained](https://docs.sqlalchemy.org/en/20/glossary.html#term-N-plus-one-problem)
