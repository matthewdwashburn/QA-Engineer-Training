import React, { useState, useMemo, useEffect, useRef, useCallback } from "react";
import {
  ArrowLeft, Shuffle, RotateCcw, Eye, Check, RefreshCw,
  Layers, Code2, Database, Coffee, ChevronRight, Keyboard, Trophy
} from "lucide-react";

/* =========================================================================
   REVATURE QC PREP — Java + SQL
   Write-first flashcards. Type your answer, reveal, self-check keywords,
   grade yourself. Missed cards recirculate until mastered.
   Covers every concept + coding prompt from the 5 source files.
   ========================================================================= */

const THEME = {
  sky:     { dot: "bg-sky-400",     text: "text-sky-300",     ring: "ring-sky-400/40",     soft: "bg-sky-400/10",     bar: "bg-sky-400" },
  cyan:    { dot: "bg-cyan-400",    text: "text-cyan-300",    ring: "ring-cyan-400/40",    soft: "bg-cyan-400/10",    bar: "bg-cyan-400" },
  violet:  { dot: "bg-violet-400",  text: "text-violet-300",  ring: "ring-violet-400/40",  soft: "bg-violet-400/10",  bar: "bg-violet-400" },
  amber:   { dot: "bg-amber-400",   text: "text-amber-300",   ring: "ring-amber-400/40",   soft: "bg-amber-400/10",   bar: "bg-amber-400" },
  rose:    { dot: "bg-rose-400",    text: "text-rose-300",    ring: "ring-rose-400/40",    soft: "bg-rose-400/10",    bar: "bg-rose-400" },
  emerald: { dot: "bg-emerald-400", text: "text-emerald-300", ring: "ring-emerald-400/40", soft: "bg-emerald-400/10", bar: "bg-emerald-400" },
  fuchsia: { dot: "bg-fuchsia-400", text: "text-fuchsia-300", ring: "ring-fuchsia-400/40", soft: "bg-fuchsia-400/10", bar: "bg-fuchsia-400" },
  teal:    { dot: "bg-teal-400",    text: "text-teal-300",    ring: "ring-teal-400/40",    soft: "bg-teal-400/10",    bar: "bg-teal-400" },
  orange:  { dot: "bg-orange-400",  text: "text-orange-300",  ring: "ring-orange-400/40",  soft: "bg-orange-400/10",  bar: "bg-orange-400" },
};

/* ------------------------------------------------------------------ DECKS */

const DECKS = [
  /* ===================== SQL: FUNDAMENTALS & DESIGN ===================== */
  {
    id: "sql-fund", name: "SQL — Fundamentals & Design", color: "sky", kind: "concept",
    icon: Database,
    cards: [
      { q: "What is SQL?", a: "Structured Query Language — the standard language for defining, manipulating, querying, and controlling data in a relational database.", keywords: ["Structured Query Language", "relational", "standard"] },
      { q: "What is a relational database management system (RDBMS)?", a: "Software that stores data in tables (relations) of rows and columns, enforces relationships/constraints between them, and is queried with SQL. Examples: PostgreSQL, MySQL, SQL Server, Oracle.", keywords: ["tables", "relations", "rows", "columns", "constraints"] },
      { q: "What is a database?", a: "An organized, persistent collection of structured data managed by a DBMS so it can be stored, accessed, updated, and queried efficiently.", keywords: ["organized", "persistent", "structured", "DBMS"] },
      { q: "What are the sublanguages (categories) of SQL? Give an example of each.", a: "DDL – defines structure (CREATE, ALTER, DROP, TRUNCATE). DML – changes data (INSERT, UPDATE, DELETE). DQL – reads data (SELECT). DCL – permissions (GRANT, REVOKE). TCL – transaction control (COMMIT, ROLLBACK, SAVEPOINT).", keywords: ["DDL", "DML", "DQL", "DCL", "TCL", "CREATE", "INSERT", "SELECT", "GRANT", "COMMIT"] },
      { q: "What is CRUD?", a: "Create, Read, Update, Delete — the four basic persistence operations, mapping to INSERT, SELECT, UPDATE, DELETE.", keywords: ["Create", "Read", "Update", "Delete", "INSERT", "SELECT"] },
      { q: "What is cardinality (in data modeling)?", a: "The numeric relationship between rows of two related tables: one-to-one, one-to-many, or many-to-many. (Column cardinality also means the number of distinct values in a column.)", keywords: ["one-to-one", "one-to-many", "many-to-many", "distinct"] },
      { q: "What is multiplicity? Give a 1-to-1, 1-to-N, and N-to-N example.", a: "How many instances of one entity relate to another. 1-to-1: person ↔ passport. 1-to-N: customer ↔ orders. N-to-N: students ↔ courses (needs a junction table).", keywords: ["1-to-1", "1-to-N", "N-to-N", "junction"] },
      { q: "What is a candidate key?", a: "A minimal set of columns that can uniquely identify a row. A table may have several candidate keys; one is chosen as the primary key, the rest are alternate keys.", keywords: ["minimal", "uniquely identify", "primary key", "alternate"] },
      { q: "What are primary keys and foreign keys?", a: "Primary key: column(s) that uniquely identify each row — unique, not null, one per table. Foreign key: column(s) referencing a primary/unique key in another (or the same) table to enforce relationships.", keywords: ["unique", "not null", "identify", "references", "relationship"] },
      { q: "What is referential integrity?", a: "The rule that every foreign key value must match an existing primary/unique key in the referenced table (or be null) — so there are no orphan references.", keywords: ["foreign key", "match", "orphan", "referenced"] },
      { q: "What are common column constraints?", a: "NOT NULL, UNIQUE, PRIMARY KEY, FOREIGN KEY, CHECK, and DEFAULT.", keywords: ["NOT NULL", "UNIQUE", "PRIMARY KEY", "FOREIGN KEY", "CHECK", "DEFAULT"] },
      { q: "What is an Entity Relationship Diagram (ERD)?", a: "A visual model of a schema showing entities (tables), their attributes, and the relationships/cardinality between them.", keywords: ["entities", "attributes", "relationships", "cardinality"] },
      { q: "What is a junction (associative) table, and when do you need one?", a: "A table that implements a many-to-many relationship. It holds two foreign keys (e.g. student_id, course_id), often with a composite PK or uniqueness on the pair. Needed when neither side can hold the other's key without repeating or losing multiplicity.", keywords: ["many-to-many", "two foreign keys", "composite", "enrollment"] },
      { q: "Why is DECIMAL/NUMERIC preferred over FLOAT for money?", a: "FLOAT/REAL store approximate binary values and cause rounding errors on decimal fractions. DECIMAL(p,s) is fixed-point and gives exact decimal arithmetic — the safe choice for currency.", keywords: ["exact", "floating point", "rounding", "fixed-point", "approximate"] },
      { q: "What is normalization?", a: "Organizing tables to reduce redundancy and improve data integrity by decomposing data into related tables with defined keys.", keywords: ["reduce redundancy", "integrity", "decompose"] },
      { q: "Explain 1NF, 2NF, and 3NF.", a: "1NF: values are atomic — no repeating groups, one value per cell. 2NF: 1NF + every non-key column depends on the WHOLE composite key (no partial dependency). 3NF: 2NF + no transitive dependency (non-key columns depend only on the key, not on other non-key columns).", keywords: ["atomic", "composite key", "partial", "transitive", "non-key"] },
      { q: "What is a port number? What is the default port for MySQL (and Postgres)?", a: "A port number (0–65535) is a logical endpoint identifying a specific service/process on a host for network traffic. MySQL default = 3306. PostgreSQL default = 5432.", keywords: ["endpoint", "service", "3306", "5432"] },
    ],
  },

  /* ===================== SQL: QUERIES & JOINS ===================== */
  {
    id: "sql-queries", name: "SQL — Queries & Joins", color: "cyan", kind: "concept",
    icon: Database,
    cards: [
      { q: "What is the difference between WHERE and HAVING?", a: "WHERE filters individual rows BEFORE grouping and cannot use aggregates. HAVING filters GROUPS AFTER aggregation and can use aggregate functions like COUNT(*) or SUM().", keywords: ["before", "after", "aggregate", "groups", "rows"] },
      { q: "What is the difference between GROUP BY and ORDER BY?", a: "GROUP BY collapses rows into groups so aggregates can be computed per group. ORDER BY sorts the final result set (ASC/DESC). Different jobs — one groups, one sorts.", keywords: ["group", "aggregate", "sort", "ASC", "DESC"] },
      { q: "What is the logical order of operations in a SELECT query?", a: "FROM → WHERE → GROUP BY → HAVING → SELECT → ORDER BY → LIMIT. Even though SELECT is written first, columns are projected after HAVING and before ORDER BY.", keywords: ["FROM", "WHERE", "GROUP BY", "HAVING", "SELECT", "ORDER BY"] },
      { q: "Contrast INNER JOIN and LEFT OUTER JOIN.", a: "INNER JOIN keeps only rows where the join condition is true on BOTH sides. LEFT JOIN keeps EVERY row from the left table; unmatched right-side columns are NULL. Use LEFT to include entities with no related rows (all customers, even with no orders).", keywords: ["match", "both", "all left", "NULL"] },
      { q: "What are the different joins in SQL?", a: "INNER (matches only), LEFT OUTER (all left + matches), RIGHT OUTER (all right + matches), FULL OUTER (all rows both sides), CROSS (every pair / Cartesian product), and SELF join (a table joined to itself). The ON clause is the join condition.", keywords: ["INNER", "LEFT", "RIGHT", "FULL", "CROSS", "condition"] },
      { q: "What are the set operations in SQL, and which allow duplicates?", a: "UNION (combines, removes duplicates), UNION ALL (combines, KEEPS duplicates), INTERSECT (rows in both), EXCEPT/MINUS (rows in first not second). Only UNION ALL keeps duplicates. Column count and types must be compatible.", keywords: ["UNION", "UNION ALL", "INTERSECT", "EXCEPT", "duplicates"] },
      { q: "What is the difference between UNION and UNION ALL?", a: "Both stack compatible result sets vertically. UNION removes duplicates (extra dedup work). UNION ALL concatenates without dedup and is faster when duplicates are impossible or acceptable.", keywords: ["duplicates", "concatenate", "faster", "dedup"] },
      { q: "What is the difference between joins and set operations?", a: "Joins combine tables HORIZONTALLY, matching rows on a condition to add columns. Set operations combine result sets VERTICALLY (stack rows) and require compatible column counts/types.", keywords: ["horizontal", "vertical", "columns", "rows", "compatible"] },
      { q: "What does LIKE do?", a: "Pattern matching in a WHERE clause using wildcards: % matches any sequence of characters, _ matches exactly one character. e.g. name LIKE 'A%' finds names starting with A.", keywords: ["pattern", "%", "_", "wildcard"] },
      { q: "How does BETWEEN work?", a: "BETWEEN is an inclusive range test: col BETWEEN a AND b is equivalent to col >= a AND col <= b. Works on numbers, dates, and text ranges.", keywords: ["inclusive", "range", ">=", "<="] },
      { q: "What is a subquery and how do you use one?", a: "A query nested inside another (in SELECT, FROM, or WHERE). It can be scalar (single value), correlated (references the outer query), or return a set used with IN / EXISTS / comparison operators.", keywords: ["nested", "scalar", "correlated", "IN", "EXISTS"] },
      { q: "What is the difference between an aggregate function and a scalar function? Give examples.", a: "An aggregate function operates over a SET of rows and returns one value: SUM, COUNT, AVG, MIN, MAX. A scalar function operates per row/value and returns one value per input: UPPER, LOWER, LENGTH, ROUND, CONCAT, NOW.", keywords: ["set of rows", "one value", "per row", "SUM", "COUNT", "UPPER", "ROUND"] },
      { q: "How do you create an alias, and what does AS do?", a: "AS assigns a temporary name to a column or table for readability. e.g. SELECT salary AS pay; FROM employees AS e (AS is optional for table aliases). Aliases are used in the output and to reference joined tables.", keywords: ["temporary name", "column", "table", "AS", "readability"] },
    ],
  },

  /* ===================== SQL: TRANSACTIONS & ADVANCED ===================== */
  {
    id: "sql-adv", name: "SQL — Transactions & Advanced", color: "violet", kind: "concept",
    icon: Database,
    cards: [
      { q: "What is a transaction?", a: "A unit of work: one or more statements executed as a single all-or-nothing logical operation, bounded by BEGIN and COMMIT (or ROLLBACK).", keywords: ["unit of work", "all-or-nothing", "COMMIT", "ROLLBACK"] },
      { q: "What does it mean that an operation is atomic?", a: "Indivisible — it either fully completes or has no effect at all. No partial state is ever visible to others.", keywords: ["indivisible", "fully", "no effect", "partial"] },
      { q: "What does ACID stand for?", a: "Atomicity — all changes commit or roll back together. Consistency — the DB moves between valid states (constraints + rules). Isolation — concurrent transactions don't see each other's improper intermediate effects. Durability — after COMMIT, data survives crashes (WAL/redo).", keywords: ["Atomicity", "Consistency", "Isolation", "Durability", "COMMIT"] },
      { q: "What does BASE stand for?", a: "Basically Available, Soft state, Eventual consistency — a NoSQL/distributed model that favors availability and eventual consistency over strict ACID guarantees.", keywords: ["Basically Available", "Soft state", "Eventual consistency", "NoSQL"] },
      { q: "What are the transaction isolation levels, and what do they prevent?", a: "READ UNCOMMITTED (prevents nothing), READ COMMITTED (prevents dirty reads), REPEATABLE READ (also prevents non-repeatable reads), SERIALIZABLE (also prevents phantom reads). Higher isolation = fewer anomalies but more locking / less concurrency.", keywords: ["READ UNCOMMITTED", "READ COMMITTED", "REPEATABLE READ", "SERIALIZABLE"] },
      { q: "Explain dirty reads, non-repeatable reads, and phantom reads.", a: "Dirty read: reading another transaction's UNCOMMITTED data that may roll back. Non-repeatable read: re-reading the same row gives a different value (another txn updated + committed). Phantom read: re-running a range query returns new/removed rows (inserts/deletes committed by another txn).", keywords: ["dirty", "uncommitted", "non-repeatable", "phantom", "range"] },
      { q: "What is a view? What is a materialized view?", a: "A view is a stored, named query that behaves like a virtual table — the engine expands it against base tables on each query, so it does NOT auto-cache or replace indexes. A materialized view stores the query RESULT physically and must be refreshed — faster reads but data can be stale.", keywords: ["named query", "virtual", "base tables", "materialized", "refresh"] },
      { q: "What is an index, and what are its advantages and disadvantages?", a: "An index (usually a B-tree) on column(s) speeds up lookups, joins, and sorts by avoiding full table scans. Advantage: faster SELECT/WHERE/JOIN/ORDER BY. Disadvantage: slower INSERT/UPDATE/DELETE (index must be maintained) and extra storage.", keywords: ["B-tree", "scan", "faster reads", "slower writes", "storage"] },
      { q: "When would you add a secondary (non-clustered) index, and what's the downside of many indexes?", a: "Add one on selective columns used in WHERE, JOIN, or ORDER BY — like foreign keys and high-cardinality filters — where scans are costly. Downside: every write must update every relevant index, slowing INSERT/UPDATE/DELETE and consuming storage; unused indexes are pure cost.", keywords: ["selective", "WHERE", "JOIN", "writes", "maintenance"] },
      { q: "What is a DAO?", a: "Data Access Object — a pattern that encapsulates SQL and row mapping behind an interface (e.g. CustomerDao with JdbcCustomerDao), separating persistence from business/service logic so JDBC isn't scattered through the app.", keywords: ["Data Access Object", "interface", "SQL", "separation", "persistence"] },
      { q: "What is the danger of putting values directly into queries, and how do you prevent it?", a: "String-concatenating user input enables SQL injection (e.g. ' OR '1'='1) and blocks plan reuse. Use a PreparedStatement with ? placeholders and setString/setInt — the DB treats bound values as DATA, not SQL syntax.", keywords: ["SQL injection", "PreparedStatement", "bind", "data", "syntax"] },
      { q: "What trade-offs exist between stored procedures and logic only in application services?", a: "Procedures: fewer network round trips, logic centralized next to data, enforced regardless of client. But they're often vendor-specific, harder to unit-test like app code, and versioning can lag deployments. It's an architecture choice, not a universal rule.", keywords: ["round trips", "centralized", "vendor-specific", "testing", "versioning"] },
      { q: "What is the CAP Theorem, and what does it mean during a network partition?", a: "A distributed data store can guarantee at most two of Consistency, Availability, Partition tolerance. Since partitions happen, you must choose: stay consistent by rejecting/erroring (CP) or stay available by serving possibly-stale data (AP).", keywords: ["Consistency", "Availability", "Partition tolerance", "two", "CP", "AP"] },
    ],
  },

  /* ===================== JAVA: OOP & LANGUAGE ===================== */
  {
    id: "java-oop", name: "Java — OOP & Language", color: "amber", kind: "concept",
    icon: Coffee,
    cards: [
      { q: "What do you know about Java?", a: "An object-oriented, strongly-typed language compiled to bytecode that runs on the JVM (write once, run anywhere). It has automatic garbage collection and a rich standard library. Core OOP pillars: encapsulation, inheritance, polymorphism, abstraction.", keywords: ["object-oriented", "JVM", "bytecode", "garbage collection", "platform-independent"] },
      { q: "What is the difference between a class and an object?", a: "A class is the blueprint (fields, methods, constructors). An object is a runtime INSTANCE of that class, created with new, living on the heap; variables hold references to it.", keywords: ["blueprint", "instance", "new", "heap", "reference"] },
      { q: "What is a constructor and what does it do?", a: "A special method with the class's name and no return type, invoked by new to initialize a new object's state. If you declare none, a default no-arg constructor is provided. Constructors can be overloaded and can chain with this() / super().", keywords: ["initialize", "new", "no return type", "default", "this", "super"] },
      { q: "What does the static keyword mean?", a: "static members belong to the CLASS, not to instances — there's one shared copy, accessible without an object. Static methods can't use this or reference instance members directly. Used for shared constants, counters, and utility/factory methods.", keywords: ["class", "shared", "one copy", "no this"] },
      { q: "What does the final keyword do?", a: "final variable = can't be reassigned (a constant; for references the object's fields may still change). final method = can't be overridden. final class = can't be extended. final parameter = can't be reassigned in the method.", keywords: ["reassign", "constant", "override", "extend"] },
      { q: "How do you create a constant variable in Java?", a: "Use static final with an UPPER_SNAKE_CASE name, e.g. public static final int MAX_SIZE = 10;. static means one class-level copy; final means it can't be reassigned.", keywords: ["static final", "UPPER_SNAKE_CASE", "reassign"] },
      { q: "What are the four access modifiers, and what does the default mean?", a: "public — visible everywhere. protected — same package + subclasses (including in other packages for inherited members). default / package-private (no keyword) — same package only. private — only inside the declaring class. Default = package-private.", keywords: ["public", "protected", "package-private", "private", "same package"] },
      { q: "What is encapsulation, and what is abstraction?", a: "Encapsulation hides internal state (private fields) and exposes controlled behavior through methods (getters/setters, validation). Abstraction emphasizes WHAT a client can do (interfaces, abstract APIs) while hiding HOW it's done. Encapsulation supports abstraction by keeping a stable surface over changing internals.", keywords: ["private", "getters", "setters", "hide", "what vs how", "contract"] },
      { q: "What is the difference between method overloading and overriding?", a: "Overloading: same method name, DIFFERENT parameter lists in the same class — resolved at COMPILE time (return type alone can't distinguish). Overriding: a subclass provides a method with the SAME signature as the superclass — resolved at RUNTIME via virtual dispatch on the actual object type.", keywords: ["compile time", "runtime", "same signature", "different parameters", "subclass"] },
      { q: "What is an interface?", a: "A contract/abstract type declaring method signatures (and constants) that implementing classes must fulfill. It supports multiple inheritance of type and enables polymorphism and loose coupling. Since Java 8 it can have default and static methods (and private methods since 9).", keywords: ["contract", "signatures", "multiple inheritance", "default methods", "polymorphism"] },
      { q: "What is an abstract class?", a: "A class declared abstract that cannot be instantiated. It can hold abstract methods (no body), concrete methods, fields, and constructors. Subclasses must implement its abstract methods or be abstract too. Used for a shared base with partial implementation.", keywords: ["cannot instantiate", "abstract methods", "concrete", "fields", "subclass"] },
      { q: "What is the difference between an abstract class and an interface?", a: "Abstract class: single inheritance, can hold instance state/fields, constructors, and any access modifier; mixes abstract + concrete. Interface: multiple implementation, no instance state, historically only public abstract methods + constants (default/static since 8). Use an interface for a capability/contract, an abstract class for a shared base with state.", keywords: ["single inheritance", "state", "multiple", "constructor", "contract"] },
      { q: "Why would we use abstract classes and interfaces?", a: "For abstraction, polymorphism, and loose coupling — program to an interface, not an implementation. Interfaces define a capability/contract; abstract classes share common base state/behavior. Both enable dependency injection, swapping implementations, and easier testing.", keywords: ["abstraction", "polymorphism", "loose coupling", "contract", "testing"] },
      { q: "What is the difference between a static (nested) class and a public class?", a: "These are different axes, not opposites. 'static' typically refers to a static NESTED class — one that doesn't hold a reference to its outer instance, so it can be created without an outer object. 'public' is an access modifier meaning visible everywhere. Top-level classes can't be static.", keywords: ["nested", "outer instance", "access modifier", "visible"] },
      { q: "What are wrapper classes?", a: "Object wrappers for the 8 primitives: Integer, Long, Double, Float, Boolean, Character, Byte, Short. They let primitives be used in generics/collections and provide utilities/constants (Integer.parseInt, MAX_VALUE). Autoboxing/unboxing converts automatically; they're immutable — beware Integer caching (-128..127) and == pitfalls.", keywords: ["primitives", "Integer", "autoboxing", "immutable", "collections"] },
      { q: "What is short-circuit evaluation for && and ||, and why does it matter for null checks?", a: "For a && b, if a is false, b is NOT evaluated. For a || b, if a is true, b is NOT evaluated. This enables safe guards like if (x != null && x.length() > 0) so length() is never called on null, avoiding a NullPointerException.", keywords: ["&&", "||", "not evaluated", "null", "NullPointerException"] },
      { q: "What is the difference between equals and == for object references?", a: "== on references tests IDENTITY (same object in memory). equals expresses LOGICAL equality when overridden (e.g. same business key). Default Object.equals behaves like == until you override it.", keywords: ["identity", "value", "Object.equals", "override"] },
      { q: "Why must you override hashCode when you override equals?", a: "The contract requires equal objects (by equals) to have equal hashCode values. Hash-based collections (HashMap, HashSet) use hashCode to pick a bucket, then equals to resolve collisions. Breaking it causes lost lookups, duplicates, or inconsistent membership. Implement both from the same fields (Objects.equals / Objects.hash).", keywords: ["contract", "HashMap", "HashSet", "bucket", "collision"] },
    ],
  },

  /* ===================== JAVA: EXCEPTIONS ===================== */
  {
    id: "java-exc", name: "Java — Exceptions", color: "rose", kind: "concept",
    icon: Coffee,
    cards: [
      { q: "Tell me about exceptions.", a: "An exception is an event that disrupts normal flow, represented as a Throwable object. Throwable splits into Error (serious JVM problems like OutOfMemoryError) and Exception. Exception splits into checked (compiler-enforced, e.g. IOException) and unchecked / RuntimeException (e.g. NullPointerException). They propagate up the call stack until caught.", keywords: ["Throwable", "Error", "Exception", "checked", "unchecked", "call stack"] },
      { q: "Tell me about exception handling.", a: "Handle exceptions with try/catch/finally: try wraps risky code, catch handles matching types (most specific first), finally always runs for cleanup. try-with-resources auto-closes AutoCloseable resources. Alternatively declare throws to propagate to the caller. Don't swallow exceptions silently.", keywords: ["try", "catch", "finally", "try-with-resources", "throws"] },
      { q: "What is a try/catch block?", a: "try holds code that might throw an exception; catch handles a thrown exception of a matching type, preventing a crash and letting you recover or log instead. An optional finally block runs regardless for cleanup.", keywords: ["try", "catch", "matching type", "recover", "finally"] },
      { q: "What is the difference between a checked and an unchecked exception?", a: "Checked exceptions extend Exception but not RuntimeException; the compiler forces you to catch or declare throws. Unchecked exceptions are RuntimeException (and Error) and need no throws. Checked often models recoverable external failures; unchecked usually signals programming bugs.", keywords: ["Exception", "RuntimeException", "throws", "compiler", "recoverable"] },
      { q: "What is the throws keyword?", a: "In a method signature, throws declares that the method may throw the listed checked exceptions, pushing the responsibility to handle them onto the caller. It's different from throw, which actually throws an instance.", keywords: ["signature", "checked", "caller", "propagate", "throw vs throws"] },
      { q: "If you're not sure what exception is thrown, how can you write the try/catch?", a: "Catch a broad supertype such as Exception (or RuntimeException) as a fallback, or use multi-catch (catch (A | B e)). Prefer catching the most specific type you actually expect; use the broad catch to log e / e.getClass() rather than swallow silently.", keywords: ["Exception", "supertype", "multi-catch", "specific", "log"] },
    ],
  },

  /* ===================== JAVA: COLLECTIONS & DS&A ===================== */
  {
    id: "java-coll", name: "Java — Collections & DS&A", color: "emerald", kind: "concept",
    icon: Layers,
    cards: [
      { q: "What do you know about Java Collections?", a: "The Collections Framework: interfaces (List, Set, Queue/Deque, Map) plus implementations. List (ordered, indexed, dupes): ArrayList, LinkedList. Set (no dupes): HashSet, LinkedHashSet, TreeSet. Queue/Deque: ArrayDeque, PriorityQueue. Map (key→value): HashMap, LinkedHashMap, TreeMap. Generics give type safety; the Collections utility class provides sort/etc.", keywords: ["List", "Set", "Map", "Queue", "ArrayList", "HashMap", "generics"] },
      { q: "What are data structures?", a: "Ways to organize, manage, and store data that enable efficient access and modification when matched to a specific problem.", keywords: ["organize", "store", "efficient", "access", "modification"] },
      { q: "What is the difference between a List and a Set?", a: "List is ordered, indexed, and allows duplicates (positional access via get(i)). Set stores unique elements only and is about membership — HashSet (unordered), LinkedHashSet (insertion order), TreeSet (sorted).", keywords: ["ordered", "indexed", "duplicates", "unique", "membership"] },
      { q: "When is ArrayList preferred over LinkedList (and vice versa)?", a: "ArrayList is the default: get(i)/set(i) are O(1) with good locality. LinkedList's get(i) is O(n) because it walks nodes. Prefer LinkedList when you do lots of insertions/deletions, especially at the ends (O(1)), not for heavy random indexing.", keywords: ["random access", "get(i)", "O(1)", "O(n)", "insertions"] },
      { q: "What is a linked list (singly vs doubly)? Advantages and disadvantages?", a: "A linear structure whose elements (nodes) aren't in contiguous memory but linked by pointers; it's dynamic (grows/shrinks). Singly: one forward pointer — O(1) insert/delete, space-efficient, but no backward traversal and O(n) search. Doubly: adds a previous pointer — iterate both directions, easy delete/reverse, but extra memory and overhead per node. java.util.LinkedList is a doubly linked list.", keywords: ["nodes", "pointers", "dynamic", "forward", "previous", "O(1)"] },
      { q: "Where are linked lists used?", a: "To implement stacks, queues, hash tables, and graphs; undo/redo functionality; OS thread/process scheduling lists; games (deck of cards); and forward/back navigation (music players, browser history).", keywords: ["stacks", "queues", "undo/redo", "navigation", "scheduler"] },
      { q: "What is a stack? Benefits and a real-world example?", a: "A linear LIFO (Last-In-First-Out) structure — you push/pop from one end. Great for nested structures: function call stacks, parsers, expression evaluation, and undo/redo. Simple example: reversing a string letter by letter, or a text editor's undo.", keywords: ["LIFO", "push", "pop", "nested", "undo"] },
      { q: "What is a queue? What is a deque?", a: "A queue is a linear FIFO (First-In-First-Out) structure, open at both ends — enqueue at the back, dequeue at the front. A deque (double-ended queue) allows adding/removing at BOTH the front and back.", keywords: ["FIFO", "enqueue", "dequeue", "deque", "both ends"] },
      { q: "What is a set / a HashSet?", a: "A Set stores unordered, unique elements (no duplicates). A HashSet stores elements using hashing for fast average O(1) add/contains, holds unique elements only, allows a null value, and is non-synchronized.", keywords: ["unique", "unordered", "hashing", "null", "non-synchronized"] },
      { q: "What is a map, and what are real-world uses? Can it have duplicate keys? Null keys/values?", a: "A Map holds key→value pairs for fast lookup by key (e.g. zip code → city, region → countries). Keys must be unique — no duplicate keys (put with an existing key overwrites). Nulls depend on implementation: HashMap/LinkedHashMap allow a null key and null values; TreeMap does not allow null keys.", keywords: ["key value", "lookup", "unique keys", "null", "HashMap", "TreeMap"] },
      { q: "What happens when you add a duplicate KEY to a Map? A duplicate VALUE? A duplicate value to a Set?", a: "Duplicate key: put() overwrites/replaces the existing value and returns the old one — keys stay unique. Duplicate value: allowed — different keys may map to the same value. Duplicate value in a Set: add() returns false and the set is unchanged.", keywords: ["overwrite", "replace", "returns old", "allowed", "false"] },
      { q: "When would you choose TreeSet over HashSet?", a: "Choose TreeSet when you need sorted iteration or range/NavigableSet operations and can pay O(log n) per add/contains; elements must be mutually Comparable or you supply a Comparator. Choose HashSet for fast average membership when order doesn't matter.", keywords: ["sorted", "Comparable", "Comparator", "O(log n)", "order"] },
      { q: "How do HashMap and Hashtable differ, and what do you use for a concurrent map?", a: "HashMap is unsynchronized, allows one null key and null values, and is the usual default. Hashtable is legacy, synchronized on every method, and disallows null keys/values. For a shared concurrent map, prefer ConcurrentHashMap over Hashtable or Collections.synchronizedMap for scalability.", keywords: ["synchronized", "null", "legacy", "ConcurrentHashMap"] },
      { q: "A HashMap keyed by a custom Employee type fails lookups for 'the same' employee. What's the likely cause?", a: "Broken equals/hashCode on Employee — either default Object identity semantics when logical equality should use id, or hashCode wasn't overridden consistently with equals. Equal keys MUST produce equal hash codes. Fix by implementing both from the same fields (Objects.equals / Objects.hash).", keywords: ["equals", "hashCode", "contract", "fields", "identity"] },
    ],
  },

  /* ===================== JAVA: CONCURRENCY, JAVA 8, PATTERNS ===================== */
  {
    id: "java-adv", name: "Java — Threads, Java 8 & Patterns", color: "fuchsia", kind: "concept",
    icon: Coffee,
    cards: [
      { q: "What is multithreading, and how do you create a thread?", a: "Multithreading is running multiple paths of execution concurrently within a program. Create a thread by extending Thread and overriding run(), or by implementing Runnable and passing it to a Thread. Call start() (not run()) to launch a new thread of execution.", keywords: ["concurrent", "Thread", "Runnable", "run", "start"] },
      { q: "What is the lifecycle of a thread?", a: "The Thread.State enum: NEW (created, not started) → RUNNABLE (after start(); eligible to run or actively running) → BLOCKED (waiting for a monitor lock) / WAITING / TIMED_WAITING (via wait()/sleep()/join()) → TERMINATED (finished, can't restart).", keywords: ["NEW", "RUNNABLE", "BLOCKED", "WAITING", "TERMINATED"] },
      { q: "What is deadlock?", a: "When two or more threads are each waiting on locks held by the others, so none can proceed — the program stalls permanently.", keywords: ["threads", "locks", "waiting", "none proceed"] },
      { q: "What is the synchronized keyword?", a: "It enforces thread-safety by allowing only one thread at a time to enter a synchronized method or block (via the object/class monitor lock), preventing concurrent access to shared mutable state.", keywords: ["one thread", "lock", "monitor", "thread-safety"] },
      { q: "What is a StackOverflowError, and how do you avoid it in deep recursion?", a: "Each recursive call pushes a stack frame; too many nested calls exhaust the thread's stack → StackOverflowError. Sound recursion needs a base case and arguments that progress toward it. Because the JVM doesn't reliably optimize tail recursion, rewrite very deep linear recursion as an iterative loop.", keywords: ["stack frame", "base case", "tail recursion", "iteration", "JVM"] },
      { q: "What is the Reflection API?", a: "Reflection lets a program inspect and manipulate classes, methods, and fields at runtime. The Class object is the entry point: Class.forName() loads a class, getName() returns its name, newInstance()/getConstructor() create instances, and getMethods()/getFields()/getSuperclass()/getInterfaces() expose its members.", keywords: ["runtime", "Class", "forName", "getMethods", "getFields"] },
      { q: "Describe the Singleton and Factory design patterns.", a: "Singleton: ensures only one instance exists; a private constructor plus a static accessor returns the same object in memory. Factory: abstracts away instantiation logic behind a method that decides which object to create — often paired with Singleton.", keywords: ["Singleton", "one instance", "private constructor", "Factory", "instantiation"] },
      { q: "What is a functional interface, and name a few from java.util.function.", a: "A functional interface has exactly one abstract method (SAM), so a lambda can implement it; @FunctionalInterface documents this and triggers compiler checks. Examples: Predicate<T> (test), Function<T,R> (apply), Consumer<T> (accept), Supplier<T> (get).", keywords: ["SAM", "one abstract method", "@FunctionalInterface", "Predicate", "Function", "Consumer", "Supplier"] },
      { q: "What are lambdas?", a: "Lambdas are like anonymous functions — concise implementations of a functional interface's single method without writing a full class. Syntax: (args) -> expression or (args) -> { statements }.", keywords: ["anonymous", "functional interface", "->", "concise"] },
      { q: "What is the Streams API? Difference between intermediate and terminal operations?", a: "Streams process sequences/collections functionally (filter/map/reduce) without mutating the source, and are lazy. Intermediate operations return a Stream and are lazy — filter, map, sorted, distinct, limit — and chain. Terminal operations trigger execution and produce a result/side effect — collect, forEach, count, reduce, findFirst, anyMatch. e.g. list.stream().filter(x -> x > 2).map(x -> x * 2).collect(Collectors.toList()).", keywords: ["lazy", "intermediate", "terminal", "filter", "map", "collect", "reduce"] },
      { q: "What is an advantage of using a logging library, and what are Logback's levels?", a: "A logging library lets you set severity thresholds, route/format output, and turn logging up or down without code changes — better than System.out. Logback is a Java logging library; its levels are TRACE, DEBUG, INFO, WARN, ERROR (FATAL is often folded into ERROR).", keywords: ["thresholds", "levels", "TRACE", "DEBUG", "INFO", "WARN", "ERROR"] },
    ],
  },

  /* ===================== SQL CODING ===================== */
  {
    id: "sql-code", name: "SQL — Coding Challenges", color: "teal", kind: "code", lang: "sql",
    icon: Code2,
    note: "Sample schema:  departments(dept_id PK, name)  ·  employees(emp_id PK, name, salary, dept_id FK→departments)  ·  users(user_id PK, email)",
    cards: [
      { q: "Select every column for all employees in department 3.", a: "SELECT *\nFROM employees\nWHERE dept_id = 3;" },
      { q: "Return the names and salaries of the 5 highest-paid employees.", a: "SELECT name, salary\nFROM employees\nORDER BY salary DESC\nLIMIT 5;" },
      { q: "Count how many employees are in each department.", a: "SELECT dept_id, COUNT(*) AS num_employees\nFROM employees\nGROUP BY dept_id;" },
      { q: "List only the departments that have more than 5 employees.", a: "SELECT dept_id, COUNT(*) AS num_employees\nFROM employees\nGROUP BY dept_id\nHAVING COUNT(*) > 5;" },
      { q: "Show each employee's name alongside their department name.", a: "SELECT e.name, d.name AS department\nFROM employees e\nINNER JOIN departments d ON e.dept_id = d.dept_id;" },
      { q: "List all departments and their employee count, INCLUDING departments with no employees.", a: "SELECT d.name, COUNT(e.emp_id) AS num_employees\nFROM departments d\nLEFT JOIN employees e ON d.dept_id = e.dept_id\nGROUP BY d.name;\n\n-- LEFT JOIN keeps departments with no matching employees;\n-- COUNT(e.emp_id) is 0 for them (COUNT ignores NULLs)." },
      { q: "Find the second-highest salary.", a: "-- Option A: subquery\nSELECT MAX(salary) AS second_highest\nFROM employees\nWHERE salary < (SELECT MAX(salary) FROM employees);\n\n-- Option B: distinct + offset\nSELECT DISTINCT salary\nFROM employees\nORDER BY salary DESC\nLIMIT 1 OFFSET 1;" },
      { q: "Show the average salary per department, only for departments averaging over 60000.", a: "SELECT dept_id, AVG(salary) AS avg_salary\nFROM employees\nGROUP BY dept_id\nHAVING AVG(salary) > 60000;" },
      { q: "List employees who earn more than the company-wide average salary.", a: "SELECT name, salary\nFROM employees\nWHERE salary > (SELECT AVG(salary) FROM employees);" },
      { q: "Find duplicate emails in the users table.", a: "SELECT email, COUNT(*) AS cnt\nFROM users\nGROUP BY email\nHAVING COUNT(*) > 1;" },
      { q: "Give everyone in department 3 a 10% raise, then delete the employee with emp_id 42.", a: "UPDATE employees\nSET salary = salary * 1.10\nWHERE dept_id = 3;\n\nDELETE FROM employees\nWHERE emp_id = 42;" },
      { q: "Write DDL to create the employees table with a primary key, a NOT NULL name, a non-negative salary, and a foreign key to departments.", a: "CREATE TABLE employees (\n    emp_id   INT PRIMARY KEY,\n    name     VARCHAR(100) NOT NULL,\n    salary   DECIMAL(10,2) CHECK (salary >= 0),\n    dept_id  INT REFERENCES departments(dept_id)\n);" },
    ],
  },

  /* ===================== JAVA CODING ===================== */
  {
    id: "java-code", name: "Java — Coding Challenges", color: "orange", kind: "code", lang: "java",
    icon: Code2,
    note: "Common Revature-style timed problems: strings, arrays, collections, sliding window.",
    cards: [
      { q: "Reverse a string in place (no library reverse).", a: "public String reverse(String s) {\n    char[] c = s.toCharArray();\n    int i = 0, j = c.length - 1;\n    while (i < j) {\n        char t = c[i]; c[i] = c[j]; c[j] = t;\n        i++; j--;\n    }\n    return new String(c);\n}" },
      { q: "Check whether a string is a palindrome.", a: "public boolean isPalindrome(String s) {\n    int i = 0, j = s.length() - 1;\n    while (i < j) {\n        if (s.charAt(i) != s.charAt(j)) return false;\n        i++; j--;\n    }\n    return true;\n}" },
      { q: "Print FizzBuzz from 1 to n.", a: "for (int i = 1; i <= n; i++) {\n    if (i % 15 == 0)      System.out.println(\"FizzBuzz\");\n    else if (i % 3 == 0)  System.out.println(\"Fizz\");\n    else if (i % 5 == 0)  System.out.println(\"Buzz\");\n    else                  System.out.println(i);\n}" },
      { q: "Find the maximum value in an int array.", a: "public int max(int[] arr) {\n    int max = arr[0];\n    for (int x : arr)\n        if (x > max) max = x;\n    return max;\n}" },
      { q: "Count the frequency of each character in a string using a Map.", a: "Map<Character, Integer> freq = new HashMap<>();\nfor (char c : s.toCharArray()) {\n    freq.put(c, freq.getOrDefault(c, 0) + 1);\n}\nreturn freq;" },
      { q: "Two Sum: return the indices of the two numbers that add up to target.", a: "public int[] twoSum(int[] nums, int target) {\n    Map<Integer, Integer> seen = new HashMap<>();\n    for (int i = 0; i < nums.length; i++) {\n        int need = target - nums[i];\n        if (seen.containsKey(need))\n            return new int[]{ seen.get(need), i };\n        seen.put(nums[i], i);\n    }\n    return new int[]{-1, -1};\n}" },
      { q: "Return the set of duplicate values in an int array.", a: "Set<Integer> seen = new HashSet<>();\nSet<Integer> dups = new HashSet<>();\nfor (int x : arr) {\n    if (!seen.add(x)) dups.add(x); // add() returns false if already present\n}\nreturn dups;" },
      { q: "Compute the nth Fibonacci number iteratively.", a: "public int fib(int n) {\n    if (n < 2) return n;\n    int a = 0, b = 1;\n    for (int i = 2; i <= n; i++) {\n        int c = a + b;\n        a = b; b = c;\n    }\n    return b;\n}" },
      { q: "Check whether two strings are anagrams.", a: "public boolean isAnagram(String a, String b) {\n    if (a.length() != b.length()) return false;\n    int[] counts = new int[26];\n    for (char c : a.toCharArray()) counts[c - 'a']++;\n    for (char c : b.toCharArray())\n        if (--counts[c - 'a'] < 0) return false;\n    return true;\n}" },
      { q: "Find the first non-repeating character in a string.", a: "Map<Character, Integer> f = new LinkedHashMap<>();\nfor (char c : s.toCharArray())\n    f.put(c, f.getOrDefault(c, 0) + 1);\nfor (Map.Entry<Character, Integer> e : f.entrySet())\n    if (e.getValue() == 1) return e.getKey();\nreturn '_'; // none found\n\n// LinkedHashMap preserves insertion order so \"first\" is correct." },
      { q: "Sliding window: max sum of any contiguous subarray of size k.", a: "public int maxSum(int[] a, int k) {\n    int sum = 0;\n    for (int i = 0; i < k; i++) sum += a[i];\n    int max = sum;\n    for (int i = k; i < a.length; i++) {\n        sum += a[i] - a[i - k]; // slide window: add new, drop oldest\n        max = Math.max(max, sum);\n    }\n    return max;\n}" },
      { q: "Remove duplicates from a list while preserving order.", a: "List<Integer> out = new ArrayList<>();\nSet<Integer> seen = new HashSet<>();\nfor (int x : list) {\n    if (seen.add(x)) out.add(x); // add() true only the first time\n}\nreturn out;" },
    ],
  },
];

const STORAGE_KEY = "revature_qc_progress_v1";

/* ------------------------------------------------------------------ helpers */

function shuffle(arr) {
  const a = [...arr];
  for (let i = a.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [a[i], a[j]] = [a[j], a[i]];
  }
  return a;
}

function cardKey(deckId, idx) {
  return `${deckId}::${idx}`;
}

/* ==================================================================== APP */

export default function App() {
  const [screen, setScreen] = useState("home"); // home | study
  const [activeDeckId, setActiveDeckId] = useState(null);
  const [mastered, setMastered] = useState({}); // { "deckId::idx": true }
  const [loaded, setLoaded] = useState(false);

  // hydrate progress (best-effort)
  useEffect(() => {
    let live = true;
    (async () => {
      try {
        const res = await window.storage.get(STORAGE_KEY);
        if (live && res && res.value) setMastered(JSON.parse(res.value));
      } catch (_) { /* first run / unavailable — ignore */ }
      if (live) setLoaded(true);
    })();
    return () => { live = false; };
  }, []);

  const persist = useCallback((next) => {
    try { window.storage.set(STORAGE_KEY, JSON.stringify(next)); } catch (_) { /* ignore */ }
  }, []);

  const markMastered = useCallback((deckId, idx, value) => {
    setMastered((prev) => {
      const next = { ...prev };
      const k = cardKey(deckId, idx);
      if (value) next[k] = true; else delete next[k];
      persist(next);
      return next;
    });
  }, [persist]);

  const resetDeck = useCallback((deckId) => {
    setMastered((prev) => {
      const next = {};
      for (const k of Object.keys(prev)) if (!k.startsWith(deckId + "::")) next[k] = prev[k];
      persist(next);
      return next;
    });
  }, [persist]);

  const activeDeck = DECKS.find((d) => d.id === activeDeckId) || null;

  return (
    <div className="min-h-screen w-full bg-stone-950 text-stone-200 font-sans antialiased">
      <style>{`
        @keyframes rise { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: none; } }
        .rise { animation: rise .35s cubic-bezier(.2,.7,.2,1) both; }
        @media (prefers-reduced-motion: reduce) { .rise { animation: none; } }
        textarea:focus { outline: none; }
      `}</style>

      <div className="mx-auto max-w-3xl px-4 pb-24 pt-6 sm:pt-10">
        <Header />
        {screen === "home" && (
          <Home
            mastered={mastered}
            onOpen={(id) => { setActiveDeckId(id); setScreen("study"); }}
          />
        )}
        {screen === "study" && activeDeck && (
          <Study
            deck={activeDeck}
            mastered={mastered}
            onMaster={markMastered}
            onReset={() => resetDeck(activeDeck.id)}
            onBack={() => setScreen("home")}
          />
        )}
      </div>
    </div>
  );
}

/* ------------------------------------------------------------------ Header */

function Header() {
  return (
    <header className="mb-8 flex items-baseline justify-between border-b border-stone-800 pb-4">
      <div>
        <h1 className="font-serif text-2xl font-semibold tracking-tight text-stone-100 sm:text-3xl">
          Revature QC Prep
        </h1>
        <p className="mt-1 text-sm text-stone-400">Java + SQL · write it, reveal it, grade it.</p>
      </div>
      <span className="hidden rounded-full bg-amber-400/10 px-3 py-1 text-xs font-medium text-amber-300 ring-1 ring-amber-400/30 sm:inline-block">
        Active recall
      </span>
    </header>
  );
}

/* ------------------------------------------------------------------ Home */

function Home({ mastered, onOpen }) {
  const totals = useMemo(() => {
    let total = 0, done = 0;
    for (const d of DECKS) {
      total += d.cards.length;
      for (let i = 0; i < d.cards.length; i++) if (mastered[cardKey(d.id, i)]) done++;
    }
    return { total, done };
  }, [mastered]);

  const conceptDecks = DECKS.filter((d) => d.kind === "concept");
  const codeDecks = DECKS.filter((d) => d.kind === "code");

  return (
    <div className="rise">
      <div className="mb-8 rounded-2xl border border-stone-800 bg-stone-900/60 p-5">
        <div className="flex items-center justify-between text-sm">
          <span className="text-stone-400">Overall mastered</span>
          <span className="font-mono text-stone-200">{totals.done} / {totals.total}</span>
        </div>
        <div className="mt-3 h-2 w-full overflow-hidden rounded-full bg-stone-800">
          <div
            className="h-full rounded-full bg-amber-400 transition-all duration-500"
            style={{ width: `${totals.total ? (totals.done / totals.total) * 100 : 0}%` }}
          />
        </div>
        <p className="mt-3 text-xs text-stone-500">
          Type your answer before revealing. Cards you miss come back until you master them.
        </p>
      </div>

      <SectionLabel>Concepts</SectionLabel>
      <div className="mb-8 grid gap-3 sm:grid-cols-2">
        {conceptDecks.map((d) => (
          <DeckCard key={d.id} deck={d} mastered={mastered} onOpen={onOpen} />
        ))}
      </div>

      <SectionLabel>Coding</SectionLabel>
      <div className="grid gap-3 sm:grid-cols-2">
        {codeDecks.map((d) => (
          <DeckCard key={d.id} deck={d} mastered={mastered} onOpen={onOpen} />
        ))}
      </div>
    </div>
  );
}

function SectionLabel({ children }) {
  return (
    <div className="mb-3 flex items-center gap-3">
      <span className="text-xs font-semibold uppercase tracking-widest text-stone-500">{children}</span>
      <span className="h-px flex-1 bg-stone-800" />
    </div>
  );
}

function DeckCard({ deck, mastered, onOpen }) {
  const t = THEME[deck.color];
  const Icon = deck.icon || Layers;
  const done = useMemo(() => {
    let n = 0;
    for (let i = 0; i < deck.cards.length; i++) if (mastered[cardKey(deck.id, i)]) n++;
    return n;
  }, [deck, mastered]);
  const pct = deck.cards.length ? (done / deck.cards.length) * 100 : 0;
  const complete = done === deck.cards.length && deck.cards.length > 0;

  return (
    <button
      onClick={() => onOpen(deck.id)}
      className={`group flex flex-col rounded-2xl border border-stone-800 bg-stone-900/60 p-4 text-left transition hover:border-stone-700 hover:bg-stone-900 focus:outline-none focus-visible:ring-2 ${t.ring}`}
    >
      <div className="flex items-start justify-between">
        <div className={`flex h-9 w-9 items-center justify-center rounded-lg ${t.soft}`}>
          <Icon className={`h-5 w-5 ${t.text}`} strokeWidth={1.8} />
        </div>
        {complete
          ? <Trophy className="h-5 w-5 text-amber-300" strokeWidth={1.8} />
          : <ChevronRight className="h-5 w-5 text-stone-600 transition group-hover:translate-x-0.5 group-hover:text-stone-400" />}
      </div>
      <h3 className="mt-3 font-medium leading-snug text-stone-100">{deck.name}</h3>
      <div className="mt-3 flex items-center gap-2 text-xs text-stone-500">
        <span className="font-mono">{done}/{deck.cards.length}</span>
        <div className="h-1.5 flex-1 overflow-hidden rounded-full bg-stone-800">
          <div className={`h-full rounded-full ${t.bar} transition-all duration-500`} style={{ width: `${pct}%` }} />
        </div>
      </div>
    </button>
  );
}

/* ------------------------------------------------------------------ Study */

function Study({ deck, mastered, onMaster, onReset, onBack }) {
  const t = THEME[deck.color];

  // Build the working queue: all indices not yet mastered.
  const buildQueue = useCallback(() => {
    const remaining = [];
    for (let i = 0; i < deck.cards.length; i++) {
      if (!mastered[cardKey(deck.id, i)]) remaining.push(i);
    }
    return remaining;
  }, [deck, mastered]);

  const [queue, setQueue] = useState(() => buildQueue());
  const [pos, setPos] = useState(0);
  const [revealed, setRevealed] = useState(false);
  const [answer, setAnswer] = useState("");
  const taRef = useRef(null);

  // Reset when switching decks.
  useEffect(() => {
    setQueue(buildQueue());
    setPos(0);
    setRevealed(false);
    setAnswer("");
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [deck.id]);

  const masteredCount = deck.cards.length - buildQueue().length;
  const doneWithQueue = pos >= queue.length;
  const currentIdx = doneWithQueue ? null : queue[pos];
  const card = currentIdx == null ? null : deck.cards[currentIdx];

  const advance = useCallback(() => {
    setRevealed(false);
    setAnswer("");
    setPos((p) => p + 1);
    setTimeout(() => taRef.current && taRef.current.focus(), 60);
  }, []);

  const gotIt = useCallback(() => {
    if (currentIdx == null) return;
    onMaster(deck.id, currentIdx, true);
    advance();
  }, [currentIdx, deck.id, onMaster, advance]);

  const reviewAgain = useCallback(() => {
    if (currentIdx == null) return;
    // push this card to the end of the queue so it recirculates
    setQueue((q) => [...q, currentIdx]);
    advance();
  }, [currentIdx, advance]);

  const restart = useCallback(() => {
    onReset();
    setTimeout(() => {
      // rebuild from a clean slate (all cards)
      const all = deck.cards.map((_, i) => i);
      setQueue(all);
      setPos(0);
      setRevealed(false);
      setAnswer("");
    }, 0);
  }, [deck, onReset]);

  const shuffleQueue = useCallback(() => {
    const rest = queue.slice(pos);
    setQueue([...queue.slice(0, pos), ...shuffle(rest)]);
    setRevealed(false);
    setAnswer("");
  }, [queue, pos]);

  // keyboard: Ctrl/Cmd+Enter reveals; when revealed, ArrowRight = Got it
  const onKeyDown = (e) => {
    if ((e.ctrlKey || e.metaKey) && e.key === "Enter") {
      e.preventDefault();
      if (!revealed) setRevealed(true);
    }
  };

  return (
    <div className="rise">
      {/* top bar */}
      <div className="mb-5 flex flex-wrap items-center justify-between gap-3">
        <button
          onClick={onBack}
          className="flex items-center gap-1.5 rounded-lg px-2 py-1.5 text-sm text-stone-400 transition hover:bg-stone-900 hover:text-stone-200"
        >
          <ArrowLeft className="h-4 w-4" /> All decks
        </button>
        <div className="flex items-center gap-2">
          <button onClick={shuffleQueue} title="Shuffle remaining"
            className="flex items-center gap-1.5 rounded-lg border border-stone-800 px-2.5 py-1.5 text-xs text-stone-400 transition hover:bg-stone-900 hover:text-stone-200">
            <Shuffle className="h-3.5 w-3.5" /> Shuffle
          </button>
          <button onClick={restart} title="Reset progress for this deck"
            className="flex items-center gap-1.5 rounded-lg border border-stone-800 px-2.5 py-1.5 text-xs text-stone-400 transition hover:bg-stone-900 hover:text-stone-200">
            <RotateCcw className="h-3.5 w-3.5" /> Reset
          </button>
        </div>
      </div>

      {/* deck header + progress */}
      <div className="mb-5">
        <div className="flex items-center gap-2">
          <span className={`h-2.5 w-2.5 rounded-full ${t.dot}`} />
          <h2 className="font-serif text-xl font-semibold text-stone-100">{deck.name}</h2>
        </div>
        <div className="mt-3 flex items-center gap-3 text-xs text-stone-500">
          <span className="font-mono">{masteredCount}/{deck.cards.length} mastered</span>
          <div className="h-1.5 flex-1 overflow-hidden rounded-full bg-stone-800">
            <div className={`h-full rounded-full ${t.bar} transition-all duration-500`}
              style={{ width: `${deck.cards.length ? (masteredCount / deck.cards.length) * 100 : 0}%` }} />
          </div>
          {!doneWithQueue && <span className="font-mono">card {pos + 1} of {queue.length}</span>}
        </div>
      </div>

      {deck.note && !doneWithQueue && (
        <div className="mb-4 rounded-xl border border-stone-800 bg-stone-900/40 px-4 py-3 text-xs leading-relaxed text-stone-400 font-mono">
          {deck.note}
        </div>
      )}

      {doneWithQueue ? (
        <DeckComplete deck={deck} onRestart={restart} onBack={onBack} />
      ) : (
        <Card
          key={`${deck.id}-${pos}`}
          deck={deck}
          card={card}
          revealed={revealed}
          answer={answer}
          setAnswer={setAnswer}
          onReveal={() => setRevealed(true)}
          onGotIt={gotIt}
          onReview={reviewAgain}
          taRef={taRef}
          onKeyDown={onKeyDown}
        />
      )}

      <p className="mt-6 flex items-center justify-center gap-1.5 text-xs text-stone-600">
        <Keyboard className="h-3.5 w-3.5" /> Ctrl/⌘ + Enter to reveal
      </p>
    </div>
  );
}

/* ------------------------------------------------------------------ Card */

function Card({ deck, card, revealed, answer, setAnswer, onReveal, onGotIt, onReview, taRef, onKeyDown }) {
  const t = THEME[deck.color];
  const isCode = deck.kind === "code";

  const keywordHits = useMemo(() => {
    if (!card.keywords) return [];
    const lower = answer.toLowerCase();
    return card.keywords.map((k) => ({ word: k, hit: lower.includes(k.toLowerCase()) }));
  }, [card, answer]);

  const hitCount = keywordHits.filter((k) => k.hit).length;

  return (
    <div className="rise">
      {/* Question / front — index-card feel */}
      <div className="relative overflow-hidden rounded-2xl bg-stone-100 p-6 text-stone-900 shadow-xl sm:p-8">
        <span className={`absolute left-0 top-0 h-full w-1.5 ${t.bar}`} />
        <div className="mb-3 flex items-center gap-2">
          <span className={`h-2 w-2 rounded-full ${t.dot}`} />
          <span className="text-[11px] font-semibold uppercase tracking-widest text-stone-500">
            {isCode ? `Write ${deck.lang.toUpperCase()}` : "Question"}
          </span>
        </div>
        <p className={`${isCode ? "font-sans" : "font-serif"} text-lg font-medium leading-snug text-stone-900 sm:text-xl`}>
          {card.q}
        </p>
      </div>

      {/* Answer input */}
      <div className="mt-4">
        <textarea
          ref={taRef}
          value={answer}
          onChange={(e) => setAnswer(e.target.value)}
          onKeyDown={onKeyDown}
          placeholder={isCode ? "// write your solution here…" : "Type your answer from memory…"}
          spellCheck={!isCode}
          className={`min-h-[120px] w-full resize-y rounded-xl border border-stone-800 bg-stone-900/70 p-4 text-sm text-stone-200 placeholder:text-stone-600 focus:border-stone-600 ${isCode ? "font-mono" : "font-sans leading-relaxed"}`}
        />
      </div>

      {!revealed ? (
        <button
          onClick={onReveal}
          className={`mt-3 flex w-full items-center justify-center gap-2 rounded-xl bg-stone-100 px-4 py-3 text-sm font-semibold text-stone-900 transition hover:bg-white focus:outline-none focus-visible:ring-2 ${t.ring}`}
        >
          <Eye className="h-4 w-4" /> Reveal answer
        </button>
      ) : (
        <div className="mt-4 rise">
          {/* keyword self-check (concept only) */}
          {card.keywords && (
            <div className="mb-3 rounded-xl border border-stone-800 bg-stone-900/50 p-4">
              <div className="mb-2 flex items-center justify-between">
                <span className="text-xs font-semibold uppercase tracking-widest text-stone-500">Keyword check</span>
                <span className="font-mono text-xs text-stone-400">{hitCount}/{card.keywords.length} hit</span>
              </div>
              <div className="flex flex-wrap gap-1.5">
                {keywordHits.map((k) => (
                  <span
                    key={k.word}
                    className={`rounded-md px-2 py-1 text-xs font-medium ${
                      k.hit
                        ? "bg-emerald-400/15 text-emerald-300 ring-1 ring-emerald-400/30"
                        : "bg-stone-800 text-stone-500 ring-1 ring-stone-700"
                    }`}
                  >
                    {k.hit ? "✓ " : ""}{k.word}
                  </span>
                ))}
              </div>
              <p className="mt-2 text-[11px] text-stone-600">Green = you mentioned it. Grey = missed — worth working into your answer.</p>
            </div>
          )}

          {/* model answer */}
          <div className="overflow-hidden rounded-2xl bg-stone-100 text-stone-900 shadow-lg">
            <div className="flex items-center gap-2 border-b border-stone-300 px-5 py-2.5">
              <span className={`h-2 w-2 rounded-full ${t.dot}`} />
              <span className="text-[11px] font-semibold uppercase tracking-widest text-stone-500">
                {isCode ? "Reference solution" : "Model answer"}
              </span>
            </div>
            {isCode ? (
              <pre className="overflow-x-auto bg-stone-900 p-5 text-[13px] leading-relaxed text-stone-100">
                <code className="font-mono">{card.a}</code>
              </pre>
            ) : (
              <p className="whitespace-pre-wrap p-5 text-[15px] leading-relaxed text-stone-800">{card.a}</p>
            )}
          </div>

          {/* grade */}
          <div className="mt-4 grid grid-cols-2 gap-3">
            <button
              onClick={onReview}
              className="flex items-center justify-center gap-2 rounded-xl border border-rose-400/30 bg-rose-400/10 px-4 py-3 text-sm font-semibold text-rose-300 transition hover:bg-rose-400/20 focus:outline-none focus-visible:ring-2 focus-visible:ring-rose-400/40"
            >
              <RefreshCw className="h-4 w-4" /> Review again
            </button>
            <button
              onClick={onGotIt}
              className="flex items-center justify-center gap-2 rounded-xl border border-emerald-400/30 bg-emerald-400/10 px-4 py-3 text-sm font-semibold text-emerald-300 transition hover:bg-emerald-400/20 focus:outline-none focus-visible:ring-2 focus-visible:ring-emerald-400/40"
            >
              <Check className="h-4 w-4" /> Got it
            </button>
          </div>
          <p className="mt-2 text-center text-[11px] text-stone-600">
            “Review again” sends this card to the back of the stack. “Got it” marks it mastered.
          </p>
        </div>
      )}
    </div>
  );
}

/* ------------------------------------------------------------------ Complete */

function DeckComplete({ deck, onRestart, onBack }) {
  const t = THEME[deck.color];
  return (
    <div className="rise rounded-2xl border border-stone-800 bg-stone-900/60 p-8 text-center">
      <div className={`mx-auto mb-4 flex h-14 w-14 items-center justify-center rounded-full ${t.soft}`}>
        <Trophy className={`h-7 w-7 ${t.text}`} strokeWidth={1.8} />
      </div>
      <h3 className="font-serif text-xl font-semibold text-stone-100">Deck cleared</h3>
      <p className="mx-auto mt-2 max-w-sm text-sm text-stone-400">
        You worked through every card in <span className="text-stone-200">{deck.name}</span>. Run it again
        cold — no notes — to prove it sticks under interview pressure.
      </p>
      <div className="mt-6 flex justify-center gap-3">
        <button
          onClick={onRestart}
          className="flex items-center gap-2 rounded-xl bg-stone-100 px-5 py-2.5 text-sm font-semibold text-stone-900 transition hover:bg-white"
        >
          <RotateCcw className="h-4 w-4" /> Run it again
        </button>
        <button
          onClick={onBack}
          className="flex items-center gap-2 rounded-xl border border-stone-800 px-5 py-2.5 text-sm font-medium text-stone-300 transition hover:bg-stone-900"
        >
          <ArrowLeft className="h-4 w-4" /> All decks
        </button>
      </div>
    </div>
  );
}
