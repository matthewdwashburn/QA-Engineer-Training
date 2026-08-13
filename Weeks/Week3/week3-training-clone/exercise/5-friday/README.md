# Week 3 — Friday exercises

**Epic tie-in:** Logging levels (`written/5-friday/java-logback-logging-levels.md`) and interview practice (`interview-preparation.md`).

**Time:** ~1.5–2.5 hours.

---

## Lab 1 — Logging levels (`exercise_logging`)

**Mode:** Implementation (extends Thursday artifact).

### Tasks

1. Open your **Thursday pair submission** (or use `starter_code/logging_stub/` if you did not finish Thursday—minimal apps provided).
2. Introduce **at least two named loggers** (e.g. `pair.a.words`, `pair.b.tasks`) or packages matching your structure.
3. Update **`logback.xml`**:
   - Different **levels** per logger (e.g. one at `DEBUG`, one at `WARN`).
   - Customize **pattern** to include **level** and **logger name** (and optionally **thread**).
4. Add **`WARN`** when input is empty or degenerate; **`DEBUG`** inside tight loops; **`INFO`** at lifecycle boundaries.
5. Capture **one screenshot** or paste **10 lines** of log output into `LOG_SAMPLE.md` showing level filtering working after you change a threshold and rerun.

### Definition of done

- `LOG_SAMPLE.md` explains **before/after** threshold change in **2 sentences**.
- File appender still produces a file under `logs/` (or document path).

### References

- `demos/5-friday/code/DemoLoggingLevels.java`, `demos/5-friday/code/logback.xml`

---

## Lab 2 — Mock interview practice (`exercise_interview_practice`)

**Mode:** Conceptual / behavioral (pair).

### Setup

Pair up (can be same or different partner from Thursday). Each pair runs **two rounds** (~12 min each):

1. **Round 1:** Person X = interviewer, Y = candidate.
2. **Round 2:** Swap roles.

### Deliverables

1. Complete `templates/CANDIDATE_PREP.md` **individually** before round 1.
2. Complete `templates/INTERVIEW_SCORECARD.md` **per round** (interviewer fills while listening).
3. Together write **3 bullets** in `RETRO.md` (provided empty template) on what to improve next time.

### Question bank

Interviewer picks **at least 3 technical** from `templates/QUESTION_BANK.md` + **1 behavioral** STAR prompt.

### Definition of done

- All three template files filled and submitted with the week’s bundle or repo.

---

## Submission

Zip or repo folder `week3-friday/` containing logging evidence + interview templates, **or** merge into your main Week 3 repo with clear paths listed in a top-level `FRIDAY_SUBMISSION.md` (optional).
