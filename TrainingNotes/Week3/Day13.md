# Day 13 - Java Collections & Exceptions

---

## `List<T>` — Interface-Typed Collections

Declare the variable as the interface, instantiate as an implementation:

```java
List<Person> people = new ArrayList<>();
people.add(ryan);
people.add(2, ryan);  // insert at index 2
people.get(2);        // access by index
people.size();        // number of elements
```

---

## `ArrayList` vs `LinkedList` vs `Vector`

Benchmarked with `System.nanoTime()`:

| Operation | ArrayList | LinkedList | Vector |
|---|---|---|---|
| Add to end | Fast | Fast | Fast (synchronized) |
| Add to front | Slow (shifts all) | Fast (pointer swap) | Slow |
| Random access `get(i)` | Fast (array index) | Slow (traverse) | Fast |
| Iterator traversal | Fast | Fast | Fast |
| Thread-safe | No | No | Yes |

`Vector` — legacy synchronized `ArrayList`; prefer `Collections.synchronizedList()` in modern code.

---

## `HashSet<T>` — Unique Elements

Unordered, no duplicates. Uses `hashCode()` + `equals()` for deduplication:

```java
Set<Person> personSet = new HashSet<>();
personSet.addAll(people);  // duplicates are dropped
personSet.size();
```

---

## `HashMap<K,V>` / `Map<K,V>`

Key→value store. Keys must be unique:

```java
Map<Integer, Person> personMap = new HashMap<>();
personMap.put(1, ryan);
personMap.put(42, paul);
personMap.get(42);  // returns paul

// Preferred: enhanced for loop over keySet
for (Integer key : personMap.keySet()) {
    System.out.println(key + " = " + personMap.get(key));
}

// Less efficient: convert keySet to List first (extra allocation, no benefit)
List<Integer> keyList = new ArrayList<>(personMap.keySet());
```

---

## `Math.random()`

Returns a `double` in `[0.0, 1.0)`:

```java
double luck = Math.random();
```

---

## `System.nanoTime()`

High-precision timing:

```java
long start = System.nanoTime();
// ... work ...
long end = System.nanoTime();
System.out.println((end - start) / 1_000_000 + "ms");  // ns → ms
```

`1_000_000` — numeric literals can use underscores for readability (Java 7+).

---

## Factory Pattern

A static method returning different implementations based on input — callers only see the interface:

```java
public static List<Object> getList(int choice) {
    switch (choice) {
        case 1: return new ArrayList<>();
        case 2: return new LinkedList<>();
        case 3: return new Vector<>();
        default: return new ArrayList<>();
    }
}
```

---

## Exception Handling

### `try/catch/finally`

```java
try {
    int result = x / y;
} catch (ArithmeticException e) {   // most specific FIRST
    e.printStackTrace();            // prints full stack trace
} catch (RuntimeException e) {      // less specific second
    System.out.println("Something went wrong!");
} finally {
    scan.close();   // ALWAYS runs — exception or not
}
```

`e.getMessage()` — returns the message string passed to the exception constructor.

### `throws` — declaring checked exceptions

Methods that can throw a checked exception must declare it; callers must catch or re-declare:

```java
public static void readAwfulFile() throws FileNotFoundException {
    File f = new File("/not/a/real/file");
    FileReader read = new FileReader(f);  // throws FileNotFoundException if missing
}
```

### Custom Exceptions

Extend `RuntimeException` for unchecked (no `throws` required), `Exception` for checked:

```java
public class LostMoneyException extends RuntimeException {
    public LostMoneyException(String msg) {
        super(msg);  // passes message up to RuntimeException
    }
}

// Throw it
throw new LostMoneyException("You lost all your money!");

// Declare + catch it
public static void gamble(double n) throws LostMoneyException {
    if (n <= 0.5) throw new LostMoneyException("You lost!");
}

try {
    gamble(luck);
} catch (LostMoneyException e) {
    System.out.println(e.getMessage());
    e.printStackTrace();
}
```

### Checked vs Unchecked

| | Checked | Unchecked |
|---|---|---|
| Extends | `Exception` | `RuntimeException` |
| Must declare `throws`? | Yes | No |
| Examples | `FileNotFoundException`, `IOException`, `SQLException` | `ArithmeticException`, `NullPointerException` |

### `File` / `FileReader`

```java
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;

File f = new File("/path/to/file");
f.exists();                   // boolean — does the file exist?
FileReader read = new FileReader(f);  // throws FileNotFoundException if missing
```
