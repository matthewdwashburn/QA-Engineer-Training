# Introduction to JDBC

## Learning Objectives

- Define **JDBC** and its role in the Java ecosystem.
- Describe JDBC **architecture**: API, driver manager, JDBC drivers, database.
- Outline the **connection lifecycle** from Java code to executing SQL.

## Why This Matters

This week’s epic ends with **code integration**. **JDBC** is Java’s standard API for relational databases—used directly in apps and under the hood in many frameworks. Understanding JDBC clarifies what ORMs (Friday’s SQLAlchemy) abstract away.

## The Concept

**JDBC** (Java Database Connectivity) is a **vendor-neutral API** (`java.sql`, `javax.sql`) for connecting to relational databases, executing SQL, and processing results.

### Quick glossary

- **JDBC driver**: the library that knows how to talk to a specific database (PostgreSQL, MySQL, SQL Server).
- **JDBC URL**: connection string starting with `jdbc:` that identifies host/db/options.
- **Connection**: a session to the database (expensive to open; must be closed).
- **Statement/PreparedStatement**: objects used to send SQL to the DB.
- **ResultSet**: cursor over returned rows.

### Architecture layers

1. **Application code** uses JDBC interfaces (`Connection`, `Statement`, `PreparedStatement`, `ResultSet`).
2. **DriverManager** or **DataSource** obtains connections.
3. **JDBC driver** (provided by the database vendor or a third party) translates JDBC calls to the database wire protocol.
4. **Database server** executes SQL and returns results.

**Thin drivers** (Type 4) are pure Java and talk native protocols—common today.

### Connection lifecycle (typical)

1. **Load driver** (often automatic via `ServiceLoader` on modern JDBC 4+).
2. **Open connection** with JDBC URL, user, password (or `DataSource` configuration).
3. **Create statement** (`Statement` or `PreparedStatement`).
4. **Execute** SQL; for queries, traverse `ResultSet`.
5. **Close** resources in `finally` or **try-with-resources** (preferred).

```java
String url = "jdbc:postgresql://localhost:5432/training";
try (Connection conn = DriverManager.getConnection(url, user, pass)) {
    // use conn
}
```

### Common pitfalls (what causes bugs)

- **Leaking connections** by not closing them (kills pools, causes timeouts).
- Using **string concatenation** for user input (SQL injection) instead of `PreparedStatement`.
- Assuming **auto-commit** behavior without checking (every statement becomes its own transaction unless disabled).
- Putting a `Connection` in a static/global and sharing across threads (unsafe).

### JDBC in the Java ecosystem

- **Spring JDBC / Spring Data** wrap JDBC for templates and repositories.
- **JPA/Hibernate** use JDBC internally for SQL execution.
- **Connection pools** (`HikariCP`, etc.) implement `DataSource` for scalable server apps.

## Summary

- JDBC is Java’s standard bridge to relational databases via drivers and the `java.sql` API.
- The lifecycle is **connect → statement → execute → process results → close**.
- Frameworks build on JDBC; knowing the baseline helps debug connection leaks and SQL issues.

## Additional Resources

- [Oracle JDBC Tutorial (Java SE)](https://docs.oracle.com/javase/tutorial/jdbc/basics/index.html)
- [PostgreSQL JDBC Documentation](https://jdbc.postgresql.org/documentation/)
- [MySQL Connector/J Developer Guide](https://dev.mysql.com/doc/connector-j/en/)
