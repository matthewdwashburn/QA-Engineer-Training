# Maven Build Lifecycle

## Learning Objectives
- Name the main **lifecycle phases**: validate, compile, test, package, verify, install, deploy.
- Explain how **goals** attach to phases via **plugins**.
- Run common commands: `mvn compile`, `mvn test`, `mvn package`.

---

## Why This Matters

> **Weekly Epic Connection:** CI pipelines invoke specific phases. Knowing whether a failure happened in **compile** vs **test** vs **package** tells you which log file to open and which agent to notify.

---

## The Concept

### Lifecycle vs phase vs goal

- **Lifecycle** — ordered sequence of **phases** (e.g. **default** lifecycle for building an app).
- **Phase** — a named step (`compile`, `test`, …). Running a phase runs **all prior phases** in that lifecycle up to that point.
- **Goal** — a **task** implemented by a **plugin** (e.g. `compiler:compile`). Goals **bind** to phases.

### Default lifecycle (subset)

| Phase | Typical work |
|-------|----------------|
| `validate` | Check project is correct |
| `compile` | Compile main sources |
| `test-compile` | Compile tests |
| `test` | Run unit tests (Surefire) |
| `package` | Produce JAR/WAR/etc. |
| `verify` | Run checks (e.g. integration tests) |
| `install` | Put artifact in local `~/.m2` repository |
| `deploy` | Publish to remote repository |

So **`mvn test`** runs `validate` … through **`test`** (including **`compile`**).

### Plugins

Plugins are the **execution engines** of Maven. Each plugin provides one or more **goals**. Plugins bind their goals to lifecycle phases in the default configuration, but you can configure them explicitly in `pom.xml`:

```xml
<build>
  <plugins>

    <!-- Compiler plugin: set Java version -->
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-compiler-plugin</artifactId>
      <version>3.12.1</version>
      <configuration>
        <release>21</release>           <!-- Compile for Java 21 -->
        <encoding>UTF-8</encoding>
      </configuration>
    </plugin>

    <!-- Surefire plugin: controls how unit tests are run -->
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-surefire-plugin</artifactId>
      <version>3.2.5</version>
      <configuration>
        <!-- Run tests in parallel (4 threads) -->
        <parallel>methods</parallel>
        <threadCount>4</threadCount>
        <!-- Include integration test classes -->
        <includes>
          <include>**/*Test.java</include>
          <include>**/*IT.java</include>
        </includes>
      </configuration>
    </plugin>

  </plugins>
</build>
```

Key built-in plugins:

| Plugin | Goal(s) | Phase |
|--------|---------|-------|
| `maven-compiler-plugin` | `compile`, `testCompile` | `compile`, `test-compile` |
| `maven-resources-plugin` | `resources`, `testResources` | `process-resources`, `process-test-resources` |
| `maven-surefire-plugin` | `test` | `test` |
| `maven-jar-plugin` | `jar` | `package` |
| `maven-install-plugin` | `install` | `install` |
| `maven-deploy-plugin` | `deploy` | `deploy` |

### Common Commands

```bash
mvn -q compile              # Compile main sources only
mvn test                    # compile + test-compile + run tests
mvn package -DskipTests     # Package JAR, skip test execution (not test compilation)
mvn package -Dmaven.test.skip=true  # Package JAR, skip ALL test activity (compile + run)
mvn clean test              # Delete target/, then compile + run tests
mvn clean package           # Clean build + produce JAR
mvn clean install           # Clean build + install JAR to ~/.m2 (for local multi-module use)
```

**`clean`** is a **separate lifecycle** (not part of the default lifecycle) that deletes `target/`. It is commonly prefixed to ensure a fresh build: `mvn clean test`.

---

## What actually runs when you type a phase

Key rule: **running a phase runs every earlier phase in that lifecycle**.

- `mvn test` implies: `validate` → `compile` → `test-compile` → `test`
- `mvn package` implies: all of the above **plus** `package`

That is why “we only ran tests” still compiles the app first.

### Seeing the effective lifecycle in logs

In output, Maven prints plugin executions like:

```
[INFO] --- maven-compiler-plugin:...:compile (default-compile) @ my-app ---
[INFO] --- maven-resources-plugin:...:resources (default-resources) @ my-app ---
[INFO] --- maven-surefire-plugin:...:test (default-test) @ my-app ---
```

Read each line as:

- **plugin** (`maven-surefire-plugin`)
- **goal** (`test`)
- **execution id** (`default-test`)
- project **artifactId** (`@ my-app`)

If you can point at the exact plugin goal that failed, you can usually fix the right thing quickly.

---

## The most common QA/CI failures by phase

### `compile` failures (developer fix most of the time)

- Syntax/type errors, missing imports
- Wrong Java version (`release` mismatch)
- Annotation processing / Lombok configuration issues

### `test` failures (could be product bug or test bug)

- Assertion failures (expected vs actual)
- Flaky tests (timing, shared state)
- Environment assumptions (timezone, locale, file paths)

### `package` / `verify` failures (pipeline and integration checks)

- Shading/packaging misconfig, missing resources
- Integration test failures (if bound to `verify`)
- Static analysis checks (SpotBugs/Checkstyle) if configured

---

## Useful Commands for Troubleshooting

```bash
mvn -e test                         # Show stack traces for Maven errors
mvn -X test                         # Very verbose DEBUG output — use when stuck
mvn -DskipTests package             # Package JAR without running tests
mvn -Dtest=MyTest test              # Run only one test class (Surefire filter)
mvn -Dtest="MyTest#myMethod" test   # Run a single test method
mvn -pl module-name test            # Run tests in a specific sub-module
```

> In CI, prefer **reproducing locally** with the exact command shown in the pipeline logs.

### Maven Profiles — Environment-Specific Configuration

**Profiles** activate different build configurations depending on the environment:

```xml
<profiles>
  <profile>
    <id>ci</id>   <!-- Activate with: mvn test -Pci -->
    <build>
      <plugins>
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-surefire-plugin</artifactId>
          <configuration>
            <parallel>classes</parallel>
            <threadCount>8</threadCount>
          </configuration>
        </plugin>
      </plugins>
    </build>
  </profile>
</profiles>
```

```bash
mvn test -Pci    # Activate the 'ci' profile
mvn test -P!ci   # Deactivate the 'ci' profile explicitly
```

Profiles can also be auto-activated by OS, JDK version, or environment variable — useful for cross-platform CI.

### Parent POMs and Inheritance

In multi-module projects, a **parent POM** defines shared settings inherited by all child modules:

```xml
<!-- child/pom.xml -->
<parent>
  <groupId>com.example</groupId>
  <artifactId>parent-project</artifactId>
  <version>1.0-SNAPSHOT</version>
</parent>
<!-- Child inherits: plugins, dependencies, properties from parent -->
```

```bash
mvn clean test                          # Build all modules from the parent
mvn clean test -pl services/api -am     # Build one module + its dependencies
```

---

## Summary

- Phases run in **order**; later phases imply earlier ones.
- **Plugins** bind **goals** to phases (`compiler` → `compile`).
- **`mvn test`** is the everyday QA/developer command for “did the build and unit tests pass?”

---

## Practice (10 minutes)

1. Run `mvn clean test` and identify which plugin runs tests.
2. Introduce a deliberate failing assertion and confirm it fails in the **test** phase (not compile).
3. Introduce a compile error (rename a method used by another class) and confirm it fails in **compile** before tests start.

---

## Additional Resources

- [Build Lifecycle](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html)
- [Plugin bindings](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html#Built-in_Lifecycle_Bindings)
- [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)
