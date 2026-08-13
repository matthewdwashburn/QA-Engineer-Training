# Java Packages

## Learning Objectives
- Explain **packages** as named namespaces that prevent class name collisions.
- Apply **reverse-DNS** naming conventions and understand why they work.
- Map **package declarations** to the required **directory structure** on disk.
- Use all four **access modifiers** and understand what `package-private` means.
- Organise code into **layered package structures** that match enterprise patterns.
- Connect **Maven's `src/main/java`** layout to package declarations.
- Distinguish the Maven `groupId` from the Java package and know the convention.

---

## Why This Matters

> **Weekly Epic Connection:** Large codebases compile because every class lives in a **package** that prevents name collisions across hundreds of libraries. Maven's **directory tree must mirror** package names — misalignment causes `"class not found"` build failures. Understanding package structure also enables you to navigate enterprise repositories quickly and understand access control between components.

---

## The Concept

### What Is a Package?

A **package** is a **named namespace** that groups related Java types (`class`, `interface`, `enum`, `record`, `annotation`). Without packages, every class name would have to be globally unique — impossible across thousands of libraries.

Packages solve three problems:
1. **Name collision prevention** — `com.example.util.StringUtils` and `com.apache.commons.lang.StringUtils` can coexist because they have different fully qualified names.
2. **Logical organisation** — related classes live together (e.g. all HTTP client classes in `com.example.http`).
3. **Access control** — `package-private` visibility lets classes collaborate internally without exposing internals publicly.

---

### Package Declaration

The `package` statement must be the **first non-comment line** in a source file:

```java
package com.example.qa.utils;    // ← first line

import java.util.List;           // imports come after

public class IdGenerator {
    public static String generate() {
        return java.util.UUID.randomUUID().toString();
    }
}
```

The **fully qualified class name** of this class is `com.example.qa.utils.IdGenerator`. Other classes use this full name to refer to it without importing.

---

### Naming Convention — Reverse-DNS

The standard convention uses **reverse internet domain name** as the root:

```
com.example.qa.utils.IdGenerator
│    │       │   │     └── Class name (PascalCase)
│    │       │   └── Sub-package / layer name (lowercase)
│    │       └── Project or product name
│    └── Organisation domain (e.g. company.com reversed)
└── Top-level domain (reversed)
```

**Examples:**
```
com.google.gson                 → Google's Gson JSON library
org.junit.jupiter.api           → JUnit 5 public API
io.rest-assured                 → REST Assured testing library
com.example.qea.model           → Your course project's model layer
```

**Rules:**
- All **lowercase** — no uppercase, hyphens, or special characters
- Segments separated by **dots** (`.`)
- Each segment should be a valid Java identifier (avoid starting with a number)
- Hyphens in domain names become underscores (e.g. `my-company.com` → `com.my_company`)

---

### Package Path Must Match Directory Structure

The Java compiler **requires** the file's location on disk to match the package declaration. This is not optional — misalignment causes compile errors.

For a class declared as:
```java
package com.example.qa.utils;
public class IdGenerator { }
```

The file **must** be located at:
```
src/main/java/com/example/qa/utils/IdGenerator.java
└─────────────┘└─────────────────────────────────────┘
  source root          package path / filename
```

**Maven's standard directory layout:**
```
my-project/
├── pom.xml
└── src/
    ├── main/
    │   └── java/                     ← main source root
    │       └── com/
    │           └── example/
    │               └── qa/
    │                   ├── model/
    │                   │   └── User.java
    │                   ├── service/
    │                   │   └── UserService.java
    │                   └── utils/
    │                       └── IdGenerator.java
    └── test/
        └── java/                     ← test source root
            └── com/
                └── example/
                    └── qa/
                        ├── service/
                        │   └── UserServiceTest.java
                        └── utils/
                            └── IdGeneratorTest.java
```

The **test package structure typically mirrors the main structure** — test classes live in the same package as the classes they test, giving them access to `package-private` members.

---

### Access Modifiers and Package Visibility

Java has four access levels. **Package-private** is the default (no keyword):

| Modifier | Class itself | Same package | Subclass (any package) | Any class |
|----------|------------|--------------|----------------------|-----------|
| `public` | ✅ | ✅ | ✅ | ✅ |
| `protected` | ✅ | ✅ | ✅ | ❌ |
| *(none — package-private)* | ✅ | ✅ | ❌ | ❌ |
| `private` | ✅ | ❌ | ❌ | ❌ |

**Package-private** (no modifier) is a powerful tool — it lets classes in the same package collaborate directly while keeping internals completely hidden from the outside world:

```java
// Internal helper — package-private, not part of the public API
class RequestValidator {           // No 'public' keyword
    boolean validate(Request r) {  // Also package-private
        return r != null && r.getPath() != null;
    }
}

// Public service — can use the package-private helper directly
public class RequestHandler {
    private final RequestValidator validator = new RequestValidator();

    public Response handle(Request request) {
        if (!validator.validate(request)) {
            return Response.badRequest();
        }
        // ...
    }
}
```

External callers cannot instantiate `RequestValidator` directly — they can only use `RequestHandler`.

> **QA note:** Test classes in the **same package** can access package-private members — this is why test source follows the same package structure as production code. It lets you test internal helpers without making them `public`.

---

### The Default Package (Avoid)

If you omit the `package` declaration, the class goes into the **unnamed default package**:

```java
// No package declaration — class is in the default package
public class MyClass { }
```

**Problems with the default package:**
- Classes in named packages **cannot import** from the default package
- JAR files cannot expose default-package classes
- Tools (IDEs, documentation generators, static analysis) handle them poorly
- Name collisions become likely in any real project

**Only acceptable for:** very small scratch programs, quick throwaway tests, and tutorial "hello world" programs. **Never** in production code or shared libraries.

---

### Layered Package Structure in Enterprise Projects

Enterprise Java projects conventionally organise packages by **architectural layer** or **feature**:

**Layer-based layout** (common for smaller projects):
```
com.example.app/
├── controller/     → HTTP endpoint classes, REST controllers
├── service/        → Business logic
├── repository/     → Data access (database queries)
├── model/          → Data classes, entities, DTOs
├── config/         → Configuration classes (Spring beans, etc.)
└── util/           → Stateless helper utilities
```

**Feature-based layout** (common for microservices and larger projects):
```
com.example.app/
├── user/
│   ├── UserController.java
│   ├── UserService.java
│   ├── UserRepository.java
│   └── User.java
├── order/
│   ├── OrderController.java
│   ├── OrderService.java
│   └── Order.java
└── shared/
    └── util/
        └── DateUtils.java
```

Both are valid — what matters is **consistency** within the project.

---

### Packages vs Maven `groupId`

The Maven **`groupId`** in `pom.xml` and the Java **`package`** declaration are **related by convention but not enforced**:

```xml
<!-- pom.xml -->
<groupId>com.example.qa</groupId>
<artifactId>test-framework</artifactId>
```

```java
// Java source file
package com.example.qa.framework.utils;   // extends groupId with more segments
```

**Convention:** The `groupId` becomes the root prefix of all packages in the project. This makes it easy to find which JAR a class came from.

**Not enforced:** The compiler does not verify that your package matches the `groupId`. Mismatches cause confusion but not build errors.

---

### Accessing Classes Across Packages

Once you have defined classes in packages, other classes reference them:

```java
package com.example.qa.service;

// Import the IdGenerator from the utils package
import com.example.qa.utils.IdGenerator;

public class UserService {
    public String createUser(String name) {
        String id = IdGenerator.generate();   // No package prefix needed after import
        // ...
        return id;
    }
}
```

Or use the fully qualified name without importing:

```java
String id = com.example.qa.utils.IdGenerator.generate();
```

---

## Summary

- **Packages** are named namespaces that prevent class name collisions and organise code logically.
- Use **reverse-DNS** naming: all lowercase, dot-separated, starting with your domain reversed (e.g. `com.example.qa`).
- The **file path on disk** must exactly mirror the `package` declaration — misalignment causes compile errors.
- **Maven's `src/main/java`** and `src/test/java` are source roots; the package directory tree begins inside them.
- **Access modifiers** — `public` (all), `protected` (same package + subclasses), **package-private** (same package only), `private` (class only). No modifier = package-private.
- The **default package** (no `package` declaration) is acceptable only for scratch programs — avoid in real projects.
- Test classes mirror the main package structure so they can access `package-private` members without requiring `public` exposure.
- The Maven `groupId` conventionally matches the root Java package — by team convention, not compiler enforcement.

---

## Additional Resources

- [Creating and Using Packages (Oracle Tutorial)](https://docs.oracle.com/javase/tutorial/java/package/index.html)
- [Managing Source and Class Files (Oracle Tutorial)](https://docs.oracle.com/javase/tutorial/java/package/managingfiles.html)
- [Maven Standard Directory Layout](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html)
- [JLS: Packages and Modules](https://docs.oracle.com/javase/specs/jls/se21/html/jls-7.html)
