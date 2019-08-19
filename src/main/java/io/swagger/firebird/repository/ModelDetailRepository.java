package io.swagger.firebird.repository;

import io.swagger.firebird.model.ModelDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelDetailRepository extends JpaRepository<ModelDetail, Integer> {
}
