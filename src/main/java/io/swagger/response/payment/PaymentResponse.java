package io.swagger.response.payment;

import io.swagger.postgres.model.enums.PaymentState;
import io.swagger.postgres.model.enums.PaymentType;
import io.swagger.postgres.model.payment.PaymentRecord;
import lombok.Data;

import java.util.Date;

@Data
public class PaymentResponse {

    private String orderId;
    private String orderNumber;
    private Integer amount;
    private PaymentType paymentType;
    private String errorMessage;
    private PaymentState paymentState;
    private Date createDate;
    private Date registerDate;

    public PaymentResponse(PaymentRecord paymentRecord) {
        this.orderId = paymentRecord.getOrderId();
        this.orderNumber = paymentRecord.getOrderNumber();
        this.amount = paymentRecord.getAmount();
        this.paymentType = paymentRecord.getPaymentType();
        this.errorMessage = paymentRecord.getErrorMessage();
        this.paymentState = paymentRecord.getPaymentState();
        this.createDate = paymentRecord.getCreateDate();
        this.registerDate = paymentRecord.getRegisterDate();
    }

}
