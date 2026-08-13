# String Interning

## Learning Objectives
- Explain what string interning is and the memory optimization problem it solves.
- Understand exactly when Python automatically interns strings.
- Use `id()` to inspect object identity in memory.
- Clearly distinguish `is` (identity) from `==` (equality) and know when to use each.
- Use `sys.intern()` to force interning of arbitrary strings.

---

## Why This Matters

> **Weekly Epic Connection:** Understanding identity vs. equality prevents subtle bugs, especially in test assertions. Knowing *when* `is` works and *why* it can silently produce wrong results for seemingly identical values is a must-know Python concept. Test engineers who confuse `is` with `==` produce assertions that pass or fail unpredictably across environments.

---

## The Concept

### Identity vs. Equality — A Crucial Distinction

Python objects have two concepts of "sameness":

| Operator | Meaning | Example |
|----------|---------|---------|
| `==` | **Value equality** — do they contain the same data? | `[1,2,3] == [1,2,3]` → `True` |
| `is` | **Identity** — are they the *exact same object* in memory? | `[1,2,3] is [1,2,3]` → `False` |

```python
a = [1, 2, 3]
b = [1, 2, 3]

a == b    # True  — same VALUE
a is b    # False — DIFFERENT objects, same content
```

Two objects can be **equal** (`==`) without being **identical** (`is`). Think of two identical-looking keys on a keyring — they open the same lock, but they are physically different objects.

---

### `id()` — The Object's Memory Address

Every Python object has a unique ID, which in CPython is its memory address:

```python
a = [1, 2, 3]
b = [1, 2, 3]
c = a           # c is an ALIAS — points to the same object as a

print(id(a))    # e.g., 140234567890
print(id(b))    # e.g., 140234567960  ← different!
print(id(c))    # e.g., 140234567890  ← same as a!

print(a is c)   # True  — same object in memory
print(a is b)   # False — same value, different objects
```

When you write `c = a`, you are **not** copying the list — you are creating a second name that refers to the same underlying object. Any change through `c` will be visible through `a`:

```python
c.append(4)
print(a)   # [1, 2, 3, 4]  ← modified via c, visible through a!
```

---

### What Is String Interning?

**String interning** is a memory optimization technique where Python maintains an **intern table** — a global dictionary that maps string values to a single canonical object. When an eligible string is created, Python first checks the intern table:

- If the value already exists in the table → return the existing object (no allocation).
- If it does not → create a new object and add it to the table.

The result: multiple variables holding the same interned string value all point to the *same object* in memory:

```python
a = "hello"
b = "hello"

print(a == b)   # True  — same value
print(a is b)   # True  — same object! (automatically interned)
print(id(a) == id(b))  # True
```

This is why `a is b` is `True` for simple strings but `False` for lists — Python never automatically merges lists, only eligible strings.

---

### When Does Python Automatically Intern Strings?

Python's automatic interning is an **implementation detail** of CPython (the standard Python interpreter). It is not guaranteed by the language specification, but the practical rules are:

1. **Identifier-like strings** — contain only letters, digits, and underscores, and look like a valid Python identifier.
2. **String literals** in source code that the compiler can recognize as compile-time constants.
3. **Short strings** — typically interned at compile time; very long strings usually are not.

```python
# ✅ Automatically interned (looks like an identifier)
a = "hello"
b = "hello"
print(a is b)    # True

# ✅ Also interned (single characters are always interned)
x = "a"
y = "a"
print(x is y)   # True

# ❌ NOT automatically interned (contains a space)
a = "hello world"
b = "hello world"
print(a is b)   # False (most implementations — not guaranteed)

# ❌ NOT interned when dynamically created at runtime
a = "hello"
b = "hel" + "lo"    # This IS interned — compiler can optimize constants
c = "".join(["h", "e", "l", "l", "o"])   # This is NOT — computed at runtime

print(a is b)   # True  (compile-time constant folding)
print(a is c)   # False (runtime result)
print(a == c)   # True  (same value)
```

> **Key takeaway:** Never rely on interning behavior in application or test code. The rules vary by Python version and implementation.

---

### Integer "Interning" — Small Integer Caching

Python also caches (interns) small integers in the range **-5 to 256**. These integers are pre-created at interpreter startup and reused:

```python
a = 256
b = 256
print(a is b)   # True — within the cached range

a = 257
b = 257
print(a is b)   # False — outside cached range (may vary by implementation)

# The cached range works for all ways of obtaining the value
x = 100 + 100   # 200 — cached
y = 200
print(x is y)   # True
```

The motivation is the same as string interning: small integers (loop counters, array indices, boolean values) are used constantly, so pre-allocating them avoids millions of tiny memory allocations.

---

### Forcing Interning with `sys.intern()`

When you *know* a string will be used repeatedly as a lookup key (e.g., thousands of dictionary lookups), you can force it into the intern table:

```python
import sys

# Force interning of any string
a = sys.intern("hello world")
b = sys.intern("hello world")

print(a is b)   # True — both forced into the intern table

# Practical use: large-scale string deduplication
# If you parse a 100MB log file and every line contains the same 50 field names,
# interning those field names avoids thousands of redundant string allocations.
```

**When to use `sys.intern()`:**
- String keys in dictionaries that are looked up millions of times.
- Parsing large files with many repeated field/tag names.
- Building high-performance compilers or interpreters in Python.

**When NOT to use it:**
- In normal application or test code — the overhead and complexity are not worth it.

---

### The `is` vs. `==` Rule — The Definitive Guide

```python
# ✅ CORRECT: Use == for all value comparisons
if response_code == 200:
    print("Success")

if username == "admin":
    print("Admin access")

if name == "Alice":
    print("Found Alice")

# ✅ CORRECT: Use 'is' ONLY for None, True, False
# (These are singletons — there is only one None, one True, one False in a process)
if result is None:
    print("No result returned")

if debug_mode is True:
    print("Debug mode on")

# ❌ WRONG: Never use 'is' for string or number value comparisons
if name is "Alice":         # Might work by accident (interning), but WRONG
    print("Found Alice")    # Can fail on different Python implementations

if count is 1000:           # 1000 is outside the integer cache — will fail
    print("One thousand")
```

**The rule, stated plainly:**
- `==` is for **data comparison** (values you compute or receive).
- `is` is for **singleton comparison**: `None`, `True`, `False` — and nothing else in production code.

---

### Why This Matters in Test Assertions

```python
# ❌ Fragile assertion — relies on interning
assert test_name is "login"      # May fail depending on how test_name was created

# ✅ Correct assertion
assert test_name == "login"      # Always compares values correctly

# ❌ Checking for no result
assert result is not None        # ✅ Correct — None is always a singleton
assert result != None            # Works but violates Python style conventions
```

Linters like `flake8` and `pylint` will flag `x is "string"` comparisons with an `E712` or `SyntaxWarning` in Python 3.8+ because the behavior is unreliable.

---

## Summary

- `==` checks **value equality**; `is` checks **identity** (same object in memory).
- `id()` returns an object's unique memory identifier (its address in CPython).
- **String interning:** Python reuses a single object for eligible strings (identifier-like, compile-time constants). This is an optimization detail — never rely on it.
- **Small integer caching:** Integers `-5` to `256` are pre-allocated and always identical.
- `sys.intern()` forces arbitrary strings into the intern table — use only for performance-critical scenarios.
- **The golden rule:** Use `==` for values. Use `is` **only** for `None`, `True`, and `False`.
- In test assertions, always use `==` for string and number comparisons.

---

## Additional Resources
- [Python Docs — Comparisons (`is` vs `==`)](https://docs.python.org/3/reference/expressions.html#comparisons)
- [Real Python — Python Internals: String Interning](https://realpython.com/lessons/string-interning/)
- [Python Docs — `id()`](https://docs.python.org/3/library/functions.html#id)
- [Python Docs — `sys.intern()`](https://docs.python.org/3/library/sys.html#sys.intern)
