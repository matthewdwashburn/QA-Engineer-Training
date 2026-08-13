# The `Map` Interface

## Learning Objectives

- Use `put`, `get`, `containsKey`, `remove`, and **`entrySet`** / **`keySet`** / **`values`** views.
- Explain **unique keys** and one **value** per key (latest `put` wins).
- Iterate maps idiomatically with `entrySet` and for-each.

## Why This Matters

Maps are everywhere: caches, indexes, configuration, JSON-like structures. Correct iteration and view behavior avoids subtle concurrent-modification bugs and unnecessary lookups.

## The Concept

### What a `Map` is (definition)

A **`Map<K, V>`** stores associations from a **unique key** to a **value**:

- Each key appears at most once.
- A key maps to exactly one value at any moment (a later `put` for the same key replaces the old value).
- A `Map` is **not** a `Collection` because it is not “a bag of elements”; it’s key → value pairs.

### Core operations

- **`put(K key, V value)`** associates; returns previous value or `null`.
- **`get(key)`** returns value or `null` if missing (watch `null` vs missing with `getOrDefault` / `containsKey`).
- **`containsKey` / `containsValue`** membership tests.
- **`remove(key)`** unmaps.

#### Missing key vs mapped-to-null (common confusion)

`get(k)` returns `null` in two different situations:
- key is missing
- key is present but mapped to `null`

Use one of these to disambiguate:

```java
map.containsKey(k);
map.getOrDefault(k, defaultValue);
```

### Views

- **`keySet()`:** `Set<K>` backed by map—changes reflect map.
- **`values()`:** `Collection<V>` (not a `Set`—duplicates possible).
- **`entrySet()`:** `Set<Map.Entry<K,V>>` for efficient key+value iteration.

Because these are **backed views**:
- removing from `keySet()` removes from the map
- removing from `entrySet()` removes from the map
- the `values()` view is also live

### Iteration

```java
for (var e : map.entrySet()) {
    System.out.println(e.getKey() + " -> " + e.getValue());
}
```

Avoid repeated `get` inside `keySet` loop when you need values—use `entrySet`.

### Useful modern helpers (Java 8+)

These reduce boilerplate and avoid error-prone “check then act” patterns:

- **`putIfAbsent`**: set a default only if missing
- **`computeIfAbsent`**: lazily create a value for a missing key
- **`merge`**: update counters/aggregates

```java
Map<String, Integer> counts = new HashMap<>();
counts.merge("apple", 1, Integer::sum); // increments, defaulting from 0

Map<String, List<String>> groups = new HashMap<>();
groups.computeIfAbsent("teamA", k -> new ArrayList<>()).add("Ann");
```

### Common pitfalls

- **Modifying while iterating**: like other collections, many maps are fail-fast; prefer iterator removal or collect keys to remove later.
- **Using `containsValue`**: often O(n); most map workloads should use keys.
- **Using mutable keys**: if a key’s fields used by `equals`/`hashCode` change after insertion in `HashMap`, you may not be able to find/remove it.

## Code Example

```java
Map<String, Integer> scores = new HashMap<>();
scores.put("Ann", 95);
scores.putIfAbsent("Ann", 0);
scores.merge("Bob", 1, Integer::sum);
```

## Summary

- Map = unique keys to values; not a `Collection` but part of the framework.
- Prefer `entrySet` for full traversals; know view backing semantics.
- Implementations (`HashMap`, `TreeMap`, …) differ in order and cost—next reading.

## Additional Resources

- [Map Interface](https://docs.oracle.com/javase/tutorial/collections/interfaces/map.html)
- [Map (Java SE API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Map.html)
