# Exercise: EP + BVA + Error Guessing

**Mode:** Conceptual (test design)  
**Time:** ~60–75 minutes  
**Relates to:** `written/5-friday/equivalence-partitioning.md`, `written/4-thursday/boundary-value-analysis.md`, `written/5-friday/error-guessing.md`, `written/5-friday/positive-and-negative-testing.md`  
**Demo tie-in:** `demos/4-thursday/demo_bva/` (BVA pattern on a registration form), `demos/5-friday/demo_equivalence_partitioning/` (EP applied to form validation), `demos/5-friday/demo_positive_negative/` (EP-derived negative test sets + authorization category)

## Instructions

1. Read `templates/feature_spec.md` (**Event RSVP** API snippet).
2. Produce **`test_design.md`** with **three** numbered sections:

### Section A — Equivalence partitions

- List **valid** and **invalid** partitions for **each** input (`event_id`, `guest_email`, `party_size`).
- For **each partition**, pick **one** representative test (ID `EP-###`).

### Section B — Boundary values

- Identify **ordered** domains (if any). Apply **BVA** (min, min−1, max, max+1 where applicable).
- Tests IDs `BVA-###`. **Note:** `party_size` is integer **1–8** inclusive for this spec.

### Section C — Error guessing

- Add **≥ 5** **guess-based** tests `EG-###` not already covered by EP/BVA (e.g., unicode, header tampering, double-submit **hypothesis**).
- For each, state **oracle** (what you would observe).

## Definition of Done

- [ ] **≥ 8** EP-derived tests total across inputs (can be more).
- [ ] **≥ 6** BVA tests touching **party_size** and **at least one** other boundary you argue is relevant.
- [ ] **≥ 5** error-guessing tests **distinct** from EP/BVA rows (not re-labeled duplicates).
- [ ] **One** paragraph: **Which testing principle** (week recap) most influenced your prioritization?

## Deliverable

Single `test_design.md` (Markdown tables encouraged).

## Stretch

Add **decision table** **mini** (Promo flag × party_size bucket) **only** if you can keep it **≤ 8** columns.
