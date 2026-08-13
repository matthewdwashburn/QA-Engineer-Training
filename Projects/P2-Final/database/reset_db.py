"""Restores expenses and approvals to the seeded baseline, between e2e scenarios.

Data-only on purpose: users are left alone so their bcrypt hashes never have to
be recomputed here. Runs inside the db-init image, which already reaches the
Postgres service over the compose network.
"""

import sys
from pathlib import Path

import psycopg
from psycopg.rows import dict_row

base_dir = Path(__file__).parent
sys.path.append(str(base_dir.parent))

from employee_app.repository.database import resolve_dsn

# Users seed.sql references: userId 1 and 2, reviewer 3.
EXPECTED_USERS = {1: "brian", 2: "landon", 3: "siri"}

with psycopg.connect(resolve_dsn(), row_factory=dict_row) as conn:
    cursor = conn.cursor()

    # Fail early and actionably rather than seeding rows for absent users.
    rows = cursor.execute(
        "SELECT id, username FROM users WHERE id IN (1, 2, 3)"
    ).fetchall()
    found = {row["id"]: row["username"] for row in rows}
    if found != EXPECTED_USERS:
        sys.exit(f"Missing seeded users (expected {EXPECTED_USERS}, found {found}).")

    # seed.sql references expense ids 1-8 literally, so the identity counters
    # have to go back to 1 along with the rows. approvals is listed because it
    # holds the foreign key onto expenses.
    cursor.execute("TRUNCATE approvals, expenses RESTART IDENTITY")

    seed_file = base_dir / "seed.sql"
    if not seed_file.exists():
        sys.exit(f"seed.sql was not found at {seed_file}")
    cursor.execute(seed_file.read_text())

    conn.commit()

print("Database reset to seed baseline.")
