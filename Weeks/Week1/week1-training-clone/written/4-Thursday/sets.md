# Python Sets

## Learning Objectives
- Understand what a set is and the key constraints it enforces (uniqueness, no guaranteed order).
- Add and remove elements from a set safely.
- Apply set operations — union, intersection, difference, and symmetric difference — to solve practical data problems.
- Identify when to choose a set over a list for performance.

---

## Why This Matters

> **Weekly Epic Connection:** In QA and data engineering you frequently need to answer questions like: "Which test IDs ran in both environments?", "Are there any duplicate entries in this report?", or "Which items failed but not passed?". Sets are built precisely for these questions. Their mathematical operations let you express complex comparisons in a single, readable line of code.

---

## The Concept

A **set** is a **mutable, unordered collection of unique elements**. Two key rules govern every set:

1. **No duplicates** — adding a value that already exists is silently ignored.
2. **No guaranteed order** — you cannot access elements by index.

Sets are implemented as hash tables, which gives them **O(1) average-case membership testing** — meaning `"value" in my_set` is nearly instant regardless of how large the set grows.

### Creating a Set

```python
# Set literal — curly braces with values
unique_ids = {1, 2, 3, 4, 5}

# ⚠️ IMPORTANT: {} creates an empty DICT, not an empty set!
empty_dict = {}        # dict
empty_set  = set()     # ✅ correct way to create an empty set

# Duplicates are silently removed on creation
numbers = {1, 2, 2, 3, 3, 3}
print(numbers)   # {1, 2, 3}  — only unique values remain

# From any iterable
from_list = set([10, 20, 20, 30])   # {10, 20, 30}
from_str  = set("hello")            # {'h', 'e', 'l', 'o'}  — unique characters
```

### Adding and Removing Elements

```python
unique_ids = {1, 2, 3}

# Add a single element
unique_ids.add(4)       # {1, 2, 3, 4}
unique_ids.add(2)       # {1, 2, 3, 4} — already exists, no change, no error

# Remove — raises KeyError if element not found
unique_ids.remove(1)    # {2, 3, 4}
unique_ids.remove(99)   # ❌ KeyError: 99

# Discard — safe removal, no error if missing
unique_ids.discard(3)   # {2, 4}
unique_ids.discard(99)  # No error, no change

# Pop — remove and return an ARBITRARY element (order is unpredictable)
val = unique_ids.pop()

# Clear — remove all elements
unique_ids.clear()      # set()
```

> **Tip:** Prefer `discard()` over `remove()` when you are not certain the element exists. It follows the same "ask forgiveness" pattern as a try/except but in one line.

### Membership Testing — Where Sets Shine

```python
# List — O(n): Python checks each element one by one
large_list = list(range(1_000_000))
"999999" in large_list    # Slow — may scan all 1,000,000 items

# Set — O(1): hash lookup, nearly instant regardless of size
large_set = set(range(1_000_000))
999999 in large_set       # Fast
```

For large collections where you frequently check membership, converting to a set can dramatically improve performance.

### Set Operations — Mathematical Power

Sets support the same operations you learned in mathematics: union, intersection, difference, and symmetric difference. Each operation has both an **operator** and a **method** form.

```python
passed   = {"login", "search", "profile", "settings"}
failed   = {"checkout", "payment", "profile"}
all_tests = {"login", "search", "checkout", "profile", "payment", "settings"}
```

#### Union `|` — All elements from either set

```python
all_results = passed | failed
# or
all_results = passed.union(failed)
# {"login", "search", "profile", "settings", "checkout", "payment"}
```

#### Intersection `&` — Elements present in BOTH sets

```python
in_both = passed & failed
# or
in_both = passed.intersection(failed)
# {"profile"}  — "profile" appears in both passed and failed (flaky test!)
```

#### Difference `-` — Elements in the first set but NOT in the second

```python
not_yet_passed = all_tests - passed
# or
not_yet_passed = all_tests.difference(passed)
# {"checkout", "payment"}  — tests that haven't passed

# Note: order matters for difference
passed - all_tests   # set() — everything in passed is already in all_tests
```

#### Symmetric Difference `^` — Elements in either set, but NOT in both

```python
exclusive = passed ^ failed
# or
exclusive = passed.symmetric_difference(failed)
# {"login", "search", "settings", "checkout", "payment"}  — excludes "profile"
```

### Subset and Superset Checks

```python
core_tests = {"login", "search"}
full_suite = {"login", "search", "checkout", "payment"}

core_tests.issubset(full_suite)     # True  — all core tests are in full suite
full_suite.issuperset(core_tests)   # True  — full suite contains all core tests
core_tests.isdisjoint({"logout"})   # True  — no elements in common
```

### In-Place Set Operations

All set operations have **in-place variants** that modify the original set instead of returning a new one:

```python
passed = {"login", "search", "profile"}
new_passes = {"checkout", "payment"}

# In-place union — add all elements from another set
passed.update(new_passes)         # passed is now {'login', 'search', 'profile', 'checkout', 'payment'}
# Equivalent operator: passed |= new_passes

# In-place intersection — keep only elements in BOTH sets
stable = {"login", "search", "profile", "checkout"}
passed.intersection_update(stable)
# passed is now {"login", "search", "profile", "checkout"}
# Equivalent operator: passed &= stable

# In-place difference — remove elements that appear in another set
passed.difference_update({"profile"})
# Equivalent operator: passed -= {"profile"}

# In-place symmetric difference
passed.symmetric_difference_update({"login", "logout"})
# Equivalent operator: passed ^= {"login", "logout"}
```

| Standard | In-Place Method | In-Place Operator |
|----------|----------------|------------------|
| `a \| b` | `a.update(b)` | `a \|= b` |
| `a & b` | `a.intersection_update(b)` | `a &= b` |
| `a - b` | `a.difference_update(b)` | `a -= b` |
| `a ^ b` | `a.symmetric_difference_update(b)` | `a ^= b` |

### Practical Examples — QA Use Cases

#### Detecting Duplicate Test IDs

```python
test_ids = ["TC001", "TC002", "TC003", "TC002", "TC004", "TC001"]

unique = set(test_ids)
if len(test_ids) != len(unique):
    duplicates = [t for t in test_ids if test_ids.count(t) > 1]
    print(f"⚠️ Duplicate test IDs found: {set(duplicates)}")
    # ⚠️ Duplicate test IDs found: {'TC001', 'TC002'}
```

#### Comparing Two Test Runs

```python
run_1_passed = {"login", "search", "profile", "checkout"}
run_2_passed = {"login", "profile", "payment", "logout"}

# Tests that passed in both runs (stable)
stable = run_1_passed & run_2_passed
print(f"Stable passes: {stable}")
# {'login', 'profile'}

# Tests that regressed (passed in run 1, failed in run 2)
regressions = run_1_passed - run_2_passed
print(f"Regressions: {regressions}")
# {'search', 'checkout'}

# New passes in run 2
new_passes = run_2_passed - run_1_passed
print(f"New passes: {new_passes}")
# {'payment', 'logout'}
```

### Frozenset — The Immutable Set

A `frozenset` is the immutable equivalent of a set. Because it is hashable, it can be used as a dictionary key or stored inside another set.

```python
config_flags = frozenset(["debug", "verbose"])

# Can be used as a dict key
cache = {config_flags: "cached_result"}

# Set operations still work on frozensets
```

---

## Summary

- **Sets** are defined with curly braces `{}` (or `set()` for empty) and store **unique, unordered** elements.
- Use `add()` and `discard()` (safe) or `remove()` (raises `KeyError`) to manage elements.
- Sets excel at **fast membership testing** (O(1)) — far superior to lists for large data.
- Set operations — **union `|`**, **intersection `&`**, **difference `-`**, **symmetric difference `^`** — make data comparison elegant and concise.
- Use a **frozenset** when you need an immutable, hashable set (e.g., as a dictionary key).
- Choose a set when you need **uniqueness**, **membership checks**, or **set-theoretic comparisons**.

---

## Additional Resources
- [Python Docs — Sets](https://docs.python.org/3/tutorial/datastructures.html#sets)
- [Real Python — Sets in Python](https://realpython.com/python-sets/)
- [W3Schools — Python Sets](https://www.w3schools.com/python/python_sets.asp)
