# Python `functools.reduce()` — Aggregating a Sequence to a Single Value

## Learning Objectives
- Understand what `reduce()` does and trace its accumulation step by step.
- Apply `reduce()` with both lambda functions and named functions.
- Use the optional `initializer` argument safely.
- Recognise when a Python built-in (`sum`, `max`, `min`, `any`, `all`) is a better choice than `reduce()`.

---

## Why This Matters

> **Weekly Epic Connection:** Aggregation is the final step of almost every data pipeline — summing test durations, finding the slowest test, tallying pass/fail counts, or merging multiple configuration dictionaries into one. `reduce()` generalises this pattern: it collapses an entire iterable into a **single value** using any two-argument combining function you provide.

---

## The Concept

`reduce()` lives in the `functools` module — it is not a built-in function like `map()` and `filter()`. It applies a **two-argument function cumulatively** from left to right across a sequence, carrying the running result (the **accumulator**) forward.

**Syntax:**
```python
from functools import reduce

reduce(function, iterable)
reduce(function, iterable, initializer)
```

| Argument | Description |
|----------|-------------|
| `function` | A two-argument function: `(accumulator, current_element) → new_accumulator` |
| `iterable` | The sequence to reduce |
| `initializer` | Optional starting value for the accumulator. **Always provide this when the iterable might be empty.** |

---

### Tracing `reduce()` Step by Step

The best way to understand `reduce()` is to trace each step manually:

```python
from functools import reduce

numbers = [1, 2, 3, 4, 5]

total = reduce(lambda acc, x: acc + x, numbers)
print(total)   # 15
```

What happens internally:

```
Initial:        acc = 1   (first element, no initializer)
Step 1:  acc=1,  x=2  →  1  + 2  =  3
Step 2:  acc=3,  x=3  →  3  + 3  =  6
Step 3:  acc=6,  x=4  →  6  + 4  =  10
Step 4:  acc=10, x=5  →  10 + 5  =  15
Result: 15
```

---

### Common Aggregations

```python
from functools import reduce

numbers = [3, 1, 4, 1, 5, 9, 2, 6]

# Sum — prefer sum() in practice, but reduce() generalises the pattern
total   = reduce(lambda acc, x: acc + x, numbers)
print(f"Sum:     {total}")     # 31

# Product — no built-in, so reduce() is the right tool
product = reduce(lambda acc, x: acc * x, numbers)
print(f"Product: {product}")   # 6480  (3×1×4×1×5×9×2×6)

# Maximum — prefer max() in practice
maximum = reduce(lambda acc, x: acc if acc > x else x, numbers)
print(f"Max:     {maximum}")   # 9

# Concatenate strings
words = ["Python", "is", "powerful"]
sentence = reduce(lambda acc, w: acc + " " + w, words)
print(f"Sentence: {sentence}")  # Python is powerful
```

---

### The `initializer` Argument

Without an initializer, `reduce()` uses the **first element** as the starting accumulator. This is fine for arithmetic, but causes a `TypeError` on an empty iterable.

```python
# ⚠️ This raises TypeError on an empty list
total = reduce(lambda acc, x: acc + x, [])

# ✅ With initializer — safe on empty iterables
total = reduce(lambda acc, x: acc + x, [], 0)
print(total)   # 0
```

**Rule:** Always provide an `initializer` when:
- The iterable might be empty.
- The accumulator starts from a neutral value that differs from the element type (e.g., building a dict from a list of tuples).

---

### Building Complex Accumulators

`reduce()` is not limited to numbers. The accumulator can be any type — a dict, a list, a string:

```python
from functools import reduce

# Count occurrences — build a frequency dict
words = ["pass", "fail", "pass", "pass", "fail"]

counts = reduce(
    lambda acc, word: {**acc, word: acc.get(word, 0) + 1},
    words,
    {}   # initializer: start with an empty dict
)
print(counts)   # {'pass': 3, 'fail': 2}
```

```python
# Group test names by status
results = [
    {"name": "test_login",  "status": "pass"},
    {"name": "test_search", "status": "fail"},
    {"name": "test_pay",    "status": "fail"},
    {"name": "test_logout", "status": "pass"},
]

grouped = reduce(
    lambda acc, r: {**acc, r["status"]: acc.get(r["status"], []) + [r["name"]]},
    results,
    {}
)
print(grouped)
# {'pass': ['test_login', 'test_logout'], 'fail': ['test_search', 'test_pay']}
```

---

### QA Use Case — Aggregating a Full Test Run

```python
from functools import reduce

test_results = [
    {"name": "test_login",    "status": "pass", "duration_ms": 1200},
    {"name": "test_checkout", "status": "fail", "duration_ms": 3500},
    {"name": "test_search",   "status": "pass", "duration_ms": 850},
    {"name": "test_payment",  "status": "fail", "duration_ms": 2100},
]

# Total duration across all tests
total_ms = reduce(lambda acc, r: acc + r["duration_ms"], test_results, 0)
print(f"Total time:    {total_ms}ms")   # 7650ms

# Total failure time — chain with filter()
fail_ms = reduce(
    lambda acc, r: acc + r["duration_ms"],
    filter(lambda r: r["status"] == "fail", test_results),
    0
)
print(f"Failure time:  {fail_ms}ms")    # 5600ms

# Find the slowest test
slowest = reduce(
    lambda acc, r: r if r["duration_ms"] > acc["duration_ms"] else acc,
    test_results
)
print(f"Slowest test:  {slowest['name']} ({slowest['duration_ms']}ms)")
# Slowest test: test_checkout (3500ms)
```

---

### When NOT to Use `reduce()`

Python provides built-ins that are more readable than `reduce()` for common operations:

| Instead of `reduce(lambda acc, x: …, it)` | Use this |
|-------------------------------------------|----------|
| Sum all numbers | `sum(iterable)` |
| Find maximum | `max(iterable)` |
| Find minimum | `min(iterable)` |
| Check if any element is truthy | `any(iterable)` |
| Check if all elements are truthy | `all(iterable)` |
| Concatenate strings | `"".join(iterable)` |

Use `reduce()` when **no built-in covers your aggregation** — such as building a dict, computing a running product, or folding a list into a custom object.

---

## Summary

- `reduce(func, iterable, initializer)` collapses a sequence into a **single value** by applying `func` cumulatively.
- The function takes two arguments: `(accumulator, current_element)`.
- Always provide an **initializer** when the iterable might be empty.
- The accumulator can be **any type** — number, string, list, or dict.
- Prefer Python **built-ins** (`sum`, `max`, `min`, `any`, `all`, `"".join`) for common aggregations.
- Use `reduce()` when no built-in covers your custom aggregation logic.
- Chaining `filter()` → `map()` → `reduce()` creates a **full functional data pipeline**.

---

## Additional Resources
- [Python Docs — functools.reduce()](https://docs.python.org/3/library/functools.html#functools.reduce)
- [Real Python — Python's reduce(): From Functional to Pythonic Style](https://realpython.com/python-reduce-function/)
- [Real Python — Functional Programming in Python](https://realpython.com/python-functional-programming/)
