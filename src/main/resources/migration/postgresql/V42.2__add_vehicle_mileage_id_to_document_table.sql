ALTER TABLE vehicle_mileage DROP COLUMN document_id;
ALTER TABLE service_document ADD COLUMN vehicle_mileage_id bigint;
ALTER TABLE service_document ADD FOREIGN KEY (vehicle_mileage_id) REFERENCES vehicle_mileage(id);
