# Reading from the Console with `Scanner`

## Learning Objectives
- Explain how **`java.util.Scanner`** tokenises input and what a "token" is.
- Choose between **`nextLine()`**, **`next()`**, **`nextInt()`**, **`nextDouble()`**, and related methods with confidence.
- Diagnose and fix the **"newline left in buffer"** pitfall when mixing `nextInt()` and `nextLine()`.
- Validate input using **`hasNext*()`** predicates and retry loops.
- Use `Scanner` to read from **files** and **strings** (not just `System.in`).
- Apply **`try-with-resources`** to ensure `Scanner` is always closed.

---

## Why This Matters

> **Weekly Epic Connection:** Console exercises, quick test harnesses, and data-driven test scripts all read input. `Scanner` is the standard teaching tool; the same concepts — tokenising, delimiters, validation, buffering — apply when reading CSV files, network streams, and log files in production QA tooling.

---

## The Concept

### How Scanner Works — Tokenisation

`Scanner` reads an **input stream** and breaks it into **tokens** using a configurable **delimiter pattern**. By default, the delimiter is any **whitespace** (spaces, tabs, newlines). This is key to understanding its behaviour:

```
Input stream contents: "  Alice   42   3.14\n"
                                            ^
Tokens separated by whitespace: ["Alice", "42", "3.14"]
```

When you call `nextInt()`, Scanner:
1. Skips any leading whitespace
2. Reads characters until it hits whitespace again
3. Tries to parse those characters as an integer
4. **Leaves the whitespace (including the newline) in the buffer**

This buffered newline is the source of the most common `Scanner` bug.

---

### Basic Setup

```java
import java.util.Scanner;

public class ConsoleDemo {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        System.out.print("Enter your age: ");
        int age = scanner.nextInt();

        System.out.printf("Hello, %s! You are %d years old.%n", name, age);

        scanner.close();  // Important for file-backed scanners; less critical for System.in
    }
}
```

---

### Reading Method Reference

| Method | Returns | Behaviour |
|--------|---------|-----------|
| `nextLine()` | `String` | Reads everything up to (and consuming) the next `\n`. Returns the line without the newline. |
| `next()` | `String` | Reads the next whitespace-delimited token. Does **not** consume the trailing whitespace. |
| `nextInt()` | `int` | Reads the next token as an `int`. Throws `InputMismatchException` if not an integer. |
| `nextLong()` | `long` | Reads the next token as a `long`. |
| `nextDouble()` | `double` | Reads the next token as a `double`. Uses locale — `.` or `,` as decimal separator. |
| `nextFloat()` | `float` | Reads the next token as a `float`. |
| `nextBoolean()` | `boolean` | Reads `"true"` / `"false"` (case-insensitive). |
| `hasNext()` | `boolean` | Returns `true` if there is another token (blocks if stream is open). |
| `hasNextLine()` | `boolean` | Returns `true` if there is another line. |
| `hasNextInt()` | `boolean` | Returns `true` if the next token can be parsed as `int`. Does not consume it. |
| `hasNextDouble()` | `boolean` | Returns `true` if the next token can be parsed as `double`. |

---

### The "Newline Left in Buffer" Pitfall

This is the most common `Scanner` bug for beginners. It happens when you mix `nextInt()` (or `nextDouble()`) with `nextLine()`:

```java
Scanner sc = new Scanner(System.in);

// User types: 25 [Enter]
int age = sc.nextInt();         // Reads "25", but leaves "\n" in buffer

// Now the buffer contains: "\n"
String city = sc.nextLine();    // Immediately consumes "\n" — returns empty string!
System.out.println("City: '" + city + "'");  // City: ''  ← wrong!
```

**Why:** `nextInt()` reads the token `25` and stops. The `\n` character stays in the buffer because `nextInt()` only reads the token, not the following whitespace.

**Fix 1 — Consume the leftover newline explicitly:**
```java
int age = sc.nextInt();
sc.nextLine();              // Drain the leftover newline
String city = sc.nextLine(); // Now reads the user's actual input
```

**Fix 2 — Use `nextLine()` for everything, then parse:**
```java
System.out.print("Enter age: ");
String ageLine = sc.nextLine();         // Always reads the full line
int age = Integer.parseInt(ageLine.trim()); // Parse from String

System.out.print("Enter city: ");
String city = sc.nextLine();             // No buffer issue
```

Fix 2 is generally preferred because it gives you full control over the raw input and avoids the pitfall entirely. It also makes input validation easier.

---

### Input Validation with `hasNext*()` Predicates

`hasNextInt()`, `hasNextDouble()`, and similar methods **look ahead** without consuming the token. This enables clean retry loops:

```java
Scanner sc = new Scanner(System.in);

// Loop until user enters a valid integer
System.out.print("Enter a number: ");
while (!sc.hasNextInt()) {
    System.out.println("That's not a number. Try again: ");
    sc.next();   // Discard the invalid token so the loop can retry
}
int value = sc.nextInt();
System.out.println("You entered: " + value);
```

**Full validated input example:**
```java
Scanner sc = new Scanner(System.in);
int score = -1;

while (score < 0 || score > 100) {
    System.out.print("Enter score (0-100): ");

    if (sc.hasNextInt()) {
        score = sc.nextInt();
        if (score < 0 || score > 100) {
            System.out.println("Score must be between 0 and 100.");
        }
    } else {
        System.out.println("Please enter a whole number.");
        sc.next();   // Discard invalid token
    }
}
System.out.println("Valid score: " + score);
```

---

### Custom Delimiter

By default, Scanner splits on whitespace. You can set a custom delimiter using a **regex pattern**:

```java
// Split on commas — useful for simple CSV-like input
Scanner sc = new Scanner("Alice,42,London");
sc.useDelimiter(",");

String name   = sc.next();    // "Alice"
int age       = sc.nextInt(); // 42
String city   = sc.next();    // "London"

// Split on comma OR semicolon
sc.useDelimiter("[,;]");
```

---

### Reading from a String

`Scanner` can parse a `String` directly — useful for unit testing parsing logic without real user input:

```java
// Parse from a String — no user interaction needed
Scanner sc = new Scanner("Alice 42 3.14");

String name   = sc.next();       // "Alice"
int age       = sc.nextInt();    // 42
double score  = sc.nextDouble(); // 3.14
sc.close();
```

```java
// Read multi-line string
String input = "Alice\n42\nLondon\n";
Scanner sc = new Scanner(input);

String name = sc.nextLine();    // "Alice"
int age     = Integer.parseInt(sc.nextLine().trim());  // 42
String city = sc.nextLine();    // "London"
sc.close();
```

This is a powerful testing technique — your parsing code doesn't need to change for tests versus production.

---

### Reading from a File

`Scanner` can read from a file just as easily as from `System.in`:

```java
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileReader {
    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("data.txt");
        Scanner sc = new Scanner(file);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            System.out.println("Read: " + line);
        }
        sc.close();
    }
}
```

---

### `try-with-resources` — Guaranteed Cleanup

`Scanner` implements `AutoCloseable`, so it can be used with `try-with-resources` to ensure it is always closed, even if an exception occurs:

```java
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class SafeFileReader {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(new File("data.txt"))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                processLine(line);
            }
        } catch (IOException e) {
            System.err.println("Failed to read file: " + e.getMessage());
        }
        // Scanner is closed automatically — no explicit sc.close() needed
    }

    private static void processLine(String line) {
        System.out.println(line);
    }
}
```

> **Best practice:** Always use `try-with-resources` for file-backed `Scanner` instances. For `System.in`, closing Scanner also closes stdin — avoid calling `close()` when reading from the console in long-running programs.

---

### Locale Considerations for Numbers

`nextDouble()` and `nextFloat()` use the **system locale** for the decimal separator:
- English/US locale: `3.14` (dot separator)
- German/French locale: `3,14` (comma separator)

For locale-independent parsing in automation scripts:
```java
import java.util.Locale;

Scanner sc = new Scanner(System.in);
sc.useLocale(Locale.US);  // Force dot as decimal separator

double value = sc.nextDouble();  // Always expects "3.14" format
```

---

### Common Exceptions

| Exception | When It Occurs | Fix |
|-----------|---------------|-----|
| `InputMismatchException` | `nextInt()` called when next token is not an integer | Use `hasNextInt()` before `nextInt()` |
| `NoSuchElementException` | `next()` called when no more tokens exist | Use `hasNext()` before `next()` |
| `IllegalStateException` | Method called after `scanner.close()` | Don't close before you're done reading |
| `FileNotFoundException` | `new Scanner(new File("x.txt"))` when file doesn't exist | Check file path; use try-catch |

---

### Scanner vs BufferedReader — When to Use Each

| | `Scanner` | `BufferedReader` |
|--|-----------|-----------------|
| **Primary use** | Parsing tokens from interactive input | Efficient line-by-line reading of large files |
| **API style** | High-level: `nextInt()`, `nextDouble()` | Low-level: `readLine()` returns `String` |
| **Performance** | Slower (regex tokenisation) | Faster (no regex overhead) |
| **Thread safety** | Not thread-safe | Not thread-safe |
| **When to use** | Console input, small file parsing, learning | Large log files, high-throughput file processing |

```java
// BufferedReader for large files (faster than Scanner for line-by-line)
import java.io.*;

try (BufferedReader br = new BufferedReader(new FileReader("large-log.txt"))) {
    String line;
    while ((line = br.readLine()) != null) {
        // Process each line
    }
}
```

---

## Summary

- `Scanner` breaks input into **tokens** using a delimiter (whitespace by default).
- `nextLine()` reads a whole line and consumes the newline; `next()`, `nextInt()`, etc. read a token and **leave the newline in the buffer**.
- **Mixing `nextInt()` and `nextLine()`** causes the empty-string bug — drain the buffer with `sc.nextLine()` or use `nextLine()` + `parseInt()` everywhere.
- Use **`hasNextInt()`/`hasNextDouble()`** before consuming tokens for safe, validated input.
- `Scanner` can read from **`System.in`**, **`String`**, or **`File`** — same API.
- Use **`try-with-resources`** for file-backed `Scanner` to ensure cleanup.
- Set **`sc.useLocale(Locale.US)`** for locale-independent decimal parsing in automation.
- For large files, prefer **`BufferedReader`** over `Scanner` for performance.

---

## Additional Resources

- [Scanner javadoc (Java 21)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Scanner.html)
- [Oracle Tutorial: Scanning](https://docs.oracle.com/javase/tutorial/essential/io/scanning.html)
- [BufferedReader javadoc](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/io/BufferedReader.html)
