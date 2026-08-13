# Feature spec — Event RSVP API (fictional)

`POST /events/{event_id}/rsvp`

**Headers:** `Authorization: Bearer <token>` required.

**JSON body:**

```json
{
  "guest_email": "string",
  "party_size": 1
}
```

## Rules

1. `event_id` must be **UUID v4** format (lowercase hex with hyphens).
2. `guest_email` must be **valid** per a typical **HTML-style** email check: contains `@`, local part **1–64** chars, domain contains `.`, total length **≤ 254**. (This is a **training** simplification—not production RFC compliance.)
3. `party_size` integer **1–8** inclusive.
4. If the same `guest_email` RSVPs twice for the **same** `event_id`, return **`200`** with body `{"status":"already_registered"}` (idempotent).
5. If `event_id` unknown, return **`404`** `{"error":"EVENT_NOT_FOUND"}`.
6. If validation fails, return **`400`** with `{"error":"VALIDATION","fields":[...]}` listing **field keys**.

## Non-goals

Rate limiting, authentication edge cases (assume token always valid **unless** you choose EG tests).
