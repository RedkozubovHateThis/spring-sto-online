package io.swagger.postgres.repository;

import io.swagger.postgres.model.VehicleMileage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleMileageRepository extends JpaRepository<VehicleMileage, Long> {
}
