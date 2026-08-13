# SOLID Principles

## Learning Objectives

- Name the five **SOLID** principles and state—in your own words—what design risk each one reduces.
- Spot **common violations** in small examples (bloated classes, giant `if` chains, unsafe inheritance, fat interfaces, hard-coded dependencies).
- Connect **SRP**, **DIP**, and **OCP** to **unit testing**, **test doubles**, and **fewer regression hotspots** in a QA-minded workflow.
- Explain **LSP** and **ISP** well enough to discuss them in **code reviews** without treating every rule as absolute dogma.

---

## Why This Matters

> **Weekly Epic Connection:** Thursday demos often contrast **messy code** with **refactored** versions. **SOLID** is the shared vocabulary in **pull requests**, **architecture discussions**, and **post-incident reviews**. When you say “this class violates **SRP**” or “we need **DIP** here for testability,” engineers know what you mean.

For **QA**, SOLID is not academic decoration. **Tangled responsibilities** and **concrete dependencies** make systems **hard to test in isolation**, **hard to change safely**, and **easy to break** when requirements shift. Recognizing these smells helps you **prioritize review effort**, **write sharper bug reports** (“risk: changing X forces edits across unrelated concerns”), and **advocate** for refactors that reduce **flaky** or **slow** test suites.

---

## The Concept

**SOLID** is a mnemonic from five **object-oriented design** principles, popularized by **Robert C. Martin** and others. They overlap with patterns you will see later; this lesson stays at **principle** level—**what** we protect—without requiring you to memorize full pattern catalogs.

**Analogy — a small repair shop:** **SRP** is “one technician owns brake jobs end-to-end.” **OCP** is “add a new service package without rewiring the whole booking system.” **LSP** is “any certified mechanic can stand in without the car behaving oddly.” **ISP** is “the intake form only asks for what that job needs.” **DIP** is “the scheduler talks to a ‘calendar API,’ not one person’s paper notebook.”

---

### S — Single Responsibility Principle (SRP)

**Idea:** A module (often a **class**) should have **one reason to change**—one **cohesive** job in business or technical terms.

**Why:** When many unrelated concerns live together, a bug fix for **reporting** can accidentally break **email delivery** or **parsing**. Tests become **wide** and **brittle** because “everything touches everything.”

**Violation (smell):** `ReportGenerator` that **parses** JSON, **builds** PDFs, **sends** email, and **logs** to a database. Four different change drivers, one class.

**Direction (not the only design):** Split into collaborators: a **parser**, a **renderer**, a **notifier**, and an **orchestrator** that wires them. Each piece has a **narrower** test surface.

```java
// Smell: one class doing parsing + rendering + transport
public class ReportEverything {
    public void run(String json) { /* parse, render PDF, send email, log */ }
}
```

```python
# Clearer split (illustrative): each type has one primary job
class JsonReportParser:
    def parse(self, raw: str) -> dict: ...

class PdfReportRenderer:
    def render(self, data: dict) -> bytes: ...
```

---

### O — Open/Closed Principle (OCP)

**Idea:** Be **open for extension** (add new behavior) but **closed for modification** of stable core logic—avoid opening the same central file for every new variant and growing a **rat’s nest** of conditionals.

**Why:** Every edit to hot-path code is a **regression** risk. **OCP** pushes **new cases** into **new types** or **plug-ins** that satisfy an existing contract.

**Violation:** `OrderService.applyDiscount(order)` with endless **`if (promoType == …)`** branches that someone edits weekly.

**Direction:** Represent rules as **strategies** (objects implementing a small interface). Adding a promo means **adding a class**, not **surgery** on a 400-line method. (You will see **Strategy** as a named pattern later; here, only the **idea** matters.)

**QA angle:** Feature flags and new business rules land constantly. **OCP-friendly** designs tend to produce **localized diffs**—easier to review and to scope **regression tests**.

---

### L — Liskov Substitution Principle (LSP)

**Idea:** If **`B`** is a subtype of **`A`**, then **anywhere** code expects an **`A`**, substituting a **`B`** must **not** surprise callers—**observable behavior** and **contracts** (preconditions, postconditions, invariants) must hold.

**Why:** Inheritance is not just “reuse code.” Broken subtyping causes **subtle bugs** at runtime when polymorphism swaps implementations.

**Classic pitfall:** `Square extends Rectangle` if **`Rectangle`** allows independent **`width`** and **`height`** but **`Square`** forces **`width == height`**. Code that does `r.setWidth(5); r.setHeight(10);` expects a **5×10** rectangle; a square may **violate** that mental model.

**Less theatrical example:** A `FileReader` base type promises “**read** returns bytes.” A subtype that **throws** unless a network share is mounted may break callers that assumed **local files only**.

**QA angle:** When you see **inheritance**, ask: **Would every subclass honor what callers assume?** LSP violations often show up as **“works with A, fails with B”** in integration tests.

---

### I — Interface Segregation Principle (ISP)

**Idea:** Prefer **small, focused** interfaces. Clients should not **depend on methods they do not use**—and should not be forced to implement **stubs** that throw `UnsupportedOperationException`.

**Why:** A **fat** interface creates **false coupling**. Every change to the mega-interface can **ripple** to unrelated implementers.

**Violation:** `Worker` with `code()`, `attendMeeting()`, `cleanOffice()`, and `driveForklift()`. A **`RemoteDeveloper`** is pressured to implement **`driveForklift()`** as **no-op** or **error**.

**Direction:** Split into `Coder`, `MeetingParticipant`, `FacilitiesHelper`, etc. Types **implement only** what applies.

```java
// Leaner contracts (sketch)
interface CodeContributor { void commitFeature(); }
interface OnCallResponder { void handlePage(); }
// Some classes implement both; others implement one.
```

**QA angle:** **ISP** aligns with **role-based** thinking—easier to **mock** only what the class under test needs.

---

### D — Dependency Inversion Principle (DIP)

**Idea:** High-level policy should depend on **abstractions** (interfaces / protocols), not on **concrete** low-level details (a specific database driver, HTTP client, or `System.currentTimeMillis()`).

**Why:** **Concrete** dependencies are **hard to replace** in tests and **hard to swap** in production (new vendor, read replica, fake clock).

**Pattern direction:** **Constructor injection** or **factories** supply implementations of **`PaymentGateway`**, **`Clock`**, **`UserDirectory`**, etc.

**Java (sketch):**

```java
public class CheckoutService {
    private final PaymentGateway payments;
    private final Clock clock;

    public CheckoutService(PaymentGateway payments, Clock clock) {
        this.payments = payments;
        this.clock = clock;
    }
}
```

**Python (sketch):**

```python
class CheckoutService:
    def __init__(self, payments: PaymentGateway, clock: Clock) -> None:
        self._payments = payments
        self._clock = clock
```

**QA angle:** **DIP** is why you can run **fast unit tests** with **fakes** and **contract tests** against **real** adapters—without rewriting **`CheckoutService`** for each environment.

---

## How the principles reinforce each other

- **SRP** keeps classes **small enough** to reason about; **ISP** keeps **interfaces** honest about what clients need.
- **DIP** makes **SRP** easier: dependencies are **explicit** and **swappable**.
- **OCP** is easier when **extension points** are **clear abstractions** (**DIP**) rather than **copy-paste** branches.
- **LSP** guards **inheritance hierarchies** so polymorphism stays **trustworthy**.

None of this requires **perfect** design on the first commit. It gives you **language** for **incremental** improvement.

---

## SOLID and testing (expanded)

| Principle | Testing payoff (typical) |
|-----------|-------------------------|
| **SRP** | Smaller units, **fewer** setup steps per test, clearer **failure** attribution. |
| **OCP** | New behavior often means **new class + new tests** instead of **rewriting** a fragile mega-test for one giant method. |
| **LSP** | Fewer **“passes on base type, fails on subtype”** surprises when tests use **polymorphism**. |
| **ISP** | **Narrow fakes**: implement only the methods the unit under test calls. |
| **DIP** | **Inject** test doubles, **freeze time**, **stub** HTTP/DB; enable **contract** tests on **adapters**. |

---

## A practical note: principles, not religion

Real codebases **balance** SOLID with **shipping**, **team skill**, and **YAGNI** (“you aren’t gonna need it”). Over-abstracting too early can hurt readability. Use SOLID to **name problems** and **guide refactors** when **pain** appears—**untestable** code, **merge conflicts** in one file every sprint, **subtypes** that behave oddly.

---

## Summary

- **SRP:** One **cohesive** job per module; fewer accidental cross-feature breaks.
- **OCP:** Prefer **extending** with new types over **editing** a growing conditional core.
- **LSP:** Subtypes must **honor** base-type **expectations**—inheritance is a **behavioral** contract.
- **ISP:** **Small** interfaces so clients do not depend on **unused** obligations.
- **DIP:** Depend on **abstractions** so production and **tests** can **swap** implementations.

---

## Additional Resources

- [SOLID (Wikipedia)](https://en.wikipedia.org/wiki/SOLID) — compact overview and history of the acronym.
- [Oracle: Interfaces and Inheritance](https://docs.oracle.com/javase/tutorial/java/IandI/index.html) — Java mechanics that support **ISP** and **DIP**.
- Robert C. Martin, *Agile Software Development, Principles, Patterns, and Practices* — optional book depth on these principles in context.
