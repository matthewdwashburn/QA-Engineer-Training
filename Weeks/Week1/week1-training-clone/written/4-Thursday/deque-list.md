# Deque vs. List

## Learning Objectives
- Explain what `collections.deque` is and the abstract data type it implements.
- Understand O(1) vs O(n) complexity for append and pop from both ends.
- Use all major `deque` operations: `append`, `appendleft`, `pop`, `popleft`, `rotate`, `maxlen`.
- Know when to use `deque` vs. `list` based on access patterns.

---

## Why This Matters

> **Weekly Epic Connection:** Performance matters when processing large volumes of test data or managing work queues in automation. Using the right data structure — `deque` vs. `list` — can mean the difference between an instantaneous operation and painfully slow one that scales linearly with data size. Understanding complexity trade-offs is a mark of a professional engineer.

---

## The Concept

### What Is a Deque?

A **deque** (pronounced "deck") stands for **Double-Ended Queue**. It is an abstract data type (ADT) that generalizes both a **stack** (last-in, first-out) and a **queue** (first-in, first-out) by supporting efficient add and remove operations at **both ends**.

Python provides `deque` in the `collections` module. Under the hood, it is implemented as a **doubly-linked list of fixed-size blocks**, which gives it:
- **O(1)** time complexity for `append` and `pop` from *either* end.
- **O(n)** time complexity for access by index (you must traverse nodes).

```python
from collections import deque
```

---

### The Problem with Lists at the Left End

Python lists are backed by a **dynamic array** — a contiguous block of memory. This design makes right-end operations fast but left-end operations expensive:

```python
my_list = [1, 2, 3, 4, 5]

# O(1) — no shifting needed
my_list.append(6)      # Add at the right end: [1, 2, 3, 4, 5, 6]
my_list.pop()          # Remove from right end: 6

# O(n) — EVERY element must shift one position
my_list.insert(0, 0)   # Add at left: shifts all 6 elements right
my_list.pop(0)         # Remove from left: shifts all 5 elements left
```

For a list of 1,000,000 items, `insert(0, x)` copies all 1,000,000 references. A `deque` performs the equivalent operation in a fixed, constant time regardless of size.

---

### Creating a Deque

```python
from collections import deque

# Empty deque
dq = deque()

# From an iterable
dq = deque([1, 2, 3, 4, 5])
print(dq)   # deque([1, 2, 3, 4, 5])

# With a maximum size (maxlen)
recent = deque(maxlen=5)
```

---

### Core Operations

```python
from collections import deque

dq = deque([1, 2, 3, 4, 5])

# --- Adding elements ---
dq.append(6)           # Add to right end  → deque([1, 2, 3, 4, 5, 6])
dq.appendleft(0)       # Add to left end   → deque([0, 1, 2, 3, 4, 5, 6])

# Add multiple elements at once
dq.extend([7, 8])      # Add iterable to right  → deque([0, 1, 2, 3, 4, 5, 6, 7, 8])
dq.extendleft([-1, -2])  # Add iterable to left (reversed order)
# Each item in [-1, -2] is appendleft'd in sequence:
# appendleft(-1) → [-1, 0, 1, ...]
# appendleft(-2) → [-2, -1, 0, 1, ...]

# --- Removing elements ---
right_val = dq.pop()       # Remove and return from right end
left_val  = dq.popleft()   # Remove and return from left end

# --- Other operations ---
dq.count(3)        # Count occurrences of value 3
dq.remove(3)       # Remove first occurrence of 3 (raises ValueError if missing)
dq.clear()         # Remove all elements → deque([])
dq.copy()          # Return a shallow copy
dq.reverse()       # Reverse the deque in place
```

---

### Performance Comparison

| Operation | List | Deque | Notes |
|-----------|------|-------|-------|
| `append` (right) | **O(1)** | **O(1)** | Both fast |
| `pop` (right) | **O(1)** | **O(1)** | Both fast |
| `append` left (`insert(0, x)`) | **O(n)** ❌ | **O(1)** ✅ | Deque wins |
| `pop` left (`pop(0)`) | **O(n)** ❌ | **O(1)** ✅ | Deque wins |
| Access by index (`[i]`) | **O(1)** ✅ | **O(n)** ❌ | List wins |
| Length (`len`) | **O(1)** | **O(1)** | Both fast |
| Search (`in`) | **O(n)** | **O(n)** | Both linear |

> **Key insight:** If your algorithm frequently inserts or removes from the *left* side, switch to a `deque`. If you frequently access elements by index (random access), stick with a `list`.

---

### `maxlen` — Fixed-Size Sliding Window

When you create a `deque` with `maxlen`, it becomes **bounded**: once full, adding a new item to one end automatically drops the item from the opposite end.

```python
from collections import deque

# Keep only the last 5 log entries
recent_logs = deque(maxlen=5)

for i in range(10):
    recent_logs.append(f"Log entry {i}")

print(list(recent_logs))
# ['Log entry 5', 'Log entry 6', 'Log entry 7', 'Log entry 8', 'Log entry 9']
```

This is the canonical Python pattern for a **sliding window buffer** — common in log monitoring, moving averages, and sensor data processing.

---

### `rotate()` — Circular Rotation

`rotate(n)` moves `n` elements from the right end to the left (positive `n`) or from the left end to the right (negative `n`):

```python
from collections import deque

dq = deque([1, 2, 3, 4, 5])

dq.rotate(2)     # Move 2 from right → left: deque([4, 5, 1, 2, 3])
dq.rotate(-1)    # Move 1 from left → right: deque([5, 1, 2, 3, 4])
```

**Use case:** Round-robin task scheduling — rotate by 1 after each task to cycle through workers.

---

### Practical Use Cases

#### 1. FIFO Queue (First In, First Out)

The classic use case for `deque`: add to the right, remove from the left.

```python
from collections import deque

task_queue = deque()
task_queue.append("test_login")       # Enqueue
task_queue.append("test_checkout")
task_queue.append("test_profile")

next_task = task_queue.popleft()      # Dequeue → "test_login"
print(f"Running: {next_task}")
print(f"Remaining: {list(task_queue)}")
# Remaining: ['test_checkout', 'test_profile']
```

#### 2. Undo/Redo History

```python
from collections import deque

# Cap history at 50 actions
history = deque(maxlen=50)

def perform_action(action):
    history.append(action)
    execute(action)

def undo():
    if history:
        last = history.pop()
        reverse(last)
```

#### 3. Breadth-First Search (BFS)

`deque` is the standard data structure for BFS because of its O(1) `popleft()`:

```python
from collections import deque

def bfs(graph, start):
    visited = set()
    queue   = deque([start])

    while queue:
        node = queue.popleft()     # O(1) — crucial for BFS performance
        if node not in visited:
            visited.add(node)
            queue.extend(graph[node])

    return visited
```

#### 4. Moving Average (Test Performance Tracking)

```python
from collections import deque

window = deque(maxlen=5)

def record_duration(ms):
    window.append(ms)
    return sum(window) / len(window)

for ms in [100, 120, 95, 130, 110, 140]:
    avg = record_duration(ms)
    print(f"Duration: {ms}ms | Rolling 5-avg: {avg:.1f}ms")
```

---

### When to Use Which

| Use **`list`** when... | Use **`deque`** when... |
|---|---|
| You need random access by index | You add/remove from both ends |
| Most operations are `append`/`pop` at the right | You're implementing a FIFO queue |
| You need slicing (`[a:b]`) | You need a fixed-size sliding window (`maxlen`) |
| Sorting is required | You need efficient `appendleft`/`popleft` |
| Data set is small (O(n) left ops are acceptable) | You need a BFS queue or task dispatcher |

---

## Summary

- `deque` (**Double-Ended Queue**) from `collections` provides **O(1) append and pop from both ends** — lists are O(n) at the left end.
- Key operations: `append`, `appendleft`, `pop`, `popleft`, `extend`, `extendleft`, `rotate`, `count`, `clear`.
- Use `maxlen` for **fixed-size sliding windows** and recent history buffers — old items are dropped automatically.
- Use `rotate(n)` for **circular scheduling** patterns.
- **Trade-off:** `deque` has O(n) random index access; lists have O(1). Choose based on your dominant operation.
- Use `deque` for queues and pipelines; use `list` for indexed, sorted, or sliced data.

---

## Additional Resources
- [Python Docs — collections.deque](https://docs.python.org/3/library/collections.html#collections.deque)
- [Real Python — Deque in Python](https://realpython.com/python-deque/)
- [Python Docs — Time Complexity of Common Operations](https://wiki.python.org/moin/TimeComplexity)
