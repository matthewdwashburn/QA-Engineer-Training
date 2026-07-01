import React, { useState, useMemo, useEffect, useRef, useCallback } from "react";
import {
  ArrowLeft, ArrowRight, Check, X, RotateCcw, Eye, RefreshCw, Shuffle,
  ListChecks, Layers, Trophy, BookOpen, Keyboard, ChevronRight, CircleDot, CheckCircle2
} from "lucide-react";

/* =========================================================================
   QC — MISSED CONCEPTS REVIEW
   Two modes:
     • Multiple Choice  — your exact missed questions + reinforcement,
                          each with a why-it's-right explanation.
     • Flashcards       — write-first recall on the same weak spots.
   Focused entirely on the gaps from the quizzes + interview.
   ========================================================================= */

const ACCENT = {
  bar: "bg-amber-400", dot: "bg-amber-400", text: "text-amber-300",
  soft: "bg-amber-400/10", ring: "ring-amber-400/40",
};

/* ----------------------------------------------------- MULTIPLE CHOICE DATA
   correct: array of indices (supports multi-select).
   miss:    true if this is a question you actually got wrong.
------------------------------------------------------------------------- */

const TOPICS = [
  "Platform & Language",
  "Static, final & Polymorphism",
  "Abstract vs Interface",
  "Exceptions",
  "Collections",
  "Build Tools",
  "SQL",
  "JDBC & ORM",
];

const MCQ = [
  /* ---- Platform & Language ---- */
  {
    t: "Platform & Language", miss: true,
    q: "Which Java feature allows it to run on any platform?",
    options: ["Platform-specific compilation", "Write once, run anywhere", "Direct hardware access", "Cross-platform compatibility"],
    correct: [1],
    why: "Java compiles to bytecode that runs on the JVM, so the same bytecode runs on any OS with a JVM — 'write once, run anywhere.' 'Cross-platform compatibility' describes the result, but WORA (via bytecode + JVM) is the named mechanism. 'Platform-specific compilation' is the C model, the opposite of Java.",
  },
  {
    t: "Platform & Language", miss: true,
    q: "How do you declare an array of integers in Java?",
    options: ["int[] arr;", "int arr[];", "Both are valid", "array arr;"],
    correct: [2],
    why: "Both int[] arr; and int arr[]; compile. int[] arr is the preferred style because the [] belongs to the type. array is not a keyword.",
  },
  {
    t: "Platform & Language", miss: true,
    q: "Which scenario causes a compile-time error with switch in Java?",
    options: ["Using String in switch", "Using enum in switch", "Using double in switch", "Using char in switch"],
    correct: [2],
    why: "switch supports byte, short, char, int (+ wrappers), String, and enum. It rejects long, float, double, and boolean — so switching on a double fails to compile.",
  },
  {
    t: "Platform & Language", miss: true,
    q: "Can we have multiple classes in a single Java file?",
    options: ["Yes, but only one can be public", "No, only one class per file", "Yes, unlimited public classes", "Maximum 2 classes per file"],
    correct: [0],
    why: "A file may hold several top-level classes, but at most one public class, and the file must be named after that public class.",
  },
  {
    t: "Platform & Language", miss: false,
    q: "What does the JVM actually execute?",
    options: ["Java source code directly", "Bytecode (.class files)", "Native machine code compiled per OS", "Assembly written by the developer"],
    correct: [1],
    why: "javac compiles source to platform-neutral bytecode; the JVM translates that bytecode to native instructions at runtime.",
  },
  {
    t: "Platform & Language", miss: false,
    q: "Predict the output:  int[] a = {1,2,3}; System.out.println(a.length);",
    options: ["3", "Compile error (should be a.length())", "2", "a.size()"],
    correct: [0],
    why: "Arrays expose length as a FIELD (no parentheses) → prints 3. a.length() would be an error; .size() is for collections, not arrays.",
  },
  {
    t: "Platform & Language", miss: false, multi: true,
    q: "Which types are NOT allowed in a switch? (select all)",
    options: ["long", "double", "int", "boolean"],
    correct: [0, 1, 3],
    why: "long, double (and float), and boolean are not switchable. int IS allowed (along with byte/short/char, String, and enum).",
  },

  /* ---- Static, final & Polymorphism ---- */
  {
    t: "Static, final & Polymorphism", miss: true,
    q: "Method overriding occurs at:",
    options: ["Compile time", "Runtime", "Preprocessing", "Linking"],
    correct: [1],
    why: "Overriding is resolved at runtime via dynamic dispatch — the JVM picks the method based on the ACTUAL object type. Overloading is the compile-time one. Mnemonic: overRIDE → RUNtime.",
  },
  {
    t: "Static, final & Polymorphism", miss: false,
    q: "Method overloading is resolved at:",
    options: ["Runtime", "Compile time", "Class loading", "Garbage collection"],
    correct: [1],
    why: "Overloading (same name, different parameter lists) is chosen by the compiler from the argument types — static binding at compile time.",
  },
  {
    t: "Static, final & Polymorphism", miss: false,
    q: "Can a static method be overridden?",
    options: [
      "Yes, exactly like an instance method",
      "No — a same-signature static in a subclass hides it (method hiding), resolved by reference type at compile time",
      "Yes, but only if it is also final",
      "No, static methods can't be inherited at all",
    ],
    correct: [1],
    why: "Overriding needs an object to dispatch on, but static methods belong to the class. A matching static in a subclass HIDES the parent's version; which runs depends on the reference type at compile time, not the object.",
  },
  {
    t: "Static, final & Polymorphism", miss: false,
    q: "Can constructors be overridden?",
    options: [
      "Yes, with @Override",
      "No — constructors aren't inherited, so they can't be overridden (they can be overloaded)",
      "Only the no-arg constructor",
      "Only in abstract classes",
    ],
    correct: [1],
    why: "Constructors are not inherited, so overriding doesn't apply. You CAN overload them (multiple constructors with different parameter lists).",
  },
  {
    t: "Static, final & Polymorphism", miss: false,
    q: "What is a constructor's return type?",
    options: ["void", "The class type", "It has no return type at all", "Object"],
    correct: [2],
    why: "A constructor declares no return type — not even void. Writing 'void ClassName()' makes it a regular method, not a constructor.",
  },
  {
    t: "Static, final & Polymorphism", miss: false,
    q: "Why is the main method declared static?",
    options: [
      "So it can be overridden by subclasses",
      "So the JVM can call it without first creating an instance",
      "So it executes faster",
      "Because main must always be final",
    ],
    correct: [1],
    why: "At startup no objects exist yet. Making main static lets the JVM invoke it directly on the class, with no instance required.",
  },
  {
    t: "Static, final & Polymorphism", miss: false,
    q: "static and final are examples of which kind of modifier?",
    options: ["Access modifiers", "Non-access modifiers", "Data types", "Reserved return types"],
    correct: [1],
    why: "Access modifiers control visibility (public/protected/private/default). static and final are NON-access modifiers — they control behavior.",
  },
  {
    t: "Static, final & Polymorphism", miss: false,
    q: "A final variable holding an object reference means:",
    options: [
      "The object's fields can never change",
      "The reference can't be reassigned, though the object's fields may still change",
      "The class can't be extended",
      "The method can't be overridden",
    ],
    correct: [1],
    why: "final on a variable prevents REASSIGNMENT. For a reference, you can't point it elsewhere, but the referenced object can still be mutated. (final method → no override; final class → no extend.)",
  },
  {
    t: "Static, final & Polymorphism", miss: false,
    q: "Java Strings are:",
    options: ["Mutable", "Immutable — every 'change' returns a new String", "Mutable only if not final", "Mutable via setCharAt"],
    correct: [1],
    why: "String objects can't change after creation. Identical literals are shared in the string pool; use new String(...) to force a distinct object. Compare content with .equals(), not ==.",
  },

  /* ---- Abstract vs Interface ---- */
  {
    t: "Abstract vs Interface", miss: true,
    q: "Primary difference between abstract classes and interfaces (pre-Java 8)?",
    options: [
      "Interfaces can have constructors",
      "Abstract classes support multiple inheritance",
      "Interfaces cannot have method implementations",
      "Abstract classes cannot define constants",
    ],
    correct: [2],
    why: "Pre-Java 8 every interface method was just a signature (no body); abstract classes could always have concrete methods. Abstract classes do NOT support multiple inheritance — Java has single class inheritance.",
  },
  {
    t: "Abstract vs Interface", miss: false,
    q: "In Java, a single class can:",
    options: ["extend multiple classes", "implement multiple interfaces", "extend multiple interfaces", "implement multiple classes"],
    correct: [1],
    why: "A class extends exactly one class but can implement many interfaces — that's how Java gives multiple inheritance of TYPE.",
  },
  {
    t: "Abstract vs Interface", miss: false,
    q: "Since Java 8, interfaces may contain:",
    options: ["constructors", "instance fields (state)", "default and static methods", "private instance variables"],
    correct: [2],
    why: "Java 8 added default and static methods (with bodies); Java 9 added private helper methods. Interfaces still have no constructors or instance state.",
  },

  /* ---- Exceptions ---- */
  {
    t: "Exceptions", miss: true,
    q: "What is the parent class of all exceptions?",
    options: ["Exception", "Error", "Throwable", "RuntimeException"],
    correct: [2],
    why: "Both Exception and Error extend Throwable, so Throwable is the common root of everything throwable. 'Exception' is wrong because Error is not under Exception.",
  },
  {
    t: "Exceptions", miss: false,
    q: "Checked exceptions:",
    options: [
      "extend RuntimeException",
      "must be caught or declared with throws (compiler-enforced)",
      "are always optional to handle",
      "are subclasses of Error",
    ],
    correct: [1],
    why: "Checked exceptions extend Exception but not RuntimeException; the compiler forces you to catch them or declare throws. They model recoverable external failures (IOException, SQLException).",
  },
  {
    t: "Exceptions", miss: false,
    q: "NullPointerException is:",
    options: ["a checked exception", "an unchecked (RuntimeException)", "a subclass of Error", "a compile-time error"],
    correct: [1],
    why: "NPE extends RuntimeException, so it's unchecked — no throws required. It typically signals a programming bug.",
  },

  /* ---- Collections ---- */
  {
    t: "Collections", miss: true,
    q: "Which collection guarantees sorted order based on natural ordering or a comparator?",
    options: ["HashSet", "TreeSet", "LinkedList", "ArrayList"],
    correct: [1],
    why: "TreeSet is tree-backed and iterates in sorted order (via Comparable or a supplied Comparator). ArrayList keeps INSERTION order, not sorted order.",
  },
  {
    t: "Collections", miss: true,
    q: "Predict the output:\n\nArrayDeque<Integer> ad = new ArrayDeque<>();\nad.push(4);\nSystem.out.println(ad.pop());",
    options: ["4", "Compile-time Error", "Run Time Error", "Input Mismatch Error"],
    correct: [0],
    why: "ArrayDeque implements Deque; push()/pop() work on the head as a LIFO stack. push(4) then pop() returns 4 — it simply prints 4. No error.",
  },
  {
    t: "Collections", miss: false,
    q: "Used as a stack, ArrayDeque's push() and pop() operate on:",
    options: ["opposite ends (queue behavior)", "the same end (the head) — LIFO", "random positions", "the tail only"],
    correct: [1],
    why: "As a stack, both push and pop act on the head → Last-In-First-Out. ArrayDeque is the recommended stack in modern Java over the legacy Stack class.",
  },
  {
    t: "Collections", miss: false,
    q: "Which collection allows duplicates AND index-based access?",
    options: ["HashSet", "TreeSet", "ArrayList", "HashMap"],
    correct: [2],
    why: "ArrayList is ordered, indexed, and allows duplicates. Sets forbid duplicates; Map is key/value, not indexed.",
  },

  /* ---- Build Tools ---- */
  {
    t: "Build Tools", miss: true,
    q: "Maven uses which repository by default?",
    options: ["Private", "Central Repository", "Local cache only", "GitHub"],
    correct: [1],
    why: "Maven resolves dependencies from the Central Repository by default. Your local ~/.m2 is a CACHE checked first, not the default remote repo.",
  },
  {
    t: "Build Tools", miss: false,
    q: "The local .m2 folder is:",
    options: ["Maven's default remote repository", "a local cache of downloaded dependencies", "where your source code lives", "the JDK installation"],
    correct: [1],
    why: ".m2 caches artifacts already downloaded. On a cache miss Maven fetches from Central.",
  },

  /* ---- SQL ---- */
  {
    t: "SQL", miss: true, multi: true,
    q: "A column marked PRIMARY KEY is implicitly... (select all that apply)",
    options: ["unique", "final", "serial", "not null"],
    correct: [0, 3],
    why: "PRIMARY KEY implies UNIQUE + NOT NULL. 'final' is a Java keyword (irrelevant); 'serial' is a Postgres auto-increment type — a PK does NOT imply auto-increment.",
  },
  {
    t: "SQL", miss: false,
    q: "How does a PRIMARY KEY differ from a plain UNIQUE constraint?",
    options: [
      "A PRIMARY KEY may contain NULLs",
      "A PRIMARY KEY also enforces NOT NULL, and there is only one per table",
      "UNIQUE columns can't be indexed",
      "There is no difference",
    ],
    correct: [1],
    why: "A UNIQUE column can allow a NULL; a PRIMARY KEY additionally forbids NULL and there's exactly one primary key per table.",
  },
  {
    t: "SQL", miss: true,
    q: "What is the default SQLite database created when using ':memory:'?",
    options: ["Permanent database file", "Temporary in-memory database", "Shared database", "Cloud database"],
    correct: [1],
    why: "':memory:' creates a temporary in-memory database that exists only for the connection and disappears when it closes — nothing is written to disk.",
  },

  /* ---- JDBC & ORM ---- */
  {
    t: "JDBC & ORM", miss: true,
    q: "Which JDBC interface is used to establish a physical connection with the database?",
    options: ["Statement", "ResultSet", "Connection", "Driver"],
    correct: [2],
    why: "DriverManager.getConnection(...) returns a Connection, which represents the physical database session. The Driver is the vendor implementation DriverManager uses behind the scenes to create it.",
  },
  {
    t: "JDBC & ORM", miss: true,
    q: "A JDBC Utility class is generally used to:",
    options: ["Execute SQL queries directly", "Handle database connection and resource management tasks", "Create database tables", "Design the UI of the application"],
    correct: [1],
    why: "A JDBC utility class centralizes opening connections and closing Connection/Statement/ResultSet so resources aren't leaked. Running business queries is the DAO's job.",
  },
  {
    t: "JDBC & ORM", miss: false,
    q: "In JDBC, which safely executes a parameterized query (preventing SQL injection)?",
    options: ["Statement", "PreparedStatement", "ResultSet", "Driver"],
    correct: [1],
    why: "PreparedStatement binds values with ? placeholders as DATA, not SQL syntax, blocking injection. Concatenating input into a Statement is unsafe.",
  },
  {
    t: "JDBC & ORM", miss: true,
    q: "Which SQLAlchemy component is used to interact with database records as Python objects?",
    options: ["ORM", "Engine", "Cursor", "SessionMaker"],
    correct: [0],
    why: "The ORM (Object-Relational Mapper) maps table rows to Python objects. Cursor is the low-level DB-API concept, not SQLAlchemy's object layer.",
  },
  {
    t: "JDBC & ORM", miss: true,
    q: "In SQLAlchemy, which object is typically used to manage transactions?",
    options: ["Engine", "Cursor", "Session", "Table"],
    correct: [2],
    why: "The Session is the ORM's unit of work — it tracks objects and manages transactions (add/flush/commit/rollback). The Engine manages the connection pool, not the transaction.",
  },
  {
    t: "JDBC & ORM", miss: false,
    q: "In SQLAlchemy, the Engine is primarily responsible for:",
    options: ["mapping objects to tables", "managing the connection pool and connectivity", "committing transactions", "defining ORM classes"],
    correct: [1],
    why: "The Engine is the connectivity starting point — connection pooling and dialect. Object mapping is the ORM; transactions are the Session.",
  },
];

/* ----------------------------------------------------- FLASHCARD DATA ---- */

const CARDS = [
  { q: "How does Java achieve 'write once, run anywhere'?", a: "javac compiles source to platform-neutral bytecode (.class). The JVM on each OS translates that same bytecode to native instructions at runtime, so one compiled artifact runs anywhere a JVM exists.", keywords: ["bytecode", "JVM", "platform", "run anywhere"] },
  { q: "Give the two valid ways to declare an int array. Which is preferred?", a: "int[] arr; and int arr[]; both compile. int[] arr is preferred because the [] belongs to the type. Arrays expose length as a field (no parentheses).", keywords: ["int[] arr", "int arr[]", "preferred", "length"] },
  { q: "Which primitive types cannot be used in a switch?", a: "long, float, double, and boolean cannot. switch allows byte, short, char, int (and their wrappers), String, and enum.", keywords: ["long", "double", "boolean", "float"] },
  { q: "Can one .java file contain multiple classes? What's the rule?", a: "Yes — multiple top-level classes are allowed, but at most one may be public, and the file must be named after that public class.", keywords: ["multiple", "one public", "filename"] },
  { q: "Overloading vs overriding — when is each resolved?", a: "Overloading (same name, different params) is resolved at compile time (static binding, by argument types). Overriding (same signature in a subclass) is resolved at runtime via dynamic dispatch on the actual object. Mnemonic: overRIDE → RUNtime.", keywords: ["compile time", "runtime", "dynamic dispatch", "object"] },
  { q: "Can static methods be overridden?", a: "No. A same-signature static method in a subclass HIDES the parent's (method hiding), and which runs is decided by the reference type at compile time, not the object. Static methods can be overloaded, not overridden.", keywords: ["hiding", "reference type", "compile time", "not overridden"] },
  { q: "Can constructors be overloaded or overridden? What's a constructor's return type?", a: "Constructors can be overloaded (different parameter lists) but never overridden — they aren't inherited. A constructor has no return type at all, not even void.", keywords: ["overloaded", "not inherited", "no return type"] },
  { q: "Why is the main method static?", a: "So the JVM can call it directly on the class at startup, before any object exists. main is the entry point: public static void main(String[] args).", keywords: ["JVM", "without instance", "entry point"] },
  { q: "What category of modifier are static and final?", a: "Non-access modifiers (they control behavior). Access modifiers are public, protected, private, and default.", keywords: ["non-access", "modifiers", "behavior"] },
  { q: "What does final mean on a variable, a method, and a class?", a: "Variable: can't be reassigned (a reference can't be repointed, though the object may still mutate). Method: can't be overridden. Class: can't be extended (e.g. String).", keywords: ["reassign", "override", "extend"] },
  { q: "Are Java Strings mutable? What is the string pool?", a: "Strings are immutable — every 'change' returns a new String. The string pool caches literals, so identical literals share one object; == compares references while .equals() compares content. new String() forces a distinct object.", keywords: ["immutable", "pool", "interned", "equals"] },
  { q: "Abstract class vs interface (pre-Java 8) — the key difference?", a: "Pre-Java 8, interfaces could not have method implementations (only signatures); abstract classes could always have concrete methods, state, and constructors. Java classes do not support multiple inheritance; interfaces give multiple inheritance of type.", keywords: ["implementations", "state", "multiple", "type"] },
  { q: "What can interfaces contain since Java 8+?", a: "default methods and static methods (with bodies), and private helper methods (Java 9+). Interfaces still have no constructors or instance fields.", keywords: ["default", "static", "private", "no state"] },
  { q: "What is the parent class of all exceptions and errors?", a: "Throwable. Both Exception and Error extend Throwable, so it's the common root. (Exception is wrong as 'the parent of all' because Error is not under Exception.)", keywords: ["Throwable", "Error", "Exception", "root"] },
  { q: "Checked vs unchecked exceptions?", a: "Checked extend Exception (not RuntimeException) and the compiler forces catch-or-throws; they model recoverable external failures. Unchecked are RuntimeException/Error and need no throws; they usually signal bugs.", keywords: ["RuntimeException", "throws", "compiler", "recoverable"] },
  { q: "How do you define a custom checked exception?", a: "Subclass Exception (or RuntimeException for unchecked) and pass a message to super. e.g. class InvalidAgeException extends Exception { public InvalidAgeException(String m){ super(m); } } then throw new InvalidAgeException(\"Invalid age\").", keywords: ["extends Exception", "super", "throw"] },
  { q: "Which collection is sorted by natural ordering or a comparator?", a: "TreeSet — tree-backed, iterates in sorted order via Comparable or a supplied Comparator, O(log n) per operation. ArrayList keeps insertion order, not sorted order.", keywords: ["TreeSet", "Comparable", "Comparator", "sorted"] },
  { q: "ArrayDeque as a stack: what do push/pop do, and what does push(4); pop() print?", a: "ArrayDeque implements Deque; used as a stack, push() and pop() both act on the head → LIFO. push(4) then pop() returns and prints 4. No error. It's the recommended modern stack.", keywords: ["LIFO", "head", "4", "Deque"] },
  { q: "What repository does Maven use by default, and what is .m2?", a: "Maven resolves from the Central Repository by default. ~/.m2 is a local cache checked first; on a miss Maven downloads from Central.", keywords: ["Central", ".m2", "cache"] },
  { q: "What does marking a column PRIMARY KEY imply?", a: "UNIQUE + NOT NULL, with one primary key per table. It does not imply auto-increment (serial) or a default.", keywords: ["unique", "not null", "one per table"] },
  { q: "What is a SQLite ':memory:' database?", a: "A temporary in-memory database that exists only for the duration of the connection and is destroyed when it closes — nothing is written to disk. Great for tests.", keywords: ["temporary", "in-memory", "connection"] },
  { q: "In JDBC, which interface represents the physical connection, and how does Driver relate?", a: "Connection represents the physical database session. DriverManager.getConnection(...) returns it, using the vendor's Driver implementation behind the scenes to create it.", keywords: ["Connection", "DriverManager", "Driver"] },
  { q: "What is a JDBC utility class for?", a: "Centralizing database connection creation and resource cleanup — opening Connections and closing Connection/Statement/ResultSet to avoid leaks. Running queries is the DAO's job.", keywords: ["connection", "resource", "close", "cleanup"] },
  { q: "SQLAlchemy: what do the ORM, Session, and Engine each do?", a: "ORM maps rows to Python objects (records as objects). Session is the unit of work that manages transactions (add/commit/rollback). Engine manages the connection pool / connectivity.", keywords: ["ORM objects", "Session transactions", "Engine pool"] },
];

/* ------------------------------------------------------------- helpers --- */
const STORAGE_KEY = "qc_missed_review_v1";
function shuffle(a) { a = [...a]; for (let i = a.length - 1; i > 0; i--) { const j = (Math.random() * (i + 1)) | 0; [a[i], a[j]] = [a[j], a[i]]; } return a; }
const eqSet = (x, y) => x.length === y.length && [...x].sort().every((v, i) => v === [...y].sort()[i]);

/* ================================================================== APP == */
export default function App() {
  const [view, setView] = useState("home"); // home | quiz | cards

  return (
    <div className="min-h-screen w-full bg-stone-950 text-stone-200 font-sans antialiased">
      <style>{`
        @keyframes rise { from { opacity:0; transform: translateY(10px);} to {opacity:1; transform:none;} }
        .rise { animation: rise .35s cubic-bezier(.2,.7,.2,1) both; }
        @media (prefers-reduced-motion: reduce){ .rise{ animation:none; } }
        textarea:focus{ outline:none; }
      `}</style>
      <div className="mx-auto max-w-3xl px-4 pb-24 pt-6 sm:pt-10">
        <header className="mb-8 border-b border-stone-800 pb-4">
          <h1 className="font-serif text-2xl font-semibold tracking-tight text-stone-100 sm:text-3xl">
            Missed Concepts Review
          </h1>
          <p className="mt-1 text-sm text-stone-400">The gaps from your quizzes + interview, drilled two ways.</p>
        </header>

        {view === "home" && <Home onQuiz={() => setView("quiz")} onCards={() => setView("cards")} />}
        {view === "quiz" && <Quiz onBack={() => setView("home")} />}
        {view === "cards" && <Cards onBack={() => setView("home")} />}
      </div>
    </div>
  );
}

/* ------------------------------------------------------------------ Home -- */
function Home({ onQuiz, onCards }) {
  const missCount = MCQ.filter((q) => q.miss).length;
  return (
    <div className="rise">
      <div className="mb-6 flex items-start gap-3 rounded-2xl border border-amber-400/20 bg-amber-400/5 p-4">
        <BookOpen className="mt-0.5 h-5 w-5 shrink-0 text-amber-300" strokeWidth={1.8} />
        <p className="text-sm leading-relaxed text-stone-300">
          Read <span className="font-medium text-stone-100">QC-Missed-Concepts-Guide.md</span> first for the ground-up
          walkthrough, then test yourself cold here. Anything you miss, go back to that section.
        </p>
      </div>

      <div className="grid gap-3 sm:grid-cols-2">
        <ModeCard
          icon={ListChecks} title="Multiple choice"
          desc={`${MCQ.length} questions — your ${missCount} actual misses plus reinforcement, each with an explanation.`}
          cta="Start quiz" onClick={onQuiz}
        />
        <ModeCard
          icon={Layers} title="Flashcards"
          desc={`${CARDS.length} write-first cards on the same weak spots. Type it, reveal, self-check.`}
          cta="Start cards" onClick={onCards}
        />
      </div>

      <div className="mt-8">
        <div className="mb-3 flex items-center gap-3">
          <span className="text-xs font-semibold uppercase tracking-widest text-stone-500">Topics covered</span>
          <span className="h-px flex-1 bg-stone-800" />
        </div>
        <div className="flex flex-wrap gap-2">
          {TOPICS.map((t) => (
            <span key={t} className="rounded-lg border border-stone-800 bg-stone-900/60 px-3 py-1.5 text-xs text-stone-300">{t}</span>
          ))}
        </div>
      </div>
    </div>
  );
}

function ModeCard({ icon: Icon, title, desc, cta, onClick }) {
  return (
    <button onClick={onClick}
      className={`group flex flex-col rounded-2xl border border-stone-800 bg-stone-900/60 p-5 text-left transition hover:border-stone-700 hover:bg-stone-900 focus:outline-none focus-visible:ring-2 ${ACCENT.ring}`}>
      <div className={`flex h-10 w-10 items-center justify-center rounded-lg ${ACCENT.soft}`}>
        <Icon className={`h-5 w-5 ${ACCENT.text}`} strokeWidth={1.8} />
      </div>
      <h3 className="mt-3 font-medium text-stone-100">{title}</h3>
      <p className="mt-1 text-sm leading-relaxed text-stone-400">{desc}</p>
      <span className="mt-4 flex items-center gap-1 text-sm font-semibold text-amber-300">
        {cta} <ChevronRight className="h-4 w-4 transition group-hover:translate-x-0.5" />
      </span>
    </button>
  );
}

/* ------------------------------------------------------------------ Quiz -- */
function Quiz({ onBack }) {
  const [selectedTopics, setSelectedTopics] = useState(() => new Set(TOPICS));
  const [started, setStarted] = useState(false);
  const [onlyMisses, setOnlyMisses] = useState(false);

  const pool = useMemo(() => {
    let p = MCQ.filter((q) => selectedTopics.has(q.t));
    if (onlyMisses) p = p.filter((q) => q.miss);
    return p;
  }, [selectedTopics, onlyMisses]);

  const [order, setOrder] = useState([]);
  const [pos, setPos] = useState(0);
  const [picked, setPicked] = useState([]);
  const [submitted, setSubmitted] = useState(false);
  const [results, setResults] = useState([]); // {correct:bool}

  const begin = () => {
    setOrder(shuffle(pool.map((_, i) => i)));
    setPos(0); setPicked([]); setSubmitted(false); setResults([]); setStarted(true);
  };

  if (!started) {
    return (
      <div className="rise">
        <TopBar onBack={onBack} />
        <h2 className="mb-4 font-serif text-xl font-semibold text-stone-100">Build your quiz</h2>
        <p className="mb-4 text-sm text-stone-400">Toggle topics, then start. Questions are shuffled each run.</p>

        <div className="mb-4 flex flex-wrap gap-2">
          {TOPICS.map((t) => {
            const on = selectedTopics.has(t);
            return (
              <button key={t} onClick={() => {
                const n = new Set(selectedTopics); on ? n.delete(t) : n.add(t); setSelectedTopics(n);
              }}
                className={`rounded-lg border px-3 py-1.5 text-xs transition ${on
                  ? "border-amber-400/40 bg-amber-400/10 text-amber-200"
                  : "border-stone-800 bg-stone-900/60 text-stone-500"}`}>
                {t}
              </button>
            );
          })}
        </div>

        <label className="mb-6 flex w-fit cursor-pointer items-center gap-2 text-sm text-stone-300">
          <input type="checkbox" checked={onlyMisses} onChange={(e) => setOnlyMisses(e.target.checked)}
            className="h-4 w-4 accent-amber-400" />
          Only my actual missed questions
        </label>

        <div>
          <button onClick={begin} disabled={pool.length === 0}
            className="flex items-center gap-2 rounded-xl bg-stone-100 px-5 py-3 text-sm font-semibold text-stone-900 transition hover:bg-white disabled:opacity-40">
            Start quiz · {pool.length} question{pool.length === 1 ? "" : "s"}
          </button>
        </div>
      </div>
    );
  }

  const done = pos >= order.length;
  if (done) {
    const score = results.filter((r) => r.correct).length;
    const missedIdx = order.filter((_, i) => results[i] && !results[i].correct);
    return (
      <div className="rise">
        <TopBar onBack={onBack} />
        <div className="rounded-2xl border border-stone-800 bg-stone-900/60 p-8 text-center">
          <div className={`mx-auto mb-4 flex h-14 w-14 items-center justify-center rounded-full ${ACCENT.soft}`}>
            <Trophy className={`h-7 w-7 ${ACCENT.text}`} strokeWidth={1.8} />
          </div>
          <h3 className="font-serif text-2xl font-semibold text-stone-100">{score} / {order.length}</h3>
          <p className="mt-2 text-sm text-stone-400">
            {score === order.length ? "Clean sweep. Run it again cold to be sure." : "Re-drill what you missed — the fix is understanding the mechanism."}
          </p>
          <div className="mt-6 flex flex-wrap justify-center gap-3">
            {missedIdx.length > 0 && (
              <button onClick={() => { setOrder(shuffle(missedIdx)); setPos(0); setPicked([]); setSubmitted(false); setResults([]); }}
                className="flex items-center gap-2 rounded-xl border border-rose-400/30 bg-rose-400/10 px-5 py-2.5 text-sm font-semibold text-rose-300 transition hover:bg-rose-400/20">
                <RefreshCw className="h-4 w-4" /> Retry {missedIdx.length} missed
              </button>
            )}
            <button onClick={begin}
              className="flex items-center gap-2 rounded-xl bg-stone-100 px-5 py-2.5 text-sm font-semibold text-stone-900 transition hover:bg-white">
              <RotateCcw className="h-4 w-4" /> New run
            </button>
            <button onClick={onBack}
              className="flex items-center gap-2 rounded-xl border border-stone-800 px-5 py-2.5 text-sm font-medium text-stone-300 transition hover:bg-stone-900">
              <ArrowLeft className="h-4 w-4" /> Home
            </button>
          </div>
        </div>
      </div>
    );
  }

  const q = pool[order[pos]];
  const isMulti = !!q.multi;

  const toggle = (i) => {
    if (submitted) return;
    if (isMulti) setPicked((p) => p.includes(i) ? p.filter((x) => x !== i) : [...p, i]);
    else setPicked([i]);
  };

  const submit = () => {
    if (picked.length === 0) return;
    const correct = eqSet(picked, q.correct);
    setResults((r) => { const n = [...r]; n[pos] = { correct }; return n; });
    setSubmitted(true);
  };

  const next = () => { setPos((p) => p + 1); setPicked([]); setSubmitted(false); };

  return (
    <div className="rise">
      <TopBar onBack={onBack} />
      <div className="mb-4 flex items-center gap-3 text-xs text-stone-500">
        <span className="font-mono">Q{pos + 1} / {order.length}</span>
        <div className="h-1.5 flex-1 overflow-hidden rounded-full bg-stone-800">
          <div className={`h-full rounded-full ${ACCENT.bar} transition-all duration-300`} style={{ width: `${(pos / order.length) * 100}%` }} />
        </div>
        <span className="rounded-md bg-stone-900 px-2 py-0.5 text-[11px] text-stone-400">{q.t}</span>
      </div>

      <div className="rounded-2xl bg-stone-100 p-6 text-stone-900 shadow-xl sm:p-7">
        {q.miss && (
          <span className="mb-3 inline-block rounded-md bg-rose-100 px-2 py-1 text-[11px] font-semibold uppercase tracking-wide text-rose-700">
            You missed this one
          </span>
        )}
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
          } else if (chosen) {
            cls = "border-amber-400/50 bg-amber-400/10 text-amber-100";
          }
          return (
            <button key={i} onClick={() => toggle(i)} disabled={submitted}
              className={`flex w-full items-center gap-3 rounded-xl border px-4 py-3 text-left text-sm transition ${cls}`}>
              <span className="shrink-0">
                {submitted && (isCorrect || chosen)
                  ? mark
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
          <div className="mb-1 flex items-center gap-2">
            {results[pos]?.correct
              ? <span className="flex items-center gap-1.5 text-sm font-semibold text-emerald-300"><Check className="h-4 w-4" /> Correct</span>
              : <span className="flex items-center gap-1.5 text-sm font-semibold text-rose-300"><X className="h-4 w-4" /> Not quite</span>}
          </div>
          <p className="text-sm leading-relaxed text-stone-300">{q.why}</p>
        </div>
      )}

      <div className="mt-4">
        {!submitted ? (
          <button onClick={submit} disabled={picked.length === 0}
            className="flex w-full items-center justify-center gap-2 rounded-xl bg-stone-100 px-4 py-3 text-sm font-semibold text-stone-900 transition hover:bg-white disabled:opacity-40">
            Check answer
          </button>
        ) : (
          <button onClick={next}
            className={`flex w-full items-center justify-center gap-2 rounded-xl px-4 py-3 text-sm font-semibold text-stone-900 ${ACCENT.bar} transition hover:brightness-110`}>
            {pos + 1 >= order.length ? "See results" : "Next question"} <ArrowRight className="h-4 w-4" />
          </button>
        )}
      </div>
    </div>
  );
}

/* --------------------------------------------------------------- Cards ---- */
function Cards({ onBack }) {
  const [mastered, setMastered] = useState({});
  const [loaded, setLoaded] = useState(false);

  useEffect(() => {
    let live = true;
    (async () => {
      try { const r = await window.storage.get(STORAGE_KEY); if (live && r && r.value) setMastered(JSON.parse(r.value)); } catch (_) {}
      if (live) setLoaded(true);
    })();
    return () => { live = false; };
  }, []);
  const persist = (n) => { try { window.storage.set(STORAGE_KEY, JSON.stringify(n)); } catch (_) {} };

  const buildQueue = useCallback(() => CARDS.map((_, i) => i).filter((i) => !mastered[i]), [mastered]);
  const [queue, setQueue] = useState(() => buildQueue());
  const [pos, setPos] = useState(0);
  const [revealed, setRevealed] = useState(false);
  const [answer, setAnswer] = useState("");
  const taRef = useRef(null);

  const masteredCount = CARDS.length - buildQueue().length;
  const done = pos >= queue.length;
  const idx = done ? null : queue[pos];
  const card = idx == null ? null : CARDS[idx];

  const advance = () => { setRevealed(false); setAnswer(""); setPos((p) => p + 1); setTimeout(() => taRef.current?.focus(), 60); };
  const gotIt = () => { const n = { ...mastered, [idx]: true }; setMastered(n); persist(n); advance(); };
  const again = () => { setQueue((q) => [...q, idx]); advance(); };
  const restart = () => { setMastered({}); persist({}); setQueue(CARDS.map((_, i) => i)); setPos(0); setRevealed(false); setAnswer(""); };

  const hits = useMemo(() => {
    if (!card?.keywords) return [];
    const low = answer.toLowerCase();
    return card.keywords.map((k) => ({ word: k, hit: low.includes(k.toLowerCase()) }));
  }, [card, answer]);
  const hitCount = hits.filter((h) => h.hit).length;

  return (
    <div className="rise">
      <div className="mb-5 flex items-center justify-between">
        <TopBar onBack={onBack} />
        <button onClick={restart}
          className="flex items-center gap-1.5 rounded-lg border border-stone-800 px-2.5 py-1.5 text-xs text-stone-400 transition hover:bg-stone-900 hover:text-stone-200">
          <RotateCcw className="h-3.5 w-3.5" /> Reset
        </button>
      </div>

      <div className="mb-5 flex items-center gap-3 text-xs text-stone-500">
        <span className="font-mono">{masteredCount}/{CARDS.length} mastered</span>
        <div className="h-1.5 flex-1 overflow-hidden rounded-full bg-stone-800">
          <div className={`h-full rounded-full ${ACCENT.bar} transition-all duration-500`} style={{ width: `${(masteredCount / CARDS.length) * 100}%` }} />
        </div>
        {!done && <span className="font-mono">card {pos + 1} of {queue.length}</span>}
      </div>

      {done ? (
        <div className="rounded-2xl border border-stone-800 bg-stone-900/60 p-8 text-center">
          <div className={`mx-auto mb-4 flex h-14 w-14 items-center justify-center rounded-full ${ACCENT.soft}`}>
            <Trophy className={`h-7 w-7 ${ACCENT.text}`} strokeWidth={1.8} />
          </div>
          <h3 className="font-serif text-xl font-semibold text-stone-100">All cards cleared</h3>
          <p className="mx-auto mt-2 max-w-sm text-sm text-stone-400">Run them cold once more, no notes, to lock it in before the quiz.</p>
          <div className="mt-6 flex justify-center gap-3">
            <button onClick={restart} className="flex items-center gap-2 rounded-xl bg-stone-100 px-5 py-2.5 text-sm font-semibold text-stone-900 hover:bg-white">
              <RotateCcw className="h-4 w-4" /> Run again
            </button>
            <button onClick={onBack} className="flex items-center gap-2 rounded-xl border border-stone-800 px-5 py-2.5 text-sm font-medium text-stone-300 hover:bg-stone-900">
              <ArrowLeft className="h-4 w-4" /> Home
            </button>
          </div>
        </div>
      ) : (
        <div key={pos} className="rise">
          <div className="relative overflow-hidden rounded-2xl bg-stone-100 p-6 text-stone-900 shadow-xl sm:p-8">
            <span className={`absolute left-0 top-0 h-full w-1.5 ${ACCENT.bar}`} />
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
                    <span key={k.word} className={`rounded-md px-2 py-1 text-xs font-medium ${k.hit
                      ? "bg-emerald-400/15 text-emerald-300 ring-1 ring-emerald-400/30"
                      : "bg-stone-800 text-stone-500 ring-1 ring-stone-700"}`}>
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
                <button onClick={again}
                  className="flex items-center justify-center gap-2 rounded-xl border border-rose-400/30 bg-rose-400/10 px-4 py-3 text-sm font-semibold text-rose-300 transition hover:bg-rose-400/20">
                  <RefreshCw className="h-4 w-4" /> Review again
                </button>
                <button onClick={gotIt}
                  className="flex items-center justify-center gap-2 rounded-xl border border-emerald-400/30 bg-emerald-400/10 px-4 py-3 text-sm font-semibold text-emerald-300 transition hover:bg-emerald-400/20">
                  <Check className="h-4 w-4" /> Got it
                </button>
              </div>
            </div>
          )}
          <p className="mt-6 flex items-center justify-center gap-1.5 text-xs text-stone-600">
            <Keyboard className="h-3.5 w-3.5" /> Ctrl/⌘ + Enter to reveal
          </p>
        </div>
      )}
    </div>
  );
}

function TopBar({ onBack }) {
  return (
    <button onClick={onBack}
      className="flex items-center gap-1.5 rounded-lg px-2 py-1.5 text-sm text-stone-400 transition hover:bg-stone-900 hover:text-stone-200">
      <ArrowLeft className="h-4 w-4" /> Home
    </button>
  );
}
