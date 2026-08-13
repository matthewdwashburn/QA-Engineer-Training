# Agile and Scrum Processes

## Learning Objectives

By the end of this reading you will be able to:

- Name the three **Scrum accountabilities** and explain what each role optimizes for.
- Describe the three main **Scrum artifacts** and their associated commitments.
- Trace how a **Sprint** converts Product Backlog items into a usable, "Done" increment.
- Explain what the **Definition of Done** means and why it matters to testers.

---

## Why This Matters

The word "Agile" describes a mindset and a set of values (from the Manifesto). **Scrum** is the most widely adopted *framework* that implements those values in practice. It is the system your team probably runs, your job ads reference, and your clients expect you to navigate.

Testers who understand Scrum at a structural level know **who decides what, where work is recorded, how commitments are made**, and how to participate professionally in delivery. This is the scaffolding on which everything else this week — ceremonies, test planning, defect management — hangs.

---

## The Concept

### What Is Scrum?

**Scrum** is a **lightweight framework** for developing complex products through **empirical process control** — meaning you learn by doing, not by theorizing. It rests on three pillars:

- **Transparency** — Everyone can see the state of work, the backlog, the impediments.
- **Inspection** — The team regularly examines progress, the product, and the process.
- **Adaptation** — Based on inspection, the team changes what needs to change.

Scrum is deliberately **minimal**: it defines three accountabilities (roles), three artifacts, five events, and a handful of rules. Everything else is left to the team to figure out.

> Important: Scrum sits inside the broader **Agile** values from the Manifesto. All Scrum teams are Agile; not all Agile teams use Scrum.

---

### The Three Scrum Accountabilities

Scrum uses the word "accountability" rather than "role" deliberately — each person is *accountable for specific outcomes*, not just a job title.

#### 1. Product Owner (PO)

The Product Owner is accountable for **maximizing the value of the product** resulting from the Scrum Team's work.

In practice, the PO:
- **Owns and orders the Product Backlog** — deciding what to build and in what priority.
- **Communicates the Product Goal** — the longer-term objective the product is working toward.
- Writes or approves **acceptance criteria** for backlog items so developers and testers know when something is "done."
- **Accepts or rejects completed work** based on whether it meets the Definition of Done and intended value.
- Is the single decision-maker on product direction — not a committee.

**Tester relationship with the PO:**
- The tester's primary question at refinement: "Can you help me understand what *done* looks like for edge case X?"
- The tester's primary contribution at sprint review: "Here is what we tested, what we found, and what risk remains."

The PO is your quality partner on the **"are we building the right thing?"** side of testing (validation).

---

#### 2. Scrum Master (SM)

The Scrum Master is accountable for **the Scrum Team's effectiveness** — helping the team and the organization use Scrum well.

The Scrum Master:
- Coaches the team on Scrum events, artifacts, and rules.
- Removes **impediments** — blockers that slow the team down (broken CI, waiting for access, unclear requirement).
- Facilitates Scrum events effectively.
- Helps the organization understand and support Scrum (e.g., protecting the team from mid-sprint scope changes).
- Serves the team — this is a **leadership role**, not a management role. The SM has no authority to assign work.

**Tester relationship with the SM:**
- Raise test environment blockers to the SM: they can escalate and resolve faster.
- Work with the SM to get testing activities included in the team's Definition of Done and planning capacity.
- Use retrospectives (facilitated by the SM) to surface test process improvements.

---

#### 3. Developers

"Developers" in Scrum means every professional who works to create any aspect of the usable Increment each Sprint. This explicitly includes **testers, designers, database administrators, DevOps engineers** — not only software engineers.

The Developers:
- Create a **plan for the Sprint** (the Sprint Backlog).
- Instil quality by **adhering to the Definition of Done**.
- Adapt the Sprint Backlog daily based on progress toward the Sprint Goal.
- Hold each other **mutually accountable** as professionals.

In Scrum there are **no sub-teams**. There is one cohesive Scrum Team — testers are not a separate testing team that receives handoffs. They are equal contributors to the Sprint Goal.

**Optimal team size:** 10 or fewer, including the PO and SM. Smaller teams communicate better.

---

### The Three Scrum Artifacts and Their Commitments

Each artifact has an associated **commitment** — a focus point that gives the team a clear target:

| Artifact | Purpose | Commitment |
|----------|---------|------------|
| **Product Backlog** | Ordered list of everything needed for the product. | **Product Goal** — The long-term objective the product is moving toward. |
| **Sprint Backlog** | Plan for the current Sprint: selected items + how to deliver them. | **Sprint Goal** — Why this Sprint is valuable; a focused objective. |
| **Increment** | The sum of all "Done" work — usable, valuable output of the Sprint. | **Definition of Done** — Quality bar every item must meet before it counts. |

---

#### Product Backlog

The **Product Backlog** is an **ordered, emergent** list of what the product needs. It is never "complete" — it evolves as the team learns and as the market changes.

Items at the **top of the backlog** are small, well-defined, and ready to be pulled into a Sprint (often called **sprint-ready** or **refined**). Items deeper in the backlog may be large epics or vague ideas — they will be broken down and clarified as they rise in priority.

**Backlog refinement** (not an official Scrum event, but a regular team activity) is where items are:
- Discussed and clarified.
- Estimated (in story points or similar).
- Split from large epics into sprint-sized stories.
- Acceptance criteria added.

This is where testers contribute most to quality prevention — by raising questions before a single line of code is written.

---

#### Sprint Backlog

The **Sprint Backlog** is the team's plan for the Sprint. It contains:
- The selected Product Backlog items for the Sprint.
- The Sprint Goal (why this Sprint matters).
- A plan for how to deliver the selected items (often task-level breakdowns).

The Sprint Backlog is **owned by the Developers** — they decide how to organize and execute the work. The PO should not add new items to the Sprint Backlog mid-sprint without discussion and agreement.

**Tester contribution:**
- Break down test activities as tasks in the Sprint Backlog: "Write test cases for Story 42," "Set up test data for Story 45," "Execute exploratory session — 2 hours."
- Make testing work **visible** on the board — not hidden behind a single "In Testing" column.

---

#### Increment

The **Increment** is the sum of all completed Product Backlog items that meet the **Definition of Done** by the end of the Sprint. It must be **usable** — meaning it could theoretically be released to users, even if the team decides not to release yet.

Multiple Increments may be created within a Sprint. The Increment is the **output that demonstrates empirical progress**.

**The Definition of Done (DoD):**

The DoD is the **quality contract** for the Increment. Any item that does not meet the DoD is **not "Done"** — it stays in the backlog.

A sample DoD might include:

```
✅ All acceptance criteria verified
✅ Unit tests written and passing
✅ No critical (P1) defects open
✅ Code peer-reviewed
✅ Feature integrated into main branch and deployable to staging
✅ Demonstrated to Product Owner
✅ Regression suite passing
✅ Release notes or documentation updated if applicable
```

Testers help **define** the DoD (especially quality-related items), **enforce** it (don't let stories be called done without it), and **verify** it on every sprint.

---

### The Sprint Structure

A **Sprint** is a **fixed-length event** of one month or less during which the Scrum Team turns selected backlog items into a Done, usable Increment.

Key rules:
- Sprints have **consistent lengths** — one week, two weeks, or one month.
- No changes should be made that **endanger the Sprint Goal** mid-sprint.
- **Scope may be clarified and renegotiated** between the PO and Developers if new learning demands it — but the goal stays stable.
- A Sprint can be **cancelled** by the PO if the Sprint Goal becomes obsolete (rare).

Inside each Sprint, four formal events occur. These are covered in depth in `scrum-ceremonies.md`.

---

## Worked Example: A Tester's View of the Sprint Flow

Imagine a Sprint with the Goal: *"Users can search for and filter products by category."*

**Sprint Planning (Day 1):**
- Tester reviews Story 67 (Product Search): "What should happen if the search query is empty? Should it return all results or nothing?"
- PO clarifies: "Empty query should show all products, sorted by bestseller."
- Tester adds task: "Set up test data: 200 sample products across 5 categories in the test environment."

**Days 2–6 (Development + Testing in parallel):**
- Developer builds the search API endpoint.
- Tester writes test cases for Story 67 using the refined acceptance criteria.
- CI pipeline runs unit tests on every commit — immediate feedback.
- Tester runs an exploratory session on the Category Filter feature completed in Day 4 — finds that selecting multiple categories returns an empty result (bug). Developer fixes same day.

**Days 7–9:**
- Tester executes planned test cases for Story 67.
- Integration test: search + filter working together. One edge case fails (search with special characters). Bug logged, fixed, re-tested in Day 9.
- Regression suite run — all passing.

**Sprint Review (Day 10):**
- Tester helps demonstrate: "Here is the search with an empty query — showing all products. Here is filtering by two categories simultaneously. Here is what happens with a search for products that don't exist."
- Stakeholder feedback: "Can we add a 'No results found' message with suggestions?"
- Tester notes this as a new backlog item to be refined before next sprint.

**Sprint Retrospective:**
- Tester raises: "Test environment was unstable on Day 3 — lost half a day. Can we add an environment health check to our morning routine?"
- Team agrees to add it. Scrum Master captures as an action item.

This is **how a tester lives inside the Scrum framework** — not as a phase at the end, but as a continuous contributor to the Sprint Goal.

---

## Summary

- Scrum has **three accountabilities**: Product Owner (product value), Scrum Master (team effectiveness), Developers (creating the Done Increment — including testers).
- **Three artifacts** provide transparency: Product Backlog (Product Goal), Sprint Backlog (Sprint Goal), Increment (Definition of Done).
- A **Sprint** is a fixed-length cycle producing a Done, usable Increment through planning, daily work, review, and retrospective.
- The **Definition of Done** is the team's quality contract — testers help define, enforce, and verify it.

---

## Additional Resources

- [Scrum Guide (2020)](https://scrumguides.org/scrum-guide.html) — The authoritative, free definition of Scrum.
- [Scrum Glossary](https://www.scrum.org/resources/scrum-glossary) — Quick reference for Scrum terms.
- [Agile Manifesto](https://agilemanifesto.org/) — Values underpinning the Scrum framework.
- `scrum-ceremonies.md` — Detailed breakdown of Sprint Planning, Daily Scrum, Sprint Review, and Sprint Retrospective.
