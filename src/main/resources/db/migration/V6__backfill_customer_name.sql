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