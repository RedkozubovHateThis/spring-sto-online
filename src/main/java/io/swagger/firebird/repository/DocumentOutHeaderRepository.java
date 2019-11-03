package io.swagger.firebird.repository;

import io.swagger.firebird.model.DocumentOutHeader;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DocumentOutHeaderRepository extends JpaRepository<DocumentOutHeader, Integer> {

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE DOCUMENT_OUT_HEADER SET STATE = :state WHERE DOCUMENT_OUT_HEADER_ID = :documentOutHeaderId")
    void updateState(@Param("documentOutHeaderId") Integer documentOutHeaderId,
                     @Param("state") Integer state);

}
