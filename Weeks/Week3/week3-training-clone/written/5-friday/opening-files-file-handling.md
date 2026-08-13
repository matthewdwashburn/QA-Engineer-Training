# Opening Files & File Handling in Java

## Learning Objectives
- Understand the difference between the legacy `java.io.File` class and the modern `java.nio.file` (NIO.2) API.
- Use `Path` and `Paths` to represent file system locations.
- Retrieve file metadata such as size, last-modified time, and existence.
- Apply best practices for safe, portable file handling in Java applications.

---

## Why This Matters

Throughout this week you have built robust Java programs — with OOP hierarchies, collection processing, lambda expressions, and structured logging. Real-world applications almost always need to **persist data between runs** or **consume input from external sources**. Files are the simplest, most universal form of persistence: configuration files, log archives, CSV exports, test fixtures, and report outputs are all file-based.

As a Quality Engineer you will frequently need to read test-data files, write result logs, and validate file output. Knowing Java's file APIs inside-out makes you far more effective in every project you touch.

> **Weekly Epic connection:** By Friday you should be able to design class hierarchies, work with collections, write lambdas — *and now* handle file I/O. This topic completes that foundation.

---

## The Concept

### 1. The Legacy `java.io.File` Class

Before Java 7, developers used `java.io.File` exclusively. While still present in the JDK today, it has several well-known limitations: methods return `boolean` instead of throwing meaningful exceptions, paths are not always portable, and symbolic links are poorly supported.

```java
import java.io.File;

File file = new File("data/report.txt");

System.out.println("Exists:        " + file.exists());
System.out.println("Is file:       " + file.isFile());
System.out.println("Is directory:  " + file.isDirectory());
System.out.println("Absolute path: " + file.getAbsolutePath());
System.out.println("Size (bytes):  " + file.length());
System.out.println("Readable:      " + file.canRead());
System.out.println("Writable:      " + file.canWrite());
```

> **Note:** `file.exists()` returns `false` silently even if the path is invalid — it does **not** tell you *why* the file was not found. This is the core weakness of the legacy API.

---

### 2. The Modern NIO.2 API — `java.nio.file`

Introduced in **Java 7** as part of JSR-203, the `java.nio.file` package provides:
- The `Path` interface — a platform-independent representation of a file-system path.
- The `Paths` utility class — factory methods to create `Path` objects.
- The `Files` utility class — static methods for almost every file operation.
- Rich, typed exceptions (`NoSuchFileException`, `AccessDeniedException`, etc.).

Think of `Path` as the modern replacement for `java.io.File`.

---

### 3. Creating a `Path`

```java
import java.nio.file.Path;
import java.nio.file.Paths;

// Relative path (relative to the working directory)
Path relative = Paths.get("data/report.txt");

// Absolute path
Path absolute = Paths.get("C:/Training/QEA/data/report.txt");

// Joining path segments
Path joined = Paths.get("data", "output", "results.csv");

System.out.println(relative);          // data\report.txt  (Windows)
System.out.println(absolute.toFile()); // C:\Training\QEA\data\report.txt
System.out.println(joined.toAbsolutePath());
```

> **Java 11+ shorthand:** `Path.of("data/report.txt")` is equivalent to `Paths.get(...)`.

---

### 4. Querying File Metadata with `Files`

The `Files` class provides static methods to inspect a path without opening the file itself.

```java
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;

Path path = Paths.get("data/report.txt");

// Existence and type checks
boolean exists    = Files.exists(path);
boolean isFile    = Files.isRegularFile(path);
boolean isDir     = Files.isDirectory(path);
boolean readable  = Files.isReadable(path);
boolean writable  = Files.isWritable(path);

System.out.printf("Exists: %b | File: %b | Dir: %b | Read: %b | Write: %b%n",
        exists, isFile, isDir, readable, writable);

// Size
if (Files.exists(path)) {
    long sizeBytes = Files.size(path);
    System.out.println("Size: " + sizeBytes + " bytes");
}

// Rich attributes (creation, last-modified, last-access times)
if (Files.exists(path)) {
    BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
    Instant created  = attrs.creationTime().toInstant();
    Instant modified = attrs.lastModifiedTime().toInstant();
    System.out.println("Created:  " + created);
    System.out.println("Modified: " + modified);
}
```

---

### 5. Working with Directories

```java
Path dir = Paths.get("data/output");

// Create a single directory
Files.createDirectory(dir);

// Create directory AND all missing parent directories
Files.createDirectories(Paths.get("data/output/reports/2024"));

// Check if a path is a non-empty directory
boolean hasChildren = Files.list(dir).findAny().isPresent(); // Java 8+

// List directory contents
try (var stream = Files.list(dir)) {
    stream.forEach(System.out::println);
}
```

---

### 6. Checking Existence Before Operations

A common source of bugs is performing file operations without first checking whether the path is valid. Always guard your operations:

```java
Path config = Paths.get("config/app.properties");

if (!Files.exists(config)) {
    System.err.println("Configuration file not found: " + config.toAbsolutePath());
    // Throw a custom exception, use defaults, or exit gracefully
    throw new IllegalStateException("Missing required config file.");
}

// Safe to proceed
System.out.println("Config found. Size: " + Files.size(config) + " bytes");
```

---

### 7. Legacy `File` ↔ NIO.2 Interoperability

Many older libraries still require a `java.io.File` parameter. You can convert between the two:

```java
File legacyFile = new File("data/report.txt");
Path nioPath    = legacyFile.toPath();   // File → Path

Path nioPath2   = Paths.get("data/report.txt");
File legacyFile2 = nioPath2.toFile();   // Path → File
```

---

## Quick Reference: `File` vs NIO.2

| Feature | `java.io.File` | `java.nio.file.Path` + `Files` |
|---|---|---|
| Introduced | Java 1.0 | Java 7 |
| Error reporting | Returns `boolean` | Throws typed `IOException` |
| Symbolic links | Poor support | Full support |
| Metadata | Limited | Rich (`BasicFileAttributes`) |
| Watch service | ❌ | ✅ (`WatchService`) |
| Recommend today? | Only for legacy APIs | ✅ Preferred |

---

## Summary

| Concept | Key Class/Method |
|---|---|
| Represent a path | `Path`, `Paths.get()`, `Path.of()` |
| Check existence | `Files.exists(path)` |
| Check type | `Files.isRegularFile()`, `Files.isDirectory()` |
| Get size | `Files.size(path)` |
| Get timestamps | `Files.readAttributes(path, BasicFileAttributes.class)` |
| Create directories | `Files.createDirectories(path)` |
| Convert legacy ↔ NIO.2 | `.toPath()` / `.toFile()` |

> **Up Next:** Now that you can represent and inspect files, we'll cover how to actually **read from and write to** them using readers, writers, and the powerful `Files` convenience methods.

---

## Additional Resources

- [Java NIO.2 — Oracle Official Docs](https://docs.oracle.com/javase/tutorial/essential/io/fileio.html)
- [java.nio.file.Files API Reference](https://docs.oracle.com/en/java/docs/api/java.base/java/nio/file/Files.html)
- [Baeldung: Java NIO.2 File API](https://www.baeldung.com/java-nio-2-file-api)
