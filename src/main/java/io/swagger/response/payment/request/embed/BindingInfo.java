package io.swagger.response.payment.request.embed;

import lombok.Data;

@Data
public class BindingInfo {
    private String clientId;
    private String bindingId;
    private Long authDateTime;
    private String authRefNum;
    private String terminalId;
}
