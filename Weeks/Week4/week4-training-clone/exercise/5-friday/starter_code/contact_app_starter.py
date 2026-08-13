"""
Trainee starter — sqlite3 contact CLI.
TODO: implement subcommands per exercise_sqlite3.md
Run: python contact_app_starter.py --help
"""

from __future__ import annotations

import argparse
import sqlite3
from pathlib import Path

DEFAULT_DB = Path(__file__).with_name("contacts.db")


def open_db(path: Path) -> sqlite3.Connection:
    conn = sqlite3.connect(path)
    conn.execute("PRAGMA foreign_keys = ON")
    conn.row_factory = sqlite3.Row
    return conn


def bootstrap(conn: sqlite3.Connection) -> None:
    conn.executescript(
        """
        CREATE TABLE IF NOT EXISTS contact (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            email TEXT NOT NULL UNIQUE
        );
        """
    )
    conn.commit()


def main() -> None:
    parser = argparse.ArgumentParser(description="Contacts (sqlite3 exercise)")
    parser.add_argument("--db", type=Path, default=DEFAULT_DB, help="SQLite database file")
    sub = parser.add_subparsers(dest="cmd", required=True)

    sub.add_parser("list", help="List contacts")

    p_add = sub.add_parser("add", help="Add contact")
    p_add.add_argument("--name", required=True)
    p_add.add_argument("--email", required=True)

    p_get = sub.add_parser("get", help="Get one contact")
    p_get.add_argument("--id", type=int, required=True)

    p_upd = sub.add_parser("update", help="Update contact")
    p_upd.add_argument("--id", type=int, required=True)
    p_upd.add_argument("--name")
    p_upd.add_argument("--email")

    p_del = sub.add_parser("delete", help="Delete contact")
    p_del.add_argument("--id", type=int, required=True)

    args = parser.parse_args()
    conn = open_db(args.db)
    bootstrap(conn)

    if args.cmd == "list":
        raise SystemExit("TODO: SELECT * FROM contact")
    if args.cmd == "add":
        raise SystemExit("TODO: INSERT with ? placeholders")
    if args.cmd == "get":
        raise SystemExit("TODO: fetchone")
    if args.cmd == "update":
        raise SystemExit("TODO: dynamic UPDATE with WHERE id = ?")
    if args.cmd == "delete":
        raise SystemExit("TODO: DELETE WHERE id = ?")


if __name__ == "__main__":
    main()
