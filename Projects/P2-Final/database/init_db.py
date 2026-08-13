import sys
import time
from pathlib import Path

import psycopg

# Configure imports so this script can use the Employee App services
base_dir = Path(__file__).parent
sys.path.append(str(base_dir.parent))
sys.path.append(str(base_dir.parent / "employee_app"))

from employee_app.repository.database import resolve_dsn
from employee_app.repository.user_repository import UserRepository
from employee_app.service.authentication_service import AuthenticationService

dsn = resolve_dsn()


def wait_for_postgres(timeout_seconds=60):
    """Blocks until the server accepts connections.

    Compose's service_healthy condition already gates this, but `docker compose
    run` is also invoked by hand and on the deploy host, where nothing else
    guarantees ordering.
    """
    deadline = time.monotonic() + timeout_seconds

    while True:
        try:
            with psycopg.connect(dsn, connect_timeout=3):
                return
        except psycopg.OperationalError:
            if time.monotonic() >= deadline:
                raise
            time.sleep(1)


def run_sql_file(conn, filename):
    sql_file = base_dir / filename

    if not sql_file.exists():
        raise FileNotFoundError(f"{filename} was not found at {sql_file}")

    # psycopg runs a multi-statement script in one execute, as long as it
    # carries no parameters.
    conn.execute(sql_file.read_text())
    print(f"Executed {filename}")


wait_for_postgres()

# Step 1: Rebuild the database schema
# schema.sql drops the tables first, so this is the clean slate the old
# script got by deleting the SQLite file.
with psycopg.connect(dsn) as conn:
    run_sql_file(conn, "schema.sql")
    conn.commit()


# Step 2: Seed users through the application layer
# This ensures passwords are hashed before being stored.
user_repo = UserRepository(dsn)
auth_service = AuthenticationService(
    user_repository=user_repo,
    jwt_secret="seed-only-unused-secrety",
    token_expiration_hours=24,
)

auth_service.register_user("brian", "password", "employee")
auth_service.register_user("landon", "password", "employee")
auth_service.register_user("siri", "password", "manager")

print("Secure hashed users seeded successfully.")


# Step 3: Seed expenses and approvals
with psycopg.connect(dsn) as conn:
    run_sql_file(conn, "seed.sql")
    conn.commit()

print("Database initialized successfully.")
