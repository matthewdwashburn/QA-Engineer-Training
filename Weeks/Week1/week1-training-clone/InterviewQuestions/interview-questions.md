# Interview Questions: Week 1 — Git-Python

Curriculum-aligned question bank (hidden answers). Difficulty mix targets ~70% beginner, ~25% intermediate, ~5% advanced.

---

## Beginner (Foundational)

### Q1: What is Git, and how does a *distributed* version control system differ from a *centralized* one?

**Keywords:** Git, distributed, centralized, collaboration, snapshot

<details>
<summary>Click to Reveal Answer</summary>

Git is a **distributed version control system (DVCS)** that tracks changes to files, supports collaboration, and lets you roll back to prior states. In a **centralized** system, history lives primarily on one server; in a **distributed** system like Git, every clone carries a full copy of the repository and its history, so work can continue and be synchronized peer-to-peer via remotes (for example GitHub) without a single central bottleneck.
</details>

---

### Q2: Name Git’s three main areas and describe what happens when you run `git add` and `git commit`.

**Keywords:** Working directory, staging area, repository, commit

<details>
<summary>Click to Reveal Answer</summary>

The three areas are the **working directory** (your edited files), the **staging area** (index — what is prepared for the next commit), and the **repository** (committed history in `.git`). **`git add`** moves changes from the working directory into the staging area. **`git commit`** records the staged snapshot as a new commit in the repository history.
</details>

---

### Q3: What is a Python virtual environment, and why is dependency isolation important?

**Keywords:** venv, isolation, pip, conflicts

<details>
<summary>Click to Reveal Answer</summary>

A **virtual environment** is an isolated Python environment (its own interpreter location and installed packages) for a project. **Isolation** matters because installing packages globally forces one version per library for the whole machine; different projects often need different versions, and isolation prevents upgrades in one project from breaking another.
</details>

---

### Q4: What does the LEGB rule describe in Python?

**Keywords:** Local, Enclosing, Global, Built-in, scope

<details>
<summary>Click to Reveal Answer</summary>

**LEGB** is the order Python uses to **resolve a name** to a variable: **L**ocal (inside the current function), **E**nclosing (outer nested functions), **G**lobal (module level), **B**uilt-in (Python’s built-in names). Python searches L → E → G → B and uses the first match.
</details>

---

### Q5: How do lists, tuples, and sets differ in terms of mutability, ordering, and duplicates?

**Keywords:** Mutable, immutable, ordered, unique

<details>
<summary>Click to Reveal Answer</summary>

**Lists** are **mutable** and **ordered**; they allow duplicates. **Tuples** are **immutable** and **ordered**; they allow duplicates. **Sets** are **mutable** (the set object can change), **unordered**, and store **unique** elements only.
</details>

---

### Q6: What are the four pillars of object-oriented programming (OOP)?

**Keywords:** Encapsulation, inheritance, polymorphism, abstraction

<details>
<summary>Click to Reveal Answer</summary>

The four pillars are **encapsulation** (bundling data and behavior and controlling access), **inheritance** (deriving new types from existing ones), **polymorphism** (the same interface or call can behave differently for different types), and **abstraction** (hiding complexity behind simpler interfaces).
</details>

---

### Q7: When calling a function, what rule applies to mixing positional and keyword arguments?

**Keywords:** Positional, keyword, order, SyntaxError

<details>
<summary>Click to Reveal Answer</summary>

**Positional arguments must come before keyword arguments.** If you place a positional argument after a keyword argument, Python raises a **SyntaxError**. Keyword arguments can be passed in any order as long as all positional-required parameters are satisfied first.
</details>

---

### Q8: What is the difference between using `print()` and the `logging` module for application output?

**Keywords:** Levels, timestamps, configuration, production

<details>
<summary>Click to Reveal Answer</summary>

`print()` sends text to standard output with no built-in **severity levels**, **timestamps**, or flexible **destinations**. The **`logging`** module supports levels (e.g. DEBUG, INFO, WARNING, ERROR, CRITICAL), can route output to console, files, or other handlers, and is **configurable** at runtime—making it appropriate for **production** and long-running automation, unlike ad-hoc prints.
</details>

---

### Q9: What is a Python decorator, and what does `@decorator_name` above a function mean?

**Keywords:** Wrap, behavior, syntactic sugar, function

<details>
<summary>Click to Reveal Answer</summary>

A **decorator** is a callable (usually a function) that **takes a function and returns a wrapper** that adds behavior before/after the original call without editing the original’s body. The **`@decorator_name`** syntax is **syntactic sugar** for passing the function object to the decorator and rebinding the name to the returned wrapper (e.g. `f = my_decorator(f)`).
</details>

---

### Q10: In a `try` / `except` / `else` / `finally` structure, when does each of `else` and `finally` run?

**Keywords:** Exception, always, no exception

<details>
<summary>Click to Reveal Answer</summary>

**`except`** runs only if a matching exception occurs in `try`. **`else`** runs only if the `try` block completes **with no exception**. **`finally`** runs **always**—whether an exception occurred or not, and even if there is a `return`—so it is used for cleanup that must happen regardless.
</details>

---

### Q11: What is an *iterable* versus an *iterator* in Python?

**Keywords:** `__iter__`, `__next__`, StopIteration

<details>
<summary>Click to Reveal Answer</summary>

An **iterable** is an object with **`__iter__()`** that returns an **iterator** (e.g. list, str, dict). An **iterator** is an object with **`__next__()`** that returns the next value and raises **`StopIteration`** when exhausted. A `for` loop calls `iter()` then repeatedly `next()` until `StopIteration`.
</details>

---

## Intermediate (Application)

### Q12: Why is `def add_item(item, items=[])` dangerous, and what pattern should you use instead?

**Keywords:** Mutable default, shared, None

**Hint:** Default values are created once, not per call.

<details>
<summary>Click to Reveal Answer</summary>

A **mutable default** (like a list) is created **once** when the function is defined, so every call **shares the same list**, leading to surprising accumulation of data across calls. Use **`None`** as the default and create a fresh list inside the function when needed, for example:

```python
def add_item(item, items=None):
    if items is None:
        items = []
    items.append(item)
    return items
```
</details>

---

### Q13: Why does the curriculum discourage bare `except:` and broad `except Exception:` in production-style code?

**Keywords:** Specific exceptions, KeyboardInterrupt, debugging

**Hint:** Think about what you might accidentally swallow.

<details>
<summary>Click to Reveal Answer</summary>

A **bare `except:`** catches *everything*, including events you usually do not want to trap (for example **`KeyboardInterrupt`** and **`SystemExit`**), which makes programs hard to stop and hides real bugs. **`except Exception:`** is still very broad and can mask unexpected failures. Prefer **catching specific exception types** you can handle and recover from, so real defects surface during testing and debugging.
</details>

---

### Q14: You need to **increment a module-level counter** from inside a function. What keyword is required, and what goes wrong if you omit it?

**Keywords:** global, assignment, local variable

<details>
<summary>Click to Reveal Answer</summary>

You must declare **`global counter`** (using the actual variable name) inside the function before assigning to it. Without **`global`**, Python treats **`counter += 1`** as writing to a **local** variable that was never assigned a value in that scope, which leads to an **UnboundLocalError** when you try to read-update the global name.
</details>

---

## Advanced (Deep Dive)

### Q15: How can two objects become uncollectable by **reference counting alone**, and what part of Python helps clean them up?

**Keywords:** Circular reference, `gc`, generational

<details>
<summary>Click to Reveal Answer</summary>

**Reference counting** frees an object when its count hits zero, but **circular references** (objects that refer to each other in a cycle) can keep counts above zero even when no outside references remain—so the cycle is not freed by reference counting alone. Python’s **`gc` module** implements a **generational garbage collector** that periodically detects and collects such cycles (objects are grouped into generations 0–2 and collected with decreasing frequency).
</details>

---
