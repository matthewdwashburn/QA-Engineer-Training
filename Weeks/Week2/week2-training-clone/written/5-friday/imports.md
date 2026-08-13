# Import Statements in Java

## Learning Objectives
- Explain why `import` statements exist and what problem they solve.
- Use **single-type imports**, **on-demand (wildcard) imports**, and **static imports**.
- Identify which packages are **auto-imported** without any `import` statement.
- Resolve **naming conflicts** when two packages export the same class name.
- Use **fully qualified class names** as an alternative to imports.
- Understand how **IDEs** manage imports automatically.
- Recognise the **Java module system's** effect on import availability (overview).

---

## Why This Matters

> **Weekly Epic Connection:** Every Java test file imports `@Test`, `assertEquals`, `Mockito`, and more. Understanding import rules lets you diagnose `cannot find symbol` compile errors quickly, manage naming conflicts between libraries, and understand why `import static` makes JUnit assertions read cleanly without class prefixes.

---

## The Concept

### Why Imports Exist

Java organises its standard library (and your own code) into **packages** — named namespaces like `java.util`, `java.io`, `org.junit.jupiter.api`. Without imports, you'd have to write the **fully qualified class name** every time:

```java
// Without import — verbose, every reference needs full package path
java.util.List<String> names = new java.util.ArrayList<>();
java.util.Collections.sort(names);
java.io.File config = new java.io.File("config.txt");
```

**`import`** declarations tell the compiler which classes to resolve by simple name, eliminating the package prefix in code:

```java
// With imports — clean, readable
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.io.File;

List<String> names = new ArrayList<>();
Collections.sort(names);
File config = new File("config.txt");
```

**`import` is compile-time only** — it does not affect runtime, bytecode, or performance. It is purely a shorthand for the compiler.

---

### Import Rules and Placement

Import statements must appear:
1. **After** the `package` declaration (if any)
2. **Before** the class declaration
3. In any order (though most style guides use alphabetical within groups)

```java
package com.example.tests;         // 1. package declaration (if present)

import java.util.List;             // 2. imports — after package, before class
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class MyTest {              // 3. class declaration
    // ...
}
```

---

### Automatically Imported Packages

You **never** need to import `java.lang` — it is automatically imported by the compiler into every Java file:

```java
// These are always available without any import:
String           // java.lang.String
System           // java.lang.System
Math             // java.lang.Math
Integer          // java.lang.Integer
Object           // java.lang.Object
Exception        // java.lang.Exception
RuntimeException // java.lang.RuntimeException
Thread           // java.lang.Thread
StringBuilder    // java.lang.StringBuilder
```

Everything else requires an explicit import.

**Also:** Classes in the **same package** as the current file do not need importing — they are visible automatically.

---

### Single-Type Import (Preferred Style)

Imports exactly one class from a package. This is the **recommended style** for production code because it makes dependencies explicit:

```java
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// Test imports
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
```

**Benefit:** Anyone reading the file can instantly see every external dependency by scanning the import list.

---

### On-Demand (Wildcard) Import `*`

Imports all **public types** in a package with a single declaration:

```java
import java.util.*;       // Imports List, ArrayList, Map, HashMap, Collections, etc.
import java.io.*;         // Imports File, FileReader, IOException, etc.
```

**Important: `*` is NOT recursive.** `import java.util.*` does **not** import sub-packages like `java.util.concurrent` or `java.util.function` — those need separate imports:

```java
import java.util.*;                  // java.util.List, java.util.Map, ...
import java.util.concurrent.*;       // ExecutorService, Future, ...
import java.util.function.*;         // Function, Predicate, Consumer, ...
```

**Style guidance:**
- Wildcard imports are **shorter** to write but **less explicit** — a reader can't tell which classes are actually used
- Many teams ban wildcard imports via IDE/Checkstyle rules for production code
- IDEs like IntelliJ IDEA have a setting to **auto-expand wildcards** to single-type imports (`Optimize Imports`)
- Exception: test code sometimes uses `import static org.junit.jupiter.api.Assertions.*;` for brevity

---

### Static Import

Imports **static members** (static fields and static methods) so you can use them without the class name prefix:

```java
// Without static import
double r = Math.sqrt(Math.pow(3, 2) + Math.pow(4, 2));
boolean pass = Assertions.assertEquals(expected, actual);

// With static import
import static java.lang.Math.sqrt;
import static java.lang.Math.pow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

double r = sqrt(pow(3, 2) + pow(4, 2));   // Much cleaner
assertEquals(expected, actual);            // Reads like English
assertNotNull(result);
```

**Common static import uses in QA:**
```java
// JUnit 5 assertions
import static org.junit.jupiter.api.Assertions.*;

// Mockito DSL
import static org.mockito.Mockito.*;

// AssertJ fluent assertions
import static org.assertj.core.api.Assertions.*;

// Hamcrest matchers
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
```

**Static import of constants:**
```java
import static java.lang.Math.PI;
import static java.util.Collections.EMPTY_LIST;

double circumference = 2 * PI * radius;  // No Math.PI needed
```

**Caution:** Over-using static imports can make code harder to understand when the origin of a method isn't obvious. Use judiciously, primarily for well-known APIs (Assertions, Mockito, Math).

---

### Fully Qualified Class Names — No Import Needed

You can always use the full package path inline without any import:

```java
// No import needed — fully qualified reference
java.time.LocalDate today = java.time.LocalDate.now();
java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\d+");
```

**When to use fully qualified names:**
- **Resolving naming conflicts** — when two packages both export a class with the same name
- **One-off uses** — when you only use a class once and don't want to pollute the import list
- **Disambiguation in documentation** or Javadoc

---

### Resolving Naming Conflicts

If two packages export a class with the same **simple name**, you can't import both by simple name. Use one import and a fully qualified reference for the other:

```java
// java.util.Date and java.sql.Date both exist
// If you need both:
import java.util.Date;              // Import one by simple name

// Use the other fully qualified
java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());
Date utilDate = new Date();         // Refers to java.util.Date
```

Another common conflict:

```java
// java.awt.List and java.util.List
import java.util.List;              // Use the more common one

java.awt.List awtList = new java.awt.List();  // Fully qualified for the other
```

---

### IDE Import Management

Modern Java IDEs handle imports automatically:

| Action | IDE Behaviour |
|--------|--------------|
| Type a class name | IDE suggests an import and adds it on acceptance |
| `Alt+Enter` / Quick Fix | Adds missing import for the symbol under cursor |
| `Ctrl+Alt+O` (IntelliJ) | **Optimize Imports** — removes unused, collapses wildcards |
| Save with auto-format | Many setups optimize imports on save |
| Unused import | IDE highlights it; compiler warns with `-Xlint:all` |

> **Build hygiene:** Remove unused imports before committing. They are noise in code reviews and can trigger Checkstyle or SonarQube warnings in CI.

---

### The Java Module System (Java 9+ — Awareness)

Since Java 9, the JDK is structured into **modules** (`java.base`, `java.sql`, `java.xml`, etc.). In **unnamed module** projects (all Maven projects without `module-info.java`), you can still use all JDK packages as before — the module system doesn't block you.

In **named module** projects (`module-info.java` present), you must explicitly `requires` the modules you use:

```java
// module-info.java
module com.example.app {
    requires java.sql;           // Needed to import java.sql.*
    requires org.junit.jupiter;  // Needed to import JUnit
    exports com.example.app.api; // Makes this package accessible to others
}
```

For this course, you will work with **unnamed module** Maven projects — no `module-info.java` is needed and all standard imports work as normal.

---

### Import Checklist

When you see `cannot find symbol` on a type reference:
1. ✅ Is the class in `java.lang`? → No import needed — check the spelling.
2. ✅ Is the class in the same package? → No import needed — check the spelling.
3. ✅ Add the import — use IDE quick-fix or add manually.
4. ✅ Is the dependency in `pom.xml`? If not, Maven won't have downloaded the JAR and the class won't be on the classpath.
5. ✅ Is there a naming conflict? → Import one, use fully qualified name for the other.

---

## Summary

- **`import`** is compile-time shorthand — it does not affect runtime or performance.
- **`java.lang`** (and the current package) are always auto-imported — no declaration needed.
- **Single-type imports** (`import java.util.List;`) are preferred — explicit and unambiguous.
- **Wildcard `*`** imports all types in a package but is **not recursive** and hides which classes are used.
- **`import static`** brings static members (methods, fields) into scope — enables clean JUnit / Mockito DSL.
- **Fully qualified names** resolve naming conflicts and eliminate the need for an import for one-off uses.
- IDEs manage imports automatically — **Optimize Imports** removes unused and expands wildcards.
- In **named module** projects, `module-info.java` controls which packages are readable.

---

## Additional Resources

- [Using Package Members (Oracle Tutorial)](https://docs.oracle.com/javase/tutorial/java/package/usepkgs.html)
- [JLS: Import Declarations](https://docs.oracle.com/javase/specs/jls/se21/html/jls-7.html#jls-7.5)
- [Java Platform Module System Overview (JEP 261)](https://openjdk.org/jeps/261)
- [IntelliJ IDEA: Optimize Imports](https://www.jetbrains.com/help/idea/creating-and-optimizing-imports.html)
