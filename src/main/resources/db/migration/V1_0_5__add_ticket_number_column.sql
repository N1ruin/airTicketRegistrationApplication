ALTER TABLE tickets_application.ticket ADD COLUMN ticket_number SERIAL UNIQUE;
ALTER TABLE tickets_application.ticket ADD COLUMN updated_date TIMESTAMP;
ALTER TABLE tickets_application.ticket ADD COLUMN ticket_status VARCHAR(20);