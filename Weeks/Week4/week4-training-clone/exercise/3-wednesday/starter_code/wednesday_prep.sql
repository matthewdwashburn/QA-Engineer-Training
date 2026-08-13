-- Run after ../2-tuesday/starter_code/baseline_ecommerce.sql (PostgreSQL).
-- Adds an event log table for index exercises.

BEGIN;

CREATE TABLE IF NOT EXISTS product_event (
    id BIGSERIAL PRIMARY KEY,
    product_id INTEGER NOT NULL REFERENCES product (product_id),
    event_type TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

TRUNCATE product_event;

INSERT INTO product_event (product_id, event_type)
SELECT p.product_id,
       CASE WHEN random() < 0.5 THEN 'ADJUST' ELSE 'VIEW' END
FROM product p
CROSS JOIN generate_series(1, 8000) AS s;

ANALYZE product_event;

COMMIT;
