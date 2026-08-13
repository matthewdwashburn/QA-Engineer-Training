# 3 Authentication controller tests: 2 positive and 1 Negative
from datetime import timedelta

import allure
import pytest

from repository.user_model import User


pytestmark = [
    pytest.mark.unit,
]


@allure.epic("Employee Portal Backend")
@allure.feature("Authentication")
@allure.parent_suite("Employee - Controller Layer")
@allure.suite("Authentication Controller")
@allure.issue("KAN-16", "KAN-16")
@allure.story("Employee Login and Authentication")
class TestAuthController:

    
    @allure.title("Login succeeds for valid employee credentials")
    @allure.severity(allure.severity_level.BLOCKER)
    @pytest.mark.smoke
    def test_login_succeeds_for_valid_employee(self, client, app, mocker):
    # Arrange: Mock the authentication service and set up expected return values
        auth_service = mocker.Mock()
        auth_service.login.return_value = User(id=1, username="brian", password="hashed", role="Employee")
        auth_service.generate_jwt_token.return_value = "test-jwt-token"
        auth_service.token_expiry = timedelta(hours=24)
        app.auth_service = auth_service

    # Act: Hit the login endpoint with successful credentials
        response = client.post(
            "/auth/login",
            json={"username": "brian", "password": "password"},
        )

    # Assert: Verify success status code, payload contains expected values, and header initialized jwt
        assert response.status_code == 200
        payload = response.get_json()
        assert payload["message"] == "Welcome back, brian!"
        assert payload["user"]["username"] == "brian"
        assert "jwt_token=test-jwt-token" in response.headers.get("Set-Cookie", "")


    @allure.title("Login rejects invalid credentials")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.negative
    def test_login_rejects_bad_credentials(self, client, app, mocker):
    # Arrange: Mock the authentication service and set up expected return values
        auth_service = mocker.Mock()
        auth_service.login.side_effect = ValueError("Invalid username or password.")
        auth_service.token_expiry = timedelta(hours=24)
        app.auth_service = auth_service

    # Act: Hit the login endpoint with bad credentials
        response = client.post(
            "/auth/login",
            json={"username": "brian", "password": "wrong"},
        )

    # Assert: Verify unauthorized status code and proper return message
        assert response.status_code == 401
        assert response.get_json()["error"] == "Invalid username or password."


    @allure.title("Login hides unexpected internal error details")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.negative
    def test_login_returns_generic_server_error_when_service_fails(self, client, app, mocker):
        auth_service = mocker.Mock()
        auth_service.login.side_effect = RuntimeError("UNIQUE constraint failed: users.username")
        app.auth_service = auth_service

        response = client.post(
            "/auth/login",
            json={"username": "brian", "password": "password"},
        )

        assert response.status_code == 500
        assert response.get_json() == {"error": "Login failed"}


    @allure.title("Logout clears JWT cookie")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.security
    def test_logout_clears_jwt_cookie(self, client):
    # Arrange: (None required for this test)

    # Act: Hit the logout endpoint
        response = client.post("/auth/logout")

    # Assert: Verify success status code, return message, and header drops jwt
        assert response.status_code == 200
        assert response.get_json()["message"] == "Logged out successfully"
        assert "jwt_token=;" in response.headers.get("Set-Cookie", "")


    @allure.title("Login blocks manager account access")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.security
    def test_login_rejects_manager_account(self, client, app, mocker):
    # Arrange: Mock authentication service rejecting managers
        auth_service = mocker.Mock()

        auth_service.login.side_effect = PermissionError(
            "Access Denied: Managers must manage tasks and authenticate "
            "exclusively through the corporate Job Application portal."
        )

        app.auth_service = auth_service

    # Act
        response = client.post(
            "/auth/login",
            json={
                "username": "manager",
                "password": "password"
            },
        )

    # Assert
        assert response.status_code == 403

        payload = response.get_json()

        assert payload["error"] == (
            "Access Denied: Managers must manage tasks and authenticate "
            "exclusively through the corporate Job Application portal."
        )

        auth_service.generate_jwt_token.assert_not_called()
