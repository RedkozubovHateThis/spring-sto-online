ALTER TABLE payment_record ADD COLUMN promised_record_id bigint;
CREATE INDEX payment_record_promised_record_id_idx ON payment_record (promised_record_id);
