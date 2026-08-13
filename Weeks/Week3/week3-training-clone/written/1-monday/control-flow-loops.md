# Control Flow: Loops, `break`, `continue`, and Labels

## Learning Objectives

- Choose among `for`, enhanced `for`, `while`, and `do-while` for a given problem.
- Use `break` and `continue` correctly, including with labeled loops.

## Why This Matters

Loops process collections, parse input, retry operations, and walk data structures—skills you will combine with arrays today and with collections later in the week. Clear loop choice (`for` vs `while`) makes code easier to read and review in interviews and production.

## The Concept

### `for` loop

```java
for (init; condition; update) { ... }
```

Best when you know iteration bounds or need an index: `for (int i = 0; i < n; i++)`.

### Enhanced `for` (“for-each”)

```java
for (Type element : iterableOrArray) { ... }
```

Read-only iteration over arrays or anything implementing `Iterable`. No index unless you track it yourself; cannot remove elements from most collections while iterating this way without extra care (concurrent modification).

### `while` and `do-while`

- **`while`:** test **before** each iteration—may run zero times.
- **`do-while`:** test **after** each iteration—runs **at least once**.

Use `while` for “until condition” loops where the count is not known upfront; use `do-while` rarely, e.g. menus that must show once before checking exit.

### `break`

Exits the **innermost** loop or `switch` immediately.

### `continue`

Skips the rest of the **current** iteration of the innermost loop and jumps to the next iteration’s condition/update.

### Labeled loops

A **label** is an identifier followed by `:` before a loop. `break label` or `continue label` applies to that labeled loop—useful for nested loops when you need to exit or skip the **outer** loop.

```java
outer: for (int i = 0; i < 3; i++) {
    for (int j = 0; j < 3; j++) {
        if (i == 1 && j == 1) break outer;
    }
}
```

Use labels sparingly; often extracting a method is clearer than deep nesting with `break outer`.

## Code Example

```java
public class LoopDemo {
    public static void main(String[] args) {
        int[] nums = { 2, 4, 6 };

        for (int i = 0; i < nums.length; i++) {
            if (nums[i] % 2 != 0) continue;
            System.out.println(nums[i]);
        }

        for (int n : nums) {
            System.out.println("foreach: " + n);
        }

        int x = 0;
        while (x < 3) {
            System.out.println("while " + x++);
        }

        int y = 0;
        do {
            System.out.println("do-while " + y++);
        } while (y < 2);
    }
}
```

## Summary

- `for` / enhanced `for` / `while` / `do-while` fit different control patterns.
- `break` exits the nearest loop or `switch`; `continue` advances to the next iteration.
- Labels target an outer loop for `break`/`continue` when nested structure requires it.

## Additional Resources

- [Oracle Java Tutorial — The while and do-while Statements](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/while.html)
- [Oracle Java Tutorial — The for Statement](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/for.html)
