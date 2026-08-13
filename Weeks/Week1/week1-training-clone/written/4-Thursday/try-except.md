# Try-Except

## Learning Objectives
- Use `try`, `except`, `else`, and `finally` blocks correctly.
- Catch specific exceptions and avoid bare `except` clauses.
- Apply exception handling patterns for robust, production-grade code.

---

## Why This Matters

> **Weekly Epic Connection:** Exception handling is how your code responds gracefully to errors. A test script that crashes on the first error is useless — one that catches errors, logs them, and continues testing is professional. `try-except` is the mechanism that makes this possible.

---

## The Concept

### Basic `try-except`

```python
try:
    result = 10 / 0
except ZeroDivisionError:
    print("Cannot divide by zero!")
```

### The Full Structure

```python
try:
    # Code that might raise an exception
    result = int(input("Enter a number: "))
except ValueError:
    # Runs if ValueError occurs
    print("That's not a valid number!")
except (TypeError, KeyError) as e:
    # Catch multiple exception types
    print(f"Error: {e}")
else:
    # Runs only if NO exception occurred
    print(f"You entered: {result}")
finally:
    # ALWAYS runs — whether exception occurred or not
    print("Cleanup complete.")
```

| Block | Runs When |
|-------|-----------|
| `try` | Always (the code you're protecting) |
| `except` | Only if the matching exception occurs |
| `else` | Only if NO exception occurred |
| `finally` | ALWAYS — even if there's an exception, even if you `return` |

### Catching Specific Exceptions

**Always catch specific exceptions**, not bare `except`:

```python
# ❌ BAD — catches EVERYTHING (including KeyboardInterrupt, SystemExit)
try:
    risky_operation()
except:
    print("Something went wrong")

# ❌ STILL BAD — too broad
try:
    risky_operation()
except Exception:
    print("Something went wrong")

# ✅ GOOD — catch exactly what you expect
try:
    value = int(user_input)
except ValueError:
    print("Please enter a valid number")
```

### Accessing the Exception Object

```python
try:
    data = json.loads(raw_text)
except json.JSONDecodeError as e:
    print(f"JSON parse error: {e}")
    print(f"Error at line {e.lineno}, column {e.colno}")
```

### Multi-Exception Handling

```python
# Separate handlers for different exceptions
try:
    config = load_config(path)
    validate(config)
    connect(config["host"])
except FileNotFoundError:
    print(f"Config file not found: {path}")
except KeyError as e:
    print(f"Missing config key: {e}")
except ConnectionError as e:
    print(f"Cannot connect: {e}")
```

### `finally` — Guaranteed Cleanup

```python
def read_first_line(filepath):
    f = None
    try:
        f = open(filepath)
        return f.readline()
    except FileNotFoundError:
        return "File not found"
    finally:
        if f:
            f.close()  # Always runs, even after return!
```

**Better approach — use context managers:**

```python
def read_first_line(filepath):
    try:
        with open(filepath) as f:
            return f.readline()
    except FileNotFoundError:
        return "File not found"
```

### Re-Raising Exceptions

Sometimes you want to log an error but still let it propagate:

```python
try:
    process_data(data)
except ValueError as e:
    logger.error(f"Data processing failed: {e}")
    raise  # Re-raise the same exception
```

### Exception Chaining

Python 3 supports chaining — linking a new exception to the original cause:

```python
try:
    value = int(config["timeout"])
except KeyError as e:
    raise ConfigError("Missing timeout setting") from e
# Output shows both: ConfigError caused by KeyError
```

### Practical Patterns

**Retry pattern:**
```python
def fetch_with_retry(url, max_retries=3):
    for attempt in range(1, max_retries + 1):
        try:
            response = requests.get(url, timeout=10)
            return response
        except requests.Timeout:
            print(f"Attempt {attempt} timed out")
            if attempt == max_retries:
                raise
```

**Default value on failure:**
```python
def safe_parse_int(value, default=0):
    try:
        return int(value)
    except (ValueError, TypeError):
        return default
```

**EAFP vs. LBYL:**
```python
# LBYL (Look Before You Leap) — check first
if "key" in dictionary:
    value = dictionary["key"]

# EAFP (Easier to Ask Forgiveness than Permission) — try it
try:
    value = dictionary["key"]
except KeyError:
    value = default

# Python prefers EAFP!
```

**`contextlib.suppress` — suppress specific exceptions silently:**

When you want to do something and simply ignore it if it fails, `suppress` is cleaner than a try/except with an empty `pass`:

```python
from contextlib import suppress

# Instead of:
try:
    os.remove("temp.txt")
except FileNotFoundError:
    pass

# Use:
import os
with suppress(FileNotFoundError):
    os.remove("temp.txt")

# Multiple exception types
with suppress(FileNotFoundError, PermissionError):
    os.remove("temp.txt")
```

**Integrating with logging:**

In production and test automation code, always log exceptions rather than silently swallowing them:

```python
import logging

logger = logging.getLogger(__name__)

def run_test(test_name):
    try:
        execute(test_name)
    except TimeoutError as e:
        logger.warning("Test %s timed out: %s", test_name, e)
        return "timeout"
    except Exception as e:
        logger.exception("Unexpected error in %s", test_name)  # logs full traceback
        return "error"
    else:
        logger.info("Test %s passed", test_name)
        return "pass"
    finally:
        logger.debug("Test %s finished", test_name)
```

> **Note:** `logger.exception()` is equivalent to `logger.error()` but automatically appends the current exception's traceback to the log message. Use it inside `except` blocks when you want the full traceback in your log.

---

## Summary

- `try-except` catches and handles exceptions gracefully.
- **Always catch specific exceptions** — never use bare `except`.
- `else` runs when no exception occurs; `finally` runs no matter what.
- Use `raise` to re-raise exceptions; use `from` for exception chaining.
- Python favors **EAFP** (try-except) over LBYL (check-first).
- Combine with the retry pattern for robust automation code.

---

## Additional Resources
- [Python Docs — Errors and Exceptions](https://docs.python.org/3/tutorial/errors.html)
- [Real Python — Python Exceptions](https://realpython.com/python-exceptions/)
- [PEP 3134 — Exception Chaining](https://peps.python.org/pep-3134/)
