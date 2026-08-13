# The `java.lang.Object` Class

## Learning Objectives
- Explain why every Java class implicitly or explicitly extends `Object`.
- Override `toString()` to produce useful string representations.
- Implement `equals(Object o)` correctly following the **five-property contract**.
- Implement `hashCode()` consistently with `equals` following the **hashCode contract**.
- Use IDE code generation and `java.util.Objects` helpers to avoid manual bugs.
- Understand `getClass()` for runtime type inspection.
- Know when to use **`record`** (Java 16+) as an alternative to manually overriding these methods.

---

## Why This Matters

> **Weekly Epic Connection:** `Object` is the root of the entire Java class hierarchy — every class inherits its methods. Correct `equals`/`hashCode` implementations are essential for collections (`HashMap`, `HashSet`, `TreeSet`) later this week. Broken `equals` causes tests to silently pass with wrong data. Broken `hashCode` causes `HashMap` lookups to fail mysteriously. These bugs are subtle, common, and produce exactly the kind of intermittent test failures that QA must diagnose.

---

## The Concept

### Every Class Extends `Object`

If your class declaration omits `extends`, the compiler inserts `extends Object` automatically:

```java
public class Point { }                  // Implicitly: public class Point extends Object { }
public class Point extends Object { }   // Explicit — identical behaviour
```

This means every object in Java, regardless of type, has these methods available:

| Method | Default Behaviour (from `Object`) | Override? |
|--------|----------------------------------|-----------|
| `toString()` | `"ClassName@hexHashCode"` e.g. `"Point@1b6d3586"` | ✅ Yes — almost always |
| `equals(Object o)` | Reference equality (`this == o`) | ✅ Yes — when value equality matters |
| `hashCode()` | Memory-address-derived integer | ✅ Yes — whenever you override `equals` |
| `getClass()` | Runtime `Class<?>` object | ❌ `final` — cannot override |
| `clone()` | Shallow copy (protected) | Rarely — use copy constructors instead |
| `finalize()` | Empty hook before GC | ❌ Deprecated — never override |
| `wait()`, `notify()`, `notifyAll()` | Thread coordination | ❌ Specialised threading use only |

---

### `toString()` — Readable String Representation

The default `toString()` from `Object` returns something like `"Point@1b6d3586"` — the class name and a hex hash. This is useless for debugging.

Override `toString()` to return meaningful information:

```java
public class Point {
    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Point(" + x + ", " + y + ")";
    }
}

Point p = new Point(3, 4);
System.out.println(p);              // "Point(3, 4)"  — println calls toString() implicitly
System.out.println("Location: " + p); // "Location: Point(3, 4)"  — string concat calls toString()
```

**Why `@Override` matters:** The annotation tells the compiler you intend to override a method from a superclass. If you misspell the name (`tostring()` with lowercase `s`), the compiler warns you — without `@Override`, you silently define a new method and the bug goes unnoticed.

**Null-safe `toString` with `Objects.toString()`:**
```java
String result = Objects.toString(someObject, "N/A");   // Returns "N/A" if someObject is null
```

---

### `equals(Object o)` — The Five-Property Contract

The `equals` contract is defined in the `Object` javadoc. Your override **must** satisfy all five properties — breaking any one causes subtle, hard-to-debug failures in collections and assertions:

| Property | Meaning | Consequence of Breaking |
|----------|---------|------------------------|
| **Reflexive** | `x.equals(x)` is always `true` | Broken set membership checks |
| **Symmetric** | If `x.equals(y)` then `y.equals(x)` | `HashSet.contains()` gives different results depending on order |
| **Transitive** | If `x.equals(y)` and `y.equals(z)` then `x.equals(z)` | Equivalence classes break — sorting and deduplication fail |
| **Consistent** | Repeated calls return same result (no random state) | Non-deterministic test failures |
| **Null-safe** | `x.equals(null)` is always `false` (never throws) | `NullPointerException` when comparing to null |

**Correct `equals` implementation pattern:**

```java
@Override
public boolean equals(Object o) {
    // 1. Reflexive check — shortcut for identity
    if (this == o) return true;

    // 2. Null check + type check (ensures null-safety and symmetry)
    if (o == null || getClass() != o.getClass()) return false;

    // 3. Cast — safe because we checked getClass()
    Point other = (Point) o;

    // 4. Compare all significant fields
    return this.x == other.x && this.y == other.y;
}
```

**Using `Objects.equals` for reference field comparison** (handles null safely):
```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Person other = (Person) o;

    return age == other.age                      // Primitives: use ==
        && Objects.equals(name, other.name);     // References: use Objects.equals (null-safe)
}
```

---

### `hashCode()` — The Two-Part Contract

`hashCode` works in tandem with `equals`. The contract has two parts:

| Rule | Meaning |
|------|---------|
| **Consistency with `equals`** | If `a.equals(b)` is `true`, then `a.hashCode() == b.hashCode()` **must** be `true` |
| **Internal consistency** | `hashCode()` must return the same value for the same object during a single JVM run (unless `equals`-relevant fields change) |

> **The reverse is NOT required:** Two objects with the same `hashCode` are not necessarily equal (hash collision is acceptable). But equal objects MUST have the same hash code.

**Why this matters for collections:**
```
HashMap.get(key):
  1. Compute key.hashCode() → find the bucket
  2. For each object in the bucket: call key.equals(candidate)
  3. Return if found

If hashCode is wrong → wrong bucket → equals never called → lookup always fails
```

**Correct `hashCode` with `Objects.hash()`:**
```java
@Override
public int hashCode() {
    return Objects.hash(x, y);   // Hash all fields used in equals — same set, same order
}
```

`Objects.hash()` accepts varargs and combines them using a standard algorithm. Include **exactly the same fields** you compare in `equals`:

```java
// equals compares: name, age, email
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Person p = (Person) o;
    return age == p.age
        && Objects.equals(name, p.name)
        && Objects.equals(email, p.email);
}

// hashCode must include: name, age, email (same fields)
@Override
public int hashCode() {
    return Objects.hash(name, age, email);  // ✅ Same fields as equals
}
```

**Common mistake:**
```java
// ❌ equals includes email but hashCode omits it
@Override
public int hashCode() {
    return Objects.hash(name, age);  // Wrong — email is in equals but not hashCode
}
// Two Person objects with same name+age but different emails will:
//   hashCode: same (both go to same bucket) ✅
//   equals: different (email differs) ✅
// BUT: two objects with same name+age+email:
//   hashCode: same ✅
//   equals: true ✅
// This specific case actually works... but it's fragile and wrong by contract.
```

---

### IDE Code Generation

Writing `equals` and `hashCode` by hand is error-prone. Most IDEs can generate correct implementations:

**IntelliJ IDEA:**
1. Place cursor inside the class
2. `Alt+Insert` (Windows/Linux) or `Cmd+N` (Mac)
3. Select **Generate → `equals()` and `hashCode()`**
4. Choose the fields to include

**The generated code typically uses `Objects.equals` and `Objects.hash` — review the field selection:**
```java
// Typical IntelliJ-generated output
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Person person = (Person) o;
    return age == person.age
        && Objects.equals(name, person.name)
        && Objects.equals(email, person.email);
}

@Override
public int hashCode() {
    return Objects.hash(name, age, email);
}
```

> **QA check after generation:** Verify the correct fields are selected. Sometimes auto-generated code includes `id` or `createdAt` fields that should not affect equality (e.g. two DTOs representing the same logical entity might have different database IDs).

---

### `getClass()` — Runtime Type Inspection

`getClass()` returns the runtime `Class<?>` object representing the actual type of the object:

```java
Object obj = "hello";
System.out.println(obj.getClass());           // class java.lang.String
System.out.println(obj.getClass().getName()); // java.lang.String
System.out.println(obj.getClass().getSimpleName()); // String

// Type check without instanceof
if (obj.getClass() == String.class) {
    System.out.println("It's a String");
}

// Safe to use in equals — ensures exact type match (rejects subclasses)
if (o == null || getClass() != o.getClass()) return false;
```

`getClass()` is `final` — you cannot override it.

---

### `clone()` — Mostly Avoided

`Object.clone()` is `protected` and performs a **shallow copy** (copies field values, including references — not the objects they point to). To use it, a class must:
1. Implement the marker interface `Cloneable`
2. Override `clone()` with `public` visibility
3. Call `super.clone()` and handle `CloneNotSupportedException`

In practice, most teams avoid `clone()` because of its complexity and pitfalls. Prefer:
- **Copy constructors**: `new User(existingUser)`
- **Factory methods**: `User.copyOf(existingUser)`
- **Record copy-with**: `existingRecord.withName("newName")` (Java 14+ records)

---

### `record` — Auto-Generated `equals`, `hashCode`, `toString` (Java 16+)

If your class is an immutable data carrier, use a **`record`** — the compiler automatically generates correct `equals`, `hashCode`, and `toString` based on all record components:

```java
// record — compiler generates: constructor, accessors, equals, hashCode, toString
public record Point(int x, int y) { }

// Usage
Point p1 = new Point(3, 4);
Point p2 = new Point(3, 4);
Point p3 = new Point(5, 6);

System.out.println(p1.equals(p2));   // true  — value equality by components
System.out.println(p1.equals(p3));   // false
System.out.println(p1.hashCode() == p2.hashCode());  // true
System.out.println(p1);              // "Point[x=3, y=4]"

// Accessors (not getX() — just x())
System.out.println(p1.x());  // 3
System.out.println(p1.y());  // 4
```

**When to use `record` vs regular class:**

| Scenario | Use |
|----------|-----|
| Immutable data carrier (DTO, value object, test fixture) | `record` |
| Mutable state, complex business logic | Regular class |
| Needs to extend another class | Regular class (records can't extend) |
| Needs custom `equals` logic | Regular class |
| Configuration, service objects | Regular class |

---

## Summary

- Every Java class implicitly extends `Object` and inherits `toString`, `equals`, `hashCode`, `getClass`, `clone`, `finalize`, and thread methods.
- Override **`toString()`** to return meaningful debug output; use `@Override` to catch typos.
- Override **`equals()`** following the five-property contract: reflexive, symmetric, transitive, consistent, null-safe. Use `Objects.equals()` for reference fields.
- Override **`hashCode()`** whenever you override `equals` — use the **same set of fields** as in `equals`. Use `Objects.hash()`. Equal objects **must** have equal hash codes.
- Use **IDE code generation** for `equals`/`hashCode` — review the selected fields carefully.
- `getClass()` returns the runtime type; `final` — cannot be overridden.
- Avoid `clone()` — prefer copy constructors or factory methods.
- Use **`record`** (Java 16+) for immutable data carriers — auto-generates all three methods correctly.

---

## Additional Resources

- [Object (Java SE 21 API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Object.html)
- [Oracle Java Tutorial — The Object Class](https://docs.oracle.com/javase/tutorial/java/IandI/objectclass.html)
- [Effective Java — Item 10: Obey the general contract when overriding equals](https://www.oreilly.com/library/view/effective-java/9780134686097/)
- [Effective Java — Item 11: Always override hashCode when you override equals](https://www.oreilly.com/library/view/effective-java/9780134686097/)
- [JEP 395: Records (Java 16)](https://openjdk.org/jeps/395)
