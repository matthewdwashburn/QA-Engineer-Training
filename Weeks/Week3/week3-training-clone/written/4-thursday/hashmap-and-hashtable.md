# `HashMap` vs `Hashtable`

## Learning Objectives

- Contrast **`HashMap`** (modern default) with legacy **`Hashtable`**.
- Explain **synchronization** and **null** key/value rules.
- Know when **`ConcurrentHashMap`** is the right concurrent choice (high level).

## Why This Matters

Interview classic: why not `Hashtable`? Understanding synchronization overhead and null rules prevents cargo-cult typing and thread-safety mistakes.

## The Concept

### `HashMap`

- **Not synchronized**—faster for single-threaded or externally coordinated use.
- Allows **one `null` key** and **multiple `null` values** (by contract of `HashMap`).
- Default go-to for general-purpose maps.

#### Performance intuition

With good hashing, `get`/`put`/`remove` are **average O(1)**. Worst case can degrade if many keys collide, but modern JDKs mitigate extreme collision cases.

### `Hashtable`

- **Legacy** (Java 1.0); methods **`synchronized`**—coarse-grained lock per operation.
- **Does not allow `null` key or null values**—throws `NullPointerException`.
- Largely **obsolete**; prefer `HashMap` or, for concurrency, **`ConcurrentHashMap`**.

### Thread safety

- `Hashtable` thread-safe for **single** operations, not for **check-then-act** sequences without extra locking.
- **`Collections.synchronizedMap(new HashMap<>())`** wraps with a mutex—still compound-operation pitfalls.
- **`ConcurrentHashMap`** designed for concurrent **get/put** with better scalability.

#### The check-then-act pitfall (why “synchronized map” isn’t enough)

Even if individual methods are synchronized, this is still unsafe without extra locking:

```java
// two threads can still race here
if (!map.containsKey(k)) {
    map.put(k, 1);
}
```

Prefer atomic helpers:

```java
map.putIfAbsent(k, 1);
map.computeIfAbsent(k, kk -> 1);
```

For counters under concurrency, use `ConcurrentHashMap` + `merge`, or dedicated concurrent counters.

## Code Example

```java
Map<String, String> modern = new HashMap<>();
modern.put("k", "v");

// Map<String, String> legacy = new Hashtable<>();
// legacy.put(null, "x"); // NPE
```

## Summary

- Use `HashMap` by default; understand null allowances.
- Avoid `Hashtable` in new code unless interfacing with legacy APIs.
- For shared mutable maps under concurrency, use `ConcurrentHashMap`, not `Hashtable`.

## Additional Resources

- [HashMap (Java SE API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/HashMap.html)
- [Hashtable (Java SE API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Hashtable.html)
