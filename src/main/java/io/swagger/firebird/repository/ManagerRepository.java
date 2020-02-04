package io.swagger.firebird.repository;

import io.swagger.firebird.model.CarBodyType;
import io.swagger.firebird.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Integer> {

    @Query("SELECT DISTINCT m FROM Manager AS m " +
            "INNER JOIN m.organizationStructure AS os " +
            "INNER JOIN os.organization AS o " +
            "WHERE o.id = :organizationId")
    List<Manager> findByOrganizationId(@Param("organizationId") Integer organizationId);

}
