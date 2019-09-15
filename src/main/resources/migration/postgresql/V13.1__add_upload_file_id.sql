ALTER TABLE chat_message ADD COLUMN upload_file_id bigint;
ALTER TABLE chat_message ADD FOREIGN KEY (upload_file_id) REFERENCES upload_file (id);
