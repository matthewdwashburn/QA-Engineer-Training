# Interview Questions: Week 2 — Python-Java

Curriculum-aligned question bank (hidden answers). Mix targets ~**70%** beginner, ~**25%** intermediate, ~**5%** advanced. Grounded in `content/Week2-Python-Java/written/`.

---

## Beginner (Foundational)

### Q1: In Python, what does the `with` statement give you when opening a file that manual `open()` / `close()` does not guarantee?

**Keywords:** Context manager, cleanup, exception, `__exit__`

<details>
<summary>Click to Reveal Answer</summary>

A **`with`** block uses a **context manager** so the file’s **`__exit__`** runs when leaving the block—even if an error occurs—so the file is **closed reliably**. Manual `close()` is easy to skip after an exception.
</details>

---

### Q2: What is the difference between a **JDK** and a **JRE** for a developer who needs to **compile** Java?

**Keywords:** `javac`, JDK, JRE, compile

<details>
<summary>Click to Reveal Answer</summary>

The **JDK** includes **development tools** such as **`javac`** (the compiler) **plus** a **JRE**. A **JRE** alone can **run** bytecode but does **not** include **`javac`**, so it is not enough to compile new `.java` source.
</details>

---

### Q3: What does the **`javac`** tool produce from `.java` source files?

**Keywords:** Bytecode, `.class`, JVM

<details>
<summary>Click to Reveal Answer</summary>

**`javac`** compiles Java source into **bytecode** stored in **`.class`** files. The **`java`** launcher loads those classes into the **JVM** to execute them.
</details>

---

### Q4: In Flask, how does a **URL path** (e.g. `/items`) connect to your Python code?

**Keywords:** Route, decorator, view function

<details>
<summary>Click to Reveal Answer</summary>

You register a **route** with decorators like **`@app.get("/items")`** or **`@app.post(...)`**, which binds that **URL** to a **view function**. When a request matches, Flask calls that function and turns its return value into an HTTP **response**.
</details>

---

### Q5: In a standard Maven layout, where does **production** Java source code live?

**Keywords:** `src/main/java`, convention, directory

<details>
<summary>Click to Reveal Answer</summary>

Production code lives under **`src/main/java`**. Tests go under **`src/test/java`**—Maven’s **convention over configuration** layout.
</details>

---

### Q6: What precondition does **binary search** on an array require that **linear search** does not?

**Keywords:** Sorted, ordering, halves

<details>
<summary>Click to Reveal Answer</summary>

**Binary search** requires the data to be **ordered** (typically a **sorted** array) so each step can discard half the remaining range. **Linear search** can run on **unsorted** data but is **O(n)** in the worst case.
</details>

---

### Q7: In Java, why should you compare two strings for **equal text** with **`.equals(...)`** instead of **`==`** in application logic?

**Keywords:** Reference, value, `String`

<details>
<summary>Click to Reveal Answer</summary>

**`==`** compares **references** (same object in memory). **`.equals`** compares **character content**. Relying on **`==`** for text is unsafe because **`new String(...)`** or different instances can hold the same characters but fail **`==`**.
</details>

---

### Q8: What does the **“S”** in **SOLID** stand for, in one sentence?

**Keywords:** Single Responsibility, one reason to change

<details>
<summary>Click to Reveal Answer</summary>

**Single Responsibility Principle:** a class should have **one cohesive reason to change**—one primary job—so changes to unrelated concerns do not force edits in the same class.
</details>

---

### Q9: A Java file begins with `package com.example.qa.app;`. Under a source root, which **folder path** must contain that file?

**Keywords:** Package, directory, mirror

<details>
<summary>Click to Reveal Answer</summary>

The path under the source root must mirror the package: **`com/example/qa/app/`** (each segment is a folder). The **`package`** declaration and on-disk layout must **match**.
</details>

---

### Q10: In Python, what is the effect of opening an existing text file with mode **`'w'`**?

**Keywords:** Truncate, overwrite, create

<details>
<summary>Click to Reveal Answer</summary>

Mode **`'w'`** opens for writing and **truncates** the file if it already exists (empties/overwrites from the start), or **creates** it if missing. Use **`'a'`** to append without truncating.
</details>

---

### Q11: In Java, how can two methods in the same class legally have the **same name**?

**Keywords:** Overloading, parameter list, return type

<details>
<summary>Click to Reveal Answer</summary>

**Method overloading:** same **method name** but a **different parameter list** (number, types, or order). You **cannot** overload using **return type alone**—the compiler must disambiguate calls from arguments.
</details>

---

## Intermediate (Application)

### Q12: You call `scanner.nextInt()` and then `scanner.nextLine()`, but `nextLine()` returns an **empty** string before you can read the user’s name. What is going wrong and how do you fix it?

**Keywords:** Buffer, newline, `nextInt`

**Hint:** What does `nextInt` leave in the stream?

<details>
<summary>Click to Reveal Answer</summary>

**`nextInt()`** reads the number but often leaves the **newline** in the input buffer. The following **`nextLine()`** then consumes that **empty line**. Fix by calling an extra **`nextLine()`** to discard the remainder of the line after **`nextInt()`**, or read everything with **`nextLine()`** and **`Integer.parseInt`** with validation.
</details>

---

### Q13: Your team’s CSV pipeline must run on Windows and Linux and handle non-ASCII characters. What two practices from this week should you apply in Python when reading and writing those files?

**Keywords:** `encoding='utf-8'`, `with`, context manager

<details>
<summary>Click to Reveal Answer</summary>

Use **`encoding='utf-8'`** explicitly when opening text files so behavior is **portable** across platforms, and use **`with open(...) as f:`** so handles **close** correctly even when parsing throws—avoiding corrupted partial writes and leaked handles.
</details>

---

### Q14: You have a **large sorted** array of IDs and must look up whether a value exists many times. Another list is **small and unsorted**. Which search strategy fits each case, per this week’s material?

**Keywords:** Binary search, linear, O(log n), O(n)

<details>
<summary>Click to Reveal Answer</summary>

For the **large sorted** array, **binary search** is appropriate—**O(log n)** per lookup. For the **small unsorted** list, **linear search** is fine; sorting just to binary search has overhead, and linear is simple **O(n)** for tiny **n**.
</details>

---

## Advanced (Deep Dive)

### Q15: When implementing binary search with integer indices `lo` and `hi`, why do many implementations set `mid = lo + (hi - lo) / 2` instead of `(lo + hi) / 2`?

**Keywords:** Overflow, midpoint, int

<details>
<summary>Click to Reveal Answer</summary>

For large arrays, **`lo + hi`** can **overflow** a 32-bit **`int`**, producing a **negative** midpoint and breaking the algorithm. **`lo + (hi - lo) / 2`** computes the same midpoint using a rearrangement that stays within the representable range for typical bounds.
</details>

---
