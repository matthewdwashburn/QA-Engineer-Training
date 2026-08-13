# Data-Driven Testing

## Learning Objectives

By the end of this reading you will be able to:

- Explain the **core principle** of data-driven testing: separating test logic from test data.
- Implement a parameterized test using **pytest** and describe equivalent patterns in other frameworks.
- List common **external data sources** and explain when to use each.
- Describe **test data management** concerns: determinism, isolation, privacy, and refresh.
- Combine data-driven testing with **equivalence partitioning and BVA** to select meaningful rows.

---

## Why This Matters

A common testing trap is writing essentially the same test five times with slightly different input values:

```python
def test_login_with_valid_user_1():
    ...
def test_login_with_valid_user_2():
    ...
def test_login_with_valid_user_3():
    ...
```

This is tedious to write, tedious to maintain, and produces test code that is harder to reason about than the application itself. When the test logic changes, every copy must be updated — and inconsistencies creep in.

Data-driven testing solves this by separating **what the test does** (the logic) from **what data it uses** (the inputs and expected outputs). One parameterized test definition can cover dozens of equivalence class representatives and boundary values, making coverage explicit, visible, and maintainable.

---

## The Concept

### The Core Pattern

The data-driven pattern follows this structure:

```
For each row in a dataset:
    Arrange: Set up the system with this row's input data.
    Act: Execute the action being tested.
    Assert: Verify the actual output matches this row's expected output.
```

The dataset is the variable element. The test logic (the Arrange-Act-Assert structure) is stable and reused across every data row.

This separation has two important properties:
1. **Adding a new test case** requires only adding a row to the dataset — no new test code.
2. **Changing the test logic** (e.g., the API endpoint changes) requires editing one place — the test body — and all data variants benefit.

---

### Parameterized Tests in pytest

`pytest.mark.parametrize` is Python's primary mechanism for data-driven tests:

```python
import pytest

# (input_username, input_password, expected_status_code, description)
login_test_data = [
    ("valid@example.com", "ValidP@ss1", 200, "Happy path — valid credentials"),
    ("valid@example.com", "WrongPass!", 401, "Negative — wrong password"),
    ("unregistered@example.com", "ValidP@ss1", 401, "Negative — unregistered user"),
    ("VALID@EXAMPLE.COM", "ValidP@ss1", 200, "Case-insensitive email match"),
    ("", "ValidP@ss1", 400, "Edge — empty email"),
    ("valid@example.com", "", 400, "Edge — empty password"),
    ("valid@example.com", "A" * 129, 400, "Boundary — password over max length (128 chars)"),
]

@pytest.mark.parametrize("username, password, expected_status, description", login_test_data)
def test_login(username, password, expected_status, description):
    response = post("/api/login", {"username": username, "password": password})
    assert response.status_code == expected_status, f"FAILED for: {description}"
```

**What this achieves:**
- 7 test cases defined as data — one test method.
- Every case appears individually in the test report with its description.
- Adding a new boundary case or invalid class representative requires only adding a tuple to `login_test_data`.
- The description field makes failures readable: "FAILED for: Boundary — password over max length."

---

### Parameterization Patterns in Other Frameworks

The data-driven pattern is framework-independent:

| Framework | Parameterization Mechanism | Notes |
|-----------|--------------------------|-------|
| **pytest (Python)** | `@pytest.mark.parametrize` | Native, flexible, supports fixtures |
| **JUnit 5 (Java)** | `@ParameterizedTest` + `@CsvSource`, `@MethodSource` | Standard Java unit testing |
| **TestNG (Java)** | `@DataProvider` | Common in enterprise Java automation |
| **Mocha/Jest (JavaScript)** | `test.each(data)` | Node.js test frameworks |
| **NUnit (.NET)** | `[TestCaseSource]`, `[Values]`, `[Range]` attributes | C# test framework |
| **SpecFlow / Cucumber** | **Scenario Outline + Examples** table | BDD scenario-level parameterization |

**SpecFlow/Cucumber example (BDD-style):**

```gherkin
Scenario Outline: Login validation
  Given I am on the login page
  When I enter "<username>" and "<password>"
  Then the response status should be <expected_status>

  Examples:
    | username                  | password    | expected_status |
    | valid@example.com         | ValidP@ss1  | 200             |
    | valid@example.com         | WrongPass!  | 401             |
    | unregistered@example.com  | ValidP@ss1  | 401             |
    | valid@example.com         |             | 400             |
```

---

### External Data Sources

For large datasets or data that changes independently of test code, external data sources separate data from code entirely:

**CSV files:**
```python
import csv
import pytest

def load_csv_cases(filepath):
    with open(filepath, newline="") as f:
        return list(csv.DictReader(f))

@pytest.mark.parametrize("row", load_csv_cases("test_data/login_cases.csv"))
def test_login_from_csv(row):
    response = post("/api/login", {
        "username": row["username"],
        "password": row["password"]
    })
    assert response.status_code == int(row["expected_status"])
```

**When to use CSV:**
- Large datasets (50+ rows) that a business analyst or product owner manages.
- Data that non-developers need to be able to edit.
- Test cases generated from a decision table or requirement matrix.

**JSON/YAML fixtures:**
```json
[
  {"username": "valid@example.com", "password": "ValidP@ss1", "expected_status": 200},
  {"username": "valid@example.com", "password": "WrongPass!", "expected_status": 401}
]
```

**When to use JSON/YAML:**
- Rich, nested data structures (e.g., nested API request bodies).
- Fixtures that are version-controlled alongside tests.
- Data managed by developers.

**Database fixtures (SQL seeds):**
For integration tests that require specific database states:
```sql
-- test_fixtures/sprint_15_data.sql
INSERT INTO users (id, email, password_hash, account_status)
VALUES 
  (1001, 'active@example.com', '<hash>', 'active'),
  (1002, 'locked@example.com', '<hash>', 'locked'),
  (1003, 'pending@example.com', '<hash>', 'pending_verification');
```

The CI pipeline runs this seed script before integration tests execute, ensuring a known database state.

---

### Test Data Management: The Critical Discipline

Data-driven tests are only as reliable as the data they use. Poor data management is the most common cause of "flaky" tests in CI pipelines.

**1. Determinism**

Tests must produce the same result every time they run with the same input. Shared mutable data (e.g., shared test accounts that other tests modify) breaks determinism.

**Anti-pattern:**
```python
# ANTI-PATTERN: Uses a shared user account that test_admin also modifies
def test_login():
    login(email="shared-user@example.com", password="SharedPass!")
```

**Better:**
```python
# Each test creates its own user, or uses a fixture-isolated user
def test_login(create_test_user):
    user = create_test_user(role="standard")
    login(email=user.email, password=user.password)
```

**2. Isolation**

Each test should set up the state it needs and clean it up afterward. Tests must not depend on other tests having run first or in a specific order.

Techniques:
- **Database transactions with rollback:** Wrap each test in a transaction that rolls back after execution — no data persists to the next test.
- **Test fixtures:** `@pytest.fixture` creates and tears down data for each test.
- **Ephemeral environments:** CI runs tests in fresh, containerized environments spun up for each pipeline run.

**3. Privacy and Compliance**

Real production data should never be used in test environments without PII masking. GDPR, CCPA, and HIPAA have explicit requirements about the environments where personal data may be processed.

Rules:
- Use **synthetic data** (generated with Faker, Factory Boy, or custom scripts) wherever possible.
- When realistic distributions are needed, use **masked/anonymized copies** of production data — masking must be one-way (email → `user_[hash]@example.com`, name → random name from the same locale).
- Never commit **credentials, passwords, or personal data** to a git repository — use secret managers (HashiCorp Vault, AWS Secrets Manager, Azure Key Vault) or CI secret variables.

**4. Refresh Strategy**

Test environments accumulate state over time. Without a refresh strategy:
- Old test data from previous sprints causes false failures ("this user was supposed to be in state X but is now in state Y from last sprint's tests").
- Disk space and database bloat degrade environment performance.
- Known-bad data (from bugs that were fixed) persists and confuses regression runs.

**Refresh approaches:**
- **Nightly reset:** CI pipeline script runs at 2 AM that drops and rebuilds the test database from a seed script.
- **Per-test cleanup:** `@afterEach` / `teardown()` removes created data.
- **Baseline personas:** Fixed, well-known user profiles (`vip_user`, `standard_user`, `locked_user`) are restored to a known state by the reset script. Tests can rely on their existence and initial state.

---

### Combining Data-Driven Testing with EP and BVA

Data-driven testing is most powerful when the **rows are chosen systematically** rather than arbitrarily:

| Row selection strategy | How to apply |
|----------------------|-------------|
| **One representative per equivalence class** | One row for valid input, one for each invalid class |
| **BVA values at each boundary** | Rows for just-below, at, and just-above each boundary |
| **Decision table rules** | One row per rule (column) in the decision table |
| **State transition paths** | One row per transition in the state model |

**Example: Combining EP and BVA for a quantity field (valid: 1–10)**

```python
quantity_cases = [
    # Equivalence class representatives
    (5, 200, "Valid class — interior"),
    (-1, 400, "Invalid — negative"),
    (15, 400, "Invalid — too high"),
    ("abc", 400, "Invalid — non-numeric"),
    # BVA values
    (0, 400, "BVA — just below lower boundary"),
    (1, 200, "BVA — at lower boundary"),
    (2, 200, "BVA — just above lower boundary"),
    (9, 200, "BVA — just below upper boundary"),
    (10, 200, "BVA — at upper boundary"),
    (11, 400, "BVA — just above upper boundary"),
]
```

This 10-row dataset covers both EP representatives and all BVA values for the numeric boundaries — a complete, efficient test set derived from theory, not intuition.

---

## Summary

- Data-driven testing **separates test logic from test data** — one parameterized test covers many cases through different data rows.
- `pytest.mark.parametrize`, `@ParameterizedTest`, `test.each()`, and Scenario Outlines are the primary mechanisms across frameworks.
- External data sources (CSV, JSON, SQL seeds) allow large datasets or business-owned data to live outside test code.
- **Test data management** requires: determinism (no shared mutable state), isolation (per-test setup/teardown), privacy (no real PII), and refresh (regular environment baseline resets).
- **Select data rows using EP, BVA, and decision tables** — not arbitrary values — to maximize defect detection per row added.

---

## Additional Resources

- [pytest parametrize documentation](https://docs.pytest.org/en/stable/how-to/parametrize.html) — Comprehensive examples of parameterized test patterns in Python.
- [JUnit 5 Parameterized Tests](https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests) — Java framework data-driven guide.
- [Cucumber Scenario Outlines](https://cucumber.io/docs/gherkin/reference/#scenario-outline) — BDD-style parameterized scenarios.
- [Faker library (Python)](https://faker.readthedocs.io/) — Synthetic data generation for test fixtures.
- `test-data-organization.md` (Friday) — Broader test data strategy and environment management.
