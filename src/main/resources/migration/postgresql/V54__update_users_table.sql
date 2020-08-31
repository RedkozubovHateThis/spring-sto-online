ALTER TABLE users DROP COLUMN subscription_type_id;
ALTER TABLE users DROP COLUMN current_subscription_id;
ALTER TABLE users ADD COLUMN current_ad_subscription_id bigint;
ALTER TABLE users ADD COLUMN current_operator_subscription_id bigint;

ALTER TABLE users ADD FOREIGN KEY (current_ad_subscription_id) REFERENCES subscription(id);
ALTER TABLE users ADD FOREIGN KEY (current_operator_subscription_id) REFERENCES subscription(id);
