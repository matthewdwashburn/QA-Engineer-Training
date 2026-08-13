# End-to-End Tests

Browser-driven acceptance tests for the Revature Expense Manager, written with
**Cucumber (Gherkin)** + **Selenium WebDriver** + **JUnit 5**.

These are true end-to-end tests: they drive a real Chrome browser against the
running React frontend, which talks to both live backends and a real SQLite
database. Nothing is mocked or stubbed.

**Current coverage: 10 scenarios across 5 feature files — 3 happy paths, 2 sad paths.**

---

## Running the tests

### 1. Start all three services

The tests drive the real application, so every tier must be running first. Use a
separate terminal for each.

**Employee backend — Flask, port 5000**
```bash
cd P1-Group6
source venv/bin/activate
python employee_app/main.py
```

**Manager backend — Javalin, port 7001**
```bash
cd P1-Group6/manager-app
mvn exec:java -Dexec.mainClass="com.revature.expensemanager.Main"
```

**Frontend — Vite, port 5173**
```bash
cd P1-Group6/frontend
npm run dev
```

### 2. Reset the database (recommended)

The database is a persistent file (`database/expense_manager.db`) — it is **not**
reset between runs. The suite is designed to clean up after itself, but if a run
fails partway through it can leave an orphaned expense behind, which may cause
confusing failures on the next run.

```bash
cd P1-Group6
python database/init_db.py
```

This drops the file, rebuilds the schema, and reseeds users and expenses.

### 3. Run the suite

```bash
cd P1-Group6/e2e-tests
mvn test
```

A Chrome window opens and closes for each scenario — expect the full run to take
around 30 seconds. Chrome does not need to be installed manually; Selenium 4.6+
ships **Selenium Manager**, which downloads and matches the correct chromedriver
automatically.

### Running a subset

```bash
# One feature file
mvn test -Dcucumber.features=src/main/resources/features/employee-expense-lifecycle.feature

# Validate step wiring without opening a browser (see "Adding a new scenario")
mvn test -Dcucumber.execution.dry-run=true
```

---

## Test users

Seeded by `database/init_db.py`. All use the password `password`.

| Username | Role     |
|----------|----------|
| `brian`  | employee |
| `landon` | employee |
| `siri`   | manager  |

---

## What's covered

### Happy paths

| Feature file | Scenario |
|---|---|
| `employee-expense-lifecycle.feature` | Employee logs in, submits an expense, confirms it's pending, edits it, deletes it |
| `manager-review-lifecycle.feature` | Manager views the pending queue and approves / denies an expense (Scenario Outline — runs twice, once per decision) |
| `cross-role-lifecycle.feature` | Employee submits → manager approves → employee sees it move from pending into history with the decision and comment |

### Sad paths

| Feature file | Scenario |
|---|---|
| `authentication-route-protection.feature` | Unauthenticated direct-URL access to the employee dashboard redirects to a recoverable sign-in state |
| | Same for the manager dashboard |
| | Manager credentials rejected on the employee login |
| | Employee credentials rejected on the manager login |
| | Wrong password rejected with a clear error |
| `expense-validation-recovery.feature` | Invalid expense data is rejected with a clear error, no record is created, and a corrected resubmission succeeds |

---