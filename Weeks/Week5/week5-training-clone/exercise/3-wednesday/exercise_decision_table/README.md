# Exercise: Decision Table → Test Cases

**Mode:** Conceptual (tables)  
**Time:** ~45–55 minutes  
**Relates to:** `written/3-wednesday/decision-table-testing.md`  
**Demo tie-in:** `demos/3-wednesday/demo_decision_table/`

## Instructions

1. Read `templates/shipping_business_rules.md`.
2. Build a **decision table** with conditions **PromoActive**, **CartSubtotal ≥ 75**, **CustomerTier in {Gold, Standard}** (two-valued tier), and action **DiscountPercent** in `{0, 10, 15}` per the rules.
3. Use **“-”** (don’t care) **only** where you can **justify** it in one sentence.
4. Derive **one test case per rule column** (minimum). Add **IDs** `DT-SHIP-01…`.

## Deliverables

| File | Purpose |
|------|---------|
| `decision_table.md` | Markdown table(s) + short rule captions |
| `tests_from_table.md` | Test case list with inputs and **expected** discount |
| `assumptions.md` | Any spec ambiguity you resolved |

## Definition of Done

- [ ] **Every** combination from the written rules appears as a **column/rule** **or** is **collapsed** with **documented** “-”.
- [ ] **≥ 6** test cases total (may be more if you do not collapse).
- [ ] At least **one** test is **negative** in the sense of “customer expects discount but gets 0” (if applicable).

## Stretch

Rewrite **tier** as **three** levels (**Gold / Silver / Standard**) with a **new** rule: Silver gets **5%** only if `PromoActive` and subtotal ≥ **100**. Extend your table—how many rules **before** collapse?
