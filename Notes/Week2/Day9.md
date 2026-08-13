# Day 9 - Maven, Java Memory, OOP & Python Decorators

---

## Maven

**Maven** — dependency management and build tool for Java. Every project is identified by **GAV**: Group ID, Artifact ID, Version (defined in `pom.xml`).

### Lifecycle Phases (in order)

| Phase | What it does |
|---|---|
| `validate` | Check project is correct |
| `compile` | Compile main sources |
| `test-compile` | Compile test sources |
| `test` | Run unit tests (Surefire) |
| `package` | Produce JAR/WAR/etc. |
| `verify` | Run integration test checks |
| `install` | Put artifact in local `~/.m2` repository |
| `deploy` | Publish to remote repository |

### Common Commands

```bash
mvn clean test      # delete target/, compile everything, run tests (no JAR produced)
mvn clean package   # same as above + bundle compiled code into a .jar in target/
```

---

## Java Memory Management

`==` on **object references** compares memory addresses. `==` on **primitives** compares values. Use `.equals()` to compare object values:

```java
// Primitives — == compares values
int a = 5; int b = 5;
a == b  // true

// Objects — == compares references
String s1 = new String("hello");
String s2 = new String("hello");
s1 == s2      // false (different objects in memory)
s1.equals(s2) // true (same value)
```

You can use `==` on `int`, `char`, `double`, etc., but **not** on `String`, `Integer`, `Scanner`, or any class.

---

## JDK vs JRE

- **JDK** (Java Development Kit) — includes `javac` (compiler) + JRE. Required to write and compile Java.
- **JRE** (Java Runtime Environment) — runs existing Java apps. JRE is included inside the JDK.
- `javac` — compiles `.java` source → `.class` bytecode files
- `java` — launches the JVM to execute `.class` files

---

## Java / Python Interview Notes

- `import java.util.*` does **not** import subpackages like `java.util.concurrent` — wildcard imports are not recursive
- `String.substring(start, end)` — `end` index is **exclusive** (not included in result)
- `switch` expression with `->` arms (Java 14+) — no fall-through between cases
- After `scanner.nextInt()`, calling `scanner.nextLine()` may return `""` because the newline is still in the buffer — consume it first or use `Integer.parseInt(scanner.nextLine())`
- `write()` and `writelines()` in Python do **not** add newlines — you must include `\n` yourself
- Use `encoding='utf-8'` in `open()` for portable text files
- `with open(...) as f:` — file is automatically closed when the block exits; no need to call `f.close()`
- In a Java stack trace, your application code appears near the top (after the exception type line)

---

## SOLID Principles

1. **Single Responsibility** — each class/module should do one thing only
2. **Open/Closed** — open for extension, closed for modification (add behavior without editing existing code)
3. **Liskov Substitution** — a subclass should be usable anywhere its base class is, without breaking things
4. **Interface Segregation** — prefer several small, specific interfaces over one large general-purpose one
5. **Dependency Inversion / Loose Coupling** — components should depend on abstractions, not concretions; changes shouldn't ripple across the codebase

---

## OOP Principles

### Encapsulation

Bundling data and methods into a class while restricting direct access to internal state via `private` fields and getters/setters:

```python
class BankAccount:
    def __init__(self, owner, balance):
        self.owner = owner
        self.__balance = balance  # double underscore = private

    def get_balance(self):
        return self.__balance

    def deposit(self, amount):
        if amount > 0:
            self.__balance += amount

account = BankAccount("Alice", 1000)
account.deposit(500)
print(account.get_balance())  # 1500
# account.__balance  # AttributeError — can't access directly
```

### Abstraction

Hiding complex implementation details and exposing only what the caller needs. Use `ABC` + `@abstractmethod` to enforce that subclasses implement required methods:

```python
from abc import ABC, abstractmethod

class CoffeeMachine(ABC):
    @abstractmethod
    def brew_coffee(self):
        pass

class EspressoMachine(CoffeeMachine):
    def brew_coffee(self):
        return "Boiling water, grinding beans, forcing steam..."

my_pot = EspressoMachine()
print(my_pot.brew_coffee())
```

### Inheritance

A child class acquires attributes and behaviors from a parent class. `super().__init__()` calls the parent constructor:

```python
class Shape:
    def __init__(self, name):
        self.name = name

class Circle(Shape):
    def __init__(self, radius):
        super().__init__("Circle")  # calls Shape.__init__
        self.radius = radius

    def area(self):
        return 3.14159 * self.radius ** 2

my_circle = Circle(5)
print(my_circle.name)   # Circle (inherited)
print(my_circle.area()) # 78.53975 (overridden)
```

### Polymorphism

Different objects respond differently to the same method call:

```python
class Animal:
    def make_sound(self): pass

class Dog(Animal):
    def make_sound(self): return "Bark!"

class Cat(Animal):
    def make_sound(self): return "Meow!"

dog, cat = Dog(), Cat()
dog.make_sound()  # Bark!
cat.make_sound()  # Meow!
```

---

## Python Decorators

A decorator modifies or extends a function's behavior without changing its source code. Implemented as a function that wraps another function, applied with `@`:

```python
def my_logger(func):
    def wrapper(*args, **kwargs):
        print(f"--> Starting: {func.__name__}")
        result = func(*args, **kwargs)
        print(f"--> Finished: {func.__name__}")
        return result
    return wrapper

@my_logger
def add_numbers(a, b):
    return a + b

total = add_numbers(5, 10)
print(f"Result: {total}")
```

In Flask, `@app.get("/items")` is a decorator that registers the URL route and binds it to the view function below it.

---

## Maven Project Structure

```
src/main/java   — production source code
src/test/java   — test source code
```
