package io.swagger.postgres.repository;


import io.swagger.postgres.model.security.User;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT DISTINCT user FROM User user " +
            "LEFT JOIN FETCH user.roles AS role " +
            "WHERE user.username = :username " +
            "AND user.enabled = true")
    User findByUsername(@Param("username") String username);

    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT u.id FROM user_ AS u " +
            "WHERE u.username = :username )")
    Boolean isUserExists(@Param("username") String username);
}
