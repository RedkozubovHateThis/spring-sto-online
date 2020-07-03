package io.swagger.postgres.repository;

import io.swagger.postgres.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query("SELECT DISTINCT v FROM Vehicle AS v WHERE v.id IN ( SELECT DISTINCT sd.vehicle.id FROM ServiceDocument AS sd WHERE sd.executor.id = :executorId )")
    List<Vehicle> findPreviousVehicles(@Param("executorId") Long executorId);

    @Query("SELECT DISTINCT v FROM Vehicle AS v WHERE upper(v.vinNumber) LIKE upper(:vinNumber)")
    List<Vehicle> findAllByVinNumber(@Param("vinNumber") String vinNumber);

}
