# Wrapper Classes, Boxing, and Parsing

## Learning Objectives

- Use wrapper types (`Integer`, `Double`, `Boolean`, …) alongside primitives.
- Explain **autoboxing** and **unboxing** and their pitfalls.
- Parse strings into numeric primitives with `parse*` methods.

## Why This Matters

Collections and generics require **reference** types—`List<int>` is invalid; you use `List<Integer>`. APIs (JDBC, JSON, configuration) often deliver strings or objects; wrappers bridge primitives and object-oriented APIs.

## The Concept

### Wrappers

Each primitive has a wrapper in `java.lang`: `Byte`, `Short`, `Integer`, `Long`, `Float`, `Double`, `Character`, `Boolean`.

Wrappers matter because they:

- can be used in **generics** (collections, optional types)
- can be **null** (represent “missing” values)
- have utility methods (parsing, comparisons, constants like `Integer.MAX_VALUE`)

### Autoboxing / unboxing

**Autoboxing** is the **automatic** conversion from a **primitive** type to its matching **wrapper** type. The compiler rewrites your code so the primitive is wrapped in an object (for example, assigning an `int` literal to an `Integer` variable or passing an `int` where an `Integer` is expected).

**Unboxing** is the **automatic** conversion from a **wrapper** instance to its **primitive** value. The compiler inserts a call like `intValue()` so you can use an `Integer` where an `int` is needed.

Together they let you mix primitives and wrappers without writing `Integer.valueOf(...)` or `.intValue()` by hand (though that is what happens under the hood).

```java
Integer boxed = 42;        // autobox: int 42 → Integer
int n = boxed;             // unbox: Integer → int
```

**Pitfalls:** `NullPointerException` if unboxing a **null** `Integer`. **Identity:** `Integer` caching for small values (`-128` to `127`) affects `==` comparisons—**always** use `equals` for value comparison of wrappers.

```java
Integer a = 100;
Integer b = 100;
System.out.println(a == b);      // true (cached range)

Integer x = 1000;
Integer y = 1000;
System.out.println(x == y);      // false (different objects)
System.out.println(x.equals(y)); // true
```

Also watch performance: heavy autoboxing in tight loops can create many temporary objects. Prefer primitives (`int`, `long`) in compute-heavy code.

### Parsing

```java
int x = Integer.parseInt("10");
double d = Double.parseDouble("3.14");
boolean b = Boolean.parseBoolean("true");
```

`valueOf` returns **boxed** instances; `parseX` returns primitives.

#### `parseX` vs `valueOf` (and exceptions)

- `parseInt("10")` → `int` (primitive)
- `Integer.valueOf("10")` → `Integer` (wrapper)

Both can throw `NumberFormatException` for invalid numeric text:

```java
try {
    int v = Integer.parseInt("not a number");
} catch (NumberFormatException e) {
    // handle invalid input
}
```

## Code Example

```java
List<Integer> scores = new ArrayList<>();
scores.add(95); // autoboxed
int first = scores.get(0); // unboxed
```

## Summary

- Wrappers represent primitives as objects for collections and APIs.
- **Autoboxing** converts primitives to wrappers; **unboxing** converts wrappers to primitives—both are compiler-inserted. Watch **null** on unbox and use **`equals`** for values.
- Use `parse*` / `valueOf` for string conversion.

## Additional Resources

- [Autoboxing and Unboxing](https://docs.oracle.com/javase/tutorial/java/data/autoboxing.html)
- [Integer (Java SE API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Integer.html)
