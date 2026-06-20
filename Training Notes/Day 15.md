# Day 15 - Java Queues, Regex, Logging & Comparators

---

## SLF4J + Logback — Java Logging

**SLF4J** is the logging API (interface). **Logback** is the implementation that runs behind it. Same idea as Python's `logging` module — named loggers, levels, appenders.

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

private static final Logger log = LoggerFactory.getLogger("com.revature.MyClass");

log.trace("...");   // most verbose — below DEBUG
log.debug("...");   // development detail
log.info("...");    // normal operational messages
log.warn("...");    // unexpected but recoverable
log.error("...");   // failure
```

### `logback.xml` — configuration file

Placed in `src/main/resources/`. Controls where logs go and at what level:

```xml
<configuration>
    <!-- Console appender -->
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- File appender -->
    <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <file>logs/demo-logback.log</file>
        <append>false</append>   <!-- false = overwrite on each run -->
        <encoder>
            <pattern>%d{ISO8601} %-5level %logger - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- Per-package level override -->
    <logger name="com.revature" level="DEBUG"/>

    <!-- Root (everything else) -->
    <root level="INFO">
        <appender-ref ref="STDOUT"/>
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

| Pattern token | Meaning |
|---|---|
| `%d{HH:mm:ss.SSS}` | timestamp |
| `[%thread]` | thread name |
| `%-5level` | level left-padded to 5 chars |
| `%logger{36}` | logger name, max 36 chars |
| `%msg` | the log message |
| `%n` | newline |

---

## `Deque<T>` / `ArrayDeque<T>`

A **Deque** (double-ended queue) can be used as both a queue (FIFO) and a stack (LIFO). `ArrayDeque` is the go-to implementation.

```java
import java.util.ArrayDeque;
import java.util.Deque;

Deque<String> d = new ArrayDeque<>();
```

### As a Queue (FIFO — add back, remove front)

```java
d.addLast("a");          // enqueue to back
d.addLast("b");
d.removeLast();          // remove from back (browser history "go back" pattern)
d.pollFirst();           // remove from front — returns null if empty (safe)
```

### As a Stack (LIFO — push/pop front)

```java
d.push("JAVA");   // push to front
d.push("SQL");
d.peek();         // look at front without removing
d.pop();          // remove and return front
```

### Sliding window (addLast / pollFirst)

```java
deque.addLast(num);
if (deque.size() > m) deque.pollFirst();   // drop oldest element to maintain window size
```

---

## `PriorityQueue<T>`

A **min-heap** by default — `poll()` always removes the element with the lowest `compareTo()` value. Requires the element type to implement `Comparable<T>`.

```java
import java.util.PriorityQueue;
import java.util.Queue;

Queue<Task> tasks = new PriorityQueue<>();
tasks.add(new Task("Fix bug", 1));
tasks.add(new Task("Update site", 3));

tasks.poll();       // removes + returns lowest priority number (1 out first)
tasks.isEmpty();    // check before polling
```

To get **max-heap** behavior, reverse the comparison in `compareTo()`:

```java
// min-heap (lowest number out first):
return Integer.compare(this.priority, o.priority);

// max-heap (highest number out first):
return Integer.compare(o.priority, this.priority);
```

---

## `Comparator<T>` vs `Comparable<T>`

Both define ordering, but in different places:

| | `Comparable<T>` | `Comparator<T>` |
|---|---|---|
| Location | inside the class being sorted | separate class (external) |
| Method | `compareTo(T o)` | `compare(T a, T b)` |
| Used by | `PriorityQueue`, `TreeSet`, natural sort | `Arrays.sort(arr, cmp)`, `Collections.sort(list, cmp)` |
| How many orderings | one (natural) | many — define as many as you need |

```java
// Comparable — baked into the class
public class Task implements Comparable<Task> {
    @Override
    public int compareTo(Task o) {
        return Integer.compare(this.priority, o.priority);
    }
}

// Comparator — external, passed to sort call
public class Checker implements Comparator<Player> {
    @Override
    public int compare(Player a, Player b) {
        if (a.score != b.score) return b.score - a.score;  // desc score
        return a.name.compareTo(b.name);                   // asc name on tie
    }
}

Arrays.sort(players, new Checker());   // pass comparator instance to sort
```

### Multi-key `compareTo()` pattern

```java
@Override
public int compareTo(Student s) {
    if (s.getCGPA() != this.getCGPA())
        return Double.compare(s.getCGPA(), this.getCGPA());  // desc CGPA
    if (!s.getName().equals(this.getName()))
        return this.getName().compareTo(s.getName());        // asc name
    return Integer.compare(this.getID(), s.getID());         // asc id
}
```

Compare helpers: `Integer.compare(a, b)`, `Double.compare(a, b)`, `String.compareTo()` — all return negative/zero/positive.

---

## Java Regex — `Pattern` & `Matcher`

```java
import java.util.regex.*;

Pattern p = Pattern.compile("<(.+?)>([^<>]+)</\\1>");
Matcher m = p.matcher(inputString);

while (m.find()) {   // advances to next match; returns false when no more matches
    m.group(0);      // entire match
    m.group(1);      // first capture group  (...)
    m.group(2);      // second capture group
}
```

### Key regex syntax in Java strings

| Syntax | Meaning |
|---|---|
| `(.+?)` | non-greedy capture group — as few chars as possible |
| `[^<>]+` | one or more chars that are NOT `<` or `>` |
| `\\1` | backreference to group 1 (double backslash = one real backslash in the pattern) |
| `\\.` | literal dot |

Pattern `<(.+?)>([^<>]+)</\\1>` — matches `<tag>content</tag>` where the closing tag must exactly match the opening tag (via backreference `\1`).

---

## Sliding Window Pattern (Deque + HashMap)

Find the max number of unique values in any window of size `m` across `n` numbers:

```java
Deque<Integer> deque = new ArrayDeque<>();
HashMap<Integer, Integer> freq = new HashMap<>();
int maxUnique = 0;

for (int i = 0; i < n; i++) {
    int num = in.nextInt();
    deque.addLast(num);
    freq.put(num, freq.getOrDefault(num, 0) + 1);  // increment frequency

    if (deque.size() > m) {
        int removed = deque.pollFirst();            // drop oldest from front
        freq.put(removed, freq.get(removed) - 1);
        if (freq.get(removed) <= 0) freq.remove(removed);
    }

    if (deque.size() == m)
        maxUnique = Math.max(maxUnique, freq.size());  // map.size() = unique count
}
```

`map.size()` = number of keys currently in the map = number of distinct values in the window.

---

## JUnit 5 — Redirecting stdin/stdout for Console App Tests

When testing a program that reads from `System.in` or prints to `System.out`, inject fake streams:

```java
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

@Test
public void testMain() throws Exception {
    String input = "4\nline one\nline two\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(out));

    try {
        MyClass.main(new String[]{});
    } finally {
        System.setIn(System.in);     // always restore — don't leak between tests
        System.setOut(originalOut);
    }

    assertEquals("expected output", out.toString().trim());
}
```

`ByteArrayInputStream` — wraps a string as bytes so `Scanner` reads from it as if it were real stdin.
`ByteArrayOutputStream` — captures everything printed to stdout into a buffer you can assert on.

---

## `HashMap` — `getOrDefault` / `containsKey`

```java
map.containsKey(k)                   // boolean — check before get to avoid null
map.getOrDefault(k, 0)               // get value or default if key absent (cleaner)
map.put(k, map.getOrDefault(k,0)+1) // increment pattern
map.remove(k)                        // delete key
map.size()                           // number of keys present
```

---

## `Double.parseDouble()` / `Integer.parseInt()`

Parse strings from split input into numeric types:

```java
String[] parts = line.split(" ");
double cgpa = Double.parseDouble(parts[2]);
int id       = Integer.parseInt(parts[3]);
```

---

## `scan.nextLine()` vs `scan.nextInt()`

`nextInt()` reads a number but **leaves the newline in the buffer** — the next `nextLine()` will consume that leftover newline and return `""`. Avoid mixing them; prefer `nextLine()` + manual parse:

```java
int n = Integer.parseInt(scan.nextLine());  // safe — consumes the whole line including newline
```
