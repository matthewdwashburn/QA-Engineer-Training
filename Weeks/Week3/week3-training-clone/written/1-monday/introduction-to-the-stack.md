# Introduction to the Call Stack

## Learning Objectives
- Define the **call stack** and describe what a **stack frame** contains.
- Trace how frames are **pushed** and **popped** as methods call and return.
- Read a **stack trace** in an IDE and in Maven output and identify the root cause frame.
- Explain why each thread has its **own** stack and what this means for concurrent programs.
- Diagnose `StackOverflowError` and explain what causes it.
- Compare **recursion** vs **iteration** from a stack-depth perspective.

---

## Why This Matters

> **Weekly Epic Connection:** Every method call you write creates stack activity. Understanding the call stack clarifies local variable scope, how exceptions propagate (Wednesday), why recursion has depth limits (Tuesday), and — critically for QA — how to read the stack traces in failing tests and production errors. The stack trace is the most important debugging tool you have.

---

## The Concept

### What Is the Call Stack?

The **call stack** (formally the **JVM Stack**) is a region of memory dedicated to **one thread's execution context**. It tracks the chain of method calls currently in progress.

Every time a method is invoked, the JVM **pushes** a new **stack frame** onto the top. When the method returns (normally or via exception), its frame is **popped** and control returns to the caller's frame.

The stack operates **LIFO** — Last In, First Out. The method currently executing is always at the top.

---

### What Is in a Stack Frame?

Each stack frame holds all the information needed for one method invocation:

| Slot | Contents |
|------|---------|
| **Local variable array** | All method parameters + locally declared variables (primitives stored directly; object references stored as addresses) |
| **Operand stack** | Workspace for expression evaluation (JVM pushes/pops values here during computation) |
| **Frame data** | Link back to the calling frame's position (return address), reference to the runtime constant pool |
| **`this` reference** | For instance methods — the implicit first local variable pointing to the receiver object |

```java
public static int add(int a, int b) {
    int result = a + b;   // 'a', 'b', 'result' all live in this frame's local variable array
    return result;        // Returns result; frame is popped; caller's frame resumes
}
```

---

### Push and Pop — Tracing a Call Chain

```java
public class StackDemo {
    public static void main(String[] args) {
        int x = computeSquare(5);   // Calls computeSquare
        System.out.println(x);
    }

    static int computeSquare(int n) {
        return multiply(n, n);       // Calls multiply
    }

    static int multiply(int a, int b) {
        return a * b;                // Returns 25
    }
}
```

Stack state at the deepest point (`multiply` running):

```
TOP   ┌─────────────────────────────────────┐
      │ Frame: multiply(5, 5)               │  ← currently executing
      │   a = 5, b = 5                      │
      ├─────────────────────────────────────┤
      │ Frame: computeSquare(5)             │  ← waiting for multiply to return
      │   n = 5                             │
      ├─────────────────────────────────────┤
      │ Frame: main([""])                   │  ← waiting for computeSquare to return
      │   args = ref, x = (not yet set)     │
BOTTOM└─────────────────────────────────────┘
```

When `multiply` returns `25`:
1. `multiply`'s frame is popped
2. `computeSquare` resumes with the return value `25`
3. `computeSquare` returns `25`, its frame is popped
4. `main` resumes, assigns `x = 25`

---

### Each Thread Has Its Own Stack

Every thread in a Java program has its **own independent call stack**. The stacks do not share frames:

```
Thread "main"          Thread "worker-1"       Thread "worker-2"
┌──────────────┐       ┌──────────────┐        ┌──────────────┐
│ processOrder │       │ sendEmail    │        │ generateReport│
│ validateItem │       │ formatBody   │        │ fetchData     │
│ main         │       │ workerMain   │        │ workerMain    │
└──────────────┘       └──────────────┘        └──────────────┘
```

This means local variables in different threads are completely isolated — they don't share the stack. Only **heap objects** (and static fields) are shared between threads, which is why thread safety concerns focus on object mutation, not local variables.

---

### Reading Stack Traces

When an exception is thrown, the JVM captures the call stack and formats it as a **stack trace**. Reading it correctly is the most critical debugging skill for a Java developer or QA engineer.

**Example exception:**
```
java.lang.NullPointerException: Cannot invoke "String.length()" because "name" is null
	at com.example.service.UserService.validate(UserService.java:28)   ← root cause
	at com.example.service.UserService.create(UserService.java:15)
	at com.example.controller.UserController.handlePost(UserController.java:42)
	at org.springframework.web.servlet.DispatcherServlet.doDispatch(...)
	at org.springframework.web.servlet.DispatcherServlet.doService(...)
```

**How to read it:**
1. **First line** — the exception type and message. Read this carefully — it often tells you exactly what happened.
2. **Second line (first `at`)** — the **innermost** frame: the exact file and line where the exception was thrown. **Start here.**
3. **Subsequent lines** — the call chain going outward (caller → caller → caller). Read down to find **your code**.
4. **Framework frames** (Spring, JUnit, Maven) at the bottom — usually not the cause; your code is closer to the top.

**In a JUnit test:**
```
org.opentest4j.AssertionFailedError: expected: <200> but was: <404>
	at org.junit.jupiter.api.Assertions.failNotEqual(...)   ← JUnit internals — skip
	at org.junit.jupiter.api.Assertions.assertEquals(...)   ← JUnit internals — skip
	at com.example.api.UserApiTest.getUser_returns200(UserApiTest.java:45)  ← YOUR TEST
```

**Rule:** Find the first `at com.yourpackage...` — that is where to look.

---

### `StackOverflowError`

Each thread's stack has a **fixed maximum size** (configurable with `-Xss`, default typically 512KB–1MB). When the call depth exceeds this limit, the JVM throws `java.lang.StackOverflowError`:

```java
// Infinite recursion — no base case
public static int countDown(int n) {
    return countDown(n - 1);   // Always calls itself, never returns
}
```

```
Exception in thread "main" java.lang.StackOverflowError
    at com.example.Demo.countDown(Demo.java:3)
    at com.example.Demo.countDown(Demo.java:3)
    at com.example.Demo.countDown(Demo.java:3)
    ... (hundreds of repeated frames)
```

**The diagnosis signature:** The same method name repeated many times in the stack trace — a clear signal of infinite or excessively deep recursion.

**Common real causes:**
- Missing **base case** in a recursive algorithm
- Two methods calling each other (mutual recursion) with no exit condition
- Accidentally calling the same method from itself (e.g., `toString()` calling `toString()`)
- Parsing deeply nested data structures (very deep JSON/XML)

**Fix:** Add a base case, switch to an iterative algorithm, or increase stack size (`java -Xss4m`) if depth is genuinely large.

---

### Recursion vs Iteration — Stack Implications

Every recursive call pushes a new frame onto the stack. Iterative approaches reuse the same frame:

```java
// Recursive — each call pushes a new frame
// For n=10000, this pushes 10000 frames → StackOverflowError for large n
public static long factorialRecursive(int n) {
    if (n <= 1) return 1;           // Base case — stops recursion
    return n * factorialRecursive(n - 1);  // New frame per call
}

// Iterative — single frame, reuses local variable
// Handles arbitrarily large n without stack growth
public static long factorialIterative(int n) {
    long result = 1;
    for (int i = 2; i <= n; i++) {
        result *= i;   // Same frame reused on every iteration
    }
    return result;
}
```

**Trade-offs:**
| Aspect | Recursion | Iteration |
|--------|-----------|-----------|
| Code clarity | Often cleaner for tree/graph traversal | Cleaner for simple counting/accumulation |
| Stack usage | O(depth) — grows with call depth | O(1) — constant, no new frames |
| Stack overflow risk | Yes, for deep inputs | No |
| Tail-call optimisation | Not performed by JVM (unlike some languages) | N/A |

**Practical rule:** For inputs with bounded, small depth (tree traversal of depth ≤ 1000), recursion is fine and often cleaner. For unbounded or large inputs (processing millions of elements), prefer iteration or consider an explicit stack (`Deque`) to simulate recursion.

---

### Thread Stack Size Configuration

```bash
# Increase stack size per thread (default is JVM-dependent, typically 512k-1m)
java -Xss2m MyApp         # 2 MB stack per thread
java -Xss8m -jar app.jar  # 8 MB stack per thread (for deep recursion)
```

Increasing `-Xss` allows deeper recursion but uses more memory per thread. If you have 200 threads each with 8 MB stacks, that's 1.6 GB for stacks alone.

---

## Summary

- The **call stack** is a per-thread memory region; each method call **pushes** a frame, each return **pops** it.
- A **stack frame** contains: local variables (including parameters and `this`), operand stack, and return linkage.
- Each thread has its **own** independent stack — local variables are thread-safe by default; heap objects are not.
- **Stack traces** read from top (innermost, closest to exception) to bottom (outermost caller). Find your package in the `at` frames — that's where to look first.
- `StackOverflowError` = call stack exhausted, almost always from **missing base case** in recursion — look for repeated method names in the trace.
- **Recursion** uses O(depth) stack frames; **iteration** uses O(1). Prefer iteration for unbounded input sizes.
- Increase thread stack size with `-Xss` if deep recursion is genuinely needed (and unavoidable).

---

## Additional Resources

- [JVM Specification: Stacks](https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-2.html#jvms-2.5.2)
- [Oracle Java Tutorial — What Is an Error?](https://docs.oracle.com/javase/tutorial/essential/exceptions/errors.html)
- [Understanding Java Stack Traces (Baeldung)](https://www.baeldung.com/java-stack-trace)
