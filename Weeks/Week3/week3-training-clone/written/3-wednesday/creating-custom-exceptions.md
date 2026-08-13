# Creating Custom Exceptions

## Learning Objectives

- Subclass **`Exception`** (checked) or **`RuntimeException`** (unchecked) for domain errors.
- Add constructors that accept **message**, **cause**, and context fields.
- Document when callers should handle vs fail fast.

## Why This Matters

Domain exceptions (`InsufficientFundsException`, `InvalidOrderException`) make `catch` blocks **meaningful** and logs **searchable**. They complete the exception story alongside try/catch and distinguish business rules from generic `Exception`.

## The Concept

### What a “custom exception” is (definition)

A **custom exception** is an exception type you create to represent a specific failure in your domain or subsystem. Good custom exceptions:

- communicate intent (what went wrong) without parsing message strings
- make it easy to catch *only* what you can handle
- preserve the original cause for debugging

### Choosing base type

- **`extends Exception`:** checked—forces handling in calling code (banking transfers, file contracts).
- **`extends RuntimeException`:** unchecked—validation bugs, impossible states if API contract violated.

Rule of thumb:
- If a caller can realistically **recover** (retry, ask user for different input), checked can be appropriate.
- If the failure represents a **programmer error** or a violated precondition, unchecked is usually better.

### Constructors

Provide at least:

```java
public class InsufficientFundsException extends Exception {
    public InsufficientFundsException() { super(); }
    public InsufficientFundsException(String message) { super(message); }
    public InsufficientFundsException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

Optional: extra fields (`BigDecimal shortfall`) with getters.

### Messages

Make messages **actionable** for operators: include ids, limits, and what to retry vs escalate.

### Chaining

Wrap low-level cause: `new ServiceException("withdraw failed", sqlEx)` preserves stack chains in logs.

### Common best practices

- Name exceptions by *problem* (`InvalidOrderException`), not by *action* (`OrderFailedException`).
- Prefer **immutable context fields** (e.g., `final String orderId`), exposed via getters.
- Don’t lose the cause: always pass the original exception into your wrapper.
- Avoid creating dozens of near-identical exception types; keep a small, meaningful hierarchy.

## Code Example

```java
public class InvalidAccountException extends Exception {
    private final String accountId;

    public InvalidAccountException(String accountId) {
        super("Unknown account: " + accountId);
        this.accountId = accountId;
    }

    public String getAccountId() { return accountId; }
}
```

### Example usage (how it improves calling code)

```java
try {
    service.withdraw(accountId, amount);
} catch (InvalidAccountException e) {
    // specific handling: prompt for a different account id
} catch (InsufficientFundsException e) {
    // specific handling: inform user of shortfall
}
```

## Summary

- Pick checked vs unchecked based on recoverability and API ergonomics.
- Supply message + cause constructors; add domain fields when useful.
- Use custom types instead of generic `Exception` for clarity.

## Additional Resources

- [Creating Exception Classes](https://docs.oracle.com/javase/tutorial/essential/exceptions/creating.html)
- [Exception (Java SE API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Exception.html)
