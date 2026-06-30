# Day 5 - Decorators, Logging, SQLite & More

---

## Decorators

A decorator **wraps a function** to inject behavior before and/or after it runs, without modifying the function itself.

The pattern: an outer function takes the target function as an argument, defines an inner `wrapper` that calls it, and returns the wrapper. The `@syntax` is shorthand — `@my_decorator` above a function is identical to `func = my_decorator(func)`.

```python
def my_decorator(func):
    def wrapper():
        print("Before")
        func()              # call the original function
        print("After")
    return wrapper          # return wrapper, NOT wrapper() — don't call it

@my_decorator               # same as: say_hello = my_decorator(say_hello)
def say_hello():
    print("Hello!")
```

**Decorator that works with any function signature** — use `*args, **kwargs` in the wrapper so it passes through whatever arguments the wrapped function expects:

```python
def my_decorator(func):
    def wrapper(*args, **kwargs):
        print("Starting...")
        result = func(*args, **kwargs)   # forward all args to the original
        print("Done.")
        return result                    # don't forget to return the result
    return wrapper

@my_decorator
def add(a, b):
    return a + b

@my_decorator               # same decorator, different function — reusable
def multiply(a, b):
    return a * b
```

**Practical use — access control:**
```python
def require_admin(func):
    def wrapper(user):
        if user != "admin":
            print("Access Denied!")
            return             # early return — original function never runs
        func(user)
    return wrapper

@require_admin
def delete_database(user):
    print("Database deleted")
```

---

## `collections.deque`

Double-ended queue — O(1) append and pop from **both** ends (a list is O(n) for the left side).

```python
from collections import deque

d = deque([1, 2, 3])
d.append(4)       # add to right  → deque([1, 2, 3, 4])
d.appendleft(0)   # add to left   → deque([0, 1, 2, 3, 4])
d.pop()           # remove right  → returns 4
d.popleft()       # remove left   → returns 0

list(d)           # convert to regular list
```

Useful when you need to build something symmetrically from both ends (e.g., centering content).

---

## Generators — `next()`

`next(generator)` manually advances a generator one step at a time. The function body runs until it hits `yield`, pauses, and resumes on the next `next()` call.

```python
def gen():
    for x in range(1_000_000_000_000):
        yield x             # only computes one value at a time — never fills memory

g = gen()
print(next(g))   # 0
print(next(g))   # 1 — picks up exactly where it left off
```

Raises `StopIteration` when exhausted. A `for` loop handles this automatically.

---

## Functional Programming

### `zip()`
Pairs up elements from multiple iterables into tuples, stopping at the shortest:

```python
names  = ["Alice", "Bob"]
scores = [85, 92]

pairs = list(zip(names, scores))          # [("Alice", 85), ("Bob", 92)]
d     = dict(zip(names, scores))          # {"Alice": 85, "Bob": 92}
```

### `functools.reduce()`
Repeatedly applies a function to accumulate a list down to a single value (left to right):

```python
from functools import reduce

l = [1, 2, 3, 4, 5]
total = reduce(lambda x, y: x + y, l)    # ((((1+2)+3)+4)+5) = 15
```

### `min()` / `max()` as a clamp
```python
min(score, 100)   # caps score at 100 — if score > 100, returns 100
max(score, 0)     # floors score at 0  — if score < 0, returns 0
```

---

## SQLite — `sqlite3`

Built-in module for a local file-based SQL database. No server needed.

**The pattern: connect → cursor → execute → fetch/commit → close**

```python
import sqlite3

conn = sqlite3.connect('students.db')   # creates file if it doesn't exist
c = conn.cursor()                       # cursor executes SQL statements

c.execute("""CREATE TABLE IF NOT EXISTS students (name TEXT, age INTEGER)""")
c.execute("""INSERT INTO students VALUES ('Mark', 43), ('Irving', 65)""")
c.execute("""SELECT * FROM students""")

rows = c.fetchall()      # returns list of tuples: [('Mark', 43), ('Irving', 65)]
for row in rows:
    print(row)

conn.commit()            # save INSERT/UPDATE/DELETE changes to disk
conn.close()             # always close when done
```

**Key SQL in this context:**

| SQL | What it does |
|---|---|
| `CREATE TABLE IF NOT EXISTS t (col TYPE)` | create table, skip if exists |
| `INSERT INTO t VALUES (...)` | insert row(s) |
| `SELECT * FROM t` | fetch all rows and columns |
| `TEXT`, `INTEGER` | SQLite column types |

---

## Logging

The `logging` module is the right way to record what an application is doing. Unlike `print`, it supports levels, filters, timestamps, and multiple output destinations simultaneously.

### Level hierarchy (low → high)
`DEBUG` → `INFO` → `WARNING` → `ERROR` → `CRITICAL`

A handler set to `INFO` will receive `INFO`, `WARNING`, `ERROR`, and `CRITICAL` — but not `DEBUG`.

### Logger vs Handler
- **Logger** — the source (you call `.debug()`, `.info()`, etc. on it)
- **Handler** — the destination (console, file, etc.) — one logger can have many handlers

```python
import logging

# Quick setup — root logger to stdout
logging.basicConfig(
    level=logging.DEBUG,
    format="%(asctime)s | %(levelname)-8s | %(message)s",
    datefmt="%H:%M:%S",
    force=True          # reset any previous config
)

# Named logger (preferred over root logger)
logger = logging.getLogger("my_app")
logger.setLevel(logging.DEBUG)

# Console handler — INFO and above
console = logging.StreamHandler()
console.setLevel(logging.INFO)
console.setFormatter(logging.Formatter(" %(levelname)-8s | %(message)s"))

# File handler — DEBUG and above
file_handler = logging.FileHandler("app.log", mode="w")   # mode="w" overwrites on start
file_handler.setLevel(logging.DEBUG)
file_handler.setFormatter(logging.Formatter(
    "%(asctime)s | %(levelname)-8s | %(name)s:%(lineno)d | %(message)s",
    datefmt="%Y-%m-%d %H:%M:%S"
))

logger.addHandler(console)
logger.addHandler(file_handler)

logger.debug("Detailed info")
logger.info("Normal operation")
logger.warning("Something unexpected")
logger.error("Something failed")
logger.critical("System down")
```

**Logging exceptions** — use `%s` placeholders, not f-strings (logging is lazy — only formats if the message is actually emitted):
```python
logger.error("Division by zero: %s / %s", a, b)
```

**Format placeholders:**

| Placeholder | Value |
|---|---|
| `%(asctime)s` | timestamp |
| `%(levelname)s` | `DEBUG`, `INFO`, etc. |
| `%(message)s` | the log message |
| `%(name)s` | logger name |
| `%(lineno)d` | line number in source file |
| `%-8s` | left-align in 8 chars (padding) |

---

## `match` / `case` — Structural Pattern Matching

Python 3.10+. Cleaner than long `if/elif` chains when switching on a single value.

```python
match action:
    case "a":
        my_car.accelerate()
    case "b":
        my_car.brake()
    case "o":
        print(f"Odometer: {my_car.odometer}")
```

No `break` needed between cases — Python doesn't fall through.

---

## `while True:` — Infinite Loop

Runs forever until an explicit `break`. Common for interactive input loops:

```python
while True:
    action = input("Choose (a/b/q): ")
    if action == "q":
        break
    if action not in "ab":
        continue       # re-prompt without doing anything
    # handle valid action
```

---

## String Interning

CPython automatically reuses the same object for **identifier-like strings** (letters, digits, underscores only). This means `is` can return `True` for two separately assigned string variables — but only by coincidence of the interpreter's optimization, not a language guarantee.

```python
a = 'config_key'
b = 'config_key'
a is b   # True  — auto-interned (identifier-like)

word = 'hello'
a = word + ' world'   # runtime construction
b = word + ' world'
a is b   # False — runtime strings are NOT interned

# Force interning manually (useful for heavy dict lookups on the same keys)
import sys
a = sys.intern(word + ' world')
b = sys.intern(word + ' world')
a is b   # True
```

**Key rule:** always use `==` to compare string *values*. `is` compares *identity* (same object in memory) — only use it for `None`, `True`, `False`.

---

## `__name__` in Modules

`__name__` equals `"__main__"` when a file is run directly, and equals the module's filename when it's imported. Useful for seeing the import path in a module:

```python
# car_module.py
print(__name__)   # prints "car_module" when imported, "__main__" when run directly

# other_module.py
import car_module as cm   # __name__ inside car_module is "car_module"
print(__name__)           # prints "__main__" — this is the entry point
```

---

## `"".join(iterable)`

Joins a list of strings into one string using the separator before the dot:

```python
"".join(["*", " ", "*"])     # "* *"   — no separator
" ".join(["a", "b", "c"])    # "a b c" — space separator
"".join(list(deque_obj))     # convert deque of chars → single string
```

---

## Useful Built-ins Added

| Function / Module | What it does |
|---|---|
| `next(generator)` | advance generator one step, returns next yielded value |
| `zip(a, b, ...)` | pair elements from multiple iterables into tuples |
| `min(a, b)` / `max(a, b)` | also works as a clamp: `min(value, cap)` |
| `functools.reduce(fn, iterable)` | fold iterable to single value with accumulator |
| `sys.intern(string)` | force string interning — reuse same object in memory |
| `sqlite3.connect(file)` | open/create a SQLite database file |
