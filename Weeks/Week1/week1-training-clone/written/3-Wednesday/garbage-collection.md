# Garbage Collection

## Learning Objectives
- Understand how Python manages memory automatically.
- Explain reference counting and the `gc` module.
- Know what circular references are and how Python handles them.

---

## Why This Matters

> **Weekly Epic Connection:** While Python handles memory automatically, understanding garbage collection helps you write efficient code, debug memory issues, and understand why objects are (or aren't) cleaned up. Long-running test suites and automation scripts can develop memory leaks if you're not aware of how this works.

---

## The Concept

### How Python Manages Memory

When you create an object, Python allocates memory for it. When the object is no longer needed, Python automatically frees that memory. This process is called **garbage collection**.

### Reference Counting

Python's primary memory management mechanism is **reference counting**. Every object tracks how many references point to it:

```python
a = [1, 2, 3]     # Reference count: 1
b = a              # Reference count: 2 (a and b both point to the list)
c = a              # Reference count: 3

del b              # Reference count: 2
del c              # Reference count: 1
del a              # Reference count: 0 → OBJECT IS FREED
```

When the reference count drops to **zero**, Python frees the memory immediately.

Check reference counts with `sys.getrefcount()`:

```python
import sys

x = "hello"
print(sys.getrefcount(x))  # Typically 2+ (one for x, one for the getrefcount argument)
```

### Circular References

Reference counting alone can't handle **circular references** — objects that reference each other:

```python
class Node:
    def __init__(self, value):
        self.value = value
        self.next = None

# Create a cycle
a = Node("A")
b = Node("B")
a.next = b   # A → B
b.next = a   # B → A (circular!)

# Even after deleting a and b, the cycle keeps both alive
del a
del b
# Reference counts never reach 0 because they reference each other!
```

### The `gc` Module — Cycle Detector

Python has a **generational garbage collector** (the `gc` module) that periodically detects and cleans up circular references:

```python
import gc

# Force garbage collection
gc.collect()

# Check if gc is enabled
print(gc.isenabled())  # True (enabled by default)

# Get garbage collection stats
print(gc.get_stats())
```

### Generational Collection

Python groups objects into three generations:
- **Generation 0:** Newly created objects. Collected most frequently.
- **Generation 1:** Objects that survived one collection. Collected less often.
- **Generation 2:** Long-lived objects. Collected least frequently.

The intuition: most objects are short-lived, so checking new objects frequently is efficient.

### Best Practices

```python
# ✅ Let variables go out of scope naturally
def process_data():
    large_dataset = load_data()    # Created
    result = analyze(large_dataset)
    return result
    # large_dataset is freed after the function returns

# ✅ Use context managers for resource cleanup
with open("large_file.csv") as f:
    data = f.read()
# File is automatically closed and resources freed

# ⚠️ Avoid creating unnecessary circular references
# If you must, use weakref
import weakref
class Node:
    def __init__(self, value):
        self.value = value
        self.parent = None  # Could use weakref.ref() for parent

# ✅ Explicitly delete large objects if needed
large_data = [0] * 10_000_000
del large_data  # Freed immediately
```

### When to Care About GC

For most Python code, you never think about garbage collection. Care about it when:
- Running **long-lived processes** (servers, test suites that run for hours).
- Working with **large datasets** in memory.
- Creating objects with **circular references**.
- Debugging **memory leaks** in production systems.

---

## Summary

- Python uses **reference counting** as its primary memory management mechanism.
- When an object's reference count reaches zero, it's freed immediately.
- **Circular references** can't be handled by reference counting alone.
- The **`gc` module** detects and collects circular references using generational collection.
- For most code, garbage collection is automatic and invisible — just let it work.

---

## Additional Resources
- [Python Docs — gc: Garbage Collector](https://docs.python.org/3/library/gc.html)
- [Real Python — Python Memory Management](https://realpython.com/python-memory-management/)
- [Python Docs — Data Model (object lifecycle)](https://docs.python.org/3/reference/datamodel.html)
