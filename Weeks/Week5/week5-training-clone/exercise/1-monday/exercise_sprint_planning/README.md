# Exercise: Sprint Planning — Story Points & Sprint Backlog

**Mode:** Conceptual (planning)  
**Time:** ~45–60 minutes  
**Relates to:** `written/1-monday/agile-and-scrum-processes.md`, `scrum-ceremonies.md`, `story-pointing-and-burndown-charts.md`  
**Demo tie-in:** `demos/1-monday/demo_scrum_simulation/`

## Setup

Work in a **table group** of 3–5 (or solo if needed—you will play multiple roles).

**Declared team velocity for this exercise:** **21 story points** per two-week sprint (instructor may change this number—use the value they announce).

## Tasks

1. Open `templates/product_backlog.csv`. Each row is a **user story** with a **hint** column—**do not** use hints as points; they are for instructors only.
2. **Estimate** each story using **Fibonacci story points** (1, 2, 3, 5, 8, 13). Use **Planning Poker** style: discuss, then reveal estimates; reconcile disagreements with **assumption** notes.
3. **Order** the backlog **by value/risk** as a team (write your rationale in 2–3 bullets).
4. Select stories for **Sprint 1** that **fit ≤ 21 points** and write a **Sprint Goal** **one sentence** that connects the selected work.
5. For **one** chosen story, decompose into **3–6** tasks (dev, test, docs, spikes)—at least **two** tasks must be **testing-related**.

## Deliverables

Submit **one** bundle per group:

| File | Content |
|------|---------|
| `sprint_plan.md` | Sprint Goal; table of chosen stories with **final** points; running total; **explicit** statement if you **left** capacity unused |
| `estimation_notes.md` | For **two** stories where estimates diverged, what **assumption** resolved it? |
| `backlog_ordering.md` | Why you ordered the backlog this way (risk/value) |
| `tasks_STORY-ID.md` | Task breakdown for one story |

Use the templates in `templates/` and rename/copy as needed.

## Definition of Done

- [ ] Every story in `product_backlog.csv` has a **point** estimate and a **one-line** rationale or assumption where **13** or **8** was used.
- [ ] Sprint 1 **respects** velocity cap (≤ 21 with instructor’s number).
- [ ] Sprint Goal is **coherent** (not a generic “finish work”).
- [ ] Task breakdown includes **test** work and **clear** “done” hints (e.g., “automated API check passes on staging”).

## Stretch

Add a **mini burndown table**: Day 0–5 **ideal remaining points** vs a **hypothetical actual** that shows **one** mid-sprint impediment—**one sentence** explaining the chart.
