package io.swagger.firebird.repository;

import io.swagger.firebird.model.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelRepository extends JpaRepository<Model, Integer> {

    @Query("SELECT DISTINCT m FROM DocumentServiceDetail AS dsd " +
            "INNER JOIN dsd.modelLink AS ml " +
            "INNER JOIN ml.modelDetail AS md " +
            "INNER JOIN md.model AS m")
    List<Model> findAllUsed();

}
