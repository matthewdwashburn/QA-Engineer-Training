# State Transition Testing

## Learning Objectives

By the end of this reading you will be able to:

- Define the core terms of state modeling: **state, transition, event, guard, and action**.
- Build a **state transition diagram** and derive a corresponding **state table**.
- Design a **test set** that covers valid transitions (0-switch coverage) and invalid transitions.
- Apply state transition testing to realistic software scenarios: user accounts, order workflows, and session management.

---

## Why This Matters

Many defects are not simple input validation errors — they are **state errors**: behaviors that depend not just on what input is provided, but on what state the system is currently in. Examples:

- A user clicks "Submit" twice before the first submission completes — what happens?
- A locked account attempts a password reset — is it allowed?
- An order in "Shipped" status is marked as "Refunded" — without going through "Delivered" first?
- A shopping cart attempts checkout when it contains out-of-stock items added while the session was long-lived.

These defects cannot be found by equivalence partitioning or BVA alone — they require knowledge of **state sequences**. State transition testing makes these behaviors explicit, visible, and systematically testable.

---

## The Concept

### Core Terminology

| Term | Definition | Example |
|------|-----------|---------|
| **State** | A stable condition the system occupies between events. | `Account: Active`, `Cart: Empty`, `Order: Processing` |
| **Transition** | A change from one state to another. | Active → Locked |
| **Event** | The stimulus that triggers a transition. | 5 failed login attempts |
| **Guard** | A condition that must be True for a transition to occur. | `failedAttempts >= 5` |
| **Action** | A side effect that occurs when a transition fires. | `sendLockoutEmail()`, `reserveInventory()` |

A state transition notation on a diagram: `Event [Guard] / Action`

Example: `SubmitLogin [failedAttempts >= 5] / sendLockoutEmail()` → transition from `Active` to `Locked`.

---

### State Transition Diagrams

A **state transition diagram** represents the system's behavior visually:
- **Nodes** = states (circles or rectangles).
- **Directed arrows** = transitions.
- Arrow labels = `Event [Guard] / Action`.
- **Initial state** is indicated by a filled circle → arrow.
- **Final states** (if any) by a double circle.

**Example: User Account State Diagram**

```
        Register
[Start] ─────────────► [Pending Verification]
                              │
                      VerifyEmail
                              │
                              ▼
                          [Active] ◄──────────────────┐
                              │                        │
            5 failed          │                   AdminUnlock
            attempts          │                        │
                              ▼                        │
                          [Locked] ───────────────────►┘
                              │
                      DeleteAccount
                              │
                              ▼
                          [Closed]
```

**Transitions:**
1. Register → [Pending Verification]
2. VerifyEmail [token valid and not expired] → [Active]
3. SubmitLogin [failedAttempts >= 5] / sendLockoutEmail → [Locked]
4. AdminUnlock → [Active]
5. DeleteAccount → [Closed]

---

### State Tables

A **state table** represents the same information in tabular format — easier to systematically derive test cases from:

- **Rows:** Current states.
- **Columns:** Events.
- **Cells:** Next state (on valid transition) or "Error/Ignore" (on invalid transition).

**User Account State Table:**

| Current State | Register | VerifyEmail (valid) | SubmitLogin (5 failures) | AdminUnlock | DeleteAccount |
|--------------|---------|-------------------|--------------------------|-------------|--------------|
| [Start] | Pending Verification | — | — | — | — |
| Pending Verification | — | Active | — | — | — |
| Active | — | — | Locked | — | Closed |
| Locked | — | — | Error (ignore) | Active | Closed |
| Closed | — | — | Error | — | Error (already closed) |

**Empty cells reveal specification gaps:**
- What happens if a user attempts to call `VerifyEmail` on an already `Active` account? Is it a no-op, or an error?
- Can a `Locked` account be deleted directly without unlocking first?
- Can a `Closed` account be reopened?

Completing the state table forces stakeholders to answer these questions explicitly — before a defect sneaks in.

---

### Designing Tests from State Models

**Step 1: 0-Switch Coverage (Every Valid Transition Once)**

The minimum test set for state transition testing exercises every valid transition at least once. This is called **0-switch coverage** (or **all-transitions coverage**).

For the user account model:

| Test | Starting State | Event | Expected End State |
|------|----------------|-------|-------------------|
| T1 | [Start] | Register | Pending Verification |
| T2 | Pending Verification | VerifyEmail (valid token) | Active |
| T3 | Active | SubmitLogin (5 failures) | Locked |
| T4 | Locked | AdminUnlock | Active |
| T5 | Active | DeleteAccount | Closed |
| T6 | Locked | DeleteAccount | Closed |

Six tests cover all defined valid transitions — a minimal but defensible test set.

**Step 2: Invalid Transition Tests**

After covering valid transitions, test what happens for **events that should not be allowed** in a given state:

| Test | Current State | Invalid Event | Expected Behavior |
|------|--------------|--------------|------------------|
| T7 | Active | AdminUnlock (already active) | Error or no-op per spec |
| T8 | Locked | SubmitLogin (attempt while locked) | Error: "Account is locked. Contact support." |
| T9 | Closed | SubmitLogin | Error: "This account no longer exists." |
| T10 | Pending Verification | SubmitLogin (before verification) | Error: "Please verify your email first." |

Invalid transition tests are where many production defects lurk: developers test the happy path, but users find unusual state sequences.

**Step 3: Sequence Tests (1-Switch Coverage for High-Risk Paths)**

**1-switch coverage** tests every pair of consecutive transitions — every sequence of length 2. This catches defects that only appear when two specific transitions occur in sequence.

For example: `Register → VerifyEmail → SubmitLogin (5 times) → AdminUnlock → SubmitLogin (5 times again)`

Does the `failedAttempts` counter reset after unlocking? If not, one more failed login will re-lock the account. A sequence test reveals this; a single-transition test would not.

---

### State Transition Testing for Order Workflows

Order management systems are a classic state transition testing domain:

**Order Status States:**
`Pending → Confirmed → Processing → Shipped → Delivered → Closed`
with branches: `→ Cancelled` (from Pending, Confirmed, Processing)
and: `→ Refunded` (from Delivered or Closed)

**State table excerpt:**

| Current State | Confirm | Cancel | Ship | Deliver | Refund |
|--------------|---------|--------|------|---------|--------|
| Pending | Confirmed | Cancelled | Error | Error | Error |
| Confirmed | Error | Cancelled | Processing | Error | Error |
| Processing | Error | Cancelled | Shipped | Error | Error |
| Shipped | Error | Error | Error | Delivered | Error |
| Delivered | Error | Error | Error | Error | Refunded |
| Cancelled | Error | Error | Error | Error | Error |

**Key test cases:**

Valid path (T1): Pending → Confirm → Ship → Deliver → Refund (full lifecycle).

Invalid transitions (critical):
- **T-INV1:** Attempt to Cancel a Shipped order → should fail with "Order already shipped; initiate return process."
- **T-INV2:** Attempt to Deliver a Cancelled order → should fail.
- **T-INV3:** Attempt to Refund a Processing order (not yet delivered) → should fail per business rule.

Business rule validation:
- What happens if a Refund is requested 91 days after Delivery when the policy is 90 days? (Guard condition: `daysAfterDelivery <= 90`)

---

### State Transition Testing for Session Management

**Session states:** `No Session → Active Session → Expired Session`

**Events:** Login, Perform Action (resets expiry timer), Timeout (30 min inactivity), Logout, Attempt Action (expired).

| Current State | Login | Perform Action | Timeout | Logout | Attempt Action (expired) |
|--------------|-------|----------------|---------|--------|--------------------------|
| No Session | Active Session | — | — | — | — |
| Active Session | Error (already logged in) | Active Session (timer reset) | Expired Session | No Session | — |
| Expired Session | Active Session (re-auth) | — | — | No Session | Error: "Session expired — please log in again." |

**Key test scenarios:**
- T1: Login → perform action at 29 minutes → confirm session still active at 30 minutes (timer should have reset).
- T2: Login → idle for 31 minutes → attempt action → expect "session expired" and redirect to login.
- T3: Login → logout → attempt to use the old session token directly via API → expect 401 (session invalidated, not just expired).

---

## Worked Example: Flight Booking State Machine

**States:** `Search → Results → Seat Selected → Payment → Confirmed → Cancelled`

**Derived test scenarios:**

| Priority | Scenario | States Traversed | Key Verification |
|---------|---------|-----------------|-----------------|
| P1 | Happy path booking | Search → Results → Seat Selected → Payment → Confirmed | Booking reference issued; seat reserved; payment recorded. |
| P1 | Cancellation after confirmation | Confirmed → Cancelled | Refund initiated per policy; seat released; cancellation email sent. |
| P1 | Payment failure → retry | Payment → Payment (retry) → Confirmed | Seat remains held during retry; second payment succeeds; booking confirmed. |
| P2 | Session timeout mid-booking | Seat Selected → [timeout] → Search | Seat hold released after timeout; user directed to search again. |
| P2 | Attempt to cancel already-cancelled booking | Cancelled → attempt Cancel again | Error: "Booking is already cancelled." |
| P2 | Direct URL access to Payment page without selecting seat | No state / invalid | Redirect to Search; no partial booking created. |

The final scenario tests a common web application defect: users bookmark or directly navigate to mid-flow pages, bypassing required states.

---

## Summary

- State transition testing models **behavior over time**: what the system does depends on both the event and the current state.
- **State diagrams** (visual) and **state tables** (tabular) are equivalent representations — use the format that best communicates to your team.
- **0-switch coverage** (every valid transition at least once) is the minimum test set. **Invalid transition tests** and **sequence tests** (1-switch) provide stronger coverage for complex workflows.
- State tables reveal **specification gaps** (empty cells) — forcing the team to define behavior for all possible event/state combinations before coding begins.
- Pair state transition testing with **BVA** on guard conditions (e.g., "exactly how many failed attempts trigger lockout?") for complete coverage of stateful, bounded behaviors.

---

## Additional Resources

- [ISTQB Foundation Syllabus — State Transition Testing](https://www.istqb.org/) — Official treatment of the technique.
- [ISTQB Glossary — State Transition](https://glossary.istqb.org/) — Canonical definitions.
- [UML State Machine Diagrams — OMG Specification](https://www.omg.org/spec/UML/) — Visual language for state modeling.
- `boundary-value-analysis.md` — Complementary technique for guard conditions at state boundaries.
