# Introduction to Jira

## Learning Objectives

By the end of this reading you will be able to:

- Describe what **Jira** is and the core problems it solves for delivery and QA teams.
- Navigate the key **structural concepts**: projects, boards, backlogs, sprints, and workflows.
- Distinguish **Scrum** and **Kanban** board configurations and their implications for quality tracking.
- Use **Jira for QA activities**: logging bugs, linking tests to stories, and creating quality-visibility dashboards.

---

## Why This Matters

Jira is used by more than 65,000 organizations worldwide and is the tool most commonly encountered in software delivery teams. For new testers, the ability to use Jira fluently — not just to log bugs, but to track quality, manage test scope, and communicate risk — is a core professional skill.

Jira closes the loop between the testing activities you have studied this week and the team's working system. A defect you found is only useful if it is recorded, triaged, prioritized, and tracked to resolution. A story you tested is only "done" if the evidence of testing is linked and visible. Jira is where all of this happens in practice.

---

## The Concept

### What Is Jira?

**Jira** is a work management platform developed by **Atlassian**. Its primary use cases are:
- **Issue tracking:** Recording bugs, stories, tasks, and any other work item that needs to be tracked.
- **Agile project management:** Sprint planning, backlog management, Scrum and Kanban boards.
- **Workflow automation:** Moving issues through defined states with configurable rules and triggers.
- **Reporting:** Burndown charts, velocity reports, control charts, and custom dashboards.

**Jira Software** (for development teams) is what you will encounter in most software delivery teams. Jira also has **Jira Service Management** (for support ticketing) and **Jira Work Management** (for business teams) — the same platform, different configurations.

**Important caveat:** Jira is **highly configurable**. Every Jira instance looks different — different issue types, different workflows, different field names, different board layouts. The concepts you learn here are universal; the specific configuration varies by organization and Jira admin.

---

### Jira Projects

A **Jira project** is a container for all issues related to a product, team, or initiative. Every issue exists within a project.

**Project configuration includes:**
- **Issue types:** What kinds of things can be tracked (Story, Bug, Task, Epic, Spike, etc.).
- **Workflow:** What statuses exist and what transitions are allowed between them.
- **Components:** Groupings within the project (e.g., "Authentication", "Checkout", "Reporting").
- **Permissions:** Who can view, create, edit, and transition issues.
- **Versions/Releases:** Named milestones that issues can be linked to (e.g., "v2.4.15", "Sprint 15 Release").

**Project templates:**
When a project is created, a template is selected:
- **Scrum template:** Includes sprints, sprint backlog, velocity reporting.
- **Kanban template:** Continuous flow; no sprints; WIP limits configurable.
- **Bug tracking template:** Lightweight issue tracking without Agile planning features.

---

### The Board

A **Jira board** visualizes work as **columns** representing workflow statuses. Issues move left to right as they progress through the workflow.

**Typical Scrum board columns:**

```
| To Do | In Progress | Code Review | Ready for QA | In QA | Done |
```

**Typical Kanban board columns:**

```
| Backlog | In Progress | Review | Done |
```

QA teams benefit from having a **"Ready for QA"** column (or "In QA" status) that is explicitly visible. This:
- Makes it clear what is waiting for testing — without requiring testers to chase developers.
- Enables WIP limiting on the QA column (prevents too many stories piling up simultaneously).
- Provides visibility to Product Owners on what has been tested vs what is still waiting.

**Board-level filters:**
Boards can be filtered to show only issues assigned to a specific person, component, or label. A tester can filter the board to show only issues in "In QA" or "Ready for QA" status — their personal view of what needs attention.

---

### The Backlog

The **backlog** is the ordered list of all work items for the project. In Scrum projects:
- Issues in the backlog are candidates for future sprints.
- The Product Owner orders the backlog (highest priority at the top).
- During Sprint Planning, the team pulls items from the top of the backlog into the sprint.

In Scrum Jira, the backlog view shows:
- **Active sprint** (stories currently in the sprint, with their status).
- **Backlog** (all other issues, ordered by priority).

For testers, the backlog is where you find:
- Upcoming stories to review for testability gaps (before they enter a sprint).
- Open bugs and their priority relative to features.
- Test-related tasks (automation debt, environment setup, tooling work).

---

### Sprints in Jira

A **sprint** in Jira Scrum is a time-boxed container (typically 1–4 weeks) for a set of issues the team has committed to completing.

**Sprint lifecycle in Jira:**
1. **Sprint Planning:** Product Owner and team pull issues from backlog into the sprint. Sprint goal is set. Sprint is started.
2. **During the sprint:** Issues move through board columns as work progresses. Testers update status as testing occurs.
3. **Sprint Review / Close:** Sprint is closed in Jira. Incomplete issues are moved back to backlog or next sprint. Velocity is calculated.

**Burndown chart:**
Jira automatically generates a sprint burndown chart — total story points remaining over time. Testers should understand how to read this: a story is "burned" when it reaches "Done." If stories are completing but not being marked Done (lingering in "In QA"), the burndown looks flat even though work is done — this is a common data hygiene issue.

---

### Jira for QA Workflows

Beyond story tracking, Jira supports several QA-specific workflows:

**Logging bugs:**
When a tester finds a defect, a Bug issue type is created. Best practices for bug tickets are covered in depth in `defect-lifecycle.md` (Thursday). For navigation purposes:
- Use "Create Issue" → select Bug issue type.
- Link the bug to the story it was found on: "Found in Story AUTH-77."
- Assign a priority and severity.
- Add reproduction steps, expected and actual behavior, environment, and build number.

**Linking tests to stories:**
With Jira-native integrations (Xray, Zephyr Scale, TestRail for Jira), testers can:
- Create test cases as a special issue type ("Test" or "Test Case").
- Link test cases to the stories they verify — providing native traceability.
- Mark tests as executed and record pass/fail directly in Jira.
- Generate coverage reports showing which stories have test cases and which are verified.

**Without a dedicated test management extension:**
Testers can use:
- A custom comment or description field on the story to list test case IDs.
- A "Test Tasks" sub-task on each story for testing work.
- Confluence page linked from the story for test plans and exploratory session notes.

---

### Filters and Quality Dashboards

**JQL (Jira Query Language)** allows testers to create precise queries for quality-relevant views:

```
# All open bugs in this project, by priority
project = SHOP AND issuetype = Bug AND status != Done ORDER BY priority ASC

# Stories ready for QA testing right now
project = SHOP AND issuetype = Story AND status = "Ready for QA"

# Bugs found in the last 7 days
project = SHOP AND issuetype = Bug AND created >= -7d

# Bugs that escaped to production (found in production version)
project = SHOP AND issuetype = Bug AND "Found in" = "Production" AND created >= -30d
```

Save these as **Jira filters** and pin them to a **dashboard**. A QA dashboard might include:
- Open bug count by severity (pie chart gadget).
- "Ready for QA" story list (issue list gadget).
- Burndown chart for the active sprint.
- Escaped defect count this release (issue count gadget).

This dashboard gives the team and Product Owner **real-time quality visibility** without requiring a separate status update.

---

### Navigation Quick Reference

| Goal | How to Get There |
|------|-----------------|
| Create a new issue | Click "Create" button in top navigation (or press `C` shortcut) |
| Find a project | Left sidebar → Projects → Select project name |
| View the sprint board | Left sidebar → Board |
| View the backlog | Left sidebar → Backlog |
| View all issues | Left sidebar → Issues → View all issues |
| Run a JQL search | Left sidebar → Issues → Search for issues → Switch to "Advanced" |
| Create a dashboard | Top navigation → Dashboards → Create dashboard |
| View a sprint report | Left sidebar → Reports → Sprint Burndown |

*Note: Exact menu locations differ between Jira Cloud and Jira Data Center, and between project templates and custom configurations.*

---

## Worked Example: A Tester's Daily Jira Workflow

**9:00 AM — Check the board:**
Filter to "In QA" and "Ready for QA" columns. Three stories are waiting. Prioritize by sprint goal and dependency.

**9:15 AM — Begin testing Story AUTH-77:**
Confirm the story is in "In QA" status. Execute test cases. All pass. Log exploratory session notes as a comment on the story. Transition story to "Done." Tag it as "Verified: Sprint 15."

**10:30 AM — Find a defect on Story PROF-42:**
Create a Bug issue: "Profile photo upload silently fails for PNGs with transparent layers." Link to PROF-42. Priority: High. Assign to the developer. Comment on PROF-42: "Blocked on DEF-315 — cannot close until resolved."

**11:00 AM — Standup:**
Report: "AUTH-77 is Done. PROF-42 is Blocked — DEF-315 filed. PROF-43 is next. Burndown shows we are on track for the sprint goal."

**4:00 PM — Retest DEF-315:**
Developer marks it Fixed. Tester transitions DEF-315 to "Ready for Retest," executes the specific scenario, confirms fix. Transitions DEF-315 to "Done" (resolution: Fixed). Resumes PROF-42 testing — passes remaining cases. Transitions PROF-42 to "Done."

**End of day — Update dashboard:**
Open bug count: 0. Stories Done: 2/3. On track for sprint.

---

## Summary

- **Jira** is a work management platform used for issue tracking, Agile planning (Scrum/Kanban), workflow management, and reporting.
- A **Jira project** contains issues, workflows, components, and permissions — configuration varies widely by organization.
- The **board** visualizes workflow statuses; the **backlog** is the ordered work queue. Testers benefit from visible "Ready for QA" and "In QA" board columns.
- QA activities in Jira: logging bugs (Bug issue type), linking tests to stories, creating JQL filters for quality visibility, and building dashboards.
- **JQL** enables precise quality queries — save and share them as team dashboards to provide continuous quality visibility.

---

## Additional Resources

- [Atlassian Jira Software Documentation](https://support.atlassian.com/jira-software-cloud/) — Official documentation for Jira Cloud.
- [Atlassian — JQL Reference](https://support.atlassian.com/jira-software-cloud/docs/use-advanced-search-with-jira-query-language-jql/) — Complete JQL function and field reference.
- [Atlassian Agile Tutorials](https://www.atlassian.com/agile/tutorials) — Scrum and Kanban board setup and usage.
- [Xray for Jira Documentation](https://www.getxray.app/) — Test case management extension with traceability.
- `jira-issues.md` — Issue types, fields, and hygiene best practices.
- `tracking-issue-statuses.md` — Workflows, JQL filters, and QA dashboards in depth.
