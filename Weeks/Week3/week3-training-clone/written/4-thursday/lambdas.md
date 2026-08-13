# Lambda Expressions and Method References

## Learning Objectives

- Write lambda syntax: parameters, arrow, body (expression or block).
- Understand **target typing** and **type inference** limitations.
- Use **method references** (`::`) for static, instance, and constructor handles.
- Describe **variable capture** rules (effectively final).

## Why This Matters

Lambdas replace verbose anonymous classes for short behavior snippets—sorting, filtering, listeners. They are the standard style for collections + streams and align with functional interfaces from the previous reading.

## The Concept

### What a lambda is (definition)

A **lambda expression** is an inline way to provide an implementation of a **functional interface** (a “piece of behavior”).

Think: “pass a function as an argument,” using Java’s interface types as the container.

### Lambda form

```java
Comparator<String> byLen = (a, b) -> Integer.compare(a.length(), b.length());
```

- Parameter types optional if compiler infers from context (**target typing**).
- Single inferred parameter can omit parens: `s -> s.length()`.
- Body: expression (return implied) or `{ ...; return x; }`.

More examples of valid forms:

```java
Predicate<String> notBlank = s -> !s.isBlank();
Consumer<String> printer = (String s) -> System.out.println(s); // explicit type
Supplier<List<String>> factory = () -> new ArrayList<>();       // no params

Function<String, Integer> parse = s -> {                        // block body
    return Integer.parseInt(s.trim());
};
```

### Method references

| Kind | Example |
|------|---------|
| Static | `Integer::parseInt` |
| Bound instance | `System.out::println` |
| Unbound instance | `String::length` |
| Constructor | `ArrayList::new` |

Must match expected functional interface signature.

#### When method references work best

Use `::` when the lambda simply forwards to one method:

```java
names.removeIf(String::isBlank);     // s -> s.isBlank()
names.forEach(System.out::println);  // s -> System.out.println(s)
```

If you need extra logic, a lambda is clearer.

### Variable capture

Lambdas may read **local** and **enclosing** variables only if they are **effectively final** (assigned once, never mutated). Instance fields are fine to read/write per normal rules.

```java
int limit = 3; // effectively final
Predicate<String> longEnough = s -> s.length() >= limit;

// limit++; // ❌ would break "effectively final"
```

### Common pitfalls

- **Overloading ambiguity**: the same lambda might match multiple overloaded methods; adding an explicit type can help.
- **`this` meaning**: inside a lambda, `this` refers to the enclosing instance (unlike anonymous classes).
- **Checked exceptions**: many functional interfaces don’t allow checked exceptions; handle them inside the lambda or redesign.

## Code Example

```java
List<String> names = new ArrayList<>(List.of("Ann", "bob"));
names.removeIf(String::isBlank);
names.sort(String.CASE_INSENSITIVE_ORDER);
names.forEach(s -> System.out.println(s.toUpperCase()));
```

## Summary

- Lambdas implement functional interfaces concisely.
- Method references abbreviate lambdas that only forward to one method.
- Respect effectively final rules for locals.

## Additional Resources

- [Lambda Expressions](https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html)
- [Method References](https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html)
