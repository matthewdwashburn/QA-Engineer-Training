# `ArrayList` vs `LinkedList`

## Learning Objectives

- Contrast **random access** vs **node-based** linked structure.
- Describe big-O intuition for get, add-at-end, add-in-middle, remove.
- Choose between the two for typical workloads.

## Why This Matters

Picking the wrong `List` implementation can turn linear algorithms into quadratic ones. Interviews and profiling both assume you understand why `ArrayList` is the default and when `LinkedList` still helps (and when it does not).

## The Concept

### What these types really are (definition)

- **`ArrayList<E>`**: a resizable array implementation of `List`.
- **`LinkedList<E>`**: a doubly-linked node chain implementing both `List` and `Deque`.

They both satisfy the `List` contract, but their internal structure leads to different performance characteristics.

### `ArrayList`

- **Backing array**; grows by copying when full.
- **`get(i)`** and **`set(i)`** are **O(1)**.
- **Add at end** amortized **O(1)**.
- **Insert/remove in the middle** requires shifting elements—**O(n)** in the worst case.

**Default choice** for most lists.

#### Capacity growth (why appends are “amortized”)

Appending is usually fast, but when the backing array fills up, the list allocates a larger array and copies elements over. That occasional copy is why `add(e)` is **amortized O(1)** rather than strict O(1).

If you know the approximate size, pre-size to reduce reallocations:

```java
List<String> items = new ArrayList<>(10_000);
```

### `LinkedList`

- **Doubly linked nodes**; implements `List` and `Deque`.
- **`get(i)`** walks from nearest end—**O(n)**.
- **Add/remove at known node** with iterator is **O(1)**; by index still costs traversal.

Good when you frequently **add/remove at ends** or use **iterator-based** removal while traversing—not when you mostly index by `i`.

### Memory

`LinkedList` has per-node overhead; `ArrayList` is denser for plain sequences.

### Summary table (big-O intuition)

| Operation | `ArrayList` | `LinkedList` | Notes |
|----------|-------------|--------------|------|
| `get(i)` | O(1) | O(n) | LinkedList must traverse |
| `add(e)` (end) | amortized O(1) | O(1) | LinkedList needs node allocation |
| `add(0, e)` | O(n) | O(1) | LinkedList is good at ends (as Deque) |
| `remove(i)` | O(n) | O(n) | LinkedList still needs traversal to index |
| iterate | O(n) | O(n) | ArrayList often faster due to cache locality |

### A common myth

“`LinkedList` is good for inserting in the middle.”  
Only if you already have an iterator positioned at the insertion point. If you insert by index, you still pay traversal time.

## Code Example

```java
List<Integer> fastRandom = new ArrayList<>();
fastRandom.add(0, 1); // shifts — OK occasionally

Deque<Integer> ends = new LinkedList<>();
ends.addFirst(1);
ends.addLast(2);
```

## Summary

- `ArrayList`: fast indexed access, good cache locality—use by default.
- `LinkedList`: strong at middle insertion with iterators, ends as deque—avoid for heavy random indexing.
- Measure if unsure; algorithm choice often matters more than list type.

## Additional Resources

- [Implementations](https://docs.oracle.com/javase/tutorial/collections/implementations/index.html)
- [ArrayList (Java SE API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/ArrayList.html)
