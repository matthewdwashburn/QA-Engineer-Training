# Introduction to Maven

## Learning Objectives
- Explain what **Maven** is and why teams standardize on it.
- Describe **convention over configuration** and the standard directory layout.
- Identify the **POM** (`pom.xml`) as the project descriptor.

---

## Why This Matters

> **Weekly Epic Connection:** Most Java projects you test are **Maven** (or Gradle) builds. CI runs `mvn test`; dependencies come from **Maven Central**. Speaking Maven is speaking Java delivery.

---

## The Concept

### What is Maven?

**Maven** is a **build automation** and **dependency management** tool for Java (and other JVM languages). It:

- Compiles source and runs tests with one command
- Downloads libraries (**artifacts**) from repositories
- Packages **JARs** / **WARs**
- Enforces a **standard layout** so any developer can open any repo and know where things live

### What Maven is *not*

- Maven is not “just a dependency downloader.” The dependencies are a **side effect** of building a defined lifecycle.
- Maven is not your IDE. Your IDE may *call Maven* under the hood, but Maven is a **CLI tool** that also runs in CI.
- Maven is not “the Java compiler.” It **invokes** the compiler (`javac`) using a plugin.

### Convention over configuration

Instead of scripting every classpath by hand, Maven assumes defaults:

| Path | Purpose |
|------|---------|
| `src/main/java` | Production Java source |
| `src/main/resources` | Config files packaged with the app |
| `src/test/java` | Test source (JUnit, etc.) |
| `src/test/resources` | Test fixtures |
| `target/` | Build output (generated; often gitignored) |

Override only when you have a good reason.

### POM — Project Object Model

**`pom.xml`** is the heart: **XML** describing:

- **`groupId`**, **`artifactId`**, **`version`** — **GAV** coordinates (unique identity)
- **Parent POM** (optional) for shared settings
- **Dependencies**
- **Plugins** (compiler, Surefire for tests, etc.)
- **Properties** (e.g. Java release level, dependency versions)

### Using `<properties>` for Version Management

`<properties>` lets you define reusable values referenced throughout the POM with `${property.name}` syntax:

```xml
<properties>
  <!-- Java version used by the compiler plugin -->
  <maven.compiler.release>21</maven.compiler.release>
  <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>

  <!-- Centralise dependency versions — change once, applies everywhere -->
  <junit.version>5.10.2</junit.version>
  <mockito.version>5.10.0</mockito.version>
  <slf4j.version>2.0.9</slf4j.version>
</properties>

<dependencies>
  <dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>${junit.version}</version>   <!-- References the property -->
    <scope>test</scope>
  </dependency>
  <dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>${mockito.version}</version>
    <scope>test</scope>
  </dependency>
</dependencies>
```

> **Best practice:** Always define dependency versions in `<properties>`, never inline in `<dependency>`. This makes version bumps a one-line change.

### Inspecting the Effective POM

The **effective POM** is the final merged POM after applying parent inheritance, plugin defaults, and profiles. It shows exactly what Maven will use:

```bash
mvn help:effective-pom
```

This is invaluable when debugging unexpected behaviour — the effective POM reveals the actual configuration Maven is applying, including plugin versions you didn’t explicitly declare.

Minimal sketch:

```xml
<project>
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.example</groupId>
  <artifactId>demo</artifactId>
  <version>1.0-SNAPSHOT</version>
</project>
```

### A more realistic “hello test” POM

Below is a small-but-teachable POM that compiles with a modern Java version and runs JUnit 5 tests.

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>com.example</groupId>
  <artifactId>hello-maven</artifactId>
  <version>1.0-SNAPSHOT</version>

  <properties>
    <maven.compiler.release>21</maven.compiler.release>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <junit.jupiter.version>5.10.2</junit.jupiter.version>
  </properties>

  <dependencies>
    <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter</artifactId>
      <version>${junit.jupiter.version}</version>
      <scope>test</scope>
    </dependency>
  </dependencies>

  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>3.2.5</version>
      </plugin>
    </plugins>
  </build>
</project>
```

> If your organization uses Java 17 instead of 21, set `<maven.compiler.release>17</maven.compiler.release>` and keep everything else the same.

### Quick “create a Maven project” workflow (CLI)

For learning, you can generate a skeleton project and then inspect the folders Maven creates.

```bash
mvn -v
mvn archetype:generate -DgroupId=com.example -DartifactId=hello-maven -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
cd hello-maven
mvn test
```

What to look for:

- `src/main/java/...` has application code
- `src/test/java/...` has tests
- `target/` appears after the build (compiled `.class` files and test reports)

### Maven output: what success looks like

Maven logs are noisy, but the “shape” matters:

```
[INFO] --- maven-compiler-plugin:...:compile (default-compile) @ hello-maven ---
[INFO] --- maven-surefire-plugin:...:test (default-test) @ hello-maven ---
[INFO] BUILD SUCCESS
```

If you can identify **which plugin + phase** failed, you can usually route the issue correctly (compile vs test vs packaging).

### Maven Wrapper (`mvnw`)

Many repos ship **`mvnw`** / **`mvnw.cmd`** so everyone uses the **same Maven version** without a global install.

When a project has the wrapper, prefer:

```bash
./mvnw test
```

On Windows PowerShell you’ll typically run:

```bash
.\mvnw.cmd test
```

### Common “first day” failures (and what they mean)

- **`mvn: command not found`**: Maven is not installed or not on PATH (wrapper fixes this if present).
- **“Unsupported class file major version …”**: Java version mismatch (project compiled for newer Java than your runtime).
- **Dependency download failures**: blocked internet/proxy/cert; CI may need repository mirror settings.

---

## Summary

- Maven = **build + dependencies + conventions**.
- **`src/main/java`** and **`src/test/java`** are the default homes for code and tests.
- **`pom.xml`** declares identity, plugins, and dependencies.

---

## Practice (10–15 minutes)

1. Create a Maven project (archetype or IDE wizard).
2. Add one method in `src/main/java` and one unit test in `src/test/java`.
3. Run `mvn test` and locate:
   - the compiled classes under `target/`
   - test reports under `target/surefire-reports/`
4. Break it on purpose (rename a method without updating the test) and note how the failure differs between:
   - compile error (fails early)
   - test failure (compiles, then fails assertions)

---

## Additional Resources

- [Maven: What is Maven?](https://maven.apache.org/what-is-maven.html)
- [Introduction to the POM](https://maven.apache.org/pom.html)
- [Standard Directory Layout](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html)
