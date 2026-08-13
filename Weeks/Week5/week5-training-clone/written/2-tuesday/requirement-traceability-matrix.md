# Requirement Traceability Matrix (RTM)

## Learning Objectives

By the end of this reading you will be able to:

- Explain **forward and backward traceability** and why both are essential for quality assurance and audits.
- Build a **complete RTM** — selecting appropriate columns, identifiers, and linking test cases to requirements.
- Choose between **Agile-light** and **regulated-heavy** RTM formats based on organizational context.
- Recognize and prevent common RTM pitfalls: stale links, orphaned tests, and coverage illusions.

---

## Why This Matters

"How do you know you tested everything?" is one of the most common questions in a quality review, release gate, or compliance audit. The Requirement Traceability Matrix is the **standard artifact** for answering that question systematically.

Without an RTM (or its equivalent in a test management tool), a team can only say "we tested our test cases" — not "we tested every specified requirement." This distinction matters for:

- **Regulatory compliance:** ISO 13485 (medical devices), DO-178C (aviation), GDPR compliance testing, and financial services audits all require evidence that requirements were tested.
- **Change impact analysis:** When a requirement changes, which tests need to be updated? The RTM answers this question immediately.
- **Coverage gaps:** Are there requirements with no tests? Are there tests with no requirement? The RTM makes both visible.
- **Release decisions:** Can the Product Owner or release manager point to evidence that every mandated behavior was exercised? The RTM provides that evidence.

---

## The Concept

### What Is Requirement Traceability?

**Requirements traceability** is the ability to follow the life of a requirement in **both directions**:

**Forward traceability:** Starting from a requirement, follow it forward through design, implementation, and test artifacts. Answers: "For this requirement, what tests verify it?"

**Backward traceability:** Starting from a test case or defect, trace back to the requirement it covers. Answers: "What requirement does this test case verify? Which requirement does this defect affect?"

An **RTM** is the tabular artifact that documents these links in a structured, queryable format — either a spreadsheet, a test management tool export, or a combination of Jira links and filter reports.

---

### Standard RTM Structure

A complete RTM contains these elements:

| Column | Purpose | Example |
|--------|---------|---------|
| **Requirement ID** | Stable, unique identifier for the requirement. Links to source document. | `AUTH-77-AC-01`, `SRS-2.4.3`, `GDPR-Art-17` |
| **Requirement Summary** | Brief description — saves reviewers from opening the source document. | "Password reset link expires after 60 minutes." |
| **Source / Reference** | Where the requirement is documented. | User Story AUTH-77, SRS Section 4.2, Regulation Article 17 |
| **Test Case / Scenario IDs** | All tests that verify this requirement. Multiple tests per requirement are common. | `TC-AUTH-007`, `TC-AUTH-008 (negative)`, `PERF-AUTH-01` |
| **Test Type** | Positive, negative, boundary, NFR (performance), security — helps assess coverage quality. | Positive + Negative + BVA |
| **Last Verified Build** | Which software build was used for the most recent execution. Prevents "stale green" reporting. | `v2.4.15 (Sprint 15)` |
| **Execution Result** | Pass, Fail, Blocked, Waived-with-approval — for the last execution. | Pass |
| **Open Defects** | Any defects currently linked to this requirement that are not resolved. | `DEF-307 (Open, Medium)` |
| **Risk Level** | Risk classification for coverage prioritization. | P1 (Critical path) |

**Not every column is mandatory for every project.** Strip down to the minimum that serves your organization's needs while answering the two key questions: "Which tests cover this requirement?" and "What does this test verify?"

---

### Forward and Backward Traceability in Practice

**Forward traceability use case: Change impact analysis**

Scenario: The Product Owner says "We are changing the password expiry from 60 minutes to 30 minutes in Story AUTH-92."

With an RTM:
1. Find `AUTH-77-AC-01` (password reset link expiry requirement) in the RTM.
2. Identify all test cases in the `Test Case IDs` column.
3. Update those test cases to expect 30-minute expiry.
4. Schedule re-execution of those specific tests on the new build.

Without an RTM: The team must search through all test cases trying to remember which ones test expiry timing, or risk missing tests that now have wrong expected results.

**Backward traceability use case: Defect impact assessment**

Scenario: DEF-317 is filed — "Password reset link is not expiring correctly."

With an RTM:
1. Find the defect in the RTM's open defects column.
2. Identify the linked requirement (`AUTH-77-AC-01`).
3. Immediately know the business impact: "Password reset security is compromised — this affects GDPR compliance and security requirements."
4. Communicate the impact appropriately to the Product Owner and security team.

Without an RTM: The team knows there is a defect but must manually assess what requirement it violates and what business risk it carries.

---

### Building an RTM Step by Step

**Step 1: List all requirements to be covered**

Sources:
- User story acceptance criteria (each AC item is a testable requirement).
- Non-functional requirements (performance SLAs, security requirements, accessibility standards).
- Regulatory requirements (GDPR articles, financial regulations, industry standards).
- Architecture constraints ("Must use HTTPS for all endpoints").

**Step 2: Assign stable identifiers**

Each requirement needs a stable, unique ID that will not change even if the requirement's wording is refined. For Agile teams:
- `{Story Key}-AC-{number}`: `AUTH-77-AC-1`, `AUTH-77-AC-2`, `AUTH-77-AC-3`.
- Or use the story key directly if each story has a single clear requirement.

**Step 3: Map test cases to requirements**

For each requirement, identify which test cases verify it. One requirement may have multiple tests (positive, negative, BVA). Link them all.

**Step 4: Record execution results and build references**

After each test cycle, update the RTM with: which build was tested, what the result was, and any open defects.

**Step 5: Identify and address gaps**

Look for:
- **Requirements with no tests:** Missing coverage — add tests before release.
- **Tests with no requirement:** Orphaned tests — either remove them or link them to the correct requirement.
- **Requirements with only positive tests:** Missing negative coverage — add error handling and boundary tests.

---

### Agile RTM: Lightweight Living Traceability

In Agile, creating and maintaining a heavyweight RTM spreadsheet every sprint is impractical. Instead, traceability is maintained through **structural linking in Jira and the test suite**:

**1. Story-level traceability:**
Every user story in Jira is the requirement. Tests are linked to the story via:
- Xray/Zephyr test case issues linked to the story.
- Sub-task "Test Story AUTH-77" on the story.
- BDD feature files tagged with the story key (`@AUTH-77`).

**2. Directory structure traceability:**
Test files organized by story key provide structural traceability:
```
tests/
  auth/
    AUTH-77_password_reset/
      test_valid_reset_link.py        # → AUTH-77-AC-1
      test_expired_reset_link.py      # → AUTH-77-AC-3
      test_used_reset_link.py         # → AUTH-77-AC-4
```

**3. JQL-generated traceability view:**
In Jira, a filter can produce an on-demand traceability view:
```jql
project = SHOP AND issuetype = Story AND sprint = "Sprint 15" AND status = Done
```
Each story shows linked test cases and linked defects — a lightweight live RTM.

**4. Sprint release gate filter:**
```jql
project = SHOP AND "Requirement Covered" = false AND sprint in openSprints()
```
(If teams use a custom "Requirement Covered" field on stories.)

**When to produce a formal RTM:**
- Before a major release to a regulated market.
- When a compliance audit requests test evidence.
- When entering a highly visible phase (go-live, major client handoff).
- When the team is onboarding to a new regulated product domain.

---

### RTM for Regulated Industries: Formal Requirements

In regulated environments (medical devices, aviation software, financial services, government), RTMs must meet strict standards:

**Version control:** The RTM is a controlled document — versioned, with a change history log. The version used for a specific release is frozen and retained as audit evidence.

**Baseline management:** RTM version N.M corresponds to Requirements Specification version N.M and Test Case Specification version N.M. Changes to any one of these trigger a re-baseline.

**Sign-off:** The RTM is approved by the Test Manager, and sometimes additionally by the Product Owner, Safety Engineer, or Regulatory Affairs representative.

**Evidence retention:** The RTM and its execution results are retained for the product's lifecycle — sometimes 10+ years for medical device software.

**Coverage metrics in the RTM:**
- % of requirements with at least one test (coverage completeness).
- % of requirements with executed and passing tests (verification completeness).
- % of requirements with both positive and negative tests (coverage quality).

---

## Complete Example: Sprint RTM (Lightweight Agile)

**Sprint 15 — Authentication Feature**

| Req ID | Summary | Tests | Type | Last Build | Result | Open Defects |
|--------|---------|-------|------|-----------|--------|-------------|
| AUTH-77-AC-1 | Password reset email sent within 2 minutes of request | TC-AUTH-001 | Positive | v2.4.15 | Pass | — |
| AUTH-77-AC-2 | Reset link expires after 60 minutes | TC-AUTH-002, TC-AUTH-003 | Positive + BVA | v2.4.15 | Pass | — |
| AUTH-77-AC-3 | Used reset link rejected with clear message | TC-AUTH-004 | Negative | v2.4.15 | Pass | — |
| AUTH-77-AC-4 | Reset attempt for unregistered email gives generic message (no account revelation) | TC-AUTH-005 | Negative/Security | v2.4.15 | Pass | — |
| AUTH-77-NFR-1 | Reset API response time < 500ms at 95th percentile | PERF-AUTH-001 | Performance | v2.4.15 | Pass | — |
| PROF-42-AC-1 | Profile photo uploads in JPG, PNG, GIF up to 5MB | TC-PROF-001, TC-PROF-002 | Positive + BVA | v2.4.15 | Pass | — |
| PROF-42-AC-2 | File types outside JPG/PNG/GIF rejected with specific message | TC-PROF-003, TC-PROF-004 | Negative | v2.4.15 | Pass | — |
| PROF-42-AC-3 | Files over 5MB rejected with size limit message | TC-PROF-005 | Negative/BVA | v2.4.15 | Pass | DEF-308 (Low, accepted) |

**Coverage analysis:**
- 8 requirements covered.
- 11 test cases in total (some requirements have multiple tests).
- Both positive and negative coverage on all functional requirements.
- 1 open defect (Low severity, formally accepted by PO).
- Release recommendation: GO.

---

## Summary

- An RTM links **requirements → tests → execution results**, enabling forward (impact analysis) and backward (defect impact) traceability.
- Build RTMs by: listing requirements → assigning stable IDs → mapping test cases → recording results → auditing for gaps.
- **Agile RTMs** are maintained through structural Jira linking, directory organization, and on-demand JQL filters — not necessarily a heavyweight spreadsheet.
- **Regulated RTMs** are versioned, baselined, signed-off controlled documents retained as compliance evidence.
- RTM gaps (requirements with no tests, orphaned tests, positive-only coverage) are discovered and resolved before release.

---

## Additional Resources

- [ISO/IEC/IEEE 29148 — Requirements Traceability](https://www.iso.org/standard/45171.html) — Traceability requirements in structured requirements processes.
- [ISO/IEC/IEEE 29119-3 — Test Documentation](https://www.iso.org/standard/45142.html) — Relationship between test artifacts and requirements.
- [ISTQB Glossary — Traceability](https://glossary.istqb.org/) — Canonical definitions for forward/backward traceability.
- [Xray for Jira — Traceability Reports](https://www.getxray.app/) — Automated RTM generation from Jira-linked test cases.
- `test-documentation.md` (Thursday) — How the RTM fits into the broader test evidence pack.
