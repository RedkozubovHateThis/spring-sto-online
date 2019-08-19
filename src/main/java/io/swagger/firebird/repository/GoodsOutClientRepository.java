package io.swagger.firebird.repository;

import io.swagger.firebird.model.GoodsOutClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsOutClientRepository extends JpaRepository<GoodsOutClient, Integer> {
}
