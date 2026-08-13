# Binary Search

## Learning Objectives

- Explain why binary search **requires** a **sorted** sequence (or the same **total order** applied consistently).
- Implement **iterative** binary search on an array/list with **inclusive** bounds **`[lo, hi]`**, returning an index or a clear **not found** result.
- Sketch **recursive** binary search and contrast **extra space** **O(1)** (iterative) vs **O(log n)** stack (typical recursive).
- State **O(log n)** time and contrast with **linear search** **O(n)** using concrete **n** intuition.
- Name common **pitfalls** (overflow in `mid`, off-by-one, duplicates) and **test ideas** a QA mindset would cover.
- Know that libraries (**`Arrays.binarySearch`**, Python **`bisect`**) exist and when wrapping them is appropriate.

---

## Why This Matters

> **Weekly Epic Connection:** Ordered data shows up constantly: **version ranges**, **timestamps**, **sorted IDs**, **configuration tables**, and **log lines** by time. Binary search is the textbook **divide-and-conquer** pattern: cut the problem in half each step. That pattern also trains you to bisect bugs (“which half of the release or config is wrong?”) and to read tree-shaped structures later on.

For **QA**, binary search is a **behavior contract**: the implementation assumes **sorted** input. Tests that pass on small or accidentally sorted data can **fail in production** when order is wrong. Performance-wise, **O(log n)** means doubling the dataset adds **one more** comparison step in the worst case—very different from **linear search**, which may need to touch **every** element when the target is absent.

---

## The Concept

### Idea in one sentences

Maintain a **range of indices** where the target **might** still live. Each step compares the **middle** element of that range to the target and **discards half** of the range. Repeat until you find the target or the range becomes **empty**.

**Analogy — a paper phone book (sorted by name):** Open to the middle. If the name you want is alphabetically **before** that page, search the **left** half; if **after**, search the **right** half. Each decision throws away about **half** of the remaining pages. That is **O(log n)** decisions vs reading every line (**O(n)**).

### Why “sorted” is non-negotiable

Binary search only works when the **ordering rule** matches the data. If the array is **not** sorted according to that rule, “go left” or “go right” can **discard** the only place the value lives.

**Tiny counterexample:** target **3** in `[5, 1, 4, 2, 3]` — not sorted. A naive halving rule can miss **3** forever. **Precondition:** the array is sorted in **non-decreasing** order (or you use a consistent comparator that matches that order).

### Invariant (inclusive bounds)

Using **inclusive** indices **`lo`** and **`hi`**:

- At the start: if the target exists anywhere, its index is in **`[lo, hi]`**.
- Each iteration shrinks **`[lo, hi]`** while preserving that property.
- Loop **while `lo <= hi`**. When **`lo > hi`**, the range is empty → **not found**.

The **midpoint** is **`mid = lo + (hi - lo) / 2`** (integer division). This avoids **`(lo + hi) / 2`** **integer overflow** when **`lo`** and **`hi`** are large (more relevant in low-level or very large index spaces; still a good habit).

### What happens with duplicates?

The classic “return any index where **`sorted[mid] == target`**” may return the **first**, **last**, or **middle** occurrence depending on luck and ties. If product requirements need **first** or **last** occurrence, you use **tweaked** comparisons and bounds (**lower bound** / **upper bound** variants). Same **O(log n)** flavor; slightly trickier edge cases—worth separate exercises.

---

## Code Examples

### Iterative binary search (Java)

```java
public static int binarySearch(int[] sorted, int target) {
    int lo = 0;
    int hi = sorted.length - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        int v = sorted[mid];
        if (v == target) {
            return mid;
        } else if (v < target) {
            lo = mid + 1;
        } else {
            hi = mid - 1;
        }
    }
    return -1;
}
```

### Iterative binary search (Python)

```python
def binary_search(sorted_vals: list[int], target: int) -> int:
    lo, hi = 0, len(sorted_vals) - 1
    while lo <= hi:
        mid = lo + (hi - lo) // 2
        v = sorted_vals[mid]
        if v == target:
            return mid
        if v < target:
            lo = mid + 1
        else:
            hi = mid - 1
    return -1
```

### Recursive form (Java)

Same logic; each call fixes **`[lo, hi]`**. Depth is **O(log n)** in typical balanced narrowing → **O(log n)** **call stack** space.

```java
public static int binarySearchRec(int[] sorted, int target) {
    return searchRange(sorted, target, 0, sorted.length - 1);
}

private static int searchRange(int[] a, int target, int lo, int hi) {
    if (lo > hi) {
        return -1;
    }
    int mid = lo + (hi - lo) / 2;
    int v = a[mid];
    if (v == target) {
        return mid;
    }
    if (v < target) {
        return searchRange(a, target, mid + 1, hi);
    }
    return searchRange(a, target, lo, mid - 1);
}
```

### Recursive form (Python)

```python
def binary_search_rec(sorted_vals: list[int], target: int) -> int:
    def search(lo: int, hi: int) -> int:
        if lo > hi:
            return -1
        mid = lo + (hi - lo) // 2
        v = sorted_vals[mid]
        if v == target:
            return mid
        if v < target:
            return search(mid + 1, hi)
        return search(lo, mid - 1)
    return search(0, len(sorted_vals) - 1)
```

### Libraries (production awareness)

- **Java:** [`Arrays.binarySearch`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Arrays.html#binarySearch(int%5B%5D,int)) returns an index **≥ 0** if found, or a **negative insertion point** encoded value if not found—**read the Javadoc** before using the return value in tests.
- **Python:** the [`bisect`](https://docs.python.org/3/library/bisect.html) module implements binary-search-style insertion points for **sorted lists**; use it when you want **“where should this value go?”** semantics, not only **exact match**.

---

## Complexity and comparison with linear search

| | Linear search | Binary search (sorted array, random access) |
|--|---------------|---------------------------------------------|
| **Requires sorted input?** | No | Yes (for correctness) |
| **Time (worst)** | **O(n)** | **O(log n)** |
| **Extra space (iterative)** | **O(1)** | **O(1)** |
| **Extra space (recursive)** | **O(1)** (typical loop) | **O(log n)** stack (typical) |

**Intuition:** **`n = 1_000_000`** → binary search needs at most on the order of **20** comparisons in the worst case; linear search may need **1_000_000** when the item is absent.

**Preprocessing:** If data is **unsorted** and you **sort once** for **many** queries, you pay **O(n log n)** for the sort (typical comparison sort) plus **O(log n)** per query—often beats **O(n)** per query when queries are frequent.

---

## Pitfalls and test ideas

**Implementation pitfalls**

- **`mid = (lo + hi) / 2`** — prefer **`lo + (hi - lo) / 2`** (overflow-safe idiom).
- **Off-by-one:** mixing **inclusive** **`[lo, hi]`** with **`while (lo < hi)`** without adjusting updates leads to infinite loops or wrong answers—stick to one **bounds convention** and test it.
- **Empty array:** **`hi = -1`** → loop should not enter; return **not found**.
- **Single element:** both **match** and **no match** paths.
- **Duplicates:** document whether **any** index is enough or you need **first/last**.

**QA-style cases**

- Target **first**, **middle**, **last**, **absent**.
- Absent target **between** two values (insertion-point behavior if using library APIs).
- **Not sorted** input (negative test): custom implementation may return wrong index; contract should require sorted data or sort first.

---

## Summary

- Binary search **halves** the search interval each step; it needs **sorted** data under the same order used in comparisons.
- **Iterative** version uses **O(log n)** time and **O(1)** extra space; **recursive** uses **O(log n)** stack space in typical implementations.
- Use a safe **`mid`** calculation; keep **`lo` / `hi`** updates consistent with **`while (lo <= hi)`**.
- **O(log n)** vs **O(n)** linear search is the headline tradeoff for large **n** on **random-access** sorted arrays.
- Standard libraries (**`Arrays.binarySearch`**, **`bisect`**) differ in **exact return conventions**—verify docs in real code and tests.

---

## Additional Resources

- [Java `Arrays.binarySearch` (Java 21)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Arrays.html#binarySearch(int%5B%5D,int)) — official behavior and return-value encoding.
- [Python `bisect` module](https://docs.python.org/3/library/bisect.html) — insertion-point helpers on sorted sequences.
- [Binary search algorithm (Wikipedia)](https://en.wikipedia.org/wiki/Binary_search_algorithm) — variants, history, and careful boundary discussion.
