package io.swagger.postgres.repository;

import io.swagger.postgres.model.VehicleDictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleDictionaryRepository extends JpaRepository<VehicleDictionary, Long> {

    @Query("SELECT DISTINCT vd FROM VehicleDictionary AS vd WHERE lower(vd.name) LIKE lower(:name)")
    List<VehicleDictionary> findAllByName(@Param("name") String name);
}
