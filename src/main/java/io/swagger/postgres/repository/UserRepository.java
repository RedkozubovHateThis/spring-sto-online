package io.swagger.postgres.repository;

import io.swagger.postgres.model.security.User;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Primary
@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    @Query("SELECT u FROM User AS u WHERE u.username = ?#{principal.username}")
    User findCurrentUser();

    @Query("SELECT DISTINCT u FROM User AS u " +
            "INNER JOIN u.roles AS ur " +
            "WHERE ur.name = :roleName")
    List<User> findUsersByRoleName(@Param("roleName") String roleName);

    @Query("SELECT DISTINCT u.id FROM User AS u " +
            "INNER JOIN u.roles AS ur " +
            "WHERE ur.name = :roleName")
    List<Long> collectUserIdsByRoleName(@Param("roleName") String roleName);

    @Query("SELECT DISTINCT u FROM User AS u " +
            "INNER JOIN u.roles AS ur " +
            "WHERE ur.name = :roleName " +
            "AND u.id <> :userId")
    List<User> findUsersByRoleNameExceptId(@Param("roleName") String roleName,
                                           @Param("userId") Long userId);

    @Query("SELECT DISTINCT user FROM User user " +
            "LEFT JOIN FETCH user.roles AS role " +
            "WHERE ( user.username = :username " +
            "OR user.phone = :username " +
            "OR user.email = :username ) " +
            "AND user.enabled = true")
    User findByUsername(@Param("username") String username);

    @Query("SELECT u FROM User AS u " +
            "WHERE u.id = :moderatorId " +
            "OR u.moderatorId = :moderatorId")
    Page<User> findAllByModeratorId(@Param("moderatorId") Long moderatorId, Pageable pageable);

    @Query("SELECT COUNT(u.id) FROM User AS u " +
            "WHERE u.id = :moderatorId " +
            "OR u.moderatorId = :moderatorId")
    Long countAllByModeratorId(@Param("moderatorId") Long moderatorId);

    @Query("SELECT COUNT(u.id) FROM User AS u " +
            "INNER JOIN u.roles AS r " +
            "WHERE ( r.name = 'CLIENT' " +
            "OR r.name = 'SERVICE_LEADER' ) " +
            "AND u.moderatorId = :moderatorId " +
            "AND u.isApproved = FALSE")
    Long countAllNotApprovedByModeratorId(@Param("moderatorId") Long moderatorId);

    @Query("SELECT COUNT(u.id) FROM User AS u ")
    Long countAll();

    @Query("SELECT COUNT(DISTINCT u.id) FROM User AS u " +
            "INNER JOIN u.roles AS r " +
            "WHERE ( r.name = 'CLIENT' " +
            "OR r.name = 'SERVICE_LEADER' ) " +
            "AND u.isApproved = FALSE")
    Long countAllNotApproved();

    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT u.id FROM users AS u " +
            "WHERE u.username = :username )")
    Boolean isUserExistsUsername(@Param("username") String username);

    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT u.id FROM users AS u " +
            "WHERE u.phone = :phone )")
    Boolean isUserExistsPhone(@Param("phone") String phone);

    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT u.id FROM users AS u " +
            "WHERE u.email = :email )")
    Boolean isUserExistsEmail(@Param("email") String email);

    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT u.id FROM users AS u " +
            "WHERE u.inn = :inn )")
    Boolean isUserExistsInn(@Param("inn") String inn);

    @Query(nativeQuery = true, value = "SELECT DISTINCT u.client_id FROM users AS u " +
            "WHERE u.moderator_id = :moderatorId AND u.client_id IS NOT NULL")
    List<Integer> collectClientIds(@Param("moderatorId") Long moderatorId);

    @Query(nativeQuery = true, value = "SELECT DISTINCT u.organization_id FROM users AS u " +
            "WHERE u.moderator_id = :moderatorId AND u.organization_id IS NOT NULL")
    List<Integer> collectOrganizationIds(@Param("moderatorId") Long moderatorId);

    @Query("SELECT u FROM User AS u WHERE u.username <> ?#{principal.username} ORDER BY u.lastName")
    List<User> findAllExceptSelf();

    @Query(nativeQuery = true, value = "SELECT DISTINCT u.* FROM users AS u " +
            "WHERE u.moderator_id = :moderatorId " +
            "UNION " +
            "SELECT DISTINCT cu.* FROM chat_message AS cm " +
            "INNER JOIN users AS cu ON cm.from_user_id = cu.id " +
            "WHERE cm.to_user_id = :userId " +
            "ORDER BY last_name")
    List<User> findAllByModerator(@Param("moderatorId") Long moderatorId,
                                 @Param("userId") Long userId);

    @Query(nativeQuery = true, value = "SELECT DISTINCT u.* FROM users AS u\n" +
            "WHERE u.id = :moderatorId\n" +
            "UNION\n" +
            "SELECT DISTINCT cu.* FROM chat_message AS cm\n" +
            "INNER JOIN users AS cu ON cm.from_user_id = cu.id\n" +
            "WHERE cm.to_user_id = :userId\n" +
            "UNION\n" +
            "SELECT DISTINCT cu.* FROM chat_message AS cm\n" +
            "INNER JOIN users AS cu ON cm.to_user_id = cu.id\n" +
            "WHERE cm.from_user_id = :userId\n" +
            "UNION\n" +
            "SELECT DISTINCT rm.* FROM users AS u\n" +
            "INNER JOIN users AS rm ON rm.id = u.replacement_moderator_id\n" +
            "WHERE u.id = :moderatorId\n" +
            "AND u.in_vacation = TRUE\n" +
            "ORDER BY last_name")
    List<User> findAllModerators(@Param("moderatorId") Long moderatorId,
                                 @Param("userId") Long userId);

    @Query(nativeQuery = true, value = "SELECT DISTINCT fu.* FROM event_message AS em " +
            "INNER JOIN users AS fu ON em.send_user_id = fu.id " +
            "WHERE em.message_type = 'DOCUMENT_CHANGE' " +
            "ORDER BY fu.last_name, fu.first_name, fu.middle_name")
    List<User> findEventMessageFromUsersByAdmin();

    @Query(nativeQuery = true, value = "SELECT DISTINCT fu.* FROM event_message AS em " +
            "INNER JOIN users AS fu ON em.send_user_id = fu.id " +
            "AND em.target_user_id = :targetUserId " +
            "ORDER BY fu.last_name, fu.first_name, fu.middle_name")
    List<User> findEventMessageFromUsers(@Param("targetUserId") Long targetUserId);

    @Query(nativeQuery = true, value = "SELECT DISTINCT tu.* FROM event_message AS em " +
            "INNER JOIN users AS tu ON em.target_user_id = tu.id " +
            "WHERE em.message_type = 'DOCUMENT_CHANGE' " +
            "ORDER BY tu.last_name, tu.first_name, tu.middle_name")
    List<User> findEventMessageToUsersByAdmin();

    @Query(nativeQuery = true, value = "SELECT DISTINCT tu.* FROM event_message AS em " +
            "INNER JOIN users AS tu ON em.target_user_id = tu.id " +
            "AND em.target_user_id = :targetUserId " +
            "ORDER BY tu.last_name, tu.first_name, tu.middle_name")
    List<User> findEventMessageToUsers(@Param("targetUserId") Long targetUserId);

    @Query(nativeQuery = true, value = "SELECT DISTINCT em.document_id FROM event_message AS em " +
            "WHERE em.document_id IS NOT NULL")
    List<Integer> collectDocumentIdsByAdmin();

    @Query(nativeQuery = true, value = "SELECT DISTINCT em.document_id FROM event_message AS em " +
            "WHERE em.document_id IS NOT NULL " +
            "AND em.target_user_id = :targetUserId")
    List<Integer> collectDocumentIds(@Param("targetUserId") Long targetUserId);
}
