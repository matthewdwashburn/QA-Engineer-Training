# Day 6 - File Handling, JSON, Flask CRUD & pathlib

---

## File Handling

### Opening & Closing

```python
file = open("data.txt", "r")   # open in read mode
content = file.read()          # read entire file as one string
file.close()                   # always close manually if not using `with`
```

**File modes:**

| Mode | Behaviour |
|---|---|
| `"r"` | read text (error if file doesn't exist) |
| `"w"` | write text — creates file or **overwrites** if it exists |
| `"a"` | append text — creates file or adds to end if it exists |
| `"rb"` | read binary |
| `"wb"` | write binary |

### Read methods
```python
file.read()        # entire file → one string
file.readline()    # one line → string (includes `\n`)
file.readlines()   # all lines → list of strings
```

### Write methods
```python
file.write("Hello\n")           # write a single string
file.writelines(["a", "b"])     # write a list of strings (no separator added)
```

### `FileNotFoundError`
```python
try:
    file = open("missing.txt", "r")
    content = file.read()
    file.close()
except FileNotFoundError:
    print("File not found!")
```

---

## `with` Statement — Context Manager

Auto-closes the file when the block exits, even if an exception is raised. Preferred over manual `.close()`.

```python
with open("data.txt", "r") as file:
    content = file.read()
# file is automatically closed here — no file.close() needed
```

Works for any object that implements `__enter__` / `__exit__` (files, db connections, locks, etc.).

---

## Binary Files

Store raw bytes — no encoding, no newlines, not human-readable.

```python
# Write bytes
with open("numbers.dat", "wb") as file:
    file.write(bytes([10, 20, 30, 40]))   # bytes() from a list of ints (0–255)

# Read bytes back
with open("numbers.dat", "rb") as file:
    data = file.read()    # returns a bytes object: b'\n\x14\x1e('
    print(data)
```

---

## Pickle — Serialize Python Objects

`pickle` serializes any Python object (list, dict, class instance, etc.) to binary and restores it exactly. Not human-readable. Do not unpickle data from untrusted sources.

```python
import pickle

numbers = [1, 2, 3, 4, 5]

# Write (serialize)
with open("data.dat", "wb") as file:
    pickle.dump(numbers, file)

# Read (deserialize)
with open("data.dat", "rb") as file:
    data = pickle.load(file)    # returns original Python object
    print(data)                 # [1, 2, 3, 4, 5]
```

---

## `json` Module

Converts between Python objects and JSON strings/files.

```python
import json

# String ↔ Python
json.loads('{"id": 1, "name": "Alice"}')   # JSON string → Python dict
json.dumps({"id": 1, "name": "Alice"})     # Python dict → JSON string

# File ↔ Python
with open("data.json", "r") as f:
    data = json.load(f)        # JSON file → Python object

with open("data.json", "w") as f:
    json.dump(data, f)         # Python object → JSON file
```

---

## `pathlib.Path`

Build file paths that work cross-platform. `/` operator joins path segments.

```python
from pathlib import Path

# Build a path relative to the current script file
DATA_FILE = Path(__file__).resolve().parent / "data" / "findings.json"
# __file__         → this script's path
# .resolve()       → absolute path, no symlinks
# .parent          → the directory containing this script
# / "data" / "..." → append subdirectory and filename
```

---

## Flask — Expanded

### New in Day 6

**`jsonify()`** — converts a Python dict or list to a proper JSON HTTP response (sets Content-Type header):
```python
from flask import Flask, jsonify

@app.route("/students", methods=["GET"])
def get_students():
    return jsonify(students)   # list of dicts → JSON array response
```

**`request.json`** — shorthand for `request.get_json()`, parses the JSON request body:
```python
data = request.json       # same as request.get_json()
name = data["name"]
```

**HTTP method shorthand decorators:**
```python
@app.get("/path")     # same as @app.route("/path", methods=["GET"])
@app.post("/path")    # same as @app.route("/path", methods=["POST"])
```

**Type conversion in URL parameters:**
```python
@app.route("/<int:num1>/add/<int:num2>")   # converts captured strings to int automatically
def add(num1, num2):
    return str(num1 + num2)
```

**Return a status code with a response:**
```python
return jsonify(new_item), 201    # second value is the HTTP status code
return jsonify({"message": "Not found"}), 404
```

**Debug mode** — auto-reloads on code change, shows full tracebacks in browser:
```python
app.run(debug=True)
```

### HTTP Status Codes

| Code | Meaning |
|---|---|
| `200` | OK — successful GET / DELETE |
| `201` | Created — successful POST / PUT |
| `400` | Bad Request — malformed input |
| `404` | Not Found — resource doesn't exist |

### Full CRUD Pattern

```python
students = []   # in-memory store (replace with DB in real apps)

# GET all
@app.route("/students", methods=["GET"])
def get_students():
    return jsonify(students)

# GET one by id
@app.route("/students/<id>", methods=["GET"])
def get_student(id):
    student = next((s for s in students if s["id"] == int(id)), None)
    if student is None:
        return jsonify({"message": "Not found"}), 404
    return jsonify(student)

# POST — create
@app.route("/students", methods=["POST"])
def post_student():
    data = request.json
    id = max((s["id"] for s in students), default=0) + 1   # auto-increment id
    students.append({"id": id, "name": data["name"], "course": data["course"]})
    return jsonify({"message": "Created"}), 201

# PUT — update
@app.route("/students/<id>", methods=["PUT"])
def put_student(id):
    student = next((s for s in students if s["id"] == int(id)), None)
    if student is None:
        return jsonify({"message": "Not found"}), 404
    student["name"] = request.json["name"]
    student["course"] = request.json["course"]
    return jsonify({"message": "Updated"}), 201

# DELETE
@app.route("/students/<id>", methods=["DELETE"])
def delete_student(id):
    student = next((s for s in students if s["id"] == int(id)), None)
    if student is None:
        return jsonify({"message": "Not found"}), 404
    students.remove(student)   # remove() is for lists; pop() is for dicts
    return jsonify({"message": "Deleted"}), 200
```

---

## `next()` with a Default — List Search

Find the first item in a list matching a condition, or return a default if nothing matches:

```python
student = next((s for s in students if s["id"] == int(id)), None)
# generator expression ─────────────────────────────────┘         └─ default if not found
```

Much cleaner than a for loop when you just want the first match. Returns `None` (or whatever default you pass) instead of raising `StopIteration`.

---

## `max()` with Generator + Default

Get the highest value from a collection, with a fallback when the collection is empty:

```python
id = max((s["id"] for s in students), default=0) + 1   # auto-increment: 0 if list is empty
```

Without `default=`, `max()` raises `ValueError` on an empty sequence.
