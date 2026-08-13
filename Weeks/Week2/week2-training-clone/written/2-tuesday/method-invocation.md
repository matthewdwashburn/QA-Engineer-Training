# Method Invocation in Java

## Learning Objectives
- Call **static** and **instance** methods with correct syntax.
- Understand when a **dot** (`.`) is required and what it means.
- Describe **method chaining** — when it works and when it doesn't.
- Explain **nested method calls** and argument evaluation order.
- Understand the **call stack** and how it relates to stack traces.
- Use `System.out.println()`, `Math` methods, and `String` methods as practical examples.

---

## Why This Matters

> **Weekly Epic Connection:** Fluent test DSLs (`assertThat(result).isNotNull().isGreaterThan(0)`), builder patterns (`Request.builder().url(url).method("GET").build()`), and framework APIs all rely on **method chaining** and precise invocation rules. Stack traces list **call stack** frames directly — reading them is a daily QA debugging skill.

---

## The Concept

### The Dot Operator

In Java, the **dot** (`.`) is the operator that accesses a member (method or field) of an object or class. It is the equivalent of Python's `.` notation:

```java
// Class.method()    → calls a static method on a class
// object.method()   → calls an instance method on an object
// object.field      → accesses an instance field (if accessible)
```

---

### Static Method Invocation

A **static method** belongs to the class, not to any object. Call it using the **class name** followed by a dot:

```java
// Syntax: ClassName.methodName(arguments)

double root = Math.sqrt(9.0);             // 3.0
int absolute = Math.abs(-42);             // 42
double power = Math.pow(2, 10);           // 1024.0
int random = (int)(Math.random() * 100);  // 0–99

// String static methods
String converted = String.valueOf(42);    // "42"
String formatted = String.format("Hello, %s! Score: %d", "Alice", 95);
// "Hello, Alice! Score: 95"
```

You do **not** create an object to call static methods:
```java
// ❌ Wrong — don't create an instance to call a static method
Math m = new Math();        // compile error — Math cannot be instantiated
m.sqrt(9.0);

// ✅ Correct — class name directly
Math.sqrt(9.0);
```

---

### Instance Method Invocation

An **instance method** operates on a specific object's state. You must first have an object reference, then call the method on it:

```java
// Syntax: objectReference.methodName(arguments)

String s = "Hello, World!";

int len    = s.length();             // 13
String up  = s.toUpperCase();        // "HELLO, WORLD!"
String sub = s.substring(0, 5);      // "Hello"
boolean sw = s.startsWith("Hello");  // true
int idx    = s.indexOf(",");         // 5
String rep = s.replace("World", "QA"); // "Hello, QA!"
```

Each call operates on the specific `String` object `s`. A different string object would give different results.

```java
String a = "alice";
String b = "BOB";

// Same method, different objects, different results
System.out.println(a.toUpperCase());  // "ALICE"
System.out.println(b.toLowerCase());  // "bob"
System.out.println(a.compareTo(b));   // 31 (lexicographic distance)
```

---

### Calling Methods Defined in Your Own Class

When calling a method **from within the same class**, you can omit the class name (for static) or object reference (for instance):

```java
public class Calculator {

    public static int multiply(int a, int b) {
        return a * b;
    }

    public static int square(int x) {
        return multiply(x, x);   // Calling another static in the same class — no class prefix needed
    }

    public static void main(String[] args) {
        System.out.println(square(5));    // 25
        System.out.println(multiply(3, 7)); // 21
    }
}
```

From **outside** the class, you need the class prefix:
```java
int result = Calculator.multiply(3, 7);  // Must qualify with class name
```

---

### `System.out.println()` — Decomposed

This is the most common method call you'll write in Java. Let's break it down:

```java
System.out.println("Hello");
```

| Part | What It Is |
|------|-----------|
| `System` | A **class** in `java.lang` — always available, no import needed |
| `System.out` | A **static field** on the `System` class — holds a `PrintStream` object |
| `.println(...)` | An **instance method** on the `PrintStream` object |

So this is: access a static field, then call an instance method on the object in that field.

```java
// Equivalent verbose form
PrintStream out = System.out;  // Get the static field
out.println("Hello");           // Call instance method on it
```

Variants:
```java
System.out.println("text");      // Prints text + newline
System.out.print("text");        // Prints text, no newline
System.out.printf("Name: %s, Score: %d%n", name, score);  // Formatted output
System.err.println("error!");    // Prints to stderr instead of stdout
```

---

### Method Chaining

When a method returns an object, you can call another method on that returned object **in the same expression** — without storing intermediate results:

```java
// Without chaining — three separate statements
String raw = "  hello world  ";
String trimmed = raw.trim();
String upper = trimmed.toUpperCase();
System.out.println(upper);   // "HELLO WORLD"

// With chaining — one expression
String result = "  hello world  ".trim().toUpperCase();
System.out.println(result);   // "HELLO WORLD"
```

Each method call in the chain operates on the **return value of the previous call**:

```
"  hello world  "
    .trim()           → "hello world"
    .toUpperCase()    → "HELLO WORLD"
    .replace(" ", "_") → "HELLO_WORLD"
    .length()          → 11
```

> **Important:** Chaining only works when the intermediate methods **return the right type**. `String.length()` returns an `int`, so you can't chain String methods after it.

#### Builder Pattern (Preview)

The **builder pattern** — common in Java — explicitly returns `this` from each setter so all configuration can be chained:

```java
// Example with a hypothetical RequestBuilder
Request request = new RequestBuilder()
    .url("https://api.example.com/users")
    .method("POST")
    .header("Content-Type", "application/json")
    .body("{\"name\": \"Alice\"}")
    .timeout(5000)
    .build();
```

You'll see this pattern in REST Assured, OkHttp, and many other Java libraries.

#### AssertJ Chaining (Preview — Week 4)

Test assertion libraries use chaining to create fluent, readable assertions:

```java
// AssertJ fluent assertions
assertThat(user.getName()).isNotNull().isEqualTo("Alice");
assertThat(scores).hasSize(3).contains(85, 92).doesNotContain(-1);
assertThat(response.getStatusCode()).isEqualTo(200);
```

---

### Nested Method Calls

You can pass a method call's return value directly as an argument to another method. Java evaluates **arguments left to right** before making the outer call:

```java
// Math.max takes two ints and returns the larger
int m = Math.max(Math.min(10, 5), 3);

// Evaluation order:
// 1. Math.min(10, 5) → 5
// 2. Math.max(5, 3)  → 5
// Result: 5

System.out.println(Math.abs(Math.min(-5, -10)));
// 1. Math.min(-5, -10) → -10
// 2. Math.abs(-10)     → 10
// 3. println(10)
```

Be careful with deeply nested calls — they can become hard to read. Extract to named variables when clarity suffers:

```java
// ❌ Hard to read
System.out.println(String.format("Max: %d", Math.max(Math.abs(a), Math.abs(b))));

// ✅ Clearer
int absA = Math.abs(a);
int absB = Math.abs(b);
int maximum = Math.max(absA, absB);
System.out.println(String.format("Max: %d", maximum));
```

---

### The Call Stack

When methods call other methods, the JVM maintains a **call stack** — a stack of **frames**, one per active method call. Each frame stores:

- The method's local variables
- The current execution position (program counter)
- The return address (where to go when the method finishes)

```java
public class Demo {
    public static void main(String[] args) {
        printResult(5, 3);
    }

    public static void printResult(int a, int b) {
        int sum = add(a, b);
        System.out.println("Sum: " + sum);
    }

    public static int add(int a, int b) {
        return a + b;
    }
}
```

During execution of `add()`, the call stack looks like:

```
┌──────────────────────────────────┐  ← TOP (currently executing)
│ add(int a=5, int b=3)            │
├──────────────────────────────────┤
│ printResult(int a=5, int b=3)    │
├──────────────────────────────────┤
│ main(String[] args)              │
└──────────────────────────────────┘  ← BOTTOM (first called)
```

When `add` returns, its frame is popped. `printResult` resumes. When `printResult` returns, `main` resumes.

#### Stack Overflow

If methods keep calling each other recursively without stopping (infinite recursion), the stack grows until it hits the JVM's limit and throws `StackOverflowError`:

```java
public static void infinite() {
    infinite();   // Calls itself forever
}
// Throws: java.lang.StackOverflowError
```

#### Reading Stack Traces

When an uncaught exception occurs, the JVM prints the call stack at that moment. You read them **top-to-bottom** — the top is where the exception occurred, the bottom is where execution started:

```
Exception in thread "main" java.lang.NullPointerException
    at Demo.add(Demo.java:12)          ← Exception thrown HERE
    at Demo.printResult(Demo.java:7)   ← Called by this
    at Demo.main(Demo.java:3)          ← Entry point
```

> **QA skill:** When a test fails with an exception, the stack trace tells you **exactly which line** threw and the **full chain of calls** that led there. Start at line 1 of the stack trace (the actual failure), then read downward to understand context.

---

### Common Built-in Method Calls

#### `Math` Class (Static Methods)

```java
Math.abs(-5)          // 5         — absolute value
Math.max(10, 20)      // 20        — maximum
Math.min(10, 20)      // 10        — minimum
Math.pow(2, 8)        // 256.0     — power
Math.sqrt(16.0)       // 4.0       — square root
Math.round(3.7)       // 4L        — round to nearest long
Math.floor(3.9)       // 3.0       — round down
Math.ceil(3.1)        // 4.0       — round up
Math.random()         // 0.0–1.0   — random double
```

#### `String` Class (Instance Methods)

```java
String s = "Hello, World!";
s.length()                    // 13
s.charAt(0)                   // 'H'
s.toUpperCase()               // "HELLO, WORLD!"
s.toLowerCase()               // "hello, world!"
s.trim()                      // "Hello, World!" (removes leading/trailing whitespace)
s.strip()                     // Same as trim(), but handles Unicode whitespace
s.replace("World", "QA")      // "Hello, QA!"
s.contains("World")           // true
s.startsWith("Hello")         // true
s.endsWith("!")               // true
s.split(", ")                 // ["Hello", "World!"]
s.substring(7)                // "World!"
s.substring(7, 12)            // "World"
s.indexOf("o")                // 4 (first occurrence)
s.isEmpty()                   // false
s.isBlank()                   // false (Java 11+)
s.equals("Hello, World!")     // true (always use equals, not == for Strings!)
```

---

## Summary

- **Static invocation**: `ClassName.method(args)` — no object required.
- **Instance invocation**: `objectRef.method(args)` — operates on the object's state.
- **`System.out.println()`**: static field `System.out` (a `PrintStream`) → instance method `println()`.
- **Method chaining** works when each method returns an object with further methods — common in builders and assertion libraries.
- **Nested calls** are evaluated left-to-right, inner-to-outer — extract to variables for readability.
- **The call stack** is a stack of active method frames — the **stack trace** is a snapshot of it at the moment of an exception.
- Stack traces are read **top-to-bottom** — the top line is the actual failure site.

---

## Additional Resources

- [Java `Math` class](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Math.html)
- [Java `String` class](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/String.html)
- [Understanding stack traces (Oracle Tutorial)](https://docs.oracle.com/javase/tutorial/essential/exceptions/stacktrace.html)
- [Java `System` class](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/System.html)
