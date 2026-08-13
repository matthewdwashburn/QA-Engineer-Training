# Pandas — Data Analysis in Python

## Learning Objectives
- Understand what Pandas is and how it relates to NumPy.
- Create and inspect Pandas `Series` and `DataFrame` objects.
- Load data from CSV files and explore datasets using Pandas tools.
- Select, filter, and transform data using column access, boolean indexing, and `.loc`/`.iloc`.
- Apply grouping and aggregation to summarise datasets.
- Handle missing data cleanly and reliably.
- Export processed data back to CSV.

---

## Why This Matters

> **Weekly Epic Connection:** *From Version Control to Python Mastery* — QA engineers regularly work with structured data: exported test results, bug reports, performance logs, and database query outputs. Pandas is the industry-standard library for working with tabular data in Python. It lets you load a CSV file, filter it, compute metrics, and produce a summary report — all in a few lines of code.

In later weeks you will use Pandas to:
- Process and assert on API test result datasets.
- Analyse performance test outputs from tools like JMeter.
- Load test data fixtures from CSV files for data-driven testing.

Learning Pandas now means you will be a far more effective QA engineer when you reach those tools.

---

## The Concept

### What is Pandas?

**Pandas** is an open-source Python library that provides:
1. **Series** — a one-dimensional labelled array (like a column in a spreadsheet).
2. **DataFrame** — a two-dimensional labelled table (like a spreadsheet or SQL result set).
3. **Tools for reading and writing** CSV, Excel, JSON, SQL, and many other formats.
4. **Data wrangling** — filtering, grouping, merging, reshaping, and cleaning data.

Pandas is built on top of NumPy. Every column in a DataFrame is backed by a NumPy array. This is why Pandas inherits NumPy's speed for numerical operations.

```bash
pip install pandas
```

```python
import pandas as pd   # 'pd' is the universal alias — always use this
```

---

### The Series — A Labelled 1D Array

A `Series` is like a Python list with labels (an index) attached to each element. Think of it as a single column of a spreadsheet.

```python
import pandas as pd

# Create a Series from a list — default integer index
grades = pd.Series([85, 92, 78, 95, 88])
print(grades)
# 0    85
# 1    92
# 2    78
# 3    95
# 4    88
# dtype: int64

# Create a Series with a named index (labels)
scores = pd.Series(
    [85, 92, 78, 95],
    index=["Alice", "Bob", "Charlie", "Diana"]
)
print(scores)
# Alice      85
# Bob        92
# Charlie    78
# Diana      95
```

#### Accessing Series Values

```python
# By label
print(scores["Bob"])        # 92

# By position (like a list)
print(scores.iloc[0])       # 85

# Boolean filtering
print(scores[scores > 80])
# Alice    85
# Bob      92
# Diana    95

# Useful attributes
print(scores.index)    # Index(['Alice', 'Bob', 'Charlie', 'Diana'], dtype='object')
print(scores.values)   # [85 92 78 95] — returns a NumPy array
print(scores.dtype)    # int64
print(scores.name)     # None (you can assign: scores.name = "Test Scores")
```

---

### The DataFrame — A 2D Labelled Table

A `DataFrame` is the centrepiece of Pandas — a two-dimensional data structure with labelled rows and columns. Think of it as a Python representation of a spreadsheet, a SQL table, or a CSV file.

#### Creating a DataFrame from a Dictionary

```python
data = {
    "name":   ["Alice", "Bob", "Charlie", "Diana", "Eve"],
    "score":  [85, 92, 78, 95, 61],
    "passed": [True, True, False, True, False],
    "grade":  ["B", "A", "C", "A", "F"]
}

df = pd.DataFrame(data)
print(df)
#       name  score  passed grade
# 0    Alice     85    True     B
# 1      Bob     92    True     A
# 2  Charlie     78   False     C
# 3    Diana     95    True     A
# 4      Eve     61   False     F
```

#### Creating a DataFrame from a List of Dicts

```python
records = [
    {"test": "login",    "status": "pass", "duration_ms": 1200},
    {"test": "search",   "status": "pass", "duration_ms": 850},
    {"test": "checkout", "status": "fail", "duration_ms": 2300},
    {"test": "profile",  "status": "pass", "duration_ms": 450},
]

df = pd.DataFrame(records)
```

---

### Exploring a DataFrame

Before performing any analysis, always explore your dataset first:

```python
df = pd.read_csv("test_results.csv")  # Load from file (covered below)

# Shape and size
print(df.shape)           # (100, 5) — 100 rows, 5 columns
print(len(df))            # 100 — number of rows

# Column names and types
print(df.columns)         # Index(['test', 'status', 'duration_ms', ...])
print(df.dtypes)          # Data type of each column
print(df.info())          # Summary: column names, types, non-null counts, memory

# Preview
print(df.head())          # First 5 rows
print(df.head(10))        # First 10 rows
print(df.tail(5))         # Last 5 rows
print(df.sample(5))       # 5 random rows

# Statistical summary (numerical columns only)
print(df.describe())
#        duration_ms
# count   100.000000
# mean    632.500000
# std     412.300000
# min      95.000000
# 25%     287.500000
# 50%     515.000000   <-- median
# 75%     921.250000
# max    2300.000000
```

---

### Selecting Data

#### Selecting Columns

```python
# Single column — returns a Series
names = df["name"]

# Multiple columns — returns a DataFrame
subset = df[["name", "score"]]

# Attribute access (only works when column name has no spaces)
scores_col = df.score    # Same as df["score"] — avoid this style in production
```

#### Selecting Rows with `.loc` and `.iloc`

```python
# .loc — label-based (uses index labels and column names)
print(df.loc[0])           # Row at index label 0
print(df.loc[0, "name"])   # Value at row 0, column "name" → "Alice"
print(df.loc[0:2, ["name", "score"]])  # Rows 0-2, specific columns

# .iloc — position-based (uses integer positions, like a list)
print(df.iloc[0])          # First row
print(df.iloc[0, 1])       # Row 0, column 1 (score)
print(df.iloc[0:3, 0:2])   # First 3 rows, first 2 columns
```

> 💡 **Rule of thumb:** Use `.loc` when you know the label. Use `.iloc` when you know the position. Avoid mixing them.

---

### Filtering Rows (Boolean Indexing)

This is one of the most frequently used Pandas operations:

```python
# Single condition
passed = df[df["passed"] == True]
high_scores = df[df["score"] >= 90]
failures = df[df["status"] == "fail"]

# Multiple conditions — use & (and), | (or), ~ (not)
# IMPORTANT: wrap each condition in parentheses
passed_and_high = df[(df["passed"] == True) & (df["score"] >= 85)]
low_or_fail = df[(df["score"] < 70) | (df["status"] == "fail")]

# .isin() — filter by membership in a list
a_and_b_grades = df[df["grade"].isin(["A", "B"])]

# String filtering
alice_rows = df[df["name"] == "Alice"]
names_with_a = df[df["name"].str.contains("a", case=False)]

# .query() — a more readable alternative for simple filters
passed = df.query("score >= 80 and passed == True")
```

---

### Adding and Modifying Columns

```python
# Add a new column based on an existing one
df["score_curved"] = df["score"] + 5

# Derive a column using a conditional expression
import numpy as np
df["result"] = np.where(df["score"] >= 70, "Pass", "Fail")

# Apply a custom function to each row with .apply()
def assign_grade(score):
    if score >= 90:
        return "A"
    elif score >= 80:
        return "B"
    elif score >= 70:
        return "C"
    else:
        return "F"

df["grade"] = df["score"].apply(assign_grade)

# Apply a lambda for simple transformations
df["score_pct"] = df["score"].apply(lambda x: f"{x}%")

# Rename columns
df.rename(columns={"score": "test_score", "passed": "is_passed"}, inplace=True)

# Drop a column
df.drop(columns=["score_pct"], inplace=True)
```

---

### Sorting Data

```python
# Sort by a single column (descending)
df_sorted = df.sort_values("score", ascending=False)

# Sort by multiple columns
df_multi = df.sort_values(["grade", "score"], ascending=[True, False])

# Reset the index after sorting
df_sorted = df_sorted.reset_index(drop=True)
```

---

### Grouping and Aggregation

`groupby` is one of Pandas' most powerful features — it lets you split data into groups and compute summaries for each group. This is equivalent to SQL's `GROUP BY`.

```python
# Group by "status" and calculate mean duration
grouped = df.groupby("status")["duration_ms"].mean()
print(grouped)
# status
# fail    2300.0
# pass     672.5

# Multiple aggregations at once
summary = df.groupby("status")["duration_ms"].agg(["mean", "min", "max", "count"])
print(summary)

# Group by, compute size of each group (count of rows)
print(df.groupby("grade").size())
# grade
# A    2
# B    1
# C    1
# F    1

# Full aggregation across multiple columns
report = df.groupby("status").agg(
    avg_duration=("duration_ms", "mean"),
    total_tests=("test", "count"),
    max_duration=("duration_ms", "max")
)
print(report)
```

---

### Handling Missing Data

Real-world data always has gaps. Pandas represents missing values as `NaN` (Not a Number):

```python
import pandas as pd
import numpy as np

data = {
    "name":   ["Alice", "Bob", "Charlie", "Diana"],
    "score":  [85, None, 78, 95],
    "grade":  ["B", "A", np.nan, "A"]
}
df = pd.DataFrame(data)

# Detect missing values
print(df.isnull())        # Boolean DataFrame — True where NaN
print(df.isnull().sum())  # Count of NaNs per column
print(df.isnull().any())  # True for each column that has any NaN

# Drop rows with ANY missing value
df_clean = df.dropna()

# Drop rows only if a specific column is missing
df_clean = df.dropna(subset=["score"])

# Fill missing values with a constant
df["score"] = df["score"].fillna(0)

# Fill with the column mean (common strategy)
df["score"] = df["score"].fillna(df["score"].mean())

# Forward fill — carry the last valid value forward
df["score"] = df["score"].fillna(method="ffill")
```

---

### Reading and Writing Files

```python
# Read CSV
df = pd.read_csv("test_results.csv")

# With options
df = pd.read_csv(
    "test_results.csv",
    sep=",",                  # Delimiter (default is comma)
    header=0,                 # Row to use as column names (default is 0)
    index_col="test_id",      # Use this column as the row index
    usecols=["test", "status", "duration_ms"],  # Load only these columns
    dtype={"duration_ms": int}  # Enforce data types
)

# Write to CSV
df.to_csv("processed_results.csv", index=False)  # index=False omits row numbers

# Read/write JSON
df = pd.read_json("results.json")
df.to_json("output.json", orient="records", indent=2)

# Read Excel (requires openpyxl)
df = pd.read_excel("results.xlsx", sheet_name="Sheet1")
```

---

## Code Example: Test Results Report

A complete, practical QA scenario — loading test results from a CSV, processing them, and producing a summary report:

```python
import pandas as pd

# --- Simulate loading a test results file ---
data = {
    "test_name": [
        "login", "search", "checkout", "profile", "logout",
        "register", "cart", "payment", "confirm", "home"
    ],
    "duration_ms": [1200, 850, 2300, 450, 180, 920, 1100, 3200, 670, 310],
    "status": [
        "pass", "pass", "fail", "pass", "pass",
        "pass", "pass", "fail", "pass", "pass"
    ],
    "module": [
        "auth", "search", "checkout", "user", "auth",
        "auth", "cart", "checkout", "checkout", "home"
    ]
}
df = pd.DataFrame(data)

# --- High-level summary ---
total    = len(df)
passed   = (df["status"] == "pass").sum()
failed   = (df["status"] == "fail").sum()
pass_rate = passed / total

print("=== Test Execution Summary ===")
print(f"Total:     {total}")
print(f"Passed:    {passed}  ({pass_rate:.0%})")
print(f"Failed:    {failed}")
print(f"Avg time:  {df['duration_ms'].mean():.0f}ms")
print(f"Slowest:   {df.loc[df['duration_ms'].idxmax(), 'test_name']} "
      f"({df['duration_ms'].max()}ms)")

# --- Failed tests ---
print("\n=== Failed Tests ===")
failed_tests = df[df["status"] == "fail"][["test_name", "duration_ms", "module"]]
print(failed_tests.to_string(index=False))

# --- Performance by module ---
print("\n=== Average Duration by Module ===")
module_stats = df.groupby("module")["duration_ms"].agg(
    avg_ms="mean",
    tests="count"
).sort_values("avg_ms", ascending=False)
print(module_stats)

# --- Flag slow tests (SLA: >1000ms) ---
df["sla_breach"] = df["duration_ms"] > 1000
print(f"\nSLA breaches (>1000ms): {df['sla_breach'].sum()}")
print(df[df["sla_breach"]][["test_name", "duration_ms"]].to_string(index=False))

# --- Export results ---
df.to_csv("processed_test_results.csv", index=False)
print("\nReport saved to processed_test_results.csv")
```

---

## Summary

| Concept | Key Takeaway |
|---|---|
| **Series** | Labelled 1D array — a single column with an index |
| **DataFrame** | Labelled 2D table — the primary Pandas data structure |
| **`pd.read_csv()`** | Load any CSV file into a DataFrame in one line |
| **`.loc` / `.iloc`** | Label-based vs position-based row/column selection |
| **Boolean indexing** | `df[df["col"] > value]` — filter rows by condition |
| **`.apply()`** | Apply a function to every value in a column |
| **`groupby().agg()`** | Split → Apply → Combine: SQL-style GROUP BY |
| **Missing data** | `dropna()`, `fillna()` — always check for NaN before analysis |
| **`to_csv()`** | Export your DataFrame back to a file |

Pandas is one of the most-used libraries in all of Python. You will encounter it in data-driven testing, log analysis, performance benchmarking, and reporting throughout this programme.

> 📌 **Tip:** The companion **NumPy** reading (also from today) explains the numerical engine that Pandas is built on. Reading both gives you a complete picture.

---

## Additional Resources
- [Pandas Documentation — Getting Started](https://pandas.pydata.org/docs/getting_started/index.html) — Official, structured introduction to Pandas with worked examples.
- [Pandas Documentation — User Guide](https://pandas.pydata.org/docs/user_guide/index.html) — Deep-dive reference for all Pandas operations.
- [Real Python — Pandas DataFrames 101](https://realpython.com/pandas-dataframe/) — Comprehensive tutorial with practical, real-world data examples.
