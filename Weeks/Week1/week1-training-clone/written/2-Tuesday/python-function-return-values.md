# Python Function Return Values

## Learning Objectives
- Understand how `return` works and what happens without it.
- Return multiple values using tuple unpacking.
- Recognize common return patterns in professional Python.

---

## Why This Matters

> **Weekly Epic Connection:** Every useful function produces a result. Understanding return values — including when to return `None`, how to return multiple values, and how to use return values in assertions — is essential for writing effective test functions and utilities.

---

## The Concept

### The `return` Statement

`return` does two things:
1. **Sends a value** back to the caller.
2. **Exits the function** immediately (no code after `return` executes).

```python
def add(a, b):
    return a + b

result = add(3, 7)  # result = 10
```

### Implicit Return (`None`)

A function without `return` (or with a bare `return`) returns `None`:

```python
def log_message(msg):
    print(f"[LOG] {msg}")
    # No return statement

result = log_message("starting...")
print(result)  # None
```

```python
def check(value):
    if value < 0:
        return  # Returns None explicitly (bare return)
    return value * 2
```

### Returning Multiple Values

Python lets you return multiple values as a **tuple**:

```python
def min_max(numbers):
    """Return both the minimum and maximum of a list."""
    return min(numbers), max(numbers)

# Tuple unpacking
lo, hi = min_max([4, 1, 7, 2, 9])
print(f"Min: {lo}, Max: {hi}")  # Min: 1, Max: 9

# Without unpacking — it's a tuple
result = min_max([4, 1, 7, 2, 9])
print(result)       # (1, 9)
print(type(result))  # <class 'tuple'>
```

### Returning Different Types Conditionally

Python doesn't enforce a single return type, but it's good practice to be consistent:

```python
# ⚠️ Inconsistent return types — harder to use
def find_user(user_id):
    if user_id == 1:
        return {"name": "Alice", "id": 1}
    return None  # Explicitly return None for "not found"

# ✅ Caller handles both cases
user = find_user(99)
if user is not None:
    print(f"Found: {user['name']}")
else:
    print("User not found")
```

### Common Return Patterns

**Boolean check functions:**
```python
def is_even(n):
    return n % 2 == 0

def has_duplicates(items):
    return len(items) != len(set(items))
```

**Early return (guard clauses):**
```python
def process_order(order):
    if not order:
        return {"error": "Empty order"}
    if order["total"] <= 0:
        return {"error": "Invalid total"}

    # Main logic — only reached if guards pass
    tax = order["total"] * 0.08
    return {"total": order["total"] + tax, "tax": tax}
```

**Returning the result of an expression:**
```python
def clamp(value, low, high):
    """Restrict value to the range [low, high]."""
    return max(low, min(value, high))
```

### Discarding Return Values

If you don't need the return value, you can simply not capture it:

```python
my_list = [3, 1, 2]
my_list.sort()  # Returns None; sorts in place — we don't capture it

# Convention: use _ for intentionally unused values
_, extension = "report.csv".rsplit(".", 1)
print(extension)  # "csv"
```

---

## Summary

- `return` sends a value to the caller and exits the function.
- Functions without `return` (or with bare `return`) return `None`.
- Return multiple values using tuples and **unpack** them at the call site.
- Use **early returns** (guard clauses) to keep your code flat and readable.
- Be consistent with return types — callers shouldn't have to guess.

---

## Additional Resources
- [Python Docs — The return Statement](https://docs.python.org/3/reference/simple_stmts.html#the-return-statement)
- [Real Python — Python Return Statement](https://realpython.com/python-return-statement/)
- [PEP 8 — Programming Recommendations](https://peps.python.org/pep-0008/#programming-recommendations)
