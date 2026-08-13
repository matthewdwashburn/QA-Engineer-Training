# Test Documentation

## Learning Objectives

By the end of this reading you will be able to:

- Explain the **purpose and content** of four core test documents: test plan, test case specification, test log, and test summary report.
- Tailor **formality and format** to Agile vs regulated contexts — right-sizing documentation without sacrificing the evidence it provides.
- Identify what **minimum documentation** professional teams should always maintain, regardless of methodology.
- Describe how tools (Jira, Xray, Confluence, Git) serve as living test documentation systems in modern teams.

---

## Why This Matters

"Documentation is bureaucracy" is a misconception that causes real problems. Test documentation is **memory** — it enables the team to reconstruct what was tested, when, by whom, with what result, and with what known gaps. Without it:

- Release decisions are made on verbal assurances, not evidence.
- An audit finds no proof that required testing was performed.
- A new team member has no way to understand what is covered by the existing test suite.
- A defect found in production triggers "but didn't we test that?" arguments with no way to verify.

The skill is not writing more documentation — it is writing **the right documentation at the right granularity**. A single-page sprint test summary can be as valuable as a 50-page formal test plan, when written well and used intentionally.

---

## The Concept

### The Four Core Test Documents

#### 1. Test Plan

**Purpose:** Communicates the **intent and approach** for a testing effort. Answers: What will we test? How? With what resources? Under what constraints? What are the risks?

**Typical content:**

| Section | Content |
|---------|---------|
| Scope | What features, stories, and functions are in scope for this cycle; what is explicitly out of scope. |
| Testing approach | What types of testing will be performed (functional, exploratory, performance, regression, UAT). |
| Test levels | Which levels apply (component, integration, system, acceptance). |
| Entry criteria | Conditions that must be met before testing begins. |
| Exit criteria | Conditions that define when testing is complete for this cycle. |
| Risks and mitigations | Testing risks (environment instability, unclear requirements, resource availability) and how they are managed. |
| Resources and schedule | Who is testing, when, in which environments. |
| Tooling | Test management tool, automation framework, defect tracker, CI system. |
| Defect management | Who triages defects, what is the severity scale, what blocks release. |

**Agile format (lightweight sprint test plan):**
For a two-week sprint, the test plan might be a 10-line note in Confluence or a structured comment on the sprint issue:

```markdown
## Sprint 15 Test Approach

**In scope:** AUTH-77, PROF-42, PROF-43

**Out of scope:** SSO integration (Sprint 16)

**Approach:**
- Automated API regression on every PR merge
- Manual exploratory session on each new story
- PO acceptance demo in Sprint Review

**Entry criteria:** Feature branch deployed to staging; smoke suite passing.

**Exit criteria:** All AC verified; zero open P1 defects; regression green; PO acceptance.

**Risks:** Staging email service unreliable Fridays — plan password reset tests Mon–Thu.
```

**Formal format (regulated industry):**
A versioned Microsoft Word document with sections, a document control table, a sign-off record, and cross-references to the requirements specification. The same logical content, much more formal structure.

---

#### 2. Test Case Specification

**Purpose:** Documents **what exactly will be tested** — the specific scenarios, inputs, steps, expected results, and traceability to requirements.

This is what was covered in depth in Tuesday's `test-case-design.md`. At the documentation level, test case specifications serve as:
- The **audit trail** that specific requirement behaviors were tested.
- The **execution guide** that makes tests repeatable by any team member.
- The **automation foundation** that translates directly into automated test scripts.

**Formats by context:**

| Format | When Used |
|--------|----------|
| Test management tool entries (Xray, Zephyr, TestRail) | Standard for most teams; provides traceability and reporting. |
| Gherkin/BDD feature files | When test cases serve dual purpose as living specification and automation driver. |
| Spreadsheet | Smaller teams; useful for quick review with non-technical stakeholders. |
| Automated test code | When the code IS the specification (test names serve as documentation). |

**Automated tests as living documentation:**
When test names are descriptive and tests are well-structured, the code itself serves as test case specification:

```python
class TestCheckoutDiscount:
    def test_premium_member_with_eligible_cart_receives_10_percent_discount(self):
        ...
    
    def test_non_member_does_not_receive_discount_regardless_of_cart_value(self):
        ...
    
    def test_premium_member_with_cart_below_100_does_not_receive_discount(self):
        ...
```

These test names are as readable as written test case titles — and the code is always up-to-date with the actual behavior, unlike a separate document.

---

#### 3. Test Log (Test Execution Log)

**Purpose:** A **record of what was executed**: who ran what test, when, on which build, in which environment, with what result.

The test log provides the **evidence** that testing was performed. It answers: "Did we actually run these tests, or are we just claiming we did?"

**What a test log contains:**
- Test case ID (or name for automated tests).
- Execution date and time.
- Tester name (for manual execution).
- Build/release version tested.
- Environment (staging, QA, production).
- Result (Pass, Fail, Blocked, Skipped).
- Notes (for failures: what failed; for blocks: why blocked).

**Automated CI test logs:**
CI pipelines produce detailed execution logs automatically — every test run generates a timestamped record of which tests ran, which passed/failed, and the full output. These machine-generated logs are the test log for automated tests.

CI test log tools: JUnit XML output (universal), Allure Report, pytest HTML reports, Azure Pipelines test results, GitHub Actions test summaries.

**Manual test logs:**
Test management tools (Xray, Zephyr, TestRail) allow testers to execute test cases and record results in the tool — generating a structured execution log. For teams without these tools, a shared spreadsheet with execution results and timestamps serves the same purpose.

**Minimum manual log requirement:**
At minimum, testers should record:
- Story/test case ID.
- Date executed.
- Build version.
- Result (Pass/Fail/Blocked).
- For failures: screenshot or note linking to the defect ticket.

This takes 30 seconds per test case and provides the evidence needed for sprint review discussions and release decisions.

---

#### 4. Test Summary Report

**Purpose:** Provides a **narrative summary** of the testing cycle — communicating what was tested, what was found, what was not tested, and the team's quality assessment.

The test summary report is the document that enables release decisions. It is the difference between "we think it's ready" and "here is the evidence that it is ready, with known gaps stated clearly."

**Content of a test summary report:**

| Section | Content |
|---------|---------|
| Testing scope | Which features, stories, requirements were tested; which were not. |
| Test results summary | Total tests planned/executed/passed/failed/blocked. |
| Defect summary | Defects found (by severity), open, closed, deferred; known residual risks. |
| Coverage assessment | Which requirements are verified; any coverage gaps. |
| Environments and builds | What was tested on which build in which environment. |
| Notable findings | Any significant discoveries outside the planned test scope. |
| Recommendation | Go / No-Go / Go with conditions — with clear rationale. |

**Lightweight sprint summary (Agile):**

```markdown
## Sprint 15 Test Summary — Build 2.4.15

### Scope
Stories: AUTH-77, PROF-42, PROF-43. All Done per Definition of Done.

### Test Results
- Automated regression: 283/285 passing (2 flaky tests quarantined — not story-related).
- Manual test cases: 24/24 executed; 24 passed.
- Exploratory sessions: 2 sessions (45 min each); notes attached.

### Defects
- 3 defects found during sprint; all fixed and retested.
- 0 open defects.

### Not Tested This Sprint
- IE11 compatibility (deprecated; no SLA).
- Load > 200 concurrent users (deferred to Sprint 16 performance sprint).

### Notable Findings
- Password reset email arriving 90–120 seconds late on Gmail accounts 
  (logged as informational DEF-308; within SLA; no blocker).

### Recommendation
GO for Sprint 15 release. Next sprint: address load testing gap.
```

**Formal test summary report (regulated context):**

A version-controlled document with sections cross-referenced to the test plan, RTM, and individual test case logs — with formal sign-off by the test manager, development manager, and release manager. Same content; formal structure; audit-ready.

---

### The Traceability Pack

In regulated industries (medical devices, finance, aviation), a **traceability pack** is assembled for each release:
- Requirements specification.
- Test plan.
- Test case specification.
- Executed test log.
- Defect log.
- RTM (Requirement Traceability Matrix) linking requirements → tests → results.
- Test summary report with sign-off.

Together, these documents prove to a regulatory body that: requirements existed, tests were designed to verify them, tests were executed on the correct build, results were recorded, defects were managed, and a responsible sign-off was obtained.

For Agile teams targeting regulated markets, the same logical content is maintained — often in automated tools (Jira + Xray can export compliance reports) — without requiring separate heavyweight document creation.

---

### Tooling: Where Test Documentation Lives in Modern Teams

| Document | Common Modern Home |
|---------|-------------------|
| Test plan | Confluence wiki page; sprint Jira ticket; GitHub wiki. |
| Test case specification | Xray / Zephyr in Jira; TestRail; BDD feature files in Git. |
| Test log | CI pipeline outputs; Allure Report; Xray execution results. |
| Test summary report | Confluence sprint report; Jira release notes; email to stakeholders. |
| RTM | Xray / Zephyr trace links; custom JQL filter; compliance report export. |

The principle: **documentation lives where the team actually works** — not in a separate, disconnected system. A Jira-embedded test plan that is seen every sprint is more valuable than a SharePoint document nobody reads.

---

## Summary

- The four core test documents are: **test plan** (intent and approach), **test case specification** (what to test), **test log** (proof of execution), and **test summary report** (decision-enabling narrative).
- **Right-size formality** to context: a 10-line sprint test note and a 200-page regulated test plan serve the same logical purpose at very different scales.
- **Automated tests as living documentation**: well-named, well-structured automated tests serve as both executable specification and test log.
- **Minimum evidence**: date, build, result, and defect reference for every manual execution. CI generates this automatically.
- **Tools** (Jira, Xray, Confluence, Git) are the modern homes for test documentation — keep documentation where the team actually works.

---

## Additional Resources

- [ISO/IEC/IEEE 29119-3 — Software Testing Documentation](https://www.iso.org/standard/45142.html) — International standard for test plan, test case, and test report structure.
- [ISTQB Foundation Syllabus — Test Monitoring and Control](https://www.istqb.org/) — Official treatment of test reporting and metrics.
- [Allure Report Documentation](https://docs.qameta.io/allure/) — Modern test report generation for automated suites.
- [Atlassian Xray for Jira](https://www.getxray.app/) — Test case management and traceability integrated with Jira.
- `requirement-traceability-matrix.md` (Tuesday) — RTM structure for the traceability pack.
