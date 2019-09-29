package io.swagger.firebird.repository;

import io.swagger.firebird.model.Manager;
import io.swagger.firebird.model.OrganizationStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationStructureRepository extends JpaRepository<OrganizationStructure, Integer> {
}
