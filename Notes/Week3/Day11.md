# Day 11 - Java: OOP Internals, Collections, Streams & JUnit

- `sysout` → VS Code shortcut that expands to `System.out.println()`

---

## Maven & `pom.xml`

Maven is a Java **build tool** — it compiles code, manages dependencies, and runs tests. Configuration lives in `pom.xml` (Project Object Model).

```xml
<groupId>com.day1java</groupId>       <!-- organization/package -->
<artifactId>williamjava</artifactId>  <!-- project name -->
<version>1.0-SNAPSHOT</version>

<dependencies>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.2</version>
        <scope>test</scope>           <!-- only available during testing -->
    </dependency>
</dependencies>
```

```bash
mvn compile       # compile source code
mvn test          # compile + run all tests
```

`maven-surefire-plugin` — the Maven plugin that discovers and runs JUnit tests.

---

## Operators

### Relational
```java
==  !=  >  <  >=  <=
```

### Logical
```java
&&   // short-circuit AND — skips right side if left is false
||   // short-circuit OR  — skips right side if left is true
&    // non-short-circuit AND — always evaluates both sides
|    // non-short-circuit OR  — always evaluates both sides
!    // NOT
^    // XOR — true if exactly one side is true
```

### Increment / Decrement
```java
count++   // post-increment: use value, then add 1
++count   // pre-increment:  add 1, then use value
count--   // post-decrement
```

---

## Conditionals

### `switch / case`
Best for exact-value matching on a single variable. **Requires `break`** to prevent fall-through to the next case.

```java
switch (num) {
    case 0: {
        System.out.println("zero");
        break;
    }
    case 1: {
        System.out.println("one");
        break;
    }
    default: {
        System.out.println("something else");
        break;
    }
}
```

### Ternary
```java
String result = (num > 5) ? "greater than 5" : "5 or less";
//               condition    true value         false value
```

---

## Loops

### `while`
```java
int count = 1;
while (count < 100) {
    System.out.println(count);
    count++;
}
```

### `do-while`
Executes the body **at least once**, then checks the condition.

```java
do {
    System.out.println("runs at least once");
} while (count <= 10);   // condition checked AFTER the body
```

### `Scanner` — reading a full line
```java
String input = sc.nextLine();        // reads entire line (vs sc.next() for one token)
int number = Integer.parseInt(input);
```

---

## Classes & OOP Internals

### `Object` — the base class
Every Java class implicitly extends `Object`. That's where `toString()`, `equals()`, and `hashCode()` come from.

```java
Object obj = new Object();
obj.toString();   // default: "ClassName@hashcode"
```

### Static initializer block
Runs **once** when the class is first loaded by the JVM — before any constructor:

```java
static {
    System.out.println("class loaded");   // fires once, ever
}
```

### `final` fields
Assigned exactly once (in the constructor) and immutable after that:

```java
private final int id;

Student(String name) {
    this.id = nextId++;   // assigned here, can never be changed again
}
```

### `@Override`
Annotation that tells the compiler "this method replaces one from a parent class." Causes a compile error if you get the signature wrong — use it whenever intentionally overriding:

```java
@Override
public String toString() {
    return "Student{id=" + id + ", name='" + name + "'}";
}
```

### Overriding `equals()` and `hashCode()`
Java's default `==` checks object identity (same reference). Override `equals()` for value-based comparison. **Rule: always override `hashCode()` when you override `equals()`** — collections like `HashMap` depend on them being consistent.

```java
@Override
public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Student s = (Student) o;                         // cast to the right type
    return id == s.id && Objects.equals(name, s.name);  // null-safe field compare
}

@Override
public int hashCode() {
    return Objects.hash(id, name);   // generate hash from multiple fields
}
```

`Objects.equals(a, b)` — null-safe: won't NPE if either side is null.
`Objects.hash(...)` — convenience method to combine multiple fields into one hash.

### Inner static class
A class defined inside another class with `static` — can be instantiated without an instance of the outer class:

```java
class DemoClassesObjects {
    static class Student { ... }
}

// Usage from another file:
import com.day1java.DemoClassesObjects.Student;
Student a = new Student("Ash");
```

---

## Arrays

```java
int[] scores = {72, 31, 67, 23, 88};
scores.length                           // number of elements (property, no parens)

int[][] grid = {{1,2}, {4,5,6}};        // jagged 2D array — rows can have different lengths
grid.length                             // number of rows
grid[0].length                          // number of cols in row 0
```

### `java.util.Arrays` utility
```java
import java.util.Arrays;

Arrays.toString(arr)          // "[72, 31, 67]"  — printable string of array contents
Arrays.copyOf(arr, arr.length) // shallow copy
Arrays.sort(arr)              // in-place ascending sort
Arrays.binarySearch(arr, 88)  // index of target (array must be sorted first)
```

---

## `ArrayList<T>` — Dynamic List

Unlike arrays, `ArrayList` resizes automatically. The `<T>` is the **generic type** — specifies what the list holds.

```java
import java.util.ArrayList;
import java.util.List;

ArrayList<Integer> list = new ArrayList<>();
list.add(5);              // append
list.get(0);              // access by index
list.size();              // length

List<String> names = new ArrayList<>();   // preferred: declare as interface type List<>
```

**Converting `ArrayList<Integer>` → `int[]`** (requires a stream):
```java
arrList.stream().mapToInt(Integer::intValue).toArray()
```

---

## `StringBuilder` — Mutable String

Strings in Java are immutable — every `+` creates a new object. `StringBuilder` avoids this:

```java
StringBuilder sb = new StringBuilder("hello");
sb.append(" world");     // "hello world"
sb.delete(0, 5);         // delete chars from index 0 up to (not including) 5
sb.setLength(0);         // clear — equivalent to emptying it
sb.length();             // current length
sb.toString();           // convert back to regular String
String.valueOf(ch)       // convert a char to String: String.valueOf(sentence.charAt(0))
```

`sentence.substring(0, i)` — extract substring from index 0 up to (not including) `i`.
`sentence.charAt(i)` — character at index `i`.

---

## Streams & Method References

A **stream** processes a collection pipeline-style — similar to Python's `map/filter` chain.

```java
import java.util.stream.IntStream;
import java.util.stream.Stream;

// Generate an int array [1, 2, 3, ..., n]
int[] arr = IntStream.rangeClosed(1, n).toArray();   // inclusive on both ends

// Convert ArrayList<Integer> → int[]
list.stream()
    .mapToInt(Integer::intValue)   // Integer::intValue is a method reference
    .toArray();
```

**Method reference** — shorthand for a lambda that just calls one method:
```java
Integer::intValue    // same as: x -> x.intValue()
```

---

## JUnit 5 — Unit Testing

JUnit 5 (Jupiter) is Java's standard testing framework. Tests live in `src/test/java` and Maven runs them automatically with `mvn test`.

### Basic assertion
```java
import static org.junit.jupiter.api.Assertions.assertEquals;

assertEquals(expected, actual);   // passes silently, throws AssertionError on mismatch
```

`import static` — imports a static method directly so you can call `assertEquals(...)` without the class prefix.

### Parameterized tests
Run the same test logic against multiple input/output pairs without duplicating code:

```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.DisplayName;
import java.util.stream.Stream;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("Set Reducer")         // human-readable name shown in test output
class ChallengesTest {

    // Method that supplies test cases — must be static, return Stream<Arguments>
    static Stream<Arguments> sampleTests() {
        return Stream.of(
            arguments(2, new int[]{0, 4, 6, 8}),   // (expected, input)
            arguments(3, new int[]{8, 1, 6, 1})
        );
    }

    @ParameterizedTest(name = "Input: {1}")   // {1} = second arg in the name
    @MethodSource                              // use the method with the same name as the source
    @DisplayName("Sample Tests")
    void sampleTests(int expected, int[] input) {
        assertEquals(expected, Challenges.setReducer(input));
    }
}
```

| Annotation | Purpose |
|---|---|
| `@ParameterizedTest` | marks test as parameterized — runs once per argument set |
| `@MethodSource` | points to a static method that returns `Stream<Arguments>` |
| `@DisplayName` | custom label in test output |
| `arguments(...)` | wraps one set of test inputs |

---

## Recursion in Java

Same concept as Python — base case stops it, recursive case calls itself with a smaller input:

```java
public static int fib(int n) {
    if (n == 0) return 0;   // base case
    if (n == 1) return 1;   // base case
    return fib(n - 1) + fib(n - 2);   // recursive case
}
```
