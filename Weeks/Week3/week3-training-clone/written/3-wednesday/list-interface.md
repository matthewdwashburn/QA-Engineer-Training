# The `List` Interface

## Learning Objectives

- Use core `List` operations: positional access, search, modification.
- Explain **ordered** list semantics and **duplicate** elements.
- Choose `List` when sequence and index matter.

## Why This Matters

`List` is the default choice for sequences: UI rows, query results, ordered pipelines. Implementations differ in performance (`ArrayList` vs `LinkedList`—next reading)—the interface stays the stable API.

## The Concept

### What a `List` is (definition)

A **`List<E>`** is an ordered sequence of elements with **positional (index-based)** access.

- “Ordered” means elements have a defined position from `0` to `size()-1`.
- “Allows duplicates” means the same value can appear multiple times at different indices.

### Contract

- **Ordered:** iteration follows **insertion order** (for standard implementations).
- **Allows duplicates** and multiple `null` (unless implementation forbids—rare).
- **Positional** `get(i)`, `set(i, e)`, `add(index, e)`, `remove(index)`.

Indexes are **0-based**:

```java
List<String> xs = List.of("a", "b", "c");
System.out.println(xs.get(0)); // a
System.out.println(xs.get(2)); // c
```

### Common methods

| Method | Role |
|--------|------|
| `add(e)` / `add(i, e)` | Append or insert |
| `get(i)` | Element at index |
| `set(i, e)` | Replace at index |
| `remove(i)` / `remove(Object)` | By index or first equal element |
| `indexOf` / `lastIndexOf` | Search |
| `subList(from, to)` | view backed by original list |

#### `remove(int)` vs `remove(Object)` (classic pitfall)

With `List<Integer>`, `remove(1)` removes index `1`, not the value `1`:

```java
List<Integer> nums = new ArrayList<>(List.of(10, 20, 30));
nums.remove(1);                 // removes element at index 1 (20)
nums.remove(Integer.valueOf(30)); // removes the value 30
```

#### `subList` is a view (not a copy)

`subList(from, to)` returns a **view backed by the original list**:

- modifying the sublist modifies the original
- structural modifications to the original list (outside the view) can invalidate the sublist and cause exceptions

```java
List<String> names = new ArrayList<>(List.of("a", "b", "c", "d"));
List<String> mid = names.subList(1, 3); // [b, c]
mid.set(0, "B");
System.out.println(names); // [a, B, c, d]
```

If you need an independent list, copy it: `new ArrayList<>(names.subList(...))`.

### Iteration and safe removal

- Use `removeIf(...)` for filtering.
- If you must remove during manual iteration, use the iterator’s `remove`.

```java
Iterator<String> it = names.iterator();
while (it.hasNext()) {
    if (it.next().isBlank()) it.remove(); // safe
}
```

### Thread safety

Standard lists are **not** synchronized; use `Collections.synchronizedList` or concurrent structures for shared mutable lists.

### Choosing a `List` implementation (preview)

- **`ArrayList`**: best default; fast random access; middle inserts/removes can be costly.
- **`LinkedList`**: useful mainly as a `Deque`; avoid for heavy indexed `get(i)` workloads.

## Code Example

```java
List<String> names = new ArrayList<>();
names.add("Ann");
names.add("Bob");
names.add(1, "Cam");
System.out.println(names.get(2)); // Bob shifted
names.remove("Ann");
```

## Summary

- `List` = ordered sequence with index-based access and duplicates.
- Know `get`/`set`/`add`/`remove`/`indexOf` for daily use.
- Pick implementation (`ArrayList` vs `LinkedList`) by access vs mutation pattern.

## Additional Resources

- [List Interface](https://docs.oracle.com/javase/tutorial/collections/interfaces/list.html)
- [List (Java SE API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/List.html)
