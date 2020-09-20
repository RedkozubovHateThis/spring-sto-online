ALTER TABLE service_document ADD COLUMN integration_id character varying (255);
ALTER TABLE service_addon ADD COLUMN integration_id character varying (255);
ALTER TABLE service_work ADD COLUMN integration_id character varying (255);
ALTER TABLE customer ADD COLUMN integration_id character varying (255);
ALTER TABLE profile ADD COLUMN integration_id character varying (255);
