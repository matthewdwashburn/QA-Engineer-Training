# Story Pointing and Burndown Charts

## Learning Objectives

By the end of this reading you will be able to:

- Explain what **story points** are, why teams use them instead of hours, and how estimation scales work.
- Describe the **Planning Poker** technique and why simultaneous reveal matters.
- Define **velocity** and explain how it is used to forecast sprint capacity.
- Interpret **burndown** and **burnup** charts — including what deviation from the ideal line means.
- Articulate how testers use estimation and charts to communicate quality risk.

---

## Why This Matters

As a tester you participate in **sprint planning** and **refinement**. You estimate test activities, flag stories that are too large or too poorly defined to commit to, and use velocity data to argue for realistic testing capacity. When a sprint burns down flat, you know there is a blocker — and being able to say "here's what the chart means and here's the quality risk" makes you a strategic contributor, not just a script executor.

Sprint metrics are also a **trust signal**: stakeholders who see a team consistently delivering on burndown forecasts trust their quality commitments. Understanding charts lets you interpret those signals and act on them.

---

## The Concept

### Why Relative Estimation? The Problem with Hours

When teams estimate in hours, several problems emerge:

- **Individual variation:** A senior developer estimates 4 hours; a junior developer in a different timezone estimates 20 hours. Which is right? Both might be accurate for those individuals, but the team's average is misleading.
- **Interruptions:** Calendar hours are not coding hours. Meetings, context switching, and support tickets consume unplanned time.
- **Unknown unknowns:** Complex stories hide complexity until implementation begins. An "8-hour" story turns into a 3-day investigation of a legacy API.
- **Anchoring:** If one team member says "I think this is 6 hours" before others have thought, the whole group converges on 6 hours — a cognitive bias called **anchoring**.

**Story points** address these problems by shifting estimation from "how long will this take me" to "how complex is this relative to something we've done before."

Story points capture a combination of:
- **Effort** — How much work is involved?
- **Complexity** — How many unknowns? How many moving parts?
- **Uncertainty / Risk** — How confident are we in our understanding?

A 5-point story is not "5 hours" — it means "this feels about as big and complex as the other 5-point stories we've completed." The scale is **relative and local to your team**.

---

### Common Estimation Scales

#### Fibonacci-Like Sequences (1, 2, 3, 5, 8, 13, 21…)

The Fibonacci sequence is used because the **gaps grow as numbers increase**, reflecting how estimation uncertainty grows with story size:

- 1 vs 2: small difference, both feel very concrete.
- 8 vs 13: meaningful difference — a deliberate choice between confidence levels.
- 21+ stories: usually a signal that the story should be **split** into smaller stories.

Many teams use a modified Fibonacci: `1, 2, 3, 5, 8, 13, 21, ?` (or even `XS, S, M, L, XL` mapped to numbers). The exact scale matters less than **using it consistently**.

#### T-Shirt Sizes (XS, S, M, L, XL)

T-shirt sizing is used for **rapid backlog triage** — quickly sorting stories into rough buckets before detailed refinement. It is intuitive for non-technical stakeholders.

A team might map T-shirt sizes to Fibonacci points for capacity planning:
- XS = 1, S = 2, M = 3, L = 5, XL = 8

T-shirt sizes work well for **product roadmap estimation** where precision is less important than relative scale.

---

### Planning Poker: Eliminating Anchoring Bias

**Planning Poker** is the most common Agile estimation technique:

1. The Product Owner reads a story and answers clarifying questions.
2. Each team member privately selects a card with their estimate (using a Fibonacci deck or app).
3. On a signal, **all cards are revealed simultaneously**.
4. If estimates agree closely, use the consensus.
5. If estimates diverge significantly, the **highest and lowest estimators explain their reasoning**.
6. After discussion, vote again.

**Why simultaneous reveal?** It prevents anchoring. If one developer says "I think this is a 5" before others decide, the group unconsciously anchors on 5. Simultaneous reveal captures independent thinking from all team members.

**What divergence tells you:**
- High outlier (someone estimated 13 when others said 3): "I'm seeing a complexity the others aren't — let me explain."
- Low outlier (someone estimated 1 when others said 8): "I may have misunderstood the scope — let me ask."

Both conversations improve shared understanding and prevent later surprises.

**Tester's role in Planning Poker:**
Testers often estimate higher than developers on the same story — because they are thinking about edge cases, test data, environment setup, exploratory session time, and regression impact. This is healthy and valuable:

- "I'm estimating 8 because the integration with the third-party API will need a mock environment and I'll need to test the timeout and retry paths. That's not trivial."

This is not pessimism — it is honest risk-based estimation.

---

### Velocity

**Velocity** is the sum of **story points completed** (per the Definition of Done) in a Sprint.

Key properties:
- Only **Done** stories count. "90% complete" is 0 points.
- Velocity is **historical** — it reflects what the team has actually delivered, not what they planned to deliver.
- Over 3–5 sprints, average velocity stabilizes and becomes a **reliable forecast** for future sprint capacity.

**How velocity is used:**

*Sprint planning:* "Our average velocity over the last 4 sprints is 34 points. Let's pull in stories totalling approximately 32–36 points."

This is **empirical forecasting** — based on evidence, not optimistic assumptions. It prevents overcommitment, which is one of the most common causes of testing being squeezed at the end of a sprint.

**Velocity variations:**
- Team members on leave → plan for lower velocity.
- Ramp-up after a new team member joins → velocity may temporarily dip.
- Story composition changes (more complex work) → velocity may drop without the team becoming less productive.

Velocity is not a productivity target — it is a **forecasting tool**. Do not use it to pressure teams to "go faster."

---

### Burndown Charts

A **Sprint Burndown** chart tracks **remaining work** over the course of the Sprint.

**Axes:**
- X-axis: Sprint days (Day 1 through Day N).
- Y-axis: Remaining work (story points or task hours, depending on what the team tracks).

**Lines on the chart:**
- **Ideal line:** A straight diagonal from total sprint scope (top-left) to zero at sprint end (bottom-right). What "on track" looks like.
- **Actual line:** What is actually happening.

**Interpreting deviations:**

| Chart Pattern | What It Signals |
|--------------|----------------|
| Actual line above ideal | Behind plan — risk to Sprint Goal |
| Actual line below ideal | Ahead of plan — team may pull in more work |
| Flat line for multiple days | Work not completing — blocked stories, underestimation, or scope creep |
| Steep drop late in sprint | "Cliff" pattern — late push, quality risk, testing compressed |
| Actual line jumps up | Scope added mid-sprint — Sprint Goal may be at risk |

**The "cliff" pattern and quality risk:**

A cliff — where the burndown drops sharply in the last 1–2 days — often signals:
- Stories were called "Done" without proper testing.
- Testers worked overtime to verify everything at the end.
- The Definition of Done was compromised under time pressure.

Raising this pattern in a retrospective is a key tester contribution: "Our burndown looked fine until day 8, then we burned all 20 remaining points in 2 days. That means testing for half the sprint happened in the final 2 days. This is a quality risk — let's talk about how to prevent it."

---

### Burnup Charts

A **Burnup chart** shows **two lines**:
- **Scope line:** Total committed work (may change if stories are added or removed).
- **Completion line:** Completed work to date.

**Burnup vs Burndown:**

| | Burndown | Burnup |
|-|---------|--------|
| Shows | Remaining work | Completed work + total scope |
| Scope changes | Visible as unexpected extension of remaining work | Visible as the scope line moving up |
| Better for | Short-term sprint tracking | Long-term release tracking with scope transparency |

**When scope creep occurs:**

On a burndown, if the PO adds stories mid-sprint, the remaining work "bumps up" — but it looks like the team stopped making progress. On a burnup, the scope line rises visibly while the completion line continues its trajectory. Burnup makes scope changes **honest and visible**.

This is why many teams use burnup charts at the release or PI (Program Increment) level to track multi-sprint progress toward a release goal.

---

## Worked Example: Reading a Sprint Burndown

**Scenario:** A two-week sprint with a total commitment of 40 story points.

| Day | Ideal Remaining | Actual Remaining | Notes |
|-----|----------------|-----------------|-------|
| 1 | 40 | 40 | Sprint start |
| 2 | 36 | 38 | Slightly behind |
| 3 | 32 | 35 | Behind — Story 44 blocked on API mock |
| 4 | 28 | 35 | Still flat — environment issue |
| 5 | 24 | 30 | SM escalated environment; some progress |
| 6 | 20 | 24 | Back on track |
| 7 | 16 | 20 | On track |
| 8 | 12 | 18 | Behind — Story 48 (complex integration) larger than estimated |
| 9 | 8 | 12 | Working overtime |
| 10 | 0 | 5 | Sprint ended with 5 points incomplete |

**Tester's read of this burndown:**
- Days 3–4 flat line: test environment was blocking — the tester raised this in Daily Scrum.
- Day 8 behind: Story 48 was bigger than estimated — the tester had flagged this in planning ("I'm estimating 8, not 5") but the team overruled. For the retro: "Let's discuss Story 48 as an estimation lesson — the tester's concern was valid."
- 5 points incomplete: One story was not done. Sprint commitment not fully met. Honest reporting: it goes back to the top of the Product Backlog.

This is how burndown data becomes a **quality conversation**, not just a project management artifact.

---

## Summary

- **Story points** estimate relative effort, complexity, and uncertainty — not hours. They are local to your team and not comparable across teams.
- **Planning Poker** (simultaneous reveal) prevents anchoring bias and surfaces hidden complexity through divergent estimates.
- **Velocity** is historical completed points per sprint — a reliable forecasting tool when used empirically, not as a pressure metric.
- **Burndown charts** show remaining work vs ideal trajectory — flat lines indicate blockers; cliff patterns indicate testing compression.
- **Burnup charts** show completion + scope — scope changes become visible and honest.
- Testers use all of these to: **estimate testing capacity honestly**, **forecast risk**, **explain sprint outcomes**, and **advocate for sustainable quality**.

---

## Additional Resources

- [Scrum Guide — Sprint Planning](https://scrumguides.org/scrum-guide.html#sprint-planning) — Commitment through realistic planning.
- [Mountain Goat Software — User Stories and Estimation (Mike Cohn)](https://www.mountaingoatsoftware.com/agile/user-stories) — Authoritative resource on relative estimation.
- [Agile Alliance — Planning Poker](https://www.agilealliance.org/glossary/poker/) — Community explanation and tools.
- [Atlassian — Burndown Charts](https://www.atlassian.com/agile/tutorials/burndown-charts) — Practical guide to creating and reading burndown charts in Jira.
