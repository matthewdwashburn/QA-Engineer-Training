"""
conftest.py - Shared Fixtures for All Tests in This Directory

1. conftest.py is automatically discovered by pytest
2. Fixtures defined here are available to ALL tests in this directory
3. No imports needed - pytest injects them automatically
4. You can have conftest.py in subdirectories too (nested scopes)
5. Great for database connections, API clients, common test data

This file demonstrates the fixture sharing pattern.
"""

import pytest
from calculator import Calculator, StringCalculator


# ==========================================================
# SHARED FIXTURES - Available to All Tests
# ==========================================================

@pytest.fixture
def shared_calculator():
    """
    Calculator instance available to all tests.
    
    Tests can use this by adding 'shared_calculator' as parameter.
    """
    return Calculator()


@pytest.fixture
def shared_string_calculator():
    """StringCalculator available to all tests."""
    return StringCalculator()


# ==========================================================
# SAMPLE DATA FIXTURES
# ==========================================================

@pytest.fixture
def sample_numbers():
    """Common test numbers."""
    return {
        "positive": [1, 5, 10, 100],
        "negative": [-1, -5, -10, -100],
        "zero": [0],
        "mixed": [-5, -1, 0, 1, 5]
    }


@pytest.fixture
def sample_users():
    """Common test user data."""
    return [
        {"name": "Alice", "email": "alice@example.com", "age": 30},
        {"name": "Bob", "email": "bob@example.com", "age": 25},
        {"name": "Charlie", "email": "charlie@example.com", "age": 35},
    ]


# ==========================================================
# FACTORY FIXTURES
# ==========================================================

@pytest.fixture
def make_test_user():
    """Factory for creating test users."""
    def _make(name="Test User", email=None, age=25):
        if email is None:
            email = f"{name.lower().replace(' ', '.')}@test.com"
        return {"name": name, "email": email, "age": age}
    return _make


# ==========================================================
# SESSION-SCOPED FIXTURES
# ==========================================================

@pytest.fixture(scope="session")
def session_calculator():
    """
    Single calculator for entire test session.
    
    Useful for expensive setup that can be shared.
    """
    print("\n[SESSION] Creating session-scoped calculator")
    calc = Calculator()
    yield calc
    print("\n[SESSION] Cleaning up session-scoped calculator")


# ==========================================================
# MODULE-SCOPED FIXTURES
# ==========================================================

@pytest.fixture(scope="module")
def module_config():
    """Configuration shared within a test module."""
    return {
        "timeout": 30,
        "retry_count": 3,
        "debug": True
    }


# ==========================================================
# AUTOUSE FIXTURES
# ==========================================================

@pytest.fixture(autouse=True)
def test_timing(request):
    """
    Automatically measure test duration.
    
    autouse=True means this runs for every test without
    explicitly requesting it.
    """
    import time
    start = time.time()
    yield
    duration = time.time() - start
    # Uncomment to see timing:
    # print(f"\n[TIMING] {request.node.name}: {duration:.4f}s")


# ==========================================================
# PYTEST HOOKS (Advanced)
# ==========================================================

def pytest_configure(config):
    """
    Called after command line options have been parsed.
    
    Good place to register custom markers.
    """
    config.addinivalue_line(
        "markers", "slow: marks tests as slow (deselect with '-m \"not slow\"')"
    )
    config.addinivalue_line(
        "markers", "integration: marks tests as integration tests"
    )
    config.addinivalue_line(
        "markers", "smoke: marks tests as smoke tests"
    )


def pytest_collection_modifyitems(config, items):
    """
    Called after collection has been performed.
    
    Can modify test items (add markers, skip, etc.)
    """
    # Example: Auto-skip slow tests unless explicitly requested
    # if not config.getoption("--runslow"):
    #     skip_slow = pytest.mark.skip(reason="need --runslow option to run")
    #     for item in items:
    #         if "slow" in item.keywords:
    #             item.add_marker(skip_slow)
    pass


# ==========================================================
# INSTRUCTOR NOTES
# ==========================================================

"""
CONFTEST.PY STRUCTURE:

project/
├── tests/
│   ├── conftest.py           # Root fixtures (available everywhere)
│   ├── unit/
│   │   ├── conftest.py       # Unit test fixtures
│   │   └── test_calc.py
│   └── integration/
│       ├── conftest.py       # Integration test fixtures
│       └── test_api.py

FIXTURE RESOLUTION ORDER:
1. Test file itself
2. conftest.py in test file's directory
3. conftest.py in parent directories
4. Plugins

KEY POINTS:
- No need to import conftest.py - pytest finds it automatically
- Fixtures with same name in child conftest override parent
- Use scope to control fixture lifetime
- Use autouse=True sparingly (can hide dependencies)
"""

