"""
Demo: Skipping Tests and Expected Failures

INSTRUCTOR TALKING POINTS:
1. @pytest.mark.skip - Always skip this test
2. @pytest.mark.skipif - Skip if condition is true
3. @pytest.mark.xfail - Expected to fail (but run it anyway)
4. Platform-specific skips for cross-platform code
5. Skip vs xfail: Skip doesn't run, xfail runs and checks failure

RUN THIS WITH:
    pytest demo_skip_xfail.py -v
    pytest demo_skip_xfail.py -v -rs  # Show skip reasons
    pytest demo_skip_xfail.py -v --runxfail  # Run xfail tests normally
"""

import pytest
import sys
import os
from calculator import Calculator


# ==========================================================
# SECTION 1: Basic Skip
# ==========================================================

@pytest.mark.skip(reason="Demonstrating skip - feature not implemented yet")
def test_skipped_always():
    """This test is always skipped."""
    assert False, "This should never run"


@pytest.mark.skip(reason="Waiting for API v2 release")
def test_new_api_feature():
    """Skip until new API is available."""
    # api.v2.new_feature()
    pass


# ==========================================================
# SECTION 2: Conditional Skip with skipif
# ==========================================================

@pytest.mark.skipif(
    sys.version_info < (3, 10),
    reason="Requires Python 3.10+ for match statement"
)
def test_python310_feature():
    """Only runs on Python 3.10 or higher."""
    # Uses match statement (Python 3.10+)
    value = 1
    # match value:
    #     case 1:
    #         result = "one"
    assert True


@pytest.mark.skipif(
    sys.platform == "win32",
    reason="Unix-only test"
)
def test_unix_specific_feature():
    """Skip on Windows."""
    # Test Unix-specific functionality
    assert os.name == "posix"


@pytest.mark.skipif(
    sys.platform != "win32",
    reason="Windows-only test"
)
def test_windows_specific_feature():
    """Skip on non-Windows platforms."""
    # Test Windows-specific functionality
    assert os.name == "nt"


# ==========================================================
# SECTION 3: Platform-Specific Decorators
# ==========================================================

# Create reusable skip decorators
skip_on_windows = pytest.mark.skipif(
    sys.platform == "win32",
    reason="Not supported on Windows"
)

skip_on_ci = pytest.mark.skipif(
    os.environ.get("CI") == "true",
    reason="Skip in CI environment"
)


@skip_on_windows
def test_posix_permissions():
    """Test POSIX file permissions."""
    # Would fail on Windows
    assert True


@skip_on_ci
def test_slow_integration():
    """Skip slow tests in CI."""
    import time
    time.sleep(0.1)  # Simulated slow operation
    assert True


# ==========================================================
# SECTION 4: Expected Failures with xfail
# ==========================================================

@pytest.mark.xfail(reason="Known bug - issue #123")
def test_known_bug():
    """
    This test documents a known bug.
    
    - If it FAILS: Test passes (expected failure - XFAIL)
    - If it PASSES: Test shows as XPASS (unexpected pass)
    """
    calc = Calculator()
    # Pretend this is a known bug
    result = calc.divide(1, 3)
    assert result == 0.333  # Will fail due to floating point


@pytest.mark.xfail(
    reason="Edge case not handled yet",
    strict=True  # Fail if test unexpectedly passes!
)
def test_strict_xfail():
    """
    strict=True means test MUST fail.
    
    If test passes unexpectedly, it's a test failure.
    Useful when you expect to fix a bug.
    """
    assert False, "This is expected to fail"


@pytest.mark.xfail(raises=ZeroDivisionError)
def test_xfail_specific_exception():
    """xfail only if specific exception is raised."""
    calc = Calculator()
    calc.divide(1, 0)  # Expected to raise ZeroDivisionError


@pytest.mark.xfail(
    sys.platform == "win32",
    reason="Known Windows issue"
)
def test_conditional_xfail():
    """xfail only on specific platform."""
    # This feature has issues on Windows
    assert True


# ==========================================================
# SECTION 5: Skip vs xfail Comparison
# ==========================================================

class TestSkipVsXfail:
    """Demonstrate the difference between skip and xfail."""

    @pytest.mark.skip(reason="Don't run this at all")
    def test_skip_example(self):
        """
        SKIP: Test is NOT executed at all.
        
        Use when:
        - Feature not implemented
        - Missing dependency
        - Platform incompatibility
        """
        print("This will never print")
        assert False

    @pytest.mark.xfail(reason="Run but expect failure")
    def test_xfail_example(self):
        """
        XFAIL: Test IS executed, but expected to fail.
        
        Use when:
        - Documenting a known bug
        - Test for feature in development
        - Checking if bug is fixed
        """
        print("This WILL print (run with -s)")
        assert False  # Expected to fail


# ==========================================================
# SECTION 6: Skipping at Runtime (pytest.skip())
# ==========================================================

def test_runtime_skip():
    """Skip during test execution based on conditions."""
    calc = Calculator()
    result = calc.add(1, 1)
    
    if result != 2:
        pytest.skip("Calculator is broken, skipping remaining tests")
    
    # More tests...
    assert calc.add(2, 2) == 4


def test_skip_if_no_network():
    """Skip if network is unavailable."""
    try:
        import socket
        socket.create_connection(("8.8.8.8", 53), timeout=1)
    except OSError:
        pytest.skip("Network unavailable")
    
    # Network-dependent tests...
    assert True


# ==========================================================
# SECTION 7: Skipping Entire Module
# ==========================================================

# At the top of a module, you can skip all tests:
# pytestmark = pytest.mark.skip("Module under construction")

# Or conditionally:
# pytestmark = pytest.mark.skipif(
#     sys.version_info < (3, 8),
#     reason="Requires Python 3.8+"
# )


# ==========================================================
# SECTION 8: Custom Skip Markers
# ==========================================================

# Define in conftest.py or pytest.ini:
# [pytest]
# markers =
#     slow: marks tests as slow (deselect with '-m "not slow"')
#     integration: marks tests as integration tests

@pytest.mark.slow
def test_slow_operation():
    """
    Marked as slow.
    
    Run only slow tests: pytest -m slow
    Skip slow tests: pytest -m "not slow"
    """
    import time
    time.sleep(0.1)
    assert True


@pytest.mark.integration
def test_integration_test():
    """
    Marked as integration test.
    
    Can be filtered with: pytest -m integration
    """
    assert True


# ==========================================================
# SECTION 9: xfail as Decorator vs Context Manager
# ==========================================================

def test_xfail_context_manager():
    """Use xfail as context manager for part of test."""
    calc = Calculator()
    
    # Normal assertions
    assert calc.add(1, 1) == 2
    
    # This part is expected to fail
    with pytest.raises(ZeroDivisionError):
        calc.divide(1, 0)


# ==========================================================
# SECTION 10: Useful pytest.ini Configuration
# ==========================================================

"""
# pytest.ini

[pytest]
markers =
    slow: marks tests as slow
    integration: marks tests as integration tests
    smoke: marks tests as smoke tests

# Skip slow tests by default
addopts = -m "not slow"
"""


# ==========================================================
# LIVE CODING CHALLENGE
# ==========================================================

# INSTRUCTOR: Have students:
#
# 1. Create a test that skips on Python < 3.9
#
# 2. Create a test that documents a "known bug" with xfail
#
# 3. Create a test that skips at runtime if a specific
#    environment variable is not set
#
# 4. Create custom markers for "smoke" and "regression" tests
#
# Example:
# @pytest.mark.smoke
# def test_app_starts():
#     assert app.is_running()
#
# Run with: pytest -m smoke

