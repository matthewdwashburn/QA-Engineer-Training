# Introduction to Pandas and NumPy

## Learning Objectives
- Understand what Pandas and NumPy are and when to use them.
- Create and manipulate Pandas DataFrames and Series.
- Perform basic NumPy array operations.

---

## Why This Matters

> **Weekly Epic Connection:** Quality engineers frequently analyze test results, process CSV data, and compute metrics. Pandas and NumPy are the industry-standard tools for this — far more powerful than manual loops. Today is an introduction; you'll use these libraries extensively in later weeks.

---

## The Concept

### NumPy — Numerical Computing

**NumPy** (Numerical Python) provides a powerful array object and mathematical functions that operate on entire arrays at once.

```bash
pip install numpy
```

#### Creating Arrays

```python
import numpy as np

# From a list
scores = np.array([85, 92, 78, 95, 88])
print(scores)        # [85 92 78 95 88]
print(type(scores))  # <class 'numpy.ndarray'>

# Useful array creators
zeros = np.zeros(5)           # [0. 0. 0. 0. 0.]
ones = np.ones(3)             # [1. 1. 1.]
range_arr = np.arange(0, 10, 2)  # [0 2 4 6 8]
```

#### Why NumPy Over Lists?

**Speed and simplicity** — operations apply to every element automatically:

```python
# With regular lists (verbose)
scores_list = [85, 92, 78, 95, 88]
curved = [s + 5 for s in scores_list]

# With NumPy (concise and fast)
scores_np = np.array([85, 92, 78, 95, 88])
curved = scores_np + 5   # [90 97 83 100 93] — applies to ALL elements
```

#### Common Operations

```python
scores = np.array([85, 92, 78, 95, 88])

scores.mean()    # 87.6   — Average
scores.std()     # 5.86   — Standard deviation
scores.min()     # 78     — Minimum
scores.max()     # 95     — Maximum
scores.sum()     # 438    — Sum
np.median(scores)  # 88.0  — Median

# Boolean filtering
passing = scores[scores >= 80]  # [85 92 95 88]
```

---

### Pandas — Data Analysis

**Pandas** builds on NumPy and provides two key structures: **Series** (1D) and **DataFrame** (2D table).

```bash
pip install pandas
```

#### Series — A Labeled 1D Array

```python
import pandas as pd

# Create a Series
scores = pd.Series([85, 92, 78, 95], index=["Alice", "Bob", "Charlie", "Diana"])
print(scores)
# Alice      85
# Bob        92
# Charlie    78
# Diana      95

# Access by label
print(scores["Bob"])        # 92
print(scores[scores > 80])  # Alice, Bob, Diana
```

#### DataFrame — A 2D Table

A DataFrame is like a spreadsheet or SQL table:

```python
# Create from a dictionary
data = {
    "name": ["Alice", "Bob", "Charlie", "Diana"],
    "score": [85, 92, 78, 95],
    "passed": [True, True, False, True]
}
df = pd.DataFrame(data)
print(df)
#       name  score  passed
# 0    Alice     85    True
# 1      Bob     92    True
# 2  Charlie     78   False
# 3    Diana     95    True
```

#### Reading CSV Files

One of Pandas' most powerful features:

```python
# Read a CSV file into a DataFrame
df = pd.read_csv("test_results.csv")

# Quick overview
print(df.head())       # First 5 rows
print(df.shape)        # (rows, columns)
print(df.describe())   # Statistical summary
print(df.columns)      # Column names
```

#### Basic Operations

```python
# Select a column
names = df["name"]

# Filter rows
passed = df[df["passed"] == True]
high_scores = df[df["score"] >= 90]

# Add a column
df["grade"] = df["score"].apply(
    lambda s: "A" if s >= 90 else "B" if s >= 80 else "C"
)

# Sort
df_sorted = df.sort_values("score", ascending=False)

# Basic statistics
print(f"Average score: {df['score'].mean():.1f}")
print(f"Pass rate: {df['passed'].mean():.1%}")
```

### When to Use Which?

| Tool | Best For |
|------|----------|
| **NumPy** | Mathematical operations on numerical arrays, linear algebra, random numbers |
| **Pandas** | Tabular data (CSVs, databases), data analysis, grouping, filtering |
| **Plain Python** | Simple scripts where you don't need the overhead of these libraries |

---

## Code Example: Quick Test Results Analysis

```python
import pandas as pd

# Sample test results
results = pd.DataFrame({
    "test_name": ["login", "search", "checkout", "profile", "logout"],
    "duration_ms": [1200, 850, 2300, 450, 180],
    "status": ["pass", "pass", "fail", "pass", "pass"]
})

print("=== Test Results Summary ===")
print(f"Total tests:  {len(results)}")
print(f"Passed:       {(results['status'] == 'pass').sum()}")
print(f"Failed:       {(results['status'] == 'fail').sum()}")
print(f"Avg duration: {results['duration_ms'].mean():.0f}ms")
print(f"Slowest test: {results.loc[results['duration_ms'].idxmax(), 'test_name']}")
```

---

## Summary

- **NumPy** provides fast, vectorized operations on numerical arrays — no loops needed.
- **Pandas** provides DataFrames for tabular data — think spreadsheets in Python.
- `pd.read_csv()` loads CSV files; `df.describe()` gives instant statistics.
- Both libraries are the foundation of data analysis in Python.
- You'll encounter these extensively in later weeks when analyzing test results and metrics.

---

## Additional Resources
- [Pandas Documentation — Getting Started](https://pandas.pydata.org/docs/getting_started/index.html)
- [NumPy Documentation — Quickstart](https://numpy.org/doc/stable/user/quickstart.html)
- [Real Python — Pandas DataFrames](https://realpython.com/pandas-dataframe/)
