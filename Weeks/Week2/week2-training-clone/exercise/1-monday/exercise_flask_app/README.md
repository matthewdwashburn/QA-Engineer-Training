# Exercise: Flask API Backed by a File

**Mode:** Implementation (Code Lab)  
**Duration:** 60–90 minutes  
**Day:** 1-monday | **Week:** 2 — Python & Java Foundations  

---

## Objective

- Run a small **Flask** app that **reads** JSON-serializable data from a **file** on disk.
- Expose at least one **GET** endpoint to read that data.
- Expose at least one **POST** endpoint that **appends** a record and **persists** it back to the file.
- Use **`with`** when reading/writing the file.

---

## Prerequisites

| Concept | Source |
|---------|--------|
| Flask routing, `jsonify`, `request` | `written/1-monday/handling-http-requests-with-flask.md` |
| File I/O | `written/1-monday/with-statements.md`, `read-files-write-create.md` |
| Demo | `demos/1-monday/code/demo_flask_api.py` |

---

## Scenario

Build a **“QA Findings”** micro-API:

- Storage file: **`starter_code/data/findings.json`** — a JSON **array** of objects, e.g. `[{"id": 1, "title": "Login fails", "severity": "high"}, ...]`.
- **GET `/findings`** — return the full list as JSON.
- **POST `/findings`** — accept JSON body `{"title": str, "severity": str}`; assign next `id`; append to list; **rewrite** the file; return `201` + created object.

Seed file is provided with one item so GET is non-empty on first run.

---

## Setup

```bash
cd starter_code
pip install flask
python app.py
```

Test with **curl** or **Thunder Client**:

- `GET http://127.0.0.1:5000/findings`
- `POST http://127.0.0.1:5000/findings` with `Content-Type: application/json` body.

---

## Core Tasks

1. Implement **`app.py`** (skeleton provided): load JSON on startup **or** on each request (either is fine if documented—simplest is **read file per request** for learning).
2. **POST** must:
   - Validate `title` and `severity` present (return **400** JSON error if not).
   - Persist with **`json.dump`** inside `with open(...)`.
3. Use **`encoding="utf-8"`** for all text file access.

---

## Definition of Done

- [ ] GET returns contents of `findings.json`.
- [ ] POST adds a record with monotonically increasing **`id`**.
- [ ] File on disk updates after POST (refresh file in editor to confirm).
- [ ] All file access uses **`with`**.

---

## Stretch

- Add **GET `/findings/<id>`** returning **404** if missing.
- Add **`DELETE`** by id.

---

## References

- Written: `content/Week2-Python-Java/written/1-monday/handling-http-requests-with-flask.md`
- Demo: `content/Week2-Python-Java/demos/1-monday/code/demo_flask_api.py`
