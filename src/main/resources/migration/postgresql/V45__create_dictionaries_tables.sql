CREATE SEQUENCE service_addon_dictionary_id_seq;
CREATE SEQUENCE service_work_dictionary_id_seq;
CREATE SEQUENCE vehicle_dictionary_id_seq;

CREATE TABLE service_addon_dictionary
(
    id bigint PRIMARY KEY NOT NULL DEFAULT nextval('service_addon_dictionary_id_seq'::regclass),
    deleted boolean not null default false,
    is_initial boolean not null default false,
    name character varying (255)
);

CREATE TABLE service_work_dictionary
(
    id bigint PRIMARY KEY NOT NULL DEFAULT nextval('service_work_dictionary_id_seq'::regclass),
    deleted boolean not null default false,
    is_initial boolean not null default false,
    name character varying (255)
);

CREATE TABLE vehicle_dictionary
(
    id bigint PRIMARY KEY NOT NULL DEFAULT nextval('vehicle_dictionary_id_seq'::regclass),
    deleted boolean not null default false,
    is_initial boolean not null default false,
    name character varying (255)
);
