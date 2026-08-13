# Exception Handling and Custom Exceptions

## Learning Objectives
- Understand Python's exception hierarchy.
- Create custom exception classes for domain-specific errors.
- Know when to use custom exceptions vs. built-in ones.

---

## Why This Matters

> **Weekly Epic Connection:** Test automation encounters errors constantly — network timeouts, missing elements, invalid data. Custom exceptions give your test framework meaningful, domain-specific error types that make debugging faster and code more expressive.

---

## The Concept

### Python's Exception Hierarchy

All exceptions inherit from `BaseException`, but you'll mostly work with `Exception` and its subclasses:

```
BaseException
├── SystemExit
├── KeyboardInterrupt
├── GeneratorExit
└── Exception
    ├── ValueError
    ├── TypeError
    ├── KeyError
    ├── IndexError
    ├── FileNotFoundError
    ├── AttributeError
    ├── RuntimeError
    └── ... (many more)
```

### Common Built-in Exceptions

| Exception | When It Occurs |
|-----------|---------------|
| `ValueError` | Right type, wrong value: `int("hello")` |
| `TypeError` | Wrong type: `"5" + 3` |
| `KeyError` | Missing dictionary key: `d["missing"]` |
| `IndexError` | Index out of range: `[1,2,3][5]` |
| `FileNotFoundError` | File doesn't exist: `open("nope.txt")` |
| `AttributeError` | Object has no such attribute: `"hello".nonexistent()` |
| `ZeroDivisionError` | Division by zero: `1 / 0` |
| `ImportError` | Failed import: `import nonexistent_module` |

### Creating Custom Exceptions

Custom exceptions are simple classes that inherit from `Exception`:

```python
class TestFailedError(Exception):
    """Raised when a test assertion fails."""
    pass

class ConfigurationError(Exception):
    """Raised when configuration is invalid or missing."""
    pass

class TimeoutError(Exception):
    """Raised when an operation exceeds the time limit."""
    def __init__(self, operation, timeout_seconds):
        self.operation = operation
        self.timeout_seconds = timeout_seconds
        super().__init__(
            f"Operation '{operation}' timed out after {timeout_seconds}s"
        )
```

### Using Custom Exceptions

```python
class TestFramework:
    def __init__(self, config):
        if "base_url" not in config:
            raise ConfigurationError("Missing required 'base_url' in config")
        self.base_url = config["base_url"]

    def run_test(self, test_name, timeout=30):
        # ... run the test ...
        elapsed = 45  # simulated
        if elapsed > timeout:
            raise TimeoutError(test_name, timeout)
```

### Exception Hierarchy for Your Project

Organize exceptions in a hierarchy:

```python
class FrameworkError(Exception):
    """Base exception for our test framework."""
    pass

class ConfigError(FrameworkError):
    """Configuration-related errors."""
    pass

class TestExecutionError(FrameworkError):
    """Errors during test execution."""
    pass

class PageLoadError(TestExecutionError):
    """Page failed to load."""
    pass

class ElementNotFoundError(TestExecutionError):
    """UI element not found on page."""
    pass
```

This hierarchy lets you catch at different levels:

```python
try:
    run_tests()
except PageLoadError:
    print("Page didn't load — retrying...")
except TestExecutionError:
    print("Test execution failed")
except FrameworkError:
    print("Framework error")
```

### When to Create Custom Exceptions

| Use Custom When... | Use Built-in When... |
|---|---|
| You need domain-specific error types | The built-in exception fits perfectly |
| You want to add extra context (attributes) | The error is simple and self-explanatory |
| You want to catch errors at different levels in a hierarchy | It's a one-off error case |
| Multiple callers need to handle this specific error type | You're in a small script |

### Raising Exceptions

```python
def validate_score(score):
    if not isinstance(score, (int, float)):
        raise TypeError(f"Score must be numeric, got {type(score).__name__}")
    if score < 0 or score > 100:
        raise ValueError(f"Score must be 0-100, got {score}")
    return score
```

### Exception Chaining: `raise ... from ...`

Python preserves the original cause of an exception when you chain with `from`. This gives you a full picture of what went wrong:

```python
def load_config(path):
    try:
        with open(path) as f:
            return json.load(f)
    except FileNotFoundError as e:
        # The original FileNotFoundError becomes the __cause__
        raise ConfigError(f"Config file not found: {path}") from e

# Output when run:
# json.decoder.JSONDecodeError: ... (the original cause)
# 
# The above exception was the direct cause of the following exception:
# 
# ConfigError: Config file not found: /config/settings.json
```

| Technique | What It Does |
|-----------|-------------|
| `raise NewError() from original` | Sets `__cause__`; displays "direct cause" message |
| `raise NewError()` inside `except` | Sets `__context__`; displays "during handling" message |
| `raise NewError() from None` | Suppresses the original; only the new exception is shown |

```python
# Suppress chained context for a cleaner user-facing error
try:
    int(user_input)
except ValueError:
    raise ValueError(f"Invalid input: '{user_input}'") from None
```

### Adding Notes to Exceptions (Python 3.11+)

In Python 3.11 and later, you can attach additional context to any exception without creating a subclass:

```python
def run_test(test_name, config):
    try:
        execute(test_name, config)
    except Exception as e:
        e.add_note(f"Test name: {test_name}")
        e.add_note(f"Config: {config}")
        raise   # Re-raise with notes attached

# The traceback will display the notes alongside the original error
```

### Best Practices for Custom Exceptions

```python
# 1. Always inherit from Exception (not BaseException)
class MyError(Exception):   # ✅
    pass

# 2. Give them descriptive, domain-specific names ending in 'Error'
class TestTimeoutError(Exception): pass        # ✅ Clear
class TE(Exception): pass                      # ❌ Cryptic

# 3. Provide helpful messages — include the context that matters
raise ConfigError(f"Key 'base_url' missing from config: {config}")  # ✅
raise ConfigError("Config error")                                    # ❌ Useless

# 4. Add attributes for programmatic access
class RetryableError(Exception):
    def __init__(self, message, retry_after_seconds=5):
        super().__init__(message)
        self.retry_after_seconds = retry_after_seconds

try:
    ...
except RetryableError as e:
    time.sleep(e.retry_after_seconds)   # caller can use the attribute
    retry()
```

---

## Summary

- All exceptions inherit from `BaseException`; yours should inherit from `Exception`.
- Create custom exceptions by subclassing `Exception` with descriptive names.
- **Organize** custom exceptions in a hierarchy for granular error handling.
- Add **extra attributes** to custom exceptions for useful context.
- Use custom exceptions when domain-specific errors make code more expressive.

---

## Additional Resources
- [Python Docs — Built-in Exceptions](https://docs.python.org/3/library/exceptions.html)
- [Real Python — Python Custom Exceptions](https://realpython.com/python-exceptions/#custom-exceptions)
- [PEP 3134 — Exception Chaining](https://peps.python.org/pep-3134/)
