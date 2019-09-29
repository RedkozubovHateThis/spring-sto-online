package io.swagger.firebird.repository;

import io.swagger.firebird.model.CarBodyType;
import io.swagger.firebird.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CarBodyTypeRepository extends JpaRepository<CarBodyType, Integer> {
}
