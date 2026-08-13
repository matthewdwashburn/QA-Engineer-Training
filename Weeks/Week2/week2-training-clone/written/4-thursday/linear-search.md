# Linear Search

## Learning Objectives

- Implement **linear search** (sequential scan) on arrays or lists and return sensible results for **found** vs **not found**.
- State **time** **O(n)** and **extra space** **O(1)**, and describe **best / worst / average** behavior in plain language.
- Adapt the pattern for **first** vs **last** occurrence, **existence checks**, and simple **equality** rules (including reference vs value types).
- Choose when linear search is **appropriate**—and when sorting plus **binary search** or a **hash-based** lookup is worth the tradeoff (high level only).
- Relate the pattern to **QA**: predictable behavior on unsorted data, boundary tests, and performance expectations as **n** grows.

---

## Why This Matters

> **Weekly Epic Connection:** Thursday pairs **linear search** and **binary search**. Linear search is the **baseline**: it works on **any** orderable or comparable sequence without preprocessing. It is the algorithm you compare against when asking whether a fancier approach is worth the complexity.

In real systems, much data arrives **unsorted**, or is **small enough** that a single scan is cheaper than building an index. Linear search also models how you reason about **streaming** or **single-pass** logic: “look at each item once until done.” For **QA**, knowing **O(n)** helps you design cases with the **target at the start**, **middle**, **end**, and **absent**—and to spot features that accidentally scan a collection **inside a loop**, turning **O(n)** into **O(n²)**.

---

## The Concept

### Idea in one sentence

Visit elements **from first to last** (or a defined order). If the current element **matches** the search target, **stop and report success**; if you reach the end without a match, **report failure**.

**Analogy — finding a name on a paper guest list:** You read from top to bottom. You might find the name on the first line (**best case**), the last line (**worst case**), or somewhere in the middle (**average**). You do not assume the list is alphabetized unless someone sorted it for you.

### Algorithm (step by step)

1. Let **i = 0** (or start index).
2. While **i** is within bounds:
   - If **element at i** equals the **target** (by your equality rule), return **i** (or `true`, depending on API).
   - Otherwise increment **i**.
3. If the loop ends without a match, return **“not found”** (e.g. **-1** for index, or `false` for existence).

**Invariants:** After each iteration, the target is **not** among indices **0 .. i−1** (for “find any” semantics). This stays true until you return early on a match.

### Complexity

| Aspect | Linear search |
|--------|----------------|
| **Time (worst)** | **O(n)** — target absent or at last position; every element checked once. |
| **Time (best)** | **O(1)** — target at index 0 (first comparison succeeds). |
| **Time (average)** | **O(n)** for a uniform random position or absent target; still linear in **n** in the usual Big-O sense for worst-focused analysis. |
| **Extra space** | **O(1)** — only a few variables (index, maybe length), not counting the input array/list. |

The **standard** statement “linear search is **O(n)**” refers to **worst-case** time unless a problem explicitly asks for average case.

### Conventions: “not found”

Teams differ slightly; **be consistent** in one codebase.

- **Index API:** return **-1** (or optional / sentinel) when not found—common in Java-style and many exercises.
- **Boolean API:** return **`false`** for absent, **`true`** for present—readable for “does this exist?”
- **Pythonic:** may return **`None`** or raise `ValueError` in `list.index`; for learning, an explicit function with **-1** or **`None`** is clearest.

Document your choice so **callers** and **tests** agree.

---

## Code Examples

### Java: index of first match, -1 if missing

```java
public static int indexOf(int[] arr, int target) {
    for (int i = 0; i < arr.length; i++) {
        if (arr[i] == target) {
            return i;
        }
    }
    return -1;
}
```

### Python: same behavior

```python
def index_of(items: list[int], target: int) -> int:
    for i, x in enumerate(items):
        if x == target:
            return i
    return -1
```

### Existence check (no index needed)

```java
public static boolean contains(int[] arr, int target) {
    for (int v : arr) {
        if (v == target) {
            return true;
        }
    }
    return false;
}
```

```python
def contains(items: list[int], target: int) -> bool:
    return any(x == target for x in items)
```

Note: `in` on a Python **list** is still **O(n)**—it scans like linear search. **`set` membership** is a different data structure (average **O(1)**); use it when duplicates are unneeded and you build the set once.

### First vs last occurrence

**First** occurrence: the simple loop above returns the **smallest** index with a match.

**Last** occurrence: scan **forward** but **update** a running “last seen” index, or scan **backward** from `length - 1`.

```java
public static int lastIndexOf(int[] arr, int target) {
    for (int i = arr.length - 1; i >= 0; i--) {
        if (arr[i] == target) {
            return i;
        }
    }
    return -1;
}
```

```python
def last_index_of(items: list[int], target: int) -> int:
    for i in range(len(items) - 1, -1, -1):
        if items[i] == target:
            return i
    return -1
```

### Objects and equality (Java)

For **reference types**, use **`.equals`** (or `Objects.equals`) instead of `==` unless you intend identity comparison.

```java
public static int indexOfName(String[] names, String target) {
    for (int i = 0; i < names.length; i++) {
        if (target != null && target.equals(names[i])) {
            return i;
        }
        if (target == null && names[i] == null) {
            return i;
        }
    }
    return -1;
}
```

### Edge cases to test

- **Empty** array/list → not found.
- **Single element** — matches and does not match.
- **Target at index 0**, **middle**, **last**.
- **Duplicates** — first vs last policy matches your API.
- **`null` / `None`** in collections (if allowed by your design)—decide behavior explicitly.

---

## When linear search fits (and when it does not)

**Good fits**

- Data is **unsorted** and you **cannot** or **should not** pay upfront cost to sort or index.
- **n** is **small** or the scan happens **rarely**; simplicity and low memory beat micro-optimizations.
- **Streaming** or **iterator-only** access where you get **one pass** and no random access to middle elements.
- **Correctness-first** prototypes and tests where the obvious loop is enough.

**Often worth upgrading** (covered later in more depth)

- **Many repeated lookups** on the **same** static data: sorting once plus **binary search** (**O(log n)** per query) or a **hash map** (average **O(1)** per query) may dominate a naive **O(n)** scan per query.
- Very large **n** with **random access** to sorted data: **binary search** reduces comparisons dramatically.

You do not need every problem’s “best” structure on day one; you need to **recognize** the **O(n)** scan and **when** it becomes a bottleneck.

---

## Summary

- Linear search visits elements **in order** until a match or **end of data**.
- **Worst-case** time **O(n)**, **extra space** **O(1)**; **best case** **O(1)** when the first element matches.
- Return conventions (**index** vs **boolean**, **-1** vs **`None`**) should be **consistent** and **documented**.
- **First** vs **last** occurrence is a small loop variation; **objects** need correct **equality** rules.
- It is the **baseline** for Thursday: next, **binary search** on **sorted** data achieves **O(log n)** time by halving the search space each step.

---

## Additional Resources

- [GeeksforGeeks: Linear Search](https://www.geeksforgeeks.org/linear-search/) — worked examples and variants.
- [Oracle Java Tutorials — Arrays](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/arrays.html) — array indexing and iteration basics.
- **Next:** **Binary search** for **O(log n)** lookups when the collection is **sorted** (or sorted once as preprocessing).
