# ACID Properties and Transaction Isolation

## Learning Objectives

- Define **atomicity, consistency, isolation, durability** for database transactions.
- Describe common **isolation levels** and the anomalies they prevent or allow.
- Relate isolation choices to application bugs (dirty reads, phantom reads) and throughput.

## Why This Matters

Applications rarely issue one statement at a time; they perform **multi-step workflows** (debit account, insert order, decrement stock). **ACID** guarantees and **isolation levels** define what concurrent users observe—critical for JDBC auto-commit toggles, connection pools, and ORM session flushing later in the week.

## The Concept

### Quick glossary (terms learners confuse)

- **Transaction**: a unit of work that ends with `COMMIT` (keep) or `ROLLBACK` (undo).
- **Commit**: the moment changes become visible to others (and, by durability rules, survive crashes).
- **Rollback**: undo uncommitted changes in the current transaction.
- **Constraint**: a rule on stored data (e.g., `NOT NULL`, `CHECK`, `FOREIGN KEY`). Constraints help with **consistency**, but they are not the same thing as **isolation**.
- **Isolation level**: the configured strength of “how much concurrency weirdness is allowed.”

### Atomicity

**All-or-nothing:** a transaction’s changes either **commit** together or **roll back** together. Partial updates from a failed business operation must not persist.

Mini-scenario (why atomicity matters):

- Transfer \$10 from A → B:
  - `UPDATE account SET balance = balance - 10 WHERE id = 'A'`
  - `UPDATE account SET balance = balance + 10 WHERE id = 'B'`
- If the second statement fails, atomicity ensures the first is also undone (no “money disappeared” state).

### Consistency

The database moves between **valid states** according to defined rules (constraints, triggers). Consistency is partly the **DB’s job** (enforcing constraints) and partly the **application’s job** (choosing valid operations).

Important nuance:

- ACID “consistency” does **not** mean “the DB magically enforces all business rules.”
- It means: if rules are defined (constraints/triggers) and your transaction logic is correct, the DB will not commit a state that violates those defined rules.

Mini-scenario:

- If `order_line.order_id` has an FK to `order_header.id`, you cannot commit an `order_line` pointing to a non-existent order (the DB rejects it).

### Isolation

Concurrent transactions should not **interfere** in ways that break business assumptions. **Isolation levels** trade strictness for performance.

Mini-scenario (classic bug isolation prevents):

- “Check then act” race:
  - T1: `SELECT qty FROM inventory WHERE sku='X'` → sees 1
  - T2: `SELECT qty ...` → sees 1
  - Both decrement and commit → you oversell.

Higher isolation (or locking patterns) forces a safe ordering (block/abort/retry) so the invariant holds.

### Durability

After **commit**, committed data survives **crash/restart** (via WAL/redo logs, replication—implementation detail, but the promise matters to operators).

Mini-scenario:

- A user submits an order, app gets “success,” then the DB server crashes.
- Durability means the committed order is still there after restart (subject to the engine’s durability configuration).

## Isolation levels (ANSI framing)

Exact names and defaults vary by engine, but the ideas are standard:

| Level | Dirty read | Non-repeatable read | Phantom read |
|-------|------------|---------------------|--------------|
| READ UNCOMMITTED | Possible | Possible | Possible |
| READ COMMITTED | No | Possible | Possible |
| REPEATABLE READ | No | No | Engine-dependent |
| SERIALIZABLE | No | No | No |

### What the anomalies mean (with examples)

Use two concurrent sessions when you read these timelines: **T1** and **T2** both run at roughly the same time.

**Dirty read** — You read another transaction’s **uncommitted** data; if they roll back, you acted on a value that never “really” existed.

- Example: `accounts` has Alice with balance **100**. T2 runs `UPDATE accounts SET balance = 50 WHERE id = 'alice'` but does **not** commit. T1 runs `SELECT balance FROM accounts WHERE id = 'alice'` and sees **50**. T2 then **ROLLBACK**. T1 already showed the user “50” or decided a transfer using that number—**incorrect** because 50 was never committed.

**Non-repeatable read** — Inside **one** transaction you read the same row twice; **another committed transaction** changed it in between, so the two reads disagree.

- Example: T1 reads `SELECT quantity FROM inventory WHERE sku = 'X'` → **10**. T2 commits `UPDATE inventory SET quantity = 3 WHERE sku = 'X'`. T1 reads again in the same transaction → **3**. If T1 assumed “still 10” for a restock rule, its logic is inconsistent with what the database now shows.

**Phantom read** — You run the **same range or filter query** twice in one transaction; **new rows** appear (or disappear) that match the filter because another transaction **inserted** or **deleted** (and committed) rows in that range.

- Example: T1 runs `SELECT COUNT(*) FROM orders WHERE order_date = CURRENT_DATE` → **100** (for a daily cap). T2 **commits** a new order for today. T1 runs the same `COUNT` again → **101**. T1 thought the cap was not exceeded; a phantom row broke that assumption.

These are **not** theoretical-only: they show up in reporting, inventory, sign-up caps, and “check-then-act” code if isolation is too weak or transactions are left implicit.

### Each isolation level in practice

**READ UNCOMMITTED**

- **Behavior:** May read uncommitted changes from other transactions. Writes may still follow engine rules, but reads are the least protected.
- **When it appears:** Rarely chosen explicitly; sometimes legacy defaults or diagnostics. Offers the most throughput for read-heavy workloads that can tolerate stale or rolled-back values—most business apps **avoid** it.
- **Example risk:** The dirty-read scenario above is allowed at this level.

**READ COMMITTED**

- **Behavior:** You only ever see **committed** data. Each statement typically sees a **fresh** snapshot of committed rows at statement boundaries, so another transaction can **commit** between your first and second read of the “same” logical row—**non-repeatable reads** and **phantoms** remain possible.
- **When it appears:** Default on **PostgreSQL**, **SQL Server**, **Oracle** (often described as snapshot-per-statement semantics). Good default for many OLTP apps if you don’t assume stable reads across multiple queries in one transaction.
- **Example:** T1: `SELECT balance ...` → 100; T2 commits a withdrawal; T1: same `SELECT` → 80. No dirty read (80 is committed), but **non-repeatable**.

**REPEATABLE READ**

- **Behavior (ANSI idea):** Within one transaction, **re-reads of the same row** see the **same values** as the first read in that transaction—**no dirty reads, no non-repeatable reads** for those rows. **Phantom reads** may still be allowed in the strict ANSI sense, but **MySQL/InnoDB** and **PostgreSQL** implement this level with stronger guarantees (InnoDB uses next-key locking to reduce phantoms; PostgreSQL uses snapshot isolation for repeatable read, which prevents phantoms for queries in that snapshot).
- **When it appears:** Useful when a transaction runs several reads and **must** see a stable view of **rows it already touched** (or, on PostgreSQL, a stable snapshot for the whole transaction).
- **Example (non-repeatable prevented):** T1 reads row **locked or snapshotted** at balance 100; T2 updates and commits to 80; T1 reads again **still** sees 100 until T1 ends—so aggregates or rules based on “what we saw at start” stay internally consistent. **Caveat:** On engines that only lock read rows, a **new** row in a range can still appear as a phantom unless the engine uses range locks or snapshot isolation.

**SERIALIZABLE**

- **Behavior:** The strongest ANSI level: results must be **as if** transactions ran **one after another** in some order—**no dirty, non-repeatable, or phantom reads** in the model. Implementations use **locks**, **predicate locks**, or **serializable snapshot isolation (SSI)**; conflicts may surface as **deadlocks** or **serialization failures** that the app must **retry**.
- **When it appears:** Financial invariants, strict “check then act” workflows, or small critical sections where a bug from concurrency is unacceptable.
- **Example:** T1 scans orders for today and enforces a **hard cap**; T2 tries to insert another order for today. Under serializable execution, one transaction **blocks** or **fails** so the cap cannot be silently bypassed by interleaving. If the DB aborts T2 with a serialization error, the application retries T2 safely.

### Engine wrinkles (read the docs when you deploy)

- **“Repeatable read” is not identical everywhere:** PostgreSQL’s repeatable read uses **MVCC snapshots** and can **fail** transactions that would break serializability (serialization failure). InnoDB repeatable read reduces many phantom cases via locking but behaviors differ from PostgreSQL.
- **READ COMMITTED** on SQL Server uses **locks** for writes and **statement-level** consistent reads; snapshot isolation is a **separate** option (row versioning), not the same as raising the ANSI level name.
- **Defaults:** **READ COMMITTED** is common (PostgreSQL, SQL Server, Oracle). **MySQL/InnoDB** default is **REPEATABLE READ**; that surprises people who expect read-committed-by-default from other products.

**READ COMMITTED** remains a common default for general OLTP. **REPEATABLE READ** and **SERIALIZABLE** reduce anomalies but increase locking, blocking, or **retries**—design APIs and idempotent retries when using the strictest levels.

## Practical implications

- Short transactions reduce contention.
- Long-running reports may use **snapshot** isolation or **read-only** replicas—engine-specific.
- ORMs and JDBC can hide transaction boundaries; explicit `BEGIN`/`COMMIT` clarifies behavior.

## Quick selection guidance (rules of thumb)

- **Start with defaults** (often READ COMMITTED) unless you can explain the anomaly you’re preventing.
- Use **REPEATABLE READ** when one transaction must see a stable view across multiple reads (and you can tolerate more contention).
- Use **SERIALIZABLE** for strict invariants (caps, balances) and be prepared to **retry** transactions on serialization failures.

## Summary

- **ACID** frames reliable transactional behavior: atomic units, enforced rules, controlled concurrency, durable commits.
- **Isolation levels** define which concurrent anomalies can occur; stricter levels cost throughput or require retry logic.
- Match isolation to **business risk**: financial transfers vs casual analytics differ.

## Additional Resources

- [PostgreSQL Transaction Isolation](https://www.postgresql.org/docs/current/transaction-iso.html)
- [MySQL InnoDB isolation levels](https://dev.mysql.com/doc/refman/8.0/en/innodb-transaction-isolation-levels.html)
- [Microsoft: Isolation levels](https://learn.microsoft.com/en-us/sql/odbc/reference/develop-app/transaction-isolation-levels)
