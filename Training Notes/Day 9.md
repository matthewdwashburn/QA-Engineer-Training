- Maven is a dependency management tool for java
- Every maven project requires GAV depenencies, group id, artifact id, version

What are the lifecycle phases of maven?

validate - Check project is correct
compile	- Compile main sources
test-compile - Compile tests
test - Run unit tests (Surefire)
package - Produce JAR/WAR/etc.
verify - Run checks (e.g. integration tests)
install - Put artifact in local ~/.m2 repository
deploy - Publish to remote repository

Maven Commands

mvn clean test

clean: deletes the target/ directory (removes previous build output/compiled classes)
test: compiles your main and test source code, then runs the unit tests (via Surefire)
It does not produce a packaged artifact (no JAR)

mvn clean package

clean: same as above
package: runs everything test does (compile + run tests), and additionally bundles the compiled code into a distributable artifact (e.g., a .jar in target/) as defined by <packaging> in your pom.xml (default is jar)

### Java memory management

Java compares memory addresses when you use the == operator on Object references, and it compares actual values when you use the == operator on primitive types or when you use the .equals() method on objects.

### Python/Java Interview Prep Review

- Write and writelines do not create newlines for you
- For portable text files, setting encoding='utf-8' in open() is a recommended practice to ensure python reads and writes correctly.
- JDK install includes javac and is required to compile java on machine
- JDK (Java Development Kit) is used to create and compile Java software, whereas the JRE (Java Runtime Environment) is only used to run existing Java applications. JRE is included in JDK.
- You can use == on primatives like int, char, double, but not objects like String, Integer, Scanner, Shape, Etc.
- The Substring index parameters are not included in the final string output in Java
- In a Java stack trace, the line that often points to your application code first is typically A frame showing your package/class near the top of the trace (after the exception type)
- A switch expression with -> arms (Java 14+): does not exhibit classic Fall-through between cases
- After int n = scanner.nextInt();, reading a full line with scanner.nextLine() sometimes returns empty because: The newline after the number is still in the buffer
- import java.util.* does not imports all classes in subpackages like java.util.concurrent. Wildcard import is not recursive across subpackages.
- Using a with statement when opening a file allows the file to be automatically closed when you leave the with statement, so you don't have to manually run file.close()
- javac compiles Java source code into bytecode stored in .class files. The java launcher loads those classes inthe JVM (Java Virtual Machine) to execute them.

SOLID Principles:
1. **Single Responsibility** — each class/module should do one thing only
2. **Open/Closed** — open for extension, closed for modification (add new behavior without editing existing code)
3. **Liskov Substitution** — a subclass should be usable anywhere its base class is, without breaking things
4. **Interface Segregation** — prefer several small, specific interfaces over one large general-purpose one
5. **Loose Coupling** (Dependency Inversion in spirit) — components should depend on each other as little as possible, so changes 
don't ripple across the codebase

OOP Principles:
1. Encapsulation: Bundling data (variables) and methods (functions) into a single unit (a class) while restricting direct access to the internal state.

```
class BankAccount:
    def __init__(self, owner, balance):
        self.owner = owner
        self.__balance = balance  # Double underscore makes it private

    # Getter method to read data safely
    def get_balance(self):
        return self.__balance

    # Setter method to modify data with validation rules
    def deposit(self, amount):
        if amount > 0:
            self.__balance += amount
        else:
            print("Invalid deposit amount!")

account = BankAccount("Alice", 1000)
account.deposit(500)
print(account.get_balance())  # Outputs: 1500
# print(account.__balance)   # Throws an AttributeError (Protected)
```

2. Abstraction: Hiding complex, low-level implementation details and exposing only the essential high-level features.

```
from abc import ABC, abstractmethod

class CoffeeMachine(ABC):  # Abstract Class
    @abstractmethod
    def brew_coffee(self):
        pass

class EspressoMachine(CoffeeMachine):
    def brew_coffee(self):
        # The user just calls this; the complex heating logic stays inside
        return "Boiling water, grinding beans, forcing steam..."

my_pot = EspressoMachine()
print(my_pot.brew_coffee())

```

3. Inheritance: A mechanism where a new class (child/subclass) acquires the attributes and behaviors of an existing class (parent/superclass).
```
class Shape:  # Parent Class
    def __init__(self, name):
        self.name = name

    def area(self):
        pass  # Default, to be overridden by child classes

class Circle(Shape):  # Child Class inherits Shape
    def __init__(self, radius):
        super().__init__("Circle")  # Links to the parent constructor
        self.radius = radius

    def area(self):
        return 3.14159 * self.radius ** 2

my_circle = Circle(5)
print(my_circle.name)       # Inherited property: Circle
print(my_circle.area())     # Overridden method: 78.53975
```

4. Polymorphism: The ability of different objects to respond uniquely to the exact same method call. The word literally translates to "many shapes".
```
class Animal:
    def make_sound(self):
        pass

class Dog(Animal):
    def make_sound(self):
        return "Bark!"

class Cat(Animal):
    def make_sound(self):
        return "Meow!"

dog = Dog()
cat = Cat()

dog.make_sound()  # Outputs: Bark!
cat.make_sound()  # Outputs: Meow!

```

## Python Decorators

A Python decorator is a design pattern used to modify or extend the behavior of a function or method without changing its actual source code.
```
# 1. Define the decorator function
def my_logger(func):
    # The inner wrapper captures the original function's arguments
    def wrapper(*args, **kwargs):
        print(f"--> Starting: {func.__name__}")
        
        # Execute the original function and save its result
        result = func(*args, **kwargs)
        
        print(f"--> Finished: {func.__name__}")
        return result
        
    return wrapper

# 2. Apply the decorator using the @ symbol
@my_logger
def add_numbers(a, b):
    return a + b

# 3. Call the function
total = add_numbers(5, 10)
print(f"Result: {total}")

```

- In Flask, how does a URL path (e.g. /items) connect to your Python code?: You register a route with decorators like @app.get("/items") or @app.post(...), which binds that URL to a view function. When a request matches, Flask calls that function and turns its return value into an HTTP response.
- In Maven, production source code lives in src/main/java, and test code lives in src/test/java