# Conditional Testing (Branch and Condition Coverage)

## Learning Objectives

By the end of this reading you will be able to:

- Define **decision (branch) coverage** and explain why it is stronger than statement coverage.
- Define **condition coverage** and explain what it adds over branch coverage.
- Construct a **minimal test set** that achieves branch coverage for a given function.
- Analyze **compound predicates** (`&&`, `||`) and account for **short-circuit evaluation**.
- Explain where **MC/DC (Modified Condition/Decision Coverage)** fits and why it is used in safety-critical systems.

---

## Why This Matters

Business rules hide inside conditional logic — `if`, `while`, `switch`, ternary operators, and compound predicates like `A && B || C`. Statement coverage (Tuesday's reading) can miss entire branches: if a test only makes the `if` condition true, the `else` branch is never executed — but it still counts as "covered" for statement purposes (since the `if` statement line itself is executed).

Conditional testing — specifically branch and condition coverage — targets this **logical risk** directly. It is one of the most important structural testing techniques for testers working with complex business logic, and it is a frequently tested topic in ISTQB Foundation and technical interviews.

---

## The Concept

### Why Statement Coverage Misses Branches

Consider this function:

```python
def calculate_fee(amount, is_member):
    fee = amount * 0.05  # 5% base fee
    if is_member:
        fee = fee * 0.5  # 50% discount for members
    return round(fee, 2)
```

**Executable statements:** 3 (assignment, if-body, return) plus the conditional check.

A single test: `calculate_fee(100, True)` executes:
- `fee = amount * 0.05` ✅
- `if is_member` ✅ (True path)
- `fee = fee * 0.5` ✅ (True branch body)
- `return round(fee, 2)` ✅

Statement coverage = **100%** — all lines are hit.

But what about when `is_member = False`? The False branch (no fee reduction) is never tested. If there were a bug that incorrectly applied the discount to non-members, the 100% statement coverage test would miss it entirely.

**Branch coverage** catches this gap by requiring that both the True and False outcomes of `if is_member` are tested.

---

### Decision (Branch) Coverage

A **decision** is any Boolean-valued expression that controls program flow: `if`, `elif`, `else`, `while`, `for`, `switch/case`, ternary operators, and exception handlers (`try`/`except`).

**Branch coverage** requires that each decision evaluates to **True** and **False** at least once across the test suite.

**Formula:**
```
Branch Coverage (%) = (Decision Outcomes Exercised / Total Possible Decision Outcomes) × 100
```

For a function with three `if` statements, there are 6 possible decision outcomes (2 per decision — True and False). A test set that exercises all 6 achieves 100% branch coverage.

**Key property:** 100% branch coverage **implies** 100% statement coverage. You cannot exercise both the True and False outcome of every decision without visiting every statement — because both branches contain statements.

---

### Building a Branch Coverage Test Set: Step by Step

**The function:**

```python
def classify_passenger(age, has_ticket, is_vip):
    """Returns the boarding priority class."""
    if age < 5 or age >= 70:
        return "Priority Boarding"
    
    if is_vip and has_ticket:
        return "Business Class"
    
    if has_ticket:
        return "Economy"
    
    return "Standby"
```

**Step 1: Identify all decisions:**
1. `age < 5 or age >= 70`
2. `is_vip and has_ticket`
3. `has_ticket`

**Step 2: List required branch outcomes:**

| Decision | True outcome | False outcome |
|---------|-------------|--------------|
| `age < 5 or age >= 70` | Return "Priority Boarding" | Continue to next decision |
| `is_vip and has_ticket` | Return "Business Class" | Continue to next decision |
| `has_ticket` | Return "Economy" | Return "Standby" |

**Step 3: Design test cases to cover all outcomes:**

| Test | age | has_ticket | is_vip | Expected | Decisions covered |
|------|-----|-----------|--------|---------|------------------|
| T1 | 3 | True | False | "Priority Boarding" | Decision 1 True |
| T2 | 35 | True | True | "Business Class" | Decision 1 False, Decision 2 True |
| T3 | 35 | True | False | "Economy" | Decision 1 False, Decision 2 False, Decision 3 True |
| T4 | 35 | False | False | "Standby" | Decision 1 False, Decision 2 False (short-circuit: `is_vip=False` and `has_ticket=False`), Decision 3 False |

With T1–T4, all 6 decision outcomes are exercised. Branch coverage = 100%.

Note: We need at least 4 tests because the function has 4 distinct paths. Three tests would be insufficient to cover all branches.

---

### Condition Coverage

**Condition coverage** goes deeper: for **compound predicates** (decisions made up of multiple boolean sub-expressions), it requires that each **individual condition** (each atomic boolean expression) evaluates to **True** and **False** at least once.

**Example compound predicate:**
```python
if is_vip and has_ticket:
```

This decision has two conditions:
- `is_vip` (can be True or False)
- `has_ticket` (can be True or False)

**Branch coverage** only requires:
- The overall expression to be True at least once (both True).
- The overall expression to be False at least once (at least one False).

**Condition coverage** requires additionally:
- `is_vip` is True in at least one test.
- `is_vip` is False in at least one test.
- `has_ticket` is True in at least one test.
- `has_ticket` is False in at least one test.

Note: condition coverage does **not** require that each condition independently affects the outcome. It only requires each condition is exercised in both states across the test suite.

---

### Short-Circuit Evaluation: A Critical Nuance

Most languages (Python, Java, JavaScript, C#, etc.) use **short-circuit evaluation** for logical `and` and `or`:

- For `A and B`: if `A` is **False**, `B` is **not evaluated** at all. The expression short-circuits to False immediately.
- For `A or B`: if `A` is **True**, `B` is **not evaluated**. The expression short-circuits to True immediately.

This affects condition coverage:

```python
if has_ticket and is_vip:
```

If `has_ticket = False`, `is_vip` is never evaluated — even though the overall decision evaluates to False. Your coverage tool may report `is_vip` as uncovered if none of your tests evaluate it.

To ensure `is_vip` is evaluated, at least one test must have `has_ticket = True`. This forces the evaluation to proceed to the second condition.

**Practical implication:**
When designing tests for compound conditions, explicitly plan which tests will make each condition the "deciding factor" by ensuring the earlier conditions allow execution to reach the later ones.

---

### The Decision Condition Coverage Combination (DC/CC)

In practice, many teams and standards use **Decision/Condition Coverage (DC/CC)**, which requires:
- Every decision outcome exercised (branch coverage), AND
- Every individual condition exercised in both True and False states.

This is a useful middle ground — stronger than branch coverage alone, more practical than path coverage.

---

### Modified Condition/Decision Coverage (MC/DC)

**MC/DC** is the most stringent practical coverage criterion. It requires that each condition is shown to **independently affect** the overall decision outcome — meaning: for each condition, there must be a test pair where:
- Only that condition changes.
- The overall decision outcome changes as a result.
- All other conditions are held constant.

MC/DC is **required by aviation standards (DO-178C Level A)** and referenced in other safety-critical domains. It is the strongest practically achievable coverage criterion.

**Why MC/DC for aviation?**
A flight control system has compound boolean conditions controlling actuators. If a condition has no demonstrable independent effect on the control signal, it might be dead code — a safety defect hiding behind complex logic. MC/DC proves each condition matters.

For this course, you need to understand that MC/DC exists and why it is used. Full MC/DC test design requires careful analysis beyond this week's scope — it is covered in advanced ISTQB syllabi and domain-specific standards.

---

### Putting It Together: Relationship Between Coverage Criteria

```
Statement Coverage ⊂ Branch Coverage ⊂ DC/CC ⊂ MC/DC ⊂ Path Coverage
(each criterion is strictly stronger than the one before it)
```

| Criterion | Requires | Practical use |
|-----------|---------|--------------|
| Statement | Every line executed | Basic floor for all projects |
| Branch | Every decision True and False | Standard for business-critical logic |
| Condition | Every atomic condition True and False | Compound predicates in rules engines |
| DC/CC | Branch + Condition | Financial, telecom, higher-assurance products |
| MC/DC | Each condition independently affects decision | Safety-critical (aviation, medical devices) |
| Path | Every path through function | Usually infeasible; useful for small, critical functions |

For most Agile product teams, **branch coverage** as a target (combined with specification-based tests) provides a practical and effective foundation.

---

## Worked Example: Loan Eligibility Decision

```python
def is_eligible_for_loan(credit_score, income_annual, is_employed):
    """Returns True if applicant meets minimum loan criteria."""
    if credit_score < 600:
        return False
    
    if not is_employed:
        return False
    
    if income_annual < 30000:
        return False
    
    return True
```

**Decision analysis:**

| # | Decision | True path | False path |
|---|---------|---------|----------|
| D1 | `credit_score < 600` | Return False | Continue |
| D2 | `not is_employed` | Return False | Continue |
| D3 | `income_annual < 30000` | Return False | Return True |

**Minimum branch coverage test set (4 tests):**

| Test | credit_score | income_annual | is_employed | Expected | Branches covered |
|------|-------------|--------------|-------------|---------|-----------------|
| T1 | 550 | 50000 | True | False | D1 True |
| T2 | 700 | 50000 | False | False | D1 False, D2 True |
| T3 | 700 | 20000 | True | False | D1 False, D2 False, D3 True |
| T4 | 700 | 50000 | True | True | D1 False, D2 False, D3 False |

With T1–T4: all 6 decision outcomes are covered. Branch coverage = 100%.

**Condition coverage check (for compound predicates):**
In this example, all decisions are simple (single conditions) — so branch coverage and condition coverage are equivalent.

**Additional specification-based tests to add:**
- T5: credit_score = 600 (boundary — exactly at threshold).
- T6: credit_score = 599 (just below threshold).
- T7: income_annual = 30000 (exactly at threshold).

These boundary tests (BVA — Thursday's reading) are not captured by branch coverage alone — showing why you always combine structural and specification-based techniques.

---

## Summary

- **Branch coverage** requires each decision to evaluate True and False at least once — it is stronger than statement coverage and catches the "untested else" problem.
- **Condition coverage** requires each atomic condition within a compound predicate to evaluate True and False — it goes further for complex predicates.
- **Short-circuit evaluation** affects condition coverage: ensure tests are designed so later conditions in `A and B` or `A or B` are actually reached and evaluated.
- **MC/DC** proves each condition independently affects the decision outcome — required in safety-critical aviation and medical device standards.
- Always combine structural coverage (branch, condition) with **specification-based tests** (BVA, equivalence partitioning) for complete test design.

---

## Additional Resources

- [ISTQB Glossary — Branch Testing / Decision Testing](https://glossary.istqb.org/) — Canonical definitions.
- [DO-178C Overview — MC/DC in Avionics Software](https://www.faa.gov/) — FAA guidance on aviation software testing standards.
- [Google Testing Blog — Test Coverage](https://testing.googleblog.com/) — Practical perspectives on coverage goals and limitations.
- `white-box-testing.md` — Structural testing context and black-box/white-box distinction.
- `conditional-testing` exercises — Wednesday exercise: `exercise_white_box/`.
