# Overview of the Java Collections Framework

## Learning Objectives

- Describe `Iterable` → `Collection` → `List` / `Set` / `Queue`.
- Name common operations on **`Collection`** (size, add, remove, contains, bulk updates, iteration).
- Contrast `Collection` with **`Map`** (not a `Collection`).
- Name typical implementations you will use this week (`ArrayList`, `HashSet`, `HashMap`, …).

## Why This Matters

Collections are how Java programs hold groups of objects in memory. Choosing the right interface (`List` vs `Set` vs `Map`) drives correctness (duplicates? ordering?) and performance—core to the week’s epic through Thursday.

## The Concept

### What the “Collections Framework” is (definition)

The **Java Collections Framework (JCF)** is a set of:

- **Interfaces** that define *contracts* (e.g., `List`, `Set`, `Queue`, `Map`)
- **Implementations** that provide concrete data structures (e.g., `ArrayList`, `HashSet`, `ArrayDeque`, `HashMap`)
- **Algorithms/utilities** (e.g., `Collections.sort`, `Collections.unmodifiableList`)

The key idea: you program to **interfaces** and swap implementations based on behavior/performance needs.

### `Iterable` and `Collection`

- **`Iterable<T>`:** root of “things you can traverse”; requires `Iterator<T> iterator()`. Also provides **`forEach`** (default) and **`spliterator()`** for streams-style traversal.
- **`Collection<E>`:** extends `Iterable<E>` and describes a **bag of elements** (order and duplicate rules are refined by `List`, `Set`, `Queue`, …).

#### `Iterator` basics (why it matters)

`Iterator<E>` is the low-level traversal mechanism behind enhanced `for`:

```java
Iterator<String> it = collection.iterator();
while (it.hasNext()) {
    String value = it.next();
}
```

Important: if you need to remove elements while iterating, use `Iterator.remove()` (or `removeIf`) rather than `collection.remove(...)` inside the loop.

### Methods on **`Collection<E>`**

These are the shared building blocks (see the [Collection Javadoc](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Collection.html) for full signatures and default methods):

| Role | Methods |
|------|---------|
| **Size / emptiness** | `int size()`, `boolean isEmpty()` |
| **Single-element** | `boolean add(E e)`, `boolean remove(Object o)`, `boolean contains(Object o)` |
| **Bulk vs another collection** | `boolean containsAll(Collection<?> c)`, `boolean addAll(Collection<? extends E> c)`, `boolean removeAll(Collection<?> c)`, `boolean retainAll(Collection<?> c)` — `retainAll` keeps only elements also in **c** |
| **Clear** | `void clear()` |
| **Iteration** | `Iterator<E> iterator()` (from `Iterable`); enhanced `for` uses this |
| **Array snapshots** | `Object[] toArray()`, `<T> T[] toArray(T[] a)` |
| **Equality** | `boolean equals(Object o)`, `int hashCode()` — collections compare by contents per contract |

**Java 8+** adds defaults you will use often: **`void forEach(Consumer<? super E>)`** (via `Iterable`), **`boolean removeIf(Predicate<? super E>)`**, **`Stream<E> stream()`**, **`Stream<E> parallelStream()`**, **`Spliterator<E> spliterator()`**.

Return types matter: **`add` / `remove`** return **`false`** if nothing changed (e.g. duplicate add not allowed on some sets, or remove when absent).

### Main branches

| Interface | Typical contract |
|-----------|------------------|
| **`List`** | Ordered, indexed, duplicates allowed |
| **`Set`** | No duplicate elements (by `equals`/`hashCode`) |
| **`Queue`** | Head/tail operations, often FIFO or priority-based |
| **`Deque`** | Double-ended queue |

#### Quick “which one should I reach for?”

- Choose **`List`** when you care about **order** or **index-based access**.
- Choose **`Set`** when you care about **uniqueness** and membership checks.
- Choose **`Queue/Deque`** when you care about **processing order** (FIFO/LIFO) and head/tail operations.

### `Map<K,V>`

**Not** a `Collection`—maps **keys** to **values**; keys unique. Still part of the **Collections Framework** umbrella.

Why separate? A map is conceptually not a “bag of elements” — it’s an association from **key → value**, and its primary operations are `get`, `put`, `containsKey`, etc.

### Implementations (preview)

- `ArrayList`, `LinkedList` → `List`
- `HashSet`, `LinkedHashSet`, `TreeSet` → `Set`
- `PriorityQueue`, `ArrayDeque` → `Queue`/`Deque`
- `HashMap`, `LinkedHashMap`, `TreeMap` → `Map`

Prefer **interface types** on the left: `List<String> items = new ArrayList<>();`

### Ordering, duplicates, and nulls (a useful mental checklist)

When choosing a collection, ask:

- **Ordering**: do I need insertion order, sorted order, or no order?
- **Duplicates**: do I need to allow duplicates or enforce uniqueness?
- **Nulls**: do I allow null elements/keys? (some implementations do not)

Examples:
- `ArrayList`: ordered, duplicates allowed, nulls allowed.
- `HashSet`: uniqueness enforced, no stable order guarantee, null allowed.
- `TreeSet`: sorted order, uniqueness enforced, typically disallows null (because comparison).

### “Fail-fast” iterators (common pitfall)

Many collections have **fail-fast** iterators: if you structurally modify the collection while iterating (outside the iterator), you may get `ConcurrentModificationException`.

```java
for (String s : list) {
    if (s.isBlank()) {
        // list.remove(s); // ❌ often triggers ConcurrentModificationException
    }
}

list.removeIf(String::isBlank); // ✅ safe and clear
```

## Code Example

```java
Collection<String> c = new ArrayList<>();
c.add("a");
for (String s : c) { System.out.println(s); }

Map<String, Integer> map = new HashMap<>();
map.put("k", 1);
```

## Summary

- `Collection` family handles elements; `Map` handles key-value pairs.
- **`Collection<E>`** exposes shared operations: size/empty, add/remove/contains, bulk `*All`, `clear`, `iterator`, `toArray`, `equals`/`hashCode`, plus defaults like `forEach`, `stream`, `removeIf`.
- Pick interface by ordering, duplicates, and access patterns.
- Thursday deepens `Set`, `Map`, `Queue`, and lambdas over collections.

## Additional Resources

- [Collections Framework Overview](https://docs.oracle.com/javase/tutorial/collections/interfaces/index.html)
- [Collection (Java SE API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Collection.html)
