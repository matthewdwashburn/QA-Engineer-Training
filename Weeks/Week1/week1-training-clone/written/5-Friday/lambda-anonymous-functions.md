# Lambda (Anonymous Functions)

## Learning Objectives
- Understand what "first-class functions" means and why it enables lambdas.
- Write lambda expressions using correct syntax.
- Identify lambda use cases, limitations, and common pitfalls.
- Choose appropriately between `lambda`, `def`, and the `operator` module.

---

## Why This Matters

> **Weekly Epic Connection:** Lambdas are small, throwaway functions used as arguments to higher-order functions like `sorted()`, `map()`, and `filter()`. They sit at the intersection of Python's functional programming features and everyday scripting. Understanding their power — and their limits — is essential for writing clean, idiomatic Python.

---

## The Concept

### First-Class Functions

Before understanding lambdas, you need to understand **first-class functions**: in Python, functions are objects, just like integers or strings. This means you can:

- **Assign** a function to a variable.
- **Pass** a function as an argument to another function.
- **Return** a function from a function.
- **Store** functions in lists, dicts, or sets.

```python
def greet(name):
    return f"Hello, {name}!"

# Assign to a variable
say_hello = greet
print(say_hello("Alice"))   # "Hello, Alice!"

# Pass as an argument
def apply(func, value):
    return func(value)

print(apply(greet, "Bob"))  # "Hello, Bob!"

# Store in a list
operations = [str.upper, str.lower, str.title]
for op in operations:
    print(op("hello world"))
# HELLO WORLD
# hello world
# Hello World
```

Functions that accept or return other functions are called **higher-order functions**. `sorted()`, `map()`, `filter()`, `max()`, and `min()` are all higher-order functions — they accept a function as an argument. This is exactly where lambdas shine.

---

### Lambda Syntax

A lambda is an **anonymous function** — a function without a `def` statement or a name. It is defined inline using the `lambda` keyword.

**Syntax:** `lambda parameters: expression`

```python
# Regular named function
def add(a, b):
    return a + b

# Equivalent lambda — no name, no return keyword, no def
add_lambda = lambda a, b: a + b

# Both behave identically
add(3, 5)         # 8
add_lambda(3, 5)  # 8
```

Key rules:
- No `def`, no function name, no `return` keyword.
- The body is a **single expression** — not a statement block.
- The expression is automatically returned.
- Can accept zero or more parameters (including `*args` and `**kwargs`).

```python
# Zero parameters
get_pi = lambda: 3.14159

# One parameter
square = lambda x: x ** 2

# Multiple parameters
hypotenuse = lambda a, b: (a**2 + b**2) ** 0.5

# Default parameter
greet = lambda name, greeting="Hello": f"{greeting}, {name}!"
print(greet("Alice"))           # "Hello, Alice!"
print(greet("Bob", "Hi"))       # "Hi, Bob!"

# *args
add_all = lambda *args: sum(args)
print(add_all(1, 2, 3, 4))      # 10
```

---

### Common Use Cases

#### 1. Sorting with a Custom Key

The most common use for lambdas — providing a transformation function to `sorted()`, `min()`, or `max()`:

```python
users = [
    {"name": "Charlie", "age": 25, "score": 88},
    {"name": "Alice",   "age": 30, "score": 95},
    {"name": "Bob",     "age": 22, "score": 73},
]

# Sort by age (ascending)
by_age = sorted(users, key=lambda u: u["age"])

# Sort by score (descending)
by_score = sorted(users, key=lambda u: u["score"], reverse=True)

# Sort by name length, then alphabetically
by_name = sorted(users, key=lambda u: (len(u["name"]), u["name"]))

# Find user with highest score
top_scorer = max(users, key=lambda u: u["score"])
# {'name': 'Alice', 'age': 30, 'score': 95}
```

#### 2. Quick Inline Transformations

```python
# Conditional ternary expression
grade = lambda score: "PASS" if score >= 70 else "FAIL"
print(grade(85))   # "PASS"
print(grade(55))   # "FAIL"

# Clamping a value to a range
clamp = lambda x, lo, hi: max(lo, min(x, hi))
print(clamp(150, 0, 100))  # 100
print(clamp(-5, 0, 100))   # 0
print(clamp(75, 0, 100))   # 75
```

#### 3. As Arguments to Higher-Order Functions

```python
numbers = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

# map() — transform every element
doubled = list(map(lambda x: x * 2, numbers))
# [2, 4, 6, 8, 10, 12, 14, 16, 18, 20]

# filter() — keep matching elements
evens = list(filter(lambda x: x % 2 == 0, numbers))
# [2, 4, 6, 8, 10]

# sorted() + lambda combination
words = ["banana", "apple", "cherry", "date"]
by_last_char = sorted(words, key=lambda w: w[-1])
# ['banana', 'apple', 'date', 'cherry']
```

#### 4. Dispatch Tables (Dict of Functions)

```python
# Map operation names to functions — avoids lengthy if/elif chains
operations = {
    "add":      lambda a, b: a + b,
    "subtract": lambda a, b: a - b,
    "multiply": lambda a, b: a * b,
    "divide":   lambda a, b: a / b if b != 0 else None,
}

op = "multiply"
result = operations[op](6, 7)
print(f"{op}(6, 7) = {result}")  # multiply(6, 7) = 42
```

---

### The `operator` Module — Named Alternatives to Lambdas

Many common lambdas have pre-built equivalents in the `operator` module. Using them is faster (C implementation) and more readable:

```python
import operator

# Instead of: lambda x, y: x + y
operator.add(3, 5)           # 8

# Instead of: lambda x: x["age"]
from operator import itemgetter
users_sorted = sorted(users, key=itemgetter("age"))  # ✅ Clearer than lambda

# Instead of: lambda obj: obj.name
from operator import attrgetter
# sorted(objects, key=attrgetter("name"))

# Instead of: lambda x: x * 2
from operator import mul
from functools import partial
double = partial(mul, 2)
list(map(double, [1, 2, 3]))   # [2, 4, 6]
```

| Lambda | `operator` equivalent |
|--------|----------------------|
| `lambda x, y: x + y` | `operator.add` |
| `lambda x, y: x * y` | `operator.mul` |
| `lambda d: d["key"]` | `operator.itemgetter("key")` |
| `lambda obj: obj.attr` | `operator.attrgetter("attr")` |
| `lambda f, *args: f(*args)` | `operator.methodcaller(...)` |

---

### Limitations

Lambdas can only contain a **single expression**. They cannot include:

| Not allowed | Reason | Alternative |
|-------------|--------|-------------|
| Multiple statements | No statement separator | Use `def` |
| Assignments (`x = ...`) | Assignment is a statement | Use `def` |
| `return` keyword | Expression is implicitly returned | Omit it |
| `for` / `while` loops | Statements | Use a comprehension inside the lambda, or `def` |
| `try-except` blocks | Statements | Use `def` |
| `if` block (only ternary) | Full `if` is a statement | Use ternary: `a if cond else b` |

```python
# ❌ Not valid lambdas
# lambda x: x = x + 1          — assignment
# lambda x: for i in x: ...    — loop
# lambda x: try: int(x)        — try-except
# lambda x: return x + 1       — return keyword
```

---

### Common Pitfall: Default Argument Capture

A classic Python gotcha: lambda (and `def`) capture variable values **at call time**, not at definition time — unless you use a default argument:

```python
# ❌ Bug: all lambdas capture the same final value of i
functions = [lambda x: x + i for i in range(5)]
print(functions[0](10))  # 14 — not 10! (i = 4 at call time)
print(functions[2](10))  # 14 — same problem
print(functions[4](10))  # 14 — only this one is "correct" by accident

# ✅ Fix: bind the current value using a default argument
functions = [lambda x, i=i: x + i for i in range(5)]
print(functions[0](10))  # 10 (i=0 captured correctly)
print(functions[2](10))  # 12 (i=2 captured correctly)
print(functions[4](10))  # 14 (i=4 captured correctly)
```

The `i=i` default argument evaluates `i` **at definition time** and stores it as the default value.

---

### When to Use `lambda` vs. `def`

| Use `lambda` when... | Use `def` when... |
|---|---|
| The function is a simple, one-line expression | Logic requires multiple lines |
| Used as a `key=` argument to `sorted()`, `max()`, etc. | The function needs a descriptive name for clarity |
| Used once as an argument to `map()` / `filter()` | You'll call it from multiple places |
| The reader can understand it at a glance | A named function makes intent obvious |

```python
# ✅ Lambda is perfectly fine — simple, used once, obvious
sorted(tests, key=lambda t: t["duration_ms"])

# ✅ Lambda acceptable — but borderline
normalize = lambda s: s.strip().lower().replace(" ", "_")

# ❌ Lambda is too complex — use def
process = lambda x: x.strip().lower().replace(" ", "_") if x else "unknown"

# ✅ Named function is clearly better
def normalize_name(name: str) -> str:
    """Normalize a name to lowercase with underscores."""
    if not name:
        return "unknown"
    return name.strip().lower().replace(" ", "_")
```

> **PEP 8 says:** "Always use a `def` statement instead of an assignment statement that binds a lambda expression to an identifier." — i.e., `f = lambda x: x` should be `def f(x): return x`. Use lambda **only** when it's passed directly as an argument.

---

## Summary

- **First-class functions** — Python functions are objects; they can be passed, returned, and stored.
- **Lambda syntax:** `lambda params: expression` — anonymous, single-expression, no `def`, no `return`.
- Best for **short, throwaway key functions** used with `sorted()`, `max()`, `map()`, `filter()`.
- The `operator` module (`itemgetter`, `attrgetter`) often replaces lambdas more readably.
- **Pitfall:** Loop variables in lambdas are late-bound — use a default argument `lambda x, i=i: ...` to capture early.
- **Use `def`** when the function is complex, reusable, or benefits from a descriptive name.
- **PEP 8:** Don't assign a lambda to a variable — that's what `def` is for.

---

## Additional Resources
- [Python Docs — Lambda Expressions](https://docs.python.org/3/tutorial/controlflow.html#lambda-expressions)
- [Real Python — Python Lambda](https://realpython.com/python-lambda/)
- [PEP 8 — Lambda Assignment](https://peps.python.org/pep-0008/#programming-recommendations)
- [Python Docs — operator module](https://docs.python.org/3/library/operator.html)
