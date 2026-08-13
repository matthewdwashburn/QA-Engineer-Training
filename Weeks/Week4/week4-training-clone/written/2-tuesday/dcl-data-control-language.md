# DCL: Data Control Language

## Learning Objectives

- Explain `GRANT` and `REVOKE` for privileges on database objects.
- Describe **role-based access control (RBAC)** at a high level.
- Recognize that permission models differ significantly across vendors.
- Define core security terms: principal, privilege, role, and object.

## Why This Matters

Production databases hold sensitive data. **Least privilege**—granting only what each account needs—is a compliance and security baseline. Application connection strings often use DB users whose DCL-shaped footprint must be **minimal** (e.g., read-only reporting users vs migration admins).

## The Concept

**DCL** governs **who** can perform **which operations** on **which objects** (schemas, tables, views, procedures).

### Core definitions (vocabulary that shows up in real systems)

- **Principal**: an identity that can be granted permissions (a user/login, a service account, or a role depending on vendor).
- **Object**: something you can secure (schema, table, view, sequence, function/procedure).
- **Privilege**: an allowed action on an object (e.g., `SELECT` on a table, `EXECUTE` on a procedure).
- **Role**: a named bundle of privileges that principals can inherit (RBAC).

### Ownership vs privileges (important distinction)

Many systems have an **owner** for an object (or schema). Owners typically have implicit rights that are broader than granted privileges, and ownership transfer is handled by vendor-specific commands. In production, avoid having human accounts own critical objects; prefer controlled ownership patterns.

### GRANT

Gives privileges to a user or role.

Conceptual pattern (syntax varies):

```sql
GRANT SELECT, INSERT ON sales.order TO app_writer;
GRANT SELECT ON sales.order TO reporting_ro;
```

Privileges include `SELECT`, `INSERT`, `UPDATE`, `DELETE`, `REFERENCES`, `TRIGGER`, `EXECUTE` (on procedures), and administrative options.

Some systems also support “granting the ability to grant” via **grant option** / **admin option** (names vary). Treat those as powerful: they allow privilege propagation.

### REVOKE

Removes previously granted privileges.

```sql
REVOKE INSERT ON sales.order FROM app_writer;
```

**CASCADE** / **RESTRICT** options (where supported) control dependent grants.

### Roles (RBAC)

Instead of attaching dozens of grants to each human account, define **roles** (`analyst`, `app_batch`, `dba`) and grant privileges to roles. Users **inherit** role privileges—easier audits and rotation.

```sql
-- Illustrative; exact DDL differs by product
CREATE ROLE analyst;
GRANT SELECT ON ALL TABLES IN SCHEMA reporting TO analyst;
GRANT analyst TO jane_doe;
```

### Users vs applications

- **Human accounts:** often tied to SSO/LDAP in enterprises; DB may still map to roles.
- **Application service accounts:** should not have `DROP DATABASE` or broad DDL unless running migrations under controlled automation.

### Common pitfalls (what causes incidents)

- **Overbroad privileges**: granting `ALL` or wide schema rights to an app user “temporarily” and never reducing them.
- **Mixing runtime and migration users**: applications should use a restricted runtime account; schema migrations should run under a separate, controlled identity.
- **Forgetting default privileges**: some engines don’t automatically grant rights on *new* tables/views to existing roles unless configured; reports break after deployments.

## Portability note

Oracle, SQL Server, PostgreSQL, and MySQL differ in **role syntax**, **default privileges**, and **schema** semantics. Always read the vendor doc for your deployment.

## Summary

- **GRANT** assigns rights; **REVOKE** removes them.
- **RBAC** scales permission management through roles.
- Align DB privileges with **deployment stages** (dev vs prod) and **principle of least privilege**.

## Additional Resources

- [PostgreSQL GRANT](https://www.postgresql.org/docs/current/sql-grant.html)
- [MySQL Account Management](https://dev.mysql.com/doc/refman/8.0/en/account-management-sql.html)
- [SQL Server Permissions](https://learn.microsoft.com/en-us/sql/relational-databases/security/permissions-database-engine)
