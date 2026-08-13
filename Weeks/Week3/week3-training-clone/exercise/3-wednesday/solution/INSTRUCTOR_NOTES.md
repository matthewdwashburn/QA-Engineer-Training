# Instructor notes — Wednesday

- **Banking:** Encourage `transfer` to reuse `withdraw`/`deposit` to avoid duplicated balance rules. `BigDecimal` is ideal but doubles are acceptable for this lab if you warn about precision.
- **Todo list:** If trainees return the internal list from `listTasks()`, discuss defensive copy vs unmodifiable view.
- **Checked exceptions:** `BankingDemo.main` may declare `throws Exception` for speed, or use try/catch—either is fine for training.
