CREATE SEQUENCE subscription_type_id_seq;

CREATE TABLE subscription_type
(
    id bigint PRIMARY KEY NOT NULL DEFAULT nextval('subscription_type_id_seq'::regclass),
    sort_position integer NOT NULL,
    name character varying (25) NOT NULL,
    is_free boolean NOT NULL,
    cost double precision NOT NULL,
    document_cost double precision NOT NULL,
    documents_count integer NOT NULL,
    duration_days integer NOT NULL
);

ALTER TABLE subscription ADD COLUMN subscription_type_id bigint;
ALTER TABLE subscription ADD FOREIGN KEY (subscription_type_id) REFERENCES subscription_type (id);
CREATE INDEX subscription_subscription_type_id_idx ON subscription (subscription_type_id);

INSERT INTO subscription_type (sort_position, name, is_free, cost, document_cost, documents_count, duration_days)
VALUES (10, 'Пробный', true, 0.0, 0.0, 5, 15);
INSERT INTO subscription_type (sort_position, name, is_free, cost, document_cost, documents_count, duration_days)
VALUES (20, 'Эконом', false, 7500.0, 250.0, 30, 30);
INSERT INTO subscription_type (sort_position, name, is_free, cost, document_cost, documents_count, duration_days)
VALUES (30, 'Стандарт', false, 11500.0, 230.0, 50, 30);
INSERT INTO subscription_type (sort_position, name, is_free, cost, document_cost, documents_count, duration_days)
VALUES (40, 'Проф', false, 16000.0, 200.0, 80, 30);
