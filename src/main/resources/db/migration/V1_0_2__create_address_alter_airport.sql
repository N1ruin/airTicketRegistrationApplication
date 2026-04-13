CREATE TABLE IF NOT EXISTS tickets_application.address
(
    id           BIGSERIAL PRIMARY KEY,
    country      VARCHAR(70)  NOT NULL,
    city         VARCHAR(100) NOT NULL,
    street       VARCHAR(100),
    house_number INT
);

ALTER TABLE tickets_application.airport
    ADD COLUMN address_id BIGINT,
    ADD FOREIGN KEY (address_id) REFERENCES tickets_application.address (id) ON DELETE CASCADE;