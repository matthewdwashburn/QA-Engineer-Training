# Positive and Negative Testing

## Learning Objectives

By the end of this reading you will be able to:

- Precisely define **positive testing** and **negative testing** and explain the different purposes they serve.
- Explain why **both categories are required** for professional test coverage — and why teams that overinvest in positive tests leave significant gaps.
- Apply **equivalence partitioning and BVA** to select efficient negative test cases.
- Recognize that **negative tests are specification requirements** — not optional extras.

---

## Why This Matters

"Our tests all pass" means very little if those tests only verify the happy path. Real users make mistakes, send invalid data, operate under poor network conditions, and occasionally attempt unauthorized actions. Production failures are dominated not by failures of the happy path — but by failures of the system to handle the unhappy path gracefully.

Understanding positive and negative testing as a framework for thinking about coverage gaps is foundational. When a senior tester reviews your test set and asks "where are your negative tests?", they are asking whether you have planned for the failures — not just the successes.

---

## The Concept

### Positive Testing

**Positive testing** (also called "happy path testing") verifies that the system **behaves correctly when presented with valid, expected inputs in normal conditions**. It answers the question: "Does the feature work when everything is as it should be?"

**Characteristics:**
- Valid data, within specified ranges and formats.
- Authorized users performing permitted actions.
- Expected system state (no precondition violations).
- Normal network, infrastructure, and timing conditions.

**Purpose:**
- Verify that the core value delivery works.
- Confirm that acceptance criteria for the happy path are met.
- Provide basic confidence to the Product Owner that the feature is functional.

**Examples:**
- User logs in with valid email and correct password → authenticated and redirected to dashboard.
- User adds an in-stock item to cart → cart count updates, item added.
- API called with valid parameters → returns 200 with correct response body.
- File uploaded within size limit → uploaded successfully, processed, confirmation shown.

---

### Negative Testing

**Negative testing** verifies that the system **handles invalid inputs, unauthorized access, error conditions, and edge cases gracefully** — producing the correct error behavior as specified.

**Characteristics:**
- Invalid, out-of-range, or malformed data.
- Unauthorized users attempting actions beyond their permissions.
- Violated preconditions (system in a state that does not permit the action).
- Abnormal conditions (network failures, service unavailability, timeouts).

**Purpose:**
- Verify that the system **fails gracefully** — with correct error messages, no crashes, no data corruption.
- Confirm that error handling paths are implemented as specified.
- Ensure the system **protects itself and its users** from invalid or malicious inputs.
- Provide evidence of robustness and security.

**Examples:**
- Login with wrong password → rejected with non-revealing message; no authentication.
- API called with missing required parameter → returns 400 with a meaningful error message.
- File uploaded over the size limit → rejected with clear error message; no partial upload stored.
- User attempts to access another user's account data by manipulating the ID in the URL → returns 403.

---

### Why Negative Tests Are Not Optional

A common misconception is that negative tests are "extra" work — a nice-to-have if there is spare time. This is wrong for several reasons:

**1. Negative paths are specified behavior:**
If the requirements say "the system shall reject passwords shorter than 8 characters with the message 'Password must be at least 8 characters'", then the negative test ("submit a 7-character password; verify the rejection and exact message") is a first-class requirement test — exactly as mandatory as the positive "submit a valid password, verify authentication."

**2. Production failures are dominated by error path failures:**
Happy path code is written and reviewed carefully. Error handling code is often written quickly, not reviewed carefully, and almost never tested by developers manually. The corner cases where real users produce unexpected input are exactly where the untested code paths live.

**3. Security vulnerabilities live in negative test territory:**
SQL injection, cross-site scripting, unauthorized resource access, and authentication bypass are all negative scenarios — inputs and actions the system should reject. A system that has only been positive-tested has no confirmed security posture.

**4. UX quality is partly determined by error handling:**
"Your password must be at least 8 characters" is a better user experience than "Error 500: Internal Server Error." The quality of error messages, error recovery flows, and user guidance in failure cases is part of the product's user experience — and it is only tested through negative testing.

---

### Using Equivalence Partitioning to Structure Negative Tests

The most systematic way to ensure you have not missed negative test cases is to apply equivalence partitioning to identify all invalid classes:

**For every input field, ask:**
1. What makes this input invalid (too short, too long, wrong type, out of range, wrong format)?
2. Does each type of invalidity produce a different error response?
3. If yes, each type of invalidity is a separate equivalence class — and needs a representative negative test.

**Example: User registration password field**

| Invalidity type | Invalid class representative | Expected negative behavior |
|-----------------|------------------------------|---------------------------|
| Too short (< 8 chars) | "abc1!A" (6 chars) | "Password must be at least 8 characters." |
| Too long (> 128 chars) | 129-character string | "Password must not exceed 128 characters." |
| Missing uppercase | "abcdefg1!" | "Password must contain at least one uppercase letter." |
| Missing digit | "Abcdefgh!" | "Password must contain at least one digit." |
| Missing special character | "Abcdefgh1" | "Password must contain at least one special character." |
| Contains spaces | "Abc 1234!" | "Password must not contain spaces." (if specified) |

Each row is a negative test. Each negative test verifies a specific error handling requirement. This is systematic, not guesswork.

---

### Boundary Values for Negative Tests

BVA is equally important for negative testing: the boundary value is often the most critical negative test case.

**Example: Age field (must be 18–65):**

| Value | Test type | Expected behavior |
|-------|-----------|-----------------|
| 17 | **Negative (just below lower boundary)** | "Minimum age is 18." |
| 18 | Positive (at lower boundary) | Accepted. |
| 65 | Positive (at upper boundary) | Accepted. |
| 66 | **Negative (just above upper boundary)** | "Maximum age is 65." |

The negative boundary values (17 and 66) are the most defect-rich values — they test the exact operator (`>` vs `>=`) used in the validation logic.

---

### Authorization and Permission Testing: A Critical Negative Test Category

Authorization testing is a category of negative testing that tests whether **users can only access and modify what they are authorized to**:

- **User A should not be able to view User B's orders:** Test by logging in as User A and attempting to access User B's order URL directly.
- **Standard user should not be able to access admin functions:** Test by logging in as a standard user and attempting admin API endpoints.
- **Expired session should not permit API access:** Test by using a session token after logout or session expiry.

These tests require deliberate planning — they are invisible to positive testing because positive testing uses authorized users performing authorized actions.

---

### Negative ≠ Adversarial Attitude

It is important to frame negative testing correctly — both personally and when communicating with developers. Negative tests are **not** "trying to break things" or finding ways to make developers look bad. They are:

- **Specifications** — the error handling behavior is as much part of the requirement as the happy path.
- **User advocacy** — real users will hit these negative paths; the tester is verifying the experience is correct.
- **Security and robustness** — the team needs to know the system's defensive posture before release.

When a negative test finds a defect ("the system returns a 500 error instead of a validation message"), the response should be: "Good — we found it before a user did. Let's fix it."

---

## Worked Example: File Upload Feature

**Feature:** Users can upload a profile photo. Requirements:
- Accepted formats: JPG, PNG, GIF.
- Maximum file size: 5 MB.
- Uploaded image must be displayed in the user's profile after upload.

**Positive tests:**

| Test | Input | Expected |
|------|-------|---------|
| T1 | Valid JPG, 1 MB | Upload succeeds; photo displayed in profile. |
| T2 | Valid PNG, 4.9 MB | Upload succeeds; photo displayed in profile. |
| T3 | Valid GIF, 50 KB | Upload succeeds; photo displayed in profile. |

**Negative tests:**

| Test | Input | Expected |
|------|-------|---------|
| T4 | PDF file | "Unsupported file type. Please upload JPG, PNG, or GIF." |
| T5 | EXE file | "Unsupported file type." (Security: no execution of uploaded files.) |
| T6 | JPG with .png extension (spoofed type) | System validates actual file type, not just extension — rejected or safely handled. |
| T7 | 5.1 MB JPG (just over limit) | "File size must not exceed 5 MB." No partial upload stored. |
| T8 | Empty file (0 bytes) | "Uploaded file is empty. Please select a valid photo." |
| T9 | No file selected | "Please select a file before uploading." Form remains submittable state. |
| T10 | Valid JPG — non-image binary disguised as JPG | System validates file content, not just extension. Rejected with clear error. |
| T11 | Upload attempt without login | Redirect to login page; no file stored. |

The 3 positive tests verify the core happy path. The 8 negative tests verify that file type enforcement, size limits, empty/missing file handling, and authorization are all implemented correctly. Both are essential.

---

## Summary

- **Positive testing** verifies the system works with valid inputs and authorized users. **Negative testing** verifies the system handles invalid inputs, error conditions, and unauthorized access gracefully.
- Negative tests are **first-class requirements** — they test specified error behavior, not optional extras.
- Use **equivalence partitioning** to identify all invalid input classes, and **BVA** to target boundaries — systematically ensuring all error paths are covered.
- **Authorization testing** is a critical negative test category: verify that users can only access what they are authorized to, using direct manipulation and session tampering scenarios.
- Frame negative testing professionally: finding error path defects before users do is valuable, collaborative quality work — not adversarial "trying to break things."

---

## Additional Resources

- [ISTQB Foundation Syllabus — Black-Box Testing Techniques](https://www.istqb.org/) — Equivalence partitioning and BVA as the systematic basis for positive and negative test selection.
- [OWASP Testing Guide](https://owasp.org/www-project-web-security-testing-guide/) — Comprehensive negative/security testing scenarios for web applications.
- `equivalence-partitioning.md` — Systematic invalid class identification.
- `boundary-value-analysis.md` — Boundary test selection for negative test boundaries.
- `error-guessing.md` — Experience-based additions to systematic negative test sets.
