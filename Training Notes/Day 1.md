# Day 1 - Git & Python Basics

---

## Bash

```bash
ls -a          # list all including hidden
ls -la         # list all with details
```
`Ctrl+L` to move to the top of bash

---

## Git

```bash
git status
git log                          # see what has been done in the working tree
git log --oneline --graph --all  # compact visual branch history
git diff                         # what changed
git switch -c <branch-name>      # switch to and create a branch if it doesn't exist
git switch main
git merge <branch-name>          # merge branch into working branch
git restore --staged .           # restore what was staged to unstage again
```

**Resolve conflict in the merge editor in VS Code**

### Fix: Git Push Rejected (non-fast-forward)

**When this happens:** Your push is rejected because the remote has commits your local branch doesn't have.

**Step-by-step fix:**

```bash
# 1. Stash your local changes so they don't block the pull
git stash

# 2. Pull the remote changes and replay your work on top
git pull --rebase origin main

# 3. Restore your stashed changes
git stash pop

# 4. Stage, commit, and push
git add .
git commit -m "your commit message"
git push
```

**Why this works:**

- `git stash` temporarily saves your uncommitted changes
- `git pull --rebase` fetches the remote commits and puts your changes _after_ them (instead of creating a messy merge commit)
- `git stash pop` brings your changes back
- Then you commit and push as normal

**If `git stash pop` causes conflicts**, Git will mark the conflicting files. Open them, look for the `<<<<<<<` markers, resolve manually, then `git add .` and continue.

---

## Python Virtual Environment

```bash
python3 -m venv ./venv    # create virtual environment
source venv/bin/activate  # activate it
python --version
pip install numpy
deactivate
```

---

## Control Flow

```python
if pass_rate >= 0.95:
    print("RELEASE APPROVED")
elif pass_rate >= 0.80:
    print("CONDITIONAL RELEASE")
else:
    print("RELEASE BLOCKED")
```

**Ternary expression** — one-line if/else:
```python
emoji = "✅" if status == "PASS" else "❌"
```

---

## Loops

```python
for x in range(5):          # 0, 1, 2, 3, 4  (stop is exclusive)
    print(names[x])

for x in range(0, 10, 2):   # start, stop, step → 0, 2, 4, 6, 8
    ...
```

**Parallel lists** — multiple lists indexed together:
```python
names     = ["Alice", "Bob", "Trey"]
durations = [1200, 850, 2300]
statuses  = ["PASS", "PASS", "FAIL"]

for x in range(len(names)):
    print(names[x], durations[x], statuses[x])
```

---

## Strings

**String multiplication** — repeat a string `n` times:
```python
"─" * 18          # "──────────────────"
"QA " * 3         # "QA QA QA "
```

**f-string alignment** (useful for table formatting):
```python
f"{'Test Name':<16}"   # left-align in 16 chars
f"{duration:>10}"      # right-align in 10 chars
f"{value:.1%}"         # percentage:  0.95 → "95.0%"
f"{value:.2f}s"        # 2 decimals:  1.2  → "1.20s"
f"{value:,} ms"        # thousands:   1200 → "1,200 ms"
```

---

## `sys` Module

```python
import sys
sys.version    # current Python version string
```

---

## User Input & Type Casting

`input()` always returns a string — cast immediately when you need a number:
```python
test_cases    = int(input("Test Cases: "))
execution_time = float(input("Execution Time: "))
```

---

## Useful Built-ins

| Function | What it does |
|---|---|
| `range(stop)` / `range(start, stop, step)` | integer sequence, stop is exclusive |
| `len(x)` | length |
| `sum(iterable)` | sum of all values |
| `list.count(value)` | count occurrences of value in list |
| `type(x).__name__` | get the type name as a plain string (`"int"`, `"str"`, etc.) |
| `int(x)` / `float(x)` / `str(x)` | type casting |
| `input(prompt)` | reads a string from stdin |
