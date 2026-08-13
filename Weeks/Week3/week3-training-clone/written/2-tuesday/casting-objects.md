# Casting Objects and `instanceof`

## Learning Objectives
- Explain the **reference type hierarchy** and why upcasting is always safe.
- Perform **upcasting** (implicit) and **downcasting** (explicit) with correct syntax.
- Diagnose and prevent `ClassCastException` using `instanceof` guards.
- Use **pattern matching for `instanceof`** (Java 16+) to eliminate redundant casts.
- Apply **pattern matching for `switch`** (Java 21) for clean type dispatch.
- Recognise when casting is a design smell and prefer polymorphism instead.

---

## Why This Matters

> **Weekly Epic Connection:** Polymorphic APIs return supertypes (`List`, `Object`, `Shape`) but callers sometimes need the specific subtype's behaviour. Safe casting lets you retrieve it without crashing. Understanding `ClassCastException` is essential for QA: it is a common runtime failure in collection-heavy code, API deserialization, and reflection-based frameworks.

---

## The Concept

### The Reference Type Hierarchy

Every Java class is part of a hierarchy rooted at `Object`. A reference variable can point to any object that is an instance of its declared type **or any subtype**:

```
Object
  └── Animal
        ├── Dog
        │     └── GoldenRetriever
        └── Cat
```

A `Dog` object **is-a** `Animal` and **is-a** `Object`. You can safely assign it to any of those reference types.

---

### Upcasting — Always Safe, Always Implicit

Assigning a subtype reference to a supertype variable. No explicit cast syntax required:

```java
Dog dog = new Dog("Rex");
Animal animal = dog;    // ✅ Upcast — implicit, always safe
Object obj = dog;       // ✅ Further upcast to Object — also implicit

// Why it's safe: a Dog IS-A Animal — it has all Animal's methods
```

What you **lose** when upcasting: access to the subtype's specific methods through that reference.

```java
Animal animal = new Dog("Rex");
animal.speak();      // ✅ Works — speak() is declared on Animal
animal.fetch();      // ❌ Compile error — fetch() is defined on Dog, not Animal
```

The object is still a `Dog` in memory — it still has `fetch()`. You've just told the compiler to treat it as an `Animal`.

---

### Downcasting — Explicit, Risk of Failure

Telling the compiler: "trust me, this supertype reference actually points to a specific subtype":

```java
Animal animal = new Dog("Rex");   // animal points to a Dog object
Dog dog = (Dog) animal;            // ✅ Explicit downcast — succeeds at runtime
dog.fetch();                       // ✅ Now accessible
```

The cast is checked at **runtime** by the JVM. If the object is not actually the target type, the JVM throws `ClassCastException`:

```java
Animal animal = new Cat("Whiskers");  // animal points to a Cat
Dog dog = (Dog) animal;               // ❌ ClassCastException at runtime!
// java.lang.ClassCastException: class Cat cannot be cast to class Dog
```

**Stack trace you'll see:**
```
Exception in thread "main" java.lang.ClassCastException:
    class com.example.Cat cannot be cast to class com.example.Dog
    at com.example.Demo.process(Demo.java:12)
    at com.example.Demo.main(Demo.java:6)
```

→ Line 12 is where the cast is. Look at what was stored in the variable to find the root cause.

---

### `instanceof` — Guard Before Casting

Always test the runtime type before downcasting to avoid `ClassCastException`:

```java
// Old style (pre-Java 16)
if (animal instanceof Dog) {
    Dog dog = (Dog) animal;   // Safe — we know it's a Dog
    dog.fetch();
}

// Works for any depth in the hierarchy
if (obj instanceof String) {
    String s = (String) obj;
    System.out.println(s.length());
}
```

`instanceof` returns `false` (not an exception) if the reference is `null`:
```java
Animal animal = null;
System.out.println(animal instanceof Dog);  // false — no NPE
```

---

### Pattern Matching for `instanceof` (Java 16+)

Java 16 eliminated the redundant cast after `instanceof` with **pattern binding**:

```java
// ✅ Java 16+ — bind and cast in one step
if (animal instanceof Dog d) {
    d.fetch();   // d is in scope here, already typed as Dog — no cast needed
}

// Additional condition in the pattern
if (animal instanceof Dog d && d.getName().startsWith("R")) {
    System.out.println("Dog's name starts with R: " + d.getName());
}
```

`d` is only in scope where the pattern matches — the compiler enforces this.

---

### Pattern Matching for `switch` (Java 21)

For multiple type cases, switch pattern matching is cleaner than an `instanceof` chain:

```java
// ❌ Old approach — repetitive instanceof chain
void describe(Object o) {
    if (o instanceof String s) {
        System.out.println("String of length " + s.length());
    } else if (o instanceof Integer i) {
        System.out.println("Integer: " + i);
    } else if (o instanceof Double d) {
        System.out.println("Double: " + d);
    } else {
        System.out.println("Unknown: " + o);
    }
}

// ✅ Java 21 — pattern switch
void describe(Object o) {
    switch (o) {
        case String s  -> System.out.println("String of length " + s.length());
        case Integer i -> System.out.println("Integer: " + i);
        case Double d  -> System.out.println("Double: " + d);
        case null      -> System.out.println("null");
        default        -> System.out.println("Unknown: " + o);
    }
}
```

**Guarded patterns** add conditions per arm:
```java
switch (shape) {
    case Circle c when c.radius() > 100 -> System.out.println("Large circle");
    case Circle c                        -> System.out.println("Small circle");
    case Rectangle r                     -> System.out.println("Rectangle");
    default                              -> System.out.println("Other");
}
```

---

### Casting with Collections — Generics vs Raw Types

Before generics (Java 5), collections stored `Object` and required manual casting:

```java
// Legacy raw type — still compiles but generates unchecked warning
List items = new ArrayList();
items.add("hello");
String s = (String) items.get(0);  // Explicit downcast required

// ❌ Easy to forget and get ClassCastException at runtime
items.add(42);
String broken = (String) items.get(1);   // ClassCastException!
```

With generics, the compiler prevents this class of error:
```java
List<String> items = new ArrayList<>();
items.add("hello");
// items.add(42);   // ❌ Compile error — int cannot be added to List<String>
String s = items.get(0);   // ✅ No cast needed — compiler guarantees String
```

---

### When Casting Is a Design Smell

Frequent downcasting often signals a design problem. Prefer:

| Problem | Better Approach |
|---------|----------------|
| Casting to call a subtype method | Move the method to the supertype (override) |
| Many `instanceof` checks for subtypes | Polymorphism — let each type handle its own case |
| Switching on type | Sealed classes + pattern switch (Java 17+) |
| Casting `Object` out of a collection | Use generics |

```java
// ❌ Casting smell — checking subtypes repeatedly
void process(Animal animal) {
    if (animal instanceof Dog d) d.fetch();
    else if (animal instanceof Cat c) c.scratch();
}

// ✅ Polymorphism — each type knows its own behaviour
void process(Animal animal) {
    animal.doActivity();   // Dog.doActivity() fetches; Cat.doActivity() scratches
}
```

---

## Summary

- **Upcasting** (subtype → supertype) is implicit and always safe — you lose access to subtype-specific methods.
- **Downcasting** (supertype → subtype) requires explicit `(Type)` syntax and is checked at runtime — incorrect casts throw `ClassCastException`.
- Always guard downcasts with **`instanceof`** before casting.
- **Pattern matching `instanceof`** (Java 16+) binds and casts in one step — prefer it over the old check-then-cast pattern.
- **Pattern matching `switch`** (Java 21) cleanly handles multiple type cases with optional guards.
- Use **generics** for collections to eliminate casting entirely.
- Frequent downcasting is a design smell — prefer polymorphism and sealed types.

---

## Additional Resources

- [Inheritance and the IS-A Relationship (Oracle Tutorial)](https://docs.oracle.com/javase/tutorial/java/IandI/subclasses.html)
- [Pattern Matching for instanceof (JEP 394)](https://openjdk.org/jeps/394)
- [Pattern Matching for switch (JEP 441)](https://openjdk.org/jeps/441)
- [JLS: instanceof operator](https://docs.oracle.com/javase/specs/jls/se21/html/jls-15.html#jls-15.20.2)
