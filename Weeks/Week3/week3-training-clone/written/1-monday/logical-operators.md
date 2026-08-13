# Logical Operators in Java

## Learning Objectives

- Use `&&`, `||`, and `!` correctly in boolean expressions.
- Explain short-circuit evaluation and why it matters.
- Read and build simple truth tables for logical combinations.

## Why This Matters

Logical operators are how programs make compound decisions: “valid user **and** correct password,” “retry **or** abort,” “**not** expired.” They appear in validation, security checks, and control flow throughout the week’s epic—clean boolean logic keeps your OOP designs readable and avoids subtle bugs (especially around nulls and side effects).

## The Concept

Java provides three main **logical operators** that work on `boolean` values (or expressions that evaluate to `boolean`):

| Operator | Name        | Example   | Meaning                          |
|----------|-------------|-----------|----------------------------------|
| `&&`     | Conditional AND | `a && b` | True only if **both** are true |
| <code>&#124;&#124;</code> | Conditional OR  | <code>a &#124;&#124; b</code> | True if **either** is true (or both) |
| `!`      | NOT         | `!a`      | True if `a` is false           |

**Short-circuit evaluation**

- For `a && b`, if `a` is `false`, `b` is **not** evaluated—there is no way the whole expression can be true.
- For `a || b`, if `a` is `true`, `b` is **not** evaluated.

This saves work and is essential when the second operand could throw an exception or perform an expensive call only when needed:

```java
if (list != null && !list.isEmpty()) { ... }
```

If `list` were `null`, `list.isEmpty()` would not run, so you avoid a `NullPointerException`.

**Truth tables** (two variables `p` and `q`):

| p     | q     | p AND q | p OR q | NOT p |
|-------|-------|---------|--------|-------|
| false | false | false   | false  | true  |
| false | true  | false   | true   | true  |
| true  | false | false   | true   | false |
| true  | true  | true    | true   | false |

**Bitwise vs logical (preview):** `&` and `|` also exist for integers (bitwise) and, when applied to booleans, evaluate **both** sides without short-circuiting. For control flow and typical boolean logic, prefer `&&` and `||`.

## Code Example

```java
public class LogicalDemo {
    public static void main(String[] args) {
        int age = 20;
        boolean hasId = true;

        // AND: both must be true
        boolean canEnter = age >= 18 && hasId;
        System.out.println(canEnter); // true

        // OR: at least one true
        boolean weekend = false;
        boolean holiday = true;
        System.out.println(weekend || holiday); // true

        // NOT
        System.out.println(!canEnter); // false

        // Short-circuit: safe null check
        String name = null;
        if (name != null && name.length() > 0) {
            System.out.println(name);
        } else {
            System.out.println("No name");
        }
    }
}
```

## Summary

- `&&`, `||`, and `!` combine or invert boolean conditions.
- `&&` and `||` short-circuit; use them for safe, efficient compound conditions.
- Truth tables describe all combinations of true/false for two (or more) inputs.

## Additional Resources

- [Oracle Java Tutorial — Operators](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/operators.html)
- [Java Language Specification — Conditional Operators](https://docs.oracle.com/javase/specs/jls/se21/html/jls15.html#jls-15.24)
