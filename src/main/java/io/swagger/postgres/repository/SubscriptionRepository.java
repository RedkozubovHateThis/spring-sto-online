package io.swagger.postgres.repository;

import io.swagger.postgres.model.payment.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT DISTINCT s.id FROM subscription AS s " +
            "WHERE s.type = 'FREE' AND s.user_id = :userId )")
    Boolean isFreeIsFormed(@Param("userId") Long userId);

    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT DISTINCT s.id FROM subscription AS s " +
            "WHERE s.user_id = :userId )")
    Boolean isAnyIsFormed(@Param("userId") Long userId);

    @Query(nativeQuery = true, value = "SELECT s.* FROM subscription AS s " +
            "WHERE s.user_id = :userId " +
            "ORDER BY s.start_date")
    List<Subscription> findAllByUserId(@Param("userId") Long userId);

    @Query(nativeQuery = true, value = "SELECT s.* FROM subscription AS s " +
            "WHERE s.user_id = :userId " +
            "AND ( s.start_date >= :startDate OR :startDate BETWEEN s.start_date AND s.end_date ) " +
            "ORDER BY s.start_date")
    List<Subscription> findAllByUserIdAndStartDate(@Param("userId") Long userId,
                                                   @Param("startDate") Date startDate);

    @Query(nativeQuery = true, value = "SELECT s.* FROM subscription AS s " +
            "WHERE s.user_id = :userId " +
            "AND ( s.end_date <= :endDate OR :endDate BETWEEN s.start_date AND s.end_date ) " +
            "ORDER BY s.start_date")
    List<Subscription> findAllByUserIdAndEndDate(@Param("userId") Long userId,
                                                 @Param("endDate") Date endDate);

    @Query(nativeQuery = true, value = "SELECT s.* FROM subscription AS s " +
            "WHERE s.user_id = :userId " +
            "AND ( " +
            "( s.start_date >= :startDate OR :startDate BETWEEN s.start_date AND s.end_date ) " +
            "OR " +
            "( s.end_date <= :endDate OR :endDate BETWEEN s.start_date AND s.end_date ) " +
            ") " +
            "ORDER BY s.start_date")
    List<Subscription> findAllByUserIdAndBetweenDates(@Param("userId") Long userId,
                                                      @Param("startDate") Date startDate,
                                                      @Param("endDate") Date endDate);

    @Query(nativeQuery = true, value = "SELECT min(s.start_date) FROM subscription AS s " +
            "WHERE s.user_id = :userId")
    Date findFirstSubscriptionDateByUserId(@Param("userId") Long userId);

    @Query(nativeQuery = true, value = "SELECT s.* FROM subscription AS s WHERE end_date < now()")
    List<Subscription> findExpiredSubscriptions();
}
