# Java & SQL QC — Complete Study Guide

A ground-up rebuild of **every concept in your review notes**, in the order a foundation should be built. Each topic gives the idea, a short example, and a one-line takeaway. Read this end to end, then use the flashcards and quiz to make it automatic.

> Where your notes had a small slip, this guide quietly uses the correct version and flags it — those are the exact spots interviewers probe.

---

## Contents

**Part 1 — Java**
1. [The Java platform](#j1) · 2. [The four pillars of OOP](#j2) · 3. [Stack vs heap memory](#j3) · 4. [Classes, objects, constructors](#j4) · 5. [Access & non-access modifiers](#j5) · 6. [Encapsulation](#j6) · 7. [Inheritance & polymorphism](#j7) · 8. [Abstract classes vs interfaces](#j8) · 9. [equals, ==, hashCode & Strings](#j9) · 10. [Wrapper classes](#j10) · 11. [Exceptions](#j11) · 12. [The Collections Framework](#j12) · 13. [Core data structures](#j13) · 14. [Map & Set behavior](#j14) · 15. [Multithreading](#j15) · 16. [Recursion & StackOverflow](#j16) · 17. [Reflection](#j17) · 18. [Design patterns](#j18) · 19. [Functional interfaces & lambdas](#j19) · 20. [Streams](#j20) · 21. [Logging](#j21)

**Part 2 — SQL**
22. [SQL, RDBMS, database](#s1) · 23. [The five sublanguages](#s2) · 24. [CRUD](#s3) · 25. [DROP vs TRUNCATE vs DELETE](#s4) · 26. [Data types & money](#s5) · 27. [Keys](#s6) · 28. [Referential integrity & constraints](#s7) · 29. [Cardinality & multiplicity](#s8) · 30. [ERD](#s9) · 31. [Normalization](#s10) · 32. [Junction tables](#s11) · 33. [Joins](#s12) · 34. [Set operations](#s13) · 35. [Query clauses & order of operations](#s14) · 36. [LIKE, BETWEEN, subqueries](#s15) · 37. [Aggregate vs scalar functions](#s16) · 38. [Transactions & ACID](#s17) · 39. [Isolation levels & anomalies](#s18) · 40. [BASE](#s19) · 41. [Views](#s20) · 42. [Indexes](#s21) · 43. [DAO](#s22) · 44. [Stored procedures](#s23) · 45. [Prepared statements & injection](#s24) · 46. [Ports](#s25)

---

# Part 1 — Java

<a name="j1"></a>
## 1. The Java platform

Java source (`.java`) is compiled by `javac` into **bytecode** (`.class`) — not native machine code. Bytecode runs on the **Java Virtual Machine (JVM)**, which exists for every OS. So one compiled program runs anywhere a JVM is installed: **"write once, run anywhere."**

Java is also **strongly typed**: every variable has a declared type, checked at compile time, so type errors are caught before the program runs.

```
Hello.java  --javac-->  Hello.class (bytecode)  --JVM-->  runs on Windows / macOS / Linux
```

> ✅ **Takeaway:** Java → bytecode → JVM → any OS. Strongly typed, checked at compile time.

---

<a name="j2"></a>
## 2. The four pillars of OOP

| Pillar | Meaning | One-liner |
|---|---|---|
| **Abstraction** | Expose *what* something does, hide *how* | Interfaces / abstract APIs |
| **Inheritance** | A subclass reuses/extends a superclass | `class Dog extends Animal` |
| **Polymorphism** | One interface, many behaviors | Overriding, dynamic dispatch |
| **Encapsulation** | Hide internal state behind controlled access | `private` fields + getters/setters |

> ✅ **Takeaway:** Abstraction, Inheritance, Polymorphism, Encapsulation — "A PIE."

---

<a name="j3"></a>
## 3. Stack vs heap memory

Two memory regions matter:

| Region | Stores | Cleaned up |
|---|---|---|
| **Stack** | Local variables and method-call data (frames) | Automatically when the method returns |
| **Heap** | Objects and instances created with `new` | By the garbage collector when unreferenced |

A local variable on the stack holds a **reference** (address) to the object that actually lives on the heap.

```java
void demo() {
    int x = 5;                    // x is on the stack
    Employee e = new Employee();  // the Employee object is on the heap;
                                  // e (the reference) is on the stack
}
```

> ✅ **Takeaway:** Stack = local variables + method calls. Heap = objects/instances. References sit on the stack pointing to heap objects.

---

<a name="j4"></a>
## 4. Classes, objects, and constructors

A **class** is a blueprint (fields, methods, constructors). An **object** is a runtime **instance** of that class, created with `new` and stored on the heap.

A **constructor** is a special method that runs **when an instance is created**. It initializes the object's fields, usually from the parameters passed in.

Three facts to lock in:

1. **A constructor has no return type — not even `void`.**
2. **If you declare no constructor, Java provides a default no-arg constructor.** Declaring any constructor removes that freebie.
3. **Constructors are not inherited, so they can't be overridden.** They *can* be overloaded.

```java
class Employee {
    String name;
    Employee(String name) { this.name = name; }  // constructor, no return type
}
Employee e = new Employee("Matt");
```

> ✅ **Takeaway:** Constructor = no return type, runs on `new`, auto-provided only if you declare none, **overloadable but never overridable**.

---

<a name="j5"></a>
## 5. Access modifiers & non-access modifiers

**Access modifiers** control *visibility*:

| Modifier | Visible from |
|---|---|
| `public` | Everywhere |
| `protected` | Same package **+** subclasses |
| *default* (no keyword) | Same **package** only (package-private) |
| `private` | Only inside the declaring class |

**Non-access modifiers** control *behavior* — the two you'll be asked about:

- **`static`** — belongs to the **class**, one shared copy, usable without an object.
- **`final`** — variable can't be **reassigned**; method can't be **overridden**; class can't be **extended**.

> ⚠️ **Trap:** `static` and `final` are **non-access** modifiers, not access modifiers.

```java
public class Config {
    public static final int MAX = 10;  // public (visibility) + static + final (behavior)
}
```

> ✅ **Takeaway:** Access = public/protected/default/private. Non-access = static, final (plus abstract, synchronized, volatile…).

---

<a name="j6"></a>
## 6. Encapsulation

Encapsulation hides an object's internal state (usually `private` fields) and exposes **controlled** access through methods (getters/setters with validation). Callers use a stable surface while internals can change freely.

```java
class Account {
    private double balance;                 // hidden
    public double getBalance() { return balance; }
    public void deposit(double amt) {       // controlled mutation
        if (amt > 0) balance += amt;
    }
}
```

> ✅ **Takeaway:** Private state + public controlled methods. Supports abstraction by keeping a stable API over changing internals.

---

<a name="j7"></a>
## 7. Inheritance & polymorphism

**Inheritance:** a subclass `extends` one superclass and reuses/extends its behavior. **Java has single class inheritance** (one `extends`); you get multiple inheritance of *type* only through interfaces.

**Polymorphism** comes in two forms, distinguished by *when the method is chosen*:

### Overloading — compile time (static binding)

Same method **name**, **different parameter lists**, in the same class. The compiler picks the version from the argument types.

```java
int add(int a, int b) { return a + b; }
double add(double a, double b) { return a + b; }   // overload
```

### Overriding — runtime (dynamic dispatch)

A subclass provides a method with the **same signature** as the superclass. Which runs is decided **at runtime** by the **actual object**, not the reference type.

```java
class Animal { void speak() { System.out.println("..."); } }
class Dog extends Animal { @Override void speak() { System.out.println("Woof"); } }

Animal a = new Dog();
a.speak();   // "Woof" — chosen at RUNTIME by the real object (Dog)
```

> ⚠️ **Traps (both hit you before):**
> - Overriding is **runtime**, overloading is compile time. (overRIDE → RUNtime.)
> - Overriding requires the **same** parameters; overloading requires **different** parameters. (Your note had these swapped.)

### Static methods hide, they don't override

Overriding needs an object to dispatch on, but static methods belong to the **class**. A subclass static with the same signature **hides** the parent's; which runs is decided by the **reference type at compile time**.

```java
class Parent { static void greet() { System.out.println("Parent"); } }
class Child extends Parent { static void greet() { System.out.println("Child"); } }

Parent p = new Child();
p.greet();   // "Parent" — HIDING (reference type), not overriding
```

So: **static methods can be overloaded, but not overridden.** (Same for the `main` method being static — the JVM calls it on the class with no instance.)

> ✅ **Takeaway:** Overloading = compile time (different params). Overriding = runtime (same signature, real object). Static same-signature = **hiding**. Constructors overload but never override.

---

<a name="j8"></a>
## 8. Abstract classes vs interfaces

Both define a type you can't instantiate directly.

**Abstract class** — a partial blueprint. It can hold **state** (instance fields), **constructors**, concrete methods **with bodies**, and abstract methods **without** bodies. Subclasses must implement the abstract methods (or be abstract themselves).

**Interface** — a **contract**: a list of method signatures. Pre-Java 8, interfaces had **no method implementations** at all. Since Java 8 they can have `default` and `static` methods (with bodies), and `private` helpers since Java 9 — but still **no instance state or constructors**.

```java
public abstract class Car {
    public abstract void start();            // no body — subclass implements
    void honk() { System.out.println("honk"); }  // concrete body — allowed
    public static final int WHEELS = 4;      // constant
}

public class BMW extends Car {
    @Override void start() { System.out.println("start"); }
}
```

> ⚠️ **Trap:** Abstract classes do **not** support multiple inheritance — a class extends exactly one. The pre-8 difference is: **interfaces can't have method implementations.**

| | Abstract class | Interface |
|---|---|---|
| Instance fields (state)? | Yes | No |
| Constructors? | Yes | No |
| Method bodies? | Yes | Only `default`/`static` (Java 8+) |
| A class can have… | one (`extends`) | many (`implements`) |

> ✅ **Takeaway:** Abstract class = shared base with state + concrete methods. Interface = contract; pre-8 signatures only; a class implements many.

---

<a name="j9"></a>
## 9. equals, ==, hashCode & Strings

- **`==`** compares **references** (addresses) — and works directly for **primitives**.
- **`.equals()`** compares **logical value** when overridden.

```java
String a = "hi", b = "hi";
a == b;        // true  — both point to the SAME pooled literal
String c = new String("hi");
a == c;        // false — 'new' makes a distinct object
a.equals(c);   // true  — same characters
```

### The hashCode contract

Hash-based collections (`HashMap`, `HashSet`) use **`hashCode()`** to pick a bucket, then **`equals()`** to resolve collisions. The contract: **equal objects must have equal hash codes.** So if you override `equals`, you **must** override `hashCode` too, or lookups fail.

### Strings: immutable + the string pool

`String` objects are **immutable** — every "change" returns a new String. Because they're used everywhere, Java interns string **literals** in a **string pool**, so identical literals share one object. `new String(...)` forces a separate object off the pool.

> ✅ **Takeaway:** `==` = identity, `.equals()` = value. Override `hashCode` whenever you override `equals`. Strings are immutable and pooled.

---

<a name="j10"></a>
## 10. Wrapper classes

Each primitive has an object **wrapper**: `int→Integer`, `long→Long`, `double→Double`, `float→Float`, `boolean→Boolean`, `char→Character`, `byte→Byte`, `short→Short`.

They let primitives live in generics/collections and provide utilities (`Integer.parseInt`, `Integer.MAX_VALUE`). **Autoboxing/unboxing** converts automatically. They're immutable; beware the `Integer` cache (−128…127) with `==`.

```java
List<Integer> nums = new ArrayList<>();  // needs Integer, not int
nums.add(5);                             // autoboxed int → Integer
int x = nums.get(0);                     // unboxed Integer → int
```

> ✅ **Takeaway:** Wrappers = object versions of primitives; required in collections/generics; autoboxing bridges them.

---

<a name="j11"></a>
## 11. Exceptions

An exception is an object representing a disruption. The whole family descends from **`Throwable`**:

```
              Throwable            <-- root of everything throwable
             /         \
          Error       Exception
       (serious       /        \
      JVM issues)  RuntimeException   (checked: IOException, SQLException…)
                    (unchecked)
```

- **`Throwable`** is the parent of **all** exceptions **and** errors.
- **Checked** exceptions extend `Exception` (not `RuntimeException`); the compiler forces you to `catch` them or declare `throws`. Model recoverable external failures.
- **Unchecked** exceptions are `RuntimeException` (and `Error`); no `throws` needed. Usually signal bugs (`NullPointerException`).

### Handling and declaring

```java
try {
    risky();
} catch (SpecificException e) {   // most specific first
    // recover / log
} finally {
    // always runs — cleanup
}
```

- **`throw`** actually throws an instance: `throw new IllegalArgumentException("bad");`
- **`throws`** declares in the signature that a method may throw a checked exception (pushes handling to the caller).

### Custom exception

```java
class InvalidAgeException extends Exception {      // checked
    public InvalidAgeException(String message) {
        super(message);                            // pass message up
    }
}
if (age < 0) throw new InvalidAgeException("Invalid age");
```

> ⚠️ **Trap:** The parent of all exceptions is **`Throwable`**, not `Exception` (because `Error` isn't under `Exception`).

> ✅ **Takeaway:** `Throwable` root. Checked = compiler-enforced; unchecked = `RuntimeException`. Custom = subclass `Exception` + `super(message)`.

---

<a name="j12"></a>
## 12. The Collections Framework

Interfaces and their common implementations:

| Interface | Meaning | Implementations |
|---|---|---|
| **List** | Ordered, indexed, **allows duplicates** | `ArrayList`, `LinkedList` |
| **Set** | **No duplicates**, about membership | `HashSet`, `LinkedHashSet`, `TreeSet` |
| **Queue / Deque** | FIFO / double-ended | `ArrayDeque`, `PriorityQueue`, `LinkedList` |
| **Map** | Key → value lookup | `HashMap`, `LinkedHashMap`, `TreeMap` |

- **`ArrayList`** is the default List: `get(i)`/`set(i)` are O(1). **`LinkedList`** is better for frequent insert/remove at the ends.
- **`HashSet`** = fast, unordered. **`LinkedHashSet`** = insertion order. **`TreeSet`** = **sorted**.

> ✅ **Takeaway:** List (ordered, dupes) · Set (unique) · Queue/Deque · Map (key/value). Pick by ordering and access pattern.

---

<a name="j13"></a>
## 13. Core data structures

**Linked list** — elements (nodes) are **not** in contiguous memory; each node points to the next via a pointer. Dynamic size; fast insert/delete.
- **Singly** linked: only a `next` pointer.
- **Doubly** linked: `next` **and** `prev` pointers (Java's `LinkedList` is doubly linked). Linked lists underpin queues, hash tables, etc.

**Stack** — **LIFO** (Last-In-First-Out). Uses: compiler parenthesis matching, expression evaluation, undo/redo. In modern Java, `ArrayDeque` used with `push`/`pop` is the recommended stack.

**Queue** — **FIFO** (First-In-First-Out). Enqueue at the back, dequeue at the front.

**Deque** — double-ended queue: add/remove at **both** front and back.

```java
Deque<Integer> stack = new ArrayDeque<>();
stack.push(4);
System.out.println(stack.pop());   // 4  (LIFO: push and pop at the head)
```

> ✅ **Takeaway:** Linked list = pointer-linked nodes (singly/doubly). Stack = LIFO. Queue = FIFO. Deque = both ends.

---

<a name="j14"></a>
## 14. Map & Set behavior

### Duplicates and nulls

- **Duplicate key in a Map:** `put` **overwrites** the old value (returns the previous one). Keys stay unique.
- **Duplicate value in a Map:** allowed — different keys may map to the same value.
- **Duplicate value in a Set:** `add` returns **false**, set unchanged.
- **`HashMap`** allows **one null key** and **multiple null values**. **`TreeMap`** allows no null key (it must compare keys).

### TreeSet vs HashSet

Choose **`TreeSet`** for **sorted** iteration and range/`NavigableSet` operations (O(log n); elements must be `Comparable` or you supply a `Comparator`). Choose **`HashSet`** for fast average membership when order doesn't matter.

### HashMap vs Hashtable

**`HashMap`** is unsynchronized, allows one null key + null values, and is the default. **`Hashtable`** is **legacy**, synchronized on every method, and disallows nulls. For concurrent maps prefer **`ConcurrentHashMap`**.

> ✅ **Takeaway:** Map keys unique (put overwrites); Set add returns false on dupes. `TreeSet` = sorted; `HashSet` = fast. `HashMap` = default; `Hashtable` = legacy sync.

---

<a name="j15"></a>
## 15. Multithreading

**Multithreading** runs multiple paths of execution concurrently in one program. Two ways to create a thread:

1. **Extend `Thread`** and override `run()`.
2. **Implement `Runnable`** and pass it to a `Thread`.

Call **`start()`** (not `run()`) to launch a new thread.

### Thread lifecycle (`Thread.State`)

```
NEW  →  RUNNABLE  →  BLOCKED / WAITING / TIMED_WAITING  →  TERMINATED
```

- **NEW** — created, not started. **RUNNABLE** — eligible to run / running.
- **BLOCKED** — waiting for a monitor lock. **WAITING / TIMED_WAITING** — via `wait()`, `join()`, `sleep()`.
- **TERMINATED** — finished; can't restart.

### Deadlock & synchronized

- **Deadlock** — two or more threads each hold a lock the other needs, so none proceeds.
- **`synchronized`** — lets **only one thread at a time** enter a method/block via the object's monitor lock, preventing **race conditions** on shared state. (Note: it guarantees mutual exclusion; misusing multiple locks can itself *cause* deadlock.)

> ✅ **Takeaway:** Thread via `Thread`/`Runnable`, launch with `start()`. States: NEW → RUNNABLE → BLOCKED/WAITING/TIMED_WAITING → TERMINATED. `synchronized` = one-thread-at-a-time.

---

<a name="j16"></a>
## 16. Recursion & StackOverflowError

Each method call pushes a **stack frame**. A recursive method calls itself; if it recurses too deep it exhausts the thread's stack → **`StackOverflowError`**. Sound recursion needs a **base case** and arguments that **progress** toward it. For very deep linear recursion, rewrite as a loop (the JVM doesn't reliably optimize tail recursion).

```java
int factorial(int n) {
    if (n <= 1) return 1;      // base case
    return n * factorial(n-1); // progresses toward the base case
}
```

> ✅ **Takeaway:** Too many recursive calls exhaust the stack → StackOverflowError. Always have a base case that's reached.

---

<a name="j17"></a>
## 17. Reflection

The **Reflection API** lets a program **inspect and manipulate classes, methods, and fields at runtime**. The `Class` object is the entry point.

```java
Class<?> c = obj.getClass();
String name = c.getName();
Method[] methods = c.getMethods();      // discover methods at runtime
```

Used by frameworks (dependency injection, serialization, ORMs) to work with types they didn't know about at compile time.

> ✅ **Takeaway:** Reflection = examine/modify classes, methods, fields at runtime via `Class`.

---

<a name="j18"></a>
## 18. Design patterns: Singleton & Factory

- **Singleton** — ensures **only one instance** exists. A private constructor plus a static accessor returns the same object.
- **Factory** — abstracts instantiation behind a method that decides **which object to create**, so callers don't hard-code concrete classes. Often paired with Singleton.

```java
class Config {
    private static final Config INSTANCE = new Config();
    private Config() { }                      // private → no external new
    public static Config get() { return INSTANCE; }
}
```

> ✅ **Takeaway:** Singleton = one shared instance (private constructor). Factory = hides which concrete class gets created.

---

<a name="j19"></a>
## 19. Functional interfaces & lambdas

A **functional interface** has exactly **one abstract method** (SAM), so a **lambda** can implement it. `@FunctionalInterface` documents the intent.

Common ones from `java.util.function`:

| Interface | Abstract method | Meaning |
|---|---|---|
| `Function<T,R>` | `R apply(T t)` | Transform T → R |
| `Consumer<T>` | `void accept(T t)` | Consume T, return nothing |
| `Predicate<T>` | `boolean test(T t)` | Test T → true/false |
| `Supplier<T>` | `T get()` | Supply a T |

A **lambda** is an anonymous function — a concise implementation of that single method without writing a full class.

```java
Predicate<Integer> isEven = n -> n % 2 == 0;
Function<String,Integer> len = s -> s.length();
isEven.test(4);   // true
```

> ⚠️ **Note:** the mapping is `Function → apply`, `Consumer → accept`, `Predicate → test` (your note mixed these up).

> ✅ **Takeaway:** Functional interface = one abstract method; lambda implements it inline. Know Function/Consumer/Predicate/Supplier.

---

<a name="j20"></a>
## 20. Streams

The **Streams API** processes collections functionally (filter/map/reduce) **without mutating the source**, and is **lazy**.

- **Intermediate** operations return a **Stream** and are lazy: `filter`, `map`, `sorted`, `distinct`, `limit`. They chain.
- **Terminal** operations **trigger execution** and produce a result/side effect: `collect`, `forEach`, `count`, `reduce`, `findFirst`, `anyMatch`.

```java
List<Integer> out = list.stream()
    .filter(x -> x > 2)          // intermediate (lazy)
    .map(x -> x * 2)             // intermediate (lazy)
    .collect(Collectors.toList()); // terminal (runs the pipeline)

int sum = nums.stream().reduce(0, (a, b) -> a + b);  // terminal
```

Nothing runs until a **terminal** operation is called.

> ✅ **Takeaway:** Intermediate ops (filter/map/sorted) are lazy and return a Stream; terminal ops (collect/count/reduce) trigger execution.

---

<a name="j21"></a>
## 21. Logging (Logback)

A logging library beats `System.out` because it lets you set **severity thresholds**, route/format output, and turn logging **up or down without code changes**. **Logback** is a common Java logging library.

Levels, from least to most severe:

| Level | Use |
|---|---|
| **TRACE** | Finest, step-by-step |
| **DEBUG** | Diagnostic detail during development |
| **INFO** | Routine execution status |
| **WARN** | Unexpected but non-fatal; may cause a future error |
| **ERROR** | An operation failed at runtime |

> ✅ **Takeaway:** Logging libraries give configurable thresholds. Logback levels: TRACE < DEBUG < INFO < WARN < ERROR.

---

# Part 2 — SQL

<a name="s1"></a>
## 22. SQL, RDBMS, and database

- **SQL** — the standard language for **defining, querying, and manipulating** data in a relational database.
- **RDBMS** — software that stores data in **tables (rows and columns)**, enforces **constraints/relationships** between them, and is queried with SQL (PostgreSQL, MySQL, SQL Server, Oracle).
- **Database** — an organized, structured collection of data managed by a DBMS so it can be stored, accessed, queried, and updated efficiently.

> ✅ **Takeaway:** SQL = the language; RDBMS = the software managing relational tables; database = the organized data itself.

---

<a name="s2"></a>
## 23. The five sublanguages

| Sublanguage | Purpose | Examples |
|---|---|---|
| **DDL** — Data Definition | Define structure | `CREATE`, `ALTER`, `DROP`, `TRUNCATE` |
| **DML** — Data Manipulation | Change data | `INSERT`, `UPDATE`, `DELETE` |
| **DQL** — Data Query | Read data | `SELECT` |
| **DCL** — Data Control | Permissions | `GRANT`, `REVOKE` |
| **TCL** — Transaction Control | Transaction boundaries | `COMMIT`, `ROLLBACK`, `SAVEPOINT` |

```sql
ALTER TABLE customer DROP COLUMN age;   -- DDL
```

> ✅ **Takeaway:** DDL (structure), DML (data), DQL (read), DCL (permissions), TCL (transactions).

---

<a name="s3"></a>
## 24. CRUD

The four basic persistence operations: **Create, Read, Update, Delete** → `INSERT`, `SELECT`, `UPDATE`, `DELETE`.

> ✅ **Takeaway:** CRUD = Create/Read/Update/Delete = INSERT/SELECT/UPDATE/DELETE.

---

<a name="s4"></a>
## 25. DROP vs TRUNCATE vs DELETE

| Command | Removes | Keeps table? | Rollback? | Class |
|---|---|---|---|---|
| **DROP** | The whole table **object** (definition + rows) | No | No (typically) | DDL |
| **TRUNCATE** | **All rows** fast | Yes (definition) | No (typically) | DDL |
| **DELETE** | Rows (optionally with `WHERE`) | Yes | Yes | DML |

```sql
DELETE FROM emp WHERE id = 42;   -- targeted, row-by-row, can roll back
TRUNCATE TABLE emp;              -- all rows, fast, keeps the table
DROP TABLE emp;                  -- table gone entirely
```

> ✅ **Takeaway:** DROP removes the table; TRUNCATE empties it (fast, no rollback); DELETE removes rows (can target with WHERE, can roll back).

---

<a name="s5"></a>
## 26. Data types & money

Use **`DECIMAL`/`NUMERIC`** (fixed-point, **exact**) for money. **`FLOAT`/`REAL`** are binary floating-point — they introduce **rounding errors** and you can't specify a fixed length/scale, so they're wrong for currency.

```sql
price DECIMAL(10,2)   -- exact: 10 digits total, 2 after the decimal
```

> ✅ **Takeaway:** Money → `DECIMAL(p,s)` (exact). Avoid `FLOAT`/`REAL` for currency (rounding errors).

---

<a name="s6"></a>
## 27. Keys

- **Primary key (PK)** — column(s) that **uniquely identify each row**; implicitly **UNIQUE + NOT NULL**; one per table.
- **Foreign key (FK)** — column(s) **referencing a primary key** in another table to enforce a relationship. Each child value must match an existing parent key.
- **Candidate key** — any minimal set of columns that could uniquely identify a row. A table may have several; one becomes the PK, the rest are **alternate keys**.
- **Composite key** — a key spanning **multiple columns** (common in junction tables).

> ✅ **Takeaway:** PK = unique row id (UNIQUE + NOT NULL). FK = reference to a PK elsewhere. Candidate keys are the options; one is chosen as PK.

---

<a name="s7"></a>
## 28. Referential integrity & constraints

**Referential integrity:** every **foreign key** value must **match an existing primary key** in the referenced table, or be **null** — so there are **no orphan** references.

Common **column constraints:**

| Constraint | Enforces |
|---|---|
| `PRIMARY KEY` | Unique + not null identity |
| `FOREIGN KEY` | Valid reference to another table |
| `UNIQUE` | No duplicate values |
| `NOT NULL` | Value required |
| `CHECK` | Custom condition (e.g. `salary >= 0`) |
| `DEFAULT` | Value used when none supplied |

> ✅ **Takeaway:** Referential integrity = FKs must match a real PK (or be null), no orphans. Constraints: PK, FK, UNIQUE, NOT NULL, CHECK, DEFAULT.

---

<a name="s8"></a>
## 29. Cardinality & multiplicity

Both describe how rows of one entity relate to another.

| Relationship | Example |
|---|---|
| **One-to-one** | person ↔ passport · doctor ↔ doctor-details |
| **One-to-many** | customer ↔ orders · patient ↔ appointments |
| **Many-to-many** | students ↔ courses · patients ↔ doctors |

**Cardinality** is the numerical relationship between one table's rows and another's; **multiplicity** describes how many instances of an entity relate to each other. Many-to-many requires a **junction table**.

> ✅ **Takeaway:** 1-to-1, 1-to-many, many-to-many. Many-to-many needs a junction table.

---

<a name="s9"></a>
## 30. ERD

An **Entity Relationship Diagram** is a visual model of a schema showing **entities** (tables), their **attributes**, and the **relationships/cardinality** between them.

> ✅ **Takeaway:** ERD = picture of entities, attributes, and relationships.

---

<a name="s10"></a>
## 31. Normalization

**Normalization** organizes tables to **reduce redundancy** (avoid storing the same data many ways) and **improve integrity**, by decomposing data into related tables with defined keys.

| Form | Requirement |
|---|---|
| **1NF** | **Atomic** values — no repeating groups, one value per cell (no arrays/lists in a cell) |
| **2NF** | 1NF **+ no partial dependency** — every non-key column depends on the **whole** (composite) key, not part of it |
| **3NF** | 2NF **+ no transitive dependency** — non-key columns depend only on the key, not on other non-key columns |

*Transitive example (violates 3NF):* if `city → state → country`, then `country` depends on `state` (a non-key), not directly on the key.

> ✅ **Takeaway:** 1NF atomic · 2NF whole-key (no partial) · 3NF no transitive dependency.

---

<a name="s11"></a>
## 32. Junction tables

A **junction (associative) table** implements a **many-to-many** relationship. It holds **two foreign keys** (one to each side), and its **primary key is often the composite of both**, which prevents duplicate pairings.

```sql
CREATE TABLE enrollment (
    student_id INT REFERENCES students(id),
    course_id  INT REFERENCES courses(id),
    PRIMARY KEY (student_id, course_id)   -- composite PK, no duplicate pairs
);
```

> ✅ **Takeaway:** Junction table = two FKs + composite PK to resolve many-to-many without duplicates.

---

<a name="s12"></a>
## 33. Joins

Joins combine tables **horizontally** by matching rows on a condition (the `ON` clause).

| Join | Returns |
|---|---|
| **INNER** | Only rows matching on **both** sides |
| **LEFT (OUTER)** | **All left** rows; unmatched right columns are **NULL** |
| **RIGHT (OUTER)** | All right rows; unmatched left columns NULL |
| **FULL (OUTER)** | All rows from **either** side |
| **CROSS** | Every pair — the **Cartesian product** |
| **SELF** | A table joined to itself |

```sql
SELECT e.name, d.name AS dept
FROM employees e
LEFT JOIN departments d ON e.dept_id = d.dept_id;  -- all employees, dept may be NULL
```

> ✅ **Takeaway:** INNER = matches only; LEFT = all left + NULLs; FULL = either side; CROSS = all pairs; SELF = to itself.

---

<a name="s13"></a>
## 34. Set operations

Set operations combine result sets **vertically** (stack rows); the `SELECT`s must have **compatible columns/types**.

| Operation | Result | Duplicates |
|---|---|---|
| **UNION** | Rows in **either** (like OR) | Removed |
| **UNION ALL** | Rows in either | **Kept** |
| **INTERSECT** | Rows in **both** (like AND) | Removed |
| **EXCEPT / MINUS** | Rows in the **first** not the second | Removed |

> ✅ **Takeaway:** UNION dedups; UNION ALL keeps dupes; INTERSECT = in both; EXCEPT = in first not second. Joins combine horizontally, set ops vertically.

---

<a name="s14"></a>
## 35. Query clauses & order of operations

- **`WHERE`** filters **rows before** grouping (no aggregates). **`HAVING`** filters **groups after** aggregation (can use `COUNT`, `SUM`…).
- **`GROUP BY`** collapses rows into groups for aggregation. **`ORDER BY`** sorts the final result (`ASC`/`DESC`).

**Logical order of operations** (even though `SELECT` is written first):

```
FROM → WHERE → GROUP BY → HAVING → SELECT → ORDER BY → LIMIT
```

```sql
SELECT dept_id, COUNT(*) AS n
FROM employees
WHERE active = true        -- rows first
GROUP BY dept_id
HAVING COUNT(*) > 5        -- groups after
ORDER BY n DESC;
```

> ✅ **Takeaway:** WHERE (rows, before) vs HAVING (groups, after). Order: FROM → WHERE → GROUP BY → HAVING → SELECT → ORDER BY → LIMIT.

---

<a name="s15"></a>
## 36. LIKE, BETWEEN, subqueries

- **`LIKE`** — pattern matching in `WHERE` with wildcards: `%` = any sequence, `_` = one char. `name LIKE 'A%'` finds names starting with A.
- **`BETWEEN`** — inclusive range: `age BETWEEN 18 AND 65` is `age >= 18 AND age <= 65`.
- **Subquery** — a query nested inside another (in `SELECT`, `FROM`, or `WHERE`); can be scalar, correlated, or return a set used with `IN`/`EXISTS`.

```sql
SELECT name FROM employees
WHERE salary > (SELECT AVG(salary) FROM employees);  -- scalar subquery
```

> ✅ **Takeaway:** LIKE (patterns: `%`, `_`), BETWEEN (inclusive range), subquery (a query inside a query).

---

<a name="s16"></a>
## 37. Aggregate vs scalar functions

- **Aggregate** — operates over a **set of rows**, returns **one** value: `SUM`, `AVG`, `COUNT`, `MIN`, `MAX`.
- **Scalar** — operates per **single value/row**, returns one value **per input**: `UPPER`, `LOWER`, `LENGTH`, `ROUND`, `CONCAT`, `NOW`.

An **alias** (`AS`) renames a column or table for readability: `SELECT AVG(salary) AS avg_pay`.

> ✅ **Takeaway:** Aggregate = many rows → one value (SUM/COUNT). Scalar = one value → one value (UPPER/ROUND).

---

<a name="s17"></a>
## 38. Transactions & ACID

A **transaction** is a **unit of work** — one or more statements executed as a single **all-or-nothing** logical operation, starting with `BEGIN` and ending at `COMMIT` (or `ROLLBACK`).

**ACID:**

| Property | Meaning |
|---|---|
| **Atomicity** | All changes commit together or roll back together as a unit |
| **Consistency** | The database moves between **valid states** (constraints + rules) |
| **Isolation** | Concurrent transactions don't see each other's improper **intermediate** effects |
| **Durability** | After `COMMIT`, committed data **survives crashes** |

**Atomic** = full effect or no effect at all (indivisible).

> ✅ **Takeaway:** Transaction = all-or-nothing unit. ACID = Atomicity, Consistency, Isolation, Durability.

---

<a name="s18"></a>
## 39. Isolation levels & read anomalies

Higher isolation prevents more anomalies at the cost of concurrency.

| Isolation level | Prevents |
|---|---|
| **READ UNCOMMITTED** | (nothing) |
| **READ COMMITTED** | Dirty reads |
| **REPEATABLE READ** | + Non-repeatable reads |
| **SERIALIZABLE** | + Phantom reads |

**The anomalies:**

- **Dirty read** — reading another transaction's **uncommitted** data (which may roll back).
- **Non-repeatable read** — re-reading the **same row** gives **different data** (another txn updated + committed).
- **Phantom read** — re-running a **range query** returns a **different number of rows** (rows inserted/deleted).

> ✅ **Takeaway:** Dirty (uncommitted) → Non-repeatable (row changed) → Phantom (row count changed). Higher isolation stops more.

---

<a name="s19"></a>
## 40. BASE

**BASE** is the NoSQL counterpart to ACID: **Basically Available, Soft state, Eventual consistency** — it favors **availability** and **eventual consistency** over strict ACID guarantees.

> ✅ **Takeaway:** BASE = Basically Available, Soft state, Eventual consistency (availability over strict consistency).

---

<a name="s20"></a>
## 41. Views & materialized views

- **View** — a **stored, named query** that acts as a **virtual table**. It does **not** make queries faster (it re-runs against base tables), but it **simplifies** complex queries and can restrict access.
- **Materialized view** — stores the query **result physically**; faster reads, but data can be **stale** until refreshed.

> ✅ **Takeaway:** View = virtual (re-computed, simplifies queries). Materialized view = stored result (fast but can be stale).

---

<a name="s21"></a>
## 42. Indexes

An **index** speeds up lookups, joins, and sorts by avoiding full table scans. Add a **secondary index** to a column used in **selective `WHERE`/`JOIN`/`ORDER BY`** paths where scans are costly.

**Downside:** every `INSERT`/`UPDATE`/`DELETE` must **maintain** the index, so **too many indexes slow down writes** and consume storage.

> ✅ **Takeaway:** Indexes speed reads on selective columns; they slow writes and use storage. Don't over-index.

---

<a name="s22"></a>
## 43. DAO (Data Access Object)

A **DAO** is a Java **interface/class** whose methods access the database, giving a **single, shared way** to talk to the DB. It **separates persistence from services**, so raw JDBC doesn't scatter across the app and the front end never issues backend DB commands directly. You can swap or test persistence easily.

```java
interface EmployeeDao { Employee findById(int id); void save(Employee e); }
class JdbcEmployeeDao implements EmployeeDao { /* JDBC here only */ }
```

> ✅ **Takeaway:** DAO isolates all DB access behind an interface, separating persistence from business logic.

---

<a name="s23"></a>
## 44. Stored procedures — trade-offs

**Pros:** fewer **network round trips**, logic lives **next to the data**, and rules can be enforced regardless of which app connects.
**Cons:** often **vendor-specific** SQL dialects, **harder to unit-test** like app code, and versioning can lag deployments.

> ✅ **Takeaway:** Procedures cut round trips and centralize logic, but are vendor-specific and harder to test/version.

---

<a name="s24"></a>
## 45. Prepared statements & SQL injection

Concatenating user input into SQL enables **SQL injection** (e.g. `' OR '1'='1`). A **`PreparedStatement`** uses `?` placeholders and binds values, so the database treats them as **data, not SQL syntax** — which blocks injection and enables plan reuse.

```java
PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE name = ?");
ps.setString(1, userInput);   // bound as data, safe
```

> ✅ **Takeaway:** Never concatenate input into SQL. Use `PreparedStatement` with `?` — bound values are data, not syntax.

---

<a name="s25"></a>
## 46. Ports

A **port number** is a logical endpoint (0–65535) identifying a specific **service/process** on a host for network traffic.

| Database | Default port |
|---|---|
| **PostgreSQL** | **5432** |
| **MySQL** | **3306** |

> ✅ **Takeaway:** Port = logical endpoint for a service. Postgres = 5432, MySQL = 3306.

---

## Rapid recap — the ten most common traps

| Topic | The correct answer |
|---|---|
| Overriding resolved at… | **Runtime** (overloading = compile time) |
| Overloading vs overriding params | Overloading = **different** params; overriding = **same** signature |
| Static methods | **Overloaded yes, overridden no** (they hide) |
| Parent of all exceptions | **`Throwable`** |
| PRIMARY KEY implies | **UNIQUE + NOT NULL** |
| Sorted collection | **`TreeSet`** |
| Money type | **`DECIMAL`** (not FLOAT) |
| WHERE vs HAVING | WHERE = rows before; HAVING = groups after |
| UNION vs UNION ALL | UNION dedups; UNION ALL keeps dupes |
| Prepared statement | Binds values as **data**, stopping injection |

*Read the guide once, then run the quiz cold. Anything you miss, jump back to that section — the fix is understanding the mechanism, not memorizing the letter.*
