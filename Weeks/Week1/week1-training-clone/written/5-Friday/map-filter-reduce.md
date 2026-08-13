# Map, Filter, and Reduce

## Learning Objectives
- Use `map()` to transform every element in a sequence.
- Use `filter()` to select elements matching a condition.
- Use `functools.reduce()` to aggregate a sequence into a single value.
- Chain functional operations into data processing pipelines.

---

## Why This Matters

> **Weekly Epic Connection:** `map`, `filter`, and `reduce` are the core tools of functional programming in Python. They let you build concise data transformation pipelines — processing test results, cleaning datasets, and computing aggregate metrics.

---

## The Concept

### Functional Programming in Python

Functional programming is a programming style centred around three ideas:

1. **Pure functions** — a function's output depends only on its inputs; it has no side effects (doesn't mutate state, print, or write to disk).
2. **Immutability** — prefer creating new data over modifying existing data.
3. **Function composition** — build complex behaviour by combining simple, single-purpose functions.

Python is not a purely functional language (it has loops, mutable state, and classes), but it borrows functional ideas heavily. `map()`, `filter()`, and `reduce()` are the three classic functional operations:

| Operation | Question it answers | Example |
|-----------|--------------------|---------|
| `map` | "What does each element become?" | Convert durations from ms to seconds |
| `filter` | "Which elements do I keep?" | Keep only failed tests |
| `reduce` | "What is the aggregate?" | Total duration of all failures |

The power is that these compose into a **data pipeline**:
```
[raw data] → filter() → map() → reduce() → [single result]
```

### `map()` — Transform Every Element

**Syntax:** `map(function, iterable)` — applies `function` to each element.

```python
numbers = [1, 2, 3, 4, 5]

# Double each number
doubled = list(map(lambda x: x * 2, numbers))
# [2, 4, 6, 8, 10]

# Using a named function
def to_celsius(fahrenheit):
    return (fahrenheit - 32) * 5/9

temps_f = [32, 68, 100, 212]
temps_c = list(map(to_celsius, temps_f))
# [0.0, 20.0, 37.78, 100.0]

# map() with multiple iterables
names = ["alice", "bob", "charlie"]
greetings = list(map(lambda n: f"Hello, {n.title()}!", names))
# ['Hello, Alice!', 'Hello, Bob!', 'Hello, Charlie!']
```

**Note:** `map()` returns an iterator (lazy) — wrap in `list()` to see all results.

### `filter()` — Select Matching Elements

**Syntax:** `filter(function, iterable)` — keeps elements where `function` returns `True`.

```python
numbers = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

# Keep only even numbers
evens = list(filter(lambda x: x % 2 == 0, numbers))
# [2, 4, 6, 8, 10]

# Filter test results
results = [
    {"name": "login", "status": "pass", "duration": 1.2},
    {"name": "checkout", "status": "fail", "duration": 3.5},
    {"name": "search", "status": "pass", "duration": 0.8},
    {"name": "profile", "status": "fail", "duration": 2.1},
]

failures = list(filter(lambda r: r["status"] == "fail", results))
# [{'name': 'checkout', ...}, {'name': 'profile', ...}]

slow_tests = list(filter(lambda r: r["duration"] > 2.0, results))
# [{'name': 'checkout', ...}, {'name': 'profile', ...}]
```

### `functools.reduce()` — Aggregate to Single Value

**Syntax:** `reduce(function, iterable, initial)` — applies a two-argument function cumulatively.

```python
from functools import reduce

numbers = [1, 2, 3, 4, 5]

# Sum (equivalent to sum())
total = reduce(lambda acc, x: acc + x, numbers)
# 15

# How it works step by step:
# Step 1: acc=1, x=2  → 3
# Step 2: acc=3, x=3  → 6
# Step 3: acc=6, x=4  → 10
# Step 4: acc=10, x=5 → 15

# Product of all numbers
product = reduce(lambda acc, x: acc * x, numbers)
# 120

# Find the maximum (equivalent to max())
maximum = reduce(lambda acc, x: acc if acc > x else x, numbers)
# 5

# With an initial value
total = reduce(lambda acc, x: acc + x, numbers, 100)
# 115 (starts from 100)
```

### Chaining Operations

Combine `map`, `filter`, and `reduce` into a processing pipeline:

```python
from functools import reduce

# Pipeline: get the total duration of failed tests
results = [
    {"name": "login", "status": "pass", "duration": 1.2},
    {"name": "checkout", "status": "fail", "duration": 3.5},
    {"name": "search", "status": "pass", "duration": 0.8},
    {"name": "profile", "status": "fail", "duration": 2.1},
]

total_fail_time = reduce(
    lambda acc, d: acc + d,                        # 3. Sum durations
    map(
        lambda r: r["duration"],                    # 2. Extract duration
        filter(
            lambda r: r["status"] == "fail",        # 1. Keep failures
            results
        )
    ),
    0  # Initial value
)
print(f"Total failure time: {total_fail_time}s")  # 5.6s
```

### Comprehension Alternatives

For `map` and `filter`, list comprehensions are often more readable:

```python
# map equivalent
doubled = [x * 2 for x in numbers]

# filter equivalent
evens = [x for x in numbers if x % 2 == 0]

# map + filter equivalent
fail_durations = [r["duration"] for r in results if r["status"] == "fail"]
```

**Rule of thumb:** Use comprehensions for simple cases. Use `map`/`filter` when passing an existing named function or when the transformation is complex.

### Beyond the Basics — `itertools`

Python's `itertools` module extends the `map`/`filter`/`reduce` toolkit with powerful, memory-efficient tools:

```python
import itertools

# itertools.starmap — like map(), but unpacks each element as *args
pairs = [(2, 5), (3, 4), (10, 2)]
powers = list(itertools.starmap(pow, pairs))
# [32, 81, 100]  — pow(2,5), pow(3,4), pow(10,2)

# itertools.filterfalse — keeps elements where predicate returns False
from itertools import filterfalse
failing = list(filterfalse(lambda r: r["status"] == "pass", results))
# Equivalent to filter(lambda r: r["status"] != "pass", results)

# itertools.takewhile — keep elements while condition holds, stop at first failure
from itertools import takewhile
increasing = list(takewhile(lambda x: x < 5, [1, 2, 3, 6, 4, 7]))
# [1, 2, 3]  — stops at 6

# itertools.chain — concatenate multiple iterables lazily
from itertools import chain
all_tests = list(chain(unit_tests, integration_tests, e2e_tests))
```

---

## Summary

- **`map(func, iterable)`** — transforms each element (returns iterator).
- **`filter(func, iterable)`** — keeps elements where `func` returns True.
- **`reduce(func, iterable, initial)`** — aggregates to a single value.
- All three return iterators (lazy evaluation) — wrap in `list()` to see results.
- **Chain** them for data processing pipelines.
- **Comprehensions** are often more Pythonic for simple cases.

---

## Additional Resources
- [Python Docs — map()](https://docs.python.org/3/library/functions.html#map)
- [Python Docs — filter()](https://docs.python.org/3/library/functions.html#filter)
- [Python Docs — functools.reduce()](https://docs.python.org/3/library/functools.html#functools.reduce)
