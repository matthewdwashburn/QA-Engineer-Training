# Identifiers and Keywords

## Learning Objectives
- Define what identifiers are and follow Python's naming rules.
- List Python's reserved keywords and understand why they can't be used as names.
- Apply PEP 8 naming conventions for variables, functions, classes, and constants.

---

## Why This Matters

> **Weekly Epic Connection:** Naming things well is one of the hardest — and most important — skills in programming. Clear, consistent names make your test code readable, maintainable, and self-documenting. Poor names lead to confusion, bugs, and frustrated teammates during code review.

---

## The Concept

### What Is an Identifier?

An **identifier** is any name you give to a variable, function, class, module, or other object in Python.

```python
username = "alice"              # variable name
def validate_email():           # function name
    pass
class TestRunner:               # class name
    pass
MAX_RETRIES = 3                 # constant name
```

### Python's Naming Rules

| Rule | Example |
|------|---------|
| Must start with a letter (a-z, A-Z) or underscore (`_`) | `name`, `_count` ✅ |
| Can contain letters, digits (0-9), and underscores | `student_1` ✅ |
| Cannot start with a digit | `1st_name` ❌ |
| Cannot contain spaces or special characters | `my name`, `total$` ❌ |
| Cannot be a reserved keyword | `class`, `for` ❌ |
| Case-sensitive | `Name`, `name`, `NAME` are three different identifiers |

### Reserved Keywords

Python has **35 keywords** as of Python 3.12 that you cannot use as identifiers:

```python
import keyword
print(keyword.kwlist)
```

| | | | | |
|---|---|---|---|---|
| `False` | `None` | `True` | `and` | `as` |
| `assert` | `async` | `await` | `break` | `class` |
| `continue` | `def` | `del` | `elif` | `else` |
| `except` | `finally` | `for` | `from` | `global` |
| `if` | `import` | `in` | `is` | `lambda` |
| `nonlocal` | `not` | `or` | `pass` | `raise` |
| `return` | `try` | `while` | `with` | `yield` |

Your editor highlights keywords, and Python raises a `SyntaxError` if you try to use one:

```python
class = "math"   # SyntaxError: invalid syntax
```

### Don't Shadow Built-in Names

Built-in names like `print`, `len`, `type`, `list`, `str`, `int` are **not** keywords — you *can* use them as variable names, but you **should not**:

```python
# ⚠️ LEGAL but DANGEROUS — shadows the built-in
list = [1, 2, 3]        # Now list() is broken!
print = "hello"         # Now print() is broken!

# Fix: delete your variable to restore the built-in
del list
```

---

## PEP 8 Naming Conventions

**PEP 8** is Python's official style guide. The community follows these conventions universally:

### Quick Reference

| Entity | Convention | Example |
|--------|-----------|---------|
| Variable | `snake_case` | `user_count` |
| Function | `snake_case` | `calculate_total()` |
| Class | `PascalCase` | `TestRunner` |
| Constant | `UPPER_SNAKE_CASE` | `MAX_RETRIES` |
| Module | `snake_case` | `test_utils.py` |
| Package | `lowercase` | `mypackage` |
| Private | `_single_leading` | `_internal_state` |
| Name-mangled | `__double_leading` | `__secret_data` |
| Dunder | `__double_both__` | `__init__()` |

### Variables and Functions: `snake_case`

```python
# ✅ Python convention
user_name = "alice"
total_count = 42

def calculate_tax(price, rate):
    return price * rate
```

```python
# ❌ Not Pythonic
userName = "alice"         # camelCase (Java/JavaScript)
UserName = "alice"         # PascalCase (C#)
```

### Classes: `PascalCase`

```python
class TestRunner:
    pass

class HttpResponseValidator:
    pass
```

### Constants: `UPPER_SNAKE_CASE`

```python
MAX_RETRIES = 3
BASE_URL = "https://api.example.com"
DEFAULT_TIMEOUT = 30
```

Python doesn't enforce true constants — `UPPER_SNAKE_CASE` is a signal to developers: "Don't change this."

### Underscore Conventions

```python
_internal_counter = 0         # Single underscore: "internal use" convention

class User:
    def __init__(self):
        self.__password = "secret"  # Double underscore: name-mangled by Python
```

## Code Example: Good vs. Bad Naming

```python
# ❌ BAD — What does this code do?
def f(x, y):
    t = x * 0.08
    r = x + t
    return r <= y

# ✅ GOOD — Self-documenting code
TAX_RATE = 0.08

def is_within_budget(price, budget):
    """Check if the price (including tax) fits within the budget."""
    tax = price * TAX_RATE
    total = price + tax
    return total <= budget
```

Both do the same thing. The second needs **zero comments** to understand.

---

## Summary

- **Identifiers** must start with a letter/underscore, can contain letters/digits/underscores, and are case-sensitive.
- **Keywords** (35 in Python 3.12) are reserved — you can't use them as names.
- **Don't shadow built-ins** (`list`, `print`, `type`) — it breaks their functionality.
- **PEP 8:** `snake_case` for variables/functions, `PascalCase` for classes, `UPPER_SNAKE_CASE` for constants.
- **Good naming is self-documenting** — choose names that communicate intent.

---

## Additional Resources
- [PEP 8 — Naming Conventions](https://peps.python.org/pep-0008/#naming-conventions)
- [Python Docs — Keywords](https://docs.python.org/3/reference/lexical_analysis.html#keywords)
- [Real Python — Python Naming Conventions](https://realpython.com/python-pep8/#naming-conventions)
