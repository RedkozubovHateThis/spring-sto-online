package io.swagger.firebird.repository;

import io.swagger.firebird.model.Color;
import io.swagger.firebird.model.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Integer> {
}
