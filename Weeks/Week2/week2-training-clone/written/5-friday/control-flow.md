# Control Flow in Java

## Learning Objectives
- Use **`if` / `else if` / `else`** for branching with correct operator usage.
- Write **classic `switch` statements** and avoid the **fall-through** bug.
- Apply the **ternary operator** `? :` for compact conditional expressions.
- Write all three Java loop forms: **`for`**, **`while`**, and **`do-while`**.
- Use the **enhanced `for-each`** loop to iterate collections and arrays.
- Apply **`break`**, **`continue`**, and **labeled** versions for precise loop control.
- Understand **blocks** `{ }` and **variable scope** inside control structures.
- Compare Java control flow syntax with its Python equivalents.

---

## Why This Matters

> **Weekly Epic Connection:** Menu-driven test tools, data validators, log parsers, and retry mechanisms are all built from **control flow**. Clear branching and loops with correct `break`/`continue` usage reduces wrong-path bugs and makes test automation predictable.

---

## The Concept

### Comparison and Logical Operators

Control flow depends on **boolean expressions**. Know these before writing conditions:

| Operator | Meaning | Example |
|----------|---------|---------|
| `==` | Equal to | `score == 100` |
| `!=` | Not equal to | `status != null` |
| `<` | Less than | `retries < 3` |
| `>` | Greater than | `age > 18` |
| `<=` | Less than or equal | `count <= maxCount` |
| `>=` | Greater than or equal | `score >= 70` |
| `&&` | Logical AND (short-circuit) | `a > 0 && b > 0` |
| `\|\|` | Logical OR (short-circuit) | `isAdmin \|\| isOwner` |
| `!` | Logical NOT | `!list.isEmpty()` |

> **Important for strings and objects:** Use `.equals()` to compare **content**, not `==`. `==` on objects compares **references** (memory addresses).

```java
String status = getStatus();
if ("PASS".equals(status)) { }   // ✅ correct — value comparison
if (status == "PASS") { }        // ❌ wrong — reference comparison, unreliable
```

---

### `if` / `else if` / `else`

The fundamental branching structure — executes different code based on boolean conditions:

```java
int score = 85;

if (score >= 90) {
    System.out.println("A — Excellent");
} else if (score >= 80) {
    System.out.println("B — Good");
} else if (score >= 70) {
    System.out.println("C — Pass");
} else {
    System.out.println("F — Fail");
}
```

Rules:
- Conditions are evaluated **top-to-bottom** — the first `true` branch runs; the rest are skipped.
- The `else` block is optional — it runs only if no `if` or `else if` condition was true.
- Each condition must produce a `boolean` — Java does not treat `0` or `null` as false (unlike Python/C).

```java
// ❌ Not valid Java — only boolean expressions allowed
if (count) { }        // compile error: int is not boolean
if (name) { }         // compile error: String is not boolean

// ✅ Correct
if (count > 0) { }
if (name != null && !name.isBlank()) { }
```

---

### Blocks and Variable Scope

Variables declared **inside a block** `{ }` are only visible within that block:

```java
if (ready) {
    int result = compute();   // result declared inside the if-block
    System.out.println(result);
}
// result is NOT accessible here — it's out of scope

for (int i = 0; i < 10; i++) {
    // i is only accessible inside this loop
}
// i is NOT accessible here
```

This is called **block scope** — a key difference from Python, where loop variables remain accessible after the loop.

---

### Classic `switch` Statement

`switch` dispatches on a **discrete value** (an `int`, `char`, `String`, or `enum`). It is more readable than long `else if` chains when testing a single variable:

```java
String day = "MON";

switch (day) {
    case "MON":
    case "TUE":
    case "WED":
    case "THU":
        System.out.println("Weekday");
        break;              // ← CRITICAL: prevents fall-through
    case "FRI":
        System.out.println("Almost weekend");
        break;
    case "SAT":
    case "SUN":
        System.out.println("Weekend");
        break;
    default:
        System.out.println("Unknown day: " + day);
}
```

**The fall-through trap:** Without `break`, execution **falls through** to the next `case` regardless of the label:

```java
int code = 2;
switch (code) {
    case 1:
        System.out.println("one");    // Not printed
    case 2:
        System.out.println("two");    // Printed
    case 3:
        System.out.println("three");  // Also printed! — falls through because no break
    default:
        System.out.println("done");   // Also printed!
}
// Output: two, three, done
```

Intentional fall-through (rare) should be documented with a comment. Accidental fall-through is a common bug.

> **Types supported by classic `switch`:** `byte`, `short`, `int`, `char`, their wrapper classes, `String` (Java 7+), and `enum` types. **Not** `boolean`, `long`, `float`, `double`.

---

### Ternary Operator `? :`

A compact **expression** that returns one of two values based on a condition:

```java
// Syntax: condition ? valueIfTrue : valueIfFalse

String label = (n % 2 == 0) ? "even" : "odd";

int capped = (score > 100) ? 100 : score;

String status = (passed) ? "PASS" : "FAIL";
System.out.println("Result: " + status);
```

Use for **simple** one-line expressions. Avoid nesting ternaries — they become unreadable quickly:

```java
// ❌ Hard to read — nested ternary
String grade = score >= 90 ? "A" : score >= 80 ? "B" : score >= 70 ? "C" : "F";

// ✅ Use if-else instead for multiple conditions
```

---

### The `for` Loop

The classic counting loop — best when you know **how many times** to iterate:

```java
// Syntax: for (initializer; condition; update)
for (int i = 0; i < 10; i++) {
    System.out.println(i);   // 0, 1, 2, ..., 9
}

// Countdown
for (int i = 10; i > 0; i--) {
    System.out.println(i);   // 10, 9, ..., 1
}

// Step by 2
for (int i = 0; i <= 20; i += 2) {
    System.out.println(i);   // 0, 2, 4, ..., 20
}

// Iterating an array by index
String[] names = {"Alice", "Bob", "Charlie"};
for (int i = 0; i < names.length; i++) {
    System.out.println(i + ": " + names[i]);
}
```

All three parts (initializer, condition, update) are **optional**:

```java
// Infinite loop — use with break inside
for (;;) {
    if (doneCondition()) break;
}
```

---

### Enhanced `for-each` Loop

The cleanest way to iterate over **arrays** and **collections** when you don't need the index:

```java
// Iterating an array
String[] fruits = {"apple", "banana", "cherry"};
for (String fruit : fruits) {
    System.out.println(fruit);
}

// Iterating a List
List<Integer> scores = List.of(85, 92, 78, 95);
int total = 0;
for (int score : scores) {
    total += score;
}
System.out.println("Average: " + (double) total / scores.size());
```

**Limitation:** Cannot modify the collection while iterating (throws `ConcurrentModificationException`). Use an indexed `for` loop or an `Iterator` when you need to remove elements.

**Python equivalent:**
```python
for fruit in fruits:
    print(fruit)
```

---

### `while` Loop

Best when you don't know in advance how many iterations are needed — loop continues while a condition is true:

```java
// Retry loop — continue until success or max attempts
int retries = 0;
int maxRetries = 3;

while (retries < maxRetries) {
    boolean success = attemptConnection();
    if (success) {
        System.out.println("Connected!");
        break;
    }
    retries++;
    System.out.println("Retry " + retries + " failed. Waiting...");
}

if (retries == maxRetries) {
    System.out.println("All retries exhausted.");
}
```

```java
// Reading lines until end of file
Scanner sc = new Scanner(new File("data.txt"));
while (sc.hasNextLine()) {
    String line = sc.nextLine();
    processLine(line);
}
```

**Watch for infinite loops:** If the condition never becomes false and there's no `break`, the loop runs forever:
```java
int i = 0;
while (i < 10) {
    System.out.println(i);
    // ❌ Forgot i++ — infinite loop!
}
```

---

### `do-while` Loop

Like `while`, but the condition is checked **after** the first iteration — the body always executes **at least once**:

```java
// Input validation — must ask at least once
int number;
do {
    System.out.print("Enter a number between 1 and 10: ");
    number = scanner.nextInt();
} while (number < 1 || number > 10);

System.out.println("You entered: " + number);
```

```java
// Menu loop — show menu at least once
int choice;
do {
    displayMenu();
    choice = scanner.nextInt();
    handleChoice(choice);
} while (choice != 0);  // 0 = exit
```

Use `do-while` when you need to execute the body first and then decide whether to continue.

---

### `break` and `continue`

**`break`** exits the **innermost** loop or `switch` immediately:
```java
// Find first passing score
int[] scores = {45, 62, 83, 91, 55};
int firstPass = -1;

for (int score : scores) {
    if (score >= 70) {
        firstPass = score;
        break;   // Stop searching — found it
    }
}
System.out.println("First pass: " + firstPass);   // 83
```

**`continue`** skips the **rest of the current iteration** and jumps to the next:
```java
// Process only non-null entries
for (String item : items) {
    if (item == null) {
        continue;   // Skip this item, move to next
    }
    processItem(item);
}
```

**Labeled `break` and `continue`** — for nested loops, you can target an outer loop:
```java
outer:
for (int row = 0; row < matrix.length; row++) {
    for (int col = 0; col < matrix[row].length; col++) {
        if (matrix[row][col] == target) {
            System.out.println("Found at: " + row + ", " + col);
            break outer;   // Exits BOTH loops
        }
    }
}
```

---

### Java vs Python Control Flow — Quick Comparison

| Feature | Java | Python |
|---------|------|--------|
| `if` condition | Must be `boolean` | Any truthy/falsy value |
| Block delimiter | `{ }` | Indentation |
| `else if` | `else if` | `elif` |
| Switch/match | `switch` (Java), `switch` expr (Java 14+) | `match` (Python 3.10+) |
| Classic `for` | `for (init; condition; update)` | No direct equivalent |
| `for-each` | `for (Type item : collection)` | `for item in collection:` |
| `while` | `while (condition)` | `while condition:` |
| `do-while` | `do { } while (condition);` | Not built-in (use `while True: ... if: break`) |
| `break` | Exits innermost loop/switch | Exits innermost loop |
| `continue` | Skips current iteration | Skips current iteration |
| Labeled break | ✅ `break outerLabel;` | ❌ Not available |
| Loop variable scope | Scoped to loop block | Accessible after loop |

---

## Summary

- **`if`/`else if`/`else`** — top-to-bottom evaluation; only the first matching branch runs. Use `.equals()` for object comparisons, not `==`.
- **`switch`** — multi-way dispatch on discrete values; **`break`** is mandatory to prevent fall-through.
- **Ternary `? :`** — compact expression for simple two-way choices; don't nest.
- **`for`** — counting/index loop; **`for-each`** — cleaner iteration over arrays and collections.
- **`while`** — condition-first loop for indefinite iteration. **`do-while`** — body executes at least once.
- **`break`** exits a loop or switch; **`continue`** skips to the next iteration. Both support **labels** for nested loops.
- Variables declared inside `{ }` blocks are **scoped** to that block.

---

## Additional Resources

- [Control Flow Statements (Oracle Tutorial)](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/flow.html)
- [JLS: Statements](https://docs.oracle.com/javase/specs/jls/se21/html/jls-14.html)
- [JLS: `switch` statement](https://docs.oracle.com/javase/specs/jls/se21/html/jls-14.html#jls-14.11)
- [Comparison operators (JLS §15.20)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-15.html#jls-15.20)
