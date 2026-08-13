# Exercise: Jira — Project, Stories, Bugs, Board

**Mode:** Implementation (tooling) **or** documented simulation  
**Time:** ~60–90 minutes  
**Relates to:** `written/5-friday/introduction-to-jira.md`, `jira-issues.md`, `tracking-issue-statuses.md`  
**Demo tie-in:** `demos/5-friday/demo_jira_workflow/`

## Option A — Live Jira (preferred)

Use your **training** Jira site or **sandbox** project (instructor provides project key).

### Tasks

1. **Create** (or verify) a Scrum **software** project named **`QEA Trainee <YourInitials>`**.
2. **Create Epic** `QEA-EPIC-01` — title: **“Week 5 learning — ShopRight checkout hardening”**.
3. **Create 3 Stories** under the epic with:
   - **Summary**, **description**, **acceptance criteria** (bullets or G/W/T),
   - **Story points** if your template exposes them,
   - **At least one** story must include **test-focused** AC (e.g., “Given … When … Then …” for error path).
4. **Create 2 Bugs** with **environment**, **steps**, **expected/actual**, **priority**, link **relates to** the most relevant story.
5. **Run Sprint 1:** add **two** stories + **one** bug to the sprint; move items across **at least 3** statuses on the board (e.g., To Do → In Progress → In QA → Done — **exact names** depend on your workflow).
6. **Saved filter:** JQL `project = <KEY> AND assignee = currentUser() AND updated >= -7d` (adapt if `currentUser()` unsupported).
7. **Dashboard** or **board screenshot**: capture **final** state.

### Deliverables

- `jira_checklist.md` — copy `templates/jira_checklist.md` and check boxes **honestly**.
- **Screenshots** OR **exported CSV** of issues (instructor preference).
- Paste **JQL** you used for the filter into `jira_checklist.md`.

## Option B — No Jira access (simulation)

Complete `templates/paper_jira_pack.md` as if you were the PO + tester: **write** issue keys `SIM-101…`, statuses, and **transition log** table.

## Definition of Done

- [ ] Epic + **3** stories + **2** bugs exist (live or paper).
- [ ] **Every** story has **testable** AC (instructor spot-checks **one**).
- [ ] **≥ 1** bug has **repro** steps **numbered**.
- [ ] **Sprint** contains **planned** work and shows **movement** across workflow (paper: explicit table).
- [ ] **Filter** JQL documented.

## Ethics

**No real customer PII.** Use **synthetic** emails and **staging** URLs only.
