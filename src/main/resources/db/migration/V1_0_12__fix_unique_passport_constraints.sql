ALTER TABLE tickets_application.passport DROP CONSTRAINT IF EXISTS passport_passport_series_key;
ALTER TABLE tickets_application.passport DROP CONSTRAINT IF EXISTS passport_passport_number_key;
ALTER TABLE tickets_application.passport
    ADD CONSTRAINT passport_unique_series_number UNIQUE (passport_series, passport_number);