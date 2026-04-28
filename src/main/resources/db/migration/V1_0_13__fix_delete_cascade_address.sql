ALTER TABLE tickets_application.airport
    DROP CONSTRAINT airport_address_id_fkey;

ALTER TABLE tickets_application.airport
    ADD CONSTRAINT airport_address_id_fkey FOREIGN KEY (address_id)
        REFERENCES tickets_application.address (id) ON DELETE CASCADE;