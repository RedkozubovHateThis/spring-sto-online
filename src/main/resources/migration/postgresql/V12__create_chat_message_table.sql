CREATE SEQUENCE chat_message_id_seq;

CREATE TABLE chat_message
(
    id bigint PRIMARY KEY NOT NULL DEFAULT nextval('chat_message_id_seq'::regclass),
    message_date TIMESTAMP NOT NULL,
    message_text TEXT,
    from_user_id BIGINT NOT NULL,
    to_user_id BIGINT NOT NULL
);

CREATE INDEX chat_message_message_date_idx ON chat_message (message_date);
CREATE INDEX chat_message_from_user_id_idx ON chat_message (from_user_id);
CREATE INDEX chat_message_to_user_id_idx ON chat_message (to_user_id);

ALTER TABLE chat_message ADD FOREIGN KEY (from_user_id) REFERENCES users (id);
ALTER TABLE chat_message ADD FOREIGN KEY (to_user_id) REFERENCES users (id);
