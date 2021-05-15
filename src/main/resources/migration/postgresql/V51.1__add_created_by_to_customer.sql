ALTER TABLE customer ADD COLUMN created_by_id bigint;
ALTER TABLE customer ADD FOREIGN KEY (created_by_id) REFERENCES profile (id);
