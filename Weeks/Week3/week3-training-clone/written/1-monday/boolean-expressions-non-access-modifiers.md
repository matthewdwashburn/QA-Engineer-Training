# Boolean Expressions and Non-Access Modifiers

## Learning Objectives

- Describe how Java evaluates boolean expressions and common pitfalls.
- Explain what **non-access modifiers** control versus **access modifiers** (visibility on Tuesday).
- Use `static`, `final`, `abstract`, and `synchronized` at a high level and know when each applies.

## Why This Matters

Booleans drive every `if`, `while`, and guard clause in your programs. Non-access modifiers (`static`, `final`, `abstract`, `synchronized`) shape **how** members belong to a class or object and how they behave—core to the week’s epic as you move from simple types to real class design. Access modifiers (`public`, `private`, …) are covered in depth on Tuesday; here we focus on evaluation and these four keywords.

## The Concept

### Boolean evaluation

- Conditions in `if`, `while`, `for`, and the `?:` operator must be **boolean** (or `Boolean` unboxed—wrapper details come Wednesday).
- **Relational operators** (`<`, `>`, `<=`, `>=`, `==`, `!=`) produce `boolean` results for primitives; for reference types, `==` compares **references**, not object contents (use `equals` for value equality—see `object-class.md`).
- **Logical operators** `&&`, `||`, `!` combine booleans; `&&` and `||` short-circuit (see `logical-operators.md`).

Avoid assigning “truthy/falsy” meanings like some other languages—only `boolean`/`Boolean` is valid in condition positions (except legacy quirks you should not rely on).

### Non-access modifiers

**Non-access modifiers** control things like whether a member belongs to the **class** or to **instances**, whether it can be **overridden** or **subclassed**, and how **threads** coordinate around it—they answer “how does this behave?” not “who can reference it from another class?” **Access modifiers** (`public`, `private`, `protected`, and default package visibility) control visibility and are covered in depth on Tuesday. This page introduces four common non-access keywords you will see on fields, methods, types, and blocks: `static`, `final`, `abstract`, and `synchronized`. Several can appear together (for example, `public static final` on a constant).

### `static`

- Belongs to the **class**, not to any single instance.
- **Static fields** are shared by all instances; **static methods** run without `this` and can only directly access static members (unless they receive an instance as a parameter).
- **Static blocks** run once when the class is loaded, for one-time initialization.

Use `static` for utilities, constants shared by all instances, or factory-style helpers—not as a substitute for object state when each instance needs its own data.

### `final`

- **Final variable:** assign once; cannot be reassigned (a reference stays pointing at the same object; the object’s fields may still change unless they are also final or immutable).
- **Final method:** cannot be overridden in subclasses.
- **Final class:** cannot be extended.

`final` communicates intent (“do not change this binding” or “do not subclass”) and enables compiler optimizations and clearer APIs.

### `abstract`

- **Abstract class:** cannot be instantiated; may contain abstract methods (no body) and concrete methods.
- **Abstract method:** declares signature only; concrete subclasses **must** implement it.

You will use abstract types heavily when learning encapsulation, inheritance, and polymorphism later in the week.

### `synchronized`

- On a **method** or **block**, ensures only one thread at a time executes that code for a given lock (the instance, or `ClassName.class` for static synchronized methods).

Important for concurrent code; misuse can hurt performance or cause deadlocks. Deeper concurrency is typically a separate topic—here, recognize that `synchronized` is Java’s built-in **mutual exclusion** tool for methods and blocks.

## Code Example

```java
public abstract class Config {
    public static final String APP_NAME = "MyApp"; // static + final

    static {
        System.out.println("Config class loaded");
    }

    public abstract String getEnvironment(); // subclass implements

    public static void printBanner() {
        System.out.println("=== " + APP_NAME + " ===");
    }
}

public class DevConfig extends Config {
    @Override
    public String getEnvironment() {
        return "dev";
    }
}
```

## Summary

- Boolean expressions use relational and logical operators; conditions must be boolean typed.
- **Non-access modifiers** (`static`, `final`, `abstract`, `synchronized`) shape behavior and sharing; visibility is covered with access modifiers on Tuesday.
- `static` ties members to the class; `final` prevents reassignment, overriding, or subclassing as applicable.
- `abstract` marks incomplete types or methods for subclasses to complete.
- `synchronized` coordinates access across threads for critical sections.

## Additional Resources

- [Oracle Java Tutorial — Class Variables (static)](https://docs.oracle.com/javase/tutorial/java/javaOO/classvars.html)
- [Oracle Java Tutorial — Abstract Methods and Classes](https://docs.oracle.com/javase/tutorial/java/IandI/abstract.html)
