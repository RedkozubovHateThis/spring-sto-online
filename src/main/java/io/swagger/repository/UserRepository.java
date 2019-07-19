package io.swagger.repository;


import io.swagger.model.security.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT DISTINCT user FROM User user " +
            "INNER JOIN FETCH user.roles AS role " +
            "INNER JOIN FETCH role.permissions AS permission " +
            "WHERE user.username = :username")
    User findByUsername(@Param("username") String username);
}
