CREATE SEQUENCE event_message_id_seq;

CREATE TABLE event_message
(
    id bigint PRIMARY KEY NOT NULL DEFAULT nextval('event_message_id_seq'::regclass),
    message_date timestamp not null,
    message_type character varying (50) not null,
    additional_information text,
    document_id integer,
    document_name character varying (50),
    send_user_id bigint,
    target_user_id bigint
);

CREATE INDEX event_message_send_user_id_idx ON event_message (send_user_id);
CREATE INDEX event_message_target_user_id_idx ON event_message (target_user_id);
CREATE INDEX event_message_message_date_idx ON event_message (message_date);
CREATE INDEX event_message_message_type_idx ON event_message (message_type);

ALTER TABLE event_message ADD FOREIGN KEY (send_user_id) REFERENCES users (id);
ALTER TABLE event_message ADD FOREIGN KEY (target_user_id) REFERENCES users (id);
