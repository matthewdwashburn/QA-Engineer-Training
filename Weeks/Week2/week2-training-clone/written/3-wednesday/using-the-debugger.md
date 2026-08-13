# Using the Java Debugger (IDE)

## Learning Objectives
- Set **breakpoints** and run a program in **debug** mode.
- **Step over**, **step into**, and **step out** of methods.
- Inspect **variables** and **watches** to test hypotheses about failures.

---

## Why This Matters

> **Weekly Epic Connection:** Wednesday’s exercises include **debugging buggy code**. The debugger turns guesses into evidence—essential for QA when reproducing intermittent issues.

---

## The Concept

### Breakpoints

A **breakpoint** pauses execution on a **line** (or on **exceptions** / **conditions** in advanced use). In **IntelliJ IDEA** or **Eclipse**:

- Click in the **gutter** next to the line number, or use **Toggle Breakpoint**.
- Start with **Debug** (bug icon), not **Run**.

### Stepping

| Action | Effect |
|--------|--------|
| **Step Over** (F8 / F6) | Run current line; stop at next line in same method |
| **Step Into** (F7 / F5) | Enter the callee on the current line |
| **Step Out** (Shift+F8 / F7) | Finish current method; pause in caller |
| **Resume** (F9 / F8) | Run until next breakpoint |

Use **Step Over** to move line-by-line; **Step Into** only when you need to see inside a helper.

### Variables and watches

The **Variables** panel shows **locals** and **`this`** in scope. **Watches** let you pin expressions (`user.getId()`, `list.size()`).

### Evaluate expression

Many IDEs let you run arbitrary code in the **current frame** (e.g. call a method, inspect a substring)—powerful for “what if” checks without recompiling.

### Conditional Breakpoints

Pause only when a specific condition is true — drastically reduces noise in loops and bulk processing:

```java
// In the Variables panel, right-click a breakpoint → "Edit Breakpoint" → add condition:
count > 100           // Pause when count exceeds 100
user == null          // Pause only on the null case
userId.equals("bad") // Pause for a specific user
items.size() == 0    // Pause when the list becomes empty
```

Conditional breakpoints are essential when:
- You're iterating 10,000 records and only care about record 5,432.
- You want to pause on a specific test case without stopping on every other one.

### Watches and Evaluate Expression

The **Watches** panel lets you pin expressions that are re-evaluated every time execution pauses:

```
// Examples of useful watch expressions:
user.getEmail()          // Call a getter — safer than accessing fields directly
items.size()             // Track collection size as you step
request.headers.get("Authorization")  // Inspect nested structure
result != null && result.isValid()    // Compound boolean
```

**Evaluate Expression** (Alt+F8 / shortcut varies by IDE) lets you execute arbitrary code in the current frame at the moment execution is paused:

```java
// Examples:
items.stream().filter(i -> i.getPrice() > 100).count()  // Query the live collection
String.format("%s has %d items", user.getName(), cart.size())  // Build a formatted string
new java.util.ArrayList<>(items).sort(Comparator.naturalOrder())  // Sort and inspect
```

> **Caution:** Evaluate Expression can have side effects. Calling `save()` or `delete()` in evaluation will actually execute those operations. Prefer read-only expressions (getters, size, contains) while debugging.

### Frame Drop (Re-run From Here)

Some IDEs (IntelliJ IDEA) support **Drop Frame** — this resets the execution to the beginning of the current method, re-running it with the same inputs. Useful when:
- You stepped too far and missed the failing line.
- You want to set a watch on a value that was already computed.

> **Note:** Frame drop doesn't undo side effects (database writes, HTTP calls). It only resets the stack frame.

### Exception Breakpoints — Pause at Any Throw

Instead of guessing which line throws, configure the debugger to **pause automatically when any exception is thrown**:

**IntelliJ IDEA:**
1. Open **Run → View Breakpoints** (Ctrl+Shift+F8)
2. Click **+** → **Java Exception Breakpoints**
3. Type the exception class (`NullPointerException`, `IllegalArgumentException`, or `java.lang.Exception` for everything)
4. Choose: **Caught exceptions**, **Uncaught exceptions**, or both

This is extremely useful when:
- You don't know which line throws.
- The exception is caught somewhere deep in a framework and rethrown as a different type.
- You're investigating intermittent failures — run in debug mode and it pauses automatically when the condition occurs.

### Remote Debugging — Attaching to a Running JVM

When a bug only reproduces in a remote environment (a Docker container, a CI server, a staging server), you can attach the IntelliJ/Eclipse debugger **remotely** without restarting the JVM:

**Step 1:** Start the JVM with debug flags:
```bash
# Enable remote debugging on port 5005, suspend=n means don't wait for debugger
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar myapp.jar

# For Docker, expose the port:
docker run -p 5005:5005 -e JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005" myapp
```

**Step 2:** In IntelliJ IDEA:
1. **Run → Edit Configurations → + → Remote JVM Debug**
2. Set host (e.g. `localhost` or server IP) and port (`5005`)
3. Click **Debug** — the IDE connects and your breakpoints work remotely

> **Security note:** Remote debugging ports are unauthenticated. Never expose them on public-facing servers. Restrict to `localhost` or VPN-protected networks.

### Hot Swap — Change Code Without Restarting

While paused at a breakpoint, you can sometimes modify the Java code and apply changes **without restarting**:

- **Hot Swap (standard JVM):** Can redefine method bodies only. Changing method signatures, adding fields, or renaming classes requires a full restart.
- **JetBrains HotSwap / DCEVM:** Extended hot swap — can add methods and fields. Configured via run configuration.

In IntelliJ IDEA: after editing code while paused, press **Build → Reload Changed Classes**.

---

## Workflow (bug hunt)

1. Reproduce the failure; read **stack trace** line.
2. Set breakpoint **before** the bad line (or on the throwing line).
3. **Debug**; when paused, confirm **values** that should hold invariants.
4. Step until you see the **first wrong state**—fix there, not downstream symptoms.

---

## A concrete demo you can run (and debug)

Use the following intentionally buggy code to practice breakpoints and stepping:

```java
import java.util.List;

public class DebugDemo {
    public static void main(String[] args) {
        List<String> names = List.of("Ada", "Grace", "Linus");
        System.out.println(totalChars(names));
    }

    static int totalChars(List<String> names) {
        int total = 0;
        for (String n : names) {
            total += n.length();
        }
        return total;
    }
}
```

Now introduce a bug: change one element to `null`:

```java
List<String> names = List.of("Ada", null, "Linus");
```

### Debug steps to try

- Put a breakpoint on `total += n.length();`
- Start in Debug mode
- When it pauses:
  - Inspect `n` in Variables
  - Add a Watch for `n == null`
- Use “Resume” to hit the breakpoint again and observe the change of `n`
- Step Over on the failing line and watch the exception trigger

### Exception breakpoints (high value for QA)

Most IDEs can pause **exactly when an exception is thrown**, even if you don’t know the line yet.

Suggested configuration for practice:

- Break on `NullPointerException`
- (Optionally) Break on “Any exception” but only when it’s **uncaught** to reduce noise

This is extremely useful when investigating intermittent failures where you can’t predict which line will throw.

---

## Debugger tips that save time

- **Don’t Step Into library code** unless you must: if you step into `ArrayList` or JUnit internals by accident, use Step Out.
- **Use conditional breakpoints** in loops: pause only when `i == 999` or `userId.equals("bad")`.
- **Restart frame** (if available): re-run the current method with the same inputs after changing code (IDE support varies).
- **Evaluate expression** carefully: calling methods can have side effects; prefer pure getters during debugging.

---

## Summary

- **Breakpoints** stop execution; **step** commands walk the code.
- **Variables / watches** validate assumptions.
- Combine with **stack traces** from the prior lesson for efficient debugging.

---

## Practice (10–15 minutes)

1. Debug the `DebugDemo` above and capture:
   - the value of `n` at each iteration
   - the exact line where the exception is thrown
2. Add a conditional breakpoint: pause only when `n == null`.
3. Add an exception breakpoint for `NullPointerException` and verify you stop at the throw site even without a line breakpoint.

---

## Additional Resources

- [IntelliJ: Debugging](https://www.jetbrains.com/help/idea/debugging-code.html)
- [Eclipse JDT: Java debugging](https://www.eclipse.org/jdt/debug/)
- [Oracle: jdb (command-line debugger)](https://docs.oracle.com/en/java/javase/21/docs/specs/man/jdb.html) — optional for servers without GUI
