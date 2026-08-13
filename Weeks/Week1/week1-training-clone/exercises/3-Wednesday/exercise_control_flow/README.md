# Exercise: Control Flow — Conditionals, Loops, and Classic Problems

**Mode:** Implementation (Code Lab)
**Duration:** 60–90 minutes
**Day:** Wednesday | **Week:** 1 — Git & Python Fundamentals

---

## Objective

By the end of this exercise, you will be able to:
- Write conditional logic with `if/elif/else` and ternary expressions.
- Use `for` and `while` loops with `break`, `continue`, and `else`.
- Apply `enumerate()` and `range()` effectively.
- Solve classic programming problems using control flow.

---

## Prerequisites

| Concept | Source |
|---------|--------|
| If-Else | `written/3-Wednesday/if-else.md` |
| For & While Loops | `written/3-Wednesday/for-while-loops.md` |
| Variable Scopes | `written/3-Wednesday/variable-scopes.md` |
| Instructor Demo | `demos/3-Wednesday/INSTRUCTOR_GUIDE.md` (Demo 1) |

---

## Core Tasks

### Task 1: Input Validator (15 min)

Create `starter_code/input_validator.py`:

Write a function `validate_password(password)` that checks:
- At least 8 characters long
- Contains at least one uppercase letter
- Contains at least one lowercase letter
- Contains at least one digit
- Contains at least one special character (`!@#$%^&*`)

Return a dict with:
```python
{
    "valid": True/False,
    "errors": ["list of failure reasons"]
}
```

**Test cases:**
```python
validate_password("Abc123!x")    # valid
validate_password("abc")         # too short, no upper, no digit, no special
validate_password("ABCDEFGH")    # no lower, no digit, no special
validate_password("ABCDefgh1!")  # valid
```

---

### Task 2: FizzBuzz Extended (15 min)

Create `starter_code/fizzbuzz.py`:

Implement `fizzbuzz(n)` that prints numbers 1 through `n` with these rules:
- Divisible by 3: print "Fizz"
- Divisible by 5: print "Buzz"
- Divisible by 7: print "Boom"
- Divisible by 3 AND 5: print "FizzBuzz"
- Divisible by 3 AND 7: print "FizzBoom"
- Divisible by 5 AND 7: print "BuzzBoom"
- Divisible by 3 AND 5 AND 7: print "FizzBuzzBoom"
- Otherwise: print the number

Run for `n=105` and verify that 105 prints "FizzBuzzBoom".

---

### Task 3: Number Guessing Game (20 min)

Create `starter_code/guessing_game.py`:

1. Generate a random number between 1 and 100.
2. Give the user 7 attempts to guess it.
3. After each guess, tell them "Too high!" or "Too low!".
4. Track the number of attempts used.
5. If they guess correctly: print a congratulations message with attempts used.
6. If they use all 7 attempts: reveal the answer.
7. Use the `while` loop with `else` clause.

**Hint:** `import random; answer = random.randint(1, 100)`

---

### Task 4: Grade Processor (20 min)

Create `starter_code/grade_processor.py`:

Given a list of student scores, write a program that:

1. Uses a `for` loop with `enumerate` to process each score.
2. Assigns letter grades using `if/elif/else`.
3. Uses `continue` to skip any negative scores (invalid data).
4. Uses `break` if a score of `-999` is encountered (sentinel value — stop processing).
5. At the end, prints:
   - Each student's grade
   - Class average (excluding invalid scores)
   - Highest and lowest grades
   - Distribution count (how many A's, B's, C's, D's, F's)

**Input data:**
```python
scores = [88, 92, 75, -1, 63, 95, 81, 70, -5, 55, 100, 78, -999, 90, 85]
```

**Expected behavior:** Process through index 11 (78), skip negatives, stop at -999.

---

## Stretch Goals

- [ ] In the guessing game, implement a binary search strategy hint system.
- [ ] Add a "difficulty" mode to FizzBuzz where rules can be customized.
- [ ] Make the grade processor read scores from a file instead of a hardcoded list.

---

## Definition of Done

- [ ] `input_validator.py` correctly validates all test cases.
- [ ] `fizzbuzz.py` handles all 7 divisibility combinations.
- [ ] `guessing_game.py` is playable and tracks attempts.
- [ ] `grade_processor.py` correctly handles `continue`, `break`, and the else clause.
- [ ] All scripts run without errors.
