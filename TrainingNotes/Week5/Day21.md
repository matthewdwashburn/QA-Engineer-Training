# Day 21 - Agile Methodologies & Sprint Burndown Charts (Pandas/Matplotlib)

---

## Agile
- Iterative
- Flexible, malleable, efficient
- Expect failures, changes
- Multiple cycles of design, develop, deploy sprints

### Pros
- Quick responce to change
- Allows for uncertainty
- Faster review cycles
- Greater flexibility

### Cons
- Can lead to bad behaviors
- Not intuitive, requires more knowledge
- Too much flexibility, sometimes doesn't have clear direction

## Waterfall
- Sequential
- Expensive, rigid, inefficient
- Cant have any problems after deploying, no changes

### Pros
- Clean well defined steps
- Intuitive
- Clear and concrete goals

### Cons
- Exlcudes the client 
- Delays testing until completion
- Lack of flexibility or problem handling

## Big Bang
- Start and finish, all in one go
- Explosive method

## Software Development Life Cycle
1. Gather requirements 
2. Analysis
3. Design
4. Development
5. Testing
6. User Acceptance Testing
7. Release
8. Maintain

## Scrum
- 2-4 Week cycles of design, develop, deploy sprints
- Product backlog, sprint planning, sprint backlog, sprint, backlog refinement, daily standups, review, retrospective
- Transparency, inspection, and adaptation

### Pros
- Makes it easier to find and recify mistaks
- Enforces communication
- Constant feedback loops
- Involves client

### Cons
- Lacks final deadlines
- Lacks specific requirements

## Kanban
- Table of tasks in sections, move tasks along the board as they are being completed

### Pros
- Event driven instead of time driven
- Can be added whenever there is capacity
- Board is persistent

### Cons
- Lacks timeline
- Boards can be overcomplicated
- Boards can become outdated

## ScrumBan
- Identify sets of user stories and combine them into Epics (features), one epic per sprint usually
- Pair programming, pilot (coder) and navigator (High level architechure) can be coding over the shoulder
- Velocity is the number of story points completed in a sprint

## Story points
- Story points are used to help give us an estimate, determine difficulty, and prioritization of our use cases.

## Vocab
**SDLC** - The software development life cycle is a general overview of the process of software development. 
**Methodology** - The overall strategies for software development. Big Bang, Waterfall, and Agile.
**Framework** - Specific implementations of methodologies. Such as Scrum, Kanban, and XP.
**Velocity** - The rate at which your team is able to complete tasks.

---

## Pandas — `pd.read_csv()`

Loads a CSV straight into a DataFrame; columns are accessed like dict keys.

```python
import pandas as pd
df = pd.read_csv("sprint_remaining_points.csv")
df["ideal_remaining"]   # column -> Series
len(df)                  # row count
```

## Matplotlib — line chart + save to file

```python
import matplotlib.pyplot as plt

plt.figure(figsize=(8, 4))
plt.plot(x, df["ideal_remaining"], label="Ideal Remaining")
plt.plot(x, df["actual_remaining"], label="actual Remaining")
plt.xticks(x, df["day_label"])   # replace numeric ticks with custom labels
plt.ylabel("story points remaining")
plt.xlabel("Sprint day")
plt.title("Sprint burndown (sample week 5 data)")
plt.legend()
plt.savefig("burndown_sample.png")   # writes chart to disk instead of plt.show()
```

Used to build a **sprint burndown chart**: ideal-remaining vs actual-remaining story points per sprint day, plotted from CSV data to visualize whether a Scrum sprint is trending toward completion.

## IP Address Range — coding challenge

Convert dotted IPv4 strings into a single base-256 integer so two addresses can be subtracted directly:

```python
split = [int(x) for x in start.split(".")]   # "10.0.1.0" -> [10, 0, 1, 0]
total = (split[0] * 256**3) + (split[1] * 256**2) + (split[2] * 256) + split[3]
diff = abs(start_total - end_total)
```

- `str.split(".")` + list comprehension to parse octets into ints
- treats each octet as a digit of a base-256 number

---

## Java — Coding Challenges (JUnit 5)

### `Arrays.stream(arr).mapToInt(Integer::parseInt).toArray()` — String[] to int[]

```java
String[] split_start_string = start.split("\\.");
int[] split_start = Arrays.stream(split_start_string).mapToInt(Integer::parseInt).toArray();
// stream the array, map each String to int via parseInt, collect back into int[]
```

Java port of the IP-address exercise — same base-256 logic as the Python version, but needs `long` (not `int`) for the totals since `256^3` overflows a 32-bit int, and `Math.pow(256, 3)` returns a `double` so it's cast back: `(long) Math.pow(256, 3)`.

### `FindOutlier` — parity check on a sample to identify the "odd one out"

```java
if (integers[i] % 2 != 0) { odd_count++; }   // sample the first 3 to detect which parity is rare
```
Checks a few elements to decide whether the list is mostly odd or mostly even, then scans for the one value that breaks the majority parity.

### `LandPerimiter` — 2D grid padding + 8-neighbor scan

```java
int[][] landArr = new int[y_length + 2][x_length + 2];   // pad border with 0s to avoid bounds checks
for (int k = -1; k < 2; k++)
    for (int l = -1; l < 2; l++)
        // check all 8 neighbors of a cell; perimeter += 4 - count of adjacent land cells
```
Padding the grid by 1 on every side removes the need for edge-of-array bounds checks when scanning neighbors.

### JUnit 5 — `@Test` and `assertEquals`

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@Test
void testBasic() {
    assertEquals("Total land perimeter: 60", LandPerimiter.landPerimeter(new String[] { "OXOOOX", ... }));
}

@Test
public void ipAddressTest() {
    doTest((1l << 32l) - 1l, "0.0.0.0", "255.255.255.255");   // bit-shift to compute 2^32 - 1
}
```
- `assertEquals(expected, actual, message)` — optional 3rd arg shown only on failure, useful for parameterized-style helper methods like `doTest(...)`

---

## Notes
- Burndown chart = ideal trend line vs actual remaining points per day; gap between the two lines shows if a sprint is ahead/behind
- `plt.savefig()` instead of `plt.show()` when running headless / generating a file artifact
- Same IP-address exercise solved in both Python (`exercises_python/ip_address.py`) and Java (`exercises21/CountIPAddresses.java`) — good comparison of `int` overflow handling between languages
