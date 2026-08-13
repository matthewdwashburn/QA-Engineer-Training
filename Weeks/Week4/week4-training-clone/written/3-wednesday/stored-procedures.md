# Stored Procedures

## Learning Objectives

- Define a **stored procedure** as a named, persisted routine executed by the database server.
- Describe a **stored function** as a routine that **returns** a value for use in expressions.
- Use **parameters** (`IN`, `OUT`, `INOUT`) conceptually across dialects.
- Identify use cases: encapsulation, security boundaries, batch operations, and performance nuances.

## Why This Matters

Teams centralize complex or sensitive operations in procedures so applications call **`CALL place_order(...)`** instead of scattering multi-statement logic. Whether you favor heavy procedures or keep logic in application code is an **architecture** choice—but you must **read** and **maintain** procedures in many enterprises.

## The Concept

A **stored procedure** bundles SQL and procedural constructs (variables, loops, conditionals—vendor-specific) inside the database. It runs **close to data**, reducing round trips.

### Simple example (PostgreSQL PL/pgSQL style)

```sql
CREATE OR REPLACE PROCEDURE deactivate_customer(p_id INT)
LANGUAGE plpgsql
AS $$
BEGIN
  UPDATE customer SET is_active = FALSE WHERE id = p_id;
  INSERT INTO audit_log (entity, entity_id, action)
  VALUES ('customer', p_id, 'DEACTIVATED');
END;
$$;

CALL deactivate_customer(42);
```

Syntax differs in MySQL, SQL Server, Oracle—focus on the **pattern**.

### Parameters

- **IN:** input value (default in many dialects).
- **OUT:** procedure sets a value returned to caller.
- **INOUT:** combined read/write parameter.

```sql
-- Illustrative SQL Server-style shape
-- CREATE PROCEDURE fetch_count @dept_id INT, @cnt INT OUTPUT AS ...
```

### Database functions

A **stored function** (often just “function”) is also a named, persisted routine on the server, but its contract is to **compute and return** a result—typically a **scalar** (number, text, boolean, date) or, in some products, a **table** (table-valued functions in SQL Server, `RETURNS TABLE` in PostgreSQL). Callers use that result **inside an expression**, not only as a standalone command.

**Typical uses**

- Reusable **calculations** (tax, discounts, formatting codes) so every app and report applies the same formula.
- **Predicates and projections** in `SELECT` lists and `WHERE` clauses: `SELECT id, compute_line_total(qty, price) FROM order_line`.
- In some engines, **check constraints**, **generated columns**, or **default expressions**—only where the product allows functions that are **deterministic** or marked safe enough (rules vary widely).

**Side effects:** Many databases expect functions to be **free of visible side effects** (no inserts/updates in a “pure” function). **PostgreSQL** still lets you write volatile functions that modify data, but teams usually keep **data-changing** work in **procedures** so behavior stays predictable. **SQL Server** multi-statement functions have stricter limits than procedures; always read vendor docs before mixing DML into functions.

**Simple example (PostgreSQL PL/pgSQL style)**

```sql
CREATE OR REPLACE FUNCTION line_total(p_qty NUMERIC, p_unit_price NUMERIC)
RETURNS NUMERIC
LANGUAGE plpgsql
IMMUTABLE
AS $$
BEGIN
  RETURN p_qty * p_unit_price;
END;
$$;

-- Use inside a query (expression context)
SELECT id, line_total(quantity, unit_price) AS subtotal
FROM order_line;
```

Syntax and keywords (`DETERMINISTIC`, `RETURN`, table types) differ in MySQL, SQL Server, and Oracle, but the **pattern** is the same: **define once, return a value, call from SQL expressions**.

### Procedures compared to functions

| Aspect | Stored function | Stored procedure |
|--------|-----------------|------------------|
| **Primary result** | **Return value** (scalar or, where supported, table) | Usually **no** return value; may use `OUT` args or **result sets** |
| **Typical call style** | Inside expressions: `SELECT my_fn(col) FROM t` | Standalone: `CALL my_proc(...)` / `EXEC my_proc ...` |
| **Side effects (DML)** | Often **discouraged or restricted**; depends on engine | **Normal**: multi-statement workflows, inserts/updates |
| **Use case** | Shared calculation, reusable predicate | Orchestrate steps, transactions, “do this business operation” |

**Summary:** **Functions** answer “what is the value of…?” and slot into **expressions**. **Procedures** answer “run this process” and are invoked as **commands**. Some engines blur the line (procedures returning result sets; table functions that feel like views)—**check your platform** for `CREATE FUNCTION` vs `CREATE PROCEDURE`, transaction control inside routines, and privileges (`EXECUTE` on both in many systems).

### Use cases

- **Encapsulation:** one API for a multi-step business operation.
- **Security:** grant `EXECUTE` on procedure without granting broad table DML (careful with dynamic SQL and privilege escalation).
- **Performance:** fewer network round trips; sometimes precompiled plans (engine-dependent).
- **Risks:** harder CI/CD testing, vendor lock-in, mixed versioning between app and DB logic.

## Summary

- Procedures persist **named routines** inside the DB, often with **IN/OUT** parameters.
- **Functions** persist **return-value** routines for use in **SQL expressions**; prefer them for shared calculations, procedures for multi-step or side-effect-heavy work.
- They can reduce round trips and centralize rules; they also shift complexity into the database layer.
- Syntax and capabilities are **vendor-specific**—always verify with official documentation.

## Additional Resources

- [PostgreSQL CREATE PROCEDURE](https://www.postgresql.org/docs/current/sql-createprocedure.html)
- [PostgreSQL CREATE FUNCTION](https://www.postgresql.org/docs/current/sql-createfunction.html)
- [MySQL Stored Procedures](https://dev.mysql.com/doc/refman/8.0/en/stored-programs-defining.html)
- [SQL Server CREATE PROCEDURE](https://learn.microsoft.com/en-us/sql/t-sql/statements/create-procedure-transact-sql)
- [SQL Server CREATE FUNCTION](https://learn.microsoft.com/en-us/sql/t-sql/statements/create-function-transact-sql)
