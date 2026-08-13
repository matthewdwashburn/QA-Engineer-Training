# Method Recursion

## Learning Objectives

- Explain in plain language what **recursion** is and why it needs a **base case**.
- Write recursive methods with explicit **base** and **recursive** cases.
- Reason about **stack depth** and `StackOverflowError`.
- Recognize **tail recursion** as a concept (the JVM does not optimize it like some languages).

## Why This Matters

Recursion is a way to solve a problem by solving the **same kind** of problem on a **smaller** input, over and over, until you reach a **simple case** you can answer directly. That pattern shows up in trees, divide-and-conquer, and many classic algorithms. In Java, each call uses a bit of the **call stack**—link this to Monday’s call stack lesson.

## The Concept

### Definition

**Recursion** means a **method calls itself**. You use it when the full problem looks like: “the answer is defined in terms of a **smaller** version of the same problem,” plus one or more **stopping** cases you can handle without calling again.

### The two pieces you always need

1. **Base case** — A condition where the method **returns a value (or stops) without** calling itself. Without this, calls never end.
2. **Recursive case** — The method calls **itself** with arguments that move **closer** to the base case (e.g. `n - 1` instead of `n`).

### Example: factorial

The factorial of **n** (written **n!**) is **n × (n−1) × … × 1**. We can say:

- **Base:** `0!` and `1!` are **1**.
- **Recursive:** `n! = n × (n−1)!` for **n > 1**.

```java
public static long factorial(int n) {
    if (n <= 1) {
        return 1;                      // base case — no more self-calls
    }
    return n * factorial(n - 1);       // recursive case — smaller problem: (n-1)!
}
```

**Trace for `factorial(3)`:** `3 * factorial(2)` → `3 * (2 * factorial(1))` → `3 * (2 * 1)` → **6**. Each call waits for the inner result, then finishes.

### Example: countdown (side effects only)

The idea is the same: **stop** when **n** hits zero; otherwise **print** and call again with **n − 1**.

```java
public static void countdown(int n) {
    if (n <= 0) {
        return;                        // base — done, no more calls
    }
    System.out.println(n);
    countdown(n - 1);                  // recursive — same job, smaller n
}
```

### Stack depth

Each time a method calls itself, Java adds a **stack frame** (one “layer” on the call stack) for that call. If the chain is very long (very large **n** in a linear recursion like `factorial`), you can run out of stack space and get a **`StackOverflowError`**. For **deep** linear problems, a **loop** is often safer.

### Tail recursion (short version)

A call is in **tail position** if the **recursive call is the last thing** before returning—there is no extra work (like `n * …`) after it returns. Some languages rewrite that into a loop automatically (**tail-call optimization**). **Standard Java does not** guarantee that—assume each call still costs a stack frame unless you rewrite to iteration yourself.

### When a loop is simpler

If you only need to march **step by step** (count down, walk a long list) with **no natural “smaller subproblem” tree**, a **loop** is usually easier to read and avoids deep stacks.

## Code Example

Classic **Fibonacci** recursion (good for showing **two** recursive calls—not great for large **n** because work explodes):

```java
public class RecursionDemo {
    public static int fib(int n) {
        if (n <= 1) {
            return n;                  // base
        }
        return fib(n - 1) + fib(n - 2);  // two branches — slow for big n
    }
}
```

For real programs with large **n**, use an **iterative** solution or **memoization** so you do not repeat the same work.

## Summary

- **Recursion** = a method calls itself; you need a **base case** and calls that move **toward** it.
- **`factorial`** and **`countdown`** show base + recursive cases; tracing a small **n** helps.
- Deep chains use many stack frames → possible **`StackOverflowError`**; use loops when depth is huge.
- **Tail recursion** is a style; do not rely on tail-call elimination in standard Java.

## Additional Resources

- [Algorithms — Recursion (conceptual)](https://docs.oracle.com/javase/tutorial/java/javaOO/index.html)
- [StackOverflowError](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/StackOverflowError.html)
