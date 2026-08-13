"""HTTP contract tests for POST /auth/login and POST /auth/logout.

The employee-side counterpart to LoginIT. `/auth/login` has four documented
exits — 200, 400, 401, 403 — and before this file only the 200 had ever been
driven over real HTTP.

This suite is marked `contract`, so it runs separately from fast feedback.
KAN-85 regression coverage is retained here; the defect record lives in BUGS.md.
"""

import allure
import pytest
import requests

pytestmark = [
    pytest.mark.api,
    pytest.mark.contract,
]

INVALID_BODY_MESSAGE = "Invalid login request body."
MISSING_FIELDS_MESSAGE = "Username and password are required."
INVALID_CREDENTIALS_MESSAGE = "Invalid username or password."


@allure.epic("Employee Portal Backend")
@allure.feature("Authentication")
@allure.parent_suite("Employee - API Layer")
@allure.suite("Employee Login Contract")
@allure.issue("KAN-16", "KAN-16")
@allure.story("Employee Login and Authentication")
class TestEmployeeLoginContract:

    @allure.title("Valid employee credentials return 200 with the user payload")
    @allure.severity(allure.severity_level.BLOCKER)
    @pytest.mark.smoke
    def test_valid_credentials_return_the_user_payload(self, live_server):
        response = requests.post(
            f"{live_server}/auth/login",
            json={"username": "brian", "password": "password"},
            timeout=5,
        )

        assert response.status_code == 200
        payload = response.json()
        assert payload["user"]["username"] == "brian"
        assert payload["user"]["role"] == "Employee"
        assert "brian" in payload["message"]

    @allure.title("The session cookie is issued HttpOnly and carries no credentials")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.security
    def test_login_sets_an_httponly_cookie(self, live_server):
        response = requests.post(
            f"{live_server}/auth/login",
            json={"username": "brian", "password": "password"},
            timeout=5,
        )

        assert response.status_code == 200
        set_cookie = response.headers["Set-Cookie"]
        # HttpOnly is what keeps the token out of reach of page scripts. It is a
        # property of the header, not of the cookie jar, so it has to be read here.
        assert "HttpOnly" in set_cookie
        assert "jwt_token=" in set_cookie
        assert "password" not in response.text

    @allure.title("Invalid credentials are rejected with a generic 401")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.negative
    @pytest.mark.security
    @pytest.mark.parametrize(
        "body",
        [
            pytest.param({"username": "nobody", "password": "password"}, id="unknown-username"),
            pytest.param({"username": "brian", "password": "not-the-password"}, id="wrong-password"),
        ],
    )
    def test_invalid_credentials_are_rejected(self, live_server, body):
        response = requests.post(
            f"{live_server}/auth/login",
            json=body,
            timeout=5,
        )

        assert response.status_code == 401
        # The message must not distinguish "no such user" from "wrong password",
        # or it becomes a username oracle for anyone probing the endpoint.
        assert response.json()["error"] == INVALID_CREDENTIALS_MESSAGE

    @allure.title("A manager account is blocked from the employee portal with 403")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.negative
    @pytest.mark.security
    def test_manager_account_is_blocked(self, live_server):
        # The app's signature business rule: managers authenticate through the
        # manager portal only. It had no HTTP coverage before this test, and the
        # seeded manager needed a real bcrypt hash before the branch was even
        # reachable — see the note in the live_server fixture.
        response = requests.post(
            f"{live_server}/auth/login",
            json={"username": "siri", "password": "password"},
            timeout=5,
        )

        assert response.status_code == 403
        assert "Access Denied" in response.json()["error"]

    @allure.title("Missing or blank credential fields are rejected with 400")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.negative
    @pytest.mark.parametrize(
        "body",
        [
            pytest.param({"password": "password"}, id="no-username"),
            pytest.param({"username": "brian", "password": ""}, id="blank-password"),
        ],
    )
    def test_incomplete_credentials_are_rejected(self, live_server, body):
        response = requests.post(f"{live_server}/auth/login", json=body, timeout=5)

        assert response.status_code == 400
        assert response.json()["error"] == MISSING_FIELDS_MESSAGE

    @allure.title("Malformed or non-object JSON returns 400, not 500")
    @allure.severity(allure.severity_level.CRITICAL)
    @allure.issue("KAN-91", "KAN-91")
    @allure.issue("KAN-85", "Malformed login bodies are client errors")
    @pytest.mark.negative
    @pytest.mark.regression
    @pytest.mark.parametrize(
        "body",
        [
            pytest.param("", id="empty-body"),
            pytest.param('{"username": "brian"', id="truncated-object"),
            pytest.param("[1, 2, 3]", id="valid-json-but-an-array"),
        ],
    )
    def test_malformed_request_body_returns_400(self, live_server, body):
        response = requests.post(
            f"{live_server}/auth/login",
            data=body,
            headers={"Content-Type": "application/json"},
            timeout=5,
        )

        assert response.status_code == 400
        assert response.json()["error"] == INVALID_BODY_MESSAGE

    @allure.title("Logout clears the cookie and revokes access to protected routes")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.security
    def test_logout_revokes_access(self, live_server, employee_session):
        before = employee_session.get(f"{live_server}/expenses/pending", timeout=5)
        assert before.status_code == 200

        logout = employee_session.post(f"{live_server}/auth/logout", timeout=5)
        assert logout.status_code == 200

        # The real assertion is not that the cookie jar looks empty but that the
        # server stops serving protected data to this session.
        after = employee_session.get(f"{live_server}/expenses/pending", timeout=5)
        assert after.status_code == 401
