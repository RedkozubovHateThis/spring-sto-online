package io.swagger.firebird.repository;

import io.swagger.firebird.model.ServiceGoodsAddon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface ServiceGoodsAddonRepository extends JpaRepository<ServiceGoodsAddon, Integer> {

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE SERVICE_GOODS_ADDON SET COST = :cost WHERE SERVICE_GOODS_ADDON_ID = :serviceGoodsAddonId")
    void updateCost(@Param("serviceGoodsAddonId") Integer serviceGoodsAddonId,
                    @Param("cost") Double cost);

}
