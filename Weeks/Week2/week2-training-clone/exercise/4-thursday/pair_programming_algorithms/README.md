# Pair Programming: Linear vs Binary Search

**Mode:** Collaborative (Pair Programming — Code)  
**Duration:** 2.5–3.5 hours  
**Day:** 4-thursday | **Week:** 2 — Python & Java Foundations  

---

## Objective

- **Partner A** implements **iterative linear search** on a sorted `int[]` (still valid on sorted data; use for comparison).
- **Partner B** implements **iterative binary search** on the **same** sorted `int[]`.
- Together: shared **test harness**, **timing** experiment, and a short **Big-O** write-up.
- **Swap roles** for a **second dataset** size (see Round 2).

---

## Prerequisites

| Concept | Source |
|---------|--------|
| Linear search | `written/4-thursday/linear-search.md` |
| Binary search | `written/4-thursday/binary-search.md` |
| Big-O | `written/4-thursday/time-complexity.md` |
| Demo | `demos/4-thursday/code/DemoSearchAlgorithms.java` |

---

## Roles

| Role | Responsibility |
|------|----------------|
| **Driver** | Types; runs the code; speaks aloud |
| **Navigator** | Reviews logic; spots off-by-one; checks invariants |

**Switch roles** at the halfway mark (Round 2).

---

## Deliverables (tangible artifact)

Create a small project under `pair_submission/` (or your shared repo) containing:

1. **`SearchLib.java`** — two **static** methods:
   - `linearSearch(int[] sorted, int target)` → index or `-1`
   - `binarySearch(int[] sorted, int target)` → index or `-1`
2. **`SearchBenchmark.java`** — `main` that:
   - Builds a **sorted** array of unique even integers `0, 2, 4, …` up to size **N** (Round 1: **N = 1_000_000** elements; pick **random** existing value as target).
   - Prints **elapsed milliseconds** (or nanos converted) for **linear** vs **binary** for the **same** target.
3. **`RESULTS.md`** — include:
   - Table: **Round 1** and **Round 2** (different **N**, e.g. `100_000` vs `5_000_000`) with measured times.
   - One paragraph each for **expected** **O(n)** vs **O(log n)** behavior vs what you observed.
   - **Caveat paragraph:** JVM warmup, cache, constants—why Big-O still matters.

**Starter scaffolding:** see `starter_code/` — copy into your submission and complete.

---

## Round 2 (swap roles)

- Regenerate **driver/navigator** assignments for the second table row.
- Change **N** and/or pick **worst-case** target for linear (e.g. last element) vs random—briefly note in `RESULTS.md`.

---

## Definition of Done

- [ ] Both searches return **identical** index for the same inputs on **sorted** arrays (add a few **JUnit** or **assert** checks in `main`).
- [ ] `RESULTS.md` has **two** rounds and Big-O discussion.
- [ ] Pair documents **who drove** which part in a short **Contributors** section.

---

## Grading / review checklist (peer or instructor)

- [ ] Binary search uses **`mid = lo + (hi - lo) / 2`** (overflow-safe).
- [ ] No **duplicate** logic files—pair agrees on one repo state.

---

## References

- Demo: `content/Week2-Python-Java/demos/4-thursday/code/DemoSearchAlgorithms.java`
