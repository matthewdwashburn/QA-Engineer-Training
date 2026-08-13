# String Basics (Java and Python)

## Learning Objectives

- Describe strings as **immutable** character sequences and predict what **mutating-looking** operations actually do.
- In **Java**, use core **`String`** APIs (**`length`**, **`charAt`**, **`substring`**, **`equals`**, **`isEmpty`**, **`strip`**) and explain why **`==`** is wrong for **value** comparison.
- In **Python**, use **`str`** operations (**`len`**, indexing, **slicing**, **`strip`**, **`split`**) and know that **`==`** compares **value** for strings while **`is`** tests **identity**.
- Recognize **UTF-16 code units** (Java) vs **Unicode code points** gotchas at a high level—enough to avoid surprise when **emoji** or combining characters appear in tests.
- Connect string behavior to **QA**: assertions, log parsing, API payloads, and common **off-by-one** / **equality** bugs.

---

## Why This Matters

> **Weekly Epic Connection:** Week work touches **assertions**, **log lines**, **JSON/XML**, **URLs**, and **error messages**—almost all **text**. Misunderstanding **immutability**, **substring** bounds, or **equality** produces flaky tests, false negatives in checks, and production defects that are painful to debug.

For **QA**, strings are where **boundary** mistakes hide: **empty** vs **blank**, **trim** before compare or not, **case** sensitivity, and **locale**-dependent behavior. Knowing what your language **guarantees** helps you write **precise** expected values and spot **developer shortcuts** (e.g. **`==`** on Java **`String`**).

---

## The Concept

### Immutability (both languages)

**Immutable** means: after a value is created, **no operation changes that object in place**; “changes” produce **new** strings.

**Java**

```java
String s = "hi";
String t = s.toUpperCase(); // s is still "hi"; t refers to a new String "HI"
```

**Python**

```python
s = "hi"
t = s.upper()  # s is still "hi"; t is a new str "HI"
```

**Why it matters:** Passing a string to a method does not let that method “silently rewrite” the caller’s variable unless you **reassign** the variable to a **new** return value. **Concatenation** and **methods** allocate **new** backing storage (conceptually); performance-sensitive code uses builders/buffers (**`StringBuilder`** in Java; **`list` + `join`** or **`io.StringIO`** patterns in Python for heavy assembly—covered more in later topics).

---

## Java: `String` essentials

### Internal model (high level)

Java **`String`** is backed by **`char`** values that are **UTF-16 code units**. Most everyday ASCII/English fits one **`char`** per character; some Unicode characters need **two** code units (**surrogate pairs**). For QA: **`.length()`** and **`charAt(i)`** count **code units**, not always “human characters.” For depth, see **Additional Resources**.

### Length, characters, substring

```java
String s = "hello";
int n = s.length();           // 5
char c = s.charAt(1);         // 'e' — zero-based index
String sub = s.substring(1, 3); // "el" — end index EXCLUSIVE
boolean empty = s.isEmpty();    // true only for ""
String trimmed = "  x  ".strip(); // "x" (Java 11+)
```

**Off-by-one:** **`substring(beginIndex, endIndex)`** takes **`[beginIndex, endIndex)`**—the **end** is **not** included. **`substring(1)`** means “from 1 through end of string.”

### Equality: always `.equals` for value

```java
boolean same = s1.equals(s2);
boolean sameNoCase = s1.equalsIgnoreCase(s2);
```

**`==`** compares **references** (same object), not character content. Two strings with the same letters may be **`==`** or not depending on **interning** and construction—**do not rely on `==`** for business logic.

**Null-safe idiom** (put literal first if one side might be null):

```java
"expected".equals(actual)
```

Use **`Objects.equals(a, b)`** when **both** may be null and you want **symmetric** null handling.

### Concatenation

**`+`** creates **new** strings. Fine for **few** pieces. Many appends in a loop → prefer **`StringBuilder`** (later lesson) to avoid **quadratic** copying.

### `char` vs `String` literals

- **`'A'`** — single **`char`**
- **`"A"`** — **`String`** of length 1

Mixing them in APIs causes **compile** errors; watch method signatures.

---

## Python: `str` essentials

### Model (high level)

Python 3 **`str`** holds **Unicode** text. **`len(s)`** counts **code points** in the CPython model you usually see (still be careful with **combining characters** and **grapheme clusters** in internationalized tests).

### Length, indexing, slicing

```python
s = "hello"
n = len(s)        # 5
c = s[1]          # 'e' — zero-based
sub = s[1:3]      # "el" — end index EXCLUSIVE in slice notation
empty = len(s) == 0
also_empty = s == ""
trimmed = "  x  ".strip()
```

**Slicing recap:** **`s[i:j]`** is **`[i, j)`**. Omitting start/end uses bounds of the string: **`s[:3]`**, **`s[2:]`**.

### Equality: `==` is value equality

For **`str`**, **`==`** compares **content**. **`is`** checks **object identity** (same object in memory). Do **not** use **`is`** for comparing string **values**.

```python
a = "hello"
b = "hel" + "lo"
a == b   # True (same sequence of characters)
a is b   # Often True for small interned literals — still use == for semantics
```

**Rule of thumb:** compare text with **`==`**; reserve **`is`** for **`None`** and **singletons**.

### Useful helpers (sample)

```python
"abc".startswith("a")
"xyz".endswith("z")
"a,b,c".split(",")      # ['a', 'b', 'c']
"-".join(["a", "b"])    # 'a-b'
```

---

## Pitfalls that show up in tests and reviews

| Topic | Java | Python |
|--------|------|--------|
| **Value vs identity** | Use **`equals`**, not **`==`** | Use **`==`** for value; avoid **`is`** for text |
| **Empty vs blank** | **`isEmpty()`** vs spaces-only — may need **`strip`** first | **`""`** vs **`"   "`** — check **`strip()`** if spec says “no visible text” |
| **Substring/slice end** | **`endIndex` exclusive** | **Stop index exclusive** in **`s[i:j]`** |
| **Case** | **`equalsIgnoreCase`** when spec ignores case | **`lower()`** / **`casefold()`** for comparisons (locale rules may apply) |
| **Null / missing** | **`NullPointerException`** if you call **`s.equals`** on null **`s`** | Often **`None`** — cannot call string methods until you branch |

---

## QA-minded checklist

- Does the requirement mean **exact** match, **trimmed**, or **case-insensitive**?
- Are fixtures using **Unicode** that might change **length** vs visual width (emoji, accents)?
- For **logs**, is parsing based on **fixed columns** or **delimiters**—and what if the message contains the delimiter?
- After **refactors**, did anyone switch Java **`==`** thinking it “looks cleaner”?

---

## Summary

- **Java `String`** and **Python `str`** are **immutable**; methods return **new** text rather than mutating in place.
- **Java:** **`substring(begin, end)`** uses an **exclusive** end; use **`equals`** / **`Objects.equals`** for content; **`==`** is for **reference** equality only.
- **Python:** **slicing** **`[i:j]`** is **half-open**; use **`==`** for string **value**; **`is`** is **not** for general text comparison.
- **UTF-16 / Unicode** details matter for **international** data and some **`length`/index** expectations—flag odd results early in **test design**.

---

## Additional Resources

- [Java `String` (Java 21)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/String.html) — authoritative method list.
- [Oracle Java Tutorial: Strings](https://docs.oracle.com/javase/tutorial/java/data/strings.html) — conceptual tour and examples.
- [Python 3 `str`](https://docs.python.org/3/library/stdtypes.html#text-sequence-type-str) — official documentation for string methods and formatting entry points.
