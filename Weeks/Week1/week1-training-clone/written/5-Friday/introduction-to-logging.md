# Introduction to Logging

## Learning Objectives
- Explain why logging is important and how it differs from `print()`.
- Use Python's built-in `logging` module for basic logging.
- Understand when to log and what information to include.

---

## Why This Matters

> **Weekly Epic Connection:** This is the capstone of your Python foundation week. Logging is what separates amateur scripts from professional, production-grade code. When a test fails at 3 AM in a CI/CD pipeline, print statements are useless — log files tell you what happened, when, and why.

---

## The Concept

### Why Not `print()`?

`print()` works for quick debugging, but it's inadequate for real applications:

| Feature | `print()` | `logging` |
|---------|-----------|-----------|
| Severity levels | ❌ No | ✅ DEBUG, INFO, WARNING, ERROR, CRITICAL |
| Timestamps | ❌ No | ✅ Automatic |
| Output destinations | stdout only | Files, console, email, remote servers |
| Enable/disable | Manual removal | Configure by level |
| Production-ready | ❌ No | ✅ Yes |
| Thread-safe | ❌ No | ✅ Yes |

### Basic Usage

```python
import logging

# Basic configuration (one line to get started)
logging.basicConfig(level=logging.INFO)

# Log messages at different levels
logging.debug("This is a debug message")      # Won't show (below INFO)
logging.info("Application started")           # ✅ Shows
logging.warning("Disk space is low")          # ✅ Shows
logging.error("Failed to connect to database") # ✅ Shows
logging.critical("Application is crashing!")   # ✅ Shows
```

Output:
```
INFO:root:Application started
WARNING:root:Disk space is low
ERROR:root:Failed to connect to database
CRITICAL:root:Application is crashing!
```

### Logging vs. Print — A Practical Example

```python
# ❌ Using print — messy, no metadata, can't disable
def run_tests(test_list):
    print("Starting test suite...")
    for test in test_list:
        print(f"Running {test}...")
        print(f"  {test} passed!")
    print("All tests complete.")

# ✅ Using logging — structured, configurable, professional
import logging

logger = logging.getLogger(__name__)

def run_tests(test_list):
    logger.info("Starting test suite with %d tests", len(test_list))
    for test in test_list:
        logger.debug("Running test: %s", test)
        logger.info("Test '%s' passed", test)
    logger.info("All %d tests complete", len(test_list))
```

### Creating a Logger

Best practice is to create a **named logger** per module:

```python
import logging

# Create a logger for this module
logger = logging.getLogger(__name__)

def process_data(data):
    logger.info("Processing %d records", len(data))
    try:
        result = transform(data)
        logger.debug("Transformation result: %s", result)
        return result
    except Exception as e:
        logger.error("Processing failed: %s", e, exc_info=True)
        raise
```

The `__name__` parameter creates a logger named after the module (e.g., `my_package.my_module`), making it easy to identify where messages come from.

### The Logger Hierarchy

Python's logging system is organized as a **hierarchy of loggers** connected by dots in their names:

```
root
  └── my_app
        ├── my_app.database
        ├── my_app.api
        └── my_app.tests
```

Loggers automatically **propagate** messages up to their parent. If `my_app.database` logs a `WARNING`, the message travels up to `my_app` and then to the `root` logger — all three get to handle it.

```python
import logging

# Parent
app_logger = logging.getLogger("my_app")
app_logger.setLevel(logging.INFO)

# Child — inherits the parent's level and propagates messages upward
db_logger = logging.getLogger("my_app.database")

# This message will be handled by both db_logger and app_logger
db_logger.warning("Connection pool nearly full")

# Stop propagation if you don't want the parent to also handle it
db_logger.propagate = False
```

> **Best practice:** Use `logging.getLogger(__name__)` in every module. Because `__name__` reflects the module's package path (e.g., `framework.runner.core`), you automatically get a meaningful hierarchy that matches your source code structure.

### What to Log

| Level | What to Log |
|-------|-------------|
| **DEBUG** | Detailed diagnostic info — variable values, function entry/exit |
| **INFO** | Confirmation that things work — "Test started", "Connected to DB" |
| **WARNING** | Something unexpected but not failure — low disk, deprecated API |
| **ERROR** | A failure in a specific operation — cannot connect, assertion failed |
| **CRITICAL** | System-level failure — out of memory, configuration corrupt |

### Including Context

Always include enough context to diagnose issues:

```python
# ❌ Useless log
logger.error("Error occurred")

# ✅ Actionable log
logger.error(
    "Failed to fetch user profile for user_id=%s: status=%d, response=%s",
    user_id, response.status_code, response.text[:200]
)
```

### `exc_info=True` vs. `logger.exception()`

When you catch an exception and want to include the traceback in the log:

```python
try:
    result = risky_operation()
except ValueError as e:
    # Option 1: logger.exception() — always includes traceback, uses ERROR level
    logger.exception("Operation failed: %s", e)

    # Option 2: explicit exc_info=True — same result
    logger.error("Operation failed: %s", e, exc_info=True)

    # Option 3: exc_info=True with WARNING level
    logger.warning("Non-critical issue", exc_info=True)
```

- `logger.exception(msg)` is shorthand for `logger.error(msg, exc_info=True)`.
- Use `logger.exception()` inside `except` blocks when you want the full traceback.
- Use `exc_info=True` when you want a traceback at a level other than ERROR.

### Log Message Formatting — Use `%s`, Not f-strings

Always use `%`-style string formatting in log calls, **not** f-strings:

```python
# ✅ Lazy formatting — the string is only built if the level is enabled
logger.debug("Processing user %s with %d items", user_id, count)

# ❌ Eager formatting — string is built regardless of whether DEBUG is enabled
logger.debug(f"Processing user {user_id} with {count} items")
```

With `%s` formatting, Python's logging system builds the final string **only if** the message will actually be logged. This avoids wasted computation when the log level is set above DEBUG in production.

---

## Summary

- Use `logging` instead of `print()` for anything beyond quick debugging.
- The logging module provides **severity levels**, **timestamps**, **configurable output**, and **thread safety**.
- Create named loggers with `logging.getLogger(__name__)`.
- Log at the **appropriate level** — DEBUG for details, INFO for milestones, ERROR for failures.
- Always include **context** — who, what, when, why.
- We'll configure advanced logging (handlers, formatters, config files) in the next reading.

---

## Additional Resources
- [Python Docs — Logging HOWTO](https://docs.python.org/3/howto/logging.html)
- [Real Python — Logging in Python](https://realpython.com/python-logging/)
- [Python Docs — logging Module](https://docs.python.org/3/library/logging.html)
