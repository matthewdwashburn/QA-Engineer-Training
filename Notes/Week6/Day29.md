# Day 29 - Coding Exercises (SQL, Java, Python) & Python Mocking

---

Two threads today: algorithm/query katas across three languages (notes focus on the reusable syntax each taught), plus Python mocking — the `unittest.mock` / `pytest-mock` equivalent of Day 26's Java Mockito.

## SQL

### CTE — `WITH ... AS (...)` (common table expression)

A named, temporary result set you can `JOIN` against — cleaner than a nested subquery.

```sql
WITH salary_groups AS (
    SELECT salary FROM worker
    GROUP BY salary
    HAVING count(*) >= 2        -- keep only salaries shared by 2+ workers
)
SELECT w.worker_id, w.first_name, w.salary FROM worker w
JOIN salary_groups s ON s.salary = w.salary;
```

| Pattern | Use |
|---|---|
| `GROUP BY x HAVING COUNT(*) >= 2` | Find **duplicate / shared values** (e.g. employees on the same salary) |
| `ORDER BY salary DESC LIMIT 1 OFFSET 1` | **Nth-highest** value — skip `N-1` rows, take 1 (here 2nd highest) |
| `CAST(x AS REAL)` | Force float division — avoids integer-division truncation in a rate/ratio |
| `MAX(t.cnt)` on a known-single value | Lets you `SELECT` a column that isn't in `GROUP BY` without SQL complaining |

```sql
-- processed rate per type: CAST avoids int division, MAX satisfies GROUP BY
SELECT f.type, (CAST(MAX(t.cnt) AS REAL) / count(*)) AS processed_rate
FROM facebook_complaints f
JOIN true_type_count t ON f.type = t.type
GROUP BY f.type;
```

## Java — array algorithms

### Equilibrium (even) index — running-sum pivot

Find the index where the sum of elements to its **left** equals the sum to its **right**. Key trick: keep a `totalRight` and subtract the current element before comparing, so it's **O(n)** with no nested loop.

```java
int totalRight = 0, totalLeft = 0;
for (int v : arr) totalRight += v;      // start with everything on the right
for (int i = 0; i < arr.length; i++) {
    totalRight -= arr[i];               // remove current from right
    if (totalRight == totalLeft) return i;
    totalLeft += arr[i];                // else roll current into the left
}
return -1;
```

- `Integer.MIN_VALUE` / `Integer.MAX_VALUE` — seed values for max/min tracking (see the Chairs kata, which brute-forces "seat furthest from others, tiebreak by nearest exit").
- Verified with a JUnit `@Test` + `assertEquals(expected, actual)`.

## Python — dicts, argsort & interval overlap

### `sorted(range(n), key=lambda ...)` — argsort (indices, not values)

Sort the **indices** of a list by a field, so you can process items in order while keeping their original positions.

```python
order = sorted(range(n_customers), key=lambda w: customers[w][0])  # by arrival day
```

| Syntax | Purpose |
|---|---|
| `[0] * n` | Pre-fill a fixed-size list (e.g. `assignments`) |
| `dict` of `list` (`room_dict[room].append(...)`) | Group bookings per room |
| `if x not in room_dict:` | Membership test to detect an unassigned room |

### Interval-overlap check

Two date ranges `[a1,a2]` and `[b1,b2]` overlap if either endpoint falls inside the other, **or** one fully contains the other:

```python
if ((arrival >= start and arrival <= end)
        or (departure >= start and departure <= end)
        or (arrival < start and departure > end)):
    open = False   # conflict — room already booked for these dates
```

Hotel room-allocation pattern: process customers in arrival order, walk rooms upward (`current_room += 1`) until one has no overlapping booking, then assign.

---

## Python Mocking

Testing a `UserService` in isolation from its `UserRepository` + `EmailClient`. Two libraries: **`unittest.mock`** (standard library) and **`pytest-mock`** (`pip install pytest-mock`, adds the `mocker` fixture that auto-cleans patches). Same API; `mocker` is the more Pythonic route in pytest.

### Mock vs MagicMock

| Class | Behavior |
|---|---|
| `Mock()` | Basic fake; any attribute/method access auto-returns a child mock |
| `MagicMock()` | `Mock` + pre-configured **magic methods** (`__len__`, `__getitem__`, `__iter__`, `__enter__`/`__exit__`…) — needed for `len()`, subscripting, iteration, `with` |

```python
m = Mock(); m.return_value = 42          # configure what calling it returns
magic = MagicMock()
magic.__len__.return_value = 5            # len(magic) == 5
magic.__getitem__.return_value = "item"  # magic[0] == "item"
magic.__iter__.return_value = iter([1,2,3])  # list(magic) == [1,2,3]
```

### spec / autospec — catch typos & bad signatures

```python
mock_repo = mocker.Mock(spec=UserRepository)   # only real attributes allowed
mock_repo.find_by_idd.return_value = None      # AttributeError — typo caught!

mock_calc = mocker.create_autospec(Calculator) # spec + validates ARG COUNT
mock_calc.add(1)          # fails with autospec (add needs 2 args), passes with plain spec
```

### return_value vs side_effect

| Set | Result |
|---|---|
| `mock.return_value = x` | Always returns `x` |
| `mock.side_effect = Exception(...)` | Raises it |
| `mock.side_effect = [a, b, c]` | Returns `a`, then `b`, then `c` (list of exceptions works too); 4th call → `StopIteration` |
| `mock.side_effect = func` | Calls `func` with the **same args** — dynamic logic |

`side_effect` takes precedence over `return_value`; set it back to `None` to re-enable `return_value`. Great for retry logic, pagination, rate-limiting, and per-argument responses.

```python
mock_api.call.side_effect = [ConnectionError(), ConnectionError(), {"status": "ok"}]  # fail twice then succeed
mock_repo.save.side_effect = lambda u: User(id=100, name=u.name, email=u.email)        # dynamic return
```

### Patching — replace an object for the duration of a test

| Form | Use |
|---|---|
| `@patch('os.path.exists')` decorator | Injects the mock as an arg; auto-restored after the test |
| `with patch('os.getcwd') as m:` | Scoped to the `with` block |
| `mocker.patch('target', return_value=...)` | pytest-mock version (no cleanup needed) |
| `patch.object(obj, 'method')` | Patch one attribute/method on a specific object |
| `patch.dict(os.environ, {...})` | Patch dict contents (env vars); `clear=True` empties first |
| `mocker.mock_open(read_data=...)` | Fake `builtins.open` for file reads |
| `patcher = patch(...); patcher.start()/.stop()` | Manual control in `setup_method`/`teardown_method` |

> **Golden rule — patch where the object is USED, not where it's DEFINED.** If `services.py` does `from external_api import ExternalAPIClient`, patch `'services.ExternalAPIClient'`, not `'external_api.ExternalAPIClient'`.

> **Multiple `@patch` decorators apply bottom-up** — the bottom decorator maps to the **first** mock argument.

### Verification — assert on interactions

```python
mock_repo.find_by_id.assert_called_once_with(1)  # exactly once, with these args
mock_repo.save.assert_called_once()              # exactly once, any args
mock_repo.save.assert_not_called()               # never called
mock.assert_called_with(...)                     # most recent call matched these args
mock_api.call.call_count                         # raw call count
```

**Argument capture** (the ArgumentCaptor equivalent): inspect what was passed via `call_args`.

```python
saved_user = mock_repo.save.call_args[0][0]      # [0] = positional args tuple, [0] = first arg
subject   = mock_email.send.call_args[1]['subject']  # [1] = kwargs dict
```

### mocker.spy — track calls to a *real* method

Wraps a real method so it still executes, but records the call for verification.

```python
spy = mocker.spy(service, "get_user")
service.deactivate_user(1)          # runs the real get_user internally
spy.assert_called_once_with(1)      # and we can verify it
```

### Putting it together

Real-world tests inject mocked dependencies through **fixtures** (`mock_repository`, `mock_email_client`, `user_service`), configure defaults with `return_value`/`side_effect`, then assert on both the result and the interactions. Group with test classes and cover success / error (`pytest.raises` + verify no side effects) / edge cases (email fails but user still created; unique IDs via a stateful `side_effect`).

