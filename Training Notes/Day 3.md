# Day 3 - Python: Collections, Exceptions, Scope, Modules

---

## Collections

### List
Ordered, mutable, allows duplicates.
```python
l = [1, 2, 3]
l.append(4)          # add to end
l.extend([5, 6])     # add multiple
l.insert(0, 99)      # insert at index
l.remove(99)         # remove first match (ValueError if missing)
l.pop()              # remove+return last (or l.pop(i) for index)
l.clear()            # empty list
l.index(3, 0, 4)     # find index of value (optional start/end)
l.count(2)           # occurrences of value
l.sort()             # in-place sort
l.sort(key=lambda x: x[1])  # sort by custom key
l.reverse()          # in-place reverse
l[::-1]              # reversed copy via slice
l.copy()             # shallow copy
```

### Set
Unordered, no duplicates.
```python
s = {1, 2, 3}
s.add(4)
s.remove(4)    # raises KeyError if missing
s.discard(4)   # no error if missing
s.pop()        # removes arbitrary element
# Tip: list(set(l)) removes duplicates from a list
```

### Tuple
Ordered, **immutable**.
```python
t = (1, 2, 3)
t.index("a")   # find index
t.count(1)     # occurrences
```

### Dictionary
Key-value pairs, keys must be hashable (can be strings, ints, None, or even function return values).
```python
d = {"key": "val", 100: "num", None: "ok"}
d["new"] = 5               # add/update
d.items()                  # dict_items of (key, val) tuples
d.keys() / d.values()
d.setdefault("k", "v")     # returns value if exists, else creates it
dict(sorted(d.items(), key=lambda item: map[item[0]]))  # sort by external map
```

---

## Exceptions

```python
try:
    5 / 0
except ZeroDivisionError:
    print("specific error")
except:                     # catch-all, goes last (specific → general)
    print("other error")
finally:
    print("always runs")
```

**Custom exceptions** — extend `Exception`, add `__init__` with `message`:
```python
class MyException(Exception):
    def __init__(self, message):
        self.message = message

raise MyException("Custom message")
# catch with: except MyException as e: print(e.message)
```

---

## Scope (LEGB)

- **Local** — inside a function
- **Enclosed** — inner function has access to its parent function's local variables
- **Global** — module-level, importable from other modules
- **Built-in** — Python built-ins

```python
name = "global"

def outer():
    name = "local"
    def inner():
        return name  # returns "local" (enclosed scope)
    return inner()
```
Reusing a name inside a function is **shadowing** — not recommended.

`dir()` lists names in current scope.

---

## Dunder (Magic) Methods

Override Python operators on custom classes:
```python
class Num:
    def __init__(self, n): self.number = n
    def __add__(self, other): return self.number + other * 100
    def __gt__(self, other): ...   # >
    def __sub__(self, other): ...  # -
    def __len__(self): ...         # len()
```
`__init__` → constructor, `__len__` → `len()`, `__add__` → `+`, `__sub__` → `-`, `__gt__` → `>`

---

## Modules & Packages

**Module** — a single `.py` file, imported by name.
**Package** — a directory with an `__init__.py` file.

```python
import module_four
module_four.ClassFour()         # fully qualified (preferred)

from package_one import ClassOneFun   # works because __init__.py exposes it
from package_two import module_two
module_two.ClassTwo()
```

**`__init__.py`** — runs when the package is imported; use it to control what gets exposed:
```python
from .module_one import ClassOne as ClassOneFun
__all__ = ["ClassOne"]
```
The `.` in `.module_one` is a **relative import** — refers to a file inside the same package.

---

## Useful Built-ins

| Function | What it does |
|---|---|
| `any(iterable)` | `True` if any element is truthy |
| `all(iterable)` | `True` if all elements are truthy |
| `enumerate(iterable)` | yields `(index, value)` pairs |
| `range(start, stop, step)` | integer range; `stop` is exclusive |
| `len(x)` | length |
| `sorted(iterable, key=...)` | returns new sorted list |
| `random.randint(a, b)` | random int inclusive of both ends |
| `input(prompt)` | reads a string from stdin |
| `int(x)` | convert to int (raises `ValueError` if invalid) |
| `dir()` | list names in current scope |

---

## List Comprehension

```python
flat = [item for row in grid for item in row]   # flatten 2D → 1D
squares = [x**2 for x in range(10) if x % 2 == 0]
```

## Lambda

Anonymous one-liner function:
```python
square = lambda x: x * x
# equivalent to:
def square(x): return x * x

# common use — sort key:
items.sort(key=lambda x: x[1])
```

## Slicing

```python
l[start:stop:step]
l[::-1]          # full reverse
l[i:i+width]     # sub-list from index i
```
