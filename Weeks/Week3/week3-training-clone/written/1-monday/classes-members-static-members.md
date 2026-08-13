# Instance Members vs Static Members

## Learning Objectives
- Contrast **instance fields/methods** (per-object) with **static fields/methods** (per-class) with confidence.
- Explain the memory model: one slot in class space for static, one per heap object for instance.
- Identify when `static` is appropriate and when it is the wrong choice.
- Use `static final` for constants correctly.
- Describe **static initialiser blocks** and their execution timing.
- Recognise anti-patterns: calling static members on an instance, mutable static state in tests.

---

## Why This Matters

> **Weekly Epic Connection:** Misusing `static` for state that should be per-object (or vice versa) is one of the most common design bugs in Java. It causes shared state between test cases, makes classes impossible to test in isolation, and creates subtle threading bugs. This topic supports encapsulation and clean class design across the week's OOP epic.

---

## The Concept

### Instance Members — Per-Object State and Behaviour

**Instance fields** and **instance methods** belong to a **specific object**. Each object gets its own copy of instance fields:

```java
public class BankAccount {
    private String owner;    // Each BankAccount object has its OWN owner
    private double balance;  // Each BankAccount object has its OWN balance

    public BankAccount(String owner, double initialBalance) {
        this.owner   = owner;
        this.balance = initialBalance;
    }

    public void deposit(double amount) {
        // 'this' is the specific account being operated on
        this.balance += amount;
    }

    public double getBalance() {
        return this.balance;
    }
}

BankAccount alice = new BankAccount("Alice", 1000.0);
BankAccount bob   = new BankAccount("Bob",   500.0);

alice.deposit(200.0);
// alice.balance = 1200.0 — only Alice's account changed
// bob.balance   = 500.0  — Bob's account is independent
```

**Memory model:**
```
HEAP
┌─────────────────────────────────┐
│ BankAccount { owner="Alice"     │   ← alice's object
│               balance=1200.0 }  │
├─────────────────────────────────┤
│ BankAccount { owner="Bob"       │   ← bob's object
│               balance=500.0 }   │
└─────────────────────────────────┘
```

---

### Static Members — Per-Class, Shared by All Instances

**Static fields** have **one single storage location** shared by all instances. **Static methods** belong to the class itself — no object is required to call them:

```java
public class BankAccount {
    private String owner;
    private double balance;

    // ONE storage location — shared by ALL BankAccount objects
    private static int totalAccounts = 0;
    private static double totalDepositsAllTime = 0.0;

    public BankAccount(String owner, double initialBalance) {
        this.owner   = owner;
        this.balance = initialBalance;
        totalAccounts++;              // Increment the SHARED counter
    }

    public void deposit(double amount) {
        this.balance += amount;
        totalDepositsAllTime += amount;  // Updates shared total
    }

    // Static method — no 'this', called on the class not on an instance
    public static int getTotalAccounts() {
        return totalAccounts;
    }
}

BankAccount a1 = new BankAccount("Alice", 1000.0);
BankAccount a2 = new BankAccount("Bob",   500.0);

System.out.println(BankAccount.getTotalAccounts());  // 2 — class-level call (preferred)
System.out.println(a1.getTotalAccounts());           // 2 — works but misleading; IDE warns
```

**Memory model:**
```
CLASS SPACE (Metaspace)
┌────────────────────────────────────────────┐
│ BankAccount.class {                        │
│   totalAccounts = 2            ← ONE slot  │
│   totalDepositsAllTime = 1500.0           │
│   method code for deposit(), getBalance() │
│   method code for getTotalAccounts()      │
│ }                                          │
└────────────────────────────────────────────┘

HEAP
┌────────────────────────────────────────────┐
│ BankAccount { owner="Alice", balance=1000} │  ← per-instance
│ BankAccount { owner="Bob",   balance=500 } │  ← per-instance
└────────────────────────────────────────────┘
```

---

### What Static Methods Can and Cannot Do

Static methods have **no `this` reference** — they cannot directly access instance fields or call instance methods:

```java
public class MathUtils {
    private int instanceValue = 42;      // Instance field
    private static int staticValue = 99; // Static field

    public static int add(int a, int b) {
        return a + b;               // ✅ Pure arithmetic — fine
    }

    public static void badMethod() {
        System.out.println(instanceValue);  // ❌ Compile error — no 'this' in static context
        deposit(10.0);                      // ❌ Compile error — instance method requires an instance
    }

    public static void goodMethod(MathUtils obj) {
        System.out.println(obj.instanceValue);  // ✅ Can access instance member via parameter
    }
}
```

---

### `static final` — Constants

The combination of `static` (class-scoped) and `final` (cannot be reassigned) creates **constants**. Convention: ALL_CAPS with underscores:

```java
public class Config {
    public static final String APP_NAME  = "MyApp";
    public static final int    MAX_RETRY = 3;
    public static final double TAX_RATE  = 0.085;

    // Constants from complex expressions
    public static final long   MAX_FILE_SIZE = 10L * 1024 * 1024;  // 10 MB in bytes
}

// Usage — reference by class name directly, no instance needed
System.out.println(Config.APP_NAME);   // "MyApp"
int attempts = Config.MAX_RETRY;       // 3
```

> **`static final` vs just `final`:**
> - `static final` = one value, shared by all, cannot change (a class constant)
> - `final` on an instance field = each object has its own value, but it can't be reassigned after construction

---

### When to Use `static`

Use `static` for:

| Use Case | Example |
|----------|---------|
| **Constants** — fixed values shared by all | `public static final int MAX = 100;` |
| **Utility/helper methods** — no object state needed | `Math.sqrt()`, `Collections.sort()`, `Objects.requireNonNull()` |
| **Factory methods** — create/return instances | `List.of()`, `Optional.empty()`, `LocalDate.now()` |
| **Counters/caches** truly shared across all instances | `static int nextId = 1;` in an ID generator |

**Do NOT use `static` for:**

| Anti-Pattern | Problem |
|-------------|---------|
| State that should vary per object | All instances unexpectedly share the value |
| Replacing dependency injection with a static service locator | Hard to test, hard to swap implementations |
| Mutable static fields in test code | Tests leak state to each other — flaky test suites |

---

### Static Members and Testing — A Common Pitfall

Static mutable state is dangerous in test suites because tests share the JVM and don't reset class state between runs:

```java
// ❌ Problematic — static mutable counter
public class UserService {
    private static int usersCreated = 0;

    public static int create(String name) {
        usersCreated++;
        return usersCreated;
    }

    public static int getUsersCreated() { return usersCreated; }
}

// Test 1 passes: creates user, expects usersCreated = 1
// Test 2 fails: counter was NOT reset between tests — it's still 1 from Test 1!
```

> **QA insight:** If a test passes alone but fails when run as part of a suite, suspect **static mutable state** leaking between tests. Prefer instance fields that are reset in `@BeforeEach`.

---

### Static Initialiser Blocks

A **static initialiser block** `static { }` runs **exactly once** when the class is first loaded by the JVM — before any constructor or static method call:

```java
public class Config {
    static final Properties props = new Properties();

    static {
        // Runs ONCE when Config.class is first loaded
        try (InputStream is = Config.class.getResourceAsStream("/app.properties")) {
            if (is == null) {
                throw new ExceptionInInitializerError("app.properties not found on classpath");
            }
            props.load(is);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}
```

Use static blocks for **one-time class-level setup** that is too complex for a single field initialiser (e.g. loading configuration, building a lookup map, registering drivers).

**Execution order:**
```
1. Class loaded into JVM
2. Static fields initialised (in declaration order)
3. Static blocks run (in declaration order)
4. Constructor runs (on each new instance)
```

---

### Instance Initialiser Blocks (vs Static)

Java also has **instance initialiser blocks** `{ }` — they run before the constructor body, after `super()`:

```java
public class Widget {
    private final long createdAt;

    {   // Instance initialiser — runs for every new Widget()
        createdAt = System.currentTimeMillis();
    }

    public Widget() {
        // Constructor runs AFTER instance initialiser
    }
}
```

Instance initialisers are rare in modern Java — prefer putting logic in constructors directly.

---

## Summary

- **Instance fields/methods** are **per-object** — each instance has its own copy.
- **Static fields/methods** are **per-class** — one storage location shared by all instances.
- Static methods have **no `this`** — they cannot access instance members without a reference.
- `static final` creates **class constants** — uppercase by convention.
- Call static members via the **class name** (`BankAccount.getTotalAccounts()`), never via an instance (works but misleads readers).
- Avoid **mutable static state** — it leaks between tests and makes code hard to reason about.
- **Static blocks** run once at class load time for one-time complex initialisation.

---

## Additional Resources

- [Oracle Java Tutorial — Understanding Class Members](https://docs.oracle.com/javase/tutorial/java/javaOO/classvars.html)
- [JLS: Static Initializers](https://docs.oracle.com/javase/specs/jls/se21/html/jls-8.html#jls-8.7)
- [Effective Java (Bloch) — Item 4: Enforce noninstantiability with a private constructor](https://www.oreilly.com/library/view/effective-java/9780134686097/)
