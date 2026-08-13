# Method Declaration and Syntax in Java

## Learning Objectives
- Read and write a Java **method signature**: modifiers, return type, name, and parameters.
- Understand each part of the method anatomy and what it controls.
- Distinguish all four **access modifiers** and know when to use each.
- Explain the difference between **`static`** and **instance** methods.
- Use **`void`** vs typed return types correctly.
- Apply Java **naming conventions** (camelCase, verb-led method names).
- Compare Java method declarations to equivalent Python function definitions.

---

## Why This Matters

> **Weekly Epic Connection:** Every Java class you read — in test code, production APIs, or framework source — is built from **methods**. Being able to fluently read and write method signatures lets you navigate APIs, understand what a method does without running it, mock dependencies in tests, and write your own utility helpers.

---

## The Concept

### Anatomy of a Method

Every Java method has the same structure:

```java
[access modifier] [other modifiers] returnType methodName(paramType paramName, ...) {
    // method body
    return value;  // required unless return type is void
}
```

A concrete example with all parts labelled:

```java
//   ①       ②     ③    ④          ⑤
public static int  add(int a, int b) {
    return a + b;   // ⑥
}
```

| # | Part | Example | Meaning |
|---|------|---------|---------|
| ① | Access modifier | `public` | Who can call this method |
| ② | Non-access modifier | `static` | Belongs to the class, not an instance |
| ③ | Return type | `int` | The type of value the method returns |
| ④ | Method name | `add` | Identifier — camelCase, verb-led |
| ⑤ | Parameter list | `(int a, int b)` | Typed inputs the caller provides |
| ⑥ | Method body | `{ return a + b; }` | The statements that execute when called |

---

### Access Modifiers

Access modifiers control which other code can call a method. Java has four levels:

| Modifier | Keyword | Accessible From | Typical Use |
|----------|---------|----------------|-------------|
| **Public** | `public` | Anywhere — any class, any package | API methods, `main`, constructors |
| **Protected** | `protected` | Same package + subclasses | Template methods in base classes |
| **Package-private** | *(no keyword)* | Same package only | Internal helpers within a module |
| **Private** | `private` | Only inside the declaring class | Implementation details, helper methods |

```java
public class BankAccount {

    public double getBalance() {           // Accessible by anyone
        return calculateBalance();
    }

    private double calculateBalance() {   // Internal implementation — hidden
        return credits - debits;
    }

    protected void audit(String event) {  // Visible to subclasses
        log(event);
    }

    void resetCache() {                   // Package-private — no keyword
        cache.clear();
    }
}
```

> **Best practice (encapsulation):** Default to `private`. Make something `public` only if external callers need it. This is the same principle as using leading underscores in Python for "private" members — Java enforces it at the compiler level.

---

### Non-Access Modifiers

Beyond access modifiers, Java methods can have additional modifiers:

| Modifier | Meaning |
|----------|---------|
| `static` | Method belongs to the **class**, not an instance. Called without creating an object. |
| `final` | Cannot be **overridden** by a subclass. |
| `abstract` | Has no body — subclasses **must** provide the implementation. |
| `synchronized` | Only one thread can execute this method at a time (thread safety). |

---

### `static` vs Instance Methods

This is one of the most important distinctions in Java method design:

#### Static Methods

A **`static`** method belongs to the **class itself**, not to any particular object instance. It can be called without creating an object:

```java
public class MathUtils {

    public static int square(int x) {
        return x * x;
    }

    public static double circleArea(double radius) {
        return Math.PI * radius * radius;
    }
}

// Called on the class — no object needed
int result = MathUtils.square(5);         // 25
double area = MathUtils.circleArea(3.0);  // 28.274...
```

A static method **cannot** access instance fields (`this.field`) — it has no `this`. It can only use its own parameters and other static members.

#### Instance Methods

An **instance method** operates on a specific object's state. It requires an object to call it:

```java
public class Counter {
    private int count = 0;  // Instance field

    public void increment() {    // Instance method — uses this.count
        count++;
    }

    public int getCount() {
        return count;
    }
}

Counter c1 = new Counter();
Counter c2 = new Counter();
c1.increment();
c1.increment();
c2.increment();

System.out.println(c1.getCount());  // 2
System.out.println(c2.getCount());  // 1 — separate object, separate state
```

> **When to use `static`:** When the method is a pure function (depends only on its parameters, not on object state). Utility classes (`Math`, `Arrays`, `Collections`) are full of static methods. If the method needs `this`, it must be an instance method.

---

### Return Types

Every method must declare a return type. The method body **must** return a value of that type on every code path.

#### `void` — No Return Value

```java
public void printReport(String title) {
    System.out.println("=== " + title + " ===");
    System.out.println("Generated: " + LocalDate.now());
    // No return statement needed (return; is optional for early exit)
}
```

#### Primitive Return Types

```java
public static int add(int a, int b) {
    return a + b;   // Returns int
}

public static boolean isEven(int n) {
    return n % 2 == 0;   // Returns boolean
}

public static double average(int[] nums) {
    int sum = 0;
    for (int n : nums) sum += n;
    return (double) sum / nums.length;   // Returns double
}
```

#### Object Return Types

```java
public static String formatScore(int score) {
    if (score < 0) {
        return "invalid";    // Returns on this path
    }
    if (score >= 70) {
        return "PASS";       // Returns on this path
    }
    return "FAIL";           // Returns on this path — compiler requires coverage of ALL paths
}

public static List<String> getPassingNames(List<String> names, List<Integer> scores) {
    List<String> result = new ArrayList<>();
    for (int i = 0; i < names.size(); i++) {
        if (scores.get(i) >= 70) {
            result.add(names.get(i));
        }
    }
    return result;   // Returns a List<String>
}
```

> **The compiler enforces completeness:** If any code path through a non-void method doesn't end with a `return` statement, the compiler gives `error: missing return statement`. This is a major advantage of static typing.

---

### The `main` Method — The Entry Point

Every executable Java application needs exactly one special method that the JVM looks for to start execution:

```java
public static void main(String[] args) {
    // Program starts executing here
}
```

Each part of this signature is **mandatory**:

| Part | Why Required |
|------|-------------|
| `public` | The JVM must be able to call it from outside the class |
| `static` | Called without creating an instance of the class |
| `void` | The JVM doesn't use a return value from `main` |
| `main` | The exact name the JVM looks for |
| `String[] args` | Command-line arguments passed after the class name |

```bash
# The JVM passes these as args[0]="Alice", args[1]="42"
java MyApp Alice 42
```

```java
public static void main(String[] args) {
    if (args.length > 0) {
        System.out.println("Hello, " + args[0]);
    }
}
```

---

### Java Naming Conventions

Java has strong naming conventions enforced by the community (and often by style tools like Checkstyle):

| Element | Convention | Example |
|---------|-----------|---------|
| **Methods** | `camelCase`, verb-led | `calculateTotal()`, `isValid()`, `getName()` |
| **Classes** | `PascalCase` | `UserService`, `OrderController`, `TestResult` |
| **Variables / parameters** | `camelCase` | `firstName`, `testCount`, `maxRetries` |
| **Constants** | `UPPER_SNAKE_CASE` | `MAX_RETRIES`, `DEFAULT_TIMEOUT` |
| **Packages** | `lowercase.dotted` | `com.example.utils`, `org.junit.jupiter` |

```java
public class TestRunner {
    private static final int MAX_RETRIES = 3;   // constant

    public boolean runTest(String testName) {    // camelCase method, boolean return
        return executeWithRetry(testName, MAX_RETRIES);
    }

    private boolean executeWithRetry(String name, int retries) {
        // implementation
        return false;
    }
}
```

---

### Comparison: Java Method vs Python Function

```java
// Java method
public static double average(double a, double b) {
    return (a + b) / 2.0;
}
```

```python
# Python equivalent
def average(a: float, b: float) -> float:
    return (a + b) / 2.0
```

Key differences:
1. Java requires **explicit types** on every parameter and the return type.
2. Java uses `public static` modifiers — Python has no equivalent.
3. Java uses `{` `}` for blocks; Python uses indentation and `:`.
4. Java requires a **`return`** statement; Python does too (unless `None` is intended).
5. Java's `void` ≈ Python's implicit `return None`.

---

## Code Examples

### Utility Class with Multiple Methods

```java
public class StringUtils {

    // Private constructor — prevents instantiation of a utility class
    private StringUtils() {}

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static String repeat(String s, int times) {
        if (isNullOrEmpty(s) || times <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    public static String capitalize(String s) {
        if (isNullOrEmpty(s)) {
            return s;
        }
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }

    public static void main(String[] args) {
        System.out.println(repeat("QA ", 3));       // "QA QA QA "
        System.out.println(capitalize("hELLO"));    // "Hello"
        System.out.println(isNullOrEmpty(""));      // true
        System.out.println(isNullOrEmpty("test"));  // false
    }
}
```

---

## Summary

- A Java method signature = **access modifier** + **other modifiers** + **return type** + **name** + **parameter list** + **body**.
- **Access modifiers** (`public` > `protected` > package-private > `private`) control visibility — default to `private`.
- **`static`** methods belong to the class; **instance** methods belong to an object and can access `this`.
- **`void`** for no return value; any other type must be returned on **every** code path.
- **`main`** (`public static void main(String[] args)`) is the JVM's entry point — its signature is mandatory.
- Follow conventions: **camelCase** methods, **PascalCase** classes, **UPPER_SNAKE_CASE** constants.

---

## Additional Resources

- [Oracle Java Tutorial: Methods](https://docs.oracle.com/javase/tutorial/java/javaOO/methods.html)
- [Oracle Java Tutorial: Controlling Access to Members of a Class](https://docs.oracle.com/javase/tutorial/java/javaOO/accesscontrol.html)
- [Java Language Specification: Methods](https://docs.oracle.com/javase/specs/jls/se21/html/jls-8.html#jls-8.4)
- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) — industry-standard naming and formatting rules
