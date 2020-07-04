package io.swagger.postgres.repository;

import io.swagger.postgres.model.ServiceAddonDictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceAddonDictionaryRepository extends JpaRepository<ServiceAddonDictionary, Long> {
}
