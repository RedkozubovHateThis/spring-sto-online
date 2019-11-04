package io.swagger.postgres.repository;

import io.swagger.postgres.model.DocumentUserState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentUserStateRepository extends JpaRepository<DocumentUserState, Long> {

    DocumentUserState findByUserId(Long userId);
    DocumentUserState findByClientId(Integer clientId);

}
