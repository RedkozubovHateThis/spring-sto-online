package io.swagger.postgres.repository;

import io.swagger.postgres.model.security.User;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    @Query("SELECT u FROM User AS u WHERE u.username = ?#{principal.username}")
    User findCurrentUser();

    @Query("SELECT DISTINCT user FROM User user " +
            "LEFT JOIN FETCH user.roles AS role " +
            "WHERE ( user.username = :username " +
            "OR user.phone = :username " +
            "OR user.email = :username ) " +
            "AND user.enabled = true")
    User findByUsername(@Param("username") String username);

    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT u.id FROM users AS u " +
            "WHERE u.phone = :phone )")
    Boolean isUserExistsPhone(@Param("phone") String phone);

    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT u.id FROM users AS u " +
            "WHERE u.email = :email )")
    Boolean isUserExistsEmail(@Param("email") String email);
}
