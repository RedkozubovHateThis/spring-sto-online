package io.swagger.postgres.repository;

import io.swagger.postgres.model.ServiceAddon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceAddonRepository extends JpaRepository<ServiceAddon, Long> {
    List<ServiceAddon> findByDocumentId(Long documentId);
}
