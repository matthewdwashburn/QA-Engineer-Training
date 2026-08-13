# Dunder Methods (Magic Methods)

## Learning Objectives
- Understand what dunder methods are and their role in Python's data model.
- Implement `__str__`, `__repr__`, `__eq__`, `__lt__`, `__len__`, and `__add__`.
- Use operator overloading to make custom classes work with Python operators.

---

## Why This Matters

> **Weekly Epic Connection:** Dunder methods let your classes integrate seamlessly with Python's syntax — supporting `print()`, `==`, `<`, `len()`, `+`, and more. This is what makes Python classes feel "native" and Pythonic.

---

## The Concept

### What Are Dunder Methods?

**Dunder** = "double underscore." Methods like `__init__`, `__str__`, `__eq__` are called **magic methods** or **special methods**. They let Python know how your objects should behave with built-in operations.

### `__str__` — Human-Readable Representation

Called by `print()` and `str()`:

```python
class TestResult:
    def __init__(self, name, status):
        self.name = name
        self.status = status

    def __str__(self):
        icon = "✅" if self.status == "pass" else "❌"
        return f"{icon} {self.name}"

result = TestResult("test_login", "pass")
print(result)  # ✅ test_login
```

### `__repr__` — Developer Representation

Called in the REPL and by `repr()`. Should be unambiguous and ideally recreatable:

```python
class TestResult:
    def __init__(self, name, status):
        self.name = name
        self.status = status

    def __repr__(self):
        return f"TestResult(name='{self.name}', status='{self.status}')"

result = TestResult("test_login", "pass")
print(repr(result))  # TestResult(name='test_login', status='pass')

# In the REPL:
# >>> result
# TestResult(name='test_login', status='pass')
```

**Rule of thumb:** Always implement `__repr__`. Implement `__str__` when you want a user-friendly version. If only `__repr__` is defined, `str()` falls back to it.

### `__eq__` — Equality Comparison

Called by `==`:

```python
class Point:
    def __init__(self, x, y):
        self.x = x
        self.y = y

    def __eq__(self, other):
        if not isinstance(other, Point):
            return NotImplemented
        return self.x == other.x and self.y == other.y

p1 = Point(3, 4)
p2 = Point(3, 4)
p3 = Point(1, 2)

print(p1 == p2)  # True  (without __eq__, this would be False!)
print(p1 == p3)  # False
```

### `__lt__` — Less Than (Enables Sorting)

Called by `<` and used by `sorted()`:

```python
class TestResult:
    def __init__(self, name, duration_ms):
        self.name = name
        self.duration_ms = duration_ms

    def __lt__(self, other):
        return self.duration_ms < other.duration_ms

    def __repr__(self):
        return f"{self.name}({self.duration_ms}ms)"

results = [
    TestResult("checkout", 2400),
    TestResult("login", 800),
    TestResult("profile", 1200),
]

# sorted() uses __lt__
print(sorted(results))
# [login(800ms), profile(1200ms), checkout(2400ms)]
```

### `__len__` — Length

Called by `len()`:

```python
class TestSuite:
    def __init__(self, name):
        self.name = name
        self.tests = []

    def add(self, test):
        self.tests.append(test)

    def __len__(self):
        return len(self.tests)

suite = TestSuite("Regression")
suite.add("test_login")
suite.add("test_search")
print(len(suite))  # 2
```

### `__add__` — Addition Operator

Called by `+`:

```python
class Score:
    def __init__(self, value):
        self.value = value

    def __add__(self, other):
        if isinstance(other, Score):
            return Score(self.value + other.value)
        return Score(self.value + other)

    def __repr__(self):
        return f"Score({self.value})"

s1 = Score(85)
s2 = Score(92)
print(s1 + s2)    # Score(177)
print(s1 + 10)    # Score(95)
```

### `__bool__` — Truthiness

Called by `bool()` and by `if obj:`. If not defined, Python falls back to `__len__` (falsy if length is 0):

```python
class TestSuite:
    def __init__(self, name):
        self.tests = []

    def add(self, test):
        self.tests.append(test)

    def __bool__(self):
        return len(self.tests) > 0   # Suite is truthy if it has tests

suite = TestSuite("Regression")
if not suite:
    print("No tests loaded")    # Prints this — suite is empty
suite.add("test_login")
if suite:
    print("Ready to run")       # Prints this — suite has tests
```

### `__enter__` and `__exit__` — Context Manager Protocol

These dunder methods make your class work with the `with` statement — enabling automatic setup and teardown:

```python
class DatabaseConnection:
    def __init__(self, url):
        self.url = url
        self.conn = None

    def __enter__(self):
        """Called when entering the 'with' block. Returns the resource."""
        print(f"Connecting to {self.url}")
        self.conn = connect(self.url)   # hypothetical connect function
        return self.conn

    def __exit__(self, exc_type, exc_val, exc_tb):
        """Called when exiting the 'with' block — even if an exception occurred.
        
        Arguments:
            exc_type:  The exception type, or None if no exception.
            exc_val:   The exception value, or None.
            exc_tb:    The traceback object, or None.
        
        Return True to suppress the exception; False/None to let it propagate.
        """
        print("Closing connection")
        if self.conn:
            self.conn.close()
        return False   # Don't suppress exceptions

# Usage
with DatabaseConnection("localhost:5432") as conn:
    conn.execute("SELECT * FROM tests")
# Connection is automatically closed — even if an exception occurs
```

### `__call__` — Make Objects Callable

Implement `__call__` to make instances behave like functions:

```python
class Validator:
    """A callable validator that checks if a value meets a threshold."""

    def __init__(self, min_value, max_value):
        self.min_value = min_value
        self.max_value = max_value

    def __call__(self, value):
        return self.min_value <= value <= self.max_value


is_valid_score = Validator(0, 100)

print(is_valid_score(85))   # True
print(is_valid_score(150))  # False
print(is_valid_score(-5))   # False

# Works like a function — can be passed as a callback
scores = [75, 150, 95, -5, 100]
valid_scores = list(filter(is_valid_score, scores))
# [75, 95, 100]
```

### `__iter__` and `__next__` — Iterator Protocol

Implementing these makes your class work in `for` loops and comprehensions (covered in detail in the **Iterators and Iterables** lesson):

```python
class CountDown:
    def __init__(self, start):
        self.current = start

    def __iter__(self):
        return self

    def __next__(self):
        if self.current <= 0:
            raise StopIteration
        value = self.current
        self.current -= 1
        return value

for n in CountDown(3):
    print(n, end=" ")   # 3 2 1
```

### Shortcut for Comparison Methods: `functools.total_ordering`

Implementing all six comparison dunder methods (`__lt__`, `__le__`, `__gt__`, `__ge__`, `__eq__`, `__ne__`) is tedious. `functools.total_ordering` fills in the missing ones if you define `__eq__` and any one of the ordering methods:

```python
from functools import total_ordering

@total_ordering
class TestResult:
    def __init__(self, name, duration_ms):
        self.name = name
        self.duration_ms = duration_ms

    def __eq__(self, other):
        if not isinstance(other, TestResult):
            return NotImplemented
        return self.duration_ms == other.duration_ms

    def __lt__(self, other):
        if not isinstance(other, TestResult):
            return NotImplemented
        return self.duration_ms < other.duration_ms

    def __repr__(self):
        return f"{self.name}({self.duration_ms}ms)"

results = [
    TestResult("checkout", 2400),
    TestResult("login", 800),
    TestResult("profile", 1200),
]

# ALL comparison operators work — total_ordering derived __le__, __gt__, __ge__
print(sorted(results))
# [login(800ms), profile(1200ms), checkout(2400ms)]
print(max(results))     # checkout(2400ms)
print(min(results))     # login(800ms)
```

### Common Dunder Methods Reference

| Method | Triggered By | Purpose |
|--------|-------------|---------|
| `__str__` | `print()`, `str()` | Human-readable string |
| `__repr__` | `repr()`, REPL | Developer string |
| `__eq__` | `==` | Equality comparison |
| `__ne__` | `!=` | Inequality |
| `__lt__` | `<` | Less than |
| `__le__` | `<=` | Less than or equal |
| `__gt__` | `>` | Greater than |
| `__ge__` | `>=` | Greater than or equal |
| `__len__` | `len()` | Length |
| `__add__` | `+` | Addition |
| `__bool__` | `bool()`, `if obj:` | Truthiness |
| `__contains__` | `in` | Membership test |
| `__getitem__` | `obj[key]` | Index/key access |
| `__setitem__` | `obj[key] = val` | Index/key assignment |
| `__delitem__` | `del obj[key]` | Index/key deletion |
| `__iter__` | `for x in obj:` | Return an iterator |
| `__next__` | `next(obj)` | Next value from iterator |
| `__call__` | `obj(args)` | Make object callable |
| `__enter__` | `with obj as x:` | Context manager setup |
| `__exit__` | End of `with` block | Context manager teardown |
| `__hash__` | `hash()`, dict keys | Hashing |

### Important: Don't Invent Your Own

Names with double underscores on both sides are reserved for Python. Never create your own like `__custom_method__` — use regular method names instead.

---

## Summary

- **Dunder methods** let your classes integrate with Python's operators and built-in functions.
- `__str__` for users, `__repr__` for developers — always implement `__repr__`.
- `__eq__` for `==`, `__lt__` for `<` and sorting, `__len__` for `len()`, `__add__` for `+`.
- `__bool__` controls truthiness in `if` statements; falls back to `__len__` if not defined.
- `__enter__` / `__exit__` enables the `with` statement — context managers for safe resource handling.
- `__call__` makes instances callable like functions.
- `__iter__` / `__next__` plugs your class into `for` loops and comprehensions.
- Use `functools.total_ordering` to derive all six comparison methods from just two.
- Return `NotImplemented` (not `raise`) when an operation isn't supported for a given type.
- Never invent custom dunder names — they're reserved for Python.

---

## Additional Resources
- [Python Docs — Data Model (Special Methods)](https://docs.python.org/3/reference/datamodel.html#special-method-names)
- [Real Python — Operator and Function Overloading](https://realpython.com/operator-function-overloading/)
- [Python Docs — Emulating Numeric Types](https://docs.python.org/3/reference/datamodel.html#emulating-numeric-types)
