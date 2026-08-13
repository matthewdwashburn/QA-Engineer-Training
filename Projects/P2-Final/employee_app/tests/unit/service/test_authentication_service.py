# 5 Authentication service unit tests: 2 Positive and 3 Negative
import bcrypt
from datetime import datetime, timedelta, timezone

import allure
import jwt
import pytest

from repository.user_model import User
from service.authentication_service import AuthenticationService

pytestmark = [
    pytest.mark.unit,
]

TEST_JWT_SECRET = "unit-test-secret-key-at-least-32-chars"
TEST_TOKEN_EXPIRATION_HOURS = 24


def _hashed(password: str) -> str:
    # Helper Function: Hashes the given password using bcrypt and returns the hashed string
    return bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt()).decode("utf-8")


@allure.epic("Employee Portal Backend")
@allure.feature("Authentication")
@allure.parent_suite("Employee - Service Layer")
@allure.suite("Authentication Service")
@allure.issue("KAN-16", "KAN-16")
@allure.story("Employee Login and Authentication")
class TestAuthenticationService:


    @allure.title("Login successfully returns employee for valid credentials")
    @allure.severity(allure.severity_level.BLOCKER)
    @pytest.mark.smoke
    def test_login_returns_employee_on_valid_credentials(self, mocker):
        # Arrange: Mock setup, service instance, and stubbed repo return values
        repo = mocker.Mock()
        service = AuthenticationService(
            repo,
            jwt_secret=TEST_JWT_SECRET,
            token_expiration_hours=TEST_TOKEN_EXPIRATION_HOURS,
        )

        user = User(id=1, username="brian", password=_hashed("password"), role="Employee")
        repo.find_by_username.return_value = user

        # Act: Call the login method with valid credentials
        result = service.login("brian", "password")

        # Assert: Verify that the returned user has the expected username
        assert result.username == "brian"


    @allure.title("Login raises error for unknown username")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.negative
    def test_login_raises_value_error_for_unknown_username(self, mocker):
        # Arrange: Mock setup, service instance, and stubbed repo return values
        repo = mocker.Mock()
        service = AuthenticationService(
            repo,
            jwt_secret=TEST_JWT_SECRET,
            token_expiration_hours=TEST_TOKEN_EXPIRATION_HOURS,
        )
        repo.find_by_username.return_value = None

        # Act & Assert: Attempt to login with an unknown username and expect a ValueError
        with pytest.raises(ValueError, match="Invalid username or password"):
            service.login("missing", "password")


    @allure.title("Authenticate user returns None for invalid or blank credentials")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.negative
    @pytest.mark.parametrize("test_username, test_password, mock_user_exists", [
        ("missing", "password", False),  # Scenario 1: Unknown username
        ("brian", "wrong", True),        # Scenario 2: Valid username, bad password
        ("", "password", False),         # Scenario 3: Blank username
        ("brian", "", True),             # Scenario 4: Valid username, blank password
        ("", "", False),                 # Scenario 5: Both fields blank
    ])
    def test_authenticate_user_returns_none_for_bad_credentials(self, mocker, test_username, test_password, mock_user_exists):
        # Arrange
        repo = mocker.Mock()
        service = AuthenticationService(
            repo,
            jwt_secret=TEST_JWT_SECRET,
            token_expiration_hours=TEST_TOKEN_EXPIRATION_HOURS,
        )

        if mock_user_exists:
            # Mocking a valid user in the database
            user = User(id=1, username="brian", password=_hashed("password"), role="Employee")
            repo.find_by_username.return_value = user
        else:
            # Mocking that the user does not exist
            repo.find_by_username.return_value = None

        # Act
        result = service.authenticate_user(test_username, test_password)

        # Assert
        assert result is None


    @allure.title("Login blocks manager role")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.security
    def test_login_blocks_manager_accounts(self, mocker):
        # Arrange: Mock setup, service instance, and stubbed repo return values
        repo = mocker.Mock()
        service = AuthenticationService(
            repo,
            jwt_secret=TEST_JWT_SECRET,
            token_expiration_hours=TEST_TOKEN_EXPIRATION_HOURS,
        )

        manager = User(id=3, username="siri", password=_hashed("password"), role="Manager")
        repo.find_by_username.return_value = manager

        # Act & Assert: Attempt to login with a manager account and expect a PermissionError
        with pytest.raises(PermissionError, match="Managers"):
            service.login("siri", "password")


    @allure.title("JWT generation and verification round trip succeeds")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.security
    def test_generate_and_verify_jwt_token_round_trip(self, mocker):
        # Arrange: Mock setup, service instance, and stubbed repo return values
        repo = mocker.Mock()
        service = AuthenticationService(
            repo,
            jwt_secret=TEST_JWT_SECRET,
            token_expiration_hours=TEST_TOKEN_EXPIRATION_HOURS,
        )

        user = User(id=1, username="brian", password=_hashed("password"), role="Employee")

        # Act: Generate a JWT token for the user
        token = service.generate_jwt_token(user)
        payload = service.verify_jwt_token(token)

        # Assert: Verify that the payload contains the expected user information
        assert payload is not None
        assert payload["user_id"] == 1
        assert payload["username"] == "brian"


    @allure.title("JWT verification returns none for malformed token")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.security
    def test_verify_jwt_token_returns_none_for_malformed_token(self, mocker):
        repo = mocker.Mock()
        service = AuthenticationService(
            repo,
            jwt_secret=TEST_JWT_SECRET,
            token_expiration_hours=TEST_TOKEN_EXPIRATION_HOURS,
        )

        payload = service.verify_jwt_token("not-a-jwt-token")

        assert payload is None


    @allure.title("JWT verification returns none for expired token")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.security
    def test_verify_jwt_token_returns_none_for_expired_token(self, mocker):
        repo = mocker.Mock()
        service = AuthenticationService(
            repo,
            jwt_secret=TEST_JWT_SECRET,
            token_expiration_hours=TEST_TOKEN_EXPIRATION_HOURS,
        )

        now = datetime.now(timezone.utc)
        expired_payload = {
            "user_id": 1,
            "username": "brian",
            "role": "Employee",
            "exp": now - timedelta(minutes=1),
            "iat": now - timedelta(minutes=2),
            "iss": "revature-expense-manager-employee-app",
        }
        expired_token = jwt.encode(expired_payload, TEST_JWT_SECRET, algorithm="HS256")

        payload = service.verify_jwt_token(expired_token)

        assert payload is None


    @allure.title("Parse token returns none for invalid token")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.security
    def test_parse_token_returns_none_when_token_invalid(self, mocker):
        repo = mocker.Mock()
        service = AuthenticationService(
            repo,
            jwt_secret=TEST_JWT_SECRET,
            token_expiration_hours=TEST_TOKEN_EXPIRATION_HOURS,
        )

        user = service.parse_token("malformed-token")

        assert user is None
