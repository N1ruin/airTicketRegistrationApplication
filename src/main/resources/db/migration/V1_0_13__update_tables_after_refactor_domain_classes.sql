ALTER TABLE tickets_application.airport
    RENAME COLUMN status TO worked;

ALTER TABLE tickets_application.airport
ALTER
COLUMN worked TYPE BOOLEAN USING worked::BOOLEAN;

ALTER TABLE tickets_application.airport
    ALTER COLUMN worked SET DEFAULT TRUE;

ALTER TABLE tickets_application.users
    RENAME COLUMN user_role TO role;

ALTER TABLE tickets_application.ticket
    RENAME COLUMN service_class TO ticket_rank;

ALTER TABLE tickets_application.flight
ALTER
COLUMN departure_date TYPE TIMESTAMP WITH TIME ZONE;

ALTER TABLE tickets_application.flight
ALTER
COLUMN arrival_date TYPE TIMESTAMP WITH TIME ZONE;

ALTER TABLE tickets_application.ticket
ALTER
COLUMN purchase_date TYPE TIMESTAMP WITH TIME ZONE;

ALTER TABLE tickets_application.ticket
ALTER
COLUMN updated_date TYPE TIMESTAMP WITH TIME ZONE;

ALTER TABLE tickets_application.flight
    RENAME COLUMN departure_airport_id TO departure_airport_code;

ALTER TABLE tickets_application.flight
ALTER
COLUMN departure_airport_code TYPE VARCHAR(10);

ALTER TABLE tickets_application.flight
    RENAME COLUMN arrival_airport_id TO arrival_airport_code;

ALTER TABLE tickets_application.flight
ALTER
COLUMN arrival_airport_code TYPE VARCHAR(10);

ALTER TABLE tickets_application.passenger
DROP
COLUMN IF EXISTS first_name CASCADE;

ALTER TABLE tickets_application.passenger
DROP
COLUMN IF EXISTS last_name CASCADE;

ALTER TABLE tickets_application.passenger
DROP
COLUMN IF EXISTS father_name CASCADE;

ALTER TABLE tickets_application.passenger
DROP
COLUMN IF EXISTS male CASCADE;

ALTER TABLE tickets_application.passenger
DROP
COLUMN IF EXISTS birth_date CASCADE;

ALTER TABLE tickets_application.passport
    ADD COLUMN IF NOT EXISTS first_name VARCHAR (100);

ALTER TABLE tickets_application.passport
    ADD COLUMN IF NOT EXISTS last_name VARCHAR (100);

ALTER TABLE tickets_application.passport
    ADD COLUMN IF NOT EXISTS father_name VARCHAR (100);

ALTER TABLE tickets_application.passport
    ADD COLUMN IF NOT EXISTS birth_date DATE;

ALTER TABLE tickets_application.passport
    ADD COLUMN IF NOT EXISTS passenger_id BIGINT;

ALTER TABLE tickets_application.passport
    ADD CONSTRAINT fk_passport_passenger
        FOREIGN KEY (passenger_id) REFERENCES tickets_application.passenger (id) ON DELETE CASCADE;

ALTER TABLE tickets_application.users
DROP
COLUMN IF EXISTS first_name CASCADE;

ALTER TABLE tickets_application.users
DROP
COLUMN IF EXISTS last_name CASCADE;

ALTER TABLE tickets_application.users
DROP
COLUMN IF EXISTS father_name CASCADE;

ALTER TABLE tickets_application.passenger_favorite_airports
DROP
COLUMN IF EXISTS flights_count CASCADE;

ALTER TABLE tickets_application.passport
    ADD CONSTRAINT passport_unique_series_number UNIQUE (passport_series, passport_number);