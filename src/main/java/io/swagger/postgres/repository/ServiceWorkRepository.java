package io.swagger.postgres.repository;

import io.swagger.postgres.model.ServiceWork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceWorkRepository extends JpaRepository<ServiceWork, Long> {
}
