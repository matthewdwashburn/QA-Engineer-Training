# Conditional Statements — Advanced Patterns

## Learning Objectives
- Flatten deeply **nested `if` statements** using **guard clauses** for improved readability.
- Use **`switch` expressions** (Java 14+) with **`->`** arrow syntax and understand why they eliminate fall-through.
- Use **`yield`** to return values from multi-statement `switch` expression arms.
- Apply **pattern matching for `instanceof`** (Java 16+) for cleaner type checks.
- Apply **pattern matching for `switch`** (Java 21) for type-dispatching.
- Know which features require specific **JDK versions** and how to check your project's version.

---

## Why This Matters

> **Weekly Epic Connection:** Modern Java test code and production APIs use **`switch` expressions** for their safety (no accidental fall-through) and readability. Pattern matching reduces brittle `instanceof` + cast sequences — a common source of `ClassCastException`. CI JDK version determines what syntax is available — mismatches cause compile failures in pipelines.

---

## The Concept

### Nested `if` — The Nesting Problem

Deeply nested `if` statements are hard to read and easy to get wrong:

```java
// ❌ Deeply nested — hard to see the "happy path"
if (request != null) {
    if (request.getUser() != null) {
        if (request.getUser().isActive()) {
            if (request.getUser().hasPermission("edit")) {
                processEdit(request);
            } else {
                throw new ForbiddenException("No edit permission");
            }
        } else {
            throw new IllegalStateException("User is inactive");
        }
    } else {
        throw new IllegalArgumentException("Missing user");
    }
} else {
    throw new IllegalArgumentException("Missing request");
}
```

The deeper the nesting, the harder it is to track which `else` belongs to which `if`, and the main logic (`processEdit`) is buried deep.

---

### Guard Clauses — Eliminating Nesting

**Guard clauses** invert the condition and return/throw early. This keeps the "happy path" at the leftmost indentation level:

```java
// ✅ Guard clauses — early exit for error cases
if (request == null) {
    throw new IllegalArgumentException("Missing request");
}
if (request.getUser() == null) {
    throw new IllegalArgumentException("Missing user");
}
if (!request.getUser().isActive()) {
    throw new IllegalStateException("User is inactive");
}
if (!request.getUser().hasPermission("edit")) {
    throw new ForbiddenException("No edit permission");
}
// Happy path — all checks passed, full left-margin
processEdit(request);
```

**Benefits:**
- Each failure is handled at its own level, not nested inside others
- The normal execution path is clear and unindented
- New conditions are easy to add without restructuring

**Rule of thumb:** When you see `if (condition) { big block } else { throw/return }`, invert it to a guard clause.

---

### `switch` Expression (Java 14+ — Standard Feature)

The **`switch` expression** (standardised in Java 14) fixes the classic `switch` statement's fall-through problem and makes `switch` return a **value**:

```java
// switch EXPRESSION — assigns a value
String badge = switch (grade) {
    case "A", "B" -> "pass";          // Multiple labels per arm — comma-separated
    case "C"      -> "marginal";
    case "D", "F" -> "fail";
    default       -> "unknown";       // default is required if not all values are covered
};

System.out.println(badge);  // "pass" (if grade is "A" or "B")
```

Key differences from classic `switch` **statement**:

| Aspect | Classic `switch` Statement | `switch` Expression (Java 14+) |
|--------|--------------------------|-------------------------------|
| Fall-through | ❌ Yes — must use `break` | ✅ No — `->` arms don't fall through |
| Returns a value | ❌ No | ✅ Yes — can assign to a variable |
| Syntax | `case X:` + `break;` | `case X ->` |
| Multiple labels | Only by stacking `case` lines | `case X, Y, Z ->` |
| `default` required | Only if used as expression | Always required if not all cases covered |

**Using with `enum` (type-safe, exhaustive):**
```java
enum Status { PENDING, RUNNING, PASSED, FAILED }

String icon = switch (status) {
    case PENDING  -> "⏳";
    case RUNNING  -> "🔄";
    case PASSED   -> "✅";
    case FAILED   -> "❌";
    // No default needed — all enum values are covered (compiler checks exhaustiveness)
};
```

---

### `yield` — Multi-Statement `switch` Arms

When an arm needs more than one statement, use a **block** with `yield` to produce the return value:

```java
int zone = switch (errorCode) {
    case 200, 201 -> 0;          // Simple one-line arm

    case 400 -> {                // Multi-statement arm — use { } and yield
        System.out.println("Bad request — logging for audit");
        logBadRequest(errorCode);
        yield 1;                 // yield replaces return in switch expressions
    }

    case 500 -> {
        String alert = buildAlert(errorCode);
        notifyOps(alert);
        yield 2;
    }

    default -> {
        System.out.println("Unrecognised code: " + errorCode);
        yield -1;
    }
};
```

**`yield` rules:**
- Only used inside `switch` **expression** blocks — it produces the expression's value
- Cannot use `return` inside a `switch` expression arm (that would return from the enclosing method)
- Every block arm must have a reachable `yield`

---

### Pattern Matching for `instanceof` (Java 16+)

Before Java 16, `instanceof` checks required a manual cast on the next line:

```java
// ❌ Old style — redundant cast after instanceof
if (obj instanceof String) {
    String s = (String) obj;    // Redundant — we already know it's a String
    System.out.println(s.length());
}
```

Java 16+ lets you bind the result directly in the `instanceof` check:

```java
// ✅ Pattern matching for instanceof (Java 16+)
if (obj instanceof String s) {   // Declares 's' of type String in scope
    System.out.println(s.length());  // No cast needed
}

// Can include additional conditions
if (obj instanceof String s && s.length() > 5) {
    System.out.println("Long string: " + s);
}
```

This removes the most common source of `ClassCastException` — forgetting to check before casting.

---

### Pattern Matching for `switch` (Java 21 — Final Feature)

Java 21 finalised **pattern matching for `switch`**, enabling type dispatch without `instanceof` chains:

```java
// ❌ Old approach — instanceof chain
Object result = getResult();
if (result instanceof String s) {
    System.out.println("String length: " + s.length());
} else if (result instanceof Integer i) {
    System.out.println("Integer value: " + i);
} else if (result instanceof List<?> list) {
    System.out.println("List size: " + list.size());
} else {
    System.out.println("Unknown: " + result);
}

// ✅ Java 21 — pattern switch
switch (result) {
    case String s  -> System.out.println("String length: " + s.length());
    case Integer i -> System.out.println("Integer value: " + i);
    case List<?> list -> System.out.println("List size: " + list.size());
    case null      -> System.out.println("null result");
    default        -> System.out.println("Unknown: " + result);
}
```

**Guarded patterns** — add conditions per arm:
```java
String category = switch (shape) {
    case Circle c when c.radius() > 100  -> "large circle";
    case Circle c                         -> "small circle";
    case Rectangle r when r.width() == r.height() -> "square";
    case Rectangle r                      -> "rectangle";
    default                               -> "other shape";
};
```

---

### Verifying Your Java Version

Before using `switch` expressions or pattern matching, confirm your project's Java version:

**Check the installed JDK:**
```bash
java --version
javac --version
```

**Check the project's configured version in `pom.xml`:**
```xml
<properties>
    <maven.compiler.release>21</maven.compiler.release>
</properties>
```

**Feature availability summary:**

| Feature | Minimum JDK | Status |
|---------|------------|--------|
| `switch` expression with `->` | Java 14 | ✅ Standard |
| Text blocks (`"""`) | Java 15 | ✅ Standard |
| `instanceof` pattern matching | Java 16 | ✅ Standard |
| Records | Java 16 | ✅ Standard |
| `switch` with type patterns | Java 21 | ✅ Standard (was preview in 17–20) |
| Unnamed classes / instance main | Java 21 | ⚠️ Preview |

> **CI/CD alignment:** Your pipeline's JDK version must be ≥ the version you use in code. If your `pom.xml` says `<release>17</release>` but you write Java 21 pattern matching, `javac` will reject it — even if your local machine has Java 21.

---

### When to Use Which Pattern

| Scenario | Recommended Pattern |
|----------|-------------------|
| Simple true/false condition | `if`/`else` |
| Multiple conditions on one variable (discrete values) | `switch` expression (Java 14+) |
| Error cases with early exit | Guard clauses (inverted `if` + `return`/`throw`) |
| Simple inline choice | Ternary `? :` |
| Type-based dispatch | Pattern matching `switch` (Java 21) |
| Single type check + cast | `instanceof` pattern (Java 16+) |

---

## Summary

- **Guard clauses** invert error conditions for early exit, keeping the happy path unindented and readable.
- **`switch` expression** with `->` arms (Java 14+) eliminates fall-through, supports multiple labels (`case A, B ->`), and can return a value.
- **`yield`** returns a value from a multi-statement `switch` expression arm — `return` exits the method, not the switch.
- **`instanceof` pattern matching** (Java 16+) eliminates the redundant cast after a type check.
- **Pattern matching for `switch`** (Java 21) enables clean type dispatch replacing `instanceof` chains.
- Always check `maven.compiler.release` in `pom.xml` and the CI JDK version before using modern syntax features.

---

## Additional Resources

- [Switch Expressions (Oracle — Java 17)](https://docs.oracle.com/en/java/javase/17/language/switch-expressions.html)
- [JEP 361: Switch Expressions (Java 14 — Final)](https://openjdk.org/jeps/361)
- [JEP 441: Pattern Matching for switch (Java 21 — Final)](https://openjdk.org/jeps/441)
- [JEP 394: Pattern Matching for instanceof (Java 16 — Final)](https://openjdk.org/jeps/394)
- [Dev.java: Pattern Matching](https://dev.java/learn/pattern-matching/)
