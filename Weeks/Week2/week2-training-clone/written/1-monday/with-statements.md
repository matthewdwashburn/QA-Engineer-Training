# Context Managers and the `with` Statement

## Learning Objectives
- Explain what a context manager is and why it matters for resources like files.
- Use the `with` statement so files and locks close or release reliably.
- Recognize how `__enter__` and `__exit__` implement the context manager protocol.
- Use `contextlib` helpers (`contextmanager`, `closing`) for simple cases.

---

## Why This Matters

> **Weekly Epic Connection:** *From Python Mastery to Java Foundations* — Before you leave Python, you must handle files and small services safely. Context managers are how professional Python code avoids leaked file handles, database connections, and network sockets. They guarantee **setup** and **teardown** in a predictable order — exactly what you want in automation pipelines and test fixtures.

Without `with`, it is easy to forget `close()` after an exception. Context managers guarantee **setup** and **teardown** in a predictable order.

### The Problem Without `with`

```python
# ❌ Fragile: if f.read() raises an exception, f.close() never runs
f = open("data.txt")
data = f.read()       # Could raise UnicodeDecodeError, PermissionError, etc.
f.close()             # Skipped if the line above throws!

# The file remains open, locking it on Windows until GC or process exit
```

### The Solution: `with`

```python
# ✅ Safe: f.close() is guaranteed even if f.read() raises
with open("data.txt") as f:
    data = f.read()
# f is closed automatically when the block ends — on success OR exception
```

---

## The Concept

### The problem

```python
f = open("data.txt")
data = f.read()
# If read() or later code raises, f.close() might never run.
f.close()
```

### The solution: `with`

```python
with open("data.txt") as f:
    data = f.read()
# f is closed automatically when the block ends—even on exceptions.
```

The `open()` built-in returns a **file object** that implements the **context manager protocol**.

### The protocol

A context manager defines:

1. **`__enter__(self)`** — runs when entering the `with` block; its return value is bound to `as name`.
2. **`__exit__(self, exc_type, exc_val, exc_tb)`** — runs when leaving the block (normally or via exception). If it returns `False`, exceptions propagate; if it swallows them, return `True` (rare for learners).

### Custom context manager (class)

```python
class ManagedResource:
    def __enter__(self):
        print("acquire")
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        print("release")
        return False  # do not suppress exceptions

with ManagedResource() as r:
    print("inside")
```

### `contextlib.contextmanager`

Turn a generator into a context manager:

```python
from contextlib import contextmanager

@contextmanager
def managed_tag(name):
    print(f"start {name}")
    try:
        yield name
    finally:
        print(f"end {name}")

with managed_tag("job") as n:
    print(f"working on {n}")
```

`yield` separates “enter” (before) from “exit” (`finally`).

### `contextlib.closing`

Ensures `.close()` is called on objects that have `close()` but are not full context managers:

```python
from contextlib import closing
from urllib.request import urlopen

with closing(urlopen("https://example.com")) as response:
    body = response.read(200)
```

### `contextlib.suppress` — Silently Ignore Specific Exceptions

`suppress` is a context manager that catches and discards specified exception types, replacing a `try/except: pass` pattern:

```python
from contextlib import suppress
import os

# Instead of:
try:
    os.remove("temp_file.txt")
except FileNotFoundError:
    pass

# Use:
with suppress(FileNotFoundError):
    os.remove("temp_file.txt")

# Multiple exception types
with suppress(FileNotFoundError, PermissionError):
    os.remove("temp_file.txt")
```

### `contextlib.ExitStack` — Dynamic Resource Management

`ExitStack` lets you manage a **variable number of resources** — useful when you don't know at write time how many context managers you need:

```python
from contextlib import ExitStack
from pathlib import Path

# Open a dynamic list of files
log_files = ["app.log", "db.log", "api.log"]

with ExitStack() as stack:
    handles = [
        stack.enter_context(open(f, encoding="utf-8"))
        for f in log_files
    ]
    # All files are open; all will be closed when the block exits
    for handle in handles:
        for line in handle:
            if "ERROR" in line:
                print(f"{handle.name}: {line.rstrip()}")
```

> **ExitStack** is the professional solution when you need to open a list of files, database connections, or network sockets whose count is determined at runtime.

### `contextlib.nullcontext` — Optional Context Manager

`nullcontext` is a no-op context manager — useful when a function optionally wraps its work in a context manager:

```python
from contextlib import nullcontext

def process(data, file=None):
    """Process data, optionally writing output to a file."""
    # If file is given, open it; otherwise use a no-op context
    cm = open(file, "w", encoding="utf-8") if file else nullcontext()
    with cm as out:
        result = transform(data)
        if out:   # out is None when nullcontext is used
            out.write(result)
    return result

process(data)                     # No file — nullcontext does nothing
process(data, file="output.txt")  # Write to file
```

---

## Code Example: nested `with`

```python
with open("in.txt") as src, open("out.txt", "w") as dst:
    dst.write(src.read().upper())
```

Both files close when the block ends.

---

## Summary

- Use **`with`** for anything that must be **released** (files, locks, connections).
- Context managers implement **`__enter__` / `__exit__`** (or use **`@contextmanager`**).
- **`contextlib`** reduces boilerplate for simple setup/teardown patterns.

---

## Additional Resources

- [Python docs: The `with` statement](https://docs.python.org/3/reference/compound_stmts.html#the-with-statement)
- [Python docs: contextlib](https://docs.python.org/3/library/contextlib.html)
- [PEP 343 – The “with” Statement](https://peps.python.org/pep-0343/)
