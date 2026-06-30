# Day 8 - Java OOP, Packages, Debugging & Algorithms

---

## SOLID Principles

A quick mental checklist for class/module design:

1. **Single Responsibility** — each class/module should do one thing only
2. **Open/Closed** — open for extension, closed for modification (add new behavior without editing existing code)
3. **Liskov Substitution** — a subclass should be usable anywhere its base class is, without breaking things
4. **Interface Segregation** — prefer several small, specific interfaces over one large general-purpose one
5. **Loose Coupling** (Dependency Inversion in spirit) — components should depend on each other as little as possible, so changes don't ripple across the codebase

---

## Modifiers

### Access modifiers — who can see it
| Modifier | Visibility |
|---|---|
| `public` | accessible from anywhere |
| `private` | accessible only within the same class |
| `protected` | accessible within package + subclasses |
| *(none/default)* | accessible only within the same package |

### Non-access modifiers — how it behaves
```java
static  // binds to the class itself, not an instance — Calculator.add(), shared across all objects
final   // locks it permanently:
        //   final variable → constant, can't be reassigned
        //   final method   → can't be overridden by subclasses
        //   final class    → can't be inherited from
```

---

## Packages

A **package** groups related classes — like a Python package/folder. Declared at the top of the file, and the folder structure must match the package name.

```java
package com.revature.constructors;   // file lives in src/com/revature/constructors/
```

To use a class from another package, it needs to be `public` and (in real projects) imported — within the same project structure here, the build tool resolves it via folder layout.

---

## Constructors & Encapsulation

A **constructor** has the same name as the class and no return type — runs when you create an object with `new`.

```java
public class Person {

    private String name;   // private fields — encapsulation
    private int age;

    public Person(String name, int age) {   // constructor
        this.name = name;   // 'this' refers to the current instance's field,
        this.age = age;     // distinguishing it from the parameter of the same name
    }

    // getters/setters — controlled access to private fields
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
}
```

```java
Person person = new Person("Jake", 15);   // 'new' allocates and calls the constructor
person.getName();                         // "Jake"
```

---

## Methods

### Varargs — variable number of arguments
`type ...name` lets a method accept any number of arguments, accessed as an array:

```java
public static int sum(int ...numbers) {
    int total = 0;
    for (int num : numbers) {   // enhanced for-loop, see below
        total += num;
    }
    return total;
}

sum(2, 3);        // 5
sum(1, 2, 3, 4);  // 10
```

### Enhanced for-loop ("for-each")
```java
for (int num : numbers) {   // "for each num in numbers"
    ...
}
```
Equivalent to Python's `for num in numbers:` — no index management needed.

### Calling static methods
```java
sum(2, 3);                  // works from inside the same class
DemoMethods.sum(2, 3);      // fully-qualified — works from anywhere
```

---

## Arrays

```java
String[] cases  = { "alpha", "beta", "gamma" };
int[] scores    = { 10, 20, 25 };
int[] arr       = new int[n];     // create array of size n, all zeros

arr.length        // property, not a method — no parentheses
arr[i] = i * 2;   // assign by index
```

---

## Common Bugs & Gotchas (from debugging exercise)

| Bug type | Example | Fix |
|---|---|---|
| **NullPointerException (NPE)** | calling `.trim()` on a `null` String | check `if (x == null)` before using it |
| **Inverted comparison** | `roleLevel < required` when it should grant access at `>=` | re-check operator direction against the requirement |
| **Integer division** | `int sum / int length` truncates the decimal | make one operand a `float`/`double` before dividing |
| **Missing return** | loop finds the value but falls through to `return -1` | `return` immediately when the match is found |
| **Off-by-one** | `for (i = 0; i <= arr.length; i++)` reads one past the end | use `<`, not `<=`, when comparing to `.length` |

**String comparison** — never use `==` for String content (compares object identity in Java, not Day 1-3 Python's interning behavior). Use `.equals()`:
```java
"guest".equals(label)     // correct content comparison
label == "guest"          // unreliable — may compare references
```

`Math.abs(x)` — absolute value, useful for comparing floating-point results with a tolerance:
```java
Math.abs(average(scores) - expected) < 0.001
```

---

## Algorithms — Linear vs Binary Search

**Linear search** — check every element one by one. O(n): time grows proportionally with input size.
```java
for (int i = 0; i < sorted.length; i++) {
    if (sorted[i] == target) return i;
}
return -1;
```

**Binary search** — repeatedly halve the search range. Requires a **sorted** array. O(log n): doubling the input only adds one more step.
```java
int low = 0, high = sorted.length - 1;
while (low <= high) {
    int mid = low + (high - low) / 2;   // avoids overflow vs (low+high)/2
    if (sorted[mid] == target) return mid;
    if (sorted[mid] < target) low = mid + 1;   // target is in upper half
    else high = mid - 1;                       // target is in lower half
}
return -1;
```

**Measured difference** (1 billion elements): linear ~23ms, binary ~0.007ms — binary search is ~3000x faster at this scale, and the gap widens as `n` grows.

---

## Timing Code

```java
long start = System.nanoTime();
// ... code to measure ...
long end = System.nanoTime();
double durationMs = (end - start) / 1_000_000.0;   // ns → ms
```

`1_000_000` — underscores in numeric literals for readability, same as Python.

---

## `java.util.Random`

```java
import java.util.Random;

Random rand = new Random();
int randomTarget = rand.nextInt(n);   // random int in [0, n)
```
