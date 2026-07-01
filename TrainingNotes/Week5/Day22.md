# Day 22 - pytest Coverage, QA Artifacts, SQL Joins & Java Data Structures

---

## pytest — Running Tests & Coverage

```bash
pip install pytest pytest-cov
```

### `pytest <file>` — run all tests in a file

```bash
pytest test_shipping.py
```

### `pytest <file>::<test_name>` — run a single test

```bash
pytest test_shipping.py::test_standard_returned_for_large_non_priority_order
```

### `--cov=<module>` — measure coverage of a module

```bash
pytest test_shipping.py --cov=shipping
```

### `--cov-report=term-missing` — show uncovered line numbers in terminal

```bash
pytest test_shipping.py --cov=shipping --cov-report=term-missing
```

### `--cov-report=html` — generate visual HTML report in `htmlcov/`

```bash
pytest test_shipping.py --cov=shipping --cov-report=html
```

### Test file structure

```python
import pytest
from shipping import get_shipping_tier   # import the module under test

def test_express_returned_for_large_priority_order():
    assert get_shipping_tier(150, priority=True) == "EXPRESS"

def test_no_free_shipping_returned_for_small_order():
    assert get_shipping_tier(50.00, priority=False) == "NO_FREE_SHIPPING"
```

- Test functions must start with `test_`
- Use keyword args to make test intent obvious: `priority=True`
- **100% coverage ≠ 100% correctness** — a test with a wrong expected value still covers the line

### Type hints in function signatures

```python
def get_shipping_tier(order_total: float, priority: bool) -> str:
    ...
```

---

## QA Artifacts — User Stories, Test Cases & RTM

### User Story format

```
As a [user type], I want to [action], so that [benefit]
```

### Requirement types

| Type | Abbreviation | Description |
|---|---|---|
| Business Requirement | BR | What the business needs |
| Functional Requirement | FR | Specific system behaviors (FR-1, FR-2…) |
| Non-Functional Requirement | NFR | Performance, security, scalability |

### Acceptance Criteria — Given/When/Then

```
Given [precondition]
When  [action]
Then  [expected outcome]
```

### Test Case types

| Type | When to use |
|---|---|
| Positive | Valid inputs, happy path |
| Negative | Invalid inputs, rejection cases |
| Boundary | Values at the exact edge of a rule (e.g. exactly 12 chars) |
| Performance | Load, concurrency, response time |

### RTM (Requirements Traceability Matrix)

Links each acceptance criterion to its test case(s), test type, build version, and pass/fail result. Ensures every requirement is tested and every test maps to a requirement.

```
Requirement ID | Requirement         | Test Case(s)  | Type     | Build  | Result | Notes
AUTH-77-AC-1   | Reset email ≤ 2 min | TC-AUTH-001,2 | Pos, Bnd | v1.0.0 | Pass   | ...
AUTH-77-NFR-2  | 500 concurrent req  | PERF-AUTH-002 | Perf     | v1.0.0 | Fail   | Defect
```

---

## SQL — Joins & Advanced DML

### `LEFT JOIN LATERAL` — correlated subquery per row (get latest record per customer)

```sql
SELECT c.*, recent.order_id FROM customer c
LEFT JOIN LATERAL (
    SELECT h.order_id
    FROM order_header h
    WHERE h.customer_id = c.customer_id
    ORDER BY h.placed_at DESC
    LIMIT 1
) recent ON TRUE;
-- LATERAL lets the subquery reference the outer row (c); ON TRUE always joins the result
```

### `RIGHT JOIN` — keep all rows from the right table

```sql
SELECT c.email, oh.order_id FROM order_header oh
RIGHT JOIN customer c ON c.customer_id = oh.customer_id;
-- customers with no orders still appear (NULL for oh.order_id)
```

### `FULL OUTER JOIN` — keep all rows from both tables

```sql
SELECT c.*, oh.* FROM customer c
FULL OUTER JOIN order_header oh ON c.customer_id = oh.customer_id;
-- unmatched rows from either side appear with NULLs on the other side
```

### `CROSS JOIN (VALUES ...)` — generate rows from literal values

```sql
SELECT * FROM customer
CROSS JOIN (VALUES ('STOCK_GOOD'), ('STOCK_OK'), ('STOCK_BAD')) AS status_table(status_desc);
-- produces every combination of customer × status_desc
```

### Multi-row `INSERT`

```sql
INSERT INTO order_line (order_id, line_no, product_id, qty, unit_price) VALUES
    (2, 1, 1, 50, 7000.00),
    (2, 2, 2, 60, 7000.00),
    (2, 3, 3, 60, 7000.00);
```

### `DELETE` with `OR`

```sql
DELETE FROM order_header WHERE order_id = 1 OR order_id = 2;
```

### `ON DELETE CASCADE` vs `ON DELETE RESTRICT`

```sql
-- CASCADE: deleting a parent auto-deletes its child rows
order_id INT NOT NULL REFERENCES order_header (order_id) ON DELETE CASCADE

-- RESTRICT: blocks deletion of parent while child rows exist (must delete children first)
customer_id INT NOT NULL REFERENCES customer (customer_id) ON DELETE RESTRICT
```

---

## Java — `StringBuilder` & `Deque` (Stack)

### `StringBuilder` — mutable string (efficient for building strings in a loop)

```java
StringBuilder sb = new StringBuilder();
sb.append(String.valueOf(s.charAt(i)).toUpperCase());   // append char as uppercase String
sb.append("-");
sb.toString();   // convert to immutable String when done
```
`String` is immutable — concatenating in a loop creates a new object each time. `StringBuilder` mutates in place.

### `String.valueOf(char)` — char to String

```java
String.valueOf(s.charAt(i)).toUpperCase()   // char -> String -> uppercase
String.valueOf(s.charAt(i)).toLowerCase()   // char -> String -> lowercase
```

### `Deque<Character>` / `ArrayDeque<>` — used as a stack for brace matching

```java
Deque<Character> braceStack = new ArrayDeque<>();
braceStack.addLast(c);        // push
braceStack.peekLast();        // peek top (no remove)
braceStack.removeLast();      // pop
braceStack.peek();            // returns null if empty — used to detect empty stack
braceStack.size() != 0;       // check anything left after full scan
```

Brace matching pattern: push every open brace, on a close brace check the top — if it's the matching opener, pop; otherwise invalid. If the stack is non-empty after the loop, unclosed braces remain.

### `switch` on `char`

```java
switch (c) {
    case ')':
        if (top != '(') return false;
        braceStack.removeLast();
        break;
    case '}':
        ...
}
```

---

## Git Reset

```bash
git reset --soft    # move HEAD back; changes stay staged (index unchanged)
git reset --mixed   # move HEAD back; changes unstaged but kept in working tree (default)
git reset --hard    # move HEAD back; changes discarded completely (destructive)
```

---

## Notes
- When writing test cases, check that all AC have both positive AND boundary tests — easy to miss
- RTM `Result: Fail` = open defect; track the defect ID in Notes column
