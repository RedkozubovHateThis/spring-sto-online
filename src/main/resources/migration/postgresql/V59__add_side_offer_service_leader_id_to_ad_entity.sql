ALTER TABLE ad_entity ADD COLUMN side_offer_service_leader_id bigint;
ALTER TABLE ad_entity ADD FOREIGN KEY (side_offer_service_leader_id) REFERENCES users (id);
