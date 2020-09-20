CREATE UNIQUE INDEX service_document_integration_id_idx ON service_document (integration_id);
CREATE UNIQUE INDEX service_addon_integration_id_idx ON service_addon (integration_id);
CREATE UNIQUE INDEX service_work_integration_id_idx ON service_work (integration_id);
CREATE UNIQUE INDEX customer_integration_id_idx ON customer (integration_id);
CREATE UNIQUE INDEX profile_integration_id_idx ON profile (integration_id);
