# Lists, Tuples, and Sets

## Learning Objectives
- Compare lists (mutable, ordered), tuples (immutable, ordered), and sets (mutable, unordered, unique).
- Perform CRUD operations on each collection type.
- Choose the right collection for each use case.

---

## Why This Matters

> **Weekly Epic Connection:** Collections are how you organize and process groups of data — test results, configuration values, unique identifiers, and more. Choosing the right collection type affects both correctness and performance.

---

## The Concept

### Lists — Mutable, Ordered

The most versatile collection. Can hold any type, supports duplicates, maintains order.

```python
tests = ["login", "search", "checkout"]

# Access by index
tests[0]       # "login"
tests[-1]      # "checkout"

# Modify
tests[1] = "advanced_search"
tests.append("logout")          # Add to end
tests.insert(0, "setup")       # Add at index
tests.extend(["teardown"])     # Add multiple items

# Remove
tests.remove("setup")          # Remove by value
popped = tests.pop()           # Remove and return last item
del tests[0]                   # Remove by index

# Common operations
len(tests)                     # Length
"login" in tests               # Membership check → True
tests.sort()                   # Sort in place
sorted_copy = sorted(tests)    # Return sorted copy
tests.reverse()                # Reverse in place
tests.index("login")           # Find index of value
tests.count("login")           # Count occurrences
```

**List slicing:**
```python
numbers = [0, 1, 2, 3, 4, 5]
numbers[1:4]     # [1, 2, 3]
numbers[::2]     # [0, 2, 4] — every 2nd element
numbers[::-1]    # [5, 4, 3, 2, 1, 0] — reversed
```

### Tuples — Immutable, Ordered

Like lists, but **cannot be modified** after creation. Useful for data that shouldn't change.

```python
coordinates = (10, 20)
rgb_color = (255, 128, 0)
single_item = (42,)  # Note the trailing comma!

# Access by index (same as lists)
coordinates[0]   # 10

# ❌ Cannot modify
# coordinates[0] = 99  → TypeError

# Tuple unpacking
x, y = coordinates
print(f"x={x}, y={y}")  # x=10, y=20

# Common operations
len(coordinates)          # 2
10 in coordinates         # True
coordinates.count(10)     # 1
coordinates.index(20)     # 1
```

**When to use tuples:**
- Return multiple values from functions: `return x, y`
- Dictionary keys (lists can't be keys — they're mutable)
- Data that should never change (configuration values, constants)
- Slight performance advantage over lists

### Sets — Mutable, Unordered, Unique

Store **unique values only**. No duplicates, no guaranteed order. Incredibly fast for membership testing.

```python
unique_ids = {1, 2, 3, 4, 5}
empty_set = set()  # NOT {} — that creates an empty dict!

# Duplicates are automatically removed
numbers = {1, 2, 2, 3, 3, 3}
print(numbers)  # {1, 2, 3}

# Add and remove
unique_ids.add(6)
unique_ids.discard(3)    # Remove (no error if missing)
unique_ids.remove(2)     # Remove (raises KeyError if missing)

# ❌ Cannot access by index
# unique_ids[0]  → TypeError (sets are unordered)
```

**Set operations — the real power:**
```python
passed = {"login", "search", "profile", "settings"}
failed = {"checkout", "payment"}
all_tests = {"login", "search", "checkout", "profile", "payment", "settings"}

# Union — all elements from both sets
passed | failed       # All test names
passed.union(failed)

# Intersection — elements in BOTH sets
passed & all_tests    # Tests that passed AND are in all_tests

# Difference — elements in first but not second
all_tests - passed    # Tests that haven't passed
all_tests.difference(passed)

# Symmetric difference — elements in either, but not both
passed ^ failed       # Elements unique to each set
```

**Practical use — finding duplicates:**
```python
test_names = ["login", "search", "login", "checkout", "search"]
unique = set(test_names)
has_dupes = len(test_names) != len(unique)
print(f"Duplicates: {has_dupes}")  # True
```

### Quick Comparison

| Feature | List | Tuple | Set |
|---------|------|-------|-----|
| **Syntax** | `[1, 2, 3]` | `(1, 2, 3)` | `{1, 2, 3}` |
| **Mutable** | ✅ Yes | ❌ No | ✅ Yes |
| **Ordered** | ✅ Yes | ✅ Yes | ❌ No |
| **Duplicates** | ✅ Allowed | ✅ Allowed | ❌ Unique only |
| **Indexable** | ✅ Yes | ✅ Yes | ❌ No |
| **Use as dict key** | ❌ No | ✅ Yes | ❌ No |
| **Membership test** | O(n) slow | O(n) slow | **O(1) fast** |

### When to Use Which

| Collection | Use When... |
|-----------|-------------|
| **List** | You need an ordered, changeable sequence. The default choice. |
| **Tuple** | Data shouldn't change, function returns, dictionary keys. |
| **Set** | You need uniqueness, fast membership checks, or set operations. |

---

## Summary

- **Lists** `[]` — mutable, ordered, allow duplicates. The workhorse.
- **Tuples** `()` — immutable, ordered, allow duplicates. For fixed data.
- **Sets** `{}` — mutable, unordered, unique values only. Fast membership testing.
- Use set operations (union, intersection, difference) for powerful data comparisons.
- Choose the right collection based on: mutability, ordering, and uniqueness needs.

---

## Additional Resources
- [Python Docs — Data Structures](https://docs.python.org/3/tutorial/datastructures.html)
- [Real Python — Lists and Tuples](https://realpython.com/python-lists-tuples/)
- [Real Python — Sets](https://realpython.com/python-sets/)
