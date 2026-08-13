# Exercise: Functional Programming & Decorators

**Mode:** Implementation (Code Lab)
**Duration:** 60–90 minutes
**Day:** Friday | **Week:** 1 — Git & Python Fundamentals

---

## Objective

By the end of this exercise, you will be able to:
- Process data using `map()`, `filter()`, and `reduce()`.
- Combine sequences with `zip()`.
- Write generator expressions for memory-efficient iteration.
- Build custom decorators with `@` syntax and `functools.wraps`.

---

## Prerequisites

| Concept | Source |
|---------|--------|
| Lambda Functions | `written/5-Friday/lambda-anonymous-functions.md` |
| map() | `written/5-Friday/map.md` |
| filter() | `written/5-Friday/filter.md` |
| reduce() | `written/5-Friday/reduce.md` |
| Zip | `written/5-Friday/zip.md` |
| Generator Expressions | `written/5-Friday/generator-expressions.md` |
| Decorators | `written/5-Friday/decorators.md` |
| Instructor Demo | `demos/5-Friday/INSTRUCTOR_GUIDE.md` (Demos 2 & 3) |

---

## The Scenario

You're building a **Test Data Processing Pipeline** using functional programming, then adding reusable decorators for timing, logging, and retry logic to your QA framework.

---

## Part A: Functional Programming (45 min)

### Task 1: Lambda & Sorting (10 min)

Create `starter_code/functional_pipeline.py`:

Given this dataset:
```python
test_results = [
    {"name": "test_login", "module": "auth", "duration_ms": 1200, "status": "pass"},
    {"name": "test_register", "module": "auth", "duration_ms": 2100, "status": "pass"},
    {"name": "test_logout", "module": "auth", "duration_ms": 300, "status": "pass"},
    {"name": "test_search", "module": "search", "duration_ms": 850, "status": "fail"},
    {"name": "test_filter", "module": "search", "duration_ms": 1800, "status": "fail"},
    {"name": "test_sort", "module": "search", "duration_ms": 670, "status": "pass"},
    {"name": "test_add_cart", "module": "checkout", "duration_ms": 2300, "status": "fail"},
    {"name": "test_payment", "module": "checkout", "duration_ms": 3100, "status": "pass"},
    {"name": "test_confirm", "module": "checkout", "duration_ms": 1900, "status": "pass"},
    {"name": "test_view_profile", "module": "profile", "duration_ms": 380, "status": "pass"},
    {"name": "test_edit_profile", "module": "profile", "duration_ms": 540, "status": "pass"},
    {"name": "test_settings", "module": "profile", "duration_ms": 420, "status": "fail"},
]
```

Use **lambda functions** to:
1. Sort by duration (ascending).
2. Sort by module name, then by duration within each module.
3. Sort by status ("fail" before "pass") then by name.

---

### Task 2: Map & Filter (15 min)

Using the same dataset, use `map()` and `filter()` to:

1. **Extract** just the test names → `["test_login", "test_register", ...]`
2. **Get failures** → all results where status is "fail"
3. **Slow tests** → results where duration > 1500ms
4. **Transform** → create a list of summary strings: `"✅ test_login (1200ms)"` or `"❌ test_search (850ms)"`
5. **Module names** → unique set of module names using `map` + `set`

---

### Task 3: Reduce (10 min)

Using `functools.reduce()`:

1. **Total duration** of all tests.
2. **Total failure time** (sum of durations for failed tests only).
3. **Longest test name** (by character count).
4. **Build a module summary dict:**
   ```python
   # Expected output:
   {"auth": 3, "search": 3, "checkout": 3, "profile": 3}
   ```

---

### Task 4: Zip (10 min)

1. Given two parallel lists, combine them:
   ```python
   endpoints = ["/login", "/search", "/checkout", "/profile"]
   expected_codes = [200, 200, 201, 200]
   actual_codes = [200, 500, 201, 403]
   ```
   Use `zip()` to compare expected vs. actual and print pass/fail for each.

2. **Unzip** the test_results into separate lists: `names, modules, durations, statuses`.

3. Create a **dict** mapping test names to durations using `zip`.

---

## Part B: Decorators (45 min)

### Task 5: Timer Decorator (15 min)

Create `starter_code/decorators.py`:

```python
import time
from functools import wraps

def timer(func):
    """Decorator that measures and prints execution time.

    Output format: "⏱️ {func_name} completed in {seconds:.4f}s"
    """
    pass  # TODO: Implement
```

**Test:**
```python
@timer
def slow_operation():
    time.sleep(0.5)
    return "done"

result = slow_operation()
# Should print: ⏱️ slow_operation completed in 0.50XXs
assert result == "done"
assert slow_operation.__name__ == "slow_operation"  # @wraps preserves metadata
```

---

### Task 6: Retry Decorator with Parameters (15 min)

```python
def retry(max_attempts=3, delay=0.5, exceptions=(Exception,)):
    """Parameterized decorator that retries on failure.

    Args:
        max_attempts: Maximum number of tries
        delay: Seconds between retries
        exceptions: Tuple of exception types to catch

    Prints progress: "⚠️ Attempt {n}/{max}: {error}. Retrying in {delay}s..."
    On final failure: "💥 {func_name} failed after {max} attempts"
    """
    pass  # TODO: Implement (3-layer nesting: factory → decorator → wrapper)
```

**Test:**
```python
attempt_count = 0

@retry(max_attempts=3, delay=0.1)
def flaky_function():
    global attempt_count
    attempt_count += 1
    if attempt_count < 3:
        raise ConnectionError("Server unavailable")
    return "success"

result = flaky_function()
assert result == "success"
```

---

### Task 7: Call Logger Decorator (10 min)

```python
def log_calls(func):
    """Decorator that logs function calls with arguments and return value.

    Output:
        "📞 Calling func_name(arg1, arg2, key=val)"
        "✅ func_name → return_value"
    """
    pass  # TODO
```

---

### Task 8: Stack Decorators (5 min)

Apply multiple decorators to a single function:

```python
@timer
@log_calls
@retry(max_attempts=2, delay=0.1)
def process_data(data):
    """Process data with timing, logging, and retry."""
    if not data:
        raise ValueError("Empty data")
    return [x * 2 for x in data]

result = process_data([1, 2, 3])
```

**Question:** In what order do the decorators execute? Write your answer as a comment.

---

## Stretch Goals

- [ ] Write a `@cache` decorator that memoizes function results (like `functools.lru_cache`).
- [ ] Write a `@validate_types` decorator that checks argument types against type hints.
- [ ] Combine Part A and B: use your `@timer` decorator on each functional pipeline step.

---

## Definition of Done

- [ ] Tasks 1–4: All functional operations produce correct results.
- [ ] Task 5: `@timer` prints execution time and preserves function metadata.
- [ ] Task 6: `@retry` is parameterized with 3-layer nesting and handles exceptions.
- [ ] Task 7: `@log_calls` prints function name, args, and return value.
- [ ] Task 8: All 3 decorators stacked and executing in correct order.
- [ ] All scripts run without errors.
