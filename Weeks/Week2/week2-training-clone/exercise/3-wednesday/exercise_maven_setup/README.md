# Exercise: Create a Maven Project and Run Tests

**Mode:** Implementation (Code Lab)  
**Duration:** 60–90 minutes  
**Day:** 3-wednesday | **Week:** 2 — Python & Java Foundations  

---

## Objective

- Create a **new** Maven project from scratch (archetype **or** hand-written `pom.xml` + folders).
- Add **JUnit Jupiter** from **Maven Central** as a **test** dependency.
- Implement a tiny production class and a **passing** unit test.
- Run **`mvn test`** successfully from the command line.

---

## Prerequisites

| Concept | Source |
|---------|--------|
| Maven basics | `written/3-wednesday/introduction-to-maven.md` |
| Lifecycle | `written/3-wednesday/maven-build-lifecycle.md` |
| Central | `written/3-wednesday/central-repository.md` |
| Demo | `demos/3-wednesday/demo_maven_project/` |

---

## Option A — Archetype (quickstart)

```bash
mvn -B archetype:generate \
  -DarchetypeGroupId=org.apache.maven.archetypes \
  -DarchetypeArtifactId=maven-archetype-quickstart \
  -DarchetypeVersion=1.5 \
  -DgroupId=com.yourname.qa \
  -DartifactId=week2-maven-lab \
  -Dpackage=com.yourname.qa.week2 \
  -Dversion=1.0-SNAPSHOT
```

Then add **JUnit 5** to `pom.xml` (replace default JUnit 4 if present) and write at least one **`@Test`** using **`org.junit.jupiter.api`**.

---

## Option B — Hand-built (deeper understanding)

1. Create folder `week2-maven-lab/`.
2. Add `pom.xml` with:
   - `modelVersion` **4.0.0**
   - Your `groupId`, `artifactId`, `version`
   - `maven.compiler.release` **17** or **21** (match your JDK)
   - **JUnit Jupiter** dependency `scope` **test**
   - **Surefire** plugin (3.x) under `<build><plugins>` so `mvn test` runs JUnit 5
3. Create:
   - `src/main/java/.../Greeter.java` — method `String hello(String name)` returns `"Hello, " + name` (non-blank names only; throw `IllegalArgumentException` if blank).
   - `src/test/java/.../GreeterTest.java` — two tests: happy path + exception for blank.

---

## Definition of Done

- [ ] `mvn -q test` exits **0** from your project root.
- [ ] `pom.xml` shows JUnit coordinates you copied from **search.maven.org** (or equivalent).
- [ ] Screenshot or terminal paste of successful test run in your submission.

---

## Stretch

- Add **`mvn package`** and inspect **`target/*.jar`**.
- Add a **parameterized** test with **`@ParameterizedTest`** (one case is enough).

---

## References

- Instructor demo project: `content/Week2-Python-Java/demos/3-wednesday/demo_maven_project/`
