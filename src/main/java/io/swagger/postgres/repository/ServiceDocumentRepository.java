package io.swagger.postgres.repository;

import io.swagger.postgres.model.ServiceDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceDocumentRepository extends JpaRepository<ServiceDocument, Long> {
}
