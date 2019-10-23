package io.swagger.postgres.repository;

import io.swagger.postgres.model.EventMessage;
import io.swagger.postgres.model.enums.MessageType;
import io.swagger.postgres.model.security.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventMessageRepository extends JpaRepository<EventMessage, Long> {

    @Query("SELECT em FROM EventMessage AS em WHERE em.messageType = :messageType " +
            "ORDER BY em.messageDate DESC")
    List<EventMessage> findAllByMessageType(@Param("messageType") MessageType messageType);

    @Query("SELECT em FROM EventMessage AS em WHERE em.targetUser = :targetUser " +
            "ORDER BY em.messageDate DESC")
    List<EventMessage> findAllByTargetUser(@Param("targetUser") User targetUser);

}
