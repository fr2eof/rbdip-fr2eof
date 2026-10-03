ALTER TABLE customers
    ALTER COLUMN full_name DROP NOT NULL;

ALTER TABLE orders
    ALTER COLUMN customer_full_name DROP NOT NULL;

DROP TRIGGER IF EXISTS order_customer_legacy_sync ON orders;
DROP FUNCTION IF EXISTS sync_order_legacy_customer();

DROP TRIGGER IF EXISTS customer_name_sync ON customers;
DROP FUNCTION IF EXISTS sync_customer_full_name();

ALTER TABLE customers
    ALTER COLUMN first_name SET NOT NULL;

ALTER TABLE customers
    ALTER COLUMN last_name SET NOT NULL;