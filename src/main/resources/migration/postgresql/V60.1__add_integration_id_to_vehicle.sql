ALTER TABLE vehicle ADD COLUMN integration_id character varying (255);
CREATE UNIQUE INDEX vehicle_integration_id_idx ON vehicle (integration_id);
