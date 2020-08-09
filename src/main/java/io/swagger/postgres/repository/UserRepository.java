package io.swagger.postgres.repository;

import io.swagger.postgres.model.security.User;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Primary
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @Query("SELECT u FROM User AS u WHERE u.username = ?#{principal.username}")
    User findCurrentUser();

    @Query("SELECT DISTINCT u FROM User AS u " +
            "INNER JOIN u.roles AS ur " +
            "WHERE ur.name = :roleName")
    List<User> findUsersByRoleName(@Param("roleName") String roleName);

    @Query("SELECT DISTINCT u FROM User AS u " +
            "INNER JOIN u.roles AS ur " +
            "WHERE ur.name IN ( :roleNames )")
    List<User> findUsersByRoleNames(@Param("roleNames") List<String> roleNames);

    @Query(nativeQuery = true, value = "SELECT u.* FROM users AS u " +
            "WHERE u.enabled = TRUE AND u.organization_id = :organizationId " +
            "LIMIT 1")
    User findUserByOrganizationId(@Param("organizationId") Integer organizationId);

    @Query("SELECT COUNT(DISTINCT u.id) FROM User AS u " +
            "INNER JOIN u.roles AS ur " +
            "WHERE ur.name = :roleName")
    Long countUsersByRoleName(@Param("roleName") String roleName);

    @Query("SELECT COUNT(DISTINCT u.id) FROM User AS u " +
            "INNER JOIN u.roles AS ur " +
            "WHERE ur.name IN ( :roleNames )")
    Long countUsersByRoleNames(@Param("roleNames") List<String> roleNames);

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
            "INNER JOIN user.profile AS p " +
            "WHERE ( user.username = :username " +
            "OR p.phone = :username " +
            "OR p.email = :username ) " +
            "AND user.enabled = true")
    User findByUsername(@Param("username") String username);

    @Query("SELECT DISTINCT user FROM User user " +
            "LEFT JOIN FETCH user.roles AS role " +
            "INNER JOIN user.profile AS p " +
            "WHERE p.phone = :phone " +
            "AND user.enabled = true")
    User findByPhone(@Param("phone") String phone);

    @Query("SELECT DISTINCT user FROM User user " +
            "LEFT JOIN FETCH user.roles AS role " +
            "INNER JOIN user.profile AS p " +
            "WHERE p.email = :email " +
            "AND user.enabled = true")
    User findByEmail(@Param("email") String email);

    @Query("SELECT COUNT(u.id) FROM User AS u ")
    Long countAll();

    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT u.id FROM users AS u " +
            "WHERE u.username = :username )")
    Boolean isUserExistsUsername(@Param("username") String username);

//    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT u.id FROM users AS u " +
//            "WHERE u.phone = :phone )")
//    Boolean isUserExistsPhone(@Param("phone") String phone);

//    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT u.id FROM users AS u " +
//            "WHERE u.email = :email )")
//    Boolean isUserExistsEmail(@Param("email") String email);

//    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT u.id FROM users AS u " +
//            "WHERE u.inn = :inn )")
//    Boolean isUserExistsInn(@Param("inn") String inn);

//    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT u.id FROM users AS u " +
//            "WHERE u.inn = :inn AND u.id <> :userId )")
//    Boolean isUserExistsInnNotSelf(@Param("inn") String inn,
//                                   @Param("userId") Long userId);

//    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT u.id FROM users AS u " +
//            "WHERE u.phone = :phone AND u.id <> :userId )")
//    Boolean isUserExistsPhoneNotSelf(@Param("phone") String phone,
//                              @Param("userId") Long userId);

//    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT u.id FROM users AS u " +
//            "WHERE u.email = :email AND u.id <> :userId )")
//    Boolean isUserExistsEmailNotSelf(@Param("email") String email,
//                              @Param("userId") Long userId);

    @Query("SELECT u FROM User AS u WHERE u.username <> ?#{principal.username} ORDER BY u.lastName")
    List<User> findAllExceptSelf();

    @Query(nativeQuery = true, value = "SELECT DISTINCT fu.* FROM event_message AS em " +
            "INNER JOIN users AS fu ON em.send_user_id = fu.id AND fu.enabled = TRUE " +
            "WHERE em.message_type = 'DOCUMENT_CHANGE' " +
            "ORDER BY fu.last_name, fu.first_name, fu.middle_name")
    List<User> findEventMessageFromUsersByAdmin();

    @Query(nativeQuery = true, value = "SELECT DISTINCT fu.* FROM event_message AS em " +
            "INNER JOIN users AS fu ON em.send_user_id = fu.id AND fu.enabled = TRUE " +
            "AND em.target_user_id = :targetUserId " +
            "ORDER BY fu.last_name, fu.first_name, fu.middle_name")
    List<User> findEventMessageFromUsers(@Param("targetUserId") Long targetUserId);

    @Query(nativeQuery = true, value = "SELECT DISTINCT tu.* FROM event_message AS em " +
            "INNER JOIN users AS tu ON em.target_user_id = tu.id AND tu.enabled = TRUE " +
            "WHERE em.message_type = 'DOCUMENT_CHANGE' " +
            "ORDER BY tu.last_name, tu.first_name, tu.middle_name")
    List<User> findEventMessageToUsersByAdmin();

    @Query(nativeQuery = true, value = "SELECT DISTINCT tu.* FROM event_message AS em " +
            "INNER JOIN users AS tu ON em.target_user_id = tu.id AND tu.enabled = TRUE " +
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

    @Query(nativeQuery = true, value = "SELECT SUM(u.balance) FROM users AS u " +
            "INNER JOIN users_user_roles AS uur ON u.id = uur.user_id " +
            "INNER JOIN user_role AS ur ON uur.user_role_id = ur.id " +
            "WHERE u.enabled = TRUE " +
            "AND ur.name = 'SERVICE_LEADER'")
    Double countTotalBalance();

    @Query(nativeQuery = true, value = "SELECT u.balance FROM users AS u " +
            "INNER JOIN profile AS p ON u.profile_id = p.id AND p.deleted = FALSE " +
            "WHERE u.enabled = TRUE " +
            "AND p.id = :profileId")
    Double getBalanceByProfileId(@Param("profileId") Long profileId);
}
