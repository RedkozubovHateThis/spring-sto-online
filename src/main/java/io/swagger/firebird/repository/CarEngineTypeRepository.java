package io.swagger.firebird.repository;

import io.swagger.firebird.model.CarBodyType;
import io.swagger.firebird.model.CarEngineType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarEngineTypeRepository extends JpaRepository<CarEngineType, Integer> {
}
