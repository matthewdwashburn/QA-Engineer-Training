# The String Pool and `intern()`

## Learning Objectives

- Explain what the **string pool** (intern pool) is and why **string literals** with the same value may share **one** JVM object.
- Predict **`==`** vs **`.equals()`** outcomes for **literals**, **`new String(...)`**, **`intern()`**, and strings built at **runtime**.
- Describe **`String.intern()`** semantics: when it helps with **deduplication**, and what **risks** (memory pressure, **CPU** cost) come from careless use.
- Relate **identity** vs **value** to **QA**: flaky assumptions in tests, log comparisons, and **serialization**/API boundaries where **new** strings appear.
- Recognize that **Python** also **interns** some strings **implementation-defined**—and why **`==`** remains the right default for **text equality**.

---

## Why This Matters

> **Weekly Epic Connection:** Week work mixes **literals**, **parsed** text, and **constructed** strings. The pool explains why **`a == b`** sometimes “works” in a scratch program and **breaks** in production when one value came from **`new String`**, a **database**, or **JSON**. **QA** sees this as **intermittent** failures when tests use **`==`** or when code assumes **reference** equality for **cache keys** without **interning**.

Understanding the pool separates **correctness** (always **`.equals`** for business logic) from **optimization** (**`intern()`** after **measurement**). It also explains oddities in **memory profiles** (many distinct **`String`** objects with the same characters vs fewer **interned** copies).

---

## The Concept

### What the pool is

The **string pool** is a JVM facility where certain **`String`** instances are **canonical**: for a given sequence of characters, the runtime may keep **one** shared instance so **literals** and **interned** values **reuse** storage instead of allocating duplicates everywhere.

**Important:** Exact details (where it lives in memory, GC interaction) are **JVM/version** dependent. For learning, the mental model is enough: **some** strings are **shared by reference**; **most application code** should still compare by **value** with **`.equals`**.

### Literals and sharing

**String literals** in source code are **interned** when the class is loaded (conceptually: the literal is tied to a pooled instance).

```java
String a = "hi";
String b = "hi";
System.out.println(a == b);      // true — typically same pooled instance
System.out.println(a.equals(b)); // true — value match (always use for logic)
```

**Analogy — a shared whiteboard label:** Two people both write the word “EXIT” on the same official sign. They point at **one** sign. **`==`** asks “same physical sign?” **`.equals`** asks “same letters?” For literals, both can be true; for separately built strings, only **`.equals`** is reliable.

### `new String` creates a separate heap object

```java
String x = new String("hi"); // new object; characters may copy from "hi"
String y = "hi";
System.out.println(x == y);      // false — different objects
System.out.println(x.equals(y)); // true — same character sequence
```

Even though **`new String("hi")`** uses the literal **`"hi"`** internally, **`x`** refers to a **distinct** object unless you **intern** it.

### Compile-time constant expressions

The compiler may fold **constant** **`String`** expressions so they behave like **literals**:

```java
String a = "hel" + "lo";
String b = "hello";
System.out.println(a == b); // true — single pooled "hello" for both
```

If **any** part is **not** a compile-time constant (e.g. built with **`StringBuilder`** at runtime, read from a file), you generally get **different** objects:

```java
String part = "lo";
String c = "hel" + part;   // runtime concatenation
String d = "hello";
System.out.println(c == d); // false (typical) — use equals
```

**Rule for application logic:** treat **runtime-built** strings as **not** pooled unless you **`intern()`** them—and **do not** **`intern()`** “just in case.”

---

## `String.intern()`

### What it does

```java
String s = someString.intern();
```

**`intern()`** returns the **canonical** representation from the pool: if an equal string already exists in the pool, you get **that** reference; otherwise your string may be **added** (implementation details vary by JVM version and tuning).

### Possible benefits

- **Deduplication** when you have **many** instances of the **same** text (e.g. repeated **enum-like** labels from **parsing**) and you want **`==`** for **fast** identity checks **after** interning—**rare** in typical business apps without profiling proof.

### Costs and risks

- **CPU:** interning walks pool structures; doing it in **tight loops** on **unique** strings can **hurt** more than it helps.
- **Memory:** the pool **retains** canonical strings; **interning unbounded** unique strings (e.g. every user id from a firehose) can behave like a **memory leak** relative to expectations.
- **Correctness:** relying on **`==`** after **partial** interning (some code paths intern, others do not) creates **subtle bugs**.

**Default guidance:** compare with **`.equals`**; consider **`intern()`** only with **measurement** and **clear** ownership of lifetimes.

---

## When `==` is still appropriate (Java)

- **`enum`** constants and intentional **singletons** (identity is the **meaning**).
- **Very specialized** code paths where **everything** in a **hot** structure is **proven** interned—**expert** territory, not a default pattern.

**Not** appropriate: comparing **user input**, **API fields**, or **DB** values with **`==`** unless you have a **documented** interning contract end-to-end.

---

## QA and testing angles

- **Assertions:** Prefer **`assertEquals(expected, actual)`** (JUnit/Hamcrest) which uses **value** equality for **`String`**, not **`==`**.
- **Flaky demos:** A test that **`==`** passes locally with **literals** may fail when production code passes **`new String(...)`** or **substring** results—still **equal** by **`.equals`**.
- **Caches and maps:** Using **`String`** keys: **`HashMap`** uses **`equals`/`hashCode`**; identity does not matter unless you deliberately use **`IdentityHashMap`**.
- **Serialization/network:** Bytes round-trip through parsers often yield **new** **`String`** instances—**never** assume **`==`** across boundaries.

---

## Python aside (CPython)

**Python** may **intern** some **string literals** and small strings **implementation-dependent** (do not rely on **`is`** for equality of text).

```python
a = "hello"
b = "hello"
a is b  # Often True for literals — not a language guarantee for all strings
```

**Best practice:** compare text with **`==`**. Use **`is`** for **`None`** and explicit **singleton** checks, not general **string** content.

This lesson’s **pool** and **`intern()`** focus is **Java/JVM**-centric; the **principle** is the same: **value** vs **identity**.

---

## Summary

- **String literals** (and some **constant** folds) are typically **interned**; **`new String`** and most **runtime-built** strings are **separate objects**.
- **`==`** tests **reference** identity; **`.equals`** tests **character** content—use **`.equals`** (or **`Objects.equals`**) for **normal** business logic.
- **`intern()`** can **deduplicate** at the cost of **CPU** and **long-lived** pool entries; use **deliberately** and **profile**, not by default.
- **QA:** avoid **`==`** for **strings** in tests and reviews; expect **new** instances from **I/O** and parsers.

---

## Additional Resources

- [JLS §3.10.5: String Literals](https://docs.oracle.com/javase/specs/jls/se21/html/jls-3.html#jls-3.10.5) — specification of literal behavior.
- [`String.intern()` (Java 21)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/String.html#intern()) — official method contract and caveats.
- [Oracle Tutorial: Strings](https://docs.oracle.com/javase/tutorial/java/data/strings.html) — broader context on immutability and usage.
