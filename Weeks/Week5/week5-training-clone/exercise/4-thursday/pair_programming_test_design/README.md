# Pair Exercise: Shopping Cart — State Model, BVA, Data-Driven Tests

**Mode:** Collaborative (Pair Programming / design review)  
**Time:** ~2.5–3.5 hours (instructor may split across two sessions)  
**Weekly synthesis:** State transitions (`written/4-thursday/state-transition-diagram.md`), BVA (`written/4-thursday/boundary-value-analysis.md`), data-driven testing (`written/4-thursday/data-driven-testing.md`), test documentation (`written/4-thursday/test-documentation.md`)  
**Demo tie-in:** Run these demos **before** releasing this exercise — `demos/4-thursday/demo_state_transition/` (ATM model → same technique at larger scale), `demos/4-thursday/demo_bva/` (registration form BVA → quantity/line-count BVA here), `demos/4-thursday/demo_data_driven/` (pytest parametrize → coupon CSV deliverable here)

## Roles

- **Partner A — Driver (first half):** Owns keyboard/drawing for **state model** draft.
- **Partner B — Navigator:** Challenges missing states, **invalid** transitions, and **edge** values.
- **Swap** roles for the **review + data-driven** section.

## Product brief (shared)

**ShopRight** web cart:

- Guest may add/remove items; **checkout requires login**.
- Max **distinct line items = 20**; max **quantity per line = 99** (integers).
- Applying **invalid coupon** shows error `COUP_BAD` and **does not** change totals.
- Successful **payment** clears cart to **empty** state.
- User may **abandon** checkout returning to **shopping** with cart preserved until session timeout (**ignore timeout** in this exercise—treat as out of scope).

## Deliverables (one **package** per pair)

| # | Artifact | Owner lead | Notes |
|---|----------|------------|-------|
| 1 | `diagrams/cart_state_model.mermaid` | A then B review | **States**, **events**, note **guards** on arrows |
| 2 | `tests/state_transition_tests.md` | B | **≥ 10** tests: **valid** transitions + **invalid** events with **expected** handling |
| 3 | `tests/bva_quantity_price.md` | B | BVA table for **quantity** **1–99** and **line count** **1–20** (pick **boundaries**; justify) |
| 4 | `tests/data_driven_coupon.csv` | A after swap | **≥ 6** rows: columns `cart_subtotal,coupon_code,expected_outcome` (define outcome: success flag + error code) |
| 5 | `docs/test_plan_one_pager.md` | Both | Scope, risks, **entry/exit**, environments, **traceability** to story `SR-CART-01` |
| 6 | `docs/test_summary_skeleton.md` | Both | Fill with **hypothetical** execution summary (what you **would** report after a sprint) |

Use `templates/` starters; **rename** or copy into the paths above.

## Pair protocol

1. **15 min** — Read brief; write **Sprint Goal**-style **test mission** in `docs/test_plan_one_pager.md`.
2. **45 min** — Build **state model** together (Driver draws, Navigator probes).
3. **45 min** — Derive **state tests** + **BVA** tables.
4. **30 min** — Swap roles; build **CSV** + **traceability** section (matrix snippet).
5. **20 min** — **Peer review** another pair’s **invalid transition** coverage (if class size allows) **or** self-review checklist in README.

## Definition of Done

- [ ] State diagram includes **at least 5** distinct **states** and **checkout** as a distinguishable phase.
- [ ] **≥ 2** **invalid** transition tests (event fired in wrong state) with **expected** system response.
- [ ] BVA covers **min/min-1/min+1** style for **both** quantity max and line-count max **or** explains alternative BVA convention used.
- [ ] CSV is **parseable** (header row, consistent columns) and includes **≥ 1** **negative** coupon row.
- [ ] Test plan references **at least three** **risks** (e.g., payment integration, concurrency—can be hypothetical).
- [ ] `test_summary_skeleton.md` states **open defects** as **unknown / none** honestly.

## Submission

Zip/repo folder **`pair_cart_<pairId>/`** with structure above.

## Reflection (5 min, submit as `reflection.md`)

What **one** practice from this week would have **prevented** a defect you hypothesized in the cart?
