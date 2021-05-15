CREATE SEQUENCE user_role_id_seq;

CREATE TABLE user_role
(
    id bigint PRIMARY KEY NOT NULL DEFAULT nextval('user_role_id_seq'::regclass),
    name character varying(255),
    name_rus character varying(255)
);

CREATE UNIQUE INDEX user_role_name_idx ON user_role (name);