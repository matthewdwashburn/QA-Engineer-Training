# Type Casting

## Learning Objectives
- Understand how Python's type system distinguishes between implicit and explicit conversion.
- Use `int()`, `float()`, `str()`, `bool()`, `list()`, `tuple()`, `set()`, and `bytes()` for type casting.
- Apply safe conversion patterns to handle invalid input without crashing.
- Recognize common type casting pitfalls and how to avoid them.

---

## Why This Matters

> **Weekly Epic Connection:** When processing test data — CSV values, API responses, user inputs — you constantly need to convert between types. A response status code may arrive as the string `"200"` that must be compared as an integer. A CSV column holding a price arrives as text; your calculation needs a `float`. Type casting is the everyday bridge between raw data and usable values.

---

## The Concept

### How Python's Type System Works

Python is **strongly and dynamically typed**:

- **Strongly typed** — Python will *not* silently coerce incompatible types. `"5" + 3` raises a `TypeError`, unlike JavaScript which would produce `"53"`.
- **Dynamically typed** — variables themselves have no type; only the *values* they hold have types. You can rebind a variable to a different type at any time.

```python
x = 42        # x holds an int
x = "hello"   # x now holds a str — perfectly legal
x = [1, 2]    # x now holds a list
```

Every value has a type that you can inspect:

```python
print(type(42))          # <class 'int'>
print(type(3.14))        # <class 'float'>
print(type("hello"))     # <class 'str'>
print(type(True))        # <class 'bool'>
print(type([1, 2]))      # <class 'list'>
print(type(None))        # <class 'NoneType'>
```

---

### Implicit Type Conversion

Python automatically converts types in certain arithmetic situations, always promoting to the **wider** or **more precise** type:

```python
# int + float → float
result = 5 + 3.14
print(result)        # 8.14
print(type(result))  # <class 'float'>

# bool is a subclass of int: True == 1, False == 0
result = True + 5
print(result)        # 6

# bool + float → float
result = True + 2.5
print(result)        # 3.5
```

Python **refuses** to implicitly convert between strings and numbers — a design choice that prevents hidden bugs:

```python
# ❌ TypeError — Python will not guess your intent
"Score: " + 42         # TypeError: can only concatenate str (not "int") to str
"5" + 3                # TypeError: can only concatenate str (not "int") to str

# ✅ You must be explicit
"Score: " + str(42)    # "Score: 42"
int("5") + 3           # 8
```

---

### Explicit Type Conversion

Use Python's built-in constructor functions to convert values between types.

#### `int()` — Convert to Integer

```python
int("42")          # 42   — numeric string to int
int("  99  ")      # 99   — strips surrounding whitespace
int(3.99)          # 3    — truncates toward zero (does NOT round!)
int(-3.99)         # -3   — also truncates toward zero
int(True)          # 1
int(False)         # 0

# Base conversion — parse a string in a non-decimal base
int("0b1010", 2)   # 10  — binary
int("0xFF", 16)    # 255 — hexadecimal
int("0o17", 8)     # 15  — octal

# ❌ Raises ValueError
int("hello")       # not a number
int("3.14")        # has a decimal point — use float() first

# ✅ Correct two-step conversion
int(float("3.14")) # 3
```

> **Critical Note:** `int()` *truncates*, it does not round. `int(9.9)` gives `9`, not `10`. Use `round()` if you need rounding: `round(9.9)` → `10`.

#### `float()` — Convert to Float

```python
float("3.14")      # 3.14
float("  2.5  ")   # 2.5   — whitespace stripped
float(42)          # 42.0
float("inf")       # inf   — positive infinity
float("-inf")      # -inf  — negative infinity
float("nan")       # nan   — not a number (result of undefined operations)

import math
math.isnan(float("nan"))   # True
math.isinf(float("inf"))   # True
```

#### `str()` — Convert to String

`str()` works on virtually every Python object because every object has a `__str__` method:

```python
str(42)            # "42"
str(3.14)          # "3.14"
str(True)          # "True"
str(False)         # "False"
str(None)          # "None"
str([1, 2, 3])     # "[1, 2, 3]"
str({"a": 1})      # "{'a': 1}"
str((1, 2))        # "(1, 2)"
```

> **Note:** `str()` is different from `repr()`. For most simple types they look the same, but for strings: `str("hi")` → `hi` while `repr("hi")` → `'hi'` (with quotes).

#### `bool()` — Convert to Boolean (Truthiness)

Every Python value has a boolean interpretation. The following values are **falsy** — everything else is **truthy**:

| Value | `bool()` Result |
|-------|-----------------|
| `0` | `False` |
| `0.0` | `False` |
| `""` (empty string) | `False` |
| `[]` (empty list) | `False` |
| `()` (empty tuple) | `False` |
| `{}` (empty dict or set) | `False` |
| `set()` | `False` |
| `None` | `False` |
| Everything else | `True` |

```python
bool(0)        # False
bool(1)        # True
bool(-42)      # True (any non-zero number)
bool("")       # False
bool("hello")  # True
bool([])       # False
bool([0])      # True  ← non-empty list, even if it only contains 0!
bool(None)     # False
```

Python uses truthiness implicitly in `if` statements, so you rarely need to call `bool()` directly:

```python
results = []

# Idiomatic Python
if results:
    process(results)

# Equivalent but verbose
if bool(results) == True:
    process(results)
```

---

### Casting to Collection Types

#### `list()`, `tuple()`, `set()` — Convert Between Collections

Any **iterable** can be converted to a list, tuple, or set:

```python
# From range
list(range(5))           # [0, 1, 2, 3, 4]
tuple(range(5))          # (0, 1, 2, 3, 4)
set(range(5))            # {0, 1, 2, 3, 4}

# From string — iterates character by character
list("hello")            # ['h', 'e', 'l', 'l', 'o']
set("hello")             # {'h', 'e', 'l', 'o'} — unique chars

# Between collection types
original_list = [1, 2, 2, 3, 3, 3]
as_set   = set(original_list)    # {1, 2, 3}  — removes duplicates
as_tuple = tuple(original_list)  # (1, 2, 2, 3, 3, 3)
back_to_list = list(as_set)      # [1, 2, 3]  — order not guaranteed

# De-duplicate a list while preserving order (common pattern)
seen = set()
deduped = [x for x in original_list if not (x in seen or seen.add(x))]
# [1, 2, 3]
```

#### `dict()` — Convert to Dictionary

```python
# From a list of key-value pairs
pairs = [("host", "localhost"), ("port", 5432)]
config = dict(pairs)
# {'host': 'localhost', 'port': 5432}

# From keyword arguments
config = dict(host="localhost", port=5432)
# {'host': 'localhost', 'port': 5432}

# From two parallel lists using zip
keys   = ["name", "status", "duration"]
values = ["login", "pass", 120]
result = dict(zip(keys, values))
# {'name': 'login', 'status': 'pass', 'duration': 120}
```

---

### Practical Patterns

#### Pattern 1 — API Response Processing

```python
# HTTP response headers come as strings
headers = {"Content-Length": "1024", "X-Retry-Count": "3"}

content_length = int(headers["Content-Length"])   # 1024
retry_count    = int(headers["X-Retry-Count"])    # 3
```

#### Pattern 2 — CSV Data Processing

```python
# CSV rows are always strings
row = ["Alice", "85", "3.9", "True"]

name    = row[0]             # "Alice" — already a string
score   = int(row[1])        # 85
gpa     = float(row[2])      # 3.9
passed  = row[3] == "True"   # True  ← don't use bool("True") — it's always True!
```

> **Common Trap:** `bool("False")` returns `True` because the string `"False"` is non-empty. Always compare strings directly: `row[3] == "True"`.

#### Pattern 3 — Safe Conversion with Error Handling

When input is untrusted, wrap conversions in `try/except`:

```python
def safe_int(value, default=0):
    """Convert value to int, returning default if conversion fails."""
    try:
        return int(value)
    except (ValueError, TypeError):
        return default

safe_int("42")      # 42
safe_int("hello")   # 0
safe_int(None)      # 0
safe_int(3.9)       # 3

def safe_float(value, default=0.0):
    try:
        return float(value)
    except (ValueError, TypeError):
        return default
```

---

### Common Pitfalls Summary

| Pitfall | Wrong | Right |
|---------|-------|-------|
| Parsing a float string as int | `int("3.14")` → `ValueError` | `int(float("3.14"))` → `3` |
| `int()` rounds | `int(9.9)` → `10` ✗ | `int(9.9)` → `9` (truncates) |
| `bool()` on a string | `bool("False")` → `True` | `value == "True"` |
| Empty dict vs empty set | `{}` → dict | `set()` → empty set |
| Losing order in set | `list(set([3,1,2]))` may not be `[3,1,2]` | Use sorted if order matters |

---

## Summary

- Python is **strongly typed** — it will not silently coerce incompatible types.
- **Implicit conversion:** Python auto-promotes `int` → `float` in mixed arithmetic; `bool` is a subclass of `int`.
- **Explicit conversion:** Use `int()`, `float()`, `str()`, `bool()`, `list()`, `tuple()`, `set()`, `dict()` to convert manually.
- `int()` **truncates** floats — it does not round. Use `round()` for rounding.
- Invalid conversions raise `ValueError` — always wrap in `try/except` for untrusted input.
- `bool("False")` is `True` — compare strings directly rather than casting them.

---

## Additional Resources
- [Python Docs — Built-in Functions](https://docs.python.org/3/library/functions.html)
- [Real Python — Type Conversion](https://realpython.com/python-data-types/#type-conversion)
- [Python Docs — Numeric Types](https://docs.python.org/3/library/stdtypes.html#numeric-types-int-float-complex)
- [Real Python — Truthy and Falsy Values](https://realpython.com/python-boolean/)
