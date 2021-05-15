CREATE SEQUENCE compiled_report_id_seq;

CREATE TABLE compiled_report
(
    id bigint PRIMARY KEY NOT NULL DEFAULT nextval('compiled_report_id_seq'::regclass),
    file_name character varying(250) NOT NULL,
    uuid character varying(50) NOT NULL,
    compile_date timestamp NOT NULL
);

CREATE INDEX compiled_report_uuid_idx ON compiled_report (uuid);
