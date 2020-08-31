package io.swagger.postgres.repository;

import io.swagger.postgres.model.AdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface AdEntityRepository extends JpaRepository<AdEntity, Long>, JpaSpecificationExecutor<AdEntity> {

    @Query(nativeQuery = true, value =
            "SELECT ae FROM ad_entity AS ae\n" +
            "WHERE ae.current = TRUE\n" +
            "AND ae.deleted = FALSE\n" +
            "AND ae.active = TRUE\n" +
            "LIMIT 1")
    AdEntity findCurrentAdEntity();

    @Query(nativeQuery = true, value =
            "WITH min_date AS (\n" +
            "    SELECT min(ae.create_date) AS first_date FROM ad_entity AS ae\n" +
            ")\n" +
            "SELECT ae.* FROM ad_entity AS ae\n" +
            "WHERE ae.create_date = (SELECT first_date FROM min_date)\n" +
            "AND ae.deleted = FALSE\n" +
            "AND ae.active = TRUE\n" +
            "LIMIT 1")
    AdEntity findFirstAdEntity();

    @Query(nativeQuery = true, value =
            "SELECT ae.* FROM ad_entity AS ae\n" +
            "WHERE ae.create_date > :date\n" +
            "AND ae.deleted = FALSE\n" +
            "AND ae.active = TRUE\n" +
            "ORDER BY ae.create_date\n" +
            "LIMIT 1")
    AdEntity findNextAdEntity(@Param("date") Date date);

}
