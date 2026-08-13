# Java & SQL QC — Missed Concepts, From the Ground Up

A focused rebuild of the fundamentals behind every question you missed on the quizzes and in the interview. Read this **before** doing the flashcards and quiz. Each section starts from the ground and ends with the exact fact the question was testing.

**How to read this:** the mechanism first, then a small example, then a boxed takeaway. The trap callouts (⚠️) are the specific wrong answer that's tempting — usually the one you picked.

---

## Table of contents

1. [The Java platform: bytecode, the JVM, and "write once, run anywhere"](#1-the-java-platform)
2. [Memory: the stack vs the heap](#2-memory-stack-vs-heap)
3. [Classes, objects, and constructors](#3-classes-objects-and-constructors)
4. [The `static` keyword (and why `main` is static)](#4-the-static-keyword)
5. [`final`, immutability, and the String pool](#5-final-immutability-and-the-string-pool)
6. [Polymorphism: overloading vs overriding vs hiding](#6-polymorphism)
7. [Abstract classes vs interfaces](#7-abstract-classes-vs-interfaces)
8. [Language mechanics that tripped you up](#8-language-mechanics)
9. [Exceptions from the ground up](#9-exceptions)
10. [Collections: TreeSet ordering and ArrayDeque as a stack](#10-collections)
11. [Build tooling: Maven and the Central Repository](#11-maven)
12. [SQL: what PRIMARY KEY really means](#12-sql-primary-key)
13. [Talking to databases: JDBC, SQLite, and SQLAlchemy](#13-persistence)
14. [Rapid recap — every miss in one line](#14-rapid-recap)

---

<a name="1-the-java-platform"></a>
## 1. The Java platform: bytecode, the JVM, and "write once, run anywhere"

When you compile a C program, the compiler produces machine code for **one** specific processor and OS. Move that binary to a different platform and it won't run.

Java takes a different path. The `javac` compiler does **not** produce machine code. It produces an intermediate format called **bytecode** (`.class` files). Bytecode isn't tied to any physical CPU — it targets an abstract machine called the **Java Virtual Machine (JVM)**.

```
YourCode.java  --javac-->  YourCode.class (bytecode)  --JVM-->  runs on any OS
```

Every platform (Windows, macOS, Linux) has its own JVM implementation. Each JVM knows how to translate the *same* bytecode into that platform's native instructions at runtime. So you compile once, and the bytecode runs anywhere a JVM exists.

That property is Java's slogan: **"Write once, run anywhere" (WORA).**

> ⚠️ **Interview trap:** "Platform-specific compilation" is the opposite of what Java does — that's the C model. And while "cross-platform compatibility" is a *true statement about the result*, the named **feature/mechanism** the question wants is **write once, run anywhere**, powered by bytecode + the JVM.

> ✅ **Takeaway:** Java source → **bytecode** → runs on the **JVM** on any OS. The JVM is what executes bytecode; that's what makes Java platform-independent.

---

<a name="2-memory-stack-vs-heap"></a>
## 2. Memory: the stack vs the heap

Two regions of memory matter for interviews:

| Region | Stores | Lifetime |
|--------|--------|----------|
| **Stack** | Local variables, method-call frames (parameters, return addresses) | Popped automatically when the method returns |
| **Heap** | Objects and instances created with `new` | Lives until no references remain; cleaned by the garbage collector |

When you call a method, a **stack frame** is pushed holding its local variables. When the method returns, that frame is popped. Objects, however, live on the **heap** — your local variable on the stack just holds a **reference** (an address) pointing to the object on the heap.

```java
void demo() {
    int x = 5;                 // x lives on the stack
    Employee e = new Employee(); // the Employee OBJECT is on the heap;
                               // 'e' (the reference) is on the stack
}
```

This is also why deep recursion throws a `StackOverflowError`: each call adds a frame, and the stack has a fixed size.

> ✅ **Takeaway:** Stack = local variables + method-call data. Heap = objects/instances. References live on the stack and point to objects on the heap.

---

<a name="3-classes-objects-and-constructors"></a>
## 3. Classes, objects, and constructors

A **class** is a blueprint: it declares fields (attributes), methods (behavior), and constructors. An **object** is a concrete, runtime **instance** of that class, created with `new` and stored on the heap.

```java
class Employee {          // the blueprint
    String name;
    Employee(String name) { this.name = name; }  // constructor
}

Employee e = new Employee("Matt");  // 'e' references one instance
```

### Constructors — the details that get tested

A constructor is a special method that runs **when a new instance is created**. Its job is to initialize the object's fields, usually from the parameters passed in.

Three facts interviewers probe:

1. **A constructor has no return type — not even `void`.** If you write `void Employee()`, that's a regular method that happens to share the class name, *not* a constructor.
2. **If you declare no constructor, Java gives you a default no-argument constructor.** The moment you declare *any* constructor, that free default disappears.
3. **Constructors are not inherited, so they cannot be overridden.** They *can* be overloaded (several constructors with different parameter lists).

```java
class Account {
    Account() { }              // overload 1
    Account(double balance) { }// overload 2 — legal (overloading)
}
// You cannot "@Override" a constructor — it isn't inherited.
```

> ✅ **Takeaway:** Constructor = no return type, runs on `new`, auto-provided only if you declare none, **overloadable but never overridable**.

---

<a name="4-the-static-keyword"></a>
## 4. The `static` keyword (and why `main` is static)

`static` means a member belongs to the **class itself**, not to any individual instance. There is exactly **one shared copy**, and you can access it **without creating an object**.

```java
class Counter {
    static int count = 0;   // ONE copy shared by all instances
    int id;                 // each instance gets its own
}
Counter.count++;            // accessed via the class, no object needed
```

### Why `public static void main(String[] args)` is static

When the JVM starts your program, **no objects exist yet**. If `main` were an instance method, the JVM would face a chicken-and-egg problem: it needs an object to call `main`, but `main` is what creates objects. Making `main` **static** lets the JVM call it directly on the class, without instantiating anything.

### Static and `final` are *non-access* modifiers

Java modifiers fall into two groups:

- **Access modifiers** (control visibility): `public`, `protected`, `private`, and default (package-private).
- **Non-access modifiers** (control behavior): `static`, `final`, `abstract`, `synchronized`, `volatile`, `transient`, `native`.

> ⚠️ **Interview trap:** `static` and `final` are **non-access** modifiers. They're often listed alongside `public`/`private`, but they control *behavior*, not *visibility*.

### Static methods can be overloaded but **not** overridden

This one you flagged yourself. A static method can be **overloaded** (same name, different parameters). But it cannot be **overridden**. If a subclass declares a static method with the identical signature, that is **method hiding**, not overriding — see [Section 6](#6-polymorphism) for the full mechanics.

> ✅ **Takeaway:** `static` = belongs to the class, one shared copy, callable with no object. `main` is static so the JVM can launch it before any object exists. Static methods overload, they don't override.

---

<a name="5-final-immutability-and-the-string-pool"></a>
## 5. `final`, immutability, and the String pool

`final` locks something down, but *what* it locks depends on where it's applied:

| Applied to | Meaning |
|-----------|---------|
| **variable** | Can't be **reassigned**. For a reference, the variable can't point elsewhere — but the object's own fields may still change. |
| **method** | Can't be **overridden** by a subclass. |
| **class** | Can't be **extended** (no subclasses). `String` is a final class. |

```java
final int MAX = 10;      // MAX = 20; would not compile
final List<String> list = new ArrayList<>();
list.add("ok");          // legal — we mutated the object, not the reference
// list = new ArrayList<>();  // illegal — reassigning the reference
```

### Strings are immutable, and the string pool

A `String` object **cannot be changed after it's created**. Every operation that "modifies" a string (`concat`, `toUpperCase`, `replace`) actually returns a **new** String.

Because strings are immutable and used everywhere, Java keeps a **string pool** (a cache of string literals). Two identical literals share the **same** pooled object:

```java
String a = "hello";
String b = "hello";
a == b;                 // true — both point to the SAME pooled object

String c = new String("hello");
a == c;                 // false — 'new' forces a distinct object off the pool
a.equals(c);            // true — same characters (compare with equals, not ==)
```

> ✅ **Takeaway:** `final` variable → no reassign; `final` method → no override; `final` class → no extend. Strings are **immutable**; identical literals are shared in the **string pool**; compare content with `.equals()`, not `==`.

---

<a name="6-polymorphism"></a>
## 6. Polymorphism: overloading vs overriding vs hiding

This is the highest-value section for you — it explains **four** separate misses at once (overriding runtime, static hiding, and the overload/override notes).

Polymorphism comes in two flavors distinguished by **when the method to run is chosen**:

### Overloading = compile-time (static binding)

Same method **name**, **different parameter lists**, in the same class. The compiler picks which version to call based on the argument types **at compile time**.

```java
int add(int a, int b) { return a + b; }
double add(double a, double b) { return a + b; }
add(2, 3);      // compiler binds to the int version — decided at COMPILE time
```

### Overriding = runtime (dynamic dispatch)

A **subclass** provides a method with the **same signature** as its superclass. Which version runs is decided **at runtime**, based on the **actual object type**, not the reference type. This is also called *dynamic dispatch* or *late binding*.

```java
class Animal { void speak() { System.out.println("..."); } }
class Dog extends Animal { @Override void speak() { System.out.println("Woof"); } }

Animal a = new Dog();
a.speak();   // prints "Woof" — chosen at RUNTIME by the real object (Dog)
```

Even though `a` is *declared* as `Animal`, the JVM looks at the real object (`Dog`) at runtime and calls `Dog.speak()`.

> ⚠️ **Interview trap:** "Method overriding occurs at compile time" is the classic wrong answer. **Overriding is resolved at runtime.** Overloading is the compile-time one. Remember: **overRIDE → RUNtime**, overLOAD → compile.

### Static methods "hide" — they don't override

Overriding relies on the *actual object*. But static methods belong to the **class**, and there's no object to dispatch on. So if a subclass declares a static method with the same signature, it **hides** the parent's version. Which one runs is decided by the **reference type at compile time**, not the object at runtime.

```java
class Parent { static void greet() { System.out.println("Parent"); } }
class Child extends Parent { static void greet() { System.out.println("Child"); } }

Parent p = new Child();
p.greet();   // prints "Parent" — chosen by the REFERENCE type, not the object
             // this is HIDING, not overriding
```

That's why we say **static methods can be overloaded but not overridden**.

### Quick comparison

| | Overloading | Overriding | Hiding (static) |
|---|---|---|---|
| Where | Same class | Subclass | Subclass |
| Signature | **Different** params | **Same** | **Same** |
| Resolved | Compile time | **Runtime** | Compile time |
| Based on | Argument types | **Actual object** | Reference type |

> ✅ **Takeaway:** Overloading = compile-time (different params). Overriding = **runtime** (same signature, real object). Static same-signature = **hiding** (compile-time, reference type). Constructors overload but never override.

---

<a name="7-abstract-classes-vs-interfaces"></a>
## 7. Abstract classes vs interfaces

Both let you define a type you can't instantiate directly, but they solve different problems.

An **abstract class** is a partial blueprint: it can hold **state** (instance fields), **constructors**, concrete methods **with bodies**, and abstract methods **without** bodies. Subclasses fill in the abstract methods.

An **interface** is a pure **contract**: a list of method signatures an implementing class promises to fulfill.

### The pre-Java 8 distinction the quiz tested

**Before Java 8, interfaces could not contain method implementations** — every method was implicitly `public abstract`, just a signature. Abstract classes always could have concrete methods. That's the primary difference the question wanted.

```java
// pre-Java 8 interface — signatures only, no bodies
interface Drivable {
    void start();        // no body allowed (pre-8)
}

abstract class Vehicle {
    void honk() { System.out.println("honk"); }  // concrete body — always allowed
    abstract void start();                        // abstract — subclass implements
}
```

> ⚠️ **Interview trap:** "Abstract classes support multiple inheritance" is **false** — Java has single **class** inheritance (a class `extends` exactly one class). You get multiple inheritance of **type** only through interfaces (a class can `implement` many). The real pre-8 difference is: **interfaces cannot have method implementations.**

### What changed in Java 8+

Interfaces gained **`default`** methods (with bodies) and **`static`** methods, and later **`private`** helper methods. They still **cannot** hold instance state or constructors.

### When to use which

- Need shared **state** or a common concrete base → **abstract class**.
- Need a **capability/contract** that unrelated classes can adopt, or you need a class to have several types → **interface**.

| | Abstract class | Interface |
|---|---|---|
| Instantiate? | No | No |
| Instance fields (state)? | Yes | No |
| Constructors? | Yes | No |
| Method bodies? | Yes | Only `default`/`static` (Java 8+) |
| A class can have… | one (`extends`) | many (`implements`) |

> ✅ **Takeaway:** Pre-Java 8, the key difference is **interfaces have no method implementations**. Java classes do **not** support multiple inheritance; interfaces give multiple inheritance of *type*.

---

<a name="8-language-mechanics"></a>
## 8. Language mechanics that tripped you up

Small syntax/rule facts that are pure recall.

### Declaring an array — both forms are legal

```java
int[] arr;   // preferred style (type clearly reads "array of int")
int arr[];   // also valid — C-style, legal but discouraged
```

Both compile. Style guides prefer `int[] arr` because the `[]` belongs to the type. The quiz's answer was **"Both A and B."**

> 💡 Related recall: arrays use `arr.length` (a **field**, no parentheses). That's different from `String.length()` (a **method**) and `List.size()` (collections). Mixing these up is a common output-prediction trap.

### `switch` — which types are allowed

`switch` works on: `byte`, `short`, `char`, `int` (and their wrapper classes), **`String`** (Java 7+), and **`enum`**.

It does **not** accept: `long`, `float`, **`double`**, `boolean`.

```java
double d = 1.0;
switch (d) { ... }   // COMPILE-TIME ERROR — double is not allowed
```

> ⚠️ **Interview trap:** `enum` and `String` in a switch are perfectly legal. The one that fails to compile is **`double`** (and `float`, `long`, `boolean`).

### Multiple classes in one `.java` file

Yes — a single file may contain multiple **top-level** classes, but **at most one can be `public`**, and the file must be named after that public class.

```java
// File must be named Main.java because Main is public
public class Main { }
class Helper { }     // legal — non-public companion class
```

> ⚠️ **Interview trap:** "No, only one class per file" is wrong. The rule is **one public class per file**, named to match the file.

### Wrapper classes (quick recall)

Each primitive has an object **wrapper**: `int → Integer`, `double → Double`, `char → Character`, `boolean → Boolean`, etc. Wrappers let primitives live in generics/collections and carry utility methods (`Integer.parseInt`, `Integer.MAX_VALUE`). **Autoboxing** converts between them automatically. They're immutable — beware the `Integer` cache (−128…127) with `==`.

> ✅ **Takeaway:** `int[] arr` and `int arr[]` both work; `switch` rejects `double`; a file allows many classes but one `public`; primitives have object wrappers.

---

<a name="9-exceptions"></a>
## 9. Exceptions from the ground up

An **exception** is an object representing a problem that disrupts normal flow. The whole family descends from one root:

```
            Object
              |
          Throwable          <-- the true root of everything throwable
          /       \
       Error      Exception
      (serious    /       \
     JVM issues) RuntimeException   (other checked exceptions:
                  (unchecked)         IOException, SQLException, ...)
```

### `Throwable` is the parent of all exceptions **and** errors

Both `Exception` and `Error` extend **`Throwable`**. So the common ancestor of *everything* you can `throw` or `catch` is `Throwable`, not `Exception`.

> ⚠️ **Interview trap:** "The parent class of all exceptions is `Exception`" is wrong, because `Error` is **not** under `Exception` — both sit under **`Throwable`**. `Throwable` is the real root.

### Checked vs unchecked

- **Checked** (extend `Exception`, not `RuntimeException`): the **compiler forces** you to `catch` them or declare `throws`. Model recoverable, external failures (`IOException`, `SQLException`).
- **Unchecked** (`RuntimeException` and its subclasses, plus `Error`): no `throws` required. Usually signal programming bugs (`NullPointerException`, `ArrayIndexOutOfBoundsException`).

### Handling: try / catch / finally, throw vs throws

```java
try {
    // risky code
} catch (SpecificException e) {   // most specific first
    // recover or log
} finally {
    // always runs — cleanup (close resources)
}
```

- **`throw`** actually throws an instance: `throw new IllegalArgumentException("bad");`
- **`throws`** is a *declaration* in a method signature that it may throw a checked exception, pushing handling to the caller.

### Writing a custom exception (your notes)

```java
class InvalidAgeException extends Exception {   // checked (extends Exception)
    public InvalidAgeException(String message) {
        super(message);                          // pass message up to Exception
    }
}

// usage:
if (age < 0) {
    throw new InvalidAgeException("Invalid age");
}
```

You subclass `Exception` (checked) or `RuntimeException` (unchecked), and typically pass a message to `super(...)`.

> ✅ **Takeaway:** **`Throwable`** is the root of all exceptions and errors. Checked = compiler-enforced; unchecked = `RuntimeException`. Custom exception = subclass `Exception` and call `super(message)`.

---

<a name="10-collections"></a>
## 10. Collections: TreeSet ordering and ArrayDeque as a stack

### The collection that keeps things sorted: `TreeSet`

Among the common collections:

| Collection | Order |
|-----------|-------|
| `ArrayList` | Insertion order, index access, allows duplicates |
| `HashSet` | **No** guaranteed order, unique elements |
| `LinkedList` | Insertion order (as a list/deque) |
| **`TreeSet`** | **Sorted** by natural ordering (`Comparable`) or a supplied `Comparator` |

`TreeSet` is backed by a balanced tree, so iterating it yields elements **in sorted order**, at `O(log n)` per add/contains. Its elements must be mutually **`Comparable`**, or you pass a **`Comparator`**.

```java
TreeSet<Integer> t = new TreeSet<>();
t.add(5); t.add(1); t.add(3);
System.out.println(t);   // [1, 3, 5] — always sorted
```

> ⚠️ **Interview trap:** `ArrayList` preserves *insertion* order, not *sorted* order. The collection guaranteeing sorted order by natural ordering or a comparator is **`TreeSet`**.

### `ArrayDeque` as a stack — predict the output

`ArrayDeque` implements `Deque` ("double-ended queue"). Used as a **stack**, `push()` adds to the head and `pop()` removes from the head — classic **LIFO**.

```java
ArrayDeque<Integer> ad = new ArrayDeque<>();
ad.push(4);                       // stack: [4]
System.out.println(ad.pop());     // removes & returns 4  ->  prints 4
```

No compile error, no runtime error — it prints **`4`**.

> ⚠️ **Interview trap:** This compiles and runs fine. `push`/`pop` on a `Deque` are legal stack operations; the output is simply **`4`**. `ArrayDeque` is in fact the recommended stack in modern Java (over the legacy `Stack` class).

> ✅ **Takeaway:** **`TreeSet`** = sorted. **`ArrayDeque`** used with `push`/`pop` = LIFO stack; `push(4); pop()` returns `4`.

---

<a name="11-maven"></a>
## 11. Build tooling: Maven and the Central Repository

**Maven** is a build/dependency-management tool. You list dependencies in `pom.xml`, and Maven fetches the right JARs automatically instead of you downloading them by hand.

Where does it fetch from? By default, the **Maven Central Repository** (`repo.maven.apache.org` / `repo1.maven.org`). Maven first checks your **local repository** — the `.m2` folder on your machine, which is just a **cache** of previously downloaded artifacts. On a cache miss, it downloads from **Central**.

```
pom.xml declares dependency
      |
      v
check local ~/.m2 cache  --miss-->  download from Maven Central (the default remote repo)
```

> ⚠️ **Interview trap:** "Local cache only" describes `.m2`, which is a cache — **not** the default *repository* Maven resolves from. "GitHub" is not Maven's default. The default is the **Central Repository**.

> ✅ **Takeaway:** Maven's default remote is the **Central Repository**; `.m2` is a local **cache** checked first.

---

<a name="12-sql-primary-key"></a>
## 12. SQL: what PRIMARY KEY really means

Marking a column `PRIMARY KEY` isn't just a label — it **implicitly enforces two constraints**:

1. **UNIQUE** — no two rows may share the value.
2. **NOT NULL** — the value is required; it can never be null.

A table has **one** primary key (which may span multiple columns as a composite key). It's how each row is uniquely identified.

```sql
CREATE TABLE employees (
    emp_id INT PRIMARY KEY,   -- automatically UNIQUE and NOT NULL
    name   VARCHAR(100)
);
```

Why the other options were wrong:

- **`final`** — a **Java** keyword, meaningless in SQL.
- **`serial`** — a Postgres pseudo-type for **auto-increment**; a primary key does *not* imply auto-increment (you choose that separately).
- **`default`** — a primary key has no implied default value.
- **`foreign key`** — unrelated; that's a *reference* to another table's key.

> ⚠️ **Interview trap:** PRIMARY KEY implies exactly **UNIQUE + NOT NULL** — nothing about `serial`/auto-increment or defaults.

> ✅ **Takeaway:** `PRIMARY KEY` ⇒ **UNIQUE and NOT NULL**, one per table.

---

<a name="13-persistence"></a>
## 13. Talking to databases: JDBC, SQLite, and SQLAlchemy

Several misses all live in the "how does code reach the database" layer. Here's the map.

### JDBC (Java's database API)

JDBC is a set of **interfaces**; each database vendor ships a **driver** that implements them. The key players, in order of use:

| Interface / class | Role |
|---|---|
| `Driver` | The **vendor's implementation**. `DriverManager` uses it to *create* connections. It is a factory, not the connection itself. |
| **`Connection`** | Represents the **physical session/connection** to a specific database. This is what you actually open and hold. |
| `Statement` / `PreparedStatement` | Sends SQL over that connection. Use `PreparedStatement` (with `?` placeholders) to prevent SQL injection. |
| `ResultSet` | The rows returned by a query; you iterate it. |

```java
Connection conn = DriverManager.getConnection(url, user, pass);  // <-- Connection = the physical connection
PreparedStatement ps = conn.prepareStatement("SELECT * FROM emp WHERE id = ?");
ps.setInt(1, 42);
ResultSet rs = ps.executeQuery();
```

> ⚠️ **Interview trap:** `Driver` is the mechanism `DriverManager` uses *behind the scenes*, but the interface that **represents the physical connection** is **`Connection`**. `DriverManager.getConnection(...)` hands you a `Connection`.

### The JDBC "utility class"

In JDBC apps you typically write a small helper (a **JDBC utility class**) that **centralizes connection creation and resource cleanup** — opening a `Connection`, and closing `Connection`/`Statement`/`ResultSet` so you don't leak resources. It's about **connection and resource management**, not running business queries (that's the DAO's job).

> ⚠️ **Interview trap:** A JDBC utility class **handles database connection and resource management**, not "executing SQL queries directly."

### SQLite `:memory:`

SQLite is a lightweight, file-based database. Passing the special name **`:memory:`** creates a **temporary, in-memory database** that lives only for the duration of that connection and **vanishes when the connection closes** — nothing is written to disk. It's ideal for tests.

```python
import sqlite3
conn = sqlite3.connect(":memory:")   # temporary DB, gone when conn closes
```

> ⚠️ **Interview trap:** `:memory:` is **not** a permanent file — it's a throwaway **in-memory** database.

### SQLAlchemy (Python's ORM/toolkit): Engine vs Session vs ORM

SQLAlchemy has two layers. Three names you must separate:

| Component | Role |
|---|---|
| **Engine** | The starting point for connectivity — manages the **connection pool** and dialect to the database. It is *not* where you manage transactions or map objects. |
| **ORM** | The Object-Relational Mapper — lets you work with **database rows as Python objects** (mapped classes). This is the "records as objects" layer. |
| **Session** | The ORM's **unit of work** — it tracks your objects and **manages transactions** (`add`, `flush`, `commit`, `rollback`). |

```python
engine = create_engine("sqlite:///app.db")   # connection pool / connectivity
Session = sessionmaker(bind=engine)
session = Session()                            # manages the transaction
session.add(user)                              # 'user' is a mapped ORM object
session.commit()                               # commit the transaction
```

> ⚠️ **Interview traps (two you hit):**
> - "Interact with records as Python objects" → the **ORM** (not the Cursor — that's the low-level DB-API).
> - "Manage transactions" → the **Session** (not the Engine — the Engine manages *connections*, the Session manages the *transaction*).

> ✅ **Takeaway:** JDBC: **`Connection`** = physical connection; utility class = **connection/resource management**. SQLite **`:memory:`** = temporary in-memory DB. SQLAlchemy: **ORM** = records as objects, **Session** = transactions, **Engine** = connection pool.

---

<a name="14-rapid-recap"></a>
## 14. Rapid recap — every miss in one line

Use this the morning of. If any line surprises you, reread that section.

| # | Question | The answer |
|---|----------|-----------|
| 1 | Java feature to run on any platform | **Write once, run anywhere** (bytecode + JVM) |
| 2 | Declare an int array | **Both** `int[] arr;` **and** `int arr[];` |
| 3 | `switch` type that fails to compile | **`double`** (also float/long/boolean) |
| 4 | Multiple classes in one file? | **Yes — but only one `public`** |
| 5 | Method overriding occurs at… | **Runtime** (overloading = compile time) |
| 6 | Can static methods be overridden? | **No — same-signature static = hiding** (compile time) |
| 7 | Can constructors be overridden? | **No** (not inherited); they can be **overloaded** |
| 8 | Constructor return type | **None** (not even `void`) |
| 9 | Why is `main` static? | So the **JVM can call it with no instance** |
| 10 | `static` / `final` are… | **Non-access** modifiers |
| 11 | Are Strings mutable? | **No — immutable**; literals share the **string pool** |
| 12 | Abstract vs interface (pre-8) | **Interfaces can't have method implementations** |
| 13 | Do abstract classes allow multiple inheritance? | **No** — Java has single class inheritance |
| 14 | Parent class of all exceptions | **`Throwable`** (not `Exception`) |
| 15 | Sorted collection by natural/comparator order | **`TreeSet`** |
| 16 | `ArrayDeque`: `push(4); pop()` prints | **`4`** (LIFO, no error) |
| 17 | Maven's default repository | **Central Repository** (`.m2` is a local cache) |
| 18 | `PRIMARY KEY` implicitly implies | **UNIQUE + NOT NULL** |
| 19 | SQLite `:memory:` | **Temporary in-memory database** |
| 20 | JDBC interface for the physical connection | **`Connection`** |
| 21 | JDBC utility class does | **Connection & resource management** |
| 22 | SQLAlchemy: records as Python objects | **ORM** |
| 23 | SQLAlchemy: manages transactions | **Session** (Engine = connection pool) |

---

*Read this once end-to-end, then run the multiple-choice quiz cold. Anything you miss on the quiz, come back to that section — the fix is almost always understanding the mechanism, not memorizing the answer.*
