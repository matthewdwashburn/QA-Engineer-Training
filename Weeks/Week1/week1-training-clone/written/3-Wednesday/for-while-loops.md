# For and While Loops

## Learning Objectives
- Write `for` loops to iterate over sequences and ranges.
- Write `while` loops for condition-based repetition.
- Use `break`, `continue`, `range()`, and the `else` clause on loops.

---

## Why This Matters

> **Weekly Epic Connection:** Loops power repetitive tasks — iterating through test cases, processing data files line by line, retrying operations, and polling for conditions. They're everywhere in automation code.

---

## The Concept

### The `for` Loop

Iterates over each item in a sequence (list, string, range, etc.):

```python
# Iterate over a list
fruits = ["apple", "banana", "cherry"]
for fruit in fruits:
    print(fruit)

# Iterate over a string
for char in "Python":
    print(char, end=" ")  # P y t h o n

# Iterate over a dictionary
config = {"host": "localhost", "port": 8080}
for key, value in config.items():
    print(f"{key}: {value}")
```

### The `range()` Function

Generates a sequence of numbers:

```python
# range(stop) — 0 to stop-1
for i in range(5):
    print(i)  # 0, 1, 2, 3, 4

# range(start, stop)
for i in range(2, 6):
    print(i)  # 2, 3, 4, 5

# range(start, stop, step)
for i in range(0, 20, 5):
    print(i)  # 0, 5, 10, 15

# Counting down
for i in range(5, 0, -1):
    print(i)  # 5, 4, 3, 2, 1
```

### `enumerate()` — Index + Value

When you need both the index and the value:

```python
tests = ["login", "search", "checkout"]
for index, test_name in enumerate(tests):
    print(f"Test {index + 1}: {test_name}")
# Test 1: login
# Test 2: search
# Test 3: checkout

# Start from a different index
for i, test in enumerate(tests, start=1):
    print(f"Test {i}: {test}")
```

### The `while` Loop

Repeats as long as a condition is `True`:

```python
count = 0
while count < 5:
    print(f"Count: {count}")
    count += 1
```

**Common use cases:**

```python
# Retry pattern
max_retries = 3
attempts = 0
success = False

while attempts < max_retries and not success:
    attempts += 1
    print(f"Attempt {attempts}...")
    # success = try_operation()
    success = attempts == 3  # Simulated success on 3rd try

if success:
    print("Operation succeeded!")
else:
    print("All retries exhausted.")
```

**⚠️ Infinite loop warning:**

```python
# This runs forever — missing the increment!
# while True:
#     print("stuck!")

# Correct infinite loop with exit condition
while True:
    user_input = input("Enter 'quit' to exit: ")
    if user_input == "quit":
        break
```

### `break` — Exit the Loop Immediately

```python
# Find the first negative number
numbers = [4, 7, -2, 9, -5, 3]
for num in numbers:
    if num < 0:
        print(f"First negative: {num}")
        break
```

### `continue` — Skip to the Next Iteration

```python
# Process only positive numbers
numbers = [4, -2, 7, -5, 3, 0]
for num in numbers:
    if num <= 0:
        continue  # Skip non-positive numbers
    print(f"Processing: {num}")
# Processing: 4
# Processing: 7
# Processing: 3
```

### `else` Clause on Loops

A loop's `else` block runs if the loop completes **without** hitting `break`:

```python
# Search for a value
target = 42
numbers = [10, 20, 30, 40, 50]

for num in numbers:
    if num == target:
        print(f"Found {target}!")
        break
else:
    print(f"{target} not found in the list.")
# Output: 42 not found in the list.
```

### Nested Loops

```python
# Multiplication table
for i in range(1, 4):
    for j in range(1, 4):
        print(f"{i} × {j} = {i * j:2}", end="  ")
    print()  # New line after each row

# 1 × 1 =  1  1 × 2 =  2  1 × 3 =  3
# 2 × 1 =  2  2 × 2 =  4  2 × 3 =  6
# 3 × 1 =  3  3 × 2 =  6  3 × 3 =  9
```

### `for` vs. `while` — When to Use Which

| Use `for` when... | Use `while` when... |
|---|---|
| You know how many iterations | You don't know how many iterations |
| You're iterating over a collection | You're waiting for a condition to change |
| You're processing each item in a sequence | You're implementing retry logic |
| Most of the time — it's the default choice | The loop depends on external input |

---

## Summary

- **`for`** loops iterate over sequences — combine with `range()`, `enumerate()`.
- **`while`** loops repeat until a condition is false — watch for infinite loops.
- **`break`** exits the loop; **`continue`** skips to the next iteration.
- The **`else`** clause runs if the loop finishes without `break`.
- Prefer `for` over `while` when possible — it's more Pythonic and less error-prone.

---

## Additional Resources
- [Python Docs — for Statements](https://docs.python.org/3/tutorial/controlflow.html#for-statements)
- [Python Docs — range()](https://docs.python.org/3/library/stdtypes.html#range)
- [Real Python — Python for Loop](https://realpython.com/python-for-loop/)
