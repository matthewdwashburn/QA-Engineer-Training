# Time Complexity (Big-O)

## Learning Objectives

- Explain **Big-O** as an **upper bound** on how runtime **grows** with input size **n**, not as exact wall-clock time.
- Distinguish **time complexity** from **space complexity**, and name common classes: **O(1)**, **O(log n)**, **O(n)**, **O(n log n)**, **O(n²)**.
- Read simple loops and nested structures and **estimate** complexity using the “how often does the inner work run?” rule.
- Connect asymptotic reasoning to **QA**: performance expectations, regressions, and talking to engineers about **scalability**.

---

## Why This Matters

> **Weekly Epic Connection:** Thursday pairs **linear search** and **binary search**. The headline difference between them is not “Java vs Python”—it is **O(n)** vs **O(log n)** time on a sorted collection. When you file a performance bug, design a load test, or ask “will this still work at 10× traffic?”, teams use **the same Big-O vocabulary** as algorithm courses.

In production, **constants** (CPU speed, cache, JIT warmup) absolutely matter for milliseconds. But **growth rate** answers questions like: “If we double the dataset, do we double the work, or do we only add one more step?” That is what Big-O is for: **comparing algorithms** and **spotting accidental quadratic behavior** (a classic source of outages when data gets big).

---

## The Concept

### What Big-O measures (and what it ignores)

**Input size** is usually called **n** (length of an array, number of rows, size of a collection). We ask: as **n → ∞**, how does the **number of basic steps** grow?

**Big-O** describes **worst-case** growth **up to constant factors** and **lower-order terms**:

- **O(1)** means “bounded by a constant”: the step count does not grow with **n** (still might be “50 steps” vs “5 steps”—both are O(1)).
- **O(n)** means “grows linearly with **n**”: doubling **n** roughly doubles the work.
- **O(n²)** means “grows like **n** squared”: doubling **n** can **quadruple** the work.

We **drop constants** and **drop** terms that are dominated by the largest one: **3n² + 100n + 42** is still **O(n²)** because when **n** is huge, the **n²** term wins.

**Analogy — cleaning a theater:** Counting seats one-by-one is **O(n)** in the number of seats. If you had to compare **every pair** of seats for some rule, that is **O(n²)**—there are about **n(n−1)/2** pairs. Big-O is the shorthand for “which **kind** of growth is this?”

### Worst case, average case, best case (high level)

For the **same** code, people may quote different Big-O labels:

- **Worst case** is the usual default when someone says “this sort is O(n log n)” unless they specify otherwise.
- **Average case** can be better (e.g., **quicksort** is often fast on random data but has a bad worst case without careful pivot choices).
- **Best case** is sometimes misleadingly rosy (e.g., a loop that **sometimes** exits early might be **O(1)** best case but **O(n)** worst case).

For **QA**, **worst case** often matches **adversarial** or **pathological** production data (sorted when you assumed random, duplicates everywhere, etc.). When in doubt, ask engineers which case they designed for.

### Common complexity classes (quick reference)

| Class        | Typical pattern                                      | Rough intuition (large **n**)   |
|-------------|-------------------------------------------------------|----------------------------------|
| **O(1)**    | Index array, hash map get (average), fixed math       | Same cost as **n** grows         |
| **O(log n)**| Halve the problem each step (binary search on sorted) | Extremely gentle growth          |
| **O(n)**    | Single pass, scan all **n** items                     | Grows in proportion to **n**     |
| **O(n log n)** | Divide-and-conquer sorts, many efficient algorithms | Common “good” cost for sorting   |
| **O(n²)**   | Nested loops both up to **n**, all-pairs comparisons  | Gets expensive fast              |

**Logarithms:** In CS you almost always see **log₂** when we “halve” the problem. **O(log n)** and **O(log₂ n)** are the **same class** in Big-O notation because changing the log base is only a **constant factor**.

### Logarithmic growth: why binary search is “cheap”

If each step **cuts the remaining work in half**, the number of steps is about **log₂(n)**:

- **n = 16** → at most about **4** halvings.
- **n = 1,000,000** → at most about **20** halvings.

So **O(log n)** grows **much** more slowly than **O(n)**. That is the heart of why **binary search** (on **sorted** data) can feel “instant” on large arrays while a naive **scan** still has to look at many elements in the worst case.

### Space complexity (memory)

**Space complexity** counts **extra** memory used by the algorithm (definitions vary slightly by textbook; in interviews, say what you count).

- **O(1)** extra space: only a handful of variables (indices, counters).
- **O(n)** extra space: allocating another array of length **n**, or recursion depth **n** in the worst case (e.g., a naive recursive Fibonacci-style pattern—or deep recursion on **n**).

The **input itself** is often excluded when people say “O(1) space” for an in-place sort; clarify if the interviewer cares about **total** memory including input.

### “Constants matter in the real world”

Asymptotics do **not** say which implementation wins when **n = 20**. A tight **O(n²)** loop on small **n** can beat a heavy **O(n log n)** setup with big overhead. But when **n** is large—or grows over years—**wrong asymptotics** dominates. **QA** value: if a feature “worked in demo” with **n = 200** but production has **n = 200,000**, ask about expected complexity and typical **n**.

---

## Code Examples

### O(1): fixed work regardless of array length

```java
// O(1) time — index the first element (array must exist and have length > 0)
int first = arr[0];
int len = arr.length;
```

```python
# O(1) time — index the first element (list must be non-empty)
first = items[0]
n = len(items)
```

### O(n): one loop over n elements

```java
// O(n) time — visits each index once
for (int i = 0; i < arr.length; i++) {
    // O(1) work inside → total O(n)
}
```

```python
# O(n) time — each element visited once
for x in items:
    # O(1) work inside → total O(n)
    pass
```

A **linear scan** to find whether a value exists (without extra structure) is **O(n)** in the worst case because you might inspect every element.

### O(n²): nested loops, both driven by n

```java
// O(n²) time — inner loop runs n times for each of n outer iterations
for (int i = 0; i < n; i++) {
    for (int j = 0; j < n; j++) {
        // constant work → n × n iterations
    }
}
```

```python
# O(n²) time — same nested structure
for i in range(n):
    for j in range(n):
        pass
```

**Not always n²:** If the inner loop runs a **fixed** number of times (say 3), the whole thing is still **O(n)**. The rule: **multiply** loop counts when they depend on **n** together.

### O(n log n): repeated “sort-sized” work (pattern sketch)

Many efficient sorting algorithms perform **O(log n)** levels of splitting or heap operations, each level touching **O(n)** elements → **O(n log n)** total. You do not need to memorize every sort’s proof this week; recognize the **pattern** when you see “divide and combine across all elements.”

### Estimating complexity: a small checklist

1. **What is n?** (array length? string length? grid size **r × c**?)
2. **Where is the innermost work?** How many times does it run as a function of **n**?
3. **Are data structures hidden costs?** Copying a whole list on every iteration can add an extra **O(n)** per outer step → **O(n²)** overall.
4. **Recursion:** Each call has a cost × depth × branching (tree recursion can explode—e.g., naive Fibonacci is **O(2ⁿ)** time, much worse than **O(n)** dynamic programming).

---

## Summary

- **Big-O** classifies **growth** with **n**; it ignores constant factors and lower-order terms.
- **Single** loop over **n** → often **O(n)**; **nested** loops both scaling with **n** → often **O(n²)**.
- **Halving** the problem each step → **O(log n)**; **binary search** on sorted data is the star example.
- **Space** is analyzed the same way: variables vs **O(n)** auxiliary structures or recursion depth.
- For **QA**, this vocabulary helps you **frame** performance risks, read complexity notes in tickets, and ask sharper questions about **scale** and **worst-case** data.

---

## Additional Resources

- [Big-O cheat sheet](https://www.bigocheatsheet.com/) — compact reference for common structures and sorts.
- [MIT OCW 6.006 Introduction to Algorithms](https://ocw.mit.edu/courses/6-006-introduction-to-algorithms-fall-2011/) — optional deeper theory and proofs.
- **Next:** **Linear search** implements the **O(n)** scan idea directly; **binary search** implements **O(log n)** on sorted data—compare them side by side with **n** in mind.
