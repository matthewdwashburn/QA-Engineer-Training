# Python `map()` — Transforming Every Element

## Learning Objectives
- Understand what `map()` does and how it differs from a `for` loop.
- Apply `map()` with both lambda functions and named functions.
- Recognise that `map()` is lazy and know when to materialise it with `list()`.
- Choose between `map()` and a list comprehension for a given situation.

---

## Why This Matters

> **Weekly Epic Connection:** In QA and data engineering, you frequently need to transform every item in a collection — convert raw timestamps to readable strings, normalise test scores, format API responses, or extract a single field from a list of records. `map()` expresses that intent in a single line without a loop, making your pipeline-style code cleaner and easier to reason about.

---

## The Concept

`map()` applies a function to **every element** of an iterable and returns an **iterator** of the results. The original iterable is never modified.

**Syntax:**
```python
map(function, iterable)
map(function, iterable1, iterable2)   # Multiple iterables
```

The return value is a `map` object (a lazy iterator). Wrap it in `list()` to get all results at once.

---

### Basic Usage

```python
numbers = [1, 2, 3, 4, 5]

# Using a lambda
doubled = list(map(lambda x: x * 2, numbers))
print(doubled)   # [2, 4, 6, 8, 10]

# Using a named function — often more readable for complex logic
def to_celsius(fahrenheit):
    return round((fahrenheit - 32) * 5 / 9, 1)

temps_f = [32, 68, 100, 212]
temps_c = list(map(to_celsius, temps_f))
print(temps_c)   # [0.0, 20.0, 37.8, 100.0]
```

---

### `map()` with Multiple Iterables

When you pass two iterables, `map()` calls the function with one element from each, stopping at the shortest:

```python
prices   = [10.0, 25.0, 5.0]
tax_rate = [0.1,  0.08, 0.15]

total_prices = list(map(lambda p, t: round(p + p * t, 2), prices, tax_rate))
print(total_prices)  # [11.0, 27.0, 5.75]
```

---

### `map()` is Lazy

`map()` does **not** process any elements until you ask for them. This is called lazy evaluation — it is memory-efficient for large datasets.

```python
numbers = range(1_000_000)

# Nothing is computed yet — map_obj is just a plan
map_obj = map(lambda x: x * 2, numbers)

# Only the first 5 elements are computed
print(next(map_obj))   # 0
print(next(map_obj))   # 2
print(next(map_obj))   # 4

# Materialise all at once — only do this when you need the full list
all_doubled = list(map(lambda x: x * 2, range(10)))
```

> **Tip:** If you only need to iterate once (e.g., inside a `for` loop or passing to `reduce()`), leave the result as an iterator. Only call `list()` when you need random access or multiple passes.

---

### QA Use Case — Transforming Test Records

```python
test_results = [
    {"name": "test_login",    "duration_ms": 1200, "status": "pass"},
    {"name": "test_checkout", "duration_ms": 3500, "status": "fail"},
    {"name": "test_search",   "duration_ms": 850,  "status": "pass"},
]

# Extract just the test names
names = list(map(lambda r: r["name"], test_results))
print(names)   # ['test_login', 'test_checkout', 'test_search']

# Format each result as a status badge string
badges = list(map(
    lambda r: f"{'✅' if r['status'] == 'pass' else '❌'} {r['name']} ({r['duration_ms']}ms)",
    test_results
))
for badge in badges:
    print(badge)
# ✅ test_login (1200ms)
# ❌ test_checkout (3500ms)
# ✅ test_search (850ms)
```

---

### `map()` vs List Comprehension

Both produce the same result. The choice is a matter of style and context.

```python
numbers = [1, 2, 3, 4, 5]

# map() — idiomatic when passing an existing named function
squared_map  = list(map(lambda x: x ** 2, numbers))

# List comprehension — often more readable for simple transforms
squared_comp = [x ** 2 for x in numbers]

print(squared_map == squared_comp)   # True
```

| Prefer `map()` when… | Prefer comprehension when… |
|----------------------|---------------------------|
| Passing an already-defined named function | The transform is a short inline expression |
| Feeding into another functional call (`reduce`, `zip`) | You need a conditional in the transform |
| Working in a functional pipeline style | Readability for the team matters most |

### `itertools.starmap` — map with Argument Unpacking

When each element of the iterable is a tuple of arguments to unpack, use `itertools.starmap`:

```python
from itertools import starmap

# Each element is a (base, exponent) pair
pairs = [(2, 10), (3, 5), (10, 3)]

# starmap unpacks each tuple as separate arguments to pow()
results = list(starmap(pow, pairs))
# [1024, 243, 1000]   — pow(2,10), pow(3,5), pow(10,3)

# Equivalent without starmap
results = list(map(lambda pair: pow(*pair), pairs))
```

### Chaining `map()` into a Pipeline

`map()` returns a lazy iterator, making it easy to chain with `filter()` and `reduce()`:

```python
from functools import reduce

test_results = [
    {"name": "test_login",    "status": "pass", "duration_ms": 1200},
    {"name": "test_checkout", "status": "fail", "duration_ms": 3500},
    {"name": "test_search",   "status": "pass", "duration_ms": 850},
]

# Pipeline: average duration of passed tests (in seconds)
pass_durations = map(
    lambda r: r["duration_ms"] / 1000,           # 2. Convert ms → seconds
    filter(lambda r: r["status"] == "pass",        # 1. Keep only passed tests
           test_results)
)
pass_list = list(pass_durations)   # [1.2, 0.85]
average = sum(pass_list) / len(pass_list)
print(f"Average pass duration: {average:.2f}s")   # Average pass duration: 1.02s
```

---

## Summary

- `map(func, iterable)` applies `func` to **every element** and returns a lazy iterator.
- Wrap in `list()` to materialise results; leave as iterator when chaining with other functions.
- Pass a **lambda** for short inline transforms; pass a **named function** for clarity on complex logic.
- `map()` with **multiple iterables** applies the function element-by-element across all sequences.
- For simple cases, **list comprehensions are often more Pythonic** — choose based on readability and context.

---

## Additional Resources
- [Python Docs — map()](https://docs.python.org/3/library/functions.html#map)
- [Real Python — Python's map() Function](https://realpython.com/python-map-function/)
- [PEP 279 — The enumerate() and map() Built-ins](https://peps.python.org/pep-0279/)
