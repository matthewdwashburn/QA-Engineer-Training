# 🔥 MASTER CRAM SHEET — Weeks 7 & 8, All Tools
### 40-Question MC Quiz + Verbal QC Interview | Built from every `written/` file in `lastQC/`

**Scope covered (equal weight):** Postman · REST Assured (Java) · Python `requests`/pytest · JMeter/Performance · Selenium (Java) · Selenium (Python) · Cucumber (Java BDD) · Behave (Python BDD) · Playwright (Java) · Integration & System Testing theory.

> **How to use this in 24 hours:**
> **Hours 1–3** → §1 (Theory Spine) + §2 (Tool Cores). This is 60% of the interview.
> **Hours 4–7** → §3 (Cross-Tool Tables). This is where MC questions are written from.
> **Hours 8–10** → §4 (Trap Drill). Read out loud.
> **Hours 11–12** → §5 (Verbal Answers) + §6 (Syntax Recognition) + §7 (Numbers).
> **Final 30 min** → §8 only.

---
---

# §1 — THE THEORY SPINE (everything hangs off this)

## 1.1 The Test Levels Ladder — the single most-asked structure

```
UNIT           → one class/function, ALL dependencies mocked        (Developer)
INTEGRATION    → 2+ real components talking, SOME real deps         (Dev/QA)
SYSTEM         → the whole assembled app, ALL deps real             (QA team)
ACCEPTANCE     → validated against business requirements            (Business/PO)
```

| | Unit | Integration | System |
|---|---|---|---|
| Scope | Single function/class | Multiple components | Entire system |
| Dependencies | Mocked/Stubbed | Some real, some mocked | **All real** |
| Tester | Developer | Developer/Tester | Tester/QA Team |
| Test basis | Code specifications | **Interface/design specs** | **System requirements** |
| Objective | Code correctness | **Interface correctness** | System behavior |
| Database | Mocked | Real **test** database | Real |
| Speed | Very fast | Slower | Slowest |

**V-Model pairings (memorize the diagonal):**
```
Requirements Analysis  ←──→  Acceptance Testing
System Design          ←──→  System Testing
Architecture Design    ←──→  Integration Testing
Module Design          ←──→  Unit Testing
```

**Test Pyramid + recommended distribution:**
```
      /\      UI Tests        5–10%   slow, expensive, brittle — critical journeys only
     /--\     API/Integration 20–30%  fast, reliable, business logic
    /----\    Unit Tests      60–70%  fastest, cheapest, isolated
```
Where each tool sits: **JUnit/pytest = base** · **Postman/REST Assured/requests/JMeter = middle** · **Selenium/Playwright/Cucumber/Behave = tip.**

## 1.2 Integration Testing — the 5 approaches (guaranteed question)

| Approach | Order | Test doubles needed |
|---|---|---|
| **Big Bang** | Everything at once | None — but defects are hard to isolate, found late |
| **Incremental** | One/few at a time | Stubs and/or drivers |
| **Top-Down** | High-level modules first | **STUBS** (fake the lower/not-yet-built modules) |
| **Bottom-Up** | Low-level modules first | **DRIVERS** (fake the higher/calling modules) |
| **Sandwich / Hybrid** | Both ends meeting in the middle | Both |

> **Memory hook:** "Top-down needs a **S**tub because the lower **S**ubordinates aren't built. Bottom-up needs a **D**river to **D**rive it from above."

- **Stub** = dummy implementation of a module *called by* the code under test (returns canned data).
- **Driver** = dummy caller that *invokes* the code under test.
- Objectives of integration testing (6): Interface Verification · Data Integrity · API Contract Validation · Error Handling across boundaries · Configuration Validation · External Service Integration.
- Trade-offs: Big Bang = simple/less planning but hard to isolate & late detection. Incremental = easier isolation & earlier detection but more planning + stub/driver overhead. Top-Down = early prototype + critical modules first, but lower modules tested late. Bottom-Up = no stubs + thorough low-level coverage, but no working prototype until late.

## 1.3 System Testing

- **Functional types:** Feature, Workflow, Data Flow, UI, Error Handling, Boundary.
- **Non-functional types:** Performance · Load · Stress · Security · Usability · Compatibility · Recovery · Installation.
- **Entry criteria:** integration tests passing · environment ready · test data loaded · test cases approved · dependencies available · docs available.
- **Exit criteria:** all planned cases executed · Critical/High defects resolved · **≥95% pass rate** · all requirements covered · stakeholder sign-off.
- **Design techniques:** Equivalence Partitioning · Boundary Value Analysis · State Transition Testing · Use Case Testing.
  - BVA example: password 8–20 chars → test **7, 8, 20, 21**.
  - State transition: `Pending → Processing → Shipped → Delivered`; backwards transitions are invalid.

## 1.4 API Testing Fundamentals

**REST vs SOAP:**

| | REST | SOAP |
|---|---|---|
| Nature | **Architectural style** | **Strict protocol** |
| Data format | JSON, XML, others | **XML only** |
| Contract | OpenAPI/Swagger | **WSDL** |
| Security | HTTPS, OAuth, JWT | **WS-Security** |
| Errors | HTTP status codes | Built-in fault handling |
| Weight | Lightweight/faster | Heavier/slower |
| Use case | Web, mobile, public APIs | Enterprise, financial, legacy |

**REST principles:** Stateless · Resource-based (URIs) · Uniform interface · Multiple formats.

**HTTP method contract — memorize Safe vs Idempotent:**

| Method | Purpose | Safe | Idempotent | Body | Success |
|---|---|---|---|---|---|
| GET | Retrieve | ✅ | ✅ | No | 200 |
| POST | Create | ❌ | ❌ | Yes | **201** |
| PUT | **Replace ENTIRE** resource | ❌ | ✅ | Yes | 200/204 |
| PATCH | **Partial** update | ❌ | ❌ | Yes | 200/204 |
| DELETE | Remove | ❌ | ✅ | No | 200/**204** |
| HEAD | Headers only | ✅ | ✅ | No | 200 |
| OPTIONS | Allowed methods | ✅ | ✅ | No | 200/204 |

- **Safe** = no server state change. **Idempotent** = N identical calls ≡ 1 call.
- **Path parameter** = *identifies* a resource (`/users/123`). **Query parameter** = *filters/modifies* (`/users?active=true`).

**Status codes you must place instantly:**

| Code | Meaning | Fires when |
|---|---|---|
| 200 | OK | Successful GET/PUT/PATCH |
| **201** | Created | Successful POST |
| **204** | No Content | Successful DELETE (no body) |
| **400** | Bad Request | Malformed/invalid input |
| **401** | Unauthorized | **Missing/invalid/expired credentials → AUTHENTICATION** |
| **403** | Forbidden | **Valid identity, insufficient permission → AUTHORIZATION** |
| 404 | Not Found | Resource doesn't exist |
| **422** | Unprocessable / Validation Error | Well-formed but semantically invalid |
| **429** | Too Many Requests | Rate limit exceeded |
| 500 | Internal Server Error | Unhandled server fault |
| **504** | Gateway Timeout | Downstream/dependency timeout |

## 1.5 The 7 Typical API Defect Families (interview gold)

1. **Status code mismatch** — 200 instead of 201 on create; 200-with-empty-body instead of 404; 500 instead of 400 for validation.
2. **Data validation errors** — *Missing validation* (accepts `"email": "not-an-email"`, `"age": -5` and returns 201) or *Over-validation* (rejects `Mary O'Connor`, `mary@example.co.uk`).
3. **Authentication failures** — missing auth check, expired token accepted, token valid after logout, weak/predictable tokens, token leakage in URLs/logs, session fixation.
4. **Rate limiting issues** — no limit on login (enables brute force), inconsistent limits, bypassable via headers, wrong `X-RateLimit-*` headers.
5. **Payload structure problems** — inconsistent shape (sometimes wrapped in `data`, sometimes not), missing documented fields, type inconsistency (`"id": 123` vs `"id": "124"`), `null` vs field-omitted.
6. **Timeout issues** — no timeout (hangs), too-short timeout, silent timeout, cascade timeout, no async option for long operations.
7. **Authorization errors** — **Horizontal privilege escalation** (User A reads User B's data), **Vertical privilege escalation** (regular user hits `/admin` and gets 200 instead of 403), **IDOR** (change `/documents/12345` → `/documents/12346` and it works).

Plus **Error message defects**: too vague (`"Something went wrong"`), too revealing (leaks SQL/stack traces), or inconsistent format across endpoints.

**Authorization test matrix to quote verbally:**
```
                  Owner  Admin  OtherUser  Anonymous
View own data      200    200      403        401
Admin functions    403    200      403        401
```

## 1.6 BDD — the conceptual layer over Cucumber/Behave

- **Definition:** BDD emerged from TDD, combining TDD with **domain-driven design** and **object-oriented analysis** to give teams a shared process and shared tools.
- **The Three Pillars:** **Discovery → Formulation → Automation.**
- **The Five Principles:** Enough is Enough · Deliver Stakeholder Value · It's All Behavior · **Ubiquitous Language** · Examples Guide Development.
- **The Three Amigos:** Business/PO (*what problem & what value?*) + Developer (*how do we build it?*) + Tester/QA (*what could go wrong / edge cases?*). Output = **shared understanding + concrete examples**.
- **TDD is Red→Green→Refactor (inside-out, code level). BDD is Discover→Formulate→Automate (outside-in, behavior level).**

| | TDD | BDD |
|---|---|---|
| Focus | Code design | System behavior |
| Language | Programming language | Natural language |
| Audience | Developers | Everyone |
| Level | Unit tests | Acceptance tests |
| Start point | Method behavior | **User story** |
| Documentation | Coverage | **Living documentation** |

- **User story:** `As a [role] / I want [goal] / So that [benefit]` → **WHO / WHAT / WHY**.
- **INVEST:** **I**ndependent, **N**egotiable, **V**aluable, **E**stimable, **S**mall, **T**estable.
- **Given/When/Then AC:** GIVEN = precondition (multiple allowed via And) · WHEN = **usually a single action** · THEN = outcome (multiple allowed via And).
- **The 6 BDD anti-patterns:** writing scenarios after code · technical language in scenarios (`click button with CSS selector ".submit-btn"`) · scenario explosion · implementation details (`the database has product ID 12345`) · no business involvement · incidental details (`it is Monday, January 15th, 2024`).

## 1.7 Performance Testing Types (JMeter's conceptual layer)

| Type | Load Level | Duration | Goal | Finds |
|---|---|---|---|---|
| **Load** | Expected | 15–60 min | Meet SLAs | Baseline, bottlenecks |
| **Stress** | Beyond expected | Until failure | Find limits | Breaking point, failure modes |
| **Endurance / Soak** | Normal | **8–72 hours** | Find leaks | **Memory leaks**, resource exhaustion |
| **Spike** | Sudden variable (e.g. 500→5000→500) | Minutes | Handle surges | Auto-scaling, recovery |
| **Scalability** | Variable | Variable | Measure scaling | Horizontal vs vertical gains |

**Metrics & formulas (calculation questions live here):**
```
Error Rate = (Failed Requests / Total Requests) × 100
TPS  = Total Transactions / Test Duration (sec)     e.g. 15,000 / 300 = 50 TPS
RPS  = Total Requests / Test Duration (sec)         e.g. 45,000 / 300 = 150 RPS
Concurrent Users = TPS × Avg Response Time (sec)    e.g. 50 × 2 = 100 users   ← Little's Law
Expected Samples = Users × Loops × Samplers         e.g. 100 × 50 × 3 = 15,000
Ramp-up rate     = Ramp-up Period ÷ Threads         e.g. 60s ÷ 100 = 1 user every 0.6 s
```
**Thresholds:** Error rate — ideal <0.1%, acceptable <1%, concerning 1–5%, **critical >5%**. CPU — healthy <70%, warning 70–85%, **critical >85%**. Memory — <75% / 75–90% / >90%. Baseline tolerance **±10%**, minimum **3 runs**.

**Why percentiles beat averages:** `100ms, 100ms, 100ms, 5000ms` → Average **1325ms** (looks broken) but **P90 = 100ms** (real experience). Healthy spread ≈ **4× from median to P99**; **25× median at P99 = investigate outliers**.

**Bottleneck signature table (great interview answer):**

| Pattern | Likely cause |
|---|---|
| Response time rises as users rise | CPU saturation · thread pool exhaustion · DB connection limit |
| Throughput plateaus but response times spike | Application bottleneck · slow queries · external service latency |
| Error rate suddenly jumps | Memory exhaustion · connection pool drained · rate limiting |
| **Oscillating** response times | **Garbage collection** · background processes · auto-scaling |
| **Gradual** increase over a long run | **Memory leak** / resource exhaustion |
| Throughput plateau | **Max capacity reached — adding users won't help** |

---
---

# §2 — TOOL-BY-TOOL CORE BREAKDOWN

Each tool: **Purpose → Execution lifecycle → Top 5 concepts → Setup rules.**

## 2.1 POSTMAN (API client + scripting)

**Purpose:** GUI API client for building, sending, scripting and asserting HTTP requests; industry standard for exploration, manual API testing, and lightweight automated collections.

**⚙️ Execution lifecycle — memorize verbatim:**
```
1. Collection Pre-request Script
2. Folder Pre-request Script
3. Request Pre-request Script
   ─────── REQUEST SENT ───────
4. Response Received
5. Request-level Tests (post-response)
6. Folder-level Tests
7. Collection-level Tests
```

**Top 5 concepts:**
1. **Variable scope & resolution order** (highest → lowest): **Local (`pm.variables`) > Data (CSV/JSON in Runner) > Environment > Collection > Global.** *Narrower scope wins.*
2. **Pre-request vs Test scripts** — before-send (generate data, refresh token, sign request) vs after-response (assert, extract for chaining).
3. **`pm.test()` + `pm.expect()`** — Chai BDD assertion library.
4. **Request chaining** — `pm.environment.set("id", res.id)` in Test → `{{id}}` in the next request's URL.
5. **Environments** — Initial Value (shared with team) vs **Current Value (local only — put secrets here)**.

**Setup rules:** desktop app recommended (localhost, certs). Request tabs: Params · Authorization · Headers · Body · Scripts (Pre-request/Post-response) · Settings. Response tabs: Body · Cookies · Headers · **Test Results**. Console = `Cmd/Ctrl+Alt+C`.

**Auth types:** No Auth · API Key (header or query) · **Basic** (Base64 `user:pass`) · **Bearer** (JWT/OAuth) · OAuth 2.0 (grant type + token URL + client id/secret, auto-refresh) · Digest · **Inherit from parent**.

---

## 2.2 REST ASSURED (Java API automation)

**Purpose:** Open-source **Java** library for testing/validating REST APIs with a fluent **BDD Given/When/Then** syntax. Created by **Johan Haleby, 2010**. The de-facto Java API-testing standard.

**⚙️ Execution lifecycle / chain contract — THE highest-value table:**

| Call | Returns |
|---|---|
| `given()` | **`RequestSpecification`** |
| `when()` | **`RequestSender`** |
| `.get()/.post()/...` | **`Response`** |
| `then()` | **`ValidatableResponse`** |

Order rule: **validate, then extract.** `.extract()` terminates the chain.

**Top 5 concepts:**
1. `given().when().then()` fluent chain + Hamcrest matchers.
2. **RequestSpecification / ResponseSpecification** reuse — `RequestSpecBuilder` uses **`set*` / `add*`**; `ResponseSpecBuilder` uses **`expect*`**.
3. **GPath/JsonPath body expressions** — `.body("orders.find { it.id == 1 }.total", equalTo(100.00f))`; closure variable is **`it`**.
4. **POJO serialization/deserialization** via **Jackson** (`.body(user)` out, `.extract().as(User.class)` in).
5. **JSON Schema validation** = **contract testing** — `matchesJsonSchemaInClasspath("schemas/user-schema.json")`.

**Setup rules:** Maven groupId **`io.rest-assured`** (hyphen) but package **`io.restassured`** (no hyphen). Version **5.4.0**, `<scope>test</scope>`. Static imports:
```java
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static io.restassured.module.jsv.JsonSchemaValidator.*;
```
Globals: `RestAssured.baseURI` (URI all-caps) / `.basePath` / `.port` / `.reset()` in `@AfterAll`.

---

## 2.3 PYTHON `requests` + pytest (Python API automation)

**Purpose:** "HTTP for Humans" — the Python HTTP client, paired with pytest for assertions/fixtures.

**⚙️ Lifecycle:** `conftest.py` fixtures (session/function scope) → test function → plain `assert` → fixture teardown after `yield`.

**Top 5 concepts:**
1. **`json=` vs `data=`** — `json=` sets `Content-Type: application/json`; `data=` sets `application/x-www-form-urlencoded`.
2. **Response object members** — `.status_code`, `.text` (str), `.content` (bytes), `.json()` (**method**), `.headers`, `.ok`, `.elapsed` (timedelta), `.raise_for_status()`.
3. **Sessions** — persist headers/cookies/auth across requests; use as a context manager.
4. **Exception hierarchy** — `RequestException` is the **base**; catch it **last**. Order: `ConnectionError → Timeout → HTTPError → RequestException`.
5. **pytest fixtures** — everything before `yield` is setup, after `yield` is teardown; `conftest.py` fixtures are auto-discovered (no import).

**Setup rules:** `pip install requests pytest`. pytest discovery: files **`test_*.py`**, classes **`Test*`**, functions **`test_*`**. `pytest -m marker`, `pytest -n 4` (pytest-xdist parallel), `pytest --html=report.html`, `pytest --alluredir=allure-results` then `allure serve`. Test IDs use `file::Class::method`.
Key libs: `requests`, `pytest`, `jsonschema`, `pydantic`, `pytest-html`, `pytest-xdist`, `allure-pytest`, `faker`, `python-dotenv`.

---

## 2.4 JMETER (performance/load)

**Purpose:** Open-source, **Java-based** load-testing tool. Simulates thousands of virtual users across HTTP/REST/SOAP/JDBC/FTP/JMS/TCP.

**⚙️ Test plan hierarchy (nesting order matters):**
```
Test Plan
└── Thread Group                    ← virtual users
    ├── Config Elements             (HTTP Request Defaults, CSV Data Set Config, HTTP Header Manager)
    ├── Pre-Processors              (User Parameters)
    ├── Samplers                    (HTTP Request, JDBC Request…)
    ├── Post-Processors             (JSON Extractor)
    ├── Assertions                  (Response Assertion, Duration Assertion)
    ├── Timers                      (Constant, Uniform Random)
    └── Logic Controllers           (Loop, If, ForEach)
└── Listeners                       (Summary Report, Aggregate Report, View Results Tree)
```

**Top 5 concepts:**
1. **The 10 element types** — Test Plan, Thread Group, Sampler, Logic Controller, Config Element, Pre-Processor, Post-Processor, Assertion, Timer, Listener.
2. **Thread Group settings** — Number of Threads (users), **Ramp-up Period**, Loop Count, Scheduler Duration, Startup Delay. Sampler-error actions: **Continue (default)** / Start Next Thread Loop / Stop Thread / Stop Test / Stop Test Now.
3. **GUI = design, CLI = execute.** GUI ~500MB RAM, 15–20% CPU, max ~200 threads. CLI ~200MB, 2–5% CPU, 1000+ threads.
4. **Listeners** — **Summary Report has NO percentiles; Aggregate Report ADDS Median/90%/95%/99%.** View Results Tree = debugging only, **must be disabled during load tests**.
5. **Parameterization** — User Defined Variables `${BASE_URL}`, properties `${__P(threadCount,10)}` fed by `-JthreadCount=100`, built-in functions `${__Random(1,100)}`, `${__UUID()}`, `${__counter()}`, `${__threadNum}`, `${__time()}`, `${__CSV(file)}`.

**Setup rules:** Java **8+ (11+ recommended)**; test plan = **`.jmx`**, results = **`.jtl`**. Thread group types include **setUp** (runs before) and **tearDown** (runs after) Thread Groups.

---

## 2.5 SELENIUM WEBDRIVER (Java & Python)

**Purpose:** Open-source cross-browser UI automation. Drives real browsers as a real user would.

**⚙️ Architecture / lifecycle:**
```
Test Code → WebDriver API → (serialize to JSON over HTTP, W3C protocol)
         → Browser Driver (ChromeDriver, port 9515) → Browser
```
Historically: **Selenium Core (2004) → RC (2006, server + JS injection) → WebDriver (2008, direct) → Selenium 2 (2011, merged, RC deprecated) → W3C recommendation (2016) → Selenium 4 (2021: relative locators, CDP, new Grid).**

**Top 5 concepts:**
1. **Locator strategies (8):** `id, name, className, tagName, linkText, partialLinkText, cssSelector, xpath`. Priority: **ID > name/data-testid > CSS > XPath > className > linkText > tagName.**
2. **`findElement` vs `findElements`** — single `WebElement` + **throws `NoSuchElementException`** vs `List<WebElement>` + **returns empty list**.
3. **Waits** — implicit (global, existence-only) · explicit (`WebDriverWait` + `ExpectedConditions`, condition-specific) · fluent (timeout + polling + ignored exceptions). **Never mix implicit and explicit.** Never `Thread.sleep()`.
4. **Page Object Model** — one class per page; **locators private**; methods return page objects (new page after navigation, `this` if you stayed); **no assertions inside page objects**; waits live in the page object.
5. **Context switching** — you must `switchTo()` for alerts, frames and windows before interacting.

**Setup rules:**
- Java manual: `System.setProperty("webdriver.chrome.driver", path)` — keys: `webdriver.chrome.driver`, **`webdriver.gecko.driver`** (Firefox), `webdriver.edge.driver`.
- Java automated: **WebDriverManager 5.6.2** — `WebDriverManager.chromedriver().setup();`
- Python manual: `Service(executable_path="...")` then `webdriver.Chrome(service=service)`.
- Python automated: `Service(ChromeDriverManager().install())` — **`.install()` returns a path string**.
- Selenium **4.15.0**, JUnit **5.10.0**, Java 17. `selenium-java` aggregates api/remote/chrome/firefox/edge/safari/support.
- **Selenium Grid 4** = **Router** (entry) → **Distributor** + **Session Map** + **New Session Queue** → **Nodes**. Hub URL `http://localhost:4444/wd/hub`. Connect with `RemoteWebDriver`.
- **Selenium IDE** = record/playback browser extension (rebuilt 2018), exports to JUnit/TestNG/pytest/Mocha/NUnit/RSpec/`.side`; weakness = **generates absolute XPath**, brittle, no POM.

---

## 2.6 PLAYWRIGHT (Java)

**Purpose:** **Microsoft's** modern browser automation library (**2020**) — one API across **Chromium, Firefox, WebKit** with built-in auto-waiting, tracing, video and network interception.

**⚙️ Object model & lifecycle:**
```
Playwright → Browser → BrowserContext → Page → Locator / Frame

1. Playwright.create()
2. playwright.chromium().launch()
3. browser.newContext()
4. context.newPage()
5. interact
6. CLEANUP IN REVERSE: page → context → browser → playwright
```
JUnit mapping: **`@BeforeAll` (static) = Playwright + Browser** (expensive, shared) · **`@BeforeEach` = Context + Page** (cheap, isolated) · `@AfterEach` = `context.close()` · `@AfterAll` = browser then playwright.

**Top 5 concepts:**
1. **Auto-waiting / actionability checks.** `click()` waits for: **attached to DOM · visible · stable (not animating) · enabled · receives events (not obscured)** (5). `fill()` waits for: **attached · visible · editable** (3).
2. **Web-first assertions auto-retry** — `assertThat(page).hasTitle(...)`, default timeout **5 seconds**.
3. **BrowserContext = isolated incognito session.** Contexts share **nothing**: cookies, localStorage, sessionStorage, cache. This is how Playwright parallelizes **without a Grid**.
4. **Preferred locators:** `getByTestId()` > `getByRole()` > `getByLabel()` > CSS. Avoid structural CSS (`div:nth-child(3) > button`).
5. **Trace Viewer** — `context.tracing().start(...)` / `.stop(path)`; a `.zip` containing screenshots, DOM snapshots, network, console, timeline, source.

**Setup rules:** Maven `com.microsoft.playwright:playwright:1.40.0`. Browsers installed via
`mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"` (add **`--with-deps`** in CI).
Cache: `~/.cache/ms-playwright` (Linux), `~/Library/Caches/ms-playwright` (mac), `%USERPROFILE%\AppData\Local\ms-playwright` (Win).
Static import: `import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;`

---

## 2.7 CUCUMBER (Java BDD)

**Purpose:** BDD tool that runs human-readable **Gherkin** specs as automated acceptance tests. Created **2008 by Aslak Hellesøy for Ruby** (from RSpec's Story Runner); **Cucumber-JVM 2011**; **Rule keyword arrived in Cucumber 6 (2020)**.

**⚙️ Architecture & hook execution order:**
```
Feature File (.feature)  →  Step Definitions (glue code)  →  Application

@BeforeAll                       (static, once)
  @Before                        (each scenario)
    @BeforeStep / Step / @AfterStep   (each step)
  @After                         (each scenario)
@AfterAll                        (static, once)
```
**`order` rule: `@Before` = LOWER order runs FIRST (1,2,3). `@After` = HIGHER order runs FIRST (3,2,1).**

**Top 5 concepts:**
1. **Gherkin keywords:** Feature, Rule, Background, Scenario (= **Example**), Scenario Outline + **Examples:**, Given/When/Then/And/But, Data Tables, Doc Strings (`"""`), tags `@name`.
2. **Cucumber Expressions:** `{int} {long} {float} {double} {word} {string} {} {bigdecimal} {biginteger}`. `{string}` consumes the quotes.
3. **Hooks vs Background** — Background is *visible business setup in the feature file*, per feature; hooks are *hidden technical setup in Java*, can be global or tagged.
4. **Tags + tag expressions** — `and` / `or` / `not` / `()`, all **lowercase words**. Tags **cascade from Feature to every Scenario**.
5. **`@CucumberOptions`** — `features`, `glue`, `plugin`, `tags`, `dryRun`, `strict`, `monochrome`, `name`.

**Setup rules:** Cucumber **7.14.0**. Features in `src/test/resources/features`, steps in `src/test/java/.../stepdefinitions`.
- JUnit 4: `@RunWith(Cucumber.class)` + `@CucumberOptions`, artifact **`cucumber-junit`**.
- JUnit 5: `@Suite` + `@IncludeEngines("cucumber")` + `@SelectClasspathResource("features")` + `@ConfigurationParameter(...)`, artifact **`cucumber-junit-platform-engine`**.
- Config precedence: **Command line > System properties > properties file**.
- CLI main class: **`io.cucumber.core.cli.Main`**.

---

## 2.8 BEHAVE (Python BDD)

**Purpose:** Python's Cucumber equivalent — Gherkin feature files with Python step functions. Version **1.2.6**.

**⚙️ Hook execution order (plain functions in `features/environment.py` — NO decorators):**
```
before_all
  before_feature
    before_scenario
      before_step / STEP / after_step
    after_scenario
  after_feature
after_all
(+ before_tag / after_tag)
```

**Top 5 concepts:**
1. **`context` object** carries all state; **first parameter of every step function**; **reset between scenarios** (persist via `before_all` or `context._root`).
2. **Decorators** `@given / @when / @then` and the universal **`@step`** (matches all three).
3. **Parse-format params:** `{name}` (str), **`{name:d}`** (int), **`{name:f}`** (float), custom via `register_type` + `@parse.with_pattern`.
4. **`context.table`** = data table (`row['col']`, `.headings`, `.rows`); **`context.text`** = doc string.
5. **No runner class** — the `behave` CLI *is* the runner; `features/steps/` is auto-discovered (no glue setting).

**Setup rules:** `pip install behave selenium webdriver-manager allure-behave`.
```
project/
├── features/
│   ├── login.feature
│   ├── steps/            ← *_steps.py, auto-discovered
│   └── environment.py    ← hooks (BESIDE steps/, not inside)
├── behave.ini            ← [behave] and [behave.userdata]
```
Config precedence: **command line > behave.ini/.behaverc > setup.cfg**. Userdata via `-D key=value` → `context.config.userdata` (**all values arrive as strings**).

---
---

# §3 — CROSS-TOOL COMPARISONS (the cross-examination layer)

## 3.1 Selenium vs Playwright ⭐

| Feature | Playwright | Selenium |
|---|---|---|
| **Auto-waiting** | **Built-in, intelligent** | **Manual waits needed** |
| **Architecture** | **Single process, WebSocket** (direct) | **WebDriver protocol, separate driver process** |
| Hops | Test → Playwright → Browser | Test → WebDriver → Browser Driver → Browser |
| **Browsers** | Chromium, Firefox, **WebKit** (3 engines) | Chrome, Firefox, Safari, Edge, **IE** |
| **Parallel execution** | **Native browser contexts** (no Grid) | **Requires Grid**/external tools |
| Network interception | Built-in (`page.route()`) | Limited, extension needed |
| Mobile emulation | Built-in | Limited |
| Video recording | Built-in (context option) | External tools |
| Tracing | **Built-in Trace Viewer** | Third-party |
| Language support | JS, Python, Java, C# | **Many languages** ← Selenium wins here |
| Maturity | Newer (**2020**) | Mature (**2004**) |
| Assertions | **Auto-retrying** (`assertThat`, 5s default) | Static assert on `getText()` — can fail on timing |

**Choose Playwright:** new framework, need built-in video/tracing, simplified waiting, parallel without Grid, modern SPAs, network mocking.
**Choose Selenium:** existing Selenium infra, need IE/legacy browsers, team familiarity, Selenium-based tooling (**Appium** for mobile), broader language support.

**Code contrast to quote:**
```java
// Selenium
new WebDriverWait(driver, Duration.ofSeconds(10))
    .until(ExpectedConditions.elementToBeClickable(By.id("submit"))).click();
// Playwright
page.locator("#submit").click();   // auto-waits until actionable
```

## 3.2 Cucumber vs Behave ⭐

| Dimension | Cucumber (Java) | Behave (Python) |
|---|---|---|
| Feature files | `.feature` | `.feature` |
| Gherkin | Full (incl. **Rule**) | Full Gherkin (**Rule not covered in course**) |
| Feature dir | `src/test/resources/features` | **`features/`** |
| Step dir | `.../stepdefinitions` (declared via **`glue`**) | **`features/steps/`** (auto-discovered, no glue) |
| Step marker | **Annotations** `@Given/@When/@Then` | **Decorators** `@given/@when/@then` + **`@step`** |
| Universal keyword | none | **`@step`** matches Given/When/Then |
| Param tokens | **`{int} {string} {double} {word}`** | **`{name:d}` `{name:f}` `{name}`** |
| Quotes | `{string}` **consumes** the quotes | quotes typed **literally**: `'I enter "{username}"'` |
| Data table | **`DataTable`** → `.asMaps()` → `List<Map<String,String>>` | **`context.table`** → `row['col']`, `.headings`, `.rows` |
| Doc string | extra `String` method parameter | **`context.text`** |
| Hooks | `@Before/@After/@BeforeStep/@AfterStep/@BeforeAll/@AfterAll` in any glue class | **plain functions** in **`features/environment.py`** |
| Feature-level hook | **none** | **`before_feature` / `after_feature` exist** |
| Tag hook | `@Before("@ui")` | `before_tag(context, tag)` |
| Hook ordering | explicit **`order = n`** attribute | no order attribute — file/statement order |
| Tag expressions | `and` / `or` / `not` / `()` | **identical** syntax |
| Tag test in code | `scenario.getSourceTagNames()` (**with `@`**) | `scenario.effective_tags` (**WITHOUT `@`**) |
| State sharing | **Dependency injection** (constructor `TestContext`) | **`context` object** (reset between scenarios) |
| Config | `@CucumberOptions` / `cucumber.properties` | `behave.ini` / `.behaverc` / `setup.cfg` |
| Runtime data | `-Dkey=value` system props | **`-D key=value`** → `context.config.userdata` |
| Undefined step throws | **`PendingException`** | **`NotImplementedError`** |
| Runner | **Runner class required** | **None — `behave` CLI is the runner** |
| Parallel | built-in `cucumber.execution.parallel.*` | **"Limited built-in"** → `behave-parallel` |
| Allure | `allure-cucumber7-jvm` | `allure-behave` (`allure_behave.formatter:AllureFormatter`) |

**Same Gherkin, different glue:**
```gherkin
Given the product catalog contains "Widget"
```
```java
@Given("the product catalog contains {string}")
public void catalogContains(String product) { catalog.add(product); }
```
```python
@given('the product catalog contains "{product}"')
def catalog_contains(context, product): context.catalog.add(product)
```

## 3.3 Selenium Java vs Selenium Python ⭐ (a whole quiz section lives here)

| Operation | Java | Python |
|---|---|---|
| Find one | `driver.findElement(By.id("x"))` | `driver.find_element(By.ID, "x")` — **2 args, By.ID is a CONSTANT** |
| Find many | `findElements` → `List<WebElement>` | `find_elements` → `list` (`[]` if none) |
| Type | `sendKeys("t")` | `send_keys("t")` |
| Get text | `getText()` | **`.text`** (property, no parens) |
| Get attribute | `getAttribute("href")` | `get_attribute("href")` |
| CSS value | `getCssValue("color")` | **`value_of_css_property("color")`** |
| Navigate history | **`driver.navigate().back()`** | **`driver.back()`** — no `navigate()` in Python |
| URL / Title | `getCurrentUrl()` / `getTitle()` | **`current_url` / `title`** (properties) |
| Page source | `getPageSource()` | `page_source` (property) |
| Implicit wait | `manage().timeouts().implicitlyWait(Duration.ofSeconds(10))` | **`implicitly_wait(10)`** (plain seconds) |
| Explicit wait | `new WebDriverWait(driver, Duration.ofSeconds(10))` | `WebDriverWait(driver, 10)` |
| Condition | `ExpectedConditions.elementToBeClickable(By.id("x"))` | **`EC.element_to_be_clickable((By.ID, "x"))`** ← double parens |
| Fluent wait | separate **`FluentWait`** class `.withTimeout().pollingEvery().ignoring()` | **no FluentWait class** — `WebDriverWait(driver, timeout=, poll_frequency=, ignored_exceptions=)` |
| Actions | `new Actions(driver)…build().perform()` | `ActionChains(driver)…perform()` — **no `build()`** |
| Right-click | `contextClick()` | `context_click()` |
| Hover | `moveToElement()` | `move_to_element()` |
| Window handles | `getWindowHandle()` / `getWindowHandles()` (**Set**) | `current_window_handle` / `window_handles` (**list, properties**) |
| Switch | `driver.switchTo().window(h)` | **`driver.switch_to.window(h)`** — property, no `()` |
| Frame exit | `switchTo().defaultContent()` / `parentFrame()` | `switch_to.default_content()` / `switch_to.parent_frame()` |
| Alert text | `alert.getText()` | `alert.text` |
| Select | `new Select(el).selectByVisibleText("US")` | `Select(el).select_by_visible_text("US")` |
| Options class | **real classes** `ChromeOptions`/`FirefoxOptions` | class is literally **`Options`** in each package — `ChromeOptions` is just an `as` alias |
| Screenshot | cast to **`TakesScreenshot`**, `getScreenshotAs(OutputType.FILE/BYTES/BASE64)` | `driver.save_screenshot()`, `get_screenshot_as_png()`, `get_screenshot_as_base64()` |
| Element screenshot | `element.getScreenshotAs(...)` | `element.screenshot("f.png")` (method) but **`element.screenshot_as_png`** (property) |
| Driver setup | `System.setProperty("webdriver.chrome.driver", p)` / `WebDriverManager.chromedriver().setup()` | `Service(path)` / `Service(ChromeDriverManager().install())` |

## 3.4 PUT vs PATCH · Path vs Query params

| | PUT | PATCH |
|---|---|---|
| Semantics | Replace the **ENTIRE** resource | Apply **partial** modifications |
| Body | Complete resource, all fields | Only changed fields |
| Missing fields | **May be wiped/reset** | **Left untouched** |
| Idempotent | **Yes** | **Not necessarily** |
| Special format | — | JSON Patch **RFC 6902**, content type **`application/json-patch+json`**, ops `replace`/`add` |

| | Path Parameter | Query Parameter |
|---|---|---|
| Purpose | **Identify a resource** | **Filter / sort / paginate** |
| Position | In the URL path | After `?` |
| Required? | Usually required | Usually optional |
| Example | `/users/123` | `/users?active=true&page=2` |
| Postman | `:userId` colon syntax | Params tab |
| REST Assured | `.pathParam("userId",123)` + `.get("/users/{userId}")` | `.queryParam("status","active")` |
| Python | f-string in URL | **`params={"page":1}`** |

## 3.5 Implicit vs Explicit vs Fluent Waits vs Playwright auto-wait ⭐

| | Implicit | Explicit | Fluent | Playwright auto-wait |
|---|---|---|---|---|
| Scope | **Global**, all `findElement` calls | **One specific condition** | One condition + tuned polling | **Every action, automatic** |
| Waits for | **Existence in the DOM only** | Any `ExpectedConditions` (visible, clickable, text, URL, frames, alerts…) | Same + custom lambdas | attached/visible/stable/enabled/receives-events |
| Poll interval | ~**500 ms** (default) | ~500 ms | **configurable** (`pollingEvery`/`poll_frequency`) | internal |
| Ignore exceptions | n/a | default set | **explicit `.ignoring(...)` / `ignored_exceptions=`** | n/a |
| Throws on failure | **`NoSuchElementException`** | **`TimeoutException`** | **`TimeoutException`** | action timeout |
| Java | `driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10))` | `new WebDriverWait(driver, Duration.ofSeconds(10)).until(...)` | `new FluentWait<>(driver).withTimeout().pollingEvery().ignoring()` | `page.locator("#x").click()` |
| Python | `driver.implicitly_wait(10)` | `WebDriverWait(driver,10).until(EC...)` | `WebDriverWait(driver, timeout=30, poll_frequency=0.5, ignored_exceptions=[...])` | — |

🚨 **Never mix implicit + explicit waits — "unpredictable behavior."** 🚨 **Never use `Thread.sleep()` / `time.sleep()`** — always waits the full duration.
🚨 Implicit wait does **not** help a hidden element → you still get **`ElementNotInteractableException`**.

## 3.6 XPath vs CSS Selectors ⭐

| Aspect | XPath | CSS |
|---|---|---|
| **Text matching** | ✅ `text()`, `contains(text(),...)` | ❌ **impossible** |
| **Parent navigation** | ✅ `parent::`, `..`, `ancestor::` | ❌ |
| Sibling navigation | ✅ full control (`following-sibling::`, `preceding-sibling::`) | Limited (`+` adjacent, `~` general) |
| Ends-with | ❌ **XPath 1.0 has NO `ends-with()`** — use `contains()` | ✅ **`[attr$='val']`** |
| Starts-with | ✅ `starts-with()` | ✅ `[attr^='val']` |
| Contains | ✅ `contains()` | ✅ `[attr*='val']` |
| Index base | **1-based** (`(//input)[1]` = first) | `:nth-child(n)` is 1-based |
| Syntax | Verbose | Concise |
| Performance | Slightly slower | Slightly faster |

**Absolute vs Relative XPath:** absolute starts with `/` (`/html/body/div[1]/...`) — faster but **extremely brittle, generated by recorders, never use in production**. Relative starts with `//` — robust, recommended.
**XPath functions taught:** `contains()`, `starts-with()`, `text()`, `normalize-space()`, `translate()` (case-insensitive trick), `last()`, `position()`.
**Axes:** `parent::`, `ancestor::`, `child::`, `descendant::`, `following-sibling::`, `preceding-sibling::`, `following::`, `preceding::` — **double colon**.

## 3.7 Assertion styles across the whole stack

| Tool | Syntax | Style |
|---|---|---|
| Postman | `pm.expect(x).to.equal(y)` / `pm.response.to.have.status(200)` | Chai BDD |
| REST Assured | `.body("name", equalTo("Test"))` | Hamcrest matchers |
| JUnit 5 | `assertEquals(expected, actual, "message")` | **message LAST** |
| JUnit 4 | `assertTrue("message", condition)` | **message FIRST** |
| pytest | `assert x == y, f"msg"` | bare `assert` |
| Behave | `assert x in y, f"msg"` | bare `assert` |
| Playwright | `assertThat(locator).hasText("x")` | web-first, **auto-retrying (5s)** |
| JMeter | Response Assertion / Duration Assertion | GUI element |

## 3.8 Reporting & parallelism across tools

| Tool | Report plugins/formats | Parallel mechanism |
|---|---|---|
| Postman | Test Results tab / Collection Runner | Collection Runner iterations |
| REST Assured/JUnit5 | Surefire, `allure-junit5` + `allure-rest-assured` | `junit-platform.properties` (`parallel.enabled=true`, `strategy=fixed`, `parallelism=4`) + Surefire `<parallel>methods</parallel>` |
| pytest | `pytest-html`, `allure-pytest` | **`pytest-xdist`** → `pytest -n 4` |
| JMeter | HTML dashboard (`-e -o`), `.jtl` | **Thread Groups**; distributed via `-r`/`-R` + `jmeter-server` |
| Selenium | Surefire XML, Allure attachments | Surefire parallel + **Selenium Grid**; `ThreadLocal<WebDriver>` |
| Cucumber | `pretty progress html json junit rerun timeline usage` | `cucumber.execution.parallel.*` (`dynamic`/`fixed`) |
| Behave | `pretty plain progress json json.pretty junit rerun null` | **limited** → `behave-parallel --processes=4` |
| Playwright | Trace zip, video `.webm`, screenshots | **BrowserContexts** (no Grid needed) |

## 3.9 Setup-command Rosetta Stone

| Tool | Install / run |
|---|---|
| Postman | download app / `web.postman.co` |
| REST Assured | `io.rest-assured:rest-assured:5.4.0` (test scope) |
| requests/pytest | `pip install requests pytest`; `pytest -v -m smoke -n 4` |
| JMeter | unzip; `jmeter -n -t test.jmx -l results.jtl -e -o report` |
| Selenium Java | `org.seleniumhq.selenium:selenium-java:4.15.0`; `mvn test -Dtest=LoginTest#testValidLogin` |
| Selenium Python | `pip install selenium webdriver-manager` |
| Cucumber | `io.cucumber:cucumber-java:7.14.0` + `cucumber-junit-platform-engine`; `mvn test -Dcucumber.filter.tags="@smoke"` |
| Behave | `pip install behave`; `behave --tags=@smoke -f pretty` |
| Playwright | `com.microsoft.playwright:playwright:1.40.0` + `exec.args="install"`; codegen via `exec.args="codegen <url>"` |

---
---

# §4 — MULTIPLE-CHOICE TRAP DRILL (read these out loud)

## 4.1 API / Postman traps
1. **401 vs 403.** 401 = "I don't know who you are" (authentication). 403 = "I know you, you're not allowed" (authorization). A logged-in non-admin hitting `/admin` must get **403**.
2. **Authentication ≠ Authorization.** Identity vs permission. IDOR is an **authorization** defect.
3. **POST → 201**, DELETE → **204**, validation failure → **400** (or **422** = Validation Error), rate limit → **429** (not 403), downstream timeout → **504**.
4. **Variable priority is Local > Data > Environment > Collection > Global.** Global is the *lowest*, not highest.
5. **Pre-request script** is where you set a value used *in the outgoing request*. Tests run too late.
6. **Collection pre-request runs BEFORE folder, which runs BEFORE request.** For tests it's request → folder → collection.
7. **Initial Value is shared with the team; Current Value is local.** Secrets go in Current Value only.
8. **Basic Auth is Base64 — encoding, not encryption.** Fully reversible.
9. `pm.response.json()` is a **method**; `pm.response.responseTime` is a property in **milliseconds**.
10. **PUT is idempotent, PATCH is not.** GET/PUT/DELETE/HEAD/OPTIONS idempotent; **POST and PATCH are not**.

## 4.2 REST Assured traps
11. **groupId `io.rest-assured` (hyphen), package `io.restassured` (no hyphen).**
12. `given()`→`RequestSpecification`, `when()`→`RequestSender`, verb→`Response`, `then()`→`ValidatableResponse`.
13. **`RequestSpecBuilder` uses `set*`/`add*`; `ResponseSpecBuilder` uses `expect*`.**
14. **`RestAssured.baseURI`** (all-caps URI) as a static field vs **`given().baseUri()`** (camelCase) as an instance method.
15. **`.auth().preemptive().basic(...)`** = send credentials immediately without waiting for the 401 challenge.
16. Hamcrest: **`contains` = exact order AND exact contents**; `containsInAnyOrder` = same set, any order; `hasItem` = one member; `hasItems` = several; `hasSize(n)` = size. **`empty()`** = collection, **`emptyString()`** = String.
17. `.time(lessThan(2000L))` — **milliseconds with an `L` suffix**.
18. GPath closure variable is **`it`**: `orders.find { it.id == 1 }.total`. Money matchers need the `f` suffix: `equalTo(250.50f)`.
19. **`matchesJsonSchemaInClasspath("…")`** vs `matchesJsonSchema(File|String)`.
20. **Validate before you extract** — `.extract()` ends the chain.
21. URL encoding is **ON by default**; disable with `.urlEncodingEnabled(false)`.
22. Multipart uses **`.multiPart()`** (capital P), not `.body()`. Form data uses `ContentType.URLENC` + `.formParam()`.
23. **`@TestInstance(Lifecycle.PER_CLASS)` allows non-static `@BeforeAll`.**

## 4.3 Python requests / pytest traps
24. **`json=` sets application/json; `data=` sets x-www-form-urlencoded.**
25. **`.text` = str, `.content` = bytes, `.json()` = parsed (a METHOD).** `.ok` is True for 2xx.
26. **`.elapsed` is a `timedelta`** — use `.total_seconds()`.
27. **`raise_for_status()` raises `HTTPError` for 4xx/5xx.** `RequestException` is the **base** — catch it last.
28. **`timeout=(3, 10)` is (connect, read).** `timeout=None` = no timeout, "not recommended."
29. `Retry` comes from **`urllib3.util.retry`**, with `status_forcelist=[500,502,503,504]` and `backoff_factor=1` → waits 1, 2, 4 s.
30. pytest discovery: **`test_*.py` / `Test*` / `test_*`**. `-m` = marker, **`-n` = parallel (xdist)**, `::` separates file::class::method.
31. In a fixture, **everything before `yield` is setup, after `yield` is teardown**. `conftest.py` fixtures need no import.

## 4.4 JMeter traps
32. **`-n` non-GUI · `-t` .jmx · `-l` .jtl · `-e` generate report · `-o` output folder · `-j` JMeter log file · `-J` JMeter PROPERTY · `-G` global property · `-D` system property · `-g` report from existing jtl · `-q` property file · `-r`/`-R`/`-X` remote.** Lowercase `-j` ≠ uppercase `-J`.
33. **`-o` folder must be empty or non-existent.**
34. **Test plan = `.jmx`, results = `.jtl`.**
35. **Aggregate Report has Median/90%/95%/99%; Summary Report does NOT.**
36. **View Results Tree must be disabled during load tests** (resource intensive).
37. **Ramp-up:** 100 threads over 60 s = **1 new user every 0.6 s**. 50 threads over 50 s = 1/second.
38. **Uniform Random Timer total delay = Constant Offset + Random(0, Max)** → offset 1000 + max 5000 = **1000–6000 ms**.
39. Default sampler-error action is **Continue**.
40. **Endurance/soak = 8–72 h and finds memory leaks.** Stress runs until failure and finds limits. Load = 15–60 min against SLAs.
41. **setUp Thread Group runs before, tearDown Thread Group runs after** the main thread groups.
42. **A spike test is built with three Thread Groups** (baseline → spike → back down).
43. Content-Type belongs in the **HTTP Header Manager**, not the sampler.

## 4.5 Selenium traps (Java + Python)
44. **`findElements`/`find_elements` NEVER throws `NoSuchElementException`** — it returns an empty list.
45. **`By.className("btn btn-primary")` FAILS** — className takes exactly one class. Use `By.cssSelector(".btn.btn-primary")`.
46. **`linkText`/`partialLinkText` only work on `<a>` and are case-sensitive.**
47. **`parentFrame()` = one level up; `defaultContent()` = all the way to the main page.**
48. **`close()` closes the current window; `quit()` closes all windows and ends the session.** After `close()` you must explicitly `switchTo().window(...)`.
49. **`getWindowHandle()` = String; `getWindowHandles()` = `Set<String>`** (Java) / `window_handles` = **list** (Python).
50. **Actions do nothing until `.perform()`.** `.build()` creates a reusable `Action` **without** executing. **Python has no `.build()`.**
51. **Firefox headless = `-headless` (single dash); Chrome/Edge = `--headless=new` (double dash, Chrome 109+).** Firefox private = `-private`, Chrome = `--incognito`, Edge = `--inprivate`.
52. **`add_experimental_option()` is Chrome/Edge only; `set_preference()` is Firefox only.**
53. **`selectByIndex` / `select_by_index` is 0-based.** `deselect*()` throws **`UnsupportedOperationException`** on a single-select. `Select` only works on real `<select>` tags.
54. **`getAttribute("value")` for input contents, NOT `getText()`.** Boolean attributes return `"true"` or **`null`** — never `"false"`. Python `get_attribute` returns **`None`** if absent.
55. **XPath `text()` = direct text only; `.` (dot) includes descendant text.** `<span>Hello <b>World</b></span>` → `//span[text()='Hello World']` fails; `//span[contains(., 'Hello World')]` matches.
56. **`starts-with(@id,'btn')` does NOT match `id="submit-btn"`.** **XPath 1.0 has no `ends-with()`.**
57. **Searching inside an element requires the leading dot**: `element.find_element(By.XPATH, ".//td[1]")` — `"//td[1]"` searches the whole document.
58. **XPath index is 1-based; frame index is 0-based; `.nth()` in Playwright is 0-based.**
59. **`(//input)[2]`** = 2nd input document-wide; **`//input[2]`** = 2nd input *within each parent*.
60. **`@FindBys` = AND (chained); `@FindAll` = OR.** `PageFactory.initElements(driver, this)` must be in the constructor; elements are **lazily loaded**.
61. **No assertions inside Page Objects.** Locators stay **private**. Methods return page objects.
62. **`OutputType.FILE` returns a TEMP file** — you must `FileUtils.copyFile` / `Files.copy` it.
63. **Python `EC` conditions take a TUPLE**: `EC.element_to_be_clickable((By.ID,"x"))` — double parens. `EC.visibility_of(element)` takes an element, `EC.visibility_of_element_located(locator)` takes a tuple.
64. **Python has no `FluentWait` class** — use `WebDriverWait(driver, timeout=, poll_frequency=, ignored_exceptions=)`.
65. **`driver.switch_to` is a property** — `switch_to.window(h)`, never `switch_to().window(h)`.
66. **Deprecated `find_element_by_id()` family: deprecated in 4.0, REMOVED in 4.3.0.** Same for `executable_path=` on the driver constructor (use `Service`).
67. Python driver-manager classes: **`GeckoDriverManager`** (Firefox), **`EdgeChromiumDriverManager`** from **`webdriver_manager.microsoft`**. `.install()` returns a **path string** and must be wrapped in `Service(...)`.
68. System property keys: `webdriver.chrome.driver`, **`webdriver.gecko.driver`** (NOT `webdriver.firefox.driver`), `webdriver.edge.driver`.
69. Page load strategies: **NORMAL / EAGER (DOMContentLoaded) / NONE.**
70. `options.timeouts` values are in **milliseconds**; `implicitly_wait(10)` / `set_page_load_timeout(30)` are in **seconds**.

## 4.6 Cucumber traps
71. **`Example:` is an alias for `Scenario:` — NOT for `Examples:`.** `Examples:` is the Scenario Outline data table.
72. **Background re-runs before EVERY scenario**, not once per feature. Keep it to **1–3 steps**.
73. **A feature-level Background AND a rule-level Background can coexist**; the Rule one applies only inside that Rule.
74. **`Rule` requires Cucumber 6+.**
75. **`But` is functionally identical to `And`** — readability only. **`And`/`But` inherit the previous Given/When/Then meaning.**
76. **`@After` hooks: HIGHER order runs FIRST. `@Before`: LOWER order runs FIRST.**
77. **`@BeforeAll`/`@AfterAll` must be `static`.**
78. **`monochrome = true` REMOVES color** (for CI logs).
79. **`dryRun` does not execute steps** — it only verifies step mappings. **`strict = false` marks undefined steps as skipped**; `true` fails the build.
80. **Property is `cucumber.filter.tags`, not `cucumber.tags`** (and `cucumber.filter.name`).
81. **Tag operators are lowercase words `and` / `or` / `not` / `()`** — not `&&`, `||`, `!`.
82. **Tags cascade from Feature down to every Scenario.**
83. **`glue` points to PACKAGES (dotted); `features` points to PATHS (slashed).**
84. **JUnit 4 = `cucumber-junit` + `@RunWith(Cucumber.class)`; JUnit 5 = `cucumber-junit-platform-engine` + `@Suite` + `@IncludeEngines("cucumber")` + `@SelectClasspathResource("features")` + `@ConfigurationParameter`.**
85. **JUnit 4 assertion message goes FIRST; JUnit 5 message goes LAST.**
86. Rerun runner uses `features = "@target/cucumber-reports/rerun.txt"` — **the leading `@` is required**.
87. `{word}` = single unquoted token; **`{string}` = a quoted string (and consumes the quotes)**.
88. `progress` plugin legend: **`.` = pass, `F` = fail**.
89. CLI main class = **`io.cucumber.core.cli.Main`**.
90. Placeholders `<name>` must match the Examples header **exactly and case-sensitively**.

## 4.7 Behave traps
91. **Hooks are plain module-level functions with fixed names in `features/environment.py` — no decorators.** (`@before_scenario` is a fake.)
92. **Behave HAS `before_feature`/`after_feature`; Cucumber-Java has no feature-level hook.**
93. **Every step function's first parameter is `context`.**
94. **Type conversion is `{name:d}` / `{name:f}`**, not `{int}` / `{float}`.
95. **Quotes must be written literally** in a Behave decorator: `@when('I enter username "{username}"')`.
96. **`context.table` = data table; `context.text` = doc string.** Swapping these is the #1 Behave distractor.
97. **Tag checks in Python drop the `@`**: `if 'ui' in scenario.effective_tags`. `effective_tags` **includes inherited feature tags**.
98. **Context is RESET between scenarios** — persist via `before_all` or `context._root`.
99. **All `userdata` values are strings** → `int(userdata.get('timeout', 10))`, `.lower() == 'true'`.
100. **`--junit --junit-directory=`** (not `--format=junit --outfile=`). **`-f` = format, `-o` = outfile, `-D` = userdata.**
101. **`--no-capture` reveals `print()` output**; stdout/stderr/log capture default to ON.
102. Undefined step raises **`NotImplementedError`**, not `PendingException`.
103. Rerun: `behave --format=rerun --outfile=rerun.txt` then **`behave @rerun.txt`**.
104. **`@step` matches Given, When AND Then.**
105. Config precedence: **command line > behave.ini/.behaverc > setup.cfg**. Option is **`format`**, not `default_format`.

## 4.8 Playwright traps
106. **Java uses `page.navigate()`, NOT `page.goto()`** (`goto` is JS/Python).
107. **Java uses `route.resume()`, NOT `route.continue()`** (`continue` is a Java reserved word). Route verbs: `fulfill()`, `resume()`, `abort()`.
108. **`.nth(2)` is the THIRD element — 0-indexed.**
109. **Default assertion timeout = 5 seconds.**
110. **`hasText` = exact; `containsText` = substring.** Negation is **`.not().isVisible()`**, not `isNotVisible()`.
111. **Tracing lives on the BrowserContext**: `context.tracing().start/stop`. **`stop()` with no path DISCARDS the trace.**
112. **Video is configured on the CONTEXT** (`setRecordVideoDir`), saved **when the context/page closes**, format **`.webm`**.
113. **click() checks 5 things (attached, visible, stable, enabled, receives events); fill() checks 3 (attached, visible, EDITABLE).**
114. **Cleanup is in reverse: page → context → browser → playwright.**
115. **`@BeforeAll` (static) creates Playwright + Browser; `@BeforeEach` creates Context + Page.**
116. **Playwright supports 3 engines (Chromium/Firefox/WebKit).** Edge is a **channel** of Chromium (`setChannel("msedge")`). **Only Selenium supports IE.**
117. **Selenium has broader language support** — that's the one column where Selenium wins in the table.
118. **BrowserContexts are incognito by default** and share nothing. `launchPersistentContext()` is the exception — it's called on the **BrowserType**, takes a userDataDir, and **returns a BrowserContext, not a Browser**.
119. Option class confusion: `Page.ScreenshotOptions` vs `Locator.ScreenshotOptions` · `PageAssertions.HasScreenshotOptions` vs `LocatorAssertions.HasScreenshotOptions` · `Tracing.StartOptions` vs `Tracing.StopOptions` · `BrowserType.LaunchOptions` vs `Browser.NewContextOptions`.
120. **`setFullPage()` is a Page-screenshot option only**; **`setQuality()` is JPEG only**.
121. **`waitForResponse(matcher, action)`** — matcher FIRST, triggering Runnable SECOND.
122. Java events are **`page.onConsoleMessage(...)`**, not `page.on("console", ...)`.
123. Storage state: save with `context.storageState(...setPath(...))`; load with `newContext(...setStorageStatePath(...))` — **different method names**.
124. Visual baselines: `<name>-<browser>-<platform>.png` with platforms **linux / win32 / darwin**; update with **`-Dplaywright.updateSnapshots=all`** (or `missing`). `setMaxDiffPixelRatio` (0–1) vs `setMaxDiffPixels` (absolute) vs `setThreshold` (per-pixel color 0–1).
125. **JUnit 5 `@AfterEach` can't see failure status** — use a **`TestWatcher`** extension for screenshot/trace-on-failure.
126. **Codegen output is a `main()` method, not a JUnit test** — it's "a starting point, not a final product." `fill()` auto-clicks, so remove recorded `click()`-before-`fill()`.
127. CI browser install needs **`--with-deps`**. Trace Viewer default port **9323**; `npx playwright show-trace trace.zip` or upload to `trace.playwright.dev`.

## 4.9 Cross-tool "which tool?" traps
128. **JMeter is for performance, not functional API testing** — "Don't use JMeter for simple functional API testing (use Postman), unit testing (use JUnit/pytest), UI testing (use Selenium)."
129. **Selenium/Playwright automate SYSTEM tests**, not integration tests.
130. **Postman is exploration + lightweight automation; REST Assured/requests are production CI/CD automation.**
131. **Appium is the answer for mobile** — Selenium has no native mobile support (and WinAppDriver for desktop).
132. **Selenium IDE's known weakness is brittle absolute XPath and no POM support.**

---
---

# §5 — VERBAL QC INTERVIEW CHEAT SHEET

Every answer below is 2–4 sentences. Lead with the definition, follow with the "why," close with an example.

**Q: What is an API, and why do we test it?**
> "An API is a contract that lets two systems talk — like a waiter between you and the kitchen. We test it because APIs carry the business logic, and API tests are faster and far more stable than UI tests, so we catch defects earlier and cheaper — that's the 'shift-left' idea."

**Q: Explain the test pyramid.**
> "Lots of unit tests at the base because they're fast and cheap, a healthy middle layer of API and integration tests, and only a few UI tests at the top because they're slow and brittle. Roughly 60–70% unit, 20–30% API, 5–10% UI. The mistake teams make is inverting it — an ice cream cone of UI tests."

**Q: What is integration testing and how is it different from unit and system testing?**
> "Unit testing checks one component in isolation with everything else mocked. Integration testing checks that two or more real components actually talk to each other correctly — the interfaces and data flow between them. System testing checks the whole assembled application against requirements, from the user's point of view, with all dependencies real."

**Q: Explain stubs and drivers.**
> "A stub is a fake stand-in for a module *below* the one you're testing — you use it in top-down integration when the lower modules aren't built yet. A driver is a fake caller that sits *above* your module and invokes it — you use it in bottom-up integration when the higher modules aren't ready."

**Q: What is the Page Object Model and why do we use it?**
> "POM is a design pattern where every page gets a class that owns its locators and its interactions, and tests only call those methods. It means when a locator changes, I fix it in exactly one place instead of fifty tests, and tests read like user stories instead of CSS selectors. The key rules are: locators stay private, page methods return page objects, and assertions never go inside a page object — they belong in the test."

**Q: Explain implicit vs explicit waits.**
> "An implicit wait is a global setting that only waits for an element to *exist* in the DOM. An explicit wait waits for a *specific condition* on a *specific element* — visible, clickable, text present. Explicit is what you want, because 'exists' isn't the same as 'usable' — an element can be in the DOM but hidden, and you'll still get an ElementNotInteractableException. And you never mix the two, because the timeouts compound unpredictably."

**Q: Why is `Thread.sleep()` bad?**
> "It always waits the full duration whether or not the app is ready, so it makes your suite slow when things are fast and still flaky when things are slow. An explicit wait returns the instant the condition is true and only fails if it never becomes true."

**Q: How does BDD bridge the gap between business and QA?**
> "BDD gets the Three Amigos — product, dev and QA — in a room *before* the code is written, and the output is concrete examples written in Gherkin. Because those feature files are plain English, the business can read and even write them, and the same file is the requirement, the test and the living documentation. The QA value is that ambiguity gets caught in conversation instead of in a bug report."

**Q: What are the Three Amigos?**
> "Business asks 'what problem are we solving and what's the value?', the developer asks 'how do we build this and what are the constraints?', and the tester asks 'what could go wrong and what are the edge cases?'. You come out with agreed acceptance criteria and shared understanding of 'done'."

**Q: Difference between Background and a @Before hook?**
> "Background is business setup that's *visible in the feature file* and runs before every scenario in that feature — stakeholders can read it. A @Before hook is *technical* setup hidden in code, like launching the browser, and it can be global or tag-scoped. Rule of thumb: if the business would care about it, it goes in Background; if it's plumbing, it goes in a hook."

**Q: Scenario vs Scenario Outline?**
> "A Scenario is one concrete example. A Scenario Outline is the same behavior run against multiple data sets — you put placeholders in angle brackets and feed them from an Examples table. Use an Outline when only the data changes; if the *steps* change, write separate Scenarios. And don't use an Outline for one or two rows — it's less readable than just writing them out."

**Q: How do you parameterize a performance test in JMeter?**
> "Three layers. User Defined Variables for static config like base URL. The `${__P(name,default)}` property function for anything I want to override at runtime — then I pass `-JthreadCount=100` on the command line so the same .jmx runs at dev, staging and prod scale. And CSV Data Set Config plus functions like `${__Random(1,100)}` or `${__UUID()}` for per-user test data, with a JSON Extractor to correlate IDs from one response into the next request."

**Q: Why run JMeter in non-GUI mode?**
> "The GUI is for building and debugging the plan; it eats around 500MB and 15–20% CPU and caps out near 200 threads, and the listener overhead skews your own results. Non-GUI drops to roughly 200MB and 2–5% CPU, scales past 1000 threads, and it's the only sane option in CI. `jmeter -n -t test.jmx -l results.jtl -e -o report`."

**Q: Average response time is 300ms — is the app healthy?**
> "I can't say from the average alone. If one request in a hundred takes ten seconds, the average still looks fine but 1% of my users are having a terrible experience. I'd look at P90, P95 and P99 — a healthy spread is roughly 4× from median to P99; if P99 is 25× the median, there are outliers worth investigating."

**Q: How do you identify a bottleneck from a load test?**
> "I look at the shape, not the single number. If response time climbs as users climb, I suspect CPU saturation or a thread/connection pool limit. If throughput plateaus while response times spike, the bottleneck is in the application or a slow downstream call. Gradually rising response times over a long soak points at a memory leak, and oscillating response times usually means garbage collection."

**Q: Explain the difference between API integration testing and UI automation testing.**
> "API testing hits the service layer directly — it's fast, stable, has no locators to break, and it validates business logic and contracts before a UI even exists. UI automation drives a real browser and validates what the user actually sees and does, end to end. I use API tests for breadth and depth of logic, and UI tests only for the critical user journeys — and I often use API calls to set up state quickly so my UI test doesn't waste time clicking through registration."

**Q: Selenium or Playwright for a new project?**
> "Playwright, unless there's a constraint. It has auto-waiting built in so I write far fewer wait lines, it parallelizes with browser contexts instead of needing a Grid, and video, tracing and network mocking are built in. I'd stay on Selenium if there's existing infrastructure, if we need Internet Explorer or legacy browsers, if the team is Selenium-fluent, or if we're tied into Selenium-based tooling like Appium."

**Q: What is Playwright's auto-waiting actually doing?**
> "Before it clicks, it checks five things: the element is attached to the DOM, visible, stable — not mid-animation — enabled, and actually able to receive the event, meaning nothing is covering it. For `fill()` it checks attached, visible and editable. And the assertions auto-retry for five seconds, so the classic 'read the text before it rendered' race just disappears."

**Q: What is a browser context and why does it matter?**
> "It's an isolated, incognito-by-default session inside one browser — its own cookies, local storage, session storage and cache, shared with nothing. That's why I can run tests in parallel in a single browser without them stepping on each other, and why I can restore a saved auth state per test instead of logging in through the UI every time. It's also far cheaper than launching a whole new browser."

**Q: What variable scopes exist in Postman and which one wins?**
> "Local, Data, Environment, Collection, Global — resolved in that order, so the narrowest scope wins and Global is the fallback. Globals are for constants that never change, environments are for things that differ between dev, staging and prod like base URL and API key, and collection variables are for data specific to that test suite."

**Q: How do you chain requests in Postman?**
> "In the test script of the first request I parse the response and store what I need — `pm.environment.set("userId", pm.response.json().id)` — and then the next request just references `{{userId}}` in its URL or body. That's how you build a full CRUD flow: create, read, update, delete, then unset the variable to clean up."

**Q: Give me a real API defect you'd look for.**
> "The one I always check for is IDOR — insecure direct object reference. I take my own token, then just increment the resource ID in the URL. If I get a 200 back with someone else's data, that's a critical authorization defect. It should be a 403. Right next to that I check that a status code actually matches the outcome — a create returning 200 instead of 201, or a validation failure returning 500 instead of 400, will silently break every client that branches on the status code."

**Q: How do you make tests reliable in CI?**
> "Independence and isolation first — every test creates its own data and cleans it up, so order never matters. Explicit waits instead of sleeps. Headless with a fixed window size so rendering is deterministic. Screenshots, traces or videos captured on failure and uploaded as artifacts. And tagging, so I can run a smoke set on every PR and the full regression nightly."

**Q: How do you decide what to automate?**
> "Risk and repetition. High-business-impact flows and anything I'd otherwise run manually every regression cycle. I push the assertion as low in the pyramid as it will go — if I can prove it with an API test, I don't write a UI test for it. And I don't automate things that are one-off, still changing every sprint, or genuinely need human judgement like visual aesthetics or exploratory testing."

---
---

# §6 — CODE & SYNTAX RECOGNITION CHEAT SHEET

## 6.1 Postman `pm.*`
```javascript
// Status / time / type
pm.test("Status code is 200", () => pm.response.to.have.status(200));
pm.test("Under 500ms", () => pm.expect(pm.response.responseTime).to.be.below(500));
pm.test("Is JSON", () => pm.response.to.be.json);
pm.expect(pm.response.code).to.be.within(200, 299);

// Body
const data = pm.response.json();
pm.expect(data).to.have.property("id");
pm.expect(data).to.have.all.keys("id", "name", "email");
pm.expect(data.id).to.be.a("number");
pm.expect(data.status).to.be.oneOf(["active", "pending"]);
pm.expect(data.tags).to.have.lengthOf(3);
pm.response.to.have.jsonSchema(schema);

// Headers
pm.response.to.have.header("Content-Type");
pm.expect(pm.response.headers.get("Content-Type")).to.include("application/json");

// Variables
pm.environment.set("userId", data.id);   pm.environment.get("userId");
pm.collectionVariables.set(k, v);        pm.globals.set(k, v);
pm.variables.set(k, v);                  pm.environment.unset("temp");

// Dynamic variables (in request fields)
{{$guid}} {{$randomUUID}} {{$timestamp}} {{$isoTimestamp}} {{$randomInt}}
{{$randomEmail}} {{$randomFirstName}} {{$randomLastName}}
```
Chai matchers: `.to.equal / .to.eql / .to.be.a("string") / .to.include / .to.have.property / .to.have.lengthOf / .to.be.above / .below / .within / .to.be.oneOf / .to.not…`

## 6.2 REST Assured
```java
given()
    .baseUri("https://api.example.com").basePath("/api/v1")
    .contentType(ContentType.JSON).accept(ContentType.JSON)
    .header("Authorization", "Bearer token")
    .pathParam("userId", 123)
    .queryParam("status", "active")
    .body(userPojo)
    .log().all()
.when()
    .put("/users/{userId}")
.then()
    .log().ifValidationFails()
    .statusCode(200)
    .contentType(ContentType.JSON)
    .body("name", equalTo("John"))
    .body("orders.size()", greaterThan(0))
    .body("orders.find { it.id == 1 }.total", equalTo(100.00f))
    .body(matchesJsonSchemaInClasspath("schemas/user.json"))
    .time(lessThan(2000L))
    .extract().as(User.class);

// Specs
RequestSpecification req = new RequestSpecBuilder()
    .setBaseUri("...").setContentType(ContentType.JSON).addHeader("k","v").build();
ResponseSpecification res = new ResponseSpecBuilder()
    .expectStatusCode(200).expectContentType(ContentType.JSON).build();

// Auth
.auth().basic(u,p)   .auth().preemptive().basic(u,p)   .auth().oauth2(token)   .auth().digest(u,p)
```

## 6.3 Python requests + pytest
```python
r = requests.get(url, params={"page":1}, headers=h, timeout=(3,10))
r = requests.post(url, json=payload)          # application/json
r = requests.post(url, data=form)             # x-www-form-urlencoded
r.status_code; r.ok; r.text; r.content; r.json(); r.headers; r.elapsed.total_seconds()
r.raise_for_status()                          # HTTPError on 4xx/5xx

with requests.Session() as s:
    s.headers.update({"Accept":"application/json"})
    s.auth = ("user","pass")
    s.get(url)

@pytest.fixture(scope="session")
def base_url(): return os.getenv("API_BASE_URL", "https://api.example.com")

@pytest.fixture
def created_user(base_url, auth_headers):
    u = requests.post(f"{base_url}/users", json={...}, headers=auth_headers).json()
    yield u                                   # ← setup above, teardown below
    requests.delete(f"{base_url}/users/{u['id']}", headers=auth_headers)
```

## 6.4 JMeter CLI + functions
```bash
jmeter -n -t test.jmx -l results.jtl                       # basic
jmeter -n -t test.jmx -l results.jtl -e -o report/         # + HTML dashboard
jmeter -g results.jtl -o report/                           # report from existing jtl
jmeter -n -t test.jmx -l r.jtl -JthreadCount=100 -JrampUp=120 -Jduration=600
jmeter -n -t test.jmx -l r.jtl -q test.properties
jmeter -n -t test.jmx -l r.jtl -R server1,server2 -X       # distributed
```
```
${BASE_URL}                 ${__P(threadCount,10)}      ${__Random(1,100)}
${__RandomString(10)}       ${__UUID()}                 ${__time(yyyy-MM-dd)}
${__counter()}              ${__threadNum}              ${__CSV(file)}    ${__env(VAR)}
```

## 6.5 Selenium Java
```java
WebDriverManager.chromedriver().setup();
ChromeOptions o = new ChromeOptions();
o.addArguments("--headless=new", "--window-size=1920,1080", "--no-sandbox");
WebDriver driver = new ChromeDriver(o);

driver.get(url);  driver.navigate().to(url); driver.navigate().back();
WebElement e = driver.findElement(By.cssSelector(".btn.btn-primary"));
List<WebElement> all = driver.findElements(By.xpath("//tr"));

new WebDriverWait(driver, Duration.ofSeconds(10))
    .until(ExpectedConditions.elementToBeClickable(By.id("submit"))).click();

Wait<WebDriver> fw = new FluentWait<>(driver)
    .withTimeout(Duration.ofSeconds(30)).pollingEvery(Duration.ofMillis(250))
    .ignoring(NoSuchElementException.class);

new Select(driver.findElement(By.id("country"))).selectByVisibleText("United States");
new Actions(driver).moveToElement(menu).pause(Duration.ofMillis(500))
                   .contextClick(item).keyDown(Keys.CONTROL).sendKeys("a")
                   .keyUp(Keys.CONTROL).perform();

driver.switchTo().frame("content-frame");  driver.switchTo().parentFrame();
driver.switchTo().defaultContent();        driver.switchTo().alert().accept();
String orig = driver.getWindowHandle();    Set<String> hs = driver.getWindowHandles();

File f = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
FileUtils.copyFile(f, new File("screenshots/x.png"));
```
```java
// Page Factory
@FindBy(id = "username") private WebElement usernameField;
@FindBys({@FindBy(css="form.login"), @FindBy(css="button")}) private WebElement chained; // AND
@FindAll({@FindBy(id="submit"), @FindBy(css=".submit-btn")}) private WebElement any;     // OR
public LoginPage(WebDriver d) { PageFactory.initElements(d, this); }
```

## 6.6 Selenium Python
```python
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.support.ui import WebDriverWait, Select
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from webdriver_manager.chrome import ChromeDriverManager
from selenium.common.exceptions import NoSuchElementException, TimeoutException

options = Options(); options.add_argument("--headless=new")
driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)

driver.get(url); driver.back(); driver.refresh()
driver.current_url; driver.title                          # properties
el = driver.find_element(By.ID, "username")               # 2 args
items = driver.find_elements(By.CSS_SELECTOR, ".item")    # [] if none
el.send_keys("text", Keys.TAB); el.clear(); el.text; el.get_attribute("value")

wait = WebDriverWait(driver, 10, poll_frequency=0.5,
                     ignored_exceptions=[NoSuchElementException])
btn = wait.until(EC.element_to_be_clickable((By.ID, "submit")))   # ← tuple!

ActionChains(driver).move_to_element(menu).pause(0.5).context_click(item).perform()
driver.switch_to.frame(0); driver.switch_to.parent_frame(); driver.switch_to.default_content()
driver.switch_to.window(handle); driver.close(); driver.quit()
driver.save_screenshot("s.png"); el.screenshot("e.png"); el.screenshot_as_png
```

## 6.7 Gherkin (works for both Cucumber & Behave)
```gherkin
@feature-tag
Feature: Shopping Cart Management
  As an online shopper
  I want to manage my cart
  So that I can buy what I need

  Background:
    Given the user is logged in
    And the user has an empty cart

  Rule: Users cannot exceed stock

    @smoke @critical
    Example: Add single item
      When the user adds "Widget" to the cart
      Then the cart should contain 1 item

  Scenario Outline: Login with different credentials
    Given I am on the login page
    When I enter username "<username>" and password "<password>"
    Then I should see <result_count> results

    Examples: Valid users
      | username | password  | result_count |
      | john     | pass123   | 5            |
      | jane     | secret456 | 3            |

  Scenario: Seed data with a table and a doc string
    Given the following products exist:
      | name   | price | stock |
      | Widget | 9.99  | 100   |
    When I submit the following feedback:
      """
      Multi-line
      doc string text
      """
    Then I should see "Thank you"
```

## 6.8 Cucumber Java glue
```java
import io.cucumber.java.en.*;
import io.cucumber.java.{Before, After, Scenario};
import io.cucumber.datatable.DataTable;

@Given("the user has {int} items at ${double} each")
public void items(int qty, double price) { cart.addItems(qty, price); }

@When("the user searches for {string}")
public void search(String q) { searchPage.search(q); }

@Given("the following users exist:")
public void users(DataTable table) {
    List<Map<String,String>> rows = table.asMaps();
    rows.forEach(r -> userService.create(r.get("username"), r.get("email")));
}

@Before(value = "@ui", order = 1) public void setup() { }
@After public void teardown(Scenario s) {
    if (s.isFailed()) s.attach(png, "image/png", "failure");
}
```
```java
// JUnit 5 runner
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.example.stepdefinitions")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:target/report.html")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@smoke and not @wip")
public class TestRunner { }

// JUnit 4 runner
@RunWith(Cucumber.class)
@CucumberOptions(features="src/test/resources/features", glue={"com.example.stepdefinitions"},
    plugin={"pretty","html:target/report.html","json:target/report.json"},
    tags="@smoke and not @wip", monochrome=true, dryRun=false)
public class TestRunner { }
```
```bash
mvn test -Dcucumber.filter.tags="(@smoke or @regression) and not @slow"
mvn test -Dcucumber.filter.name="Successful login"
mvn test -Dcucumber.execution.dry-run=true
```

## 6.9 Behave
```python
# features/steps/login_steps.py
from behave import given, when, then, step

@given('I am on the login page')
def step_impl(context):
    context.browser.get(context.base_url + "/login")

@when('I enter username "{username}" and password "{password}"')
def step_impl(context, username, password):
    context.login_page.login(username, password)

@then('I should see {count:d} results')
def step_impl(context, count):
    assert len(context.results) == count, f"Expected {count}, got {len(context.results)}"

@given('the following users should be registered')
def step_impl(context):
    for row in context.table:                 # data table
        context.user_service.register(row['username'], row['email'])

@when('I submit the following feedback')
def step_impl(context):
    context.feedback_page.submit(context.text)   # doc string
```
```python
# features/environment.py  — plain functions, no decorators
def before_all(context):
    context.base_url = context.config.userdata.get('base_url', 'http://localhost')
    context.timeout  = int(context.config.userdata.get('timeout', 10))

def before_scenario(context, scenario):
    if 'ui' in scenario.effective_tags:      # NOTE: no '@'
        setup_browser(context)
    context.test_data = {}

def after_scenario(context, scenario):
    if scenario.status == 'failed' and hasattr(context, 'browser'):
        capture_screenshot(context, scenario)
    if hasattr(context, 'browser'): context.browser.quit()
```
```ini
[behave]
paths = features
format = pretty
tags = not @wip and not @skip
show_timings = true
junit = true
junit_directory = reports/junit

[behave.userdata]
browser = chrome
base_url = https://staging.example.com
```
```bash
behave --tags="@smoke and not @wip"
behave --name=".*login.*" --no-capture --stop
behave -f pretty -f json --outfile=report.json
behave --junit --junit-directory=reports/junit
behave -f allure_behave.formatter:AllureFormatter -o reports/allure
behave --format=rerun --outfile=rerun.txt   &&  behave @rerun.txt
behave -D browser=firefox -D base_url=https://staging.example.com
```

## 6.10 Playwright Java
```java
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@BeforeAll static void launch() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
        .setHeadless(System.getenv("CI") != null).setSlowMo(100));
}
@BeforeEach void ctx() {
    context = browser.newContext(new Browser.NewContextOptions()
        .setViewportSize(1920,1080).setRecordVideoDir(Paths.get("videos/")));
    context.tracing().start(new Tracing.StartOptions()
        .setScreenshots(true).setSnapshots(true).setSources(true));
    page = context.newPage();
}
@AfterEach void close() { context.close(); }
@AfterAll static void done() { browser.close(); playwright.close(); }

@Test void login() {
    page.navigate("https://example.com/login");
    page.getByLabel("Email").fill("user@example.com");
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")).click();

    assertThat(page).hasURL(Pattern.compile(".*dashboard.*"));
    assertThat(page.getByTestId("welcome")).hasText("Welcome");
    assertThat(page.locator(".error")).not().isVisible();
    assertThat(page.locator(".item")).hasCount(5);
}

// Locators
page.locator("#id"); page.locator("text=Click me"); page.getByTestId("submit");
page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setLevel(1));
page.getByLabel("Username"); page.getByPlaceholder("Enter email"); page.getByAltText("Logo");
page.locator(".item").first(); .last(); .nth(2);      // nth is 0-indexed
page.locator("button").filter(new Locator.FilterOptions().setHasText("Submit"));

// Screenshot / trace / visual
page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("p.png")).setFullPage(true));
page.locator("#header").screenshot(new Locator.ScreenshotOptions().setPath(Paths.get("h.png")));
context.tracing().stop(new Tracing.StopOptions().setPath(Paths.get("trace.zip")));
assertThat(page).hasScreenshot("homepage.png",
    new PageAssertions.HasScreenshotOptions().setMaxDiffPixelRatio(0.01)
        .setAnimations(ScreenshotAnimations.DISABLED)
        .setMask(Arrays.asList(page.locator(".timestamp"))));

// Network
page.route("**/api/users", route -> route.fulfill(new Route.FulfillOptions()
    .setStatus(200).setContentType("application/json").setBody("[]")));
page.route("**/*.{png,jpg}", Route::abort);
page.onConsoleMessage(m -> System.out.println(m.type() + ": " + m.text()));
Response r = page.waitForResponse("**/api/users", () -> page.navigate(url));
```

## 6.11 JSON structures you must recognize on sight
```jsonc
// Request/response pair
POST /v1/books   Content-Type: application/json   Authorization: Bearer <jwt>
{ "title": "The Art of Testing", "price": 29.99 }
→ HTTP/1.1 201 Created
{ "id": 12345, "title": "The Art of Testing", "createdAt": "2024-01-15T10:30:00Z" }

// JSON Schema (draft-07) — used for CONTRACT TESTING
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "required": ["id", "name", "email"],
  "properties": {
    "id":    { "type": "integer", "minimum": 1 },
    "name":  { "type": "string", "minLength": 1, "maxLength": 100 },
    "email": { "type": "string", "format": "email" },
    "roles": { "type": "array", "items": { "type": "string", "enum": ["admin","user"] } }
  },
  "additionalProperties": false
}

// JSON Patch (RFC 6902) — Content-Type: application/json-patch+json
[ { "op": "replace", "path": "/name", "value": "New Name" },
  { "op": "add", "path": "/tags", "value": ["vip"] } ]

// Best-practice error envelope
{ "error": { "code": "VALIDATION_ERROR", "message": "Invalid input provided",
             "details": [{ "field": "email", "message": "Must be a valid email address" }],
             "requestId": "abc-123", "timestamp": "2024-01-15T10:30:00Z" } }
```

---
---

# §7 — NUMBERS, VERSIONS & NAMES (rapid recall)

| Item | Value |
|---|---|
| REST Assured | **5.4.0** · groupId `io.rest-assured` · package `io.restassured` · author **Johan Haleby, 2010** |
| Hamcrest / JUnit 5 / TestNG | 2.2 / 5.10.0 / 7.8.0 |
| Selenium | **4.15.0** · WebDriverManager **5.6.2** · Grid hub `:4444/wd/hub` · ChromeDriver port **9515** |
| Playwright | **1.40.0** · by **Microsoft** · released **2020** · Trace Viewer port **9323** · assertion timeout **5 s** |
| Cucumber | **7.14.0** · created **2008 / Ruby / Aslak Hellesøy** · Cucumber-JVM **2011** · Rule = **v6+** |
| Behave | **1.2.6** |
| JMeter | Java **8+ (11+ rec.)** · `.jmx` / `.jtl` · GUI ~500MB/15-20% CPU/~200 threads · CLI ~200MB/2-5%/1000+ |
| Selenium history | Core 2004 · RC 2006 · WebDriver 2008 · Selenium 2 / RC deprecated **2011** · W3C **2016** · Selenium 4 **2021** |
| Selenium IDE | rebuilt as cross-browser extension **2018** |
| Chrome headless | **`--headless=new`** (Chrome **109+**) |
| Deprecated `find_element_by_*` | deprecated **4.0**, removed **4.3.0** |
| Test distribution | Unit 60–70% · API 20–30% · UI 5–10% |
| System test exit criteria | **≥95% pass rate** |
| Perf baseline | **±10% tolerance, minimum 3 runs** |
| Error rate bands | ideal <0.1% · acceptable <1% · concerning 1–5% · critical >5% |
| CPU / Memory bands | <70% / 70–85% / >85% — <75% / 75–90% / >90% |
| Endurance duration | **8–72 hours** |
| Selenium wait poll default | **500 ms** (Python `poll_frequency=0.5`) |
| Playwright browser cache | `~/.cache/ms-playwright` (Linux) |
| Visual baseline platforms | **linux / win32 / darwin** |

**Package/import paths worth memorizing:**
```
org.openqa.selenium                     WebDriver, WebElement, By, Keys, Alert
org.openqa.selenium.support.ui          WebDriverWait, ExpectedConditions, Select, FluentWait
org.openqa.selenium.interactions        Actions
org.openqa.selenium.support             FindBy, FindBys, FindAll, PageFactory, CacheLookup
io.cucumber.java.en.*                   @Given @When @Then @And
io.cucumber.java.*                      @Before @After @BeforeStep @AfterStep, Scenario
io.cucumber.datatable.DataTable         DataTable
io.cucumber.core.cli.Main               CLI runner
selenium.webdriver.common.by            By          (Python)
selenium.webdriver.common.keys          Keys        (Python)
selenium.webdriver.common.action_chains ActionChains(Python)
selenium.webdriver.support.ui           WebDriverWait, Select (Python)
selenium.webdriver.support              expected_conditions as EC (Python)
selenium.common.exceptions              NoSuchElementException, TimeoutException (Python)
webdriver_manager.chrome/firefox/microsoft  ChromeDriverManager/GeckoDriverManager/EdgeChromiumDriverManager
com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
```

---
---

# §8 — FINAL 30 MINUTES (read only this)

1. **Levels:** Unit (mocked) → Integration (interfaces, some real) → System (all real, requirements) → Acceptance.
2. **Top-down = STUBS. Bottom-up = DRIVERS.** Big Bang = all at once, hard to isolate.
3. **401 = authentication (who). 403 = authorization (allowed).** IDOR/privilege escalation → 403.
4. **POST→201, DELETE→204, bad input→400/422, rate limit→429, downstream timeout→504.**
5. **PUT replaces everything and is idempotent. PATCH is partial and is not.**
6. **Postman order:** collection → folder → request pre-request → SEND → request → folder → collection tests.
7. **Postman scope:** Local > Data > Environment > Collection > Global.
8. **REST Assured:** given→RequestSpecification, when→RequestSender, verb→Response, then→ValidatableResponse. Validate *then* extract.
9. **`json=` vs `data=`; `.text` str, `.content` bytes, `.json()` method; `RequestException` is the base.**
10. **JMeter:** `-n -t -l -e -o`; `.jmx`/`.jtl`; Aggregate has percentiles, Summary doesn't; disable View Results Tree under load; ramp-up ÷ threads = interval.
11. **Little's Law: Users = TPS × Avg Response Time.** Average lies — quote P90/P95/P99.
12. **`findElements` returns `[]`, never throws.** `findElement` throws `NoSuchElementException`; `wait.until` throws `TimeoutException`.
13. **Never mix implicit + explicit waits. Never `Thread.sleep()`.** Implicit = existence only.
14. **`parentFrame()` up one level; `defaultContent()` all the way out. `close()` = one window; `quit()` = everything.**
15. **Actions need `.perform()`.** Java has `.build()`, Python does not.
16. **Python: `By.ID` constant + 2 args, `.text` property, no `navigate()`, `switch_to` is a property, `EC` needs a tuple, no `FluentWait` class.**
17. **POM: private locators, methods return page objects, no assertions inside, waits inside.** `@FindBys` = AND, `@FindAll` = OR.
18. **Gherkin:** `Example` = alias for `Scenario` (≠ `Examples:`). Background reruns each scenario. Rule = Cucumber 6+. `And`/`But` inherit the previous keyword.
19. **Cucumber `@Before` low-order-first; `@After` HIGH-order-first.** `monochrome=true` removes color. `dryRun` doesn't execute.
20. **Cucumber `{int}/{string}` vs Behave `{name:d}/{name:f}`.** Cucumber `{string}` eats the quotes; Behave needs literal quotes.
21. **Behave hooks are plain functions in `environment.py`**, tags compared **without `@`**, `context.table` vs `context.text`, context resets between scenarios.
22. **Playwright click waits for 5 things (attached/visible/stable/enabled/receives events); fill waits for 3 (attached/visible/editable).** Assertions retry 5 s.
23. **`page.navigate()` not `goto()`; `route.resume()` not `continue()`; `.nth()` is 0-indexed.**
24. **Tracing/video live on the BrowserContext. Cleanup is reverse: page→context→browser→playwright.**
25. **Playwright = 3 engines, no Grid needed, WebSocket. Selenium = more languages, IE support, WebDriver protocol + separate driver process.**

> **If you blank in the interview:** name the level → name the tool → name the trade-off. "That's an integration concern, so I'd verify it at the API layer with REST Assured rather than through the UI, because it's faster and there are no locators to break."
