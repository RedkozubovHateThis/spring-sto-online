DROP INDEX upload_file_uuid_idx;
CREATE UNIQUE INDEX upload_file_uuid_idx ON upload_file (uuid);
