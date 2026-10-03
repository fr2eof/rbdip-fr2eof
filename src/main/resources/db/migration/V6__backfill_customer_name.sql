UPDATE customers
SET
    first_name = CASE
                     WHEN position(' ' IN trim(full_name)) = 0
                         THEN trim(full_name)
                     ELSE substring(
                             trim(full_name)
                             FROM 1
                             FOR position(' ' IN trim(full_name)) - 1
                          )
        END,
    last_name = CASE
                    WHEN position(' ' IN trim(full_name)) = 0
                        THEN ''
                    ELSE trim(
                            substring(
                                    trim(full_name)
                                    FROM position(' ' IN trim(full_name)) + 1
                            )
                         )
        END
WHERE first_name IS NULL
   OR last_name IS NULL;


CREATE OR REPLACE FUNCTION sync_order_legacy_customer()
    RETURNS TRIGGER AS $$
BEGIN
    SELECT
        c.first_name ||
        CASE
            WHEN c.last_name IS NULL OR c.last_name = ''
                THEN ''
            ELSE ' ' || c.last_name
            END,
        c.address,
        c.phone
    INTO
        NEW.customer_full_name,
        NEW.customer_address,
        NEW.customer_phone
    FROM customers c
    WHERE c.id = NEW.customer_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER order_customer_legacy_sync
    BEFORE INSERT OR UPDATE OF customer_id
    ON orders
    FOR EACH ROW
EXECUTE FUNCTION sync_order_legacy_customer();