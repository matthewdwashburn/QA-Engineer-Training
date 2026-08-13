# Comments in Java

## Learning Objectives
- Use **single-line**, **multi-line**, and **Javadoc** comment types appropriately.
- Write well-structured Javadoc with core tags: **`@param`**, **`@return`**, **`@throws`**.
- Use additional Javadoc tags: **`@see`**, **`@since`**, **`@deprecated`**, **`@author`**.
- Apply inline tags **`{@code}`** and **`{@link}`** within Javadoc prose.
- Mark deprecated code correctly with **`@Deprecated`** annotation and **`@deprecated`** Javadoc.
- Apply **best practices**: explain *why*, not *what*; keep comments updated; avoid noise.
- Distinguish **comments** (for humans) from **annotations** (for tools and the compiler).

---

## Why This Matters

> **Weekly Epic Connection:** Javadoc is the raw material for IDE tooltips, generated HTML documentation, Sonar rules, and API contracts. In QA, reading Javadoc for `@param` constraints and `@throws` conditions tells you what inputs to test and what failures to expect — without reading the implementation. Clear comments in test code also make test intent obvious during code reviews.

---

## The Concept

### Three Comment Types

Java provides three syntactically distinct comment forms, each with a different purpose:

| Type | Syntax | Purpose |
|------|--------|---------|
| Single-line | `// text` | Short in-line or above-line notes |
| Multi-line block | `/* text */` | Longer block notes; temporarily disabling code |
| Javadoc | `/** text */` | API documentation for classes, methods, fields |

---

### Single-Line Comments `//`

Everything from `//` to the end of the line is ignored by the compiler:

```java
// Increment retry count before the exponential backoff wait
retries++;

int timeout = calculateTimeout();  // Timeout in milliseconds — not seconds!
```

**Good uses:**
- Explain **why** something is done a particular way
- Flag non-obvious constraints or values
- Mark temporary code: `// TODO:`, `// FIXME:`, `// HACK:`

```java
// TODO: Replace with async variant once API v2 is deployed
connection.sendSync(payload);

// FIXME: This hardcoded base URL will need env variable injection
String base = "https://api.example.com";

// HACK: Vendor API returns HTTP 200 with error in body — check body explicitly
if (response.getBody().contains("\"error\":")) {
    throw new VendorApiException(response.getBody());
}
```

---

### Multi-Line Block Comments `/* ... */`

Spans multiple lines. Used for longer explanatory notes or temporarily disabling a block of code:

```java
/*
 * Legacy format required by the vendor API v1 contract.
 * The trailing semicolons in the request body are intentional —
 * removing them causes a 400 response from the vendor endpoint.
 * See: JIRA-4821 for the upstream defect.
 */
String payload = buildLegacyPayload();
```

**Temporarily disable code (debugging aid):**
```java
/*
int debug = computeExpensive();
System.out.println("debug value: " + debug);
*/
```

> **Note:** You **cannot nest** `/* */` block comments — `/* outer /* inner */ */` will not parse correctly. Use `//` line comments when you need to comment out code that already contains block comments.

---

### Javadoc Comments `/** ... */`

Javadoc comments are attached to **declarations** (classes, interfaces, methods, fields, constructors). They are processed by the **`javadoc`** tool to generate HTML documentation, and are displayed by IDEs as **hover tooltips**.

**Structure:**
```java
/**
 * First sentence — this becomes the summary in the doc index.
 * Additional sentences form the full description.
 *
 * <p>HTML is valid inside Javadoc — use {@code} for inline code,
 * {@link} for cross-references.</p>
 *
 * @param  paramName  Description of the parameter — what it means, constraints
 * @param  anotherParam  Another parameter
 * @return             Description of what is returned (omit for void)
 * @throws ExceptionType  When this exception is thrown and why
 * @since  1.3           Version when this was introduced
 * @see    OtherClass#otherMethod  Cross-reference to related API
 */
public ReturnType methodName(Type paramName, Type anotherParam) {
    // ...
}
```

---

### Core Javadoc Tags

#### `@param`

Documents each method parameter — the name, meaning, and any constraints:

```java
/**
 * Applies a percentage discount to a base price.
 *
 * @param basePrice    the original price before discount; must be &gt;= 0
 * @param discountRate the discount fraction; must be in the range [0.0, 1.0]
 */
public double applyDiscount(double basePrice, double discountRate) {
```

One `@param` tag per parameter. List them in the same order as the method signature.

#### `@return`

Documents the return value — what it represents and any notable conditions (`null` safety, empty collections, etc.):

```java
/**
 * @return the discounted price, or 0.0 if basePrice is 0
 */
```

Omit `@return` for `void` methods.

#### `@throws` (also `@exception` — synonym)

Documents exceptions the method may throw — both **checked** (declared in signature) and important **unchecked** ones:

```java
/**
 * @throws IllegalArgumentException if basePrice is negative or discountRate is
 *                                  outside the range [0.0, 1.0]
 * @throws NullPointerException     if price is null (when using wrapper types)
 */
```

> **QA value of `@throws`:** This is the primary signal for **negative test cases**. If a method documents `@throws IllegalArgumentException if input is negative`, your test suite should have a test that passes a negative value and asserts the exception is thrown.

---

### Additional Javadoc Tags

#### `@see`

Creates a cross-reference link to another class, method, or URL:

```java
/**
 * @see PriceCalculator#applyDiscount(double, double)
 * @see <a href="https://docs.example.com/pricing">Pricing Rules</a>
 */
```

#### `@since`

Records when the API was introduced — useful for maintaining compatibility notes:

```java
/**
 * @since 2.1.0
 */
public void newFeatureMethod() { }
```

#### `@author`

Records the original author of the class — commonly used on class-level Javadoc:

```java
/**
 * Utilities for test data generation.
 *
 * @author Alice Smith
 * @since 1.0.0
 */
public class TestDataFactory { }
```

#### `@deprecated` and `@Deprecated`

Marking something as deprecated requires **both** the annotation and the Javadoc tag:

```java
/**
 * Returns the legacy user identifier format.
 *
 * @deprecated As of version 3.0, use {@link #getUserId()} instead.
 *             This method will be removed in version 4.0.
 */
@Deprecated(since = "3.0", forRemoval = true)
public String getLegacyId() {
    return "legacy-" + id;
}
```

- **`@Deprecated` annotation** — signals the compiler to emit deprecation warnings at call sites
- **`@deprecated` Javadoc tag** — provides the human-readable explanation and migration path
- Both are needed — the annotation triggers tooling, the Javadoc tag provides context

---

### Inline Tags in Javadoc Prose

Inline tags appear inside the description text to add formatting and cross-references:

#### `{@code ...}` — Format as code

Use for class names, method names, values, and code fragments within prose:

```java
/**
 * Parses the date string using ISO-8601 format. The {@code dateStr}
 * parameter must not be {@code null} and must conform to the pattern
 * {@code yyyy-MM-dd}.
 *
 * Returns {@code null} if the string cannot be parsed.
 */
```

`{@code}` renders as monospace and correctly escapes `<`, `>`, and `&` in HTML output.

#### `{@link ClassName#methodName}` — Clickable cross-reference

Creates a hyperlink to another class or method in generated documentation:

```java
/**
 * Validates and formats the amount. After calling this method, use
 * {@link Money#format(Locale)} to convert to a display string.
 *
 * @see {@link Currency} for supported currency codes
 */
```

**`{@link}`** creates a clickable link in generated HTML; **`@see`** appears in a dedicated "See Also" section.

---

### Class-Level Javadoc

Class Javadoc describes the purpose of the class as a whole, not any specific method:

```java
/**
 * Factory for creating test fixture data for the User domain.
 *
 * <p>All factory methods return immutable objects with sensible defaults.
 * Use the {@code with*} builder methods to override specific fields.</p>
 *
 * <pre>{@code
 * User user = UserFactory.defaultUser()
 *                        .withName("Alice")
 *                        .withRole(Role.ADMIN);
 * }</pre>
 *
 * @author QA Team
 * @since 1.0.0
 * @see User
 * @see Role
 */
public class UserFactory {
```

`<pre>{@code ... }</pre>` renders a multi-line code block in the HTML documentation.

---

### Comments vs Annotations — An Important Distinction

**Comments** (including Javadoc) are for **humans** — they are stripped during compilation and have no effect on behaviour.

**Annotations** (`@Override`, `@Test`, `@Deprecated`, `@SuppressWarnings`) are for **tools and the compiler** — they are read at compile time or runtime and affect behaviour:

| | Comments | Annotations |
|--|----------|------------|
| Syntax | `// text`, `/* */`, `/** */` | `@AnnotationName` |
| Read by | Humans, `javadoc` tool | Compiler, IDEs, frameworks, JVM |
| Effect on runtime | None — stripped | Can trigger warnings, inject behaviour, enable features |
| Examples | In-code explanation | `@Override`, `@Test`, `@Deprecated`, `@Autowired` |

```java
// This is a comment — no effect on compilation or runtime
// @param in a comment has no effect on the compiler

@Override           // This is an annotation — compiler verifies the method is actually overriding
public String toString() {
    return name;
}
```

---

### Best Practices

1. **Explain *why*, not *what*.** Code shows what happens; comments explain the reasoning.
   ```java
   // ❌ Restates the code — useless
   i++;   // Increment i
   
   // ✅ Explains the reason
   i++;   // Skip the header row — data starts at index 1
   ```

2. **Keep comments updated.** Stale comments (describing what code used to do) are **worse than no comment** — they actively mislead.

3. **Write Javadoc for all public API.** Every `public` class, method, and field should have Javadoc. IDE tools and code review processes rely on it.

4. **Use `@throws` to define test cases.** Every `@throws` condition is a negative test case waiting to be written.

5. **Prefer readable names over comments.** A method named `calculateDiscountedTotal()` may not need a comment; `processData()` always does.

6. **Use `TODO`/`FIXME` consistently.** Most IDEs highlight these — they're a lightweight issue tracker embedded in code.

---

### Generating Javadoc HTML

```bash
# Generate Javadoc HTML for your project
mvn javadoc:javadoc

# Output appears in: target/site/apidocs/index.html
```

Open `target/site/apidocs/index.html` in a browser to see your Javadoc rendered as API documentation.

---

## Summary

- **`//`** — single-line comment; use for explaining *why* and flagging `TODO`/`FIXME`.
- **`/* */`** — block comment; use for longer notes or temporarily disabling code. Cannot be nested.
- **`/** */`** — Javadoc; attached to declarations; processed by the `javadoc` tool and displayed in IDE tooltips.
- **Core Javadoc tags:** `@param` (parameter name + constraint), `@return` (return value meaning), `@throws` (exception conditions — the input for negative test design).
- **Additional tags:** `@see` (cross-references), `@since` (version introduced), `@author` (ownership), `@deprecated` + `@Deprecated` annotation (migration path).
- **Inline tags:** `{@code}` (monospace code in prose), `{@link}` (clickable cross-reference).
- **Annotations** (`@Override`, `@Test`, `@Deprecated`) are for tools and the compiler — they affect behaviour, unlike comments.
- Write Javadoc for all `public` API; explain *why*, not *what*; keep comments in sync with code.

---

## Additional Resources

- [How to Write Doc Comments for the Javadoc Tool (Oracle)](https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html)
- [Javadoc Reference (Java 21)](https://docs.oracle.com/en/java/javase/21/docs/specs/javadoc/javadoc-spec.html)
- [mvn javadoc:javadoc plugin](https://maven.apache.org/plugins/maven-javadoc-plugin/)
- [Javadoc tags reference](https://docs.oracle.com/javase/8/docs/technotes/tools/windows/javadoc.html#CHDJFCCC)
