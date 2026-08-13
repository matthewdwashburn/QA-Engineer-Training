# The Object Class

## Learning Objectives
- Understand that all Python classes inherit from `object`.
- Identify the default methods every class gets from `object`.
- Know how inheritance from `object` works behind the scenes.

---

## Why This Matters

> **Weekly Epic Connection:** Understanding the base `object` class explains *why* every Python class has methods like `__str__`, `__repr__`, and `__eq__` even if you didn't define them. It's the root of Python's inheritance tree.

---

## The Concept

### Everything Inherits from `object`

In Python 3, **every class implicitly inherits from `object`**:

```python
class MyClass:
    pass

# These are equivalent:
class MyClass(object):
    pass
```

```python
print(isinstance(MyClass(), object))  # True
print(isinstance(42, object))         # True — even integers!
print(isinstance("hello", object))    # True — even strings!
```

In Python, **everything is an object** — integers, strings, functions, classes, modules.

### Default Methods from `object`

Every class inherits these methods, even if you don't define them:

| Method | Default Behavior |
|--------|-----------------|
| `__str__(self)` | Returns a generic string like `<__main__.MyClass object at 0x...>` |
| `__repr__(self)` | Returns the same generic string (meant for developers) |
| `__eq__(self, other)` | Compares by identity (`is`), not by value |
| `__hash__(self)` | Returns a hash based on the object's identity |
| `__init__(self)` | Does nothing (empty initializer) |
| `__class__` | Reference to the object's class |

```python
class Point:
    def __init__(self, x, y):
        self.x = x
        self.y = y

p = Point(3, 4)

# Default __str__ — not very useful
print(str(p))  # <__main__.Point object at 0x7f...>

# Default __eq__ — identity, not value
p1 = Point(3, 4)
p2 = Point(3, 4)
print(p1 == p2)  # False! Different objects, despite same values
```

### Overriding Default Methods

You can (and should) override these methods to make your classes more useful. We'll cover **dunder methods** in depth on Thursday, but here's a preview:

```python
class Point:
    def __init__(self, x, y):
        self.x = x
        self.y = y

    def __str__(self):
        return f"Point({self.x}, {self.y})"

    def __repr__(self):
        return f"Point(x={self.x}, y={self.y})"

    def __eq__(self, other):
        return self.x == other.x and self.y == other.y

p1 = Point(3, 4)
p2 = Point(3, 4)

print(str(p1))    # Point(3, 4)  — much better!
print(p1 == p2)   # True  — compares by value now
```

### The Method Resolution Order (MRO)

When you call a method on an object, Python looks up the inheritance chain using the **MRO**:

```python
class Animal:
    def speak(self):
        return "..."

class Dog(Animal):
    def speak(self):
        return "Woof!"

# MRO: Dog → Animal → object
print(Dog.__mro__)
# (<class 'Dog'>, <class 'Animal'>, <class 'object'>)
```

Python searches: `Dog` → `Animal` → `object`, stopping at the first match.

### `super()` — Calling the Parent

`super()` lets you call a method from the parent class:

```python
class Animal:
    def __init__(self, name):
        self.name = name

class Dog(Animal):
    def __init__(self, name, breed):
        super().__init__(name)  # Call Animal's __init__
        self.breed = breed

d = Dog("Rex", "Labrador")
print(d.name)   # "Rex"  — set by Animal.__init__
print(d.breed)  # "Labrador" — set by Dog.__init__
```

### `dir()` — Seeing All Inherited Methods

```python
class Empty:
    pass

print(dir(Empty()))
# ['__class__', '__delattr__', '__dict__', '__dir__', '__doc__',
#  '__eq__', '__format__', '__ge__', '__getattribute__', '__gt__',
#  '__hash__', '__init__', '__init_subclass__', '__le__', '__lt__',
#  '__ne__', '__new__', '__reduce__', '__repr__', '__setattr__',
#  '__sizeof__', '__str__', '__subclasshook__']
```

All of these come from `object` — your empty class already has 25+ methods!

---

## Summary

- All Python classes inherit from `object` — it's the root of every inheritance tree.
- `object` provides default methods (`__str__`, `__repr__`, `__eq__`, etc.) that you can override.
- The **MRO** determines the order Python searches for methods up the inheritance chain.
- Use `super()` to call parent class methods.
- We'll dive deeper into overriding dunder methods on Thursday.

---

## Additional Resources
- [Python Docs — Built-in Types: object](https://docs.python.org/3/library/functions.html#object)
- [Python Docs — Method Resolution Order](https://docs.python.org/3/glossary.html#term-method-resolution-order)
- [Real Python — super()](https://realpython.com/python-super/)
