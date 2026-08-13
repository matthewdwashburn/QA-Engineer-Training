# Defect Lifecycle

## Learning Objectives

- Describe typical **defect states** from discovery to closure.
- Explain **reopen** flows and **duplicate/invalid** handling.
- Connect lifecycle to **Jira workflows** (detailed Friday).
- Define **severity** (impact on the system) and **priority** (urgency of the fix) and apply both during triage.
- Distinguish between a **high-severity/low-priority** defect and a **low-severity/high-priority** defect with real examples.

## Why This Matters

Teams coordinate through **shared states**. Precise **transitions** reduce **lost bugs**, **duplicate** work, and **mis-triage**—especially when multiple roles touch an issue.

## The Concept

Lifecycle **names** vary by tool, but the **pattern** is universal:

### Common states

1. **New / Open** — Discovered, logged, awaiting **triage**.
2. **Assigned / In progress** — A developer owns **investigation/fix**.
3. **Fixed / Ready for retest** — Fix merged/deployed to a testable environment.
4. **Re-testing** — Tester verifies resolution against **acceptance** of the fix.
5. **Closed** — Confirmed fixed or **accepted** risk documented.
6. **Reopened** — Failure persists or **regression** introduced; returns to dev.

### Additional resolutions

- **Duplicate** — Same root cause as another issue; link items.
- **Cannot reproduce** — Needs more **data**; may return to **New** when info arrives.
- **Won’t fix / Deferred** — Business decision; document **risk**.
- **Not a defect** — Works as specified; may trigger **requirement change** instead.

### Triage

During triage the team assigns two independent ratings to every defect: **Severity** and **Priority**. These are often confused — understanding the difference is one of the most important skills a QA Engineer develops early in their career.

---

#### Severity — How Bad Is the Impact?

**Severity** measures the **technical impact** a defect has on the system or the user experience. It is assessed by the **tester** based on what the system does (or fails to do) when the bug occurs.

Severity is independent of business deadlines or commercial considerations — it is purely about the observable effect of the failure.

| Severity Level | Definition | Example |
|---|---|---|
| **Critical** | System crash, data loss, or complete loss of a core feature — no workaround exists | Payment gateway throws a 500 error and no order is recorded; users cannot check out at all |
| **Major / High** | A significant feature is broken or produces wrong results; a workaround exists but is painful | The "Forgot Password" email is never sent — users must contact support to reset |
| **Minor / Medium** | A feature works but behaves incorrectly in an edge case; the workaround is straightforward | Filtering a product list by price returns results in the wrong sort order, but the data is correct |
| **Low / Trivial** | Cosmetic or minor UX issue; no functional impact | The company logo on the login page is 5 px misaligned on Firefox |

> **Rule of thumb:** Ask *"If this bug shipped to production right now, how many users would be blocked or harmed, and how badly?"*

---

#### Priority — How Soon Must It Be Fixed?

**Priority** measures the **business urgency** of resolving the defect. It is assigned during triage, typically by the **Product Owner** in consultation with the Test Lead, and drives **scheduling** — which sprint or release the fix lands in.

Priority considers factors like: upcoming release dates, customer visibility, regulatory requirements, and commercial impact.

| Priority Level | Definition | Example |
|---|---|---|
| **P1 — Immediate** | Must be fixed before the current release goes live; a blocker for go/no-go | The checkout button does nothing on mobile — release is tomorrow |
| **P2 — High** | Must be fixed in the next sprint or release cycle | Exported CSV reports contain commas inside values that break Excel imports — reported by key enterprise clients |
| **P3 — Medium** | Should be fixed within the next 2–3 sprints; tracked actively | The date picker does not support keyboard navigation — an accessibility issue but not yet flagged by compliance |
| **P4 — Low** | Nice to fix; scheduled opportunistically | The footer copyright year still reads 2024 |

> **Rule of thumb:** Ask *"If we do not fix this before the next release, what is the business consequence?"*

---

#### Why Severity ≠ Priority

These two dimensions are **independent** and the mismatch between them is where experienced testers add real value. Here are two classic examples:

**Example A — High Severity, Low Priority**

> A tester finds that entering `NULL` as a product ID in the internal admin tool crashes the server.

- **Severity: Critical** — the system crashes; data integrity is at risk.
- **Priority: P3 (Medium)** — only internal staff use this tool; the input is validated in the UI; the crash cannot be triggered by regular customers. It is serious but not release-blocking.

**Example B — Low Severity, High Priority**

> The company logo on the homepage login page is broken (shows a missing-image icon) one week before a major product launch event.

- **Severity: Low / Trivial** — no functionality is affected; users can still log in.
- **Priority: P1 (Immediate)** — the CEO will be demoing the product live at a conference. A broken logo on the first screen is unacceptable from a brand perspective.

These examples illustrate that **triage is a conversation**, not a solo call. The tester brings the severity assessment; the Product Owner brings the business context that informs priority.

---

#### Severity × Priority Triage Matrix

Use this matrix as a quick reference during defect triage sessions:

|  | **P1 — Immediate** | **P2 — High** | **P3 — Medium** | **P4 — Low** |
|---|---|---|---|---|
| **Critical** | Fix now — release blocker | Fix this sprint | Fix next sprint | Rare; document risk |
| **Major** | Fix now if visible to users | Fix this sprint | Backlog — active | Backlog — passive |
| **Minor** | Unusual; escalate to PO | Fix if time allows | Normal backlog | Opportunistic |
| **Low** | Brand/compliance driven | Low-traffic workaround | Cosmetic backlog | Won't fix candidate |

**Tip:** Align your team's severity and priority definitions **once**, write them down (e.g., in Confluence or a team wiki), and reference them every sprint. Consistent labels prevent the escalation arguments that slow triage down.

---

**Align definitions** across **PO**, **dev**, and **test** to avoid arguments — a defect with an agreed severity and priority moves through the lifecycle faster and with less friction.

### Evidence expectations

Good defects carry **steps**, **expected vs actual**, **environment**, **logs**, **screenshots**, **build/version**—supports faster **fixed → closed** flow.

## Example: Minimal Workflow

`New → Assigned → Fixed → Retest → Closed`  
If retest fails: `Reopened → Assigned → …`

## Summary

- Lifecycle tracks **accountability** and **verification** of fixes.
- **Closures** should be **honest** — document accepted risks instead of silent waivers.
- **Severity** = technical impact on the system; assessed by the **tester**.
- **Priority** = business urgency of the fix; driven by the **Product Owner** and business context.
- A defect can be **high severity and low priority** (critical crash in an internal admin tool only staff use) or **low severity and high priority** (broken logo one day before a public demo).
- Aligning severity and priority definitions **as a team** prevents triage arguments and keeps defects moving through the lifecycle efficiently.

## Additional Resources

- [ISTQB Glossary — defect](https://glossary.istqb.org/) — Official definitions for defect, error, failure, and severity.
- [ISTQB Glossary — severity](https://glossary.istqb.org/en_US/term/severity) — Formal definition distinguishing severity from priority.
- [Ministry of Testing — Severity vs Priority](https://www.ministryoftesting.com/articles/bug-severity-vs-priority) — Practical guidance with real-world examples.
- Friday: `tracking-issue-statuses.md` — Jira workflows, filters, and how severity/priority fields map to board views.
- Demo: `demos/demo_defect_lifecycle.md` (when published).
