package io.swagger.postgres.repository;

import io.swagger.postgres.model.payment.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {

    PaymentRecord findByOrderNumber(@Param("orderNumber") String orderNumber);
    PaymentRecord findByOrderId(@Param("orderId") String orderId);
    @Query(nativeQuery = true, value = "SELECT pr.* FROM payment_record AS pr " +
            "WHERE pr.user_id = :userId " +
            "AND pr.create_date BETWEEN :fromDate AND :toDate " +
            "ORDER BY pr.create_date DESC")
    List<PaymentRecord> findAllByUserId(@Param("userId") Long userId,
                                        @Param("fromDate") Date fromDate,
                                        @Param("toDate") Date toDate);

    @Query(nativeQuery = true, value = "SELECT COUNT( DISTINCT pr.id ) FROM payment_record AS pr WHERE " +
            "pr.user_id = :userId AND pr.payment_type = :paymentType")
    Long countByUserIdAndPaymentType(@Param("userId") Long userId,
                                     @Param("paymentType") String paymentType);

    @Query(nativeQuery = true, value = "SELECT pr.* FROM payment_record AS pr " +
            "WHERE pr.expiration_date IS NOT NULL " +
            "AND pr.expiration_date <= now() " +
            "AND pr.is_expired IS NOT NULL " +
            "AND pr.is_expired = FALSE")
    List<PaymentRecord> findAllExpiringPromisedRecords();

    @Query(nativeQuery = true, value = "SELECT pr.* FROM payment_record AS pr " +
            "WHERE pr.payment_type = 'PROMISED' " +
            "AND pr.user_id = :userId " +
            "ORDER BY pr.create_date DESC " +
            "LIMIT 1")
    PaymentRecord findLastPromisedRecordByUserId(@Param("userId") Long userId);
}
