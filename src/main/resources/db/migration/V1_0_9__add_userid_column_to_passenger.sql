ALTER TABLE tickets_application.passenger
    ADD COLUMN user_id BIGINT REFERENCES tickets_application.users (id)  ON DELETE CASCADE