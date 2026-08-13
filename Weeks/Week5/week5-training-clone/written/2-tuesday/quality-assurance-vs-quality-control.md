# Quality Assurance vs Quality Control

## Learning Objectives

By the end of this reading you will be able to:

- Precisely contrast **QA** (Quality Assurance) and **QC** (Quality Control) using the process vs product distinction.
- Give multiple **concrete software examples** of each.
- Explain why **both** are needed in Agile teams and how QC signals drive QA improvements.
- Correct the common industry misconception that all testers do is "QA."

---

## Why This Matters

In the software industry, the term "QA" is used carelessly. Job postings advertise for "QA Engineers" whose work is almost entirely test execution — which is technically **Quality Control**. This matters for several reasons:

- **Career understanding:** If you know the distinction, you can advocate for your role, explain your work accurately in interviews, and identify where you want to grow.
- **Investment decisions:** When quality is poor, leaders need to know: should we add more testing (QC) or fix the process (QA)? Without the distinction, they often choose more testing as the answer to problems that more testing alone cannot solve.
- **Blame avoidance:** When a defect escapes to production, teams sometimes blame "QA." But if the defect escaped because a requirement was never reviewed or an edge case was never discussed, the problem is a QA failure, not a QC failure.

---

## The Concept

### The Fundamental Distinction

| | **Quality Assurance (QA)** | **Quality Control (QC)** |
|---|---------------------------|--------------------------|
| **Focus** | *How* the team works | *What* the team produced |
| **Primary intent** | Prevent defects from being created | Detect defects in what was created |
| **When it happens** | Before and during creation | During and after creation |
| **Orientation** | Process-improvement | Product-inspection |
| **Analogy** | Building a better kitchen and training chefs | Tasting the dish before it goes to the customer |

Neither is more important than the other — both are essential. A team that only does QC (testing, reviews) without QA (process improvement) will find the same types of defects sprint after sprint. A team that only does QA (processes, training) without QC will have no evidence that their processes are actually working.

---

### Quality Assurance in Software: What It Actually Looks Like

QA is about designing and improving the **system that creates software** so that defects are less likely to be introduced. It is largely invisible when working well — you do not see the bugs that were never written.

**Process-level QA activities:**

**1. Acceptance criteria standards in refinement**
Requiring that every user story has testable, specific acceptance criteria before it enters a sprint is a QA activity. When this standard is consistently applied, developers write code against clear specifications — and whole categories of "I misunderstood the requirement" defects disappear.

**2. Definition of Done (DoD) enforcement**
A DoD that includes "unit tests written and passing," "code reviewed," and "no open P1 defects" is a QA mechanism. It prevents incomplete or unreviewed work from being called Done.

**3. Mandatory code review before merge**
Requiring that a second developer reviews every pull request before it merges to the main branch is a QA activity. It catches logic errors, missing error handling, and security issues before they reach testing.

**4. Branch protection rules and CI gates**
Configuring git repositories so that code cannot be merged unless automated tests pass is a QA mechanism. It prevents broken builds from propagating.

**5. Blameless retrospectives and postmortems**
When a production incident occurs, running a blameless postmortem to find the root cause (not the person to blame) and implementing process changes is QA. If a certain type of defect keeps escaping, adding it to the Three Amigos template or code review checklist is QA.

**6. Training, pairing, and knowledge sharing**
Testers sharing test design knowledge with developers (so developers write better unit tests), or architects sharing design patterns that prevent security vulnerabilities — these are QA activities that raise the team's collective capability.

**QA example in practice:**

A team notices three consecutive sprints with defects related to incorrect currency formatting. In the retrospective, they agree to:
- Add a "currency formatting rules" section to the coding guidelines.
- Create a shared utility function for currency formatting so the logic is written once.
- Add "currency edge cases" to the Three Amigos checklist for all financial features.

Next sprint, zero currency formatting defects. That is QA success — invisible in the test results, but real in outcome.

---

### Quality Control in Software: What It Actually Looks Like

QC is about examining what was built — the code, the running system, the documentation — and determining whether it meets defined quality standards.

**Product-level QC activities:**

**1. Unit testing**
Running automated tests against individual functions or classes to verify they produce expected outputs. These tests can be run by developers (developer testing) or integrated into a CI pipeline. They detect logic errors early and cheaply.

**2. Integration testing**
Testing that separate components work correctly when combined. Detects interface mismatches, incorrect data formats between services, and assumptions that were not explicitly agreed.

**3. System testing**
Testing the complete, integrated system against functional requirements and acceptance criteria. This is where most formal "QA tester" work lives — executing test cases, reporting defects.

**4. Exploratory testing**
Testers using skill, experience, and intuition to investigate the system — not following a script, but actively looking for unexpected behaviors. This detects defects that scripted tests miss.

**5. Performance and load testing**
Running the system under realistic or extreme load to detect latency, throughput, and resource issues that would not appear in functional testing.

**6. Security testing**
Scanning code for vulnerabilities, running penetration tests, and attempting to exploit known attack vectors. Detects security defects before malicious actors find them.

**7. UAT (User Acceptance Testing)**
Having real users or business representatives exercise the system to validate that it meets their actual needs. Detects misalignment between technical implementation and user expectations.

**QC example in practice:**

During system testing, a tester runs TC-CART-013: "Add an item to the cart, apply a 20% coupon, then remove the item." Expected result: cart shows empty with $0.00 total. Actual result: cart shows empty but the coupon discount is still shown as "-$12.40," making the total -$12.40. This is a defect detected by QC that would cause financial errors in production if released.

---

### How QC Signals Drive QA Improvements

The most powerful quality management teams treat **QC findings as inputs to QA improvements** — not just items to fix and move on.

**The signal-and-response model:**

```
QC finds recurring defect type
        ↓
Root cause analysis (retrospective)
        ↓
QA process change (prevent recurrence)
        ↓
Fewer future defects of that type
        ↓
QC confirms lower defect rate
```

**Example: Same Bug, Two Lenses**

**Symptom:** Wrong tax calculation in checkout. A tester finds it in system testing.

**QC response (immediate fix):**
- Tester files defect with reproduction steps.
- Developer fixes the calculation logic.
- Tester verifies the fix.
- Defect closed.

**QA response (preventing recurrence):**
- Retrospective asks: "Why did AC not specify the tax calculation formula? Why didn't Three Amigos surface this?"
- Root cause: The story was written without involving the finance domain expert.
- QA change: Add "involve finance domain expert for any story involving monetary calculations" to the refinement checklist.
- QA change: Create a unit test template for tax calculation that all developers must follow.
- QA change: Add tax calculation scenarios to the automated regression suite so future regressions are caught in CI.

The QC fix prevented that specific defect in production. The QA response prevents the whole class of similar defects in future sprints.

---

### The Agile Perspective: Testers Contribute to Both

In Scrum, the whole team shares quality accountability. Testers — as quality specialists — contribute to both QA and QC:

**Tester QC contributions:**
- Writing and executing test cases.
- Running exploratory sessions.
- Triaging and documenting defects.
- Verifying bug fixes.
- Running regression suites.

**Tester QA contributions:**
- Facilitating Three Amigos conversations.
- Reviewing requirements and acceptance criteria for testability.
- Updating the DoD when quality gaps are found.
- Leading retrospective quality discussions.
- Building and maintaining automated test suites (prevention of regression escapes).
- Sharing test knowledge with developers (improving their ability to write better unit tests).

A tester who only does QC is doing half their job. A tester who balances QC execution with QA improvement activities is genuinely raising the team's quality capability.

---

## Worked Example: Contrasting QA and QC Activities for One Feature

Feature: **User Password Reset Flow**

| Activity | QA or QC? | Description |
|---------|----------|-------------|
| Three Amigos session for password reset story | QA | Prevents ambiguous requirements from reaching coding |
| Review AC: "Token expires after 15 minutes" → clarify timezone handling | QA | Prevents timezone defect from being introduced |
| Developer writes unit test for token expiry logic | QC (detection) | Detects if the expiry logic has a bug |
| Tester runs system test for valid token flow | QC (detection) | Verifies happy path works end-to-end |
| Tester runs negative test: expired token link | QC (detection) | Detects if expired token handling is correct |
| Retrospective: "We keep finding timezone bugs — add to code review checklist" | QA | Prevents future timezone bugs across all features |
| Add password reset scenarios to automated regression suite | QA + QC | Prevents future regression while also detecting current issues |

---

## Summary

- **Quality Assurance (QA)** is process-focused and preventive — it improves *how* the team works so defects are less likely to occur.
- **Quality Control (QC)** is product-focused and detective — it examines *what* was built to find defects before they reach users.
- **Both are necessary:** QC without QA creates a team trapped in a defect detection cycle; QA without QC has no evidence of effectiveness.
- **QC findings should drive QA improvements:** when the same type of defect recurs, the root cause is usually a process gap — which QA addresses.
- Testers contribute to **both** QA (requirements reviews, Three Amigos, DoD, retrospective improvements) and QC (test execution, exploratory testing, defect management).

---

## Additional Resources

- [ASQ — Quality Assurance vs Quality Control](https://asq.org/quality-resources/quality-assurance-vs-control) — The classic QA/QC distinction from the American Society for Quality.
- [ISTQB Glossary](https://glossary.istqb.org/) — Canonical testing vocabulary for QA and QC terms.
- `quality-management.md` — The broader planning and improvement cycle that both QA and QC serve.
