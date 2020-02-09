package io.swagger.firebird.repository;

import io.swagger.firebird.model.ModelDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelDetailRepository extends JpaRepository<ModelDetail, Integer> {

    @Deprecated
    @Query("SELECT DISTINCT md.vinNumber from ModelDetail AS md " +
            "INNER JOIN md.modelLinks AS ml " +
            "INNER JOIN ml.documentServiceDetails AS dsd " +
            "INNER JOIN dsd.documentOutHeader AS doh " +
            "INNER JOIN doh.documentOut AS do " +
            "INNER JOIN do.client AS c " +
            "WHERE c.id = :clientId AND md.vinNumber IS NOT NULL")
    String[] findVinNumbers(@Param("clientId") Integer clientId);

}
