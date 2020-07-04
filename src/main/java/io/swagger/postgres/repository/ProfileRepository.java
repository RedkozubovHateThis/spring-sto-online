package io.swagger.postgres.repository;

import io.swagger.postgres.model.security.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    @Query("SELECT DISTINCT p FROM Profile AS p WHERE lower(p.phone) LIKE lower(:phone) OR lower(p.email) LIKE lower(:email) OR lower(p.name) LIKE lower(:fio)")
    List<Profile> findAllByPhoneOrEmail(@Param("phone") String phone,
                                        @Param("email") String email,
                                        @Param("fio") String fio);

    @Query(nativeQuery = true, value = "SELECT DISTINCT p.* FROM service_document AS sd\n" +
            "INNER JOIN profile AS p ON sd.client_id = p.id AND p.deleted = FALSE\n" +
            "WHERE sd.executor_id = :executorId AND sd.deleted = FALSE")
    List<Profile> findClientsByExecutorId(@Param("executorId") Long executorId);

    @Query(nativeQuery = true, value = "SELECT DISTINCT p.* FROM users AS u\n" +
            "INNER JOIN users_user_roles AS uur ON u.id = uur.user_id\n" +
            "INNER JOIN user_role AS ur ON uur.user_role_id = ur.id\n" +
            "INNER JOIN profile AS p ON u.profile_id = p.id\n" +
            "WHERE u.enabled = TRUE AND ur.name = 'SERVICE_LEADER'")
    List<Profile> findExecutors();

    @Query(nativeQuery = true, value = "SELECT DISTINCT p.* FROM service_document AS sd\n" +
            "INNER JOIN profile AS p ON sd.executor_id = p.id AND p.deleted = FALSE\n" +
            "WHERE sd.client_id = :clientId AND sd.deleted = FALSE")
    List<Profile> findExecutorsByClientId(@Param("clientId") Long clientId);
}
