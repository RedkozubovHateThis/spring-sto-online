package io.swagger.postgres.repository;

import io.swagger.postgres.model.EventMessage;
import io.swagger.postgres.model.enums.MessageType;
import io.swagger.postgres.model.security.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventMessageRepository extends PagingAndSortingRepository<EventMessage, Long>, JpaSpecificationExecutor<EventMessage> {

    @Query("SELECT em FROM EventMessage AS em WHERE em.messageType = :messageType")
    Page<EventMessage> findAllByMessageType(@Param("messageType") MessageType messageType, Pageable pageable);

    @Query("SELECT em FROM EventMessage AS em WHERE em.targetUser = :targetUser")
    Page<EventMessage> findAllByTargetUser(@Param("targetUser") User targetUser, Pageable pageable);

}
