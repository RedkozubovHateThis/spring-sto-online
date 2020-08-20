package io.swagger.postgres.repository;

import io.swagger.postgres.model.security.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long>, JpaSpecificationExecutor<Profile> {

    @Query("SELECT DISTINCT p FROM Profile AS p " +
            "WHERE lower(p.phone) LIKE lower(:phone) " +
            "OR lower(p.email) LIKE lower(:email) " +
            "OR lower(p.name) LIKE lower(:fio) " +
            "OR lower(p.inn) LIKE lower(:inn)")
    List<Profile> findAllByPhoneOrEmail(@Param("phone") String phone,
                                        @Param("email") String email,
                                        @Param("fio") String fio,
                                        @Param("inn") String inn);

    @Query(nativeQuery = true, value = "SELECT DISTINCT p.* FROM service_document AS sd\n" +
            "INNER JOIN profile AS p ON sd.client_id = p.id AND p.deleted = FALSE\n" +
            "WHERE sd.executor_id = :executorId AND sd.deleted = FALSE")
    List<Profile> findClientsByExecutorId(@Param("executorId") Long executorId);

    @Query(nativeQuery = true, value = "SELECT DISTINCT p.* FROM users AS u\n" +
            "INNER JOIN users_user_roles AS uur ON u.id = uur.user_id\n" +
            "INNER JOIN user_role AS ur ON uur.user_role_id = ur.id\n" +
            "INNER JOIN profile AS p ON u.profile_id = p.id AND p.deleted = FALSE\n" +
            "WHERE u.enabled = TRUE AND ur.name = 'SERVICE_LEADER'")
    List<Profile> findExecutors();

    @Query(nativeQuery = true, value = "SELECT DISTINCT p.* FROM service_document AS sd\n" +
            "INNER JOIN profile AS p ON sd.executor_id = p.id AND p.deleted = FALSE\n" +
            "WHERE sd.client_id = :clientId AND sd.deleted = FALSE")
    List<Profile> findExecutorsByClientId(@Param("clientId") Long clientId);

    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT p.id FROM profile AS p " +
            "WHERE p.phone = :phone AND p.deleted = FALSE )")
    Boolean isProfileExistsPhone(@Param("phone") String phone);

    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT p.id FROM profile AS p " +
            "WHERE p.email = :email AND p.deleted = FALSE )")
    Boolean isProfileExistsEmail(@Param("email") String email);

    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT p.id FROM profile AS p " +
            "WHERE p.inn = :inn AND p.deleted = FALSE )")
    Boolean isProfileExistsInn(@Param("inn") String inn);

    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT p.id FROM profile AS p " +
            "WHERE p.inn = :inn AND p.id <> :userId AND p.deleted = FALSE )")
    Boolean isProfileExistsInnNotSelf(@Param("inn") String inn,
                                      @Param("userId") Long userId);

    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT p.id FROM profile AS p " +
            "WHERE p.phone = :phone AND p.id <> :userId AND p.deleted = FALSE )")
    Boolean isProfileExistsPhoneNotSelf(@Param("phone") String phone,
                                        @Param("userId") Long userId);

    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT p.id FROM profile AS p " +
            "WHERE p.email = :email AND p.id <> :userId AND p.deleted = FALSE )")
    Boolean isProfileExistsEmailNotSelf(@Param("email") String email,
                                        @Param("userId") Long userId);

    @Query(nativeQuery = true, value = "SELECT DISTINCT p.* FROM users AS u " +
            "INNER JOIN users_user_roles AS uur ON u.id = uur.user_id " +
            "INNER JOIN user_role AS ur ON uur.user_role_id = ur.id " +
            "INNER JOIN profile AS p on u.profile_id = p.id AND p.deleted = FALSE " +
            "WHERE ur.name = 'CLIENT' " +
            "ORDER BY p.name")
    List<Profile> findClients();

    @Query(nativeQuery = true, value = "SELECT DISTINCT p.* FROM users AS u " +
            "INNER JOIN users_user_roles AS uur ON u.id = uur.user_id " +
            "INNER JOIN user_role AS ur ON uur.user_role_id = ur.id " +
            "INNER JOIN profile AS p on u.profile_id = p.id AND p.deleted = FALSE " +
            "WHERE ur.name = 'CLIENT' " +
            "AND p.created_by_id = :createById " +
            "ORDER BY p.name")
    List<Profile> findClientsByCreatedBy(@Param("createById") Long createdById);
}
