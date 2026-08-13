# Sample partial solution (instructor) — not exhaustive

Use the fully worked cases below as a **debrief reference** and to calibrate trainee expectations on field quality. Cases TC-BT-004 through TC-BT-008 are listed as stubs — trainees should have defined these fully in their submissions.

---

## Fully worked cases (show during debrief)

| Field | TC-BT-001 | TC-BT-002 | TC-BT-003 |
|-------|-----------|-----------|-----------|
| **ID** | TC-BT-001 | TC-BT-002 | TC-BT-003 |
| **Title** | (Positive) Create wishlist with valid name | (Edge) Name with leading/trailing spaces is trimmed | (Negative) Duplicate name same user — case-insensitive |
| **Preconditions** | User authenticated; user has 0 existing wishlists | User authenticated; user has 0 existing wishlists | User authenticated; wishlist named `"summer"` already exists |
| **Steps** | 1. POST `/wishlists` body `{"name":"My Reading List"}` with valid bearer token. | 1. POST `/wishlists` body `{"name":" summer "}` (leading + trailing space). | 1. POST `/wishlists` body `{"name":"Summer"}` (capital S). |
| **Expected result** | HTTP 201; response body includes `{"id":"<uuid>","name":"My Reading List"}`; wishlist appears in GET `/wishlists`. | HTTP 201; stored name is `"summer"` (trimmed); no error. | HTTP 409 (or spec-defined code) with body `{"error":"WL_DUP_NAME"}`; no new list created. |
| **Priority** | P1 — core create path; all other tests depend on creation working | P2 — data quality; trim failure corrupts UI display | P1 — security/data integrity; duplicate list corruption |
| **Traceability** | AC1 (up to 10 wishlists; names 1–40 chars) | AC1 (trim leading/trailing spaces) | AC2 (duplicate case-insensitive → `WL_DUP_NAME`) |

---

## Remaining stubs (instructor reference)

| ID | Category | Key input | Expected |
|----|----------|-----------|----------|
| TC-BT-004 | Negative | 11th wishlist creation | Error; spec does not define code — trainees must choose and document |
| TC-BT-005 | Negative + BVA | Add 50th book (OK), then 51st | 50th: success; 51st: `WL_FULL` (AC3) |
| TC-BT-006 | Negative — Auth | Authenticated user B calls GET `/wishlists/{id}` owned by user A | HTTP 404 — no enumeration (AC5) |
| TC-BT-007 | Edge | Remove `book_id` not in wishlist (already removed or catalog-deleted) | HTTP 200/204 — idempotent remove (AC4) |
| TC-BT-008 | NFR | List retrieval — 50 books in wishlist on staging reference hardware | Response time ≤ 800 ms at p95; document measurement tool and environment (AC6 / NFR) |

---

> **Instructor note:** Trainees should define **expected codes/messages per their interpretation** where the story has gaps — use divergence as a review moment on testability and requirement precision.
