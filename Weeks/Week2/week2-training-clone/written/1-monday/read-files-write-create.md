# Reading and Writing Files

## Learning Objectives
- Read file contents using `read()`, `readline()`, `readlines()`, and iteration.
- Understand when to load everything at once vs. process line by line.
- Write and append data using `write()` and `writelines()`, and understand newline behaviour.
- Use `seek()` and `tell()` for random-access file operations.
- Apply `flush()` to force data to disk.
- Work with `StringIO` and `BytesIO` for in-memory file-like objects.
- Safely create, overwrite, and append files using the correct mode.

---

## Why This Matters

> **Weekly Epic Connection:** Pipelines that ingest CSV/JSON logs and emit reports are core QA skills. Knowing how to read without loading gigabytes into memory keeps automation scripts fast and stable. Knowing how to write reliably — including flushing and handling encoding — prevents corrupted output files that silently break downstream analysis.

---

## The Concept

### Reading Files

Python gives you several ways to read a file, each suited to different file sizes and use cases.

#### `read()` — Entire File as One String

```python
with open("data.txt", encoding="utf-8") as f:
    whole = f.read()   # Returns one str containing the entire file

print(type(whole))   # <class 'str'>
print(len(whole))    # Number of characters
```

> **⚠️ Warning:** `read()` loads the entire file into memory at once. For a 2 GB log file, this will consume 2 GB of RAM. Always ask: "how large could this file realistically be?"

You can limit how much is read by passing a size:

```python
with open("data.txt", encoding="utf-8") as f:
    first_1000 = f.read(1000)   # Read at most 1000 characters
```

#### `readline()` — One Line at a Time

`readline()` reads up to and including the next `\n`, or returns `''` (empty string) at end-of-file:

```python
with open("data.txt", encoding="utf-8") as f:
    line1 = f.readline()   # "first line\n"
    line2 = f.readline()   # "second line\n"
    eof   = f.readline()   # ""  — end of file

# The trailing \n is included — strip it if you need the clean value
line1_clean = f.readline().rstrip("\n")
```

`readline()` is rarely used directly — a `for` loop over the file is simpler and more readable.

#### `readlines()` — All Lines as a List

Returns a **list** of all lines, each with its trailing `\n` intact:

```python
with open("data.txt", encoding="utf-8") as f:
    lines = f.readlines()   # ['first line\n', 'second line\n', 'third line\n']

print(lines[0])    # 'first line\n'
```

> **⚠️ Warning:** Like `read()`, `readlines()` loads the entire file into memory. Use it only for small files where random access to line numbers is needed.

#### Iterating the File Object (Preferred for Large Files)

Iterating directly over the file object is the most memory-efficient approach — it reads **one line at a time**:

```python
with open("app.log", encoding="utf-8") as f:
    for line in f:
        # line is "ERROR: timeout at 09:32:14\n"
        line = line.rstrip("\n")   # Remove the trailing newline
        if "ERROR" in line:
            print(line)
```

This approach works correctly on files of any size — it never loads more than one line into memory at a time.

#### Choosing the Right Read Method

| Method | Returns | Memory | Use When |
|--------|---------|--------|----------|
| `f.read()` | One `str` | Entire file | Small files, need full text at once |
| `f.read(n)` | At most `n` chars | n chars | Chunked reading, binary headers |
| `f.readline()` | One line as `str` | One line | Manual line-by-line with state |
| `f.readlines()` | List of lines | Entire file | Small files, need line index access |
| `for line in f:` | One line per iteration | One line | **Default — use this for large files** |

---

### Writing Files

#### `write(s)` — Write a String

`write()` writes the string `s` exactly as given and returns the number of characters written. It does **not** add a newline — you must include `\n` explicitly:

```python
with open("out.txt", "w", encoding="utf-8") as f:
    chars = f.write("Hello, World!\n")   # Returns 14
    f.write("Second line\n")
    f.write("Third line\n")
```

#### `writelines(iterable)` — Write Multiple Strings

`writelines()` writes each string in the iterable in sequence. Like `write()`, it does **not** add newlines between items:

```python
lines = ["Alice,85\n", "Bob,92\n", "Charlie,78\n"]

with open("scores.csv", "w", encoding="utf-8") as f:
    f.writelines(lines)   # Writes all three lines in one call
```

> **Common mistake:** Forgetting to add `\n` at the end of each element:
> ```python
> # ❌ All items written as one long line
> f.writelines(["Alice", "Bob", "Charlie"])   # "AliceBobCharlie"
>
> # ✅ Each item on its own line
> f.writelines(["Alice\n", "Bob\n", "Charlie\n"])
> ```

#### `flush()` — Force Data to Disk

Python uses **buffered I/O** — writes are staged in an in-memory buffer for performance, then flushed (written to disk) periodically or when `close()` is called. In some situations you need data on disk immediately (e.g., a live log file being watched by another process):

```python
import time

with open("live.log", "w", encoding="utf-8", buffering=1) as f:  # line-buffered
    for i in range(100):
        f.write(f"Step {i} complete\n")
        f.flush()   # Force to disk immediately — another process can see this line now
        time.sleep(0.1)
```

> **Note:** `buffering=1` enables **line buffering** — the buffer is flushed automatically after each `\n`. This is often sufficient for log files without calling `flush()` manually.

---

### Creating Files Safely

| Mode | File exists | File absent | Notes |
|------|------------|-------------|-------|
| `'w'` | **Overwrites** (data is lost!) | Creates | Default write mode |
| `'x'` | **Raises `FileExistsError`** | Creates | Safe "create once" pattern |
| `'a'` | Appends to end | Creates | Good for log files |

```python
from pathlib import Path

# ❌ Dangerous if file already contains important data
with open("report.txt", "w") as f:
    f.write("Overwriting existing data!\n")

# ✅ Safe — raises immediately if the file already exists
try:
    with open("report.txt", "x", encoding="utf-8") as f:
        f.write("Created fresh.\n")
except FileExistsError:
    print("Report already exists — will not overwrite.")

# ✅ Append — safe for cumulative logs
with open("test_results.log", "a", encoding="utf-8") as f:
    f.write("[PASS] test_login\n")
```

---

### Random Access: `seek()` and `tell()`

The file object maintains a **current position** pointer. Reads and writes advance this pointer. Use `tell()` to query the position and `seek()` to move it:

```python
with open("data.txt", "r+", encoding="utf-8") as f:
    # Read the first line
    first_line = f.readline()
    print(f.tell())    # Position is now after the first line

    # Go back to the beginning
    f.seek(0)

    # Read again from the start
    first_line_again = f.readline()

    # Jump to the end
    f.seek(0, 2)       # whence=2 means "relative to end"
    print(f.tell())    # Total file size in bytes
```

`seek(offset, whence)`:
- `whence=0` — offset from the **start** (default)
- `whence=1` — offset from the **current position**
- `whence=2` — offset from the **end**

---

### Quick Patterns

#### Read-Modify-Write (Using `pathlib`)

For small files, `pathlib.Path` provides convenient shortcuts:

```python
from pathlib import Path

p = Path("config.txt")

# Read → modify → write in three lines
text    = p.read_text(encoding="utf-8")
updated = text.replace("OLD_HOST", "NEW_HOST")
p.write_text(updated, encoding="utf-8")
```

> **Note:** `Path.write_text()` always overwrites the file. For large files, use the streaming patterns above.

#### Processing a CSV File Line by Line

```python
from pathlib import Path

totals = {}

with open(Path("data") / "scores.csv", encoding="utf-8") as f:
    next(f)   # Skip header row
    for line in f:
        name, score_str = line.rstrip("\n").split(",")
        totals[name] = int(score_str)

print(totals)   # {'Alice': 85, 'Bob': 92, 'Charlie': 78}
```

#### Writing a Report File

```python
from pathlib import Path
from datetime import datetime

report_path = Path("reports") / f"test_run_{datetime.now():%Y%m%d_%H%M%S}.txt"
report_path.parent.mkdir(parents=True, exist_ok=True)

results = [
    ("test_login",    "PASS", 1200),
    ("test_checkout", "FAIL", 3500),
    ("test_profile",  "PASS",  850),
]

with open(report_path, "w", encoding="utf-8") as f:
    f.write(f"Test Run Report — {datetime.now()}\n")
    f.write("=" * 50 + "\n")
    for name, status, ms in results:
        f.write(f"{status:<6} {name:<25} {ms}ms\n")
    f.write("=" * 50 + "\n")
    passed = sum(1 for _, s, _ in results if s == "PASS")
    f.write(f"Passed: {passed}/{len(results)}\n")
```

---

### In-Memory Files: `StringIO` and `BytesIO`

Sometimes you want a file-like object that lives entirely in memory — no disk I/O. The `io` module provides `StringIO` (for text) and `BytesIO` (for bytes). They implement the same interface as real file objects:

```python
from io import StringIO

# Create an in-memory text "file"
buffer = StringIO()
buffer.write("Alice,85\n")
buffer.write("Bob,92\n")

# Rewind and read back
buffer.seek(0)
print(buffer.read())
# Alice,85
# Bob,92

# Use cases:
# - Build output in memory before writing to disk
# - Pass to functions that expect a file object (CSV reader, JSON parser)
# - Testing: mock file reads without creating actual files
```

```python
from io import StringIO
import csv

# Test CSV parsing without a real file
csv_data = "name,score\nAlice,85\nBob,92\n"
reader = csv.reader(StringIO(csv_data))
for row in reader:
    print(row)
# ['name', 'score']
# ['Alice', '85']
# ['Bob', '92']
```

---

## Summary

- **`read()`** loads everything at once — use only for small files.
- **`for line in f:`** is the most memory-efficient way to process large files — one line at a time.
- **`write()` and `writelines()`** do not add newlines for you — be explicit with `\n`.
- Use **`'x'`** mode when you must not overwrite an existing file.
- Use **`'a'`** mode to append to a log file without destroying existing content.
- **`flush()`** forces buffered data to disk — useful for live log files.
- **`seek()` / `tell()`** enable random access — rarely needed in text mode; common in binary.
- **`StringIO` / `BytesIO`** provide in-memory file-like objects — great for testing and intermediate processing.

---

## Additional Resources

- [Python Docs — I/O Tutorial](https://docs.python.org/3/tutorial/inputoutput.html)
- [Python Docs — Text I/O](https://docs.python.org/3/library/io.html#text-i-o)
- [Python Docs — `pathlib.Path.read_text` / `write_text`](https://docs.python.org/3/library/pathlib.html#pathlib.Path.read_text)
- [Python Docs — `io.StringIO`](https://docs.python.org/3/library/io.html#io.StringIO)
