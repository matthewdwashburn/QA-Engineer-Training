import React, { useState, useMemo, useEffect, useRef, useCallback } from "react";
import {
  ArrowLeft, ArrowRight, Check, X, RotateCcw, Eye, RefreshCw,
  Layers, ListChecks, Trophy, BookOpen, Keyboard, ChevronRight,
  CircleDot, CheckCircle2, Coffee, Database
} from "lucide-react";

/* =========================================================================
   QC — COMPLETE REVIEW  (Java + SQL)
   Every concept from the full notes, drilled two ways:
     • Flashcards     — write-first recall, keyword self-check, mastery loop
     • Multiple choice — with a why-it's-right explanation on every question
   Pick a mode up top, then a deck.
   ========================================================================= */

const THEME = {
  amber:   { bar: "bg-amber-400",   dot: "bg-amber-400",   text: "text-amber-300",   soft: "bg-amber-400/10",   ring: "ring-amber-400/40" },
  rose:    { bar: "bg-rose-400",    dot: "bg-rose-400",    text: "text-rose-300",    soft: "bg-rose-400/10",    ring: "ring-rose-400/40" },
  emerald: { bar: "bg-emerald-400", dot: "bg-emerald-400", text: "text-emerald-300", soft: "bg-emerald-400/10", ring: "ring-emerald-400/40" },
  fuchsia: { bar: "bg-fuchsia-400", dot: "bg-fuchsia-400", text: "text-fuchsia-300", soft: "bg-fuchsia-400/10", ring: "ring-fuchsia-400/40" },
  orange:  { bar: "bg-orange-400",  dot: "bg-orange-400",  text: "text-orange-300",  soft: "bg-orange-400/10",  ring: "ring-orange-400/40" },
  sky:     { bar: "bg-sky-400",     dot: "bg-sky-400",     text: "text-sky-300",     soft: "bg-sky-400/10",     ring: "ring-sky-400/40" },
  cyan:    { bar: "bg-cyan-400",    dot: "bg-cyan-400",    text: "text-cyan-300",    soft: "bg-cyan-400/10",    ring: "ring-cyan-400/40" },
  violet:  { bar: "bg-violet-400",  dot: "bg-violet-400",  text: "text-violet-300",  soft: "bg-violet-400/10",  ring: "ring-violet-400/40" },
};

/* ============================================================ DECK DATA == */

const DECKS = [
  /* ================= JAVA — CORE & OOP ================= */
  {
    id: "java-core", track: "Java", name: "Core & OOP Foundations", color: "amber", icon: Coffee,
    cards: [
      { q: "How does Java achieve 'write once, run anywhere', and what does 'strongly typed' mean?", a: "javac compiles source to platform-neutral bytecode that runs on the JVM; any OS with a JVM runs the same bytecode. Strongly typed means every variable has a declared type checked at compile time, catching type errors before runtime.", keywords: ["bytecode", "JVM", "run anywhere", "compile time"] },
      { q: "Name the four pillars of OOP.", a: "Abstraction (expose what, hide how), Inheritance (subclass reuses a superclass), Polymorphism (one interface, many behaviors), Encapsulation (hide state behind controlled access).", keywords: ["Abstraction", "Inheritance", "Polymorphism", "Encapsulation"] },
      { q: "What lives on the stack vs the heap?", a: "The stack stores local variables and method-call data (frames), cleaned up when the method returns. The heap stores objects/instances created with new, cleaned by the garbage collector. Stack references point to heap objects.", keywords: ["local variables", "method call", "heap", "objects", "reference"] },
      { q: "Class vs object?", a: "A class is a blueprint (fields, methods, constructors). An object is a runtime instance of that class, created with new and stored on the heap; variables hold references to it.", keywords: ["blueprint", "instance", "new", "heap", "reference"] },
      { q: "What is a constructor? Return type? What if you declare none?", a: "A special method that runs when an instance is created, initializing fields from its params. It has no return type — not even void. If you declare no constructor, Java provides a default no-arg one; declaring any removes that freebie.", keywords: ["initialize", "new", "no return type", "default no-arg"] },
      { q: "List the four access modifiers and what default means.", a: "public (everywhere), protected (same package + subclasses), default/package-private (same package only, no keyword), private (declaring class only). Default = package-private.", keywords: ["public", "protected", "package-private", "private"] },
      { q: "What are non-access modifiers, and what do static and final each do?", a: "Non-access modifiers control behavior (static, final, abstract, synchronized…). static = belongs to the class, one shared copy, usable without an object. final = variable can't be reassigned, method can't be overridden, class can't be extended.", keywords: ["non-access", "static", "final", "reassign", "override", "extend"] },
      { q: "Why is the main method static?", a: "At startup no objects exist yet, so making main static lets the JVM call it directly on the class without creating an instance. Signature: public static void main(String[] args).", keywords: ["JVM", "no instance", "class", "entry point"] },
      { q: "What is encapsulation and how is it implemented?", a: "Hiding internal state (private fields) and exposing controlled behavior through methods (getters/setters with validation), so callers use a stable API while internals can change.", keywords: ["private", "getters", "setters", "controlled", "hide state"] },
    ],
    mcq: [
      { q: "Which Java feature lets it run on any platform?", options: ["Platform-specific compilation", "Write once, run anywhere (bytecode + JVM)", "Direct hardware access", "Static linking per OS"], correct: [1], why: "Java compiles to bytecode that any JVM executes, so the same artifact runs on any OS." },
      { q: "static and final are examples of:", options: ["Access modifiers", "Non-access modifiers", "Data types", "Return types"], correct: [1], why: "Access modifiers control visibility (public/protected/private/default). static and final control behavior — they're non-access modifiers." },
      { q: "What is a constructor's return type?", options: ["void", "The class type", "No return type at all", "Object"], correct: [2], why: "A constructor declares no return type. Writing 'void ClassName()' makes it an ordinary method, not a constructor." },
      { q: "If you declare no constructor in a class:", options: ["The class won't compile", "Java provides a default no-arg constructor", "You can't create objects", "A copy constructor is generated"], correct: [1], why: "Java supplies a default no-arg constructor — but only if you declare none yourself." },
      { q: "Which stores objects created with new?", options: ["The stack", "The heap", "The method area only", "Registers"], correct: [1], why: "Objects live on the heap; the stack holds local variables and method-call frames, including references to those heap objects." },
      { q: "The default (no keyword) access level means visible:", options: ["everywhere", "same package only", "subclasses only", "same class only"], correct: [1], why: "Default = package-private: visible only to types in the same package." },
      { q: "Why must main be static?", options: ["So it can be overridden", "So the JVM can call it without an instance", "So it runs faster", "Because it must be final"], correct: [1], why: "No objects exist at startup, so static lets the JVM invoke main directly on the class." },
    ],
  },

  /* ================= JAVA — POLYMORPHISM & TYPES ================= */
  {
    id: "java-poly", track: "Java", name: "Polymorphism, Inheritance & Types", color: "rose", icon: Coffee,
    cards: [
      { q: "Overloading vs overriding — parameters and when each is resolved?", a: "Overloading = same name, DIFFERENT parameters, same class, resolved at compile time (static binding). Overriding = subclass method with the SAME signature, resolved at runtime via dynamic dispatch on the actual object. Mnemonic: overRIDE → RUNtime.", keywords: ["different params", "same signature", "compile time", "runtime", "dispatch"] },
      { q: "Can static methods be overridden?", a: "No. A same-signature static method in a subclass HIDES the parent's (method hiding); which runs is decided by the reference type at compile time, not the object. Static methods can be overloaded, not overridden.", keywords: ["hiding", "reference type", "compile time", "not overridden"] },
      { q: "Can constructors be overloaded or overridden?", a: "Overloaded yes (different parameter lists), overridden no — constructors aren't inherited, so overriding doesn't apply.", keywords: ["overloaded", "not inherited", "not overridden"] },
      { q: "Does Java support multiple inheritance? How do you get multiple types?", a: "A class extends exactly one class (single class inheritance). You get multiple inheritance of type by implementing many interfaces.", keywords: ["single", "extends one", "implement many", "interfaces"] },
      { q: "Abstract class vs interface (pre-Java 8)?", a: "Abstract class: can't be instantiated; has state, constructors, and both abstract and concrete methods. Interface (pre-8): only method signatures — no implementations. A class extends one class but implements many interfaces.", keywords: ["implementations", "state", "constructors", "multiple"] },
      { q: "What can interfaces contain since Java 8+?", a: "default methods and static methods (with bodies), and private helper methods (Java 9+). Interfaces still have no constructors or instance state.", keywords: ["default", "static", "private", "no state"] },
      { q: "== vs .equals()?", a: "== compares references (addresses) and works directly for primitives. .equals() compares logical value when overridden. Default Object.equals behaves like == until overridden.", keywords: ["references", "primitives", "value", "override"] },
      { q: "Why must you override hashCode when you override equals?", a: "Hash collections use hashCode to pick a bucket, then equals to resolve collisions. The contract requires equal objects to have equal hash codes; break it and lookups fail. Implement both from the same fields.", keywords: ["contract", "bucket", "collision", "equal hash"] },
      { q: "Are Strings mutable? What is the string pool?", a: "Strings are immutable — every change returns a new String. Java interns literals in the string pool, so identical literals share one object; new String() forces a distinct object. Compare content with .equals().", keywords: ["immutable", "pool", "interned", "new String"] },
      { q: "What are wrapper classes?", a: "Object versions of primitives (int→Integer, double→Double, boolean→Boolean, char→Character, etc.). They enable primitives in generics/collections and provide utilities; autoboxing converts automatically.", keywords: ["primitives", "Integer", "collections", "autoboxing"] },
    ],
    mcq: [
      { q: "Method overriding is resolved at:", options: ["Compile time", "Runtime", "Class loading", "Linking"], correct: [1], why: "Overriding uses dynamic dispatch — chosen at runtime by the actual object type. Overloading is the compile-time one." },
      { q: "Overloading requires methods with:", options: ["the same signature", "different parameter lists", "different return types only", "the @Override annotation"], correct: [1], why: "Overloading = same name, different parameters. Overriding requires the SAME signature." },
      { q: "Can a static method be overridden?", options: ["Yes, like any method", "No — a same-signature static hides it (compile-time, by reference type)", "Only if final", "Only in interfaces"], correct: [1], why: "Static methods belong to the class; a matching subclass static hides rather than overrides." },
      { q: "Can constructors be overridden?", options: ["Yes with @Override", "No — they aren't inherited (they can be overloaded)", "Only default ones", "Only in abstract classes"], correct: [1], why: "Constructors aren't inherited, so overriding doesn't apply; overloading does." },
      { q: "Primary difference between abstract classes and interfaces (pre-Java 8)?", options: ["Interfaces can have constructors", "Abstract classes support multiple inheritance", "Interfaces cannot have method implementations", "Abstract classes can't define constants"], correct: [2], why: "Pre-8 interfaces held only signatures. Abstract classes do NOT support multiple inheritance." },
      { q: "In Java, a class can:", options: ["extend multiple classes", "implement multiple interfaces", "extend multiple interfaces", "implement multiple classes"], correct: [1], why: "Single class inheritance (one extends), but many interfaces via implements." },
      { q: "== on two object references tests:", options: ["logical value", "identity (same object)", "hash code", "class type"], correct: [1], why: "== compares references/identity; .equals() compares logical value when overridden." },
      { q: "If you override equals but not hashCode, using the object as a HashMap key:", options: ["works fine", "can fail lookups because the contract is broken", "throws a compile error", "forces TreeMap"], correct: [1], why: "Equal objects must have equal hash codes; breaking it sends equal keys to different buckets and lookups miss." },
    ],
  },

  /* ================= JAVA — COLLECTIONS & DATA STRUCTURES ================= */
  {
    id: "java-collections", track: "Java", name: "Collections & Data Structures", color: "emerald", icon: Layers,
    cards: [
      { q: "Name the core collection interfaces and a couple of implementations of each.", a: "List (ordered, indexed, dupes): ArrayList, LinkedList. Set (unique): HashSet, LinkedHashSet, TreeSet. Queue/Deque: ArrayDeque, PriorityQueue. Map (key→value): HashMap, LinkedHashMap, TreeMap.", keywords: ["List", "Set", "Queue", "Map", "ArrayList", "HashMap"] },
      { q: "List vs Set?", a: "List is ordered, indexed, and allows duplicates. Set stores unique elements and is about membership (HashSet unordered, LinkedHashSet insertion order, TreeSet sorted).", keywords: ["ordered", "indexed", "duplicates", "unique", "membership"] },
      { q: "ArrayList vs LinkedList — when each?", a: "ArrayList is the default: get(i)/set(i) are O(1) with good locality. LinkedList is better for frequent insert/delete at the ends (O(1)); its get(i) is O(n).", keywords: ["random access", "O(1)", "O(n)", "insert", "ends"] },
      { q: "Singly vs doubly linked list?", a: "A linked list stores nodes not in contiguous memory, linked by pointers, with dynamic size. Singly = only a next pointer. Doubly = next and prev pointers (Java's LinkedList is doubly linked).", keywords: ["nodes", "pointers", "next", "prev", "dynamic"] },
      { q: "Stack vs queue vs deque?", a: "Stack = LIFO (used for parenthesis matching, undo/redo). Queue = FIFO (enqueue at back, dequeue at front). Deque = double-ended; add/remove at both front and back.", keywords: ["LIFO", "FIFO", "enqueue", "dequeue", "both ends"] },
      { q: "ArrayDeque as a stack: what does push(4); pop() print?", a: "ArrayDeque implements Deque; push and pop act on the head → LIFO. push(4) then pop() returns and prints 4. No error. It's the recommended modern stack over the legacy Stack class.", keywords: ["LIFO", "head", "4", "Deque"] },
      { q: "What happens with a duplicate key in a Map? A duplicate value? A duplicate in a Set?", a: "Duplicate key: put overwrites and returns the old value (keys stay unique). Duplicate value: allowed. Duplicate in a Set: add returns false, set unchanged.", keywords: ["overwrite", "returns old", "allowed", "false"] },
      { q: "Which collection is sorted, and by what?", a: "TreeSet (and TreeMap) — sorted by natural ordering (Comparable) or a supplied Comparator, O(log n) per operation. HashSet is fast but unordered.", keywords: ["TreeSet", "Comparable", "Comparator", "O(log n)"] },
      { q: "HashMap vs Hashtable, and null handling?", a: "HashMap is unsynchronized, allows one null key and multiple null values, and is the default. Hashtable is legacy, synchronized on every method, and disallows nulls. Prefer ConcurrentHashMap for concurrency.", keywords: ["unsynchronized", "null key", "legacy", "ConcurrentHashMap"] },
    ],
    mcq: [
      { q: "Which collection guarantees sorted order by natural ordering or a comparator?", options: ["HashSet", "TreeSet", "LinkedList", "ArrayList"], correct: [1], why: "TreeSet is tree-backed and iterates in sorted order. ArrayList keeps insertion order." },
      { q: "Predict output:\n\nArrayDeque<Integer> ad = new ArrayDeque<>();\nad.push(4);\nSystem.out.println(ad.pop());", options: ["4", "Compile-time Error", "Run Time Error", "null"], correct: [0], why: "push/pop on a Deque are LIFO stack ops on the head; pop() returns 4." },
      { q: "Adding a duplicate value to a Set:", options: ["throws an exception", "returns false and leaves the set unchanged", "overwrites the element", "creates a second copy"], correct: [1], why: "Set.add returns false when the element already exists; the set is unchanged." },
      { q: "Putting an existing key into a Map:", options: ["adds a duplicate key", "overwrites the value and returns the previous one", "throws", "is ignored"], correct: [1], why: "Keys are unique; put overwrites and returns the old value." },
      { q: "Which is doubly linked in the JDK?", options: ["ArrayList", "LinkedList", "HashSet", "ArrayDeque"], correct: [1], why: "java.util.LinkedList is implemented as a doubly linked list (next + prev pointers)." },
      { q: "A stack follows which ordering?", options: ["FIFO", "LIFO", "sorted", "random"], correct: [1], why: "Stack = Last-In-First-Out. A queue is FIFO." },
      { q: "HashMap (vs Hashtable) allows:", options: ["no nulls", "one null key and multiple null values", "only null values", "synchronized access by default"], correct: [1], why: "HashMap is unsynchronized and permits one null key + null values; Hashtable is legacy, synchronized, and null-hostile." },
      { q: "Which allows duplicates AND index access?", options: ["HashSet", "TreeSet", "ArrayList", "HashMap"], correct: [2], why: "ArrayList is ordered, indexed, and allows duplicates." },
    ],
  },

  /* ================= JAVA — CONCURRENCY, JAVA 8 & PATTERNS ================= */
  {
    id: "java-advanced", track: "Java", name: "Concurrency, Java 8 & Patterns", color: "fuchsia", icon: Coffee,
    cards: [
      { q: "What is multithreading and how do you create a thread?", a: "Running multiple paths of execution concurrently in one program. Create a thread by extending Thread and overriding run(), or by implementing Runnable and passing it to a Thread. Call start() (not run()) to launch it.", keywords: ["concurrent", "Thread", "Runnable", "start"] },
      { q: "Give the thread lifecycle states.", a: "NEW → RUNNABLE → (BLOCKED / WAITING / TIMED_WAITING) → TERMINATED. BLOCKED waits for a lock; WAITING/TIMED_WAITING come from wait()/join()/sleep(); TERMINATED can't restart.", keywords: ["NEW", "RUNNABLE", "BLOCKED", "WAITING", "TERMINATED"] },
      { q: "What is deadlock, and what does synchronized do?", a: "Deadlock: two or more threads each hold a lock the other needs, so none proceeds. synchronized lets only one thread at a time enter a method/block via the monitor lock, preventing race conditions on shared state.", keywords: ["locks", "waiting", "one thread", "monitor"] },
      { q: "What causes a StackOverflowError and how do you avoid it?", a: "Too many nested/recursive calls exhaust the thread's stack. Ensure a base case that's actually reached with arguments progressing toward it; for very deep recursion, use a loop instead.", keywords: ["recursive", "stack", "base case", "loop"] },
      { q: "What is the Reflection API?", a: "It lets a program inspect and manipulate classes, methods, and fields at runtime via the Class object (getClass(), getMethods(), getFields()). Frameworks use it for DI, serialization, ORMs.", keywords: ["runtime", "Class", "getMethods", "inspect"] },
      { q: "Describe the Singleton and Factory patterns.", a: "Singleton: only one instance, via a private constructor + static accessor returning the same object. Factory: abstracts instantiation behind a method that decides which object to create.", keywords: ["one instance", "private constructor", "Factory", "instantiation"] },
      { q: "What is a functional interface? Name a few.", a: "An interface with exactly one abstract method (SAM), so a lambda can implement it. Function<T,R> (apply), Consumer<T> (accept), Predicate<T> (test), Supplier<T> (get).", keywords: ["one abstract method", "SAM", "Function", "Consumer", "Predicate"] },
      { q: "What is a lambda?", a: "An anonymous function — a concise implementation of a functional interface's single method without writing a full class. Syntax: (args) -> expression or { statements }.", keywords: ["anonymous", "functional interface", "->", "concise"] },
      { q: "Streams: intermediate vs terminal operations?", a: "Streams process collections functionally without mutating the source, and are lazy. Intermediate ops return a Stream and are lazy (filter, map, sorted). Terminal ops trigger execution (collect, forEach, count, reduce, findFirst). Nothing runs until a terminal op.", keywords: ["lazy", "intermediate", "terminal", "filter", "collect"] },
      { q: "Why use a logging library, and what are Logback's levels?", a: "It lets you set severity thresholds and route output, turning logging up/down without code changes. Logback levels: TRACE < DEBUG < INFO < WARN < ERROR.", keywords: ["thresholds", "TRACE", "DEBUG", "INFO", "WARN", "ERROR"] },
    ],
    mcq: [
      { q: "Which launches a new thread of execution?", options: ["run()", "start()", "execute()", "begin()"], correct: [1], why: "Call start(); calling run() directly just runs it on the current thread." },
      { q: "The thread lifecycle is:", options: ["NEW → RUNNABLE → BLOCKED/WAITING → TERMINATED", "START → RUN → STOP", "OPEN → ACTIVE → CLOSED", "INIT → EXEC → DONE"], correct: [0], why: "Thread.State: NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED." },
      { q: "Deadlock is:", options: ["one thread hogging the CPU", "two+ threads each waiting on a lock the other holds", "a thread that never starts", "an uncaught exception"], correct: [1], why: "Mutual waiting on each other's locks means none can proceed." },
      { q: "A functional interface has:", options: ["no methods", "exactly one abstract method", "only static methods", "at least two abstract methods"], correct: [1], why: "One abstract method (SAM) lets a lambda implement it." },
      { q: "Which is a terminal stream operation?", options: ["filter", "map", "collect", "sorted"], correct: [2], why: "collect triggers execution and produces a result. filter/map/sorted are lazy intermediates." },
      { q: "Consumer<T>'s abstract method is:", options: ["R apply(T t)", "void accept(T t)", "boolean test(T t)", "T get()"], correct: [1], why: "Consumer → accept; Function → apply; Predicate → test; Supplier → get." },
      { q: "Logback log levels in increasing severity:", options: ["DEBUG < TRACE < INFO < WARN < ERROR", "TRACE < DEBUG < INFO < WARN < ERROR", "INFO < DEBUG < WARN < TRACE < ERROR", "TRACE < INFO < DEBUG < ERROR < WARN"], correct: [1], why: "TRACE, DEBUG, INFO, WARN, ERROR — least to most severe." },
      { q: "A StackOverflowError typically comes from:", options: ["allocating too many objects", "recursion with no reachable base case", "too many threads", "a null pointer"], correct: [1], why: "Unbounded recursion exhausts the thread's stack of call frames." },
    ],
  },

  /* ================= JAVA — EXCEPTIONS ================= */
  {
    id: "java-exceptions", track: "Java", name: "Exceptions", color: "orange", icon: Coffee,
    cards: [
      { q: "What is the parent class of all exceptions and errors?", a: "Throwable. Both Exception and Error extend Throwable, so it's the common root of everything you can throw or catch.", keywords: ["Throwable", "Error", "Exception", "root"] },
      { q: "Checked vs unchecked exceptions?", a: "Checked extend Exception (not RuntimeException); the compiler forces catch-or-throws; they model recoverable external failures (IOException, SQLException). Unchecked are RuntimeException/Error and need no throws; they usually signal bugs (NullPointerException).", keywords: ["RuntimeException", "throws", "compiler", "recoverable"] },
      { q: "try / catch / finally — what does each do?", a: "try wraps risky code; catch handles a matching exception (most specific first); finally always runs for cleanup (e.g. closing resources), whether or not an exception occurred.", keywords: ["try", "catch", "finally", "cleanup"] },
      { q: "throw vs throws?", a: "throw actually throws an instance (throw new IllegalArgumentException(...)). throws is a method-signature declaration that the method may throw a checked exception, pushing handling to the caller.", keywords: ["throw instance", "throws declaration", "caller"] },
      { q: "How do you write a custom exception?", a: "Subclass Exception (checked) or RuntimeException (unchecked) and pass a message to super. e.g. class InvalidAgeException extends Exception { public InvalidAgeException(String m){ super(m); } } then throw new InvalidAgeException(\"Invalid age\").", keywords: ["extends Exception", "super", "throw"] },
      { q: "If you're unsure which exception is thrown, how do you catch it?", a: "Catch a broad supertype like Exception (or RuntimeException) as a fallback, or use multi-catch (catch (A | B e)). Prefer the most specific type you expect; use the broad catch to log, not swallow.", keywords: ["Exception", "supertype", "multi-catch", "log"] },
    ],
    mcq: [
      { q: "The parent class of all exceptions is:", options: ["Exception", "Error", "Throwable", "RuntimeException"], correct: [2], why: "Both Exception and Error extend Throwable; Error is not under Exception, so Throwable is the root." },
      { q: "Checked exceptions:", options: ["extend RuntimeException", "must be caught or declared with throws", "are optional to handle", "are Errors"], correct: [1], why: "The compiler enforces handling of checked exceptions via catch or throws." },
      { q: "Which block always runs, exception or not?", options: ["try", "catch", "finally", "throws"], correct: [2], why: "finally always executes — used for cleanup like closing resources." },
      { q: "throws in a method signature:", options: ["throws an exception immediately", "declares the method may throw a checked exception", "catches exceptions", "is the same as throw"], correct: [1], why: "throws declares possible checked exceptions; throw actually raises an instance." },
      { q: "NullPointerException is:", options: ["checked", "unchecked (RuntimeException)", "an Error", "a compile error"], correct: [1], why: "NPE extends RuntimeException, so it's unchecked and needs no throws." },
      { q: "A custom checked exception is created by:", options: ["extending RuntimeException", "extending Exception and calling super(message)", "implementing Throwable", "annotating with @Exception"], correct: [1], why: "Subclass Exception (checked) and pass the message up via super." },
    ],
  },

  /* ================= SQL — FOUNDATIONS & DESIGN ================= */
  {
    id: "sql-foundations", track: "SQL", name: "Foundations & Design", color: "sky", icon: Database,
    cards: [
      { q: "Define SQL, RDBMS, and database.", a: "SQL: the standard language to define, query, and manipulate relational data. RDBMS: software storing data in tables (rows/columns) and enforcing constraints, queried with SQL. Database: an organized, structured collection of data managed by a DBMS.", keywords: ["language", "tables", "constraints", "organized"] },
      { q: "Name the five SQL sublanguages with an example each.", a: "DDL (CREATE/ALTER/DROP), DML (INSERT/UPDATE/DELETE), DQL (SELECT), DCL (GRANT/REVOKE), TCL (COMMIT/ROLLBACK).", keywords: ["DDL", "DML", "DQL", "DCL", "TCL"] },
      { q: "What is CRUD?", a: "Create, Read, Update, Delete — the four basic persistence operations, mapping to INSERT, SELECT, UPDATE, DELETE.", keywords: ["Create", "Read", "Update", "Delete", "INSERT"] },
      { q: "Why use DECIMAL over FLOAT for money?", a: "DECIMAL/NUMERIC is fixed-point and exact. FLOAT/REAL are binary floating-point, introduce rounding errors, and can't specify a fixed length/scale — wrong for currency.", keywords: ["exact", "fixed-point", "rounding", "FLOAT"] },
      { q: "Primary key, foreign key, candidate key?", a: "PK: uniquely identifies each row (implicitly UNIQUE + NOT NULL), one per table. FK: references a PK in another table to enforce a relationship. Candidate key: any minimal set that could be the PK; one is chosen, others are alternate keys.", keywords: ["unique", "not null", "references", "candidate", "alternate"] },
      { q: "What is referential integrity?", a: "Every foreign key value must match an existing primary key in the referenced table, or be null — so there are no orphan references.", keywords: ["foreign key", "match", "orphan", "null"] },
      { q: "List common column constraints.", a: "PRIMARY KEY, FOREIGN KEY, UNIQUE, NOT NULL, CHECK, DEFAULT.", keywords: ["PRIMARY KEY", "FOREIGN KEY", "UNIQUE", "NOT NULL", "CHECK", "DEFAULT"] },
      { q: "Cardinality/multiplicity — give a 1:1, 1:N, and N:N example.", a: "1:1 person↔passport (or doctor↔details). 1:N customer↔orders (patient↔appointments). N:N students↔courses (patients↔doctors) — needs a junction table.", keywords: ["one-to-one", "one-to-many", "many-to-many", "junction"] },
      { q: "What is an ERD?", a: "An Entity Relationship Diagram — a visual model of a schema showing entities (tables), attributes, and the relationships/cardinality between them.", keywords: ["entities", "attributes", "relationships", "cardinality"] },
      { q: "Explain 1NF, 2NF, 3NF.", a: "1NF: atomic values, no repeating groups (no arrays in a cell). 2NF: 1NF + no partial dependency — non-key columns depend on the whole composite key. 3NF: 2NF + no transitive dependency — non-key columns depend only on the key.", keywords: ["atomic", "partial", "whole key", "transitive"] },
      { q: "What is a junction table?", a: "A table implementing a many-to-many relationship: two foreign keys (one per side), usually with a composite primary key of both to prevent duplicate pairings.", keywords: ["many-to-many", "two foreign keys", "composite"] },
    ],
    mcq: [
      { q: "A column marked PRIMARY KEY is implicitly... (select all)", multi: true, options: ["unique", "final", "serial", "not null"], correct: [0, 3], why: "PRIMARY KEY implies UNIQUE + NOT NULL. 'final' is a Java keyword; 'serial' is a Postgres auto-increment type a PK does not imply." },
      { q: "Which type should store money?", options: ["FLOAT", "REAL", "DECIMAL / NUMERIC", "DOUBLE"], correct: [2], why: "DECIMAL is fixed-point and exact; floating types cause rounding errors." },
      { q: "Referential integrity means a foreign key must:", options: ["always be null", "match an existing primary key (or be null)", "be unique", "auto-increment"], correct: [1], why: "FK values must reference a real PK (or be null) — no orphans." },
      { q: "1NF requires:", options: ["no partial dependencies", "atomic values, no repeating groups", "no transitive dependencies", "a composite key"], correct: [1], why: "1NF = atomic cells, one value per cell, no arrays/repeating groups." },
      { q: "A many-to-many relationship is implemented with:", options: ["a single table", "a junction table with two foreign keys", "a view", "a self join"], correct: [1], why: "A junction table holds a FK to each side, often with a composite PK." },
      { q: "Which is NOT a column constraint?", options: ["CHECK", "DEFAULT", "SERIAL", "NOT NULL"], correct: [2], why: "CHECK, DEFAULT, NOT NULL, UNIQUE, PRIMARY/FOREIGN KEY are constraints. SERIAL is an auto-increment data type in Postgres, not a constraint." },
      { q: "3NF eliminates:", options: ["repeating groups", "partial dependencies", "transitive dependencies", "foreign keys"], correct: [2], why: "3NF removes transitive dependencies (non-key depending on another non-key)." },
      { q: "A candidate key is:", options: ["always the primary key", "a minimal set of columns that could uniquely identify a row", "any foreign key", "a composite of all columns"], correct: [1], why: "Candidate keys are the options; one becomes the PK, the rest are alternate keys." },
    ],
  },

  /* ================= SQL — QUERIES, JOINS & FUNCTIONS ================= */
  {
    id: "sql-queries", track: "SQL", name: "Queries, Joins & Functions", color: "cyan", icon: Database,
    cards: [
      { q: "DROP vs TRUNCATE vs DELETE?", a: "DROP removes the whole table object (definition + rows). TRUNCATE removes all rows fast but keeps the table (no rollback, typically). DELETE removes rows (optionally with WHERE), keeps the table, and can be rolled back.", keywords: ["table object", "all rows", "rollback", "WHERE"] },
      { q: "Contrast the join types.", a: "INNER: rows matching on both sides. LEFT: all left rows, unmatched right = NULL. RIGHT: all right rows. FULL OUTER: all rows from either side. CROSS: every pair (Cartesian product). SELF: a table joined to itself.", keywords: ["INNER", "LEFT", "FULL", "CROSS", "SELF", "NULL"] },
      { q: "Set operations — and which keep duplicates?", a: "UNION (rows in either, dedups), UNION ALL (keeps duplicates), INTERSECT (rows in both), EXCEPT/MINUS (rows in first not second). Only UNION ALL keeps duplicates; SELECTs must have compatible columns.", keywords: ["UNION", "UNION ALL", "INTERSECT", "EXCEPT", "duplicates"] },
      { q: "Joins vs set operations?", a: "Joins combine tables horizontally by matching rows on a condition (add columns). Set operations combine result sets vertically (stack rows) and require compatible column counts/types.", keywords: ["horizontal", "vertical", "columns", "rows"] },
      { q: "WHERE vs HAVING?", a: "WHERE filters rows before grouping and can't use aggregates. HAVING filters groups after aggregation and can use aggregates like COUNT/SUM.", keywords: ["before", "after", "aggregate", "groups"] },
      { q: "GROUP BY vs ORDER BY, and the logical order of operations?", a: "GROUP BY collapses rows into groups for aggregation; ORDER BY sorts the result. Logical order: FROM → WHERE → GROUP BY → HAVING → SELECT → ORDER BY → LIMIT.", keywords: ["group", "sort", "FROM", "WHERE", "SELECT", "ORDER BY"] },
      { q: "LIKE and BETWEEN?", a: "LIKE is pattern matching with wildcards (% = any sequence, _ = one char); 'A%' finds names starting with A. BETWEEN is an inclusive range: BETWEEN 18 AND 65 = >= 18 AND <= 65.", keywords: ["pattern", "%", "_", "inclusive", "range"] },
      { q: "What is a subquery?", a: "A query nested inside another (in SELECT, FROM, or WHERE). It can be scalar, correlated, or return a set used with IN/EXISTS/comparison.", keywords: ["nested", "scalar", "correlated", "IN", "EXISTS"] },
      { q: "Aggregate vs scalar functions, with examples?", a: "Aggregate operates over a set of rows and returns one value: SUM, AVG, COUNT, MIN, MAX. Scalar operates per value/row and returns one per input: UPPER, LOWER, LENGTH, ROUND, CONCAT, NOW.", keywords: ["set of rows", "one value", "per row", "SUM", "UPPER"] },
      { q: "What does AS (alias) do?", a: "Gives a column or table a temporary name for readability: SELECT AVG(salary) AS avg_pay; FROM employees AS e. AS is optional for table aliases.", keywords: ["temporary name", "column", "table", "readability"] },
    ],
    mcq: [
      { q: "Which removes all rows but keeps the table and is typically not rollback-able?", options: ["DELETE", "DROP", "TRUNCATE", "ALTER"], correct: [2], why: "TRUNCATE empties the table fast and keeps its definition; DELETE can target rows and roll back; DROP removes the whole table." },
      { q: "A LEFT JOIN returns:", options: ["only matching rows", "all left rows; unmatched right columns are NULL", "all right rows", "every pair"], correct: [1], why: "LEFT keeps every left row; right-side columns are NULL when there's no match." },
      { q: "Which set operation keeps duplicates?", options: ["UNION", "UNION ALL", "INTERSECT", "EXCEPT"], correct: [1], why: "UNION dedups; UNION ALL keeps duplicates." },
      { q: "WHERE differs from HAVING because WHERE:", options: ["filters groups after aggregation", "filters rows before grouping (no aggregates)", "sorts rows", "can use COUNT()"], correct: [1], why: "WHERE filters rows pre-grouping; HAVING filters groups post-aggregation and can use aggregates." },
      { q: "The logical order of operations is:", options: ["SELECT → FROM → WHERE → GROUP BY", "FROM → WHERE → GROUP BY → HAVING → SELECT → ORDER BY", "WHERE → FROM → SELECT → ORDER BY", "FROM → SELECT → WHERE → ORDER BY"], correct: [1], why: "SELECT is written first but evaluated after HAVING, before ORDER BY." },
      { q: "COUNT, SUM, AVG, MIN, MAX are:", options: ["scalar functions", "aggregate functions", "window frames", "constraints"], correct: [1], why: "They operate over a set of rows returning one value — aggregates. UPPER/ROUND/NOW are scalar." },
      { q: "name LIKE 'A%' matches:", options: ["names containing A", "names ending in A", "names starting with A", "names exactly 'A'"], correct: [2], why: "% is any sequence; 'A%' = starts with A." },
      { q: "A CROSS JOIN produces:", options: ["only matches", "the Cartesian product (every pair)", "all left rows", "distinct rows"], correct: [1], why: "CROSS JOIN pairs every row of one table with every row of the other." },
    ],
  },

  /* ================= SQL — TRANSACTIONS & ARCHITECTURE ================= */
  {
    id: "sql-transactions", track: "SQL", name: "Transactions & Architecture", color: "violet", icon: Database,
    cards: [
      { q: "What is a transaction, and what does atomic mean?", a: "A unit of work — one or more statements executed as a single all-or-nothing logical operation (BEGIN…COMMIT/ROLLBACK). Atomic means full effect or no effect at all; indivisible.", keywords: ["unit of work", "all-or-nothing", "COMMIT", "indivisible"] },
      { q: "What does ACID stand for?", a: "Atomicity (commit/rollback together), Consistency (database stays in valid states), Isolation (concurrent txns don't see each other's improper intermediate effects), Durability (committed data survives crashes).", keywords: ["Atomicity", "Consistency", "Isolation", "Durability"] },
      { q: "List the isolation levels and what they prevent.", a: "READ UNCOMMITTED (nothing), READ COMMITTED (prevents dirty reads), REPEATABLE READ (also non-repeatable reads), SERIALIZABLE (also phantom reads). Higher isolation = fewer anomalies, less concurrency.", keywords: ["READ UNCOMMITTED", "READ COMMITTED", "REPEATABLE READ", "SERIALIZABLE"] },
      { q: "Dirty, non-repeatable, and phantom reads?", a: "Dirty: reading another txn's uncommitted data (may roll back). Non-repeatable: same row re-read returns different data. Phantom: a range query re-run returns a different number of rows.", keywords: ["dirty", "uncommitted", "non-repeatable", "phantom", "range"] },
      { q: "What does BASE stand for?", a: "Basically Available, Soft state, Eventual consistency — a NoSQL model favoring availability and eventual consistency over strict ACID.", keywords: ["Basically Available", "Soft state", "Eventual consistency", "NoSQL"] },
      { q: "View vs materialized view?", a: "A view is a stored named query acting as a virtual table; it doesn't make queries faster but simplifies complex ones. A materialized view stores the result physically — faster reads but data can be stale.", keywords: ["named query", "virtual", "materialized", "stale"] },
      { q: "What is an index, and its trade-off?", a: "An index speeds up lookups/joins/sorts by avoiding full scans; add one on selective WHERE/JOIN/ORDER BY columns. Downside: every write must maintain it, so too many indexes slow writes and use storage.", keywords: ["faster reads", "selective", "slow writes", "storage"] },
      { q: "What is a DAO?", a: "A Java interface/class whose methods access the database, giving one shared way to talk to the DB. It separates persistence from services so raw JDBC doesn't scatter across the app, and lets you swap/test persistence.", keywords: ["interface", "separates", "persistence", "JDBC"] },
      { q: "Stored procedures — pros and cons?", a: "Pros: fewer network round trips, logic centralized next to the data, enforceable regardless of client. Cons: vendor-specific dialects, harder to unit-test, versioning can lag deployments.", keywords: ["round trips", "centralized", "vendor-specific", "testing"] },
      { q: "How do you prevent SQL injection?", a: "Use a PreparedStatement with ? placeholders and bind values, so the database treats them as data, not SQL syntax — blocking clauses like ' OR '1'='1. Never concatenate user input into SQL.", keywords: ["PreparedStatement", "bind", "data", "syntax", "injection"] },
      { q: "What is a port number, and the defaults for Postgres and MySQL?", a: "A logical endpoint (0–65535) identifying a service/process on a host for network traffic. PostgreSQL default = 5432; MySQL default = 3306.", keywords: ["endpoint", "service", "5432", "3306"] },
    ],
    mcq: [
      { q: "ACID's 'A' stands for:", options: ["Availability", "Atomicity", "Alignment", "Association"], correct: [1], why: "Atomicity: all changes commit together or roll back together." },
      { q: "READ COMMITTED prevents which anomaly?", options: ["phantom reads", "non-repeatable reads", "dirty reads", "deadlocks"], correct: [2], why: "READ COMMITTED stops dirty reads (reading uncommitted data). Higher levels stop more." },
      { q: "A phantom read is when:", options: ["you read uncommitted data", "the same row returns different values", "a range query returns a different number of rows", "a write is lost"], correct: [2], why: "Phantom = a re-run range query sees new/removed rows (different row count)." },
      { q: "BASE stands for:", options: ["Basic Atomic Stored Entities", "Basically Available, Soft state, Eventual consistency", "Binary Access Secure Encryption", "Balanced ACID Set Extension"], correct: [1], why: "BASE is the NoSQL counterpart favoring availability and eventual consistency." },
      { q: "A (non-materialized) view:", options: ["always speeds up queries", "stores results physically", "is a stored query acting as a virtual table", "replaces indexes"], correct: [2], why: "A view is a virtual table recomputed from base tables; it simplifies queries but isn't inherently faster." },
      { q: "Too many indexes primarily:", options: ["speed up writes", "slow down writes and use storage", "prevent injection", "enforce constraints"], correct: [1], why: "Each write must maintain every relevant index, slowing INSERT/UPDATE/DELETE and using storage." },
      { q: "The safe way to run a query with user input in JDBC is:", options: ["string concatenation", "a PreparedStatement with ? placeholders", "a Statement", "a stored view"], correct: [1], why: "PreparedStatement binds values as data, not SQL syntax, preventing injection." },
      { q: "PostgreSQL's default port is:", options: ["3306", "5432", "1433", "8080"], correct: [1], why: "Postgres = 5432; MySQL = 3306." },
      { q: "A DAO's main purpose is to:", options: ["render the UI", "isolate all DB access behind an interface", "define tables", "manage threads"], correct: [1], why: "The DAO separates persistence from business logic so JDBC doesn't scatter across the app." },
    ],
  },
];

const STORAGE_KEY = "qc_complete_review_v1";
function shuffle(a) { a = [...a]; for (let i = a.length - 1; i > 0; i--) { const j = (Math.random() * (i + 1)) | 0; [a[i], a[j]] = [a[j], a[i]]; } return a; }
const eqSet = (x, y) => x.length === y.length && [...x].sort().every((v, i) => v === [...y].sort()[i]);
const key = (deckId, i) => `${deckId}::${i}`;

/* ================================================================== APP == */
export default function App() {
  const [mode, setMode] = useState("cards"); // cards | mcq
  const [deckId, setDeckId] = useState(null);
  const [mastered, setMastered] = useState({});
  const [, setLoaded] = useState(false);

  useEffect(() => {
    let live = true;
    (async () => {
      try { const r = await window.storage.get(STORAGE_KEY); if (live && r && r.value) setMastered(JSON.parse(r.value)); } catch (_) {}
      if (live) setLoaded(true);
    })();
    return () => { live = false; };
  }, []);
  const persist = (n) => { try { window.storage.set(STORAGE_KEY, JSON.stringify(n)); } catch (_) {} };

  const setCardMastered = useCallback((dId, i, val) => {
    setMastered((prev) => { const n = { ...prev }; const k = key(dId, i); if (val) n[k] = true; else delete n[k]; persist(n); return n; });
  }, []);
  const resetDeck = useCallback((dId) => {
    setMastered((prev) => { const n = {}; for (const k of Object.keys(prev)) if (!k.startsWith(dId + "::")) n[k] = prev[k]; persist(n); return n; });
  }, []);

  const deck = DECKS.find((d) => d.id === deckId) || null;

  return (
    <div className="min-h-screen w-full bg-stone-950 text-stone-200 font-sans antialiased">
      <style>{`
        @keyframes rise{from{opacity:0;transform:translateY(10px)}to{opacity:1;transform:none}}
        .rise{animation:rise .35s cubic-bezier(.2,.7,.2,1) both}
        @media (prefers-reduced-motion: reduce){.rise{animation:none}}
        textarea:focus{outline:none}
      `}</style>
      <div className="mx-auto max-w-3xl px-4 pb-24 pt-6 sm:pt-10">
        <header className="mb-6 border-b border-stone-800 pb-4">
          <h1 className="font-serif text-2xl font-semibold tracking-tight text-stone-100 sm:text-3xl">QC Complete Review</h1>
          <p className="mt-1 text-sm text-stone-400">Every concept from your notes — Java + SQL, two ways to drill.</p>
        </header>

        {!deck && (
          <Home mode={mode} setMode={setMode} mastered={mastered} onOpen={setDeckId} />
        )}
        {deck && mode === "cards" && (
          <Flashcards deck={deck} mastered={mastered} onMaster={setCardMastered} onReset={() => resetDeck(deck.id)} onBack={() => setDeckId(null)} />
        )}
        {deck && mode === "mcq" && (
          <Quiz deck={deck} onBack={() => setDeckId(null)} />
        )}
      </div>
    </div>
  );
}

/* ------------------------------------------------------------------ Home -- */
function Home({ mode, setMode, mastered, onOpen }) {
  const tracks = ["Java", "SQL"];
  return (
    <div className="rise">
      <div className="mb-6 flex items-start gap-3 rounded-2xl border border-amber-400/20 bg-amber-400/5 p-4">
        <BookOpen className="mt-0.5 h-5 w-5 shrink-0 text-amber-300" strokeWidth={1.8} />
        <p className="text-sm leading-relaxed text-stone-300">
          Read <span className="font-medium text-stone-100">QC-Complete-Study-Guide.md</span> first for the ground-up walkthrough,
          then drill here. Flashcards build recall; the quiz checks it.
        </p>
      </div>

      {/* mode toggle */}
      <div className="mb-6 inline-flex rounded-xl border border-stone-800 bg-stone-900/60 p-1">
        <ModeTab active={mode === "cards"} onClick={() => setMode("cards")} icon={Layers} label="Flashcards" />
        <ModeTab active={mode === "mcq"} onClick={() => setMode("mcq")} icon={ListChecks} label="Multiple choice" />
      </div>

      {tracks.map((track) => (
        <div key={track} className="mb-7">
          <div className="mb-3 flex items-center gap-3">
            <span className="text-xs font-semibold uppercase tracking-widest text-stone-500">{track}</span>
            <span className="h-px flex-1 bg-stone-800" />
          </div>
          <div className="grid gap-3 sm:grid-cols-2">
            {DECKS.filter((d) => d.track === track).map((d) => (
              <DeckCard key={d.id} deck={d} mode={mode} mastered={mastered} onOpen={onOpen} />
            ))}
          </div>
        </div>
      ))}
    </div>
  );
}

function ModeTab({ active, onClick, icon: Icon, label }) {
  return (
    <button onClick={onClick}
      className={`flex items-center gap-2 rounded-lg px-4 py-2 text-sm font-medium transition ${active ? "bg-stone-100 text-stone-900" : "text-stone-400 hover:text-stone-200"}`}>
      <Icon className="h-4 w-4" /> {label}
    </button>
  );
}

function DeckCard({ deck, mode, mastered, onOpen }) {
  const t = THEME[deck.color];
  const Icon = deck.icon || Layers;
  const count = mode === "cards" ? deck.cards.length : deck.mcq.length;
  const done = useMemo(() => {
    if (mode !== "cards") return 0;
    let n = 0; for (let i = 0; i < deck.cards.length; i++) if (mastered[key(deck.id, i)]) n++; return n;
  }, [deck, mastered, mode]);
  const pct = deck.cards.length ? (done / deck.cards.length) * 100 : 0;
  const complete = mode === "cards" && done === deck.cards.length;

  return (
    <button onClick={() => onOpen(deck.id)}
      className={`group flex flex-col rounded-2xl border border-stone-800 bg-stone-900/60 p-4 text-left transition hover:border-stone-700 hover:bg-stone-900 focus:outline-none focus-visible:ring-2 ${t.ring}`}>
      <div className="flex items-start justify-between">
        <div className={`flex h-9 w-9 items-center justify-center rounded-lg ${t.soft}`}>
          <Icon className={`h-5 w-5 ${t.text}`} strokeWidth={1.8} />
        </div>
        {complete ? <Trophy className="h-5 w-5 text-amber-300" strokeWidth={1.8} />
          : <ChevronRight className="h-5 w-5 text-stone-600 transition group-hover:translate-x-0.5 group-hover:text-stone-400" />}
      </div>
      <h3 className="mt-3 font-medium leading-snug text-stone-100">{deck.name}</h3>
      <div className="mt-3 flex items-center gap-2 text-xs text-stone-500">
        <span className="font-mono">{mode === "cards" ? `${done}/${count}` : `${count} Q`}</span>
        {mode === "cards" && (
          <div className="h-1.5 flex-1 overflow-hidden rounded-full bg-stone-800">
            <div className={`h-full rounded-full ${t.bar} transition-all duration-500`} style={{ width: `${pct}%` }} />
          </div>
        )}
      </div>
    </button>
  );
}

/* ------------------------------------------------------------- Flashcards -- */
function Flashcards({ deck, mastered, onMaster, onReset, onBack }) {
  const t = THEME[deck.color];
  const build = useCallback(() => deck.cards.map((_, i) => i).filter((i) => !mastered[key(deck.id, i)]), [deck, mastered]);
  const [queue, setQueue] = useState(() => build());
  const [pos, setPos] = useState(0);
  const [revealed, setRevealed] = useState(false);
  const [answer, setAnswer] = useState("");
  const taRef = useRef(null);

  useEffect(() => { setQueue(build()); setPos(0); setRevealed(false); setAnswer(""); /* eslint-disable-next-line */ }, [deck.id]);

  const masteredCount = deck.cards.length - build().length;
  const done = pos >= queue.length;
  const idx = done ? null : queue[pos];
  const card = idx == null ? null : deck.cards[idx];

  const advance = () => { setRevealed(false); setAnswer(""); setPos((p) => p + 1); setTimeout(() => taRef.current?.focus(), 60); };
  const gotIt = () => { onMaster(deck.id, idx, true); advance(); };
  const again = () => { setQueue((q) => [...q, idx]); advance(); };
  const restart = () => { onReset(); setTimeout(() => { setQueue(deck.cards.map((_, i) => i)); setPos(0); setRevealed(false); setAnswer(""); }, 0); };

  const hits = useMemo(() => {
    if (!card?.keywords) return [];
    const low = answer.toLowerCase();
    return card.keywords.map((k) => ({ word: k, hit: low.includes(k.toLowerCase()) }));
  }, [card, answer]);
  const hitCount = hits.filter((h) => h.hit).length;

  return (
    <div className="rise">
      <div className="mb-5 flex items-center justify-between">
        <BackBtn onBack={onBack} />
        <button onClick={restart} className="flex items-center gap-1.5 rounded-lg border border-stone-800 px-2.5 py-1.5 text-xs text-stone-400 transition hover:bg-stone-900 hover:text-stone-200">
          <RotateCcw className="h-3.5 w-3.5" /> Reset
        </button>
      </div>

      <div className="mb-5">
        <div className="flex items-center gap-2">
          <span className={`h-2.5 w-2.5 rounded-full ${t.dot}`} />
          <h2 className="font-serif text-xl font-semibold text-stone-100">{deck.name}</h2>
        </div>
        <div className="mt-3 flex items-center gap-3 text-xs text-stone-500">
          <span className="font-mono">{masteredCount}/{deck.cards.length} mastered</span>
          <div className="h-1.5 flex-1 overflow-hidden rounded-full bg-stone-800">
            <div className={`h-full rounded-full ${t.bar} transition-all duration-500`} style={{ width: `${(masteredCount / deck.cards.length) * 100}%` }} />
          </div>
          {!done && <span className="font-mono">card {pos + 1} of {queue.length}</span>}
        </div>
      </div>

      {done ? (
        <Complete deck={deck} t={t} onRestart={restart} onBack={onBack} label="cards" />
      ) : (
        <div key={pos} className="rise">
          <div className="relative overflow-hidden rounded-2xl bg-stone-100 p-6 text-stone-900 shadow-xl sm:p-8">
            <span className={`absolute left-0 top-0 h-full w-1.5 ${t.bar}`} />
            <span className="text-[11px] font-semibold uppercase tracking-widest text-stone-500">Recall</span>
            <p className="mt-2 font-serif text-lg font-medium leading-snug sm:text-xl">{card.q}</p>
          </div>

          <textarea ref={taRef} value={answer} onChange={(e) => setAnswer(e.target.value)}
            onKeyDown={(e) => { if ((e.ctrlKey || e.metaKey) && e.key === "Enter") { e.preventDefault(); setRevealed(true); } }}
            placeholder="Type your answer from memory…"
            className="mt-4 min-h-[110px] w-full resize-y rounded-xl border border-stone-800 bg-stone-900/70 p-4 text-sm leading-relaxed text-stone-200 placeholder:text-stone-600 focus:border-stone-600" />

          {!revealed ? (
            <button onClick={() => setRevealed(true)}
              className="mt-3 flex w-full items-center justify-center gap-2 rounded-xl bg-stone-100 px-4 py-3 text-sm font-semibold text-stone-900 transition hover:bg-white">
              <Eye className="h-4 w-4" /> Reveal answer
            </button>
          ) : (
            <div className="mt-4 rise">
              <div className="mb-3 rounded-xl border border-stone-800 bg-stone-900/50 p-4">
                <div className="mb-2 flex items-center justify-between">
                  <span className="text-xs font-semibold uppercase tracking-widest text-stone-500">Keyword check</span>
                  <span className="font-mono text-xs text-stone-400">{hitCount}/{card.keywords.length} hit</span>
                </div>
                <div className="flex flex-wrap gap-1.5">
                  {hits.map((k) => (
                    <span key={k.word} className={`rounded-md px-2 py-1 text-xs font-medium ${k.hit ? "bg-emerald-400/15 text-emerald-300 ring-1 ring-emerald-400/30" : "bg-stone-800 text-stone-500 ring-1 ring-stone-700"}`}>
                      {k.hit ? "✓ " : ""}{k.word}
                    </span>
                  ))}
                </div>
              </div>
              <div className="overflow-hidden rounded-2xl bg-stone-100 text-stone-900 shadow-lg">
                <div className="border-b border-stone-300 px-5 py-2.5">
                  <span className="text-[11px] font-semibold uppercase tracking-widest text-stone-500">Model answer</span>
                </div>
                <p className="whitespace-pre-wrap p-5 text-[15px] leading-relaxed text-stone-800">{card.a}</p>
              </div>
              <div className="mt-4 grid grid-cols-2 gap-3">
                <button onClick={again} className="flex items-center justify-center gap-2 rounded-xl border border-rose-400/30 bg-rose-400/10 px-4 py-3 text-sm font-semibold text-rose-300 transition hover:bg-rose-400/20">
                  <RefreshCw className="h-4 w-4" /> Review again
                </button>
                <button onClick={gotIt} className="flex items-center justify-center gap-2 rounded-xl border border-emerald-400/30 bg-emerald-400/10 px-4 py-3 text-sm font-semibold text-emerald-300 transition hover:bg-emerald-400/20">
                  <Check className="h-4 w-4" /> Got it
                </button>
              </div>
            </div>
          )}
          <p className="mt-6 flex items-center justify-center gap-1.5 text-xs text-stone-600"><Keyboard className="h-3.5 w-3.5" /> Ctrl/⌘ + Enter to reveal</p>
        </div>
      )}
    </div>
  );
}

/* ------------------------------------------------------------------ Quiz -- */
function Quiz({ deck, onBack }) {
  const t = THEME[deck.color];
  const [order, setOrder] = useState(() => shuffle(deck.mcq.map((_, i) => i)));
  const [pos, setPos] = useState(0);
  const [picked, setPicked] = useState([]);
  const [submitted, setSubmitted] = useState(false);
  const [results, setResults] = useState([]);

  useEffect(() => { setOrder(shuffle(deck.mcq.map((_, i) => i))); setPos(0); setPicked([]); setSubmitted(false); setResults([]); /* eslint-disable-next-line */ }, [deck.id]);

  const restart = (subset) => {
    setOrder(shuffle(subset || deck.mcq.map((_, i) => i)));
    setPos(0); setPicked([]); setSubmitted(false); setResults([]);
  };

  const done = pos >= order.length;
  if (done) {
    const score = results.filter((r) => r?.correct).length;
    const missed = order.filter((_, i) => results[i] && !results[i].correct);
    return (
      <div className="rise">
        <BackBtn onBack={onBack} />
        <div className="mt-5 rounded-2xl border border-stone-800 bg-stone-900/60 p-8 text-center">
          <div className={`mx-auto mb-4 flex h-14 w-14 items-center justify-center rounded-full ${t.soft}`}>
            <Trophy className={`h-7 w-7 ${t.text}`} strokeWidth={1.8} />
          </div>
          <h3 className="font-serif text-2xl font-semibold text-stone-100">{score} / {order.length}</h3>
          <p className="mt-2 text-sm text-stone-400">{deck.name}</p>
          <div className="mt-6 flex flex-wrap justify-center gap-3">
            {missed.length > 0 && (
              <button onClick={() => restart(missed)} className="flex items-center gap-2 rounded-xl border border-rose-400/30 bg-rose-400/10 px-5 py-2.5 text-sm font-semibold text-rose-300 transition hover:bg-rose-400/20">
                <RefreshCw className="h-4 w-4" /> Retry {missed.length} missed
              </button>
            )}
            <button onClick={() => restart()} className="flex items-center gap-2 rounded-xl bg-stone-100 px-5 py-2.5 text-sm font-semibold text-stone-900 transition hover:bg-white">
              <RotateCcw className="h-4 w-4" /> New run
            </button>
            <button onClick={onBack} className="flex items-center gap-2 rounded-xl border border-stone-800 px-5 py-2.5 text-sm font-medium text-stone-300 transition hover:bg-stone-900">
              <ArrowLeft className="h-4 w-4" /> Decks
            </button>
          </div>
        </div>
      </div>
    );
  }

  const q = deck.mcq[order[pos]];
  const isMulti = !!q.multi;
  const toggle = (i) => {
    if (submitted) return;
    if (isMulti) setPicked((p) => p.includes(i) ? p.filter((x) => x !== i) : [...p, i]);
    else setPicked([i]);
  };
  const submit = () => { if (!picked.length) return; const correct = eqSet(picked, q.correct); setResults((r) => { const n = [...r]; n[pos] = { correct }; return n; }); setSubmitted(true); };
  const next = () => { setPos((p) => p + 1); setPicked([]); setSubmitted(false); };

  return (
    <div className="rise">
      <BackBtn onBack={onBack} />
      <div className="mb-4 mt-4 flex items-center gap-3 text-xs text-stone-500">
        <span className="font-mono">Q{pos + 1} / {order.length}</span>
        <div className="h-1.5 flex-1 overflow-hidden rounded-full bg-stone-800">
          <div className={`h-full rounded-full ${t.bar} transition-all duration-300`} style={{ width: `${(pos / order.length) * 100}%` }} />
        </div>
        <span className="rounded-md bg-stone-900 px-2 py-0.5 text-[11px] text-stone-400">{deck.name}</span>
      </div>

      <div className="rounded-2xl bg-stone-100 p-6 text-stone-900 shadow-xl sm:p-7">
        <p className="whitespace-pre-wrap font-serif text-lg font-medium leading-snug sm:text-xl">{q.q}</p>
        {isMulti && <p className="mt-2 text-xs font-medium text-stone-500">Select all that apply.</p>}
      </div>

      <div className="mt-4 space-y-2.5">
        {q.options.map((opt, i) => {
          const chosen = picked.includes(i);
          const isCorrect = q.correct.includes(i);
          let cls = "border-stone-800 bg-stone-900/60 text-stone-200 hover:border-stone-600";
          let mark = null;
          if (submitted) {
            if (isCorrect) { cls = "border-emerald-400/40 bg-emerald-400/10 text-emerald-200"; mark = <Check className="h-4 w-4 text-emerald-400" />; }
            else if (chosen) { cls = "border-rose-400/40 bg-rose-400/10 text-rose-200"; mark = <X className="h-4 w-4 text-rose-400" />; }
            else cls = "border-stone-800 bg-stone-900/40 text-stone-500";
          } else if (chosen) cls = "border-amber-400/50 bg-amber-400/10 text-amber-100";
          return (
            <button key={i} onClick={() => toggle(i)} disabled={submitted}
              className={`flex w-full items-center gap-3 rounded-xl border px-4 py-3 text-left text-sm transition ${cls}`}>
              <span className="shrink-0">
                {submitted && (isCorrect || chosen) ? mark
                  : (isMulti
                    ? (chosen ? <CheckCircle2 className="h-4 w-4 text-amber-300" /> : <span className="block h-4 w-4 rounded border border-stone-600" />)
                    : (chosen ? <CircleDot className="h-4 w-4 text-amber-300" /> : <span className="block h-4 w-4 rounded-full border border-stone-600" />))}
              </span>
              <span className="whitespace-pre-wrap">{opt}</span>
            </button>
          );
        })}
      </div>

      {submitted && (
        <div className="mt-4 rise rounded-xl border border-stone-800 bg-stone-900/50 p-4">
          <div className="mb-1">
            {results[pos]?.correct
              ? <span className="flex items-center gap-1.5 text-sm font-semibold text-emerald-300"><Check className="h-4 w-4" /> Correct</span>
              : <span className="flex items-center gap-1.5 text-sm font-semibold text-rose-300"><X className="h-4 w-4" /> Not quite</span>}
          </div>
          <p className="text-sm leading-relaxed text-stone-300">{q.why}</p>
        </div>
      )}

      <div className="mt-4">
        {!submitted ? (
          <button onClick={submit} disabled={!picked.length}
            className="flex w-full items-center justify-center gap-2 rounded-xl bg-stone-100 px-4 py-3 text-sm font-semibold text-stone-900 transition hover:bg-white disabled:opacity-40">
            Check answer
          </button>
        ) : (
          <button onClick={next}
            className={`flex w-full items-center justify-center gap-2 rounded-xl px-4 py-3 text-sm font-semibold text-stone-900 ${t.bar} transition hover:brightness-110`}>
            {pos + 1 >= order.length ? "See results" : "Next question"} <ArrowRight className="h-4 w-4" />
          </button>
        )}
      </div>
    </div>
  );
}

/* -------------------------------------------------------------- shared ---- */
function Complete({ deck, t, onRestart, onBack, label }) {
  return (
    <div className="rounded-2xl border border-stone-800 bg-stone-900/60 p-8 text-center">
      <div className={`mx-auto mb-4 flex h-14 w-14 items-center justify-center rounded-full ${t.soft}`}>
        <Trophy className={`h-7 w-7 ${t.text}`} strokeWidth={1.8} />
      </div>
      <h3 className="font-serif text-xl font-semibold text-stone-100">Deck cleared</h3>
      <p className="mx-auto mt-2 max-w-sm text-sm text-stone-400">Every {label} in {deck.name} is mastered. Run it cold once more to lock it in.</p>
      <div className="mt-6 flex justify-center gap-3">
        <button onClick={onRestart} className="flex items-center gap-2 rounded-xl bg-stone-100 px-5 py-2.5 text-sm font-semibold text-stone-900 hover:bg-white">
          <RotateCcw className="h-4 w-4" /> Run again
        </button>
        <button onClick={onBack} className="flex items-center gap-2 rounded-xl border border-stone-800 px-5 py-2.5 text-sm font-medium text-stone-300 hover:bg-stone-900">
          <ArrowLeft className="h-4 w-4" /> Decks
        </button>
      </div>
    </div>
  );
}

function BackBtn({ onBack }) {
  return (
    <button onClick={onBack} className="flex items-center gap-1.5 rounded-lg px-2 py-1.5 text-sm text-stone-400 transition hover:bg-stone-900 hover:text-stone-200">
      <ArrowLeft className="h-4 w-4" /> All decks
    </button>
  );
}
