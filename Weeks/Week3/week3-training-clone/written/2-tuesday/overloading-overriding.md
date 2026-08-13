# Method Overloading vs Method Overriding

## Learning Objectives

- Distinguish **overloading** (compile-time) from **overriding** (runtime).
- Use `@Override` correctly and recognize when the compiler will not treat a method as an override.

## Why This Matters

Mixing up overloads and overrides causes subtle bugs (e.g. you thought you overrode `equals` but overloaded it). This distinction is interview-frequent and essential for polymorphism and the `Object` contract.

## The Concept

### Overloading

- **Same method name**, **different parameter lists** (arity or types) in the **same class** (or inherited visible methods—overload resolution is static).
- Return type alone is **not** enough to overload; you need a distinct signature.
- Resolved at **compile time** based on static types of arguments.

```java
void print(int x) { }
void print(String s) { }
```

### Overriding

- **Subclass** provides a method with the **same name and compatible signature** as an **instance** method in the superclass.
- Return types must be **covariant** (same or subtype) for Java 5+.
- Resolved at **runtime** for instance methods (**virtual method invocation**): the actual object type wins.

```java
class Parent {
    void greet() { System.out.println("parent"); }
}
class Child extends Parent {
    @Override
    void greet() { System.out.println("child"); }
}
```

### `@Override`

Annotating intended overrides catches typos: if the method does not actually override anything, the compiler errors.

### Classic pitfall: `equals`

```java
public boolean equals(MyClass other) // overload — wrong for Object.equals contract
public boolean equals(Object o)      // override — correct shape
```

## Code Example

```java
class Calculator {
    int add(int a, int b) { return a + b; }
    double add(double a, double b) { return a + b; } // overload
}

class Greeter {
    String hello() { return "Hi"; }
}
class FormalGreeter extends Greeter {
    @Override
    String hello() { return "Good day"; } // override
}
```

## Summary

- Overloading: multiple methods, same name, different parameters; compile-time binding.
- Overriding: subclass replaces superclass instance behavior; runtime binding.
- Use `@Override` for clarity and safety.

## Additional Resources

- [Defining Method](https://docs.oracle.com/javase/tutorial/java/javaOO/methods.html)
- [Polymorphism](https://docs.oracle.com/javase/tutorial/java/IandI/polymorphism.html)
