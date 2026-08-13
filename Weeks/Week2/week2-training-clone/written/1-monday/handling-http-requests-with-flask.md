# Handling HTTP Requests with Flask

## Learning Objectives
- Describe what Flask is and how a minimal web app is structured.
- Explain the request/response cycle for HTTP.
- Define a **route**, read request data, and return JSON or text from an endpoint.
- Send requests to your Flask app using `curl` and interpret the results (status codes + JSON).

---

## Why This Matters

> **Weekly Epic Connection:** Small HTTP APIs let you mock services, expose test data, and glue automation together. Flask is a lightweight way to practice HTTP before you work with larger frameworks and Java servlets later.

As QA, you will constantly validate:

- **Inputs** (query params, JSON bodies, headers)
- **Outputs** (status codes, JSON schema, error messages)
- **Edge cases** (missing fields, wrong types, unexpected routes)

Flask is a great “lab framework” to practice these patterns before you test larger services.

---

## The Concept

### What is Flask?

**Flask** is a micro web framework for Python. It maps **URL paths** to **Python functions** (view functions) and handles HTTP details so you can focus on behavior.

Install (typically in a virtual environment):

```bash
pip install flask
```

### How to run a Flask app (and what `debug=True` really means)

When you do:

```python
app.run(debug=True)
```

Flask enables:

- **auto-reload** on file changes (dev convenience)
- a **debugger** page in the browser if your code crashes (dev convenience, security risk)

Never run `debug=True` in production.

### Minimal app

```python
from flask import Flask

app = Flask(__name__)

@app.get("/")
def home():
    return "Hello, QA!"

if __name__ == "__main__":
    app.run(debug=True)
```

- **`Flask(__name__)`** — application instance.
- **`@app.get("/")`** — register a handler for **GET** `/`.
- **`app.run()`** — development server (not for production load).

### Request/response cycle

1. Client sends **HTTP request** (method, path, headers, optional body).
2. Flask **matches the path** to a route.
3. Your function runs and returns a **response** (string, tuple with status, or `jsonify`).

### The `request` object (what you can read)

Common sources of client input:

- **Path parameters**: `/items/123` → `item_id` in your function signature
- **Query parameters**: `/search?q=test&page=2` → `request.args`
- **Headers**: `Authorization`, `Content-Type`, custom headers → `request.headers`
- **Body**: JSON / form data / raw bytes → `request.get_json()`, `request.form`, `request.data`

### Routes and methods

```python
from flask import request, jsonify

@app.post("/items")
def create_item():
    data = request.get_json(silent=True) or {}
    name = data.get("name", "unknown")
    return jsonify({"created": True, "name": name}), 201

@app.get("/items/<int:item_id>")
def get_item(item_id):
    return jsonify({"id": item_id})
```

- **`request`** — current HTTP request (JSON body, query args, headers).
- **`jsonify`** — returns a **Response** with `Content-Type: application/json`.

### Returning JSON: dict vs `jsonify`

Flask can return a plain Python `dict` and it will serialize to JSON in modern Flask versions. However, `jsonify(...)` is explicit and widely used in examples and production code. Pick one style and be consistent.

### Query parameters

```python
@app.get("/search")
def search():
    q = request.args.get("q", "")
    return {"query": q}
```

`GET /search?q=test` → `{"query": "test"}`.

### Status codes

Return `(body, status)`:

```python
return "Not found", 404
```

### Custom Error Handlers

Instead of checking errors inside every route, register a handler once for an entire error code. Flask calls it whenever that status is returned or `abort()` is raised:

```python
from flask import Flask, jsonify
from werkzeug.exceptions import HTTPException

app = Flask(__name__)

@app.errorhandler(404)
def not_found(error):
    """Return a consistent JSON 404 response for every missed route."""
    return jsonify({"error": "not_found", "message": str(error)}), 404

@app.errorhandler(400)
def bad_request(error):
    return jsonify({"error": "bad_request", "message": str(error)}), 400

@app.errorhandler(Exception)
def handle_unexpected(error):
    """Catch-all for unhandled exceptions — prevents stack traces reaching the client."""
    code = error.code if isinstance(error, HTTPException) else 500
    return jsonify({"error": "internal_error"}), code
```

### `abort()` — Short-Circuit a Route

`abort()` immediately stops route execution and triggers the appropriate error handler:

```python
from flask import abort

@app.get("/items/<int:item_id>")
def get_item(item_id):
    item = ITEMS.get(item_id)
    if not item:
        abort(404)   # Triggers the @app.errorhandler(404) handler
    return item

@app.post("/items")
def create_item():
    data = request.get_json(silent=True)
    if not isinstance(data, dict):
        abort(400)   # Triggers @app.errorhandler(400)
    ...
```

### Reading Request Headers

Headers carry metadata about the request. Common QA test cases involve validating headers:

```python
@app.get("/secure")
def secure_endpoint():
    # Read a custom header
    api_key = request.headers.get("X-API-Key", "")
    if api_key != "secret123":
        return {"error": "unauthorized"}, 401

    # Check Content-Type on a POST
    content_type = request.headers.get("Content-Type", "")
    if not content_type.startswith("application/json"):
        return {"error": "unsupported_media_type"}, 415

    return {"message": "Access granted"}
```

---

## Code Example: a small “items” API (with curl tests)

This example demonstrates:

- path params (`/items/<id>`)
- query params (`/items?q=...`)
- JSON request body (`POST /items`)
- status codes for success and validation errors

```python
from flask import Flask, request, jsonify

app = Flask(__name__)

# In-memory "database" for learning purposes
ITEMS = {
    1: {"id": 1, "name": "pencil"},
    2: {"id": 2, "name": "notebook"},
}
NEXT_ID = 3


@app.get("/health")
def health():
    return {"status": "ok"}


@app.get("/items")
def list_items():
    q = request.args.get("q", "").strip().lower()
    items = list(ITEMS.values())
    if q:
        items = [it for it in items if q in it["name"].lower()]
    return {"count": len(items), "items": items}


@app.get("/items/<int:item_id>")
def get_item(item_id: int):
    item = ITEMS.get(item_id)
    if not item:
        return {"error": "not_found", "message": f"item {item_id} does not exist"}, 404
    return item


@app.post("/items")
def create_item():
    global NEXT_ID
    data = request.get_json(silent=True)
    if not isinstance(data, dict):
        return {"error": "bad_request", "message": "JSON body must be an object"}, 400

    name = (data.get("name") or "").strip()
    if not name:
        return {"error": "validation", "message": "name is required"}, 422

    item = {"id": NEXT_ID, "name": name}
    ITEMS[NEXT_ID] = item
    NEXT_ID += 1
    return item, 201


if __name__ == "__main__":
    app.run(debug=True)
```

### Try it with curl

Start the server, then in a second terminal run:

```bash
curl -i http://127.0.0.1:5000/health
```

```bash
curl -i http://127.0.0.1:5000/items
curl -i "http://127.0.0.1:5000/items?q=note"
```

```bash
curl -i http://127.0.0.1:5000/items/2
curl -i http://127.0.0.1:5000/items/999
```

Create an item:

```bash
curl -i -X POST http://127.0.0.1:5000/items ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"eraser\"}"
```

Trigger a validation error:

```bash
curl -i -X POST http://127.0.0.1:5000/items ^
  -H "Content-Type: application/json" ^
  -d "{}"
```

> On Windows PowerShell, you can also use `Invoke-RestMethod`, but `curl` keeps the HTTP visible (status line + headers), which is great for learning.

---

## Common pitfalls (and what to test)

- **Missing `Content-Type: application/json`**: `request.get_json()` may return `None`.
- **Wrong JSON shape**: body is a list/string instead of an object → return a clear 400.
- **Silent JSON parsing** (`silent=True`) hides parse errors; good for learners, but be deliberate in production.
- **Returning inconsistent error formats**: pick `{error, message}` or similar so clients/tests can assert reliably.
- **Assuming order**: dict ordering is stable in modern Python, but APIs should not rely on it unless documented.

---

## Code Example: simple file-backed read

```python
from pathlib import Path
from flask import Flask, jsonify

app = Flask(__name__)
DATA = Path(__file__).parent / "data.json"

@app.get("/data")
def read_data():
    if not DATA.exists():
        return jsonify({"error": "no data"}), 404
    return jsonify({"raw": DATA.read_text(encoding="utf-8")})
```

*(Parsing JSON safely is a natural next step in exercises.)*

---

## Summary

- Flask connects **URLs** to **Python functions**.
- **GET/POST** and **`request`** give you access to client input.
- **`jsonify`** is the usual way to return JSON APIs.

---

## Practice (15–25 minutes)

1. Add `DELETE /items/<id>`:
   - return `204 No Content` if deleted
   - return `404` if missing
2. Add one header-based check (very light “auth”):
   - require header `X-API-Key: secret`
   - otherwise return `401` with a JSON error object
3. Add one negative test per endpoint:
   - missing body, invalid JSON, missing required field, wrong id, etc.

---

## Additional Resources

- [Flask Quickstart](https://flask.palletsprojects.com/en/stable/quickstart/)
- [Flask API: request](https://flask.palletsprojects.com/en/stable/api/#flask.request)
- [MDN: HTTP overview](https://developer.mozilla.org/en-US/docs/Web/HTTP/Overview)
