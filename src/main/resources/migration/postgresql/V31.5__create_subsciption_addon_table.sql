CREATE SEQUENCE subscription_addon_id_seq;

CREATE TABLE subscription_addon
(
    id bigint PRIMARY KEY NOT NULL DEFAULT nextval('subscription_addon_id_seq'::regclass),
    date timestamp NOT NULL,
    documents_count integer NOT NULL,
    cost double precision NOT NULL,
    subscription_id bigint
);

CREATE INDEX subscription_addon_subscription_id_idx ON subscription_addon (subscription_id);

ALTER TABLE payment_record ADD COLUMN subscription_addon_id bigint;
CREATE INDEX payment_record_subscription_addon_id_idx ON payment_record (subscription_addon_id);
