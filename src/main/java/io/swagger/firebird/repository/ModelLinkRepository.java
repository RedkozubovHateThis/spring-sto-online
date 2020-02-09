package io.swagger.firebird.repository;

import io.swagger.firebird.model.ModelLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelLinkRepository extends JpaRepository<ModelLink, Integer> {

    @Query("SELECT DISTINCT ml FROM ModelLink AS ml " +
            "INNER JOIN ml.modelDetail AS md " +
            "WHERE md.vinNumber IN ( :vinNumbers )")
    List<ModelLink> findByVinNumbers(@Param("vinNumbers") List<String> vinNumbers);

    @Query("SELECT DISTINCT ml FROM ModelLink AS ml " +
            "INNER JOIN ml.modelDetail AS md " +
            "WHERE md.vinNumber IS NOT NULL AND UPPER(md.vinNumber) = UPPER(:vinNumber)")
    List<ModelLink> findByVinNumber(@Param("vinNumber") String vinNumber);

}
