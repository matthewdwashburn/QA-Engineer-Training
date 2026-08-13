# Day 23 - QC Interview Prep: Java & SQL Fundamentals Review

---

Quality Check / mock-interview prep day. Built two ground-up study guides (`QC-Complete-Study-Guide.md`, `QC-Missed-Concepts-Guide.md`) plus flashcard/quiz apps rebuilding every Java & SQL fundamental. Below is the condensed cheat-sheet — the exact facts interviewers probe. Full detail lives in the study guides.

## Java — core concepts (one-liners)

| Concept | The fact to know |
|---|---|
| **Bytecode / JVM** | `javac` → bytecode (`.class`) → runs on any JVM = "write once, run anywhere" |
| **Stack vs Heap** | Stack = local vars + method frames; Heap = objects (`new`). Reference on stack points to heap object |
| **Constructor** | No return type (not even `void`); auto-provided only if you declare none; overloadable, **never overridable** |
| **Access modifiers** | `public` / `protected` / default (package-private) / `private` |
| **Non-access modifiers** | `static`, `final`, `abstract`, `synchronized`, `volatile` (control behavior, not visibility) |
| **`static`** | Belongs to the class, one shared copy, callable with no object (why `main` is static) |
| **`final`** | variable → no reassign; method → no override; class → no extend |
| **Wrapper classes** | `int→Integer`, `char→Character`… needed in collections/generics; autoboxing bridges them |
| **`==` vs `.equals()`** | `==` = reference identity (& primitives); `.equals()` = value. Override `hashCode` whenever you override `equals` |
| **Strings** | Immutable + interned in the **string pool**; `new String()` forces a distinct object |
| **Reflection** | Inspect/modify classes, methods, fields at runtime via `Class` object |

## OOP — the four pillars (A PIE)

- **Abstraction** — expose *what*, hide *how* (interfaces / abstract APIs).
- **Polymorphism** — one interface, many behaviors. Two forms:
  - **Overloading** = compile-time, same name **different params**, same class.
  - **Overriding** = runtime (dynamic dispatch), **same signature**, subclass, chosen by the *actual object*. (overRIDE → RUNtime)
  - Static same-signature = **hiding** (compile-time, by reference type), *not* overriding.
- **Inheritance** — subclass `extends` one superclass (Java = single class inheritance). Multiple inheritance of *type* only via interfaces.
- **Encapsulation** — private state + public controlled getters/setters.

### Abstract class vs Interface

| | Abstract class | Interface |
|---|---|---|
| State (instance fields)? | Yes | No |
| Constructors? | Yes | No |
| Method bodies? | Yes | Only `default`/`static` (Java 8+) |
| A class can have… | one (`extends`) | many (`implements`) |

Pre-Java 8 key difference: **interfaces could not have method implementations**.

## Java — exceptions, collections, functional

- **`Throwable`** = root of *everything* thrown (parent of both `Error` and `Exception`).
- **Checked** (extend `Exception`) = compiler-forced catch/throws. **Unchecked** (`RuntimeException`, `Error`) = not enforced.
- Custom exception = subclass `Exception` + `super(message)`.
- **Collections:** `List` (ordered, dupes) · `Set` (unique) · `Queue/Deque` · `Map` (key→value). `TreeSet` = **sorted**; `HashSet` = fast; `ArrayDeque` (push/pop) = modern stack (LIFO).
- **Functional interfaces** (one abstract method → lambda): `Function<T,R>` → `apply`, `Consumer<T>` → `accept`, `Predicate<T>` → `test`, `Supplier<T>` → `get`.
- **Streams:** intermediate ops (`filter`/`map`/`sorted`) are lazy & return a Stream; terminal ops (`collect`/`count`/`reduce`) trigger execution.
- **Threads:** `Thread`/`Runnable`, launch with `start()`. States: NEW → RUNNABLE → BLOCKED/WAITING/TIMED_WAITING → TERMINATED. `synchronized` = one thread at a time.
- **Design patterns:** Singleton (one instance, private constructor) · Factory (hides which concrete class is created).
- **Maven:** default remote = **Central Repository**; `.m2` = local cache checked first.

## SQL — core concepts (one-liners)

| Concept | The fact to know |
|---|---|
| **Sublanguages** | DDL (structure) · DML (data) · DQL (`SELECT`) · DCL (`GRANT`/`REVOKE`) · TCL (`COMMIT`/`ROLLBACK`) |
| **DROP / TRUNCATE / DELETE** | DROP removes table; TRUNCATE empties fast (no rollback); DELETE removes rows (WHERE-able, rollback-able) |
| **Money type** | `DECIMAL(p,s)` (exact) — never `FLOAT`/`REAL` (rounding errors) |
| **PRIMARY KEY** | Implies **UNIQUE + NOT NULL**, one per table |
| **FOREIGN KEY** | References a PK elsewhere; every value must match a real PK or be null (referential integrity, no orphans) |
| **Junction table** | Resolves many-to-many: two FKs + composite PK |
| **Normalization** | 1NF atomic · 2NF no partial dependency · 3NF no transitive dependency |
| **Joins** | INNER (matches) · LEFT (all left + NULLs) · FULL (either) · CROSS (all pairs) · SELF |
| **Set ops** | UNION (dedups) · UNION ALL (keeps dupes) · INTERSECT (both) · EXCEPT (first not second) |
| **WHERE vs HAVING** | WHERE filters rows before grouping; HAVING filters groups after aggregation |
| **Query order** | FROM → WHERE → GROUP BY → HAVING → SELECT → ORDER BY → LIMIT |
| **Aggregate vs scalar** | Aggregate = many rows→one (`SUM`/`COUNT`); scalar = one→one (`UPPER`/`ROUND`) |

### Transactions, isolation & persistence

- **ACID:** Atomicity (all-or-nothing) · Consistency (valid states) · Isolation · Durability (survives crash). NoSQL counterpart = **BASE**.
- **Isolation levels** (each stops more): READ UNCOMMITTED → READ COMMITTED (stops dirty read) → REPEATABLE READ (+ non-repeatable) → SERIALIZABLE (+ phantom).
- **View** = virtual (re-computed, not faster). **Materialized view** = stored result (fast, can be stale).
- **Index** = speeds selective reads; slows writes + uses storage. Don't over-index.
- **DAO** = interface isolating all DB access from business logic. **PreparedStatement** (`?` placeholders) binds input as data → blocks SQL injection.
- **JDBC:** `Connection` = the physical connection (from `DriverManager.getConnection`); utility class = connection/resource management.
- **SQLite `:memory:`** = throwaway in-memory DB. **SQLAlchemy:** ORM = rows as objects, Session = transactions, Engine = connection pool.
- **Ports:** PostgreSQL = **5432**, MySQL = **3306**.

## Top interview traps (the ones that catch people)

- Overriding = **runtime**; overloading = compile-time.
- Static methods **hide**, they don't override; constructors can't be overridden.
- Parent of all exceptions = **`Throwable`**, not `Exception`.
- Sorted collection = **`TreeSet`**; money = **`DECIMAL`**.
- `switch` rejects `double`/`float`/`long`/`boolean`.
- `int[] arr` and `int arr[]` are **both** valid; a file allows many classes but **one `public`**.
