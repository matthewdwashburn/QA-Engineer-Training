-- Demo: E-Commerce Schema Creation
-- ERD-to-SQL implementation

-- Reset if needed
DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS customers CASCADE;

-- Create customers table
CREATE TABLE customers (
    customer_id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT NOW()
);

-- Create products table
CREATE TABLE products (
    product_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    stock_quantity INTEGER DEFAULT 0 CHECK (stock_quantity >= 0),
    category VARCHAR(50)
);

-- Create orders table (references customers)
CREATE TABLE orders (
    order_id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL REFERENCES customers(customer_id),
    order_date TIMESTAMP DEFAULT NOW(),
    total_amount DECIMAL(10, 2),
    status VARCHAR(20) DEFAULT 'pending'
);

-- Create order_items junction table
CREATE TABLE order_items (
    item_id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL REFERENCES orders(order_id) ON DELETE CASCADE,
    product_id INTEGER NOT NULL REFERENCES products(product_id),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10, 2) NOT NULL
);

-- Create indexes for foreign keys
CREATE INDEX idx_orders_customer ON orders(customer_id);
CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_product ON order_items(product_id);

-- Insert sample data
INSERT INTO customers (first_name, last_name, email, phone)
VALUES 
    ('Alice', 'Johnson', 'alice@email.com', '555-0101'),
    ('Bob', 'Smith', 'bob@email.com', '555-0102'),
    ('Carol', 'Williams', 'carol@email.com', '555-0103');

INSERT INTO products (name, description, price, stock_quantity, category)
VALUES 
    ('Laptop Pro', '15-inch laptop with 16GB RAM', 999.99, 50, 'Electronics'),
    ('Wireless Mouse', 'Ergonomic wireless mouse', 29.99, 200, 'Electronics'),
    ('Standing Desk', 'Adjustable height desk', 299.99, 30, 'Furniture'),
    ('Monitor 27"', '4K display monitor', 399.99, 40, 'Electronics'),
    ('Keyboard', 'Mechanical keyboard', 79.99, 100, 'Electronics');

-- Create an order for Alice
INSERT INTO orders (customer_id, total_amount, status)
VALUES (1, 1029.98, 'shipped');

-- Add items to Alice's order
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
VALUES 
    (1, 1, 1, 999.99),  -- 1 Laptop
    (1, 2, 1, 29.99);   -- 1 Mouse

-- Create another order for Bob
INSERT INTO orders (customer_id, total_amount, status)
VALUES (2, 779.97, 'pending');

INSERT INTO order_items (order_id, product_id, quantity, unit_price)
VALUES 
    (2, 4, 1, 399.99),  -- 1 Monitor
    (2, 3, 1, 299.99),  -- 1 Desk
    (2, 5, 1, 79.99);   -- 1 Keyboard

-- Query: Show all orders with customer and product details
SELECT 
    c.first_name || ' ' || c.last_name AS customer,
    c.email,
    o.order_id,
    o.order_date,
    o.status,
    p.name AS product,
    oi.quantity,
    oi.unit_price,
    (oi.quantity * oi.unit_price) AS line_total
FROM customers c
JOIN orders o ON c.customer_id = o.customer_id
JOIN order_items oi ON o.order_id = oi.order_id
JOIN products p ON oi.product_id = p.product_id
ORDER BY o.order_id, oi.item_id;

-- Query: Order summary
SELECT 
    o.order_id,
    c.first_name || ' ' || c.last_name AS customer,
    o.order_date,
    COUNT(oi.item_id) AS items,
    o.total_amount,
    o.status
FROM orders o
JOIN customers c ON o.customer_id = c.customer_id
JOIN order_items oi ON o.order_id = oi.order_id
GROUP BY o.order_id, c.first_name, c.last_name, o.order_date, o.total_amount, o.status;
