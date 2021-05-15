package io.swagger.response.payment.request.embed;

import io.swagger.postgres.model.enums.PaymentState;
import lombok.Data;

@Data
public class PaymentAmountInfo {
    private Integer approvedAmount;
    private Integer depositedAmount;
    private Integer refundedAmount;
    private PaymentState paymentState;
    private Integer feeAmount;
}
