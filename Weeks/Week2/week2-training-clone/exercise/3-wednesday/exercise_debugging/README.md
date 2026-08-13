# Exercise: Find and Fix Five Bugs with the Debugger

**Mode:** Implementation (Code Lab)  
**Duration:** 75–90 minutes  
**Day:** 3-wednesday | **Week:** 2 — Python & Java Foundations  

---

## Objective

- Import **`starter_code/BuggyReport.java`** into your IDE (IntelliJ / Eclipse / VS Code with Java).
- Use **breakpoints**, **step over**, **step into**, and **variables** to find **five** distinct bugs.
- Fix all five so **`java BuggyReport`** prints **`VERIFIED: all checks passed`** with no exceptions.

---

## Prerequisites

| Concept | Source |
|---------|--------|
| Stack traces | `written/3-wednesday/understanding-stack-traces.md` |
| Debugger | `written/3-wednesday/using-the-debugger.md` |
| Demo | `demos/3-wednesday/code/DemoStackTraces.java` |

---

## Rules

- Do **not** rewrite the program from scratch—**surgical fixes** only (a few lines per bug).
- Keep the **public API** of `BuggyReport` the same: `main` and method signatures stay unless the bug *is* the signature (it isn’t).
- Document each bug in **`BUGFIXES.md`** (one short paragraph per bug: symptom → root cause → fix).

---

## Setup

```bash
cd starter_code
javac BuggyReport.java
java BuggyReport   # expect crash or wrong output before fixes
```

---

## Definition of Done

- [ ] Five bugs fixed; **`BUGFIXES.md`** lists all five.
- [ ] `javac BuggyReport.java && java BuggyReport` ends with **`VERIFIED: all checks passed`**.
- [ ] You can **demo** one fix to a peer using the debugger (breakpoint + watch).

---

## Hints (use only if stuck)

- One bug involves a **null** reference before calling a `String` method.
- One bug is an **off-by-one** loop bound over an array.
- One bug is **integer division** where a **double** average was intended.
- One bug **declares** a correct index but never **returns** it.
- One bug is an **inverted** comparison for access control.

---

## References

- Written: `content/Week2-Python-Java/written/3-wednesday/`
- Demo: `content/Week2-Python-Java/demos/3-wednesday/`
