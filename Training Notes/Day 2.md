# Day 2 - Functions, Classes & OOP

- Python is strongly typed but type annotations are **not enforced** — they're hints only
- `Ctrl+Shift+V` previews markdown in VSCode

---

## Data Types

```python
my_int   = 1
my_float = 1.1
my_str   = "hello"
my_bool  = True       # False
my_none  = None

# Underscore separator for readability (no effect on value)
population = 8_000_000_000

# Scientific notation
scientific = 2.5e6    # 2500000.0

# Python has no int overflow — big numbers just work
big = 2 ** 100

# Type casting
int("42")     # 42
float("3.14") # 3.14
str(99)       # "99"
type(x)       # returns the type of x
```

**Falsy values:** `0`, `0.0`, `""`, `[]`, `{}`, `None`, `False` — everything else is truthy.

**`None` checks:** always use `is None`, not `== None`.

**Float precision gotcha:** `0.1 + 0.2 != 0.3` — this is normal CPU float behavior.
Fix: `abs(result - 0.3) < 1e-9`

**`bool` is a subclass of `int`:** `True + True == 2`

---

## Strings

```python
name = "Wil"
f"Hello {name}"           # f-string (preferred)
"Hello {}".format(name)   # older style

# f-string formatting
f"{1200:,}"       # "1,200"    — thousands separator
f"{1.2:.2f}"      # "1.20"     — 2 decimal places
f"{0.856:.1%}"    # "85.6%"    — percentage
f"{name:>20}"     # right-align in 20 chars
f"{name:<20}"     # left-align
f"{name:^20}"     # center
f"{42:05d}"       # "00042"    — zero-pad

s = "Hello Wil"
s[0:5]      # "Hello"    — slicing [start:end]
s[6:]       # "Wil"      — from index to end
s[0:-2]     # "Hello W"  — negative index = from end
s[0::2]     # every other letter
s[::-1]     # reversed

# String methods
s.upper()           # "HELLO WIL"
s.lower()           # "hello wil"
s.title()           # "Hello Wil"
s.split()           # ["Hello", "Wil"]  — splits on whitespace by default
" ".join(["a","b"]) # "a b"
len(s)              # 9
```

Strings are **immutable** — you can't change a character in place, you create a new string.

```python
import re
match = re.search(r'\d+', "Order ID: 12345")
match.group()                    # "12345"
re.fullmatch(r'^\w+$', name)     # full string must match pattern
```

---

## Operators

```python
a, b = 17, 5
a + b    # 22  addition
a - b    # 12  subtraction
a * b    # 85  multiplication
a / b    # 3.4 division — always returns float
a // b   # 3   floor division — truncates to int
a % b    # 2   modulo (remainder)
a ** b   # exponentiation

# Comparison
5 == 5.0   # True  — value equality
5 is 5.0   # False — identity (same object in memory?)

# Logical
True and False   # False
True or False    # True
not True         # False
```

---

## Functions

```python
def my_func(p1: str, p2: int) -> str:   # type annotations, not enforced
    pass

def variadic(*args):        # args is a tuple
    for item in args: ...

def kw_func(**kwargs):      # kwargs is a dict
    for key, value in kwargs.items(): ...

# passing a function as an argument
def runner(func):
    return func()
```

**Curried function** — a function that returns another function, called in two steps:
```python
from typing import Callable

def add(n: int) -> Callable:
    def inner(x):
        return n + x
    return inner

add(5)(3)   # 8
```

**Argument unpacking** — spread a list/tuple into positional args with `*`:
```python
results = [r1, r2, r3]
analyze_results(*results)   # same as analyze_results(r1, r2, r3)
```

---

## Classes

```python
class MyClass:
    class_var = 0                          # shared across all instances

    def __init__(self, name="default"):    # constructor, only one allowed
        self.name = name                   # instance variable

    def __str__(self):                     # human-readable — controls print(obj) and str(obj)
        return f"name: {self.name}"

    def __repr__(self):                    # developer-readable — should recreate the object
        return f"MyClass('{self.name}')"   # i.e. you could copy-paste this and get the same object

    @classmethod
    def class_method(cls):                 # receives the class as first arg — can read/write class state
        return cls.class_var

    @staticmethod
    def static_method():                   # no access to class or instance — just a namespaced utility
        return "utility function"
```

**Method type comparison:**

| Type | First arg | Can access | Use when |
|---|---|---|---|
| Regular method | `self` (instance) | instance + class state | behaviour tied to a specific object |
| `@classmethod` | `cls` (class) | class state only | factory methods, shared counters |
| `@staticmethod` | nothing | neither | utility logic that just lives on the class |

### Abstract Classes

```python
from abc import ABC, abstractmethod

class MyAbstract(ABC):
    @abstractmethod
    def must_implement(self):              # child classes must define this
        pass
```

### Inheritance

```python
class Child(Parent):
    def __init__(self, age, name):
        super().__init__(name)             # call parent constructor
        self.age = age
```

---

## QA-Relevant Patterns

```python
# Default params + kwargs override pattern
def build_config(**settings):
    defaults = {"browser": "chrome", "timeout": 30}
    for key, value in settings.items():
        if key in defaults:
            defaults[key] = value
    return defaults

# Returning multiple values as a tuple (unpack on use)
def analyze():
    return passed, failed, rate, avg

passed, failed, rate, avg = analyze()

# Safe dict access with a default (no KeyError)
value = my_dict.get("key", False)

# Format a test name from free text
"test_" + "_".join(name.lower().split())   # "Valid Login" → "test_valid_login"
```

---

## Assert

Silent on pass, raises `AssertionError` on fail — used for quick inline testing.

```python
assert add(5)(3) == 8       # nothing printed if correct
assert add(5)(3) == 99      # AssertionError
```

---

## Useful Built-ins

| Function | What it does |
|---|---|
| `type(x)` | returns the type of x |
| `len(x)` | length of string, list, etc. |
| `int(x)` / `float(x)` / `str(x)` | type casting |
| `min(iterable)` / `max(iterable)` | smallest / largest value |
| `reversed(iterable)` | iterate in reverse (returns iterator, not list) |
| `input(prompt)` | reads a string from stdin |
| `isinstance(x, type)` | check if x is an instance of type |

---

## `if __name__ == "__main__":`

Code inside this block only runs when the file is executed directly, not when it's imported as a module.

```python
def main():
    ...

if __name__ == "__main__":
    main()
```
