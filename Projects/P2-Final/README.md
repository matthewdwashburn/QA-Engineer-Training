# Revature Expense Manager
![Python](https://img.shields.io/badge/Python-3776AB?style=for-the-badge&logo=python&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-07405E?style=for-the-badge&logo=sqlite&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)
![React](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)
![Flask](https://img.shields.io/badge/Flask-000000?style=for-the-badge&logo=flask&logoColor=white)
![Javalin](https://img.shields.io/badge/Javalin-0B4F6C?style=for-the-badge)
![pytest](https://img.shields.io/badge/pytest-0A9EDC?style=for-the-badge&logo=pytest&logoColor=white)
![JUnit 5](https://img.shields.io/badge/JUnit_5-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![Allure](https://img.shields.io/badge/Allure-EA4AAA?style=for-the-badge&logo=allure&logoColor=white)
![Apache JMeter](https://img.shields.io/badge/Apache_JMeter-D22128?style=for-the-badge&logo=apachejmeter&logoColor=white)

The Revature Expense Manager is a React web application backed by a Python/Flask employee API and a Java/Javalin manager API. Both applications use a shared SQLite data model for the complete expense lifecycle, with a quality strategy designed around the Phase 2 requirements: isolated unit tests, authenticated API contracts, database integration checks, performance plans, and traceable defect regression coverage.

## Contents

- [Overview](#overview)
- [Setup and installation](#setup-installation)
- [How to run the web platform](#run-web-platform)
- [Environment and database modes](#environment-database-modes)
- [Testing](#testing)
- [Database schema](#database-schema)
- [API reference](#api-reference)
- [Logs and exports](#logs-exports)

<a id="overview"></a>
## 1. Overview 📌
> Revature Expense Manager delivers a unified expense workflow through two coordinated applications: a Python Employee App for submission and self-service updates, and a Java Manager App for review, approval, and reporting. Both applications operate against a shared SQLite database to keep records consistent across the full lifecycle.

## 2. Features & Stretch Goals ✅
- React/Vite frontend serving dedicated employee and manager workflows.
- JWT-based session handling in both app contexts for secure, role-aligned access control.
- Password hashing with bcrypt before persisted user credentials are stored.
- Structured Java logging (console + file appender) for operational visibility during live runs.
- Purpose-built quality gates: pytest and JUnit/Mockito target business rules in isolation, while real HTTP contract tests prove each API's status codes, payloads, authentication, authorization, and persistence boundaries.
- Coverage-focused delivery baseline: **93% Python branch coverage** and **86% Java branch coverage**, with HTML reports generated on every full suite run.
- JMeter plans exercise manager login, read, and write workloads against a dedicated seeded database rather than demo data.
- Shared schema design to enable coordinated approvals, auditability, and downstream export/reporting paths.

## 3. User Flow 👥
- Employee App (Python): authenticate, submit expenses, view ledger, view pending items, edit pending expenses, delete pending expenses.
- Manager App (Java): authenticate as manager, review pending submissions, approve or deny expenses, and generate manager-facing reporting outputs.

## 4. Tech Stack & Architecture 🏗️
- Frontend: React/Vite browser application communicating with both local APIs.
- Employee API: Python/Flask application for employee authentication and expense management.
- Manager API: Java/Maven/Javalin application for manager authentication, approvals, reports, and CSV export.
- Data Layer: Shared SQLite database (`database/expense_manager.db`) used by both applications.
- Security: JWT authentication and bcrypt password hashing.
- Quality Architecture: pytest + pytest-mock and JUnit 5 + Mockito isolate services and controllers; temporary SQLite fixtures validate repository behavior; focused RestAssured/requests contracts verify the live Flask and Javalin HTTP surfaces.
- Coverage: branch-aware Python coverage and JaCoCo Java coverage produce a **93% / 86% branch-coverage baseline** for the employee and manager backends, respectively.
- Observability: Logback-backed Java logs written to `manager-app/logs/manager-app.log`.

<a id="setup-installation"></a>
## 5. Setup & Installation ⚙️
<details>
<summary><strong>Expand setup instructions</strong></summary>

### Prerequisites
- Python 3.11+ with `venv`
- Node.js 20+ and npm
- Java 17+
- Maven 3.9+
- Allure CLI to view test reports

### Step 1: Configure local environment files
Create `employee_app/.env` from [employee_app/.env.example](employee_app/.env.example):

```env
JWT_SECRET=replace-with-a-local-employee-secret
JWT_EXPIRATION_HOURS=24
# DATABASE_ENV=development
```

Create `manager-app/.env`:

```env
JWT_SECRET=replace-with-a-local-manager-secret
JWT_EXPIRATION_HOURS=24
# DATABASE_ENV=development
```

`DATABASE_ENV` is optional. Omit it, comment it out, or set it to `development` for normal local work.

### Step 2: Create the Python environment and install dependencies
From the repository root:

```bash
python3 -m venv .venv
.venv/bin/pip install -r requirements.txt
```

### Step 3: Install frontend dependencies

```bash
cd frontend
npm ci
cd ..
```

### Step 4: Initialize and seed the development database

```bash
.venv/bin/python database/init_db.py
```

This recreates `database/expense_manager.db`, applies the schema and seed data, and creates the local bcrypt-backed users.

### Step 5: Resolve Java dependencies
From `manager-app`:

```bash
mvn -q validate
```

</details>

<a id="run-web-platform"></a>
## 6. How to Run the Web Platform 🌐

Start these three processes in separate terminals after completing setup.

| Process | Run from | Command | Local address |
| --- | --- | --- | --- |
| Employee backend | `employee_app` | `python main.py` | `http://127.0.0.1:5000` |
| Manager backend | `manager-app` | `mvn exec:java "-Dexec.mainClass=com.revature.expensemanager.Main"` | `http://127.0.0.1:7001` |
| React frontend | `frontend` | `npm run dev` | Vite prints the local URL, normally `http://127.0.0.1:5173` |

The frontend calls both backend URLs directly. Start both APIs before opening the Vite URL in a browser. When using the virtual environment created during setup, activate it before running `python main.py` or invoke it explicitly as `../.venv/bin/python main.py`.

<details>
<summary><strong>Frontend production build and local seed accounts</strong></summary>

```bash
cd frontend
npm run build
npm run preview
```

| Role | Username | Password |
| --- | --- | --- |
| Employee | `brian` | `password` |
| Employee | `landon` | `password` |
| Manager | `siri` | `password` |

</details>

<a id="environment-database-modes"></a>
## 7. Environment and Database Modes 🗃️
| Mode | `DATABASE_ENV` | Database | Intended use |
| --- | --- | --- | --- |
| Development | omitted, commented out, or `development` | `database/expense_manager.db` | Normal frontend/API work and demos |
| JMeter testing | `testing` | `database/expense_manager_test.db` | Isolated manager load testing |
| Automated tests | no setting needed | Temporary SQLite databases | pytest and Java integration/repository tests |

> [!WARNING]
> When the manager API starts with `DATABASE_ENV=testing`, it clears expenses and approvals in `expense_manager_test.db` and seeds 5,000 load-test expenses. Never point load testing at the development database.

<details>
<summary><strong>Switch to the JMeter test database</strong></summary>

1. Create a fresh development database if necessary:

	```bash
	.venv/bin/python database/init_db.py
	```

2. Make or refresh the isolated testing copy:

	```bash
	cp database/expense_manager.db database/expense_manager_test.db
	```

3. In **`manager-app/.env`**, add:

	```env
	DATABASE_ENV=testing
	```

4. Start the manager backend. Its startup seeder prepares the test database for the JMeter plans.
5. When finished, remove, comment out, or change `DATABASE_ENV=testing` to `development` before starting the normal application again.

The employee backend also recognizes `DATABASE_ENV`; leave its environment in development mode unless you intentionally need it to use the isolated database.

</details>

<a id="testing"></a>
## 8. Testing 🧪

The test suites turn the Phase 2 quality requirements into executable evidence. Fast unit/controller tests isolate business rules with mocks; direct SQLite tests validate persistence behavior; real-HTTP contract suites verify status codes, response shapes, invalid-input handling, JWT authentication, role authorization, and data ownership; and end-to-end API journeys exercise complete expense workflows. The current quality baseline is **93% Python branch coverage** and **86% Java branch coverage**.

<details>
<summary><strong>pytest — employee application</strong></summary>

Run the full employee suite from the repository root:

```bash
.venv/bin/pytest
```

The suite targets Flask controllers and services with mocks, repository behavior with temporary SQLite databases, and live contract tests using real HTTP requests. This keeps rule-level failures fast to diagnose while preserving a focused safety net for externally visible API behavior.

| Marker | Purpose | Example |
| --- | --- | --- |
| `unit` | Service/controller logic with mocks | `.venv/bin/pytest -m unit` |
| `db` | Repository behavior against temporary SQLite | `.venv/bin/pytest -m db` |
| `api` | API and journey coverage | `.venv/bin/pytest -m api` |
| `contract` | Real HTTP, cookie/JWT, route wiring, and persistence-boundary contracts | `.venv/bin/pytest -m contract` |
| `smoke` | Critical happy paths | `.venv/bin/pytest -m smoke` |
| `negative` | Expected rejection and error behavior | `.venv/bin/pytest -m negative` |
| `security` | JWT, role, authorization, ownership, and disclosure behavior | `.venv/bin/pytest -m security` |
| `regression` | Fixed defects, including KAN-85 | `.venv/bin/pytest -m regression` |
| `edge_case` | Boundary and minor behavior | `.venv/bin/pytest -m edge_case` |

Useful combinations:

```bash
# Fast feedback: units, repositories, and journeys; excludes live HTTP contracts.
.venv/bin/pytest -m "not contract"

# Authentication/authorization contracts over real HTTP.
.venv/bin/pytest -m "contract and security"
```

</details>

<details>
<summary><strong>Maven and JUnit 5 — manager application</strong></summary>

Run the complete manager suite from `manager-app`:

```bash
mvn clean test
```

The JUnit suite isolates service rules with Mockito, validates DAO behavior against SQLite, and uses RestAssured integration contracts against a running Javalin application. It verifies both success and rejection paths without duplicating every validation permutation at the HTTP layer.

| Tag | Purpose | Example |
| --- | --- | --- |
| `unit` | Mockito/service-level behavior | `mvn test -Dgroups=unit` |
| `api` | Controller and API behavior | `mvn test -Dgroups=api` |
| `integration` | Real Javalin HTTP with SQLite wiring | `mvn test -Dgroups=integration` |
| `db` | Direct JDBC/SQLite DAO checks | `mvn test -Dgroups=db` |
| `smoke` | Critical happy paths | `mvn test -Dgroups=smoke` |
| `negative` | Expected errors and rejections | `mvn test -Dgroups=negative` |
| `security` | Authentication, authorization, and injection defenses | `mvn test -Dgroups=security` |
| `edge_case` | Boundary and minor behavior | `mvn test -Dgroups=edge_case` |

</details>

<details>
<summary><strong>Cucumber and Selenium — browser E2E application tests</strong></summary>

These tests drive a real Chrome browser through the React frontend against both live APIs and the shared SQLite database. They cover employee expense management, manager review decisions, cross-role lifecycle visibility, protected routes, wrong-role authentication, and validation recovery.

Start all three application services in separate terminals from the repository root:

```bash
# Employee API
cd employee_app
../.venv/bin/python main.py
```

```bash
# Manager API
cd manager-app
mvn exec:java "-Dexec.mainClass=com.revature.expensemanager.Main"
```

```bash
# React frontend
cd frontend
npm run dev
```

Optionally reset the shared database before a run to restore the seeded state:

```bash
.venv/bin/python database/init_db.py
```

Run the E2E suite from `e2e-tests`:

```bash
cd e2e-tests
mvn test
```

The scenarios publish to the shared `allure-results/` directory under **Employee - End-to-End Tests**, **Manager - End-to-End Tests**, or **Cross Role Lifecycle**. They include `e2e` workflow tags, severity, and applicable smoke, security, negative, or regression labels.

</details>

<details>
<summary><strong>JMeter — manager API load testing</strong></summary>

The manager JMeter plans are in [manager-app/src/test/jmeter](manager-app/src/test/jmeter):

- `manager_login_test.jmx`
- `manager_read_performance_test.jmx`
- `manager_write_performance_test.jmx`

1. Follow the [JMeter database switch](#environment-database-modes) steps and set `DATABASE_ENV=testing` in `manager-app/.env`.
2. Start the manager backend on port `7001`.
3. In another terminal, run all configured plans:

	```bash
	cd manager-app
	mvn verify
	```

4. Review generated JMeter output under `manager-app/target/jmeter`.
5. Stop the backend and restore `manager-app/.env` to development mode.

</details>

<details>
<summary><strong>Allure — combined test report</strong></summary>

| Evidence | Location |
| --- | --- |
| Python coverage HTML | `htmlcov/index.html` after pytest |
| Java JaCoCo coverage HTML | `manager-app/target/site/jacoco/index.html` after Maven tests |
| JMeter output | `manager-app/target/jmeter` after `mvn verify` |
| Shared raw Allure results | `allure-results/` |

The reports make the coverage baseline inspectable: **93% branch coverage for pytest** and **86% branch coverage for Java/JaCoCo**. Allure then combines the functional evidence from both backends into one delivery dashboard with suite, feature, tag, and regression context.

The manager Maven suite, pytest suite, and Cucumber/Selenium E2E suite all write Allure result files into the repository-level `allure-results/` directory. For a complete report, clear it once, run the relevant suites, then view or generate the report:

```bash
rm -rf allure-results
mkdir allure-results

cd manager-app
mvn clean test
cd ..

.venv/bin/pytest

cd e2e-tests
mvn test
cd ..

allure serve allure-results
```

`allure serve` opens a temporary local dashboard. To generate a static deliverable instead:

```bash
allure generate allure-results --clean -o allure-report
```

Allure's suite labels distinguish the Manager, Employee, and E2E layers in one dashboard. Running fewer suites produces a valid but partial report.

</details>

<a id="database-schema"></a>
## 9. Database Schema 🗃️
<details>
<summary><strong>Expand schema tables</strong></summary>

### users
| Column | Type | Constraints | Description |
|---|---|---|---|
| id | INTEGER | PRIMARY KEY, AUTOINCREMENT | Unique user identifier |
| username | TEXT | UNIQUE, NOT NULL | Login username |
| password | TEXT | NOT NULL | Hashed password value |
| role | TEXT | NOT NULL | User role (`employee` or `manager`) |

### expenses
| Column | Type | Constraints | Description |
|---|---|---|---|
| id | INTEGER | PRIMARY KEY, AUTOINCREMENT | Unique expense identifier |
| userId | INTEGER | NOT NULL, FK -> users.id | Expense owner |
| amount | REAL | NOT NULL | Expense amount |
| description | TEXT | NOT NULL | Expense narrative |
| category | TEXT | NOT NULL, CHECK | Expense classification |
| date | TEXT | NOT NULL | Expense date (ISO string) |

Allowed `category` values: `TRAVEL`, `MEALS`, `LODGING`, `OFFICE_SUPPLIES`, `EQUIPMENT`, `SOFTWARE`, `TRAINING`, and `OTHER`.

### approvals
| Column | Type | Constraints | Description |
|---|---|---|---|
| id | INTEGER | PRIMARY KEY, AUTOINCREMENT | Unique approval identifier |
| expenseId | INTEGER | NOT NULL, UNIQUE, FK -> expenses.id | Target expense |
| status | TEXT | NOT NULL | Approval state (`pending`, `approved`, `denied`) |
| reviewer | INTEGER | FK -> users.id | Manager reviewer id |
| comment | TEXT | Nullable | Manager decision note |
| review_date | TEXT | Nullable | Review timestamp/date |

</details>

<a id="api-reference"></a>
## 10. API Reference 🔌

<details>
<summary><strong>Expand API Endpoints & JSON Shapes</strong></summary>

*Select an application below to view its specific routing and JSON schemas:*


<details>
<summary><strong>Employee App API (Python Flask)</strong></summary>

### Employee App API (Python Flask)

Base route prefixes are defined in blueprints:
- Auth: `/auth`
- Expenses: `/expenses`

#### POST /auth/login
- Description: Authenticates an employee and sets `jwt_token` as an HTTP-only cookie.
- Request Body JSON:

```json
{
	"username": "string",
	"password": "string"
}
```

- Success Response JSON (200):

```json
{
	"message": "Welcome back, <username>!",
	"user": {
		"id": 1,
		"username": "string",
		"role": "Employee"
	}
}
```

#### POST /auth/logout
- Description: Clears the employee `jwt_token` cookie.
- Request Body JSON: None
- Success Response JSON (200):

```json
{
	"message": "Logged out successfully"
}
```

#### POST /expenses/submit
- Description: Creates a new expense for the authenticated employee.
- Request Body JSON:

```json
{
	"amount": "number|string",
	"description": "string",
	"category": "TRAVEL|MEALS|LODGING|OFFICE_SUPPLIES|EQUIPMENT|SOFTWARE|TRAINING|OTHER",
	"expense_date": "YYYY-MM-DD"
}
```

- Success Response JSON (201):

```json
{
	"message": "Expense submitted successfully and is now pending manager review.",
	"next_step": "string",
	"expense_id": 10,
	"amount": "22.00"
}
```

#### GET /expenses/ledger
- Description: Returns pending expenses and non-pending history for the authenticated employee.
- Request Body JSON: None
- Success Response JSON (200):

```json
{
	"pending_expenses": [
		{
			"expense_id": 1,
			"user_id": 1,
			"amount": "22.00",
			"description": "string",
			"category": "string",
			"expense_date": "YYYY-MM-DD",
			"status": "pending|approved|denied",
			"manager_comment": "string|null",
			"review_date": "YYYY-MM-DD|null"
		}
	],
	"expense_history": [
		{
			"expense_id": 2,
			"user_id": 1,
			"amount": "10.00",
			"description": "string",
			"category": "string",
			"expense_date": "YYYY-MM-DD",
			"status": "approved|denied",
			"manager_comment": "string|null",
			"review_date": "YYYY-MM-DD|null"
		}
	],
	"message": "Ledger retrieved successfully. Pending and history are included.",
	"summary": {
		"pending_count": 1,
		"history_count": 1
	},
	"next_step": "string"
}
```

#### GET /expenses/pending
- Description: Returns only pending expenses for the authenticated employee.
- Request Body JSON: None
- Success Response JSON (200):

```json
{
	"pending_expenses": [
		{
			"expense_id": 1,
			"user_id": 1,
			"amount": "22.00",
			"description": "string",
			"category": "string",
			"expense_date": "YYYY-MM-DD",
			"status": "pending",
			"manager_comment": "string|null",
			"review_date": "YYYY-MM-DD|null"
		}
	]
}
```

#### PUT /expenses/{expense_id}
- Description: Updates amount and description of an owned pending expense.
- Request Body JSON:

```json
{
	"amount": "number|string",
	"description": "string"
}
```

- Success Response JSON (200):

```json
{
	"message": "Expense updated successfully.",
	"next_step": "string"
}
```

#### DELETE /expenses/{expense_id}
- Description: Deletes an owned pending expense.
- Request Body JSON: None
- Success Response JSON (200):

```json
{
	"message": "Expense deleted successfully.",
	"next_step": "string"
}
```

</details>

&nbsp;

<details>
<summary><strong>Manager App API (Java Javalin)</strong></summary>

### Manager App API (Java Javalin)

Routes are defined in `manager-app/src/main/java/com/revature/expensemanager/Main.java`.

Auth behavior:
- `Authorization: Bearer <token>` with a manager role is required for `/expenses/*` and `/reports/*`.

#### POST /login
- Description: Authenticates manager credentials and returns a manager JWT.
- Request Body JSON (`LoginRequest`):

```json
{
	"username": "string",
	"password": "string"
}
```

- Success Response JSON (200) (`LoginResponse`):

```json
{
	"id": 1,
	"username": "string",
	"role": "manager",
	"token": "jwt-string"
}
```

#### GET /expenses/pending
- Description: Returns all pending expenses for manager review.
- Request Body JSON: None
- Success Response JSON (200):

```json
[
	{
		"id": 1,
		"userId": 2,
		"amount": 49.99,
		"description": "string",
		"category": "string",
		"date": "YYYY-MM-DD"
	}
]
```

#### PUT /expenses/{id}/review
- Description: Approves or denies a specific expense.
- Request Body JSON (`ApprovalRequest`):

```json
{
	"status": "approved|denied",
	"comment": "string"
}
```

- Success Response JSON (200):

```json
"Expense reviewed successfully"
```

#### GET /reports/employee?userId={id}[&export=true]
- Description: Returns expenses for one employee by user id. When `export=true`, response includes `Report-File` header with CSV path.
- Request Body JSON: None
- Success Response JSON (200):

```json
[
	{
		"id": 1,
		"userId": 2,
		"amount": 49.99,
		"description": "string",
		"category": "string",
		"date": "YYYY-MM-DD"
	}
]
```

#### GET /reports/category?category={value}[&export=true]
- Description: Returns expenses for one category. When `export=true`, response includes `Report-File` header.
- Request Body JSON: None
- Success Response JSON (200): array of `Expense` objects (same shape as above).

#### GET /reports/date?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD[&export=true]
- Description: Returns expenses within inclusive date range. When `export=true`, response includes `Report-File` header.
- Request Body JSON: None
- Success Response JSON (200): array of `Expense` objects (same shape as above).

#### GET /employees
- Description: Returns employee lookup values used for manager reporting workflows.
- Request Body JSON: None
- Success Response JSON (200) (`EmployeeSummary[]`):

```json
[
	{
		"id": 1,
		"username": "string"
	}
]
```

</details>

</details>

<a id="logs-exports"></a>
## 11. Logs and Exports

- Manager runtime logs: `manager-app/logs/manager-app.log`
- Manager logging configuration: `manager-app/src/main/resources/logback.xml`
- Manager CSV exports: `manager-app/reports/` when the backend runs from `manager-app`

<details>
<summary><strong>Troubleshooting</strong></summary>

| Symptom | Check |
| --- | --- |
| An API will not start | Confirm that its own `.env` has `JWT_SECRET` and `JWT_EXPIRATION_HOURS`. |
| The frontend shows network errors | Confirm Flask runs on port 5000 and Javalin runs on port 7001 before starting Vite. |
| JMeter changed normal data | Stop the backend, restore development mode, recreate the development database, and use `expense_manager_test.db` next time. |
| Python import failures | Use `.venv/bin/python` and install [requirements.txt](requirements.txt) into that environment. |
| Allure report only includes one application | Clear results once, run Maven then pytest, and only then launch Allure. |
| Frontend dependency issue | Run `npm ci` in `frontend`; do not run `npm audit fix --force`. |

</details>
