# Week 4 — Thursday pair exercise (JDBC + DAO)

**Format:** **Pair programming** — synthesize SQL persistence with **Java JDBC** and the **DAO** pattern.

## Time box

Roughly **3–4 hours** (can split across two sessions).

## Roles

| Partner | First rotation | Swap |
|---------|----------------|------|
| **Driver A / Navigator B** | DAO interfaces + `Jdbc*Dao` with **`PreparedStatement`** only | Swap keyboard |
| **Driver B / Navigator A** | Service + **manual test harness** (`main` or JUnit if configured) calling DAOs | Same |

## Starter kit

See **`starter_code/`** — SQLite file DB, Maven `pom.xml`, and TODO classes.

## Exercise document

Follow **[pair_programming_jdbc.md](pair_programming_jdbc.md)** for deliverables and grading checklist.

## Artifact

One **repo folder** or zip per pair containing:

- Source under `src/main/java`
- Short **`PAIR_REPORT.md`**: who drove which part, one JDBC lesson learned, one pitfall avoided
