# Maven Central Repository

## Learning Objectives
- Describe **Maven Central** and how artifacts are identified by **coordinates**.
- Add a **dependency** to `pom.xml` and let Maven resolve the **transitive** graph.
- Understand **versioning** basics: release vs **SNAPSHOT**, pinning versions.

---

## Why This Matters

> **Weekly Epic Connection:** Vulnerable or missing dependencies break builds and security scans. QA often verifies **SBOMs** and reproducible builds—Central is where most JVM libraries live.

---

## The Concept

### Maven Central

**Maven Central** is the default **public repository** for open-source Java artifacts. Maven downloads JARs and POMs into your **local repository** (`~/.m2/repository`) on first use.

### Coordinates (GAV)

Each artifact is addressed by:

- **`groupId`** — usually reverse DNS (`org.junit.jupiter`)
- **`artifactId`** — project name (`junit-jupiter`)
- **`version`** — `5.10.2`, `1.0-SNAPSHOT`, etc.

Together: **`groupId:artifactId:version`**

### Declaring a dependency

```xml
<dependencies>
  <dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.2</version>
    <scope>test</scope>
  </dependency>
</dependencies>
```

**`scope`:**

| Scope | Classpath: compile | Classpath: test | Classpath: runtime | Typical Use |
|-------|-------------------|----------------|-------------------|-------------|
| `compile` (default) | ✅ | ✅ | ✅ | Most libraries |
| `test` | ❌ | ✅ | ❌ | JUnit, Mockito, WireMock |
| `provided` | ✅ | ✅ | ❌ | Servlet API (container provides it) |
| `runtime` | ❌ | ✅ | ✅ | JDBC drivers, logging implementations |
| `import` | N/A | N/A | N/A | BOM imports only (see below) |

### Transitive Dependencies

If library A depends on B, Maven pulls **B** automatically. The entire resolved graph ends up on your classpath:

- A single declared dependency can pull in **dozens** of transitives.
- Security scanners report on **all** of them — including transitives.
- Version **conflicts** arise when two paths require different versions. Maven resolves by **nearest definition** (fewest hops from your POM wins).

### Seeing the Resolved Dependency Tree

When “it works on my machine” happens, the **resolved versions** are often the reason. Maven can print the full graph:

```bash
mvn -q dependency:tree
```

Sample output:
```
[INFO] com.example:hello-maven:jar:1.0-SNAPSHOT
[INFO] \- org.junit.jupiter:junit-jupiter:jar:5.10.2:test
[INFO]    +- org.junit.jupiter:junit-jupiter-api:jar:5.10.2:test
[INFO]    |  \- org.opentest4j:opentest4j:jar:1.3.0:test
[INFO]    +- org.junit.jupiter:junit-jupiter-params:jar:5.10.2:test
[INFO]    \- org.junit.jupiter:junit-jupiter-engine:jar:5.10.2:test
```

What to look for: the same library at **different versions**, wrong scope, unexpected size of transitive chains, and **where** a vulnerable library came from.

### `dependency:analyze` — Finding Unused and Undeclared Dependencies

```bash
mvn dependency:analyze
```

Reports:
- **Used undeclared** — you rely on a transitive library but haven’t declared it. If the parent removes it, your code breaks. Declare it explicitly.
- **Unused declared** — you declared something nothing uses. Remove it to keep the classpath lean.

### Excluding Transitive Dependencies

Use `<exclusions>` to drop a transitive dependency that conflicts or is unwanted:

```xml
<dependency>
  <groupId>com.example</groupId>
  <artifactId>some-library</artifactId>
  <version>1.0.0</version>
  <exclusions>
    <exclusion>
      <groupId>org.slf4j</groupId>
      <artifactId>slf4j-log4j12</artifactId>
    </exclusion>
  </exclusions>
</dependency>
```

### Bill of Materials (BOM) — Aligned Versions at Scale

A **BOM** is a special POM that declares a consistent set of versions for related libraries. Import it via `<dependencyManagement>` with `scope=import`:

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.junit</groupId>
      <artifactId>junit-bom</artifactId>
      <version>5.10.2</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>

<dependencies>
  <!-- No version needed — BOM provides it -->
  <dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <scope>test</scope>
  </dependency>
</dependencies>
```

Popular BOMs: **Spring Boot BOM**, **JUnit BOM**, **Testcontainers BOM**, **AWS SDK BOM**.

### SNAPSHOT vs Release

| | Release (`1.0.0`) | SNAPSHOT (`1.0-SNAPSHOT`) |
|---|---|---|
| Immutable? | ✅ Yes — same artifact forever | ❌ No — content can change |
| Cached locally? | ✅ Yes — downloaded once | ❌ Re-fetched on each build (by default) |
| CI/production use | ✅ Recommended | ❌ Avoid — builds become non-reproducible |
| When to use | All released libraries | Your own unreleased modules in development |

Production builds must depend on **releases** or pinned versions via BOM. SNAPSHOT dependencies make CI results non-reproducible — a passing build today may fail tomorrow because a SNAPSHOT changed upstream.

### The Local Repository — `~/.m2/repository`

When Maven downloads an artifact, it caches it in your **local repository** at `~/.m2/repository`. The directory structure mirrors the GAV coordinates:

```
~/.m2/repository/
  org/junit/jupiter/
    junit-jupiter/
      5.10.2/
        junit-jupiter-5.10.2.jar        ← the actual library
        junit-jupiter-5.10.2.pom        ← its POM (used for transitive resolution)
        junit-jupiter-5.10.2.jar.sha1   ← checksum for integrity verification
```

Useful operations:
```bash
# Force re-download all dependencies (fixes corruption issues)
mvn dependency:resolve -U

# Clear the entire local cache (nuclear option — slow next build)
rm -rf ~/.m2/repository
```

### Mirror and Proxy Configuration

In corporate environments, direct access to Maven Central is often blocked. Your organisation runs an **artifact proxy** (Nexus, Artifactory, JFrog) that:
- Mirrors Maven Central locally for speed and availability
- Proxies external requests for audit/compliance
- Hosts internal (private) artifacts

Configure in `~/.m2/settings.xml`:

```xml
<settings>
  <mirrors>
    <mirror>
      <id>corporate-nexus</id>
      <mirrorOf>*</mirrorOf>  <!-- Mirror ALL repositories through this -->
      <url>https://nexus.corp.example.com/repository/maven-public/</url>
    </mirror>
  </mirrors>
</settings>
```

> **QA tip:** If `mvn test` fails with download errors in CI but works locally, the CI agent's `settings.xml` is likely pointing to the wrong mirror, or the mirror doesn't contain a required library.

### Searching for Coordinates

Use [search.maven.org](https://search.maven.org/) to find coordinates and copy-paste XML snippets. You can also search by class name — useful when you know the class but not which JAR it lives in.

---

## Common mistakes (and how to recognize them)

- **Wrong `<scope>`**
  - **Symptom**: compiles in IDE but fails at runtime with `ClassNotFoundException`, or CI behaves differently.
  - **Fix**: if code needs a library at runtime, it should not be `test` scope.
- **SNAPSHOT drift**
  - **Symptom**: pipeline passes one day and fails the next with no code change because SNAPSHOT content changed upstream.
  - **Fix**: prefer releases; use SNAPSHOT only when you control the producing build.
- **Assuming “transitive” means “safe”**
  - **Symptom**: you never declared a library, yet it appears in SBOM/security scan output.
  - **Fix**: treat the full dependency graph as owned; if needed, exclude/override versions intentionally (more advanced Maven topic).

---

## Summary

- **Central** hosts artifacts; Maven caches them locally.
- **GAV** identifies dependencies; **`pom.xml`** declares them.
- **Transitive** resolution pulls the whole graph; **scope** controls classpath visibility.

---

## Practice (10–15 minutes)

1. Add JUnit Jupiter as a test dependency in a Maven project.
2. Run `mvn -q dependency:tree` and find where `junit-jupiter` appears.
3. Identify at least one transitive dependency that arrived “because JUnit depends on it.”
4. Change the JUnit version, rerun the tree, and confirm the resolved version changed.

---

## Additional Resources

- [Maven Central Repository](https://central.sonatype.com/)
- [POM dependencies](https://maven.apache.org/pom.html#Dependencies)
- [Dependency Mechanism](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html)
