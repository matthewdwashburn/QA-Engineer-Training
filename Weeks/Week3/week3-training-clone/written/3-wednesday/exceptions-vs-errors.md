# Exceptions vs Errors (`Throwable` Hierarchy)

## Learning Objectives

- Navigate `Throwable` → `Error` vs `Exception`.
- Explain when **Errors** are typically unrecoverable and should not be caught casually.
- Contrast **checked** exceptions (preview: full detail in the next lesson).

## Why This Matters

Robust applications distinguish **expected failure modes** (I/O, bad input) from **serious JVM problems** (out of memory). Misusing `Error` or catching `Throwable` hides bugs and breaks diagnostics—foundation for Wednesday’s exception-handling block.

## The Concept

### Definitions (quick and precise)

- **Exception**: a condition your program can *sometimes* anticipate or handle (bad input, missing file, failed network call, business rule failure).
- **Error**: a serious problem the application typically can’t recover from safely (JVM/resource failures, class loading/linkage issues).

Both are **throwables**: they interrupt normal control flow and can be propagated up the call stack.

### Hierarchy

- **`Throwable`:** root of all errors and exceptions.
- **`Error`:** serious problems applications usually **should not** catch—`OutOfMemoryError`, `StackOverflowError`, `LinkageError`, etc. Often **non-recoverable** at the application level.
- **`Exception`:** conditions applications **might** handle—broken I/O, parsing failures, business rule violations.

### `RuntimeException` and subclasses

**Unchecked** exceptions—compiler does not force handling. Often indicate **programming bugs** (`NullPointerException`, `IllegalArgumentException`) or optional recovery.

### Other `Exception` subclasses

Often **checked**—compiler forces `try`/`catch` or `throws` (next reading).

### Don’t catch too broadly (why `catch (Throwable)` is dangerous)

`catch (Throwable t)` catches *everything*, including `Error`. This can:

- hide fatal JVM problems and keep the process limping in an unsafe state
- break observability (the original failure gets swallowed or wrapped without context)
- interfere with interruption/cancellation flows in some systems

Prefer catching the **smallest set** you can genuinely handle.

### Practice

- Catch **specific** exception types you can handle.
- Avoid `catch (Throwable t)` in business code—it swallows `Error`.
- Let **unexpected** errors propagate or map to a generic failure response at a boundary.

### What about `InterruptedException`? (important special case)

`InterruptedException` is checked and signals that a thread was asked to stop waiting. Best practice is:

- either **propagate** it (`throws InterruptedException`)
- or **restore** the interrupt flag after catching:

```java
try {
    Thread.sleep(1000);
} catch (InterruptedException e) {
    Thread.currentThread().interrupt(); // restore
    return; // or rethrow/wrap in a way your codebase expects
}
```

## Code Example

```java
try {
    risky();
} catch (IOException e) {
    // expected, recoverable
} // do not catch OutOfMemoryError here in normal apps
```

## Summary

- `Error` ≈ JVM/platform serious failures; rarely catch.
- `Exception` ≈ application-level conditions; many are checked vs unchecked.
- Design APIs with meaningful exception types, not broad `Throwable`.

## Additional Resources

- [What Is an Exception?](https://docs.oracle.com/javase/tutorial/essential/exceptions/definition.html)
- [Exceptions — The Java™ Tutorials](https://docs.oracle.com/javase/tutorial/essential/exceptions/index.html)
