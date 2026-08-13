# Comprehensions

## Learning Objectives
- Write list, dict, and set comprehensions using proper syntax.
- Apply filtering conditions and inline if-else transformations within comprehensions.
- Use generator expressions for memory-efficient, lazy iteration.
- Understand how nested comprehensions work and when they are appropriate.
- Recognize when to stop using comprehensions and switch back to a regular loop.

---

## Why This Matters

> **Weekly Epic Connection:** Comprehensions are the Pythonic way to transform and filter data — replacing multi-line loops with concise, readable one-liners. They are everywhere in professional Python code, from test data generation to parsing API responses and filtering result sets. A Python developer who cannot read or write comprehensions fluently is not yet working at a professional level.

---

## The Concept

### What Is a Comprehension?

A **comprehension** is a compact, declarative syntax for building a collection from an iterable. Instead of writing:

```python
# Imperative (step-by-step)
squares = []
for x in range(10):
    squares.append(x ** 2)
```

You write:

```python
# Declarative (describe the result)
squares = [x ** 2 for x in range(10)]
```

Python supports four types of comprehensions:

| Type | Syntax | Result |
|------|--------|--------|
| **List** | `[expr for x in iterable if cond]` | A new `list` |
| **Dict** | `{key: val for x in iterable if cond}` | A new `dict` |
| **Set** | `{expr for x in iterable if cond}` | A new `set` |
| **Generator** | `(expr for x in iterable if cond)` | A lazy `generator` |

---

### List Comprehension

**Syntax:** `[expression for item in iterable if condition]`

The `if condition` is optional — include it only when filtering.

```python
# Without filter — transform every element
squares = [x ** 2 for x in range(10)]
# [0, 1, 4, 9, 16, 25, 36, 49, 64, 81]

# With filter — only keep elements that satisfy the condition
even_squares = [x ** 2 for x in range(10) if x % 2 == 0]
# [0, 4, 16, 36, 64]

# Filter strings — keep only non-empty values
raw = ["login", "", "checkout", None, "profile", ""]
valid = [s for s in raw if s]      # Truthy check removes "" and None
# ['login', 'checkout', 'profile']
```

#### With an `if-else` (transform based on condition)

When you need an `if-else` inside a comprehension, it is an **expression** (not a filter) and goes *before* the `for`:

```python
# Syntax: [value_if_true if condition else value_if_false for item in iterable]
labels = ["PASS" if score >= 70 else "FAIL" for score in [85, 62, 91, 55]]
# ['PASS', 'FAIL', 'PASS', 'FAIL']

# Normalize: convert None to "N/A"
data = ["Alice", None, "Bob", None]
cleaned = [s if s is not None else "N/A" for s in data]
# ['Alice', 'N/A', 'Bob', 'N/A']
```

> **Key distinction:**
> - `[x for x in data if condition]` — the `if` after `for` is a **filter** (exclude items).
> - `[a if cond else b for x in data]` — the `if/else` before `for` is a **ternary transform** (keep all items, change their value).

---

### Dictionary Comprehension

**Syntax:** `{key_expression: value_expression for item in iterable if condition}`

```python
# Map names to their lengths
names = ["Alice", "Bob", "Charlie"]
name_lengths = {name: len(name) for name in names}
# {'Alice': 5, 'Bob': 3, 'Charlie': 7}

# Invert a dictionary (swap keys and values)
original = {"a": 1, "b": 2, "c": 3}
inverted = {v: k for k, v in original.items()}
# {1: 'a', 2: 'b', 3: 'c'}

# Filter dictionary — keep only entries with values above a threshold
scores = {"Alice": 85, "Bob": 55, "Charlie": 92, "Dana": 48}
passed = {name: score for name, score in scores.items() if score >= 70}
# {'Alice': 85, 'Charlie': 92}

# Build a lookup table from a list of records
records = [
    {"id": "TC001", "status": "pass"},
    {"id": "TC002", "status": "fail"},
    {"id": "TC003", "status": "pass"},
]
status_lookup = {r["id"]: r["status"] for r in records}
# {'TC001': 'pass', 'TC002': 'fail', 'TC003': 'pass'}
```

---

### Set Comprehension

**Syntax:** `{expression for item in iterable if condition}`

The result is a `set` — duplicate values are automatically removed.

```python
# Unique first letters from a list of names
names = ["Alice", "Anna", "Bob", "Charlie", "Carol"]
first_letters = {name[0] for name in names}
# {'A', 'B', 'C'}   — order not guaranteed

# Extract unique domains from email addresses
emails = ["user@gmail.com", "admin@company.com", "test@gmail.com"]
domains = {email.split("@")[1] for email in emails}
# {'gmail.com', 'company.com'}

# Find which status codes appeared (unique)
responses = [200, 404, 200, 500, 200, 404]
unique_codes = {code for code in responses}
# {200, 404, 500}
```

---

### Generator Expression

A generator expression uses **parentheses** instead of square brackets and produces values **lazily** — one at a time, only when requested. It does **not** build the entire collection in memory upfront.

```python
# List comprehension — builds the ENTIRE list in memory immediately
sum_list = sum([x ** 2 for x in range(1_000_000)])

# Generator expression — computes values one by one, no list stored
sum_gen = sum(x ** 2 for x in range(1_000_000))

# Same mathematical result — but the generator uses ~8 bytes vs ~8 MB
```

When a generator expression is the *only* argument to a function, you can omit the extra parentheses:

```python
# Both are equivalent
total = sum((x ** 2 for x in range(10)))
total = sum(x ** 2 for x in range(10))   # one pair of parens is enough
```

#### When to Choose Generator over List Comprehension

| Situation | Use |
|-----------|-----|
| You only need to iterate once | Generator |
| You need to index or slice the result | List |
| Memory is a concern (large datasets) | Generator |
| You need `len()` of the result | List |
| You're passing to `sum()`, `any()`, `all()`, `max()` | Generator |

---

### Using `zip()` in Comprehensions

`zip()` pairs up two or more iterables, which is frequently useful inside comprehensions:

```python
names  = ["Alice", "Bob", "Charlie"]
scores = [85, 62, 91]

# Build a list of formatted strings
results = [f"{name}: {score}" for name, score in zip(names, scores)]
# ['Alice: 85', 'Bob: 62', 'Charlie: 91']

# Build a dict from two parallel lists
result_dict = {name: score for name, score in zip(names, scores)}
# {'Alice': 85, 'Bob': 62, 'Charlie': 91}
```

---

### Nested Comprehensions

You can nest comprehensions to process multi-dimensional data. Read them **left to right, outer to inner** — the same order as nested `for` loops.

```python
# Flatten a 2D list (matrix)
matrix = [[1, 2, 3], [4, 5, 6], [7, 8, 9]]
flat = [num for row in matrix for num in row]
# [1, 2, 3, 4, 5, 6, 7, 8, 9]

# Equivalent nested for loop (read order is the same):
flat = []
for row in matrix:         # outer loop — matches "for row in matrix"
    for num in row:        # inner loop — matches "for num in row"
        flat.append(num)
```

```python
# Create a multiplication table as a list of lists
table = [[row * col for col in range(1, 6)] for row in range(1, 6)]
# [[1, 2, 3, 4, 5],
#  [2, 4, 6, 8, 10],
#  ...
#  [5, 10, 15, 20, 25]]
```

---

### Practical QA Examples

```python
# 1. Generate bulk test users
test_users = [
    {"name": f"user_{i}", "email": f"user_{i}@test.com", "id": i}
    for i in range(1, 6)
]

# 2. Filter failed tests from a result list
results = [
    {"name": "login",    "status": "pass"},
    {"name": "checkout", "status": "fail"},
    {"name": "profile",  "status": "pass"},
    {"name": "payment",  "status": "fail"},
]
failures = [r["name"] for r in results if r["status"] == "fail"]
# ['checkout', 'payment']

# 3. Count tests by status using a dict comprehension
summary = {
    status: len([r for r in results if r["status"] == status])
    for status in {"pass", "fail"}
}
# {'pass': 2, 'fail': 2}

# 4. Extract all unique error codes from a list of API responses
api_responses = [
    {"status": 200, "path": "/login"},
    {"status": 500, "path": "/checkout"},
    {"status": 200, "path": "/profile"},
    {"status": 404, "path": "/missing"},
]
error_codes = {r["status"] for r in api_responses if r["status"] >= 400}
# {404, 500}

# 5. Normalize a list of URLs (strip whitespace, lowercase)
raw_urls = ["  /Login  ", "/CHECKOUT", " /profile "]
normalized = [url.strip().lower() for url in raw_urls]
# ['/login', '/checkout', '/profile']
```

---

### Common Beginner Mistakes

```python
# ❌ Mistake 1: Confusing filter-if and ternary-if positions
# This is a filter (keeps only even numbers):
evens = [x for x in range(10) if x % 2 == 0]   # [0, 2, 4, 6, 8]

# This is a transform (replaces odds with 0):
zeroed = [x if x % 2 == 0 else 0 for x in range(10)]  # [0, 1→0, 2, 3→0, ...]

# ❌ Mistake 2: Side effects in comprehensions
# Comprehensions are for BUILDING data, not for causing side effects
results = [print(x) for x in data]   # Works but wrong — use a for loop

# ❌ Mistake 3: Nested comprehension that's unreadable
result = [transform(x) for x in data if validate(x) for y in process(x) if check(y)]

# ✅ Better — use a regular loop when logic is complex
result = []
for x in data:
    if validate(x):
        for y in process(x):
            if check(y):
                result.append(transform(x))
```

**Rule:** If a comprehension doesn't fit comfortably on one line, or if you need to pause to understand it, use a `for` loop instead. Clarity always beats cleverness.

---

### When NOT to Use Comprehensions

Use a regular `for` loop when:
- The body has **side effects** (printing, writing to a file, mutating external state).
- The logic spans **more than one meaningful conditional**.
- The comprehension requires **more than one line** to be readable.
- You need to **break** early or **skip with continue** in complex ways.
- You are **debugging** — loops let you add `print()` statements more naturally.

---

## Summary

- **List comprehension:** `[expr for x in iterable if cond]` — builds a list.
- **Dict comprehension:** `{key: val for x in iterable if cond}` — builds a dict.
- **Set comprehension:** `{expr for x in iterable if cond}` — builds a set, unique values only.
- **Generator expression:** `(expr for x in iterable if cond)` — lazy, memory-efficient, single-pass.
- `if` *after* `for` is a **filter**; `if/else` *before* `for` is a **ternary transform**.
- Use `zip()` to iterate multiple iterables in parallel inside a comprehension.
- Nested comprehensions mirror nested `for` loops — read left to right, outer to inner.
- **If it's hard to read at a glance, use a regular loop.**

---

## Additional Resources
- [Python Docs — List Comprehensions](https://docs.python.org/3/tutorial/datastructures.html#list-comprehensions)
- [Real Python — List Comprehensions](https://realpython.com/list-comprehension-python/)
- [PEP 274 — Dict Comprehensions](https://peps.python.org/pep-0274/)
- [Real Python — Generator Expressions](https://realpython.com/introduction-to-python-generators/)
