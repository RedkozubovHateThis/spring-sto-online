ALTER TABLE subscription DROP COLUMN renewal_type;
ALTER TABLE users ADD COLUMN subscription_type character varying (25);
CREATE INDEX users_renewal_type_idx ON users (subscription_type);
