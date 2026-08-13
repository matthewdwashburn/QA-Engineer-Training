# Test Case Organization

## Learning Objectives

By the end of this reading you will be able to:

- Describe **test suites** and explain how to structure them by purpose and automation tier.
- Outline the key elements of a **lightweight Agile test plan**.
- Explain how **traceability matrices** and **priority schemes** support organized test management.
- Design a **suite ladder** that maps test types to CI/CD pipeline stages.

---

## Why This Matters

As a team's product grows, individual test cases accumulate rapidly — hundreds, then thousands. Without organization, tests become:

- Hard to find (which tests cover Story 87?).
- Impossible to scope for a release ("which tests do we need to run before go/no-go?").
- Unmanageable for reporting ("what percentage of our P1 scenarios are passing?").
- Wasteful in CI ("we run everything on every commit, even tests that take 4 hours").

Test case organization transforms a collection of individual tests into a **managed testing system** — where the right tests run at the right time, results are visible and actionable, and coverage can be demonstrated to stakeholders.

---

## The Concept

### Test Suites: Grouping Tests by Purpose

A **test suite** is a named group of test cases that share a common purpose, scope, or execution context. Organizing tests into suites allows you to:

- Run a targeted subset of tests in specific pipeline stages.
- Report pass rates per area (e.g., "Auth suite: 100% passing; Checkout suite: 95% passing").
- Identify which suite needs attention when a defect cluster appears.

**Common suite types:**

| Suite Name | Purpose | Size | When Runs |
|------------|---------|------|-----------|
| **Unit tests** | Verify individual functions/classes in isolation. Developer-owned. | Many (hundreds–thousands) | Every commit (CI) |
| **Contract / API tests** | Verify API schemas, status codes, and behaviors against contracts. | Dozens–hundreds | Every commit or PR merge |
| **Smoke suite** | Minimal set proving the build is alive and critical paths work. | 5–20 tests | Every deployment to any environment |
| **Regression suite** | Broad coverage of stable, previously verified features. | Hundreds | Nightly or pre-release |
| **Component suite** | Tests scoped to a specific module or service. | Varies | When that module changes |
| **NFR suite** | Performance, security, accessibility — non-functional checks. | Varies | Scheduled or pre-release |
| **Exploratory sessions** | Session-based, not automated — tracked separately. | N/A | Mid-sprint on new/changed features |

**Suite ladder for CI/CD:**

A practical pattern is the **suite ladder** — mapping suites to pipeline stages so that faster feedback runs first:

```
Stage 1 (every commit, < 5 min):
  - Unit tests
  - API contract tests
  - Linting and static analysis

Stage 2 (every PR merge to main, < 15 min):
  - Smoke suite (UI + API smoke)
  - Integration tests

Stage 3 (nightly or pre-release, 1–4 hours):
  - Full regression suite
  - Performance tests
  - Security scans
  - Accessibility audit

Stage 4 (pre-production release):
  - Manual exploratory session on changed areas
  - Release checklist execution
  - UAT sign-off
```

This ladder gives developers fast feedback (Stage 1) while protecting releases with comprehensive coverage (Stages 3–4).

---

### Organizing Tests Within a Suite

Within a suite, tests can be further organized by:

**By feature / component:**
- `auth/` — login, logout, session management, password reset
- `checkout/` — cart, payment, confirmation, refunds
- `reporting/` — dashboard, export, scheduled reports

**By risk / priority:**
Label or tag tests with priority (P1, P2, P3) so that when time is short, P1 tests run first. If a release decision must be made before the full suite completes, P1 results are available first.

**By test type:**
- `functional/` — behavior against acceptance criteria
- `negative/` — error handling and invalid input scenarios
- `boundary/` — edge values and limits
- `security/` — authorization and injection tests

**Tooling support:**
Most test management tools (Jira/Xray, TestRail, Azure DevOps Test Plans, Zephyr, Allure) support hierarchical suite organization. Automated test frameworks (pytest, JUnit, NUnit) support tags or markers to filter runs:

```bash
# Run only P1 smoke tests
pytest -m "smoke and p1"

# Run only checkout-related tests
pytest tests/checkout/ -v
```

---

### Test Planning: The Agile Lightweight Version

A **test plan** in Agile does not need to be a 50-page document. It is a living, sprint-updated record of:

**What is in scope:**
Which features, user stories, or requirements are being tested this sprint/release?

**What is out of scope (and why):**
Explicitly acknowledging what is not being tested prevents "we assumed you were testing X" misunderstandings.

**Testing approach:**
What types of testing will be applied? Automated regression? Manual exploratory sessions? Performance testing? UAT?

**Environments and data:**
What environment(s) will testing occur in? What test data is needed? Who is responsible for data setup?

**Entry and exit criteria:**

| Criterion | Entry (when can we start?) | Exit (when are we done?) |
|-----------|---------------------------|--------------------------|
| Development | Feature branch merged and deployed to test env. | |
| Testing | | All P1/P2 test cases executed; zero open P1 defects; regression passing. |
| Release | | PO acceptance; release checklist complete; rollback plan approved. |

**Risks and mitigations:**
What could prevent testing from completing? (Environment instability, resource availability, unclear acceptance criteria.) What is the mitigation?

**Defect triage process:**
Who triages found defects? What is the threshold for blocking a release (all P1 defects, or a count of P2 defects)?

**A one-page sprint test plan might look like:**

```markdown
# Sprint 15 Test Plan — Authentication & Profile

## Scope
- Story AUTH-77: Password reset via email link
- Story PROF-42: Edit profile photo
- Story PROF-43: Change display name

## Out of Scope
- SSO integration (separate sprint)
- Mobile app (mobile team tests separately)

## Approach
- Automated: API contract tests + regression suite
- Manual: Exploratory sessions on each new story
- UAT: PO to review in Sprint Review

## Environment
- Staging (staging.example.com)
- Test data: See /test-data/sprint15/ in the shared drive

## Exit Criteria
- All acceptance criteria verified
- Zero open P1 defects
- Regression suite passing
- PO acceptance in Sprint Review

## Known Risks
- Staging email service unreliable on Fridays — run password reset tests Monday–Thursday
```

This plan is created in 15 minutes, updated as the sprint progresses, and provides clear scope and expectations for the whole team.

---

### Traceability in the Organization Structure

Test organization and traceability go hand in hand. When tests are organized by story or requirement ID, traceability becomes structural rather than requiring a separate tracking exercise:

```
tests/
  auth/
    AUTH-77_password_reset/
      test_valid_reset_link.py      # covers AUTH-77 AC item 1
      test_expired_reset_link.py    # covers AUTH-77 AC item 3
      test_used_reset_link.py       # covers AUTH-77 AC item 4
  profile/
    PROF-42_edit_photo/
      test_valid_photo_upload.py
      test_oversized_photo_rejected.py
```

When the test directory structure mirrors the story/requirement structure, a Product Owner can answer "what tests cover this story?" by navigating the directory — no separate RTM required for Agile teams.

For regulated contexts, a formal RTM (see `requirement-traceability-matrix.md`) supplements this structural organization with explicit linkage evidence.

---

### Priority and Risk-Based Test Ordering

Not all tests are equally important. Priority schemes ensure the most critical coverage happens first:

**Common priority labels:**

| Priority | Meaning | Example |
|----------|---------|---------|
| **P1 / Critical** | Failure blocks release; immediate attention needed. | Payment processing, user authentication, data integrity. |
| **P2 / High** | Significant user impact; should be fixed before release if feasible. | Core workflows, common user paths. |
| **P3 / Medium** | Noticeable but workaround exists; can be addressed in next sprint. | Edge cases, infrequent user paths. |
| **P4 / Low** | Cosmetic or trivial; fix when time allows. | Minor UI inconsistencies, rarely-used features. |

**Risk-based test ordering:**

When executing a regression suite under time pressure, run tests in risk-order:
1. Most recently changed modules (highest regression risk).
2. Modules with historical defect density (defect clustering — Principle 4 from Tuesday).
3. Critical business flows (payment, authentication, data submission).
4. Stable, unchanged, well-tested areas.

This ensures that if you run out of time, you have at least covered the highest-risk areas.

---

## Worked Example: Suite Organization for a SaaS Product

**Product:** A SaaS project management tool. Teams of 2–8 testers, 2-week sprints.

**Suite structure:**

```
tests/
  unit/                          → 850 unit tests, 45-second run time
  contract/                      → 120 API contract tests, 3-minute run time
  smoke/                         → 18 smoke tests, 8-minute run time
  regression/
    auth/                        → 65 tests
    projects/                    → 92 tests
    tasks/                       → 78 tests
    reporting/                   → 45 tests
    integrations/                → 38 tests
  nfr/
    performance/                 → 12 load test scenarios
    security/                    → OWASP ZAP scan configuration
    accessibility/               → Axe-core rule set
```

**Suite ladder:**

```
On every commit:       unit/ + contract/    (< 5 min)
On every PR merge:     smoke/               (< 10 min)
Nightly at 2am:        regression/ + nfr/   (~ 2 hours)
Pre-release:           manual exploratory + release checklist
```

**Priority tagging in pytest:**

```python
@pytest.mark.p1
@pytest.mark.smoke
def test_user_login_success():
    ...

@pytest.mark.p1
@pytest.mark.regression
def test_payment_checkout_complete():
    ...

@pytest.mark.p3
def test_profile_photo_alt_text():
    ...
```

**Running only P1 tests before an urgent release:**
```bash
pytest -m "p1" --tb=short
```

This command runs all P1 tests across all suites, giving a fast confidence signal that the most critical scenarios pass before a time-pressured release.

---

## Summary

- **Test suites** group tests by purpose (smoke, regression, component, NFR) and enable targeted execution at each CI/CD pipeline stage.
- The **suite ladder** maps suites to pipeline stages: unit tests on every commit, smoke on every deploy, regression nightly, exploratory pre-release.
- **Agile test plans** are lightweight, living documents — one page per sprint, covering scope, approach, environment, entry/exit criteria, and risks.
- **Priority labels** (P1–P4) and **risk-based ordering** ensure critical coverage completes first under time pressure.
- **Directory structure mirroring** story/requirement IDs provides structural traceability without a separate tracking document.

---

## Additional Resources

- [ISTQB Foundation Syllabus — Test Planning](https://www.istqb.org/) — Formal treatment of test plan content and purpose.
- [ISO/IEC/IEEE 29119-3 — Test Documentation](https://www.iso.org/standard/45142.html) — Standard for test plan, test case, and test summary report structure.
- [Atlassian — Organizing Tests in Jira](https://support.atlassian.com/jira-software-cloud/) — Practical guide to using Jira/Xray for test case management.
- `test-documentation.md` (Thursday) — Formal test documentation artifacts in more depth.
- `requirement-traceability-matrix.md` (Tuesday) — RTM structure for explicit coverage linkage.
