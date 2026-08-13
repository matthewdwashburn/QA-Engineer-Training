# NumPy — Numerical Computing in Python

## Learning Objectives
- Understand what NumPy is and why it outperforms native Python lists for numerical work.
- Create NumPy arrays using multiple methods (from lists, built-in creators, random generation).
- Understand array shapes, dimensions, and data types (`dtype`).
- Apply vectorized operations, slicing, and boolean indexing to arrays.
- Use NumPy's mathematical and statistical functions for data analysis.
- Understand broadcasting and how it enables operations across arrays of different shapes.

---

## Why This Matters

> **Weekly Epic Connection:** *From Version Control to Python Mastery* — As you progress through this programme, you will write automation scripts that process large volumes of test results, timing data, and metrics. NumPy is the numerical backbone of the entire Python data ecosystem. Pandas (covered separately today) is built on top of NumPy arrays. Tools like SciPy, scikit-learn, and TensorFlow also rely on NumPy under the hood.

As a QA engineer, NumPy lets you:
- Calculate performance benchmarks (averages, percentiles, standard deviations) on large test result datasets in milliseconds.
- Generate structured test data (random integers, floats, ranges) programmatically.
- Perform mathematical assertions in automated tests without writing manual loops.

---

## The Concept

### What is NumPy?

**NumPy** (Numerical Python) provides:
1. A powerful **N-dimensional array object** (`ndarray`) — the core data structure.
2. **Vectorized operations** — functions that operate on entire arrays at once, without loops.
3. **Mathematical and statistical functions** — mean, median, standard deviation, percentiles, and more.

```bash
pip install numpy
```

```python
import numpy as np  # 'np' is the universal alias — always use this
```

---

### Why NumPy Instead of Python Lists?

Python lists can hold mixed types but are slow for numerical work. NumPy arrays enforce a **single data type** and store data in a contiguous memory block, enabling computation loops in compiled C code rather than Python.

```python
import numpy as np
import time

size = 1_000_000

py_list = list(range(size))
start = time.time()
result = [x * 2 for x in py_list]
print(f"List loop:        {time.time() - start:.4f}s")

np_array = np.arange(size)
start = time.time()
result = np_array * 2   # Applies to all 1 million elements at once
print(f"NumPy vectorized: {time.time() - start:.4f}s")
# NumPy is typically 10x-100x faster
```

---

### Creating Arrays

#### From a Python List

```python
scores = np.array([85, 92, 78, 95, 88])
print(scores.dtype)   # int64

# 2D array from a list of lists
matrix = np.array([[1, 2, 3], [4, 5, 6], [7, 8, 9]])
print(matrix.shape)   # (3, 3)
print(matrix.ndim)    # 2
```

#### Built-in Array Creators

```python
np.zeros(5)                # [0. 0. 0. 0. 0.]
np.ones((2, 3))            # 2x3 matrix of ones
np.eye(3)                  # 3x3 identity matrix
np.arange(0, 10, 2)        # [0 2 4 6 8] — start, stop, step
np.linspace(0, 1, 5)       # [0.   0.25  0.5  0.75  1.  ] — 5 evenly spaced points
```

#### Random Arrays (Reproducible)

```python
rng = np.random.default_rng(seed=42)   # Seed for reproducibility

rng.random(5)                          # 5 random floats [0, 1)
rng.integers(1, 100, size=10)          # 10 random ints between 1 and 99
rng.normal(loc=250, scale=60, size=50) # Normal distribution — great for test timings
```

---

### Array Attributes

```python
arr = np.array([[1, 2, 3], [4, 5, 6]], dtype=np.float32)

arr.shape    # (2, 3)
arr.ndim     # 2
arr.size     # 6 — total elements
arr.dtype    # float32
arr.nbytes   # 24 — total bytes used

# Casting
arr.astype(np.int32)  # Truncates floats to ints
```

---

### Indexing and Slicing

```python
scores = np.array([85, 92, 78, 95, 88])

scores[0]    # 85 — first element
scores[-1]   # 88 — last element
scores[1:4]  # [92 78 95]
scores[::-1] # [88 95 78 92 85] — reversed
```

#### 2D Indexing

```python
matrix = np.array([[10, 20, 30], [40, 50, 60], [70, 80, 90]])

matrix[0, 0]    # 10
matrix[1, 2]    # 60
matrix[0, :]    # [10 20 30] — entire row 0
matrix[:, 1]    # [20 50 80] — entire column 1
matrix[0:2, 1:] # [[20 30], [50 60]] — submatrix
```

#### Boolean (Fancy) Indexing

```python
scores = np.array([85, 92, 78, 95, 88, 62, 74, 91])

passing = scores[scores >= 80]             # [85 92 95 88 91]
high    = scores[scores >= 90]             # [92 95 91]
mid     = scores[(scores >= 75) & (scores < 90)]  # [85 88] — use & not 'and'
```

---

### Vectorized Operations

```python
scores = np.array([85, 92, 78, 95, 88])

scores + 5      # [90 97 83 100 93] — add 5 to every element
scores / 2      # [42.5 46.  39.  47.5 44. ]
scores ** 2     # [7225 8464 6084 9025 7744]
scores >= 80    # [ True  True False  True  True] — boolean array
```

---

### Statistical Functions

```python
scores = np.array([85, 92, 78, 95, 88, 62, 74, 91])

np.mean(scores)          # 83.125
np.median(scores)        # 86.5
np.std(scores)           # 10.53
np.min(scores)           # 62
np.max(scores)           # 95
np.argmax(scores)        # 3 — index of the max
np.percentile(scores, 95)  # 94.3 — 95th percentile (p95 latency)
np.cumsum(scores)        # Running total
np.sort(scores)          # Sorted ascending
np.argsort(scores)       # Indices that would sort the array
```

---

### Reshaping and Broadcasting

```python
flat = np.arange(12)
flat.reshape(3, 4)   # 3 rows, 4 columns — same data, new shape
flat.reshape(4, -1)  # -1 = NumPy calculates the missing dimension
flat.flatten()       # Back to 1D

# Broadcasting — scalar applies to every element
arr = np.array([1, 2, 3])
arr + 10  # [11 12 13]

# Broadcasting — 1D row added to each row of a 2D matrix
matrix = np.array([[1, 2, 3], [4, 5, 6]])
offset  = np.array([10, 20, 30])
matrix + offset  # [[11 22 33], [14 25 36]]
```

---

## Code Example: API Response Time Analysis

```python
import numpy as np

rng = np.random.default_rng(seed=99)
response_times = rng.normal(loc=250, scale=60, size=100).astype(int)

print("=== API Performance Report ===")
print(f"Total requests:    {response_times.size}")
print(f"Mean:              {np.mean(response_times):.1f} ms")
print(f"Median:            {np.median(response_times):.1f} ms")
print(f"Std deviation:     {np.std(response_times):.1f} ms")
print(f"Min / Max:         {np.min(response_times)} / {np.max(response_times)} ms")
print(f"p95 latency:       {np.percentile(response_times, 95):.0f} ms")
print(f"p99 latency:       {np.percentile(response_times, 99):.0f} ms")

slow = response_times[response_times > 400]
print(f"\nSLA breaches >400ms: {len(slow)} ({len(slow)/len(response_times):.1%})")
print(f"Slow run indices:    {np.where(response_times > 400)[0]}")
```

---

## Summary

| Concept | Key Takeaway |
|---|---|
| **ndarray** | Single-type, contiguous-memory array — far faster than Python lists |
| **Vectorization** | Operations apply element-wise — no explicit loops needed |
| **dtype / shape** | All elements share one type; shape describes dimensions |
| **Boolean indexing** | Filter arrays with conditions: `arr[arr > 80]` |
| **Statistics** | `np.mean()`, `np.std()`, `np.percentile()` — essential for metrics |
| **Broadcasting** | Operations between different-shaped arrays — NumPy expands automatically |

> 📌 **Coming Up Next:** Read the separate **Pandas** material to see how NumPy arrays power DataFrames — the tabular data structure you'll use daily for test result analysis.

---

## Additional Resources
- [NumPy Official Documentation — Quickstart](https://numpy.org/doc/stable/user/quickstart.html)
- [NumPy Official Documentation — Array Indexing](https://numpy.org/doc/stable/user/basics.indexing.html)
- [Real Python — NumPy Tutorial](https://realpython.com/numpy-tutorial/)
