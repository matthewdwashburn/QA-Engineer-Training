# Functional Interfaces

## Learning Objectives

- Define **SAM** types and the `@FunctionalInterface` annotation.
- Use `Predicate`, `Function`, `Consumer`, and `Supplier` from `java.util.function`.
- Recognize functional interfaces in the JDK (`Runnable`, `Comparator`, …).

## Why This Matters

Lambdas and method references (next reading) only work where a **functional interface** is expected. Streams, optional handling, and event callbacks all use these types—finishing the week’s epic toward fluent, expressive Java.

## The Concept

### Functional interface

Exactly **one abstract method** (SAM = single abstract method). Default and static methods on interfaces do not count against SAM.

```java
@FunctionalInterface
public interface IntPredicate {
    boolean test(int value);
}
```

`@FunctionalInterface` is optional but documents intent and lets the compiler enforce single abstraction.

### What “target type” means (why SAM matters)

A lambda expression has no standalone type. It becomes a value only when the compiler knows the **target functional interface** it should implement.

```java
Predicate<String> p = s -> !s.isBlank(); // lambda targets Predicate<String>
Runnable r = () -> System.out.println("run"); // lambda targets Runnable
```

This is why “lambdas only work where a functional interface is expected.”

### Standard library functional types

| Interface | Method | Typical use |
|-----------|--------|-------------|
| **`Predicate<T>`** | `boolean test(T t)` | filter, conditions |
| **`Function<T,R>`** | `R apply(T t)` | map, transform |
| **`Consumer<T>`** | `void accept(T t)` | side effects |
| **`Supplier<T>`** | `T get()` | lazy factories, provide |

Primitives variants: `IntPredicate`, `ToIntFunction`, etc., reduce boxing.

#### Composition helpers (very common in real code)

These interfaces come with default methods for composition:

```java
Predicate<String> notBlank = s -> !s.isBlank();
Predicate<String> longEnough = s -> s.length() >= 3;
Predicate<String> ok = notBlank.and(longEnough); // combine predicates

Function<String, String> trim = String::trim;
Function<String, Integer> length = String::length;
Function<String, Integer> trimmedLength = trim.andThen(length);
```

### Existing SAM types

`Runnable`, `Callable`, `Comparator`, `ActionListener`—all accept lambdas where those types appear.

Examples you’ll see constantly:

```java
List<String> names = new ArrayList<>(List.of("Ann", "bob"));
names.sort(String.CASE_INSENSITIVE_ORDER); // Comparator<String>

Thread t = new Thread(() -> doWork());     // Runnable
t.start();
```

## Code Example

```java
Predicate<String> notBlank = s -> !s.isBlank();
Function<String, Integer> len = String::length;
Consumer<String> print = System.out::println;
Supplier<Double> rnd = Math::random;

print.accept("len=" + len.apply("hi") + " rnd=" + rnd.get());
```

### Common pitfalls

- **Accidentally adding a second abstract method**: breaks SAM. `@FunctionalInterface` helps the compiler catch it.
- **Boxing overhead**: prefer primitive specializations (`IntPredicate`, `IntFunction`, etc.) when processing lots of numbers.
- **Checked exceptions**: most `java.util.function` types do not declare checked exceptions; you often need to handle exceptions inside the lambda or write a custom functional interface.

## Summary

- Functional interface = one abstract method; enables lambda assignment.
- Know Predicate/Function/Consumer/Supplier for APIs and streams.
- `@FunctionalInterface` clarifies design and catches mistakes.

## Additional Resources

- [Functional Interfaces](https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html)
- [java.util.function package](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/function/package-summary.html)
