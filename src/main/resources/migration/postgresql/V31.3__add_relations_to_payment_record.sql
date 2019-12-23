ALTER TABLE payment_record ADD COLUMN subscription_id bigint;
ALTER TABLE payment_record ADD COLUMN document_id integer;
CREATE INDEX payment_record_subscription_id_idx ON payment_record (subscription_id);
CREATE INDEX payment_record_document_id_idx ON payment_record (document_id);
