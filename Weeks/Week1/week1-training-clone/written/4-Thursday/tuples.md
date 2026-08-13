# Python Tuples

## Learning Objectives
- Understand what a tuple is and how it differs from a list.
- Recognise why immutability is a deliberate design choice.
- Use tuple packing, unpacking, and indexing effectively.
- Identify real-world scenarios where a tuple is the correct choice over a list.

---

## Why This Matters

> **Weekly Epic Connection:** In professional Python code, you will encounter functions that return multiple values (coordinates, status codes, database rows) and configuration data that must never change at runtime. Tuples are the language's built-in way of expressing "this data is fixed and intentional." Using tuples where appropriate makes your code safer, self-documenting, and slightly faster.

---

## The Concept

A **tuple** is an **ordered, immutable** sequence. Like a list, it maintains the insertion order of its elements and supports index-based access. Unlike a list, once a tuple is created, **its contents cannot be changed** — no appending, no removing, no replacing.

### Creating a Tuple

```python
# With parentheses (most common)
coordinates = (10, 20)
rgb_color   = (255, 128, 0)
empty_tuple = ()

# Without parentheses — "tuple packing"
point = 10, 20            # Still a tuple

# Single-element tuple — the trailing comma is REQUIRED
one_item = (42,)          # ✅ This is a tuple
not_a_tuple = (42)        # ❌ This is just the integer 42

# From an iterable
from_list = tuple([1, 2, 3])
```

> **Common Mistake:** Forgetting the trailing comma when creating a single-element tuple. `(42)` is an integer wrapped in parentheses, not a tuple.

### Accessing Elements

Tuples use the same **zero-based indexing** as lists, including negative indices and slicing.

```python
coordinates = (10, 20, 30)

coordinates[0]    # 10  — first element
coordinates[-1]   # 30  — last element
coordinates[1:]   # (20, 30) — slice returns a new tuple
```

### Immutability — Why It Matters

```python
config = ("localhost", 5432, "mydb")

# ❌ Attempting to modify raises a TypeError
config[0] = "production_host"
# TypeError: 'tuple' object does not support item assignment

# ❌ No methods to add or remove items
# config.append("new") → AttributeError
```

Immutability is a **feature**, not a limitation. It signals to any reader of the code:
- This data is not meant to change.
- This function's return values are a fixed group.
- This value is safe to use as a dictionary key.

### Tuple Unpacking

Unpacking lets you assign each element of a tuple to a named variable in one statement.

```python
coordinates = (10, 20)
x, y = coordinates
print(f"x={x}, y={y}")  # x=10, y=20

# Swap two variables without a temporary variable
a, b = 1, 2
a, b = b, a
print(a, b)  # 2 1

# Extended unpacking with * (star expression)
first, *rest = (1, 2, 3, 4, 5)
print(first)  # 1
print(rest)   # [2, 3, 4, 5]  — note: rest becomes a list

*head, last = (1, 2, 3, 4, 5)
print(head)   # [1, 2, 3, 4]
print(last)   # 5
```

### Functions That Return Multiple Values

Python functions that appear to return multiple values actually return a **single tuple**.

```python
def get_server_info():
    host = "localhost"
    port = 5432
    return host, port   # This IS a tuple: ("localhost", 5432)

# Unpack directly
host, port = get_server_info()
print(f"Connecting to {host}:{port}")

# Or keep as tuple
info = get_server_info()
print(info[0])  # "localhost"
```

### Common Operations

Because tuples are immutable, they expose fewer methods than lists. The available operations are:

```python
dimensions = (1920, 1080, 1920, 720)

len(dimensions)           # 4
1920 in dimensions        # True
dimensions.count(1920)    # 2  — occurrences of a value
dimensions.index(1080)    # 1  — index of first occurrence
```

### Tuples as Dictionary Keys

Lists **cannot** be used as dictionary keys because they are mutable (and therefore not hashable). Tuples can.

```python
# Store test results keyed by (module, test_name) pair
results = {}
results[("auth", "login")] = "PASSED"
results[("auth", "logout")] = "FAILED"
results[("api", "get_users")] = "PASSED"

print(results[("auth", "login")])  # PASSED
```

### Named Tuples — Self-Documenting Tuples

The `collections.namedtuple` factory creates tuple subclasses where fields have names. This dramatically improves readability.

```python
from collections import namedtuple

TestResult = namedtuple("TestResult", ["name", "status", "duration_ms"])

result = TestResult(name="login", status="PASSED", duration_ms=120)

print(result.name)        # "login"   — access by name
print(result.status)      # "PASSED"
print(result[2])          # 120       — access by index still works
print(result)             # TestResult(name='login', status='PASSED', duration_ms=120)
```

> **Note:** `namedtuple` is still a tuple — it is immutable. We will explore more flexible data containers in later weeks.

### Typed NamedTuple

Python 3.6+ introduced `typing.NamedTuple`, which gives you type annotations alongside the named fields:

```python
from typing import NamedTuple

class TestResult(NamedTuple):
    name: str
    status: str
    duration_ms: int
    error_message: str = ""   # Default value

result = TestResult(name="login", status="PASSED", duration_ms=120)
print(result.name)        # "login"
print(result.status)      # "PASSED"
print(result)             # TestResult(name='login', status='PASSED', duration_ms=120, error_message='')

# Still a tuple underneath
print(isinstance(result, tuple))  # True
print(result[2])                  # 120 — index access still works
```

> **Tuples vs. Dataclasses:** If you need immutability and minimal overhead, use `NamedTuple`. If you need mutability, methods, or richer behaviour, Python 3.7+ `dataclasses` are usually the better choice. We cover dataclasses in a later week.

### When to Use a Tuple

| Situation | Why Tuple? |
|-----------|------------|
| Function returning multiple values | Clear intent; unpack naturally at call site |
| Configuration constants | Immutability prevents accidental changes |
| Dictionary keys | Tuples are hashable; lists are not |
| Fixed records (coordinates, RGB, DB row) | Signals the data is a coherent, fixed group |
| Slight performance advantage over list | Tuple iteration is marginally faster |

### Tuple vs. List: Heterogeneous vs. Homogeneous Data

A useful mental model for choosing between tuple and list:

| Collection | Data Type Expectation | Example |
|-----------|----------------------|--------|
| **Tuple** | **Heterogeneous** — different types, each position has meaning | `("Alice", 85, True)` — (name, score, passed) |
| **List** | **Homogeneous** — same type, position doesn’t imply meaning | `[85, 92, 78, 61]` — a collection of scores |

```python
# Tuple: each position is semantically different (name, port, db)
db_config = ("localhost", 5432, "mydb")

# List: all items are the same kind of thing
test_names = ["login", "checkout", "profile"]

# A function signature hints at this too:
def connect(host, port, database):   # three distinct things → tuple natural
    ...
def run_all(tests):                   # many of the same thing → list natural
    ...
```

---

## Summary

- **Tuples** are defined with parentheses `()` (or no delimiters) and are **ordered** and **immutable**.
- A **single-element tuple requires a trailing comma**: `(42,)`.
- **Tuple unpacking** assigns multiple variables in one clean statement.
- Functions that `return x, y` are returning a tuple — unpacking is the natural way to consume them.
- Tuples can be used as **dictionary keys**; lists cannot.
- Choose a tuple when the data represents a **fixed, coherent group** that should not change.

---

## Additional Resources
- [Python Docs — Tuples and Sequences](https://docs.python.org/3/tutorial/datastructures.html#tuples-and-sequences)
- [Real Python — Lists and Tuples in Python](https://realpython.com/python-lists-tuples/)
- [Python Docs — collections.namedtuple](https://docs.python.org/3/library/collections.html#collections.namedtuple)
