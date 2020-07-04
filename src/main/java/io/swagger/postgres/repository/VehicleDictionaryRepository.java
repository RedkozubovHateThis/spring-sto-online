package io.swagger.postgres.repository;

import io.swagger.postgres.model.VehicleDictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleDictionaryRepository extends JpaRepository<VehicleDictionary, Long> {
}
