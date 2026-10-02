ALTER TABLE customers ADD COLUMN first_name VARCHAR(255);
ALTER TABLE customers ADD COLUMN last_name VARCHAR(255);

CREATE OR REPLACE FUNCTION sync_customer_full_name()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.full_name :=
            CASE
                WHEN NEW.last_name IS NULL OR NEW.last_name = ''
                    THEN NEW.first_name
                ELSE NEW.first_name || ' ' || NEW.last_name
                END;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER customer_name_sync
    BEFORE INSERT OR UPDATE OF first_name, last_name
    ON customers
    FOR EACH ROW
EXECUTE FUNCTION sync_customer_full_name();