# Lab: Java File I/O — Log File Analyzer

## Overview

A QE team has been running automated tests overnight. The tests write a raw log file of results. Your job is to build a **Log File Analyzer** that:

1. **Reads** the raw log file line by line.
2. **Parses and filters** entries by status (`PASS`, `FAIL`, `SKIP`).
3. **Writes** a structured summary report to a new file.
4. **Appends** a timestamped run-summary line to a persistent audit log.

This exercise directly applies the concepts from today's written topics:
- `opening-files-file-handling.md` — Path inspection and existence checks.
- `read-files-write-create.md` — Reading with `BufferedReader` / `Files.readAllLines()`, writing with `Files.write()` and `APPEND`.

---

## Learning Objectives

By completing this lab you will be able to:
- Represent file paths safely using `Path` and `Paths.get()`.
- Guard file operations with `Files.exists()` before reading.
- Choose the right read API (`BufferedReader` vs `Files.readAllLines()`) for the job.
- Write and create output files using `Files.newBufferedWriter()`.
- Append to an existing log file using `StandardOpenOption.APPEND`.
- Use `try-with-resources` correctly to prevent resource leaks.

---

## The Scenario

Navigate to `starter_code/`. You will find:

```
starter_code/
  LogAnalyzer.java           ← your main class — contains TODO blocks
  data/
    test-run.log             ← sample raw log (pre-provided, do not modify)
  output/                    ← your code will create files here
```

The raw log file (`data/test-run.log`) has entries in the format:

```
[PASS] LoginTest - 120ms
[FAIL] CheckoutTest - 340ms | Reason: NullPointerException on line 87
[PASS] SearchTest - 95ms
[SKIP] PaymentTest - 0ms | Reason: dependency not available
[FAIL] ProfileUpdateTest - 210ms | Reason: AssertionError: expected 200 but was 404
[PASS] LogoutTest - 88ms
```

---

## Core Tasks

### Task 1 — Validate the Input File (20 min)
**File:** `LogAnalyzer.java` → `validateInput()` method

Implement the method so that it:
- Uses `Paths.get()` to construct the path to `data/test-run.log`.
- Checks `Files.exists()` and `Files.isRegularFile()` before proceeding.
- Prints the file size in bytes and last-modified timestamp using `BasicFileAttributes`.
- Throws an `IllegalStateException` with a descriptive message if the file is missing.

**Expected console output (approx):**
```
[OK] Log file found: data/test-run.log
     Size: 312 bytes | Last modified: 2026-04-20T...
```

---

### Task 2 — Read and Parse the Log (30 min)
**File:** `LogAnalyzer.java` → `parseLog()` method

Implement the method so that it:
- Reads every line from `data/test-run.log` using `Files.newBufferedReader()` inside a `try-with-resources`.
- Extracts the status tag at the start of each line (`[PASS]`, `[FAIL]`, `[SKIP]`).
- Populates three `List<String>` fields: `passList`, `failList`, `skipList`.

**Tip:** `line.startsWith("[FAIL]")` is all you need — no regex required.

---

### Task 3 — Write the Summary Report (30 min)
**File:** `LogAnalyzer.java` → `writeSummaryReport()` method

Implement the method so that it:
- Creates `output/` directory if it does not exist (`Files.createDirectories()`).
- Writes `output/summary-report.txt` using `Files.newBufferedWriter()`.
- The report must contain:
  - A header with the run timestamp.
  - The total count of PASS / FAIL / SKIP.
  - All FAIL entries listed under a `## FAILURES` section.
  - All SKIP entries listed under a `## SKIPPED` section.

**Expected `output/summary-report.txt`:**
```
=== Log Analyzer Report ===
Generated: 2026-04-20T15:30:00

Total Tests : 6
  PASS      : 3
  FAIL      : 2
  SKIP      : 1

## FAILURES
  [FAIL] CheckoutTest - 340ms | Reason: NullPointerException on line 87
  [FAIL] ProfileUpdateTest - 210ms | Reason: AssertionError: expected 200 but was 404

## SKIPPED
  [SKIP] PaymentTest - 0ms | Reason: dependency not available
```

---

### Task 4 — Append to the Audit Log (20 min)
**File:** `LogAnalyzer.java` → `appendAuditEntry()` method

Implement the method so that it:
- Appends a single line to `output/audit.log` using `StandardOpenOption.APPEND` and `CREATE`.
- The line format: `[YYYY-MM-DDTHH:MM:SS] Run complete | PASS=3 FAIL=2 SKIP=1`
- Run the analyzer **twice** and verify that `audit.log` has **two entries** — one per run.

---

## Stretch Goals

If you finish early, attempt one or both:

**Stretch A — Streams:** Refactor `parseLog()` to use `Files.readAllLines()` + `.stream().filter()` + `.collect(Collectors.toList())` instead of `BufferedReader`. Compare the two approaches.

**Stretch B — Path composition:** Refactor all hard-coded path strings to be composed from a single `BASE_DIR` constant using `base.resolve("data/test-run.log")`. This makes the tool portable.

---

## Running Your Solution

```bash
# From the starter_code/ directory:
javac LogAnalyzer.java
java LogAnalyzer
```

Then inspect:
- `output/summary-report.txt` — the formatted report.
- `output/audit.log` — your appended audit entries.

---

## Definition of Done

- [ ] `validateInput()` — prints file metadata; throws if missing.
- [ ] `parseLog()` — correctly populates all three lists from the sample log.
- [ ] `writeSummaryReport()` — output file matches the expected format above.
- [ ] `appendAuditEntry()` — a second run appends a second line (not overwrites).
- [ ] All file operations use `try-with-resources` — no bare `close()` calls.
- [ ] Program exits gracefully with a clear error message if `test-run.log` is missing.

---

## Tips

- `LocalDateTime.now()` (from `java.time`) gives you the timestamp.
- `writer.newLine()` writes the OS-correct line separator — prefer it over `"\n"`.
- Check `Files.exists(output.getParent())` before `createDirectories()` — or just always call `createDirectories()`, it's idempotent.
