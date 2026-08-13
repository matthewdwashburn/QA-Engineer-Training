# Normalization: 1NF, 2NF, 3NF, BCNF, and Denormalization

## Learning Objectives

- State the goals of **first**, **second**, **third**, and **Boyce-Codd** normal forms.
- Decompose example tables to reduce redundancy and update anomalies.
- Explain when **denormalization** is a deliberate trade-off for performance or simplicity.

## Why This Matters

Normalization reduces **duplication** and **anomalies** (insert/update/delete inconsistencies). Later in the week you will query across many tables—clean decomposition makes joins predictable. You will also see when teams **denormalize** for read performance; knowing the theory lets you do it **intentionally**, not accidentally.

---

## The Concept

### First Normal Form (1NF)

Rules:
- Every column holds **atomic** (indivisible) values — no lists, arrays, or comma-separated values crammed into one cell.
- Every row is uniquely identifiable (has a primary key).

#### ❌ Before 1NF — Violation

| student_id | student_name | phone_numbers            | courses                  |
|------------|--------------|--------------------------|--------------------------|
| 101        | Alice         | 555-1111, 555-2222       | Math, Physics            |
| 102        | Bob           | 555-3333                 | Chemistry                |

> **Problem:** `phone_numbers` and `courses` each hold multiple values. You cannot query "find all students enrolled in Physics" without string parsing.

#### ✅ After 1NF — Fixed

**student**

| student_id | student_name |
|------------|--------------|
| 101        | Alice         |
| 102        | Bob           |

**student_phone**

| student_id | phone     |
|------------|-----------|
| 101        | 555-1111  |
| 101        | 555-2222  |
| 102        | 555-3333  |

**enrollment**

| student_id | course    |
|------------|-----------|
| 101        | Math      |
| 101        | Physics   |
| 102        | Chemistry |

> Each cell now holds exactly one value. Queries and indexes work cleanly.

---

### Second Normal Form (2NF)

Rules:
- Must already be in **1NF**.
- Every **non-key** attribute must depend on the **entire** composite primary key, not just part of it.

> 2NF only applies when the primary key is **composite** (two or more columns).

#### ❌ Before 2NF — Violation

`enrollment` table with PK = `(student_id, course_id)`

| student_id | course_id | student_name | course_name       | grade |
|------------|-----------|--------------|-------------------|-------|
| 101        | C01       | Alice         | Math              | A     |
| 101        | C02       | Alice         | Physics           | B     |
| 102        | C01       | Bob           | Math              | C     |

> **Problem:**
> - `student_name` depends only on `student_id` (partial dependency).
> - `course_name` depends only on `course_id` (partial dependency).
> - If Alice changes her name, you must update **every row** she appears in.

#### ✅ After 2NF — Fixed

**student**

| student_id | student_name |
|------------|--------------|
| 101        | Alice         |
| 102        | Bob           |

**course**

| course_id | course_name |
|-----------|-------------|
| C01       | Math        |
| C02       | Physics     |

**enrollment**

| student_id | course_id | grade |
|------------|-----------|-------|
| 101        | C01       | A     |
| 101        | C02       | B     |
| 102        | C01       | C     |

> Now `student_name` and `course_name` are stored exactly once. A name change = one row update.

---

### Third Normal Form (3NF)

Rules:
- Must already be in **2NF**.
- No **non-key** attribute depends on another non-key attribute (no **transitive dependencies**).

#### ❌ Before 3NF — Violation

`order` table with PK = `order_id`

| order_id | customer_id | customer_name | customer_email      | order_total |
|----------|-------------|---------------|---------------------|-------------|
| 5001     | C10         | Alice          | alice@example.com   | 120.00      |
| 5002     | C10         | Alice          | alice@example.com   | 45.00       |
| 5003     | C20         | Bob            | bob@example.com     | 200.00      |

> **Problem:** The dependency chain is:
> `order_id → customer_id → customer_name / customer_email`
> `customer_email` tells us about the **customer**, not the **order**. Alice's email is duplicated across orders.

#### ✅ After 3NF — Fixed

**customer**

| customer_id | customer_name | customer_email    |
|-------------|---------------|-------------------|
| C10         | Alice          | alice@example.com |
| C20         | Bob            | bob@example.com   |

**order**

| order_id | customer_id | order_total |
|----------|-------------|-------------|
| 5001     | C10         | 120.00      |
| 5002     | C10         | 45.00       |
| 5003     | C20         | 200.00      |

> `customer_email` lives in one place. Updating Alice's email = one row, zero risk of inconsistency.

---

### Boyce-Codd Normal Form (BCNF)

Stricter than 3NF: every **determinant** of any functional dependency must be a **candidate key**.

BCNF matters in tables that have **overlapping composite candidate keys**. Most well-designed OLTP schemas naturally achieve BCNF alongside 3NF, but here is a classic edge case:

#### ❌ Before BCNF — Violation

`course_teacher` table — each course section has one teacher; each teacher teaches exactly one subject.

| student_id | subject    | teacher     |
|------------|------------|-------------|
| 101        | Math       | Prof. Smith |
| 102        | Math       | Prof. Smith |
| 103        | Physics    | Prof. Jones |
| 101        | Physics    | Prof. Jones |

Candidate keys: `(student_id, subject)` and `(student_id, teacher)`.

Functional dependency: `teacher → subject` — but `teacher` is **not** a candidate key on its own.

> **Problem:** Prof. Smith's subject (Math) is repeated per student. If he switches to Chemistry, many rows change.

#### ✅ After BCNF — Fixed

**teacher_subject**

| teacher      | subject |
|--------------|---------|
| Prof. Smith  | Math    |
| Prof. Jones  | Physics |

**student_teacher**

| student_id | teacher      |
|------------|--------------|
| 101        | Prof. Smith  |
| 102        | Prof. Smith  |
| 103        | Prof. Jones  |
| 101        | Prof. Jones  |

> Every determinant (`teacher`) is now a key in its own table.

---

### Denormalization

**Denormalization** intentionally copies or derives data to speed reads or simplify queries—at the cost of redundancy.

#### Example: Storing a Derived `order_total`

Rather than summing `order_line` rows on every query, an e-commerce platform may store a pre-calculated total:

| order_id | customer_id | order_total | line_count |
|----------|-------------|-------------|------------|
| 5001     | C10         | 120.00      | 3          |
| 5002     | C10         | 45.00       | 1          |

> `order_total` and `line_count` are derivable — they are stored here for read performance.

**Trade-offs:**

| Concern         | Normalized                        | Denormalized                        |
|-----------------|-----------------------------------|-------------------------------------|
| Storage         | Minimal                           | Higher (duplicated data)            |
| Update anomalies | Low (one place to change)        | Risk if sync is missed              |
| Read performance | More joins required              | Faster — pre-aggregated            |
| Consistency      | Enforced by structure             | Requires triggers / app logic       |

> Use denormalization when **measured** need exists (SLA breaches, slow query plans), not by default.

---

## Practical Example — Full Journey

**Start: Unnormalized receipt**

| receipt_id | customer      | items                       | city        |
|------------|---------------|-----------------------------|-------------|
| 1          | Alice / C10   | SKU1 x2; SKU2 x1            | New York    |
| 2          | Bob / C20     | SKU3 x5                     | Boston      |

**Problems:** multi-value `items` cell, concatenated ID+name in `customer`, `city` is a fact about the customer not the receipt.

---

**Step 1 → 1NF** (atomic values, one row per line item)

| receipt_id | customer_id | customer_name | city      | sku  | qty |
|------------|-------------|---------------|-----------|------|-----|
| 1          | C10         | Alice          | New York  | SKU1 | 2   |
| 1          | C10         | Alice          | New York  | SKU2 | 1   |
| 2          | C20         | Bob            | Boston    | SKU3 | 5   |

PK = `(receipt_id, sku)` — but partial and transitive dependencies exist.

---

**Step 2 → 2NF** (remove partial dependencies; `customer_*` depends only on `customer_id`)

**customer**

| customer_id | customer_name | city     |
|-------------|---------------|----------|
| C10         | Alice          | New York |
| C20         | Bob            | Boston   |

**receipt**

| receipt_id | customer_id |
|------------|-------------|
| 1          | C10         |
| 2          | C20         |

**receipt_line**

| receipt_id | sku  | qty |
|------------|------|-----|
| 1          | SKU1 | 2   |
| 1          | SKU2 | 1   |
| 2          | SKU3 | 5   |

---

**Step 3 → 3NF** (remove transitive dependency; `city` depends on `customer_id`, which is fine — but if `city` determined `tax_rate` stored on customer, that would need its own table)

Assume we also track `zip_code → city` — city is determined by zip:

**zip**

| zip_code | city     |
|----------|----------|
| 10001    | New York |
| 02101    | Boston   |

**customer** (updated)

| customer_id | customer_name | zip_code |
|-------------|---------------|----------|
| C10         | Alice          | 10001    |
| C20         | Bob            | 02101    |

> Now `city` lives in one place (the `zip` table). No duplication if multiple customers share a zip code.

---

## Summary

| Normal Form | Key Rule                                                  |
|-------------|-----------------------------------------------------------|
| **1NF**     | Atomic values; no repeating groups; identifiable rows     |
| **2NF**     | No partial dependencies on a composite key                |
| **3NF**     | No transitive dependencies through non-key columns        |
| **BCNF**    | Every determinant of a functional dependency is a key     |
| **Denorm**  | Deliberate redundancy for read speed — apply with evidence |

## Additional Resources

- [Wikipedia: Database normalization](https://en.wikipedia.org/wiki/Database_normalization)
- [Stanford / classic texts on functional dependencies (supplementary)](https://en.wikipedia.org/wiki/Functional_dependency)
- [Use The Index, Luke: Performance vs normalization context](https://use-the-index-luke.com/sql/preface)
