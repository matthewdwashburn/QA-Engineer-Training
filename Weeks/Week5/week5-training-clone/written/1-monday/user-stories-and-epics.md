# User Stories and Epics

## Learning Objectives

By the end of this reading you will be able to:

- Explain what an **Epic** is and how it relates to a large business goal.
- Write a well-formed **User Story** using the standard "As a / I want / So that" format.
- Describe the three components of a good story: **role, goal, and benefit**.
- Write clear **Acceptance Criteria** using the **Given / When / Then** format.
- Explain how Epics are **decomposed** into User Stories and how User Stories are pulled into Sprints.
- Identify the difference between a story that is **sprint-ready** and one that is not.

---

## Why This Matters

Before a single line of code is written — before Sprint Planning, before test cases, before any estimate — someone must answer a deceptively simple question:

> *"What are we actually building, and for whom?"*

**User Stories** and **Epics** are the tools the industry uses to answer that question in a structured, testable, and human-readable way.

As a Quality Engineer, you will read user stories every single Sprint. You will ask questions about them in refinement, derive your test cases directly from them, and determine whether a feature is "Done" based on their acceptance criteria. Teams that write poor user stories produce software that solves the wrong problem — and whose testers have no clear target to test against.

Understanding this discipline at a deep level separates a reactive tester (who tests what they're handed) from a proactive quality engineer (who shapes requirements before they reach development).

---

## The Concept

### What Is an Epic?

An **Epic** is a **large body of work** that represents a significant business capability or product feature. It is too big to be completed in a single Sprint — often spanning several sprints or even an entire release cycle.

Think of an Epic as a **chapter heading** in the story of your product. It gives direction without dictating every detail of implementation.

**Analogy:** Imagine you are building a house. The Epic might be *"Build the Kitchen."* That's a meaningful, valuable outcome — but no single tradesperson can complete it in a day. A plumber, an electrician, a tiler, and a carpenter each have their own piece. Those pieces are your User Stories.

**Examples of Epics:**

| Domain | Epic |
|--------|------|
| E-commerce platform | *As an online retailer, I need a complete checkout flow so customers can purchase products.* |
| Banking app | *Implement secure user authentication and account management.* |
| HR system | *Enable employees to view, request, and track leave from a self-service portal.* |
| QA platform | *Build a test results dashboard so teams can track quality trends over time.* |

Epics live high up in the **Product Backlog**. They are not sprint-ready — they must be broken down (decomposed) into smaller User Stories before work begins.

---

### What Is a User Story?

A **User Story** is a short, plain-language description of a feature **told from the perspective of the person who benefits from it**. It is intentionally non-technical — it describes *what* a user needs and *why*, without prescribing *how* to build it.

The universally accepted format is:

```
As a [type of user],
I want [to do something / have something],
So that [I achieve some goal or get some benefit].
```

This three-part structure forces you to think in terms of **who, what, and why** — the three questions that make a feature valuable and testable.

---

### Breaking Down the Three Parts

#### Part 1: "As a [type of user]" — The Role

This defines *who* the story is for. It should be specific enough to be meaningful.

| Weak (too vague) | Strong (specific) |
|------------------|-------------------|
| As a user... | As a **registered customer**... |
| As a person... | As a **warehouse manager**... |
| As someone... | As a **first-time visitor without an account**... |

Why does specificity matter? Because different users have different permissions, different needs, and different risk profiles. A **registered customer** can check out; a **guest user** may have different payment restrictions. These are different stories with different test scenarios.

---

#### Part 2: "I want [to do something]" — The Goal

This is the action or capability the user needs. Keep it focused on **one thing**.

A common mistake is cramming multiple goals into one story:

> ❌ *"I want to search for products, filter by category, and save items to a wishlist..."*

That is three stories disguised as one. Each goal should be independently testable and deliverable.

> ✅ *"I want to search for products by name"*  
> ✅ *"I want to filter search results by category"*  
> ✅ *"I want to save a product to my wishlist"*

---

#### Part 3: "So that [benefit]" — The Why

This is the **most important and most frequently omitted** part of a user story. It explains the value — what the user is actually trying to achieve.

Why does the "so that" matter for testers?

> If you understand **why** someone wants a feature, you can test edge cases that go beyond the happy path. You can ask: *"Does this implementation actually deliver the stated benefit?"*

**Example:**
> *"As a job applicant, I want to receive an email confirmation after submitting my application **so that** I know my submission was received and I don't apply twice."*

The "so that" tells you the real test: *not just* does an email get sent, but does it contain enough information to confirm receipt and prevent duplicate applications?

---

### Putting It Together: Full User Story Examples

**Epic:** *Enable employees to manage leave requests via a self-service portal.*

| # | User Story |
|---|------------|
| 1 | As an **employee**, I want to **view my remaining leave balance** so that I can plan my time off. |
| 2 | As an **employee**, I want to **submit a leave request for specific dates** so that my manager knows when I will be absent. |
| 3 | As a **manager**, I want to **approve or reject leave requests** so that I can plan team capacity. |
| 4 | As an **employee**, I want to **receive a notification when my leave request is approved or rejected** so that I can make travel or personal arrangements in time. |
| 5 | As an **HR administrator**, I want to **export leave records to a spreadsheet** so that I can run quarterly compliance reports. |

Notice how each story is small enough to be completed within a Sprint, yet each delivers a distinct, testable piece of value.

---

### Acceptance Criteria: Making Stories Testable

A User Story alone is not enough to build or test from. You also need **Acceptance Criteria** — the specific conditions that must be true for the story to be considered **Done**.

The most widely used format is **Given / When / Then** (also called **Gherkin** format):

```
Given  [some initial context or precondition],
When   [an action is performed],
Then   [an expected outcome occurs].
```

**Example Story:**
> *As an employee, I want to submit a leave request so that my manager is notified and can approve it.*

**Acceptance Criteria:**

```gherkin
Given I am logged in as an employee with at least 3 days of remaining leave,
When I submit a leave request for 2 consecutive working days,
Then the request appears in my "Pending" leave list,
  And my manager receives an email notification with my name, dates, and a link to approve or reject.

Given I am logged in as an employee with 0 days of remaining leave,
When I attempt to submit a leave request,
Then the system displays an error message: "You have no remaining leave balance."
  And the request is not submitted.

Given I am logged in as an employee,
When I submit a leave request for a date that falls on a public holiday,
Then the system warns: "One or more selected dates are public holidays."
  And I can choose to continue or cancel.
```

Notice that well-written acceptance criteria cover:
- The **happy path** (normal, successful scenario)
- **Boundary and edge cases** (zero balance, holiday dates)
- **Error states** (what the system does when something goes wrong)

This is exactly where your value as a tester starts — during refinement, before code is written.

---

### The Epic → Story → Task Hierarchy

It helps to visualize how work is organized from the strategic level down to daily tasks:

```
Epic  (weeks/months)
  └── User Story  (days/1 Sprint)
        └── Task  (hours)
```

**Example breakdown:**

```
Epic: Enable employees to manage leave requests via a self-service portal.
│
├── Story 1: View remaining leave balance
│     ├── Task: Design the balance calculation API endpoint
│     ├── Task: Build the balance display UI component
│     └── Task: Write and execute test cases for balance display
│
├── Story 2: Submit a leave request for specific dates
│     ├── Task: Build date picker + form UI
│     ├── Task: Implement business logic (holiday validation, balance check)
│     ├── Task: Set up test data (employee records, leave policies)
│     └── Task: Write acceptance tests (Given/When/Then)
│
└── Story 3: Manager approval / rejection of leave requests
      ├── Task: Build manager notification email template
      ├── Task: Build approve/reject action screen
      └── Task: Test notification delivery and status update
```

The key rule: **Tasks are not on the Product Backlog — Stories are.** Tasks are created by the Developers during Sprint Planning as their plan for *how* to deliver a Story.

---

### What Makes a Story "Sprint-Ready"?

A story is sprint-ready (also called **refined**) when the whole team — developers and testers — could pick it up and start working without needing to ask basic clarifying questions.

A widely-used checklist for sprint-readiness is the **INVEST criteria**:

| Letter | Criterion | What it means |
|--------|-----------|---------------|
| **I** | Independent | The story can be developed and delivered without waiting for another unfinished story. |
| **N** | Negotiable | The story is not a fixed contract — it is an invitation to a conversation about the best solution. |
| **V** | Valuable | The story delivers something useful to the end user or business. |
| **E** | Estimable | The team has enough understanding to estimate its size. |
| **S** | Small | The story fits within a single Sprint. |
| **T** | Testable | Clear acceptance criteria exist that allow us to verify the story is done. |

**As a tester, your main concerns are V, S, and T:**

- Is this story actually **valuable** — does the "so that" make sense?
- Is it **small** enough that we can fully test it within the Sprint?
- Is it **testable** — do we have clear, unambiguous acceptance criteria, or will testers be guessing what "done" looks like?

---

### Common Anti-Patterns to Watch For

During refinement sessions you will encounter stories that are not well-formed. Here are the most frequent problems and how to address them:

#### 1. The Missing "So That"
> ❌ *"As an admin, I want a button to delete users."*

Without the "so that," you don't know *why* — is this for security (deactivating bad actors quickly)? Compliance (GDPR right-to-erasure)? The answer changes what the feature should actually do and what tests are needed.

> ✅ *"As an admin, I want to permanently delete a user account so that I can fulfil GDPR deletion requests within the required 30-day window."*

#### 2. The Story That's Really an Epic
> ❌ *"As a customer, I want to manage my account."*

"Manage my account" could mean: change password, update address, view order history, cancel subscription, add payment method, update notification preferences... This is an Epic, not a Story. Ask the PO to break it down.

#### 3. The Technical Task Disguised as a Story
> ❌ *"As a developer, I want to refactor the database schema."*

User stories describe value to **users**, not implementation steps. Refactoring is a valid task, but it belongs inside a Story or as a team-level tech debt item — not as a standalone story on the backlog.

#### 4. Acceptance Criteria That Are Too Vague
> ❌ *"The page should load quickly."*

"Quickly" is not testable. Replace with a measurable criterion:
> ✅ *"The page must load within 2 seconds on a standard 4G connection."*

---

## Worked Example: A Tester's Perspective at Refinement

**Story under review:**
> *"As a registered user, I want to reset my password so that I can regain access to my account."*

A junior tester might read this and think: *"Sure, seems straightforward."*

A quality engineer asks:

- *"What should happen if the email address doesn't exist in the system?"*
- *"How long is the reset link valid for?"*
- *"Can the same reset link be used more than once?"*
- *"What happens if a user requests two reset emails — does the first link get invalidated?"*
- *"Are there any password complexity rules the new password must meet?"*
- *"Is there a lockout after too many failed reset attempts?"*

Each of these questions either reveals a missing acceptance criterion or surfaces a security risk. This is the quality engineer's contribution **before a single line of code is written** — the highest-leverage moment in the entire Agile process.

---

## Summary

- An **Epic** is a large business capability that spans multiple Sprints. It is decomposed into User Stories before work begins.
- A **User Story** captures a feature from the user's perspective: *"As a [role], I want [goal], so that [benefit]."*
- The **"so that"** clause is critical — it explains *why* the feature exists and drives deeper, more meaningful test coverage.
- **Acceptance Criteria** (Given / When / Then) make a story testable and form the foundation of your test cases.
- The **INVEST criteria** is a practical checklist for evaluating whether a story is sprint-ready.
- Quality engineers add the most value during **refinement** — by asking the questions that reveal missing criteria, edge cases, and risks before development starts.

---

## Additional Resources

- [Atlassian: User Stories](https://www.atlassian.com/agile/project-management/user-stories) — A practical guide with examples from a widely-used Agile tool vendor.
- [Mountain Goat Software: INVEST in Good Stories](https://www.mountaingoatsoftware.com/blog/invest-in-good-stories-and-smart-tasks) — The original article explaining the INVEST acronym by Mike Cohn.
- [Cucumber: Writing Good Gherkin](https://cucumber.io/docs/gherkin/reference/) — Official reference for the Given / When / Then acceptance criteria format used in test automation.
- `agile-and-scrum-processes.md` — How User Stories and Epics slot into the Product Backlog and the Sprint cycle.
- `story-pointing-and-burndown-charts.md` — How stories are estimated and tracked across a Sprint.
