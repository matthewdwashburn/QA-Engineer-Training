# User Input and Output

## Learning Objectives
- Use `input()` to capture user input from the command line.
- Use `print()` with various formatting techniques.
- Master f-strings (formatted string literals) for clean, readable output.

---

## Why This Matters

> **Weekly Epic Connection:** Every interactive script — from a test data generator to a CLI tool that configures test environments — needs to communicate with its user. `input()` and `print()` are Python's most basic I/O tools, and f-strings make your output clean and professional.

---

## The Concept

### Output with `print()`

`print()` sends text to the console (standard output):

```python
print("Hello, World!")                    # Basic string
print("Score:", 95)                       # Multiple arguments (separated by space)
print("A", "B", "C", sep="-")           # Custom separator → A-B-C
print("Loading", end="...")              # Custom ending (default is newline)
print()                                   # Empty line
```

**`print()` parameters:**

| Parameter | Default | Purpose |
|-----------|---------|---------|
| `*objects` | — | Values to print (any number) |
| `sep` | `" "` | Separator between values |
| `end` | `"\n"` | String appended after the last value |
| `file` | `sys.stdout` | Output destination |

### Input with `input()`

`input()` pauses the program, displays an optional prompt, and waits for the user to type something:

```python
name = input("What is your name? ")
print(f"Hello, {name}!")
```

Terminal session:
```
What is your name? Alice
Hello, Alice!
```

**Critical detail:** `input()` **always returns a string**, regardless of what the user types:

```python
age = input("Enter your age: ")    # User types: 25
print(type(age))                    # <class 'str'> — NOT int!
print(age + 1)                      # ❌ TypeError!

# Convert to int for numeric operations
age = int(input("Enter your age: "))
print(age + 1)                      # ✅ 26
```

### Type Conversion for Input

```python
# String to integer
count = int(input("How many tests? "))

# String to float
price = float(input("Enter price: "))

# Handle invalid input
try:
    number = int(input("Enter a number: "))
except ValueError:
    print("That's not a valid number!")
```

---

## String Formatting Methods

Python offers three ways to format strings. **f-strings are the modern standard.**

### 1. f-strings (Formatted String Literals) — The Best Way

Introduced in Python 3.6. Prefix the string with `f` and embed expressions in `{}`:

```python
name = "Alice"
score = 95.5
tests_passed = 47
total_tests = 50

# Basic variable insertion
print(f"Hello, {name}!")

# Expressions inside braces
print(f"Pass rate: {tests_passed / total_tests:.1%}")  # 94.0%

# Method calls inside braces
print(f"Name: {name.upper()}")  # Name: ALICE

# Arithmetic inside braces
print(f"Remaining: {total_tests - tests_passed} tests")
```

**f-string format specifications:**

```python
value = 42.567

# Decimal places
f"{value:.2f}"      # "42.57"

# Width and alignment
f"{name:>20}"       # "               Alice" (right-aligned, 20 chars)
f"{name:<20}"       # "Alice               " (left-aligned)
f"{name:^20}"       # "       Alice        " (centered)

# Thousands separator
f"{1000000:,}"      # "1,000,000"

# Percentage
f"{0.856:.1%}"      # "85.6%"

# Padding with zeros
f"{42:05d}"         # "00042"
```

**Multi-line f-strings:**
```python
report = f"""
Test Report
===========
Name:   {name}
Score:  {score:.1f}
Status: {"PASS" if score >= 70 else "FAIL"}
"""
print(report)
```

### 2. `.format()` Method — The Older Way

```python
print("Hello, {}!".format(name))
print("Score: {:.1f}%".format(score))
print("{0} scored {1}".format(name, score))  # Positional
print("{name} scored {score}".format(name="Alice", score=95))  # Named
```

`.format()` is still valid but f-strings are more readable and preferred.

### 3. %-formatting — The Legacy Way

```python
print("Hello, %s!" % name)
print("Score: %.1f%%" % score)
```

**Don't use this in new code.** It's Python 2-era syntax kept for backward compatibility.

---

## Practical Examples

### Example 1: Simple Calculator

```python
num1 = float(input("Enter first number: "))
operator = input("Enter operator (+, -, *, /): ")
num2 = float(input("Enter second number: "))

if operator == "+":
    result = num1 + num2
elif operator == "-":
    result = num1 - num2
elif operator == "*":
    result = num1 * num2
elif operator == "/":
    result = num1 / num2 if num2 != 0 else "Error: Division by zero"
else:
    result = "Invalid operator"

print(f"{num1} {operator} {num2} = {result}")
```

### Example 2: Formatted Test Report

```python
test_name = "Login API Test"
status = "PASS"
duration = 1.4567
endpoint = "/api/v2/auth/login"

# Formatted report line
print(f"{'Test:':<12} {test_name}")
print(f"{'Status:':<12} {status}")
print(f"{'Duration:':<12} {duration:.2f}s")
print(f"{'Endpoint:':<12} {endpoint}")
print(f"{'-' * 40}")
```

Output:
```
Test:        Login API Test
Status:      PASS
Duration:    1.46s
Endpoint:    /api/v2/auth/login
----------------------------------------
```

### Example 3: Escape Characters

```python
# Common escape characters
print("Line 1\nLine 2")         # \n = newline
print("Column1\tColumn2")       # \t = tab
print("She said \"hello\"")     # \" = literal quote
print("Path: C:\\Users\\Alice")  # \\ = literal backslash

# Raw strings (ignore escape characters)
print(r"C:\Users\Alice\new_folder")  # r-prefix: backslashes are literal
```

---

## Summary

- `input()` captures user input as a **string** — convert with `int()` or `float()` for numbers.
- `print()` outputs to console — customize with `sep`, `end`, and `file` parameters.
- **f-strings** (`f"..."`) are the modern, preferred way to format strings — use them everywhere.
- Format specs inside f-strings control decimal places (`.2f`), alignment (`<`, `>`, `^`), and more.
- Use escape characters (`\n`, `\t`, `\\`) or raw strings (`r"..."`) for special characters.

---

## Additional Resources
- [Python Docs — Input and Output](https://docs.python.org/3/tutorial/inputoutput.html)
- [Real Python — Python f-Strings](https://realpython.com/python-f-strings/)
- [PEP 498 — Literal String Interpolation](https://peps.python.org/pep-0498/)
