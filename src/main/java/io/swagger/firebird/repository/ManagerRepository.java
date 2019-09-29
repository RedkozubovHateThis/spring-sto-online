package io.swagger.firebird.repository;

import io.swagger.firebird.model.CarBodyType;
import io.swagger.firebird.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Integer> {
}
