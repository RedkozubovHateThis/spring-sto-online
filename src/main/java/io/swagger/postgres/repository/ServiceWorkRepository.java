package io.swagger.postgres.repository;

import io.swagger.postgres.model.ServiceWork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceWorkRepository extends JpaRepository<ServiceWork, Long> {
    List<ServiceWork> findByDocumentId(Long documentId);
}
