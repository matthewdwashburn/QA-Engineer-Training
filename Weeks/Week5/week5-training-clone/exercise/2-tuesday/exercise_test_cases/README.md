# Exercise: Test Cases from a User Story

**Mode:** Conceptual (test design)  
**Time:** ~45–60 minutes  
**Relates to:** `written/2-tuesday/requirements.md`, `test-case-design.md`, `positive-and-negative-testing` (preview Friday)  
**Demo tie-in:** `demos/2-tuesday/demo_test_case_writing/`

## Instructions

1. Read `templates/user_story.md` (**BookTrack** wishlist feature).
2. Write **at least 8** test cases in `test_cases.md` using the structure below.
3. Include **at least:**
   - **3** **positive** (happy path / valid data),
   - **4** **negative** or **edge** (invalid data, authorization, limits),
   - **1** **non-functional** angle **test idea** (performance, security, accessibility, logging—pick **one** category and make it **testable**).

## Required fields (per test case)

| Field | Requirement |
|-------|-------------|
| ID | `TC-BT-###` |
| Title | Short |
| Preconditions | Data, role, feature flags |
| Steps | Numbered, atomic |
| Expected result | Observable |
| Priority | P1–P3 with **one-line** risk reason |
| Traceability | Which **AC** line (AC1–ACn) |

## Definition of Done

- [ ] **≥ 8** cases, **labeled** positive/negative/NFR in the title or a column.
- [ ] Every case maps to **at least one** acceptance criterion.
- [ ] **No** vague expected results (“works”, “OK”)—use **UI text**, **HTTP codes**, **DB fields**, or **metrics** where applicable.
- [ ] **One** case explicitly tests **boundary** of **50** books (per AC).

## Stretch

Add a **traceability matrix** snippet: rows = AC ids, columns = test IDs.

## Submission

Markdown or spreadsheet export—**consistent** field layout.
