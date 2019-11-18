package io.swagger.firebird.repository;

import io.swagger.firebird.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("firebirdUserRepository")
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u FROM User AS u WHERE u.hidden = 0")
    List<User> findAllVisibleUsers();

}
