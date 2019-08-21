package io.swagger.firebird.repository;

import io.swagger.firebird.model.DocumentOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentOutRepository extends JpaRepository<DocumentOut, Integer> {
}