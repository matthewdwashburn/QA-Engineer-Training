# Interfaces in Java

## Learning Objectives

- Declare interfaces, implement them, and use multiple `implements`.
- Use **default** and **static** methods on interfaces (Java 8+).
- Relate interfaces to “multiple inheritance of type” and API contracts.

## Why This Matters

Interfaces are the main way Java achieves **flexible abstraction** and **multiple inheritance of behavior contracts**. They power callbacks, lambdas (functional interfaces Thursday), test doubles, and library design across the ecosystem.

## The Concept

### What an interface is (definition)

An **interface** is a Java type that defines a **contract**: the set of methods a class promises to provide.

- It describes **what** operations are available, not **how** a class stores its data.
- A class can implement **multiple** interfaces, which is Java’s primary form of “multiple inheritance” (of type).

### Basic interface

```java
public interface SerializableLogger {
    void log(String message);
}

public class FileLogger implements SerializableLogger {
    @Override
    public void log(String message) {
        // write to file
    }
}
```

A class may **`implements` several** interfaces, comma-separated.

### Polymorphism with interfaces (why they’re everywhere)

Interfaces let you write code that depends on a stable contract and swap implementations freely:

```java
public static void runJob(SerializableLogger logger) {
    logger.log("starting job");
}

runJob(new FileLogger());
// later: runJob(new ConsoleLogger());
```

Testing benefit: pass a fake logger (test double) that records messages instead of writing files.

### Constants

Fields in interfaces are implicitly `public static final`.

Practical note: interface constants are a **global API surface** once published. Prefer `enum` or a dedicated constants class when the constants are not truly part of the interface’s semantic contract.

### Default methods (Java 8+)

Provide a **body** on the interface; implementors inherit or may override.

```java
public interface Greeter {
    default void greet(String name) {
        System.out.println("Hello, " + name);
    }
}
```

Resolves **diamond** issues with explicit rules when two defaults clash.

**Default method conflict rule (key idea):** if a class implements two interfaces that both provide the same default method, the class must **override** it and choose what to do.

```java
interface A { default String id() { return "A"; } }
interface B { default String id() { return "B"; } }

class Both implements A, B {
    @Override public String id() {
        return A.super.id(); // choose explicitly (or combine)
    }
}
```

### Static methods on interfaces

Belong to the interface type; called as `InterfaceName.method()`. Not inherited by implementors.

This is commonly used for factories and helpers:

```java
interface Parser {
    static Parser json() { return new JsonParser(); }
    Object parse(String input);
}
```

### Multiple inheritance via interfaces

A type can implement **many** interfaces, gaining **all abstract/default** method contracts—unlike single class inheritance.

### When to choose an interface vs an abstract class (quick guide)

- **Use an interface** when you are modeling a **capability/role** that many unrelated types might share (`Comparable`, `Runnable`, `AutoCloseable`).
- **Use an abstract class** when you need **shared instance state**, protected helpers, or want to enforce an algorithm skeleton (Template Method).

## Code Example

```java
public interface Flyer {
    void fly();
}

public interface Swimmer {
    void swim();
}

public class Duck implements Flyer, Swimmer {
    @Override public void fly() { }
    @Override public void swim() { }
}
```

### Common pitfalls

- **Forgetting `public` on implementing methods**: interface methods are implicitly `public`, so the implementing class methods must be `public` too (or the compiler will error).
- **Leaking too many defaults**: default methods are great for evolving an API, but too many can make interfaces “semi-classes.” Keep defaults small and clearly behavioral.

## Summary

- Interfaces define contracts; classes implement one or more.
- Default methods add evolution without breaking implementors; static methods are scoped to the interface.
- Use interfaces for capabilities and polymorphic APIs.

## Additional Resources

- [Interfaces](https://docs.oracle.com/javase/tutorial/java/IandI/createinterface.html)
- [Default Methods](https://docs.oracle.com/javase/tutorial/java/IandI/defaultmethods.html)
