package io.swagger.firebird.repository;

import io.swagger.firebird.model.DocumentServiceDetail;
import io.swagger.response.report.ClientsNativeResponse;
import io.swagger.response.report.ExecutorsNativeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DocumentServiceDetailRepository extends PagingAndSortingRepository<DocumentServiceDetail, Integer>,
        JpaSpecificationExecutor<DocumentServiceDetail> {

    Page<DocumentServiceDetail> findAll(Pageable pageable);

    @Query("SELECT DISTINCT dsd FROM DocumentServiceDetail AS dsd " +
            "INNER JOIN dsd.documentOutHeader AS doh " +
            "INNER JOIN doh.documentOut AS do " +
            "INNER JOIN do.client AS c " +
            "WHERE c.id = :clientId " +
            "AND doh.state = :state")
    List<DocumentServiceDetail> findByClientIdAndState(@Param("clientId") Integer clientId,
                                                       @Param("state") Integer state);

    @Query("SELECT DISTINCT dsd FROM DocumentServiceDetail AS dsd " +
            "INNER JOIN dsd.documentOutHeader AS doh " +
            "INNER JOIN doh.documentOut AS do " +
            "INNER JOIN do.client AS c " +
            "WHERE dsd.id = :documentId AND c.id = :clientId")
    DocumentServiceDetail findOneByClientId(@Param("documentId") Integer documentId,
                                            @Param("clientId") Integer clientId);

    @Query("SELECT DISTINCT dsd FROM DocumentServiceDetail AS dsd " +
            "INNER JOIN dsd.documentOutHeader AS doh " +
            "INNER JOIN doh.documentOut AS do " +
            "INNER JOIN do.organization AS o " +
            "WHERE dsd.id = :documentId AND o.id = :organizationId")
    DocumentServiceDetail findOneByOrganizationId(@Param("documentId") Integer documentId,
                                                  @Param("organizationId") Integer organizationId);

    @Query("SELECT DISTINCT dsd FROM DocumentServiceDetail AS dsd " +
            "INNER JOIN dsd.documentOutHeader AS doh " +
            "INNER JOIN doh.documentOut AS do " +
            "INNER JOIN do.organization AS o " +
            "WHERE dsd.id = :documentId AND o.id IN ( :organizationIds )")
    DocumentServiceDetail findOneByOrganizationIds(@Param("documentId") Integer documentId,
                                                   @Param("organizationIds") List<Integer> organizationIds);

    @Query("SELECT DISTINCT dsd FROM DocumentServiceDetail AS dsd " +
            "INNER JOIN dsd.documentOutHeader AS doh " +
            "INNER JOIN doh.documentOut AS do " +
            "INNER JOIN do.client AS c " +
            "WHERE dsd.id = :documentId AND c.id IN ( :clientIds )")
    DocumentServiceDetail findOneByClientIds(@Param("documentId") Integer documentId,
                                             @Param("clientIds") List<Integer> clientIds);

    @Query("SELECT DISTINCT dsd FROM DocumentServiceDetail AS dsd " +
            "INNER JOIN dsd.documentOutHeader AS doh " +
            "INNER JOIN doh.documentOut AS do " +
            "INNER JOIN do.client AS c " +
            "INNER JOIN do.organization AS o " +
            "WHERE dsd.id = :documentId AND ( c.id IN ( :clientIds ) OR o.id IN ( :organizationIds ) )")
    DocumentServiceDetail findOneByClientIdsAndOrganizationIds(@Param("documentId") Integer documentId,
                                                               @Param("clientIds") List<Integer> clientIds,
                                                               @Param("organizationIds") List<Integer> organizationIds);

    @Query("SELECT COUNT(DISTINCT dsd.id) FROM DocumentServiceDetail AS dsd")
    Integer countAll();

    @Query("SELECT COUNT(DISTINCT dsd.id) FROM DocumentServiceDetail AS dsd " +
            "INNER JOIN dsd.documentOutHeader AS doh " +
            "WHERE doh.state = :state")
    Integer countByState(@Param("state") Integer state);

    @Query("SELECT COUNT(DISTINCT dsd.id) FROM DocumentServiceDetail AS dsd " +
            "INNER JOIN dsd.documentOutHeader AS doh " +
            "INNER JOIN doh.documentOut AS do " +
            "INNER JOIN do.client AS c " +
            "WHERE c.id = :clientId")
    Integer countByClientId(@Param("clientId") Integer clientId);

    @Query("SELECT DISTINCT dsd.id FROM DocumentServiceDetail AS dsd " +
            "INNER JOIN dsd.documentOutHeader AS doh " +
            "INNER JOIN doh.documentOut AS do " +
            "INNER JOIN do.client AS c " +
            "WHERE c.id = :clientId")
    Integer[] collectIdsByClientId(@Param("clientId") Integer clientId);

    @Query("SELECT DISTINCT dsd.id FROM DocumentServiceDetail AS dsd " +
            "INNER JOIN dsd.documentOutHeader AS doh " +
            "INNER JOIN doh.documentOut AS do " +
            "INNER JOIN do.client AS c " +
            "WHERE c.id = :clientId " +
            "AND doh.state = :state")
    Integer[] collectIdsByClientIdAndState(@Param("clientId") Integer clientId,
                                           @Param("state") Integer state);

    @Query("SELECT COUNT(DISTINCT dsd.id) FROM DocumentServiceDetail AS dsd " +
            "INNER JOIN dsd.documentOutHeader AS doh " +
            "INNER JOIN doh.documentOut AS do " +
            "INNER JOIN do.client AS c " +
            "WHERE c.id = :clientId " +
            "AND doh.state = :state")
    Integer countByClientIdAndState(@Param("clientId") Integer clientId,
                                    @Param("state") Integer state);

    @Query("SELECT COUNT(DISTINCT dsd.id) FROM DocumentServiceDetail AS dsd " +
            "INNER JOIN dsd.documentOutHeader AS doh " +
            "INNER JOIN doh.documentOut AS do " +
            "INNER JOIN do.client AS c " +
            "WHERE c.id IN ( :clientIds )")
    Integer countByClientIds(@Param("clientIds") List<Integer> clientIds);

    @Query("SELECT COUNT(DISTINCT dsd.id) FROM DocumentServiceDetail AS dsd " +
            "INNER JOIN dsd.documentOutHeader AS doh " +
            "INNER JOIN doh.documentOut AS do " +
            "INNER JOIN do.client AS c " +
            "WHERE c.id IN ( :clientIds ) " +
            "AND doh.state = :state")
    Integer countByClientIdsAndState(@Param("clientIds") List<Integer> clientIds,
                                     @Param("state") Integer state);

    @Query("SELECT COUNT(DISTINCT dsd.id) FROM DocumentServiceDetail AS dsd " +
            "INNER JOIN dsd.documentOutHeader AS doh " +
            "INNER JOIN doh.documentOut AS do " +
            "INNER JOIN do.organization AS o " +
            "WHERE o.id = :organizationId")
    Integer countByOrganizationId(@Param("organizationId") Integer organizationId);

    @Query("SELECT COUNT(DISTINCT dsd.id) FROM DocumentServiceDetail AS dsd " +
            "INNER JOIN dsd.documentOutHeader AS doh " +
            "INNER JOIN doh.documentOut AS do " +
            "INNER JOIN do.organization AS o " +
            "WHERE o.id = :organizationId " +
            "AND doh.state = :state")
    Integer countByOrganizationIdAndState(@Param("organizationId") Integer organizationId,
                                           @Param("state") Integer state);

    List<ExecutorsNativeResponse> findExecutors(@Param("organizationId") Integer organizationId,
                                                @Param("startDate") Date startDate,
                                                @Param("endDate") Date endDate);

    List<ClientsNativeResponse> findClients(@Param("organizationId") Integer organizationId,
                                            @Param("startDate") Date startDate,
                                            @Param("endDate") Date endDate);

    @Query("SELECT dsd FROM DocumentServiceDetail AS dsd " +
            "WHERE dsd.id IN ( :documentIds )")
    List<DocumentServiceDetail> findByIds(@Param("documentIds") List<Integer> documentIds);

    @Query(nativeQuery = true, value = "SELECT COUNT(DISTINCT dsd.DOCUMENT_SERVICE_DETAIL_ID) FROM DOCUMENT_SERVICE_DETAIL AS dsd\n" +
            "INNER JOIN DOCUMENT_OUT_HEADER AS doh ON doh.DOCUMENT_OUT_HEADER_ID = dsd.DOCUMENT_OUT_HEADER_ID\n" +
            "INNER JOIN DOCUMENT_OUT AS do ON do.DOCUMENT_OUT_ID = doh.DOCUMENT_OUT_HEADER_ID\n" +
            "INNER JOIN ORGANIZATION AS o ON do.ORGANIZATION_ID = o.ORGANIZATION_ID\n" +
            "WHERE CAST(dsd.DATE_START AS DATE) BETWEEN '01.09.2019' AND '01.10.2020'\n" +
            "AND o.ORGANIZATION_ID = :organizationId")
    Integer countDocumentsByOrganizationIdAndDates(@Param("organizationId") Integer organizationId);

}
