package io.swagger.postgres.repository;

import io.swagger.postgres.model.security.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    @Query("SELECT DISTINCT p FROM Profile AS p WHERE lower(p.phone) LIKE lower(:phone) OR lower(p.email) LIKE lower(:email)")
    List<Profile> findAllByPhoneOrEmail(@Param("phone") String phone,
                                        @Param("email") String email);
}
