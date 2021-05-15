ALTER TABLE profile ADD COLUMN created_by_id bigint;
ALTER TABLE profile ADD FOREIGN KEY (created_by_id) REFERENCES profile (id);
