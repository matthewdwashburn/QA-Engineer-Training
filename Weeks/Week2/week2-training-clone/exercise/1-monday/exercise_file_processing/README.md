# Exercise: CSV File Processing with Context Managers

**Mode:** Implementation (Code Lab)  
**Duration:** 60–90 minutes  
**Day:** 1-monday | **Week:** 2 — Python & Java Foundations  

---

## Objective

- Read a CSV file using **`with open(..., encoding="utf-8")`**.
- Compute aggregates from rows (totals / averages).
- Write a **new** text or CSV report using a **separate** `with` block.
- Never leak file handles—if an error occurs mid-read, files still close.

---

## Prerequisites

| Concept | Source |
|---------|--------|
| `with` / context managers | `written/1-monday/with-statements.md` |
| `open()` modes, UTF-8 | `written/1-monday/opening-files-file-handling.md` |
| read/write | `written/1-monday/read-files-write-create.md` |
| Demo | `demos/1-monday/code/demo_file_handling.py` |

---

## Scenario

You receive **`starter_code/data/sales.csv`** with columns: `sku`, `units`, `unit_price` (header row included). Produce **`output/summary.txt`** (create `output/` if needed) that includes:

1. Total number of **data rows** (excluding header).
2. **Grand total** sales: sum of `units * unit_price` per row (treat values as numbers).
3. **Average** revenue per line item (grand total ÷ row count).
4. The **SKU** with the highest line revenue (`units * unit_price`). If tied, pick the first in file order.

All file I/O must use **`with`**.

---

## Setup

1. `cd` into `exercise_file_processing`.
2. (Recommended) Use the same venv you use for Week 2 Python work.
3. Inspect `starter_code/data/sales.csv`—do not edit the data file.

---

## Core Tasks

### Task 1 — Read and parse (30 min)

In **`starter_code/process_sales.py`** (create this file):

- Open `data/sales.csv` with `csv.DictReader`, **`encoding="utf-8"`**, **`newline=""`** on write if you also write CSV later.
- Parse `units` and `unit_price` as numbers (`int` / `float` as appropriate).
- Skip or fail gracefully on malformed rows (optional stretch: log count of bad rows to stderr).

### Task 2 — Write report (25 min)

- Use a **second** `with open(..., "w", encoding="utf-8")` targeting **`output/summary.txt`** (path relative to `starter_code/` or project root—pick one and document it in a comment).
- Write plain text, human-readable lines, for example:

```text
rows=...
grand_total=...
average_line_revenue=...
top_sku=...
top_line_revenue=...
```

### Task 3 — Self-check (10 min)

- Run your script; open `output/summary.txt`.
- Manually verify **grand total** for at least the first two rows with a calculator.

---

## Definition of Done

- [ ] `process_sales.py` uses **`with`** for every file open.
- [ ] `output/summary.txt` exists after a successful run and matches the spec.
- [ ] Code runs from **`starter_code/`** via `python process_sales.py` (adjust paths accordingly).

---

## Stretch

- Also emit **`output/summary.json`** with the same fields as JSON.
- Add **`argparse`** so input/output paths are CLI flags.

---

## References (paths)

- Written: `content/Week2-Python-Java/written/1-monday/`
- Demo: `content/Week2-Python-Java/demos/1-monday/`
