# Generator Expressions

## Learning Objectives
- Understand what a generator function is and how `yield` suspends execution.
- Distinguish generator expressions from list comprehensions.
- Explain lazy evaluation and why it matters for memory efficiency.
- Use generators for infinite sequences, file processing, and data pipelines.
- Know about `yield from`, and the generator lifecycle (`send`, `close`, `throw`).

---

## Why This Matters

> **Weekly Epic Connection:** When processing large test datasets — millions of log lines, thousands of test results — loading everything into memory at once isn't feasible. Generators produce values one at a time, using almost no memory regardless of data size. They also enable **data pipelines**: a chain of transformations where data flows through stages without any intermediate lists.

---

## The Concept

### How Functions Work vs. How Generators Work

An ordinary function runs to completion and returns once:

```python
def get_numbers():
    return [1, 2, 3]   # Creates the whole list, returns it

numbers = get_numbers()  # [1, 2, 3] is fully created in memory
```

A **generator function** uses `yield` instead of `return`. Each time the caller asks for the next value, execution **resumes from where it was paused**:

```python
def gen_numbers():
    yield 1   # Pause here, hand 1 to caller
    yield 2   # Pause here, hand 2 to caller
    yield 3   # Pause here, hand 3 to caller
    # When this point is reached with no more yields, StopIteration is raised

gen = gen_numbers()   # Nothing executes yet — just creates the generator object
print(type(gen))      # <class 'generator'>
```

The generator object is both **an iterable and an iterator** — it implements `__iter__()` and `__next__()` automatically.

---

### `yield` — The Suspension Point

`yield` does two things simultaneously:
1. **Produces** a value to the caller (like `return`).
2. **Suspends** the function's execution state (local variables, position in code) until the next `next()` call.

```python
def simple_gen():
    print("→ Before first yield")
    yield "A"
    print("→ Between yields")
    yield "B"
    print("→ After last yield")
    yield "C"
    print("→ Generator exhausted")

gen = simple_gen()

print(next(gen))   # Prints "→ Before first yield", returns "A"
print(next(gen))   # Prints "→ Between yields",    returns "B"
print(next(gen))   # Prints "→ After last yield",  returns "C"
print(next(gen))   # Prints "→ Generator exhausted", then raises StopIteration
```

The key insight: **nothing in `simple_gen` runs until you call `next()`**. The function is paused between yields, with all its local state preserved.

---

### Generator Functions

Any function containing at least one `yield` statement is a **generator function**. Calling it returns a generator object — it does not run the body:

```python
def countdown(n):
    """Generator that counts down from n to 1."""
    while n > 0:
        yield n       # Produce n, then pause
        n -= 1        # Resumes here on next next() call

# Create the generator — nothing executes yet
gen = countdown(5)

# Iterate — execution proceeds step by step
for num in gen:
    print(num, end=" ")
# 5 4 3 2 1
```

Generators implement the iterator protocol automatically — no need to write `__iter__` or `__next__` yourself.

---

### Generator Expressions

Generator expressions are the **generator version of list comprehensions**. They use parentheses `()` instead of square brackets `[]` and produce values lazily:

```python
# List comprehension — builds the ENTIRE list in memory immediately
squares_list = [x ** 2 for x in range(1_000_000)]   # ~8 MB in memory

# Generator expression — produces values one by one on demand
squares_gen = (x ** 2 for x in range(1_000_000))    # ~200 bytes
```

```python
import sys

list_comp = [x ** 2 for x in range(100_000)]
gen_expr  = (x ** 2 for x in range(100_000))

print(sys.getsizeof(list_comp))   # ~824,456 bytes (~800 KB)
print(sys.getsizeof(gen_expr))    # ~200 bytes (constant regardless of range size!)
```

Generator expressions work anywhere an iterable is expected:

```python
# Pass directly to built-in functions — no extra parentheses needed
total   = sum(x ** 2 for x in range(1000))
maximum = max(abs(x) for x in [-5, 3, -8, 2])
any_neg = any(x < 0 for x in [1, -2, 3])
all_pos = all(x > 0 for x in [1, 2, 3])
```

---

### Memory Comparison

```python
import sys

# Same data — dramatically different memory usage
small  = [x for x in range(10)]
large  = [x for x in range(1_000_000)]
gen_s  = (x for x in range(10))
gen_l  = (x for x in range(1_000_000))

print(sys.getsizeof(small))    # 184 bytes
print(sys.getsizeof(large))    # 8,697,456 bytes  (~8.3 MB!)
print(sys.getsizeof(gen_s))    # 200 bytes
print(sys.getsizeof(gen_l))    # 200 bytes  ← same! generators don't store data
```

---

### Lazy Evaluation — Compute Only What You Need

Generators are **lazy** — they compute the next value only when you ask for it. This enables:

**Infinite sequences:**
```python
def infinite_counter(start=0):
    """Generates integers forever — safely, because it never stores them all."""
    n = start
    while True:
        yield n
        n += 1

counter = infinite_counter()
print(next(counter))   # 0
print(next(counter))   # 1

# Take only the first 5 values with itertools.islice
from itertools import islice
first_10 = list(islice(infinite_counter(), 10))
# [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
```

**Early stopping:**
```python
def find_first(iterable, predicate):
    """Return the first element matching the predicate, or None."""
    return next((x for x in iterable if predicate(x)), None)

result = find_first(range(1_000_000), lambda x: x > 500 and x % 13 == 0)
print(result)   # 507 — stops immediately, never processes the remaining 999,492 elements
```

---

### Practical Applications

#### Processing Large Files Line by Line

```python
def read_errors(filepath):
    """Yield only ERROR lines from a log file — never loads the whole file."""
    with open(filepath) as f:
        for line in f:
            if "ERROR" in line:
                yield line.strip()

# Process errors without loading the entire file into memory
for error in read_errors("application.log"):
    print(error)
```

#### Generating Test Data On-the-Fly

```python
def test_data_generator(count):
    """Generate test user records lazily — one at a time."""
    for i in range(count):
        yield {
            "id":    i,
            "name":  f"user_{i}",
            "email": f"user_{i}@test.com",
        }

# Process 1 million users with minimal memory — only one dict in memory at a time
for user in test_data_generator(1_000_000):
    validate_user(user)   # Process and discard before the next is generated
```

#### Chaining Generators — Data Pipelines

Generators compose naturally into **pipelines**: each stage is a generator that consumes the previous one. Nothing is computed until the final `for` loop drives the pipeline:

```python
# Stage 1: Read raw lines from a file
def read_lines(filepath):
    with open(filepath) as f:
        for line in f:
            yield line.strip()

# Stage 2: Keep only error lines
def filter_errors(lines):
    for line in lines:
        if line.startswith("ERROR"):
            yield line

# Stage 3: Parse out just the timestamp
def extract_timestamps(lines):
    for line in lines:
        yield line.split("|")[0].strip()

# Assemble the pipeline — no computation yet
pipeline = extract_timestamps(
    filter_errors(
        read_lines("app.log")
    )
)

# Drive the pipeline — now data flows through all three stages
for timestamp in pipeline:
    print(timestamp)
```

This pipeline reads the file **one line at a time** — the memory footprint is constant regardless of file size.

---

### `yield from` — Delegating to a Sub-Generator

`yield from` lets one generator delegate part of its work to another iterable or generator:

```python
# Without yield from — verbose
def chain_lists(list1, list2):
    for item in list1:
        yield item
    for item in list2:
        yield item

# With yield from — clean delegation
def chain_lists(list1, list2):
    yield from list1    # Delegate to list1 — yields each item
    yield from list2    # Then delegate to list2

list(chain_lists([1, 2, 3], [4, 5, 6]))
# [1, 2, 3, 4, 5, 6]
```

`yield from` works with any iterable, including nested generators:

```python
def flatten(nested):
    """Recursively flatten a nested list structure."""
    for item in nested:
        if isinstance(item, list):
            yield from flatten(item)   # Recurse via yield from
        else:
            yield item

list(flatten([1, [2, [3, 4]], [5, 6]]))
# [1, 2, 3, 4, 5, 6]
```

---

### Generator Lifecycle Methods

Generator objects support three additional methods beyond `__next__()`:

| Method | What It Does |
|--------|-------------|
| `gen.send(value)` | Resumes the generator AND sends a value back in (received as the result of the `yield` expression) |
| `gen.throw(ExcType)` | Injects an exception at the current `yield` point |
| `gen.close()` | Terminates the generator by throwing `GeneratorExit` |

```python
def accumulator():
    """Generator that accepts values via send() and yields running totals."""
    total = 0
    while True:
        value = yield total   # yield sends total out; send() passes value in
        if value is None:
            break
        total += value

gen = accumulator()
next(gen)          # Prime the generator — must call once before send()
gen.send(10)       # → 10
gen.send(20)       # → 30
gen.send(5)        # → 35

gen.close()        # Cleanly terminate
```

> **Note for beginners:** `send()` and `throw()` are advanced features used in coroutines and async-style code. For most use cases, you only need `yield` and `yield from`.

---

### Generators Are Single-Use

Unlike lists and ranges, a generator can only be consumed **once**. Once exhausted, it produces no more values:

```python
gen = (x for x in range(5))

print(list(gen))   # [0, 1, 2, 3, 4]
print(list(gen))   # []  — exhausted! Cannot be reset.

# Fix: recreate the generator expression
gen = (x for x in range(5))   # New generator object
print(list(gen))   # [0, 1, 2, 3, 4]
```

---

## Summary

- A **generator function** uses `yield` to pause and resume execution, producing values one at a time.
- Calling a generator function returns a **generator object** — a lazy iterator. It does not run the body.
- **Generator expressions** `(expr for x in iterable)` are like lazy list comprehensions using `()`.
- Generators use **constant memory** — the size does not grow with the data set.
- Use generators for **large datasets**, **infinite sequences**, **data pipelines**, and **file processing**.
- **`yield from`** delegates to another iterable or generator — clean, recursive-friendly composition.
- **Generators are single-use** — once exhausted, you must create a new generator object.
- Advanced: `send()`, `throw()`, `close()` support coroutine-style two-way communication.

---

## Additional Resources
- [Python Docs — Generator Expressions](https://docs.python.org/3/reference/expressions.html#generator-expressions)
- [Real Python — Python Generators](https://realpython.com/introduction-to-python-generators/)
- [PEP 255 — Simple Generators](https://peps.python.org/pep-0255/)
- [PEP 342 — Coroutines via Enhanced Generators (`send`, `throw`)](https://peps.python.org/pep-0342/)
- [PEP 380 — `yield from`](https://peps.python.org/pep-0380/)
