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
    private Double amount;
    private PaymentType paymentType;
    private String errorMessage;
    private PaymentState paymentState;
    private Date createDate;
    private Date registerDate;
    private String actionCodeDescription;

    public PaymentResponse(PaymentRecord paymentRecord) {

        if ( paymentRecord.getPaymentState().equals( PaymentState.DEPOSITED ) &&
                paymentRecord.getDepositedAmount() != null )
            this.amount = paymentRecord.getDepositedAmount().doubleValue() / 100.0;
        else
            this.amount = paymentRecord.getAmount().doubleValue() / 100.0;

        this.orderId = paymentRecord.getOrderId();
        this.orderNumber = paymentRecord.getOrderNumber();
        this.paymentType = paymentRecord.getPaymentType();
        this.errorMessage = paymentRecord.getErrorMessage();
        this.paymentState = paymentRecord.getPaymentState();
        this.createDate = paymentRecord.getCreateDate();
        this.registerDate = paymentRecord.getRegisterDate();
        this.actionCodeDescription = paymentRecord.getActionCodeDescription();
    }

}
