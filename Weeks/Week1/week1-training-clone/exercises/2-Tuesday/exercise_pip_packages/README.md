# Exercise: Package Management — PyPI, pip, and Requirements

**Mode:** Implementation (Code Lab)
**Duration:** 45–60 minutes
**Day:** Tuesday | **Week:** 1 — Git & Python Fundamentals

---

## Objective

By the end of this exercise, you will be able to:
- Search for and install packages from PyPI using pip.
- Use an installed package in a script.
- Generate and use a `requirements.txt` file.
- Understand package versioning and pinning.

---

## Prerequisites

| Concept | Source |
|---------|--------|
| PyPI & pip | `written/2-Tuesday/pypi-repository-pip.md` |
| Pandas & NumPy | `written/2-Tuesday/pandas-numpy.md` |
| Virtual Environments | `written/1-Monday/virtual-environments.md` |
| Instructor Demo | `demos/2-Tuesday/INSTRUCTOR_GUIDE.md` (Demo 3) |

---

## The Scenario

Your team needs a data analysis script that processes test execution data from a CSV file. You'll install the necessary packages, build the script, and create a reproducible environment using `requirements.txt`.

---

## Setup

1. Create a new project directory: `data-analysis-lab`.
2. Create and activate a virtual environment.
3. Verify pip is available: `pip --version`.

---

## Core Tasks

### Task 1: Install Packages (10 min)

1. Install the following packages:
   ```bash
   pip install pandas numpy requests
   ```
2. Run `pip list` to see all installed packages (including dependencies).
3. Run `pip show pandas` to see package details.
4. **Question:** How many dependencies did pandas install? Write the answer in a comment.

---

### Task 2: Create Test Data (10 min)

Create a file called `starter_code/test_data.csv` with the following content:

```csv
test_name,module,duration_ms,status,timestamp
test_login_valid,auth,1200,pass,2026-04-01 09:00:00
test_login_invalid,auth,850,pass,2026-04-01 09:01:00
test_login_locked,auth,920,fail,2026-04-01 09:02:00
test_register_new,auth,2100,pass,2026-04-01 09:05:00
test_search_basic,search,450,pass,2026-04-01 09:10:00
test_search_filters,search,1800,fail,2026-04-01 09:12:00
test_search_sort,search,670,pass,2026-04-01 09:14:00
test_checkout_cart,checkout,2300,fail,2026-04-01 09:20:00
test_checkout_payment,checkout,3100,pass,2026-04-01 09:25:00
test_checkout_confirm,checkout,1900,pass,2026-04-01 09:28:00
test_profile_view,profile,380,pass,2026-04-01 09:30:00
test_profile_edit,profile,540,pass,2026-04-01 09:32:00
```

---

### Task 3: Analyze with Pandas (25 min)

Create `starter_code/analyze_results.py` and implement:

1. **Load the CSV:**
   ```python
   import pandas as pd
   df = pd.read_csv("test_data.csv")
   ```

2. **Print basic info:**
   - Total number of tests
   - Column names and dtypes
   - First 5 rows

3. **Calculate aggregate metrics:**
   - Overall pass rate
   - Average duration (in ms and seconds)
   - Slowest test (name and duration)
   - Fastest test (name and duration)

4. **Group by module:**
   - Pass rate per module
   - Average duration per module
   - Number of tests per module

5. **Filter and display:**
   - All failed tests (name, module, duration)
   - Tests slower than 1500ms
   - Tests in the "auth" module

6. **Add a computed column:**
   - `duration_sec` = duration_ms / 1000

7. **Sort and export:**
   - Sort by duration (descending)
   - Save the result to `output/results_sorted.csv`

**Expected console output (partial):**
```
══════════════════════════════════════
  Test Results Analysis
══════════════════════════════════════

  Total Tests:    12
  Pass Rate:      75.0%
  Avg Duration:   1,351ms (1.35s)
  Slowest:        test_checkout_payment (3,100ms)
  Fastest:        test_profile_view (380ms)

  ── By Module ──
  Module        Tests  Pass Rate  Avg Duration
  auth             4     75.0%      1,268ms
  checkout         3     66.7%      2,433ms
  profile          2    100.0%        460ms
  search           3     66.7%        973ms

  ── Failed Tests ──
  test_login_locked     auth       920ms
  test_search_filters   search   1,800ms
  test_checkout_cart    checkout  2,300ms
```

---

### Task 4: Freeze Requirements (5 min)

1. Generate a requirements file:
   ```bash
   pip freeze > requirements.txt
   ```
2. Open `requirements.txt` and observe the pinned versions.
3. **Question:** Why do we pin exact versions? Write your answer as a comment in `requirements.txt`.

**Bonus:** Try deleting and recreating the venv, then installing from the requirements file:
```bash
deactivate
# (delete venv folder)
python -m venv venv
# (activate)
pip install -r requirements.txt
python starter_code/analyze_results.py
```

---

## Stretch Goals

- [ ] Use `numpy` to calculate the standard deviation of test durations.
- [ ] Create a simple bar chart of pass/fail counts per module (hint: `df.plot()`).
- [ ] Add a `--module` command-line argument to filter results by module.

---

## Definition of Done

- [ ] pandas, numpy, and requests installed in a virtual environment.
- [ ] `test_data.csv` created with 12 test results.
- [ ] `analyze_results.py` loads, analyzes, filters, and exports data.
- [ ] `requirements.txt` generated with pinned versions.
- [ ] Script runs without errors in a fresh venv using only the requirements file.
