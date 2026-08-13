# Understanding Java Stack Traces

## Learning Objectives
- Read a **stack trace** from top to bottom and find the **root cause** frame.
- Recognize common exception types (`NullPointerException`, `IllegalArgumentException`, etc.).
- Distinguish **caused by** (chained) exceptions in nested errors.

---

## Why This Matters

> **Weekly Epic Connection:** Failed tests and production incidents arrive as **stack traces**. Reading them quickly is a core QA skill—especially before you pair with the debugger the same day.

---

## The Concept

### What is a stack trace?

When an exception is thrown and not caught, the JVM prints a **stack trace**: a list of **stack frames**, each line showing a method call waiting to complete. The **top** of the trace is usually where the exception **originated** (sometimes one frame below framework noise).

Example:

```
Exception in thread "main" java.lang.NullPointerException: Cannot invoke "String.length()" because "name" is null
    at com.example.Demo.run(Demo.java:12)
    at com.example.Demo.main(Demo.java:6)
```

Read **message first**, then **first application frame** (`com.example...`), not JDK internals above it if those are just reflection.

### Caused by (chained exceptions)

```
java.io.IOException: ...
Caused by: java.net.ConnectException: Connection refused
```

The **root** is often at the **bottom** `Caused by` chain—start there for “why,” use upper layers for “where it was wrapped.”

---

## A systematic reading recipe (QA-friendly)

When you get a wall of red text, follow the same order every time:

1. **First line**: exception type + message (what happened)
2. **First application frame**: your package/class (where it happened)
3. **Find the “Caused by” chain**: scan to the deepest cause (why it happened)
4. **Check line numbers**: open the file:line and inspect values
5. **Confirm inputs**: what arguments/data triggered it (test data, request payload, file)

### Example with “Caused by”

```
Exception in thread "main" java.lang.RuntimeException: Failed to load config
    at com.example.app.App.start(App.java:22)
    at com.example.app.App.main(App.java:10)
Caused by: java.nio.file.NoSuchFileException: config/app.properties
    at java.base/sun.nio.fs.WindowsException.translateToIOException(WindowsException.java:85)
    at java.base/java.nio.file.Files.newByteChannel(Files.java:380)
    at java.base/java.nio.file.Files.readAllBytes(Files.java:3288)
    at com.example.app.ConfigLoader.load(ConfigLoader.java:14)
    ... 2 more
```

How to read it:

- **What**: `RuntimeException: Failed to load config`
- **Where** (first app frame): `App.start(App.java:22)`
- **Why** (deepest cause): `NoSuchFileException: config/app.properties`
- **Action**: check working directory / path, confirm file exists in the expected location

### “... N more” lines

When you see:

```
... 2 more
```

It means frames were **repeated** from earlier in the trace and omitted to reduce noise. You usually do not need them unless you’re reconstructing the full call path.

### Common Exceptions (Recognition Table)

| Exception | Typical Meaning | Common Cause |
|-----------|-----------------|--------------|
| `NullPointerException` | Dereferenced `null` (method/field on null reference) | Uninitialized variable, missing null check |
| `IllegalArgumentException` | Bad parameter value passed to a method | Input validation failure |
| `IllegalStateException` | Object not ready or in wrong state for this operation | Calling in wrong order (e.g. reading a closed stream) |
| `ArrayIndexOutOfBoundsException` | Array index is negative or ≥ array length | Off-by-one in loops |
| `IndexOutOfBoundsException` | List/String index out of range | Same as above but for `List`, `String` |
| `ClassCastException` | Invalid downcast between incompatible types | Incorrect use of generics raw type |
| `NumberFormatException` | `Integer.parseInt()` on non-numeric string | User input not validated before parsing |
| `UnsupportedOperationException` | Operation not supported on this type | Calling `add()` on an unmodifiable list |
| `ConcurrentModificationException` | Collection modified while iterating | Modifying a list inside a `for-each` loop |
| `StackOverflowError` | Call stack exhausted by infinite recursion | Recursive method with no base case |
| `OutOfMemoryError` | JVM heap exhausted | Loading too much data; memory leak |
| `NoClassDefFoundError` | Class compiled against is missing at runtime | Classpath/dependency not on runtime classpath |
| `ClassNotFoundException` | `Class.forName(name)` can't find the class | Reflective load of a class not on classpath |

### JUnit Test Failure Stack Traces

Test failures appear in a slightly different format. Knowing how to read them is critical for QA:

**Assertion failure (expected ≠ actual):**
```
org.opentest4j.AssertionFailedError: expected: <200> but was: <404>
    at org.junit.jupiter.api.AssertionFailer.fail(AssertionFailer.java:110)
    at org.junit.jupiter.api.Assertions.failNotEqual(Assertions.java:843)
    at org.junit.jupiter.api.Assertions.assertEquals(Assertions.java:420)
    at com.example.UserApiTest.getUser_returns200(UserApiTest.java:45)   ← your test, line 45
```

How to read it:
- **First line**: the assertion type + what was expected vs what was actually received.
- **Your test frame**: the last frame with your package (`com.example`) — this is the assertion that failed.
- Skip all the JUnit/opentest4j frames above — they're framework internals.

**Unexpected exception in test:**
```
java.lang.NullPointerException: Cannot invoke "String.length()" because "name" is null
    at com.example.UserService.validate(UserService.java:28)  ← root cause in production code
    at com.example.UserServiceTest.validate_throwsOnNull(UserServiceTest.java:52)  ← test frame
```

This means your test triggered a production code bug — the exception came from `UserService`, not from an assertion.

---

## Framework noise vs your code

In real stacks (Spring, JUnit), the top frames might be framework internals. Your goal is to find the **first frame in your project**:

- Package names like `org.junit...`, `org.springframework...`, `java.base/...` are usually infrastructure
- Package names like `com.yourcompany...` or `qa.training...` are usually where you should start

If the exception is thrown by the JDK (e.g., `Files.readString`) you still want the frame in **your code** that called it (that’s the decision point you can change).

### Tips

1. Find **your package** in the trace (`com.yourcompany...`).
2. Open the **file:line** cited.
3. Re-run with logging or **debugger** to inspect variables (next topic).

### Multi-Threaded Stack Traces

When an exception occurs in a background thread (e.g. a thread pool worker, async task, or Kafka consumer), the stack trace may only show the thread's context — not the code that submitted the task:

```
Exception in thread "pool-1-thread-3" java.lang.RuntimeException: Task failed
    at com.example.Worker.process(Worker.java:44)
    at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1144)
    at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:642)
    at java.base/java.lang.Thread.run(Thread.java:1589)
```

Key points:
- The thread name (`pool-1-thread-3`) identifies which thread failed.
- The **first application frame** (`Worker.java:44`) is still where you look.
- The submitting code (whatever called `executor.submit(...)`) is **not** in this trace. Add logging there if you need to trace back to the origin.
- **`jstack <pid>`** prints stack traces for **all** threads simultaneously — use it to diagnose deadlocks or hung threads in production.

### Suppressed Exceptions

Java's `try-with-resources` can generate **suppressed exceptions** — exceptions thrown during resource cleanup that are attached to the primary exception:

```
java.io.IOException: Failed to read file
    at com.example.FileProcessor.read(FileProcessor.java:25)
    Suppressed: java.io.IOException: Error closing stream
        at com.example.FileProcessor.close(FileProcessor.java:35)
```

- The **main exception** was the read failure.
- The **suppressed exception** occurred during the implicit `close()` call in the `try-with-resources` block.
- Both are preserved — inspect both when diagnosing resource cleanup issues.

### Special Cases: OOM and SOF

**`StackOverflowError`** — the call stack grew too deep (usually infinite recursion):
```
java.lang.StackOverflowError
    at com.example.Tree.traverse(Tree.java:18)
    at com.example.Tree.traverse(Tree.java:18)
    at com.example.Tree.traverse(Tree.java:18)
    ... (repeating hundreds of times)
```

The repeated frame is your clue — find the recursive call and add a base case.

**`OutOfMemoryError: Java heap space`** — the JVM ran out of heap:
```
java.lang.OutOfMemoryError: Java heap space
    at java.base/java.util.Arrays.copyOf(Arrays.java:3513)
    at java.base/java.util.ArrayList.grow(ArrayList.java:274)
    at com.example.DataLoader.loadAll(DataLoader.java:89)
```

Actions:
1. Check if the code loads an unbounded amount of data at once (`loadAll()` is suspicious).
2. Increase heap temporarily: `java -Xmx2g MyApp` to confirm it's a size issue.
3. Capture a heap dump with `jcmd <pid> GC.heap_dump heap.hprof` and analyze in VisualVM or Eclipse MAT.

---

## Summary

- Stack traces list **call path** at failure; message + first app frame are key.
- **Caused by** chains hide the **root** at the bottom.
- Pattern-match **common exceptions** to speed triage.

---

## Practice (10 minutes)

Take any stack trace (from a failing exercise or a small demo) and answer:

- What is the **exception type**?
- What is the **message**?
- What is the **first application frame** (class + file + line)?
- Is there a **Caused by**? If yes, what is the deepest cause?
- What input would you change to avoid the crash (data, path, null check)?

---

## Additional Resources

- [Oracle: How to analyze stack traces](https://docs.oracle.com/javase/tutorial/essential/exceptions/stacktrace.html)
- [Java Exception class](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Exception.html)
- [NullPointerException message improvements (Java 14+)](https://openjdk.org/jeps/358)
