"""
Demo: Loading Test Data - External Files, JSON, CSV, Fixtures

INSTRUCTOR TALKING POINTS:
1. Test data often comes from external files (JSON, CSV, YAML)
2. Fixtures can load and parse test data
3. Parametrize tests with data from files
4. Use conftest.py to share data loading fixtures
5. pytest-datafiles plugin for file-based test data

RUN THIS WITH:
    pytest demo_test_data_loading.py -v
"""

import pytest
import json
from pathlib import Path
from calculator import Calculator


# ==========================================================
# SECTION 1: JSON Test Data Loading
# ==========================================================

# Sample test data (in real project, this would be in a separate JSON file)
CALCULATION_TEST_DATA = [
    {"operation": "add", "a": 2, "b": 3, "expected": 5},
    {"operation": "add", "a": -1, "b": 1, "expected": 0},
    {"operation": "subtract", "a": 10, "b": 3, "expected": 7},
    {"operation": "multiply", "a": 4, "b": 5, "expected": 20},
    {"operation": "divide", "a": 10, "b": 2, "expected": 5.0},
]


@pytest.fixture
def calculation_test_cases():
    """
    Provide test data from a JSON-like structure.
    
    In real projects, this would load from:
    - tests/data/calculations.json
    - tests/fixtures/test_cases.json
    """
    return CALCULATION_TEST_DATA


def test_calculations_from_data(calculation_test_cases):
    """Run all test cases from the data fixture."""
    calc = Calculator()
    
    for test_case in calculation_test_cases:
        operation = test_case["operation"]
        a = test_case["a"]
        b = test_case["b"]
        expected = test_case["expected"]
        
        method = getattr(calc, operation)
        result = method(a, b)
        
        assert result == expected, f"{operation}({a}, {b}) = {result}, expected {expected}"


# ==========================================================
# SECTION 2: Loading Data from Files
# ==========================================================

@pytest.fixture
def test_data_dir(tmp_path):
    """Create a test data directory with sample files."""
    data_dir = tmp_path / "test_data"
    data_dir.mkdir()
    
    # Create JSON test file
    json_data = {
        "users": [
            {"name": "Alice", "age": 30},
            {"name": "Bob", "age": 25}
        ]
    }
    json_file = data_dir / "users.json"
    json_file.write_text(json.dumps(json_data))
    
    # Create CSV test file
    csv_content = "name,age\nCharlie,35\nDiana,28"
    csv_file = data_dir / "more_users.csv"
    csv_file.write_text(csv_content)
    
    return data_dir


@pytest.fixture
def loaded_json_data(test_data_dir):
    """Load JSON data from test file."""
    json_file = test_data_dir / "users.json"
    with open(json_file) as f:
        return json.load(f)


def test_json_data_loading(loaded_json_data):
    """Test that JSON data loads correctly."""
    assert "users" in loaded_json_data
    assert len(loaded_json_data["users"]) == 2
    assert loaded_json_data["users"][0]["name"] == "Alice"


def test_csv_data_loading(test_data_dir):
    """Load and parse CSV test data."""
    import csv
    
    csv_file = test_data_dir / "more_users.csv"
    
    with open(csv_file) as f:
        reader = csv.DictReader(f)
        users = list(reader)
    
    assert len(users) == 2
    assert users[0]["name"] == "Charlie"
    assert users[1]["age"] == "28"


# ==========================================================
# SECTION 3: Parameterizing Tests with File Data
# ==========================================================

def load_test_params():
    """
    Load parameters from test data.
    
    This function is called during test collection,
    so it needs to work without fixtures.
    """
    return [
        (2, 3, 5),
        (0, 0, 0),
        (-1, 1, 0),
        (100, 200, 300),
    ]


@pytest.mark.parametrize("a,b,expected", load_test_params())
def test_add_from_loaded_params(a, b, expected):
    """Parameterized test with loaded data."""
    calc = Calculator()
    assert calc.add(a, b) == expected


# ==========================================================
# SECTION 4: Factory Fixtures for Test Data
# ==========================================================

@pytest.fixture
def make_user():
    """
    Factory fixture that creates users with defaults.
    
    Allows tests to customize only what they need.
    """
    def _make_user(name="Default User", email=None, age=25, active=True):
        if email is None:
            email = f"{name.lower().replace(' ', '.')}@example.com"
        return {
            "name": name,
            "email": email,
            "age": age,
            "active": active
        }
    return _make_user


def test_default_user(make_user):
    """Use factory with defaults."""
    user = make_user()
    assert user["name"] == "Default User"
    assert user["active"] is True


def test_custom_user(make_user):
    """Use factory with custom values."""
    user = make_user(name="Alice", age=30)
    assert user["name"] == "Alice"
    assert user["age"] == 30
    assert user["email"] == "alice@example.com"


def test_inactive_user(make_user):
    """Use factory to create inactive user."""
    user = make_user(name="Bob", active=False)
    assert user["active"] is False


# ==========================================================
# SECTION 5: Test Data with Multiple Fixtures
# ==========================================================

@pytest.fixture
def user_database(make_user):
    """Fixture that uses the factory to create a mock database."""
    return {
        "users": [
            make_user("Alice", age=30),
            make_user("Bob", age=25),
            make_user("Charlie", age=35),
        ],
        "admins": [
            make_user("Admin", email="admin@example.com")
        ]
    }


def test_user_database_has_users(user_database):
    """Test the mock database fixture."""
    assert len(user_database["users"]) == 3
    assert len(user_database["admins"]) == 1


def test_find_user_by_age(user_database):
    """Use database fixture for complex test."""
    users = user_database["users"]
    young_users = [u for u in users if u["age"] < 30]
    assert len(young_users) == 1
    assert young_users[0]["name"] == "Bob"


# ==========================================================
# SECTION 6: Indirect Parametrization with Fixtures
# ==========================================================

@pytest.fixture
def operation_fixture(request):
    """
    Fixture that returns different operations based on parameter.
    
    Used with indirect parametrization.
    """
    calc = Calculator()
    operation_map = {
        "add": calc.add,
        "subtract": calc.subtract,
        "multiply": calc.multiply,
    }
    return operation_map[request.param]


@pytest.mark.parametrize("operation_fixture,a,b,expected", [
    ("add", 2, 3, 5),
    ("subtract", 10, 3, 7),
    ("multiply", 4, 5, 20),
], indirect=["operation_fixture"])
def test_operations_indirect(operation_fixture, a, b, expected):
    """Test with indirect fixture parametrization."""
    result = operation_fixture(a, b)
    assert result == expected


# ==========================================================
# SECTION 7: Using YAML for Test Data (if PyYAML installed)
# ==========================================================

# Example of YAML test data structure:
YAML_TEST_DATA = """
calculator_tests:
  addition:
    - {a: 1, b: 2, expected: 3}
    - {a: -1, b: 1, expected: 0}
  subtraction:
    - {a: 5, b: 3, expected: 2}
"""


def parse_yaml_data():
    """
    Parse YAML test data.
    
    Requires: pip install pyyaml
    """
    try:
        import yaml
        data = yaml.safe_load(YAML_TEST_DATA)
        return data
    except ImportError:
        return {"calculator_tests": {"addition": [], "subtraction": []}}


def test_yaml_data_structure():
    """Test YAML parsing."""
    data = parse_yaml_data()
    assert "calculator_tests" in data


# ==========================================================
# LIVE CODING CHALLENGE
# ==========================================================

# INSTRUCTOR: Have students:
#
# 1. Create a JSON file with test cases for email validation:
#    [
#      {"email": "valid@example.com", "valid": true},
#      {"email": "invalid", "valid": false},
#      ...
#    ]
#
# 2. Create a fixture that loads this JSON file
#
# 3. Create a parameterized test that validates emails
#
# 4. Use the factory pattern to create test users with emails

