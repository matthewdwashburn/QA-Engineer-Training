# Encapsulation and Abstraction

## Learning Objectives

- Explain **encapsulation** as data hiding plus controlled access via methods.
- Contrast **abstraction** (what) with implementation (how) in APIs and types.

## Why This Matters

These are two of the four OOP pillars (with inheritance and polymorphism). Encapsulation keeps invariants safe; abstraction lets teams depend on stable contracts while changing internals—essential for scalable Java systems and clean class design.

## The Concept

### Encapsulation

**Encapsulation** means keeping an object’s internal state **hidden** and only allowing controlled access through a **public API**.

- **Bundle** state + behavior in one unit (a class).
- **Hide** representation details (usually with `private` fields).
- **Expose** operations (methods) that maintain the class invariants (validation, consistency, thread safety, logging).

If clients can freely change fields, they can put your object into invalid states and make bugs hard to trace.

```java
public class Temperature {
    private double celsius;

    public void setCelsius(double c) {
        if (c < -273.15) throw new IllegalArgumentException("Below absolute zero");
        this.celsius = c;
    }

    public double getFahrenheit() {
        return celsius * 9.0 / 5 + 32;
    }
}
```

Callers cannot put `celsius` into an illegal range without going through `setCelsius`.

#### Encapsulation best practices (practical)

- Prefer **immutable** objects when possible (`final` fields, no setters).
- Keep fields `private`; widen visibility only when there’s a clear need.
- Don’t automatically generate getters/setters for everything—expose *behaviors* that make sense (e.g., `deposit(amount)` rather than `setBalance(...)`).

### Abstraction

**Abstraction** is about modeling the **essential idea** of something while hiding unnecessary details.

- It focuses on **what** clients can do, not **how** it is implemented.
- In Java, abstraction is expressed via **interfaces**, **abstract classes**, and well-designed public APIs.
- Encapsulation is what makes abstraction *safe*: you can change internals while keeping the same external contract.

**Analogy:** A car’s **steering wheel and pedals** are the abstract interface; the engine layout is hidden. You drive via the abstraction.

#### Abstraction examples in everyday Java

- `List` is an abstraction: you can use `ArrayList` or `LinkedList` without changing code that depends on the `List` contract.
- `InputStream` is an abstraction: file/network/byte-array streams all present the same read API.

## Code Example

```java
// Abstraction: callers depend on "notify", not on how message is sent
public interface Notifier {
    void notifyUser(String message);
}

public class EmailNotifier implements Notifier {
    public void notifyUser(String message) {
        // implementation detail encapsulated inside
    }
}
```

### Encapsulation vs abstraction (common confusion)

- **Encapsulation**: *hiding data/implementation details* and controlling access.
- **Abstraction**: *choosing the right level of detail* in the contract clients depend on.

They work together: abstraction gives a clean API; encapsulation protects that API from internal churn.

## Summary

- Encapsulation = hide state, expose behavior through a controlled API.
- Abstraction = emphasize what clients need, not internal structure.
- Together they reduce coupling and make systems easier to evolve.

## Additional Resources

- [Object-Oriented Programming Concepts](https://docs.oracle.com/javase/tutorial/java/concepts/)
- [Abstract Methods and Classes](https://docs.oracle.com/javase/tutorial/java/IandI/abstract.html)
