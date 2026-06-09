# Day 7 - Java Fundamentals

- A class is a collection of similar objects
- Object comes before the class conceptually — a class is just a blueprint for objects
- The `main` method is `static` (no object needed to call it), `void` (returns nothing), `public` (accessible from anywhere)
- `Ctrl+Shift+P → Java Clean` if a Java file won't run in VS Code

---

## Java Ecosystem

| Term | What it is |
|---|---|
| **JDK** | Java Development Kit — compiler + tools, needed to write Java |
| **JRE** | Java Runtime Environment — needed to *run* compiled Java |
| **JVM** | Java Virtual Machine — executes bytecode, handles memory |

Java compiles to bytecode (`.class`), not native machine code. The JVM runs bytecode on any platform — "write once, run anywhere."

---

## Compilation & Running

```bash
javac App.java          # compile → produces App.class
java App                # run (no .class extension)
java App John Doe 3 5   # run with command-line arguments
```

Unlike Python, **Java has a compile step** — type errors and syntax errors are caught before the program ever runs.

---

## Syntax Basics

Java is **statically typed** — every variable must have its type declared, and types are enforced at compile time (not just hints like Python annotations).

```java
// Variable declaration: type name = value;
String firstName = "Alice";
int num = 42;
double price = 9.99;

// Statements end with semicolons
System.out.println("Hello");

// Blocks use curly braces, not indentation
if (num > 0) {
    System.out.println("Positive");
}
```

**Core types:**

| Java type | Python equivalent | Notes |
|---|---|---|
| `int` | `int` | 32-bit integer |
| `double` | `float` | 64-bit decimal |
| `String` | `str` | capital S — it's a class, not a primitive |
| `boolean` | `bool` | `true` / `false` (lowercase) |
| `void` | `None` return | method returns nothing |

---

## Class & File Structure

Every Java file contains exactly one public class, and **the filename must match the class name exactly**.

```java
// File: App.java
public class App {

    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
    }
}
```

`throws Exception` — declares that this method might throw a checked exception (required by the compiler if you use certain operations).

---

## `main` — Entry Point

The JVM always looks for this exact signature to start a program:

```java
public static void main(String[] args)
//     ──────         ──── ──────────
//     accessible     no   array of command-line
//     everywhere     return  arguments (strings)
```

---

## Command-Line Arguments

Passed as a `String[]` array, 0-indexed. Must be parsed if you need numbers:

```java
String firstName = args[0];               // first arg as String
int num1 = Integer.parseInt(args[2]);     // parse String → int
```

`Integer.parseInt()` — converts a `String` to an `int`. Throws `NumberFormatException` if the string isn't a valid integer.

---

## Output

```java
System.out.println("Hello " + name);     // print to stdout with newline
System.out.print("No newline");          // print without newline
System.err.println("Something failed");  // print to stderr (for errors/warnings)
```

String concatenation uses `+`, same as Python.

---

## Static Methods

`static` means the method belongs to the **class itself**, not to any instance — call it via the class name, no object needed.

```java
public class Calculator {

    public static double add(double a, double b) {
        return a + b;
    }

    public static double divide(double a, double b) {
        if (b == 0) {
            System.err.println("Cannot divide by zero.");
            return Double.NaN;    // special value: Not a Number
        }
        return a / b;
    }
}

// Call without instantiating:
Calculator.add(12, 6);
Calculator.divide(12, 0);   // returns Double.NaN
```

---

## Method Overloading

Java allows multiple methods with the **same name** as long as their parameter lists differ (different number or types of parameters). The compiler picks the right one at compile time.

```java
public static double add(double a, double b) { ... }
public static double add(double a, double b, double c) { ... }

Calculator.add(12, 6);        // calls 2-param version
Calculator.add(12, 6, 7);     // calls 3-param version
```

Python doesn't have this — you'd use default parameters or `*args` instead.

---

## `Scanner` — User Input

```java
import java.util.Scanner;   // must import before using

Scanner sc = new Scanner(System.in);   // create scanner reading from stdin

String name = sc.next();               // read next whitespace-delimited token
// sc.nextLine()  → read full line
// sc.nextInt()   → read int directly
// sc.nextDouble()→ read double directly

sc.close();   // close when done
```

---

## Comments

```java
// Single-line comment

/* Multi-line
   comment */

/**
 * Javadoc comment — generates API documentation.
 * Goes above a class or method.
 */
```

---

## Java vs Python — Key Differences

| | Java | Python |
|---|---|---|
| Typing | Static — declared, enforced at compile time | Dynamic — inferred, checked at runtime |
| Compile step | Required (`javac`) | None |
| Block delimiters | `{ }` | Indentation |
| Statement endings | `;` required | None |
| Entry point | `public static void main(String[] args)` | `if __name__ == "__main__":` |
| Print | `System.out.println()` | `print()` |
| String type | `String` (capital S, a class) | `str` |
| `null` / `None` | `null` | `None` |
| Method overloading | Yes | No (use defaults / `*args`) |
| Access modifiers | `public`, `private`, `protected` | Convention only (`_`, `__`) |
