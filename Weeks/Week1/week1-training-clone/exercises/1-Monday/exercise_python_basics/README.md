# Exercise: Python Basics — Virtual Environment, Input, Calculations, Output

**Mode:** Implementation (Code Lab)
**Duration:** 60–90 minutes
**Day:** Monday | **Week:** 1 — Git & Python Fundamentals

---

## Objective

By the end of this exercise, you will be able to:
- Create and activate a Python virtual environment.
- Write a script that accepts user input.
- Perform calculations with different data types.
- Format and display output with f-strings.

---

## Prerequisites

| Concept | Source |
|---------|--------|
| Installing Python, REPL | `written/1-Monday/installing-python-repl.md` |
| Virtual Environments | `written/1-Monday/virtual-environments.md` |
| Data Types & Operators | `written/1-Monday/primitive-data-types-operators.md` |
| Identifiers & Keywords | `written/1-Monday/identifiers-and-keywords.md` |
| Input & Output | `written/1-Monday/user-input-and-output.md` |
| Instructor Demo | `demos/1-Monday/INSTRUCTOR_GUIDE.md` (Demos 2 & 3) |

---

## The Scenario

You are building a QA Metrics Calculator — a small command-line tool that helps your team calculate test execution statistics from user input.

---

## Setup

1. Create a project directory: `qa-metrics-calculator`.
2. Initialize a Git repository inside it (practice from the previous exercise!).
3. Create a virtual environment: `python -m venv venv`.
4. Activate the virtual environment.
5. Verify: `python --version` and check for `(venv)` in your prompt.

**✅ Checkpoint:** Virtual environment is active and Python is running.

---

## Core Tasks

### Task 1: Hello, QA! (10 min)

Create `starter_code/hello_qa.py` and implement:

```python
# 1. Ask the user for their name
# 2. Ask for their role (e.g., "QA Engineer")
# 3. Print a greeting using an f-string:
#    "Welcome, {name}! Your role is {role}."
# 4. Print the current Python version (hint: import sys)
```

**Expected output:**
```
What is your name? Alice
What is your role? QA Engineer
Welcome, Alice! Your role is QA Engineer.
Python version: 3.12.0
```

---

### Task 2: Test Metrics Calculator (25 min)

Create `starter_code/metrics_calculator.py` and implement:

1. Ask the user for:
   - Total number of test cases (integer)
   - Number of passed tests (integer)
   - Total execution time in seconds (float)

2. Calculate and display:
   - Number of failed tests
   - Pass rate (as a percentage with 1 decimal place)
   - Fail rate (as a percentage with 1 decimal place)
   - Average time per test (with 2 decimal places)

3. Display a final verdict:
   - If pass rate >= 95%: "✅ RELEASE APPROVED"
   - If pass rate >= 80%: "⚠️ CONDITIONAL RELEASE — review failures"
   - Otherwise: "❌ RELEASE BLOCKED — too many failures"

**Expected output:**
```
═══════════════════════════════════════
  QA Test Metrics Calculator
═══════════════════════════════════════

Enter total test cases: 200
Enter passed tests: 187
Enter total execution time (seconds): 345.5

───────────────────────────────────────
  Test Results Summary
───────────────────────────────────────
  Total Tests:     200
  Passed:          187
  Failed:           13
  Pass Rate:       93.5%
  Fail Rate:        6.5%
  Avg Time/Test:   1.73s
  Total Time:      345.50s

  Verdict: ⚠️ CONDITIONAL RELEASE — review failures
═══════════════════════════════════════
```

---

### Task 3: Data Type Explorer (15 min)

Create `starter_code/type_explorer.py` and implement:

1. Declare variables of each primitive type: `int`, `float`, `str`, `bool`, `NoneType`.
2. Print each variable, its value, and its type using f-strings.
3. Demonstrate:
   - Integer division (`//`) vs. regular division (`/`)
   - String multiplication (`"abc" * 3`)
   - Boolean arithmetic (`True + True + False`)
   - The `0.1 + 0.2` floating-point precision issue
4. Show the difference between `==` and `is` using two examples.

**Expected output (partial):**
```
Variable Exploration:
  age         = 28         (type: int)
  price       = 19.99      (type: float)
  name        = Alice      (type: str)
  is_active   = True       (type: bool)
  result      = None       (type: NoneType)

Operators Demo:
  17 // 5     = 3          (floor division)
  17 / 5      = 3.4        (true division)
  "QA " * 3  = QA QA QA
  True + True = 2

Precision Gotcha:
  0.1 + 0.2  = 0.30000000000000004 (not exactly 0.3!)
```

---

### Task 4: Formatted Report Generator (15 min)

Create `starter_code/report_generator.py` and implement:

1. Define 5 test results as variables (test name, duration in ms, status).
2. Print a formatted table using f-strings with alignment:

**Expected output:**
```
┌──────────────────┬────────────┬──────────┐
│ Test Name        │ Duration   │ Status   │
├──────────────────┼────────────┼──────────┤
│ test_login       │   1,200 ms │ ✅ PASS  │
│ test_search      │     850 ms │ ✅ PASS  │
│ test_checkout    │   2,300 ms │ ❌ FAIL  │
│ test_profile     │     450 ms │ ✅ PASS  │
│ test_logout      │     180 ms │ ✅ PASS  │
├──────────────────┼────────────┼──────────┤
│ TOTAL            │   4,980 ms │ 4/5 Pass │
└──────────────────┴────────────┴──────────┘
```

**Hints:**
- Use `f"{value:>10,}"` for right-aligned numbers with commas.
- Use `f"{name:<16}"` for left-aligned strings.

---

## Stretch Goals (Optional)

- [ ] Add input validation — handle non-numeric input gracefully using `try/except` (preview of Thursday's material).
- [ ] Commit each task as a separate Git commit with a meaningful message.
- [ ] Add a `.gitignore` file that excludes the `venv/` directory.

---

## Definition of Done

- [ ] Virtual environment created and activated.
- [ ] `hello_qa.py` runs and greets the user with their name and role.
- [ ] `metrics_calculator.py` calculates pass/fail rates and displays a verdict.
- [ ] `type_explorer.py` demonstrates all 5 primitive types and operator behaviors.
- [ ] `report_generator.py` outputs a formatted table with alignment.
- [ ] All scripts run without errors.
