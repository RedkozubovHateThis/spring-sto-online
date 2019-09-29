package io.swagger.firebird.repository;

import io.swagger.firebird.model.Contact;
import io.swagger.firebird.model.Executor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExecutorRepository extends JpaRepository<Executor, Integer> {
}
