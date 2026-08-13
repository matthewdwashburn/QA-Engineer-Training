# Read Files, Write & Create in Java

## Learning Objectives
- Read text files using `BufferedReader` and the modern `Files.readString()` / `Files.readAllLines()`.
- Write and create files using `BufferedWriter`, `PrintWriter`, and `Files.writeString()` / `Files.write()`.
- Understand the difference between overwriting and appending.
- Use `try-with-resources` to guarantee streams are always closed.
- Choose the right API for the task at hand.

---

## Why This Matters

In the previous topic you learned how to represent a file path and inspect its metadata. Now comes the practical work: **actually reading data in and writing data out**. As a Quality Engineer, you will routinely:

- Load test-data CSV files into your tests.
- Write structured log and report files from test runs.
- Create or update configuration files during automated workflows.

Java offers *two layers* of file I/O APIs — the classic `java.io` stream/reader/writer classes and the modern `java.nio.file.Files` convenience methods. Understanding both makes you productive across old and new codebases alike.

> **Weekly Epic connection:** By the end of today you can design class hierarchies, manipulate collections with lambdas, log structured output — and now persist and consume data from the file system. That's a complete, production-capable Java skill set.

---

## The Concept

### 1. The `try-with-resources` Pattern

Before looking at any specific reader or writer, understand this safety rule: **always use `try-with-resources`** with any stream, reader, or writer. It guarantees the resource is closed even if an exception is thrown — preventing resource leaks.

```java
try (BufferedReader reader = new BufferedReader(new FileReader("data.txt"))) {
    // use reader here
} // reader.close() is called automatically
catch (IOException e) {
    e.printStackTrace();
}
```

> Every example below follows this pattern.

---

### 2. Reading Text Files

#### 2a. `BufferedReader` — Classic API (Line by Line)

`BufferedReader` wraps a lower-level `FileReader` and adds a buffer for efficiency. Use it when you need to process a file **one line at a time** — ideal for large files.

```java
import java.io.*;
import java.nio.file.*;

Path path = Paths.get("data/students.txt");

try (BufferedReader reader = Files.newBufferedReader(path)) {
    String line;
    while ((line = reader.readLine()) != null) {
        System.out.println(line);
    }
} catch (IOException e) {
    System.err.println("Could not read file: " + e.getMessage());
}
```

> **Tip:** `Files.newBufferedReader(path)` (NIO.2) is preferred over `new BufferedReader(new FileReader("path"))` because it is charset-aware (defaults to UTF-8) and works with `Path` objects.

---

#### 2b. `Files.readAllLines()` — Read Entire File into a List

For **smaller files** where you want all lines in memory at once, `readAllLines()` is the most concise option. It returns a `List<String>`.

```java
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

Path path = Paths.get("data/students.txt");

try {
    List<String> lines = Files.readAllLines(path);
    System.out.println("Total lines: " + lines.size());
    lines.forEach(System.out::println); // lambda from Thursday!
} catch (IOException e) {
    System.err.println("Error: " + e.getMessage());
}
```

---

#### 2c. `Files.readString()` — Read Entire File as One String (Java 11+)

The simplest possible way to read a small text file entirely into memory:

```java
import java.io.IOException;
import java.nio.file.*;

Path path = Paths.get("config/app.properties");

try {
    String content = Files.readString(path);
    System.out.println(content);
} catch (IOException e) {
    System.err.println("Error reading file: " + e.getMessage());
}
```

> **When to use:** Config files, small templates, test fixtures. Avoid for large files (hundreds of MB) — use line-by-line streaming instead.

---

### 3. Writing & Creating Text Files

#### 3a. `BufferedWriter` — Classic API

`BufferedWriter` wraps a `FileWriter` and buffers writes for efficiency. Use it when you need fine-grained control over what you write — useful in loops or when building output incrementally.

```java
import java.io.*;
import java.nio.file.*;

Path path = Paths.get("output/results.txt");

// Creates the file if it doesn't exist; OVERWRITES if it does
try (BufferedWriter writer = Files.newBufferedWriter(path)) {
    writer.write("Test Run Results");
    writer.newLine(); // OS-appropriate line separator
    writer.write("PASS: LoginTest");
    writer.newLine();
    writer.write("PASS: SearchTest");
    writer.newLine();
    writer.write("FAIL: CheckoutTest");
    writer.newLine();
} catch (IOException e) {
    System.err.println("Could not write file: " + e.getMessage());
}

System.out.println("Results written to: " + path.toAbsolutePath());
```

---

#### 3b. Appending to an Existing File

By default, writing opens a file in **overwrite mode**. To add to an existing file without erasing it, use the `StandardOpenOption.APPEND` flag:

```java
import java.io.*;
import java.nio.file.*;

Path logFile = Paths.get("output/test.log");

try (BufferedWriter writer = Files.newBufferedWriter(logFile, StandardOpenOption.APPEND,
                                                              StandardOpenOption.CREATE)) {
    writer.write("[INFO] New test run started at " + java.time.LocalDateTime.now());
    writer.newLine();
} catch (IOException e) {
    System.err.println("Failed to append to log: " + e.getMessage());
}
```

> `StandardOpenOption.CREATE` creates the file if it does not yet exist, and `APPEND` adds to the end if it does.

---

#### 3c. `Files.writeString()` — Write a String to a File (Java 11+)

The most concise way to write a complete string to a file:

```java
import java.io.IOException;
import java.nio.file.*;

String report = "PASS: 48\nFAIL: 2\nSKIPPED: 0\n";
Path path = Paths.get("output/summary.txt");

try {
    Files.writeString(path, report);                          // overwrite
    Files.writeString(path, "\n--- Appended ---\n",
            StandardOpenOption.APPEND);                       // append
} catch (IOException e) {
    System.err.println("Write failed: " + e.getMessage());
}
```

---

#### 3d. `Files.write()` — Write a Collection of Lines

Works well when your data is already in a `List<String>`:

```java
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

List<String> testResults = List.of(
    "PASS - LoginTest",
    "PASS - SearchTest",
    "FAIL - CheckoutTest"
);

Path path = Paths.get("output/results.txt");

try {
    Files.write(path, testResults);
    System.out.println("Wrote " + testResults.size() + " lines.");
} catch (IOException e) {
    System.err.println("Write failed: " + e.getMessage());
}
```

---

### 4. Creating Empty Files and Directories

```java
Path newFile = Paths.get("output/placeholder.txt");
Path newDir  = Paths.get("output/reports/2024");

// Create an empty file (throws exception if it already exists)
Files.createFile(newFile);

// Create directory(ies) — does NOT fail if they already exist
Files.createDirectories(newDir);
```

---

### 5. Putting It Together — A Practical Example

Below is a small utility that reads a raw CSV of student scores, filters failures, and writes a report file:

```java
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

public class ScoreReporter {

    public static void main(String[] args) throws IOException {
        Path inputPath  = Paths.get("data/scores.csv");
        Path outputPath = Paths.get("output/failures.txt");

        // Read all lines
        List<String> allLines = Files.readAllLines(inputPath);

        // Filter failing scores (score < 50) — using streams and lambdas from Thursday
        List<String> failures = allLines.stream()
                .skip(1)                          // skip header row
                .filter(line -> {
                    String[] parts = line.split(",");
                    int score = Integer.parseInt(parts[1].trim());
                    return score < 50;
                })
                .collect(Collectors.toList());

        // Write report
        Files.createDirectories(outputPath.getParent());
        Files.write(outputPath, failures);

        System.out.println("Report written: " + failures.size() + " failures found.");
    }
}
```

> Notice how `Files.readAllLines()` and `Files.write()` work cleanly with Java streams — a natural combination with this week's lambda and collections topics.

---

## API Comparison Cheat Sheet

| Task | Classic `java.io` | Modern `java.nio.file.Files` (Java 7+/11+) |
|---|---|---|
| Read line by line | `BufferedReader.readLine()` | `Files.newBufferedReader()` |
| Read all lines | `BufferedReader` + loop | `Files.readAllLines()` |
| Read entire file as String | `BufferedReader` + `StringBuilder` | `Files.readString()` *(Java 11)* |
| Write text | `BufferedWriter.write()` | `Files.newBufferedWriter()` |
| Write entire string | `FileWriter` | `Files.writeString()` *(Java 11)* |
| Write list of lines | Manual loop | `Files.write(path, list)` |
| Append to file | `new FileWriter(file, true)` | `Files.newBufferedWriter(path, APPEND)` |

---

## Summary

- Use **`BufferedReader` / `Files.newBufferedReader()`** for reading large files line by line.
- Use **`Files.readAllLines()`** or **`Files.readString()`** for small, fully in-memory reads.
- Use **`BufferedWriter` / `Files.newBufferedWriter()`** for fine-grained writing.
- Use **`Files.writeString()`** or **`Files.write(list)`** for quick, concise writes.
- Always use **`try-with-resources`** — it guarantees streams are closed.
- Control create-vs-append behavior with **`StandardOpenOption`** flags.

---

## Additional Resources

- [Java I/O Tutorial — Oracle Official Docs](https://docs.oracle.com/javase/tutorial/essential/io/)
- [Files Class API Reference](https://docs.oracle.com/en/java/docs/api/java.base/java/nio/file/Files.html)
- [Baeldung: Reading and Writing Files in Java](https://www.baeldung.com/reading-file-in-java)
