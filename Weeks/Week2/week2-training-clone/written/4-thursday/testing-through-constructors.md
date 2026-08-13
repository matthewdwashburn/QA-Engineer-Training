# Constructors and Object Initialization

## Learning Objectives
- Define **constructors** precisely and explain how they differ from ordinary methods.
- Contrast **default** (implicit), **no-arg**, and **parameterized** constructors.
- Use **`this`** to disambiguate fields from parameters and **`this(...)`** for constructor chaining.
- Use **`super(...)`** to chain to a parent class constructor.
- Enforce **invariants** through constructor validation.
- Apply the **factory method** and **copy constructor** patterns.
- Understand why "testing through constructors" reduces test brittleness.
- Compare Java constructors to Python's `__init__`.

---

## Why This Matters

> **Weekly Epic Connection:** The filename "testing through constructors" reflects a key testing philosophy: **constructors are where objects become valid**. In unit tests you construct the **System Under Test (SUT)** via constructor or factory. If the constructor enforces invariants (e.g., non-null name, positive price), your test setup catches bad data immediately — making tests more reliable and failures clearer.

---

## The Concept

### What Is a Constructor?

A **constructor** is a special block of code that runs when you create a new object with `new`. It:

- Has the **same name as the class**
- Has **no return type** (not even `void`)
- Cannot be called on an existing instance — only on `new`
- Is responsible for **initializing the object's state** so it is valid

```java
public class User {
    // ↓ field declarations
    private final String name;
    private final String email;
    private int loginCount;

    // ↓ constructor — same name as class, no return type
    public User(String name, String email) {
        this.name  = name;
        this.email = email;
        this.loginCount = 0;   // Initialize to a sensible default
    }
}

// Creating an object — constructor runs here
User alice = new User("Alice", "alice@example.com");
```

**Comparison with Python:**

```python
class User:
    def __init__(self, name: str, email: str) -> None:  # Python's constructor equivalent
        self.name = name
        self.email = email
        self.login_count = 0
```

| Aspect | Java Constructor | Python `__init__` |
|--------|----------------|-------------------|
| Name | Must match class name | Always `__init__` |
| Return type | None (omitted, not `void`) | None (implicit `None`) |
| Self reference | `this` | `self` (explicit first parameter) |
| Multiple constructors | ✅ Overloading | ❌ Only one `__init__` (workaround: default args) |
| Chain to another constructor | `this(...)` | `self.__class__(...)` or reorganize |

---

### The Default (Implicit) Constructor

If you **declare no constructors**, Java automatically provides a **public, no-argument default constructor** that does nothing:

```java
public class Box { }          // No constructor declared

Box b = new Box();            // ✅ Works — Java provides default no-arg constructor
```

**Critical rule:** The default constructor disappears **as soon as you declare any constructor of your own**:

```java
public class Box {
    public Box(int size) {    // Custom constructor added
        this.size = size;
    }
}

Box empty = new Box();        // ❌ Compile error! No-arg constructor gone
Box sized = new Box(10);      // ✅ Works
```

If you need **both**, declare the no-arg constructor explicitly:

```java
public class Box {
    private int size;

    public Box() {            // ← explicitly provide no-arg back
        this.size = 0;
    }

    public Box(int size) {
        this.size = size;
    }
}
```

---

### `this` — Disambiguating Field vs Parameter

When a constructor parameter has the **same name** as a field, `this.fieldName` refers to the **instance field**, while the unqualified name refers to the **parameter**:

```java
public class Product {
    private String name;
    private double price;

    public Product(String name, double price) {
        //         ↑ parameter
        this.name  = name;    // this.name = field; name = parameter
        this.price = price;
        //   ↑ field  ↑ parameter
    }
}
```

Without `this.`, the assignment `name = name;` does nothing — it assigns the parameter to itself, leaving the field at its default value (`null`).

---

### Constructor Overloading

Java allows **multiple constructors** with different parameter lists — the compiler selects the correct one based on argument types:

```java
public class Connection {
    private final String host;
    private final int port;
    private final int timeoutMs;

    // Minimal constructor with defaults
    public Connection(String host) {
        this(host, 8080);          // Chains to the 2-arg constructor
    }

    // Mid-level constructor
    public Connection(String host, int port) {
        this(host, port, 5000);    // Chains to the full constructor
    }

    // Full constructor — all fields initialized here
    public Connection(String host, int port, int timeoutMs) {
        this.host      = host;
        this.port      = port;
        this.timeoutMs = timeoutMs;
    }
}

// Usage
Connection c1 = new Connection("localhost");             // host="localhost", port=8080, timeout=5000
Connection c2 = new Connection("prod.example.com", 443); // port=443, timeout=5000
Connection c3 = new Connection("api.example.com", 443, 2000); // all explicit
```

---

### Constructor Chaining with `this(...)`

`this(...)` calls **another constructor in the same class**. Rules:
- Must be the **first statement** in the constructor body — nothing else can come before it.
- You can only chain to **one** other constructor.
- Prevents code duplication by centralizing initialization in one "canonical" constructor.

```java
public class Report {
    private final String title;
    private final String author;
    private final boolean includeHeader;

    // 1-arg: delegates to 2-arg
    public Report(String title) {
        this(title, "Unknown");      // Must be FIRST statement
    }

    // 2-arg: delegates to 3-arg
    public Report(String title, String author) {
        this(title, author, true);   // Must be FIRST statement
    }

    // Canonical constructor — all others delegate here
    public Report(String title, String author, boolean includeHeader) {
        this.title         = title;
        this.author        = author;
        this.includeHeader = includeHeader;
    }
}
```

---

### Calling the Parent Constructor with `super(...)`

When a class **extends** another class, the child constructor must call the parent's constructor using `super(...)`. Java automatically calls the parent's **no-arg constructor** if you don't call `super(...)` explicitly — which fails if the parent has no no-arg constructor.

```java
public class Animal {
    protected final String name;

    public Animal(String name) {
        this.name = name;
    }
}

public class Dog extends Animal {
    private final String breed;

    public Dog(String name, String breed) {
        super(name);         // MUST be first — calls Animal(String name)
        this.breed = breed;
    }
}

Dog rex = new Dog("Rex", "German Shepherd");
```

- `super(...)` must also be the **first statement** in the constructor.
- You cannot have both `super(...)` and `this(...)` in the same constructor — the first statement can only be one of them.

---

### Enforcing Invariants in Constructors

A **class invariant** is a condition that must be true for the object to be valid. Constructors are the right place to enforce invariants — if the data is invalid, throw an exception **before** the object is created:

```java
public class Temperature {
    private final double celsius;

    public Temperature(double celsius) {
        if (celsius < -273.15) {
            throw new IllegalArgumentException(
                "Temperature below absolute zero: " + celsius
            );
        }
        this.celsius = celsius;
    }
}

// ✅ Valid
Temperature boiling = new Temperature(100.0);

// ❌ Throws IllegalArgumentException immediately
Temperature impossible = new Temperature(-300.0);
```

Common invariants to enforce:
```java
// Non-null name
if (name == null || name.isBlank()) {
    throw new IllegalArgumentException("Name cannot be null or blank");
}

// Positive price
if (price <= 0) {
    throw new IllegalArgumentException("Price must be positive, was: " + price);
}

// Collection not null (copy defensively)
if (items == null) {
    throw new NullPointerException("items must not be null");
}
this.items = new ArrayList<>(items);   // Defensive copy
```

> **QA testing angle:** When you test constructors directly, you can verify:
> 1. Valid inputs produce a correctly initialized object (happy path)
> 2. Invalid inputs throw the expected exception (validation tests)
> This makes test setup explicit and failures immediately obvious.

---

### The Factory Method Pattern

Sometimes a **static factory method** is cleaner than a constructor — it can have a descriptive name, return cached instances, or return a subtype:

```java
public class Color {
    private final int r, g, b;

    // Private constructor — only accessible through factory methods
    private Color(int r, int g, int b) {
        this.r = r; this.g = g; this.b = b;
    }

    // Factory methods with descriptive names
    public static Color of(int r, int g, int b) {
        if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
            throw new IllegalArgumentException("RGB values must be 0-255");
        }
        return new Color(r, g, b);
    }

    public static Color fromHex(String hex) {
        // Parse "#FF5733" format
        int r = Integer.parseInt(hex.substring(1, 3), 16);
        int g = Integer.parseInt(hex.substring(3, 5), 16);
        int b = Integer.parseInt(hex.substring(5, 7), 16);
        return new Color(r, g, b);
    }

    public static Color red()   { return new Color(255, 0, 0); }
    public static Color green() { return new Color(0, 255, 0); }
    public static Color blue()  { return new Color(0, 0, 255); }
}

// Usage
Color c1 = Color.of(255, 100, 50);
Color c2 = Color.fromHex("#FF5733");
Color c3 = Color.red();
```

---

### The Copy Constructor

A **copy constructor** creates a new object as an independent copy of an existing object — useful for making **defensive copies** and avoiding aliasing bugs:

```java
public class Cart {
    private List<String> items;

    public Cart() {
        this.items = new ArrayList<>();
    }

    // Copy constructor — creates an independent copy
    public Cart(Cart other) {
        this.items = new ArrayList<>(other.items);  // Deep copy of the list
    }

    public void addItem(String item) { this.items.add(item); }
    public List<String> getItems()   { return Collections.unmodifiableList(items); }
}

Cart original = new Cart();
original.addItem("Widget");

Cart copy = new Cart(original);   // Independent copy
copy.addItem("Gadget");

System.out.println(original.getItems());  // ["Widget"]    — unchanged
System.out.println(copy.getItems());      // ["Widget", "Gadget"]
```

---

### `record` — Immutable Data Carriers (Java 16+)

For simple data-holding classes, Java 16+ provides **records** — they automatically generate the constructor, getters, `equals`, `hashCode`, and `toString`:

```java
// record automatically provides:
// - A canonical constructor: TestResult(String name, boolean passed, int durationMs)
// - Accessor methods: name(), passed(), durationMs()
// - equals(), hashCode(), toString()
public record TestResult(String name, boolean passed, int durationMs) { }

// Usage
TestResult r = new TestResult("login test", true, 142);
System.out.println(r.name());       // "login test"
System.out.println(r.passed());     // true
System.out.println(r.durationMs()); // 142
System.out.println(r);              // TestResult[name=login test, passed=true, durationMs=142]
```

Use `record` when your class is a transparent, immutable data carrier. Use a regular class when you need mutable state, invariant validation, or custom behaviour.

---

## Testing Through Constructors — The Philosophy

The reason this file is named "testing through constructors" is a testing principle:

> **Prefer constructing objects in tests through their constructor (or factory), never through setters on a partially initialized object.**

```java
// ❌ Fragile test setup — object created then mutated
User user = new User();
user.setName("Alice");
user.setEmail("alice@example.com");
// What if a field is forgotten? Object is in invalid state.

// ✅ Robust test setup — constructor enforces validity
User user = new User("Alice", "alice@example.com");
// Object is guaranteed valid from line 1
```

This also means: **test the constructor itself**:

```java
@Test
void constructor_rejectsNullName() {
    assertThrows(IllegalArgumentException.class,
        () -> new User(null, "alice@example.com"));
}

@Test
void constructor_initializesFieldsCorrectly() {
    User user = new User("Alice", "alice@example.com");
    assertEquals("Alice", user.getName());
    assertEquals("alice@example.com", user.getEmail());
    assertEquals(0, user.getLoginCount());  // Default initialized correctly
}
```

---

## Summary

- A **constructor** = same name as class + no return type + runs on `new`.
- The **default no-arg constructor** disappears when you declare any constructor — restore it explicitly if needed.
- **`this.field`** disambiguates instance field from constructor parameter.
- **`this(...)`** chains to another constructor in the same class — must be the first statement.
- **`super(...)`** calls the parent class constructor — must be the first statement.
- Enforce **invariants** in constructors by throwing `IllegalArgumentException` or `NullPointerException` on bad input.
- **Factory methods** are a cleaner alternative when you need descriptive names or custom creation logic.
- **Copy constructors** create independent copies, preventing aliasing bugs.
- **`record`** (Java 16+) auto-generates constructor, accessors, `equals`, `hashCode`, `toString` for immutable data classes.
- **Test constructors directly** to verify invariants — robust test setup creates valid objects through constructors, not through setters.

---

## Additional Resources

- [Oracle: Providing Constructors for Your Classes](https://docs.oracle.com/javase/tutorial/java/javaOO/constructors.html)
- [JLS: Constructors](https://docs.oracle.com/javase/specs/jls/se21/html/jls-8.html#jls-8.8)
- [Java Records (JEP 395)](https://openjdk.org/jeps/395)
- [Effective Java (Bloch) — Item 1: Consider static factory methods over constructors](https://www.oreilly.com/library/view/effective-java/9780134686097/)
