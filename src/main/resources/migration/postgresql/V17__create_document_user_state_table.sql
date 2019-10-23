CREATE SEQUENCE document_user_state_id_seq;

CREATE TABLE document_user_state
(
    id bigint PRIMARY KEY NOT NULL DEFAULT nextval('document_user_state_id_seq'::regclass),
    documents integer[] not null,
    last_update_date timestamp not null,
    user_id bigint
);

CREATE UNIQUE INDEX document_user_state_user_id_idx ON document_user_state (user_id);

ALTER TABLE document_user_state ADD FOREIGN KEY (user_id) REFERENCES users (id);
