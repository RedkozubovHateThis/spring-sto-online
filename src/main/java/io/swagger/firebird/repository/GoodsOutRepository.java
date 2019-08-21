package io.swagger.firebird.repository;

import io.swagger.firebird.model.GoodsOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsOutRepository extends JpaRepository<GoodsOut, Integer> {
}
