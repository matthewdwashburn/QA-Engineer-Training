# Exercise: Refactor contacts to SQLAlchemy ORM

## Goal

Re-implement the **Friday sqlite3** contact app using **SQLAlchemy 2.x ORM** with a **one-to-many** relationship.

## Model

- **`Contact`:** `id`, `name`, `email` (unique)
- **`PhoneNumber`:** `id`, `contact_id` FK, `label`, `number`

Requirements:

- Declarative mappings with typing style from class notes (`Mapped`, `mapped_column`).
- **`relationship`** with `back_populates` (or equivalent) so `contact.phone_numbers` works.
- **`Session`**-based CRUD — no hand-written `INSERT` strings in your service functions.

## CLI

Match the same subcommands as Part 1 **or** document intentional differences in a **`REFACTOR_NOTES.md`** (max half page).

Minimum commands: **`add`**, **`list`**, **`get`**, **`delete`**; **`add-phone`** / **`list-phones`** for the child rows.

## Technical requirements

- `create_engine("sqlite:///...")` with a configurable path (arg or constant at top).
- `Base.metadata.create_all(engine)` acceptable for this lab (no Alembic required).
- Use **`select()`** for reads; add/update/delete via session API.

## Definition of done

- Adding a contact with **two phone numbers** persists all three rows; `list` shows nested data clearly.
- Deleting a contact **cascades** phone rows (ORM `cascade` **or** DB FK — state which).

## Reference

- `written/5-friday/sqlalchemy-overview.md`, `sqlalchemy-setup.md`, `sqlalchemy-usage.md`
- `demos/5-friday/code/demo_sqlalchemy.py`
