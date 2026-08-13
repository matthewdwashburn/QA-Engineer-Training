# Comments, Modules, and Imports

## Learning Objectives
- Write effective single-line and multi-line comments.
- Create Python modules and understand the module system.
- Use `import` statements and understand the `__name__` variable.

---

## Why This Matters

> **Weekly Epic Connection:** As your Python projects grow beyond a single file, you'll need to organize code into modules and import functionality between them. Comments and documentation make your code understandable to teammates — and to your future self. These are skills you'll use in every project throughout this training.

---

## The Concept

### Comments

Comments are notes in your code that Python ignores. They exist solely for human readers.

#### Single-Line Comments

Use the `#` symbol. Everything after `#` on that line is a comment:

```python
# This is a full-line comment
count = 0  # This is an inline comment

# Calculate the total with tax
total = price * (1 + TAX_RATE)
```

#### Multi-Line Comments

Python doesn't have a dedicated multi-line comment syntax. Two approaches:

**Approach 1: Multiple `#` lines (preferred)**
```python
# This function validates user credentials
# against the database and returns True if
# the credentials are valid.
def validate_user(username, password):
    pass
```

**Approach 2: Triple-quoted strings (docstrings)**
```python
"""
This is technically a string literal, not a comment.
But when not assigned to a variable, Python discards it.
Use this sparingly for comments — reserve triple quotes for docstrings.
"""
```

#### Comment Best Practices

```python
# ❌ BAD: States the obvious
x = x + 1  # Increment x by 1

# ✅ GOOD: Explains WHY
x = x + 1  # Account for the header row in the CSV

# ❌ BAD: Outdated comment
# Calculate discount (10%)
total = price * 0.85  # Comment says 10% but code does 15%!

# ✅ GOOD: No comment needed — code is self-documenting
discount_rate = 0.15
total = price * (1 - discount_rate)
```

**Rules of thumb:**
- Comment the **why**, not the **what**. Code shows what; comments explain why.
- Keep comments up to date — outdated comments are worse than no comments.
- If you need a comment to explain *what* the code does, consider rewriting the code to be clearer.

### Docstrings

**Docstrings** are special string literals placed as the first statement in a function, class, or module. They serve as documentation:

```python
def calculate_tax(price, rate=0.08):
    """Calculate tax amount for a given price.

    Args:
        price: The base price before tax.
        rate: The tax rate as a decimal (default: 0.08).

    Returns:
        The calculated tax amount.
    """
    return price * rate
```

Access docstrings programmatically:
```python
>>> help(calculate_tax)
>>> print(calculate_tax.__doc__)
```

---

### Modules

A **module** is simply a Python file (`.py`). Every `.py` file you create is a module.

#### Creating a Module

```python
# math_helpers.py — This IS a module

PI = 3.14159

def circle_area(radius):
    """Calculate the area of a circle."""
    return PI * radius ** 2

def circle_circumference(radius):
    """Calculate the circumference of a circle."""
    return 2 * PI * radius
```

#### Using a Module (Importing)

From another file in the same directory:

```python
# main.py
import math_helpers

area = math_helpers.circle_area(5)
print(f"Area: {area}")
```

### Import Styles

```python
# Style 1: Import the entire module
import math_helpers
math_helpers.circle_area(5)      # Must use the module prefix

# Style 2: Import specific items
from math_helpers import circle_area, PI
circle_area(5)                    # No prefix needed
print(PI)

# Style 3: Import with an alias
import math_helpers as mh
mh.circle_area(5)

# Style 4: Import everything (⚠️ avoid this!)
from math_helpers import *       # Pollutes your namespace — hard to trace origins
```

**Best practice:** Use Style 1 or Style 2. Style 4 (`import *`) makes it unclear where names come from and can cause naming collisions.

### The Standard Library

Python ships with a massive **standard library** — hundreds of modules ready to use without installation:

```python
import os           # Operating system interface
import sys          # System-specific parameters
import json         # JSON encoding/decoding
import datetime     # Date and time handling
import random       # Random number generation
import math         # Mathematical functions
import pathlib      # Object-oriented filesystem paths
import csv          # CSV file reading/writing
import re           # Regular expressions
```

```python
# Examples
import random
print(random.randint(1, 100))    # Random number between 1-100

import datetime
print(datetime.datetime.now())    # Current date and time

import os
print(os.getcwd())               # Current working directory
```

### The `__name__` Variable

Every Python module has a special built-in variable called `__name__`:

- When a file is **run directly** (`python myfile.py`), `__name__` is set to `"__main__"`.
- When a file is **imported** by another file, `__name__` is set to the module's name (e.g., `"myfile"`).

This lets you write code that behaves differently depending on whether it's run directly or imported:

```python
# greetings.py

def say_hello(name):
    return f"Hello, {name}!"

def say_goodbye(name):
    return f"Goodbye, {name}!"

# This block only runs when the file is executed directly
if __name__ == "__main__":
    # Test code / demo code goes here
    print(say_hello("World"))
    print(say_goodbye("World"))
```

```bash
# Run directly — the __main__ block executes
python greetings.py
# Output:
# Hello, World!
# Goodbye, World!
```

```python
# Import from another file — the __main__ block does NOT execute
from greetings import say_hello
print(say_hello("Alice"))
# Output: Hello, Alice!
# (No "Goodbye" line — the __main__ block was skipped)
```

**Why this matters:** The `if __name__ == "__main__":` pattern lets you:
- Include test/demo code in a module without it running when imported.
- Create files that work both as importable libraries AND standalone scripts.
- Follow the professional Python convention used in virtually every project.

### Module Search Path

When you write `import something`, Python looks for the module in this order:

1. **Current directory** (the directory of the script that's running).
2. **PYTHONPATH** (environment variable, if set).
3. **Standard library** directories.
4. **Site-packages** (third-party packages installed via pip).

```python
import sys
print(sys.path)  # Shows the full search path
```

### Packages (Preview)

When you have multiple related modules, you organize them into a **package** — a directory with an `__init__.py` file:

```
my_test_framework/
├── __init__.py          # Makes this directory a package
├── assertions.py        # Module 1
├── reporters.py         # Module 2
└── utils/
    ├── __init__.py      # Sub-package
    └── helpers.py       # Module in sub-package
```

```python
from my_test_framework import assertions
from my_test_framework.utils import helpers
```

---

## Summary

- **Comments** (`#`) explain *why*, not *what* — keep them up to date.
- **Docstrings** (triple-quoted) document functions, classes, and modules — accessible via `help()`.
- **Modules** are `.py` files — every Python file is a module.
- **Import styles:** `import module`, `from module import item`, `import module as alias`.
- **`__name__`:** equals `"__main__"` when run directly, equals the module name when imported.
- The `if __name__ == "__main__":` pattern is essential professional Python practice.

---

## Additional Resources
- [Python Docs — Modules](https://docs.python.org/3/tutorial/modules.html)
- [PEP 257 — Docstring Conventions](https://peps.python.org/pep-0257/)
- [Real Python — Python Modules and Packages](https://realpython.com/python-modules-packages/)
