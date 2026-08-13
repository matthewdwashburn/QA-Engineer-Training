# JVM, JRE, and JDK

## Learning Objectives
- Define **JVM**, **JRE**, and **JDK** precisely and describe how they relate to each other.
- Explain what the JVM does at runtime (class loading, bytecode execution, JIT, GC).
- List the key tools that ship with the JDK and when you need them.
- Choose the correct distribution and version for a project.
- Set and verify `JAVA_HOME` and the `PATH` for consistent tooling.

---

## Why This Matters

> **Weekly Epic Connection:** CI/CD pipelines, Maven builds, and local laptops all assume you know whether you have a **compiler** (`javac`) and **which Java version** is on your `PATH`. Mixing JRE-only installs with Maven build jobs, or running a Java 21-compiled JAR on a Java 11 JVM, causes cryptic failures. Getting this right from the start prevents "works on my machine" problems.

---

## The Concept

### JVM — Java Virtual Machine

The **JVM** (Java Virtual Machine) is an abstract computing machine — a software layer that sits between your Java bytecode and the physical hardware. It is what makes Java's "Write Once, Run Anywhere" promise work.

The JVM is responsible for:

| Responsibility | What It Does |
|---------------|-------------|
| **Class Loading** | Finds, loads, and links `.class` files at startup and during execution |
| **Bytecode Verification** | Checks that bytecode is valid and safe before executing (security layer) |
| **Bytecode Execution** | Interprets bytecode instructions OR compiles them to native code |
| **JIT Compilation** | Identifies "hot" (frequently executed) methods and compiles them to native machine code for speed |
| **Garbage Collection (GC)** | Automatically reclaims memory from objects that are no longer reachable |
| **Thread Management** | Schedules Java threads on OS threads |
| **Memory Areas** | Manages the Heap (objects), Stack (method frames), and Method Area (class metadata) |

#### JIT — Just-In-Time Compilation

When the JVM first loads a method, it interprets the bytecode (slower). After that method has been called enough times (the "hot" threshold), the **JIT compiler** compiles it directly to native machine code for the host CPU — no more interpretation overhead. This is why Java warm-up time exists but steady-state performance is high.

```
First few calls:  JVM interprets bytecode (slow)
After threshold:  JIT compiles to native code (fast)
Subsequent calls: Run native code directly (very fast)
```

#### Garbage Collection

Java has **automatic memory management**. You never call `free()` as in C/C++. When no more references point to an object, the GC can reclaim that memory. The JVM ships with several GC algorithms:

- **G1 GC** (default since Java 9) — balanced throughput and latency
- **ZGC** — ultra-low pause times for large heaps (Java 15+)
- **Shenandoah** — concurrent, low-latency GC (OpenJDK)
- **Serial/Parallel GC** — for smaller applications or batch jobs

> **QA relevance:** GC pauses can cause test flakiness in performance/load tests. A test expecting a response within 200ms may occasionally fail if a GC pause occurs. Understand GC logs when diagnosing intermittent timeouts.

---

### JRE — Java Runtime Environment

The **JRE** is the minimum package needed to **run** a compiled Java application. It contains:

- The **JVM** (HotSpot or another implementation)
- The **Java standard class libraries** — the built-in modules (`java.lang`, `java.util`, `java.io`, `java.net`, etc.)
- Supporting files (property files, resource files, DLLs/shared libraries)

**End users** who only need to *run* a Java application (e.g., a desktop app delivered as a JAR) might install a JRE or a bundled runtime.

> **Note:** Since Java 11, Oracle no longer ships a standalone JRE separate from the JDK. You typically install the full JDK. Custom minimal runtimes can be built with `jlink`.

---

### JDK — Java Development Kit

The **JDK** is everything in the JRE plus **developer tools**:

| Tool | Purpose |
|------|---------|
| **`javac`** | Java compiler — compiles `.java` → `.class` bytecode |
| **`java`** | JVM launcher — runs compiled classes and JARs |
| **`jar`** | Creates, updates, and extracts JAR archives |
| **`javadoc`** | Generates HTML API documentation from source comments |
| **`jshell`** | Interactive REPL for Java (Java 9+) |
| **`jcmd`** | Send diagnostic commands to a running JVM |
| **`jstack`** | Print thread stack traces of a running JVM (for deadlock debugging) |
| **`jmap`** | Print memory map / heap histogram |
| **`jstat`** | Monitor GC statistics in real time |
| **`jconsole` / `jvisualvm`** | GUI monitoring and profiling tools |
| **`jlink`** | Assemble and optimize custom runtime images |
| **`keytool`** | Manage cryptographic keys and certificates |

**Developers and QA engineers who compile or build projects must have a JDK.**

---

### The Relationship Diagram

```
┌─────────────────────────────────────────────────────────┐
│                         JDK                              │
│                                                          │
│  ┌──────────────────────────────────────────────────┐   │
│  │                      JRE                          │   │
│  │                                                   │   │
│  │  ┌─────────────────────────────────────────────┐ │   │
│  │  │                    JVM                       │ │   │
│  │  │  Class Loader │ Bytecode Executor │ JIT │ GC │ │   │
│  │  └─────────────────────────────────────────────┘ │   │
│  │                                                   │   │
│  │  Core Libraries: java.lang, java.util, java.io   │   │
│  └──────────────────────────────────────────────────┘   │
│                                                          │
│  Developer Tools: javac, jar, javadoc, jshell, jstack   │
└─────────────────────────────────────────────────────────┘
```

**Rule:** If you need to *compile* Java or use Maven/Gradle, you need a **JDK**. If you only need to *run* pre-compiled JARs, a **JRE** (or JDK) suffices.

---

### Java Versions and LTS Releases

Java releases follow a **six-month cadence** since Java 9 (2017). Most organizations target **Long-Term Support (LTS)** releases, which receive security updates for years:

| Version | Release | LTS? | Status |
|---------|---------|------|--------|
| Java 8 | 2014 | ✅ LTS | Still widely deployed (legacy) |
| Java 11 | 2018 | ✅ LTS | Common in enterprises |
| Java 17 | 2021 | ✅ LTS | Current enterprise standard |
| Java 21 | 2023 | ✅ LTS | Latest LTS — recommended for new projects |
| Java 22 | 2024 | ❌ Non-LTS | Short support window |

> **Rule:** For new projects, use the **latest LTS** (Java 21 as of 2024). For existing projects, match the version already configured in `pom.xml` or `build.gradle`.

---

### Distributions — Which JDK to Install?

Multiple vendors distribute JDK builds, all based on the **OpenJDK** source:

| Distribution | Vendor | Notes |
|-------------|--------|-------|
| **Eclipse Temurin** | Eclipse Adoptium | ✅ Recommended open-source default for most teams |
| **Oracle JDK** | Oracle | Free for development; subscription required for production use of LTS |
| **Amazon Corretto** | Amazon | Free, production-ready, optimised for AWS |
| **Microsoft Build of OpenJDK** | Microsoft | Optimised for Azure |
| **Azul Zulu** | Azul Systems | Free builds + commercial support available |
| **GraalVM** | Oracle / GraalVM team | Adds native image compilation, polyglot support |

All distributions implement the same **Java Language Specification** — your `.java` code and `.class` files are interchangeable.

---

### Setting `JAVA_HOME` and `PATH`

Most build tools (Maven, Gradle, IDEs) rely on the `JAVA_HOME` environment variable to find the JDK:

**Windows (PowerShell):**
```powershell
# Set for current session
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.3.9-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Verify
java --version
javac --version
```

**macOS/Linux (bash/zsh):**
```bash
export JAVA_HOME=/usr/lib/jvm/temurin-21
export PATH="$JAVA_HOME/bin:$PATH"

# Verify
java --version
javac --version
```

**Expected output:**
```
openjdk 21.0.3 2024-04-16 LTS
OpenJDK Runtime Environment Temurin-21.0.3+9 (build 21.0.3+9)
OpenJDK 64-Bit Server VM Temurin-21.0.3+9 (build 21.0.3+9, mixed mode, sharing)
```

> **Best practice:** Use a version manager (`sdkman` on macOS/Linux, `jabba` on Windows) to switch between JDK versions per project.

---

### Multiple JDK Versions

In real QA environments you often need to test against multiple Java versions (the service runs on Java 17, but a legacy dependency requires Java 11 for compilation). Maven's **toolchains** and Gradle's **Java toolchain** support let you specify the Java version per project without changing the system default.

```xml
<!-- Maven toolchain configuration (.mvn/toolchains.xml) -->
<toolchains>
  <toolchain>
    <type>jdk</type>
    <provides>
      <version>21</version>
    </provides>
    <configuration>
      <jdkHome>/usr/lib/jvm/temurin-21</jdkHome>
    </configuration>
  </toolchain>
</toolchains>
```

---

## Summary

- **JVM** — the runtime engine: loads classes, executes/JIT-compiles bytecode, manages GC and threads.
- **JRE** — JVM + core libraries; minimum needed to *run* Java programs.
- **JDK** — JRE + developer tools (`javac`, `jar`, `jshell`, `jstack`, etc.); needed to *build* programs.
- **Install a JDK** for this course and any role that runs `javac`, Maven, or Gradle.
- Use **LTS releases** (Java 21 recommended); use **Eclipse Temurin** for a free, open-source JDK.
- Keep **Java version consistent** between local, CI, and production environments — mismatches cause subtle failures.
- Set **`JAVA_HOME`** and add `$JAVA_HOME/bin` to your `PATH` so tooling finds the right JDK.

---

## Additional Resources

- [JDK documentation (Java 21)](https://docs.oracle.com/en/java/javase/21/)
- [Eclipse Temurin (Adoptium)](https://adoptium.net/) — recommended open-source JDK builds
- [SDKMAN!](https://sdkman.io/) — JDK version manager for macOS/Linux
- [JVM Internals (IBM Developer)](https://developer.ibm.com/articles/j-nativememory-linux/) — deeper GC and memory coverage
- `java --version` and `javac --version` — always verify your install
