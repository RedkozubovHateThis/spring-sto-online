package io.swagger.firebird.repository;

import io.swagger.firebird.model.CarEngineType;
import io.swagger.firebird.model.InspectionCarImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InspectionCarImageRepository extends JpaRepository<InspectionCarImage, Integer> {
}
