# Week 3 — Monday exercises

**Epic tie-in:** Loops, arrays, and classes/objects (see `written/1-monday/` and `demos/1-monday/`).

**Time:** ~2–3 hours total (both labs).

---

## Lab 1 — Arrays & loops (`exercise_arrays_loops`)

**Mode:** Implementation (code lab).

### Tasks

Work in `starter_code/ArrayLoopsLab.java`. Implement **without** calling `Arrays.sort` for the sorting task (use any **O(n²)** loop-based sort you know, e.g. bubble sort, or selection sort).

1. **`reverse(int[] data)`** — reverse **in place**; do not allocate a second full copy for the swap logic (a temp `int` for swap is fine).
2. **`min(int[] data)`** / **`max(int[] data)`** — throw `IllegalArgumentException` if `data` is `null` or length 0.
3. **`sortAscending(int[] data)`** — in-place sort smallest → largest using **nested loops** only.
4. **`main`** — demonstrate on at least two different arrays; print before/after for reverse and sort.

### Definition of done

- All methods have no extra unexplained side effects beyond the spec.
- You can explain **time complexity** of your sort in one sentence.
- Code compiles with `javac` and runs from the `starter_code` directory.

---

## Lab 2 — `Student` & `Object` methods (`exercise_classes`)

**Mode:** Implementation (code lab).

### Tasks

Complete `starter_code/Student.java` and `starter_code/StudentDemo.java`.

1. **Static:** `private static int nextId` (or similar) and **`static int getEnrollmentCount()`** returning how many `Student` instances were constructed.
2. **Instance:** `id` (unique per instance), `name`, `program` (String). Provide getters; **encapsulate** fields (`private`).
3. **`toString()`** — readable one-line summary including `id`.
4. **`equals(Object o)`** and **`hashCode()`** — two students are “equal” if **same `id`** (immutable identity). Name/program may differ.
5. **`StudentDemo.main`** — create 3 students, print `getEnrollmentCount()`, demonstrate `equals` vs `==`.

### Definition of done

- Static initializer or static block optional; document if you use one.
- No duplicate `id` values across instances.

### References

- `written/1-monday/arrays.md`, `control-flow-loops.md`, `classes-vs-objects.md`, `classes-members-static-members.md`, `object-class.md`
