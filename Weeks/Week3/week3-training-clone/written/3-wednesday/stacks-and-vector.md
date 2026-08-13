# `Stack` and `Vector`

## Learning Objectives

- Describe `Vector` as a **synchronized** legacy `List`.
- Explain `Stack` as a `Vector` subclass with LIFO operations.
- Prefer modern **`Deque`** (`ArrayDeque`) for stack usage in new code.

## Why This Matters

You will still see `Vector` and `Stack` in older tutorials and codebases. Knowing their history avoids wrong choices (performance, design) while letting you read legacy systems—Thursday’s `Deque` ties in directly.

## The Concept

### `Vector`

- Resizable array like `ArrayList`, but methods are **`synchronized`**—thread-safe for individual calls, **not** generally safe for compound actions without external locking.
- Legacy from Java 1.0; **prefer `ArrayList`** for single-threaded or externally synchronized use.

#### What “synchronized” buys you (and what it doesn’t)

`Vector` synchronizes each method call. That means `add` and `get` are mutually exclusive across threads.

But compound actions still need external coordination:

```java
// not safe without additional locking
if (!vector.isEmpty()) {
    vector.remove(0);
}
```

Modern alternatives:
- `Collections.synchronizedList(new ArrayList<>())` for a synchronized wrapper
- `CopyOnWriteArrayList` for mostly-read, rarely-write workloads
- other concurrent structures depending on use case

### `Stack`

- Extends `Vector`; adds **`push`**, **`pop`**, **`peek`**, **`empty`**, **`search`** (1-based position—quirky).
- **LIFO** stack discipline.
- **Design smell:** subclassing `Vector` exposes full list API on a “stack,” breaking abstraction.

### Modern replacement

Use **`Deque<String> stack = new ArrayDeque<>();`** with `push`/`pop`/`peek` (same names on `Deque`). Faster, cleaner, recommended in official docs.

Why `ArrayDeque` is preferred:
- no legacy synchronization overhead
- better locality than a node-based structure
- a cleaner abstraction (you don’t accidentally treat your stack like a random-access list)

## Code Example

```java
Deque<Integer> stack = new ArrayDeque<>();
stack.push(1);
stack.push(2);
while (!stack.isEmpty()) {
    System.out.println(stack.pop());
}
```

Output is:
```
2
1
```

## Summary

- `Vector` = synchronized dynamic array; largely superseded by `ArrayList`.
- `Stack` extends `Vector`; prefer `Deque`/`ArrayDeque` for stacks.
- Read legacy code with this context; write new code with modern APIs.

## Additional Resources

- [Deque (Java SE API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Deque.html)
- [Stack (legacy) — Java SE API](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Stack.html)
