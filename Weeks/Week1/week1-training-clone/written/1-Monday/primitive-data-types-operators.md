# Primitive Data Types and Operators

## Learning Objectives
- Identify Python's primitive data types: `int`, `float`, `str`, and `bool`.
- Use arithmetic, comparison, and logical operators.
- Understand type coercion rules and common pitfalls.

---

## Why This Matters

> **Weekly Epic Connection:** Data types and operators are the atoms and molecules of every Python program. Every test assertion, every data validation check, every calculation in your automation code uses these building blocks. Understanding them deeply — including the edge cases — prevents subtle bugs in your test code.

Whether you're checking that an API response code equals `200`, verifying that a price calculation rounds correctly, or asserting that a username contains valid characters — you're using data types and operators.

---

## The Concept

### Python's Primitive Data Types

Python has four fundamental (primitive) data types:

### 1. Integers (`int`)

Whole numbers — positive, negative, or zero. No decimal point.

```python
age = 28
temperature = -15
count = 0
population = 8_000_000_000  # Underscores improve readability (Python 3.6+)

print(type(age))  # <class 'int'>
```

**Key facts about Python integers:**
- **Unlimited precision** — Python ints can be arbitrarily large. No overflow.
```python
>>> 2 ** 100
1267650600228229401496703205376  # No problem
```
- **No separate `long` type** — unlike Java or C, Python handles big numbers automatically.

### 2. Floating-Point Numbers (`float`)

Numbers with a decimal point. Used for fractional values.

```python
price = 19.99
pi = 3.14159
negative = -0.001
scientific = 2.5e6   # 2.5 × 10^6 = 2,500,000.0

print(type(price))  # <class 'float'>
```

**⚠️ Floating-point precision warning:**
```python
>>> 0.1 + 0.2
0.30000000000000004  # Not exactly 0.3!
```

This isn't a Python bug — it's how all computers represent decimal numbers in binary (IEEE 754 standard). This matters for financial calculations and test assertions:

```python
# DON'T do this
assert total == 0.3  # May fail due to floating-point precision!

# DO this instead
assert abs(total - 0.3) < 1e-9  # Check if "close enough"
```

### 3. Strings (`str`)

Sequences of characters. Defined with single quotes, double quotes, or triple quotes.

```python
name = "Alice"
message = 'Hello, World!'
multiline = """This string
spans multiple
lines."""
empty = ""

print(type(name))  # <class 'str'>
```

**Single vs. double quotes** — functionally identical. Choose one convention and stick with it:
```python
# Both are equivalent
greeting = "Hello"
greeting = 'Hello'

# Use the other type when your string contains quotes
sentence = "She said 'hello'"
html = '<div class="container">'
```

**Strings are immutable** — you cannot change a character in place:
```python
name = "Alice"
# name[0] = "B"  # ❌ TypeError: 'str' object does not support item assignment
name = "B" + name[1:]  # ✅ Create a NEW string: "Blice"
```

**Common string operations:**
```python
text = "quality engineering"

text.upper()         # "QUALITY ENGINEERING"
text.lower()         # "quality engineering"
text.title()         # "Quality Engineering"
text.strip()         # Remove leading/trailing whitespace
text.replace("quality", "software")  # "software engineering"
text.split(" ")      # ["quality", "engineering"]
text.startswith("q") # True
text.find("eng")     # 8 (index where "eng" starts)
len(text)            # 19
```

**String indexing and slicing:**
```python
word = "Python"
#       P  y  t  h  o  n
#       0  1  2  3  4  5   (positive index)
#      -6 -5 -4 -3 -2 -1   (negative index)

word[0]      # "P"
word[-1]     # "n"
word[0:3]    # "Pyt" (start inclusive, end exclusive)
word[2:]     # "thon"
word[:2]     # "Py"
word[::-1]   # "nohtyP" (reversed)
```

### 4. Booleans (`bool`)

`True` or `False`. Used for logical operations and control flow.

```python
is_active = True
has_errors = False

print(type(is_active))  # <class 'bool'>
```

**Fun fact:** In Python, `bool` is a subclass of `int`. `True` is `1` and `False` is `0`:
```python
>>> True + True
2
>>> False + 1
1
>>> isinstance(True, int)
True
```

**Truthy and Falsy values** — Python treats many values as "truthy" or "falsy" in boolean contexts:

| Falsy (evaluates to False) | Truthy (evaluates to True) |
|---------------------------|---------------------------|
| `False` | `True` |
| `0`, `0.0` | Any non-zero number |
| `""` (empty string) | Any non-empty string |
| `[]` (empty list) | Any non-empty list |
| `{}` (empty dict) | Any non-empty dict |
| `None` | Almost everything else |

```python
# These are all "falsy"
if not 0:         print("0 is falsy")
if not "":        print("empty string is falsy")
if not []:        print("empty list is falsy")
if not None:      print("None is falsy")
```

### The `None` Type

`None` is Python's null value — it means "no value" or "nothing."

```python
result = None
print(type(result))  # <class 'NoneType'>

# Common pattern: check if something has a value
if result is None:
    print("No result yet")
```

Always use `is None` (not `== None`) to check for None — this is a Python convention based on identity vs. equality.

---

## Operators

### Arithmetic Operators

```python
a = 15
b = 4

a + b    # 19    Addition
a - b    # 11    Subtraction
a * b    # 60    Multiplication
a / b    # 3.75  Division (always returns float!)
a // b   # 3     Floor division (integer result, rounds down)
a % b    # 3     Modulo (remainder of division)
a ** b   # 50625 Exponentiation (15 to the power of 4)
```

**Important:** `/` always returns a `float`, even when dividing evenly:
```python
>>> 10 / 2
5.0  # float, not int!

>>> 10 // 2
5    # Use // for integer division
```

**Negative floor division** rounds toward negative infinity:
```python
>>> -7 // 2
-4   # Not -3! Rounds DOWN (toward negative infinity)
```

### Comparison Operators

Comparisons return `True` or `False`:

```python
x = 10
y = 20

x == y    # False    Equal to
x != y    # True     Not equal to
x < y     # True     Less than
x > y     # False    Greater than
x <= y    # True     Less than or equal to
x >= y    # False    Greater than or equal to
```

**Chained comparisons** — Python supports a readable syntax:
```python
age = 25
# Instead of: age >= 18 and age <= 65
18 <= age <= 65   # True — Python allows this!
```

### Logical Operators

Combine boolean expressions:

```python
a = True
b = False

a and b    # False — Both must be True
a or b     # True  — At least one must be True
not a      # False — Inverts the value
```

**Short-circuit evaluation** — Python stops evaluating as soon as the result is determined:
```python
# Python doesn't evaluate the second condition if the first determines the result
result = False and expensive_function()   # expensive_function() never runs
result = True or expensive_function()     # expensive_function() never runs
```

**Operator precedence** (highest to lowest):

| Priority | Operator | Description |
|----------|----------|-------------|
| 1 | `**` | Exponentiation |
| 2 | `+x`, `-x`, `~x` | Unary plus, minus, bitwise NOT |
| 3 | `*`, `/`, `//`, `%` | Multiplication, division |
| 4 | `+`, `-` | Addition, subtraction |
| 5 | `<`, `<=`, `>`, `>=`, `!=`, `==` | Comparisons |
| 6 | `not` | Logical NOT |
| 7 | `and` | Logical AND |
| 8 | `or` | Logical OR |

When in doubt, **use parentheses** to make your intent explicit:
```python
# This is confusing
result = a + b * c > d and e or f

# This is clear
result = ((a + (b * c)) > d) and (e or f)
```

### Assignment Operators

Shorthand for modifying variables:

```python
x = 10

x += 5    # x = x + 5  → 15
x -= 3    # x = x - 3  → 12
x *= 2    # x = x * 2  → 24
x /= 4    # x = x / 4  → 6.0
x //= 2   # x = x // 2 → 3.0
x %= 2    # x = x % 2  → 1.0
x **= 3   # x = x ** 3 → 1.0
```

### Identity Operators: `is` vs. `==`

```python
a = [1, 2, 3]
b = [1, 2, 3]
c = a

a == b    # True — same VALUE (contents are equal)
a is b    # False — different OBJECTS (different memory locations)
a is c    # True — same OBJECT (c points to the same list as a)
```

- `==` checks **value equality** (do they look the same?).
- `is` checks **identity** (are they the same object in memory?).

**Rule:** Use `is` only for `None`, `True`, and `False`. Use `==` for everything else.

### Membership Operators: `in`

```python
"a" in "quality"          # True
3 in [1, 2, 3, 4]         # True
"key" in {"key": "value"} # True (checks dictionary keys)
```

---

## Code Example: Putting It All Together

```python
# A simple test data validator
test_name = "Login API Test"
response_code = 200
response_time = 1.45  # seconds
is_successful = response_code == 200
threshold = 2.0

# Generate a test report line
status = "PASS" if is_successful and response_time < threshold else "FAIL"
print(f"Test: {test_name}")
print(f"Status: {status}")
print(f"Response: {response_code} in {response_time}s")
print(f"Within threshold: {response_time < threshold}")

# Output:
# Test: Login API Test
# Status: PASS
# Response: 200 in 1.45s
# Within threshold: True
```

---

## Summary

- **`int`** — Unlimited-precision whole numbers.
- **`float`** — Decimal numbers (beware of floating-point precision).
- **`str`** — Immutable text sequences with rich built-in methods.
- **`bool`** — `True`/`False`, subclass of `int`.
- **`None`** — Python's null value; check with `is None`.
- **Arithmetic:** `+`, `-`, `*`, `/` (float), `//` (int), `%` (mod), `**` (power).
- **Comparison:** `==`, `!=`, `<`, `>`, `<=`, `>=` — return booleans.
- **Logical:** `and`, `or`, `not` — with short-circuit evaluation.
- **Identity vs. equality:** `is` checks object identity; `==` checks value.

---

## Additional Resources
- [Python Docs — Built-in Types](https://docs.python.org/3/library/stdtypes.html)
- [Real Python — Basic Data Types in Python](https://realpython.com/python-data-types/)
- [Python Docs — Numeric Types](https://docs.python.org/3/library/stdtypes.html#numeric-types-int-float-complex)
