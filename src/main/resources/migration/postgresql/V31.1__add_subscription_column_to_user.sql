ALTER TABLE users ADD COLUMN current_subscription_id bigint;
CREATE INDEX users_current_subscription_id_idx ON users (current_subscription_id);
