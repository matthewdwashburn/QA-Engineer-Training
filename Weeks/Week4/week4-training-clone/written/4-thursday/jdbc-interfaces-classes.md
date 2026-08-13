# JDBC Interfaces and Classes

## Learning Objectives

- Identify responsibilities of `Connection`, `Statement`, `PreparedStatement`, and `ResultSet`.
- Explain `DriverManager` vs `DataSource` for obtaining connections.
- Use **try-with-resources** for JDBC object cleanup.

## Why This Matters

JDBC types are small in count but easy to confuse. Clear mental models prevent resource leaks (open connections exhausting pools) and help you map Java calls to the SQL you studied earlier in the week.

## The Concept

### DriverManager

Legacy/simple entry point: `DriverManager.getConnection(jdbcUrl, props)` returns a `Connection`. Acceptable for learning; production services prefer **`DataSource`** (pooling, JNDI, framework injection).

### DataSource (preferred in real apps)

`DataSource` is an interface for obtaining connections. In server apps, it is typically implemented by a **connection pool** (e.g., HikariCP). Your code asks for a connection, uses it briefly, then closes it—closing usually **returns it to the pool**, not actually tearing down TCP.

### Connection

Represents a **session** to a database. Used to:

- create statements,
- control transactions (`setAutoCommit`, `commit`, `rollback`),
- access metadata (`DatabaseMetaData`).

Practical note: a `Connection` is not “free.” Treat it as a scarce resource—keep it short-lived and always close it.

### Statement

Executes **static** SQL strings. Vulnerable to **SQL injection** when concatenating user input—prefer `PreparedStatement`.

```java
try (Statement st = conn.createStatement();
     ResultSet rs = st.executeQuery("SELECT id, name FROM product")) {
    while (rs.next()) {
        int id = rs.getInt("id");
        String name = rs.getString("name");
    }
}
```

### PreparedStatement

Precompiled template with **placeholders** (`?` or named in some drivers). Binds parameters with `setInt`, `setString`, etc. **Mitigates SQL injection** and can reuse plans.

```java
String sql = "SELECT * FROM customer WHERE email = ?";
try (PreparedStatement ps = conn.prepareStatement(sql)) {
    ps.setString(1, email);
    try (ResultSet rs = ps.executeQuery()) { /* ... */ }
}
```

### ResultSet

**Cursor** over query rows. `next()` advances; getters read columns by index or label.

### CallableStatement (awareness)

Calls **stored procedures**; extends parameter binding with `OUT` parameters on some databases.

### Resource lifecycle (one rule to remember)

Close in the reverse order you acquired them:

- `ResultSet` → `Statement/PreparedStatement` → `Connection`

Using try-with-resources (as shown) is the safest way to guarantee cleanup on exceptions.

## Summary

- **`Connection`** is the session; **`Statement`/`PreparedStatement`** carry SQL; **`ResultSet`** carries rows.
- Prefer **`PreparedStatement`** for user-influenced predicates.
- Use **`DataSource`** in real apps; **`DriverManager`** suffices for small examples.

## Additional Resources

- [java.sql package overview (Oracle)](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/package-summary.html)
- [JDBC Basics: Connections](https://docs.oracle.com/javase/tutorial/jdbc/basics/connecting.html)
- [HikariCP (connection pooling)](https://github.com/brettwooldridge/HikariCP)
