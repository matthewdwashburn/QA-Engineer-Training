# Exercise: JDK Install, Compile, and Run from the CLI

**Mode:** Implementation (Checklist + Code Lab)  
**Duration:** 45–60 minutes  
**Day:** 2-tuesday | **Week:** 2 — Python & Java Foundations  

---

## Objective

- Install a **JDK** (17 or 21 LTS recommended) and verify **`java`** and **`javac`** on your PATH.
- Create a **`HelloWeek2`** program that prints your name and the **Java version** the runtime reports.
- Compile with **`javac`** and run with **`java`** from a terminal (no IDE run button for the final checkpoint—IDE optional for editing).

---

## Prerequisites

| Concept | Source |
|---------|--------|
| JDK vs JRE vs JVM | `written/2-tuesday/jvm-jre-jdk.md` |
| Compile pipeline | `written/2-tuesday/compilation-process.md` |
| `main` method | `written/4-thursday/the-main-method.md` (preview OK) |
| Demo | `demos/2-tuesday/code/DemoFirstJavaProgram.java` |

---

## Part A — Install & verify (20 min)

1. Install an **OpenJDK** build (e.g. Eclipse Temurin 21).
2. Open a **new** terminal and run:

```bash
java --version
javac --version
```

3. If either command is not found, fix **`JAVA_HOME`** / **PATH** (document what you changed in `notes.txt` in this folder).

---

## Part B — HelloWeek2 (25 min)

1. In `starter_code/`, create **`HelloWeek2.java`** with **`public class HelloWeek2`** (filename must match).
2. Implement **`public static void main(String[] args)`**:
   - If **`args.length >= 1`**, greet: `Hello, <args[0]>!`
   - Else greet: `Hello, trainee!`
   - Print one line showing **runtime** version, e.g. `Runtime.version()` or `System.getProperty("java.version")`.
3. From `starter_code/`:

```bash
javac HelloWeek2.java
java HelloWeek2
java HelloWeek2 Ada
```

---

## Definition of Done

- [ ] Screenshot or paste of `java --version` / `javac --version` in your notes or PR description.
- [ ] `HelloWeek2.java` compiles with **no** IDE-required classpath.
- [ ] Running with/without args produces the two expected greeting variants.

---

## Stretch

- Pass **two** args: first name and last name; print them together.
- Add a **Javadoc** block above `main` describing the program.

---

## References

- Written: `content/Week2-Python-Java/written/2-tuesday/`
- Demo: `content/Week2-Python-Java/demos/2-tuesday/`
