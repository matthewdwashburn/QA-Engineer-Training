# Garbage Collection and the Heap

## Learning Objectives
- Contrast **stack** memory with **heap** memory and explain what lives in each.
- Explain the **GC root reachability** model — how the GC decides what to collect.
- Describe **generational garbage collection** and why the Young/Old split matters.
- Name the major JVM GC algorithms (G1, ZGC, Shenandoah) and their design goals.
- Identify **memory leak patterns** in Java and explain why they occur despite automatic GC.
- Know why `System.gc()` should not be relied on and why `finalize()` is deprecated.
- Use basic monitoring tools (`jcmd`, `jstat`, heap dumps) to diagnose memory problems.

---

## Why This Matters

> **Weekly Epic Connection:** Java's automatic memory management is a double-edged sword. You rarely `free` memory manually — but you **can** still cause memory leaks by holding references to objects longer than needed. Understanding heap vs stack helps you reason about object lifetimes, diagnose `OutOfMemoryError`, write memory-efficient test harnesses, and have informed conversations with developers about performance.

---

## The Concept

### Stack vs Heap — Where Things Live

| Memory Area | What Lives There | Lifetime | Managed By |
|-------------|-----------------|---------|-----------|
| **Stack** | Local variables (primitives + references), method frames, return addresses | Method scope — frame destroyed on return | JVM automatically (push/pop) |
| **Heap** | All objects (`new Something()`), all arrays | Until object becomes unreachable | Garbage Collector |
| **Metaspace** (Java 8+) | Class bytecode, method tables, static fields | Until class is unloaded | JVM class loader |

```java
public static void processOrder(int orderId) {
    //           ↑ orderId is a LOCAL VARIABLE → lives on the STACK
    
    Order order = new Order(orderId);
    //    ↑ reference lives on the stack; the Order OBJECT lives on the HEAP
    
    order.validate();  // Calls on the heap object
    
} // ← stack frame is popped; 'orderId' and 'order' reference are destroyed
  //   The Order object on the heap becomes eligible for GC (if no other reference holds it)
```

---

### GC Root Reachability — What Gets Collected

The GC does not track "who created what." It identifies **GC roots** — known starting points that are always reachable — and traces all objects reachable from them. Anything **not reachable** from a GC root is eligible for collection:

**GC Roots include:**
- All active thread stacks (local variables in running methods)
- Static fields of loaded classes
- JNI (native code) references
- References in system class loaders

```
GC Roots
    │
    ├── Thread stack ──► Object A ──► Object B ──► Object C (REACHABLE — not collected)
    │
    ├── Static field ──► Object D (REACHABLE — not collected)
    │
    └── (no reference) ──► Object E (UNREACHABLE — collected!)
                    └──► Object F ──► Object G (UNREACHABLE — even though F→G exists)
```

Even if two objects reference each other (a reference cycle), if neither is reachable from a GC root, **both are collected**. This is why Java's GC is more powerful than simple reference counting (which Python's CPython uses).

---

### Generational Garbage Collection

Most GC algorithms in the JVM use a **generational** approach based on the **weak generational hypothesis**: *most objects die young*.

```
                    JVM Heap
    ┌────────────────────────────────────────────────────┐
    │  YOUNG GENERATION                                  │
    │  ┌──────────────┐  ┌───────────┐  ┌───────────┐  │
    │  │    Eden       │  │Survivor 0 │  │Survivor 1 │  │
    │  │ (new objects) │  │           │  │           │  │
    │  └──────────────┘  └───────────┘  └───────────┘  │
    │         ↓ survived N collections                  │
    │  OLD GENERATION (Tenured)                          │
    │  ┌────────────────────────────────────────┐        │
    │  │  Long-lived objects                    │        │
    │  └────────────────────────────────────────┘        │
    └────────────────────────────────────────────────────┘
```

**How it works:**
1. New objects are allocated in **Eden** (fast bump-pointer allocation).
2. When Eden fills up, a **Minor GC** occurs — short, fast. Live objects move to a Survivor space.
3. Objects that survive several Minor GCs are **promoted** to the **Old Generation**.
4. When the Old Generation fills up, a **Major GC** (or Full GC) occurs — slower, touches more memory.

**Why it matters:**
- Short-lived objects (local variables, temporary results) are cheap to collect in Minor GC.
- Keeping long-lived objects in Old Gen avoids repeatedly scanning them.
- If you create many objects that survive long enough to be promoted to Old Gen unnecessarily, you increase Major GC frequency and pauses.

---

### Modern GC Algorithms

| Algorithm | JDK | Design Goal | Pause Behaviour |
|-----------|-----|------------|----------------|
| **Serial GC** | All | Single-threaded, simple | Stop-the-world for all phases |
| **Parallel GC** | Default pre-JDK 9 | High throughput; multi-threaded | Stop-the-world, but shorter |
| **G1 GC** | Default JDK 9+ | Predictable pause times, region-based | Short, configurable pauses |
| **ZGC** | JDK 15+ (production) | Ultra-low latency | Sub-millisecond pauses at any heap size |
| **Shenandoah** | JDK 15+ | Low pause, concurrent | Concurrent compaction |

**For this course:** G1 is the default and what you'll encounter on most JDK 11/17/21 installations. ZGC is increasingly used in latency-sensitive microservices.

```bash
# Check which GC your JVM is using
java -XX:+PrintCommandLineFlags -version | grep GC

# Explicitly set G1
java -XX:+UseG1GC -jar myapp.jar

# Use ZGC (for latency-sensitive apps)
java -XX:+UseZGC -jar myapp.jar
```

---

### Memory Leaks in Java — Still Possible Despite GC

A **memory leak** in Java occurs when objects are **technically reachable** (the GC won't collect them) but are **logically no longer needed**. The GC can only collect *unreachable* objects.

**Common leak patterns:**

**1. Static collections that grow forever:**
```java
public class EventRegistry {
    // ❌ This List is static — it lives for the lifetime of the JVM
    // If you add listeners but never remove them, they accumulate forever
    private static final List<EventListener> listeners = new ArrayList<>();

    public static void register(EventListener listener) {
        listeners.add(listener);
    }
    // No deregister() → leak!
}
```

**2. Long-lived collections holding old entries:**
```java
// ❌ Cache that never expires entries
Map<String, byte[]> imageCache = new HashMap<>();

for (String url : urls) {
    imageCache.put(url, downloadImage(url));  // Never removed — heap fills up
}
```

**3. Inner class holding outer class reference:**
```java
public class Outer {
    byte[] largeData = new byte[10_000_000];  // 10 MB

    // ❌ Non-static inner class implicitly holds a reference to Outer
    // If an Inner instance escapes, it keeps largeData alive in memory
    class Inner implements Runnable {
        public void run() { /* ... */ }
    }
}
```

**4. ThreadLocal values not cleaned up:**
```java
ThreadLocal<byte[]> buffer = new ThreadLocal<>();
buffer.set(new byte[1_000_000]);
// ❌ If the thread is pooled (e.g., Tomcat thread pool), this value
//    remains associated with the thread forever unless explicitly removed
buffer.remove();   // ✅ Always clean up ThreadLocal in finally blocks
```

> **QA insight:** Memory leaks in long-running test suites cause intermittent `OutOfMemoryError` failures in CI. Heap dumps reveal the retained object graph and pinpoint the leak.

---

### `System.gc()` — Don't Rely on It

```java
System.gc();  // Suggests a GC run — the JVM may IGNORE this hint
```

`System.gc()` is a **hint**, not a command. The JVM is free to ignore it or run a partial collection. Modern GCs (G1, ZGC) have sophisticated heuristics far better than any manual hint.

**Do not use `System.gc()` for:**
- Correctness (GC runs when the JVM decides, not when you ask)
- Performance optimisation (call overhead may be worse than doing nothing)
- Testing memory behaviour (results are non-deterministic)

**Legitimate use:** Rarely, in microbenchmarks or diagnostics, to attempt to clear the heap before measuring. Even then, use `-XX:+ExplicitGCInvokesConcurrent` to make it concurrent rather than stop-the-world.

---

### `finalize()` — Deprecated, Do Not Use

`Object.finalize()` was a cleanup hook called before an object was collected. It is **deprecated for removal** (JEP 421) because:

- Timing is **unpredictable** — may run seconds, minutes, or never before collection
- Finalised objects can be **resurrected** (creating a reference to themselves in `finalize()`)
- Prolongs object lifetime, increasing memory pressure
- Cannot throw checked exceptions to callers

**Replacements:**
- **`try-with-resources`** + `AutoCloseable` — for deterministic cleanup of I/O, database connections, etc.
- **`java.lang.ref.Cleaner`** (Java 9+) — explicit, safe cleanup registration without `finalize()`

```java
// ✅ Use try-with-resources for deterministic cleanup
try (Connection conn = DriverManager.getConnection(url);
     PreparedStatement stmt = conn.prepareStatement(sql)) {
    // Use conn and stmt
} // conn.close() and stmt.close() called automatically, even on exception
```

---

### Diagnosing Memory Issues

**Check heap usage at runtime:**
```bash
# Print heap stats periodically (every 1s, 10 times)
jstat -gc <pid> 1000 10

# Print GC log output
java -Xlog:gc:gc.log -jar myapp.jar
```

**Capture a heap dump:**
```bash
# Dump the heap to a file for analysis
jcmd <pid> GC.heap_dump heap.hprof

# Or trigger on OutOfMemoryError automatically
java -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./heap.hprof -jar myapp.jar
```

**Analyse the heap dump:**
- **VisualVM** (free, bundled with JDK) — GUI for heap analysis
- **Eclipse Memory Analyzer (MAT)** — advanced leak detection with "leak suspects" report
- **IntelliJ IDEA Profiler** — integrated profiling

**Common heap dump findings:**
```
Leak suspects report:
  1. java.util.ArrayList (2,847 instances, 1.2 GB) ← holding objects too long
     Retained by: com.example.EventRegistry.listeners (static field)
```

---

## Summary

- **Stack** holds primitives and references in method frames; destroyed when method returns. **Heap** holds all objects; managed by GC.
- The GC collects objects **not reachable** from GC roots — it handles reference cycles automatically.
- **Generational GC**: Young Generation (Eden + Survivors) for short-lived objects; Old Generation for long-lived ones; Minor GC is fast and frequent; Major GC is slow and rare.
- **G1** is the default GC (Java 9+); **ZGC** targets sub-millisecond pauses for latency-sensitive apps.
- Java **can still have memory leaks** — static collections growing forever, caches with no eviction, ThreadLocal not cleaned up, non-static inner classes holding outer class references.
- **`System.gc()`** is a hint the JVM may ignore — do not rely on it.
- **`finalize()`** is deprecated — use `try-with-resources` and `Cleaner` instead.
- Use **`jstat`**, **`jcmd`**, heap dumps + VisualVM/MAT to diagnose memory issues.

---

## Additional Resources

- [Oracle Java GC Tuning Guide (Java 21)](https://docs.oracle.com/en/java/javase/21/gctuning/)
- [JEP 421: Deprecate Finalization for Removal](https://openjdk.org/jeps/421)
- [JEP 439: Generational ZGC](https://openjdk.org/jeps/439)
- [Eclipse Memory Analyzer (MAT)](https://eclipse.dev/mat/)
- [VisualVM](https://visualvm.github.io/)
