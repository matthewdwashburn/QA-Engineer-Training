# Introduction to Agile

## Learning Objectives

By the end of this reading you will be able to:

- Describe the historical **context** that led to the Agile Manifesto in 2001.
- Summarize the **four values** of the Agile Manifesto and explain what each means in practice.
- List the key **themes** from the twelve Agile principles and give a concrete example of each.
- Distinguish **iterative** from **incremental** delivery and explain how Agile combines both.

---

## Why This Matters

Modern QA is embedded in **short feedback loops** and **frequent delivery**. The Agile Manifesto is the shared vocabulary that teams use to justify collaborating with testers early, continuously improving, and responding to change. You will apply these ideas to Scrum, test philosophy, sprint ceremonies, and defect management throughout this week and your career.

When a Product Owner says "we need to ship something every sprint" or a developer says "let's not over-document this," they are appealing — consciously or not — to Agile values. Understanding those values helps you engage meaningfully, push back thoughtfully, and add real QA value rather than just executing scripts.

---

## The Concept

### Historical Context: Why Agile Emerged

Before 2001, most software teams followed structured, plan-driven methodologies — Waterfall, PRINCE2, or RUP. These methodologies demanded comprehensive upfront documentation and long development cycles before any working software was produced. The result was a wave of famous project failures:

- Projects delivered 18 months late with half the features originally promised.
- Systems built to a specification that users didn't actually want, because needs had changed during the 2-year build.
- Budgets overrun because defects found in late testing were enormously expensive to fix.

A group of experienced software practitioners met at a ski resort in Snowbird, Utah, in February 2001. They came from different backgrounds (XP, Scrum, DSDM, Crystal, FDD) but shared a frustration: **heavy process was making software delivery worse, not better**. They produced a shared statement of values — the **Agile Manifesto**.

---

### The Agile Manifesto (2001)

Seventeen practitioners signed a statement of **values**, explicitly not a methodology prescription. They valued:

| We Value More... | Over... |
|-----------------|---------|
| **Individuals and interactions** | Processes and tools |
| **Working software** | Comprehensive documentation |
| **Customer collaboration** | Contract negotiation |
| **Responding to change** | Following a plan |

The manifesto explicitly adds: *"That is, while there is value in the items on the right, we value the items on the left more."*

This is a critical nuance. Agile does **not** say:
- "Process doesn't matter." (It matters — just don't let it dominate.)
- "Documentation is worthless." (It has value — just don't let it substitute for working software.)
- "Contracts are unnecessary." (They exist — but they shouldn't block adaptation.)
- "Planning is pointless." (Plan — but don't worship the plan over reality.)

Let us unpack each value in the context of a QA professional:

---

#### Value 1: Individuals and Interactions Over Processes and Tools

A team that can talk to each other, clarify requirements face-to-face, and debug problems together will outperform a team that hides behind email chains and ticket queues — even if the second team has better tools.

**For testers:** When you discover an ambiguous acceptance criterion, *talk to the developer and Product Owner* rather than just filing a ticket. A 5-minute conversation prevents 3 days of rework. Processes (like test plans, bug reports) are still needed — but they serve the team, not the other way around.

---

#### Value 2: Working Software Over Comprehensive Documentation

The only real measure of progress in software development is software that **actually runs** and **delivers value**. A 200-page specification document with no working code is not progress — it is a theory.

**For testers:** Every sprint should produce something you can run and test — a "done" increment. If sprints end with code that passes unit tests but cannot be demonstrated, quality feedback is delayed. Agile teams ideally have a **Demo** environment at the end of every sprint.

---

#### Value 3: Customer Collaboration Over Contract Negotiation

Contracts are important, but when a customer's needs evolve (as they always do), a contract becomes a source of conflict rather than a pathway to value. Agile teams invite customers (or Product Owners representing them) into the process: sprint reviews, backlog refinement, acceptance criteria discussions.

**For testers:** The "customer" often reveals what "correct" means when they see working software. Sprint reviews are your opportunity to ask: "Does this behavior match your mental model?" — before the system goes to production.

---

#### Value 4: Responding to Change Over Following a Plan

Plans are based on knowledge at a point in time. New information (user feedback, technical discovery, market shifts, regulatory changes) should improve the plan, not be suppressed to protect it. Agile teams maintain a **backlog** that is continuously re-ordered as learning occurs.

**For testers:** This means your test suite is also alive. Acceptance criteria change; new risk areas emerge. Test maintenance is not a sign of failure — it is evidence of a healthy learning system.

---

### The Twelve Principles: Expanded Themes

The manifesto is backed by **twelve principles** (available at [agilemanifesto.org/principles.html](https://agilemanifesto.org/principles.html)). Here we group them by theme with practical examples:

---

**Theme 1: Early and Continuous Delivery**
*"Our highest priority is to satisfy the customer through early and continuous delivery of valuable software."*

Rather than one big release after 18 months, Agile teams ship small, valuable increments frequently. Each release gives real users something useful and generates feedback to improve the next increment.

*Example:* Instead of building an entire reporting module in one go, sprint 1 delivers "export as PDF for the top 5 report types." Sprint 3 adds Excel export. Sprint 5 adds scheduling. Users get value from sprint 1, and feedback shapes sprints 3 and 5.

---

**Theme 2: Welcome Changing Requirements**
*"Welcome changing requirements, even late in development."*

Change is a competitive advantage, not a nuisance. If users tell you the feature doesn't work as they expected, updating the backlog to reflect that is the responsible thing to do — not insisting the original spec was correct.

*Example:* During sprint 4, a user demos a competitor's login experience that has a "magic link" option. The Product Owner adds a story to the backlog for sprint 6. This is healthy agility, not scope creep.

---

**Theme 3: Deliver Working Software Frequently**
*"Deliver working software frequently, from a couple of weeks to a couple of months, with a preference to the shorter timescale."*

Short cycles compress the feedback loop. A two-week sprint means a defect in the design is discovered in two weeks, not six months. A monthly sprint means quality evidence is available monthly.

---

**Theme 4: Business and Development Together**
*"Business people and developers must work together daily throughout the project."*

The Product Owner (representing business) is not a person who throws requirements over a wall and waits for delivery. They are part of the team: answering questions in refinement, clarifying acceptance criteria in planning, providing feedback in reviews.

*For testers:* This means you can get a business stakeholder to clarify ambiguous expected results *before* you write test cases — not after you find a defect.

---

**Theme 5: Motivated Individuals**
*"Build projects around motivated individuals. Give them the environment and support they need, and trust them to get the job done."*

Micromanagement destroys quality. Teams that are trusted to make technical decisions, to raise quality concerns, and to improve their own processes deliver better software.

---

**Theme 6: Face-to-Face Conversation**
*"The most efficient and effective method of conveying information to and within a development team is face-to-face conversation."*

A sketch on a whiteboard, a quick Slack call, or a pair-programming session communicates more nuance than a specification document. (Remote teams adapt this to video calls and collaborative tools.)

---

**Theme 7: Working Software as the Measure of Progress**
*"Working software is the primary measure of progress."*

"We're 80% done" is not meaningful if 80% of the code has not been tested and integrated. A functional, tested, integrated increment is the honest measure.

*For testers:* A story is not "done" if it has passed unit tests but not been integrated, system-tested, or demonstrated. Your Definition of Done makes this real.

---

**Theme 8: Sustainable Pace**
*"Agile processes promote sustainable development. The sponsors, developers, and users should be able to maintain a constant pace indefinitely."*

"Crunch culture" — 80-hour weeks to hit a deadline — accumulates defects, burnout, and technical debt. Sustainable pace means quality has time to breathe.

---

**Theme 9: Technical Excellence and Good Design**
*"Continuous attention to technical excellence and good design enhances agility."*

Teams that cut corners on code quality (skipping tests, duplicating logic, avoiding refactoring) accumulate **technical debt** that slows every future sprint. Agile's fast pace demands *higher* code quality, not lower.

---

**Theme 10: Simplicity**
*"Simplicity — the art of maximizing the amount of work not done — is essential."*

Build what is needed now. Avoid speculative features ("we might need this someday"). Simple code is easier to test, easier to change, and easier to understand.

---

**Theme 11: Self-Organizing Teams**
*"The best architectures, requirements, and designs emerge from self-organizing teams."*

Teams that are given a goal and trusted to figure out *how* to achieve it make better decisions than teams given prescriptive instructions. Testers on self-organizing teams can say "I think we should test this with a state model" without needing management approval.

---

**Theme 12: Regular Reflection**
*"At regular intervals, the team reflects on how to become more effective, then tunes and adjusts its behavior accordingly."*

This is the **Sprint Retrospective** in Scrum: structured time to improve process, fix friction, and adapt working agreements. It is how Agile teams get better over time.

---

### Iterative vs. Incremental: The Distinction That Matters

These two words are often used interchangeably, but they mean different things:

**Iterative** means you **repeat and refine**. Each cycle revisits the same area of the product and makes it better based on feedback. Think of a sculptor who adds clay, steps back, adjusts, repeats.

**Incremental** means you **add new usable pieces**. Each delivery adds distinct, usable functionality that did not exist before. Think of building a house room by room, where each room is habitable when finished.

Agile teams typically do **both** at the same time:
- Each sprint **increments** the product (new features added).
- Each sprint also **iterates** on previous work based on feedback (fixing, refining, improving).

This is why Agile's short cycles are so powerful: you get new functionality *and* course-corrections in every sprint.

**Example:**
Sprint 1: Deliver basic user login (increment). 
Sprint 2: Add "forgot password" (increment) + fix login error message based on UAT feedback (iteration).
Sprint 3: Add OAuth SSO login (increment) + performance improvements to login page load time (iteration).

---

## Example: "Definition of Done" as a Manifesto Behavior

A team's **Definition of Done (DoD)** is one of the most practical ways Agile values become concrete working agreements. A sample DoD might require:

- Feature meets all acceptance criteria (customer collaboration, working software).
- Automated unit tests written and passing (technical excellence).
- Code reviewed by at least one peer (individuals and interactions).
- Integrated and deployable to the staging environment (working software).
- Demonstrated and accepted by the Product Owner (customer collaboration).
- No open critical defects (quality).
- Release notes updated (just enough documentation).

Each item in this DoD operationalizes one or more of the Manifesto values and twelve principles — turning philosophy into daily practice.

---

## Summary

- In 2001, seventeen practitioners wrote the **Agile Manifesto** in response to heavyweight, plan-driven methodologies that were failing software teams.
- The **four values** emphasize people over process, working software over documentation, collaboration over contracts, and adapting to change over following a plan.
- The **twelve principles** translate these values into concrete behaviors: short cycles, continuous delivery, technical excellence, sustainable pace, and regular reflection.
- **Iterative + incremental** delivery reduces risk compared to a single large release by generating real feedback at every cycle.
- The Agile Manifesto is not a methodology — it is a **value system**. Scrum, Kanban, XP, and other frameworks give it specific structure (which you will study in `agile-and-scrum-processes.md`).

---

## Additional Resources

- [Agile Manifesto](https://agilemanifesto.org/) — The original statement and all twelve principles.
- [Twelve Principles of Agile Software](https://agilemanifesto.org/principles.html) — Full text of each principle.
- [Scrum Guide](https://scrumguides.org/scrum-guide.html) — How the most widely adopted Agile framework implements these values (covered in depth in `agile-and-scrum-processes.md`).
- [Agile Alliance History](https://www.agilealliance.org/agile101/the-agile-manifesto/) — Context, signatories, and the thinking behind the manifesto.
