# Exercise: Menu-Driven Console App (`Scanner` + Control Flow)

**Mode:** Implementation (Code Lab)  
**Duration:** 75–90 minutes  
**Day:** 5-friday | **Week:** 2 — Python & Java Foundations  

---

## Objective

- Build a **menu-driven** program using **`Scanner`** and **`switch`** (classic **or** expression form).
- Validate **invalid menu choices** and **non-numeric** input where numbers are required.
- Use **`if`/`else`**, **ternary** at least once, and clean **`default`** / unknown handling.

---

## Prerequisites

| Concept | Source |
|---------|--------|
| Scanner | `written/4-thursday/reading-from-the-console-scanner.md` |
| Control flow | `written/5-friday/control-flow.md`, `conditional-statements.md` |
| Demo | `demos/5-friday/code/DemoControlFlow.java` |

---

## Scenario

**QA Ticket Console** — loop until user chooses **Quit**:

1. **List tickets** — print 3 hard-coded ticket strings from an **array**.
2. **Add priority** — ask for ticket index + priority `1-3`; validate range.
3. **Summary** — print count of tickets using **ternary** or compact expression for pluralization (`1 ticket` vs `N tickets`).
4. **Quit** — exit loop with goodbye message.

Invalid menu → re-prompt. Use **`nextLine()`** carefully after **`nextInt()`** (consume newline).

---

## Core Tasks

1. Implement **`starter_code/TicketMenu.java`** (skeleton provided).
2. Keep **one** `Scanner` for `System.in`; close at end or use try-with-resources.

---

## Definition of Done

- [ ] Menu loops until quit; invalid choices do not crash.
- [ ] At least one **`switch`** on menu command.
- [ ] At least one **ternary** visible in code (summary line counts OK).
- [ ] **Scanner** newline pitfall handled after reading integers.

---

## Stretch

- **`switch` expression** (Java 14+) for menu dispatch.
- Store priorities in a **parallel `int[]`** aligned with ticket titles.

---

## References

- Written: `content/Week2-Python-Java/written/5-friday/control-flow.md`
