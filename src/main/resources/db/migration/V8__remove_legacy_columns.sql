ALTER TABLE customers
    DROP COLUMN full_name;

ALTER TABLE orders
    DROP COLUMN customer_full_name;

ALTER TABLE orders
    DROP COLUMN customer_address;

ALTER TABLE orders
    DROP COLUMN customer_phone;