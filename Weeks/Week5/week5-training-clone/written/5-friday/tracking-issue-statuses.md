# Tracking Issue Statuses in Jira

## Learning Objectives

By the end of this reading you will be able to:

- Explain **Jira workflows** — how statuses and transitions model the team's process.
- Build **JQL queries** for common QA use cases: retest queues, escaped defects, sprint health.
- Create and read **Jira dashboards** that provide team-visible quality indicators.
- Describe how resolution values differ from status and why both matter for release decisions.

---

## Why This Matters

The board shows what is happening today. JQL and dashboards show what the quality picture looks like over time. Testers who can answer "what is ready for me to test right now?" in 5 seconds, rather than asking at standup, are more autonomous and more effective.

More importantly, quality dashboards answer the release question before it is asked. When a Product Owner wants to know the quality position, the answer should already be visible — not assembled on the fly from memory or ad hoc Jira searches.

Understanding workflows also prevents a common mistake: issues declared "Done" that are not actually done, or issues stuck in a status because nobody knows the correct transition. Workflow literacy is operational competence for any tester working in a Jira environment.

---

## The Concept

### Workflows: The Backbone of Jira Issue Tracking

A **Jira workflow** defines the **statuses** that an issue can occupy and the **transitions** that move it between statuses. Workflows are configured per issue type and per project.

**Default Scrum workflow for Stories:**

```
[To Do] → [In Progress] → [Code Review] → [Ready for QA] → [In QA] → [Done]
```

**Default Scrum workflow for Bugs:**

```
[To Do / Open] → [In Progress] → [Ready for Retest] → [Closed]
   ↑______________________________________________↑  (if retest fails: reopen)
```

Every arrow is a **transition** — a named action that moves the issue from one status to another. Transitions can be configured with:
- **Conditions:** Only users in specific roles can trigger this transition (e.g., only testers can move to "Done" after QA).
- **Validators:** Required fields must be filled before the transition completes (e.g., Resolution must be set before Closing).
- **Post-functions:** Automated actions triggered when the transition fires (e.g., automatically assign the issue to the reporter when reopening).

**Why custom statuses matter for QA:**
The default workflow rarely serves QA teams perfectly. Adding a "Ready for QA" status (separate from "In QA") is a simple customization that makes a significant difference:
- **Ready for QA** = developer has completed work; code is deployed to test environment; waiting for tester to pick it up.
- **In QA** = tester is actively working on the issue.

This distinction enables WIP limiting on both states — preventing developers from flooding testers with too many simultaneous stories.

---

### Statuses vs Resolutions

A common source of confusion: Jira issues have both a **status** and a **resolution** field.

**Status:** Where the issue currently sits in the workflow. It is an **active state** — it changes as work progresses.

**Resolution:** The **outcome** of the issue when it is closed. It is a **permanent record** of how the issue was resolved.

| Status | Resolution |
|--------|-----------|
| Open, In Progress, In QA | (No resolution — issue is still active) |
| Closed / Done | **Fixed** — defect was corrected and verified. |
| Closed / Done | **Won't Fix** — a decision was made not to fix this. |
| Closed / Done | **Duplicate** — same issue exists; see linked issue. |
| Closed / Done | **Cannot Reproduce** — defect could not be confirmed; may reopen if evidence provided. |
| Closed / Done | **Works as Designed** — behavior is intentional; this is the spec. |
| Closed / Done | **Deferred** — valid defect, but fix is postponed to a future release. |

**Why resolutions matter for quality reporting:**
A JQL query for "closed bugs this sprint" tells you how many were fixed — but it also includes duplicates, won't-fix decisions, and cannot-reproduces. Knowing the resolution breakdown tells the real story:
- 12 bugs closed: 8 Fixed, 2 Won't Fix (accepted by PO), 1 Duplicate, 1 Cannot Reproduce.
- A release decision informed by "12 closed" is less reliable than one informed by the breakdown.

Configure validators in Jira workflows to **require** a Resolution value before closing — many teams skip this, creating a reporting blind spot.

---

### JQL: Jira Query Language

**JQL** is Jira's search query language. It works like a simple SQL WHERE clause applied to issues.

**JQL syntax:**
```
field operator value [AND|OR field operator value] [ORDER BY field]
```

**Common fields for QA queries:**

| Field | Values | Example |
|-------|--------|---------|
| `project` | Project key (e.g., "SHOP") | `project = SHOP` |
| `issuetype` | Bug, Story, Task, Epic, Sub-task | `issuetype = Bug` |
| `status` | Status name in quotes if multi-word | `status = "Ready for QA"` |
| `priority` | Critical, High, Medium, Low | `priority in (Critical, High)` |
| `assignee` | Username or `currentUser()` | `assignee = currentUser()` |
| `reporter` | Username or `currentUser()` | |
| `sprint` | Sprint name or `openSprints()` | `sprint in openSprints()` |
| `labels` | Label string | `labels = regression` |
| `created` | Date or relative (`-7d`) | `created >= -7d` |
| `updated` | Date or relative | `updated >= -1d` |
| `fixVersion` | Release version name | `fixVersion = "v2.4"` |
| `affectsVersion` | Version name | `affectsVersion = "v2.4"` |
| `resolution` | Fixed, Duplicate, etc. | `resolution = Fixed` |

**Useful QA queries:**

**My testing queue right now:**
```jql
project = SHOP AND status = "Ready for QA" ORDER BY priority ASC, updated DESC
```

**All open bugs in this sprint:**
```jql
project = SHOP AND issuetype = Bug AND status != Done AND sprint in openSprints() ORDER BY priority ASC
```

**Bugs found in the last 7 days:**
```jql
project = SHOP AND issuetype = Bug AND created >= -7d ORDER BY created DESC
```

**Escaped defects (found in production):**
```jql
project = SHOP AND issuetype = Bug AND affectsVersion = "Production" AND created >= startOfMonth()
```

**Stories verified by me this sprint:**
```jql
project = SHOP AND issuetype = Story AND status = Done AND sprint in openSprints() AND resolution = Fixed
```

**Regression failures (bugs with regression label):**
```jql
project = SHOP AND issuetype = Bug AND labels = regression AND status != Done ORDER BY priority ASC
```

**Daily standup update — issues I updated yesterday:**
```jql
project = SHOP AND updated >= -1d AND assignee = currentUser()
```

---

### Saving Filters and Building Dashboards

**Saving a filter:**
After writing a JQL query, click "Save as" (in the Issues → Search view) and give it a name. Saved filters can be:
- **Personal:** Only you see them.
- **Shared with project:** Accessible to all team members.
- **Shared with role:** Accessible to specific roles (e.g., QA team).

Once saved, filters appear in the "Filters" menu and can be added to boards and dashboards.

**Creating a QA dashboard:**

Dashboards in Jira are composed of **gadgets** — configurable widgets that display data from filters or reports.

**Recommended QA dashboard gadgets:**

| Gadget | Configuration | What It Shows |
|--------|-------------|--------------|
| **Issue Statistics** | Filter: Open bugs; Group by: Priority | Distribution of open bugs by priority (pie chart) |
| **Issue Navigator** | Filter: Ready for QA | List of stories currently waiting for testing |
| **Issue Statistics** | Filter: Open bugs this sprint; Group by: Component | Where bugs are concentrating this sprint |
| **Sprint Burndown** | Project board | Sprint progress and completion trend |
| **Issue Count** | Filter: Escaped defects this month | Production defect count (quality health indicator) |
| **Two-Dimensional Filter Statistics** | Filter: All bugs; Row: Priority, Column: Status | Heat map of bugs by priority and status |

**Share the dashboard with the team:**
A shared QA dashboard that the PO, developers, and testers can all view eliminates "how are we doing?" questions at standup — the answer is always visible.

---

### Workflow Best Practices for QA Teams

**1. Agree on what "Ready for QA" means:**
Document the Definition of "Ready for Testing" — what criteria must be met before a developer transitions a story to "Ready for QA"? Typical criteria:
- Code committed and code review complete.
- Feature deployed to test environment.
- Smoke test for the specific feature passing.
- Developer notes added: any known issues, test data requirements.

**2. Testers maintain their own "In QA" status:**
When a tester picks up a story, transition it to "In QA." This makes WIP visible and prevents two testers from starting on the same story simultaneously.

**3. Use "Blocked" status or "Blocked by" links:**
When testing is blocked (environment down, dependency not ready, blocker bug), make the block visible with a link or label — don't leave the issue stuck in "In QA" silently.

**4. Transition bugs promptly through retest workflow:**
When a developer fixes a bug and deploys, they should transition it to "Ready for Retest" — not to "Done" (the tester verifies the fix; the developer does not self-close). When the retest passes, the tester closes the bug.

**5. Never self-close your own bugs as the reporter:**
The developer should not close bugs they fixed without tester verification. The tester who found it verifies it is fixed. This separation of concerns ensures independent verification.

---

## Worked Example: A Sprint-End Quality Picture in JQL

It is the last day of Sprint 15. The PO wants to know: "Can we release?"

The tester opens the QA dashboard and runs these queries:

**Query 1: Open stories not yet Done:**
```jql
project = SHOP AND issuetype = Story AND sprint in openSprints() AND status != Done
```
Result: 1 story ("PROF-43: Change Display Name") — In QA.

**Query 2: Open bugs in this sprint:**
```jql
project = SHOP AND issuetype = Bug AND sprint in openSprints() AND status != Done
```
Result: 0 open bugs.

**Query 3: Bugs deferred (Won't Fix or Deferred resolution) this sprint:**
```jql
project = SHOP AND issuetype = Bug AND sprint in openSprints() AND resolution in ("Won't Fix", Deferred)
```
Result: 1 bug — DEF-308 (Low priority, cosmetic — accepted by PO).

**Answer to the PO:**
"PROF-43 is in QA — testing will be complete by 3 PM. Zero open bugs. DEF-308 is deferred (Low, cosmetic, PO accepted). Regression suite green. My recommendation: release after PROF-43 closes."

The PO makes a confident, evidence-informed decision in 30 seconds — from data that was always there, not assembled in a panic.

---

## Summary

- **Workflows** define statuses and transitions — custom "Ready for QA" and "In QA" statuses give testers visibility and protect WIP discipline.
- **Resolutions** are permanent outcome records; require them when closing issues to enable meaningful defect breakdown reports.
- **JQL** is the tester's superpower: master 10–15 common query patterns and you can answer any quality question in seconds.
- **Saved filters + dashboards** convert individual JQL queries into shared, always-visible quality indicators for the whole team.
- Good workflow discipline (prompt transitions, developer-not-self-closing bugs, tester-owns-In-QA) makes Jira data reliable and trustworthy.

---

## Additional Resources

- [Atlassian — JQL Reference](https://support.atlassian.com/jira-software-cloud/docs/use-advanced-search-with-jira-query-language-jql/) — Complete JQL field, function, and operator reference.
- [Atlassian — Configure Workflows](https://support.atlassian.com/jira-cloud-administration/docs/configure-workflows/) — Admin guide to status and transition setup.
- [Atlassian — Dashboards and Gadgets](https://support.atlassian.com/jira-software-cloud/docs/use-your-project-pages/) — Dashboard creation and gadget configuration.
- `jira-issues.md` — Issue types, fields, and hygiene best practices.
- `defect-lifecycle.md` (Thursday) — Conceptual defect management process that Jira workflows implement.
