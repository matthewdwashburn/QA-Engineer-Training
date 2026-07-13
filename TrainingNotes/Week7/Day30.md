# Day 30 - Allure Reporting (Python & Java) + Coding Exercises

---

Allure test reporting in **Python** (`allure-pytest`) and a deeper pass on **Java** (`@Step`, attachments, programmatic API — builds on [Day 27](Day27.md)). Plus daily katas in Java, Python & SQL.

## Allure in Python — `allure-pytest`

```bash
pip install allure-pytest
pytest demo_allure_pytest_setup.py --alluredir=allure-results -v  # write raw results
allure serve allure-results                                       # build + open report
```

Decorators mirror the Java/JUnit annotations, `@lowercase` instead of `@Capitalized`. The test **docstring** becomes the report description.

| Decorator | Purpose |
|---|---|
| `@allure.epic("...")` / `@allure.feature("...")` / `@allure.story("...")` | Report hierarchy (epic → feature → story) |
| `@allure.title("...")` | Readable test name in the report |
| `@allure.severity(allure.severity_level.X)` | `BLOCKER` > `CRITICAL` > `NORMAL` > `MINOR` > `TRIVIAL` |
| `@allure.link(url, name=)` | Clickable link |
| `@allure.issue("BUG-456", name)` | Bug-tracker link |
| `@allure.testcase("TC-789", name)` | Test-management-system link |
| `@allure.description("...")` | Explicit description (overrides docstring) |

Works on plain functions **and** test classes, and coexists with `@pytest.mark.parametrize` / `skip` / `xfail` — each param and skip/xfail shows individually.

### Steps — logical, reportable phases

```python
with allure.step("Navigate to login page"):   # context-manager step
    ...

@allure.step("Enter value in field '{field_name}': {value}")  # step function; {params} interpolate
def enter_field(field_name, value):
    ...
```
Step functions can call other step functions → **nested steps** in the report.

### Attachments — evidence in the report

```python
allure.attach(text, name="Log", attachment_type=allure.attachment_type.TEXT)
allure.attach(json.dumps(data, indent=2), name="API Response",
              attachment_type=allure.attachment_type.JSON)
```
`attachment_type` also has `HTML`, `CSV`, `PNG`, etc. Fixtures can attach data too (e.g. log the test-data JSON they return).

### Dynamic metadata — set at runtime

```python
allure.dynamic.title(f"Test {a}+{b}={expected}")
allure.dynamic.description("...")
allure.dynamic.severity(allure.severity_level.NORMAL)
allure.dynamic.parameter("Environment", "staging")   # shows in report params
allure.dynamic.label("layer", "unit")
```

## Allure in Java — advanced features

```bash
mvn clean test          # run tests → raw results in target/allure-results
mvn allure:serve        # generate a temp report and open it in the browser automatically
mvn allure:report       # generate a static HTML report (target/site) for manual opening
```
Setup (same as Day 27): `allure-junit5` dependency + `aspectjweaver` as a `-javaagent` (required for `@Step`).

| Feature | Syntax |
|---|---|
| `@Step("...")` | Annotate a method → it's a report step. `{paramName}` interpolates args into the step name |
| `@Attachment(value=, type=)` | Method's **return value** becomes an attachment (e.g. `type = "text/html"`, `"text/csv"`) |
| `Allure.addAttachment(name, type, content)` | Attach imperatively inside a test |
| `Allure.step("...", () -> { ... })` | Inline lambda step (good for documenting preconditions) |
| `Allure.parameter(k, v)` / `Allure.label(k, v)` / `Allure.link(name, url)` / `Allure.description(...)` | Programmatic metadata at runtime |

> Security note echoed in the demos: keep secrets (passwords) out of `@Step` names and redact them in attachments.

## Coding exercises

### Java — HashMap/HashSet counting + Streams I/O

- **Frequency count** with `HashMap`: `map.containsKey(k) ? map.put(k, map.get(k)+1) : map.put(k, 1)`. Matching pairs = `Σ (count / 2)` (integer division). *(matchingSocks, validString)*
- **`HashSet<>(map.values())`** — collapse counts to distinct values to reason about them (valid string: at most 2 distinct char-counts). Iterate entries via `for (Map.Entry<K,V> e : map.entrySet())`.
- **Two-sum** (icecreamParlor): nested loop, return 1-based indices in ascending order.
- **HackerRank Stream I/O boilerplate** (reusable):
```java
List<Integer> arr = Stream.of(reader.readLine().replaceAll("\\s+$", "").split(" "))
        .map(Integer::parseInt).collect(toList());   // parse line of ints
result.stream().map(Object::toString).collect(joining(" ")); // join to output
IntStream.range(0, t).forEach(i -> {...});          // loop t test cases
```

### Python — string slicing & stepped ranges

- **Step a range**: `for i in range(0, len(s), 2)` walks every 2nd index — cleaner than manual index bookkeeping.
- **Slice pairs**: `s[i:i+2]` grabs a 2-char chunk (slices never go out of bounds). Pad odd length first: `if len(s) % 2: s += '_'`. *(split_strings — includes the naive vs. improved version)*
- **Sliding window** (lowest_product): scan `num[i] * num[i+1] * num[i+2] * num[i+3]`, tracking the min. `str(digit)` → `int(...)` to multiply characters of a number string.
- **Tower builder**: `" " * (n-i-1)` padding + `"*" * (2*i) + "*"` center → each row via string multiplication.

### SQL — filtered join with dedup

```sql
SELECT DISTINCT c.customer_id, c.customer_name
FROM online_store_customers c
JOIN online_store_orders o ON c.customer_id = o.customer_id
WHERE o.amount > 100;   -- DISTINCT collapses customers with multiple qualifying orders
```
`DISTINCT` prevents a customer appearing once per matching order (a 1-to-many join fan-out).
