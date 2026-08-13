# Statement Testing (Statement Coverage)

## Learning Objectives

By the end of this reading you will be able to:

- Define **statement coverage** and explain what it measures in white-box testing.
- Calculate simple statement coverage percentages from source code and a set of tests.
- Interpret coverage reports from common tools (Coverage.py, JaCoCo).
- Explain the **strengths and limitations** of statement coverage — including why 100% coverage does not mean bug-free code.
- Understand how statement coverage relates to higher-level coverage criteria (branch, path) studied on Wednesday.

---

## Why This Matters

Statement coverage is the **entry-level metric** for structural (white-box) testing. When developers say "we have 85% coverage," they almost always mean statement coverage. Understanding what this metric does and does not tell you is essential for:

- Interpreting CI/CD coverage reports without drawing wrong conclusions.
- Having informed conversations with developers about coverage targets.
- Identifying **untested code paths** — lines of code that have never been executed by any test.
- Knowing when to push for **higher-quality coverage criteria** (branch coverage, path coverage) for critical modules.

---

## The Concept

### What Is Statement Coverage?

**Statement coverage** (also called **line coverage** in many tools) is a white-box testing metric that measures what fraction of **executable statements** in a codebase are executed during a test run.

**The formula:**

```
Statement Coverage (%) = (Statements Executed / Total Executable Statements) × 100
```

"Executable statements" are lines of code that the runtime can actually execute — not blank lines, comments, or type annotations. The exact definition varies slightly by language and tool.

**Example:**
If a module has 100 executable statements and a test suite executes 85 of them, statement coverage is 85%.

### Why Statement Coverage Matters

Before statement coverage (or any structural coverage metric), it was possible for a large test suite to exist while significant portions of the codebase were never executed at all. This is more common than you might think:

- Error handling branches (the `except` clause) that are never triggered by tests.
- Legacy code paths that are still in the codebase but no current test reaches.
- New code added without corresponding tests.
- Dead code that is never reached due to logic conditions.

Statement coverage makes these gaps **visible**. A coverage report with red (uncovered) lines is a direct signal: "No test has ever run this code."

---

### How Coverage Tools Work

Coverage tools work by **instrumenting** code — either at compile time or at runtime — to record which lines are executed during a test run.

**General flow:**

1. **Configure the coverage tool** for your project.
2. **Run your test suite** through the coverage tool.
3. **Generate a coverage report** showing covered (green) and uncovered (red) lines.
4. **Analyze the report** to find gaps and add tests for the most important uncovered paths.

**Common tools by language:**

| Language | Common Coverage Tool | Command |
|----------|---------------------|---------|
| Python | `Coverage.py` | `coverage run -m pytest && coverage report` |
| Java | JaCoCo | Integrated with Maven/Gradle builds |
| JavaScript / TypeScript | Istanbul / nyc / c8 | `jest --coverage` or `nyc mocha` |
| .NET (C#) | Coverlet | `dotnet test --collect:"XPlat Code Coverage"` |
| Go | Built-in | `go test -cover ./...` |
| Ruby | SimpleCov | Added as a dependency to the test suite |

---

### Python Coverage.py: A Worked Walkthrough

Let us trace through a concrete example using Python and Coverage.py.

**The code under test:**

```python
# shipping.py

def calculate_shipping(weight_kg, distance_km, is_express=False):
    """Calculate shipping cost in dollars."""
    if weight_kg <= 0:
        raise ValueError("Weight must be positive.")
    
    base_cost = weight_kg * 0.5
    
    if distance_km > 500:
        base_cost += 15.00
    
    if is_express:
        base_cost *= 1.5
    
    return round(base_cost, 2)
```

**Executable statements in this function:**

1. `raise ValueError(...)` (inside if weight <= 0)
2. `base_cost = weight_kg * 0.5`
3. `base_cost += 15.00` (inside if distance > 500)
4. `base_cost *= 1.5` (inside if is_express)
5. `return round(base_cost, 2)`

(The `if` conditional checks themselves are also counted as statements in most tools.)

**Test 1 (happy path, normal shipping):**
```python
def test_standard_shipping():
    result = calculate_shipping(weight_kg=2, distance_km=100)
    assert result == 1.00  # 2 * 0.5, no distance surcharge, not express
```

**Statements executed by Test 1:**
- `base_cost = weight_kg * 0.5` ✅
- `if distance_km > 500` ✅ (evaluated as False — branch not taken)
- `if is_express` ✅ (evaluated as False — branch not taken)
- `return round(base_cost, 2)` ✅

**Statements NOT executed:**
- `raise ValueError(...)` ❌ (weight was valid)
- `base_cost += 15.00` ❌ (distance was under 500km)
- `base_cost *= 1.5` ❌ (is_express was False)

Statement coverage after Test 1 = **4 / 7 ≈ 57%** (depending on exact counting method).

**Adding more tests:**

```python
def test_long_distance():
    result = calculate_shipping(weight_kg=2, distance_km=600)
    assert result == 16.00  # 1.00 + 15.00

def test_express():
    result = calculate_shipping(weight_kg=2, distance_km=100, is_express=True)
    assert result == 1.50  # 1.00 * 1.5

def test_invalid_weight():
    with pytest.raises(ValueError):
        calculate_shipping(weight_kg=0, distance_km=100)
```

With all four tests, **all executable statements are reached**: coverage = **100%**.

---

### Reading a Coverage Report

Running `coverage report -m` in Python produces output like:

```
Name               Stmts   Miss  Cover   Missing
-----------------------------------------------
shipping.py           7      0   100%
checkout.py          45     12    73%    22-28, 41, 87-93
auth.py              30      5    83%    56, 67, 78-80
-----------------------------------------------
TOTAL                82     17    79%
```

**How to read this:**
- `Stmts`: Total executable statements in the file.
- `Miss`: Number of statements never executed during the test run.
- `Cover`: Percentage executed.
- `Missing`: Line numbers of the uncovered statements (with ranges).

The `checkout.py` row tells you: lines 22–28, 41, and 87–93 have never been executed by any test. These are your **investigation targets** — are they dead code? Error handlers that need a test? Recently added code?

---

### Interpreting Coverage: Strengths and Limitations

**What statement coverage tells you:**
- ✅ Which code was executed at least once during testing.
- ✅ Where obvious gaps in testing exist (red lines = never executed).
- ✅ Whether new code has corresponding tests.
- ✅ Trends over time (is coverage improving or degrading as the codebase grows?).

**What statement coverage does NOT tell you:**
- ❌ Whether the executed code was correct (wrong logic can still execute all lines).
- ❌ Whether all decision branches were tested (a single test can cover a line in an `if` block without testing the `else`).
- ❌ Whether the test inputs were representative of real-world data.
- ❌ Whether the assertions in the tests actually verify the right outcomes.

**The critical insight: 100% statement coverage ≠ bug-free**

```python
def calculate_discount(price, is_member):
    discount = 0
    if is_member:
        discount = price * 0.1  # BUG: should be 0.15 per business rule
    return price - discount
```

A test `calculate_discount(100, True)` achieves 100% statement coverage — every line is executed. But the assertion `assert result == 90.0` also passes if the developer wrote `0.1` instead of `0.15`. The test covers every statement but **validates the wrong business rule** because the expected value in the assertion is also wrong.

This is why coverage is a **necessary but not sufficient** condition for quality.

---

### Coverage Targets: Using Them Responsibly

Many teams set coverage targets — typically 70%, 80%, or 90% minimum for PRs or releases. These targets have value but require care:

**Useful when:**
- Used as a floor, not a ceiling: "We must not decrease coverage" prevents regression.
- Combined with code review: reviewers check that coverage is meaningful, not gamed.
- Applied selectively: critical modules (payment, auth, data processing) warrant higher targets than configuration utilities.

**Problematic when:**
- Teams write meaningless tests to hit the number: tests that execute code but assert nothing.
- Targets create a false ceiling: teams stop at 80% even when untested code is high-risk.
- Targets are applied uniformly: the same 85% target for an 8-line utility function and a 500-line payment processor.

**Better framing:** Use coverage to find gaps, not to score the team. "Lines 87–93 of checkout.py are uncovered — they handle the case where a coupon and a bundle discount are applied simultaneously. Let's add a test for that."

---

### Statement Coverage vs Other Coverage Criteria

Statement coverage is the simplest structural coverage criterion. Stronger criteria provide more confidence:

| Coverage Type | What it requires | Strength |
|--------------|-----------------|---------|
| **Statement** | Every executable line executed at least once. | Basic — identifies code never reached. |
| **Branch** | Every branch of every decision (if/else, try/except) taken at least once. | Stronger — catches both sides of conditionals. |
| **Condition** | Every individual boolean condition evaluated true and false. | Stronger — handles compound conditions. |
| **Path** | Every possible route through a function executed. | Strongest practical — often infeasible for large functions. |
| **MC/DC (Modified Condition/Decision Coverage)** | Each condition independently shown to affect the outcome. | Required for aviation and safety-critical systems. |

You will explore **branch and condition coverage** in `conditional-testing.md` on Wednesday.

---

## Summary

- **Statement coverage** measures what fraction of executable statements were executed during a test run. It reveals code that has never been tested.
- Coverage tools (Coverage.py, JaCoCo, Istanbul) instrument code during test execution and produce reports showing covered and uncovered lines.
- 100% statement coverage does **not** guarantee correctness — wrong logic can execute all lines; assertions can verify wrong expected values.
- Coverage targets are useful as **floors** (don't decrease coverage) and for finding **gaps** — not as absolute quality scores.
- Statement coverage is the first step in structural testing. **Branch and condition coverage** (Wednesday) are stronger criteria for critical logic.

---

## Additional Resources

- [Coverage.py Documentation](https://coverage.readthedocs.io/) — Python's primary coverage measurement tool; comprehensive usage guide.
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/) — Java/JVM code coverage tool used with Maven and Gradle.
- [ISTQB Glossary — Statement Coverage](https://glossary.istqb.org/) — Canonical definition and relationship to other coverage criteria.
- [Martin Fowler on Test Coverage](https://martinfowler.com/bliki/TestCoverage.html) — Thoughtful article on how to use (and not misuse) coverage metrics.
- `conditional-testing.md` (Wednesday) — Branch and condition coverage, the next level of structural testing.
