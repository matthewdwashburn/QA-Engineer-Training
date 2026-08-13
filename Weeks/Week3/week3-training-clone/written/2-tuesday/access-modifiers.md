# Access Modifiers in Java

## Learning Objectives

- Apply `public`, `protected`, default (package-private), and `private` to types and members.
- Predict visibility from any call site: same class, subclass, same package, other package.

## Why This Matters

Access control is how you enforce **encapsulation**: expose a small, stable API and hide internals. It affects API design, testing, frameworks, and library boundaries—central to the week’s OOP epic and to maintainable enterprise code.

## The Concept

Java has four access levels for members and top-level types (with restrictions on top-level classes):

| Modifier | Class | Package | Subclass (other pkg) | World |
|----------|-------|---------|----------------------|-------|
| `public` | Yes | Yes | Yes | Yes |
| `protected` | Yes | Yes | Yes | No |
| *default* | Yes | Yes | No | No |
| `private` | Yes | No | No | No |

- **Default** (no keyword): **package-private**—visible only within the same package.
- **`private`:** only inside the declaring **top-level class** (nested classes have extra rules).
- **`protected`:** package + **subclasses** (even in other packages) can access **inherited** protected members; subtle rules apply for `protected` fields vs methods across packages.
- **`public`:** visible everywhere (export of your API).

**Top-level classes/interfaces:** only `public` or package-private (no `protected`/`private`).

**Use cases**

- `private` fields with public getters/setters—hide representation, validate on write.
- `package-private` types—implementation details shared inside a module or package.
- `protected`—extension points for subclasses (use sparingly for fields).

### What each modifier is for (definition + intent)

- **`public`**: part of your external API. Once published, changing it is a breaking change for other packages/modules.
- **`private`**: internal implementation detail. Safest default for fields and helper methods.
- **Package-private (default)**: visible to “module mates” in the same package. Great for keeping APIs small while still allowing collaboration among related classes.
- **`protected`**: intended for inheritance/extensibility. It’s visible to subclasses and same-package code—so it can widen access more than you might expect.

### The subtle `protected` rule across packages (common gotcha)

In a different package, a subclass can access a `protected` member **only through the subclass type**, not through an arbitrary superclass reference.

```java
// pkg1
package pkg1;
public class Parent {
    protected int value = 10;
}

// pkg2
package pkg2;
import pkg1.Parent;

public class Child extends Parent {
    void demo(Parent p) {
        // System.out.println(p.value); // ❌ not allowed: p is a Parent reference from another package
        System.out.println(this.value); // ✅ allowed: access through Child (inherited)
        Child c = (Child) p;            // only safe if p is actually a Child
        System.out.println(c.value);    // ✅ allowed (through Child reference)
    }
}
```

This is one reason `protected` can be confusing; prefer exposing behavior via methods rather than `protected` fields.

### Choosing the “smallest” visibility (rule of thumb)

- Start with **`private`**.
- Widen to **package-private** if multiple classes in the same package need it.
- Widen to **`protected`** if subclasses must customize/extend it.
- Widen to **`public`** only when it’s intentionally part of the public API.

## Code Example

```java
// com.example.api
public class Account {
    private double balance;           // only this class

    protected String accountType;   // subclass + package

    void audit() { /* package */ } // default

    public double getBalance() { return balance; }
}
```

## Summary

- Four levels: `public`, `protected`, default, `private`.
- Default is package-private; `protected` adds subclass access outside the package.
- Prefer minimal visibility; widen only when a clear design need exists.

## Additional Resources

- [Controlling Access to Members of a Class](https://docs.oracle.com/javase/tutorial/java/javaOO/accesscontrol.html)
- [Java Language Specification — Access Control](https://docs.oracle.com/javase/specs/jls/se21/html/jls6.html#jls-6.6)
