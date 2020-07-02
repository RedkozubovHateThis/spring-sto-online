ALTER TABLE service_document DROP COLUMN executor_id;
ALTER TABLE service_document DROP COLUMN client_id;
ALTER TABLE service_document ADD COLUMN executor_id bigint;
ALTER TABLE service_document ADD COLUMN client_id bigint;

ALTER TABLE service_document ADD FOREIGN KEY (executor_id) REFERENCES profile(id);
ALTER TABLE service_document ADD FOREIGN KEY (client_id) REFERENCES profile(id);
