CREATE SEQUENCE service_document_id_seq;
CREATE SEQUENCE service_addon_id_seq;
CREATE SEQUENCE service_work_id_seq;
CREATE SEQUENCE vehicle_id_seq;
CREATE SEQUENCE vehicle_mileage_id_seq;

CREATE TABLE service_document
(
    id bigint PRIMARY KEY NOT NULL DEFAULT nextval('service_document_id_seq'::regclass),
    deleted boolean not null default false,
    number character varying (50),
    start_date timestamp,
    end_date timestamp,
    status character varying (50),
    cost double precision,
    executor_id bigint,
    client_id bigint,
    vehicle_id bigint
);

CREATE TABLE service_addon
(
    id bigint PRIMARY KEY NOT NULL DEFAULT nextval('service_addon_id_seq'::regclass),
    deleted boolean not null default false,
    number character varying (50),
    name character varying (255),
    count integer,
    cost double precision,
    document_id bigint
);

CREATE TABLE service_work
(
    id bigint PRIMARY KEY NOT NULL DEFAULT nextval('service_work_id_seq'::regclass),
    deleted boolean not null default false,
    number character varying (50),
    name character varying (255),
    count integer,
    price_norm double precision,
    price double precision,
    by_price boolean,
    time_value double precision,
    document_id bigint
);

CREATE TABLE vehicle
(
    id bigint PRIMARY KEY NOT NULL DEFAULT nextval('vehicle_id_seq'::regclass),
    deleted boolean not null default false,
    model_name character varying (100),
    vin_number character varying (50),
    reg_number character varying (50),
    year integer
);

CREATE TABLE vehicle_mileage
(
    id bigint PRIMARY KEY NOT NULL DEFAULT nextval('vehicle_mileage_id_seq'::regclass),
    deleted boolean not null default false,
    mileage integer,
    vehicle_id bigint,
    document_id bigint
);

ALTER TABLE service_document ADD FOREIGN KEY (executor_id) REFERENCES users(id);
ALTER TABLE service_document ADD FOREIGN KEY (client_id) REFERENCES users(id);
ALTER TABLE service_document ADD FOREIGN KEY (vehicle_id) REFERENCES vehicle(id);

ALTER TABLE service_addon ADD FOREIGN KEY (document_id) REFERENCES service_document(id);

ALTER TABLE service_work ADD FOREIGN KEY (document_id) REFERENCES service_document(id);

ALTER TABLE vehicle_mileage ADD FOREIGN KEY (vehicle_id) REFERENCES vehicle(id);
ALTER TABLE vehicle_mileage ADD FOREIGN KEY (document_id) REFERENCES service_document(id);
