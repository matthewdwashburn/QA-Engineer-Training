# Method Parameters and Return Types

## Learning Objectives
- Explain Java's **pass-by-value** semantics for both primitives and reference types.
- Declare methods with multiple parameters, default-like patterns, and varargs.
- Overload methods by **different parameter lists** and understand how the compiler resolves overloads.
- Declare all valid return types and use **`return`** correctly on every code path.
- Compare Java's parameter model with Python's function arguments.

---

## Why This Matters

> **Weekly Epic Connection:** Test utilities frequently overload helpers — `assertEquals(int, int)`, `assertEquals(String, String)`, `assertEquals(Object, Object)` in JUnit are real examples. Understanding **pass-by-value** prevents subtle bugs when you pass lists or other mutable objects between test helpers. Getting return types right is fundamental to writing any Java method.

---

## The Concept

### Java Primitive Types (What You Pass and Return)

Before discussing parameters, know the eight primitive types Java provides. These are the building blocks of every parameter and return type:

| Type | Size | Range / Values | Example |
|------|------|---------------|---------|
| `byte` | 8 bits | -128 to 127 | `byte b = 100;` |
| `short` | 16 bits | -32,768 to 32,767 | `short s = 1000;` |
| `int` | 32 bits | -2³¹ to 2³¹-1 (~±2.1B) | `int count = 42;` |
| `long` | 64 bits | -2⁶³ to 2⁶³-1 | `long id = 123456789L;` |
| `float` | 32 bits | ~6–7 decimal digits | `float f = 3.14f;` |
| `double` | 64 bits | ~15–16 decimal digits | `double pi = 3.14159;` |
| `boolean` | 1 bit | `true` or `false` | `boolean ok = true;` |
| `char` | 16 bits | Unicode character | `char c = 'A';` |

> **Note:** For text, `String` is not a primitive — it is a class. For numbers in collections, you use wrapper classes (`Integer`, `Double`, `Boolean`, etc.) that box primitives into objects.

---

### Pass-by-Value — The Most Important Rule

Java uses **pass-by-value for everything** — always. However, what is "passed by value" differs between primitives and reference types:

#### Primitives — A Copy of the Value

When you pass a primitive to a method, the method gets a **copy** of the value. Changes inside the method do **not** affect the caller's variable:

```java
public static void doubleIt(int x) {
    x = x * 2;    // Only the local copy changes
    System.out.println("Inside: " + x);   // 20
}

public static void main(String[] args) {
    int value = 10;
    doubleIt(value);
    System.out.println("After: " + value);   // Still 10 — not changed!
}
```

```
Inside: 20
After:  10       ← original is unaffected
```

#### Reference Types — A Copy of the Reference

When you pass an object (e.g., a `List`, `StringBuilder`, your own class), the method gets a **copy of the reference** (the "address" pointing to the object). The copy points to the **same object** in memory, so:

- The method **can** modify the object's **internal state** (fields, list contents).
- The method **cannot** make the caller's variable point to a different object.

```java
import java.util.ArrayList;
import java.util.List;

public static void addItem(List<String> items) {
    items.add("new item");    // Modifies the LIST's contents — visible to caller
    items = new ArrayList<>(); // Only local reference is reseated — caller unaffected
}

public static void main(String[] args) {
    List<String> myList = new ArrayList<>();
    myList.add("original");

    addItem(myList);

    System.out.println(myList);   // ["original", "new item"] — mutation visible
    // The reassignment inside addItem() did NOT affect myList here
}
```

```java
// StringBuilder example — common in interview questions
public static void clear(StringBuilder sb) {
    sb.setLength(0);          // Mutates the StringBuilder — caller sees this
    sb = new StringBuilder(); // Reseats local ref only — caller unaffected
}

public static void main(String[] args) {
    StringBuilder s = new StringBuilder("hello");
    clear(s);
    System.out.println(s.toString());  // "" — setLength(0) worked
}
```

**Mental model:**

```
Caller's variable:  [REF] ─────────────────────────────────── [OBJECT on heap]
                                                                ↑
Method's parameter: [REF copy] ─────────────────────────────── same object

After reassignment inside method:
Method's parameter: [REF copy] ──→ [new object]   ← only local change
Caller's variable:  [REF] ─────────────────────── [original OBJECT] ← unchanged
```

---

### Declaring Parameters

Parameters are declared as **type + name** pairs inside the parentheses. Multiple parameters are separated by commas:

```java
// Single parameter
public static double squareRoot(double number) {
    return Math.sqrt(number);
}

// Multiple parameters — each with its own type
public static String formatResult(String name, int score, boolean passed) {
    String status = passed ? "PASS" : "FAIL";
    return String.format("[%s] %s: %d", status, name, score);
}

// Array parameter
public static double average(int[] numbers) {
    int total = 0;
    for (int n : numbers) total += n;
    return (double) total / numbers.length;
}
```

#### Varargs — Variable-Length Argument Lists

Java supports **varargs** (`type... name`) — a method can accept any number of arguments of the same type. Internally, they are treated as an array:

```java
// Accepts 0 or more int arguments
public static int sum(int... numbers) {
    int total = 0;
    for (int n : numbers) total += n;
    return total;
}

// Call with any number of arguments
sum()              // 0
sum(1, 2, 3)       // 6
sum(10, 20, 30, 40) // 100

// Or pass an array directly
int[] values = {5, 10, 15};
sum(values)        // 30
```

Rules for varargs:
- Must be the **last** parameter in the list.
- Only one varargs parameter per method.
- `System.out.printf(String format, Object... args)` uses this pattern.

---

### Method Overloading

**Overloading** means defining multiple methods with the **same name** but **different parameter lists** in the same class. The compiler selects the correct one based on the argument types at the call site:

```java
public class Formatter {

    // Overload 1: format an int
    public static String format(int value) {
        return String.format("[INT: %d]", value);
    }

    // Overload 2: format a double
    public static String format(double value) {
        return String.format("[DOUBLE: %.2f]", value);
    }

    // Overload 3: format a String
    public static String format(String value) {
        return String.format("[STRING: %s]", value);
    }

    // Overload 4: format with a label
    public static String format(String label, int value) {
        return String.format("[%s: %d]", label, value);
    }

    public static void main(String[] args) {
        System.out.println(format(42));           // [INT: 42]
        System.out.println(format(3.14));         // [DOUBLE: 3.14]
        System.out.println(format("hello"));      // [STRING: hello]
        System.out.println(format("score", 95));  // [score: 95]
    }
}
```

#### Overloading Rules

| Allowed for overloading? | Example |
|--------------------------|---------|
| ✅ Different parameter **types** | `max(int a, int b)` vs `max(double a, double b)` |
| ✅ Different parameter **count** | `log(String msg)` vs `log(String msg, int level)` |
| ✅ Different parameter **order** | `pair(int n, String s)` vs `pair(String s, int n)` |
| ❌ **Return type only** (not allowed) | `int getValue()` vs `double getValue()` — ambiguous, compile error |
| ❌ **Parameter names only** (not allowed) | `add(int a, int b)` vs `add(int x, int y)` — identical signatures |

#### Overload Resolution — Which One Wins?

The compiler applies a specific algorithm to pick the **most specific matching overload**:

1. **Exact match** — finds an overload whose parameter types exactly match the arguments.
2. **Widening primitives** — `int` can widen to `long`, `float`, or `double` automatically.
3. **Autoboxing** — `int` can be boxed to `Integer`.
4. **Varargs** — checked last.

```java
public static void show(int x) { System.out.println("int: " + x); }
public static void show(double x) { System.out.println("double: " + x); }

show(5);      // Picks show(int) — exact match
show(5.0);    // Picks show(double) — exact match
show(5L);     // Picks show(double) — long widens to double (no int overload matches)
```

---

### Return Types and the `return` Statement

#### Every Path Must Return

For any non-`void` method, the compiler verifies that **every possible execution path ends with a `return` statement** returning the correct type. This is compile-time enforcement:

```java
// ✅ Every path has a return
public static String classify(int score) {
    if (score < 0) {
        return "invalid";
    } else if (score >= 90) {
        return "excellent";
    } else if (score >= 70) {
        return "pass";
    } else {
        return "fail";       // ← must cover the remaining case
    }
}

// ❌ Compile error — some paths don't return
public static String classify(int score) {
    if (score >= 70) {
        return "pass";
    }
    // What if score < 70? No return here — error: missing return statement
}
```

#### Early Return

`return` can appear anywhere in the method body — it exits immediately:

```java
public static boolean isValidEmail(String email) {
    if (email == null || email.isBlank()) {
        return false;    // Early exit — no point validating a null/blank string
    }
    if (!email.contains("@")) {
        return false;    // Must have @
    }
    if (!email.contains(".")) {
        return false;    // Must have a dot
    }
    return true;         // All checks passed
}
```

Early returns (also called "guard clauses") often make code more readable by handling edge cases first.

#### `void` with Optional `return`

A `void` method doesn't return a value, but you can use `return;` (no value) to exit early:

```java
public static void printIfPositive(int x) {
    if (x <= 0) {
        return;    // Exit early — don't print
    }
    System.out.println("Positive: " + x);
}
```

---

### Returning Multiple Values

Java methods can only return **one value**. Common patterns for returning multiple pieces of data:

```java
// Pattern 1: Return an array (homogeneous data)
public static int[] minMax(int[] numbers) {
    int min = numbers[0], max = numbers[0];
    for (int n : numbers) {
        if (n < min) min = n;
        if (n > max) max = n;
    }
    return new int[]{min, max};
}

int[] result = minMax(new int[]{5, 2, 8, 1, 9});
System.out.println("Min: " + result[0] + ", Max: " + result[1]);

// Pattern 2: Return a custom object (preferred for heterogeneous data)
public record TestResult(String name, boolean passed, int durationMs) {}

public static TestResult runTest(String name) {
    long start = System.currentTimeMillis();
    boolean ok = execute(name);
    int ms = (int)(System.currentTimeMillis() - start);
    return new TestResult(name, ok, ms);
}

TestResult r = runTest("login");
System.out.printf("Test %s: %s (%dms)%n", r.name(), r.passed() ? "PASS" : "FAIL", r.durationMs());
```

---

### Comparison: Java vs Python Parameters

```java
// Java: typed, pass-by-value-of-reference
public static String formatScore(String name, int score) {
    return name + ": " + score;
}
```

```python
# Python: dynamic types, default args, keyword args, *args, **kwargs
def format_score(name: str, score: int = 0, **extra) -> str:
    return f"{name}: {score}"
```

| Feature | Python | Java |
|---------|--------|------|
| **Type declaration** | Optional (type hints) | Mandatory |
| **Default parameter values** | ✅ `def f(x=0)` | ❌ Use overloading instead |
| **Keyword arguments** | ✅ `f(x=5)` | ❌ Positional only |
| **Arbitrary args (`*args`)** | ✅ | ✅ Varargs (`int... nums`) — last param only |
| **Keyword args (`**kwargs`)** | ✅ | ❌ No equivalent |
| **Pass-by-value** | Reference semantics (similar to Java for objects) | Always value — primitives copied, refs copied |

---

## Code Example — Full Demonstration

```java
import java.util.Arrays;

public class Stats {

    // Overloaded: int array
    public static double average(int[] values) {
        if (values == null || values.length == 0) {
            return 0.0;
        }
        int sum = 0;
        for (int v : values) sum += v;
        return (double) sum / values.length;
    }

    // Overloaded: double array
    public static double average(double[] values) {
        if (values == null || values.length == 0) {
            return 0.0;
        }
        double sum = 0;
        for (double v : values) sum += v;
        return sum / values.length;
    }

    // Varargs overload
    public static double average(int first, int... rest) {
        int sum = first;
        for (int v : rest) sum += v;
        return (double) sum / (1 + rest.length);
    }

    // Returns two values via array
    public static double[] bounds(int[] values) {
        int min = values[0], max = values[0];
        for (int v : values) {
            if (v < min) min = v;
            if (v > max) max = v;
        }
        return new double[]{min, max};
    }

    public static void main(String[] args) {
        int[] scores = {85, 92, 78, 95, 61};

        System.out.printf("Average: %.1f%n", average(scores));       // 82.2
        System.out.printf("Varargs: %.1f%n", average(10, 20, 30));   // 20.0

        double[] b = bounds(scores);
        System.out.printf("Min: %.0f, Max: %.0f%n", b[0], b[1]);    // Min: 61, Max: 95
    }
}
```

---

## Summary

- Java has **8 primitive types** (`int`, `double`, `boolean`, `char`, etc.) and unlimited reference types (classes).
- Java is **always pass-by-value** — primitives pass a copy; reference types pass a copy of the reference.
- Mutating an object's **fields** inside a method is visible to the caller; **reassigning** the local reference is not.
- **Overloading** = same name, different parameter list — the compiler selects the most specific match.
- Overloading by **return type alone** is **not allowed**.
- Every non-`void` code path **must return** the correct type — the compiler enforces this.
- **Varargs** (`type... name`) allows variable-length argument lists — must be the last parameter.
- Return multiple values via arrays or custom objects (`record`, class).

---

## Additional Resources

- [Oracle: Passing Information to a Method or Constructor](https://docs.oracle.com/javase/tutorial/java/javaOO/arguments.html)
- [JLS: Overloading](https://docs.oracle.com/javase/specs/jls/se21/html/jls-8.html#jls-8.4.9)
- [JLS: `return` statement](https://docs.oracle.com/javase/specs/jls/se21/html/jls-14.html#jls-14.17)
- [Java `record` types (Java 16+)](https://docs.oracle.com/en/java/javase/21/language/records.html)
