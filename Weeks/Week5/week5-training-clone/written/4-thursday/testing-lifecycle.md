# The Testing Lifecycle

## Learning Objectives

By the end of this reading you will be able to:

- Outline the **six phases** of a generic test process lifecycle and explain the purpose of each.
- Map the lifecycle phases to **Agile sprint activities** — showing how they run continuously rather than sequentially.
- Define **entry and exit criteria** and explain how they are applied in Agile without bureaucratic overhead.
- Use the lifecycle vocabulary fluently in interviews, standards discussions, and regulated enterprise contexts.

---

## Why This Matters

Understanding the testing lifecycle serves two purposes. First, it gives you a **shared professional vocabulary** — when you communicate with teams using formal processes (ISO 29119, ISTQB, regulated industries), you need to speak their language. Second, it reveals that **Agile testing does not skip lifecycle phases** — it compresses and repeats them every sprint rather than executing them as a single waterfall pass.

Knowing the full lifecycle also helps you plan your own week and sprint — mapping each day's work to a phase ensures you think about all the activities needed, not just test execution.

---

## The Concept

### The Six Phases

The test lifecycle (decomposition varies slightly by source; ISTQB Foundation is the most widely referenced) consists of six activities:

---

#### Phase 1: Test Planning

**What it is:**
Defining the **scope, objectives, approach, resources, schedule, and risks** for the testing effort.

**Key outputs:**
- Test plan document (or sprint test plan note).
- Scope: what will and will not be tested.
- Entry and exit criteria for the testing cycle.
- Tooling and environment requirements.
- Risk register for the testing effort.

**In Agile (sprint context):**
This does not require a 50-page document. In a sprint, test planning happens at **refinement and sprint planning**:
- "What are the riskiest stories this sprint?" (risk focus).
- "What test approach do we need for each story?" (approach).
- "What environments and data do we need?" (resources).
- "When in the sprint should testing activities happen?" (schedule — not a testing phase at the end).

Lightweight but deliberate. The sprint test plan may be a few bullet points in Confluence or a task on the sprint board.

---

#### Phase 2: Test Analysis

**What it is:**
Studying the **requirements, specifications, and system design** to identify **what to test** — the test conditions.

**Key outputs:**
- Test conditions: "We must verify that the checkout API rejects negative quantities."
- Test data requirements: "We need a user account with Platinum tier membership and zero balance."
- Coverage analysis: "Story AUTH-77 has 5 acceptance criteria items; we must test each."
- Risk analysis: "The payment integration is the highest-risk area; we allocate deepest testing there."

**In Agile:**
Test analysis happens during **refinement and story review**. Reading acceptance criteria, asking Three Amigos questions, and flagging unstated scenarios are all test analysis activities.

---

#### Phase 3: Test Design

**What it is:**
Creating the specific **test cases, test scenarios, test charters, and automation scripts** that will be used during execution.

**Key outputs:**
- Test cases with steps and expected results.
- Exploratory test charters.
- Automation test scripts.
- Test data specifications.

**Techniques used in design:**
This week's techniques — equivalence partitioning, BVA, state transition testing, decision tables, use case testing — are all **test design techniques**. They are applied in this phase to create a test set that is both efficient and comprehensive.

**In Agile:**
Test design happens in **parallel with development** — testers design test cases while developers code the feature. By the time the story is ready for testing, the test cases are already written.

---

#### Phase 4: Test Implementation

**What it is:**
Preparing the **test environment, test data, and test automation** required to execute the designed tests.

**Key outputs:**
- Test environment configured and available.
- Test data loaded or generated.
- Automation scripts executed and reviewed.
- Test harnesses (mocks, stubs, simulators) set up.

**In Agile:**
Implementation happens continuously throughout the sprint. The CI pipeline runs automated tests on every commit — this is test implementation at its most continuous. Manual test data preparation happens as stories enter "Ready for Testing" status.

Test implementation also includes managing **dependencies**: "I cannot test Story 47 until the staging environment is refreshed with the latest schema migration."

---

#### Phase 5: Test Execution

**What it is:**
**Running tests** (manual or automated), **logging results**, **reporting defects**, and **retesting** fixes.

**Key outputs:**
- Test execution log: which tests ran, when, with what result.
- Defect reports for failed tests.
- Retest confirmations for fixed defects.
- Coverage map: which test conditions have been exercised.

**In Agile:**
Test execution is continuous — not a dedicated phase at the end of the sprint. As soon as a developer marks a story's branch as "ready for review," a tester can begin execution on that story while the developer starts the next one. Automated tests execute on every commit in CI — test execution is happening constantly.

Exploratory testing sessions also occur during execution — unscripted but charter-guided discovery on new features.

---

#### Phase 6: Test Completion and Reporting

**What it is:**
Summarizing the **testing outcomes** for a cycle — what was tested, what was found, what was not tested, the quality assessment, and lessons learned.

**Key outputs:**
- Test summary report.
- Defect summary: total found, open, closed, deferred.
- Coverage summary: which requirements/stories are verified.
- Quality assessment and release recommendation.
- Lessons learned: what will be improved next sprint.

**In Agile:**
Completion activities happen at **Sprint Review** (demonstrate and discuss quality) and **Sprint Retrospective** (improve the testing process). The test summary may be as lightweight as a comment on the sprint card or a brief table in Confluence.

---

### Entry and Exit Criteria

**Entry criteria** define the conditions that must be met **before a testing phase can start**. They prevent testers from wasting time testing code that is not ready.

**Examples:**
- "Testing on this story begins when the feature branch is deployed to the test environment and the smoke suite passes."
- "Integration testing begins when both Service A and Service B have completed individual component testing."

**Exit criteria** define the conditions that must be met **before declaring a testing cycle complete**. They prevent premature "done" declarations.

**Examples:**
- "Sprint testing is complete when: all acceptance criteria are verified; zero open P1 defects; regression suite passing on the current build; exploratory session note submitted."
- "Release exit criteria: PO acceptance; all P1 and P2 defects resolved or formally deferred; performance test passed; deployment checklist complete."

**Important nuance:** Exit criteria based purely on coverage percentages ("95% of tests passing") can be gamed or misleading. Well-designed criteria combine **coverage** (what was tested) with **outcome** (what was found and what remains open) and **decision-maker acceptance** (PO has acknowledged the quality position).

---

### The Agile Lifecycle: Compress and Repeat

In Waterfall, the six phases execute sequentially — one linear pass from planning through reporting, taking months. In Agile, **the cycle repeats every sprint**:

```
Sprint Start
    ↓
[Test Planning] — Sprint Planning + Refinement
    ↓
[Test Analysis] — Refinement + Three Amigos (concurrent with development)
    ↓
[Test Design] — Test case writing (concurrent with development)
    ↓
[Test Implementation] — Environment setup + CI automation (concurrent with development)
    ↓
[Test Execution] — Story-by-story as features complete (continuous throughout sprint)
    ↓
[Test Completion] — Sprint Review + Retrospective
    ↓
Sprint End → Next Sprint: repeat
```

This is not a mini-Waterfall within each sprint — the phases are not sequential gates. They overlap and run concurrently:
- While a developer codes Story A, the tester **designs** tests for Story A AND **executes** tests for Story B (which the developer completed yesterday).
- CI runs automated tests (execution) on every commit throughout the sprint.
- Analysis and design for Story C (coming next sprint) happens in refinement this sprint.

The lifecycle provides **vocabulary for planning**, not a sequential constraint on execution.

---

## Worked Example: Sprint-Level Lifecycle Mapping

**Sprint goal:** Deliver the Invoice Generation feature.

| Day | Lifecycle Activity | What Actually Happens |
|-----|-------------------|----------------------|
| Day 1 (Sprint Planning) | **Test Planning** | Tester identifies 3 stories; notes highest risk is Story 78 (PDF generation — complex, no existing tests); plans exploratory session for Day 7. |
| Days 1–3 (Refinement) | **Test Analysis** | Tester reviews each story's AC; raises 4 questions in Three Amigos; one ambiguity requires BA clarification before Day 3. |
| Days 2–5 (Concurrent with dev) | **Test Design** | Tester writes 18 test cases for Stories 76–78; prepares test data (sample invoice configurations). |
| Days 2–5 (CI pipeline) | **Test Implementation** | CI runs unit tests + regression on every commit; tester configures test environment with invoice data seeds on Day 3. |
| Days 4–9 (Story completion) | **Test Execution** | Story 76 tested and passed Day 4. Story 77 tested Day 6 — 2 defects found, fixed, retested Day 7. Exploratory session on Story 78 (PDF edge cases) Day 7. Story 78 regression clean Day 9. |
| Day 10 (Sprint Review + Retro) | **Test Completion** | Tester presents: "Stories 76–78 all Done. 3 defects found; 3 fixed. Regression green. Known gap: large invoice (>100 line items) not performance-tested — flagged as sprint 16 risk. Recommendation: GO." |

The tester completed all six lifecycle phases within a 10-day sprint — concurrently, not sequentially.

---

## Summary

- The **six test lifecycle phases** (Planning, Analysis, Design, Implementation, Execution, Completion) provide a professional vocabulary and structured way to think about all testing activities.
- In **Agile**, the lifecycle runs **concurrently and iteratively** each sprint — it is not a sequential set of phases with handoffs.
- **Entry criteria** prevent premature testing; **exit criteria** prevent premature "done" declarations.
- Test planning in Agile is lightweight and per-sprint — not a 50-page document. The structure is the same; the scale is appropriate.
- Knowing the lifecycle vocabulary enables communication with regulated enterprises, standards bodies, and interviewers who use ISTQB or ISO 29119 language.

---

## Additional Resources

- [ISO/IEC/IEEE 29119-2 — Software Testing Processes](https://www.iso.org/standard/45142.html) — International standard defining test process components.
- [ISTQB Foundation Syllabus — Test Process](https://www.istqb.org/) — Official syllabus treatment of the test process and its activities.
- [Agile Testing (Crispin & Gregory)](https://lisacrispin.com/) — How testing activities fit within Agile sprint cadences.
- `test-documentation.md` — The artifacts produced at each lifecycle phase.
