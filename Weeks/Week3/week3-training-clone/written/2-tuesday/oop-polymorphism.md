# Polymorphism in Java

## Learning Objectives

- Contrast **compile-time** polymorphism (overloading) with **runtime** polymorphism (overriding).
- Explain **virtual method invocation** for instance methods.
- Use polymorphic references (`Supertype ref = new Subtype()`).

## Why This Matters

Runtime polymorphism lets you write code against **abstractions** (`List`, `Shape`, `Runnable`) and plug in **concrete** behavior—core to extensible designs, testing with mocks, and the collections you will use later in the week.

## The Concept

### What “polymorphism” means (in Java)

**Polymorphism** literally means “many forms.” In Java OOP, it means:

- You can treat different concrete objects as the **same supertype** (a common interface or superclass).
- When you call an **overridden instance method** through that supertype reference, Java runs the **most specific implementation** for the actual object at runtime.

This is what enables “code that doesn’t change when you add new types.”

### Compile-time polymorphism

**Method overloading:** the compiler picks which `print(...)` to call from argument types. No subclass required.

### Runtime polymorphism

**Method overriding:** the JVM dispatches **instance** methods using the **actual object’s class** at runtime, not only the reference type’s class.

```java
Animal a = new Dog("Rex");
a.speak(); // Dog’s speak() runs
```

The variable `a` is typed as `Animal`, but the object is `Dog`—**dynamic dispatch**.

### Virtual method invocation

All non-`static`, non-`final`, non-`private` instance methods are **virtually** dispatched. `static` methods are **not** polymorphic: they bind to the **reference type**.

### What is (and isn’t) polymorphic

- **Instance methods**: polymorphic when overridden (dynamic dispatch).
- **`static` methods**: not polymorphic (method hiding; chosen by reference type).
- **Fields**: not polymorphic (field access uses the reference type).
- **Constructors**: not inherited; constructor “dispatch” does not apply.

Example showing method vs field:

```java
class Parent {
    String label = "P";
    String who() { return "Parent"; }
}
class Child extends Parent {
    String label = "C";
    @Override String who() { return "Child"; }
}

Parent ref = new Child();
System.out.println(ref.who());   // Child  (polymorphic)
System.out.println(ref.label);   // P      (NOT polymorphic: uses Parent.label)
```

### Polymorphic references

You can pass a `Dog` where an `Animal` is expected (upcasting, implicit). The useful pattern:

```java
void makeSpeak(Animal animal) {
    animal.speak();
}
```

Any `Animal` subtype works without changing `makeSpeak`.

### The payoff: open/closed code

Polymorphism supports the **Open/Closed Principle**: code is open for extension (new subtypes) but closed for modification (existing calling code stays the same).

```java
interface PaymentMethod {
    void pay(double amount);
}

final class CardPayment implements PaymentMethod {
    public void pay(double amount) { System.out.println("card " + amount); }
}

final class BankTransferPayment implements PaymentMethod {
    public void pay(double amount) { System.out.println("bank transfer " + amount); }
}

static void checkout(PaymentMethod method, double total) {
    method.pay(total); // no casting, no instanceof
}
```

You can add `CryptoPayment` later without touching `checkout`.

## Code Example

```java
class Payment {
    void pay(double amount) { System.out.println("generic"); }
}
class CardPayment extends Payment {
    @Override void pay(double amount) { System.out.println("card " + amount); }
}

Payment p = new CardPayment();
p.pay(10); // "card 10"
```

### Common pitfalls to watch for (QA angle)

- **Accidental overloading instead of overriding**: signature mismatch means your “override” never runs. Use `@Override` to force the compiler to catch this.
- **Calling `private` methods**: `private` methods are not overridden; calls are bound within the declaring class.
- **Expecting `static` dispatch**: `static` methods are resolved by reference type, which can surprise you in logs/tests.

## Summary

- Overloading resolves at compile time; overriding resolves at runtime for instance methods.
- Polymorphic references enable open-ended extension behind a superclass or interface type.
- `static` methods do not participate in runtime polymorphism.

## Additional Resources

- [Polymorphism](https://docs.oracle.com/javase/tutorial/java/IandI/polymorphism.html)
- [Virtual Machine Specification — Method Invocation](https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-2.html#jvms-2.6)
