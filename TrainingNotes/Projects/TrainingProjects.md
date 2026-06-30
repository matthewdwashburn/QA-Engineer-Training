# Training Projects 1 — Python Flask API & Java Javalin API

Two full-stack backend projects: a Python expense management REST API and a Java employee management REST API.

---

## Architecture Overview — Layered Design

Both projects use the same layered pattern:

```
Controller  ← handles HTTP in/out, calls service
Service     ← business logic, validation
Repository/DAO  ← all database access
Model       ← plain data classes (no logic)
```

This separation means the database layer can change without touching controllers, and business rules live in one place.

---

# Python Project — Employee Expense Management API

## `@dataclass` — Auto-Generated Model Classes

`@dataclass` generates `__init__`, `__repr__`, and `__eq__` automatically from annotated fields:

```python
from dataclasses import dataclass
from typing import Optional

@dataclass
class Expense:
    id: Optional[int]
    user_id: int
    amount: float
    description: str
    date: str
```

### `__post_init__` — validation after auto-generated `__init__`

```python
@dataclass
class User:
    id: Optional[int]
    username: str
    password: str
    role: str

    def __post_init__(self):
        if self.role != 'Employee':
            raise ValueError("Role must be 'Employee'")
        # runs automatically after __init__ — use for validation
```

---

## Flask Blueprints

A **Blueprint** groups related routes and can be registered on the app with a `url_prefix`. Keeps controllers in separate files without circular imports.

```python
from flask import Blueprint

auth_bp = Blueprint('auth', __name__, url_prefix='/api/auth')

@auth_bp.route('/login', methods=['POST'])
def login():
    ...
```

```python
# In main.py — register blueprints on the app
app.register_blueprint(auth_bp)
app.register_blueprint(expense_bp)
# Routes become: /api/auth/login, /api/expenses, etc.
```

---

## Flask App Factory Pattern — `create_app()`

Instead of a module-level `app = Flask(__name__)`, wrap creation in a function. Makes it easier to configure for tests vs production:

```python
def create_app():
    app = Flask(__name__, static_folder='static', static_url_path='/static')
    app.config['SECRET_KEY'] = 'your-secret-key'
    app.config['JSON_SORT_KEYS'] = False

    # Wire up dependencies here, register blueprints, etc.
    app.auth_service = AuthenticationService(...)   # inject into app context
    app.register_blueprint(auth_bp)

    return app

if __name__ == '__main__':
    app = create_app()
    app.run(host='0.0.0.0', port=3000)
```

---

## `current_app` — Access Flask App Inside Blueprints

Blueprints don't have a direct reference to the app object. `current_app` is a proxy that resolves to the app at request time:

```python
from flask import current_app

def get_auth_service():
    return current_app.auth_service   # access whatever was attached in create_app()
```

---

## HTTP Cookies — `make_response` / `set_cookie`

`jsonify()` returns a response directly. `make_response()` wraps it so you can set cookies/headers before returning:

```python
from flask import make_response, jsonify

response = make_response(jsonify({'message': 'Login successful'}))

response.set_cookie(
    'jwt_token',
    token,
    httponly=True,          # JS cannot read this cookie (XSS protection)
    secure=False,           # True in production — HTTPS only
    samesite='Lax',         # CSRF protection
    max_age=24*60*60        # expire after 24 hours (seconds)
)
return response

# Expire a cookie immediately (logout):
response.set_cookie('jwt_token', '', expires=0)

# Read cookie from incoming request:
token = request.cookies.get('jwt_token')
```

---

## JWT — `PyJWT`

**JSON Web Token** — a signed, self-contained token that encodes user identity. The server signs it with a secret key; clients send it back on each request.

```python
import jwt
from datetime import datetime, timedelta

# Generate
payload = {
    'user_id': user.id,
    'username': user.username,
    'role': user.role,
    'exp': datetime.utcnow() + timedelta(hours=24),  # expiry
    'iat': datetime.utcnow()                          # issued at
}
token = jwt.encode(payload, secret_key, algorithm='HS256')

# Validate
try:
    payload = jwt.decode(token, secret_key, algorithms=['HS256'])
except jwt.ExpiredSignatureError:
    return None   # token is expired
except jwt.InvalidTokenError:
    return None   # token is tampered or malformed
```

`HS256` — HMAC-SHA256, symmetric (same key to sign and verify). Add `PyJWT` to `requirements.txt`.

---

## Auth Decorator — `@wraps` / `functools`

A reusable decorator that guards routes requiring login. `@wraps` preserves the original function's name/docstring (required for Flask route registration):

```python
from functools import wraps
from flask import request, jsonify

def require_employee_auth(f):
    @wraps(f)   # preserves f.__name__ so Flask doesn't see duplicate route names
    def decorated_function(*args, **kwargs):
        token = request.cookies.get('jwt_token')
        if not token:
            return jsonify({'error': 'Authentication required'}), 401
        user = get_auth_service().get_user_from_token(token)
        if not user or user.role != 'Employee':
            return jsonify({'error': 'Access denied'}), 403
        request.current_user = user          # attach to request for downstream use
        return f(*args, **kwargs)
    return decorated_function

# Usage — stacks on top of route decorator:
@expense_bp.route('', methods=['POST'])
@require_employee_auth
def submit_expense():
    current_user = getattr(request, 'current_user', None)
    ...
```

---

## `sqlite3` — Advanced Patterns

### `conn.row_factory = sqlite3.Row` — dict-like row access

```python
conn = sqlite3.connect(path)
conn.row_factory = sqlite3.Row   # set before any queries

cursor = conn.execute("SELECT id, username FROM users WHERE id = ?", (user_id,))
row = cursor.fetchone()
row['id']        # access by column name instead of row[0]
row['username']
```

### `cursor.lastrowid` — get auto-generated primary key

```python
cursor = conn.execute("INSERT INTO users (username) VALUES (?)", (username,))
new_id = cursor.lastrowid   # the ID the DB assigned
```

### `cursor.rowcount` — check if update/delete did anything

```python
cursor = conn.execute("DELETE FROM expenses WHERE id = ?", (expense_id,))
return cursor.rowcount > 0   # False if no rows matched
```

### `with conn:` — auto-commit context manager

```python
with self.db_connection.get_connection() as conn:
    conn.execute("INSERT ...")
    conn.commit()   # commit inside the with block
```

---

## SQL — `CREATE TABLE IF NOT EXISTS` / `FOREIGN KEY` / `JOIN`

```sql
-- Safe table creation (won't error if table already exists)
CREATE TABLE IF NOT EXISTS expenses (
    id INTEGER PRIMARY KEY,
    user_id INTEGER NOT NULL,
    amount REAL NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

-- JOIN — combine rows from two tables on a matching column
SELECT e.id, e.amount, a.status
FROM expenses e
JOIN approvals a ON e.id = a.expense_id
WHERE e.user_id = ?
ORDER BY e.date DESC;
```

`FOREIGN KEY` enforces referential integrity — can't insert an `expense` with a `user_id` that doesn't exist in `users`.

---

## `os` Utilities

```python
import os

os.getenv('DATABASE_PATH', default_value)    # read env var with fallback
os.path.abspath(__file__)                    # absolute path to this file
os.path.dirname(path)                        # parent directory
os.path.join(dir, 'filename.db')             # safely join path segments
```

---

## Type Hints — `Optional`, `List`, `Dict`, `Tuple`

```python
from typing import Optional, List, Dict, Any, Tuple

def find_by_id(self, user_id: int) -> Optional[User]: ...
def get_all(self) -> List[Expense]: ...
def get_payload(self) -> Optional[Dict[str, Any]]: ...
def get_with_status(self) -> Optional[Tuple[Expense, Approval]]: ...
```

`Optional[X]` = `X | None`. `Dict[str, Any]` = dict with string keys and any value. `Tuple[A, B]` = fixed-length tuple of those types.

---

## Serving Static Files

```python
app = Flask(__name__, static_folder='static', static_url_path='/static')

# Serve a specific file from the static folder
@app.route('/login')
def login_page():
    return app.send_static_file('login.html')
```

---

# Java Project — Employee Management API (Javalin)

## Javalin — Lightweight Java Web Framework

Javalin handles HTTP routing. Configured with a lambda, started on a port:

```java
import io.javalin.Javalin;

var app = Javalin.create(config -> {
    config.routes.get("/hello", ctx -> ctx.result("Hello World"));
    config.routes.post("/login", ac.loginHandler);
    config.routes.get("/employees", ec.getEmployeesHandler);
    config.routes.post("/employees", ec.insertEmployee);
}).start(3000);
```

---

## `Handler` and `ctx` — The Core of Javalin Routes

`Handler` is a functional interface — it takes a `Context` object (`ctx`) which gives you everything about the request and lets you build the response:

```java
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;

public Handler loginHandler = ctx -> {
    LoginDTO lDTO = ctx.bodyAsClass(LoginDTO.class);  // deserialize JSON body → Java object
    Employee emp = aDAO.login(lDTO.getFirst_name(), lDTO.getLast_name());

    if (emp != null) {
        ctx.json(emp);                        // serialize Java object → JSON response
        ctx.status(HttpStatus.ACCEPTED);      // 202
    } else {
        ctx.status(HttpStatus.UNAUTHORIZED);  // 401
    }
};

// Other ctx methods:
ctx.result("plain text response");
ctx.status(200);     // numeric status code also works
ctx.status(201);
ctx.status(401);
ctx.status(406);     // Not Acceptable
```

---

## `ctx.bodyAsClass()` — JSON Deserialization

Converts the incoming HTTP request JSON body directly into a Java object. The class needs a no-arg constructor and setters (or Javalin uses Jackson to map fields):

```java
LoginDTO lDTO = ctx.bodyAsClass(LoginDTO.class);
Employee newEmp = ctx.bodyAsClass(Employee.class);
```

---

## DTO — Data Transfer Object

A plain class that models *request/response data*, not a DB table. Used to capture login credentials without needing a full `Employee` object:

```java
public class LoginDTO {
    private String first_name;
    private String last_name;
    // getters + setters + no-arg constructor
}
```

DTOs are strictly for transferring data between layers — **never saved to the database**.

---

## `HttpSession` — Server-Side Session (Javalin)

A server-side session stores user data between requests. Javalin wraps the underlying Jakarta servlet session:

```java
import jakarta.servlet.http.HttpSession;

// On login — create session and store data
HttpSession sesh = ctx.req().getSession();
sesh.setAttribute("employee_id", loggedInEmployee.getEmployee_id());

// On a protected route — read from session
sesh.getAttribute("employee_id");

// Guard pattern — check session is not null before allowing access
if (AuthController.sesh != null) {
    // user is logged in
} else {
    ctx.result("Unauthorized. Login required.");
    ctx.status(401);
}
```

Session vs JWT: session state lives on the server (stateful). JWT is a self-contained token that lives client-side (stateless).

---

## `var` — Local Type Inference (Java 10+)

`var` lets the compiler infer the type — shorter but still statically typed:

```java
var app = Javalin.create(...).start(3000);
// same as: Javalin app = Javalin.create(...).start(3000);
```

Only works for local variables with an obvious right-hand-side type.

---

## `@SuppressWarnings`

Tells the compiler to ignore specific warnings:

```java
@SuppressWarnings("unused")
var app = Javalin.create(...).start(3000);  // suppress "unused variable" warning
```

---

## SQL Script Patterns (SQLite)

```sql
PRAGMA foreign_keys = ON;   -- enable FK enforcement (off by default in SQLite)

DROP TABLE IF EXISTS employees;   -- clean slate before recreating

CREATE TABLE IF NOT EXISTS roles (
    role_id   INTEGER PRIMARY KEY AUTOINCREMENT,
    role_title TEXT NOT NULL UNIQUE,
    role_salary INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS employees (
    employee_id INTEGER PRIMARY KEY AUTOINCREMENT,
    first_name TEXT NOT NULL,
    last_name  TEXT NOT NULL,
    role_id_fk INTEGER NOT NULL,
    FOREIGN KEY (role_id_fk) REFERENCES roles(role_id)
);
```

`AUTOINCREMENT` — SQLite auto-increments the primary key and never reuses deleted IDs.

---

## Model with FK Field — Flexible Constructors

The `Employee` model holds an entire `Role` object when read from DB, but accepts just an `int` FK for POST requests (so you don't need to send the full Role object):

```java
// Used when reading from DB (has full Role)
new Employee(id, firstName, lastName, roleObject);

// Used when inserting via POST (only FK provided)
new Employee(firstName, lastName, roleIdFk);
```

This is a common pattern for models that have nested objects from JOINs.
