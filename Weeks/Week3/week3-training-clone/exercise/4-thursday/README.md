# Week 3 — Thursday pair programming lab (`pair_programming_collections`)

**Protocol:** **Collaborative project** — driver/navigator pair, **one combined artifact** at submission.

**Epic tie-in:** `Map`, `Set`, `Queue`, lambdas, SLF4J/Logback (`written/4-thursday/`, `demos/4-thursday/`).

**Time:** ~3–4 hours (pair).

---

## Roles

| Partner | Primary deliverable | Collections |
|---------|---------------------|---------------|
| **A** | Word-frequency + vocabulary | `HashMap<String,Integer>` counts; **`TreeSet<String>`** for sorted **unique** words (lowercased tokens) |
| **B** | Task priority system | **`PriorityQueue<Task>`** (`Task` with priority + description; **lower int = higher priority** unless you document otherwise) |

Swap **driver/navigator** at least **twice** (suggest: after A’s first green run, after B’s first green run).

---

## Phase 1 — Baseline (individual tracks)

### Partner A — `partner_a`

1. Tokenize a **hard-coded** multi-line `String` paragraph (split on non-letters; `String.toLowerCase()`).
2. Build **word → count** in a `HashMap`.
3. Print **top N** frequent words (simple loop sort `Map.Entry` list **or** stream—after refactor use lambdas).
4. Build a **`TreeSet<String>`** of all unique words; print first/last to show ordering.

### Partner B — `partner_b`

1. **`record` or class `Task(int priority, String description)`** with **natural order** by priority (`Comparable`).
2. `offer` several tasks in **non-sorted** order; `poll` until empty and print order.
3. Add **`peek`** demo without draining.

---

## Phase 2 — Refactor (both)

1. Replace at least **two** explicit loops with **lambdas** / **method references** (e.g. `forEach`, `removeIf`, `sort` with `Comparator.comparing...`).
2. Extract **one** `Predicate`, `Function`, or `Comparator` into a **named variable** with a short comment.

---

## Phase 3 — Cross-review + logging

1. **Partner B reviews A’s** code; **A reviews B’s** (use `templates/PAIR_CHECKLIST.md`).
2. Add **SLF4J + Logback** to **both** programs (same dependency setup as `demos/4-thursday/code/DEPENDENCIES.md` or your course Maven project).
3. **`logback.xml`** with **console** + **file** appender (path under project, e.g. `logs/pair.log`).
4. Log at **INFO** for normal flow, **DEBUG** inside a hot loop or token step, **WARN** for empty input.

---

## Artifact (Definition of done)

Submit **one** folder or Git repo containing:

- `partner_a/` and `partner_b/` **runnable** sources (default package OK if easier).
- **`PAIR_RETRO.md`** — completed `templates/PAIR_RETRO.md`.
- **`logback.xml`** (or both apps share one config—document how to run).
- Short **`RUN.md`**: exact `javac`/`java` or `mvn` commands.

### Grading / self-check rubric (2 min)

- [ ] `HashMap` counts match manual spot-check on tiny string.
- [ ] `TreeSet` iteration is **sorted**; duplicates absent.
- [ ] `PriorityQueue` `poll` order matches **Comparable** rule you documented.
- [ ] At least **two** lambda refactors visible in final code.
- [ ] Log file created on disk with expected levels.

### References

- `written/4-thursday/map-interface.md`, `hashset-and-treeset.md`, `queue-interface-priority-queue.md`, `lambdas.md`, `java-introduction-to-logback.md`
