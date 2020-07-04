package io.swagger.postgres.repository;

import io.swagger.postgres.model.ServiceWorkDictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceWorkDictionaryRepository extends JpaRepository<ServiceWorkDictionary, Long> {

    @Query("SELECT DISTINCT swd FROM ServiceWorkDictionary AS swd WHERE lower(swd.name) LIKE lower(:name)")
    List<ServiceWorkDictionary> findAllByName(@Param("name") String name);

}
