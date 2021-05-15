ALTER TABLE chat_message ADD COLUMN chat_message_type character varying (50) NOT NULL DEFAULT 'TEXT';
UPDATE chat_message SET chat_message_type = 'FILE' WHERE upload_file_id IS NOT NULL;
