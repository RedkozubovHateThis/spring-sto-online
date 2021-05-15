ALTER TABLE payment_record ADD COLUMN expiration_date timestamp;
ALTER TABLE payment_record ADD COLUMN is_expired boolean;
CREATE INDEX payment_record_expiration_date_idx ON payment_record (expiration_date);
CREATE INDEX payment_record_is_expired_idx ON payment_record (is_expired);
