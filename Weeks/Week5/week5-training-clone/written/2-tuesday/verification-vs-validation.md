# Verification vs Validation

## Learning Objectives

By the end of this reading you will be able to:

- Recite and explain the classic V&V questions: **"building the product right"** and **"building the right product."**
- Map common software **testing activities** to verification, validation, or both.
- Explain why it is possible to **verify perfectly and still fail validation**.
- Distinguish V&V from QA/QC — related but not the same concepts.

---

## Why This Matters

When testers write test cases from acceptance criteria, they are performing verification. When users discover the delivered product does not meet their actual workflow needs, that is a validation failure. Both types of failures reach production regularly — and they have different root causes, different remedies, and different stakeholders.

Understanding the V&V distinction helps you:
- Frame test activities and results in terms that resonate with Product Owners and business stakeholders.
- Identify which type of testing evidence is missing from a release decision.
- Advocate for user-facing testing activities (UAT, usability studies) that are commonly underinvested.

---

## The Concept

### Definitions

**Verification** answers: **"Are we building the product right?"**

Verification checks whether the software **conforms to its specification** — whether it implements what was agreed. The reference point is an **artifact**: a requirements document, a user story with acceptance criteria, an API contract, a design specification.

- If the spec says "the form shall validate email format before submission" and the form validates correctly, verification passes.
- If the spec says "the API shall return HTTP 201 for a successful POST" and it returns 200, verification fails.

Verification activities focus on **conformance**: does the implementation match what was specified?

**Validation** answers: **"Are we building the right product?"**

Validation checks whether the software **meets actual user and business needs** — whether it delivers the intended value in the real world. The reference point is **real users, real workflows, and real business outcomes**.

- If the form validates email format but users on mobile devices find the error message obscured by the keyboard, that is a validation failure — the spec was satisfied, but the user need was not.
- If the API returns the correct status code but the data format is unusable for the downstream system the customer actually uses, that is a validation failure.

Validation activities focus on **fitness for purpose**: does the system solve the problem it was built to solve?

---

### A Simple Analogy

Imagine you asked a contractor to build you a bookshelf. They follow your blueprint exactly:
- Width: 80cm ✅
- Height: 180cm ✅
- Three shelves ✅
- Oak wood ✅

**Verification passes:** the bookshelf matches the specification.

However, when it arrives, you discover:
- The shelves are spaced 40cm apart, but your tallest books are 45cm tall.
- The bookshelf fits perfectly in the corner but the doors of your study now cannot open fully.
- The oak stain does not match the rest of your furniture.

**Validation fails:** the bookshelf does not serve its actual purpose in your actual context.

The contractor did their job perfectly — the specification was wrong. This is one of the most common and expensive failure modes in software: building exactly what was specified, but the specification did not capture what was actually needed.

---

### Verification vs Validation in Practice

**Verification activities (conformance to specification):**

| Activity | What it verifies |
|---------|-----------------|
| Unit testing against acceptance criteria | Does the function produce the documented output for given inputs? |
| API contract testing | Does the endpoint return the agreed schema, status codes, and headers? |
| Regression testing | Do previously working behaviors still conform to their specifications? |
| Requirements review | Is the requirement precise, complete, and unambiguous enough to build and verify against? |
| Code review | Does the implementation match the design specification? |
| Accessibility audit against WCAG 2.1 | Does the interface meet the stated accessibility standard? |

**Validation activities (fitness for purpose in real world):**

| Activity | What it validates |
|---------|-----------------|
| User Acceptance Testing (UAT) | Do real users accomplish their actual goals with the system? |
| Usability testing | Can users complete key tasks efficiently without assistance? |
| A/B testing | Does feature variant A produce better real-world outcomes than variant B? |
| Beta testing / early access | Does the product solve real user problems at scale? |
| Customer interviews and feedback sessions | Are users actually satisfied? What needs are not being met? |
| Sprint Review with stakeholders | Does the working increment demonstrate value that stakeholders recognise? |
| Production analytics and user behaviour monitoring | Are users actually using features as intended? What do drop-off rates reveal? |

---

### The Crucial Insight: You Can Verify Perfectly and Still Fail Validation

This is the most important takeaway from this topic, and it happens constantly in software delivery.

**Scenario: Mobile Login Feature**

The user story acceptance criteria say:
- Given a registered user, when they enter valid email and password, they are authenticated and land on the dashboard.
- Given an invalid password, the user sees "Invalid email or password" and login fails.

**Verification result:** All acceptance criteria tested and passing. All edge cases (empty fields, wrong password, account locked) verified. Regression passing.

**Validation result:** During UAT, field workers using the app while wearing gloves report that the password field is too small to tap accurately on mobile devices — they frequently trigger "invalid password" by mis-tapping. The error message "Invalid email or password" is also displayed at the bottom of the screen, which is below the fold on smaller phones. Users give up and call the help desk instead.

The software is **perfectly verified** — it does exactly what the spec says. But it is a **validation failure** — it does not serve the real users in their real context.

The resolution requires updating the acceptance criteria to include usability criteria: minimum tap target size, error message placement, and perhaps a "show password" toggle. Then re-verify against the improved spec, and re-validate with field workers.

---

### Relating V&V to Test Levels

While not absolute, there is a general tendency:

| Test Level | Primarily Verification or Validation? |
|-----------|--------------------------------------|
| Unit tests | Verification (against coded spec/contracts) |
| Component integration tests | Verification (against interface contracts) |
| System tests | Both — functional verification + user scenario validation |
| UAT | Primarily validation |
| Exploratory testing | Often validation-oriented (finding gaps between spec and real-world fitness) |
| Production monitoring | Validation (are users achieving their goals in the real system?) |

---

### V&V Is Not the Same as QA/QC

Students often conflate these concepts. Here is the distinction:

- **QA vs QC** is about **process vs product**: QA improves how software is built; QC inspects what was built.
- **Verification vs Validation** is about **spec conformance vs real-world fitness**: verification checks against requirements; validation checks against user and business value.

A single QC activity (e.g., running system tests) can serve **verification** (checking conformance to acceptance criteria) **and** validation (checking fitness for real workflows) — depending on what questions the test is answering.

Similarly, a QA activity like improving acceptance criteria quality serves both: better AC makes verification more meaningful, and including user-context scenarios in AC makes verification testing also capture some validation concerns.

---

## Worked Example: V&V for a Password Strength Validator

**Feature:** A password strength indicator that shows "Weak," "Medium," or "Strong" as the user types.

**Acceptance criteria (simplified):**
- Weak: fewer than 8 characters OR only lowercase letters.
- Medium: 8+ characters with at least one uppercase letter or one digit.
- Strong: 8+ characters with uppercase, digit, AND special character.

**Verification test cases:**
- TC-V-01: Type "abc" → expect indicator shows "Weak" ✅
- TC-V-02: Type "Password1" → expect indicator shows "Medium" ✅
- TC-V-03: Type "P@ssw0rd" → expect indicator shows "Strong" ✅
- TC-V-04: Type "" (empty) → expect indicator is absent or hidden ✅

Verification passes. The implementation conforms to the specification.

**Validation questions (UAT / usability):**
- Do users understand what they need to do to achieve "Strong"? (Is the feedback actionable, or just a label?)
- Is the indicator visible to users with color blindness? (Color alone may not be sufficient — a text label is needed.)
- Do users trust the "Strong" label? One user says "P@ssw0rd is a famous weak password — your tool says Strong." Is the algorithm's real-world effectiveness validated?
- Does the indicator update in real time as the user types, or only on blur/submit? (Usability expectation vs spec.)

The acceptance criteria were met. But the usability and real-world effectiveness of the feature are **not fully validated** without user-facing testing.

---

## Summary

- **Verification:** "Are we building the product right?" — conformance to specification, requirements, and contracts.
- **Validation:** "Are we building the right product?" — fitness for user and business needs in the real world.
- You can **verify perfectly and still fail validation** — if the specification did not capture what users actually need.
- Strong releases provide **evidence for both**: formal test evidence for verification, user-facing evidence (UAT, demos, feedback) for validation.
- **V&V is not the same as QA/QC**: QA/QC is about process vs product inspection; V&V is about the reference point (spec vs user reality).

---

## Additional Resources

- [IEEE 1012 — Standard for Software Verification and Validation](https://standards.ieee.org/) — Systems and software V&V standard.
- [ISTQB Glossary — Verification / Validation](https://glossary.istqb.org/) — Canonical testing vocabulary.
- [SWEBOK — Software Testing chapter](https://www.computer.org/education/bodies-of-knowledge/software-engineering/) — Engineering-level treatment of V&V in the software context.
- `testing-objectives.md` — How verification and validation connect to the four core objectives of testing.
