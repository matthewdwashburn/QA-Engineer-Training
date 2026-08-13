"""HTTP contract tests for the /expenses routes.

The employee-side counterpart to ExpenseIT. The existing journey tests prove
the happy path end to end; this file covers what happens when the request is
wrong — bad values, someone else's expense, an expense that is no longer
pending — and asserts that a rejected write leaves no trace.

The ownership rules matter most here. `update_pending_expense` and
`delete_pending_expense` enforce them at the service layer, and until now that
enforcement was only ever asserted against a mocked repository. These tests
drive it over real HTTP against a real database, which is the layer at which an
IDOR would actually be exploitable.
"""

import sqlite3

import allure
import pytest
import requests

pytestmark = [
    pytest.mark.api,
    pytest.mark.contract,
]

# Seeded rows from the temp_db_path fixture. brian is created separately by
# live_server, so none of these belong to the authenticated session.
ALICE_PENDING_EXPENSE_ID = 1
ALICE_APPROVED_EXPENSE_ID = 2
BOB_DENIED_EXPENSE_ID = 3
UNKNOWN_EXPENSE_ID = 999_999

VALID_EXPENSE = {
    "amount": "25.00",
    "description": "Team lunch",
    "category": "MEALS",
    "expense_date": "2026-07-10",
}


def pending_ids(session, base_url):
    """Returns the expense ids currently visible in the caller's pending list."""
    response = session.get(f"{base_url}/expenses/pending", timeout=5)
    assert response.status_code == 200
    return [row["expense_id"] for row in response.json()["pending_expenses"]]


@allure.epic("Employee Portal Backend")
@allure.feature("Expense Management")
@allure.parent_suite("Employee - API Layer")
@allure.suite("Employee Expense Contract")
@allure.issue("KAN-17", "KAN-17")
@allure.story("Submit New Expense")
class TestExpenseSubmissionContract:

    @allure.title("Invalid submission values are rejected with 400 and a reason")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.negative
    @pytest.mark.parametrize(
        "field, value, expected_reason",
        [
            pytest.param("amount", "abc", "valid number", id="non-numeric-amount"),
            pytest.param("description", "   ", "description is required", id="blank-description"),
            pytest.param("category", "SNACKS", "Invalid category", id="unknown-category"),
            pytest.param("expense_date", "2026-02-30", "YYYY-MM-DD", id="impossible-date"),
        ],
    )
    def test_invalid_values_are_rejected(
        self, live_server, employee_session, field, value, expected_reason
    ):
        body = {**VALID_EXPENSE, field: value}

        response = employee_session.post(
            f"{live_server}/expenses/submit", json=body, timeout=5
        )

        assert response.status_code == 400
        assert expected_reason in response.json()["error"]

    @allure.title("A rejected submission creates no expense record")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.negative
    def test_rejected_submission_creates_no_record(self, live_server, employee_session):
        # A 400 status is not by itself proof that nothing was written. The
        # user-visible promise is that a failed submission leaves the ledger alone.
        before = pending_ids(employee_session, live_server)

        rejected = employee_session.post(
            f"{live_server}/expenses/submit",
            json={**VALID_EXPENSE, "amount": "-5.00"},
            timeout=5,
        )
        assert rejected.status_code == 400

        assert pending_ids(employee_session, live_server) == before

    @allure.title("Malformed expense request bodies are client errors, not server errors")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.negative
    @pytest.mark.regression
    @pytest.mark.parametrize(
        "method, path",
        [
            pytest.param("post", "/expenses/submit", id="submit"),
            pytest.param("put", "/expenses/1", id="update"),
        ],
    )
    def test_malformed_request_body_is_a_client_error(
        self, live_server, employee_session, method, path
    ):
        response = getattr(employee_session, method)(
            f"{live_server}{path}",
            data='{"amount": "25.00"',
            headers={"Content-Type": "application/json"},
            timeout=5,
        )

        assert response.status_code == 400, "a body the server cannot read is the caller's error"


@allure.epic("Employee Portal Backend")
@allure.feature("Expense Management")
@allure.parent_suite("Employee - API Layer")
@allure.suite("Employee Expense Contract")
@allure.issue("KAN-19", "KAN-19")
@allure.story("Modify and Delete Pending Expenses")
class TestExpenseOwnershipContract:

    @allure.title("A cross-user edit is rejected and leaves the expense unchanged")
    @allure.severity(allure.severity_level.BLOCKER)
    @pytest.mark.negative
    @pytest.mark.security
    def test_cross_user_edit_is_rejected_without_persistence(
        self, live_server, employee_session, temp_db_path
    ):
        # The status code alone would not catch a handler that rejected the
        # request after already writing. Read the row back through the database.
        def read_expense():
            conn = sqlite3.connect(temp_db_path)
            try:
                return conn.execute(
                    "SELECT amount, description FROM expenses WHERE id = ?",
                    (ALICE_PENDING_EXPENSE_ID,),
                ).fetchone()
            finally:
                conn.close()

        before = read_expense()

        response = employee_session.put(
            f"{live_server}/expenses/{ALICE_PENDING_EXPENSE_ID}",
            json={"amount": "1.00", "description": "Hijacked"},
            timeout=5,
        )

        assert response.status_code == 400
        assert "only edit your own" in response.json()["error"]
        assert read_expense() == before

    @allure.title("An employee cannot delete an expense belonging to someone else")
    @allure.severity(allure.severity_level.BLOCKER)
    @pytest.mark.negative
    @pytest.mark.security
    def test_cannot_delete_another_users_expense(self, live_server, employee_session):
        response = employee_session.delete(
            f"{live_server}/expenses/{ALICE_PENDING_EXPENSE_ID}", timeout=5
        )

        assert response.status_code == 400
        assert "only delete your own" in response.json()["error"]

    @allure.title("An expense that has already been reviewed can no longer be edited")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.negative
    def test_cannot_edit_a_reviewed_expense(
        self, live_server, employee_session, temp_db_path
    ):
        # The employee app cannot approve anything, so the reviewed state has to
        # be arranged directly. Submitting first keeps the row owned by brian, so
        # this test fails on the pending rule rather than the ownership rule.
        submitted = employee_session.post(
            f"{live_server}/expenses/submit", json=VALID_EXPENSE, timeout=5
        )
        assert submitted.status_code == 201
        expense_id = submitted.json()["expense_id"]

        conn = sqlite3.connect(temp_db_path)
        try:
            conn.execute(
                "UPDATE approvals SET status = 'approved' WHERE expenseId = ?",
                (expense_id,),
            )
            conn.commit()
        finally:
            conn.close()

        response = employee_session.put(
            f"{live_server}/expenses/{expense_id}",
            json={"amount": "99.00", "description": "Too late"},
            timeout=5,
        )

        assert response.status_code == 400
        assert "Only pending expenses" in response.json()["error"]

    @allure.title("Editing or deleting an unknown expense returns 400, not 500")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.negative
    @pytest.mark.edge_case
    @pytest.mark.parametrize("method", ["put", "delete"])
    def test_unknown_expense_is_a_client_error(self, live_server, employee_session, method):
        response = getattr(employee_session, method)(
            f"{live_server}/expenses/{UNKNOWN_EXPENSE_ID}",
            json={"amount": "1.00", "description": "Ghost"},
            timeout=5,
        )

        assert response.status_code == 400
        assert "not found" in response.json()["error"].lower()


@allure.epic("Employee Portal Backend")
@allure.feature("Expense Management")
@allure.parent_suite("Employee - API Layer")
@allure.suite("Employee Expense Contract")
@allure.issue("KAN-18", "KAN-18")
@allure.story("View Expense Ledger")
class TestExpenseScopingContract:

    @allure.title("The ledger shows only the authenticated employee's expenses")
    @allure.severity(allure.severity_level.BLOCKER)
    @pytest.mark.security
    def test_ledger_is_scoped_to_the_authenticated_user(self, live_server, employee_session):
        # Three expenses belonging to alice and bob are seeded. None of them may
        # appear for brian, in either section of the ledger.
        response = employee_session.get(f"{live_server}/expenses/ledger", timeout=5)

        assert response.status_code == 200
        payload = response.json()
        visible = {
            row["expense_id"]
            for row in payload["pending_expenses"] + payload["expense_history"]
        }

        assert visible.isdisjoint(
            {ALICE_PENDING_EXPENSE_ID, ALICE_APPROVED_EXPENSE_ID, BOB_DENIED_EXPENSE_ID}
        )

    @allure.title("The pending list shows only the authenticated employee's expenses")
    @allure.severity(allure.severity_level.BLOCKER)
    @pytest.mark.security
    def test_pending_list_is_scoped_to_the_authenticated_user(
        self, live_server, employee_session
    ):
        submitted = employee_session.post(
            f"{live_server}/expenses/submit", json=VALID_EXPENSE, timeout=5
        )
        assert submitted.status_code == 201

        visible = pending_ids(employee_session, live_server)

        assert submitted.json()["expense_id"] in visible
        assert ALICE_PENDING_EXPENSE_ID not in visible

    @allure.title("Reviewed expenses appear in history rather than pending")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.edge_case
    def test_reviewed_expense_moves_out_of_pending(
        self, live_server, employee_session, temp_db_path
    ):
        submitted = employee_session.post(
            f"{live_server}/expenses/submit", json=VALID_EXPENSE, timeout=5
        )
        expense_id = submitted.json()["expense_id"]
        assert expense_id in pending_ids(employee_session, live_server)

        conn = sqlite3.connect(temp_db_path)
        try:
            conn.execute(
                "UPDATE approvals SET status = 'approved', comment = ? WHERE expenseId = ?",
                ("Approved for payment", expense_id),
            )
            conn.commit()
        finally:
            conn.close()

        ledger = employee_session.get(f"{live_server}/expenses/ledger", timeout=5).json()

        assert expense_id not in [row["expense_id"] for row in ledger["pending_expenses"]]
        history = {row["expense_id"]: row for row in ledger["expense_history"]}
        assert expense_id in history
        assert history[expense_id]["manager_comment"] == "Approved for payment"
