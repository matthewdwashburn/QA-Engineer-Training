# If-Else Statements

## Learning Objectives
- Write conditional logic using `if`, `elif`, and `else`.
- Use ternary (conditional) expressions for concise one-line conditions.
- Understand truthiness and falsiness in conditional contexts.

---

## Why This Matters

> **Weekly Epic Connection:** Conditional logic is the backbone of decision-making in code. Every test assertion, every validation check, every branching path in your automation scripts uses `if-else`. Mastering conditionals means your programs can respond intelligently to different situations.

---

## The Concept

### Basic `if` Statement

```python
temperature = 38

if temperature > 37:
    print("Fever detected!")
```

**Key syntax rules:**
- The condition ends with a colon (`:`).
- The body is **indented** (4 spaces by convention).
- No parentheses required around the condition (unlike Java/C).

### `if-else`

```python
score = 72

if score >= 70:
    print("PASS")
else:
    print("FAIL")
```

### `if-elif-else`

Chain multiple conditions — Python checks top to bottom, executing the first match:

```python
score = 85

if score >= 90:
    grade = "A"
elif score >= 80:
    grade = "B"
elif score >= 70:
    grade = "C"
elif score >= 60:
    grade = "D"
else:
    grade = "F"

print(f"Grade: {grade}")  # Grade: B
```

**Order matters!** Put the most specific conditions first:

```python
# ❌ BAD — The first condition catches everything >= 60
if score >= 60:
    grade = "D"     # A score of 95 gets "D"!
elif score >= 90:
    grade = "A"     # This never executes

# ✅ GOOD — Most restrictive first
if score >= 90:
    grade = "A"
elif score >= 80:
    grade = "B"
```

### Nested Conditionals

```python
age = 25
has_license = True

if age >= 18:
    if has_license:
        print("You can drive.")
    else:
        print("Get a license first.")
else:
    print("Too young to drive.")
```

**Tip:** Deep nesting reduces readability. You can often flatten it:

```python
# Flattened version — easier to read
if age < 18:
    print("Too young to drive.")
elif not has_license:
    print("Get a license first.")
else:
    print("You can drive.")
```

### Ternary (Conditional) Expression

A one-line `if-else` for simple conditions:

```python
# Syntax: value_if_true if condition else value_if_false

status = "PASS" if score >= 70 else "FAIL"

label = "even" if number % 2 == 0 else "odd"

greeting = f"Hello, {'admin' if is_admin else 'user'}!"
```

Use ternary expressions for simple assignments. For complex logic, use full `if-else` blocks.

### Truthiness and Falsiness

In `if` statements, Python evaluates the "truthiness" of the expression. You already learned the falsy values on Monday — here's how they work in practice:

```python
# These are all "falsy" — the if block won't execute
if 0:          ...   # False
if "":         ...   # False
if []:         ...   # False
if None:       ...   # False
if False:      ...   # False

# These are all "truthy"
if 42:         ...   # True
if "hello":    ...   # True
if [1, 2]:     ...   # True
```

**Practical pattern — checking for empty collections:**

```python
results = []

# ❌ Verbose
if len(results) == 0:
    print("No results")

# ✅ Pythonic — leverages falsiness
if not results:
    print("No results")
```

### Compound Conditions

```python
age = 25
income = 50000

# AND — both must be true
if age >= 18 and income >= 30000:
    print("Eligible for loan")

# OR — at least one must be true
if age < 18 or income < 20000:
    print("Not eligible")

# NOT — inverts the condition
if not is_blocked:
    print("Access granted")

# Combining
if (age >= 18 and income >= 30000) or is_vip:
    print("Approved")
```

### Common Patterns

```python
# Check if value is None
if result is None:
    print("No result")

# Check membership
if user_role in ["admin", "moderator"]:
    print("Has elevated privileges")

# Check string content
if "error" in log_message.lower():
    print("Error detected!")
```

---

## Summary

- `if`, `elif`, `else` for conditional branching — checked top to bottom.
- **Ternary:** `value_if_true if condition else value_if_false` for concise expressions.
- Python evaluates **truthiness** — empty collections, `0`, `""`, `None` are falsy.
- Use `and`, `or`, `not` for compound conditions.
- Avoid deep nesting — flatten with guard clauses or `elif`.

---

## Additional Resources
- [Python Docs — if Statements](https://docs.python.org/3/tutorial/controlflow.html#if-statements)
- [Real Python — Conditional Statements in Python](https://realpython.com/python-conditional-statements/)
- [PEP 308 — Conditional Expressions](https://peps.python.org/pep-0308/)
