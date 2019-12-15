CREATE SEQUENCE subscription_id_seq;

CREATE TABLE subscription
(
    id bigint PRIMARY KEY NOT NULL DEFAULT nextval('subscription_id_seq'::regclass),
    name character varying (25) NOT NULL,
    start_date timestamp NOT NULL,
    end_date timestamp NOT NULL,
    is_renewable boolean NOT NULL,
    renewal_cost double precision NOT NULL,
    document_cost double precision NOT NULL,
    documents_count integer NOT NULL,
    user_id bigint,
    as_previous_user_id bigint
);

CREATE INDEX subscription_user_id_idx ON subscription (user_id);
CREATE INDEX subscription_as_previous_user_id_idx ON subscription (as_previous_user_id);
