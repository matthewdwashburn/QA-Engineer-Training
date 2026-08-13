# `Queue`, `Deque`, and `PriorityQueue`

## Learning Objectives

- Use `Queue` operations **`offer`**, **`poll`**, **`peek`** (and fail-fast `add`/`remove`/`element` pairs).
- Contrast **FIFO** `LinkedList`/`ArrayDeque` with **`PriorityQueue`** ordering.
- Describe **`Deque`** as double-ended queue API and key methods (`offerFirst`/`Last`, `pollFirst`/`Last`, `push`/`pop`, `descendingIterator`).

## Why This Matters

Queues model work backlogs, breadth-first traversal, and schedulers. `PriorityQueue` drives task ordering by priority—used in your Thursday pair exercise and many algorithms.

## The Concept

### `Queue`

`Queue<E>` extends **`Collection<E>`**, so it inherits everything collections share (`size`, `isEmpty`, `contains`, `clear`, `iterator`, `forEach`, stream APIs, …). Below are **methods declared on `Queue`** for FIFO behavior.

#### Single-ended queue operations

Typical idioms (prefer non-throwing):

| Role | Throws on failure | Returns special value |
|------|-------------------|----------------------|
| Insert at tail | `add(e)` | `offer(e)` → `false` if full (bounded queues) |
| Remove head | `remove()` | `poll()` → `null` if empty |
| Examine head | `element()` | `peek()` → `null` if empty |

#### Other `Queue` methods

| Method | Behavior |
|--------|----------|
| `remove(Object o)` | Removes **one** matching element (from `Collection`); differs from **head** `remove()` |
| `contains(Object o)`, `containsAll(Collection<?> c)` | Search |
| `toArray()`, `toArray(T[] a)` | Snapshot as array |

Head/tail wording is conceptual: `Queue` does not name “head”/`tail` in method names—that detail appears on **`Deque`**.

### `Deque`

`Deque<E>` extends **`Queue<E>`**, so it has **all `Queue` methods** above. It adds **both ends** and a **stack** view.

#### Head and tail (insert)

| Throws if cannot | Returns `boolean` |
|------------------|-------------------|
| `addFirst(e)`, `addLast(e)` | `offerFirst(e)`, `offerLast(e)` |

#### Head and tail (remove / examine)

| Remove (throws if empty) | Remove (null if empty) | Examine (throws if empty) | Examine (null if empty) |
|---------------------------|-------------------------|----------------------------|-------------------------|
| `removeFirst()`, `removeLast()` | `pollFirst()`, `pollLast()` | `getFirst()`, `getLast()` | `peekFirst()`, `peekLast()` |

For a non-empty deque, `peek()` / `poll()` / `element()` / `remove()` follow **`Queue`** contract: they operate on the **same end** as FIFO head (`peekFirst` / `pollFirst` / …).

#### Stack idiom (`Deque` as `Stack` replacement)

| Method | Equivalent deque operation |
|--------|----------------------------|
| `push(e)` | `addFirst(e)` |
| `pop()` | `removeFirst()` |
| `peek()` | `peekFirst()` (also satisfies `Queue`’s `peek`) |

#### Other useful `Deque` methods

| Method | Behavior |
|--------|----------|
| `removeFirstOccurrence(o)`, `removeLastOccurrence(o)` | Remove **one** match from front or back |
| `descendingIterator()` | Iterate **tail → head** |

**`ArrayDeque`** is an efficient resizable-array implementation of `Deque`—preferred over legacy **`Stack`**.

### `PriorityQueue`

- **Unbounded** priority heap (binary heap implementation).
- Head is **least** element per **natural order** or **`Comparator`**.
- **O(log n)** insert/remove; **`peek`** **O(1)**.
- **Not** thread-safe; **iterator** is not ordered by priority.

## Code Example

```java
Queue<String> fifo = new ArrayDeque<>();
fifo.offer("a");
fifo.offer("b");
fifo.poll(); // a

record Task(int priority, String name) {}
Queue<Task> pq = new PriorityQueue<>(Comparator.comparingInt(Task::priority));
pq.offer(new Task(2, "low"));
pq.offer(new Task(1, "high"));
```

## Summary

- `Queue` for FIFO (usually `ArrayDeque`); core API is `offer` / `poll` / `peek`, plus inherited `Collection` methods.
- `Deque` adds head/tail pairs (`offerFirst`/`Last`, `pollFirst`/`Last`, …), stack `push`/`pop`, and `descendingIterator`.
- `PriorityQueue` orders by priority, not insertion time.

## Additional Resources

- [Queue Interface (tutorial)](https://docs.oracle.com/javase/tutorial/collections/interfaces/queue.html)
- [Deque Interface (tutorial)](https://docs.oracle.com/javase/tutorial/collections/interfaces/deque.html)
- [`Queue` (Java SE API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Queue.html)
- [`Deque` (Java SE API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Deque.html)
- [`PriorityQueue` (Java SE API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/PriorityQueue.html)
