# Java Primitive Types

## Learning Objectives
- List the eight **primitive** types, their sizes, ranges, and correct literal syntax.
- Distinguish **primitives** from **reference types** and explain how they are stored differently in memory.
- Understand **automatic type promotion** and **widening/narrowing** conversions.
- Use **wrapper classes** (`Integer`, `Double`, etc.) and understand **autoboxing/unboxing**.
- Explain **integer overflow**, **floating-point imprecision**, and when to use `BigDecimal`.
- Choose the right type for counters, flags, decimal values, characters, and large IDs.

---

## Why This Matters

> **Weekly Epic Connection:** Performance-sensitive test harnesses, financial precision, API response parsing, and integer ID fields all depend on choosing the right primitive type. **Overflow** bugs (silent wrap-around) and **floating-point imprecision** cause entire categories of subtle test failures — knowing these traps lets you write better assertions and spot data errors quickly.

---

## The Concept

### The Eight Primitive Types

Java's eight built-in primitive types are the foundation of all numeric and boolean computation:

| Type | Size | Range / Values | Default | Literal Syntax |
|------|------|---------------|---------|----------------|
| `byte` | 8 bits | -128 to 127 | `0` | `byte b = 100;` |
| `short` | 16 bits | -32,768 to 32,767 | `0` | `short s = 1000;` |
| `int` | 32 bits | -2,147,483,648 to 2,147,483,647 | `0` | `int n = 42;` |
| `long` | 64 bits | -9.2 × 10¹⁸ to 9.2 × 10¹⁸ | `0L` | `long l = 10_000_000_000L;` |
| `float` | 32 bits IEEE 754 | ~±3.4 × 10³⁸ (~6–7 decimal digits precision) | `0.0f` | `float f = 1.5f;` |
| `double` | 64 bits IEEE 754 | ~±1.8 × 10³⁰⁸ (~15–16 decimal digits precision) | `0.0` | `double d = 3.14159;` |
| `char` | 16 bits | Unicode code point U+0000 to U+FFFF | `'\u0000'` | `char c = 'A';` |
| `boolean` | ~1 bit (JVM-specific) | `true` or `false` | `false` | `boolean ok = true;` |

**Rules of thumb:**
- Use **`int`** for whole numbers unless you need larger range.
- Use **`long`** for IDs, timestamps (`System.currentTimeMillis()`), and file sizes.
- Use **`double`** for floating-point; use **`BigDecimal`** for money.
- Use **`boolean`** for flags and conditions.
- Use **`char`** rarely — prefer `String` for text; `char` is useful for single character manipulation.

---

### Literal Syntax

Java supports several literal formats for integer types:

```java
int decimal     = 255;          // Standard base-10
int hex         = 0xFF;         // Hexadecimal — same as 255
int binary      = 0b11111111;   // Binary (Java 7+) — same as 255
int octal       = 0377;         // Octal (rarely used)

// Underscores for readability in long numbers (Java 7+)
long population  = 8_000_000_000L;    // 8 billion — L suffix required for long
int million      = 1_000_000;
double precision = 3.141_592_653;

// Float suffix required — otherwise it's a double
float temp = 98.6f;            // ✅ float
float err  = 98.6;             // ❌ compile error: double cannot be converted to float
```

---

### Primitives vs Reference Types

This is a fundamental distinction in Java:

| Aspect | Primitive | Reference Type |
|--------|-----------|---------------|
| Storage | Value stored **directly** in the variable | Variable stores a **reference** (pointer) to an object on the heap |
| Memory location | Stack (usually) | Heap |
| Default value | `0`, `0.0`, `false`, `'\u0000'` | `null` |
| Nullable | ❌ Cannot be `null` | ✅ Can be `null` |
| Used in generics | ❌ Not directly | ✅ Yes |
| Used in collections | ❌ Not directly | ✅ Yes |
| Examples | `int`, `double`, `boolean`, `char` | `String`, `int[]`, `ArrayList<>`, your own classes |

```java
// Primitive — value lives in the variable itself
int x = 42;
int y = x;   // y gets a COPY of the value
y = 99;
System.out.println(x);  // 42 — x is unchanged

// Reference — variable holds an address, both point to the same object
int[] arr1 = {1, 2, 3};
int[] arr2 = arr1;       // arr2 gets a COPY of the reference — same array!
arr2[0] = 99;
System.out.println(arr1[0]);  // 99 — arr1 sees the change through arr2
```

---

### Type Promotion in Expressions

When you mix types in an arithmetic expression, Java automatically **promotes** smaller types to larger ones:

```
byte/short/char → int → long → float → double
```

```java
byte a = 10;
byte b = 20;
// byte result = a + b;   // ❌ compile error! a + b promoted to int
int result = a + b;       // ✅ — result is int

int i = 100;
long l = 200L;
long combined = i + l;    // ✅ — int promoted to long

int n = 5;
double d = 2.0;
double quotient = n / d;  // 2.5 — int promoted to double
```

**Key rule:** `byte` and `short` always promote to `int` in expressions, even with themselves. This is why you can't assign `byte + byte` back to a `byte` without a cast.

---

### Widening and Narrowing Conversions

**Widening** (small → large) is automatic — no data is lost:

```java
int  i  = 42;
long l  = i;       // ✅ widening: int → long
float f = i;       // ✅ widening: int → float (but float has limited precision for large ints!)
double d = l;      // ✅ widening: long → double
```

**Narrowing** (large → small) requires an **explicit cast** — data may be lost:

```java
double pi = 3.14159;
int truncated = (int) pi;         // 3 — decimal part silently dropped

long bigId = 10_000_000_000L;
int  small  = (int) bigId;        // -1539607552 — overflow wraps around!

double big = 1234567890.123;
float approx = (float) big;       // 1.23456794E9 — loses precision
```

> **Warning:** Narrowing casts **never throw exceptions** — they silently lose or corrupt data. Always check that the value fits in the target type before casting.

---

### Integer Overflow

Integer types **wrap around** silently when they exceed their range — no exception is thrown:

```java
int max = Integer.MAX_VALUE;  // 2,147,483,647
System.out.println(max + 1);  // -2,147,483,648 — wraps to MIN_VALUE!

int min = Integer.MIN_VALUE;  // -2,147,483,648
System.out.println(min - 1);  //  2,147,483,647 — wraps to MAX_VALUE!
```

**Useful constants on wrapper classes:**
```java
Integer.MAX_VALUE    // 2,147,483,647
Integer.MIN_VALUE    // -2,147,483,648
Long.MAX_VALUE       // 9,223,372,036,854,775,807
Double.MAX_VALUE     // 1.7976931348623157E308
Double.MIN_VALUE     // 4.9E-324 (smallest positive double)
```

**When to use `long`:** Any value that might exceed 2.1 billion — user counts for large platforms, file sizes in bytes, Unix timestamps, database row IDs.

```java
// Common overflow pitfall with timestamps
int seconds = System.currentTimeMillis() / 1000;   // ❌ overflow — millis exceeds int range!
long seconds = System.currentTimeMillis() / 1000;  // ✅ correct
```

---

### Floating-Point Imprecision

`float` and `double` use **binary** floating-point (IEEE 754). Many decimal fractions **cannot be represented exactly** in binary:

```java
System.out.println(0.1 + 0.2);           // 0.30000000000000004 — NOT 0.3!
System.out.println(1.0 - 0.9);           // 0.09999999999999998
System.out.println(0.1 + 0.2 == 0.3);   // false! Never use == for doubles
```

**Correct approach — compare with tolerance:**
```java
double a = 0.1 + 0.2;
double expected = 0.3;
double tolerance = 1e-9;   // 0.000000001

if (Math.abs(a - expected) < tolerance) {
    System.out.println("Equal (within tolerance)");
}

// In JUnit:
assertEquals(0.3, 0.1 + 0.2, 1e-9);  // third arg is delta
```

**For money and exact decimal arithmetic — use `BigDecimal`:**
```java
import java.math.BigDecimal;

BigDecimal price  = new BigDecimal("9.99");   // Use String constructor!
BigDecimal tax    = new BigDecimal("0.08");
BigDecimal total  = price.multiply(tax.add(BigDecimal.ONE));

System.out.println(total);  // 10.7892 — exact decimal arithmetic
```

> **Never use `new BigDecimal(0.1)`** — it captures the imprecise binary double. Always use `new BigDecimal("0.1")` with a String argument.

---

### `char` — Character Type

`char` represents a single Unicode character (UTF-16 code unit):

```java
char letter = 'A';
char newline = '\n';       // Escape sequences
char tab     = '\t';
char quote   = '\'';
char unicode = '\u0041';   // 'A' by Unicode code point

// char is numeric — arithmetic works on Unicode values
char next = (char)('A' + 1);  // 'B'
System.out.println((int) 'A'); // 65  — Unicode/ASCII value
System.out.println((int) 'a'); // 97

// Useful character tests
Character.isDigit('5')     // true
Character.isLetter('A')    // true
Character.isUpperCase('A') // true
Character.toLowerCase('Z') // 'z'
```

---

### Wrapper Classes and Autoboxing

Java provides a **wrapper class** for each primitive type. These are full objects that can be used in generics and collections:

| Primitive | Wrapper Class |
|-----------|--------------|
| `byte` | `Byte` |
| `short` | `Short` |
| `int` | `Integer` |
| `long` | `Long` |
| `float` | `Float` |
| `double` | `Double` |
| `char` | `Character` |
| `boolean` | `Boolean` |

**Autoboxing** — Java automatically converts primitives to wrapper objects when needed:

```java
// Autoboxing: int → Integer automatically
List<Integer> numbers = new ArrayList<>();
numbers.add(42);          // 42 is autoboxed to Integer(42)
numbers.add(99);

// Unboxing: Integer → int automatically
int first = numbers.get(0);   // Integer unboxed to int

// Manual boxing/unboxing (rarely needed)
Integer wrapped = Integer.valueOf(42);
int unwrapped = wrapped.intValue();
```

**Autoboxing pitfalls:**

```java
// Null unboxing throws NullPointerException
Integer n = null;
int x = n;   // NullPointerException! Cannot unbox null

// == compares references, not values for Integer objects
Integer a = 127;
Integer b = 127;
System.out.println(a == b);   // true  — JVM caches integers -128 to 127

Integer c = 128;
Integer d = 128;
System.out.println(c == d);   // false — different objects above cache range!
System.out.println(c.equals(d)); // true  — always use equals() for wrapper objects
```

**Useful wrapper class methods:**
```java
Integer.parseInt("42")         // String → int
Integer.toString(42)           // int → String
Integer.toBinaryString(255)    // "11111111"
Integer.toHexString(255)       // "ff"
Integer.MAX_VALUE              // 2147483647

Double.parseDouble("3.14")     // String → double
Double.isNaN(Double.NaN)       // true
Double.isInfinite(1.0 / 0.0)   // true (division by zero → Infinity, not exception!)
```

---

## Summary

- Java has **8 primitive types**: 4 integer (`byte`, `short`, `int`, `long`), 2 floating (`float`, `double`), `char`, and `boolean`.
- Primitives store **values directly**; reference types store a **reference to an object** on the heap.
- **Type promotion** converts smaller types to larger in expressions — `byte + byte` → `int`.
- **Widening** is automatic; **narrowing** requires an explicit `(type)` cast and may silently lose data.
- Integer types **overflow silently** — wrap-around with no exception. Use `long` for large counts, IDs, and timestamps.
- `double` is **not exact** — never use `==`; compare with a tolerance. Use **`BigDecimal`** for money.
- **Wrapper classes** (`Integer`, `Double`, etc.) enable primitives in collections and generics. **Autoboxing** is automatic but watch for `null` unboxing (`NullPointerException`).
- Use `Integer.parseInt()`, `Double.parseDouble()` to convert `String` input to numbers.

---

## Additional Resources

- [Primitive Data Types (Oracle Tutorial)](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html)
- [Primitive Types and Values (JLS)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-4.html#jls-4.2)
- [java.lang.Integer javadoc](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Integer.html)
- [java.math.BigDecimal javadoc](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/math/BigDecimal.html)
- [IEEE 754 Floating Point (Wikipedia)](https://en.wikipedia.org/wiki/IEEE_754) — background on why 0.1 + 0.2 ≠ 0.3
