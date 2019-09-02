package io.swagger.firebird.repository;

import io.swagger.firebird.model.DocumentOutHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface DocumentOutHeaderRepository extends JpaRepository<DocumentOutHeader, Integer> {

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE DOCUMENT_OUT_HEADER SET STATE = :state WHERE DOCUMENT_OUT_HEADER_ID = :documentOutHeaderId")
    void updateState(@Param("documentOutHeaderId") Integer documentOutHeaderId,
                     @Param("state") Integer state);

}
