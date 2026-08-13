# Python Lists

## Learning Objectives
- Understand what a list is and how it is stored in memory.
- Perform Create, Read, Update, and Delete (CRUD) operations on a list.
- Use slicing to extract sub-sequences from a list.
- Apply built-in list methods to sort, search, and manipulate data.

---

## Why This Matters

> **Weekly Epic Connection:** In QA and test automation, you will constantly manage groups of data — a suite of test case names, a batch of API responses, or a collection of log entries. The list is Python's most versatile and widely-used collection. Mastering it is not optional; it is the foundation for nearly every real-world script you will write.

---

## The Concept

A **list** is an **ordered, mutable** sequence. "Ordered" means items maintain their insertion position and can be accessed by index. "Mutable" means you can freely add, remove, or change items after the list is created.

Internally, a list is a **dynamic array** — a contiguous block of memory that stores *references* (pointers) to Python objects. The list itself does not contain the objects; it holds addresses pointing to them. This distinction matters when you copy or share lists.

### Creating a List

```python
# Empty list
tests = []

# List with initial values — any mix of types is allowed
tests = ["login", "search", "checkout"]
mixed = [1, "hello", True, 3.14]

# List from another iterable
numbers = list(range(5))  # [0, 1, 2, 3, 4]
```

### Accessing Elements

Lists use **zero-based indexing**. Negative indices count from the end.

```python
tests = ["login", "search", "checkout", "logout"]

tests[0]    # "login"   — first item
tests[-1]   # "logout"  — last item
tests[-2]   # "checkout"
```

### Modifying a List (Update)

```python
tests = ["login", "search", "checkout"]

# Replace a value
tests[1] = "advanced_search"

# Add to the end
tests.append("logout")

# Insert at a specific index
tests.insert(0, "setup")

# Merge another list in-place
tests.extend(["teardown", "report"])

print(tests)
# ["setup", "login", "advanced_search", "checkout", "logout", "teardown", "report"]
```

### Removing Elements (Delete)

```python
tests = ["setup", "login", "search", "logout"]

tests.remove("setup")     # Remove first occurrence by value
popped = tests.pop()      # Remove and return the last item → "logout"
popped_idx = tests.pop(0) # Remove and return item at index 0 → "login"
del tests[0]              # Remove item at index — no return value

tests.clear()             # Remove ALL items — leaves an empty list
```

### Common Read Operations

```python
tests = ["login", "search", "login", "checkout"]

len(tests)              # 4  — number of items
"login" in tests        # True — membership check
tests.index("search")   # 1  — index of first occurrence
tests.count("login")    # 2  — how many times "login" appears
```

### Sorting and Ordering

```python
scores = [85, 42, 99, 17, 60]

scores.sort()                  # Sort in place, ascending
scores.sort(reverse=True)      # Sort in place, descending
asc_copy = sorted(scores)      # Return a NEW sorted list, original unchanged

tests = ["checkout", "login", "search"]
tests.reverse()                # Reverse in place → ["search", "login", "checkout"]
```

### List Slicing

Slicing creates a **new list** from a portion of the original using `[start:stop:step]`. The `stop` index is **not** included.

```python
numbers = [0, 1, 2, 3, 4, 5]

numbers[1:4]    # [1, 2, 3]     — index 1 up to (not including) 4
numbers[:3]     # [0, 1, 2]     — from the beginning up to index 3
numbers[3:]     # [3, 4, 5]     — from index 3 to the end
numbers[::2]    # [0, 2, 4]     — every second element
numbers[::-1]   # [5, 4, 3, 2, 1, 0] — reversed copy
```

> **Tip:** Slicing never modifies the original list. It always returns a brand-new list.

### Copying a List

Because a list stores *references*, you must be deliberate about copying:

```python
original = [1, 2, 3]

# Shallow copy — new list object, but element references are shared
copy_a = original.copy()
copy_b = original[:]
copy_c = list(original)

# ⚠️ This does NOT copy — both names point to the SAME list!
alias = original
alias.append(4)
print(original)   # [1, 2, 3, 4]  ← original changed!
```

**Shallow copy vs deep copy for nested lists:**

```python
import copy

nested = [[1, 2], [3, 4], [5, 6]]

# Shallow copy — outer list is new, but inner lists are still shared
shallow = nested.copy()
shallow[0].append(99)
print(nested[0])   # [1, 2, 99]  ← inner list was mutated through shallow copy!

# Deep copy — all objects are recursively duplicated
nested = [[1, 2], [3, 4], [5, 6]]
deep = copy.deepcopy(nested)
deep[0].append(99)
print(nested[0])   # [1, 2]  ← original is safe
```

> **Rule:** Use `copy()` or `[:]` for flat lists. Use `copy.deepcopy()` when your list contains mutable objects (other lists, dicts, custom objects).

### Practical Example — QA Use Case

```python
# Track which tests have been executed
test_suite = ["login", "register", "search", "checkout", "payment", "logout"]
executed = []
failed = []

for test in test_suite:
    executed.append(test)
    if test in ("checkout", "payment"):
        failed.append(test)

print(f"Executed: {len(executed)}/{len(test_suite)}")
print(f"Failed tests: {failed}")
# Executed: 6/6
# Failed tests: ['checkout', 'payment']
```

### Using a List as a Stack or Queue

Lists support both stack (LIFO) and queue (FIFO) patterns natively:

```python
# Stack (Last In, First Out) — use append + pop
stack = []
stack.append("a")   # Push
stack.append("b")
stack.append("c")
stack.pop()          # Pop → "c" (last in, first out)
stack.pop()          # Pop → "b"

# Queue (First In, First Out) — use append + pop(0)
# ⚠️ pop(0) is O(n) — for large queues, use collections.deque instead
queue = []
queue.append("task_1")   # Enqueue
queue.append("task_2")
queue.pop(0)             # Dequeue → "task_1" (first in, first out)
```

### Operation Complexity Reference

| Operation | Complexity | Notes |
|-----------|-----------|-------|
| `lst[i]` | O(1) | Random access by index |
| `lst.append(x)` | O(1) amortized | Occasional resize is O(n) |
| `lst.pop()` | O(1) | Remove from right end |
| `lst.pop(0)` | O(n) | Remove from left — shifts all elements |
| `lst.insert(0, x)` | O(n) | Insert at left — shifts all elements |
| `lst.insert(i, x)` | O(n) | Insert at middle |
| `x in lst` | O(n) | Linear scan |
| `lst.sort()` | O(n log n) | Timsort |
| `len(lst)` | O(1) | Stored as attribute |

---

## Summary

- **Lists** are defined with square brackets `[]` and are **ordered** and **mutable**.
- Internally a **dynamic array** — stores references (pointers) to objects, not the objects themselves.
- Access items by **zero-based index**; use negative indices to count from the end.
- Key methods: `append()`, `insert()`, `extend()`, `remove()`, `pop()`, `sort()`, `reverse()`, `index()`, `count()`.
- **Slicing** `[start:stop:step]` extracts a sub-list without modifying the original.
- For nested lists, use `copy.deepcopy()` to avoid shared-reference bugs.
- A list is the **default, general-purpose collection** in Python — when in doubt, start with a list.

---

## Additional Resources
- [Python Docs — Lists](https://docs.python.org/3/tutorial/datastructures.html#more-on-lists)
- [Real Python — Lists and Tuples in Python](https://realpython.com/python-lists-tuples/)
- [W3Schools — Python Lists](https://www.w3schools.com/python/python_lists.asp)
