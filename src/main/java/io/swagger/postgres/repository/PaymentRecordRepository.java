package io.swagger.postgres.repository;

import io.swagger.postgres.model.payment.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {

    PaymentRecord findByOrderNumber(@Param("orderNumber") String orderNumber);
    PaymentRecord findByOrderId(@Param("orderId") String orderId);

}
