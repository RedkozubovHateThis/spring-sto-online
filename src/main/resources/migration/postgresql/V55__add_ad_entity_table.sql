CREATE SEQUENCE ad_entity_id_seq;
CREATE TABLE ad_entity
(
    id bigint PRIMARY KEY NOT NULL DEFAULT nextval('ad_entity_id_seq'::regclass),
    deleted boolean not null default false,
    name character varying (255),
    description text,
    email character varying (255),
    phone character varying (255),
    url character varying (255),
    create_date timestamp,
    current boolean not null default false,
    side_offer boolean not null default false,
    active boolean not null default true
);

ALTER TABLE users ADD COLUMN ad_entity_id bigint;
ALTER TABLE users ADD FOREIGN KEY (ad_entity_id) REFERENCES ad_entity (id);
