# Checked vs Unchecked Exceptions

## Learning Objectives

- Classify exceptions as **checked** (compile-time enforced) vs **unchecked** (`RuntimeException` and `Error`).
- Use the **`throws`** clause responsibly.
- Discuss API design trade-offs (checked in public APIs).

## Why This Matters

The compiler forces you to acknowledge **checked** failures—great for reliability, noisy if overused. Understanding the split helps you design libraries and choose between `IOException` vs `UncheckedIOException` patterns.

## The Concept

### Definitions (what “checked” actually means)

- **Checked exception**: the compiler requires you to **catch** it or **declare** it with `throws`.
  - Rough rule: it’s an `Exception` that is **not** a `RuntimeException`.
- **Unchecked exception**: the compiler does **not** require catch/throws.
  - All `RuntimeException` subclasses and all `Error` subclasses are unchecked.

### Checked exceptions

Subclasses of **`Exception`** that are **not** subclasses of `RuntimeException`. Caller must **catch** or **declare `throws`**.

```java
public void copy(Path src, Path dst) throws IOException {
    Files.copy(src, dst);
}
```

### Unchecked exceptions

**`RuntimeException`** and **`Error`**—no `throws` required. Examples: `IllegalStateException`, `NullPointerException`.

### `throws` keyword

Lists checked exceptions a method may emit. Callers then handle or redeclare.

### Catch vs declare vs wrap (three common strategies)

- **Catch** when you can recover locally (retry, default, ask for new input).
- **Declare (`throws`)** when the caller is the right place to decide (propagation with context).
- **Wrap** when you need to cross an API boundary that doesn’t want checked exceptions.

Example wrapping `IOException`:

```java
try {
    Files.readString(path);
} catch (IOException e) {
    throw new UncheckedIOException("Failed to read config: " + path, e);
}
```

### Design guidance

- Use **checked** when recovery is expected and local (I/O, protocols).
- Use **unchecked** for programming errors or when forcing `throws` through many layers hurts more than helps.
- Wrapping: `new UncheckedIOException(e)` to avoid `throws` explosion (use judiciously).

### API design trade-offs (what to avoid)

- Don’t create “catch-all” checked exceptions like `throws Exception` in public APIs. It makes callers handle too broadly.
- Prefer **specific checked types** (like `IOException`) or a small, meaningful domain exception hierarchy.
- If callers can’t reasonably do anything, forcing checked handling becomes busywork—unchecked may be better.

## Code Example

```java
public int parsePositive(String s) {
    int v = Integer.parseInt(s); // NumberFormatException — unchecked
    if (v <= 0) throw new IllegalArgumentException("must be positive");
    return v;
}
```

## Summary

- Checked = declared or caught; unchecked = optional declaration.
- `throws` documents propagated checked exceptions.
- Balance API clarity with ergonomics when choosing exception types.

## Additional Resources

- [Checked vs Unchecked Exceptions](https://docs.oracle.com/javase/tutorial/essential/exceptions/runtime.html)
- [Effective Java items on exceptions (community summaries)](https://docs.oracle.com/javase/tutorial/essential/exceptions/index.html)
