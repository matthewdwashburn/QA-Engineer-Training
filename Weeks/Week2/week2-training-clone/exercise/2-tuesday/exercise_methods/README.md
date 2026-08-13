# Exercise: Calculator Class with Methods

**Mode:** Implementation (Code Lab)  
**Duration:** 60–75 minutes  
**Day:** 2-tuesday | **Week:** 2 — Python & Java Foundations  

---

## Objective

- Implement **`Calculator`** with **static** methods: `add`, `subtract`, `multiply`, `divide`.
- Handle **division by zero** without crashing the JVM (return a sensible result or use `OptionalDouble` / error code—pick one approach and document it).
- Add **method overloading**: a second **`add`** that sums **three** `double` values.
- Write a small **`main`** that demonstrates each method.

---

## Prerequisites

| Concept | Source |
|---------|--------|
| Method syntax, static | `written/2-tuesday/method-declaration-and-syntax.md` |
| Parameters, overloads | `written/2-tuesday/method-parameters-and-return-types.md` |
| Invocation | `written/2-tuesday/method-invocation.md` |
| Demo | `demos/2-tuesday/code/DemoMethods.java` |

---

## Core Tasks

1. Open **`starter_code/Calculator.java`** and replace `TODO` stubs.
2. **`divide(a, b)`** — if `b == 0`, follow the strategy described in the file header comment (trainee fills in behavior).
3. Implement **`add(double a, double b, double c)`** alongside **`add(double a, double b)`**.
4. **`main`**: print results for a few sample calls including overload and divide edge case.

---

## Definition of Done

- [ ] `javac Calculator.java && java Calculator` runs without exception.
- [ ] Two **`add`** overloads compile and are both called from `main`.
- [ ] Division by zero is handled per your documented strategy.

---

## Stretch

- Add **`pow(base, exp)`** for non-negative integer `exp` using a loop (no `Math.pow` required).
- Add Javadoc on each public method (`@param`, `@return`).

---

## References

- Written: `content/Week2-Python-Java/written/2-tuesday/`
- Demo: `content/Week2-Python-Java/demos/2-tuesday/code/DemoMethods.java`
