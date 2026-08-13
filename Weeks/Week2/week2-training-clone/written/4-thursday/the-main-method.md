# The `main` Method

## Learning Objectives
- Explain `public static void main(String[] args)` as the **entry point** for JVM programs and why each keyword is required.
- Parse and use the **`args`** array for command-line arguments.
- Use **`System.exit()`** to signal exit codes to the shell and CI systems.
- Read **environment variables** with `System.getenv()`.
- Understand the **Java 21 unnamed class** preview for simpler single-file programs.
- Recognize when `main` is called in different execution contexts (direct launch, Maven `exec:java`, test frameworks).

---

## Why This Matters

> **Weekly Epic Connection:** Every CLI tool, test runner bootstrap, and Maven `exec:java` target eventually calls **`main`**. Typos in this signature are a classic "nothing runs" failure. In CI pipelines, **exit codes** from `main` determine pass/fail. In automation scripts, **command-line arguments** and **environment variables** configure the run without hard-coding values.

---

## The Concept

### The Canonical Signature

```java
public class App {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

The JVM looks for **exactly** this method signature to start a program. Every part is **mandatory and non-negotiable**:

| Part | Why It Must Be This |
|------|-------------------|
| `public` | The JVM must call it from outside your class — it needs to be accessible to the launcher |
| `static` | The JVM calls `main` without creating an instance of the class first — no object exists yet |
| `void` | `main` does not return a value to the JVM (use `System.exit(int)` to send an exit code) |
| `main` | The exact method name the JVM launcher searches for — case-sensitive |
| `String[] args` | Command-line arguments passed after the class name, as an array of `String` |

**What happens with a wrong signature:**
```bash
# Wrong return type
public static int main(String[] args) { return 0; }
# → Error: Main method not found in class App, please define the main method as: public static void main(String[] args)

# Missing static
public void main(String[] args) { }
# → Error: Main method is not static in class App

# Wrong parameter type
public static void main(String args) { }
# → Error: Main method not found in class App
```

---

### Allowed Variations

A few syntactic variations are accepted — they produce the same bytecode:

```java
// varargs instead of array (valid — same bytecode)
public static void main(String... args) { }

// throws Exception — valid (useful for quick scripts)
public static void main(String[] args) throws Exception { }

// Both combined
public static void main(String... args) throws Exception { }

// Annotation (e.g., Spring's @SpringBootApplication uses this internally)
@SuppressWarnings("unused")
public static void main(String[] args) { }
```

**Not valid:**
```java
public void main(String[] args) { }          // ❌ Missing static
static void main(String[] args) { }          // ❌ Missing public
public static void Main(String[] args) { }   // ❌ Wrong name (capital M)
public static void main() { }               // ❌ Missing String[] args
public static void main(String args) { }    // ❌ Wrong parameter type
```

---

### Command-Line Arguments

The `String[] args` array receives every **space-separated token** typed after the class name on the command line:

```bash
java App Alice 42 true
#             ↑     ↑    ↑
#          args[0] args[1] args[2]
```

```java
public static void main(String[] args) {
    System.out.println("Argument count: " + args.length);

    for (int i = 0; i < args.length; i++) {
        System.out.println("args[" + i + "] = " + args[i]);
    }
}
// Output:
// Argument count: 3
// args[0] = Alice
// args[1] = 42
// args[2] = true
```

**Important:** All arguments arrive as **`String`**. Parse them explicitly:
```java
public static void main(String[] args) {
    if (args.length < 2) {
        System.err.println("Usage: App <name> <count>");
        System.exit(1);   // Exit with error code
    }

    String name  = args[0];
    int    count = Integer.parseInt(args[1]);   // String → int

    for (int i = 0; i < count; i++) {
        System.out.println("Hello, " + name + "! (" + (i + 1) + ")");
    }
}
```

**Arguments with spaces** need quoting in the shell:
```bash
java App "Alice Smith" 3     # args[0] = "Alice Smith", args[1] = "3"
```

---

### `System.exit()` — Sending Exit Codes

By convention, a program that **succeeds** exits with code **0**. Any **non-zero** code indicates failure. CI/CD systems (GitHub Actions, Jenkins, GitLab CI) check this code:

```java
public static void main(String[] args) {
    try {
        runApplication(args);
        System.exit(0);    // ✅ Success — not strictly needed (JVM exits 0 by default)
    } catch (IllegalArgumentException e) {
        System.err.println("Error: " + e.getMessage());
        System.exit(1);    // ❌ Error — signals failure to the shell and CI
    } catch (Exception e) {
        System.err.println("Unexpected error: " + e.getMessage());
        System.exit(2);    // ❌ Unexpected failure — different code for different category
    }
}
```

Common exit code conventions:
| Exit Code | Meaning |
|-----------|---------|
| `0` | Success |
| `1` | General error |
| `2` | Misuse of shell command / invalid arguments |
| `126` | Permission denied |
| `127` | Command not found |

**In shell scripts:**
```bash
java App input.csv output.json
if [ $? -ne 0 ]; then
    echo "Java program failed!"
    exit 1
fi
```

**In Maven/Gradle:** `exec:java` propagates the JVM exit code — `System.exit(1)` in your `main` fails the Maven build.

> **QA note:** When automating CLI tools in test pipelines, always assert on the exit code, not just on stdout. A tool might print partial output and still fail.

---

### Reading Environment Variables

Environment variables provide configuration without hard-coding or passing every value as an argument. Access them via `System.getenv()`:

```java
public static void main(String[] args) {
    // Read with a default fallback
    String env        = System.getenv("APP_ENV");
    String host       = System.getenv("DB_HOST");
    String portStr    = System.getenv("SERVER_PORT");

    // Provide defaults when variable is not set
    String environment = (env  != null) ? env      : "development";
    String dbHost      = (host != null) ? host     : "localhost";
    int    port        = (portStr != null) ? Integer.parseInt(portStr) : 8080;

    System.out.println("Running in: " + environment);
    System.out.println("Database:   " + dbHost);
    System.out.println("Port:       " + port);
}
```

**In CI/CD** you set these as pipeline secrets or configuration variables:
```yaml
# GitHub Actions example
env:
  APP_ENV: production
  DB_HOST: ${{ secrets.DB_HOST }}
```

> **Best practice:** Never hard-code environment-specific values (URLs, passwords, ports) in source code. Use environment variables. This is the **12-factor app** principle and makes your programs portable across environments.

---

### Multiple `main` Methods

A Java project can have **many classes with a `main` method**. You specify which one to run:

```bash
java com.example.App              # Run App.main()
java com.example.tools.MigrationTool   # Run MigrationTool.main()
java -jar myapp.jar               # Runs the class specified in MANIFEST.MF Main-Class
```

This pattern is common for:
- **Entry points for different tools** in the same JAR (migration script, admin CLI, main app)
- **Demos and standalone examples** in a tutorial project
- **Test harnesses** that launch directly without a test framework

---

### Where `main` Is Called in Practice

| Context | How `main` Is Invoked |
|---------|----------------------|
| Direct launch | `java com.example.App arg1 arg2` |
| Maven exec plugin | `mvn exec:java -Dexec.mainClass=com.example.App -Dexec.args="arg1 arg2"` |
| JAR execution | `java -jar app.jar arg1 arg2` |
| IDE run button | IDE discovers `main` and launches it |
| JUnit tests | JUnit has its own `main` — your test `main` is usually NOT needed |
| Docker container | `CMD ["java", "-jar", "app.jar"]` in Dockerfile |

---

### Java 21: Unnamed Classes and Instance Main Methods (Preview)

Java 21 introduced a **preview feature** allowing simpler single-file programs without a class declaration or `static` modifier — aimed at beginners and scripting:

```java
// HelloWorld.java — Java 21 preview, no class needed
void main() {
    System.out.println("Hello, World!");
}
```

Run with:
```bash
java --enable-preview --source 21 HelloWorld.java
```

This is a **preview feature** — not production-ready. For course exercises and production code, use the standard `public static void main(String[] args)` signature.

---

### Structuring a Real `main` Method

`main` should be **thin** — it should only:
1. Parse arguments / read configuration
2. Create objects and wire dependencies
3. Call the real entry point of your logic
4. Handle top-level exceptions and exit

The business logic belongs in separate classes, not in `main`:

```java
public class App {

    public static void main(String[] args) {
        // 1. Parse arguments
        if (args.length != 2) {
            System.err.println("Usage: App <input-file> <output-file>");
            System.exit(1);
        }
        String inputPath  = args[0];
        String outputPath = args[1];

        // 2. Read configuration from environment
        String env = System.getenv().getOrDefault("APP_ENV", "production");

        // 3. Wire and run
        try {
            DataProcessor processor = new DataProcessor(inputPath, outputPath, env);
            processor.run();
            System.out.println("Done.");
        } catch (Exception e) {
            System.err.println("Failed: " + e.getMessage());
            System.exit(1);
        }
    }
}
```

This keeps `main` testable (you test `DataProcessor` in isolation) and keeps business logic out of the hard-to-test static entry point.

---

## Summary

- **`public static void main(String[] args)`** is the mandatory, exact JVM entry point signature.
  - `public`: accessible to JVM launcher.
  - `static`: called without an instance.
  - `void`: no return value — use `System.exit(int)` for exit codes.
  - `String[] args`: command-line tokens, always as `String` — parse explicitly.
- **Exit codes:** `0` = success, non-zero = failure. CI/CD checks these — use `System.exit(1)` on error.
- **Environment variables** (`System.getenv("NAME")`) configure programs without hard-coding — use `getOrDefault` for fallbacks.
- A project can have **multiple `main` methods** in different classes — specify which one to run.
- Keep `main` **thin**: parse args, wire dependencies, call domain logic, handle top-level exceptions.
- **Java 21 preview** allows simplified `void main()` for scripts — use standard signature for course exercises and production code.

---

## Additional Resources

- [Java Application Launcher (java command)](https://docs.oracle.com/en/java/javase/21/docs/specs/man/java.html)
- [Oracle Tutorial: Command-Line Arguments](https://docs.oracle.com/javase/tutorial/essential/environment/cmdLineArgs.html)
- [System.getenv() javadoc](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/System.html#getenv())
- [JEP 463: Unnamed Classes and Instance Main Methods (Java 21 Preview)](https://openjdk.org/jeps/463)
- [The Twelve-Factor App — Config](https://12factor.net/config) — environment variable best practices
