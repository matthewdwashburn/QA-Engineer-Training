# Interview Questions: Week 5 — Agile & Testing Foundations

Self-study bank aligned with `content/Week5-Agile-Testing/written/`. Try answering aloud before opening the hidden answers.

**Difficulty mix (target):** ~70% beginner · ~25% intermediate · ~5% advanced.

---

## Beginner (Foundational)

### Q1. Name the typical phases of the Waterfall software lifecycle in order.

**Keywords:** requirements, design, implementation, testing, maintenance, sequential

<details>
<summary>Click to Reveal Answer</summary>

Waterfall commonly runs **requirements → design → implementation → testing → maintenance** as a **linear sequence**, with each phase largely completing before the next begins. (Exact naming can vary by organization, but the forward-only chain is the idea.)

</details>

---

### Q2. What are the four values stated in the Agile Manifesto (the “left side” items)?

**Keywords:** individuals and interactions, working software, customer collaboration, responding to change

<details>
<summary>Click to Reveal Answer</summary>

The Manifesto values **individuals and interactions**, **working software**, **customer collaboration**, and **responding to change**—while still acknowledging value in the items on the right (processes/tools, documentation, contracts, plans).

</details>

---

### Q3. In Scrum, what are the three accountabilities (roles), and what does the Product Owner optimize for?

**Keywords:** Product Owner, Scrum Master, Developers, value, Product Backlog

<details>
<summary>Click to Reveal Answer</summary>

The three Scrum accountabilities are **Product Owner**, **Scrum Master**, and **Developers**. The **Product Owner** is accountable for **maximizing the value** of the product resulting from the Scrum Team’s work and owns **ordering** and clarity of the **Product Backlog**.

</details>

---

### Q4. List the five formal Scrum events and give the time-box for the Daily Scrum (for a one-month Sprint).

**Keywords:** Sprint Planning, Daily Scrum, Sprint Review, Sprint Retrospective, Sprint container, 15 minutes

<details>
<summary>Click to Reveal Answer</summary>

The Scrum events are **Sprint Planning**, **Daily Scrum**, **Sprint Review**, and **Sprint Retrospective**, all occurring **within** the **Sprint** (which is itself a container event, one month or less). The **Daily Scrum** is time-boxed to **15 minutes** for a one-month Sprint (often shorter when the Sprint is shorter).

</details>

---

### Q5. What is a story point, and what is team **velocity**?

**Keywords:** relative, estimation, Fibonacci, Done, story points per Sprint

<details>
<summary>Click to Reveal Answer</summary>

**Story points** estimate **relative** size/complexity/uncertainty of backlog items compared to each other—not “hours in disguise.” **Velocity** is the sum of story points **completed** to the **Definition of Done** in a Sprint; averaged over time it helps forecast capacity.

</details>

---

### Q6. Explain the difference between **Quality Assurance (QA)** and **Quality Control (QC)**.

**Keywords:** process, preventive, product, detective, testing

<details>
<summary>Click to Reveal Answer</summary>

**QA** is **process-oriented** and **preventive**—improving how work is done (standards, CI policy, DoD, retros). **QC** is **product-oriented** and **detective**—inspecting outcomes (running tests, reviews of deliverables, defect reporting). Both matter; the labels describe different emphases.

</details>

---

### Q7. What is the difference between **verification** and **validation** (use the classic questions)?

**Keywords:** building the product right, building the right product, specification, value

<details>
<summary>Click to Reveal Answer</summary>

**Verification** asks **“Are we building the product right?”**—conformance to specs, designs, and contracts. **Validation** asks **“Are we building the right product?”**—fitness for user/business purpose and real-world usefulness.

</details>

---

### Q8. State two of the seven testing principles from the curriculum (any two).

**Keywords:** exhaustive impossible, early testing, defect clustering, pesticide paradox, context dependent, absence-of-errors fallacy, presence of defects

<details>
<summary>Click to Reveal Answer</summary>

Any **two** of these (wording may vary slightly): **(1)** Testing shows **presence of defects**, not their absence. **(2)** **Exhaustive** testing is impossible. **(3)** **Early** testing saves cost. **(4)** **Defect clustering**. **(5)** **Pesticide paradox** (repeating same tests loses power). **(6)** Testing is **context dependent**. **(7)** **Absence-of-errors** fallacy (perfect code can still miss the market).

</details>

---

### Q9. What is the difference between a **functional** and a **non-functional** requirement? Give one example of each.

**Keywords:** behavior, quality attribute, performance, measurable

<details>
<summary>Click to Reveal Answer</summary>

**Functional** requirements describe **what the system does** (behaviors, features, rules). **Non-functional** requirements describe **how well** it must do it—**quality attributes** like performance, security, availability—ideally with **measurable** criteria (e.g., latency under load).

</details>

---

### Q10. In the chain **error → defect → failure**, define each term.

**Keywords:** human mistake, flaw, observable, latent

<details>
<summary>Click to Reveal Answer</summary>

An **error** is a **human mistake** that can introduce a problem. A **defect** is a **flaw** in a work product (code, config, document) that **may** cause wrong behavior. A **failure** is the **observable** incorrect behavior seen by a user or test. A defect can exist **without** an immediate failure (**latent** defect).

</details>

---

### Q11. What is **statement coverage**, and why does 100% statement coverage **not** guarantee a correct program?

**Keywords:** executable lines, executed, oracle, requirements

<details>
<summary>Click to Reveal Answer</summary>

**Statement coverage** measures the **fraction of executable statements** that ran under a test suite. **100%** only means lines were **hit**—not that assertions match **real requirements**, that logic is **correct**, or that all **bugs** are found (weak **oracles**, wrong specs, integration gaps).

</details>

---

## Intermediate (Application)

### Q12. Your burndown shows **flat** remaining work for three days mid-sprint. What **Scrum** conversations and **test** questions would you raise?

**Keywords:** Daily Scrum, impediment, environment, scope, transparency

**Hint:** Think about what a burndown measures and who removes blockers.

<details>
<summary>Click to Reveal Answer</summary>

Raise it in the **Daily Scrum** as a **progress** issue: identify **impediments** (environment down, unclear acceptance criteria, dependencies, underestimated work, **scope** creep). For testing specifically, ask whether **blockers** are **environment/data**, **flaky automation**, or **stories** not truly **ready**. The Scrum Master helps escalate; the team adapts the **Sprint Backlog** transparently—burndown is a **signal**, not blame.

</details>

---

### Q13. A Product Owner says, “We’re Agile, so we don’t need written acceptance criteria.” How do you respond constructively?

**Keywords:** shift-left, testable, Definition of Done, collaboration, working software

<details>
<summary>Click to Reveal Answer</summary>

Agile favors **working software** and **collaboration** over **waste**, not “no documentation.” **Testable acceptance criteria** are **shift-left**: they reduce rework, clarify **oracles**, and support **whole-team quality**. Propose **lightweight**, **living** criteria (examples, Given/When/Then) tied to **Definition of Done**—enough to **verify** increments without heavyweight specs.

</details>

---

### Q14. When would you choose **exploratory testing** over a fully **scripted** regression pack for a feature?

**Keywords:** charter, time box, learning, new feature, pesticide paradox

<details>
<summary>Click to Reveal Answer</summary>

Use **exploratory** when **learning** is high: **new** features, unclear risks, complex workflows, or after automation finds nothing new (**pesticide paradox**). Structure it with a **charter**, **time box**, notes, and **debrief**—not random clicking. Keep **scripted** regression for **stable** critical paths that need **repeatable** signals.

</details>

---

## Advanced (Deep Dive)

### Q15. You have strong **branch coverage** on a module but keep shipping **production** bugs in the same area. Using the week’s concepts, what **five** angles would you investigate (not just “write more tests”)?

**Keywords:** validation, requirements, defect clustering, oracle, automation pyramid, data, observability

<details>
<summary>Click to Reveal Answer</summary>

Sample strong answer (any **five** of these angles): **(1)** **Validation** gap—building the “right” thing? **(2)** **Requirements/AC** wrong or incomplete (**absence-of-errors** fallacy). **(3)** **Oracles** in tests wrong or **too shallow** (passing tests, failing users). **(4)** **Integration**/environment/**data** differences vs prod. **(5)** **Missing layers** in the **automation pyramid** (over-relying on UI, weak API/unit signals). **(6)** **Operational** failures (**observability**, config, timing) not exercised. **(7)** **Defect clustering** suggests **refactor**/risk-based **exploratory** focus and **root-cause** in process (**QA**), not only more cases.

</details>

---

## Topic map (for coaches)

| Q | Primary topics |
|---|----------------|
| 1 | Waterfall |
| 2 | Agile Manifesto |
| 3–5 | Scrum, estimation, charts |
| 6–7 | QA/QC, V&V |
| 8–9 | Principles, requirements |
| 10–11 | Defect chain, coverage |
| 12–14 | Burndown/Scrum, Agile testing practice, exploratory vs scripted |
| 15 | Systems thinking, quality strategy |
