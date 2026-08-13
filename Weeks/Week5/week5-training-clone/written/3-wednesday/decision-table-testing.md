# Decision Table Testing

## Learning Objectives

By the end of this reading you will be able to:

- Construct a **decision table** from a set of business rules with multiple conditions and actions.
- Enumerate **rules** (columns) representing all relevant condition combinations.
- Apply **don't care (–)** notation to simplify tables without losing coverage meaning.
- **Collapse** redundant rules to reduce table size while maintaining test intent.
- Derive **test cases** from a decision table.

---

## Why This Matters

Real-world features are rarely governed by a single condition. Consider a promotional discount system that depends on: membership tier, cart total, coupon validity, product category, and regional rules. Testing these combinations in an ad hoc manner is nearly guaranteed to miss important interactions.

Decision tables make **all relevant combinations of business rules explicit and visible**, so that:
- No rule combination is overlooked.
- Tests are traceable to specific business logic.
- Testers, developers, and Product Owners can verify the table against the original specification before a single test is run.
- The table doubles as living documentation of the business rules it represents.

Decision table testing is a core ISTQB Foundation technique and a frequently tested topic in technical interviews.

---

## The Concept

### The Anatomy of a Decision Table

A decision table is structured as a matrix:
- **Rows (upper half):** Conditions — the inputs or business rules that affect the outcome.
- **Rows (lower half):** Actions — the expected outcomes or system behaviors.
- **Columns:** Rules — each column represents one unique combination of condition values.

For binary (Yes/No, True/False) conditions, a table with **N conditions** has a maximum of **2^N rules** (all possible combinations). For multi-valued conditions, the number grows multiplicatively.

**Standard notation:**
- `Y` or `T` = Yes / True — condition is met.
- `N` or `F` = No / False — condition is not met.
- `–` = Don't care — the condition value does not affect this rule's action.
- `X` = Action applies for this rule.

---

### Steps to Build a Decision Table

1. **Identify all independent conditions** that affect the outcome. Keep conditions independent where possible — dependent conditions require careful ordering.

2. **List all distinct actions** (expected outcomes or behaviors). Actions are the rows in the lower half of the table.

3. **Enumerate rules**: Start with `2^N` columns for N binary conditions. Each column represents one combination.

4. **Fill in actions** for each rule — which actions apply for each combination of conditions?

5. **Apply don't care (–)**: After filling in actions, identify conditions whose value does not change the action for some rules. Replace those values with `–` to simplify.

6. **Collapse rules**: Merge columns that have identical actions and differ only in conditions that are already marked `–`.

7. **Derive test cases**: One test case per rule (column), using any representative value for `–` conditions.

---

### Don't Care (–): When and How to Use It

A **don't care** entry in a cell means: for this rule, the value of this condition does not change the action — either outcome (True or False) produces the same result.

**When to use `–`:**
- When one condition makes the action deterministic regardless of another condition's value.
- When a combination of other conditions is **logically impossible** (e.g., "discount applies" when the cart is empty — impossible business logic).
- After collapsing: when multiple rules share the same action and only differ in one condition, that condition can become `–` in the merged rule.

**Example:**
```
Condition: Payment method is credit card
Condition: Is a premium member

Action: Apply 5% credit card cashback
```

If the cashback applies only to credit card payments regardless of membership, then `Is premium member = –` in the rule "Apply cashback when credit card used."

**Risk of incorrect `–` usage:**
Applying `–` when a condition actually matters creates false collapsed rules — your table claims fewer tests are needed than there actually are. Always justify each `–` with a business rule or logical argument.

---

### Worked Example 1: Simple Discount Eligibility

**Business Rule:** A 10% discount applies **only** when:
- The customer is a Premium member, **AND**
- The cart total is ≥ $100.

**Conditions:**
- C1: Premium member? (Y/N)
- C2: Cart ≥ $100? (Y/N)

**Actions:**
- A1: Apply 10% discount

**Full table (2 conditions → 4 rules):**

| | Rule 1 | Rule 2 | Rule 3 | Rule 4 |
|---|--------|--------|--------|--------|
| C1: Premium member? | Y | Y | N | N |
| C2: Cart ≥ $100? | Y | N | Y | N |
| A1: Apply 10% discount | X | | | |

**Interpretation:**
- Rule 1 (Y, Y): Premium member with cart ≥ $100 → apply discount.
- Rules 2, 3, 4: All other combinations → no discount.

**Can we collapse?** Rules 2, 3, and 4 all produce the same outcome (no discount). We can merge them:

| | Rule 1 | Rule 2–4 (merged) |
|---|--------|--------|
| C1: Premium member? | Y | – |
| C2: Cart ≥ $100? | Y | – |
| A1: Apply 10% discount | X | |

Wait — is this accurate? The merged rule claims: "For any other combination, no discount." That is correct per the business rule. The `–` is valid here because in all non-qualifying combinations, the action is the same (no discount).

**Derived test cases:**
- TC1: Premium member, cart $150 → expect 10% discount applied.
- TC2: Premium member, cart $80 → expect no discount.
- TC3: Non-member, cart $150 → expect no discount.
- TC4: Non-member, cart $80 → expect no discount.

---

### Worked Example 2: Three-Condition Shipping Fee

**Business Rule:** Shipping fees depend on:
- C1: Customer location (Domestic / International)
- C2: Order weight > 5 kg? (Y/N)
- C3: Is Express shipping? (Y/N)

**Actions:**
- A1: Standard shipping fee $5
- A2: Standard shipping fee $15 (heavy domestic)
- A3: International shipping fee $20
- A4: International shipping fee $40 (heavy international)
- A5: Express surcharge +$10

**Full table (3 conditions → 8 rules):**

| | R1 | R2 | R3 | R4 | R5 | R6 | R7 | R8 |
|---|----|----|----|----|----|----|----|----|
| C1: Domestic? | Y | Y | Y | Y | N | N | N | N |
| C2: Weight > 5kg? | Y | Y | N | N | Y | Y | N | N |
| C3: Express? | Y | N | Y | N | Y | N | Y | N |
| A1: Std $5 | | | X | X | | | | |
| A2: Std $15 | | | | | | | | |
| A3: Intl $20 | | | | | | | X | X |
| A4: Intl $40 | | | | | X | X | | |
| A5: Express +$10 | X | | X | | X | | X | |

Reviewing and interpreting:
- R1 (Domestic, Heavy, Express): $15 + $10 = $25 → A2 + A5
- R2 (Domestic, Heavy, Standard): $15 → A2
- R3 (Domestic, Light, Express): $5 + $10 = $15 → A1 + A5
- R4 (Domestic, Light, Standard): $5 → A1
- R5 (International, Heavy, Express): $40 + $10 = $50 → A4 + A5
- R6 (International, Heavy, Standard): $40 → A4
- R7 (International, Light, Express): $20 + $10 = $30 → A3 + A5
- R8 (International, Light, Standard): $20 → A3

Each rule becomes a test case. The express surcharge applies in rules 1, 3, 5, 7 — you need tests for all four to cover the Express condition being Y in all domestic/international and weight combinations.

**Collapsing opportunity:**
Rules 1–4 (Domestic) and Rules 5–8 (International) have symmetric structures. The Express condition (`C3`) applies the same `+$10` surcharge regardless of domestic/international — so you could represent Express as an independent add-on column, reducing to 4 base rules + 1 surcharge modifier. This depends on whether the underlying code handles it that way.

---

### Common Mistakes in Decision Table Testing

**1. Missing conditions:**
Forgetting to include a condition that actually affects the outcome. Ask: "Is there any other input that could change the action for one of these rules?"

**2. Dependent conditions treated as independent:**
Example: "Is the cart populated?" and "Is cart total ≥ $100?" are not fully independent. An empty cart cannot have a total ≥ $100. These logical dependencies must be noted and the impossible combinations eliminated (marked as impossible/N/A in the table).

**3. Over-aggressive `–` usage:**
Marking a condition as don't care when it actually affects the outcome for some rules. Always validate: "In this specific rule, does changing this condition's value change the action? If yes, it is not a don't care."

**4. Not testing all rules:**
Building the table correctly but then only writing test cases for the positive paths. All rules — including the "no action" rules — should have corresponding tests to verify the system correctly does nothing when conditions are not met.

---

### Decision Tables in Automated Testing

Decision tables pair naturally with **data-driven testing** (Thursday's topic): each rule becomes a row in a data table, and the test framework iterates through all rows:

```python
import pytest

shipping_cases = [
    ("domestic", True, True, 25.0),   # R1
    ("domestic", True, False, 15.0),  # R2
    ("domestic", False, True, 15.0),  # R3
    ("domestic", False, False, 5.0),  # R4
    ("international", True, True, 50.0),  # R5
    # ... etc
]

@pytest.mark.parametrize("location, is_heavy, is_express, expected_fee", shipping_cases)
def test_shipping_fee(location, is_heavy, is_express, expected_fee):
    result = calculate_shipping_fee(location, is_heavy, is_express)
    assert result == expected_fee
```

This pattern covers all decision table rules with a single parameterized test, making it easy to add new rules by adding rows to the data table.

---

## Summary

- Decision tables make **multi-condition business rules** explicit, visible, and traceable — preventing missed combinations in ad hoc testing.
- Build tables by: identifying conditions → enumerating rules → filling in actions → applying don't care → collapsing.
- **Don't care (–)** reduces table size without losing coverage — but requires justification from the business rule.
- **Collapsing** merges rules that share the same action and differ only in `–` conditions.
- Every rule in a decision table should produce at least one test case — including rules where no action applies (the "should do nothing" paths).
- Decision tables pair naturally with **data-driven test frameworks** for efficient automated coverage of all rules.

---

## Additional Resources

- [ISTQB Foundation Syllabus — Decision Table Testing](https://www.istqb.org/) — Official treatment of the technique.
- [ISTQB Glossary — Decision Table](https://glossary.istqb.org/) — Canonical vocabulary.
- Demo: `demos/3-wednesday/demo_decision_table/` — Instructor walkthrough building a decision table for a discount calculation feature live.
- Exercise: `exercise/3-wednesday/exercise_decision_table/` — Build a decision table and derive test cases for a provided business rule.
