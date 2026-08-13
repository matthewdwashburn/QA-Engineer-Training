# Scrum Ceremonies (Events)

## Learning Objectives

By the end of this reading you will be able to:

- List the **five Scrum events** and the primary purpose of each.
- Describe who participates, what is produced, and what the timebox is for each event.
- Explain how testers contribute meaningfully in **each event** — beyond "test status updates."
- Identify anti-patterns that make Scrum events ineffective and the healthy alternatives.

---

## Why This Matters

Scrum events are where the team's **decisions, alignment, and improvements happen**. If you attend these events passively, you miss your most powerful opportunities to:

- Clarify ambiguous acceptance criteria **before** developers write the wrong code.
- Surface quality risks **before** they become expensive defects.
- Demonstrate testing value **visibly** to Product Owners and stakeholders.
- Influence process improvements through the Retrospective.

Testers who use these forums well are indispensable. Testers who treat them as overhead are invisible.

---

## The Concept

### Why Events Matter: Inspect and Adapt

Scrum's three pillars are **transparency, inspection, and adaptation**. The five events exist to provide **structured opportunities** to inspect the product and the process, and to adapt based on what is found.

All Scrum events are **timeboxed** — there is a maximum duration. Exceeding the timebox is a signal that the event needs facilitation improvement, the team needs better preparation, or the team size is too large for efficient conversation.

The five events are:

1. The Sprint (container for all work)
2. Sprint Planning
3. Daily Scrum
4. Sprint Review
5. Sprint Retrospective

---

### Event 1: The Sprint

**Purpose:** The Sprint is the **container** for all Scrum work. Everything — planning, development, testing, review, retrospective — happens inside a Sprint.

**Key properties:**
- Fixed length: **one month or less**. Two-week sprints are the most common.
- Consistent duration: teams should not change sprint length frequently, as it disrupts velocity measurement and planning cadence.
- The **Sprint Goal** must remain stable. Mid-sprint, the team cannot arbitrarily add new goals — but they can clarify and renegotiate scope with the PO to protect the goal.
- Sprints have no "phases" inside them. There is **no mini-Waterfall** where the first week is "development" and the second week is "testing." Work flows continuously.

**Tester contribution to the Sprint:**
- Testing activities happen throughout the Sprint, not only at the end.
- Stories should be tested and verified as they are completed — not queued up for a testing day at the end of the sprint.
- Exploratory sessions, regression runs, and defect investigation happen in parallel with development of other stories.

---

### Event 2: Sprint Planning

**Purpose:** Answer three questions for the upcoming Sprint:
1. **Why is this Sprint valuable?** (The Sprint Goal)
2. **What can be Done this Sprint?** (Selected backlog items)
3. **How will the chosen work get Done?** (Initial Sprint plan)

**Participants:** Whole Scrum Team. The PO presents prioritized backlog items and clarifies intent. Developers (including testers) decide what they can commit to and how.

**Timebox:** Maximum **8 hours** for a one-month sprint. For a two-week sprint: typically 2–4 hours.

**Outputs:**
- **Sprint Goal** — A short, meaningful statement of what the sprint will achieve for users or the product.
- **Sprint Backlog** — The selected backlog items, plus a plan for how the team will deliver them (tasks, dependencies, estimates).

---

#### Tester's Role in Sprint Planning

Sprint Planning is the tester's **first opportunity for shift-left quality** on each sprint's stories.

**Active contributions:**

**1. Identify missing or ambiguous acceptance criteria:**
Before a story is pulled into the sprint, ask:
- "What should happen if the user submits this form with only spaces in the required field?"
- "Is this validation client-side, server-side, or both? It matters for test design."
- "Does 'within 2 seconds' mean 95th percentile or maximum?"

Stories that cannot answer these questions should not be pulled into the sprint. Flag them as "needs refinement" and replace with a ready story.

**2. Surface dependencies and risks:**
- "Story 47 needs the payment gateway mock — is that available?"
- "Stories 50 and 51 both modify the user profile schema. If they're worked in parallel, there could be a merge conflict that delays integration testing."

**3. Plan test activities as tasks:**
Testers make testing work visible by adding explicit tasks to the Sprint Backlog:
- "Write test cases for Story 44 — 2 hours."
- "Set up test data: 50 sample orders with various states — 1 hour."
- "Exploratory session on Story 46 post-development — 2 hours."
- "Run regression suite after Story 48 merges — 30 minutes."

This prevents the "we're done with development, now we need to test" surprise on sprint day 9.

**4. Raise capacity and environment concerns:**
- "I'll be on annual leave Thursday and Friday — should we adjust what we pull in?"
- "The staging environment is being refreshed Monday; I won't have it until Tuesday. Factor that into planning."

---

### Event 3: Daily Scrum (Daily Standup)

**Purpose:** **Inspect progress toward the Sprint Goal** and **adapt the Sprint Backlog** as needed. The Daily Scrum is a planning meeting for developers — not a status report to management.

**Participants:** Developers (required). PO and SM may attend as observers or participants, depending on team norms.

**Timebox:** **15 minutes**. Non-negotiable — the whole point is efficiency.

**Common format (Scrum does not mandate a specific format, but many teams use):**
- What did I do yesterday that helped the Sprint Goal?
- What will I do today to help the Sprint Goal?
- Are there any impediments?

**What Daily Scrum is NOT:**
- A detailed progress report to management.
- A problem-solving session (take complex issues offline after the standup).
- An opportunity for the Scrum Master to assign work.

---

#### Tester's Role in Daily Scrum

Testers should communicate in terms of the **Sprint Goal and flow**, not personal task updates.

**Anti-pattern (unhealthy):**
> "Yesterday I ran 23 test cases for Story 44. Today I'm going to run another 15 test cases. No blockers."

This update tells the team nothing useful. Is Story 44 on track? Were defects found? What is the risk to the sprint?

**Healthy alternatives:**

> "Story 44 is verified — all test cases passed. I'm moving to exploratory testing on Story 46 this morning. Blocker: the test environment database is not seeded with the sample data needed for Story 47 — can someone help me after standup?"

> "Story 48 is at risk. The API change from yesterday broke three of my automated integration tests — I'm investigating now and will know in 30 minutes if it's a test issue or a code issue."

> "No blockers. I'm pairing with [Developer] this afternoon to clarify the expected error codes from the payment service — this will directly improve coverage on Story 50."

The goal is: give the team information they need to adapt the Sprint Backlog and Sprint Goal today.

---

### Event 4: Sprint Review

**Purpose:** **Inspect the outcome of the Sprint** and **adapt the Product Backlog** collaboratively with stakeholders. The Sprint Review is not a demo meeting — it is a working session where the team and stakeholders collaborate on "what next."

**Participants:** Scrum Team + invited stakeholders (PO determines who will provide useful feedback). Users, customers, business representatives, dependent teams.

**Timebox:** Maximum **4 hours** for a one-month sprint. For two-week sprints: typically 1–2 hours.

**Content:**
- The team demonstrates working increment functionality.
- Stakeholders provide feedback.
- The PO discusses what has been Done and not Done.
- Progress toward the Product Goal is discussed.
- The Product Backlog is updated based on the conversation.

---

#### Tester's Role in Sprint Review

Sprint Review is where the tester demonstrates **quality evidence and honest transparency** about the sprint's output.

**Active contributions:**

**1. Demonstrate quality-relevant behaviors:**
Rather than only showing the "happy path," include:
- An edge case that was found and fixed: "Early in the sprint we discovered that entering a quantity of 0 didn't show a validation error — that's now fixed and here's what it looks like."
- A NFR check: "The search now returns results within 800ms on our test dataset of 50,000 products."
- A known risk: "Everything in scope is working as specified. We have not yet tested on Internet Explorer 11, which one stakeholder mentioned. That's flagged as a risk for the next sprint."

**2. Maintain honesty about what was not tested:**
If time constraints meant exploratory testing on Story 52 was deferred, say so: "Story 52 was demo-ready, but we ran out of time for exploratory testing. I recommend it not go to production until that's done."

**3. Capture feedback as test-relevant backlog items:**
When stakeholders say "it would be better if..." — these are potential stories. As a tester you can help articulate the acceptance criteria for those future stories on the spot.

---

### Event 5: Sprint Retrospective

**Purpose:** **Improve quality and effectiveness** for the next Sprint. The team inspects itself — its processes, practices, tools, and interactions — and identifies the most valuable improvement to implement.

**Participants:** Scrum Team (PO, SM, Developers).

**Timebox:** Maximum **3 hours** for a one-month sprint. For two-week sprints: typically 60–90 minutes.

**Common format:** What went well? What could be improved? What will we commit to changing?

The retrospective produces **one to three specific, actionable improvements** — not a long list of theoretical fixes that gets ignored.

---

#### Tester's Role in Sprint Retrospective

The retrospective is the team's **continuous improvement engine**, and testers have specific quality-improvement insight that others may not see.

**Topics testers commonly raise:**

- **Test debt:** "We have 15 manual test cases that we run every sprint and take 4 hours. Could we automate the top 5 this sprint? I estimate 3 hours to set up, saving 2 hours every sprint going forward."
- **Flaky automation:** "The login automation test fails intermittently — once every 3 runs. We're ignoring it, but it means we trust the suite less. Can we fix or quarantine it?"
- **Slow feedback loops:** "The CI pipeline takes 45 minutes. By the time developers know a build broke, they've context-switched to another story. Can we split the fast tests from the slow ones?"
- **Unclear acceptance criteria pattern:** "For the third sprint in a row, stories were pulled in without clear error-handling criteria. Can we add 'error cases specified' to our Definition of Ready?"
- **Collaboration gaps:** "I only found out about the schema change to the orders table when I saw the migration file in the PR. Can we add a quick 'heads up' for cross-cutting changes to our standup communication?"

The key discipline: **one or two changes per sprint, committed to and measured**, not a wishlist.

---

## Worked Example: Healthy vs Unhealthy Daily Scrum

**Scenario:** The sprint goal is to deliver a user onboarding flow. Day 7 of 10. Story 55 (Email Verification) is in development. Story 56 (Welcome Dashboard) is in testing.

**Unhealthy Daily Scrum:**

> Tester: "Yesterday I executed 18 test cases for Story 56. Today I'm going to do 12 more. No blockers."  
> Developer: "I'm coding Story 55. No blockers."  
> Scrum Master: "OK, great, see you tomorrow."

Nothing actionable happened. Nobody knows if the Sprint Goal is at risk. The 10-day window is closing and no one is adapting.

---

**Healthy Daily Scrum:**

> Tester: "Story 56 is verified and passing. There's one risk: the welcome dashboard makes a call to the analytics service which was flaky yesterday — I saw two timeouts in 30 test executions. I'm flagging it as a risk, not a blocker. Today I'm starting on Story 55 test case prep so I'm ready as soon as the build is available."  

> Developer: "Story 55 is 80% done — the email send logic is working, but I'm blocked waiting for the email template from design. Without it, I can't finish by tomorrow."  

> Scrum Master: "I'll contact the design team immediately after this. If we can't get the template today, should we scope down Story 55 and use a placeholder? Let's discuss offline in 10 minutes."

This standup surfaced two actionable items in 5 minutes — exactly what it is for.

---

## Summary

- Scrum's **five events** (Sprint, Sprint Planning, Daily Scrum, Sprint Review, Sprint Retrospective) provide structured opportunities to inspect and adapt.
- **Sprint Planning** is the tester's shift-left opportunity — clarify acceptance criteria, surface risks, and plan testing work visibly.
- **Daily Scrum** is a 15-minute flow check — testers communicate Sprint Goal impact and blockers, not task lists.
- **Sprint Review** is the transparency checkpoint — demonstrate quality evidence, honest coverage, and known risks.
- **Sprint Retrospective** is the improvement engine — testers raise test debt, automation opportunities, and collaboration patterns to fix.
- **Timeboxes** protect meeting effectiveness; consistently overrunning is a process problem to solve.

---

## Additional Resources

- [Scrum Guide — Scrum Events](https://scrumguides.org/scrum-guide.html#scrum-events)
- [Scrum Guide — Sprint Planning](https://scrumguides.org/scrum-guide.html#sprint-planning)
- [Scrum Guide — Sprint Retrospective](https://scrumguides.org/scrum-guide.html#sprint-retrospective)
- [Retrospective Techniques (Retromat)](https://retromat.org/) — A collection of facilitation techniques for sprint retrospectives.
