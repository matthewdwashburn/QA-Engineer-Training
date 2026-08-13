import sqlite3
import sys
import threading
from datetime import datetime, timedelta, timezone
from pathlib import Path

import bcrypt
import jwt
import pytest
import requests
from repository.expense_repository import ExpenseRepository
from repository.user_repository import UserRepository
from service.authentication_service import AuthenticationService
from service.expense_service import ExpenseService
from werkzeug.serving import make_server


ROOT_DIR = Path(__file__).resolve().parents[2]
EMPLOYEE_APP_DIR = ROOT_DIR / "employee_app"

if str(EMPLOYEE_APP_DIR) not in sys.path:
    sys.path.insert(0, str(EMPLOYEE_APP_DIR))

from main import app as flask_app

SCHEMA_PATH = ROOT_DIR / "database" / "schema.sql"

# Must match AuthenticationService.generate_jwt_token, or verify_jwt_token
# rejects the token on the issuer claim before any test assertion is reached.
JWT_ISSUER = "revature-expense-manager-employee-app"

# Cost-12 hash of "password", computed once per session rather than once per test.
# Mirrors AbstractApiIT.HASHED_PASSWORD on the manager side; live_server is pulled
# in by every API test, so hashing inside the fixture repeated the work 28 times.
HASHED_PASSWORD = bcrypt.hashpw(b"password", bcrypt.gensalt()).decode("utf-8")


@pytest.fixture()
def app():
    flask_app.config["TESTING"] = True
    return flask_app


@pytest.fixture()
def client(app):
    return app.test_client()


@pytest.fixture()
def temp_db_path(tmp_path):
    db_path = tmp_path / "expense_manager_test.db"

    conn = sqlite3.connect(db_path)
    try:
        conn.executescript(SCHEMA_PATH.read_text())
        conn.execute(
            "INSERT INTO users (id, username, password, role) VALUES (?, ?, ?, ?)",
            (1, "alice", "alicepass", "Employee"),
        )
        conn.execute(
            "INSERT INTO users (id, username, password, role) VALUES (?, ?, ?, ?)",
            (2, "bob", "bobpass", "Employee"),
        )
        conn.execute(
            "INSERT INTO users (id, username, password, role) VALUES (?, ?, ?, ?)",
            (3, "siri", "managerpass", "Manager"),
        )
        conn.execute(
            "INSERT INTO expenses (id, userId, amount, description, category, date) VALUES (?, ?, ?, ?, ?, ?)",
            (1, 1, 100.0, "Old pending meal", "MEALS", "2026-07-01"),
        )
        conn.execute(
            "INSERT INTO expenses (id, userId, amount, description, category, date) VALUES (?, ?, ?, ?, ?, ?)",
            (2, 1, 200.5, "New approved trip", "TRAVEL", "2026-07-03"),
        )
        conn.execute(
            "INSERT INTO expenses (id, userId, amount, description, category, date) VALUES (?, ?, ?, ?, ?, ?)",
            (3, 2, 50.0, "Office supplies", "OFFICE_SUPPLIES", "2026-07-02"),
        )
        conn.execute(
            "INSERT INTO approvals (id, expenseId, status, reviewer, comment, review_date) VALUES (?, ?, ?, ?, ?, ?)",
            (1, 1, "pending", None, None, None),
        )
        conn.execute(
            "INSERT INTO approvals (id, expenseId, status, reviewer, comment, review_date) VALUES (?, ?, ?, ?, ?, ?)",
            (2, 2, "approved", 3, "Looks good", "2026-07-04"),
        )
        conn.execute(
            "INSERT INTO approvals (id, expenseId, status, reviewer, comment, review_date) VALUES (?, ?, ?, ?, ?, ?)",
            (3, 3, "denied", 3, "Missing receipt", "2026-07-03"),
        )
        conn.commit()
    finally:
        conn.close()

    return str(db_path)


@pytest.fixture()
def live_server(app, temp_db_path):
    conn = sqlite3.connect(temp_db_path)
    try:
        conn.execute(
            "INSERT INTO users (username, password, role) VALUES (?, ?, ?)",
            ("brian", HASHED_PASSWORD, "Employee"),
        )
        # siri is seeded with a plaintext password for the repository tests, which
        # assert on stored values directly. Over HTTP that makes the manager-block
        # rule unreachable: bcrypt.checkpw raises on the malformed hash and login
        # answers 401 before the role check at authentication_service.py:89 runs.
        # Hashing siri here — and only here — lets the 403 branch be tested without
        # disturbing test_user_repository's assertions on temp_db_path.
        conn.execute(
            "UPDATE users SET password = ? WHERE username = ?",
            (HASHED_PASSWORD, "siri"),
        )
        conn.commit()
    finally:
        conn.close()

    app.auth_service = AuthenticationService(
        user_repository=UserRepository(temp_db_path),
        jwt_secret=app.auth_service.jwt_secret,
        token_expiration_hours=int(app.auth_service.token_expiry.total_seconds() / 3600),
    )
    app.expense_service = ExpenseService(expense_repository=ExpenseRepository(temp_db_path))

    server = make_server("127.0.0.1", 5001, app)
    server_thread = threading.Thread(target=server.serve_forever, daemon=True)
    server_thread.start()

    try:
        yield "http://127.0.0.1:5001"
    finally:
        server.shutdown()
        server_thread.join(timeout=2)


@pytest.fixture()
def mint_token(app):
    """Mints production-compatible employee JWTs, including deliberately bad ones.

    The manager app solves the same problem in AbstractApiIT via
    validEmployeeToken() / expiredManagerToken(). The auth-contract tests need
    tokens /auth/login will never issue — expired, signed with the wrong secret,
    or pointing at a manager row — so the middleware's rejection branches can be
    driven over real HTTP rather than asserted through a mocked parse_token.

    Note that require_employee_auth resolves the role from the *database* row
    that user_id refers to, not from the role claim. Pass a user_id to choose
    which account the token authenticates as; the claim is cosmetic.
    """

    def _mint(user_id, *, username="brian", role="Employee", expires_in_hours=1, secret=None):
        now = datetime.now(timezone.utc)
        payload = {
            "user_id": user_id,
            "username": username,
            "role": role,
            # Backdated so a negative expires_in_hours still yields iat < exp.
            "iat": now - timedelta(hours=2),
            "exp": now + timedelta(hours=expires_in_hours),
            "iss": JWT_ISSUER,
        }
        return jwt.encode(payload, secret or app.auth_service.jwt_secret, algorithm="HS256")

    return _mint


@pytest.fixture()
def employee_session(live_server):
    """A requests.Session already holding a valid employee jwt_token cookie."""
    session = requests.Session()
    response = session.post(
        f"{live_server}/auth/login",
        json={"username": "brian", "password": "password"},
        timeout=5,
    )
    assert response.status_code == 200, "employee_session fixture could not authenticate"
    return session

