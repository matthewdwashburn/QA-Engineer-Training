# Classes vs Objects

## Learning Objectives
- Distinguish a **class** (blueprint/type) from an **object** (runtime instance) with precision.
- Explain where **class metadata**, **instance data**, and **references** live in JVM memory.
- Demonstrate **reference aliasing**, **`null` references**, and why `==` compares addresses not content.
- Describe the **object lifecycle**: creation, use, and garbage collection eligibility.
- Compare Java's class/object model with Python's.

---

## Why This Matters

> **Weekly Epic Connection:** Object-oriented design is the spine of this week's epic. Confusing "class" and "object" — or misunderstanding that Java variables hold *references*, not objects directly — leads to debugging mysteries: "Why did changing `b` also change `a`?" and "Why does `a == b` return false when they look the same?" Clear mental models here unlock inheritance, polymorphism, collections, and interview answers.

---

## The Concept

### Class = Blueprint (Type Definition)

A **class** is a **compile-time template** that defines:
- **Fields** — what data each instance holds (state)
- **Methods** — what each instance can do (behaviour)
- **Constructors** — how new instances are initialised

The class exists as bytecode in memory **once** (per class loader). It describes **what** instances can hold and **how** they behave — it does not hold object data itself (except static fields).

```java
public class Student {
    // Fields — each instance gets its own copy
    private String name;
    private int    grade;

    // Constructor — how instances are initialised
    public Student(String name, int grade) {
        this.name  = name;
        this.grade = grade;
    }

    // Method — behaviour available on each instance
    public String getSummary() {
        return name + " (grade " + grade + ")";
    }
}
```

---

### Object = Runtime Instance (Concrete Realisation)

An **object** is created at runtime with `new`. Each `new` call allocates a **distinct region of heap memory** holding that instance's field values:

```java
Student alice = new Student("Alice", 90);
Student bob   = new Student("Bob", 75);
Student alice2 = new Student("Alice", 90);   // Third object — distinct from alice!

System.out.println(alice.getSummary());   // "Alice (grade 90)"
System.out.println(bob.getSummary());     // "Bob (grade 75)"
```

`Student` is the **class** — one definition.
`alice`, `bob`, and `alice2` are three **objects** — three separate heap allocations.

---

### Memory Model — Where Things Live

```
                  STACK (per thread)          HEAP (shared)
                 ┌────────────────┐          ┌─────────────────────────┐
  main() frame  │ alice  ──────────────────► │ Student { name="Alice"  │
                │ bob    ──────────────────► │           grade=90 }    │
                │ alice2 ──────────────────► ├─────────────────────────┤
                └────────────────┘          │ Student { name="Bob"    │
                                            │           grade=75 }    │
                                            ├─────────────────────────┤
                                            │ Student { name="Alice"  │
                                            │           grade=90 }    │
                                            └─────────────────────────┘
```

Key facts:
- **Stack variables** (`alice`, `bob`) hold **references** (memory addresses) — not the objects themselves.
- **Heap objects** hold the actual field data.
- **Class metadata** (bytecode, method table, static fields) is managed separately by the JVM class loader in the **Metaspace** (Java 8+).
- Multiple instances share the **same method code** — only field data differs per instance.

---

### References — Not Values

This is the most important distinction from Python and other languages:

```java
Student alice  = new Student("Alice", 90);
Student copy   = alice;          // copy gets the SAME reference — NOT a copy of the object

copy.grade = 99;                 // Modifies the object that BOTH alice and copy point to
System.out.println(alice.grade); // 99 — alice sees the change!
```

`copy = alice` copies the **reference (address)**, not the object. This is called **aliasing** — two variables pointing to the same object.

To get an independent copy, you must explicitly construct one:
```java
Student independent = new Student(alice.name, alice.grade);  // New object, same values
independent.grade = 75;
System.out.println(alice.grade);       // Still 90 — unaffected
```

---

### Reference vs Value Equality (`==` vs `.equals()`)

`==` on references asks: **"Do these two variables point to the exact same object?"**

```java
Student alice  = new Student("Alice", 90);
Student alice2 = new Student("Alice", 90);   // Same values, different object

System.out.println(alice == alice2);          // false — different heap objects
System.out.println(alice.equals(alice2));     // depends on equals() override (see object-class.md)

// Reference equality is true only when pointing to the SAME object
Student ref1 = alice;
Student ref2 = alice;
System.out.println(ref1 == ref2);   // true — both point to the same object
```

**Rule:** Use `==` only to test if two references point to the **same object** or to compare **`null`**. Use `.equals()` for value/content equality.

---

### `null` — The Absent Reference

A reference variable that has been declared but not yet assigned points to `null` — it doesn't refer to any object:

```java
Student student = null;       // Valid — no object yet
student.getSummary();         // ❌ NullPointerException! No object to call the method on
```

```java
// Always guard before using a potentially null reference
if (student != null) {
    System.out.println(student.getSummary());
}

// Or use Java 14+ helpful NullPointerException messages in the stack trace
// e.g.: "Cannot invoke 'Student.getSummary()' because 'student' is null"
```

---

### Object Lifecycle

An object goes through distinct phases:

```
1. CLASS LOADING      → JVM loads Student.class bytecode into Metaspace
2. INSTANTIATION      → new Student("Alice", 90) allocates heap memory, runs constructor
3. ACTIVE USE         → Methods called, fields read/written via references
4. UNREACHABLE        → No remaining references point to the object
5. GARBAGE COLLECTION → JVM reclaims the heap memory automatically
```

```java
// Object created and referenced
Student student = new Student("Alice", 90);

// Object becomes eligible for GC when all references are dropped
student = null;   // Now no reference points to the old object — eligible for collection
// OR when variable goes out of scope (end of method)
```

You don't need to free memory manually — the **Garbage Collector** handles steps 4 and 5 automatically.

---

### Java vs Python — Class/Object Model Comparison

| Aspect | Java | Python |
|--------|------|--------|
| Class definition | `public class Name { }` | `class Name:` |
| Instance creation | `Name obj = new Name()` | `obj = Name()` |
| Self reference | `this` (implicit parameter) | `self` (explicit first parameter) |
| Variable type | References to heap objects | References to objects |
| `==` behaviour | Compares references | Compares values for `int`/`str`; references for objects |
| `is` keyword | None (use `==` for reference equality) | Tests object identity |
| Memory management | GC automatic | GC automatic (reference counting + cycle collector) |
| Class is an object? | No (Class metadata via `getClass()`) | Yes — `type` is an object |

```python
# Python equivalent
class Student:
    def __init__(self, name, grade):
        self.name = name
        self.grade = grade

alice = Student("Alice", 90)
bob   = Student("Bob", 75)
```

---

### One Class, Many Objects — Methods are Shared

All instances of `Student` share the **same method code** (stored once in class metadata). Only the **field values** differ per instance. When you call `alice.getSummary()`, the JVM runs the shared method code with `this` bound to the `alice` object:

```java
// Both calls run the SAME getSummary() code
// The JVM binds 'this' to the right object for each call
alice.getSummary()   // this = alice → "Alice (grade 90)"
bob.getSummary()     // this = bob   → "Bob (grade 75)"
```

---

## Summary

- A **class** is a compile-time blueprint defining fields, methods, and constructors — loaded once by the JVM.
- An **object** is a runtime instance created with `new`, allocated on the heap, holding its own field values.
- **Reference variables** on the stack hold memory addresses pointing to heap objects — not the objects themselves.
- **Aliasing**: `b = a` copies the reference — both variables now point to the same object. Mutating via one affects the other.
- `==` compares **references** (same heap location); use `.equals()` for **content** comparison.
- `null` is a reference that points to no object — calling a method on it throws `NullPointerException`.
- Objects are eligible for **garbage collection** when no references point to them.
- All instances share the same **method code**; each has its own **field data**.

---

## Additional Resources

- [Oracle Java Tutorial — Object-Oriented Programming Concepts](https://docs.oracle.com/javase/tutorial/java/concepts/)
- [Oracle Java Tutorial — Creating and Using Objects](https://docs.oracle.com/javase/tutorial/java/javaOO/objectcreation.html)
- [JLS: Objects](https://docs.oracle.com/javase/specs/jls/se21/html/jls-4.html#jls-4.3)
