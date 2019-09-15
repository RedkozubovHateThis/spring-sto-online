package io.swagger.postgres.repository;

import io.swagger.postgres.model.ChatMessage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends PagingAndSortingRepository<ChatMessage, Long> {

    @Query(nativeQuery = true, value = "SELECT cm.* from chat_message AS cm " +
            "WHERE ( cm.from_user_id = :fromUserId AND cm.to_user_id = :toUserId ) " +
            "OR ( cm.from_user_id = :toUserId AND cm.to_user_id = :fromUserId ) " +
            "ORDER BY cm.message_date")
    List<ChatMessage> findMessagesByUsers(@Param("fromUserId") Long fromUserId,
                                          @Param("toUserId") Long toUserId);

    @Query(nativeQuery = true, value = "SELECT cm.* from chat_message AS cm " +
            "WHERE ( cm.from_user_id = :fromUserId AND cm.to_user_id = :toUserId ) " +
            "OR ( cm.from_user_id = :toUserId AND cm.to_user_id = :fromUserId ) " +
            "ORDER BY cm.message_date DESC " +
            "LIMIT 1")
    ChatMessage findLastMessageByUsers(@Param("fromUserId") Long fromUserId,
                                       @Param("toUserId") Long toUserId);

}
