package io.swagger.postgres.repository;

import io.swagger.postgres.model.payment.SubscriptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionTypeRepository extends JpaRepository<SubscriptionType, Long> {

    @Query("SELECT st FROM SubscriptionType AS st ORDER BY st.sortPosition")
    List<SubscriptionType> findAllAndOrderBySortPosition();

    @Query("SELECT st FROM SubscriptionType AS st WHERE st.isFree = FALSE ORDER BY st.sortPosition")
    List<SubscriptionType> findNotFreeAndOrderBySortPosition();

    @Query(nativeQuery = true, value = "SELECT st.* FROM subscription_type AS st WHERE st.is_free = TRUE LIMIT 1")
    SubscriptionType findFreeSubscription();

}
