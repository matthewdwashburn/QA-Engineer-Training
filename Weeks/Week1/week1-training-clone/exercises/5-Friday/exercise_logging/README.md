# Exercise: Logging — Add Professional Logging to a Project

**Mode:** Implementation (Code Lab)
**Duration:** 45–60 minutes
**Day:** Friday | **Week:** 1 — Git & Python Fundamentals

---

## Objective

By the end of this exercise, you will be able to:
- Configure Python logging with multiple handlers (console + file).
- Use appropriate logging levels (DEBUG, INFO, WARNING, ERROR, CRITICAL).
- Add structured logging to an existing codebase.
- Use `exc_info=True` for traceback logging.

---

## Prerequisites

| Concept | Source |
|---------|--------|
| Introduction to Logging | `written/5-Friday/introduction-to-logging.md` |
| Logging Levels & Config | `written/5-Friday/logging-levels-log-configuration.md` |
| Instructor Demo | `demos/5-Friday/INSTRUCTOR_GUIDE.md` (Demo 1) |

---

## The Scenario

You've inherited a codebase that uses `print()` everywhere. Your task: replace all print statements with proper logging, configure handlers, and make the system production-ready.

---

## Core Tasks

### Task 1: Set Up Logging Configuration (15 min)

Create `starter_code/logging_config.py`:

```python
"""
Configure logging for the QA Test Framework.

Requirements:
1. Console handler: Show INFO and above, concise format
2. File handler: Capture DEBUG and above, detailed format with timestamps
3. Use a RotatingFileHandler (max 1MB, keep 3 backups)
"""

import logging
from logging.handlers import RotatingFileHandler


def setup_logging(log_file="test_framework.log", console_level=logging.INFO):
    """Configure and return the root logger for the framework.

    Args:
        log_file: Path to the log file
        console_level: Minimum level for console output

    Returns:
        logging.Logger configured with both handlers
    """
    # TODO: Create logger named "qa_framework"
    # TODO: Add StreamHandler for console (INFO+)
    # TODO: Add RotatingFileHandler for file (DEBUG+)
    # TODO: Set appropriate formatters on each handler
    # TODO: Return the configured logger
    pass
```

---

### Task 2: Refactor Print-Based Code (20 min)

The file `starter_code/legacy_runner.py` contains a test runner that uses `print()`. Refactor it to use logging:

```python
"""
LEGACY CODE — This file uses print() everywhere.
YOUR TASK: Replace all print() calls with appropriate logging calls.
"""

import time
import random


def run_test(test_name):
    """Run a single test (simulated)."""
    print(f"Running test: {test_name}")       # TODO: Log at appropriate level
    duration = random.uniform(0.1, 2.0)
    time.sleep(0.01)  # Simulate work

    if random.random() < 0.2:
        print(f"ERROR: {test_name} failed!")   # TODO: Log as ERROR
        print(f"  Duration: {duration:.2f}s")
        return False

    print(f"  {test_name} passed ({duration:.2f}s)")  # TODO: Log as INFO
    return True


def run_suite(suite_name, test_names):
    """Run a suite of tests."""
    print(f"\n{'='*50}")
    print(f"Starting suite: {suite_name}")     # TODO: Log as INFO
    print(f"Tests to run: {len(test_names)}")
    print(f"{'='*50}\n")

    results = {"passed": 0, "failed": 0}

    for i, test in enumerate(test_names, 1):
        print(f"[{i}/{len(test_names)}]", end=" ")  # TODO: Log with context
        if run_test(test):
            results["passed"] += 1
        else:
            results["failed"] += 1

    total = results["passed"] + results["failed"]
    rate = results["passed"] / total * 100

    print(f"\n{'='*50}")
    print(f"Results: {results['passed']}/{total} passed ({rate:.1f}%)")

    if rate < 80:
        print(f"WARNING: Pass rate below 80%!")  # TODO: Log as WARNING
    if rate < 50:
        print(f"CRITICAL: More than half the tests failed!")  # TODO: CRITICAL

    return results


def main():
    print("QA Test Framework v1.0")        # TODO: Log as INFO
    print("Initializing...")

    suites = {
        "Smoke Tests": ["test_login", "test_homepage", "test_search"],
        "Regression": ["test_checkout", "test_payment", "test_profile",
                       "test_settings", "test_logout"],
        "Performance": ["test_load_page", "test_api_response"],
    }

    all_results = {"passed": 0, "failed": 0}

    for suite_name, tests in suites.items():
        try:
            result = run_suite(suite_name, tests)
            all_results["passed"] += result["passed"]
            all_results["failed"] += result["failed"]
        except Exception as e:
            print(f"Suite {suite_name} crashed: {e}")  # TODO: Log with exc_info

    total = all_results["passed"] + all_results["failed"]
    print(f"\nFinal: {all_results['passed']}/{total} overall")


if __name__ == "__main__":
    main()
```

**Requirements:**
- Replace EVERY `print()` with an appropriate logging call.
- Use `logger.debug()` for detailed diagnostics.
- Use `logger.info()` for progress updates.
- Use `logger.warning()` for low pass rates.
- Use `logger.error()` for test failures (with `exc_info=True` where applicable).
- Import and use `setup_logging()` from Task 1.

---

### Task 3: Verify the Log File (10 min)

1. Run the refactored code.
2. Open the generated log file.
3. Verify that:
   - DEBUG messages appear in the file but NOT on the console.
   - Timestamps are present in the file.
   - The file contains more entries than the console output.
4. **Question:** Why is lazy formatting (`logger.info("x %s", val)`) preferred over f-strings in log calls?

---

## Stretch Goals

- [ ] Add a custom logging filter that suppresses debug messages for passing tests.
- [ ] Configure logging using a dictionary (`logging.config.dictConfig`).
- [ ] Add a third handler that logs only ERROR+ to a separate `errors.log` file.

---

## Definition of Done

- [ ] `setup_logging()` configures console + file handlers with different levels/formats.
- [ ] ALL `print()` calls replaced with appropriate logging calls.
- [ ] Log file is created with DEBUG+ entries and timestamps.
- [ ] Console shows INFO+ entries only.
- [ ] `exc_info=True` used in at least one error log.
- [ ] Script runs without errors.
