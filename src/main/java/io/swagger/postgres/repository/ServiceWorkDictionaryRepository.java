package io.swagger.postgres.repository;

import io.swagger.postgres.model.ServiceWorkDictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceWorkDictionaryRepository extends JpaRepository<ServiceWorkDictionary, Long> {
}
