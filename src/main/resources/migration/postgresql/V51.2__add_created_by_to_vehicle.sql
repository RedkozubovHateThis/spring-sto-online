ALTER TABLE vehicle ADD COLUMN created_by_id bigint;
ALTER TABLE vehicle ADD FOREIGN KEY (created_by_id) REFERENCES profile (id);
