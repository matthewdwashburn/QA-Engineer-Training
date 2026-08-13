# Exercise: E-commerce schema design (normalized)

## Scenario

You are designing the relational database for a small **online store**. The business needs to:

- Register **customers** (email must be unique; optional phone).
- Sell **products** (SKU unique, price, stock on hand).
- Record **orders** (who ordered, when, status such as `OPEN` / `PAID` / `SHIPPED`).
- Record **order lines** (which product, quantity, price **at time of sale** — think: price can change later on the product row).

Assume:

- A customer may place **many** orders.
- An order contains **many** line items.
- A product may appear on **many** order lines.
- **Addresses:** a customer may have **multiple shipping addresses**; each order ships to **one** address selected at checkout.

## Your tasks

1. **List entities** you need (tables). Include any **junction** or **child** tables required for many-to-many or one-to-many rules above.
2. For each table, list **primary key** strategy (surrogate vs natural) and important **alternate keys** (`UNIQUE`).
3. Draw an **ERD** with **cardinality** (1:1, 1:N, M:N). Use the template:
   - `templates/ecommerce_erd.mermaid`
4. **Write 5–8 bullet “design decisions”**, e.g.:
   - Why `ON DELETE RESTRICT` vs `CASCADE` on `order_line → product`.
   - Why line item stores **`unit_price`** even though `product` has a price.
   - How you model **multiple addresses per customer** and link an **order** to **one** address.

## Definition of done

- ERD includes **customer, product, order, order line**, and **address** modeling that satisfies the scenario.
- You explicitly show **where a many-to-many** would have been wrong and how you avoided it (short paragraph).
- No repeated non-key attributes across tables that violate **3NF** without justification (if you denormalize, say why).

## Reference

- `written/1-monday/normalization.md`
- `written/1-monday/table-relationships-multiplicity.md`
- `demos/1-monday/diagrams/schema-relationships.mermaid` (example shape — your model may differ)
