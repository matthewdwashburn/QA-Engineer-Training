# Error Guessing

## Learning Objectives

By the end of this reading you will be able to:

- Define **error guessing** as a technique that leverages experience and intuition to identify likely defects.
- Use **fault taxonomies** to structure guessing systematically and make it teachable.
- Apply **common error patterns** (null/empty, boundaries, concurrency, encoding, timezone) to generate targeted test ideas.
- Combine error guessing with formal techniques (EP, BVA, state testing) as a complementary "risk addition" layer.

---

## Why This Matters

No formal test design technique generates every valuable test case. EP and BVA handle boundaries and input classes. Decision tables handle business rule combinations. State models handle workflow sequencing. But all of these techniques work from specifications — they find defects in what was specified.

What about defects in what was NOT specified? What about the "this always breaks in systems like this" knowledge that experienced testers carry? What about the common coding mistakes that appear in every codebase regardless of requirements?

Error guessing is the technique that fills this gap — turning professional experience and intuition into a **structured set of targeted test hypotheses**. Combined with systematic techniques, it rounds out a complete test approach.

---

## The Concept

### What Is Error Guessing?

**Error guessing** is an experience-based test design technique where the tester uses **knowledge of common defect patterns** — from past projects, defect history, domain knowledge, and heuristics — to hypothesize where defects are likely to hide, and designs tests to investigate those hypotheses.

The ISTQB definition: "A technique used to anticipate the occurrence of errors, defects, and failures, based on the tester's knowledge, including:
- How the application has worked in the past.
- What types of errors are typically made.
- Failures that have occurred in other applications."

**Error guessing is NOT random.** It is **directed** by pattern knowledge and made **reproducible** by using lists, taxonomies, and checklists. The difference between a junior tester "trying random things" and a senior tester using error guessing is the structured application of accumulated knowledge.

---

### Sources of Error Guessing Ideas

**1. Past defects in this codebase**

If the payment module has had 4 currency rounding defects in the past 6 sprints, "currency rounding at boundary amounts" is a high-probability defect area for the next sprint. Keep a defect history log — it is one of the most valuable testing assets a team has.

**2. Common coding mistakes by defect type**

Experienced testers know the categories of defects that appear repeatedly across software systems:

| Defect Category | Common Examples |
|----------------|-----------------|
| **Null / empty handling** | Null pointer exceptions when an optional field is absent; empty string treated as valid; null returned when empty collection expected. |
| **Boundary and off-by-one** | Loop ends one iteration early; array index out of bounds; < instead of <=. |
| **Rounding and precision** | Floating point arithmetic producing 0.1 + 0.2 ≠ 0.3; currency truncated instead of rounded; intermediate rounding causing cascade errors. |
| **Timezone and locale** | System time vs user time mismatch; DST causing events to fire twice or not at all; date comparison across timezone boundaries. |
| **Encoding** | UTF-8 vs Latin-1 mismatch; emoji or extended characters causing field truncation; international characters in names causing form rejection. |
| **Concurrency and race conditions** | Two users submitting the same transaction simultaneously; double-click processing the same order twice; inventory decrement not atomic. |
| **Authorization bypass** | Accessing another user's resource by guessing/manipulating IDs; privilege escalation through URL manipulation; cached responses returning stale authorization. |
| **State management** | Back button returning to a completed checkout; session data persisting after logout; state not cleared between test runs. |
| **Integration and contract** | API consumer sending a request the producer no longer accepts; field added to response that old clients ignore (breaking hidden assumptions). |
| **Configuration** | Hardcoded values that differ between environments; feature flag not toggled correctly in staging; environment variable missing in production. |

**3. Domain-specific knowledge**

E-commerce testers know: "Coupon stacking often breaks." Financial testers know: "Currency conversion rounding at daily FX close is a defect hotspot." Healthcare testers know: "Date of birth calculation at the annual rollover edge case fails in age-based eligibility systems." This knowledge is acquired over time and applied as error guessing.

**4. Security heuristics**

Security testing is a specialized form of error guessing based on known vulnerability patterns:
- **SQL injection:** Input fields that might be passed unsanitized to database queries.
- **XSS (Cross-Site Scripting):** Input fields that render user content in browser HTML.
- **IDOR (Insecure Direct Object Reference):** Changing a resource ID in a URL or API parameter to access another user's resource.
- **Broken authentication:** Brute force, credential stuffing, weak session tokens.
- **CSRF (Cross-Site Request Forgery):** Missing or bypassable CSRF tokens on state-changing endpoints.

The [OWASP Top 10](https://owasp.org/www-top-10/) is a structured fault taxonomy for web application security — use it as an error guessing checklist.

**5. User behavior patterns**

Real users do things developers and testers don't anticipate:
- Copy-paste content from Word (with curly quotes, em-dashes, and non-breaking spaces).
- Leave browser tabs open for hours before submitting a form (session expiry).
- Hit the browser's Back button after completing a transaction.
- Submit a form by pressing Enter before filling all fields.
- Right-click → "Inspect" and modify form field values directly in the browser.
- Use a screen reader, zoom to 200%, or navigate only by keyboard.

---

### Fault Taxonomies: Making Guessing Systematic

A **fault taxonomy** is an organized classification of defect types. Using a taxonomy turns error guessing from "whatever comes to mind" into a systematic walk through a checklist of defect categories.

**Example: Data-related fault taxonomy**

```
Data Defects
├── Missing / null data
│   ├── Required field absent (null, empty string)
│   └── Optional field absent when code assumes it exists
├── Out-of-range data
│   ├── Numeric value exceeds maximum
│   └── String length exceeds column limit
├── Format issues
│   ├── Date in wrong format (ISO vs locale-specific)
│   └── Phone number missing country code
├── Type mismatches
│   ├── Integer where decimal expected
│   └── String where boolean expected
└── Special characters
    ├── SQL injection characters (<, >, ', ")
    ├── HTML/script injection (<script>)
    └── International characters (é, ñ, 中文)
```

Applying this taxonomy to a user profile update feature immediately generates a list of targeted test ideas — without requiring inspiration or domain-specific knowledge.

---

### Combining Error Guessing with Formal Techniques

Error guessing is a **supplement**, not a replacement, for formal test design. Use it at two points in the test design process:

**Before writing formal test cases:**
Walk through common defect categories and note any that are relevant to the feature. Add these as test case rows or charter items alongside EP/BVA/decision table cases.

**After writing formal test cases:**
Review the set and ask: "What do I know from experience that my formal cases might have missed?" This "error guessing review" often catches:
- Missing null/empty tests.
- Missing concurrency scenarios.
- Missing encoding or timezone edge cases.
- Missing authorization bypass scenarios.

**The "defect seeding" thought experiment:**
Ask yourself: "If I were the developer and I was going to introduce a defect here, where would I accidentally put it?" This question — looking at the code and requirements through the developer's perspective — often generates exactly the right error guessing test.

---

### Documenting Error Guessing Tests

Error guessing should be documented like any other test — not treated as informal, undocumented activity. When you derive a test from error guessing:
- Record it as a test case with the same fields as your formal cases.
- Note in the description or label that the source is error guessing (e.g., "EG: concurrency — double submit risk").
- If the test finds a defect, add a corresponding regression case so future regression covers it.

**Bias awareness:**

Error guessing is biased toward your own experience. If you have worked primarily in e-commerce, you may miss defects common in, say, embedded systems or regulatory compliance features. Mitigate this by:
- Using fault taxonomies to systematically cover categories outside your experience.
- Pairing with testers who have different domain backgrounds.
- Reviewing production incident history — escaped defects reveal blind spots.
- Using customer-reported defect data as a guide to areas your error guessing is missing.

---

## Worked Examples

### Example 1: E-commerce Checkout Error Guessing

**Feature:** Apply coupon code at checkout.

| Error Guessing Hypothesis | Test Idea | Risk |
|--------------------------|-----------|------|
| What if two users apply the same single-use coupon simultaneously? | Concurrency test: two sessions apply same code at exactly the same time. | Double redemption — financial loss. |
| What if the coupon percentage causes a fractional cent? | Order total £33.33 with 10% discount = £3.333 — how is it rounded? | Inaccurate charges; merchant/customer dispute. |
| What if the user applies a coupon, removes items, and the discount now exceeds the cart total? | Apply SAVE20 to a £50 cart; remove items until cart = £5. What is the total? | Negative total; free order; financial loss. |
| What if the coupon code is valid but the session expires during application? | Start checkout, apply coupon, wait for session to expire, then click Place Order. | Coupon applied but order fails; coupon status ambiguous. |
| What if the coupon has an accent in the code? (Internationalization) | Try code "SUMM£R20" or "20%OFF" with special chars. | Code rejected or improperly matched due to encoding. |

---

### Example 2: User Registration Error Guessing

**Feature:** New user registration form.

| Error Guessing Hypothesis | Test | Risk |
|--------------------------|------|------|
| Unicode name causing DB truncation | Enter name: "王小明" (Chinese characters, 3 chars but may be 9 bytes UTF-8). | Truncation at column byte limit — data loss. |
| Email with plus sign (valid per RFC) | "user+test@example.com" — many systems reject this incorrectly. | User cannot register with their actual email. |
| Password with `'` or `"` causing SQL injection | Password: `'; DROP TABLE users; --` | SQL injection if input is not parameterized. |
| Duplicate registration request (double-click or retry) | Submit the form twice in rapid succession. | Duplicate user accounts created; DB constraint violation exposed as 500 error. |
| Timing attack on email existence check | Compare response time for registered vs unregistered email — timing difference reveals account existence. | Account enumeration security vulnerability. |
| Maximum field length just over the limit | 255-character name, 256-character name — what happens? | Field truncation vs error message — which is the behavior? |

---

## Summary

- **Error guessing** applies professional experience, defect history, and pattern knowledge to generate targeted test hypotheses — filling gaps that formal techniques leave.
- **Fault taxonomies** (null/empty, boundary, encoding, concurrency, authorization, integration, configuration) make guessing systematic and teachable.
- Combine error guessing with formal techniques: use EP/BVA/decision tables for systematic coverage; add error guessing for experience-based risk targeting.
- Document error guessing tests like any other test case — including the rationale ("EG: concurrency risk").
- Be aware of **experience bias** — use taxonomies, diverse team perspectives, and production incident history to cover areas outside your direct experience.

---

## Additional Resources

- [OWASP Testing Guide](https://owasp.org/www-project-web-security-testing-guide/) — The most comprehensive structured fault taxonomy for web application security testing.
- [Ministry of Testing — Test Heuristics Cheat Sheet](https://www.ministryoftesting.com/) — Community-curated list of common error patterns and heuristics.
- [Rapid Software Testing — Heuristics](https://www.satisfice.com/) — James Bach's materials on heuristic testing and error guessing.
- [ISTQB Foundation Syllabus — Error Guessing](https://www.istqb.org/) — Official technique definition.
- `exploratory-testing.md` (Wednesday) — Charter-based exploration that uses heuristics and error guessing as session guidance.
