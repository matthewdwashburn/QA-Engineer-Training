# Decorators

## Learning Objectives
- Understand the decorator pattern and how it works.
- Write decorators using `@` syntax and `functools.wraps`.
- Create parameterized decorators and stack multiple decorators.

---

## Why This Matters

> **Weekly Epic Connection:** Decorators are one of Python's most powerful features — and a fitting capstone for Week 1. They combine everything you've learned: functions as first-class objects, closures, `*args`/`**kwargs`, and clean syntax. Decorators are everywhere in professional Python: `@pytest.fixture`, `@app.route`, `@property`, `@staticmethod`.

---

## The Concept

### What Is a Decorator?

A **decorator** is a function that wraps another function, adding behaviour before and/or after it — *without modifying the original function's code*.

To understand decorators fully, you need one prerequisite concept: **closures**.

### Closures — The Building Block

A **closure** is an inner function that remembers variables from its enclosing (outer) function's scope, even after the outer function has returned:

```python
def make_multiplier(factor):
    """Outer function — creates and returns an inner function."""
    def multiply(x):             # Inner function
        return x * factor        # 'factor' is a free variable — captured from outer scope
    return multiply              # Return the function object, not its result

double = make_multiplier(2)      # 'double' is now the inner 'multiply' function
triple = make_multiplier(3)      # Each call creates a separate closure

print(double(5))   # 10   — factor=2 is still accessible
print(triple(5))   # 15   — factor=3 is still accessible
print(double(10))  # 20
```

Decorators exploit exactly this pattern: the decorator is the outer function, the `wrapper` is the inner function (the closure), and the wrapped function is the free variable captured by the closure.

### The Pattern Without `@` Syntax

```python
def log_calls(func):
    """Decorator that logs when a function is called."""
    def wrapper(*args, **kwargs):
        print(f"📞 Calling {func.__name__}...")
        result = func(*args, **kwargs)
        print(f"✅ {func.__name__} returned {result}")
        return result
    return wrapper

# Apply the decorator manually
def add(a, b):
    return a + b

add = log_calls(add)     # Wrap the function
add(3, 5)
# 📞 Calling add...
# ✅ add returned 8
```

### The `@` Syntax (Syntactic Sugar)

The `@` symbol is shorthand for the manual wrapping above:

```python
@log_calls       # Equivalent to: add = log_calls(add)
def add(a, b):
    return a + b

add(3, 5)
# 📞 Calling add...
# ✅ add returned 8
```

### `functools.wraps` — Preserving Metadata

Without `functools.wraps`, the original function's name and docstring are lost:

```python
from functools import wraps

def log_calls(func):
    @wraps(func)          # Preserves __name__, __doc__, etc.
    def wrapper(*args, **kwargs):
        print(f"Calling {func.__name__}...")
        return func(*args, **kwargs)
    return wrapper

@log_calls
def add(a, b):
    """Add two numbers."""
    return a + b

print(add.__name__)   # "add" (without @wraps, this would be "wrapper")
print(add.__doc__)    # "Add two numbers."
```

**Always use `@wraps`** — it's a best practice that prevents debugging headaches.

### Practical Decorator Examples

#### Timing Decorator

```python
import time
from functools import wraps

def timer(func):
    """Measure and print execution time."""
    @wraps(func)
    def wrapper(*args, **kwargs):
        start = time.perf_counter()
        result = func(*args, **kwargs)
        elapsed = time.perf_counter() - start
        print(f"⏱️ {func.__name__} took {elapsed:.4f}s")
        return result
    return wrapper

@timer
def slow_function():
    time.sleep(1)
    return "done"

slow_function()
# ⏱️ slow_function took 1.0012s
```

#### Retry Decorator

```python
import time
from functools import wraps

def retry(max_attempts=3, delay=1):
    """Retry a function on exception."""
    def decorator(func):
        @wraps(func)
        def wrapper(*args, **kwargs):
            for attempt in range(1, max_attempts + 1):
                try:
                    return func(*args, **kwargs)
                except Exception as e:
                    if attempt == max_attempts:
                        raise
                    print(f"Attempt {attempt} failed: {e}. Retrying...")
                    time.sleep(delay)
        return wrapper
    return decorator

@retry(max_attempts=3, delay=0.5)
def unstable_api_call():
    """Simulates an unreliable API."""
    import random
    if random.random() < 0.7:
        raise ConnectionError("Server unavailable")
    return {"status": "ok"}
```

#### Authentication Decorator

```python
from functools import wraps

def require_auth(func):
    """Ensure user is authenticated before running function."""
    @wraps(func)
    def wrapper(user, *args, **kwargs):
        if not user.get("authenticated"):
            raise PermissionError(f"User '{user.get('name')}' is not authenticated")
        return func(user, *args, **kwargs)
    return wrapper

@require_auth
def view_dashboard(user):
    return f"Welcome to the dashboard, {user['name']}!"

# Usage
admin = {"name": "Alice", "authenticated": True}
guest = {"name": "Bob", "authenticated": False}

print(view_dashboard(admin))  # Welcome to the dashboard, Alice!
# view_dashboard(guest)       # Raises PermissionError
```

### Parameterized Decorators

When a decorator needs arguments, you add an **extra layer of nesting**:

```python
def repeat(times):
    """Decorator factory — returns a decorator that repeats the function."""
    def decorator(func):
        @wraps(func)
        def wrapper(*args, **kwargs):
            for _ in range(times):
                result = func(*args, **kwargs)
            return result
        return wrapper
    return decorator

@repeat(times=3)
def say_hello():
    print("Hello!")

say_hello()
# Hello!
# Hello!
# Hello!
```

### Stacking Decorators

Multiple decorators apply bottom to top:

```python
@timer           # Applied second (outer)
@log_calls       # Applied first (inner)
def compute(x):
    return x ** 2

# Equivalent to: compute = timer(log_calls(compute))
```

### Built-in Decorators in Depth

#### `@property` — Computed Attributes

`@property` turns a method into an attribute-like accessor, with optional setter and deleter:

```python
class TestSuite:
    def __init__(self, name):
        self.name = name
        self._tests = []

    @property
    def test_count(self):
        """Computed attribute — accessed like an attribute, not a method call."""
        return len(self._tests)

    @property
    def pass_rate(self):
        if not self._tests:
            return 0.0
        passed = sum(1 for t in self._tests if t["status"] == "pass")
        return passed / len(self._tests) * 100

    @pass_rate.setter
    def pass_rate(self, value):
        raise AttributeError("pass_rate is computed and cannot be set directly")

suite = TestSuite("Regression")
suite._tests = [
    {"name": "login", "status": "pass"},
    {"name": "checkout", "status": "fail"},
    {"name": "profile", "status": "pass"},
]

print(suite.test_count)   # 3   — called as attribute, not suite.test_count()
print(suite.pass_rate)    # 66.67
```

#### `@staticmethod` and `@classmethod`

| Decorator | First argument | When to use |
|-----------|---------------|-------------|
| Normal method | `self` (instance) | Needs access to instance attributes |
| `@staticmethod` | None | Utility function logically grouped in the class |
| `@classmethod` | `cls` (the class itself) | Alternative constructors, factory methods |

```python
class TestResult:
    PASS_THRESHOLD = 70

    def __init__(self, name, score):
        self.name  = name
        self.score = score

    def is_passing(self):
        """Normal method — accesses instance via self."""
        return self.score >= TestResult.PASS_THRESHOLD

    @staticmethod
    def format_score(score):
        """Static method — no self, no cls. Pure utility."""
        return f"{score:.1f}%"

    @classmethod
    def from_dict(cls, data):
        """Class method — alternative constructor using a dict."""
        return cls(name=data["name"], score=data["score"])

# Static method — callable on class or instance, no instance needed
print(TestResult.format_score(85.5))   # "85.5%"

# Class method — creates an instance from a different data format
result = TestResult.from_dict({"name": "login", "score": 88})
print(result.is_passing())   # True
```

#### `@functools.lru_cache` — Memoization

`lru_cache` caches function results so repeated calls with the same arguments are served from cache:

```python
from functools import lru_cache
import time

@lru_cache(maxsize=128)   # Cache up to 128 distinct argument combinations
def expensive_lookup(user_id: int) -> dict:
    """Simulate a slow database lookup."""
    time.sleep(1)   # Simulates network/DB latency
    return {"id": user_id, "name": f"user_{user_id}"}

expensive_lookup(42)   # Takes 1 second
expensive_lookup(42)   # Instant — served from cache
expensive_lookup(99)   # Takes 1 second (new argument)
expensive_lookup(99)   # Instant — served from cache

# Inspect the cache
print(expensive_lookup.cache_info())
# CacheInfo(hits=2, misses=2, maxsize=128, currsize=2)

# Clear the cache
expensive_lookup.cache_clear()
```

> **Note:** `lru_cache` requires function arguments to be **hashable** (no lists, dicts as arguments). Use `functools.cache` (Python 3.9+) for an unbounded cache.

### Decorators You'll See in the Real World

| Decorator | From | Purpose |
|-----------|------|---------|
| `@property` | Built-in | Computed attribute with optional setter |
| `@staticmethod` | Built-in | Method that doesn't access instance or class |
| `@classmethod` | Built-in | Method that receives the class as first argument |
| `@pytest.fixture` | pytest | Set up test dependencies |
| `@pytest.mark.parametrize` | pytest | Run test with multiple inputs |
| `@app.route("/path")` | Flask | Map URL to function |
| `@functools.lru_cache` | functools | Cache function results (memoize) |

---

## Summary

- **Decorators** wrap functions to add behavior without modifying the original code.
- Use `@decorator_name` syntax — it's shorthand for `func = decorator(func)`.
- **Always use `@functools.wraps`** to preserve the original function's metadata.
- **Common patterns:** timing, logging, retrying, authentication, caching.
- **Parameterized decorators** add an extra layer: decorator factory → decorator → wrapper.
- **Stacking:** Multiple decorators apply bottom-up.

---

## Additional Resources
- [Python Docs — Decorators](https://docs.python.org/3/glossary.html#term-decorator)
- [Real Python — Primer on Python Decorators](https://realpython.com/primer-on-python-decorators/)
- [PEP 318 — Decorators for Functions and Methods](https://peps.python.org/pep-0318/)
