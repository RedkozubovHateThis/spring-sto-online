package io.swagger.firebird.repository;

import io.swagger.firebird.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("firebirdUserRepository")
public interface UserRepository extends JpaRepository<User, Integer> {
}
