package io.swagger.firebird.repository;

import io.swagger.firebird.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Integer> {

    @Query(nativeQuery = true, value = "SELECT FIRST 1 DISTINCT o.* FROM ORGANIZATION AS o\n" +
            "WHERE LOWER(o.INN) = LOWER(:inn)")
    Organization findOrganizationByInn(@Param("inn") String inn);

    @Query(nativeQuery = true, value = "SELECT FIRST 1 DISTINCT o.* FROM ORGANIZATION AS o\n" +
            "INNER JOIN DOCUMENT_OUT AS do ON do.ORGANIZATION_ID = o.ORGANIZATION_ID\n" +
            "INNER JOIN DOCUMENT_OUT_HEADER AS doh ON doh.DOCUMENT_OUT_ID = do.DOCUMENT_OUT_ID\n" +
            "INNER JOIN DOCUMENT_SERVICE_DETAIL AS dsd ON dsd.DOCUMENT_OUT_HEADER_ID = doh.DOCUMENT_OUT_HEADER_ID\n" +
            "WHERE dsd.DOCUMENT_SERVICE_DETAIL_ID = :documentId")
    Organization findOrganizationByDocumentId(@Param("documentId") Integer documentId);

    @Query(nativeQuery = true, value = "SELECT COUNT(DISTINCT o.ORGANIZATION_ID) FROM ORGANIZATION AS o\n" +
            "INNER JOIN DOCUMENT_OUT AS do ON do.ORGANIZATION_ID = o.ORGANIZATION_ID\n" +
            "INNER JOIN CLIENT AS c ON c.CLIENT_ID = do.CLIENT_ID\n" +
            "WHERE c.CLIENT_ID = :clientId")
    Integer countServicesByClientId(@Param("clientId") Integer clientId);

}
