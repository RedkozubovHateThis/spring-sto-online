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

}
