# White-Box Testing

## Learning Objectives

By the end of this reading you will be able to:

- Define **white-box (structural) testing** and contrast it with **black-box (specification-based) testing**.
- Explain the coverage hierarchy: statement → branch → condition → path coverage.
- Apply white-box thinking to identify **hidden branches** and **untested code paths** in a real code example.
- Describe when white-box tests **complement** — but do not replace — requirements-based tests.

---

## Why This Matters

As a tester collaborating with developers in an Agile team, you will frequently work with unit tests, integration tests, and CI coverage reports. Understanding **internal code structure** allows you to:

- Design test cases that exercise defensive error handling, rare flag combinations, and integration seams that specification-based testing would never think to test.
- Review developer-written tests and identify meaningful coverage gaps.
- Have informed conversations about coverage targets and what they actually mean.
- Understand why some bugs only appear in specific runtime paths.

White-box thinking is not only for developers. Testers who can read code and reason about its paths are more valuable, more effective, and harder to fool by superficial coverage numbers.

---

## The Concept

### Black-Box vs White-Box vs Gray-Box Testing

**Black-box testing** (also called specification-based or behavioral testing) derives tests entirely from **requirements and specifications** — without looking at the internal code. The test designer treats the software as a "black box": inputs go in, outputs come out, and the internal mechanism is unknown.

- Focus: *Does the system behave as specified?*
- Sources: User stories, acceptance criteria, API contracts, UX specifications.
- Examples: Most system tests, UAT, functional test cases from acceptance criteria.
- Strengths: Tests reflect what users care about; not tied to implementation details; can be written before code exists.
- Weaknesses: May miss internal paths that the specification never described (defensive code, error handling, edge logic).

**White-box testing** (also called structural or glass-box testing) uses **knowledge of the internal code structure** — branches, conditions, data flows, loops — to design tests that exercise specific internal paths.

- Focus: *Have I executed the code I care about?*
- Sources: Source code, call graphs, control flow diagrams.
- Examples: Unit tests that explicitly target error handling branches; tests designed to exercise the `else` path of a complex conditional.
- Strengths: Finds defects in paths the specification never described; enables meaningful coverage measurement.
- Weaknesses: Can overfit to implementation — tests that break when code is refactored without behavior change; does not verify the *right* behavior, only that *some* behavior occurs.

**Gray-box testing** sits between the two: the tester has partial knowledge of internal design (architecture, database schema, API internals) but not full source-level detail. Many integration and API tests are gray-box in practice — the tester knows the endpoint returns a JSON schema and what the underlying service does, but doesn't have the source open.

---

### Why White-Box Testing Reveals Defects That Black-Box Misses

Consider a password validation function. The black-box specification says: "Reject passwords shorter than 8 characters." A specification-based tester writes:

- TC-1: 7-character password → expect rejection. ✅ Tests the boundary.
- TC-2: 8-character password → expect acceptance. ✅ Tests the boundary.

But the developer's implementation looks like this:

```python
def validate_password(password):
    if password is None:
        return False, "Password cannot be empty."
    if len(password) < 8:
        return False, "Password must be at least 8 characters."
    if len(password) > 128:
        return False, "Password must not exceed 128 characters."
    if not any(c.isupper() for c in password):
        return False, "Password must contain at least one uppercase letter."
    return True, "Password is valid."
```

The specification said nothing about:
- What happens when `password` is `None` (the first branch).
- A maximum length of 128 characters (the third branch).
- The uppercase letter requirement (the fourth branch — perhaps an undocumented or recent addition).

A **white-box** tester reading this code immediately generates additional test cases:
- TC-3: `None` value → expect the "cannot be empty" message. Tests the None branch.
- TC-4: 129-character password → expect rejection. Tests the length upper bound.
- TC-5: 8-character lowercase password → expect rejection with uppercase message. Tests the uppercase branch.
- TC-6: 8-character uppercase password → expect acceptance. Tests all branches passing.

Without looking at the code, these cases would likely never have been written. With white-box awareness, they are obvious.

---

### Code Coverage: Measuring Structural Testing

White-box testing produces measurable **coverage metrics** — percentages that tell you how much of the code's structure was exercised.

The main coverage criteria in order of increasing strength:

| Coverage Type | What Must Be Satisfied | Typical Tool Metric |
|--------------|----------------------|-------------------|
| **Statement** | Every executable line executed at least once. | "Line coverage" in most tools. |
| **Branch (Decision)** | Every branch of every decision (true AND false for each `if`, `else`, `try`, `except`) executed at least once. | "Branch coverage" in most tools. |
| **Condition** | Every atomic boolean condition (`A`, `B` in `A && B`) evaluated both true and false at least once. | Not always reported directly. |
| **Path** | Every possible distinct path through a function executed. | Often impractical — exponential combinations. |

Each level is **strictly stronger** than the previous: 100% branch coverage implies 100% statement coverage (you cannot take both branches without hitting all statements). 100% condition coverage does not necessarily imply 100% branch coverage (a compound condition can have both individual conditions true/false while the overall decision always evaluates the same way).

---

### When White-Box Testing Is Most Valuable

**High-risk, high-complexity business logic:**
Payment calculations, discount engines, eligibility checks, and tax rules contain many branches. White-box review ensures the `if is_premium_member and cart_total > 100` branch is tested with both `is_premium_member = True` and `False` separately.

**Error handling paths:**
Error handling is almost never exercised by happy-path specification tests. The defensive code that handles a database timeout, a payment gateway returning a 503, or an upstream service returning null is written by developers but rarely tested. White-box review makes these paths visible.

**Integration seams:**
When two components integrate, the code at the seam often has defensive checks: "if the upstream response is missing field X, log a warning and use a default." These are invisible in black-box tests unless the integration test specifically injects that scenario.

**Security-sensitive code:**
Authentication, authorization, and input validation code should be white-box reviewed. A tester reading the `check_permissions` function may spot a logic error (e.g., `if not is_admin or is_owner` when it should be `if not (is_admin or is_owner)`) that no functional test would catch.

---

### The Limits of White-Box Testing

**100% coverage ≠ no bugs:**

A test can execute every line of code and still not catch a defect if the assertion checks the wrong expected value, or if the business logic is wrong (the code implements the wrong formula, but consistently):

```python
def apply_discount(price, discount_rate):
    # BUG: developer used addition instead of subtraction
    return price + (price * discount_rate)  # should be price - (price * discount_rate)
```

A test `assert apply_discount(100, 0.1) == 110.0` achieves 100% coverage of this function — and passes, because the assertion is also wrong. The defect is invisible to coverage tools.

**Overfitting to implementation:**

Tests written specifically to exercise a code path (rather than to verify a behavior) may break when the code is refactored — even if the behavior is unchanged. This creates **test brittleness**: the tests become a maintenance burden without adding value.

**Balance is key:**

Use white-box testing to supplement specification-based tests, not to replace them. The combination — "does it do what the spec says?" (black-box) and "have we exercised the paths that could hide defects?" (white-box) — provides defense in depth.

---

## Worked Example: Shipping Eligibility Analyzer

```python
def check_shipping_eligibility(country, weight_kg, is_fragile):
    """Returns (eligible: bool, reason: str)"""
    if country not in ["US", "CA", "UK", "AU"]:
        return False, "Country not supported."
    
    if weight_kg > 30:
        return False, "Package exceeds maximum weight of 30 kg."
    
    if is_fragile and weight_kg > 10:
        return False, "Fragile items limited to 10 kg."
    
    return True, "Eligible for standard shipping."
```

**Black-box test cases (from spec):**
- Valid US order, 5kg, not fragile → eligible.
- Invalid country → not eligible.

**White-box analysis reveals additional paths:**
- Country is valid but weight > 30 → "exceeds weight" branch.
- Country is valid, weight <= 30, `is_fragile=True` and `weight_kg > 10` → "fragile limit" branch.
- Country is valid, `is_fragile=True`, `weight_kg <= 10` → eligible (fragile but light enough).

**Full white-box test set:**

| Test | country | weight_kg | is_fragile | Expected |
|------|---------|-----------|------------|---------|
| T1 | "US" | 5 | False | True, "Eligible" |
| T2 | "DE" | 5 | False | False, "Country not supported" |
| T3 | "US" | 35 | False | False, "Exceeds weight" |
| T4 | "US" | 15 | True | False, "Fragile limit" |
| T5 | "UK" | 8 | True | True, "Eligible" |

T1 achieves ~60% branch coverage. All five tests together achieve **100% branch coverage** — every `if` branch taken both true and false at least once. T4 and T5 were only visible through white-box analysis of the fragile + weight compound condition.

---

## Summary

- **Black-box testing** derives tests from specifications; **white-box testing** derives tests from internal code structure. Both are necessary — they find different types of defects.
- White-box analysis reveals **hidden branches** (error handling, defensive code, undocumented validations) that specification-based testing would miss.
- **Coverage metrics** (statement, branch, condition, path) measure structural test completeness — but 100% coverage does not equal correctness.
- White-box testing is most valuable for **complex logic, error handling, integration seams, and security-sensitive code**.
- Avoid **overfitting** to implementation: white-box tests that only exercise specific lines without verifying behavior become brittle maintenance burdens.

---

## Additional Resources

- [ISTQB Foundation Syllabus — White-Box Test Techniques](https://www.istqb.org/) — Official treatment of structural testing.
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/) — Java/JVM branch and line coverage tool.
- [Coverage.py](https://coverage.readthedocs.io/) — Python's coverage measurement tool.
- [Martin Fowler — Unit Testing](https://martinfowler.com/bliki/UnitTest.html) — Thoughtful perspective on unit testing and what it should achieve.
- `conditional-testing.md` — Deep dive into branch and condition coverage with worked examples.
