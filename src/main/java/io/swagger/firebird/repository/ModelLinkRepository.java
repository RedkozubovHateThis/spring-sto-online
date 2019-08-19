package io.swagger.firebird.repository;

import io.swagger.firebird.model.ModelLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelLinkRepository extends JpaRepository<ModelLink, Integer> {
}
