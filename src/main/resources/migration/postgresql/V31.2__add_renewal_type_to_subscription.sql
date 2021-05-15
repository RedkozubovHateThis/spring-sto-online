ALTER TABLE subscription ADD COLUMN renewal_type character varying (25);
CREATE INDEX subscription_renewal_type_idx ON subscription (renewal_type);
