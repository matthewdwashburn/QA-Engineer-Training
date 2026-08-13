# Exercise: Contact management with sqlite3

## Goal

Build a **command-line** contact manager using only the Python **`sqlite3`** standard library.

## Data model

- **Contact:** `id` (integer PK autoincrement), `name` (text, not null), `email` (text, unique, not null)
- **Phone** (optional stretch): `id`, `contact_id` FK, `label` (e.g. `mobile`), `number` (text)

Minimum for **core** submission: **Contact** table only. If you add **Phone**, enforce **`ON DELETE CASCADE`** (SQLite: `PRAGMA foreign_keys = ON`).

## CLI contract

Implement `contact_app.py` (name may match starter) with **`argparse`** subcommands:

| Command | Behavior |
|---------|----------|
| `add --name X --email Y` | Insert; print new `id` |
| `list` | Print all contacts |
| `get --id N` | Print one contact or “not found” |
| `update --id N [--name X] [--email Y]` | Partial update |
| `delete --id N` | Delete by id |

Use **`?` placeholders** for all dynamic SQL values.

## Requirements

- Create DB file next to the script or via `--db path` flag (your choice — document in module docstring).
- Use **`conn.commit()`** after writes; handle **`sqlite3.IntegrityError`** for duplicate email with a friendly message.
- Enable **`row_factory = sqlite3.Row`** for readable prints.

## Definition of done

- `python contact_app.py --help` works.
- Manual test sequence in comments at bottom of file (or `TESTS.md`): add two contacts, update one, delete one, list.

## Reference

- `written/5-friday/intro-to-sqlite3.md`
- `written/5-friday/data-insertion-and-persistence.md`
- `demos/5-friday/code/demo_sqlite3.py`
