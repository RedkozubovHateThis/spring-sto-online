package io.swagger.postgres.repository;

import io.swagger.postgres.model.ServiceAddonDictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceAddonDictionaryRepository extends JpaRepository<ServiceAddonDictionary, Long> {

    @Query("SELECT DISTINCT sad FROM ServiceAddonDictionary AS sad WHERE lower(sad.name) LIKE lower(:name)")
    List<ServiceAddonDictionary> findAllByName(@Param("name") String name);

}
