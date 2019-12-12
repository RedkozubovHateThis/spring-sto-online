package io.swagger.response.payment.request;

import lombok.Data;

@Data
public class BaseResponse {
    private Integer errorCode;
    private String errorMessage;
}
