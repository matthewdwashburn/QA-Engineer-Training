# Error, Defect, and Failure

## Learning Objectives

By the end of this reading you will be able to:

- Define **error**, **defect**, and **failure** precisely using ISTQB and ISO/IEC/IEEE standard definitions.
- Trace the **chain** from human error → defect introduction → observable failure.
- Explain why a defect can exist in code **without ever producing a visible failure**.
- Use these terms correctly in **bug reports, incident reviews, and team communication** — and explain why precision matters.

---

## Why This Matters

These three words — error, defect, failure — are frequently used interchangeably in casual conversation. "The login is buggy." "There's an error in checkout." "The system failed." In professional quality engineering, they have specific, distinct meanings that carry different implications for:

- **Who is responsible for the fix** (the developer fixing a defect is different from the process improvement that prevents the originating error).
- **What the tester is reporting** (a failure observed in execution; a defect inferred from analysis).
- **How root cause analysis is structured** (failures are symptoms; defects are diagnoses; errors are root causes).
- **How metrics are interpreted** ("defect density" and "failure rate" are different measurements).

Using precise language in bug reports and incident discussions makes you easier to work with, faster to communicate, and more credible as a quality professional.

---

## The Concept

### Three Definitions

**Error (Mistake):**
An **error** is a **human action** that produces an incorrect result. Errors occur in the minds and actions of people — developers, analysts, designers, product owners, testers.

An error is the **root cause** — the human action that introduced a problem into an artifact.

Examples:
- A developer misunderstands the requirement ("the spec says tax-inclusive, but I coded tax-exclusive").
- A business analyst writes an ambiguous acceptance criterion that two developers interpret differently.
- A tester uses the wrong test data and records a false positive.
- A deployment engineer configures the wrong environment variable during release.

Errors cannot be directly "fixed" — they occurred in the past. What can be improved is the **process that allowed the error** to propagate into a defect (better code review, clearer requirements, more explicit acceptance criteria).

---

**Defect (Bug, Fault):**
A **defect** is a **flaw in a work product** — code, design document, configuration file, specification, or any other artifact — that can cause the system to behave incorrectly **when that artifact is used or executed**.

A defect is the **consequence of an error** that was not caught before it was embedded in an artifact.

Key property: **A defect can exist in code without ever causing a visible failure**, if the defective code path is never triggered under the conditions of execution or testing.

Examples:
- An off-by-one error in a loop (`i < n` instead of `i <= n`) — the defect is in the code whether or not tests trigger it.
- A missing null check that will cause a NullPointerException if a null value reaches that code path.
- A wrong formula in a configuration file for calculating VAT in a specific region — the defect is there even if no transactions from that region have been processed yet.
- A hardcoded URL pointing to a staging service — works in staging, defect lies dormant until it hits production.

Defects are what developers **fix**. A defect report (bug ticket) should describe the defect location and nature, not just the observed failure.

---

**Failure:**
A **failure** is an **observable deviation of the delivered service from the specified or expected behavior**. It is what testers (and users) **observe** — a crash, a wrong result, a missing field, an incorrect error message, an unexpected redirect, a timeout.

A failure is the **symptom** — the visible evidence that a defect has been triggered under specific conditions.

Examples:
- The system crashes with a NullPointerException when a user submits a registration form with no middle name entered.
- The checkout total shows £110 instead of £100 (tax calculation defect triggered with a specific product category).
- The login page returns an HTTP 500 error when a password contains a special character.
- An order confirmation email is sent to the wrong customer (ID collision defect triggered under concurrent load).

Failures are what testers **detect and report**. A failure report (bug ticket) should describe the failure precisely and provide steps to reproduce it reliably.

---

### The Chain: Error → Defect → Failure

```
Human commits an ERROR
        ↓
A DEFECT is embedded in a work product
        ↓
(May remain latent for days, months, or years)
        ↓
Specific conditions trigger the defect
        ↓
A FAILURE is observed
```

**Not every defect leads to a visible failure immediately:**
- **Latent defects** wait for specific conditions — a particular input combination, an unusual system state, a specific time (DST, end of month, leap year), or an unusual load level.
- A defect in an error handler may only become a failure when the error condition it handles actually occurs.
- A defect in a rarely-used admin function may exist for months before any administrator happens to trigger it.

This is one of the reasons testing cannot prove absence of defects (Principle 1): even perfect test execution cannot guarantee that latent defects will surface under testing conditions.

**Not every failure is caused by a defect in the current system:**
Failures can also result from:
- **Infrastructure problems** (database disk full, network partition).
- **External dependency failures** (payment gateway returning unexpected errors).
- **Test environment issues** (wrong configuration, missing test data).
- **Test defects** (the test itself has a wrong expected value — the test fails, but the system is actually correct).

This is why testers verify failures carefully before reporting them as defects — not every observed deviation is a software defect.

---

### Where Each Term Belongs in Professional Communication

**In a bug report:**

| Section | Which term | Example |
|---------|-----------|---------|
| Title / Summary | **Failure** (observable behavior) | "Checkout total shows incorrect amount when VAT-exempt product is in cart" |
| Steps to reproduce | **Failure** (what was observed) | "Actual: £110. Expected: £100." |
| Root cause analysis (if done) | **Defect** (code/artifact flaw) | "Tax calculation function uses gross pricing for all items, ignoring the VAT-exempt flag." |
| Post-mortem / retrospective | **Error** (human action) | "Developer misread the pricing specification — requirement ambiguity was not caught in Three Amigos." |

**In a defect count metric:**
"Defect density" = defects found per module or per KLOC. This is a code quality metric.
"Failure rate" = how often a system fails under operation. This is a reliability metric.
Confusing the two produces meaningless data.

**In an incident review (production failure):**
1. We observed a **failure** at 14:32 UTC: checkout returning 500 errors.
2. The **defect** was identified: null check missing on the coupon service response when the coupon database is unreachable.
3. The **error** that introduced the defect: the coupon service timeout scenario was not included in the API contract specification, so it was never designed for.
4. **Process improvement:** Add "external service unavailable" scenarios to the Definition of Ready for stories with third-party integrations.

---

### The Tester's Role in Each Category

**Detecting failures:**
Testers are primarily **failure detectors** — they observe and document observable deviations from expected behavior. They do this through planned test execution, exploratory testing, and monitoring.

**Inferring defects:**
Skilled testers go further: they **analyze failures to infer the likely defect location and type**. "The failure occurs only with null values in the middle name field — the defect is likely a missing null check in the registration service." This analysis speeds up developer investigation and produces better bug reports.

**Understanding errors:**
Understanding the human errors that cause defects helps testers contribute to **prevention**. If defects consistently result from ambiguous acceptance criteria, advocating for better Three Amigos practices addresses the root error type.

---

## Worked Example: Leap Year Latent Defect

**The error:**
A developer writes the batch job that processes scheduled reports. The job runs at midnight. The developer writes: `if date.day == 28 and date.month == 2:` (checking for the last day of February). This is wrong — February can have 29 days in a leap year.

**The defect:**
The code contains the wrong condition. The defect is embedded on the day it is committed. Every code review, every test run, every deployment — the defect exists.

**The latent period:**
Tests run in January 2023. Staging environment. February 28 comes and goes — the batch job runs, the condition evaluates correctly, no failure. March, April... The defect is silent.

**The failure (February 29, 2024 — a leap year):**
The batch job runs at midnight on February 28, 2024. The condition `date.day == 28 and date.month == 2` is True. The job executes incorrectly — it processes one day early. On February 29, the condition is False. The batch job does not run. Reports are not generated for February 29. Customers notice missing reports on March 1.

**The chain summarized:**
- **Error:** Developer did not account for leap year logic when writing the date condition.
- **Defect:** The condition `date.day == 28 and date.month == 2` was wrong — it existed for 13 months before it caused a failure.
- **Failure:** Reports were not generated for February 29, 2024. Observable by customers.

**Preventive test design:**
This latent defect is precisely what **time-based error guessing** targets: "Does this code behave correctly at February 28/29, DST transitions, year-end, etc.?" A test with a leap year date (`2024-02-29`) as the execution date would have triggered the failure 13 months earlier — in a test environment, not in production.

---

## Summary

- **Error** = human mistake (root cause; occurred in the past; addressed by process improvement).
- **Defect** = flaw in an artifact (intermediate artifact; embedded by an error; fixed by a developer).
- **Failure** = observable deviation from expected behavior (symptom; detected by testers and users).
- The chain is: **Error → Defect → Failure** (with a potentially long latent period between defect embedding and failure observation).
- Not every defect produces a visible failure — latent defects wait for specific triggering conditions.
- Testers **observe failures, infer defects, and understand errors** — using all three levels of understanding improves bug reports, incident analysis, and prevention.

---

## Additional Resources

- [ISTQB Glossary — Defect / Failure / Error](https://glossary.istqb.org/) — Canonical definitions from the official ISTQB standard.
- [IEEE 1044 — Classification of Software Anomalies](https://standards.ieee.org/) — Formal taxonomy for software problems (organization access may be required).
- [ISTQB Foundation Syllabus — Section 1.1](https://www.istqb.org/) — Testing concepts including the error/defect/failure chain.
- `jira-issues.md` (Friday) — Translating observed failures into well-documented, actionable Jira bug tickets.
- `defect-lifecycle.md` (Thursday) — The workflow that manages defects from discovery to closure.
