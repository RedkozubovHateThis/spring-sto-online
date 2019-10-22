CREATE SEQUENCE document_user_state_id_seq;

CREATE TABLE document_user_state
(
    id bigint PRIMARY KEY NOT NULL DEFAULT nextval('upload_file_id_seq'::regclass),
    documents integer[],
    last_update_date timestamp,
    user_id bigint
);

CREATE INDEX document_user_state_user_id_idx ON document_user_state (user_id);

ALTER TABLE document_user_state ADD FOREIGN KEY (user_id) REFERENCES users (id);
