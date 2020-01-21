package io.swagger.postgres.repository;

import io.swagger.postgres.model.payment.Subscription;
import io.swagger.postgres.model.payment.SubscriptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SubscriptionTypeRepository extends JpaRepository<SubscriptionType, Long> {

    @Query("SELECT st FROM SubscriptionType AS st ORDER BY sortPosition")
    List<SubscriptionType> findAllAndOrderBySortPosition();

}
