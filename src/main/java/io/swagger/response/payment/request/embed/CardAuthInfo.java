package io.swagger.response.payment.request.embed;

import lombok.Data;

@Data
public class CardAuthInfo {
    private String maskedPan;
    private String expiration;
    private String cardholderName;
    private String approvalCode;
    private Boolean chargeback;
    private String paymentSystem;
    private String product;
    private String paymentWay;
}
