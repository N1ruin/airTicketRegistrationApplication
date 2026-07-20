CREATE SCHEMA IF NOT EXISTS tickets_application;

CREATE TABLE IF NOT EXISTS tickets_application.passenger
(
    id                    BIGSERIAL PRIMARY KEY,
    first_name            VARCHAR(100)       NOT NULL,
    last_name             VARCHAR(100)       NOT NULL,
    father_name           VARCHAR(100),
    male                  BOOLEAN            NOT NULL,
    birth_date            DATE               NOT NULL,
    passport_series       VARCHAR(10) UNIQUE,
    passport_number       VARCHAR(20) UNIQUE NOT NULL,
    citizenship           VARCHAR(50),
    passport_issue_date   DATE,
    passport_expired_date DATE
);

CREATE TABLE IF NOT EXISTS tickets_application.airport
(
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(10)  NOT NULL UNIQUE,
    name    VARCHAR(100) NOT NULL,
    address TEXT
);

CREATE TABLE IF NOT EXISTS tickets_application.flight
(
    id                   BIGSERIAL PRIMARY KEY,
    departure_airport_id BIGINT    NOT NULL REFERENCES tickets_application.airport (id),
    arrival_airport_id   BIGINT    NOT NULL REFERENCES tickets_application.airport (id),
    departure_date       TIMESTAMP NOT NULL,
    arrival_date         TIMESTAMP NOT NULL,
    all_seats            INTEGER   NOT NULL,
    free_seats           INTEGER   NOT NULL
);

CREATE TABLE IF NOT EXISTS tickets_application.ticket
(
    id              BIGSERIAL PRIMARY KEY,
    flight_id       BIGINT      NOT NULL REFERENCES tickets_application.flight (id),
    passenger_id    BIGINT      NOT NULL REFERENCES tickets_application.passenger (id),
    purchase_date   TIMESTAMP   NOT NULL,
    service_class   VARCHAR(20) NOT NULL,
    seat_number     INTEGER     NOT NULL,
    baggage_weight  DOUBLE PRECISION,
    carry_on_weight DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS tickets_application.users
(
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name    VARCHAR(100),
    last_name     VARCHAR(100),
    father_name   VARCHAR(100),
    user_role     VARCHAR(20)  NOT NULL,
    last_login    TIMESTAMP,
    is_blocked    BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS tickets_application.passenger_favorite_airports
(
    passenger_id BIGINT REFERENCES tickets_application.passenger (id) ON DELETE CASCADE,
    airport_id   BIGINT REFERENCES tickets_application.airport (id) ON DELETE CASCADE,
    PRIMARY KEY (passenger_id, airport_id)
);