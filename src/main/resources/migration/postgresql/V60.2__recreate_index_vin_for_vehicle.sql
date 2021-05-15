DROP INDEX vehicle_vin_number_idx;
CREATE INDEX vehicle_vin_number_idx ON vehicle (vin_number);
