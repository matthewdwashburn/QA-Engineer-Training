# Day 14 - Java IO, Lambdas, Sets & Maps

- DO NOT FORGET THAT == compares REFERENCE and .equals() compares VALUE in Java
- Ctrl-Shift-P java: clean java language server workspace if java files ever start losing their packages

---

## Functional Interfaces & Lambdas

A **functional interface** has exactly one abstract method — that's what a lambda implements. Java ships four core ones in `java.util.function`.

| Interface | Signature | Method | Use |
|---|---|---|---|
| `Predicate<T>` | `T → boolean` | `.test(val)` | filter / condition |
| `Function<T,R>` | `T → R` | `.apply(val)` | transform input → output |
| `Consumer<T>` | `T → void` | `.accept(val)` | use/consume, no return |
| `Supplier<T>` | `() → T` | `.get()` | produce a value, no input |

```java
import java.util.function.*;

Predicate<String>         longWord  = s -> s.length() > 5;
Function<String, Integer> getLength = s -> s.length();
Consumer<String>          printer   = s -> System.out.println(s);
Supplier<Double>          rand      = () -> Math.random();

longWord.test("elephant");   // true
getLength.apply("Java");     // 4
printer.accept("Hello");     // prints Hello
rand.get();                  // random double
```

### Chaining on streams

```java
names.stream()
     .filter(longWord)    // keep strings > 5 chars
     .map(upper)          // transform each
     .forEach(printer);   // consume each
```

### Method References

Shorthand for a lambda that just calls a single method — `Type::method` instead of `x -> x.method()`:

```java
names.removeIf(String::isBlank);   // same as: names.removeIf(s -> s.isBlank())
names.forEach(printer);            // same as: names.forEach(s -> printer.accept(s))
```

---

## Primitive Functional Interfaces

When working with primitives, use the specialized versions to avoid boxing overhead:

| Interface | Signature | Method | Example |
|---|---|---|---|
| `IntUnaryOperator` | `int → int` | `.applyAsInt(n)` | `num -> num + addTo` |
| `ToDoubleFunction<T>` | `T → double` | `.applyAsDouble(t)` | compute area from Triangle |
| `IntFunction<R>` | `int → R` | `.apply(n)` | map char code → String |
| `IntPredicate` | `int → boolean` | `.test(n)` | filter chars in a stream |

```java
// IntUnaryOperator — returns a function that closes over addTo
public static IntUnaryOperator create(int addTo) {
    return num -> num + addTo;
}
AdderFactory.create(1).applyAsInt(4);   // 5

// ToDoubleFunction — stored as a static field on the class
public static ToDoubleFunction<Triangle> f = t -> {
    t.setArea((t.height * t.base) / 2.0);
    return t.getArea();
};
Triangle.f.applyAsDouble(new Triangle(5, 10));   // 25.0

// IntPredicate — keep or remove a specific char
IntPredicate keepA = c -> c == 'a';
```

---

## `.chars()` and `IntStream` on Strings

`.chars()` converts a `String` to an `IntStream` of Unicode code points (ints):

```java
"Hello".chars()
       .filter(c -> c != 'a')                      // IntPredicate on char codes
       .mapToObj(c -> String.valueOf((char) c))    // IntStream -> Stream<String>
       .collect(Collectors.joining());             // join back into one String
```

`Collectors.joining()` — terminal stream op that concatenates all `Stream<String>` elements into one String.

---

## `TreeSet<T>` — Sorted Set

`HashSet` has no order. `TreeSet` automatically sorts elements using `compareTo()`. Your class must implement `Comparable<T>`:

```java
TreeSet<Sku> tree = new TreeSet<>();
tree.add(new Sku("C"));
tree.add(new Sku("A"));
tree.add(new Sku("B"));
// iterates in sorted order: A, B, C
```

```java
public class Sku implements Comparable<Sku> {
    @Override
    public int compareTo(Sku o) {
        return code.compareToIgnoreCase(o.code);
        // negative = this before o, 0 = equal (treated as duplicate), positive = this after o
    }
}
```

`TreeSet` uses `compareTo()` for both ordering **and** duplicate detection — two objects where `compareTo()` returns `0` are only stored once.

---

## `HashMap.merge()`

```java
Map<Sku, Integer> stock = new HashMap<>();
stock.put(new Sku("A"), 10);

// merge(key, value, mergeFn):
//   if key absent  -> insert value
//   if key exists  -> apply mergeFn(oldValue, newValue)
stock.merge(new Sku("A"), 2, Integer::sum);   // A: 10 + 2 = 12
stock.merge(new Sku("Z"), 4, Integer::sum);   // Z: 4 (inserted fresh)
```

---

## `Comparable<T>` vs `equals()`/`hashCode()`

| Method | Used by | Purpose |
|---|---|---|
| `equals()` / `hashCode()` | `HashSet`, `HashMap` | duplicate detection, key matching |
| `compareTo()` | `TreeSet`, `TreeMap`, sorting | natural ordering |

For case-insensitive matching across both:

```java
@Override public boolean equals(Object o) {
    if (!(o instanceof Sku sku)) return false;
    return code.equalsIgnoreCase(sku.code);
}
@Override public int hashCode() {
    return code.toLowerCase().hashCode();   // must be consistent with equals
}
@Override public int compareTo(Sku o) {
    return code.compareToIgnoreCase(o.code);
}
```

---

## Java IO — `java.nio.file`

Modern file API. Three main classes: `Path`, `Paths`, `Files`.

### Path / Paths

```java
import java.nio.file.Path;
import java.nio.file.Paths;

Path path = Paths.get("data/scores.csv");   // create a Path from string
path.toAbsolutePath();                      // full path from filesystem root
path.getFileName();                         // scores.csv
path.getParent();                           // data
path.resolve("output.txt");                 // data/output.txt  (append a segment)
```

### Files — inspection

```java
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

Files.exists(path)              // boolean
Files.isRegularFile(path)       // boolean
Files.size(path)                // bytes as long
Files.createDirectories(path)   // mkdir -p — creates parent dirs if missing

BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
attrs.lastModifiedTime();       // FileTime
```

### Reading — `BufferedReader` (large files)

Line-by-line — never loads the whole file into memory:

```java
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;

try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
    String line;
    while ((line = reader.readLine()) != null) {   // readLine() returns null at EOF
        System.out.println(line);
    }
}
```

### Reading — `Files` convenience methods (small files)

```java
List<String> lines = Files.readAllLines(path);   // all lines -> List<String>
String all          = Files.readString(path);    // whole file as one String
```

### Writing — three flavors

```java
import java.nio.file.StandardOpenOption;

// BufferedWriter — fine-grained control, overwrites by default
try (BufferedWriter writer = Files.newBufferedWriter(path)) {
    writer.write("some text");
    writer.newLine();
}

// Append mode — pass StandardOpenOption.APPEND
try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {
    writer.write("appended line");
    writer.newLine();
}

// writeString — most concise single write
Files.writeString(path, "Total: 50 | Pass: 48\n");

// write(List) — write a list of lines at once
Files.write(path, List.of("PASS LoginTest", "FAIL CheckoutTest"));
```

### IO Quick Reference

| Method | Notes |
|---|---|
| `Files.readAllLines(path)` | whole file -> `List<String>` |
| `Files.readString(path)` | whole file -> `String` |
| `Files.newBufferedReader(path, charset)` | line-by-line, memory safe for large files |
| `Files.newBufferedWriter(path)` | overwrite |
| `Files.newBufferedWriter(path, APPEND)` | append |
| `Files.writeString(path, str)` | one-liner write |
| `Files.write(path, list)` | write `List<String>` as lines |
| `Files.createDirectories(path)` | mkdir -p |
| `Files.exists(path)` | check before creating |

---

## Stream + IO Pipeline

Read a CSV, skip the header, filter by a parsed column value, write results:

```java
List<String> failures = Files.readAllLines(input)
    .stream()
    .skip(1)                                            // skip header row
    .filter(line -> {
        String[] parts = line.split(",");
        int score = Integer.parseInt(parts[1].trim()); // trim whitespace before parseInt
        return score < 50;
    })
    .toList();

Files.createDirectories(output.getParent());
Files.write(output, failures);
```

---

## `System.out.printf()` — Formatted Output

```java
System.out.printf("Line %2d: %s %n", lineNum, line);
// %2d  — int right-aligned in 2 chars
// %s   — string
// %n   — platform line separator (prefer over \n in printf)
// %.2f — float/double with 2 decimal places
```

---

## `LocalDateTime`

```java
import java.time.LocalDateTime;

LocalDateTime.now()   // current date + time, no timezone info
// toString example: 2026-06-18T14:30:00.123
```