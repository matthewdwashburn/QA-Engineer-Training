# Test Case Design

## Learning Objectives

By the end of this reading you will be able to:

- Identify all standard **fields in a test case** and explain what each contributes to quality and repeatability.
- Write **clear, atomic test steps** with observable expected results tied to requirements.
- Distinguish **positive (happy path)**, **negative**, and **edge case** paths in test case design.
- Apply best practices for writing test cases that are useful for both **manual execution** and as a foundation for **test automation**.

---

## Why This Matters

Test cases are the **repeatable, traceable record** of what you verified. A well-written test case:
- Can be executed by a different tester on a different day and produce the same result.
- Provides clear evidence for an audit or compliance review.
- Can be translated into an automated test with minimal reinterpretation.
- Saves significant time during regression cycles.
- Communicates quality scope to Product Owners and stakeholders.

A poorly written test case — vague steps, unclear expected results, missing preconditions — leads to inconsistent execution, false positives, false negatives, and the "cannot reproduce" cycle that wastes everyone's time.

---

## The Concept

### The Anatomy of a Test Case

A test case is a **specification of a test scenario** that describes exactly how to verify a specific behavior. Every field serves a purpose:

| Field | Purpose | Example |
|-------|---------|---------|
| **Test Case ID** | Unique, stable reference. Used for traceability, defect linking, regression tracking. | `TC-AUTH-007` |
| **Title / Summary** | One-line description of what is being tested. Should be specific enough to identify without reading the steps. | "Login with valid credentials — existing active user" |
| **Feature / Component** | Which part of the system is being tested. Helps with organization and filtered reporting. | Authentication module |
| **Linked Requirement / Story** | Traceability anchor — what requirement does this test verify? | User Story `AUTH-12`, AC Item 1 |
| **Preconditions** | The system state that must exist **before** the test starts. This is where most "flaky test" failures originate. | User `test@example.com` exists, is active, is not locked, has no active session. Test environment connected. |
| **Test Data** | The specific input values (or references to data sets) to use. | Email: `test@example.com`, Password: `ValidP@ss1` |
| **Test Steps** | Ordered, atomic actions. Each step should be a single observable action. | 1. Navigate to `/login`. 2. Enter email in the Email field. 3. Enter password in the Password field. 4. Click the "Sign In" button. |
| **Expected Result** | Observable outcome — per step or at the end. Must be specific enough to judge pass/fail. | User is redirected to `/dashboard`. The navigation bar shows the user's display name. The session cookie `auth_token` is set with a 30-minute expiry. The `last_login_at` timestamp for the user is updated in the database. |
| **Postconditions** | System state after the test — especially relevant if the test mutates shared state. | User session is active; test cleanup: logout after test execution to avoid affecting subsequent tests. |
| **Priority / Risk** | Indicates how critical the test is if it fails. Used to sequence execution under time pressure. | P1 — Critical path, must pass before release. |
| **Test Type** | Functional, performance, security, accessibility, regression, smoke. Helps categorize for reporting. | Functional — Positive |
| **Status** | Execution status: Not Run, Pass, Fail, Blocked, Skipped. | Not Run (initial state) |
| **Notes / Evidence** | Execution log, screenshots, defect references when failures occur. | Defect `DEF-234` filed — login redirects to `/home` instead of `/dashboard`. |

Not every organization uses all of these fields. Adjust to your team's tooling (Jira/Xray, TestRail, Azure DevOps Test Plans, Excel) — but always ensure at minimum: ID, linked requirement, preconditions, clear steps, and specific expected results.

---

### Writing Effective Test Steps

Test steps are the core of a test case. Here are the rules for writing them well:

**Rule 1: One action per step (when possible)**

Poor:
> Step 1: Open the login page, enter email and password, and click Sign In.

Better:
> Step 1: Navigate to `/login`.
> Step 2: Enter `test@example.com` in the Email field.
> Step 3: Enter `ValidP@ss1` in the Password field.
> Step 4: Click the "Sign In" button.

Breaking steps apart makes them easier to automate, easier to troubleshoot when they fail, and clearer about exactly what was done.

**Rule 2: Be precise about UI elements and navigation**

Poor:
> Step 1: Click the button.

Better:
> Step 1: Click the "Add to Cart" button below the product description on the Product Detail page.

Precision eliminates ambiguity when different versions of a UI exist, when multiple buttons exist on a page, or when the tester is unfamiliar with the interface.

**Rule 3: State expected results that are observable and specific**

Poor:
> Expected: Login works correctly.

Better:
> Expected: The browser redirects to `/dashboard`. The top navigation bar displays "Welcome, Jane Smith." The database record for `user_id = 42` shows `last_login_at` updated to the current timestamp (within ±5 seconds).

"Works correctly" is not testable. Observable outcomes (URL change, displayed text, database state, API response body, HTTP status code, log entry) are testable.

**Rule 4: Include cleanup instructions when tests mutate data**

If a test creates a user, submits an order, or sends an email — these actions may affect subsequent tests unless cleaned up.

> Postcondition / Cleanup: After execution, delete the test user `test@example.com` via the admin API: `DELETE /admin/users/test@example.com`.

This prevents "data pollution" between test runs.

**Rule 5: Specify test data explicitly — do not say "any valid input"**

Poor:
> Enter a valid email address.

Better:
> Enter `test@example.com` (registered in the test environment; user ID 42).

Specific data makes the test reproducible. "Valid email address" leaves the executor guessing — and different executors will choose different values, making failures harder to diagnose.

---

### Positive, Negative, and Edge Case Tests

A complete test case set for any feature should cover three types of scenarios:

**Positive (Happy Path) Tests**

These verify that the system works correctly with **valid inputs in normal conditions**. They are the most visible tests — the ones a Product Owner or developer is most likely to have manually checked.

Examples for a login feature:
- Valid email + valid password → authenticate and redirect.
- Valid email (case-insensitive) + valid password → authenticate (tests case insensitivity).
- Valid credentials → session cookie set with correct expiry.

**Negative Tests**

These verify that the system **fails gracefully** with **invalid inputs, unauthorized access, or error conditions**. They are often more important than positive tests, because user errors and attacks are common.

Examples for a login feature:
- Valid email + wrong password → reject login, show error message, do not authenticate.
- Unregistered email → reject login, show generic "email or password incorrect" (do not reveal whether email exists).
- Valid credentials for locked account → reject login, show "your account has been locked" message.
- Valid credentials + CSRF token missing → reject request with 403.
- Direct API call bypassing UI validation → same behavior as form submission.
- Session token from a logged-out session → reject with 401.

**Edge Case Tests**

These verify behavior at **boundaries, extremes, empty states, and unusual combinations**. They are the scenarios most likely to be missed in ad hoc testing.

Examples for a login feature:
- Email field with maximum allowed length (254 characters per RFC 5321) → should accept.
- Email field with 255 characters → should reject with clear error.
- Password with only spaces (looks non-empty but is whitespace) → should behave per spec (reject or trim).
- Password with Unicode characters → should handle correctly.
- Simultaneous login from two devices → per spec (is the first session invalidated? both allowed?).
- Login attempt after 30 minutes of inactivity (session expiry edge) → must re-authenticate.
- Account that becomes locked mid-session (locked by admin while user is active) → behavior per spec.

---

### Test Cases and Test Automation

Well-written manual test cases are the best foundation for automated tests. The translation is straightforward:

| Manual test case field | Automated test equivalent |
|------------------------|--------------------------|
| Preconditions | `@BeforeEach` / `setup()` / fixtures |
| Test steps (actions) | Automated UI interactions or API calls |
| Expected results | Assertions (`assertEqual`, `assertThat`, status code checks) |
| Test data | Data fixtures, parameterized inputs |
| Postconditions / cleanup | `@AfterEach` / teardown methods |

When test steps are atomic and expected results are observable, the effort to automate them is minimal. When test steps are vague ("do the login thing") and expected results are subjective ("it should work"), automation is nearly impossible.

---

## Complete Example: Login Feature Test Cases

### TC-AUTH-001: Successful Login

- **ID:** TC-AUTH-001
- **Title:** Login with valid email and password — registered active user
- **Story:** AUTH-12, AC item 1
- **Preconditions:** User `test@example.com` (password: `ValidP@ss1`) is registered, active, and not locked. Test environment accessible at `https://staging.example.com`.
- **Test Data:** Email: `test@example.com` | Password: `ValidP@ss1`
- **Steps:**
  1. Navigate to `https://staging.example.com/login`.
  2. Enter `test@example.com` in the "Email address" field.
  3. Enter `ValidP@ss1` in the "Password" field.
  4. Click "Sign In."
- **Expected Result:** Browser redirects to `/dashboard`. Top navigation shows "Welcome, Test User." HTTP response set-cookie header contains `auth_token` with a 30-minute expiry. Database `users` table shows `last_login_at` updated (within ±5 seconds of current time).
- **Priority:** P1
- **Type:** Functional – Positive

---

### TC-AUTH-002: Login Rejected — Incorrect Password

- **ID:** TC-AUTH-002
- **Title:** Login rejected — valid email, wrong password
- **Story:** AUTH-12, AC item 3
- **Preconditions:** Same as TC-AUTH-001. Account is not locked.
- **Test Data:** Email: `test@example.com` | Password: `WrongPass!`
- **Steps:**
  1. Navigate to `/login`.
  2. Enter `test@example.com` in "Email address."
  3. Enter `WrongPass!` in "Password."
  4. Click "Sign In."
- **Expected Result:** Browser remains on `/login`. Error message displayed: "Invalid email or password." No session cookie set. `failed_login_count` in the database increments by 1.
- **Priority:** P1
- **Type:** Functional – Negative

---

### TC-AUTH-003: Account Lockout After 5 Failed Attempts

- **ID:** TC-AUTH-003
- **Title:** Account locks after 5 consecutive failed login attempts
- **Story:** AUTH-15, AC item 1
- **Preconditions:** User `locktest@example.com` is registered and active. `failed_login_count` is 4 (one attempt away from lockout threshold).
- **Test Data:** Email: `locktest@example.com` | Password: `WrongPass!` (x5, last attempt triggers lock)
- **Steps:**
  1. Navigate to `/login`.
  2. Enter `locktest@example.com` and `WrongPass!`.
  3. Click "Sign In." (First failed attempt — not yet locked.)
  4. Repeat Steps 2–3 four more times (total 5 failed attempts).
  5. After the 5th failure, observe the response.
  6. Attempt login with the **correct** password.
- **Expected Result after 5th failure:** Error message changes to "Your account has been locked due to multiple failed attempts. Check your email for unlock instructions." No redirect. Database `user.account_locked = true`.
  **After step 6 (correct password while locked):** Login rejected. "Your account is locked" message shown. No authentication.
- **Priority:** P1
- **Type:** Functional – Negative / Edge

---

## Summary

- A complete test case includes: **ID, title, linked requirement, preconditions, test data, steps, expected result, postconditions, and priority**.
- **Preconditions and test data are first-class fields** — most test failures labeled "flaky" are actually precondition or data problems.
- Expected results must be **specific, observable, and unambiguous** — "works correctly" is not an expected result.
- A complete test set covers **positive paths** (normal success), **negative paths** (error handling, invalid inputs), and **edge cases** (boundaries, empty states, unusual combinations).
- Well-designed manual test cases are **directly translatable to automated tests** — the structure maps one-to-one.

---

## Additional Resources

- [ISTQB Foundation Syllabus — Test Case Specification](https://www.istqb.org/) — Formal treatment of test case design.
- [Ministry of Testing — Writing Good Test Cases](https://www.ministryoftesting.com/) — Practitioner guidance on test case quality.
- [ISO/IEC/IEEE 29119-3 — Test documentation](https://www.iso.org/standard/45142.html) — Standard for test case specification structure.
- `statement-testing.md` — Coverage metrics that complement test case completeness.
