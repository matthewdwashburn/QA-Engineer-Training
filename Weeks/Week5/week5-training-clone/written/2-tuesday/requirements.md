# Requirements and Testability

## Learning Objectives

By the end of this reading you will be able to:

- Distinguish **functional requirements** from **non-functional requirements** and give examples of each.
- Explain what makes a requirement **testable** — and rewrite weak requirements to make them testable.
- Describe how testers participate in **requirement analysis** activities that improve quality before coding begins.
- Define **traceability** and explain its role in coverage assurance and change impact analysis.

---

## Why This Matters

Test cases are only as good as the **oracles** they are based on — the agreed expected outcomes. Vague requirements produce vague test cases, which produce meaningless pass/fail results and endless debates about whether behavior is correct.

"The system shall be user-friendly" cannot be tested. "The system shall allow a first-time user to complete checkout within 3 minutes on a mobile device without external assistance, validated via usability testing" can be tested.

Testers who improve requirement quality up front save their entire team from writing, executing, and arguing about tests built on sand.

---

## The Concept

### What Are Requirements?

Requirements specify **what a system must do** (functional) or **how well it must do it** (non-functional). They are the foundation for:

- Development: what to build.
- Testing: what to verify and validate.
- Acceptance: what "done" means.
- Compliance: what must be evidenced.

Requirements exist at different levels of granularity — from high-level business goals to detailed user stories with acceptance criteria. In Agile teams, requirements are often expressed as **user stories** with **acceptance criteria** — lightweight but should still meet testability standards.

---

### Functional Requirements

**Functional requirements** describe **what the system does**: behaviors, features, workflows, business rules, and data transformations.

**Examples:**

| Functional Requirement | Testable? | Comment |
|----------------------|-----------|---------|
| "Users can log in with email and password." | Partially | Missing: what happens with wrong password, locked accounts, expired sessions? |
| "Given a registered active user, when they submit valid credentials, they are authenticated and redirected to /dashboard." | Yes | Observable, specific, unambiguous. |
| "The cart shall update when items are added or removed." | Partially | Missing: what is the update behavior? What triggers it? What is the expected timing? |
| "When an item is added to the cart, the cart icon count updates within 1 second without page reload." | Yes | Specific behavior, timing, and mechanism defined. |

**Functional requirement categories:**
- **Business rules:** "Discounts apply only to premium members with cart value ≥ $100."
- **Workflows:** "New user registration flow: email → verification → profile setup → dashboard."
- **Data processing:** "The import function shall parse CSV files with UTF-8 encoding and reject files > 10MB."
- **Error handling:** "If the payment gateway returns a timeout after 30 seconds, display 'Payment is processing — check your email for confirmation' and retry in the background."
- **Security behaviors:** "Sessions shall expire after 30 minutes of inactivity."

---

### Non-Functional Requirements (NFRs)

**Non-functional requirements** describe **how well** the system must perform — the quality attributes of the system. They are often called **"-ility" requirements** because many end in "-ility."

**Common NFR categories:**

| NFR Category | Example (testable) | Testing Approach |
|-------------|---------------------|-----------------|
| **Performance** | "Checkout API p95 latency ≤ 300ms under 200 RPS on staging environment." | Load testing (JMeter, k6, Locust) |
| **Scalability** | "System shall support 10,000 concurrent users without degradation beyond defined thresholds." | Capacity / stress testing |
| **Availability** | "System availability shall be ≥ 99.9% per month (max 43 min/month downtime)." | Monitoring, SLA reporting |
| **Security** | "All user passwords shall be stored as salted bcrypt hashes. No plaintext storage." | Security testing, code review |
| **Accessibility** | "All UI components shall meet WCAG 2.1 Level AA criteria." | Automated accessibility scan + manual review |
| **Maintainability** | "All functions shall have a cyclomatic complexity ≤ 10." | Static analysis tools |
| **Usability** | "First-time users shall complete registration without external help in ≤ 5 minutes." | Usability testing with target users |
| **Compatibility** | "Application shall function on Chrome 120+, Firefox 121+, Safari 17+, on Windows 10+, macOS 13+, and iOS 16+." | Cross-browser / cross-platform testing |
| **Compliance** | "PII data shall be retained for no more than 90 days after account deletion (GDPR Art. 17)." | Data lifecycle audit |

**Key rule for NFRs:** They must be **measurable**. "The system shall be fast" is not an NFR — it is a wish. "The system shall load the dashboard page in ≤ 2 seconds at p95 under 500 concurrent users on the reference hardware" is an NFR.

---

### Requirement Analysis: The Tester's Contribution Before Coding Begins

Testers are uniquely positioned to improve requirements before a single line of code is written. The activities below are all **QA (preventive)** activities — they stop defects from being introduced:

**1. Clarifying ambiguous terms**

Watch for words that sound precise but are not:
- "Fast" → ask for specific latency and percentile thresholds.
- "Secure" → ask which threats are in scope (OWASP Top 10? Authentication only? Data at rest?).
- "User-friendly" → ask for usability criteria or target user profile.
- "Complete" → ask what exactly must be included.
- "Recent" → ask for a specific time window.
- "Appropriate" → ask who defines appropriateness and how.

**2. Surfacing missing scenarios**

For every positive ("happy path") behavior, systematically ask about:
- **Negative paths:** What if invalid input is provided? What if the user is unauthorized?
- **Edge cases:** What are the boundary values? What if the quantity is 0? What if the list is empty?
- **Error handling:** What if a third-party service is unavailable? What should the user see?
- **Concurrency:** What if two users trigger the same action simultaneously?
- **State dependencies:** What preconditions must be true for this to work? What if they are not?

**3. Checking consistency**

Stories do not live in isolation. Check:
- Does Story 44's behavior contradict Story 37's assumption?
- Does the data format required by Story 51 match what Story 49 produces?
- Does the error message in Story 56 match the UX guidelines from the design system?

**4. Verifying testability**

Before accepting a story into a sprint, apply a simple testability check:
- **Observable:** Can I see the result? (A log entry? A UI change? An API response? A database record?)
- **Controllable:** Can I set up the precondition? (Create a specific user state? Trigger a specific event?)
- **Deterministic:** Does the same input produce the same output? (Or is there non-determinism to account for?)
- **Traceable:** Is the expected behavior linked to a requirement or acceptance criterion?

---

### Testability: What It Means and How to Test for It

A requirement is **testable** if a tester can design a clear test that produces an unambiguous pass or fail result.

**The SMART-T check for requirements:**

| Property | Meaning |
|----------|---------|
| **Specific** | The requirement says exactly what behavior is expected. |
| **Measurable** | The expected outcome can be quantified or clearly observed. |
| **Achievable** | The behavior is technically feasible. |
| **Relevant** | The requirement serves the stated user or business need. |
| **Timely** | The requirement is specified before implementation begins. |
| **Testable** | A test can be designed with clear pass/fail criteria. |

**Rewriting untestable requirements:**

| Poor (Untestable) | Better (Testable) |
|-------------------|-------------------|
| "The app should respond quickly." | "The product listing page shall load within 2 seconds (p95) on a 4G mobile connection with 100 products." |
| "Passwords must be strong." | "Passwords must be 8–64 characters, including at least one uppercase letter, one digit, and one special character." |
| "The system should handle errors gracefully." | "When the payment API returns a 5xx error, the system shall display 'Payment is temporarily unavailable' and log the error with a correlation ID. The user's cart shall not be cleared." |
| "Users must be able to register." | "Given a new visitor on /register, when they submit a valid unique email, name (2–50 chars), and password meeting complexity rules, they are created in the database, a confirmation email is sent within 60 seconds, and they are redirected to /welcome." |

---

### Traceability: Linking Requirements to Tests to Results

**Traceability** is the ability to follow the life of a requirement — from its origin, through design and implementation, to the tests that verify it and the defects that were found against it.

**Forward traceability:** From requirement → test cases → test results.
- "Show me which tests verify REQ-042."

**Backward traceability:** From test case or defect → requirement.
- "Which requirement does this failing test relate to? Which business rule does this defect break?"

**Why traceability matters:**

1. **Coverage assurance:** If every requirement maps to at least one test case, you know you have not forgotten anything. If a test case maps to no requirement, ask why it exists.

2. **Change impact analysis:** If REQ-042 changes, the traceability matrix tells you exactly which test cases need to be updated. Without traceability, you guess — and miss.

3. **Compliance evidence:** Regulated industries require proof that each mandatory requirement was tested. The traceability matrix is that proof.

4. **Release governance:** "All P1 requirements covered by executed, passing tests" is a clear release exit criterion — but only if you have traceability.

**In Agile, traceability is lighter but still real:**

Rather than a heavyweight RTM spreadsheet, Agile teams often use:
- Story IDs linked to test cases in a test management tool (e.g., Xray in Jira).
- Automated test names that include story IDs (e.g., `test_cart_add_item_AUTH77`).
- BDD scenario files where the scenario title includes the story reference.

The linkage must exist; the format can be lean. See `requirement-traceability-matrix.md` for the formal RTM structure.

---

## Worked Example: Acceptance Criteria Pattern (Given/When/Then)

The **Gherkin syntax** (Given/When/Then) is a popular format for writing testable acceptance criteria that work as both requirements and automated test definitions:

```gherkin
Feature: User registration

  Scenario: Successful registration with valid details
    Given a new visitor is on the /register page
    When they submit name "Jane Smith", email "jane@example.com", and a password meeting complexity rules
    Then they are redirected to /welcome
    And a confirmation email is sent to "jane@example.com" within 60 seconds
    And a user record is created in the database with the provided email

  Scenario: Registration rejected for duplicate email
    Given a user with email "jane@example.com" already exists
    When a new visitor submits the same email with valid other details
    Then they remain on /register
    And they see the message "An account with this email already exists."
    And no duplicate record is created in the database
```

This format is:
- **Testable:** Pass/fail is unambiguous.
- **Traceable:** Scenario title serves as the requirement reference.
- **Automation-ready:** BDD frameworks (Cucumber, Behave, SpecFlow) can drive tests from this syntax.
- **Readable:** Product Owners, developers, and testers all understand it.

---

## Summary

- **Functional requirements** describe behaviors; **non-functional requirements** describe quality attributes — both must be **measurable** to be testable.
- **Requirement analysis** activities (clarifying ambiguity, surfacing edge cases, checking consistency, verifying testability) are preventive QA — testers should participate in every refinement and planning session.
- A requirement is testable when it is **specific, observable, controllable, and deterministic** — producing a clear pass/fail result.
- **Traceability** links requirements to tests to results, enabling coverage assurance, change impact analysis, and compliance evidence.
- The **Given/When/Then format** is a powerful tool for writing requirements that are simultaneously testable, traceable, and automation-ready.

---

## Additional Resources

- [IREB (International Requirements Engineering Board)](https://www.ireb.org/en/) — Professional requirements engineering body with foundation-level certification.
- [ISO/IEC/IEEE 29148:2018 — Requirements Engineering](https://www.iso.org/standard/72089.html) — International standard for requirements lifecycle processes.
- [Agile Alliance — INVEST User Stories](https://www.agilealliance.org/glossary/invest/) — Story quality criteria: Independent, Negotiable, Valuable, Estimable, Small, Testable.
- [Cucumber Documentation — Gherkin syntax](https://cucumber.io/docs/gherkin/) — Writing Given/When/Then scenarios for BDD.
- `requirement-traceability-matrix.md` — Formal RTM structure and lightweight Agile alternatives.
