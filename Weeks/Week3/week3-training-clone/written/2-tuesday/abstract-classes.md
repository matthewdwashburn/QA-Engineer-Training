# Abstract Classes

## Learning Objectives
- Declare **abstract classes** and **abstract methods** with correct syntax.
- Understand the rules governing abstract classes (instantiation, inheritance, constructors).
- Combine abstract and concrete methods to implement the **Template Method** pattern.
- Distinguish abstract classes from interfaces and choose appropriately.
- Recognise what subclasses must do when inheriting from an abstract class.

---

## Why This Matters

> **Weekly Epic Connection:** Abstract classes are the mechanism for **partial implementation** — sharing common code while mandating specific behaviour in subclasses. They appear in frameworks (JUnit `TestCase`, Spring `AbstractController`, Servlet `HttpServlet`) and in domain models where a family of types shares most behaviour but differs in key operations. Understanding them is essential for reading and extending framework code in enterprise QA and development.

---

## The Concept

### What Is an Abstract Class?

An **abstract class** is a class that:
1. **Cannot be instantiated** with `new` — it is incomplete by design.
2. May contain **abstract methods** — method declarations with no body, using the `abstract` keyword.
3. May contain **concrete (non-abstract) fields, constructors, and methods** — like any regular class.

```java
// ✅ Abstract class — cannot be instantiated directly
public abstract class Report {
    protected final String title;         // Concrete field
    protected final LocalDate generated;

    // Concrete constructor — runs when a subclass is constructed
    protected Report(String title) {
        this.title = title;
        this.generated = LocalDate.now();
    }

    // Abstract method — no body; subclasses MUST provide the implementation
    public abstract void generateBody();

    // Concrete method — inherited by all subclasses (shared logic)
    public void printHeader() {
        System.out.println("=== " + title + " ===");
        System.out.println("Generated: " + generated);
    }
}

// ❌ Cannot instantiate an abstract class
Report r = new Report("Q1");   // Compile error: Report is abstract
```

---

### Abstract Classes Have Constructors

Even though abstract classes cannot be directly instantiated, they **do have constructors**. The constructor runs when a subclass calls `super(...)`:

```java
public abstract class Shape {
    protected final String colour;

    // Accessible to subclasses via super()
    protected Shape(String colour) {
        this.colour = colour;
    }

    public abstract double area();
    public abstract double perimeter();

    public String describe() {
        return colour + " " + getClass().getSimpleName()
             + " (area=" + String.format("%.2f", area()) + ")";
    }
}
```

---

### Concrete Subclasses Must Implement All Abstract Methods

A **concrete** (non-abstract) subclass must provide a body for every abstract method it inherits — otherwise it must also be declared `abstract`:

```java
// Concrete subclass — must implement ALL abstract methods
public class Circle extends Shape {
    private final double radius;

    public Circle(double radius, String colour) {
        super(colour);            // Calls Shape's constructor
        this.radius = radius;
    }

    @Override
    public double area() {
        return Math.PI * radius * radius;
    }

    @Override
    public double perimeter() {
        return 2 * Math.PI * radius;
    }
}

// Abstract subclass — implements some, leaves one abstract
public abstract class Polygon extends Shape {
    protected final int sides;

    protected Polygon(int sides, String colour) {
        super(colour);
        this.sides = sides;
    }

    @Override
    public double perimeter() {
        return sides * sideLength();   // Provides perimeter but uses another abstract method
    }

    public abstract double sideLength();   // Still abstract — Rectangle/Square must provide this

    // area() from Shape is still abstract here — Rectangle must implement it
}
```

---

### The Template Method Pattern

The most common use of abstract classes is the **Template Method** pattern: the abstract class defines **the algorithm's skeleton** in a concrete method, and subclasses fill in the **variable steps** via abstract methods.

```java
public abstract class DataProcessor {

    // Template method — fixed algorithm structure; final prevents overriding the order
    public final void process() {
        readData();          // Step 1: subclass provides data source
        validateData();      // Step 2: subclass validates
        transform();         // Step 3: subclass transforms
        writeOutput();       // Step 4: default output (can be overridden)
        cleanup();           // Step 5: always runs — hook method
    }

    // Abstract steps — each subclass must define these
    protected abstract void readData();
    protected abstract void validateData();
    protected abstract void transform();

    // Concrete default — subclasses may override if needed (hook method)
    protected void writeOutput() {
        System.out.println("Writing to default output...");
    }

    // Hook — empty default; subclasses override only if they need cleanup
    protected void cleanup() { }
}

// Concrete implementation for CSV files
public class CsvDataProcessor extends DataProcessor {
    @Override
    protected void readData() {
        System.out.println("Reading CSV...");
    }
    @Override
    protected void validateData() {
        System.out.println("Validating CSV schema...");
    }
    @Override
    protected void transform() {
        System.out.println("Transforming CSV rows...");
    }
    @Override
    protected void cleanup() {
        System.out.println("Closing CSV file handle.");
    }
}
```

Usage:
```java
DataProcessor processor = new CsvDataProcessor();
processor.process();   // Runs the fixed algorithm using CsvDataProcessor's steps
```

**Why `final` on the template method?** To prevent subclasses from overriding the algorithm's structure — only the variable steps (abstract methods) should change.

---

### Abstract vs Interface — Decision Guide

| Dimension | Abstract Class | Interface |
|-----------|---------------|-----------|
| Instantiation | ❌ Cannot instantiate | ❌ Cannot instantiate |
| Multiple inheritance | ❌ Single `extends` | ✅ Multiple `implements` |
| Fields | ✅ Any kind (instance, static, final) | ⚠️ Only `public static final` |
| Constructors | ✅ Yes | ❌ No |
| Method body | ✅ Any method can have a body | ✅ Default/static methods only |
| Access modifiers | Any | All are implicitly `public` |
| State sharing | ✅ Can hold mutable state per object | ❌ No instance state |
| **Best for** | Shared code + IS-A family relationship | Capability / role contracts |

**Choose abstract class when:**
- Subclasses genuinely belong to a **family** (`Shape`, `Report`, `Animal`) and share **state**
- You need **non-public** members (protected fields, package-private constructors)
- You want the **Template Method** pattern to enforce algorithm structure

**Choose interface when:**
- Defining a **capability** that unrelated classes can implement (`Comparable`, `Serializable`, `Runnable`)
- You need **multiple inheritance** of type
- Implementing the **Dependency Inversion Principle** (depend on abstractions, not concretions)

> **Modern Java note:** With default methods (Java 8+), interfaces can provide shared behaviour. The line has blurred — but abstract classes remain the tool of choice when **shared instance state** or **Template Method structure** is needed.

---

### Key Rules Summary

- `abstract` on a class = **cannot instantiate**.
- `abstract` on a method = **no body** (semicolon instead of `{ }`); the class containing it **must** also be abstract.
- A class can be `abstract` with **zero** abstract methods — legal, useful when you want to prevent direct instantiation.
- **Constructors** in abstract classes run when subclasses call `super(...)`.
- A concrete subclass must implement **all** abstract methods inherited from all ancestors.
- `final` and `abstract` on a method are **mutually exclusive** — you can't prevent overriding while also requiring it.

---

## Summary

- An **abstract class** is an incomplete blueprint — it cannot be instantiated directly, forces subclasses to implement abstract methods, and shares common concrete code.
- Abstract classes have **constructors** that subclasses call via `super(...)`.
- The **Template Method** pattern uses a `final` template method calling abstract "step" methods — defining the algorithm structure while deferring variable behaviour to subclasses.
- Use abstract classes for IS-A families with **shared state**; use **interfaces** for capability contracts and multiple inheritance.
- A concrete subclass must implement **all** abstract methods from the entire hierarchy, or itself be declared abstract.

---

## Additional Resources

- [Abstract Methods and Classes (Oracle Tutorial)](https://docs.oracle.com/javase/tutorial/java/IandI/abstract.html)
- [JLS: Abstract Classes](https://docs.oracle.com/javase/specs/jls/se21/html/jls-8.html#jls-8.1.1.1)
- [Template Method Pattern (Refactoring Guru)](https://refactoring.guru/design-patterns/template-method)
- [Effective Java — Item 20: Prefer interfaces to abstract classes](https://www.oreilly.com/library/view/effective-java/9780134686097/)
