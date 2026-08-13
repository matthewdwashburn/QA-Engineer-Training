# OOP Concepts

## Learning Objectives
- Define the four pillars of Object-Oriented Programming: encapsulation, inheritance, polymorphism, and abstraction.
- Explain why OOP matters for writing maintainable, reusable code.
- Recognize OOP patterns in real-world testing frameworks.

---

## Why This Matters

> **Weekly Epic Connection:** OOP is how professional Python code is structured. Every test framework you'll use — pytest fixtures, Selenium's Page Object Model, custom assertion libraries — is built on OOP principles. Understanding these concepts lets you write test code that's organized, reusable, and scalable.

---

## The Concept

### What Is OOP?

**Object-Oriented Programming** organizes code around **objects** — bundles of related data (attributes) and behavior (methods). Instead of writing a series of procedural steps, you model your program as interacting objects.

```python
# Procedural approach
user_name = "Alice"
user_email = "alice@example.com"
user_role = "tester"

def get_user_display(name, role):
    return f"{name} ({role})"

# OOP approach
class User:
    def __init__(self, name, email, role):
        self.name = name
        self.email = email
        self.role = role

    def display(self):
        return f"{self.name} ({self.role})"
```

### The Four Pillars

#### 1. Encapsulation

**Bundling data and methods together** and controlling access to internal state.

```python
class BankAccount:
    def __init__(self, owner, balance=0):
        self.owner = owner
        self._balance = balance  # Convention: "internal" attribute

    def deposit(self, amount):
        if amount > 0:
            self._balance += amount

    def get_balance(self):
        return self._balance
```

The balance is modified only through `deposit()` — not directly. This prevents invalid states (like a negative balance from accidental direct assignment).

**Why it matters:** In test frameworks, encapsulation keeps test configuration safe from accidental modification.

#### 2. Inheritance

**Creating new classes based on existing ones**, inheriting their attributes and methods.

```python
class Animal:
    def __init__(self, name):
        self.name = name

    def speak(self):
        return "..."

class Dog(Animal):         # Dog inherits from Animal
    def speak(self):       # Override the parent's method
        return "Woof!"

class Cat(Animal):
    def speak(self):
        return "Meow!"

dog = Dog("Rex")
print(dog.name)     # "Rex" — inherited from Animal
print(dog.speak())  # "Woof!" — overridden in Dog
```

**Why it matters:** Test frameworks use inheritance extensively — your test classes often inherit from a base class that provides shared setup/teardown logic.

#### 3. Polymorphism

**Different objects responding to the same method call** in their own way.

```python
animals = [Dog("Rex"), Cat("Whiskers"), Dog("Buddy")]

for animal in animals:
    print(f"{animal.name}: {animal.speak()}")
# Rex: Woof!
# Whiskers: Meow!
# Buddy: Woof!
```

The same `.speak()` call produces different results based on the object's type. The calling code doesn't need to know — or care — which specific type it's dealing with.

**Why it matters:** A test runner can call `.run()` on different test types (unit, integration, performance) without knowing the specifics of each.

#### 4. Abstraction

**Hiding complex implementation details** and exposing only what's necessary.

```python
# The user of this class doesn't need to know HOW
# the HTTP request is made — just that it works
class ApiClient:
    def __init__(self, base_url):
        self.base_url = base_url

    def get_user(self, user_id):
        """Get a user by ID. Implementation details hidden."""
        # Complex HTTP logic here...
        return {"id": user_id, "name": "Alice"}
```

**Why it matters:** The Page Object Model in Selenium testing is pure abstraction — you interact with `login_page.submit_credentials()` without knowing the CSS selectors, waits, and JavaScript involved.

### How the Pillars Work Together

```python
# A mini test framework demonstrating all four pillars

class BaseTest:                        # ABSTRACTION
    """Abstract base — defines the interface."""
    def setup(self):
        pass

    def run(self):
        raise NotImplementedError

    def teardown(self):
        pass

class WebTest(BaseTest):               # INHERITANCE
    """Inherits from BaseTest, adds browser setup."""
    def __init__(self):
        self._browser = None           # ENCAPSULATION

    def setup(self):
        self._browser = "Chrome"       # Protected state

    def teardown(self):
        self._browser = None

class LoginTest(WebTest):              # More INHERITANCE
    def run(self):                     # POLYMORPHISM
        return f"Testing login with {self._browser}"

class SearchTest(WebTest):
    def run(self):                     # POLYMORPHISM
        return f"Testing search with {self._browser}"

# The runner treats all tests the same — POLYMORPHISM
tests = [LoginTest(), SearchTest()]
for test in tests:
    test.setup()
    print(test.run())
    test.teardown()
```

---

## Summary

- **Encapsulation:** Bundle data + methods; control access to internal state.
- **Inheritance:** Create specialized classes from general ones.
- **Polymorphism:** Same method call, different behavior depending on the object.
- **Abstraction:** Hide complexity; expose simple interfaces.
- These four pillars are the foundation of every professional Python framework you'll use.
- We'll implement these concepts with actual Python classes in the next readings.

---

## Additional Resources
- [Real Python — OOP in Python](https://realpython.com/python3-object-oriented-programming/)
- [Python Docs — Classes](https://docs.python.org/3/tutorial/classes.html)
- [Refactoring Guru — OOP Concepts](https://refactoring.guru/refactoring/what-is-refactoring)
