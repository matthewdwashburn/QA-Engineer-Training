# Iterators and Iterables

## Learning Objectives
- Precisely define "iterable" and "iterator" and state the difference.
- Understand the iterator protocol: `__iter__()` and `__next__()`.
- Explain in mechanical terms how a `for` loop works.
- Use `iter()`, `next()`, and the sentinel form `iter(callable, sentinel)`.
- Create custom iterators by implementing the protocol.
- Recognize the single-use nature of iterators and its consequences.

---

## Why This Matters

> **Weekly Epic Connection:** The iterator protocol is the engine that powers `for` loops, comprehensions, `map()`, `zip()`, and generators — essentially everything in Python that processes sequences. Understanding it deeply lets you create memory-efficient data processing pipelines, write lazy evaluation code, and build custom collection types. It is also the conceptual foundation for generators, which we cover in tomorrow's session.

---

## The Concept

### The Two Key Definitions

Before anything else, memorize these two definitions precisely:

| Term | Definition | Protocol |
|------|-----------|---------|
| **Iterable** | Any object you can loop over. It knows how to *create* an iterator. | Must have `__iter__()` that returns an iterator |
| **Iterator** | An object that produces values one at a time. It *is* the thing doing the work. | Must have `__iter__()` that returns `self`, **AND** `__next__()` that produces the next value |

The key insight: **every iterator is iterable, but not every iterable is an iterator.**

A list is iterable (you can loop over it) but is not an iterator (calling `next()` on it directly raises a `TypeError`). An iterator wraps the list and tracks the current position.

```python
my_list = [1, 2, 3]

# ✅ list is iterable — has __iter__
iter(my_list)      # Returns an iterator object

# ❌ list is NOT an iterator — has no __next__
next(my_list)      # TypeError: 'list' object is not an iterator

# ✅ The iterator returned by iter() has both
it = iter(my_list)
next(it)           # 1
next(it)           # 2
next(it)           # 3
next(it)           # StopIteration
```

---

### Step-by-Step: Getting Values from an Iterable

```python
# Step 1: Start with an iterable
my_list = [10, 20, 30]

# Step 2: Call iter() to get an iterator — this calls my_list.__iter__()
my_iter = iter(my_list)

# Step 3: Call next() repeatedly — each call invokes my_iter.__next__()
print(next(my_iter))   # 10
print(next(my_iter))   # 20
print(next(my_iter))   # 30

# Step 4: When exhausted, StopIteration is raised
print(next(my_iter))   # StopIteration!
```

---

### How `for` Loops Really Work

Every `for` loop is secretly doing exactly the steps above:

```python
# What you write:
for item in [1, 2, 3]:
    print(item)

# What Python actually executes:
_iterator = iter([1, 2, 3])    # Step 1: get iterator
while True:                    # Step 2: loop
    try:
        item = next(_iterator) # Step 3: get next value
        print(item)
    except StopIteration:      # Step 4: stop when exhausted
        break
```

This means **anything** that implements `__iter__()` and `__next__()` works in a `for` loop — lists, strings, files, database result sets, network streams, and your own custom classes.

---

### The `iter()` and `next()` Built-in Functions

```python
text = "Python"
it = iter(text)

next(it)   # 'P'
next(it)   # 'y'
next(it)   # 't'

# Provide a default value to avoid StopIteration
next(it, "DONE")   # 'h'
next(it, "DONE")   # 'o'
next(it, "DONE")   # 'n'
next(it, "DONE")   # 'DONE' — exhausted, returns default instead of raising
```

#### The Sentinel Form: `iter(callable, sentinel)`

`iter()` has a second form: `iter(callable, sentinel)`. It calls `callable()` repeatedly and returns each result, stopping as soon as the result equals `sentinel`:

```python
# Read a file in 64-byte chunks until EOF (empty bytes b"")
with open("data.bin", "rb") as f:
    for chunk in iter(lambda: f.read(64), b""):
        process(chunk)

# Read lines until a blank line
import sys
for line in iter(input, ""):
    print(f"You typed: {line}")
```

This form is powerful for wrapping callback-based or streaming APIs in an iterable interface.

---

### Iterables Are Reusable; Iterators Are Single-Use

This is one of the most important things to understand and a common source of bugs:

```python
my_list = [1, 2, 3]    # Iterable — backed by data, can produce fresh iterators

# Loop 1 — works fine
for x in my_list:
    print(x)   # 1, 2, 3

# Loop 2 — also works fine (a NEW iterator is created each time)
for x in my_list:
    print(x)   # 1, 2, 3

# ⚠️ Now get an ITERATOR manually
my_iter = iter(my_list)

# Loop 1 through iterator — works
for x in my_iter:
    print(x)   # 1, 2, 3

# Loop 2 through the SAME iterator — produces nothing!
for x in my_iter:
    print(x)   # (nothing printed — iterator is exhausted)
```

**Practical consequence:** If you pass an iterator to a function that consumes it (e.g., `list()`, `sum()`, `max()`), that iterator is exhausted. You cannot reuse it.

```python
it = iter([1, 2, 3, 4, 5])

total = sum(it)       # Consumes the entire iterator → 15
maximum = max(it)     # ❌ Nothing left — raises ValueError: max() arg is an empty sequence

# Fix: use the original iterable, or reset
data = [1, 2, 3, 4, 5]
total = sum(data)
maximum = max(data)   # ✅
```

---

### Common Iterables in Python

| Type | Example | Iterator Behavior |
|------|---------|-------------------|
| `list` | `[1, 2, 3]` | Yields each element in order |
| `tuple` | `(1, 2, 3)` | Yields each element in order |
| `str` | `"hello"` | Yields each character |
| `dict` | `{"a": 1}` | Yields each **key** by default |
| `dict.values()` | `{"a": 1}.values()` | Yields each value |
| `dict.items()` | `{"a": 1}.items()` | Yields `(key, value)` tuples |
| `set` | `{1, 2, 3}` | Yields each element (no guaranteed order) |
| `range` | `range(5)` | Yields integers lazily |
| `file` | `open("f.txt")` | Yields each line as a string |
| `enumerate` | `enumerate(lst)` | Yields `(index, value)` tuples |
| `zip` | `zip(a, b)` | Yields paired tuples |

---

### Creating a Custom Iterator

To make a class work in a `for` loop, implement `__iter__()` and `__next__()`:

```python
class Countdown:
    """Iterator that counts down from a given number to 1."""

    def __init__(self, start):
        self.current = start

    def __iter__(self):
        """Return the iterator object itself.
        
        This is required so the iterator also satisfies the iterable protocol.
        It allows iterators to be used directly in for loops and passed to
        functions like list(), sum(), etc.
        """
        return self

    def __next__(self):
        """Return the next value or raise StopIteration when exhausted."""
        if self.current <= 0:
            raise StopIteration
        value = self.current
        self.current -= 1
        return value


# Used in a for loop — Python calls __iter__ then __next__ repeatedly
for num in Countdown(5):
    print(num, end=" ")
# 5 4 3 2 1

# Also works with built-in functions
print(list(Countdown(5)))   # [5, 4, 3, 2, 1]
print(sum(Countdown(5)))    # 15
print(max(Countdown(5)))    # 5
```

#### A Reusable Iterator (Separate Iterable and Iterator)

The `Countdown` above is single-use because `self.current` is modified. To make a **reusable** collection, separate the iterable (data holder) from the iterator (position tracker):

```python
class TestSuite:
    """Reusable iterable — creates a fresh iterator on each loop."""

    def __init__(self, tests):
        self.tests = tests

    def __iter__(self):
        # Return a NEW iterator each time — makes the suite reusable
        return TestSuiteIterator(self.tests)


class TestSuiteIterator:
    """Single-use iterator over a TestSuite."""

    def __init__(self, tests):
        self.tests = tests
        self.index = 0

    def __iter__(self):
        return self

    def __next__(self):
        if self.index >= len(self.tests):
            raise StopIteration
        test = self.tests[self.index]
        self.index += 1
        return test


suite = TestSuite(["login", "checkout", "profile"])

for test in suite:
    print(test)   # login, checkout, profile

for test in suite:
    print(test)   # login, checkout, profile — works again!
```

---

### Checking If Something Is Iterable or an Iterator

```python
from collections.abc import Iterable, Iterator

# Iterable check
print(isinstance([1, 2], Iterable))       # True
print(isinstance("hello", Iterable))      # True
print(isinstance(42, Iterable))           # False

# Iterator check
print(isinstance([1, 2], Iterator))       # False — list is iterable, not iterator
print(isinstance(iter([1, 2]), Iterator)) # True  — iter() returns an iterator

# A safer check for duck typing
def is_iterable(obj):
    try:
        iter(obj)
        return True
    except TypeError:
        return False
```

---

### Coming Next: Generators

Creating iterators with `__iter__` and `__next__` works, but it is verbose. Python provides a much more elegant way to create iterators: **generators** — functions that use the `yield` keyword. We will cover these on Friday.

```python
# Custom iterator (verbose)
class CountUp:
    ...  # 20+ lines

# Generator (elegant — tomorrow's topic)
def count_up(n):
    for i in range(1, n + 1):
        yield i

for num in count_up(5):
    print(num, end=" ")  # 1 2 3 4 5
```

---

## Summary

- **Iterable:** Has `__iter__()` — can produce an iterator. Reusable. Examples: list, str, dict, set.
- **Iterator:** Has both `__iter__()` and `__next__()`. Single-use. Tracks current position.
- `for` loops call `iter()` on the iterable, then call `next()` on the iterator until `StopIteration`.
- `iter(callable, sentinel)` — powerful second form for wrapping callback-based APIs.
- **Iterators are single-use** — once exhausted, they cannot be reset; use the original iterable to loop again.
- Create custom iterators by implementing `__iter__()` and `__next__()` — or use generators (Friday).

---

## Additional Resources
- [Python Docs — Iterator Types](https://docs.python.org/3/library/stdtypes.html#iterator-types)
- [Real Python — Python Iterators](https://realpython.com/python-iterators-iterables/)
- [PEP 234 — Iterators](https://peps.python.org/pep-0234/)
- [Python Docs — `collections.abc.Iterator`](https://docs.python.org/3/library/collections.abc.html#collections.abc.Iterator)
