package io.swagger.response.payment.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RegisterResponse extends BaseResponse {
    private String formUrl;
    private String orderId;
}
