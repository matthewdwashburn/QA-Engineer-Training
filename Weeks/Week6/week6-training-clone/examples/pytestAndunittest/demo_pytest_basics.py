"""
Demo: Pytest Basics - Test Structure and Assertions

1. Pytest uses plain 'assert' statements - no special methods needed!
2. Test functions start with 'test_' for auto-discovery
3. Test classes start with 'Test' (no __init__)
4. No inheritance required - just write functions
5. Detailed failure output shows exactly what went wrong

RUN THIS WITH:
    pytest demo_pytest_basics.py -v
    pytest demo_pytest_basics.py -v -k "add"  # Only 'add' tests
"""

import pytest
from calculator import Calculator


# ==========================================================
# SECTION 1: Basic Test Functions
# ==========================================================

def test_addition_returns_correct_sum():
    """Simple test function - no class needed!"""
    calc = Calculator()
    result = calc.add(2, 3)
    assert result == 5


def test_subtraction_returns_correct_difference():
    """Another simple test."""
    calc = Calculator()
    assert calc.subtract(10, 3) == 7


def test_multiplication_returns_correct_product():
    """Can include message in assert for clarity."""
    calc = Calculator()
    result = calc.multiply(4, 5)
    assert result == 20, f"Expected 20, got {result}"


# ==========================================================
# SECTION 2: Testing Edge Cases
# ==========================================================

def test_add_with_zero():
    """Zero doesn't change the number."""
    calc = Calculator()
    assert calc.add(42, 0) == 42
    assert calc.add(0, 42) == 42


def test_add_negative_numbers():
    """Negative numbers work correctly."""
    calc = Calculator()
    assert calc.add(-5, -3) == -8
    assert calc.add(5, -3) == 2
    assert calc.add(-5, 3) == -2


def test_multiply_by_zero():
    """Anything times zero is zero."""
    calc = Calculator()
    assert calc.multiply(100, 0) == 0
    assert calc.multiply(0, 100) == 0


# ==========================================================
# SECTION 3: Boolean Assertions
# ==========================================================

def test_is_even_with_even_numbers():
    """Even numbers return True."""
    calc = Calculator()
    assert calc.is_even(2) is True
    assert calc.is_even(0) is True
    assert calc.is_even(-4) is True


def test_is_even_with_odd_numbers():
    """Odd numbers return False."""
    calc = Calculator()
    assert calc.is_even(1) is False
    assert calc.is_even(7) is False
    assert calc.is_even(-3) is False


def test_is_positive():
    """Test positive number detection."""
    calc = Calculator()
    assert calc.is_positive(1) is True
    assert calc.is_positive(100) is True
    assert calc.is_positive(0) is False
    assert calc.is_positive(-1) is False


# ==========================================================
# SECTION 4: Test Classes (Optional but useful for grouping)
# ==========================================================

class TestCalculatorDivision:
    """
    Group related tests in a class.
    
    NOTE: 
    - Class name starts with 'Test'
    - NO __init__ method
    - Methods still start with 'test_'
    """

    def test_divide_returns_correct_quotient(self):
        calc = Calculator()
        assert calc.divide(10, 2) == 5.0

    def test_divide_returns_float(self):
        calc = Calculator()
        result = calc.divide(7, 2)
        assert result == 3.5
        assert isinstance(result, float)

    def test_divide_with_negative_numbers(self):
        calc = Calculator()
        assert calc.divide(-10, 2) == -5.0
        assert calc.divide(10, -2) == -5.0
        assert calc.divide(-10, -2) == 5.0


class TestCalculatorPower:
    """Tests for the power method."""

    def test_power_returns_correct_result(self):
        calc = Calculator()
        assert calc.power(2, 3) == 8
        assert calc.power(10, 2) == 100

    def test_power_of_zero(self):
        calc = Calculator()
        assert calc.power(5, 0) == 1  # Anything to the power of 0 is 1
        assert calc.power(0, 5) == 0  # 0 to any power is 0

    def test_power_of_one(self):
        calc = Calculator()
        assert calc.power(5, 1) == 5  # Anything to the power of 1 is itself


# ==========================================================
# SECTION 5: Exception Testing with pytest.raises
# ==========================================================

def test_divide_by_zero_raises_exception():
    """Use pytest.raises to test exception handling."""
    calc = Calculator()
    
    with pytest.raises(ZeroDivisionError):
        calc.divide(10, 0)


def test_divide_by_zero_exception_message():
    """Capture exception to verify message."""
    calc = Calculator()
    
    with pytest.raises(ZeroDivisionError) as exc_info:
        calc.divide(10, 0)
    
    assert "zero" in str(exc_info.value).lower()


def test_negative_exponent_raises_value_error():
    """Test that negative exponents raise ValueError."""
    calc = Calculator()
    
    with pytest.raises(ValueError) as exc_info:
        calc.power(2, -1)
    
    assert "negative" in str(exc_info.value).lower()


# ==========================================================
# SECTION 6: Pytest's Helpful Failure Output
# ==========================================================

# Uncomment to see pytest's detailed failure output:

# def test_intentional_failure_for_demo():
#     
#     expected = "Hello, World!"
#     actual = "Hello, world!"  # lowercase 'w'
#     assert expected == actual
#     
#     # Pytest will show:
#     # E       AssertionError: assert 'Hello, World!' == 'Hello, world!'
#     # E         - Hello, World!
#     # E         + Hello, world!


# def test_list_comparison_failure():
#     
#     expected = [1, 2, 3, 4, 5]
#     actual = [1, 2, 4, 4, 5]  # 3 changed to 4
#     assert expected == actual


# ==========================================================
# SECTION 7: Testing Collections
# ==========================================================

def test_max_value():
    """Test max_value method."""
    calc = Calculator()
    assert calc.max_value(5, 10) == 10
    assert calc.max_value(10, 5) == 10
    assert calc.max_value(7, 7) == 7


def test_min_value():
    """Test min_value method."""
    calc = Calculator()
    assert calc.min_value(5, 10) == 5
    assert calc.min_value(10, 5) == 5
    assert calc.min_value(7, 7) == 7




