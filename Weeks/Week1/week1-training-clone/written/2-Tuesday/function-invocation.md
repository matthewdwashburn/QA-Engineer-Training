# Function Invocation

## Learning Objectives
- Understand how Python calls functions and passes arguments.
- Explain the call stack and how function calls nest.
- Recognize common invocation patterns and pitfalls.

---

## Why This Matters

> **Weekly Epic Connection:** Testing involves calling functions with various inputs and verifying outputs. Understanding how Python passes arguments, manages the call stack, and resolves names ensures you can trace bugs, write effective tests, and understand error tracebacks.

---

## The Concept

### Calling a Function

When you call a function, Python:
1. Evaluates the arguments (left to right).
2. Creates a new local scope for the function.
3. Binds the arguments to the parameter names.
4. Executes the function body.
5. Returns the result to the caller.

```python
def greet(name, greeting="Hello"):
    return f"{greeting}, {name}!"

# Standard call
message = greet("Alice")           # "Hello, Alice!"

# With keyword argument
message = greet("Bob", greeting="Hey")  # "Hey, Bob!"

# Function call as an argument to another function
print(greet("Charlie"))            # Prints: Hello, Charlie!
```

### How Arguments Are Passed

Python uses **"pass by object reference"** (sometimes called "pass by assignment"):

- **Immutable objects** (int, str, tuple): The function gets a reference to the object. Since it can't be modified, it *appears* like pass-by-value.
- **Mutable objects** (list, dict, set): The function gets a reference to the *same* object. Modifications inside the function affect the original.

```python
# Immutable — original is NOT affected
def try_modify_number(x):
    x = x + 10
    print(f"Inside: {x}")

value = 5
try_modify_number(value)
print(f"Outside: {value}")  # Still 5

# Mutable — original IS affected
def add_item(my_list):
    my_list.append("new item")

items = ["a", "b"]
add_item(items)
print(items)  # ["a", "b", "new item"] — original was modified!
```

### The Call Stack

When functions call other functions, Python manages them using a **call stack**:

```python
def multiply(a, b):
    return a * b

def calculate_area(length, width):
    return multiply(length, width)

def print_area(length, width):
    area = calculate_area(length, width)
    print(f"Area: {area}")

print_area(5, 3)
```

The call stack during execution:

```
Stack (bottom to top):
┌─────────────────────────────┐
│ multiply(5, 3)              │  ← Currently executing
├─────────────────────────────┤
│ calculate_area(5, 3)        │  ← Waiting for multiply to return
├─────────────────────────────┤
│ print_area(5, 3)            │  ← Waiting for calculate_area to return
├─────────────────────────────┤
│ <module> (main script)      │  ← Where print_area was called
└─────────────────────────────┘
```

### Reading Tracebacks

When an error occurs, Python shows the call stack as a **traceback** — read it **bottom up**:

```python
def divide(a, b):
    return a / b

def calculate_ratio(x, y):
    return divide(x, y)

calculate_ratio(10, 0)
```

```
Traceback (most recent call last):
  File "main.py", line 7, in <module>
    calculate_ratio(10, 0)
  File "main.py", line 5, in calculate_ratio
    return divide(x, y)
  File "main.py", line 2, in divide
    return a / b
ZeroDivisionError: division by zero
```

Reading bottom-up: The error (`ZeroDivisionError`) happened in `divide`, which was called by `calculate_ratio`, which was called from the main script. This traceback is your debugging roadmap.

### Functions as First-Class Objects

In Python, functions are objects — you can assign them to variables, pass them as arguments, and return them:

```python
def shout(text):
    return text.upper()

def whisper(text):
    return text.lower()

# Assign a function to a variable
transform = shout
print(transform("hello"))  # "HELLO"

# Pass a function as an argument
def apply(func, text):
    return func(text)

print(apply(shout, "hello"))    # "HELLO"
print(apply(whisper, "HELLO"))  # "hello"
```

This concept will become very powerful when you learn about decorators on Friday.

### Nested Function Calls

You can chain function calls — the inner call resolves first:

```python
# Nested calls
result = int(input("Enter a number: "))
# 1. input() runs first, returns a string
# 2. int() converts that string to an integer

# Equivalent step-by-step
user_input = input("Enter a number: ")
result = int(user_input)
```

---

## Summary

- Python evaluates arguments left-to-right, creates a local scope, and executes the function body.
- Arguments are passed by **object reference** — mutable objects can be modified by the function.
- The **call stack** tracks nested function calls — read tracebacks bottom-up to debug.
- Functions are **first-class objects** — they can be assigned, passed, and returned like any value.

---

## Additional Resources
- [Python Docs — Calls](https://docs.python.org/3/reference/expressions.html#calls)
- [Real Python — The Call Stack](https://realpython.com/python-traceback/)
- [Python Docs — First-Class Functions](https://docs.python.org/3/faq/programming.html#how-do-i-write-a-function-with-output-parameters-call-by-reference)
