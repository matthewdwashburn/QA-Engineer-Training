# Use Case Testing

## Learning Objectives

By the end of this reading you will be able to:

- Read a use case and identify its **actors, preconditions, main flow, alternate flows, and exceptions**.
- Derive **test scenarios** systematically from each use case element.
- Explain the relationship between **use cases** and **Agile user stories** — and how they complement each other.
- Apply use case testing to **multi-actor flows** and produce test scenarios for handoff points.

---

## Why This Matters

Use cases remain a widely used specification format, particularly in enterprise organizations, systems integrations, and formally documented products. Many regulated industries (banking, healthcare, government) still produce use cases as primary requirements artifacts. Even in Agile teams, use cases are sometimes used to document complex, multi-step workflows that span multiple user stories.

As a tester, you will encounter use cases in two contexts:
1. Directly, when organizations provide use case documents as the primary specification.
2. Indirectly, when an epic or feature spans multiple stories that together implement a use case — and end-to-end validation requires testing the complete flow, not just individual stories.

Understanding how to derive tests from use cases closes the gap between formal documentation and test execution.

---

## The Concept

### What Is a Use Case?

A **use case** captures the interaction between an **actor** (a person, system, or external entity) and a **system** to achieve a **goal**. It is written from the actor's perspective, describing what the actor wants to accomplish — not how the system implements it.

Use cases were formalized by Ivar Jacobson in the 1980s and popularized as a core UML (Unified Modeling Language) artifact. Alistair Cockburn's work on "Writing Effective Use Cases" is the most widely cited practical guide.

---

### The Elements of a Use Case

A complete use case contains:

| Element | Description | Example |
|---------|-------------|---------|
| **Use Case Name** | Short, verb-phrase title of the goal. | "Withdraw Cash from ATM" |
| **Actor(s)** | Who initiates the interaction. May be primary (person) or secondary (system). | Primary: Bank Customer. Secondary: Banking System. |
| **Goal** | The business outcome the actor wants to achieve. | Customer receives requested cash. |
| **Preconditions** | Conditions that must be true before the use case begins. | Customer has a valid card, the ATM is operational, the account has sufficient funds. |
| **Main Success Scenario** | The "happy path" — the sequence of steps when everything goes right. | Insert card → Enter PIN → Select amount → Receive cash → Take card. |
| **Extensions / Alternate Flows** | Variations that still lead to success, or to a graceful alternative. | Customer selects language before PIN entry. Customer checks balance instead of withdrawing. |
| **Exceptions** | Error conditions — what happens when something goes wrong. | Wrong PIN → retry. Wrong PIN 3 times → card retained. Insufficient funds → decline. ATM out of cash → out-of-service message. |
| **Postconditions** | The system state after the use case completes. | Account balance is reduced by withdrawn amount. Transaction is logged. Receipt is offered. |

---

### Deriving Tests from Each Element

**From the Main Success Scenario:**
Derive one or more **positive tests** that verify the happy path works end-to-end with valid data. These tests verify:
- Each step executes in the correct order.
- The postconditions are satisfied.
- The system produces the correct output (UI change, data change, communication).

**From Each Extension / Alternate Flow:**
Derive one or more **positive variant tests** — each alternate path that leads to successful completion must be tested with its own scenario.

**From Each Exception:**
Derive one or more **negative tests** — each error condition must be tested to verify:
- The error is handled gracefully (no crashes, no data corruption).
- The error message is correct and user-actionable.
- The system returns to a safe state (the error does not leave a partial record).

**From Preconditions:**
Derive **setup data** (the "Given" clause) and **negative tests for precondition violations** — what happens when a precondition is not met?

**From Postconditions:**
Derive **assertion checks** — verify the final state of the system after the use case completes (database records, audit logs, emails sent, inventory counts).

---

### Structured Derivation Table

Use this template to systematically derive tests from a use case:

| Source Element | Test Type | Test Description |
|---------------|-----------|-----------------|
| Main success scenario | Positive | Execute the complete happy path with valid inputs. |
| Extension: language selection | Positive variant | Select Spanish before entering PIN; complete withdrawal in Spanish. |
| Exception: wrong PIN | Negative | Enter wrong PIN once; verify error message and retry is permitted. |
| Exception: 3 wrong PINs | Negative sequence | Enter wrong PIN 3 times; verify card is retained and account is locked. |
| Exception: insufficient funds | Negative | Attempt withdrawal of amount > account balance; verify decline with correct message. |
| Exception: ATM out of cash | Negative | System state where ATM has no cash; verify out-of-service behavior. |
| Precondition: expired card | Precondition violation | Insert expired card; verify immediate rejection before PIN entry. |
| Postcondition: balance updated | Assertion | After successful withdrawal of £50, verify account balance decreases by exactly £50. |
| Postcondition: transaction logged | Assertion | Verify audit log entry exists with timestamp, amount, account number, and ATM ID. |

---

### Multi-Actor Flows and Handoffs

Many use cases involve more than one actor. These flows require testing the **handoff points** — the moments where one actor's action creates a trigger for another actor's system.

**Example: Insurance Claim Processing**

**Primary actor:** Policyholder (files the claim).
**Secondary actor:** Claims Adjuster (reviews and approves/rejects).
**Supporting actor:** Payment System (issues payment on approval).

**Main flow:**
1. Policyholder submits claim with documentation.
2. System assigns claim to an available adjuster.
3. Adjuster reviews documentation and approves.
4. System triggers payment.
5. Policyholder receives confirmation and payment.

**Tests at handoff points:**

| Handoff | What to test |
|---------|-------------|
| Policyholder → System | Claim created with all required fields; confirmation email sent to policyholder. |
| System → Adjuster | Adjuster receives notification; claim appears in their queue with correct documentation attached. |
| Adjuster → System | Approval action updates claim status; payment trigger fires. |
| System → Payment System | Payment request sent with correct account, amount, and reference. |
| Payment System → Policyholder | Payment confirmation email sent; claim status updated to "Paid." |

Each handoff is an **integration test** opportunity — and a defect risk if the data format, timing, or authorization between actors is not correctly specified.

---

### Use Cases vs User Stories

These are complementary, not competing, formats:

| Aspect | Use Case | User Story |
|--------|---------|-----------|
| **Scope** | Often spans a complete interaction (multiple steps) | One small slice of value (sprint-sized) |
| **Author** | Business Analyst or Systems Analyst | Product Owner or team |
| **Format** | Structured narrative with formal elements | "As a [role] I want [feature] so that [value]" |
| **Detail** | Actor, preconditions, main/alternate/exception flows | Acceptance criteria (brief) |
| **Testing link** | Use case → multiple test scenarios | Story → test cases for each AC item |

**In Agile teams:**
- **Epics** sometimes map to use cases: the epic defines the full interaction, and user stories break it into sprint-sized slices.
- Individual story tests verify the story's acceptance criteria (verification).
- Use case-level tests verify the complete end-to-end journey (validation).

When all stories in an epic are "done" individually, an end-to-end use case test validates that they work together correctly — catching integration defects that story-level tests miss.

---

## Worked Example: Online Order Return

**Use Case: Process a Return Request**

**Actor:** Customer (primary), Return Processing System (secondary), Customer Service Agent (secondary for exceptions).

**Preconditions:**
- Customer is logged in.
- Order exists in the customer's order history.
- Order was delivered ≤ 30 days ago (within return window).
- Order status is "Delivered" (not "Refunded," "In Transit," or "Cancelled").

**Main Success Scenario:**
1. Customer navigates to Order History and selects the order.
2. Customer selects "Request Return."
3. Customer selects items to return and reason.
4. System validates eligibility (30-day window, order status).
5. System generates a return label and sends it to the customer's email.
6. Customer ships items using the label.
7. System detects tracked delivery to warehouse.
8. System processes refund to original payment method.

**Extensions:**
- Customer selects partial return (some items, not all).
- Customer has a damaged item and needs a prepaid priority label.

**Exceptions:**
- Order outside 30-day window → "Return window has expired. Contact customer service for assistance."
- Order not in "Delivered" status → "This order is not eligible for return. Current status: [status]."
- Customer service agent manually overrides return window for a loyalty customer.

---

**Derived test scenarios:**

| Test ID | Type | Scenario |
|---------|------|---------|
| UC-RET-01 | Positive (main flow) | Delivered order within 30 days → return label sent, refund processed on item receipt. |
| UC-RET-02 | Positive variant | Partial return: 1 of 3 items selected → label covers only selected items; refund is partial. |
| UC-RET-03 | Positive variant | Damaged item flow → priority label type issued; additional notes field mandatory. |
| UC-RET-04 | Negative | Order 31 days old → "Return window expired" message; no label generated. |
| UC-RET-05 | Negative | Order in "In Transit" status → "Order is not eligible" message with current status shown. |
| UC-RET-06 | Negative (precondition violation) | Guest user attempts return on order filed before account creation → requires account login. |
| UC-RET-07 | Multi-actor handoff | Adjuster manually overrides window for loyalty customer → return label still generates correctly; override logged in audit trail. |
| UC-RET-08 | Postcondition check | After refund: customer account shows correct refund amount and date; original order status updated to "Refunded"; confirmation email received. |

This is a complete test set derived systematically from the use case — covering the happy path, all variants, all exceptions, and postconditions.

---

## Summary

- Use cases describe **actor-system interactions** structured with actors, preconditions, main flow, extensions, exceptions, and postconditions.
- Derive tests systematically: **main flow → positive**, **each extension → positive variant**, **each exception → negative**, **preconditions → setup and precondition violation tests**, **postconditions → assertion checks**.
- Multi-actor use cases require testing **handoff points** — where one actor's action triggers another actor's workflow.
- Use cases complement user stories: **stories verify individual AC** (verification); **use cases test complete end-to-end journeys** (validation).
- State transition testing (Thursday's earlier reading) and use case testing work together: the use case defines the actor journey; the state model defines the allowable system states within that journey.

---

## Additional Resources

- [Alistair Cockburn — Writing Effective Use Cases](https://alistair.cockburn.us/use-cases/) — The definitive reference for use case authoring and testing.
- [UML Use Case Diagrams — OMG](https://www.omg.org/spec/UML/) — Visual actor/goal modeling notation.
- [IIBA — Business Analysis Body of Knowledge (BABOK)](https://www.iiba.org/) — Professional requirements analysis including use cases.
- `state-transition-diagram.md` — Complementary behavior modeling for system state during use case flows.
