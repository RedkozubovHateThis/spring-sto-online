CREATE SEQUENCE customer_id_seq;
CREATE TABLE customer
(
    id bigint PRIMARY KEY NOT NULL DEFAULT nextval('customer_id_seq'::regclass),
    deleted boolean not null default false,
    name character varying (255),
    address character varying (255),
    email character varying (255),
    phone character varying (255),
    inn character varying (255)
);

ALTER TABLE service_document ADD COLUMN customer_id bigint;
ALTER TABLE service_document ADD FOREIGN KEY (customer_id) REFERENCES customer(id);
