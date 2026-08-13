# Statement vs PreparedStatement in JDBC

## Learning Objectives

- Contrast `Statement` and `PreparedStatement` in terms of **security**, **performance**, and **code clarity**.
- Explain exactly **how SQL injection works** and why parameter binding prevents it at the driver level.
- Use all relevant **`setXxx`** bind methods for common Java types.
- Retrieve **auto-generated keys** from an `INSERT` using `RETURN_GENERATED_KEYS`.
- Apply **batch execution** with `addBatch` / `executeBatch` for efficient bulk DML.
- Combine batching with **transactions** for atomic, high-throughput writes.
- Distinguish `Statement`, `PreparedStatement`, and `CallableStatement` by use case.

## Why This Matters

Unsafe string concatenation in SQL is consistently ranked in the **OWASP Top 10 application vulnerabilities** — and it is entirely preventable. `PreparedStatement` is the **primary defense mechanism** built directly into JDBC. Beyond security, it offers execution-plan reuse and batch APIs that directly impact the performance of data-heavy QA automation. Every pattern in this module has a direct counterpart in Python (`sqlite3` `?` placeholders, SQLAlchemy bound parameters) that you will use in Friday's module — so the concept transfers across every technology stack.

## Background — The Three Statement Types

JDBC exposes three statement interfaces, all extending `java.sql.Statement`:

| Interface | Created via | Purpose |
|---|---|---|
| `Statement` | `conn.createStatement()` | Execute static, parameter-free SQL |
| `PreparedStatement` | `conn.prepareStatement(sql)` | Precompile SQL; bind typed parameters at runtime |
| `CallableStatement` | `conn.prepareCall("{call proc(?,?)}")` | Execute stored procedures with IN/OUT parameters |

This module focuses on `Statement` vs `PreparedStatement`. `CallableStatement` extends `PreparedStatement` and follows the same binding pattern.

---

## The Concept

### Statement — Static SQL

`Statement` is appropriate for **compile-time constant** SQL: DDL (CREATE, DROP) and queries that have **no user-supplied values**.

```java
import java.sql.*;

try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
     Statement stmt = conn.createStatement()) {

    // DDL — no parameters, completely static
    stmt.execute("CREATE TABLE IF NOT EXISTS contact (id SERIAL PRIMARY KEY, name TEXT NOT NULL)");

    // Query — safe because no user data is embedded
    try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM contact")) {
        if (rs.next()) {
            System.out.println("Total contacts: " + rs.getInt(1));
        }
    }
}
```

**Key `Statement` execution methods:**

| Method | Returns | Use when |
|---|---|---|
| `execute(sql)` | `boolean` (true if ResultSet) | DDL or when type is unknown |
| `executeQuery(sql)` | `ResultSet` | SELECT statements |
| `executeUpdate(sql)` | `int` (rows affected) | INSERT / UPDATE / DELETE |

> **Rule:** If the SQL string contains **any** value that comes from outside your code (user input, config file, API response), use `PreparedStatement` — not `Statement`.

---

### The SQL Injection Threat — Mechanics

Consider a login check built with string concatenation:

```java
// ❌ VULNERABLE — never do this
String username = req.getParameter("username");   // e.g., entered by a user
String sql = "SELECT id FROM user_account WHERE username = '" + username + "'";
stmt.executeQuery(sql);
```

**Normal input:** `username = "alice"` → SQL becomes:
```sql
SELECT id FROM user_account WHERE username = 'alice'
```

**Injected input:** `username = "' OR '1'='1"` → SQL becomes:
```sql
SELECT id FROM user_account WHERE username = '' OR '1'='1'
```

The condition `'1'='1'` is always true — the query returns **every row** in the table, bypassing authentication entirely.

**Destructive variant:** `username = "'; DROP TABLE user_account; --"` → SQL becomes:
```sql
SELECT id FROM user_account WHERE username = ''; DROP TABLE user_account; --'
```

This drops the entire table. The `--` comments out the trailing quote so the SQL parser does not error.

---

### PreparedStatement — Bound Parameters

`PreparedStatement` separates **SQL structure** from **data values** at the protocol level. The SQL template (with `?` placeholders) is sent to the database engine first. The engine **parses and validates** it at that point. Parameter values are then bound and sent as typed data — never as SQL text. Even if a value contains `'` or `;`, the engine treats it as a plain string value, not SQL syntax.

```java
// ✅ SAFE — SQL injection impossible
String sql = "SELECT id FROM user_account WHERE username = ?";

try (PreparedStatement ps = conn.prepareStatement(sql)) {
    ps.setString(1, username);    // bound as data, not SQL

    try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
            int userId = rs.getInt("id");
        }
    }
}
```

Regardless of what `username` contains, the engine will only look for a row where the `username` column equals exactly that string — it cannot alter query structure.

---

### `setXxx` Bind Methods — Type Reference

Parameters are bound by **1-based index** using type-specific setter methods:

| Method | Java type | SQL type |
|---|---|---|
| `setString(n, value)` | `String` | `VARCHAR`, `TEXT`, `CHAR` |
| `setInt(n, value)` | `int` | `INTEGER`, `INT` |
| `setLong(n, value)` | `long` | `BIGINT` |
| `setDouble(n, value)` | `double` | `DOUBLE`, `FLOAT` |
| `setBigDecimal(n, value)` | `BigDecimal` | `DECIMAL`, `NUMERIC` |
| `setBoolean(n, value)` | `boolean` | `BOOLEAN`, `BIT` |
| `setDate(n, value)` | `java.sql.Date` | `DATE` |
| `setTimestamp(n, value)` | `java.sql.Timestamp` | `TIMESTAMP` |
| `setNull(n, sqlType)` | — | Inserts SQL `NULL` for the type |
| `setObject(n, value)` | `Object` | Driver infers SQL type (use carefully) |

```java
String sql = "INSERT INTO event (name, occurred_at, score, active) VALUES (?, ?, ?, ?)";

try (PreparedStatement ps = conn.prepareStatement(sql)) {
    ps.setString(1, "Load Test Run");
    ps.setTimestamp(2, Timestamp.from(Instant.now()));
    ps.setDouble(3, 98.6);
    ps.setBoolean(4, true);
    int rowsAffected = ps.executeUpdate();
    System.out.println("Inserted: " + rowsAffected + " row(s)");
}
```

---

### Retrieving Auto-Generated Keys

When an `INSERT` creates a row with a database-generated primary key (e.g., `SERIAL`, `IDENTITY`, `AUTO_INCREMENT`), you can retrieve it without a follow-up `SELECT`:

```java
String sql = "INSERT INTO contact (name, email) VALUES (?, ?)";

try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
    ps.setString(1, "Ada Lovelace");
    ps.setString(2, "ada@example.com");
    ps.executeUpdate();

    try (ResultSet keys = ps.getGeneratedKeys()) {
        if (keys.next()) {
            long newId = keys.getLong(1);
            System.out.println("Generated id: " + newId);
        }
    }
}
```

> Pass `Statement.RETURN_GENERATED_KEYS` as the second argument to `prepareStatement()`. The returned `ResultSet` contains one column per generated key — always check `keys.next()` before reading.

---

### Performance — Execution Plan Caching

When the same `PreparedStatement` is reused with different bindings in a loop, the database engine can **cache the parsed query plan** (driver and server dependent):

1. **First call:** engine parses SQL, builds execution plan, caches it.
2. **Subsequent calls:** engine reuses cached plan; only new parameter values are evaluated.

This avoids repeated parsing overhead — measurable when executing thousands of queries.

```java
String sql = "SELECT score FROM result WHERE test_run_id = ? AND env = ?";

try (PreparedStatement ps = conn.prepareStatement(sql)) {
    for (int runId : runIds) {
        ps.setInt(1, runId);
        ps.setString(2, "production");
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                process(rs.getDouble("score"));
            }
        }
    }
}
```

---

### Batch Execution — Bulk DML

Instead of executing a `PreparedStatement` inside a loop (one network round-trip per row), **batching** accumulates DML operations and sends them to the server in a single call:

```java
String sql = "INSERT INTO log_entry (level, message, created_at) VALUES (?, ?, ?)";

conn.setAutoCommit(false);   // manage transaction manually for atomicity + speed

try (PreparedStatement ps = conn.prepareStatement(sql)) {
    for (LogEntry entry : entries) {
        ps.setString(1, entry.level());
        ps.setString(2, entry.message());
        ps.setTimestamp(3, Timestamp.from(entry.timestamp()));
        ps.addBatch();   // accumulates in client buffer
    }

    int[] results = ps.executeBatch();   // sends all rows in one round-trip
    conn.commit();

    // results[i] = rows affected by the i-th statement (usually 1 for INSERT)
    System.out.println("Batch size: " + results.length);

} catch (SQLException e) {
    conn.rollback();    // roll back entire batch on any failure
    throw e;
} finally {
    conn.setAutoCommit(true);
}
```

**`executeBatch()` return values:**

| Value | Meaning |
|---|---|
| `≥ 0` | Rows affected by that statement |
| `SUCCESS_NO_INFO` (-2) | Success, but driver doesn't report row count |
| `EXECUTE_FAILED` (-3) | That specific statement failed |

> **Always combine batching with `setAutoCommit(false)` + `commit()`**: this wraps all batch rows in a single transaction, improving both consistency (all-or-nothing) and throughput (one commit flush vs N).

---

### Statement vs PreparedStatement — Summary Comparison

| Dimension | `Statement` | `PreparedStatement` |
|---|---|---|
| SQL injection safety | ❌ Vulnerable to concatenation | ✅ Parameters treated as data |
| Dynamic values | ❌ Requires string building | ✅ `setXxx()` methods |
| Execution plan reuse | No | Yes (driver/server dependent) |
| Batch execution | `addBatch(sql)` (different SQL each call) | `addBatch()` (same SQL, different params) |
| Generated key retrieval | `RETURN_GENERATED_KEYS` | `RETURN_GENERATED_KEYS` |
| Best for | DDL, constant SQL | Any SQL with runtime values |

---

## Common Pitfalls

| Pitfall | Problem | Fix |
|---|---|---|
| Concatenating user input into SQL | SQL injection vulnerability | Use `PreparedStatement` with `?` placeholders |
| Calling `ps.executeQuery()` for INSERT/UPDATE | `SQLException` or silent failure | Use `ps.executeUpdate()` for DML |
| 0-indexed parameter positions | `SQLException: parameter index out of range` | JDBC parameters are **1-indexed** |
| Not wrapping batch in a transaction | Partial batch committed on failure; inconsistent state | Use `setAutoCommit(false)` + `commit` / `rollback` |
| Ignoring `EXECUTE_FAILED` in batch results | Silent data loss in bulk loads | Check `results[i]` array; handle failures |
| Calling `conn.close()` before reading `ResultSet` | `ResultSet` becomes invalid | Close `ResultSet` → `Statement` → `Connection` in order |
| Not using try-with-resources | Leaked `Connection` / `PreparedStatement` / `ResultSet` | Always use `try (Resource r = ...)` |

---

## Summary

- **`Statement`** executes static SQL — safe only when **no user data** is embedded in the string.
- **`PreparedStatement`** precompiles SQL; parameters are bound as typed data — **not SQL text** — making injection structurally impossible.
- Bind parameters using type-specific **`setXxx(index, value)`** methods; indexes are **1-based**.
- Retrieve database-generated PKs with **`Statement.RETURN_GENERATED_KEYS`** + `ps.getGeneratedKeys()`.
- **Batching** (`addBatch` / `executeBatch`) sends many DML statements in one round-trip; combine with `setAutoCommit(false)` for atomicity and speed.
- The same security principle — separate SQL structure from data values — applies identically in Python's `sqlite3` (`?`) and SQLAlchemy (bound parameters).

## Additional Resources

- [OWASP SQL Injection Prevention Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/SQL_Injection_Prevention_Cheat_Sheet.html)
- [Oracle JDBC PreparedStatement Javadoc](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/PreparedStatement.html)
- [Oracle JDBC Statement Javadoc](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/Statement.html)
- [PostgreSQL JDBC – Server-side Prepared Statements](https://jdbc.postgresql.org/documentation/server-prepare/)
- [JDBC Tutorial – Using Prepared Statements (Oracle)](https://docs.oracle.com/javase/tutorial/jdbc/basics/prepared.html)
