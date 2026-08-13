# Exploratory Testing

## Learning Objectives

By the end of this reading you will be able to:

- Define **exploratory testing** and explain how it differs from scripted testing in purpose and execution.
- Design an **exploratory testing session** using a charter, time box, oracles, and notes.
- Select appropriate **test charters** based on risk areas and feature context.
- Explain the **debrief** step and how session findings feed back into the team's quality process.
- Describe when exploratory testing complements — and when it replaces — scripted test cases.

---

## Why This Matters

Scripted test cases are essential for regression coverage, compliance evidence, and repeatable verification. But they are backward-looking by nature: they test what someone already anticipated. The biggest defects in production are often the ones nobody anticipated.

Exploratory testing is the discipline of **discovering what was not anticipated** — by applying skill, experience, curiosity, and structured investigation to the system in real time. It is how testers find the bugs that "no one could have written a test case for" — because they require a human intelligence actively reacting to what the software does, not just comparing output to a pre-written expected result.

In Agile teams with short sprint cycles, exploratory testing is essential: it compensates for the fact that automated regression suites are always one sprint behind the latest features.

---

## The Concept

### What Is Exploratory Testing?

**Exploratory testing** is a style of software testing where the tester simultaneously **designs test cases** (what to try next), **executes those tests**, and **learns** from the results — all in real time, guided by a mission rather than a pre-written script.

The definition is attributed to Cem Kaner: *"Exploratory testing is simultaneous learning, test design, and test execution."*

This is not the same as "just playing around with the software" or "ad hoc testing." The key difference is **discipline and structure**:

- **Ad hoc testing:** No plan, no notes, no time box. Whatever comes to mind. Impossible to audit or reproduce.
- **Exploratory testing:** Guided by a **charter** (mission), bounded by a **time box**, documented in **session notes**, and followed by a **debrief** that feeds findings back to the team.

---

### Scripted vs Exploratory: Complementary, Not Competing

| Dimension | Scripted Testing | Exploratory Testing |
|-----------|-----------------|---------------------|
| **Test design timing** | Before execution (separate activity) | During execution (simultaneous) |
| **Guidance** | Pre-written steps and expected results | Charter (mission) and oracles (heuristics) |
| **Best for** | Regression, compliance, repeatable verification | New features, complex interactions, risk investigation |
| **Adaptability** | Fixed path; changes require test case update | React to what you see; freely change direction |
| **Documentation** | Test case specification; result log | Session notes; bug reports; coverage map |
| **Learning** | Tests what was anticipated | Discovers what was not anticipated |
| **Audit trail** | Strong (precise, reproducible) | Lighter (session notes; must discipline to capture) |

The most effective teams use **both**:
- Automated scripted regression tests provide a **safety net** for known behaviors.
- Exploratory sessions **discover new defects** in recently changed or risky areas.

---

### Session-Based Test Management (SBTM)

The most widely used structure for exploratory testing was developed by **Jon Bach** and **James Bach** — **Session-Based Test Management (SBTM)**. It gives exploratory testing the discipline and auditability that managers and compliance teams need.

A **testing session** has five components:

#### 1. Charter (Mission)

The charter defines: *What are we investigating? Why? What areas are in scope or out of scope?*

A good charter is:
- **Specific enough** to give direction.
- **Open enough** to allow discovery.
- **Risk-focused**: what could be wrong that users would care about?

**Charter template:**
```
Explore [AREA/FEATURE]
Using [DATA/CONDITIONS/TOOLS]
To [DISCOVER/INVESTIGATE/VERIFY] [RISK or QUESTION]
```

**Examples:**

Good:
> "Explore the checkout flow using expired coupon codes, zero-quantity items, and session expiry mid-transaction to discover error handling gaps that could cause data inconsistency."

Too vague:
> "Test the checkout."

Good:
> "Explore the file upload feature using files near the size limit (9.9 MB, 10 MB, 10.1 MB), unsupported file types (PDF, EXE), and corrupt files to investigate whether error messages are clear and whether partial uploads are handled safely."

#### 2. Time Box

A **time box** is a fixed-duration commitment for the session — typically **60 to 90 minutes**. The time box:
- Prevents "rabbit hole" syndrome where a tester spends 4 hours investigating one obscure issue.
- Creates a cadence that fits sprint work (you can run 2–3 sessions in a day).
- Enables progress reporting: "I completed two sessions today; here is what each found."
- Focuses the tester: knowing time is limited encourages covering more area rather than diving infinitely deep.

After the time box ends, either file the current thread's notes and stop — or start a new session with a new charter if the investigation warrants it.

#### 3. Oracles (Heuristics for "Something Is Wrong")

An **oracle** is a principle or heuristic you use to decide: "Is this behavior correct, or is it suspicious?"

Since exploratory testing does not have pre-written expected results, oracles are how testers make judgment calls in real time.

**Common software testing oracles:**

| Oracle | Description | Example |
|--------|-------------|---------|
| **Specification** | Behavior matches the written requirements or AC. | Login error message does not match the agreed wording. |
| **Comparable products** | Behavior similar products exhibit. | Competitor's equivalent feature handles this scenario more gracefully. |
| **History** | Behavior the product previously exhibited. | This feature worked last sprint; now it behaves differently. |
| **User expectations** | What a user would reasonably expect. | Clicking "Cancel" during an operation clears data without confirmation — unexpected. |
| **Internal consistency** | The system behaves consistently with itself. | Error message on page A says "minimum 3 characters"; page B says "minimum 4 characters." |
| **Explainability** | You can explain why this behavior is correct. | The calculation result is 143.2 — can you explain every step that produced that number? |

Oracles are learned through experience — the more software you test, the more "that looks wrong" intuition you develop.

#### 4. Session Notes

**Notes** are the audit trail and living record of an exploratory session. They should capture:
- **Areas visited** (coverage map): "I explored the registration form, the email verification flow, and the profile edit page."
- **Bugs found**: Bug report details or Jira ticket IDs created during the session.
- **Questions and observations**: "Why does the logout button appear in both the top nav and the footer? Is this intentional? Which one logs out first if both are clicked?"
- **Ideas for follow-up**: "The error handling for the SMTP timeout was not visible in this session — that should be a separate charter."
- **Time spent**: Total session time; time on setup vs investigation vs documentation.

A good note-taking habit during exploration enables a useful debrief and means the session is defensible if an auditor asks: "What was tested last sprint?"

#### 5. Debrief

The **debrief** is the conversation after the session where the tester shares findings with the team:
- "Here is what I explored."
- "Here is what I found — bugs, risks, questions."
- "Here is what I did not reach that remains a risk."
- "Here is what I think should be explored next."

The debrief converts individual tester knowledge into **team awareness** and feeds the next sprint's planning with actionable risk information. Without the debrief, exploratory testing produces isolated bug reports but misses its broader value.

---

### Exploratory Testing Heuristics

Experienced testers use **heuristics** — mental shortcuts and mnemonics — to generate test ideas quickly during a session. Some commonly used ones:

**SFDIPOT (San Francisco Depot)** — James Bach's mnemonic for what to investigate:
- **S**tructure: What is it made of? (UI, data, code)
- **F**unction: What does it do?
- **D**ata: What information does it handle?
- **I**nterface: How does it connect to other components?
- **P**latform: What environment does it run on?
- **O**perations: How is it used (user workflows, concurrent users)?
- **T**ime: Time-related behaviors (delays, timeouts, expiry, time zones).

**FEW HICCUPS** — Michael Bolton's oracle heuristic (each letter represents a consistency oracle):
Familiar, Explainable, World (standards), History, Image, Comparable products, Claims, User expectations, Product, Purpose, Statutes (laws/regulations).

These heuristics are tools to generate session directions when you are unsure where to investigate next.

---

### When to Use Exploratory Testing

**High-value situations for exploratory testing:**

- **New features with incomplete or evolving specs:** The spec changes as the feature is built. Scripted tests can't keep up. Exploratory sessions provide coverage without requiring a finished specification.
- **Complex workflows and state management:** User journeys that involve multiple steps, state changes, and branching paths (pairs naturally with Thursday's state transition testing).
- **After automation passes:** The regression suite has passed — now explore the areas it doesn't cover: UX, user mental models, edge cases not in the test data.
- **Bug investigation:** "Someone reported a weird error in checkout — let me explore around that area."
- **Release pre-flight:** Final review of the increment before sprint review or release.
- **Usability and accessibility spot-checks:** Human judgment required.

---

## Worked Example: A Complete Exploratory Testing Session

**Feature:** File Import in a data analytics tool. Users can upload CSV files to import sales data.

---

**Charter:**
> Explore the file import feature using CSV files near the size limit (48MB, 50MB, 52MB), files with missing headers, files with special characters in data cells, and corrupt binary files disguised as CSVs — to discover error handling gaps, data integrity issues, and whether server-side validation is sufficient.

**Time box:** 75 minutes

**Oracles:** Specification (max 50MB per requirements), history (similar features had buffer overflow in large files), user expectations (clear, actionable error messages).

---

**Session notes (abbreviated):**

*[0:00–0:10] Setup:*
- Created 4 test files: sales_48mb.csv (valid), sales_50mb.csv (exactly at limit), sales_52mb.csv (over limit), corrupt_data.csv (binary content with .csv extension).
- Logged into staging as test user `import@example.com`.

*[0:10–0:35] Happy path + boundary exploration:*
- 48MB upload: SUCCESS. Import completed in 12s. 48,200 rows imported correctly. ✅
- 50MB upload: SUCCESS. Import completed in 13s. ✅ (Edge: exactly at limit — acceptable.)
- 52MB upload: ERROR. System shows "Upload failed" with no further detail. ❌ **BUG: Error message not user-actionable — should say "File exceeds 50MB limit." Filed as DEF-301.**

*[0:35–0:50] Data quality exploration:*
- CSV with missing `revenue` column header: System imported data with wrong column mapping — no validation error shown. ❌ **BUG: System should validate required columns before importing. Filed as DEF-302.**
- CSV with `$` signs in currency cells ("$12,500.00"): System imported as string, not numeric. Data analysis functions broke. ❌ **BUG: Filed as DEF-303.**

*[0:50–1:05] Security/resilience exploration:*
- corrupt_data.csv (binary disguised as CSV): System attempted to parse binary content — displayed a raw Java stack trace in the UI. ❌ **CRITICAL BUG: Stack trace exposed internal server details. Filed as DEF-304 (High severity).**
- Empty CSV (headers only, no data rows): Imported successfully, showing "0 records imported." ✅ Graceful handling.

*[1:05–1:15] Session wrap-up and notes:*
- Areas not reached this session: Import with duplicate row keys; concurrent uploads by same user; import cancellation mid-upload.
- Coverage map: Upload validation ✅, file size boundary ✅, data type handling ✅, error messaging (gaps found), security (critical gap found).

---

**Debrief summary (shared in standup):**

> "I found 4 defects in the file import feature: DEF-304 is Critical — a stack trace is exposed when a binary file is uploaded disguised as CSV. DEF-301, 302, 303 are Medium. I did not test concurrent uploads or import cancellation — those are my recommendation for tomorrow's session or for a scripted test case."

---

## Summary

- **Exploratory testing** is simultaneous learning, test design, and execution — not random clicking. It discovers what scripted tests miss.
- **SBTM structure**: Charter (mission) + Time box (60–90 min) + Oracles (heuristics for "wrong") + Notes (coverage + findings) + Debrief (team sharing).
- Oracles help testers make real-time judgments about suspicious behavior without pre-written expected results.
- Exploratory and scripted testing are **complementary**: scripted provides regression safety; exploratory discovers novelty and risk.
- Use exploratory testing for: new features, complex state, post-automation gap investigation, bug triage, and release pre-flight.
- The **debrief** converts individual session knowledge into team quality intelligence.

---

## Additional Resources

- [Explore It! (Elisabeth Hendrickson)](https://pragprog.com/titles/ehxga/explore-it/) — The definitive practitioner book on exploratory testing techniques.
- [James Bach — Session-Based Test Management](https://www.satisfice.com/download/session-based-test-management) — Original SBTM paper.
- [Michael Bolton — Heuristic Test Strategy Model](https://developsense.com/resources/) — SFDIPOT and oracle heuristics.
- [Ministry of Testing — Exploratory Testing Collection](https://www.ministryoftesting.com/) — Community articles, tutorials, and discussions.
