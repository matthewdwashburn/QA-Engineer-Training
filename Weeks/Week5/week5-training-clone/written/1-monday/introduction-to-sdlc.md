# Introduction to the Software Development Life Cycle (SDLC)

## Learning Objectives

By the end of this reading you will be able to:

- Define **SDLC** and explain why teams use lifecycle models deliberately instead of improvising delivery.
- Name and describe the **common lifecycle activity clusters** that recur across nearly every methodology.
- Contrast **predictive** (plan-driven) and **adaptive** (change-friendly) attitudes at a high level, setting up your deeper study of Waterfall and Agile later this week.
- Articulate where **testing and QA** sit inside a lifecycle, and why "testing at the end" is a risk pattern rather than a strategy.

---

## Why This Matters

Week 5's epic is **Agile delivery and testing foundations**. Everything you will study this week — methods, ceremonies, testing philosophy, test design techniques, and Jira — hangs off one foundational idea: **software is engineered through a repeatable life cycle**.

Think of building a house. You would not start hammering nails the moment you had a vague idea of what the house should look like. You would need blueprints, a budget, permits, a sequence for tradespeople (foundations before walls, walls before roof), and a final walkthrough before handing over the keys. Software delivery is no different — it is a coordinated, multi-stage process that benefits enormously from structure and explicit planning.

Organizations use an **SDLC** (Software Development Life Cycle) to:

- Clarify **who does what, and when** — eliminating duplication and gaps.
- Align **engineering, QA, compliance, and operations** around shared milestones and definitions.
- Manage **risk, scope, and quality** as the product evolves from idea to production system.
- Provide a **vocabulary** that bridges technical and business stakeholders.

As a QA professional, you will rarely "own" the lifecycle — but you will **navigate it every single day**. When you know which phase the team is in, you understand: when is it too early to write test cases? When is it too late to raise a requirement concern? When should I push back on a rushed release? Speaking clearly about lifecycle phases lets you advocate for earlier review, better traceability, and realistic schedules.

---

## The Concept

### What Is the SDLC?

The **Software Development Life Cycle (SDLC)** is the **set of phases, activities, and outputs** used to conceive, design, build, test, deploy, maintain, and eventually retire a software system. It is not one single fixed method — it is a **lens for organizing work**.

Think of the SDLC as the **route plan for a road trip**. The route plan does not tell you the exact speed at every moment or the exact rest stops — but it tells you the major waypoints, the rough timing, who is driving, and what you will do if a road is closed. Different teams choose different route plans (Waterfall, Agile, hybrid) depending on their destination, timeline, and tolerance for detours.

Standards and academic frameworks (notably **ISO/IEC/IEEE 12207**) describe software processes in detail, but in day-to-day training and industry practice we group work into familiar **engineering phase clusters**:

| Phase / Activity Cluster | Typical Focus |
|--------------------------|---------------|
| **Planning & Requirements** | *Why* are we building this? Scope, feasibility, budget, high-level architecture, agreeing what "done" means. |
| **Analysis & Design** | *What* will the system do in detail? *How* will it be structured? Interfaces, data models, architecture, UI flows. |
| **Implementation** | *Build it.* Coding, unit testing by developers, integration of components. |
| **Verification & Validation (V&V)** | *Does it work as specified and does it serve users?* System testing, integration testing, UAT, reviews. |
| **Deployment / Release** | *Ship it.* Promotion through environments, rollout strategies, rollback plans, release notes. |
| **Operations & Maintenance** | *Keep it alive.* Monitoring, support, patching, feature enhancements, and eventually planned retirement. |

> **Key insight:** These phases describe *activities* — they do not dictate *when* or *how often* you do them. That is what lifecycle **models** determine.

### SDLC Models Map Activities to Rhythm and Order

An **SDLC model** decides the **sequence**, **overlap**, and **feedback loops** between activities. There are two broad philosophies:

**Predictive (Plan-Driven) Models**
- Plan the full scope upfront; establish baselines for requirements, design, and schedule.
- Each phase is reviewed and signed off before the next begins (known as **phase gates**).
- Change is expensive once a phase is complete because subsequent phases are built on top of it.
- Best suited to: well-understood domains, regulatory environments, fixed-price contracts, hardware-integrated systems.
- Example: **Waterfall** (which you will read about immediately after this).

**Adaptive (Change-Friendly) Models**
- Plan in short horizons; expect and welcome change as normal.
- Deliver **working software frequently** in small increments, gathering real feedback after each.
- Requirements, design, and testing overlap and revisit each other within short cycles.
- Best suited to: innovative products, uncertain markets, fast-changing user needs, software products.
- Example: **Agile / Scrum** (which you will cover in depth throughout Monday and beyond).

> **Important:** No model eliminates the need for requirements, design, testing, or deployment. Every model does all of these — what differs is **when** you revisit them and **how much change** you allow mid-stream.

### Where Testing and QA Sit in the SDLC

Here is where many organizations go wrong: they treat testing as **"the last phase before release."** This is one of the most expensive habits in software delivery.

Imagine a baking analogy: you mix all the ingredients, bake the cake for 45 minutes, and *then* taste-test it. If the flour was stale or you forgot sugar, you cannot go back — you bake another cake. If you had tasted the batter earlier, you could have corrected it with almost zero cost.

In a healthy SDLC, quality activities are **distributed across the entire lifecycle**:

- **Planning phase:** Testers review requirement drafts for **ambiguity, testability, and completeness**. A requirement that says "the system shall be fast" cannot be tested — it needs a measurable threshold.
- **Design phase:** Testers participate in design reviews, asking: "How will we verify this component works? Are the interfaces observable?" Risk is discussed early.
- **Implementation phase:** Developers write **unit tests**; testers may pair-review test approaches; CI pipelines begin giving automated feedback.
- **V&V phase:** Testers execute planned test suites (system, integration, regression), exploratory sessions, and formal acceptance testing.
- **Deployment phase:** Testers support **smoke testing** in staging, validate release checklists, and participate in go/no-go decisions.
- **Operations phase:** Testers monitor production defects, triage reported issues, feed learning back into next iterations.

How much of this happens **continuously** versus in discrete chunks depends on the lifecycle model and the organization's culture. This week you will connect that directly to **Agile test philosophy** and **Scrum ceremonies**.

### A Simple Mental Model: Plan → Build → Learn

No matter how complex the methodology, effective teams loop through three fundamental activities:

1. **Plan** — Decide what to build next (and why).
2. **Build** — Create it (code, design, config, documentation).
3. **Learn** — Gather real feedback (tests, users, production) to improve the plan.

**Predictive models** make this loop very long and deliberate (months or years per cycle). **Adaptive models** compress the loop to days or weeks. Strong lifecycles make this loop **visible and managed**; weak ones hide rework until it becomes a crisis.

---

## Worked Example: Tracing a Feature Through the Lifecycle

Consider a team adding a **"Export Report to CSV"** feature to an analytics application.

**Planning & Requirements:**
- User story: *"As an analyst, I want to export the current report as a CSV file so I can work with it in Excel."*
- Acceptance criteria agreed: Maximum 10,000 rows, UTF-8 encoding, column headers included, downloaded within 5 seconds for standard reports.
- Tester raises: What happens for a 50,000-row report? What file name is used? Are there permissions on who can export?

**Analysis & Design:**
- Backend API endpoint defined: `GET /reports/{id}/export?format=csv`.
- Data transformation layer designed to stream rows rather than load all into memory.
- Tester notes: Streaming means we need to test partial downloads and network interruption scenarios.

**Implementation:**
- Developer writes the endpoint and a unit test confirming the CSV header row is correct.
- Integration test added to CI pipeline confirming a 100-row report returns within 1 second.

**Verification & Validation:**
- Tester writes test cases covering: small report (positive), large report (performance), empty report (edge case), special characters in data (encoding), download during network drop, permission check for restricted user.
- Defect found: special characters break the CSV delimiter — fixed before release.

**Deployment:**
- Feature goes to staging with a feature flag; tester runs a smoke check in staging.
- Go/no-go decision includes confirmation of defect fix and performance result.

**Operations:**
- A month later, a customer reports that semicolons in addresses break Excel import (country-specific delimiter). Logged as a new defect, fed back to the backlog.

Naming the phases helps testers know: *Which artifacts do I trace to? When can I intervene if scope is unclear? What evidence do I need for sign-off?*

---

## Summary

- The **SDLC** provides structure for how software moves from idea to maintained product — without it, teams reinvent decisions repeatedly and hide risks until late.
- **Common activity clusters** (plan, design, build, validate, deploy, maintain) appear in every organization; methodologies differ in how they **sequence and revisit** those activities.
- **Predictive models** (like Waterfall) front-load planning and enforce phase gates. **Adaptive models** (like Agile) compress feedback loops and embrace change.
- **Testing and QA** are most effective when distributed **throughout the lifecycle**, not only at the end — a theme you will reinforce with every topic this week.

---

## Additional Resources

- [ISO/IEC/IEEE 12207:2017 — Systems and Software Engineering — Life Cycle Processes](https://www.iso.org/standard/63712.html) — Formal lifecycle process standard used in mature and regulated organizations.
- [SWEBOK Guide v3 (Software Engineering Body of Knowledge)](https://www.computer.org/education/bodies-of-knowledge/software-engineering/) — Comprehensive overview of lifecycle, requirements, quality, and maintenance in professional context.
- [Agile Alliance — What is Agile?](https://www.agilealliance.org/agile-101/) — Short orientation to Agile values and principles before you study the Manifesto and Scrum in the next readings.
- [ISTQB Foundation Syllabus](https://www.istqb.org/) — Industry-standard testing certification syllabus that ties test activities directly to lifecycle phases.
