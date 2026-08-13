# Inheritance in Java

## Learning Objectives

- Use `extends` to form superclass/subclass relationships and express **IS-A**.
- Apply `super` for superclass constructors and members.
- Describe constructor chaining and order of initialization.

## Why This Matters

Inheritance models specialization and code reuse in hierarchies (e.g. `Employee` → `Manager`). It powers polymorphism later in the week and appears constantly in frameworks (Servlet, JPA entities, custom exceptions).

## The Concept

### What inheritance is (definition)

**Inheritance** lets a class (the *subclass*) reuse and specialize behavior from another class (the *superclass*).

- The subclass **inherits** accessible fields/methods from the superclass.
- The subclass can **add** new members and **override** methods to change behavior.

Key design idea:
- Use inheritance when the subclass truly **is-a** kind of the superclass (substitutability).
- Prefer composition when you just want to reuse functionality without claiming “is-a”.

### `extends`

A class **inherits** fields and methods from one **direct superclass** (Java has single inheritance for classes).

```java
public class Animal {
    protected String name;
    public Animal(String name) { this.name = name; }
    public void speak() { System.out.println("..."); }
}

public class Dog extends Animal {
    public Dog(String name) { super(name); }
    @Override
    public void speak() { System.out.println(name + " barks"); }
}
```

**IS-A:** Every `Dog` **is an** `Animal`; you can assign `Animal a = new Dog("Rex");`.

### `super`

- **`super(...)`** calls a superclass constructor—must be the **first** statement in a subclass constructor if used.
- **`super.method()`** invokes the superclass version of an overridden instance method.

Example of `super.method()`:

```java
class BaseReport {
    void print() { System.out.println("Header"); }
}
class DetailedReport extends BaseReport {
    @Override void print() {
        super.print();                 // reuse base behavior
        System.out.println("Details"); // add extra behavior
    }
}
```

### Constructor chaining

If you do not call `super(...)`, the compiler inserts **`super()`** (no-arg superclass constructor). The superclass must have an accessible no-arg constructor—or you must explicitly call another superclass constructor.

Initialization order: superclass static → subclass static → superclass instance/constructor → subclass instance/constructor.

### Overriding rules that matter with inheritance

- Use `@Override` to ensure you are actually overriding (not overloading by mistake).
- You cannot override a `final` method.
- `private` methods are not overridden (they are not visible to subclasses).
- You can widen access when overriding (e.g., `protected` → `public`), but you cannot narrow it.

### When inheritance becomes a problem (design smell)

Inheritance is powerful but can create brittle hierarchies. Watch for:

- Subclass needs to override many methods just to “turn off” behavior.
- Subclass breaks superclass invariants.
- You’re inheriting just to reuse code, not to model an IS-A relationship.

In those cases, consider **composition**:

```java
class Engine { void start() { } }

class Car {
    private final Engine engine = new Engine(); // has-a
    void start() { engine.start(); }
}
```

## Code Example

```java
class Base {
    Base() { System.out.print("Base "); }
}

class Derived extends Base {
    Derived() { super(); System.out.print("Derived"); }
}
```

If you create `new Derived()`, output is `Base Derived` because the superclass constructor always runs first.

## Summary

- `extends` creates an IS-A hierarchy with one superclass per class.
- `super(...)` chains constructors; `super.method()` targets superclass implementation.
- Understand initialization order when debugging construction issues.

## Additional Resources

- [Inheritance](https://docs.oracle.com/javase/tutorial/java/IandI/subclasses.html)
- [Using the Keyword super](https://docs.oracle.com/javase/tutorial/java/IandI/super.html)
