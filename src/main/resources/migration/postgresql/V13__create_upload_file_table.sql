CREATE SEQUENCE upload_file_id_seq;

CREATE TABLE upload_file
(
    id bigint PRIMARY KEY NOT NULL DEFAULT nextval('upload_file_id_seq'::regclass),
    file_name character varying(250) NOT NULL,
    content_type character varying(250) NOT NULL,
    size bigint NOT NULL,
    uuid character varying(25) NOT NULL,
    upload_date timestamp NOT NULL,
    upload_user_id bigint,
    chat_message_id bigint
);

CREATE INDEX upload_file_uuid_idx ON upload_file (uuid);
CREATE INDEX upload_file_upload_user_id_idx ON upload_file (upload_user_id);
CREATE INDEX upload_file_chat_message_id_idx ON upload_file (chat_message_id);

ALTER TABLE upload_file ADD FOREIGN KEY (upload_user_id) REFERENCES users (id);
ALTER TABLE upload_file ADD FOREIGN KEY (chat_message_id) REFERENCES chat_message (id);
