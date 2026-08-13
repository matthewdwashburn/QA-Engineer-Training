# Exercise: Branch & Condition Coverage (White-Box)

**Mode:** Hybrid (read code + analysis)  
**Time:** ~40–50 minutes  
**Relates to:** `written/3-wednesday/white-box-testing.md`, `written/3-wednesday/conditional-testing.md`, `written/2-tuesday/statement-testing.md`  
**Demo tie-in:** `demos/3-wednesday/demo_white_box_coverage/` (coverage hierarchy + pytest --cov-branch) and `demos/3-wednesday/demo_conditional_testing/` (compound predicates, short-circuit, MC/DC)

> **Note:** The starter code (`shipping_eligibility.py`) uses a **different function** from the demos (`shipping.py`). This is intentional — you are applying the same techniques to unseen code, which is how coverage analysis works in practice.

## Instructions

1. Open `starter_code/shipping_eligibility.py`. **Do not change** the implementation.
2. Draw **all decisions** (diamonds) on paper **or** complete `templates/control_flow_stub.mermaid` with your own nodes/arrows.
3. Answer the questions in `analysis.md` (create this file):
   - **Statement coverage:** Minimum number of tests to execute **every statement**? List **input tuples** `(cart_subtotal: float, region: str, is_member: bool)` you would use.
   - **Branch (decision) coverage:** Minimum tests so **each decision** evaluates to **True** and **False** at least once?
   - **Condition coverage** for the **compound** `if` in `member_discount_ok`: list tests that make `is_member` **T/F** and `cart_subtotal >= 50` **T/F** across the suite (watch **short-circuit** in Python).

## Definition of Done

- [ ] `analysis.md` includes **numbered** tests `T1, T2, …` with **inputs** and **expected return** (`True`/`False`).
- [ ] Explicit sentence: “**Short-circuit** affected my condition coverage plan in this way: …”
- [ ] **One paragraph:** If you achieved **100% branch** coverage, could the logic still be **wrong** for the **business**? Why?

## Constraints

- Treat `region` case as **given** in docstring (uppercase only in tests if you want—implementation uses `.upper()`).

## Stretch

Add **one** **mutant** idea (what single-line change would **not** be caught by your tests?)—conceptual only.
