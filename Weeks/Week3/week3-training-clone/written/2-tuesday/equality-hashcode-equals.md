# `equals` and `hashCode` Contract

## Learning Objectives

- State the **`equals`/`hashCode` contract** and why collections depend on it.
- Distinguish **identity** (`==` on references) from **logical equality** (`equals`).
- Implement both methods consistently using fields that define equality.

## Why This Matters

`HashMap`, `HashSet`, and `HashMap`-backed caches use **`hashCode`** to bucket entries and **`equals`** to resolve collisions. Broken contracts cause “impossible” bugs: lost keys, duplicates, flaky tests. This is a top interview topic and production pitfall.

## The Concept

### Identity vs logical equality (definition)

- **Identity**: two references point to the *same object* in memory.
  - For references, `==` checks identity.
- **Logical equality**: two *different objects* represent the same *value* (same business meaning).
  - `equals(...)` is how value equality is expressed in Java.

Default behavior reminder:
- If you do nothing, `Object.equals` behaves like `==` (identity equality).
- Many JDK classes override `equals` to be value-based (e.g., `String`, wrapper types).

```java
String a = new String("hi");
String b = new String("hi");
System.out.println(a == b);       // false (different objects)
System.out.println(a.equals(b));  // true  (same value)
```

### `equals(Object o)` contract (refined rules)

For non-null `x`, `y`, `z`:

- **Reflexive:** `x.equals(x)` is true.
- **Symmetric:** `x.equals(y)` iff `y.equals(x)`.
- **Transitive:** if `x.equals(y)` and `y.equals(z)` then `x.equals(z)`.
- **Consistent:** repeated calls return same result if no mutation.
- For any non-null `x`, `x.equals(null)` is false.

Practical interpretation for QA/debugging:
- If symmetry/transitivity fails, you’ll see weird behavior when sorting, deduplicating, or comparing across different layers.
- Consistency is often broken by using **mutable fields** in equality (or by time-based/random fields).

### `hashCode` contract

- Equal objects (by `equals`) **must** have **equal** `hashCode`.
- Unequal objects **may** share a hash (collision)—but good distribution matters for performance.

**If you override `equals`, override `hashCode`.**

### Implementation tips

- Use same **significant fields** in both methods.
- Prefer **`Objects.equals` / `Objects.hash`** for null-safe, consistent code.
- Do not use **mutable** fields in `hashCode` if the object can change while in a `HashSet`/`HashMap` key—corrupts the collection.

### How hash-based collections use them (mental model)

When you do `set.contains(x)` or `map.get(key)`, the collection roughly does:

1. Compute `hash = key.hashCode()`
2. Jump to a bucket based on that hash
3. Compare candidates in that bucket using `equals`

So:
- **Wrong `hashCode`** ⇒ collection looks in the wrong bucket.
- **Wrong `equals`** ⇒ collection can’t find the matching entry even if it’s in the bucket.

### The classic bug: “lost key” after mutation

```java
import java.util.*;

class User {
    String email; // mutable
    User(String email) { this.email = email; }

    @Override public boolean equals(Object o) {
        if (!(o instanceof User u)) return false;
        return Objects.equals(email, u.email);
    }
    @Override public int hashCode() { return Objects.hash(email); }
}

Set<User> users = new HashSet<>();
User u = new User("a@example.com");
users.add(u);

u.email = "b@example.com";        // ❌ changes hashCode while in HashSet
System.out.println(users.contains(u)); // often false (corrupted membership)
```

Fix: make key fields immutable (e.g., `final`) or never mutate while used as a key.

## Code Example

```java
import java.util.Objects;

public final class Point {
    private final int x, y;

    public Point(int x, int y) { this.x = x; this.y = y; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point p)) return false;
        return x == p.x && y == p.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
```

### Checklist: correct `equals` in real code

- Method signature is exactly `public boolean equals(Object o)` (use `@Override`).
- Handles `this == o` fast path.
- Handles `null` and wrong type safely.
- Compares the same significant fields used in `hashCode`.
- Avoids using mutable fields (or documents immutability requirement).

## Summary

- `equals` defines logical equality; `hashCode` must align for hash-based collections.
- `==` tests reference identity, not business equality.
- Use `Objects.hash`/`Objects.equals`; avoid mutable keys in hash collections.

## Additional Resources

- [Object.equals](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Object.html#equals(java.lang.Object))
- [Effective Java — equals/hashCode (summary articles)](https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html)
