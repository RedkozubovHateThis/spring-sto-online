package io.swagger.postgres.repository;

import io.swagger.postgres.model.payment.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT DISTINCT s.id FROM subscription AS s " +
            "WHERE s.type = 'FREE' AND s.user_id = :userId )")
    Boolean isFreeIsFormed(@Param("userId") Long userId);

}
