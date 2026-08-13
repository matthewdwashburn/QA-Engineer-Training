# Exercise: Functions — Parameters, Return Values, *args/**kwargs

**Mode:** Implementation (Code Lab)
**Duration:** 60–90 minutes
**Day:** Tuesday | **Week:** 1 — Git & Python Fundamentals

---

## Objective

By the end of this exercise, you will be able to:
- Write functions with positional, keyword, and default parameters.
- Use `*args` and `**kwargs` for flexible function signatures.
- Return single and multiple values from functions.
- Compose functions by calling one from another.

---

## Prerequisites

| Concept | Source |
|---------|--------|
| Function Declaration | `written/2-Tuesday/function-declaration-syntax.md` |
| Parameters | `written/2-Tuesday/function-parameters.md` |
| Return Values | `written/2-Tuesday/python-function-return-values.md` |
| Function Invocation | `written/2-Tuesday/function-invocation.md` |
| Instructor Demo | `demos/2-Tuesday/INSTRUCTOR_GUIDE.md` (Demo 1) |

---

## The Scenario

You are building a **Test Utilities Library** — a collection of reusable functions your QA team can import and use across projects.

---

## Core Tasks

### Task 1: Basic Functions (15 min)

Create `starter_code/test_utils.py` and implement the following functions:

```python
def format_test_name(name):
    """Convert a human-readable name to a test function name.

    Example:
        format_test_name("Valid Login") → "test_valid_login"
        format_test_name("  Search Results Page  ") → "test_search_results_page"

    Rules:
        - Lowercase
        - Spaces replaced with underscores
        - Leading/trailing whitespace stripped
        - Prefixed with "test_"
    """
    pass  # TODO


def is_valid_test_name(name):
    """Check if a string is a valid test function name.

    Rules:
        - Must start with "test_"
        - Must contain only lowercase letters, digits, and underscores
        - Must be at least 6 characters (e.g., "test_x")

    Returns: bool
    """
    pass  # TODO
```

**Test your functions:**
```python
assert format_test_name("Valid Login") == "test_valid_login"
assert format_test_name("  Search Results  ") == "test_search_results"
assert is_valid_test_name("test_login") == True
assert is_valid_test_name("login_test") == False
assert is_valid_test_name("test_") == False
```

---

### Task 2: Default Parameters (15 min)

Add the following functions to `test_utils.py`:

```python
def create_test_result(name, status="pass", duration_ms=0, error=None):
    """Create a test result dictionary.

    Args:
        name: Test name (required)
        status: "pass" or "fail" (default: "pass")
        duration_ms: Execution time in ms (default: 0)
        error: Error message if failed (default: None)

    Returns:
        dict with keys: name, status, duration_ms, error
    """
    pass  # TODO


def format_duration(ms, unit="ms"):
    """Format a duration value with the specified unit.

    Args:
        ms: Duration in milliseconds
        unit: "ms", "s", or "min" (default: "ms")

    Returns:
        Formatted string like "1,200ms" or "1.20s" or "0.02min"
    """
    pass  # TODO
```

**Test your functions:**
```python
r1 = create_test_result("test_login")
assert r1 == {"name": "test_login", "status": "pass", "duration_ms": 0, "error": None}

r2 = create_test_result("test_checkout", status="fail", duration_ms=2300, error="Timeout")
assert r2["status"] == "fail"
assert r2["error"] == "Timeout"

assert format_duration(1200) == "1,200ms"
assert format_duration(1200, "s") == "1.20s"
```

---

### Task 3: *args and **kwargs (20 min)

Add the following functions:

```python
def calculate_stats(*scores):
    """Calculate statistics for any number of scores.

    Returns:
        dict with keys: count, total, average, min, max

    Raises:
        ValueError if no scores provided
    """
    pass  # TODO


def build_test_config(**settings):
    """Build a test configuration with defaults.

    Default config:
        browser: "chrome"
        headless: False
        timeout: 30
        retries: 0
        base_url: "http://localhost:3000"

    Any **settings passed override the defaults.

    Returns: dict
    """
    pass  # TODO
```

**Test your functions:**
```python
stats = calculate_stats(85, 92, 78, 95, 88)
assert stats["count"] == 5
assert stats["average"] == 87.6
assert stats["min"] == 78
assert stats["max"] == 95

config = build_test_config(headless=True, timeout=60)
assert config["browser"] == "chrome"  # default
assert config["headless"] == True     # overridden
assert config["timeout"] == 60       # overridden
```

---

### Task 4: Multiple Return Values & Composition (20 min)

Add the final functions:

```python
def analyze_results(*results):
    """Analyze a list of test result dicts.

    Args:
        *results: test result dicts (from create_test_result)

    Returns:
        tuple of (passed_count, failed_count, pass_rate, avg_duration)
    """
    pass  # TODO


def generate_report(*results):
    """Generate a formatted test report string.

    Calls analyze_results() internally and formats the output.

    Returns: formatted multi-line string
    """
    pass  # TODO
```

**Test your functions:**
```python
results = [
    create_test_result("test_login", "pass", 1200),
    create_test_result("test_search", "pass", 850),
    create_test_result("test_checkout", "fail", 2300, "Timeout"),
    create_test_result("test_profile", "pass", 450),
]

passed, failed, rate, avg = analyze_results(*results)
assert passed == 3
assert failed == 1
assert rate == 75.0
```

---

## Stretch Goals

- [ ] Add type hints to all function signatures.
- [ ] Add docstring examples that can be tested with `python -m doctest test_utils.py`.
- [ ] Write a `main()` function that demonstrates all utilities.

---

## Definition of Done

- [ ] All 8 functions implemented and passing their assert tests.
- [ ] Functions use docstrings.
- [ ] `*args` and `**kwargs` used correctly.
- [ ] `generate_report` composes at least 2 other functions.
- [ ] No hardcoded values — all logic uses parameters.
