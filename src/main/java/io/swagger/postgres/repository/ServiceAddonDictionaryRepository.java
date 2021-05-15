package io.swagger.postgres.repository;

import io.swagger.postgres.model.ServiceAddonDictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceAddonDictionaryRepository extends JpaRepository<ServiceAddonDictionary, Long>, JpaSpecificationExecutor<ServiceAddonDictionary> {

    @Query("SELECT DISTINCT sad FROM ServiceAddonDictionary AS sad WHERE lower(sad.name) LIKE lower(:name)")
    List<ServiceAddonDictionary> findAllByName(@Param("name") String name);

    @Query(nativeQuery = true, value = "SELECT EXISTS(SELECT sad.id FROM service_addon_dictionary AS sad WHERE sad.name = :name AND sad.deleted = FALSE)")
    Boolean isExistsByName(@Param("name") String name);

}
