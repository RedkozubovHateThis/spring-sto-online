package io.swagger.firebird.repository;

import io.swagger.firebird.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {

    @Query(nativeQuery = true, value = "SELECT FIRST 1 DISTINCT c.* FROM CLIENT AS c\n" +
            "INNER JOIN DOCUMENT_OUT AS do ON do.CLIENT_ID = c.CLIENT_ID\n" +
            "INNER JOIN DOCUMENT_OUT_HEADER AS doh ON doh.DOCUMENT_OUT_ID = do.DOCUMENT_OUT_ID\n" +
            "INNER JOIN DOCUMENT_SERVICE_DETAIL AS dsd ON doh.DOCUMENT_OUT_HEADER_ID = dsd.DOCUMENT_OUT_HEADER_ID\n" +
            "INNER JOIN MODEL_LINK AS ml ON dsd.MODEL_LINK_ID = ml.MODEL_LINK_ID\n" +
            "INNER JOIN MODEL_DETAIL AS md ON ml.MODEL_DETAIL_ID = md.MODEL_DETAIL_ID\n" +
            "WHERE LOWER(md.VIN) = LOWER(:vinNumber)")
    Client findClientByVinNumber(@Param("vinNumber") String vinNumber);

    @Query(nativeQuery = true, value = "SELECT DISTINCT c.* FROM CLIENT AS c\n" +
            "INNER JOIN DOCUMENT_OUT AS do ON do.CLIENT_ID = c.CLIENT_ID\n" +
            "INNER JOIN ORGANIZATION AS o ON do.ORGANIZATION_ID = o.ORGANIZATION_ID\n" +
            "WHERE o.ORGANIZATION_ID = :organizationId")
    List<Client> findClientsByOrganizationId(@Param("organizationId") Integer organizationId);

    @Query(nativeQuery = true, value = "SELECT DISTINCT c.* FROM CLIENT AS c\n" +
            "INNER JOIN DOCUMENT_OUT AS do ON do.CLIENT_ID = c.CLIENT_ID\n" +
            "INNER JOIN DOCUMENT_OUT_HEADER AS doh ON do.DOCUMENT_OUT_ID = doh.DOCUMENT_OUT_ID\n" +
            "INNER JOIN ORGANIZATION AS o ON do.ORGANIZATION_ID = o.ORGANIZATION_ID\n" +
            "WHERE o.ORGANIZATION_ID = :organizationId AND doh.MANAGER_ID = :managerId")
    List<Client> findClientsByOrganizationIdAndManagerId(@Param("organizationId") Integer organizationId,
                                                         @Param("managerId") Integer managerId);

    @Query(nativeQuery = true, value = "SELECT c.* FROM CLIENT AS c " +
            "WHERE c.CLIENT_ID IN ( :clientIds ) " +
            "ORDER BY c.FULLNAME")
    List<Client> findClientsByIds(@Param("clientIds") List<Integer> clientIds);

}
