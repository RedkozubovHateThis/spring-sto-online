package io.swagger.response.payment.request;

import io.swagger.response.payment.request.embed.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExtendedResponse extends BaseResponse {
    private String orderNumber;
    private Integer orderStatus;
    private Integer actionCode;
    private String actionCodeDescription;
    private Integer amount;
    private String currency;
    private Long date;
    private String orderDescription;
    private String ip;
    private List<Attribute> attributes;
    private List<MerchantOrderParam> merchantOrderParams;
    private CardAuthInfo cardAuthInfo;
    private SecureAuthInfo secureAuthInfo;
    private BindingInfo bindingInfo;
    private PaymentAmountInfo paymentAmountInfo;
    private BankInfo bankInfo;
}
