# Opening Files and File Handling Basics

## Learning Objectives
- Explain what a file object is and how Python represents files in memory.
- Open files with `open()` using the correct **mode** for each use case.
- Describe what text vs. binary mode means for line endings, encoding, and data types.
- Use `pathlib.Path` to build cross-platform paths safely.
- Follow professional best practices: explicit encoding, context managers, and error handling.

---

## Why This Matters

> **Weekly Epic Connection:** File I/O is how tests read fixture data, write reports, process CSV exports, and parse log files. Wrong mode or missing encoding causes flaky automation and corrupted data on different operating systems. Getting this right from the start is a mark of a professional engineer.

---

## The Concept

### What Is a File Object?

When you call `open()`, Python asks the **operating system** to open the file and return a **file descriptor** — a low-level integer handle to the open file. Python wraps this in a high-level **file object** (also called a file handle or stream) that provides:

- **Read methods**: `read()`, `readline()`, `readlines()`
- **Write methods**: `write()`, `writelines()`
- **Positioning methods**: `seek()`, `tell()`
- **Resource management**: `close()`, `__enter__()`, `__exit__()`
- **Buffering**: data is staged in memory before being flushed to disk for performance

```python
f = open("data.txt", "r", encoding="utf-8")
print(type(f))   # <class '_io.TextIOWrapper'>
print(f.name)    # "data.txt"
print(f.mode)    # "r"
print(f.closed)  # False
f.close()
print(f.closed)  # True
```

> **Important:** Every open file holds an OS resource. If you forget to call `close()`, the file remains locked (especially on Windows) until the garbage collector runs — which may be never in a long-running process. Always use `with` to guarantee cleanup.

---

### `open(file, mode, encoding, errors, newline, buffering)`

The full signature of `open()`:

```python
open(
    file,           # Path (str, bytes, or Path object)
    mode='r',       # Access mode — see table below
    buffering=-1,   # -1 = system default; 0 = unbuffered (binary only); 1 = line-buffered
    encoding=None,  # Text mode only: 'utf-8', 'latin-1', etc.
    errors=None,    # How to handle encoding errors: 'strict', 'ignore', 'replace'
    newline=None,   # How newlines are handled in text mode
)
```

---

### File Modes

#### Basic Modes

| Mode | Meaning | File must exist? | Truncates? |
|------|---------|-----------------|-----------|
| `'r'` | Read text (default) | ✅ Yes — raises `FileNotFoundError` | No |
| `'w'` | Write text | No — creates if missing | ✅ Yes, overwrites |
| `'a'` | Append text | No — creates if missing | No |
| `'x'` | Exclusive create — fails if file exists | ❌ No — raises `FileExistsError` | — |

#### Binary Variants

Append **`b`** for binary mode — you get `bytes` instead of `str`:

| Mode | Meaning |
|------|---------|
| `'rb'` | Read binary |
| `'wb'` | Write binary |
| `'ab'` | Append binary |
| `'xb'` | Exclusive create, binary |

#### Read/Write Combinations

Append **`+`** to allow both reading and writing on the same file:

| Mode | Meaning | Common Use |
|------|---------|-----------|
| `'r+'` | Read and write — file must exist, not truncated, pointer at start | In-place editing |
| `'w+'` | Write and read — truncates file (or creates) | Create then immediately read back |
| `'a+'` | Append and read — pointer at end for writes, seek to read | Log files with inspection |

> **Rule of thumb:** `'r'` for reading, `'w'` for writing (overwrite), `'a'` for appending, `'x'` for safe creation. Add `'b'` for binary data (images, PDFs, pickles). Use `'+'` combinations only when you specifically need both operations on one handle.

---

### Text Mode vs. Binary Mode

This is one of the most important distinctions in Python file I/O:

#### Text Mode (`'r'`, `'w'`, `'a'`, ...)

- Python **decodes** bytes → `str` using the specified `encoding` on read.
- Python **encodes** `str` → bytes using the specified `encoding` on write.
- **Newline translation** happens automatically:
  - On Windows, `\r\n` in the file is translated to `\n` in your string.
  - On write, `\n` in your string is translated to `\r\n` in the file (on Windows).
- You work with **`str`** objects.

#### Binary Mode (`'rb'`, `'wb'`, ...)

- No encoding or decoding — data passes through **as-is**.
- No newline translation — `\r\n` stays `\r\n`.
- You work with **`bytes`** objects.
- Required for non-text formats: images, audio, compiled executables, pickle files, ZIP archives.

```python
# Text mode — you see str
with open("report.txt", "r", encoding="utf-8") as f:
    content = f.read()
    print(type(content))   # <class 'str'>

# Binary mode — you see bytes
with open("image.png", "rb") as f:
    header = f.read(4)
    print(type(header))    # <class 'bytes'>
    print(header)          # b'\x89PNG'  (PNG magic bytes)
```

---

### Encoding — Always Specify `'utf-8'`

When no `encoding` is specified, Python uses the **platform default** (`locale.getpreferredencoding(False)`):
- On modern Linux/macOS: typically `utf-8`
- On older Windows systems: `cp1252` or another legacy encoding

This means code that works on your MacBook may produce `UnicodeDecodeError` or garbled text on a colleague's Windows machine.

```python
# ❌ Platform-dependent — may fail or corrupt data
with open("data.txt") as f:
    text = f.read()

# ✅ Explicit and portable — works everywhere
with open("data.txt", encoding="utf-8") as f:
    text = f.read()
```

#### Handling Encoding Errors

Use the `errors=` parameter when you can't guarantee the encoding:

```python
# 'strict' (default) — raises UnicodeDecodeError on any bad byte
# 'ignore'           — silently drops undecodable bytes
# 'replace'          — substitutes the replacement character (?)

with open("legacy.txt", encoding="utf-8", errors="replace") as f:
    text = f.read()  # Bad bytes become ? instead of crashing
```

---

### The `newline` Parameter

The `newline=` parameter gives you fine-grained control over newline handling in text mode:

| `newline=` | Read behaviour | Write behaviour |
|-----------|---------------|----------------|
| `None` (default) | `\r\n`, `\r`, `\n` → all become `\n` | `\n` → platform default (`\r\n` on Windows) |
| `''` | No translation — you see raw bytes as-is | No translation |
| `'\n'` | Only `\n` treated as newline; `\r\n` comes through as-is | `\n` written as-is |

Use `newline=''` when working with the `csv` module — it handles newlines itself and needs the raw bytes:

```python
import csv

with open("data.csv", "r", newline="", encoding="utf-8") as f:
    reader = csv.reader(f)
    for row in reader:
        print(row)
```

---

### File Positioning: `seek()` and `tell()`

The file object maintains a **current position** (a byte offset from the start). Read and write operations advance this position:

```python
with open("data.txt", "r", encoding="utf-8") as f:
    print(f.tell())        # 0 — at the beginning
    chunk = f.read(5)      # Read 5 characters
    print(f.tell())        # 5 — position advanced
    f.seek(0)              # Return to the beginning
    again = f.read(5)      # Read the same 5 characters again
    print(chunk == again)  # True
```

| Method | Description |
|--------|-------------|
| `f.tell()` | Returns current position as a byte offset integer |
| `f.seek(offset)` | Move to byte `offset` from the start of the file |
| `f.seek(offset, whence)` | `whence=0` (start), `1` (current), `2` (end) |

> **Note:** In text mode, `seek()` only reliably works with values returned by `tell()` or `0`. Arbitrary byte offsets may land in the middle of a multi-byte character. Use binary mode for arbitrary seeking.

---

### `pathlib.Path` — Cross-Platform Paths

Always use `pathlib.Path` instead of raw strings for file paths. It works correctly on Windows (`\`), macOS (`/`), and Linux (`/`):

```python
from pathlib import Path

# Build paths safely — no string concatenation
base  = Path("data")
input_file  = base / "input" / "users.csv"
output_file = base / "output" / "report.txt"

# Useful Path methods
print(input_file.exists())          # True / False
print(input_file.suffix)            # ".csv"
print(input_file.stem)              # "users"
print(input_file.parent)            # PosixPath('data/input')
print(input_file.name)              # "users.csv"

# Create all parent directories if they don't exist
output_file.parent.mkdir(parents=True, exist_ok=True)

# Pass directly to open()
with open(input_file, encoding="utf-8") as f:
    content = f.read()

# Shortcut: Path.read_text / write_text for small files
text = output_file.read_text(encoding="utf-8")
output_file.write_text("result", encoding="utf-8")
```

#### Listing and Globbing

```python
from pathlib import Path

# List all files in a directory
for file in Path("logs").iterdir():
    print(file)

# Find all CSV files recursively
for csv_file in Path("data").rglob("*.csv"):
    print(csv_file)

# Find all test result JSON files
results = list(Path("results").glob("test_*.json"))
```

---

### Best Practices Summary

1. **Always use `with`** — guarantees `close()` even if exceptions occur.
2. **Always set `encoding='utf-8'`** for text files — removes platform-dependent behaviour.
3. **Use `pathlib.Path`** for path construction — safe on all operating systems.
4. **Choose the right mode** — `'x'` when you must not overwrite; `'a'` for logs; `'rb'`/`'wb'` for binary.
5. **For huge files, don't `read()` all at once** — iterate line by line (covered in the next topic).
6. **Handle `FileNotFoundError` and `PermissionError`** — especially when paths come from user input or config.

```python
from pathlib import Path

path = Path("output") / "report.txt"
path.parent.mkdir(parents=True, exist_ok=True)

# Write
with open(path, "w", encoding="utf-8") as f:
    f.write("status: ok\n")

# Read back
with open(path, "r", encoding="utf-8") as f:
    print(f.read())

# Safe open with error handling
try:
    with open(Path("config") / "settings.json", encoding="utf-8") as f:
        config = f.read()
except FileNotFoundError:
    config = "{}"   # Fall back to empty config
except PermissionError as e:
    raise RuntimeError(f"Cannot read config file: {e}") from e
```

---

## Summary

- `open()` returns a **file object** — a high-level wrapper around an OS file handle.
- Choose **mode** (`r/w/a/x/b/+`) based on your intent: read, overwrite, append, exclusive-create, binary, or combined.
- **Text mode** decodes bytes → `str` and translates newlines; **binary mode** passes raw `bytes` unchanged.
- **Always set `encoding='utf-8'`** for text files to avoid platform-specific failures.
- Use **`pathlib.Path`** for safe, cross-platform path construction, globbing, and directory creation.
- Use **`with open(...) as f:`** to guarantee the file is closed, regardless of errors.

---

## Additional Resources

- [Python Docs — `open()`](https://docs.python.org/3/library/functions.html#open)
- [Python Docs — Reading and Writing Files (Tutorial)](https://docs.python.org/3/tutorial/inputoutput.html#reading-and-writing-files)
- [Python Docs — pathlib](https://docs.python.org/3/library/pathlib.html)
- [Python Docs — io module](https://docs.python.org/3/library/io.html)
