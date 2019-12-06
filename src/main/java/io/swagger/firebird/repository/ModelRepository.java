package io.swagger.firebird.repository;

import io.swagger.firebird.model.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelRepository extends JpaRepository<Model, Integer> {

    @Query("SELECT DISTINCT m FROM DocumentServiceDetail AS dsd " +
            "INNER JOIN dsd.modelLink AS ml " +
            "INNER JOIN ml.modelDetail AS md " +
            "INNER JOIN md.model AS m")
    List<Model> findAllUsed();

    @Query(nativeQuery = true, value = "SELECT COUNT(DISTINCT m.MODEL_ID) FROM MODEL AS m\n" +
            "INNER JOIN MODEL_DETAIL AS md ON md.MODEL_ID = m.MODEL_ID\n" +
            "INNER JOIN MODEL_LINK AS ml ON ml.MODEL_DETAIL_ID = md.MODEL_DETAIL_ID\n" +
            "INNER JOIN DOCUMENT_SERVICE_DETAIL AS dsd ON dsd.MODEL_LINK_ID = ml.MODEL_LINK_ID\n" +
            "INNER JOIN DOCUMENT_OUT_HEADER AS doh ON doh.DOCUMENT_OUT_HEADER_ID = dsd.DOCUMENT_OUT_HEADER_ID\n" +
            "INNER JOIN DOCUMENT_OUT AS do ON do.DOCUMENT_OUT_ID = doh.DOCUMENT_OUT_ID\n" +
            "INNER JOIN CLIENT AS c ON do.CLIENT_ID = c.CLIENT_ID\n" +
            "WHERE c.CLIENT_ID = :clientId")
    Integer countVehiclesByClientId(@Param("clientId") Integer clientId);

}
