# 6 Expense Controller tests: 5 Positive and 1 Negative (has 4 test negative test cases)
from datetime import timedelta

import allure
import pytest

from repository.expense_model import Expense
from repository.user_model import User


pytestmark = [
    pytest.mark.unit,
]


def _login_employee(client, app, mocker):
    # Helper function: Gives a tokened user and an auth service instance to use RESTAPIs
    auth_service = mocker.Mock()
    auth_service.login.return_value = User(id=1, username="brian", password="hashed", role="Employee")
    auth_service.generate_jwt_token.return_value = "test-jwt-token"
    auth_service.token_expiry = timedelta(hours=24)
    app.auth_service = auth_service

    login_response = client.post(
        "/auth/login",
        json={"username": "brian", "password": "password"},
    )
    assert login_response.status_code == 200

    return auth_service


def _employee_user():
    # Helper Function: Returns specified user from _login_employee for asserting correct user return object
    return User(id=1, username="brian", password="hashed", role="Employee")


@allure.epic("Employee Portal Backend")
@allure.feature("Expense Management")
@allure.parent_suite("Employee - Controller Layer")
@allure.suite("Expense Controller")
class TestExpenseController:

    @allure.issue("KAN-16", "KAN-16")
    @allure.story("Employee Login and Authentication")
    @allure.title("Protected expense routes enforce the employee authentication contract")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.security
    @pytest.mark.parametrize(
        "token, resolved_user, expected_status, expected_error",
        [
            (None, None, 401, "Authentication token is missing"),
            ("invalid-token", None, 401, "Invalid or expired authentication token"),
            (
                "manager-token",
                User(id=3, username="siri", password="hashed", role="Manager"),
                403,
                "Access forbidden: Employee role required",
            ),
        ],
    )
    def test_protected_expense_route_rejects_invalid_authentication(
        self,
        client,
        app,
        mocker,
        token,
        resolved_user,
        expected_status,
        expected_error,
    ):
        auth_service = mocker.Mock()
        auth_service.parse_token.return_value = resolved_user
        app.auth_service = auth_service

        if token:
            client.set_cookie("jwt_token", token)

        response = client.get("/expenses/pending")

        assert response.status_code == expected_status
        assert response.get_json() == {"error": expected_error}

        if token:
            auth_service.parse_token.assert_called_once_with(token)
        else:
            auth_service.parse_token.assert_not_called()

    @allure.issue("KAN-17", "KAN-17")
    @allure.story("Submit New Expense")
    @allure.title("Submit expense succeeds for authenticated employee")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_submit_expense_succeeds_for_logged_in_employee(self, client, app, mocker):
    # Arrange: Login user, mock setup, initialize services, and expected return values
        _login_employee(client, app, mocker)

        expense_service = mocker.Mock()
        expense_service.create_expense.return_value = Expense(
            id=10,
            user_id=1,
            amount=22.0,
            description="Lunch",
            category="OTHER",
            date="2026-04-01",
        )
        expense_service.format_currency_amount.return_value = "22.00"
        app.expense_service = expense_service
        app.auth_service.parse_token.return_value = _employee_user()

    # Act: Target the submit endpoint with expense json
        response = client.post(
            "/expenses/submit",
            json={
                "amount": "22",
                "description": "Lunch",
                "category": "OTHER",
                "expense_date": "2026-04-01",
            },
        )

    # Assert: Verify success status code and return payload values
        assert response.status_code == 201
        payload = response.get_json()
        assert payload["expense_id"] == 10
        assert payload["amount"] == "22.00"


    @allure.issue("KAN-17", "KAN-17")
    @allure.story("Submit New Expense")
    @allure.title("Submission hides unexpected internal error details")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.negative
    def test_submit_expense_returns_generic_server_error_when_service_fails(self, client, app, mocker):
        _login_employee(client, app, mocker)
        expense_service = mocker.Mock()
        expense_service.create_expense.side_effect = RuntimeError(
            "NOT NULL constraint failed: expenses.amount"
        )
        app.expense_service = expense_service
        app.auth_service.parse_token.return_value = _employee_user()

        response = client.post(
            "/expenses/submit",
            json={
                "amount": "22.00",
                "description": "Lunch",
                "category": "MEALS",
                "expense_date": "2026-04-01",
            },
        )

        assert response.status_code == 500
        payload = response.get_json()
        assert payload["error"] == "Expense submission failed due to a server issue. Please try again."
        assert "details" not in payload
        assert "constraint" not in str(payload).lower()


    @allure.issue("KAN-18", "KAN-18")
    @allure.story("View Expense Ledger")
    @allure.title("Ledger endpoint returns pending and history lists")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_ledger_returns_pending_and_history_lists(self, client, app, mocker):
    # Arrange: Login user, mock setup, initialize services, and expected return values
        _login_employee(client, app, mocker)

        expense_service = mocker.Mock()
        expense_service.get_user_ledger.return_value = {
            "pending_expenses": [
                {"expense_id": 1, "amount": "22.00", "status": "Pending"}
            ],
            "expense_history": [
                {"expense_id": 2, "amount": "10.00", "status": "Approved"}
            ],
        }
        app.expense_service = expense_service
        app.auth_service.parse_token.return_value = _employee_user()

    # Act: Hit ledger endpoint
        response = client.get("/expenses/ledger")

    # Assert: Verify success status code, payload contains pending and history, and correct payload length
        assert response.status_code == 200
        payload = response.get_json()
        assert "pending_expenses" in payload
        assert "expense_history" in payload
        assert len(payload["pending_expenses"]) == 1
        assert len(payload["expense_history"]) == 1


    @allure.issue("KAN-18", "KAN-18")
    @allure.story("View Expense Ledger")
    @allure.title("Pending endpoint returns only pending items")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_pending_returns_only_pending_items(self, client, app, mocker):
    # Arrange: Login user, mock setup, initialize services, and expected return values
        _login_employee(client, app, mocker)

        expense_service = mocker.Mock()
        expense_service.get_pending_expenses.return_value = [
            {"expense_id": 1, "status": "Pending"},
            {"expense_id": 3, "status": "Pending"},
        ]
        app.expense_service = expense_service
        app.auth_service.parse_token.return_value = _employee_user()

    # Act: Hit pending expenses endpoint
        response = client.get("/expenses/pending")

    # Assert: Verify status success code and payload contains pending expense object with correct data length
        assert response.status_code == 200
        payload = response.get_json()
        assert list(payload.keys()) == ["pending_expenses"]
        assert len(payload["pending_expenses"]) == 2


    @allure.issue("KAN-19", "KAN-19")
    @allure.story("Modify and Delete Pending Expenses")
    @allure.title("Update endpoint allows owned pending expense")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_update_allows_owned_pending_expense(self, client, app, mocker):
    # Arrange: Login user, mock setup, initialize services, and expected return values
        _login_employee(client, app, mocker)

        expense_service = mocker.Mock()
        app.expense_service = expense_service
        app.auth_service.parse_token.return_value = _employee_user()

    # Act: Hit put expense endpoint for updating specified expense
        response = client.put(
            "/expenses/1",
            json={"amount": "24.50", "description": "Updated lunch"},
        )

    # Assert: Verify success status code, return message, and data was sent to mock db
        assert response.status_code == 200
        assert response.get_json()["message"] == "Expense updated successfully."
        expense_service.update_pending_expense.assert_called_once_with(
            user_id=1,
            expense_id=1,
            amount="24.50",
            description="Updated lunch",
        )


    @allure.issue("KAN-19", "KAN-19")
    @allure.story("Modify and Delete Pending Expenses")
    @allure.title("Delete endpoint allows owned pending expense")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_delete_allows_owned_pending_expense(self, client, app, mocker):
    # Arrange: Login user, mock setup, and initialize services  
        _login_employee(client, app, mocker)

        expense_service = mocker.Mock()
        app.expense_service = expense_service
        app.auth_service.parse_token.return_value = _employee_user()

    # Act: Hit expense endpoint with DELETE for ID 1 
        response = client.delete("/expenses/1")

    # Assert: Verify success status code, return message, and data was changed in mock db
        assert response.status_code == 200
        assert response.get_json()["message"] == "Expense deleted successfully."
        expense_service.delete_pending_expense.assert_called_once_with(user_id=1, expense_id=1)


    @allure.issue("KAN-19", "KAN-19")
    @allure.story("Modify and Delete Pending Expenses")
    @allure.title("Update and delete endpoints return 400 for bad requests")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.negative
    @pytest.mark.parametrize("method, endpoint, payload, error_msg", [
        ("PUT", "/expenses/2", {"amount": "24.50", "description": "Updated"}, "You can only edit your own expenses."),
        ("PUT", "/expenses/1", {"amount": "24.50", "description": "Updated"}, "Only pending expenses can be edited."),
        ("DELETE", "/expenses/2", None, "You can only delete your own expenses."),
        ("DELETE", "/expenses/1", None, "Only pending expenses can be deleted."),
    ])
    def test_update_and_delete_reject_invalid_operations(self, client, app, mocker, method, endpoint, payload, error_msg):
    # Arrange
        _login_employee(client, app, mocker)
        expense_service = mocker.Mock()
        app.expense_service = expense_service
        app.auth_service.parse_token.return_value = _employee_user()

    # Configure side effects on service calls based on method
        if method == "PUT":
            expense_service.update_pending_expense.side_effect = ValueError(error_msg)
            response = client.put(endpoint, json=payload)
        else:
            expense_service.delete_pending_expense.side_effect = ValueError(error_msg)
            response = client.delete(endpoint)

    # Assert
        assert response.status_code == 400
        assert response.get_json()["error"] == error_msg
