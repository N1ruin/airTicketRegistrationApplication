ALTER TABLE tickets_application.passenger
    DROP COLUMN passport_series,
    DROP COLUMN passport_number,
    DROP COLUMN citizenship,
    DROP COLUMN passport_issue_date,
    DROP COLUMN passport_expired_date;

CREATE TABLE IF NOT EXISTS tickets_application.passport
(
    id                    BIGSERIAL PRIMARY KEY,
    passport_series       VARCHAR(10) UNIQUE,
    passport_number       VARCHAR(20) UNIQUE NOT NULL,
    citizenship           VARCHAR(50),
    passport_issue_date   DATE,
    passport_expired_date DATE
);

ALTER TABLE tickets_application.passenger
    ADD COLUMN passport_id BIGINT,
    ADD FOREIGN KEY (passport_id) REFERENCES tickets_application.passport (id) ON DELETE CASCADE;
