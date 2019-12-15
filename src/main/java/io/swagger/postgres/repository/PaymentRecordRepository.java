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

}
