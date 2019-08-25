package io.swagger.firebird.repository;

import io.swagger.firebird.model.DocumentServiceDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentServiceDetailRepository extends PagingAndSortingRepository<DocumentServiceDetail, Integer> {

    Page<DocumentServiceDetail> findAll(Pageable pageable);
    @Query("SELECT DISTINCT dsd FROM DocumentServiceDetail AS dsd " +
            "INNER JOIN dsd.documentOutHeader AS doh " +
            "INNER JOIN doh.documentOut AS do " +
            "INNER JOIN do.client AS c " +
            "WHERE c.id = :clientId")
    Page<DocumentServiceDetail> findByClientId(@Param("clientId") Integer clientId, Pageable pageable);

}
