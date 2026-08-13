# Class Attributes and Methods

## Learning Objectives
- Distinguish between instance attributes and class attributes.
- Use instance methods, class methods (`@classmethod`), and static methods (`@staticmethod`).
- Know when to use each type.

---

## Why This Matters

> **Weekly Epic Connection:** Understanding the different types of attributes and methods lets you design classes that are clean, efficient, and correct. Class attributes store shared state, instance attributes store per-object state, and the three method types each serve distinct purposes.

---

## The Concept

### Instance Attributes vs. Class Attributes

```python
class Employee:
    # CLASS attribute — shared by ALL instances
    company = "Acme Corp"
    employee_count = 0

    def __init__(self, name, role):
        # INSTANCE attributes — unique per object
        self.name = name
        self.role = role
        Employee.employee_count += 1
```

```python
emp1 = Employee("Alice", "QA Engineer")
emp2 = Employee("Bob", "Developer")

# Instance attributes — different per object
print(emp1.name)    # "Alice"
print(emp2.name)    # "Bob"

# Class attribute — same for all
print(emp1.company)  # "Acme Corp"
print(emp2.company)  # "Acme Corp"

# Class attribute tracks all instances
print(Employee.employee_count)  # 2
```

**Key difference:**
- **Instance attributes** (`self.x = ...` in `__init__`) belong to a specific object.
- **Class attributes** (defined in the class body, outside `__init__`) are shared across all instances.

### Instance Methods

The default type — takes `self` as the first parameter, operates on instance data:

```python
class Circle:
    def __init__(self, radius):
        self.radius = radius

    def area(self):  # Instance method
        return 3.14159 * self.radius ** 2

    def circumference(self):  # Instance method
        return 2 * 3.14159 * self.radius

c = Circle(5)
print(c.area())           # 78.53975
print(c.circumference())  # 31.4159
```

### Class Methods (`@classmethod`)

Takes `cls` as the first parameter (the class itself, not an instance). Often used as **factory methods** — alternative constructors:

```python
class User:
    def __init__(self, name, email, role):
        self.name = name
        self.email = email
        self.role = role

    @classmethod
    def from_dict(cls, data):
        """Create a User from a dictionary."""
        return cls(data["name"], data["email"], data.get("role", "viewer"))

    @classmethod
    def admin(cls, name, email):
        """Create an admin user."""
        return cls(name, email, "admin")

# Using class methods as alternative constructors
user1 = User("Alice", "alice@example.com", "viewer")
user2 = User.from_dict({"name": "Bob", "email": "bob@example.com"})
user3 = User.admin("Charlie", "charlie@example.com")
```

### Static Methods (`@staticmethod`)

Takes **neither `self` nor `cls`** — it's just a regular function that belongs to the class namespace:

```python
class MathUtils:
    @staticmethod
    def is_even(n):
        return n % 2 == 0

    @staticmethod
    def celsius_to_fahrenheit(c):
        return c * 9/5 + 32

# Called on the class (no instance needed)
print(MathUtils.is_even(4))               # True
print(MathUtils.celsius_to_fahrenheit(100)) # 212.0
```

**When to use `@staticmethod`:** When the method doesn't need access to instance data (`self`) or class data (`cls`), but logically belongs in the class.

### Quick Comparison

| Type | First Param | Access To | Use Case |
|------|------------|-----------|----------|
| Instance method | `self` | Instance + class data | Most methods — operating on object state |
| Class method | `cls` | Class data only | Factory methods, alternative constructors |
| Static method | None | Neither | Utility functions grouped with the class |

### Practical Example

```python
class TestSuite:
    total_suites = 0  # Class attribute

    def __init__(self, name):
        self.name = name
        self.tests = []  # Instance attribute
        TestSuite.total_suites += 1

    def add_test(self, test_name):  # Instance method
        self.tests.append(test_name)

    @classmethod
    def get_suite_count(cls):  # Class method
        return cls.total_suites

    @staticmethod
    def is_valid_test_name(name):  # Static method
        return bool(name) and name.startswith("test_")


suite = TestSuite("Login Tests")
suite.add_test("test_valid_login")

print(TestSuite.get_suite_count())                    # 1
print(TestSuite.is_valid_test_name("test_login"))     # True
print(TestSuite.is_valid_test_name("check_login"))    # False
```

---

## Summary

- **Instance attributes** (`self.x`) — unique per object; defined in `__init__`.
- **Class attributes** — shared across all instances; defined in the class body.
- **Instance methods** — take `self`; operate on object state (the default).
- **Class methods** (`@classmethod`) — take `cls`; great for factory/alternative constructors.
- **Static methods** (`@staticmethod`) — take nothing; utility functions in the class namespace.

---

## Additional Resources
- [Real Python — Instance, Class, and Static Methods](https://realpython.com/instance-class-and-static-methods-demystified/)
- [Python Docs — classmethod](https://docs.python.org/3/library/functions.html#classmethod)
- [Python Docs — staticmethod](https://docs.python.org/3/library/functions.html#staticmethod)
