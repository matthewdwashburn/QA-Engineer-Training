# Function Parameters

## Learning Objectives
- Use positional, keyword, and default parameters.
- Understand `*args` and `**kwargs` for variable-length arguments.
- Choose the right parameter type for different situations.

---

## Why This Matters

> **Weekly Epic Connection:** Real-world functions need flexibility. A test runner might accept a variable number of test names. A configuration loader might accept optional overrides. Understanding Python's parameter system lets you write functions that are both powerful and easy to use.

---

## The Concept

### Positional Parameters

The simplest form — arguments are matched by **position**:

```python
def create_user(name, email, role):
    return {"name": name, "email": email, "role": role}

# Arguments matched left to right
user = create_user("Alice", "alice@example.com", "tester")
```

### Keyword Arguments

When calling a function, you can specify arguments by **name**:

```python
# Using keyword arguments — order doesn't matter
user = create_user(
    role="tester",
    email="alice@example.com",
    name="Alice"
)
```

**Mixing positional and keyword:**
```python
# Positional first, then keyword
user = create_user("Alice", role="tester", email="alice@example.com")

# ❌ Positional AFTER keyword is a SyntaxError
# user = create_user(name="Alice", "alice@example.com", "tester")
```

### Default Parameters

Give parameters a default value — they become **optional** when calling:

```python
def create_user(name, email, role="viewer", active=True):
    return {
        "name": name,
        "email": email,
        "role": role,
        "active": active
    }

# Use defaults
user1 = create_user("Alice", "alice@example.com")
# {"name": "Alice", "email": "...", "role": "viewer", "active": True}

# Override defaults
user2 = create_user("Bob", "bob@example.com", role="admin", active=False)
```

**⚠️ Mutable default argument trap:**

```python
# ❌ DANGEROUS — the list is shared across all calls!
def add_item(item, items=[]):
    items.append(item)
    return items

print(add_item("a"))  # ['a']
print(add_item("b"))  # ['a', 'b'] — NOT ['b']!

# ✅ CORRECT — use None as default
def add_item(item, items=None):
    if items is None:
        items = []
    items.append(item)
    return items
```

This is one of Python's most common gotchas. Always use `None` as the default for mutable types (lists, dicts, sets).

### `*args` — Variable Positional Arguments

Accept any number of positional arguments. They're collected into a **tuple**:

```python
def calculate_average(*scores):
    """Calculate the average of any number of scores."""
    if not scores:
        return 0
    return sum(scores) / len(scores)

print(calculate_average(85, 92, 78))          # 85.0
print(calculate_average(90, 95, 88, 76, 82))  # 86.2
print(calculate_average(100))                  # 100.0
```

**Mixing `*args` with regular parameters:**

```python
def log_message(level, *messages):
    """Log one or more messages at a given level."""
    for msg in messages:
        print(f"[{level}] {msg}")

log_message("INFO", "Server started", "Listening on port 8080")
# [INFO] Server started
# [INFO] Listening on port 8080
```

### `**kwargs` — Variable Keyword Arguments

Accept any number of keyword arguments. They're collected into a **dictionary**:

```python
def create_config(**settings):
    """Create a configuration from keyword arguments."""
    config = {"debug": False, "verbose": False}  # defaults
    config.update(settings)
    return config

config = create_config(debug=True, timeout=30, retries=5)
# {"debug": True, "verbose": False, "timeout": 30, "retries": 5}
```

### Combining All Parameter Types

The order must be:
1. Positional parameters
2. `*args`
3. Keyword parameters with defaults
4. `**kwargs`

```python
def api_request(method, url, *path_params, timeout=30, **headers):
    print(f"{method} {url}")
    print(f"Path params: {path_params}")
    print(f"Timeout: {timeout}")
    print(f"Headers: {headers}")

api_request(
    "GET",
    "/api/users",
    "v2", "active",
    timeout=60,
    Authorization="Bearer token123",
    Accept="application/json"
)
```

### Keyword-Only Arguments

Arguments after `*` in the function signature must be passed as keywords:

```python
def connect(host, port, *, ssl=False, timeout=30):
    """ssl and timeout MUST be passed as keyword arguments."""
    print(f"Connecting to {host}:{port} (ssl={ssl}, timeout={timeout})")

connect("localhost", 5432, ssl=True)          # ✅
# connect("localhost", 5432, True)            # ❌ TypeError
```

### Positional-Only Arguments (Python 3.8+)

Arguments before `/` must be passed positionally:

```python
def power(base, exp, /):
    """base and exp MUST be passed as positional arguments."""
    return base ** exp

power(2, 10)              # ✅ → 1024
# power(base=2, exp=10)   # ❌ TypeError
```

---

## Summary

| Parameter Type | Syntax | Use Case |
|---------------|--------|----------|
| **Positional** | `def f(a, b)` | Required arguments in specific order |
| **Default** | `def f(a, b=10)` | Optional arguments with fallback values |
| **`*args`** | `def f(*args)` | Accept any number of positional arguments |
| **`**kwargs`** | `def f(**kwargs)` | Accept any number of keyword arguments |
| **Keyword-only** | `def f(*, key)` | Force callers to use keyword syntax |
| **Positional-only** | `def f(a, /)` | Force callers to use positional syntax |

- Default parameters go **after** required ones.
- **Never use mutable defaults** (lists, dicts) — use `None` instead.
- `*args` produces a tuple; `**kwargs` produces a dictionary.

---

## Additional Resources
- [Python Docs — More on Defining Functions](https://docs.python.org/3/tutorial/controlflow.html#more-on-defining-functions)
- [Real Python — *args and **kwargs](https://realpython.com/python-kwargs-and-args/)
- [PEP 3102 — Keyword-Only Arguments](https://peps.python.org/pep-3102/)
