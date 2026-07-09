# Day 25 - JUnit Unit Testing Fundamentals

---

First hands-on Java unit testing with JUnit (Jupiter). Maven project (`junit-demo`) testing a `Calculator` SUT.

## Setup & Running

- Run tests in VS Code: **right-click → Run Tests**, or **Run Tests with Coverage** for a coverage report.
- Use **JUnit 5** for now — the Java extension's test runner doesn't yet support JUnit 6.
- JUnit 5 imports live under `org.junit.jupiter.api`.

## Test structure — AAA pattern

```java
@Test
@DisplayName("Adding two positive numbers returns their sum")
void add_twoPositiveNumbers_returnsSum() {
    int a = 5, b = 3;                 // ARRANGE - set up data
    int result = calculator.add(a, b); // ACT - call method under test
    assertEquals(8, result, "5+3 should equal 8"); // ASSERT - verify (expected, actual, message)
}
```

- **Naming convention:** `methodName_scenario_expectedBehavior`. Others: `should_expectedBehavior_when_scenario`, `given_precondition_when_action_then_result`.
- `assertEquals(expected, actual)` — expected comes **first**. Optional 3rd arg = failure message.

## Annotations

| Annotation | Purpose |
|---|---|
| `@Test` | Marks a test method |
| `@DisplayName("...")` | Human-readable name in the test report |
| `@BeforeAll` / `@AfterAll` | Run **once** per class (must be `static`) — expensive setup (DB conn, mock server) |
| `@BeforeEach` / `@AfterEach` | Run before/after **every** test — fresh instances, reset state, cleanup |
| `@Order(n)` + `@TestMethodOrder(MethodOrderer.OrderAnnotation.class)` | Force test execution order |
| `@Nested` | Inner class to group related tests |
| `TestInfo` | Inject into a test/lifecycle method to get metadata (e.g. `getDisplayName()`) |

**Test isolation:** `@BeforeEach` gives each test a fresh object, so tests don't leak state into each other.

## Assertions

| Assertion | Checks |
|---|---|
| `assertEquals(exp, act[, msg])` | Value equality |
| `assertEquals(exp, act, delta)` | Floating-point within tolerance (e.g. `0.1+0.2 != 0.3` exactly) |
| `assertNotEquals` | Values differ |
| `assertTrue` / `assertFalse` | Boolean conditions |
| `assertNull` / `assertNotNull` | Null checks |
| `assertSame` / `assertNotSame` | Reference identity (same object), not value |
| `assertArrayEquals` | Arrays element-by-element (delta overload for doubles) |
| `assertIterableEquals` | Collections in order |
| `assertAll("label", () -> ..., () -> ...)` | **Grouped** — runs all lambdas, reports **all** failures (not just first) |
| `assertThrows(Ex.class, () -> ...)` | Returns the exception for inspection (`.getMessage()`) |
| `assertDoesNotThrow(() -> ...)` | Documents that no exception should be thrown |
| `assertTimeout(Duration, () -> ...)` | Waits for completion, then fails if too slow |
| `assertTimeoutPreemptively(Duration, ...)` | **Interrupts** the operation immediately if it exceeds the limit (separate thread) |

> Prefer specific assertions over `assertTrue(x == 7)` — `assertEquals(7, x)` gives a far better failure message.

## Parameterized tests (data-driven)

Replace `@Test` with `@ParameterizedTest` — write logic once, run with many inputs. Needs the `junit-jupiter-params` dependency.

| Source | Supplies |
|---|---|
| `@ValueSource(ints = {...})` / `(strings = {...})` | A single arg per run |
| `@NullSource` / `@EmptySource` / `@NullAndEmptySource` | null / empty / both (stackable with `@ValueSource`) |
| `@CsvSource({"1, 2, 3"})` | Multiple args per row; `delimiter = '\|'` for custom separator |
| `@MethodSource("provider")` | A `static` method returning `Stream<Arguments>` (use `Arguments.of(...)`) |
| `@EnumSource(MyEnum.class)` | One run per enum constant |

```java
@ParameterizedTest(name = "{0}+{1}={2}")   // custom display name from args
@CsvSource({ "1, 1, 2", "2, 3, 5" })
void add_variousInputs(int a, int b, int expected) {
    assertEquals(expected, calculator.add(a, b));
}
```
