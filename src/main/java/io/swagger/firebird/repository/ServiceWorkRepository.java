package io.swagger.firebird.repository;

import io.swagger.firebird.model.ServiceWork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface ServiceWorkRepository extends JpaRepository<ServiceWork, Integer> {

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE SERVICE_WORK SET PRICE_NORM = :priceNorm WHERE SERVICE_WORK_ID = :serviceWorkId")
    void updateServiceWorkByPriceNorm(@Param("serviceWorkId") Integer serviceWorkId,
                                      @Param("priceNorm") Double priceNorm);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE SERVICE_WORK SET PRICE = :price WHERE SERVICE_WORK_ID = :serviceWorkId")
    void updateServiceWorkByPrice(@Param("serviceWorkId") Integer serviceWorkId,
                                  @Param("price") Double price);

}
