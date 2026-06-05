# Day 4 - OOP, Flask & Python Internals

- Python has no compile time — errors only surface at **runtime**
- Search all text in a directory: `grep -rn "text" . --exclude-dir="venv" --exclude-dir=".git"`

---

## Pass by Object Reference

Variables are **references** to objects, not copies. Assigning `l2 = l1` makes both point to the same object — mutating one mutates the other.

```python
l1 = [0, 1, 2]
l2 = l1
l1.append(3)
print(l2)       # [0, 1, 2, 3] — same object, not a copy

id(l1)          # memory address of the object — id(l1) == id(l2) after assignment
```

Same applies when passing objects to functions — no copying happens.

---

## Raw Strings

```python
s = r'this\has\no\special\characters'   # backslashes treated literally, no escape sequences
```

---

## Loop Control

```python
for value in sequence:
    if value is None:
        continue    # skip rest of this iteration, move to next
    if value == 5:
        break       # exit the loop entirely
```

---

## Comprehensions

```python
# List
words = [word.upper() for word in list_strings if len(word) > 2]

# Nested list — outer loop first, then inner (same order as a nested for loop)
flat = [x for outer in nested for x in outer if condition]

# Set — unique values only, unordered
lengths = {len(word) for word in list_strings}

# Dict
loc_map = {index: val for index, val in enumerate(list_strings)}

# List of lists (not flattened — inner comprehension produces each row)
list_of_lists = [[x for x in tup] for tup in some_tuples]
```

---

## Lambda & `Callable` Type Hint

```python
from typing import Callable

double: Callable[[int], int] = lambda x: x * 2    # Callable[[arg_types], return_type]
max_val = lambda x, y: x if x > y else y           # multi-arg lambda
```

---

## First-Class Functions

Functions are objects — store them in lists, pass them as arguments.

```python
# Pipeline: apply a list of functions in sequence
clean_ops = [str.strip, remove_punctuation, str.title]   # str.strip is a function reference
for fn in clean_ops:
    value = fn(value)

# filter() — keep elements where lambda is True (lazy, wrap in list() to evaluate)
list(filter(lambda x: x.count('e') >= 2, flat_list))

# map() — apply a function to every element (also lazy)
list(map(remove_punctuation, states))

# sum(nested_list, []) — flattens one level of nesting using [] as start value
sum([['a', 'b'], ['c']], [])   # ['a', 'b', 'c']
```

---

## `re.sub()`

```python
import re
re.sub('[!#?]', '', value)                  # remove all matched characters
re.sub('[!#?]', '', value, flags=re.I)      # case-insensitive flag
```

---

## OOP — Four Pillars

### Encapsulation
Bundling data and behavior in a class, controlling access with naming conventions:

```python
self.name       # public    — accessible anywhere
self._salary    # protected — convention: don't use outside class/subclasses (not enforced)
self.__id       # private   — Python applies name mangling: renamed to _ClassName__id internally
```

**Getter** = controlled read access to a private attribute:
```python
def get_employee_id(self):
    return self.__employee_id
```

**Name mangling bypass** (works but is intentional bad practice):
```python
emp._Employee__employee_id
```

### Abstraction
Promise the *what*, defer the *how* to subclasses. The base class can't be instantiated directly.

```python
from abc import ABC, abstractmethod

class Employee(ABC):
    @abstractmethod
    def calculate_pay(self):
        pass    # every subclass MUST implement this or instantiation raises TypeError
```

### Inheritance
Subclass receives all attributes and methods of its parent automatically.

```python
class SalariedEmployee(Employee):     # gets Employee's __init__, display_info, etc.
    def calculate_pay(self):
        return self._salary

class HourlyEmployee(Employee):
    def __init__(self, name, rate, hours):
        super().__init__(name, rate * hours)   # delegate shared setup to parent first
        self.hourly_rate = rate
        self.hours_worked = hours
```

### Polymorphism
Same method name, different behavior per class. Callers don't need to know which subclass they have.

```python
emp.calculate_pay()          # returns fixed salary
hourly_emp.calculate_pay()   # returns rate × hours
# same interface, different result
```

**Method overriding** — subclass defines its own version of a parent method, replacing it:
```python
class HourlyEmployee(Employee):
    def display_info(self):   # replaces Employee.display_info entirely
        ...
```

### Class Variable
One copy shared across all instances — good for counters, bad for mutable objects.

```python
class Employee(ABC):
    _id_counter = 1000              # owned by the class, not any instance

    def __init__(self, ...):
        Employee._id_counter += 1   # always reference via class name, not self
        self.__employee_id = Employee._id_counter
```

**Pitfall:** never use a mutable object (list, dict) as a class variable — all instances share the same object and will interfere with each other.

---

## Flask

Lightweight Python web framework for building HTTP APIs.

```python
from flask import Flask, request

app = Flask(__name__)   # __name__ tells Flask where to find resources relative to this file
```

### Routes & URL Parameters

```python
@app.route("/", methods=["GET"])
def hello_world():
    return "Hello World"

@app.route("/greeting/<name>", methods=["GET"])   # <name> captured as a string argument
def greeting(name: str):
    return f"Hello {name}"

@app.get("/data")    # shorthand decorator for GET-only routes
def query_database():
    ...

app.run()            # starts dev server on localhost:5000
```

### Request Data

```python
# POST — parse JSON body
credentials = request.get_json()        # returns dict
username = credentials["username"]

# GET — query string params  (?DB=1)
query = request.args.get("DB", "")      # second arg is default if key missing
```

### `global` Keyword

Required to modify a module-level variable from inside a function — without it, Python treats the name as a new local variable and raises `UnboundLocalError`.

```python
count = 0

@app.route("/count", methods=["PUT"])
def add_count():
    global count
    count += 1
    return f"The count is {count}"
```

### Testing with curl

```bash
curl http://localhost:5000/greeting/Matt
curl -X POST http://localhost:5000/login -H "Content-Type: application/json" -d '{"username":"good","password":"correct"}'
curl "http://localhost:5000/data?DB=1"
curl -X PUT http://localhost:5000/count
```

---

## Recursion

A function that calls itself. Requires a **base case** (stops recursion) and a **recursive case** (calls itself with a smaller problem).

```python
def superDigit(n, k):
    total = sum(int(c) for c in n) * k   # sum digits of n, multiply by k
    if len(str(total)) > 1:
        return superDigit(str(total), 1) # recursive case: still more than one digit
    else:
        return total                     # base case: single digit, done
```

Without a base case the function recurses forever and hits Python's recursion limit (`RecursionError`).

---

## Throwaway Variable `_`

Use `_` when a loop variable is needed syntactically but the value is never used:

```python
for _ in range(customer_count):   # "repeat n times, I don't need the index"
    size, offer = input().split()
```

Also useful for unpacking when you want to ignore part of a tuple:
```python
first, _ = some_tuple   # discard second value
```

---

## `input().split()`

`input()` returns the whole line as a string. Chain `.split()` to break it on whitespace into a list:

```python
stock = Counter(input().split())   # "7 6 6 5" → Counter({'6': 2, '7': 1, '5': 1})
size, offer = input().split()      # "6 100" → size="6", offer="100"
```

---

## Generators

Lazy sequences — yields values one at a time, only computing on demand (memory efficient for large data).

```python
def squares(n):
    for i in range(1, n + 1):
        yield i ** 2    # pauses here, returns value; resumes on next request

gen = squares(5)        # no code runs yet
for x in gen:           # execution starts here, one yield at a time
    print(x)

# Generator expression — parens instead of brackets, same syntax as list comprehension
gen = (x ** 2 for x in range(100))
sum(x ** 2 for x in range(100))    # pass directly to sum/min/max without list()
```

---

## Libraries

| Library | What it does | Key syntax |
|---|---|---|
| `matplotlib.pyplot` (`plt`) | Plotting / saving charts | `plt.plot(data)`, `plt.savefig("file.png")` |
| `numpy` (`np`) | Fast array math, no overflow | `np.array()`, `np.zeros()`, `np.random.rand(n).cumsum()` |
| `pandas` (`pd`) | DataFrames / data analysis | `pd.DataFrame(data, columns=[...])`, `df.groupby("col")` |
| `collections` | Specialized data structures | `Counter()` — tallies occurrences of items |
| `itertools` | Iteration utilities | `groupby(seq, key_fn)` — groups consecutive elements |
| `datetime` | Dates and times | `datetime(y,m,d)`, `.strftime('%m/%d/%Y')`, `.strptime(s, fmt)`, `timedelta` |
| `flask` | Web API framework | Routes, JSON body, query params |
| `re` | Regex | `re.sub()`, `re.search()`, `re.fullmatch()` |
| `sys` | System info | `sys.version` |

### numpy quick reference
```python
import numpy as np
np.random.rand(50).cumsum()     # 50 random floats, cumulative sum
np.array([1, 2, 3])             # create array from list
np.zeros((3, 6))                # 3×6 array of zeros
np.arange(15)                   # [0..14]
data.shape                      # (rows, cols) tuple
data.dtype                      # dtype('float64') etc.
```

### datetime quick reference
```python
from datetime import datetime
dt = datetime(2022, 1, 27, 20, 35, 15)
dt.strftime('%m/%d/%Y %H:%M')           # → '01/27/2022 20:35'
datetime.strptime('20220214', '%Y%m%d') # string → datetime
dt2 - dt                                # → timedelta(days=..., seconds=...)
dt.replace(minute=0, second=0)          # new datetime, immutable
```

### collections quick reference
```python
import collections as col
cnt = col.Counter()
for word in ['red', 'blue', 'red']:
    cnt[word] += 1
# Counter({'red': 2, 'blue': 1})
```
