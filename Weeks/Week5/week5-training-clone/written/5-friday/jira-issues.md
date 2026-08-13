# Jira Issues: Types, Fields, and Hygiene

## Learning Objectives

By the end of this reading you will be able to:

- Recognize and correctly use the **standard Jira issue types**: Epic, Story, Task, Bug, Sub-task.
- Write **effective issue fields**: clear summaries, structured descriptions, and meaningful priorities.
- Use **linking and parent-child relationships** correctly to model work hierarchy.
- Apply **issue hygiene best practices** that keep boards, dashboards, and sprint reports accurate.

---

## Why This Matters

Jira is only as useful as the quality of its data. Teams that log vague bug summaries ("Login is broken"), use issue types inconsistently (logging bugs as Tasks), and never fill in severity or environment fields produce boards that cannot be trusted and dashboards that report noise. The Product Owner cannot make release decisions from "3 open tasks" — they need to know "3 open defects in the checkout flow, 2 of which are High severity, found on build 2.4.15 in staging."

Professional issue hygiene is a signal of professional quality engineering. It enables faster triage, better decision-making, and a data-driven quality picture.

---

## The Concept

### Standard Issue Types

Most Jira Scrum project templates include these issue types:

**Epic:**
A large body of work that spans multiple sprints or represents a major feature area. Epics group related stories together and tie to product themes or release goals.
- *Example:* "User Authentication and Account Management"
- Epics are not directly assigned to a sprint — they span across sprints as their constituent stories are completed.
- Testers use epics to understand the overall feature scope and identify cross-story testing needs.

**Story (User Story):**
A user-centric slice of value, typically sized to fit within a single sprint. The primary unit of sprint work in Scrum.
- *Format:* "As a [role], I want [feature], so that [benefit]."
- Stories contain acceptance criteria — the testable conditions the tester verifies.
- Stories belong to an Epic and are assigned to a sprint.

**Task:**
A technical work item that is not user-centric — infrastructure work, refactoring, automation setup, documentation, or tooling.
- *Examples:* "Set up CI pipeline for regression suite." "Upgrade Node.js to v20." "Create load testing environment."
- Tasks should not be used as a workaround for logging bugs — that is what the Bug type is for.

**Bug:**
A defect — a deviation from specified or expected behavior that needs to be fixed. The primary issue type for quality findings.
- Bugs link to the story, sprint, and environment where they were found.
- Key fields: summary, description (steps, expected, actual), priority, severity, environment, build version.
- Bugs can be in a sprint (if they block current sprint work) or in the backlog (for future prioritization).

**Sub-task:**
A smaller piece of work that belongs to a parent Story, Task, or Bug. Sub-tasks inherit the sprint assignment from their parent.
- *Examples:* Under Story AUTH-77: "Write API test cases", "Execute manual regression", "Review AC with PO."
- Testers often use sub-tasks to track testing activities within a story transparently.

**Custom types:**
Many teams add custom issue types:
- **Spike:** Timeboxed research or investigation to reduce uncertainty before a story can be estimated.
- **Test:** A test case managed in Jira (with test management extensions like Xray or Zephyr).
- **Incident:** A production issue requiring immediate response.
- **Improvement / Technical Debt:** Non-functional improvements without immediate user-facing value.

Know your team's custom types and use them correctly — they feed into type-specific reports and workflows.

---

### Core Fields: What They Are For

**Summary (required):**
The one-line title of the issue. This is what appears on the board, in filters, in release notes, and in dashboards. It must be specific enough to identify the issue without opening it.

| Poor summary | Better summary |
|-------------|---------------|
| "Login broken" | "Login returns HTTP 500 when password contains `&` character (staging, build 1.4.55)" |
| "Performance issue" | "Product search takes >8 seconds for queries with >5 filters (load test, 100 concurrent users)" |
| "Fix the thing" | "Refactor tax calculation to use Money library for decimal precision" |

**Description (critical for bugs):**

For bugs, the description is the evidence pack that enables reproduction and resolution. A complete bug description includes:

```markdown
## Summary
[One sentence describing what is wrong]

## Steps to Reproduce
1. Navigate to [exact URL].
2. Enter [exact data].
3. Click [exact button/action].
4. Observe [what happens].

## Expected Behavior
[What should have happened according to the specification or user expectation]

## Actual Behavior
[What actually happened — be specific; include error messages exactly]

## Environment
- Environment: Staging / QA / Production
- Build/Release version: 1.4.55
- Browser: Chrome 123.0.6312.86 (if UI-related)
- User account: test@example.com (or user ID)
- Operating system: Windows 11 (if relevant)

## Evidence
[Screenshots, screen recordings, API response bodies, log snippets — attach or paste]
```

For stories and tasks, the description should include: background context, acceptance criteria (or link to them), any constraints or out-of-scope clarifications.

**Priority:**
How urgently this issue needs attention relative to other work. Priority is a **scheduling** signal — it tells the team what to work on next.

Common priority scales:

| Priority | Meaning | Example |
|----------|---------|---------|
| **Critical / P1** | Must be fixed immediately; blocks release or core workflow. | Payment processing fails for all orders. |
| **High / P2** | Should be fixed this sprint; significant user impact. | Order confirmation email not sent in ~30% of cases. |
| **Medium / P3** | Should be fixed soon; noticeable but workaround exists. | Filter sort order reverses on page refresh. |
| **Low / P4** | Fix when time allows; cosmetic or minor UX. | Button tooltip text has a typo. |

**Severity vs Priority — an important distinction:**

**Severity** measures the **technical impact** of the defect — how badly it breaks the system.
**Priority** measures the **business urgency** — how quickly it needs to be fixed.

These are not the same:
- A cosmetic bug on the CEO's presentation demo has **low severity** (nothing is technically broken) but **high priority** (it must be fixed before 9 AM tomorrow).
- A defect that prevents users from deleting their own accounts has **high severity** (broken functionality) but may have **lower priority** if accounts are rarely deleted and a workaround exists.

Some Jira configurations have separate Severity and Priority fields. Where only one field exists ("Priority"), teams commonly use it to capture the combined urgency-and-impact signal.

**Assignee and Reporter:**
- **Reporter:** Who filed the issue (usually the tester).
- **Assignee:** Who is responsible for resolving it (usually the developer, or unassigned for triage).

Avoid assigning bugs to yourself as a tester unless you are fixing them — the assignee field drives developer workflow dashboards.

**Components:**
Functional groupings within the project (e.g., "Authentication", "Checkout", "API", "Mobile"). Components enable:
- Routing bugs to the right developer or team.
- Filtering reports by area ("How many open bugs in Checkout?").
- Identifying defect clustering (many bugs in Authentication signals risk in that area).

**Labels:**
Flexible, freeform tags for cross-cutting classification. Good label conventions:
- `regression` — add when a bug turns out to be a regression (broke something that previously worked).
- `escaped-defect` — add when a bug is found in production (it escaped testing).
- `sprint-15` — sprint when the bug was found.
- `flaky` — for automated tests that fail intermittently.

Labels require team conventions to stay useful — unconstrained label use produces hundreds of unique labels that nobody can query reliably.

**Fix Version / Affects Version:**
- **Affects Version:** Which version of the product the defect is observed in.
- **Fix Version:** Which release will include the fix.

These fields enable release-level defect tracking: "How many open bugs affect version 2.4?" and "How many bugs are scheduled to be fixed in the 2.5 release?"

---

### Linking Issues

Jira supports explicit links between issues:

| Link type | When to use |
|-----------|-------------|
| **Blocks / Is Blocked By** | "DEF-315 blocks PROF-42" — the story cannot be closed until the bug is fixed. |
| **Relates to** | General relationship — useful for noting that two issues affect the same area. |
| **Duplicates / Is Duplicated By** | When the same defect is reported twice — link and close the duplicate. |
| **Clones / Is Cloned By** | When an issue is copied to a new project or sprint. |
| **Is part of / Belongs to** (Epic link) | Story to Epic relationship — available as a native field in most Scrum templates. |

For testers, the **Blocks** link is essential: when a bug prevents testing a story, link the bug to the story with "DEF-XXX blocks STORY-YYY." This makes the dependency visible on the board and in sprint reports.

---

### Issue Hygiene Best Practices

**1. Close what is resolved:**
A "Done" status that still has open sub-tasks or linked bugs pollutes sprint reports. Close issues promptly when work is confirmed complete.

**2. Update status accurately:**
If a story sits in "In Progress" for a week while it is actually waiting for testing, the board is lying. Transition issues promptly as their actual state changes.

**3. Never log bugs as Tasks:**
Tasks and Bugs have different workflows, different fields, and different reports. A bug logged as a Task is invisible in defect count reports and may not trigger the right reviewer notifications.

**4. Use descriptions, not comments, for structured data:**
Steps to reproduce, expected behavior, and actual behavior belong in the **description** (where they are always visible), not buried in a comment thread.

**5. Set "Affects Version" and environment for every bug:**
Without these fields, it is impossible to determine whether a bug reported today was already fixed in an older version, or which environment it needs to be retested on.

**6. Resolve with a resolution, not just a status:**
When closing an issue, set the **Resolution** field: Fixed, Won't Fix, Duplicate, Cannot Reproduce, Won't Do. A bug closed with no resolution is ambiguous — was it fixed? Deferred? Determined not to be a bug?

---

## Summary

- **Issue types** (Epic, Story, Task, Bug, Sub-task) have different workflows and feed different reports — use them correctly.
- **Bug summaries** must be specific enough to identify the defect without opening the ticket: include what fails, where, and on which build.
- **Bug descriptions** should include: steps to reproduce, expected behavior, actual behavior, environment, build version, and evidence (screenshots/logs).
- **Severity ≠ Priority:** Severity is technical impact; Priority is business urgency — they are related but not the same.
- **Links and labels** create the relationships and classifications that enable quality dashboards, defect tracking, and release decisions.
- **Issue hygiene** — accurate statuses, resolution codes, linked versions, and closed-when-complete discipline — is what makes Jira a reliable quality tool rather than a noisy ticketing backlog.

---

## Additional Resources

- [Atlassian — Issue Types in Jira](https://support.atlassian.com/jira-software-cloud/docs/what-are-issue-types/) — Official documentation.
- [Atlassian — Create and Update Issues](https://support.atlassian.com/jira-software-cloud/docs/create-an-issue/) — Step-by-step guidance.
- [Atlassian — Configure Priorities](https://support.atlassian.com/jira-cloud-administration/docs/configure-statuses-resolutions-and-priorities/) — Admin guide to priority schemes.
- `tracking-issue-statuses.md` — Workflows, JQL, and quality dashboards using well-maintained issues.
- `defect-lifecycle.md` (Thursday) — The full defect management process that issue types and fields support.
