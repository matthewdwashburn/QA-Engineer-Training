# Classes and Objects

## Learning Objectives
- Create classes using the `class` keyword.
- Understand `__init__`, `self`, and object instantiation.
- Distinguish between instance and class-level concepts.

---

## Why This Matters

> **Weekly Epic Connection:** Classes are how you implement the OOP concepts from the previous reading. Every test framework, every Page Object, every data model you build will be a class. This is where theory meets practice.

---

## The Concept

### Defining a Class

```python
class Dog:
    """A simple Dog class."""

    def __init__(self, name, breed, age):
        """Initialize a new Dog instance."""
        self.name = name        # Instance attribute
        self.breed = breed
        self.age = age

    def bark(self):
        """Make the dog bark."""
        return f"{self.name} says: Woof!"

    def describe(self):
        """Return a description of the dog."""
        return f"{self.name} is a {self.age}-year-old {self.breed}"
```

### `__init__` — The Constructor

`__init__` is a special method called automatically when you create a new object (instance). It initializes the object's attributes:

```python
# This call triggers __init__
my_dog = Dog("Rex", "German Shepherd", 3)
# Python does: Dog.__init__(my_dog, "Rex", "German Shepherd", 3)
```

### `self` — The Instance Reference

`self` refers to the **specific instance** being created or acted upon. It's how each object maintains its own data:

```python
dog1 = Dog("Rex", "German Shepherd", 3)
dog2 = Dog("Luna", "Golden Retriever", 5)

# Each instance has its own attributes
print(dog1.name)  # "Rex"
print(dog2.name)  # "Luna"
```

`self` is passed automatically — you never write `dog1.bark(dog1)`:

```python
print(dog1.bark())      # Rex says: Woof!
print(dog2.describe())  # Luna is a 5-year-old Golden Retriever
```

### Creating Objects (Instantiation)

```python
# Create instances
user1 = User("Alice", "alice@example.com", "admin")
user2 = User("Bob", "bob@example.com", "viewer")

# Access attributes
print(user1.name)    # "Alice"
print(user2.role)    # "viewer"

# Call methods
print(user1.display())
```

### A Complete Example: Test Result

```python
class TestResult:
    """Represents the result of a single test case."""

    def __init__(self, test_name, status, duration_ms):
        self.test_name = test_name
        self.status = status            # "pass" or "fail"
        self.duration_ms = duration_ms
        self.error_message = None

    def is_passed(self):
        """Check if the test passed."""
        return self.status == "pass"

    def mark_failed(self, error_message):
        """Mark this test as failed with an error message."""
        self.status = "fail"
        self.error_message = error_message

    def summary(self):
        """Return a formatted summary line."""
        icon = "✅" if self.is_passed() else "❌"
        line = f"{icon} {self.test_name} ({self.duration_ms}ms)"
        if self.error_message:
            line += f"\n   Error: {self.error_message}"
        return line


# Usage
result1 = TestResult("test_login", "pass", 1200)
result2 = TestResult("test_checkout", "pass", 2400)
result2.mark_failed("Timeout waiting for payment confirmation")

print(result1.summary())
print(result2.summary())
```

Output:
```
✅ test_login (1200ms)
❌ test_checkout (2400ms)
   Error: Timeout waiting for payment confirmation
```

### Instance vs. Class

- **Class** = The blueprint (e.g., `Dog`).
- **Instance** = A specific object created from the class (e.g., `my_dog`).

```python
# Check if something is an instance of a class
print(isinstance(dog1, Dog))    # True
print(isinstance("hello", Dog)) # False

# Check the type
print(type(dog1))  # <class '__main__.Dog'>
```

---

## Summary

- **Classes** are blueprints defined with `class ClassName:`.
- **`__init__`** is the constructor — it initializes instance attributes.
- **`self`** refers to the current instance — used to access attributes and methods.
- **Instantiation:** `obj = ClassName(args)` creates a new object.
- Each instance maintains **its own** attribute values independently.

---

## Additional Resources
- [Python Docs — Classes](https://docs.python.org/3/tutorial/classes.html)
- [Real Python — Python Classes and Objects](https://realpython.com/python3-object-oriented-programming/)
- [Python Docs — Data Model](https://docs.python.org/3/reference/datamodel.html)
