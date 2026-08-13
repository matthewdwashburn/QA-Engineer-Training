# Installing Python and the REPL

## Learning Objectives
- Install Python 3 on your operating system (Windows, macOS, or Linux).
- Verify the installation and understand the `python` vs. `python3` distinction.
- Use the REPL (Read-Eval-Print Loop) for quick experimentation and learning.

---

## Why This Matters

> **Weekly Epic Connection:** Before you can write your first test script or automation tool, Python needs to be on your machine and working correctly. The REPL will be your constant companion throughout this training — a scratchpad for testing ideas, exploring syntax, and debugging small pieces of code.

Getting the installation right from the start saves hours of frustration later. This reading walks through the steps carefully, covers common pitfalls, and introduces the REPL — one of Python's most powerful learning tools.

---

## The Concept

### Installing Python

#### Windows

1. **Download** the installer from [python.org/downloads](https://www.python.org/downloads/).
   - Download the latest Python 3.12+ release.

2. **Run the installer:**
   - ⚠️ **CRITICAL:** Check the box that says **"Add python.exe to PATH"** at the bottom of the first screen. This is the most common installation mistake.
   - Select **"Install Now"** for the default configuration.

3. **Verify the installation** — open PowerShell or Command Prompt:

```powershell
python --version
# Output: Python 3.12.x

pip --version
# Output: pip 24.x from ...
```

> **Common pitfall:** If you see `'python' is not recognized`, the PATH wasn't configured. Either re-run the installer and check the PATH box, or manually add Python to your system PATH.

#### macOS

macOS comes with a system Python, but it's often outdated. Install the latest version:

**Option 1: Official installer**
```bash
# Download from python.org and run the .pkg
```

**Option 2: Homebrew (recommended for developers)**
```bash
brew install python@3.12
```

**Verify:**
```bash
python3 --version
# Output: Python 3.12.x
```

> **Note on macOS:** Use `python3` and `pip3` — the `python` command may point to the old system Python 2.

#### Linux (Ubuntu/Debian)

```bash
sudo apt update
sudo apt install python3 python3-pip python3-venv
python3 --version
```

### `python` vs. `python3`

Depending on your OS and installation method:

| Command | Typical Behavior |
|---------|-----------------|
| `python` | Windows: Python 3. macOS/Linux: might be Python 2 or missing. |
| `python3` | macOS/Linux: Python 3 (reliable). Windows: may not exist. |
| `py` | Windows only: Python Launcher — detects installed versions. |

**Best practice:** Use `python3` on macOS/Linux. Use `python` or `py` on Windows. When in doubt:

```bash
python --version    # Check which version this gives you
python3 --version   # Try this if the above is Python 2
```

### What Gets Installed

When you install Python, you get:

| Component | Purpose |
|-----------|---------|
| **python** (or python3) | The Python interpreter — runs your code. |
| **pip** | The package installer — downloads and installs third-party libraries. |
| **IDLE** | A basic IDE that ships with Python (you'll use VS Code instead). |
| **Standard Library** | Hundreds of built-in modules — no installation needed. |
| **venv module** | For creating virtual environments (covered in the next reading). |

### Your First Python Script

Let's verify everything works by creating and running a simple script:

1. **Create the file** — open any text editor and save as `hello.py`:

```python
# hello.py
print("Hello from Python!")
print(f"Running Python version: {__import__('sys').version}")
```

2. **Run it:**

```bash
python hello.py
```

Output:
```
Hello from Python!
Running Python version: 3.12.x (main, ...) [MSC v.19xx 64 bit (AMD64)]
```

If you see this output — congratulations, Python is installed and working correctly.

---

## The REPL (Read-Eval-Print Loop)

### What Is the REPL?

The **REPL** is Python's interactive mode. It:
1. **Reads** a line of code you type.
2. **Evaluates** (executes) the code.
3. **Prints** the result.
4. **Loops** back, ready for the next input.

### Starting the REPL

```bash
python
# or
python3
```

You'll see the Python prompt:

```
Python 3.12.x (main, ...) [...]
Type "help", "copyright", "credits" or "license" for more information.
>>>
```

The `>>>` is the REPL prompt — it's waiting for your code.

### Using the REPL

#### Basic calculations

```python
>>> 2 + 3
5
>>> 10 / 3
3.3333333333333335
>>> 2 ** 10
1024
>>> 100 // 7
14
```

Notice: the REPL automatically prints the result of each expression. No `print()` needed.

#### Working with strings

```python
>>> "hello" + " " + "world"
'hello world'
>>> "python".upper()
'PYTHON'
>>> len("quality engineering")
19
```

#### Checking types

```python
>>> type(42)
<class 'int'>
>>> type(3.14)
<class 'float'>
>>> type("hello")
<class 'str'>
>>> type(True)
<class 'bool'>
```

#### Multi-line code

The REPL supports multi-line input. When you type an incomplete statement (like a loop or condition), it shows `...` to indicate continuation:

```python
>>> for i in range(3):
...     print(f"Iteration {i}")
...
Iteration 0
Iteration 1
Iteration 2
```

Press Enter on an empty `...` line to execute the block.

#### Getting help

```python
>>> help(len)
Help on built-in function len in module builtins:
len(obj, /)
    Return the number of items in a container.

>>> help(str.upper)
Help on method_descriptor:
upper(self, /)
    Return a copy of the string converted to uppercase.
```

The `help()` function is like having the documentation right inside the interpreter.

#### Special variable: `_`

The REPL stores the last result in a special variable called `_`:

```python
>>> 5 + 3
8
>>> _ * 2
16
>>> _ + 100
116
```

### REPL vs. Script Files

| Feature | REPL | Script File (.py) |
|---------|------|-------------------|
| **Use case** | Quick experiments, testing ideas | Real programs, automation |
| **Persistence** | Gone when you close the session | Saved permanently |
| **Auto-printing** | Expressions auto-print results | Must use `print()` explicitly |
| **Multi-line** | Awkward for long code | Natural |
| **Error recovery** | Re-type the line | Edit the file |

**Rule of thumb:** Use the REPL to *explore and learn*. Use script files to *build and save*.

### Exiting the REPL

```python
>>> exit()
# or
>>> quit()
# or press Ctrl+Z then Enter (Windows)
# or press Ctrl+D (macOS/Linux)
```

### Enhanced REPLs

The built-in REPL is functional, but there are enhanced alternatives:

| Tool | Key Feature |
|------|-------------|
| **IPython** | Syntax highlighting, auto-complete, magic commands (`%timeit`, `%history`). |
| **Jupyter Notebook** | Web-based, mix code with markdown, inline visualizations. |
| **Python 3.13+ REPL** | New built-in REPL with syntax highlighting and multi-line editing. |

For this training, the standard REPL is sufficient. You'll encounter IPython and Jupyter in later weeks when working with data analysis.

### Practical REPL Exercise

Try these in your REPL right now:

```python
# 1. Arithmetic
>>> 15 % 4          # What does % do?
>>> 3 ** 4          # What does ** do?

# 2. Strings
>>> "QA Engineer" * 3     # What happens?
>>> "hello"[0]            # What does this return?
>>> "hello"[-1]           # What about this?

# 3. Type experiments
>>> type(42) == type(42.0)       # True or False?
>>> isinstance(True, int)        # Surprised?

# 4. Built-in help
>>> help(print)
```

---

## Summary

- **Install Python 3.12+** from python.org — on Windows, always check "Add to PATH."
- **Verify** with `python --version` and `pip --version`.
- The **REPL** (`>>>`) is an interactive scratchpad: type code, get instant results.
- Use the REPL for exploration and learning; use `.py` files for real programs.
- Use `help()` and `type()` in the REPL to explore Python's built-in tools.
- `exit()` or `Ctrl+Z` (Windows) / `Ctrl+D` (Mac/Linux) to leave the REPL.

---

## Additional Resources
- [Python.org — Download Python](https://www.python.org/downloads/)
- [Real Python — Installing Python](https://realpython.com/installing-python/)
- [Python Docs — Using the Python Interpreter](https://docs.python.org/3/tutorial/interpreter.html)
