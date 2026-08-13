# Database Vendors: MySQL, PostgreSQL, Oracle, SQL Server, SQLite

## Learning Objectives

- Compare major relational database products by strengths and typical use cases.
- Understand licensing models at a high level (open source vs commercial).
- Choose a sensible default for learning vs production scenarios.
- Recognize where “SQL” differs by vendor (dialects, types, tooling).

## Why This Matters

Job postings and real systems name specific engines. Interviewers expect you to know **why** a team might pick PostgreSQL over MySQL, or when SQLite is enough. Your SQL skills transfer across products, but **features, tooling, and licensing** differ—awareness prevents costly mismatches in architecture and compliance.

## The Concept

### A quick, practical definition

A **database vendor/product** is a packaged database engine plus its surrounding ecosystem: storage and query engine, SQL dialect, drivers, admin tooling, backup/restore, replication/HA options, and support model. Most of what you learn in SQL transfers, but the details that trip teams up are usually **dialect + operations**.

### What differs across “SQL” engines

Common areas of variation:

- **SQL dialect**: syntax (`LIMIT` vs `TOP`), upsert patterns, CTE behavior, window function support, date/time functions.
- **Data types**: booleans, JSON types, UUIDs, text/blob handling, time zone behavior.
- **Concurrency/locking**: how reads and writes interact under load (MVCC vs lock-heavy models).
- **Extensions**: geospatial, full-text search, procedural languages, custom indexes.
- **Operational tooling**: backups, monitoring, replication, clustering, cloud-managed offerings.

### MySQL

**MySQL** (now under Oracle stewardship in the community/commercial split ecosystem) is widely used in web stacks (e.g., LAMP). It emphasizes ease of deployment, replication, and a large hosting footprint. **MariaDB** is a popular fork with API compatibility.

- **Use cases:** Web applications, CMS, SaaS backends where operational familiarity and ecosystem matter.
- **Licensing:** Dual-licensed (GPL community edition; commercial licenses from vendor for some distributions). Verify your distribution’s terms for redistribution.

### PostgreSQL

**PostgreSQL** is a powerful open-source RDBMS with strong standards compliance, rich types (JSON, arrays, ranges), extensions, and advanced indexing. It is a frequent choice for new greenfield services that need reliability and SQL depth without a per-core enterprise contract.

- **Use cases:** General-purpose OLTP, analytics-friendly workloads, geospatial (PostGIS), complex queries.
- **Licensing:** PostgreSQL License (permissive, BSD-style).

### Oracle Database

**Oracle** is a long-standing enterprise RDBMS known for scalability, tooling, and enterprise support. Features span high availability, partitioning, and advanced security options.

- **Use cases:** Large enterprises, packaged applications historically tied to Oracle, environments with existing Oracle operations teams.
- **Licensing:** Proprietary; typically **perpetual or subscription** with named user or processor metrics. Compliance is a major operational concern.

### Microsoft SQL Server

**SQL Server** integrates tightly with the Microsoft stack (.NET, Azure). Editions range from free **Express** to enterprise features (Always On, columnstore, etc.).

- **Use cases:** Windows-centric enterprises, .NET applications, Azure-first architectures.
- **Licensing:** Proprietary; developer/learning editions exist; production licensing is per-core or server/CAL depending on edition.

### SQLite

**SQLite** is an **embedded**, serverless library: the database is usually a single file in your process. No separate server to administer.

- **Use cases:** Mobile apps, desktop tools, embedded devices, tests, low-traffic internal tools, prototypes.
- **Licensing:** Public domain–style (SQLite is dedicated to the public domain per project claims); widely embeddable.

## Comparison at a Glance

| Product     | Deployment model   | Typical scale        | License (high level)   |
|------------|--------------------|----------------------|-------------------------|
| MySQL      | Client/server      | Small to very large  | OSS + commercial options |
| PostgreSQL | Client/server      | Small to very large  | Permissive OSS          |
| Oracle     | Client/server      | Mid to very large    | Proprietary             |
| SQL Server | Client/server      | Small to very large  | Proprietary             |
| SQLite     | In-process / file  | Single-node, bounded | Public domain           |

## Choosing a default (rules of thumb)

- **Learning SQL + relational design**: start with **PostgreSQL** (strong standards alignment, great error messages, rich feature set) or **SQLite** (zero setup) if you’re only doing local exercises.
- **Embedded / mobile / desktop / tests**: **SQLite**.
- **Existing ecosystem constraints**: choose what your team runs already (e.g., SQL Server in a Microsoft shop).
- **Need deep SQL features without enterprise licensing**: often **PostgreSQL**.
- **Hosted commodity web workloads**: often **MySQL/MariaDB**.

## Dialect “gotchas” (portable mental model)

When switching engines, you most commonly need to adjust:

- **Pagination**: `LIMIT ... OFFSET ...` (PostgreSQL/MySQL/SQLite) vs `TOP`/`OFFSET ... FETCH` (SQL Server).
- **Auto-increment**: `SERIAL`/`IDENTITY`/`AUTO_INCREMENT` differences.
- **Booleans**: `BOOLEAN` vs `BIT` vs integer patterns.
- **Date/time**: `CURRENT_TIMESTAMP` is common, but time zone types/behavior varies.

## Summary

- **PostgreSQL** and **MySQL/MariaDB** dominate open-source server deployments; PostgreSQL often wins on standards and advanced features.
- **Oracle** and **SQL Server** remain strong in enterprises with existing vendor relationships and tooling.
- **SQLite** is ideal when you want zero server ops and can accept single-writer, file-local constraints.
- Always confirm **license terms** before embedding or redistributing a database with a product.

## Additional Resources

- [MySQL Documentation (Oracle)](https://dev.mysql.com/doc/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [SQLite Documentation](https://www.sqlite.org/docs.html)
