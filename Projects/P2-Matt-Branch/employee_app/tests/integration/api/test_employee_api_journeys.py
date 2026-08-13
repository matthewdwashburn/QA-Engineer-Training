import allure
import pytest
import requests

pytestmark = [
    pytest.mark.api,
    pytest.mark.smoke,
]


@allure.epic("Employee Portal Backend")
@allure.feature("Expense Management")
@allure.parent_suite("Employee - API Layer")
@allure.suite("Employee API Journeys")
@allure.issue("KAN-16", "KAN-16")
@allure.story("Employee Login and Authentication")
@allure.issue("KAN-17", "KAN-17")
@allure.story("Submit New Expense")
class TestEmployeeApiJourneys:

    @allure.issue("KAN-18", "KAN-18")
    @allure.story("View Expense Ledger")
    @allure.title("Happy path through submission, pending view, and ledger tracking")
    @allure.severity(allure.severity_level.CRITICAL)
    def test_employee_happy_path_submission_and_ledger_tracking(self, live_server):
        session = requests.Session()
    
        with allure.step("Step 1: Login and receive JWT cookie"):
            login_response = session.post(
                f"{live_server}/auth/login",
                json={"username": "brian", "password": "password"},
                timeout=5,
            )
            assert login_response.status_code == 200
            assert session.cookies.get("jwt_token") is not None
    
        with allure.step("Step 2: Submit a valid expense and capture expense_id"):
            submit_response = session.post(
                f"{live_server}/expenses/submit",
                json={
                    "amount": "46.50",
                    "description": "Client Lunch",
                    "category": "MEALS",
                    "expense_date": "2026-07-05",
                },
                timeout=5,
            )
            assert submit_response.status_code == 201
            expense_id = submit_response.json()["expense_id"]
            assert isinstance(expense_id, int)
    
        with allure.step("Step 3: View pending expenses and verify submitted expense is present"):
            pending_response = session.get(f"{live_server}/expenses/pending", timeout=5)
            assert pending_response.status_code == 200
            pending_payload = pending_response.json()
            pending_ids = [row["expense_id"] for row in pending_payload.get("pending_expenses", [])]
            assert expense_id in pending_ids
    
        with allure.step("Step 4: View full ledger and verify contract includes pending and history arrays"):
            ledger_response = session.get(f"{live_server}/expenses/ledger", timeout=5)
            assert ledger_response.status_code == 200
            ledger_payload = ledger_response.json()
            assert "pending_expenses" in ledger_payload
            assert "expense_history" in ledger_payload
            assert isinstance(ledger_payload["pending_expenses"], list)
            assert isinstance(ledger_payload["expense_history"], list)
    
    
    @allure.issue("KAN-19", "KAN-19")
    @allure.story("Modify and Delete Pending Expenses")
    @allure.title("Correction flow through edit and delete pending expense")
    @allure.severity(allure.severity_level.CRITICAL)
    def test_employee_correction_path_edit_then_delete_pending_expense(self, live_server):
        session = requests.Session()
    
        with allure.step("Step 1: Login and persist cookie in requests session"):
            login_response = session.post(
                f"{live_server}/auth/login",
                json={"username": "brian", "password": "password"},
                timeout=5,
            )
            assert login_response.status_code == 200
            assert session.cookies.get("jwt_token") is not None
    
        with allure.step("Step 2: Submit draft expense and capture expense_id"):
            submit_response = session.post(
                f"{live_server}/expenses/submit",
                json={
                    "amount": "100.00",
                    "description": "Wrong Description",
                    "category": "OTHER",
                    "expense_date": "2026-07-05",
                },
                timeout=5,
            )
            assert submit_response.status_code == 201
            expense_id = submit_response.json()["expense_id"]
    
        with allure.step("Step 3: Edit pending expense amount and description"):
            update_response = session.put(
                f"{live_server}/expenses/{expense_id}",
                json={"amount": "50.00", "description": "Corrected Description"},
                timeout=5,
            )
            assert update_response.status_code == 200
    
        with allure.step("Step 4: Verify edited values appear in pending expenses"):
            pending_after_update = session.get(f"{live_server}/expenses/pending", timeout=5)
            assert pending_after_update.status_code == 200
            pending_items = pending_after_update.json().get("pending_expenses", [])
            edited_rows = [row for row in pending_items if row.get("expense_id") == expense_id]
            assert len(edited_rows) == 1
            assert edited_rows[0].get("description") == "Corrected Description"
            assert edited_rows[0].get("amount") == "50.00"
    
        with allure.step("Step 5: Delete pending expense"):
            delete_response = session.delete(f"{live_server}/expenses/{expense_id}", timeout=5)
            assert delete_response.status_code == 200
    
        with allure.step("Step 6: Verify expense id is removed from pending list"):
            pending_after_delete = session.get(f"{live_server}/expenses/pending", timeout=5)
            assert pending_after_delete.status_code == 200
            pending_ids_after_delete = [
                row["expense_id"]
                for row in pending_after_delete.json().get("pending_expenses", [])
            ]
            assert expense_id not in pending_ids_after_delete
