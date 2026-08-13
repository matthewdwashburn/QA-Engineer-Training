# Function Declaration and Syntax

## Learning Objectives
- Declare functions using the `def` keyword.
- Write proper docstrings and return statements.
- Understand function structure, scope basics, and best practices.

---

## Why This Matters

> **Weekly Epic Connection:** Functions are how you organize code into reusable, testable units. Every test you write, every utility you build, every automation step — they're all functions. Mastering function syntax today sets the foundation for everything that follows.

---

## The Concept

### What Is a Function?

A **function** is a named, reusable block of code that performs a specific task. Functions let you:
- **Avoid repetition** — write once, call many times.
- **Organize code** — break complex problems into manageable pieces.
- **Test independently** — verify each piece works correctly in isolation.

### Basic Syntax

```python
def function_name(parameters):
    """Docstring: describes what the function does."""
    # Function body
    return value
```

### Anatomy of a Function

```python
def calculate_total(price, tax_rate):
    """Calculate the total price including tax.

    Args:
        price: The base price of the item.
        tax_rate: The tax rate as a decimal (e.g., 0.08 for 8%).

    Returns:
        The total price after tax.
    """
    tax = price * tax_rate
    total = price + tax
    return total
```

Breaking this down:

| Component | Purpose |
|-----------|---------|
| `def` | Keyword that declares a function |
| `calculate_total` | Function name (snake_case by PEP 8) |
| `(price, tax_rate)` | Parameters the function accepts |
| `"""..."""` | Docstring documenting the function |
| Function body | The code that executes when called |
| `return total` | The value sent back to the caller |

### Calling a Function

```python
# Call the function and capture the return value
result = calculate_total(100, 0.08)
print(result)  # 108.0

# Use the return value directly
print(f"Total: ${calculate_total(49.99, 0.08):.2f}")
# Total: $53.99
```

### Functions Without Return

If a function doesn't have a `return` statement, it returns `None` implicitly:

```python
def greet(name):
    """Print a greeting. Returns nothing."""
    print(f"Hello, {name}!")

result = greet("Alice")  # Prints: Hello, Alice!
print(result)            # None
```

### The `pass` Statement

`pass` is a placeholder for an empty function body — useful when planning code structure:

```python
def validate_email(email):
    pass  # TODO: implement validation logic

def run_tests():
    pass  # Will implement later
```

### Multiple Return Values

Python functions can return multiple values using tuples:

```python
def divide(a, b):
    """Return both the quotient and remainder."""
    quotient = a // b
    remainder = a % b
    return quotient, remainder

# Unpack the returned tuple
q, r = divide(17, 5)
print(f"17 ÷ 5 = {q} remainder {r}")  # 17 ÷ 5 = 3 remainder 2
```

### Early Return

You can `return` from anywhere in a function to exit immediately:

```python
def validate_age(age):
    """Return True if age is valid, False otherwise."""
    if age < 0:
        return False  # Exit immediately
    if age > 150:
        return False  # Exit immediately
    return True
```

### Function Naming Best Practices

```python
# ✅ GOOD: Verb phrases that describe the action
def calculate_total(items):
def validate_email(address):
def is_valid_password(password):    # "is_" prefix for boolean returns
def has_permission(user, action):   # "has_" prefix for boolean checks
def get_user_by_id(user_id):        # "get_" prefix for retrieval

# ❌ BAD: Vague, unclear, or noun-like names
def process(data):          # Process how?
def do_stuff():             # What stuff?
def data(file_path):        # Is this a function or a variable?
```

---

## Code Example: Building a Mini Test Helper

```python
def run_test(test_name, expected, actual):
    """Run a single test assertion and print the result.

    Args:
        test_name: Descriptive name for the test.
        expected: The expected value.
        actual: The actual value to check.

    Returns:
        True if the test passed, False otherwise.
    """
    passed = expected == actual
    status = "✅ PASS" if passed else "❌ FAIL"
    print(f"  {status}: {test_name}")
    if not passed:
        print(f"         Expected: {expected}")
        print(f"         Actual:   {actual}")
    return passed


# Using our test helper
print("Running Calculator Tests:")
run_test("Addition", 4, 2 + 2)
run_test("Subtraction", 5, 10 - 5)
run_test("Division", 3.0, 10 / 3)  # This will fail!
```

---

## Summary

- Functions are declared with `def`, named in `snake_case`, and should have docstrings.
- `return` sends a value back to the caller; without it, the function returns `None`.
- Functions can return multiple values (as tuples).
- Use early returns to simplify logic and avoid deep nesting.
- Name functions with verb phrases that describe what they do.

---

## Additional Resources
- [Python Docs — Defining Functions](https://docs.python.org/3/tutorial/controlflow.html#defining-functions)
- [PEP 257 — Docstring Conventions](https://peps.python.org/pep-0257/)
- [Real Python — Defining Your Own Functions](https://realpython.com/defining-your-own-python-function/)
