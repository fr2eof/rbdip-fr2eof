CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    address VARCHAR(500),
    phone VARCHAR(50)
);

ALTER TABLE orders ADD COLUMN customer_id BIGINT REFERENCES customers(id);
