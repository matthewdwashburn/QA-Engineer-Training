"""HTTP contract tests for the shared employee-authentication decorator.

The manager API proves its middleware contract once, against a representative
protected route, in AuthContractIT. This is the employee-side equivalent for
`require_employee_auth`, which guards six of the eight routes on this app.

Every rejection branch here was previously reachable only through unit tests
that mocked `parse_token`. A mock cannot produce an expired signature or a
token minted against the wrong secret, so those branches had never been driven
over real HTTP. The `mint_token` fixture exists to close exactly that gap.

One detail worth knowing when reading these tests: `require_employee_auth`
resolves the role from the *database row* the token's `user_id` points at, not
from the token's own `role` claim. Forging the claim proves nothing; pointing
the token at the seeded manager row is what exercises the 403 branch.
"""

import allure
import pytest
import requests

pytestmark = [
    pytest.mark.api,
    pytest.mark.contract,
    pytest.mark.security,
]

# Seeded ids from the temp_db_dsn fixture, named so the tests read as intent.
ALICE_ID = 1
SIRI_MANAGER_ID = 3

MISSING_TOKEN_MESSAGE = "Authentication token is missing"
INVALID_TOKEN_MESSAGE = "Invalid or expired authentication token"
EMPLOYEE_REQUIRED_MESSAGE = "Access forbidden: Employee role required"

# Every route wearing @require_employee_auth. Kept as data so adding a route
# without its guard fails here rather than reaching production unprotected.
PROTECTED_ROUTES = [
    ("GET", "/auth/me"),
    ("POST", "/expenses/submit"),
    ("GET", "/expenses/ledger"),
    ("GET", "/expenses/pending"),
    ("PUT", "/expenses/1"),
    ("DELETE", "/expenses/1"),
]

REPRESENTATIVE_ROUTE = "/expenses/pending"


@allure.epic("Employee Portal Backend")
@allure.feature("Authentication")
@allure.parent_suite("Employee - API Layer")
@allure.suite("Employee Authentication Contract")
@allure.issue("KAN-16", "KAN-16")
@allure.story("Employee Login and Authentication")
class TestEmployeeAuthContract:

    @allure.title("A request with no jwt_token cookie is rejected with 401")
    @allure.severity(allure.severity_level.BLOCKER)
    @pytest.mark.negative
    def test_missing_cookie_is_rejected(self, live_server):
        response = requests.get(f"{live_server}{REPRESENTATIVE_ROUTE}", timeout=5)

        assert response.status_code == 401
        assert response.json()["error"] == MISSING_TOKEN_MESSAGE

    @allure.title("A structurally invalid token is rejected with 401")
    @allure.severity(allure.severity_level.BLOCKER)
    @pytest.mark.negative
    def test_malformed_token_is_rejected(self, live_server):
        response = requests.get(
            f"{live_server}{REPRESENTATIVE_ROUTE}",
            cookies={"jwt_token": "garbage.token.value"},
            timeout=5,
        )

        assert response.status_code == 401
        assert response.json()["error"] == INVALID_TOKEN_MESSAGE

    @allure.title("A correctly signed but expired token is rejected with 401")
    @allure.severity(allure.severity_level.BLOCKER)
    @pytest.mark.negative
    def test_expired_token_is_rejected(self, live_server, mint_token):
        expired = mint_token(ALICE_ID, username="alice", expires_in_hours=-1)

        response = requests.get(
            f"{live_server}{REPRESENTATIVE_ROUTE}",
            cookies={"jwt_token": expired},
            timeout=5,
        )

        assert response.status_code == 401
        assert response.json()["error"] == INVALID_TOKEN_MESSAGE

    @allure.title("A token signed with the wrong secret is rejected with 401")
    @allure.severity(allure.severity_level.BLOCKER)
    @pytest.mark.negative
    def test_token_signed_with_wrong_secret_is_rejected(self, live_server, mint_token):
        # Proves the signature is actually verified rather than the payload
        # merely being decoded — the difference between authentication and
        # trusting whatever the client sent.
        # 32+ bytes purely to keep PyJWT's short-key warning out of the report.
        forged = mint_token(
            ALICE_ID, username="alice", secret="an-attacker-controlled-secret-value"
        )

        response = requests.get(
            f"{live_server}{REPRESENTATIVE_ROUTE}",
            cookies={"jwt_token": forged},
            timeout=5,
        )

        assert response.status_code == 401
        assert response.json()["error"] == INVALID_TOKEN_MESSAGE

    @allure.title("A valid token for a user who no longer exists is rejected with 401")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.negative
    @pytest.mark.edge_case
    def test_token_for_unknown_user_is_rejected(self, live_server, mint_token):
        # parse_token re-reads the user from the database, so a token that
        # outlives its account must stop working rather than authenticating a
        # ghost from its own claims.
        orphaned = mint_token(999_999, username="deleted-user")

        response = requests.get(
            f"{live_server}{REPRESENTATIVE_ROUTE}",
            cookies={"jwt_token": orphaned},
            timeout=5,
        )

        assert response.status_code == 401
        assert response.json()["error"] == INVALID_TOKEN_MESSAGE

    @allure.title("A valid token belonging to a manager is forbidden with 403")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.negative
    def test_manager_token_is_forbidden(self, live_server, mint_token):
        # The role branch of require_employee_auth. /auth/login refuses to issue
        # a token to a manager at all, so this is the only way to reach it.
        manager_token = mint_token(SIRI_MANAGER_ID, username="siri", role="Manager")

        response = requests.get(
            f"{live_server}{REPRESENTATIVE_ROUTE}",
            cookies={"jwt_token": manager_token},
            timeout=5,
        )

        assert response.status_code == 403
        assert response.json()["error"] == EMPLOYEE_REQUIRED_MESSAGE

    @allure.title("A valid employee token reaches the endpoint handler")
    @allure.severity(allure.severity_level.BLOCKER)
    @pytest.mark.smoke
    def test_valid_employee_token_reaches_the_handler(self, live_server, mint_token):
        # The positive control. Without it the rejections above could all be
        # passing because the route is broken rather than because it is guarded.
        valid = mint_token(ALICE_ID, username="alice")

        response = requests.get(
            f"{live_server}{REPRESENTATIVE_ROUTE}",
            cookies={"jwt_token": valid},
            timeout=5,
        )

        assert response.status_code == 200
        assert "pending_expenses" in response.json()

    @allure.title("Every protected route rejects an unauthenticated caller")
    @allure.severity(allure.severity_level.BLOCKER)
    @pytest.mark.negative
    @pytest.mark.parametrize("method, path", PROTECTED_ROUTES)
    def test_every_protected_route_rejects_missing_cookie(self, live_server, method, path):
        response = requests.request(method, f"{live_server}{path}", json={}, timeout=5)

        assert response.status_code == 401, f"{method} {path} did not require authentication"
        assert response.json()["error"] == MISSING_TOKEN_MESSAGE

    @allure.title("A rejected request returns no expense data in its body")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.negative
    def test_rejected_request_leaks_no_data(self, live_server):
        # Mirrors EmployeesIT.anonymousCallerReceivesNoRosterData: a 401 status
        # is not on its own proof that nothing was disclosed alongside it.
        response = requests.get(f"{live_server}/expenses/ledger", timeout=5)

        assert response.status_code == 401
        payload = response.json()
        assert set(payload) == {"error"}
        assert "pending_expenses" not in payload
        assert "expense_history" not in payload
