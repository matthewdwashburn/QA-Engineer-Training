# `HashSet` and `TreeSet`

## Learning Objectives

- Explain `HashSet` **hash table** behavior and average **O(1)** operations.
- Contrast `TreeSet` **red-black tree**, **sorted** iteration, **O(log n)** cost.
- Use **`NavigableSet`** capabilities on `TreeSet` (`lower`, `higher`, `subSet`, …).

## Why This Matters

Choosing between hash and tree sets trades **speed** vs **ordering**. Wrong choice breaks assumptions (need sorted unique IDs? use `TreeSet`; need fastest membership? `HashSet`).

## The Concept

### `HashSet`

- Backed by a `HashMap` internally.
- **No ordering** guarantee; **constant-time** `add`/`contains`/`remove` on average if hash function spreads well.
- Elements need sound **`equals`/`hashCode`**.

#### Practical notes for `HashSet`

- Average O(1) assumes a decent `hashCode` distribution; poor hashes degrade performance.
- One `null` element is allowed in `HashSet`.
- Iteration order can change as the set resizes; never rely on it for determinism in tests.

### `TreeSet`

- **`NavigableSet`** implementation using a **tree**; elements ordered by **natural order** (`Comparable`) or **`Comparator`** supplied at construction.
- Operations **O(log n)**; cannot hold elements that are not mutually comparable (ClassCastException risk).
- Useful for **range views**, **first/last**, **headSet/tailSet**.

#### Comparison-based uniqueness (important subtlety)

`TreeSet` uses ordering to decide uniqueness:
- if `compare(a, b) == 0`, the set treats them as duplicates and keeps only one

So your comparator should ideally be **consistent with equals**:
- if `a.equals(b)` then comparator should return 0

Otherwise you may “lose” distinct values that compare equal.

Example:

```java
record Person(String id, String name) {}

// Comparator that only compares by name: two different ids with same name collide
Set<Person> byName = new TreeSet<>(Comparator.comparing(Person::name));
byName.add(new Person("1", "Sam"));
byName.add(new Person("2", "Sam")); // treated as duplicate because compare == 0
System.out.println(byName.size());  // 1
```

Fix by comparing on enough fields: `Comparator.comparing(Person::name).thenComparing(Person::id)`.

### `LinkedHashSet` (awareness)

Hash performance + **insertion-order** iteration—middle ground.

## Code Example

```java
Set<Integer> fast = new HashSet<>();
fast.addAll(List.of(3, 1, 2));

NavigableSet<String> sorted = new TreeSet<>(List.of("b", "a", "c"));
System.out.println(sorted.first()); // a
```

More `NavigableSet` examples:

```java
NavigableSet<Integer> s = new TreeSet<>(Set.of(10, 20, 30, 40));
System.out.println(s.lower(25));    // 20
System.out.println(s.higher(30));   // 40
System.out.println(s.subSet(20, true, 40, false)); // [20, 30]
```

## Summary

- `HashSet`: fast, unordered; fix `equals`/`hashCode` on element types.
- `TreeSet`: sorted navigable set; pay log factor; provide `Comparator` when natural order insufficient.
- Pick based on whether **order** is a requirement.

## Additional Resources

- [Set Implementations](https://docs.oracle.com/javase/tutorial/collections/implementations/set.html)
- [TreeSet (Java SE API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/TreeSet.html)
