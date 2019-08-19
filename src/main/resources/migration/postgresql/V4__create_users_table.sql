CREATE SEQUENCE users_id_seq;

CREATE TABLE users
(
    id bigint PRIMARY KEY NOT NULL DEFAULT nextval('users_id_seq'::regclass),
    account_expired boolean NOT NULL,
    account_locked boolean NOT NULL,
    credentials_expired boolean NOT NULL,
    email character varying(255),
    enabled boolean NOT NULL,
    first_name character varying(255),
    last_name character varying(255),
    middle_name character varying(255),
    password character varying(255),
    phone character varying(255),
    username character varying(255)
);

CREATE UNIQUE INDEX users_username_idx ON users (username);
CREATE UNIQUE INDEX users_email_idx ON users (email);
CREATE UNIQUE INDEX users_phone_idx ON users (phone);