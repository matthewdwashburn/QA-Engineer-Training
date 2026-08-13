# Mathematical Operators in Java

## Learning Objectives
- Use **arithmetic**, **assignment compound**, **increment/decrement**, and **modulus** operators correctly.
- Explain why **integer division truncates** and how to force a `double` result.
- Apply **operator precedence** and use parentheses for clarity.
- Avoid common pitfalls: integer overflow, mixing types, `String` concatenation traps.
- Use the **`Math`** class for common mathematical functions.

---

## Why This Matters

> **Weekly Epic Connection:** Assertions, timeouts, percentage calculations, and statistical checks in test harnesses all use arithmetic. Integer division bugs (`5 / 2 == 2`, not `2.5`) appear constantly in beginner and production code alike and cause silent data corruption that tests must catch.

---

## The Concept

### Arithmetic Operators

The five core arithmetic operators work on all numeric types:

| Operator | Name | Example | Result |
|----------|------|---------|--------|
| `+` | Addition | `3 + 4` | `7` |
| `-` | Subtraction | `10 - 3` | `7` |
| `*` | Multiplication | `4 * 5` | `20` |
| `/` | Division | `7 / 2` | `3` (int!) |
| `%` | Remainder (modulus) | `10 % 3` | `1` |

---

### Integer Division — The Most Common Gotcha

When **both operands are integers**, Java performs **integer division** — the result is truncated toward zero, not rounded:

```java
int a = 7 / 3;       // 2   — NOT 2.333...
int b = -7 / 3;      // -2  — truncates toward zero
int c = 10 / 2;      // 5   — exact, no truncation needed

// To get a double result, make at least ONE operand a double:
double d = 7.0 / 3;      // 2.3333...
double e = 7 / 3.0;      // 2.3333...
double f = (double) 7 / 3; // 2.3333... — explicit cast
double g = 7.0 / 3.0;    // 2.3333...

// Common mistake: casting the RESULT instead of an operand
double wrong = (double)(7 / 3);  // 2.0 — division already happened as int!
```

> **QA Test:** If a calculation like `averageScore = totalScore / testCount` gives wrong results, check whether both variables are `int`. The fix: `(double) totalScore / testCount`.

---

### Modulus (Remainder) Operator

`%` gives the **remainder** after integer division:

```java
10 % 3   // 1   (10 = 3×3 + 1)
15 % 5   // 0   (15 = 5×3 + 0 — evenly divisible)
7  % 2   // 1   (odd number check)
-7 % 3   // -1  (sign of result matches sign of dividend in Java)
```

Common uses:
```java
// Check if a number is even or odd
if (n % 2 == 0) { /* even */ }
if (n % 2 != 0) { /* odd */ }

// Wrap an index around a circular buffer
int next = (current + 1) % bufferSize;

// Every nth item (e.g., log every 100 iterations)
if (i % 100 == 0) { System.out.println("Progress: " + i); }
```

---

### Increment and Decrement

`++` adds 1; `--` subtracts 1. Both have **prefix** and **postfix** forms:

```java
int i = 5;

// Postfix: use the CURRENT value, THEN increment
int a = i++;    // a = 5, then i becomes 6

// Prefix: increment FIRST, THEN use the new value
int b = ++i;    // i becomes 7, then b = 7

// In isolation (most common use — in for loops)
i++;            // same as i = i + 1
++i;            // same result when standalone
i--;            // same as i = i - 1
```

```java
// Typical for-loop use (postfix vs prefix makes no practical difference here)
for (int j = 0; j < 10; j++) {    // j++ is the standard convention
    System.out.println(j);
}
```

> **Best practice:** Use `++`/`--` in isolation (standalone statements), not embedded inside complex expressions. Embedded pre/post increment is confusing and rarely worth the brevity.

---

### Compound Assignment Operators

These combine an operation with assignment. They also apply implicit narrowing casts for smaller types:

```java
int x = 10;

x += 3;    // x = x + 3  → 13
x -= 2;    // x = x - 2  → 11
x *= 2;    // x = x * 2  → 22
x /= 4;    // x = x / 4  → 5  (integer division)
x %= 3;    // x = x % 3  → 2

// Useful: avoids repeating the variable name
score += bonus;
retries--;       // equivalent to retries -= 1
```

**Implicit cast in compound assignment:**
```java
byte b = 10;
b += 5;         // ✅ valid — compiler inserts implicit cast: b = (byte)(b + 5)

byte c = 10;
c = c + 5;      // ❌ compile error — 'c + 5' promotes to int, can't assign to byte without cast
```

---

### String Concatenation with `+`

When `+` involves a `String`, it performs **string concatenation**, not addition. This causes a classic trap:

```java
String s = "Result: " + 3 + 4;   // "Result: 34"  — NOT "Result: 7"!
// String + int → String ("Result: 3"), then String + int → String ("Result: 34")

String t = "Result: " + (3 + 4); // "Result: 7"   — parentheses force arithmetic first
```

The `+` operator is evaluated **left-to-right**. Once a `String` is encountered, subsequent `+` operations become concatenation:

```java
System.out.println(1 + 2 + " apples");     // "3 apples"   — 1+2=3 first (both ints)
System.out.println("apples: " + 1 + 2);   // "apples: 12" — String first, then concat
System.out.println("apples: " + (1 + 2)); // "apples: 3"  — parentheses fix it
```

---

### Type Promotion and Casting

Java automatically **promotes** smaller types in arithmetic expressions:

| Expression | Result Type |
|-----------|------------|
| `byte + byte` | `int` |
| `int + long` | `long` |
| `int + float` | `float` |
| `int + double` | `double` |
| `long + double` | `double` |

**Widening** (small → large) is automatic. **Narrowing** (large → small) requires an **explicit cast** and may lose data:

```java
// Widening — automatic, no data loss
int i = 42;
long l = i;       // ✅ int → long
double d = i;     // ✅ int → double

// Narrowing — explicit cast required, may lose precision
double pi = 3.14159;
int truncated = (int) pi;      // 3   — decimal part dropped
long big = 10_000_000_000L;
int small = (int) big;         // unpredictable — value overflows int range!

float f = 3.14f;
int fromFloat = (int) f;       // 3
```

---

### Operator Precedence

Java evaluates expressions using a fixed precedence hierarchy. Higher in the list = evaluated first:

| Priority | Operators | Notes |
|----------|-----------|-------|
| Highest | `()`, `[]`, `.` | Grouping, array access, member access |
| | `++`, `--` (prefix), `!`, `~`, `+x`, `-x` | Unary |
| | `*`, `/`, `%` | Multiplicative |
| | `+`, `-` | Additive |
| | `<<`, `>>`, `>>>` | Bit shift |
| | `<`, `>`, `<=`, `>=`, `instanceof` | Relational |
| | `==`, `!=` | Equality |
| | `&` | Bitwise AND |
| | `^` | Bitwise XOR |
| | `\|` | Bitwise OR |
| | `&&` | Logical AND (short-circuit) |
| | `\|\|` | Logical OR (short-circuit) |
| | `? :` | Ternary |
| Lowest | `=`, `+=`, `-=`, `*=`, `/=`, `%=` | Assignment |

**Practical rule: when in doubt, add parentheses.** They cost nothing at runtime and prevent subtle bugs.

```java
// Confusing — relies on precedence
int result = 2 + 3 * 4 - 1;   // 13 (not 19!)

// Clear — explicit grouping
int result = 2 + (3 * 4) - 1;  // 13 — intent is obvious
```

---

### The `Math` Class — Common Mathematical Functions

`java.lang.Math` provides static methods for common mathematical operations:

```java
Math.abs(-42)          // 42    — absolute value
Math.abs(-3.14)        // 3.14

Math.max(10, 20)       // 20   — maximum of two values
Math.min(10, 20)       // 10   — minimum of two values

Math.pow(2, 10)        // 1024.0  — 2 to the power of 10
Math.sqrt(144.0)       // 12.0    — square root

Math.floor(3.9)        // 3.0   — round down
Math.ceil(3.1)         // 4.0   — round up
Math.round(3.5)        // 4L    — round to nearest (returns long)

Math.PI                // 3.141592653589793
Math.E                 // 2.718281828459045

// Useful in test assertions
double tolerance = 0.001;
double actual = Math.sqrt(2.0);        // 1.4142135...
double expected = 1.4142135623730951;
assert Math.abs(actual - expected) < tolerance; // floating-point comparison with tolerance
```

---

### Short-Circuit Logical Operators

`&&` (AND) and `||` (OR) are **short-circuit** — they stop evaluating as soon as the result is determined:

```java
// && stops at first false
if (list != null && list.size() > 0) {    // If list is null, .size() is never called
    // safe to access list
}

// || stops at first true
if (user == null || user.isGuest()) {     // If user is null, isGuest() is never called
    // redirect to login
}

// Non-short-circuit versions: & and | (always evaluate both sides — rarely used)
if (a() & b()) { }   // b() always called even if a() is false
```

---

## Code Example

```java
public class MathOps {
    public static void main(String[] args) {
        // Integer division trap
        int total = 7;
        int count = 2;
        System.out.println("Int division:  " + total / count);           // 3
        System.out.println("Double result: " + (double) total / count);  // 3.5

        // Modulus
        for (int i = 0; i <= 10; i++) {
            if (i % 2 == 0) System.out.print(i + " ");  // 0 2 4 6 8 10
        }
        System.out.println();

        // String + trap
        System.out.println("Sum: " + 3 + 4);     // "Sum: 34" — NOT "Sum: 7"
        System.out.println("Sum: " + (3 + 4));   // "Sum: 7"  — correct

        // Math class
        double hypotenuse = Math.sqrt(Math.pow(3, 2) + Math.pow(4, 2));
        System.out.printf("Hypotenuse: %.1f%n", hypotenuse);  // 5.0
    }
}
```

---

## Summary

- **`/`** on integers **truncates**; make at least one operand a `double` or use a cast for fractional results.
- **`%`** is remainder; useful for even/odd checks, cyclic wrap-around, and nth-item logic.
- **`++`/`--`** have pre/post forms — use standalone in loops, avoid embedding in expressions.
- **Compound operators** (`+=`, `*=`, etc.) are shorthand with an implicit narrowing cast.
- **`"string" + int`** is concatenation, not addition — use parentheses when you want arithmetic first.
- **Widening** promotions are automatic; **narrowing** requires explicit cast and may lose data.
- Use the **`Math`** class for `abs`, `max`, `min`, `pow`, `sqrt`, `floor`, `ceil`, `round`.

---

## Additional Resources

- [Java Operators Tutorial (Oracle)](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/op1.html)
- [JLS: Operators](https://docs.oracle.com/javase/specs/jls/se21/html/jls-15.html)
- [java.lang.Math javadoc](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Math.html)
