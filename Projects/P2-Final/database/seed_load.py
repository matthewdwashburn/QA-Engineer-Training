"""Seeds a large block of pending expenses for the JMeter write load plan.

manager_write_performance_test.jmx drives a counter over
/expenses/{id}/review for ids 1..5000. Against the normal seed -- 8 expenses,
3 of them pending -- almost every request returns 400, so the plan measures
the rejection path rather than the write path it was written to exercise.
This makes the plan's assumption about the database true.

Load-only: it replaces the seed baseline, so run reset_db.py afterwards if
anything else needs the normal fixture.
"""

import os
import sys
from pathlib import Path

import psycopg
from psycopg.rows import dict_row

base_dir = Path(__file__).parent
sys.path.append(str(base_dir.parent))

from employee_app.repository.database import resolve_dsn

# Must cover the counter's range in the .jmx, or the tail of the run 400s.
count = int(os.getenv("LOAD_EXPENSE_COUNT", "5000"))

# Same users seed.sql references: submitters 1 and 2, reviewer 3.
EXPECTED_USERS = {1: "brian", 2: "landon", 3: "siri"}

with psycopg.connect(resolve_dsn(), row_factory=dict_row) as conn:
    cursor = conn.cursor()

    rows = cursor.execute(
        "SELECT id, username FROM users WHERE id IN (1, 2, 3)"
    ).fetchall()
    found = {row["id"]: row["username"] for row in rows}
    if found != EXPECTED_USERS:
        sys.exit(f"Missing seeded users (expected {EXPECTED_USERS}, found {found}).")

    # Ids must restart at 1 so they line up with the plan's counter. approvals
    # is listed because it holds the foreign key onto expenses.
    cursor.execute("TRUNCATE approvals, expenses RESTART IDENTITY")

    expenses = [
        (1 if i % 2 else 2, 100.0 + i, f"Load test expense {i}", "OTHER", "2026-07-01")
        for i in range(1, count + 1)
    ]
    cursor.executemany(
        "INSERT INTO expenses (userId, amount, description, category, date)"
        " VALUES (%s, %s, %s, %s, %s)",
        expenses,
    )

    # Every expense left pending: the plan reviews each id exactly once.
    cursor.executemany(
        "INSERT INTO approvals (expenseId, status) VALUES (%s, 'pending')",
        [(i,) for i in range(1, count + 1)],
    )

    conn.commit()

print(f"Seeded {count} pending expenses for the load suite.")
