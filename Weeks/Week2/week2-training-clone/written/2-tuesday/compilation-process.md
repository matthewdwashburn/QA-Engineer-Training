# The Java Compilation Process

## Learning Objectives
- Trace the full path from **`.java`** source to **`.class`** bytecode to **JVM execution**.
- Explain what `javac` does during compilation (parsing, type checking, bytecode generation).
- Describe **bytecode** at a conceptual level — what it is, how it differs from source code and native code.
- Understand the role of the **classpath** in finding classes.
- Describe how **JARs** package compiled code for distribution.
- Read and interpret common `javac` error messages.

---

## Why This Matters

> **Weekly Epic Connection:** Build failures in Maven and Gradle point directly to **compile errors** from `javac`. Reading compiler error messages, understanding what a `.class` file represents, and knowing how classpaths work helps you unblock failing pipelines and make sense of IDE and CI output.

---

## The Concept

### The Full Pipeline

```
Source Code (.java)
       │
       ▼
  javac (Compiler)
  ├── Lexical analysis (tokenize source)
  ├── Parsing (build AST)
  ├── Semantic analysis (type checking, name resolution)
  └── Bytecode generation
       │
       ▼
Bytecode (.class files)
       │
       ▼
  java (JVM Launcher)
  ├── Class loading
  ├── Bytecode verification
  ├── Interpretation / JIT compilation
  └── Execution
       │
       ▼
Program output
```

---

### Step 1 — Writing Source Code (`.java`)

Every Java source file contains **one public class** whose name must **exactly match the filename** (case-sensitive):

```java
// File: Hello.java  ← filename must match the public class name
public class Hello {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

Rules enforced before compilation even runs:
- The public class name must match the filename.
- Each file may define multiple classes, but only **one public** class.
- Source files must be UTF-8 encoded (by default).

---

### Step 2 — Compilation with `javac`

`javac` is the **Java compiler**. It reads `.java` source files and performs four stages:

| Stage | What Happens |
|-------|-------------|
| **Lexical analysis** | Source text is broken into tokens (`public`, `class`, `Hello`, `{`, etc.) |
| **Parsing** | Tokens are assembled into an **Abstract Syntax Tree (AST)** representing the program structure |
| **Semantic analysis** | Types are resolved, variable declarations are checked, method signatures are verified — this is where **type errors** are caught |
| **Bytecode generation** | The validated AST is compiled into `.class` files containing **JVM bytecode** |

```bash
# Compile a single file
javac Hello.java

# Compile multiple files
javac Hello.java Utils.java

# Compile all .java files in a directory
javac src/**/*.java

# Specify an output directory for .class files
javac -d out/ src/Hello.java

# Specify the source and target compatibility
javac --release 21 Hello.java
```

**Output:** One `.class` file per class. If `Hello.java` defines an inner class, you get `Hello.class` and `Hello$InnerClass.class`.

---

### Step 3 — What Is Bytecode?

Bytecode is the **intermediate representation** that `javac` produces:

- It is **not** your source code — it's not readable Java.
- It is **not** native machine code for your CPU (x86, ARM) — it's not platform-specific.
- It is a **compact, stack-oriented instruction set** designed for the JVM to interpret or JIT-compile.

Think of it like a sheet of music: the score is platform-neutral (any orchestra can play it), but the actual sound depends on the instruments available (the JVM/CPU).

You can inspect bytecode with `javap`:

```bash
javap -c Hello.class
```

Output (abbreviated):
```
Compiled from "Hello.java"
public class Hello {
  public static void main(java.lang.String[]);
    Code:
       0: getstatic     #7   // Field java/lang/System.out:Ljava/io/PrintStream;
       3: ldc           #13  // String Hello, World!
       5: invokevirtual #15  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
       8: return
}
```

You do not need to write or read bytecode directly, but knowing it exists explains:
- Why you can inspect compiled code with tools even without the source
- Why `.class` files are portable but can be obfuscated
- Why the JVM can optimize (JIT) specific instructions

---

### Step 4 — Running with `java`

The `java` command **launches the JVM** and runs a compiled class:

```bash
# Run by class name (not filename — no .class extension!)
java Hello

# Run a JAR file
java -jar myapp.jar

# Pass command-line arguments
java Hello Alice Bob

# Set JVM options (heap size, GC settings)
java -Xmx512m -Xms128m Hello
```

Important: `java` takes the **class name**, not the file name:
```bash
java Hello        # ✅ Correct — class name
java Hello.class  # ❌ Wrong — includes extension
java hello        # ❌ Wrong — case-sensitive
```

The JVM looks for a method with this exact signature as the entry point:
```java
public static void main(String[] args)
```

If `main` is missing or has the wrong signature, you get:
```
Error: Main method not found in class Hello
```

---

### The Classpath

The **classpath** tells the JVM and `javac` where to find `.class` files and JAR files. Without the correct classpath, the compiler or runtime cannot find classes you depend on.

```bash
# Add a directory to the classpath
javac -cp out/ MyApp.java

# Add a JAR to the classpath
javac -cp lib/gson-2.10.jar MyApp.java

# Multiple entries — separated by : (Unix) or ; (Windows)
javac -cp out/:lib/gson-2.10.jar:lib/junit-5.10.jar MyApp.java

# Windows equivalent
javac -cp "out\;lib\gson-2.10.jar" MyApp.java
```

> **In practice:** Maven and Gradle manage the classpath for you. You rarely need to set it manually. Understanding it helps you diagnose `ClassNotFoundException` and `NoClassDefFoundError` at runtime.

---

### JAR Files — Packaging and Distribution

A **JAR** (Java ARchive) is a ZIP file that bundles `.class` files, resources, and metadata:

```bash
# Create a JAR from compiled .class files
jar cf myapp.jar -C out/ .

# Create an executable JAR with a manifest
jar cfe myapp.jar Hello -C out/ .

# Run the executable JAR
java -jar myapp.jar

# List contents of a JAR
jar tf myapp.jar

# Extract a JAR
jar xf myapp.jar
```

A JAR can contain a `META-INF/MANIFEST.MF` file that specifies the main class:
```
Manifest-Version: 1.0
Main-Class: Hello
```

> **QA relevance:** Test frameworks (JUnit, TestNG) and tools (Selenium, REST Assured) are distributed as JARs. When Maven downloads dependencies, it downloads `.jar` files and adds them to your project's classpath automatically.

---

### Common Compiler Errors and How to Read Them

```
Hello.java:5: error: ';' expected
        System.out.println("Hello")
                                   ^
1 error
```

The format is: **`filename:line: error: description`** followed by a caret (`^`) pointing to the problem location.

Common errors:

| Error Message | Likely Cause |
|--------------|-------------|
| `cannot find symbol` | Class, method, or variable not found — check spelling, imports, classpath |
| `';' expected` | Missing semicolon at end of statement |
| `incompatible types` | Assigning the wrong type to a variable |
| `method X in class Y cannot be applied` | Wrong argument types in a method call |
| `class X is public, should be declared in a file named X.java` | Filename doesn't match class name |
| `reached end of file while parsing` | Missing closing `}` |
| `variable X might not have been initialized` | Variable declared but not assigned before use |

---

### Compile Once, Run Anywhere — In Practice

```bash
# On your Windows machine
javac Hello.java        # Produces Hello.class

# Copy Hello.class to a Linux server
scp Hello.class user@server:/home/user/

# Run on Linux — same bytecode, no recompilation needed
ssh user@server "java Hello"
```

The bytecode in `Hello.class` is **identical** on both machines. The JVM on Linux interprets the same bytecode that the JVM on Windows produced.

---

## Summary

- **`javac`** compiles `.java` source through lexing → parsing → type checking → **bytecode** (`.class` files).
- **Bytecode** is a portable, stack-oriented intermediate format — not source, not native machine code.
- **`java`** launches the JVM, loads `.class` files, and calls `public static void main(String[] args)`.
- `java` takes a **class name**, not a file name — no `.class` extension.
- The **classpath** (`-cp`) tells the compiler and JVM where to find classes and JARs; Maven/Gradle manage this automatically.
- **JARs** bundle `.class` files and resources for distribution and library sharing.
- Compiler errors include **filename, line number, and a description** — read them top-to-bottom; fix the first error first.

---

## Additional Resources

- [`javac` tool docs (Java 21)](https://docs.oracle.com/en/java/javase/21/docs/specs/man/javac.html)
- [`java` launcher docs (Java 21)](https://docs.oracle.com/en/java/javase/21/docs/specs/man/java.html)
- [`javap` — class file disassembler](https://docs.oracle.com/en/java/javase/21/docs/specs/man/javap.html)
- [JVM Specification (bytecode reference)](https://docs.oracle.com/javase/specs/jvms/se21/html/index.html)
