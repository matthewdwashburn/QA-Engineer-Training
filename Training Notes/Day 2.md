# Functions, Classes & OOP

- Python is strongly typed but type annotations are **not enforced** — they're hints only
- `Ctrl+Shift+V` previews markdown in VSCode

---

## Strings

```python
name = "Wil"
f"Hello {name}"           # f-string (preferred)
"Hello {}".format(name)   # older style

s = "Hello Wil"
s[0:5]      # "Hello"    — slicing [start:end]
s[6:]       # "Wil"      — from index to end
s[0:-2]     # "Hello W"  — negative index = from end
s[0::2]     # every other letter
s[::-1]     # reversed
```

```python
import re
match = re.search(r'\d+', "Order ID: 12345")
match.group()       # "12345"
re.fullmatch(r'^\w+$', name)  # full string must match
```

---

## Functions

```python
def my_func(p1: str, p2: int) -> str:   # type annotations, not enforced
    pass

def variadic(*args):        # args is a tuple
    for item in args: ...

def kw_func(**kwargs):      # kwargs is a dict
    kwargs["key"]

# passing a function as an argument
def runner(func):
    return func()
```

---

## Classes

```python
class MyClass:
    class_var = 0                          # shared across all instances

    def __init__(self, name="default"):    # constructor, only one allowed
        self.name = name                   # instance variable

    def __str__(self):                     # controls print(obj)
        return f"name: {self.name}"

    def __repr__(self):                    # should return something that recreates the object
        return f"MyClass('{self.name}')"

    @classmethod
    def class_method(cls):                 # receives the class, not an instance
        return cls.class_var

    @staticmethod
    def static_method():                   # no access to class or instance
        return "utility function"
```

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

# f-string number formatting
f"{1200:,}"       # "1,200"   — thousands separator
f"{1.2:.2f}"      # "1.20"    — 2 decimal places
```

---

## Assert

Silent on pass, raises `AssertionError` on fail — used for quick inline testing.

```python
assert add(5)(3) == 8       # nothing printed if correct
assert add(5)(3) == 99      # AssertionError
```
