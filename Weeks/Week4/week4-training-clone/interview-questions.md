# Interview Questions: Week 4 — SQL

**Epic:** Relational data mastery — schema → queries → views/indexes/procedures → transactions → JDBC → Python (`sqlite3`, SQLAlchemy), per CMA log and `written/` topics.

**How to use:** Answer aloud or in writing, then open **Click to Reveal Answer**. Check that your response includes the **Keywords**.

**Difficulty mix (target):** ~70% beginner, ~25% intermediate, ~5% advanced (16 questions total).

---

## Beginner (Foundational)

### Q1. Name the five SQL **categories** used in the course (DDL, DML, DQL, DCL, TCL) and give **one example statement** for each.

**Keywords:** DDL, DML, DQL, DCL, TCL, CREATE, INSERT, SELECT, GRANT, COMMIT

<details>
<summary>Click to Reveal Answer</summary>

**DDL** — defines structure (e.g. `CREATE TABLE`, `ALTER`, `DROP`, `TRUNCATE`). **DML** — changes data (`INSERT`, `UPDATE`, `DELETE`). **DQL** — reads data (primarily `SELECT`). **DCL** — permissions (`GRANT`, `REVOKE`). **TCL** — transaction boundaries (`COMMIT`, `ROLLBACK`, `SAVEPOINT`). Trainees may fold `SELECT` under DML in some books; the course separates **DQL** to emphasize read vs write.
</details>

---

### Q2. What is the difference between **`DROP TABLE`**, **`TRUNCATE TABLE`**, and **`DELETE`** with no `WHERE`?

**Keywords:** object, rows, DDL, DML, structure

<details>
<summary>Click to Reveal Answer</summary>

**`DROP TABLE`** removes the **table object** from the catalog (and usually its data). **`TRUNCATE`** removes **all rows** but keeps the table definition; it is **DDL-class** in many engines and is not row-targeted like `DELETE`. **`DELETE`** without `WHERE` removes all rows but is **DML** and differs in logging/locking behavior from `TRUNCATE` in typical implementations.
</details>

---

### Q3. Why is **`DECIMAL`/`NUMERIC`** preferred over **`FLOAT`** for **money** in SQL?

**Keywords:** exact, floating point, rounding, fixed-point

<details>
<summary>Click to Reveal Answer</summary>

Binary floating types (`FLOAT`/`REAL`) store **approximate** values and can introduce rounding errors for decimal fractions. **`DECIMAL(p, s)`** is **fixed-point** and gives **exact** decimal arithmetic for currency-style values, which the written material treats as the safer choice for money.
</details>

---

### Q4. What does a **foreign key** enforce, and what do **`ON DELETE CASCADE`** vs **`ON DELETE RESTRICT`** (or `NO ACTION`) mean at a high level?

**Keywords:** parent, child, orphan, propagate, reject

<details>
<summary>Click to Reveal Answer</summary>

A **foreign key** requires child column values to match an existing **parent** primary or unique key — no **orphan** references. **`ON DELETE CASCADE`** deletes or updates **dependent child rows** when the parent row is deleted/updated (depending on definition). **`RESTRICT`/`NO ACTION`** **rejects** the parent delete/update if matching children exist, protecting referential integrity.
</details>

---

### Q5. Explain **1NF**, **2NF**, and **3NF** in one sentence each.

**Keywords:** atomic, composite key, transitive, non-key

<details>
<summary>Click to Reveal Answer</summary>

**1NF:** Values are **atomic** (no repeating groups / one fact per cell at row grain). **2NF:** In 1NF with a **composite** key, every non-key attribute depends on the **whole** key, not part of it. **3NF:** No **transitive** dependency where a non-key attribute depends on another non-key attribute instead of the key.
</details>

---

### Q6. What is a **junction table**, and when do you need one?

**Keywords:** many-to-many, two foreign keys, enrollment

<details>
<summary>Click to Reveal Answer</summary>

A **junction** (associative) table implements a **many-to-many** relationship: it usually has **two foreign keys** (e.g. `student_id`, `course_id`) and often a composite primary key or a surrogate key plus uniqueness on the pair. You need it when neither side can hold the other’s key alone without repeating or losing multiplicity (e.g. students ↔ courses).
</details>

---

### Q7. Contrast **`INNER JOIN`** and **`LEFT OUTER JOIN`** in terms of which rows appear in the result.

**Keywords:** match, all left, NULL

<details>
<summary>Click to Reveal Answer</summary>

**INNER JOIN** keeps only rows where the **join condition** is true for **both** sides. **LEFT JOIN** keeps **every row from the left** table; for non-matches, columns from the right side are **NULL**. Use LEFT when you must include entities without related rows (e.g. all customers, even those with no orders).
</details>

---

### Q8. When do you use **`WHERE`** vs **`HAVING`** in a query with **`GROUP BY`**?

**Keywords:** before, after, aggregate, groups

<details>
<summary>Click to Reveal Answer</summary>

**`WHERE`** filters **rows before** grouping. **`HAVING`** filters **groups after** aggregation (e.g. conditions on `COUNT(*)`, `SUM(...)`). Standard SQL does not allow aggregate functions in `WHERE`; use `HAVING` for predicates on group summaries.
</details>

---

### Q9. What is the difference between **`UNION`** and **`UNION ALL`**?

**Keywords:** duplicates, concatenate, performance

<details>
<summary>Click to Reveal Answer</summary>

Both combine compatible result sets vertically. **`UNION`** typically **removes duplicates** (extra work). **`UNION ALL`** **concatenates** without deduplication and is **faster** when duplicates are impossible or acceptable. Column count and types must be compatible between the two `SELECT`s.
</details>

---

### Q10. List the four **ACID** properties and give a one-line meaning for each.

**Keywords:** Atomicity, Consistency, Isolation, Durability, transaction

<details>
<summary>Click to Reveal Answer</summary>

**Atomicity** — all changes in a transaction **commit together** or **roll back** together. **Consistency** — the database moves between **valid states** (constraints + application rules). **Isolation** — concurrent transactions do not see inappropriate **intermediate** effects per isolation level. **Durability** — after **COMMIT**, committed data **survives** crashes under the engine’s guarantees (e.g. WAL/redo).
</details>

---

### Q11. What is a **database view**, and do views automatically make queries faster?

**Keywords:** named query, virtual, base tables, indexes

<details>
<summary>Click to Reveal Answer</summary>

A view is a **named, stored query** that acts like a **virtual** table; the engine usually **expands** the view definition against **base tables** when queried. Views **do not** automatically cache results or replace the need for **indexes** on underlying tables; performance still depends on the underlying query and physical design.
</details>

---

## Intermediate (Application)

### Q12. You need to prevent **SQL injection** from user input in a Java app using JDBC. What do you use instead of building SQL strings with concatenation, and why?

**Hint:** Think `?` placeholders.

**Keywords:** PreparedStatement, bind, syntax, data

<details>
<summary>Click to Reveal Answer</summary>

Use **`PreparedStatement`** with **parameter placeholders** (`?`) and `setString` / `setInt` / etc. The database treats bound values as **data**, not as SQL **syntax**, which stops attackers from injecting clauses like `' OR '1'='1`. String concatenation into **`Statement`** is unsafe for untrusted input.
</details>

---

### Q13. When would you add a **secondary (non-clustered) index** on a column, and what is a **downside** of adding many indexes?

**Keywords:** WHERE, JOIN, seek, writes, maintenance

<details>
<summary>Click to Reveal Answer</summary>

Add indexes to columns used in **selective** **`WHERE`**, **JOIN**, or **ORDER BY** paths where scans are costly — typical examples include foreign keys and high-cardinality filters. **Downside:** each **`INSERT`/`UPDATE`/`DELETE`** may need to **update** index structures, slowing writes and consuming **storage**; unused indexes add cost without benefit.
</details>

---

### Q14. What is the **DAO pattern** in JDBC applications, and what problem does it solve?

**Keywords:** interface, SQL, service, separation

<details>
<summary>Click to Reveal Answer</summary>

**DAO (Data Access Object)** encapsulates **SQL and row mapping** behind an **interface** (e.g. `CustomerDao`) with implementations like `JdbcCustomerDao`. It **separates** persistence from **services/UI**, so business logic does not scatter raw JDBC across the app and you can swap or test persistence more easily.
</details>

---

### Q15. What trade-offs do teams consider when putting logic in **stored procedures** versus **only** in application services?

**Keywords:** round trips, encapsulation, vendor-specific, CI/CD, tests

<details>
<summary>Click to Reveal Answer</summary>

**Pros of procedures:** fewer **network round trips**, logic **centralized** next to data, can enforce rules regardless of which app connects. **Cons:** often **vendor-specific** SQL/procedural dialects, **harder** to unit-test in the same way as app code, and **versioning** can lag application deployments. The course presents this as an **architecture** choice, not a universal rule.
</details>

---

## Advanced (Deep Dive)

### Q16. Two sessions run concurrently against the same row. One reads while the other updates but has **not** committed. Under a **low** isolation level (e.g. **READ UNCOMMITTED**), what anomaly can the reader see, and what does **READ COMMITTED** typically prevent?

**Keywords:** dirty read, committed, isolation, uncommitted

<details>
<summary>Click to Reveal Answer</summary>

Under **READ UNCOMMITTED**, a reader may see another transaction’s **uncommitted** changes — a **dirty read** — which can disappear if the writer **ROLLBACK**s. **READ COMMITTED** generally prevents **dirty reads** by only allowing reads of **committed** data (exact behavior and additional anomalies depend on the engine and level — e.g. non-repeatable reads may still occur). Tie answers to the week’s ACID/isolation reading, not vendor-specific edge cases.
</details>

---
