INSERT INTO customers (full_name, address, phone)
SELECT DISTINCT customer_full_name, customer_address, customer_phone
FROM orders
WHERE customer_full_name IS NOT NULL;

UPDATE orders o
SET customer_id = c.id
FROM customers c
WHERE o.customer_full_name = c.full_name
  AND COALESCE(o.customer_address, '') = COALESCE(c.address, '')
  AND COALESCE(o.customer_phone, '') = COALESCE(c.phone, '');

ALTER TABLE orders ALTER COLUMN customer_id SET NOT NULL;
ALTER TABLE orders DROP COLUMN customer_full_name;
ALTER TABLE orders DROP COLUMN customer_address;
ALTER TABLE orders DROP COLUMN customer_phone;
