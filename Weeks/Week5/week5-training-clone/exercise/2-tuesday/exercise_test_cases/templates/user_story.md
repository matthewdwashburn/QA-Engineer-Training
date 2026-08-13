# User story — BookTrack wishlists

**ID:** BT-88  
**Title:** Wishlists for readers

**Story:** As a **signed-in** reader, I want **multiple named wishlists** so I can organize books I plan to buy or read.

## Acceptance criteria

1. A user may create up to **10** wishlists. Names are **1–40** characters; **trim** leading/trailing spaces; **no** empty name after trim.
2. Duplicate **case-insensitive** names **for the same user** are **rejected** with error code `WL_DUP_NAME`.
3. A wishlist may contain at most **50** distinct books. Adding the **51st** returns `WL_FULL`.
4. Removing a book succeeds even if the book was **deleted** from the catalog (idempotent remove).
5. Only the **owner** may view or modify their wishlists; other authenticated users receive **404** (no enumeration of existence).
6. **NFR (for testing practice):** List retrieval for a wishlist with **50** items returns in **≤ 800 ms** at **p95** on staging reference hardware (documented in test plan—not automated in this exercise unless you choose to outline a **performance** test approach).

## Assumptions

- Authentication already exists; use bearer token in API tests or “logged in” for UI tests.
- Book IDs refer to catalog `book_id` UUID strings.
