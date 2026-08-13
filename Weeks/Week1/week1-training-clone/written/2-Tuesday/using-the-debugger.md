# Using the Debugger

## Learning Objectives
- Explain what a debugger is and why it's superior to `print()` debugging.
- Use Python's built-in debugger (`pdb`) to set breakpoints, step through code, and inspect variables.
- Apply debugging techniques to trace and fix bugs efficiently.

---

## Why This Matters

> **Weekly Epic Connection:** As a quality engineer, debugging is literally your job. When a test fails, you need to understand *why*. The debugger lets you pause execution at any point, inspect every variable, and step through code line-by-line. It's the difference between guessing and knowing.

---

## The Concept

### Why Not Just `print()`?

```python
# The "print debugging" approach
def calculate_discount(price, discount_pct):
    print(f"DEBUG: price={price}")           # Added for debugging
    print(f"DEBUG: discount_pct={discount_pct}")  # Added for debugging
    discount = price * discount_pct
    print(f"DEBUG: discount={discount}")      # Added for debugging
    final = price - discount
    print(f"DEBUG: final={final}")            # Added for debugging
    return final
```

Problems with print debugging:
- You have to **add and remove** print statements manually.
- You can't **interact** — you see values but can't explore further.
- You can't **pause** execution to think or test alternatives.
- In complex code, print output becomes an unreadable wall of text.

The debugger solves all of these problems.

### Python's Built-in Debugger: `pdb`

`pdb` (Python Debugger) is included in the standard library. No installation needed.

#### Setting a Breakpoint

A **breakpoint** is a marker that tells the debugger "pause here":

```python
# Method 1: Built-in breakpoint() function (Python 3.7+, RECOMMENDED)
def calculate_discount(price, discount_pct):
    discount = price * discount_pct
    breakpoint()  # Execution pauses HERE
    final = price - discount
    return final

# Method 2: Import pdb explicitly
import pdb

def calculate_discount(price, discount_pct):
    discount = price * discount_pct
    pdb.set_trace()  # Older style, same effect
    final = price - discount
    return final
```

When Python hits `breakpoint()`, it drops you into an interactive debugger prompt:

```
> c:\project\calc.py(4)calculate_discount()
-> final = price - discount
(Pdb)
```

### Essential Debugger Commands

At the `(Pdb)` prompt, you can type commands:

| Command | Short | Action |
|---------|-------|--------|
| `next` | `n` | Execute the current line, move to the next |
| `step` | `s` | Step INTO a function call |
| `continue` | `c` | Run until the next breakpoint (or end) |
| `print(expr)` | `p expr` | Print the value of an expression |
| `pp expr` | — | Pretty-print (for complex data structures) |
| `list` | `l` | Show the current code context |
| `where` | `w` | Show the call stack |
| `up` | `u` | Move up one frame in the call stack |
| `down` | `d` | Move down one frame in the call stack |
| `quit` | `q` | Exit the debugger and stop the program |

### Walkthrough Example

```python
# buggy_code.py
def process_scores(scores):
    total = 0
    for score in scores:
        total += score
    breakpoint()  # Let's inspect here
    average = total / len(scores)
    return average

result = process_scores([85, 92, 78, 0, 95])
print(f"Average: {result}")
```

Running this:
```
> buggy_code.py(6)process_scores()
-> average = total / len(scores)
(Pdb) p total
350
(Pdb) p scores
[85, 92, 78, 0, 95]
(Pdb) p len(scores)
5
(Pdb) p total / len(scores)
70.0
(Pdb) n
> buggy_code.py(7)process_scores()
-> return average
(Pdb) p average
70.0
(Pdb) c
Average: 70.0
```

### `next` vs. `step`

```python
def helper():
    return 42

def main():
    breakpoint()
    result = helper()  # ← You're here
    print(result)
```

- **`n` (next):** Executes `helper()` as a single step and moves to `print(result)`. You don't see inside `helper()`.
- **`s` (step):** Steps *into* `helper()`, letting you debug line-by-line inside it.

### Conditional Exploration

At the `(Pdb)` prompt, you can run any Python expression:

```
(Pdb) [s for s in scores if s < 80]
[78, 0]
(Pdb) max(scores) - min(scores)
95
(Pdb) type(total)
<class 'int'>
```

### IDE Debugger (VS Code)

While `pdb` works in the terminal, **VS Code's visual debugger** is much more user-friendly:

1. Click in the **gutter** (left of line numbers) to set a red dot breakpoint.
2. Press **F5** (or Run → Start Debugging).
3. Use the debug toolbar to **Step Over (F10)**, **Step Into (F11)**, **Continue (F5)**.
4. The **Variables** pane shows all current variables automatically.
5. The **Watch** pane lets you track specific expressions.
6. The **Call Stack** pane shows the function call chain.

For everyday debugging, the VS Code debugger is recommended. Use `pdb` when you need to debug in terminal-only environments (remote servers, CI/CD pipelines).

---

## Summary

- The **debugger** lets you pause, inspect, and step through code — far superior to `print()`.
- Use `breakpoint()` (Python 3.7+) to set breakpoints in your code.
- **Key commands:** `n` (next), `s` (step into), `c` (continue), `p` (print), `l` (list code).
- **VS Code's visual debugger** provides the same functionality with a graphical interface.
- Master the debugger early — it's the single most valuable debugging tool you'll ever use.

---

## Additional Resources
- [Python Docs — pdb: The Python Debugger](https://docs.python.org/3/library/pdb.html)
- [Real Python — Python Debugging with pdb](https://realpython.com/python-debugging-pdb/)
- [VS Code — Python Debugging](https://code.visualstudio.com/docs/python/debugging)
