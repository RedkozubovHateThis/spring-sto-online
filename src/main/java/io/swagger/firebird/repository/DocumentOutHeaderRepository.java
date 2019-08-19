package io.swagger.firebird.repository;

import io.swagger.firebird.model.DocumentOutHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentOutHeaderRepository extends JpaRepository<DocumentOutHeader, Integer> {
}
