# Boundary Value Analysis (BVA)

## Learning Objectives

By the end of this reading you will be able to:

- Identify **boundary values** on ordered domains (numbers, dates, string lengths, collection sizes).
- Apply **two-value BVA** and **three-value BVA** systematically with clear reasoning for each approach.
- Explain *why* defects cluster at boundaries — the types of coding errors BVA is designed to catch.
- Combine BVA with **equivalence partitioning** for a complete, efficient test set.

---

## Why This Matters

Off-by-one errors are among the most common and costly defects in software. A comparison that should be `>= 18` written as `> 18` accepts a 17-year-old as an adult. A loop that should iterate 10 times iterates 9 or 11 and misses the last record or corrupts the next one. A string length limit of 255 characters that actually allows 256 corrupts the database column.

These errors are invisible to testing that uses only "safe" middle-ground values. BVA forces coverage of the exact values where defects hide — making it one of the highest-ROI test design techniques per test case written.

---

## The Concept

### Why Defects Cluster at Boundaries

When a developer writes a conditional — `if quantity >= 1 and quantity <= 10` — they must make four decisions:
1. Which operator for the lower bound? (`>` or `>=`)
2. What value is the lower bound? (1? 0?)
3. Which operator for the upper bound? (`<` or `<=`)
4. What value is the upper bound? (10? 11?)

Any one of these decisions could be wrong. A test that only uses `quantity = 5` will never detect an error in any of them. Tests at the boundaries (0, 1, 10, 11) test all four decisions.

Common boundary defect types BVA catches:
- **Off-by-one** (`>` instead of `>=`, or `10` instead of `11` as the limit)
- **Inclusive vs exclusive** boundaries (whether the boundary value itself is valid)
- **Type limits** (integer overflow at `INT_MAX`, array index `n-1` vs `n`)
- **Date edge cases** (last day of month, leap year, timezone boundary at midnight)
- **String edge cases** (empty string vs single character vs max length)

---

### Equivalence Partitions: The Foundation

Before applying BVA, partition the input domain into **equivalence classes** — groups of values expected to be treated identically by the system.

For a ticket quantity field with the rule "must be an integer between 1 and 10 inclusive":

| Partition | Values | Expected behavior |
|-----------|--------|-------------------|
| **Valid** | 1–10 | Accepted; ticket created |
| **Invalid (too low)** | 0 and below (or empty/null) | Rejected; error message |
| **Invalid (too high)** | 11 and above | Rejected; error message |
| **Invalid (wrong type)** | 1.5, "three", empty string | Rejected; type error |

Equivalence partitioning (Friday's deep dive) selects one representative per class. BVA supplements this by testing at the **boundaries between partitions** — where the most defects live.

---

### Two-Value BVA

**Two-value BVA** (sometimes called "boundary testing") selects the value **at the boundary** and **the adjacent value just outside the valid partition** (or just inside, depending on the syllabus interpretation).

For the ticket quantity range [1, 10] inclusive:

| Boundary | Values to test | Why |
|----------|----------------|-----|
| Lower boundary | 1 (valid), 0 (invalid) | Tests the inclusive lower bound and its immediate neighbor |
| Upper boundary | 10 (valid), 11 (invalid) | Tests the inclusive upper bound and its immediate neighbor |

This gives 4 boundary values: **0, 1, 10, 11**.

Many teams also add an interior representative (e.g., 5) for the valid class. Total test set: **5 values** for a single numeric range — far fewer than exhaustive testing, but targeting exactly where defects cluster.

---

### Three-Value BVA (ISTQB Recommendation)

**Three-value BVA** adds one more value at each boundary: **below, on, above**. For a lower boundary at 1:
- **Below:** 0 (just outside the valid partition — invalid)
- **On:** 1 (exactly at the boundary — valid)
- **Above:** 2 (just inside the valid partition — valid)

For an upper boundary at 10:
- **Below:** 9 (just inside the valid partition — valid)
- **On:** 10 (exactly at the boundary — valid)
- **Above:** 11 (just outside the valid partition — invalid)

Three-value BVA values for [1, 10]: **0, 1, 2, 9, 10, 11** (6 values).

**Why three-value BVA is stronger:**
Consider a defect where the comparison is `quantity > 0 and quantity < 11` (exclusive bounds, correct behavior) vs the intended `quantity >= 1 and quantity <= 10`. For input `quantity = 0`:
- Two-value BVA tests 0 → catches the lower boundary.
- Three-value BVA also tests the value just inside (`1` and `2`) ensuring the boundary is valid on both sides.

Three-value BVA provides more confidence that both the boundary value AND its neighbors are handled correctly.

**Which to use?** Use **two-value** when time is constrained and the system is well-understood. Use **three-value** for high-risk inputs, financial calculations, and regulated functions.

---

### Non-Numeric Boundary Analysis

BVA is not limited to numbers. Any **ordered domain** has boundaries:

**String length:**
Requirement: "Username must be between 3 and 20 characters."

| Boundary | BVA Values |
|---------|-----------|
| Lower (3 chars) | 2 chars (invalid), 3 chars (valid), 4 chars (valid) |
| Upper (20 chars) | 19 chars (valid), 20 chars (valid), 21 chars (invalid) |
| Special | 0 chars (empty — invalid), 1 char (invalid) |

**Date ranges:**
Requirement: "Promotional discount applies only during December 1–31."

| Boundary | BVA Values |
|---------|-----------|
| Start | November 30 (invalid), December 1 (valid), December 2 (valid) |
| End | December 30 (valid), December 31 (valid), January 1 next year (invalid) |

Watch for: **timezone boundaries** (December 31 at 23:59 UTC is January 1 in some timezones), **leap years** (February 29), **DST transitions** (clocks jump forward/backward — a "2:30 AM" may not exist).

**Collection sizes:**
Requirement: "Shopping cart accepts 1 to 50 items."

| Boundary | BVA Values |
|---------|-----------|
| Lower | 0 items (empty cart — invalid for checkout), 1 item (minimum), 2 items |
| Upper | 49 items, 50 items (maximum), 51 items (over limit) |

---

### Multiple Variables: Single-Fault Assumption

When multiple input variables each have their own boundaries, testing all combinations simultaneously is exponentially expensive. The **single-fault assumption** — vary one variable at a time while holding others at nominal (safe middle) values — keeps the test count manageable.

For a form with **quantity** (1–10) and **discount_percentage** (0–50):

Hold discount_percentage at a nominal value (e.g., 10%) and vary quantity boundaries: 0, 1, 10, 11.
Then hold quantity at a nominal value (e.g., 5) and vary discount_percentage boundaries: 0%, 1%, 50%, 51%.

Total: 8 boundary tests instead of 24 (4 quantity boundaries × 6 discount boundaries with all combinations).

**Add multi-variable corner cases for high-risk interactions:**
- Minimum quantity (1) + maximum discount (50%): Do monetary calculations round correctly?
- Maximum quantity (10) + maximum discount (50%): Does the final price overflow?

---

## Worked Example: Loan Amount Validation

**Requirement:** Loan applications accept amounts between £1,000 and £50,000 (inclusive), in whole pounds only (no pence). Amounts outside this range are rejected with a specific error message. Non-numeric input is rejected separately.

**Equivalence partitions:**
- Valid: £1,000–£50,000 (integer)
- Invalid (too low): < £1,000 (including £0, negative values)
- Invalid (too high): > £50,000
- Invalid (not a whole number): £1,500.50
- Invalid (non-numeric): "five hundred", empty, null

**Three-value BVA for the numeric range:**

| Value | Expected result |
|-------|---------------|
| £999 | REJECT — "Minimum loan amount is £1,000." |
| £1,000 | ACCEPT — proceed to application. |
| £1,001 | ACCEPT — proceed to application. |
| £25,000 | ACCEPT (nominal interior value) — proceed. |
| £49,999 | ACCEPT — proceed to application. |
| £50,000 | ACCEPT — proceed to application. |
| £50,001 | REJECT — "Maximum loan amount is £50,000." |

**Additional tests from other partitions:**
- £0 → REJECT (lower invalid, not just below boundary)
- -£100 → REJECT (negative — invalid class)
- £1,500.50 → REJECT ("Whole pounds only")
- "five hundred" → REJECT ("Please enter a numeric amount")
- Empty → REJECT ("Amount is required")

**Total test cases: 11** — covering the full input domain with maximum defect-detection probability, including the highest-risk values (boundaries) and representatives from every invalid class.

---

### BVA Applied to APIs

BVA is equally valuable for API testing, not only UI forms.

**Example: POST /api/orders with `quantity` field (1–100)**

```python
# Test at lower boundary
response = post("/api/orders", {"quantity": 0})
assert response.status_code == 400
assert response.json()["error"] == "quantity must be between 1 and 100"

response = post("/api/orders", {"quantity": 1})
assert response.status_code == 201

# Test at upper boundary
response = post("/api/orders", {"quantity": 100})
assert response.status_code == 201

response = post("/api/orders", {"quantity": 101})
assert response.status_code == 400
```

Combined with `pytest.mark.parametrize`:

```python
@pytest.mark.parametrize("qty, expected_status, description", [
    (0, 400, "below lower boundary"),
    (1, 201, "at lower boundary"),
    (2, 201, "just above lower boundary"),
    (50, 201, "nominal interior"),
    (99, 201, "just below upper boundary"),
    (100, 201, "at upper boundary"),
    (101, 400, "above upper boundary"),
])
def test_order_quantity_boundaries(qty, expected_status, description):
    response = post("/api/orders", {"quantity": qty})
    assert response.status_code == expected_status, f"Failed for: {description}"
```

---

## Summary

- BVA targets **boundaries between equivalence partitions** — the exact values where defects like off-by-one errors, inclusive/exclusive mistakes, and type limit bugs hide.
- **Two-value BVA:** Select the boundary value and its immediate neighbor outside the valid partition.
- **Three-value BVA:** Select below, at, and above each boundary — stronger coverage for high-risk inputs.
- Apply BVA to **any ordered domain**: numbers, string lengths, collection sizes, dates, percentages.
- Use the **single-fault assumption** for multiple variables: vary one at a time at boundaries while holding others nominal.
- BVA complements **equivalence partitioning** — EP selects representatives for the interior of each class; BVA targets the edges between classes.

---

## Additional Resources

- [ISTQB Foundation Syllabus — Boundary Value Analysis](https://www.istqb.org/) — Official technique definition with two-value and three-value versions.
- [ISTQB Glossary — Boundary Value](https://glossary.istqb.org/) — Canonical vocabulary.
- `equivalence-partitioning.md` (Friday) — The complementary technique for class representative selection.
- Demo: `demos/4-thursday/demo_bva/` — Instructor walkthrough applying BVA to a financial calculation feature.
