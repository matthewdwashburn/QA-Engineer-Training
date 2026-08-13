# Interview Questions: Week 3 — Java Advanced

**Epic:** OOP → exceptions → collections → lambdas → logging (per CMA log and `written/` topics).

**How to use:** Answer aloud or in writing, then open **Click to Reveal Answer**. Compare your response to the **Keywords** first.

**Difficulty mix (target):** ~70% beginner, ~25% intermediate, ~5% advanced.

---

## Beginner (Foundational)

### Q1. What is **short-circuit** evaluation for `&&` and `||`, and why does it matter for null checks?

**Keywords:** `&&`, `||`, not evaluated, null, `NullPointerException`

<details>
<summary>Click to Reveal Answer</summary>

For `a && b`, if `a` is `false`, `b` is **not** evaluated. For `a || b`, if `a` is `true`, `b` is **not** evaluated. That lets you write safe guards like `if (x != null && x.length() > 0)` so `length()` is never called on `null`, avoiding a `NullPointerException`.
</details>

---

### Q2. What is the difference between a **class** and an **object** in Java?

**Keywords:** blueprint, instance, `new`, heap, reference

<details>
<summary>Click to Reveal Answer</summary>

A **class** is the blueprint (fields, methods, constructors). An **object** is a **runtime instance** of that class created with `new`, living on the **heap**; variables hold **references** to objects.
</details>

---

### Q3. What does **`static`** mean for a field or method?

**Keywords:** class, shared, no `this`, one copy

<details>
<summary>Click to Reveal Answer</summary>

`static` members belong to the **class**, not to each instance. There is **one** storage location for a static field; static methods cannot use **`this`** and access static members directly. Use static for shared constants or utilities that do not need per-object state.
</details>

---

### Q4. List the four Java **access modifiers** / levels and what **default** (no keyword) means.

**Keywords:** `public`, `protected`, package-private, `private`, same package

<details>
<summary>Click to Reveal Answer</summary>

**`public`** — everywhere. **`protected`** — package + subclasses (including other packages for inherited members, per JLS rules trainees study). **Default (package-private)** — same **package** only. **`private`** — only inside the declaring top-level class. *Default* means **package-private**: visible to types in the same package only.
</details>

---

### Q5. What is **encapsulation**, and what is **abstraction**?

**Keywords:** private, getters, setters, hide, contract, what vs how

<details>
<summary>Click to Reveal Answer</summary>

**Encapsulation** hides internal state (often `private` fields) and exposes controlled behavior through methods (getters/setters, validation). **Abstraction** emphasizes **what** clients can do (interfaces, abstract APIs) while hiding **how** it is implemented. Encapsulation supports abstraction by letting internals change behind a stable surface.
</details>

---

### Q6. What is the difference between **method overloading** and **method overriding**?

**Keywords:** compile time, runtime, same signature, subclass

<details>
<summary>Click to Reveal Answer</summary>

**Overloading:** same method name, **different parameter lists** in the same class (or inherited visible methods); resolved at **compile time**. **Overriding:** subclass provides an instance method with the **same signature** as the superclass; instance calls resolve at **runtime** (virtual dispatch) based on the actual object type.
</details>

---

### Q7. What does **`equals`** vs **`==`** mean for object references?

**Keywords:** identity, value, `Object.equals`, override

<details>
<summary>Click to Reveal Answer</summary>

**`==`** on references tests **identity** (same object in memory). **`equals`** is meant to express **logical equality** when overridden (e.g. same business key). Default `Object.equals` behaves like `==` until you override it.
</details>

---

### Q8. Why must you override **`hashCode`** when you override **`equals`**?

**Keywords:** contract, `HashMap`, `HashSet`, bucket, collision

<details>
<summary>Click to Reveal Answer</summary>

The contract requires that **equal** objects (by `equals`) produce **equal** `hashCode` values. Hash-based collections (`HashMap`, `HashSet`) use `hashCode` to choose a **bucket** and `equals` to resolve collisions. Breaking the contract causes lost lookups, duplicates, or inconsistent set membership.
</details>

---

### Q9. What is a **checked** exception vs an **unchecked** exception?

**Keywords:** `Exception`, `RuntimeException`, `throws`, compiler

<details>
<summary>Click to Reveal Answer</summary>

**Checked** exceptions extend `Exception` but not `RuntimeException`; the **compiler** requires `catch` or **`throws`** on propagating methods. **Unchecked** exceptions are `RuntimeException` (and `Error`); no `throws` required. Checked often models recoverable external failures; unchecked often signals programming bugs or optional recovery.
</details>

---

### Q10. When is **`ArrayList`** usually preferred over **`LinkedList`** for a **`List`**?

**Keywords:** random access, `get(i)`, index, default

<details>
<summary>Click to Reveal Answer</summary>

**`ArrayList`** is the default for most lists: **`get(i)`** and **`set(i)`** are **O(1)** and locality is good. **`LinkedList`** pays **O(n)** for arbitrary **`get(i)`** because it must walk nodes. Prefer `LinkedList` when you frequently add/remove **at ends** or use **iterator**-based middle edits—not for heavy random indexing.
</details>

---

### Q11. What is a **functional interface**, and name two types from `java.util.function`.

**Keywords:** SAM, `@FunctionalInterface`, `Predicate`, `Function`, `Consumer`, `Supplier`

<details>
<summary>Click to Reveal Answer</summary>

A **functional interface** has exactly **one abstract method** (SAM), so a lambda can implement it. Examples from `java.util.function`: **`Predicate<T>`** (`test`), **`Function<T,R>`** (`apply`), **`Consumer<T>`** (`accept`), **`Supplier<T>`** (`get`). **`@FunctionalInterface`** documents intent and triggers compiler checks.
</details>

---

## Intermediate (Application)

### Q12. You have a **`HashMap`** whose keys are a custom **`Employee`** type. Lookup fails even though “the same” employee was `put` earlier. What is the most likely mistake?

**Keywords:** `equals`, `hashCode`, contract, fields

**Hint:** Think about what the map uses to bucket and compare keys.

<details>
<summary>Click to Reveal Answer</summary>

The **`Employee`** class likely has **broken `equals`/`hashCode`**: e.g. default `Object` identity semantics while logical equality should use `id`, or **`hashCode` not updated** when `equals` was overridden. **Equal** keys must have **equal** hash codes. Fix by implementing both consistently from the same significant fields (often with `Objects.equals` / `Objects.hash`).
</details>

---

### Q13. When would you choose **`TreeSet`** over **`HashSet`**?

**Keywords:** sorted, `Comparable`, `Comparator`, `O(log n)`, order

<details>
<summary>Click to Reveal Answer</summary>

Choose **`TreeSet`** when you need **sorted** iteration or **range**/`NavigableSet` operations and can pay **O(log n)** per add/contains. Choose **`HashSet`** when you only need **fast average** membership and **do not** require sorted order. `TreeSet` requires elements to be **mutually comparable** (or a `Comparator`).
</details>

---

### Q14. How do **`HashMap`** and **`Hashtable`** differ in typical new code, and what might you use for shared **concurrent** maps?

**Keywords:** synchronized, `null`, legacy, `ConcurrentHashMap`

<details>
<summary>Click to Reveal Answer</summary>

**`HashMap`** is unsynchronized, allows **one `null` key** and **null values**, and is the usual default. **`Hashtable`** is **legacy**, **synchronized** on methods, and disallows **null** keys/values—avoid for new code unless required by an API. For **concurrent** mutable maps, prefer **`ConcurrentHashMap`** over `Hashtable` or `Collections.synchronizedMap` for typical scalability needs (still understand compound-operation risks).
</details>

---

## Advanced (Deep Dive)

### Q15. Explain **stack depth** and **`StackOverflowError`** in the context of a **recursive** method. What mitigations does the curriculum suggest for very deep linear recursion?

**Keywords:** call stack, frame, base case, iteration, tail recursion, JVM

<details>
<summary>Click to Reveal Answer</summary>

Each recursive call pushes a **stack frame**; too many nested calls exhaust the thread stack → **`StackOverflowError`**. Sound recursion needs a **base case** and arguments that **progress** toward it. The curriculum notes that **tail recursion** is not reliably optimized away on the **JVM**, so **very deep** linear recursion should be rewritten as a **loop** or another algorithm to avoid stack blow-up.
</details>

---

## Quick reference — counts

| Tier        | Questions |
|------------|-----------|
| Beginner   | Q1–Q11 (11) |
| Intermediate | Q12–Q14 (3) |
| Advanced   | Q15 (1) |

**Approximate distribution:** 11/15 ≈ **73%** beginner, **20%** intermediate, **~7%** advanced (aligned with the 70 / 25 / 5 target for a 15-question bank).

---

*Questions are written to be answerable from Week 3 `written/` lessons; advanced items stay within stated JVM/recursion and concurrency framing from those materials.*
