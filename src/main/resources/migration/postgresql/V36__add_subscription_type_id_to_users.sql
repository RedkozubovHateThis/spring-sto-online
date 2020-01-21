ALTER TABLE users DROP COLUMN subscription_type;
ALTER TABLE users ADD COLUMN subscription_type_id bigint;
ALTER TABLE users ADD FOREIGN KEY (subscription_type_id) REFERENCES subscription_type (id);
CREATE INDEX users_subscription_type_id_idx ON users (subscription_type_id);
