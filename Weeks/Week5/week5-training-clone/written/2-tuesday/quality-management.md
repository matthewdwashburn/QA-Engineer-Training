# Quality Management

## Learning Objectives

By the end of this reading you will be able to:

- Define the four pillars of quality management: **quality planning**, **quality assurance (QA)**, **quality control (QC)**, and **quality improvement**.
- Explain how these four activities form a **continuous cycle** in Agile software delivery.
- Apply the **PDCA (Plan-Do-Check-Act) model** to a sprint-level quality scenario.
- Distinguish between activities that **prevent** quality problems and activities that **detect** them.

---

## Why This Matters

The term "QA" is widely misused in the software industry. Job titles say "QA Engineer" but the role mostly executes test scripts — which is actually **Quality Control**, not Quality Assurance. Understanding the full quality management landscape helps you:

- Know which activities to prioritize at different stages of a sprint or release.
- Have informed conversations about **where to invest** when quality is struggling.
- Argue effectively for process improvements (QA activities) alongside test execution (QC activities).
- Demonstrate professional knowledge in interviews and with stakeholders.

---

## The Concept

### What Is Quality Management?

**Quality management** is the set of coordinated activities used to direct and control an organization's quality processes. In software teams, it encompasses everything from agreeing what "good" means (planning) to preventing defects (QA) to detecting them (QC) to learning from them (improvement).

A key standard is **ISO 9000**, which defines quality management principles used across industries. In software, these principles are adapted and applied within frameworks like ISTQB, Agile, and SAFe.

Quality management is not a department — it is a system of practices that the whole team participates in.

---

### Pillar 1: Quality Planning

**Quality planning** answers the question: *"How will we achieve the quality we need?"*

It involves defining:

- **Quality goals and standards:** What does "good enough to release" mean? What is the Definition of Done? What test levels are required (unit, integration, system, UAT)?
- **Risk-based test strategy:** Which features carry the most risk if they fail? Where should testing be most thorough? What can be covered with automation vs manual exploration?
- **Tooling and environments:** What test tools will be used? When will test environments be available? Who maintains test data?
- **Roles and communication:** Who owns test planning? How are defects triaged? Who makes go/no-go decisions?
- **Entry and exit criteria:** What conditions must be met before testing begins (entry) and before testing is declared complete (exit)?

**In Agile, quality planning is continuous — not a one-time document:**

Unlike Waterfall (where a Master Test Plan is produced upfront), Agile teams revisit their quality planning every sprint:
- Before a sprint: "What are the riskiest stories? What test approach do we need?"
- During refinement: "Does this story have testable acceptance criteria?"
- During retrospective: "Are our current QA practices working? What should we change?"

Quality planning sets the direction; the other three pillars execute and improve it.

---

### Pillar 2: Quality Assurance (QA)

**Quality assurance** is process-focused: it is the set of activities that build confidence that quality requirements **will be** fulfilled — by improving how the team works.

QA activities are fundamentally **preventive**: they aim to stop defects from being introduced in the first place.

**QA activities in software teams:**

| QA Activity | How It Prevents Defects |
|------------|------------------------|
| Agreeing testable acceptance criteria in refinement | Prevents ambiguous requirements from reaching development |
| Three Amigos conversations (Business + Dev + Test) | Surfaces edge cases before coding begins |
| Coding standards and linting rules | Prevents common coding errors at the point of writing |
| Mandatory code reviews before merge | Catches logic errors, security issues, style violations |
| Branch protection with required CI checks | Prevents broken code from reaching shared branches |
| Blameless retrospectives and postmortems | Identifies root causes to prevent defect patterns from repeating |
| Developer training and pairing | Builds skills that prevent future defects |
| Updating the Definition of Done when gaps are found | Raises the quality bar systematically over time |

Notice that testers contribute to QA through participation in refinement, Three Amigos, retrospectives, and DoD discussions — not only through test execution.

**QA is prevention; it is largely invisible when working well.** This is one reason it is undervalued — nobody notices the defects that were prevented.

---

### Pillar 3: Quality Control (QC)

**Quality control** is product-focused: it is the set of activities that **detect** defects in work products that have already been created.

QC activities are fundamentally **detective**: they examine what was built and identify where it does not meet expectations.

**QC activities in software teams:**

| QC Activity | What It Detects |
|------------|----------------|
| Unit testing | Logic errors in individual functions or classes |
| Integration testing | Interface mismatches and data flow errors between components |
| System testing | End-to-end behavior vs requirements and acceptance criteria |
| Exploratory testing | Unexpected behaviors, usability issues, risk areas not covered by scripted tests |
| Performance testing | Latency, throughput, and resource usage under load |
| Security testing | Vulnerabilities, unauthorized access paths, data exposure |
| UAT (User Acceptance Testing) | Whether the system meets business needs and user expectations |
| Code review (defect-finding mode) | Logic bugs, missing error handling, security risks in code |
| Release checklists | Missing deployment steps, environment configuration errors |

**QC consumes most of a tester's visible time** — and is what most people mean when they say "testing." It is necessary and valuable, but it is not the whole picture.

**The relationship between QA and QC:**

QC provides **signals** that feed back into QA. When QC activities consistently find the same types of defects (null pointer exceptions, missing form validation, timezone handling errors), QA activities are adjusted to prevent those patterns:

- Add null-check to code review checklist (QA).
- Add timezone edge cases to the Three Amigos template (QA).
- Improve developer training on form validation patterns (QA).

QC without QA produces a team that is always fire-fighting. QA without QC produces a team that believes processes work but has no evidence of it. Both together form a healthy quality management system.

---

### Pillar 4: Quality Improvement

**Quality improvement** is the cycle of **measuring, learning, and adapting** to make the quality management system progressively more effective over time.

Key improvement inputs:
- **Defect trends:** Is the number of defects per sprint increasing or decreasing? Are certain types recurring?
- **Escaped defects:** How many defects were found in production that testing should have caught? What does this say about coverage gaps?
- **Test cycle time:** How long does it take to run the full test suite? Is it slowing delivery?
- **Automation ROI:** Which automated tests catch real defects frequently? Which run constantly and never fail (possibly covering low-risk areas)?
- **Customer feedback and production incidents:** Real-world failure patterns that testing did not anticipate.

**Retrospectives as the primary Agile improvement engine:**

In Scrum, the Sprint Retrospective is the structured time for quality improvement. A team that uses retrospectives well will:
- Identify a pattern of escaped defects and add a corresponding QC activity.
- Recognize that a failing CI test is being ignored and fix or remove it.
- Acknowledge that Three Amigos sessions are not happening consistently and commit to a schedule.
- Observe that a specific module has a high defect density and propose a refactoring spike.

Quality improvement is **continuous** — not a once-a-year audit. It is the mechanism by which a team goes from "good enough" to "genuinely excellent."

---

### The PDCA Cycle Applied to Agile Quality

The **Plan-Do-Check-Act (PDCA)** cycle (also known as the Deming Wheel) is a classic quality improvement model that maps naturally to Agile sprint cadences:

```
PLAN → DO → CHECK → ACT → PLAN (next sprint) → ...
```

| PDCA Phase | Agile Sprint Equivalent |
|-----------|------------------------|
| **Plan** | Sprint Planning + Refinement: define quality approach, test strategy, DoD for this sprint. |
| **Do** | Sprint execution: develop stories, write tests, run automation, execute exploratory sessions. |
| **Check** | Sprint Review + daily standup checks: measure results vs Definition of Done, defect counts, test coverage, performance. |
| **Act** | Sprint Retrospective: identify improvements, update processes, adjust the DoD, change QA practices. |

The PDCA cycle's power in Agile is that it runs **every 2 weeks** rather than every 6–12 months. This means quality management is genuinely adaptive and continuously improving.

---

## Worked Example: Sprint-Level Quality Management in Practice

**Sprint 12 Context:** The team is delivering a payment module — high risk, high compliance requirement.

**Quality Planning (before sprint starts):**
- Prioritize payment stories for deepest testing coverage.
- Require API contract tests for all payment endpoints before acceptance.
- Test data: refresh staging database with anonymized production data on Monday.
- Exit criteria: zero open P1 defects; all payment AC verified; PCI-DSS compliance checklist signed.

**Quality Assurance (during sprint):**
- Three Amigos session for Story 78 (Apply Coupon Code) surfaces: "What happens if the coupon is used simultaneously by two users?" — Developer accounts for it with a database lock.
- Code review catches: a developer forgot to validate the currency field against allowed currencies — fixed before commit.

**Quality Control (during sprint):**
- Automated unit tests run on every commit — catch a regression in the tax calculation on Day 4.
- Tester runs exploratory session on the checkout flow — finds that a session expiry mid-payment loses the cart contents without warning.
- Performance test on Day 9: checkout API p95 latency is 380ms (threshold is 300ms) — flagged to the team.

**Quality Improvement (retrospective):**
- Escaped currency defect pattern noted: "Three of the last four sprints had currency-related defects. Can we add currency validation to our code review checklist?"
- Session expiry edge case: "This scenario should have been covered in acceptance criteria — let's update our Three Amigos template to always ask: what happens when the session expires mid-flow?"
- Performance regression: "The performance test ran too late in the sprint — let's add a performance gate to our CI pipeline for payment endpoints."

In 10 days, the team has run all four pillars of quality management — and left the sprint with concrete improvements that will prevent the same issues next time.

---

## Summary

- Quality management has four pillars: **Planning** (set targets and approach), **Quality Assurance** (improve processes to prevent defects), **Quality Control** (inspect products to detect defects), **Quality Improvement** (measure and learn to improve continuously).
- **QA is preventive and process-focused; QC is detective and product-focused** — both are necessary.
- Agile teams run the **PDCA cycle every sprint** — planning in sprint planning, doing in the sprint, checking in reviews and retrospectives, acting through process changes.
- Testers contribute to **all four pillars** — not only to test execution (QC).

---

## Additional Resources

- [ISO 9000:2015 — Quality Management Principles](https://www.iso.org/standard/62085.html) — Foundational quality concepts used across industries including software.
- [Scrum Guide — Definition of Done](https://scrumguides.org/scrum-guide.html#increment) — The Agile artifact that ties quality planning to sprint commitment.
- [ASQ — Quality Management](https://asq.org/quality-resources/quality-management) — Comprehensive practitioner resource on quality management practices.
- `quality-assurance-vs-quality-control.md` — Detailed contrast of QA and QC with software examples.
