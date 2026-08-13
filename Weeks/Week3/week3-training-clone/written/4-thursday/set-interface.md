# The `Set` Interface

## Learning Objectives

- State the `Set` contract: **no duplicate** elements.
- Contrast iteration order across implementations (undefined vs insertion vs sorted—preview).
- Use common operations: `add`, `remove`, `contains`, set algebra helpers.

## Why This Matters

Sets model unique collections: distinct users, de-duplication, membership tests. They underpin `HashSet`/`TreeSet` (next reading) and interact tightly with **`equals`/`hashCode`** for `HashSet`.

## The Concept

### What a `Set` is (definition)

A **`Set<E>`** is a collection that enforces **uniqueness**: it contains **no duplicates**.

In Java, “duplicate” is defined by equality:
- For most set implementations, two elements are considered the same if `a.equals(b)` is `true`.
- For hash-based sets, `hashCode` must also be consistent with `equals` (same rule you learned Tuesday).

### Contract

- A `Set` contains **at most one** element `e` such that for any existing `x`, `e.equals(x)` is false (for `null`, generally not allowed in most sets except one `null` in some implementations—check javadoc).
- **`add(e)`** returns `false` if already present.
- **`contains`** / **`remove`** rely on equality (and hash for hash-based sets).

#### Practical implications of the contract

- **De-duplication**: adding the same logical value twice won’t change the set.
- **Membership tests**: sets are ideal for “is this present?” checks.
- **Correctness depends on equality**: if element types have broken `equals`/`hashCode`, sets behave incorrectly (duplicates, “missing” elements).

### Order

- **`HashSet`:** iteration order **not** specified—do not depend on it.
- **`LinkedHashSet`:** **insertion order**.
- **`TreeSet`:** **sorted** per `Comparator` or natural order.

### Bulk operations

`Collection` methods plus `containsAll`, `retainAll`, `removeAll` for set-like algebra (also on `Collection` generally).

### Set algebra (union / intersection / difference)

Java doesn’t have dedicated `union()` methods, but the bulk operations implement the common algebra:

```java
Set<String> a = new HashSet<>(Set.of("java", "qa"));
Set<String> b = new HashSet<>(Set.of("qa", "dev"));

Set<String> union = new HashSet<>(a);
union.addAll(b);                 // union = a ∪ b

Set<String> intersection = new HashSet<>(a);
intersection.retainAll(b);       // intersection = a ∩ b

Set<String> difference = new HashSet<>(a);
difference.removeAll(b);         // difference = a − b
```

### Common pitfalls

- **Mutating set elements**: if an element’s fields used by `equals`/`hashCode` change while it’s in a `HashSet`, membership and removal can break (the “lost key” problem from Tuesday).
- **Assuming order**: `HashSet` order is not stable; use `LinkedHashSet` or `TreeSet` when order is a requirement.
- **`TreeSet` equality differs**: in sorted sets, uniqueness is defined by comparison (compare result 0) rather than `equals` alone. If a comparator is inconsistent with equals, you can see surprising “duplicates removed” behavior.

## Code Example

```java
Set<String> tags = new HashSet<>();
tags.add("java");
tags.add("java"); // duplicate ignored
System.out.println(tags.size()); // 1
```

## Summary

- `Set` = uniqueness by `equals` (and consistent `hashCode` for hash sets).
- Order depends on implementation; choose `HashSet`, `LinkedHashSet`, or `TreeSet` accordingly.
- Friday/logging and lambdas build on collections you manipulate with sets and maps.

## Additional Resources

- [Set Interface](https://docs.oracle.com/javase/tutorial/collections/interfaces/set.html)
- [Set (Java SE API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Set.html)
