package io.swagger.postgres.repository;

import io.swagger.postgres.model.CompiledReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompiledReportRepository extends JpaRepository<CompiledReport, Long> {
}
