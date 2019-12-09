package io.swagger.postgres.repository;

import io.swagger.postgres.model.DocumentUserState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentUserStateRepository extends JpaRepository<DocumentUserState, Long> {

    @Query(nativeQuery = true, value = "SELECT dus.* FROM document_user_state AS dus " +
            "WHERE dus.user_id = :userId")
    DocumentUserState findByUserId(@Param("userId") Long userId);

    @Query(nativeQuery = true, value = "SELECT dus.* FROM document_user_state AS dus " +
            "WHERE dus.client_id = :clientId")
    DocumentUserState findByClientId(@Param("clientId") Integer clientId);

}
