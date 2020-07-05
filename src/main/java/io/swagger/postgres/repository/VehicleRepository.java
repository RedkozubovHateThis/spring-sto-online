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

    @Query("SELECT DISTINCT v FROM Vehicle AS v " +
            "WHERE upper(v.vinNumber) LIKE upper(:vinNumber) " +
            "OR upper(v.regNumber) LIKE upper(:regNumber) " +
            "OR upper(v.modelName) LIKE upper(:modelName)")
    List<Vehicle> findAllByVinNumberOrRegNumberOrModelName(@Param("vinNumber") String vinNumber,
                                                           @Param("regNumber") String regNumber,
                                                           @Param("modelName") String modelName);

    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT v.id FROM vehicle AS v " +
            "WHERE upper(v.vin_number) = upper(:vinNumber) AND v.deleted = false )")
    Boolean isVehicleExistsVin(@Param("vinNumber") String vinNumber);

    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT v.id FROM vehicle AS v " +
            "WHERE upper(v.vin_number) = upper(:vinNumber) AND v.id <> :id AND v.deleted = false )")
    Boolean isVehicleExistsVinNotSelf(@Param("vinNumber") String vinNumber,
                                      @Param("id") Long id);

}
