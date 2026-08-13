# DAO Design Pattern with JDBC

## Learning Objectives

- Define the **Data Access Object (DAO)** pattern and its boundaries.
- Explain **separation of concerns** between persistence and business logic.
- Sketch an **interface-based** DAO and a JDBC implementation for a domain type.

## Why This Matters

Sprinkling JDBC across UI or service code creates untestable, tightly coupled systems. DAOs (or repositories) **localize** SQL, map rows to objects, and swap implementations (e.g., in-memory tests). Thursday’s pair exercise uses this structure explicitly.

## The Concept

### Roles

- **Domain model:** `Customer`, `Order`—pure business data (ideally persistence-agnostic).
- **DAO interface:** operations the app needs (`findById`, `save`, `delete`).
- **DAO implementation:** JDBC code, SQL strings, `ResultSet` mapping, exception translation.

### DAO vs service layer (a crisp boundary)

- **DAO** answers: “How do I store/retrieve this entity from the database?”
- **Service** answers: “What is the business workflow?” (may call multiple DAOs, apply rules, and manage transactions).

Rule of thumb: if the code contains **business decisions** (“only allow shipment if…”) it belongs in services; if it contains **SQL + mapping**, it belongs in DAOs.

### Interface-based design

```java
public interface CustomerDao {
    Optional<Customer> findById(long id);
    long insert(Customer customer);
    void update(Customer customer);
    void delete(long id);
}
```

A JDBC class `JdbcCustomerDao` implements this using `Connection` or a `DataSource` injected from outside—**no** `DriverManager` calls scattered through services.

### Mapping rows to objects

Centralize mapping to avoid duplication:

```java
private Customer mapRow(ResultSet rs) throws SQLException {
    return new Customer(
        rs.getLong("id"),
        rs.getString("email")
    );
}
```

### Separation of concerns

- **Service layer:** orchestrates use cases, transactions, validation.
- **DAO layer:** SQL + mapping only.
- **Controller/UI:** never constructs SQL.

### Transaction ownership (common architecture choice)

DAOs should usually **not** call `commit()` themselves. Keep transaction boundaries in the service layer so one business operation can:

- update multiple tables/DAOs, then
- commit once (or rollback once).

### Testing

With interfaces, provide a **fake** or **in-memory** DAO for fast unit tests; integration tests hit a real database with migrations.

## Summary

- **DAO** encapsulates persistence access per aggregate/table family.
- **Interfaces + JDBC implementations** enable cleaner services and better tests.
- Keep **SQL and mapping** out of higher layers—your future self thanks you during schema changes.

## Additional Resources

- [Core J2EE Patterns: Data Access Object (historical but clear)](https://www.oracle.com/java/technologies/data-access-object.html)
- [Martin Fowler: Repository vs DAO discourse (blog context)](https://martinfowler.com/eaaCatalog/repository.html)
- [Oracle JDBC Tutorial: JDBC Basics](https://docs.oracle.com/javase/tutorial/jdbc/basics/index.html)
