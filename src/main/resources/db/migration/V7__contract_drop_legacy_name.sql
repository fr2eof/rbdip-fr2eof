DROP TRIGGER IF EXISTS customer_name_sync ON customers;
DROP FUNCTION IF EXISTS sync_customer_full_name();

ALTER TABLE customers ALTER COLUMN first_name SET NOT NULL;
ALTER TABLE customers ALTER COLUMN last_name SET NOT NULL;
ALTER TABLE customers DROP COLUMN full_name;
