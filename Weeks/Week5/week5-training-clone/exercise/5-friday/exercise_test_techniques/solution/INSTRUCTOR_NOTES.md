# Instructor notes — RSVP test design

**EP highlights:** invalid UUID shapes; email without `@`; local part empty; party_size 0, 9, 9.5.

**BVA:** party_size **0,1,2** and **7,8,9**; email length at **254** vs **255** if they interpret total length.

**EG:** mixed case UUID acceptance (spec says lowercase—interesting conflict); UTF-8 local part; duplicate RSVP path; wrong `Content-Type`; SQL-ish strings in email (security smell—discussion only).

**Principle tie-in:** **Exhaustive impossible** → sampling; **defect clustering** on validation; **early testing** on contract clarity.
