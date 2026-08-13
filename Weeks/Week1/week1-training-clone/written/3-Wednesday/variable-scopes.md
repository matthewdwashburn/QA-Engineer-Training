# Variable Scopes

## Learning Objectives
- Explain the LEGB rule: Local, Enclosing, Global, Built-in scopes.
- Understand when variables are accessible and when they're not.
- Use the `global` keyword appropriately (and know when to avoid it).

---

## Why This Matters

> **Weekly Epic Connection:** Scope bugs are among the most common and confusing errors for new Python developers. Understanding *where* a variable lives and *when* it's accessible prevents mysterious `NameError` exceptions and unintended side effects in your functions.

---

## The Concept

### What Is Scope?

**Scope** determines where a variable is accessible. A variable defined inside a function isn't visible outside it, and vice versa (without special keywords).

### The LEGB Rule

Python resolves variable names by searching four scopes in order:

```
L — Local:     Inside the current function
E — Enclosing: Inside any enclosing (outer) function
G — Global:    At the module (file) level
B — Built-in:  Python's built-in names (print, len, etc.)
```

Python checks **L → E → G → B** and stops at the first match.

### Local Scope

Variables created inside a function exist only within that function:

```python
def greet():
    message = "Hello"  # Local variable
    print(message)

greet()          # "Hello"
# print(message) # ❌ NameError: name 'message' is not defined
```

### Global Scope

Variables defined at the module level (outside any function):

```python
name = "Alice"  # Global variable

def greet():
    print(f"Hello, {name}")  # ✅ Can READ global variables

greet()  # "Hello, Alice"
```

**Reading** global variables from inside a function works fine. But **modifying** them requires the `global` keyword:

```python
counter = 0

def increment():
    global counter    # Declare intent to modify the global
    counter += 1

increment()
increment()
print(counter)  # 2
```

Without `global`, Python would create a new local variable:

```python
counter = 0

def increment():
    counter = counter + 1  # ❌ UnboundLocalError!
    # Python sees the assignment and treats 'counter' as local,
    # but it hasn't been defined locally yet.
```

### Enclosing Scope

When functions are nested, the inner function can access variables from the outer function:

```python
def outer():
    x = 10              # Enclosing scope for inner()

    def inner():
        print(f"x = {x}")  # Accesses x from enclosing scope

    inner()

outer()  # x = 10
```

To **modify** an enclosing variable, use `nonlocal`:

```python
def counter_factory():
    count = 0

    def increment():
        nonlocal count
        count += 1
        return count

    return increment

counter = counter_factory()
print(counter())  # 1
print(counter())  # 2
```

### Built-in Scope

Python's built-in functions (`print`, `len`, `type`, `range`, etc.) are always available:

```python
# You can use built-ins anywhere — they're in the outermost scope
print(len("hello"))  # 5
```

### LEGB Lookup Example

```python
x = "global"

def outer():
    x = "enclosing"

    def inner():
        x = "local"
        print(x)    # Which x? → "local" (L wins)

    inner()

outer()
```

If we remove the local `x`:

```python
def outer():
    x = "enclosing"

    def inner():
        print(x)    # → "enclosing" (E is next)

    inner()
```

### Why Avoid `global`

Using `global` creates hidden dependencies between functions and module-level state. This makes code harder to test, debug, and reason about:

```python
# ❌ Hard to test — depends on global state
total = 0

def add_to_total(amount):
    global total
    total += amount

# ✅ Better — pure function, no hidden dependencies
def add(current_total, amount):
    return current_total + amount
```

**Rule of thumb:** If you're reaching for `global`, consider passing values as parameters and returning results instead.

---

## Summary

- **LEGB rule:** Python searches Local → Enclosing → Global → Built-in for names.
- **Local** variables exist only inside their function.
- **Global** variables are readable from functions; use `global` to modify them (but prefer not to).
- **Enclosing** variables are accessible in nested functions; use `nonlocal` to modify them.
- **Avoid `global`** — pass data through parameters and return values instead.

---

## Additional Resources
- [Python Docs — Scopes and Namespaces](https://docs.python.org/3/tutorial/classes.html#python-scopes-and-namespaces)
- [Real Python — Python Scope & the LEGB Rule](https://realpython.com/python-scope-legb-rule/)
- [PEP 3104 — Access to Names in Outer Scopes (nonlocal)](https://peps.python.org/pep-3104/)
