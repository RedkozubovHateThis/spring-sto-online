ALTER TABLE payment_record ADD COLUMN deposited_amount integer;

CREATE INDEX payment_record_create_date_idx ON payment_record (create_date);
CREATE INDEX payment_record_register_date_idx ON payment_record (register_date);
