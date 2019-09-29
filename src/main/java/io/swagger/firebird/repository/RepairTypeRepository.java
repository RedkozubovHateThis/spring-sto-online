package io.swagger.firebird.repository;

import io.swagger.firebird.model.CarBodyType;
import io.swagger.firebird.model.RepairType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairTypeRepository extends JpaRepository<RepairType, Integer> {
}
