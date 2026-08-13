# Day 28 - Python Testing: pytest & unittest

---

Same `Calculator` SUT as the Java demos, tested two ways in Python. Also a mock-interview review of missed topics (below).

## pytest — the modern framework

- Test **functions** start with `test_`; test **classes** start with `Test` (no `__init__`). No inheritance needed.
- Uses plain `assert` — no special methods. Detailed failure diffs are automatic.

```bash
pytest demo_pytest_basics.py -v          # verbose
pytest demo_pytest_basics.py -v -k "add" # only tests matching "add"
pytest ... -s                             # show print() output
```

```python
def test_addition_returns_correct_sum():
    calc = Calculator()
    assert calc.add(2, 3) == 5

with pytest.raises(ZeroDivisionError) as exc_info:   # exception testing
    calc.divide(10, 0)
assert "zero" in str(exc_info.value).lower()
```

## pytest fixtures — setup/teardown + dependency injection

A fixture is injected simply by naming it as a **test parameter**.

```python
@pytest.fixture
def calculator():
    return Calculator()

def test_add(calculator):        # 'calculator' auto-injected, fresh per test
    assert calculator.add(2, 3) == 5
```

| Feature | Detail |
|---|---|
| `yield` | Code before `yield` = setup, after = teardown (runs **even if the test fails**) |
| `scope=` | `function` (default) · `class` · `module` · `session` — controls how often the fixture is created |
| Fixture depends on fixture | Just take another fixture as a parameter; pytest resolves the whole tree |
| `@pytest.fixture(params=[...])` | Parameterized fixture — test runs once per param (`request.param`) |
| `autouse=True` | Runs for every test without being requested |
| Factory fixture | Return an inner `_make(...)` function so tests customize the data they need |

### Built-in fixtures

| Fixture       | Provides                                                                     |
| ------------- | ---------------------------------------------------------------------------- |
| `tmp_path`    | A unique temp directory (`pathlib.Path`) per test                            |
| `capsys`      | Captures stdout/stderr → `capsys.readouterr().out`                           |
| `monkeypatch` | Patch attributes/methods/env vars during a test (`monkeypatch.setattr(...)`) |
| `request`     | Metadata about the running test (`request.node.name`, `request.param`)       |
|               |                                                                              |

### conftest.py — shared fixtures

- Auto-discovered by pytest; fixtures defined there are available to **all** tests in the directory (no import). Nested `conftest.py` allowed; child overrides parent.
- Also hosts hooks: `pytest_configure` (register custom markers via `config.addinivalue_line`), `pytest_collection_modifyitems`.

## pytest markers — skip, xfail, parametrize, custom

| Marker | Behavior |
|---|---|
| `@pytest.mark.skip(reason=...)` | Always skip — never runs |
| `@pytest.mark.skipif(cond, reason=...)` | Skip only if condition true (e.g. `sys.platform == "win32"`) |
| `@pytest.mark.xfail(reason=...)` | Runs but **expected to fail**: fail → XFAIL, pass → XPASS. `strict=True` makes an unexpected pass a failure; `raises=`/condition narrow it |
| `pytest.skip("...")` | Skip **at runtime** from inside a test (conditional) |
| `@pytest.mark.parametrize("a,b,expected", [(2,3,5), ...])` | Data-driven test; `indirect=[...]` routes params through a fixture |
| `@pytest.mark.slow` / custom | Custom markers; run/filter with `pytest -m slow` / `-m "not slow"` (register in conftest/pytest.ini) |

**Skip vs xfail:** skip does *not* run the test; xfail runs it and checks that it fails.

## unittest — the built-in (xUnit) framework

Python's standard-library framework — class-based, familiar to Java devs. No extra dependencies; **pytest can run unittest tests too**.

```bash
python -m unittest demo_unittest_basics.py -v
python -m unittest demo_unittest_basics.TestCalculatorBasic.test_add -v
```

```python
class TestCalculatorBasic(unittest.TestCase):   # must inherit TestCase
    def setUp(self):        self.calc = Calculator()  # ~ @BeforeEach
    def tearDown(self):     self.calc = None          # ~ @AfterEach
    @classmethod
    def setUpClass(cls):    ...                        # ~ @BeforeAll (once)
    @classmethod
    def tearDownClass(cls): ...                        # ~ @AfterAll (once)

    def test_add_returns_sum(self):
        self.assertEqual(5, self.calc.add(2, 3))       # (expected, actual)
```

| Assertion method | Checks |
|---|---|
| `assertEqual` / `assertNotEqual` | Value (in)equality |
| `assertTrue` / `assertFalse` | Boolean |
| `assertGreater` / `assertLess` / `assertGreaterEqual` / `assertLessEqual` | Comparisons |
| `assertIsNone` / `assertIsNotNone` | Null checks |
| `assertIsInstance` | Type check |
| `assertIn` | Membership (e.g. substring in message) |
| `assertRaises(Err)` | Exception — context-manager style (recommended) or `assertRaises(Err, fn, *args)` |
| `assertAlmostEqual(a, b, places=2)` or `delta=` | Floating-point (like JUnit's delta) |

## pytest vs unittest

| | pytest | unittest |
|---|---|---|
| Style | Plain functions + `assert` | Class extends `TestCase`, `assertX` methods |
| Boilerplate | Minimal | More (inheritance required) |
| Setup/teardown | Fixtures (flexible, DI) | `setUp`/`tearDown`/`setUpClass` |
| Best for | New projects, expressive tests | Legacy code, no-dependency, Java devs transitioning |

## Mock-interview — topics to review

DDL & DML SQL live coding · Java OOP concepts · testing lifecycles · **bug lifecycle** · **development lifecycle (SDLC)**.
