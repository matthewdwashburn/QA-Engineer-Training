# Arrays in Java

## Learning Objectives
- Declare, create, and initialise one-dimensional arrays using all valid syntaxes.
- Understand **default values**, **fixed length**, and **zero-based indexing**.
- Handle `ArrayIndexOutOfBoundsException` and diagnose off-by-one errors.
- Explain how arrays are **reference types** — aliasing, passing to methods, and mutation.
- Work with **multidimensional** arrays (rectangular and ragged).
- Use `java.util.Arrays` utilities effectively.
- Decide when to use an array vs an `ArrayList`.

---

## Why This Matters

> **Weekly Epic Connection:** Arrays are the built-in sequential data structure in Java — they underpin `ArrayList` internally and appear in every method signature that deals with raw data (`main(String[] args)`, byte buffers, pixel data). Knowing how arrays work — their fixed size, default values, reference behaviour, and index rules — prevents the most common beginner bugs and sets you up for the Collections framework later this week.

---

## The Concept

### Declaration and Creation Styles

Java provides three styles. **All three produce identical results** — choose the clearest for the context:

```java
// Style 1: Declare then allocate (common when size is calculated at runtime)
int[] scores;
scores = new int[5];      // 5 elements, each initialised to 0

// Style 2: Declare and allocate in one line (most common)
int[] scores = new int[5];

// Style 3: Declare, allocate, and initialise with values
int[] scores = new int[] { 88, 92, 76, 100, 55 };

// Style 4: Array initialiser shorthand (compiler infers type and length)
int[] scores = { 88, 92, 76, 100, 55 };   // Only valid at declaration — not for reassignment
```

**Where to put `[]`:** Java allows both `int[] name` and `int name[]` — strongly prefer `int[]` (type carries the array nature, not the variable).

```java
int[] a, b;    // ✅ a and b are both int arrays
int c[], d;    // ❌ Confusing — c is int[], d is just int
```

---

### Fixed Length and the `.length` Property

Arrays have a **fixed length** set at creation — you cannot resize them. Use `.length` (a field, not a method) to get the size:

```java
int[] arr = new int[5];
System.out.println(arr.length);    // 5  — field, no parentheses

arr.length = 10;   // ❌ Compile error — length is final, cannot be changed
```

To "resize" an array, you must create a new one and copy:
```java
int[] bigger = Arrays.copyOf(arr, arr.length * 2);  // New array, double the size
```

---

### Default Values

When you create an array with `new`, Java automatically initialises every element to the **type's default value**:

| Element Type | Default Value |
|-------------|--------------|
| `byte`, `short`, `int` | `0` |
| `long` | `0L` |
| `float` | `0.0f` |
| `double` | `0.0` |
| `char` | `'\u0000'` (null character) |
| `boolean` | `false` |
| Any reference type (`String`, objects) | `null` |

```java
int[] nums = new int[3];       // [0, 0, 0]
boolean[] flags = new boolean[3]; // [false, false, false]
String[] names = new String[3];   // [null, null, null]
```

---

### Zero-Based Indexing and Bounds

Array indices run from `0` to `length - 1`. Accessing outside this range throws `ArrayIndexOutOfBoundsException`:

```java
int[] arr = { 10, 20, 30, 40, 50 };
//           [0]  [1]  [2]  [3]  [4]

System.out.println(arr[0]);    // 10 — first element
System.out.println(arr[4]);    // 50 — last element
System.out.println(arr[5]);    // ❌ ArrayIndexOutOfBoundsException: Index 5 out of bounds for length 5
System.out.println(arr[-1]);   // ❌ ArrayIndexOutOfBoundsException
```

**Common off-by-one patterns:**
```java
// ❌ Off-by-one — arr[arr.length] doesn't exist
for (int i = 0; i <= arr.length; i++) {   // Should be < not <=
    System.out.println(arr[i]);            // Throws on last iteration
}

// ✅ Correct
for (int i = 0; i < arr.length; i++) {
    System.out.println(arr[i]);
}

// ✅ Or use enhanced for (no index arithmetic)
for (int val : arr) {
    System.out.println(val);
}
```

---

### Arrays Are Reference Types — Aliasing and Mutation

Arrays are **objects** in Java. A variable holds a **reference** to the array, not the array itself. This means:

**Aliasing — two variables, one array:**
```java
int[] a = { 1, 2, 3 };
int[] b = a;           // b gets a COPY of the reference — both point to the SAME array

b[0] = 99;
System.out.println(a[0]);   // 99 — a sees the change through b!
```

**Passing arrays to methods — the method can mutate the original:**
```java
public static void doubleAll(int[] arr) {
    for (int i = 0; i < arr.length; i++) {
        arr[i] *= 2;      // Mutates the caller's array directly
    }
}

int[] scores = { 10, 20, 30 };
doubleAll(scores);
System.out.println(Arrays.toString(scores));   // [20, 40, 60]
```

**If you want to avoid mutating the original**, pass a copy:
```java
doubleAll(Arrays.copyOf(scores, scores.length));
```

**Comparing arrays — `==` compares references, not content:**
```java
int[] x = { 1, 2, 3 };
int[] y = { 1, 2, 3 };

System.out.println(x == y);              // false — different objects
System.out.println(Arrays.equals(x, y)); // true  — same content
```

---

### Iterating Arrays

```java
String[] names = { "Alice", "Bob", "Charlie" };

// Index-based — use when you need the index
for (int i = 0; i < names.length; i++) {
    System.out.println(i + ": " + names[i]);
}

// Enhanced for — cleaner when you only need the value
for (String name : names) {
    System.out.println(name);
}

// Reverse iteration
for (int i = names.length - 1; i >= 0; i--) {
    System.out.println(names[i]);
}
```

---

### Multidimensional Arrays — Arrays of Arrays

Java has no built-in matrix type. A 2D array is an array whose elements are themselves arrays:

```java
// Rectangular — all rows have the same column count
int[][] grid = new int[3][4];   // 3 rows, 4 columns
grid[0][0] = 1;
grid[2][3] = 99;

// Initialiser syntax
int[][] matrix = {
    { 1, 2, 3 },
    { 4, 5, 6 },
    { 7, 8, 9 }
};

System.out.println(matrix[1][2]);   // 6  (row 1, column 2)

// Ragged — rows can have different lengths
int[][] triangle = new int[3][];
triangle[0] = new int[1];   // 1 element
triangle[1] = new int[2];   // 2 elements
triangle[2] = new int[3];   // 3 elements
```

**Iterating a 2D array:**
```java
for (int row = 0; row < matrix.length; row++) {
    for (int col = 0; col < matrix[row].length; col++) {
        System.out.printf("%3d", matrix[row][col]);
    }
    System.out.println();
}
```

---

### `java.util.Arrays` — Common Utilities

Import: `import java.util.Arrays;`

| Method | Purpose | Notes |
|--------|---------|-------|
| `Arrays.toString(arr)` | Human-readable `[1, 2, 3]` string | 1D arrays |
| `Arrays.deepToString(arr)` | Nested `[[1, 2], [3, 4]]` string | Multidimensional |
| `Arrays.sort(arr)` | Sort in-place (ascending) | Primitives: natural order; Objects: `Comparable` |
| `Arrays.sort(arr, from, to)` | Sort a range `[from, to)` | |
| `Arrays.binarySearch(arr, key)` | Fast search on a **sorted** array | Returns index or negative value if not found |
| `Arrays.fill(arr, value)` | Fill all slots with one value | |
| `Arrays.copyOf(arr, newLen)` | New array, copy then pad/truncate | Useful for "resizing" |
| `Arrays.copyOfRange(arr, from, to)` | Copy a sub-range | |
| `Arrays.equals(a, b)` | Element-wise equality | 1D arrays |
| `Arrays.deepEquals(a, b)` | Element-wise equality | Nested arrays |
| `Arrays.stream(arr)` | Convert to `IntStream`/`Stream` | Enables functional operations |

```java
import java.util.Arrays;

int[] scores = { 88, 45, 92, 76, 100 };

// Sort
Arrays.sort(scores);
System.out.println(Arrays.toString(scores));   // [45, 76, 88, 92, 100]

// Binary search (array MUST be sorted first)
int idx = Arrays.binarySearch(scores, 92);
System.out.println("Found 92 at index: " + idx);   // 3

// Copy and extend
int[] extended = Arrays.copyOf(scores, scores.length + 2);  // [45, 76, 88, 92, 100, 0, 0]

// Stream — sum all scores
int total = Arrays.stream(scores).sum();
double average = Arrays.stream(scores).average().orElse(0.0);
System.out.println("Total: " + total + ", Average: " + average);
```

---

### Arrays vs ArrayList — When to Use Which

| Aspect | Array | `ArrayList` |
|--------|-------|------------|
| Size | Fixed at creation | Dynamic — grows automatically |
| Performance | Slightly faster for indexed access | Minor overhead for resizing |
| Primitives | ✅ `int[]`, `double[]`, etc. | ❌ Must use wrappers (`Integer`) — autoboxing overhead |
| Generics | ❌ No direct generic arrays | ✅ `ArrayList<String>` |
| Rich API | `Arrays.*` utilities | Full `Collection` / `List` API |
| When to use | Fixed-size data, `main` args, byte buffers, performance-critical inner loops | Everything else — prefer `ArrayList` by default |

> **Rule of thumb:** Default to `ArrayList` for new code. Use arrays when the size is fixed and known, or when working with primitive performance (e.g. large numeric datasets).

---

## Summary

- Arrays are **fixed-length**, **zero-indexed** containers. Access outside `[0, length-1]` throws `ArrayIndexOutOfBoundsException`.
- Default values fill new arrays: `0` for numbers, `false` for boolean, `null` for references.
- Arrays are **reference types** — two variables can point to the same array; passing to a method allows mutation of the original.
- Use `==` only for reference comparison; use `Arrays.equals()` for content comparison.
- 2D arrays are arrays of arrays — rows can be ragged.
- `java.util.Arrays` provides sort, search, copy, fill, stream, and string representation utilities.
- Prefer `ArrayList` for most new code; use arrays for fixed-size, primitive-heavy, or performance-critical data.

---

## Additional Resources

- [Oracle Java Tutorial — Arrays](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/arrays.html)
- [Arrays (Java SE 21 API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Arrays.html)
- [Arrays vs ArrayList — Baeldung](https://www.baeldung.com/java-array-vs-arraylist)
