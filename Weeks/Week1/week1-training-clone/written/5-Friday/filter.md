# Python `filter()` — Selecting Elements That Match a Condition

## Learning Objectives
- Understand what `filter()` does and the role of the predicate function.
- Apply `filter()` with both lambda functions and named functions.
- Recognise the special behaviour of `filter(None, iterable)`.
- Choose between `filter()` and a list comprehension for a given situation.

---

## Why This Matters

> **Weekly Epic Connection:** Every QA pipeline involves selection — isolating failing tests from passing ones, finding slow API calls above a threshold, or stripping null values from a dataset. `filter()` expresses "keep only the items that match this rule" in a single declaration. It makes your intent explicit and your code easier to audit.

---

## The Concept

`filter()` applies a **predicate function** (one that returns `True` or `False`) to each element of an iterable and returns an **iterator** of only the elements for which the predicate returns `True`.

**Syntax:**
```python
filter(function, iterable)
```

Like `map()`, the return value is a lazy `filter` object. Wrap it in `list()` to get all matching elements.

---

### Basic Usage

```python
numbers = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

# Keep only even numbers
evens = list(filter(lambda x: x % 2 == 0, numbers))
print(evens)   # [2, 4, 6, 8, 10]

# Keep only numbers greater than 5
large = list(filter(lambda x: x > 5, numbers))
print(large)   # [6, 7, 8, 9, 10]
```

---

### Using a Named Function as the Predicate

For complex conditions, a named function is clearer than a long lambda:

```python
def is_valid_username(name: str) -> bool:
    """A username is valid if it is 3–20 chars, alphanumeric, and starts with a letter."""
    return (
        3 <= len(name) <= 20
        and name.isalnum()
        and name[0].isalpha()
    )

candidates = ["alice", "bo", "charlie99", "123invalid", "dave_k", "eve"]
valid = list(filter(is_valid_username, candidates))
print(valid)   # ['alice', 'charlie99', 'eve']
```

---

### `filter(None, iterable)` — Remove Falsy Values

Passing `None` as the function removes all **falsy** values (`False`, `0`, `""`, `None`, `[]`, etc.):

```python
mixed = [0, 1, "", "hello", None, [], [1, 2], False, True]

cleaned = list(filter(None, mixed))
print(cleaned)   # [1, 'hello', [1, 2], True]
```

This is a concise idiom for stripping empty/null values from a dataset.

---

### `filter()` is Lazy

Like `map()`, `filter()` evaluates lazily — elements are only tested when consumed:

```python
numbers = range(1_000_000)

# No filtering happens yet
filter_obj = filter(lambda x: x % 2 == 0, numbers)

# Only computed on demand
print(next(filter_obj))   # 0
print(next(filter_obj))   # 2
print(next(filter_obj))   # 4
```

---

### QA Use Cases

#### Isolating Failed Tests

```python
test_results = [
    {"name": "test_login",    "status": "pass", "duration_ms": 1200},
    {"name": "test_checkout", "status": "fail", "duration_ms": 3500},
    {"name": "test_search",   "status": "pass", "duration_ms": 850},
    {"name": "test_payment",  "status": "fail", "duration_ms": 2100},
    {"name": "test_profile",  "status": "pass", "duration_ms": 380},
]

failures = list(filter(lambda r: r["status"] == "fail", test_results))
print([f["name"] for f in failures])
# ['test_checkout', 'test_payment']
```

#### Finding Slow Tests Above a Threshold

```python
SLOW_THRESHOLD_MS = 2000

slow_tests = list(filter(
    lambda r: r["duration_ms"] > SLOW_THRESHOLD_MS,
    test_results
))
print([t["name"] for t in slow_tests])
# ['test_checkout', 'test_payment']
```

#### Combining Multiple Conditions

`filter()` applies a single predicate. For multiple conditions, combine them inside the lambda or use a named function:

```python
# Failed AND slow
critical = list(filter(
    lambda r: r["status"] == "fail" and r["duration_ms"] > 2000,
    test_results
))
print([c["name"] for c in critical])
# ['test_checkout', 'test_payment']
```

---

### `filter()` vs List Comprehension

```python
numbers = range(1, 11)

# filter() — clean when the predicate is an existing named function
evens_filter = list(filter(lambda x: x % 2 == 0, numbers))

# List comprehension — more readable inline
evens_comp   = [x for x in numbers if x % 2 == 0]

print(evens_filter == evens_comp)   # True
```

| Prefer `filter()` when… | Prefer comprehension when… |
|-------------------------|---------------------------|
| The predicate is a named function you already have | The condition is a short inline expression |
| Chaining with `map()` or `reduce()` in a pipeline | You need to transform the result at the same time |
| Expressing "select items matching rule X" clearly | Team style guide favours comprehensions |

### Chaining `filter()` with `map()` and `reduce()`

Because `filter()` returns a lazy iterator, it composes seamlessly into functional pipelines:

```python
from functools import reduce

test_results = [
    {"name": "test_login",    "status": "pass", "duration_ms": 1200},
    {"name": "test_checkout", "status": "fail", "duration_ms": 3500},
    {"name": "test_search",   "status": "pass", "duration_ms": 850},
    {"name": "test_payment",  "status": "fail", "duration_ms": 2100},
]

# Pipeline: total duration of failed tests
# filter selects failures → map extracts durations → reduce sums them
total_fail_ms = reduce(
    lambda acc, ms: acc + ms,
    map(lambda r: r["duration_ms"],
        filter(lambda r: r["status"] == "fail", test_results)),
    0
)
print(f"Total failure time: {total_fail_ms}ms")  # 5600ms
```

### `itertools.filterfalse` — The Inverse of `filter()`

`itertools.filterfalse` keeps elements where the predicate returns `False` — the complement of `filter()`:

```python
from itertools import filterfalse

numbers = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

# filter() keeps evens (predicate True)
evens = list(filter(lambda x: x % 2 == 0, numbers))
# [2, 4, 6, 8, 10]

# filterfalse() keeps odds (predicate False)
odds = list(filterfalse(lambda x: x % 2 == 0, numbers))
# [1, 3, 5, 7, 9]

# Partition a sequence into two groups in one pass
passing, failing = [], []
for r in test_results:
    (passing if r["status"] == "pass" else failing).append(r)
```

---

## Summary

- `filter(func, iterable)` keeps only elements where `func(element)` returns `True`.
- The function is called a **predicate** — it must return a truthy or falsy value.
- `filter(None, iterable)` removes all **falsy** values — a handy cleanup idiom.
- `filter()` is **lazy** — elements are only tested when consumed.
- For complex predicates, use a **named function** for readability.
- For simple conditions, **list comprehensions** (`[x for x in it if condition]`) are often more Pythonic.

---

## Additional Resources
- [Python Docs — filter()](https://docs.python.org/3/library/functions.html#filter)
- [Real Python — Python's filter() Function](https://realpython.com/python-filter-function/)
- [Real Python — Functional Programming in Python](https://realpython.com/python-functional-programming/)
