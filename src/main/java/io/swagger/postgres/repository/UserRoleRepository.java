package io.swagger.postgres.repository;

import io.swagger.postgres.model.security.UserRole;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    UserRole findByName(@Param("name") String name);

}
