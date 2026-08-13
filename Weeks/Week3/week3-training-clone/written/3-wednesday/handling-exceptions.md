# Handling Exceptions: `try` / `catch` / `finally`

## Learning Objectives

- Structure `try-catch-finally` and **multi-catch**.
- Use **try-with-resources** for `AutoCloseable` types.
- Describe **exception propagation** and adding `throws` to signatures.

## Why This Matters

Correct handling keeps resources closed, logs actionable context, and preserves user trust. try-with-resources prevents leaked file handles and is idiomatic modern Java—used with JDBC, streams, and custom resources.

## The Concept

### What exception handling is (definition)

Exception handling is how Java code:

- **detects** abnormal situations (`throw`)
- **transfers control** to an error path (`catch`)
- **guarantees cleanup** (`finally` / try-with-resources)

### try / catch / finally

```java
try {
    readFile();
} catch (IOException e) {
    log.error("read failed", e);
} finally {
    // always runs after try/catch (unless JVM exits)
    cleanup();
}
```

#### Good catch blocks (practical guidance)

- Catch the **most specific** type you can handle.
- Include **actionable context** in logs (ids, filenames, user input), and pass the exception as the cause.
- Avoid empty catch blocks; they hide failures and create flaky behavior.

### Multi-catch

```java
catch (IOException | SQLException e) {
    // shared handler; e is effectively final
}
```

### try-with-resources

```java
try (var in = Files.newInputStream(path)) {
    // use in
} // in closed automatically, suppresses addSuppressed if close throws
```

Requires `AutoCloseable`. Primary exception wins; close exceptions are **suppressed** and visible via `getSuppressed()`.

Example with JDBC-like resources:

```java
try (var conn = dataSource.getConnection();
     var stmt = conn.prepareStatement(sql);
     var rs = stmt.executeQuery()) {
    // use rs
}
```

### Propagation

If a method does not catch a **checked** exception, it must **`throws`** that type (or superclass). Unchecked exceptions propagate without declaration.

### `finally` pitfalls (what to avoid)

- Don’t `return` from `finally`. It can **override** a return value or **swallow** an exception:

```java
static int bad() {
    try {
        throw new RuntimeException("boom");
    } finally {
        return 1; // ❌ exception is lost
    }
}
```

Use `finally` for cleanup only.

## Code Example

```java
public List<String> loadLines(Path p) throws IOException {
    try (var lines = Files.lines(p)) {
        return lines.toList();
    }
}
```

## Summary

- Use specific catches; avoid empty catch blocks.
- Prefer try-with-resources over manual `finally` for closables.
- Declare `throws` for checked exceptions you do not handle.

## Additional Resources

- [The try-with-resources Statement](https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html)
- [The catch Blocks](https://docs.oracle.com/javase/tutorial/essential/exceptions/catch.html)
