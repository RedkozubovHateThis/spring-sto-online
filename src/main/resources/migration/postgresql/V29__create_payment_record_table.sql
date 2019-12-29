CREATE SEQUENCE payment_record_id_seq;

CREATE TABLE payment_record
(
    id bigint PRIMARY KEY NOT NULL DEFAULT nextval('payment_record_id_seq'::regclass),
    payment_type character varying (25) NOT NULL,
    order_id character varying (50),
    order_number character varying (50) NOT NULL,
    order_status integer,
    action_code integer,
    action_code_description text,
    error_code integer,
    error_message text,
    currency character varying (25),
    amount integer NOT NULL,
    payment_state character varying (25) NOT NULL,
    create_date timestamp,
    register_date timestamp,
    ip_address character varying (50),
    masked_pan character varying (50),
    user_id bigint
);

CREATE UNIQUE INDEX payment_record_order_id_idx ON payment_record (order_id);
CREATE UNIQUE INDEX payment_record_order_number_idx ON payment_record (order_number);
CREATE INDEX payment_record_payment_type_idx ON payment_record (payment_type);
CREATE INDEX payment_record_payment_state_idx ON payment_record (payment_state);
