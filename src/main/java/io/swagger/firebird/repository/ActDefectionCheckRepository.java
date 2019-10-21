package io.swagger.firebird.repository;

import io.swagger.firebird.model.ActDefectionCheck;
import io.swagger.firebird.model.CarBodyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActDefectionCheckRepository extends JpaRepository<ActDefectionCheck, Integer> {

    @Query("SELECT adc FROM ActDefectionCheck AS adc WHERE adc.parent IS NULL " +
            "ORDER BY adc.nodePosition")
    List<ActDefectionCheck> findParents();

    @Query("SELECT adc FROM ActDefectionCheck AS adc WHERE adc.parent = :parentId " +
            "ORDER BY adc.nodePosition")
    List<ActDefectionCheck> findChildren(@Param("parentId") Integer parentId);

}
