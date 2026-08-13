# zip()

## Learning Objectives
- Explain what `zip()` is, what it returns, and how it works internally as a lazy iterator.
- Combine two or more iterables element-wise using `zip()`.
- Handle unequal-length iterables with `zip_longest` and Python 3.10's `strict=` mode.
- Unzip sequences using the `*` unpacking operator.
- Apply `zip()` in practical patterns: dict creation, parallel iteration, matrix transpose, and sliding windows.

---

## Why This Matters

> **Weekly Epic Connection:** `zip()` is one of Python's most useful built-in functions for pairing related data — combining test names with results, iterating over parallel sequences, or restructuring tabular data. Understanding it fully opens up a wide range of clean, readable data manipulation patterns.

---

## The Concept

### What `zip()` Is

`zip()` takes two or more iterables and combines them **element by element** into tuples. It returns a **lazy iterator** (a `zip` object), not a list — elements are only paired when consumed.

**Signature:**
```python
zip(*iterables)
```

Think of it like a physical zipper on a jacket: it joins two separate strips tooth-by-tooth until one strip runs out.

```python
names  = ["Alice", "Bob", "Charlie"]
scores = [85, 92, 78]

# Returns a zip iterator — nothing is materialized yet
zip_obj = zip(names, scores)
print(zip_obj)          # <zip object at 0x...>
print(type(zip_obj))    # <class 'zip'>

# Materialize by passing to list()
paired = list(zip_obj)
print(paired)
# [('Alice', 85), ('Bob', 92), ('Charlie', 78)]
```

> **Lazy evaluation:** Just like `map()` and `filter()`, `zip()` computes pairs only when asked. You can iterate it with `for`, pass it to `dict()`, or consume it with `next()` — without ever building the full list in memory.

---

### Basic Usage — Parallel Iteration

The most common pattern: iterate over two related sequences at the same time:

```python
names  = ["Alice", "Bob", "Charlie"]
scores = [85, 92, 78]

# Unpack each pair directly in the for loop
for name, score in zip(names, scores):
    print(f"{name}: {score}")
# Alice: 85
# Bob: 92
# Charlie: 78

# Compare: without zip — error-prone index access
for i in range(len(names)):
    print(f"{names[i]}: {scores[i]}")   # Equivalent but less readable
```

---

### More Than Two Iterables

`zip()` accepts any number of iterables and produces n-tuples:

```python
names  = ["Alice", "Bob", "Charlie"]
scores = [85, 92, 78]
grades = ["B", "A+", "C"]
passed = [True, True, False]

for name, score, grade, ok in zip(names, scores, grades, passed):
    status = "✅" if ok else "❌"
    print(f"{status} {name}: {score} ({grade})")
# ✅ Alice: 85 (B)
# ✅ Bob: 92 (A+)
# ❌ Charlie: 78 (C)
```

---

### Unequal Lengths — zip Stops at the Shortest

By default, `zip()` stops as soon as the **shortest** iterable is exhausted. Extra elements from longer iterables are silently discarded:

```python
a = [1, 2, 3, 4, 5]
b = ["x", "y", "z"]

result = list(zip(a, b))
# [(1, 'x'), (2, 'y'), (3, 'z')]   — stops at length of b (3)
# Elements 4 and 5 from 'a' are dropped!
```

> **Watch out:** Silent data loss can be a subtle bug. If you expect both sequences to have the same length, enforce it with `strict=True` (Python 3.10+).

---

### Python 3.10+ — `strict=` Mode

The `strict=True` keyword argument raises a `ValueError` if the iterables have different lengths — turning a silent bug into a loud, visible error:

```python
names  = ["Alice", "Bob", "Charlie"]
scores = [85, 92]         # One item short!

# Without strict — silently drops "Charlie"
list(zip(names, scores))
# [('Alice', 85), ('Bob', 92)]

# With strict — raises immediately
list(zip(names, scores, strict=True))
# ValueError: zip() has arguments with different lengths
```

Use `strict=True` whenever the two sequences **must** be the same length (e.g., pairing test IDs with results from a known-good run).

---

### `zip_longest` — Include All Elements

`itertools.zip_longest` keeps pairing until the **longest** iterable is exhausted, filling missing values with `fillvalue`:

```python
from itertools import zip_longest

a = [1, 2, 3, 4, 5]
b = ["x", "y", "z"]

result = list(zip_longest(a, b, fillvalue="N/A"))
# [(1, 'x'), (2, 'y'), (3, 'z'), (4, 'N/A'), (5, 'N/A')]
```

**When to use which:**

| Scenario | Use |
|----------|-----|
| Sequences must have equal length | `zip(..., strict=True)` |
| Stop at shortest (default) | `zip()` |
| Process all, fill missing values | `zip_longest(..., fillvalue=...)` |

---

### Unzipping with `*`

`zip()` can also **reverse** itself — turning a list of tuples back into separate sequences. This works by unpacking with `*`:

```python
pairs = [("Alice", 85), ("Bob", 92), ("Charlie", 78)]

# Unpack the list of pairs, then zip pairs them back up the other way
names, scores = zip(*pairs)

print(names)    # ('Alice', 'Bob', 'Charlie')   — tuple, not list
print(scores)   # (85, 92, 78)
```

**How it works:** `zip(*pairs)` is equivalent to `zip(("Alice", 85), ("Bob", 92), ("Charlie", 78))` — it groups the first elements, then the second elements, etc.

---

### Practical Use Cases

#### 1. Creating a Dictionary from Two Lists

```python
keys   = ["name", "email", "role"]
values = ["Alice", "alice@example.com", "tester"]

user_dict = dict(zip(keys, values))
# {'name': 'Alice', 'email': 'alice@example.com', 'role': 'tester'}
```

#### 2. Comparing Expected vs. Actual Results

```python
expected = [200, 200, 404, 200]
actual   = [200, 500, 404, 200]

for i, (exp, act) in enumerate(zip(expected, actual), start=1):
    status = "✅" if exp == act else "❌"
    print(f"Test {i}: {status}  expected={exp}, actual={act}")
# Test 1: ✅  expected=200, actual=200
# Test 2: ❌  expected=200, actual=500
# Test 3: ✅  expected=404, actual=404
# Test 4: ✅  expected=200, actual=200
```

#### 3. Transposing a Matrix (Rows ↔ Columns)

```python
matrix = [
    [1, 2, 3],
    [4, 5, 6],
    [7, 8, 9],
]

# zip(*matrix) groups column-by-column
transposed = list(zip(*matrix))
# [(1, 4, 7), (2, 5, 8), (3, 6, 9)]

# To get lists instead of tuples
transposed_lists = [list(row) for row in zip(*matrix)]
```

#### 4. Pairwise / Sliding Window

Combine `zip` with itself to create overlapping consecutive pairs:

```python
values = [10, 20, 30, 40, 50]

# Pairwise: each element with its successor
pairs = list(zip(values, values[1:]))
# [(10, 20), (20, 30), (30, 40), (40, 50)]

# Calculate differences between consecutive values
diffs = [b - a for a, b in zip(values, values[1:])]
# [10, 10, 10, 10]
```

#### 5. `zip()` with `enumerate()`

Combine `zip` and `enumerate` for indexed parallel iteration:

```python
names  = ["Alice", "Bob", "Charlie"]
scores = [85, 92, 78]

for rank, (name, score) in enumerate(zip(names, scores), start=1):
    print(f"#{rank}: {name} — {score}")
# #1: Alice — 85
# #2: Bob — 92
# #3: Charlie — 78
```

#### 6. Building a Lookup Table from Records

```python
test_ids = ["TC001", "TC002", "TC003"]
statuses = ["pass", "fail", "pass"]

# Create a status lookup in one expression
lookup = dict(zip(test_ids, statuses))
# {'TC001': 'pass', 'TC002': 'fail', 'TC003': 'pass'}

# Check a specific test
print(lookup.get("TC002", "not run"))   # "fail"
```

---

### `zip()` as a Lazy Iterator

Since `zip()` is lazy, it is memory-efficient for large sequences:

```python
import sys

a = range(1_000_000)
b = range(1_000_000)

# zip object uses negligible memory — elements computed on demand
z = zip(a, b)
print(sys.getsizeof(z))   # ~56 bytes (just the iterator object)

# Consuming one at a time — never builds the full list
print(next(z))   # (0, 0)
print(next(z))   # (1, 1)
```

---

## Summary

- **`zip(a, b, ...)`** combines iterables element-wise into tuples and returns a **lazy iterator**.
- Stops at the **shortest** iterable by default — use `strict=True` (Python 3.10+) to raise on length mismatch.
- Use `itertools.zip_longest(fillvalue=...)` to include all elements from the longest iterable.
- **Unzip** with `zip(*paired)` — groups first elements together, second elements together, etc.
- Create dicts from two lists: `dict(zip(keys, values))`.
- Transpose matrices: `list(zip(*matrix))`.
- Pairwise windows: `zip(seq, seq[1:])`.
- Combine with `enumerate()` for indexed parallel iteration.

---

## Additional Resources
- [Python Docs — zip()](https://docs.python.org/3/library/functions.html#zip)
- [Python Docs — itertools.zip_longest](https://docs.python.org/3/library/itertools.html#itertools.zip_longest)
- [Real Python — Python zip() Function](https://realpython.com/python-zip-function/)
