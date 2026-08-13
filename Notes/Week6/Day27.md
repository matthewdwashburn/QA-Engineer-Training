# Day 27 - Allure Test Reporting

---

Rich HTML test reports layered on top of JUnit 5 via **Allure 2.24.0** (`allure-junit5`). Annotations add metadata that shows up in the report — no change to test logic.

## Running

```bash
mvn clean test && allure serve target/allure-results
```
- Tests write raw results to `target/allure-results`; `allure serve` builds & opens the HTML report.
- The Allure CLI (`allure-2.24.0`) was bundled locally under `.allure/`.

## Maven setup (pom.xml)

- Dependency: `io.qameta.allure:allure-junit5`.
- **`aspectjweaver`** must run as a `-javaagent` (wired via `maven-surefire-plugin` `<argLine>`) — required for `@Step` and annotation processing to work.
- `allure.results.directory` system property points Surefire at the results folder.
- `allure-maven` plugin generates the report.

## Annotations

Imported from `io.qameta.allure.*`. Stack on top of normal `@Test` methods.

### Hierarchy (organizes the report tree)

| Annotation | Level |
|---|---|
| `@Epic("...")` | Highest — big initiative / theme |
| `@Feature("...")` | A feature within the epic |
| `@Story("...")` | A user story within the feature |

### Metadata

| Annotation | Purpose |
|---|---|
| `@Description("...")` | Free-text description of the test |
| `@Severity(SeverityLevel.X)` | `BLOCKER` > `CRITICAL` > `NORMAL` > `MINOR` > `TRIVIAL` — drives release decisions |
| `@Owner("qa-team")` | Accountability / who owns the test |
| `@DisplayName("...")` | JUnit's readable name (still used by Allure) |

### Linking to external systems

| Annotation | Links to |
|---|---|
| `@Link(name=, url=)` | Any URL (docs, requirements) — clickable icon in report |
| `@Issue("BUG-456")` | Bug tracker |
| `@TmsLink("TC-789")` | Test management system |

## Works with existing JUnit features

Allure annotations coexist with `@Nested` classes and `@ParameterizedTest` — each parameter run appears individually in the report, and failures show full detail.

```java
@Epic("Week 6: Unit Testing")
@Feature("Allure Reporting")
class Demo {
    @Test
    @Story("Basic Annotations")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Critical tests affect release decisions")
    @Link(name = "Requirements", url = "https://jira.example.com/REQ-123")
    void testCriticalSeverity() {
        assertTrue(true);
    }
}
```
