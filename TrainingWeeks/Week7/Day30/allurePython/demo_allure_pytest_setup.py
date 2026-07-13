"""
Demo: Allure Pytest Setup - First Allure-Decorated Tests

1. Install: pip install allure-pytest
2. Run: pytest --alluredir=allure-results
3. View: allure serve allure-results
4. Decorators mirror JUnit5 annotations

RUN THIS WITH:
    pytest demo_allure_pytest_setup.py --alluredir=allure-results -v
    allure serve allure-results
"""

import pytest
import allure


# ==========================================================
# SECTION 1: Basic Allure Decorators
# ==========================================================

@allure.epic("Week 6: Unit Testing")
@allure.feature("Allure Reporting")
@allure.story("Basic Decorators")
@allure.title("First Allure Pytest Test")
@allure.severity(allure.severity_level.NORMAL)
def test_basic_decorators():
    """
    This test demonstrates basic Allure decorators.
    
    The docstring becomes the description in the report.
    """
    result = 2 + 2
    assert result == 4


@allure.epic("Week 6: Unit Testing")
@allure.feature("Allure Reporting")
@allure.story("Severity Levels")
@allure.title("Critical Severity Test")
@allure.severity(allure.severity_level.CRITICAL)
def test_critical_severity():
    """Critical tests are highlighted in the report."""
    assert True


@allure.epic("Week 6: Unit Testing")
@allure.feature("Allure Reporting")
@allure.story("Severity Levels")
@allure.title("Blocker Severity Test")
@allure.severity(allure.severity_level.BLOCKER)
def test_blocker_severity():
    """Blocker is the highest severity level."""
    assert True


@allure.epic("Week 6: Unit Testing")
@allure.feature("Allure Reporting")
@allure.story("Severity Levels")
@allure.title("Minor Severity Test")
@allure.severity(allure.severity_level.MINOR)
def test_minor_severity():
    """Minor tests have low priority."""
    assert True


# ==========================================================
# SECTION 2: External Links
# ==========================================================

@allure.story("External Links")
@allure.link("https://docs.example.com/login", name="Documentation")
@allure.link("https://jira.example.com/REQ-123", name="Requirements")
@allure.title("Test with Documentation Links")
def test_with_links():
    """Links appear as clickable icons in the report."""
    assert True


@allure.story("External Links")
@allure.issue("BUG-456", "Bug Tracker")
@allure.testcase("TC-789", "Test Management System")
@allure.title("Test with Issue and TMS Links")
def test_with_issue_and_tms():
    """
    @allure.issue links to bug tracker.
    @allure.testcase links to test management system.
    """
    assert True


# ==========================================================
# SECTION 3: Test Classes with Allure
# ==========================================================

@allure.epic("Week 6: Unit Testing")
@allure.feature("Calculator")
class TestCalculatorWithAllure:
    """Calculator tests organized in a class."""

    @allure.story("Basic Operations")
    @allure.title("Addition Test")
    @allure.severity(allure.severity_level.NORMAL)
    def test_addition(self):
        """Verify addition operation works correctly."""
        assert 2 + 3 == 5

    @allure.story("Basic Operations")
    @allure.title("Subtraction Test")
    @allure.severity(allure.severity_level.NORMAL)
    def test_subtraction(self):
        """Verify subtraction operation works correctly."""
        assert 10 - 3 == 7

    @allure.story("Edge Cases")
    @allure.title("Division by Zero")
    @allure.severity(allure.severity_level.CRITICAL)
    def test_division_by_zero(self):
        """Verify division by zero raises exception."""
        with pytest.raises(ZeroDivisionError):
            _ = 1 / 0


# ==========================================================
# SECTION 4: Parameterized Tests with Allure
# ==========================================================

@allure.story("Parameterized Tests")
@allure.title("Test is_even with value {value}")
@pytest.mark.parametrize("value,expected", [
    (2, True),
    (3, False),
    (0, True),
    (-2, True),
    (-3, False),
])
def test_is_even_parameterized(value, expected):
    """Each parameter combination appears as separate test in report."""
    result = value % 2 == 0
    assert result == expected


@allure.story("Parameterized Tests")
@allure.severity(allure.severity_level.NORMAL)
@pytest.mark.parametrize("a,b,expected", [
    (1, 2, 3),
    (0, 0, 0),
    (-1, 1, 0),
    (100, 200, 300),
])
def test_addition_parameterized(a, b, expected):
    """
    Parameterized addition tests.
    
    Allure shows each parameter set in the report.
    """
    allure.dynamic.title(f"Test {a} + {b} = {expected}")
    assert a + b == expected


# ==========================================================
# SECTION 5: Dynamic Title and Description
# ==========================================================

@allure.story("Dynamic Metadata")
def test_with_dynamic_metadata():
    """Demonstrates dynamic title and description."""
    import datetime
    
    # Set title dynamically
    allure.dynamic.title("Dynamic Test - " + datetime.datetime.now().strftime("%H:%M:%S"))
    
    # Set description dynamically
    allure.dynamic.description(f"""
    This test was executed at: {datetime.datetime.now()}
    
    Dynamic descriptions are useful for:
    - Adding runtime information
    - Including environment details
    - Documenting test conditions
    """)
    
    # Add dynamic severity
    allure.dynamic.severity(allure.severity_level.NORMAL)
    
    assert True


# ==========================================================
# SECTION 6: Intentional Failure Example
# ==========================================================

# Uncomment to see how failures appear in Allure
# @allure.story("Failure Examples")
# @allure.severity(allure.severity_level.CRITICAL)
# @allure.title("Intentional Failure Demo")
# def test_intentional_failure():
#     """This test intentionally fails to demonstrate failure reporting."""
#     assert 2 + 2 == 5, "Math is broken!"


# ==========================================================
# SECTION 7: Skipped Test Example
# ==========================================================

@allure.story("Skip Examples")
@allure.title("Skipped Test")
@pytest.mark.skip(reason="Demonstrating skipped test in Allure")
def test_skipped():
    """This test is skipped and shows in Allure accordingly."""
    assert False  # Would fail if run


@allure.story("Skip Examples")
@allure.title("Expected Failure Test")
@pytest.mark.xfail(reason="Known issue - BUG-999")
def test_expected_failure():
    """This test is expected to fail."""
    assert False


# ==========================================================
# LIVE CODING CHALLENGE
# ==========================================================

# INSTRUCTOR: Have students:
#
# 1. Create a test class with @allure.epic and @allure.feature
# 2. Add 3 test methods with different severities
# 3. Use @pytest.mark.parametrize with allure decorators
# 4. Run: pytest --alluredir=allure-results -v
# 5. View: allure serve allure-results
#
# Time: 15 minutes

