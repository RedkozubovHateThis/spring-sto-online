package io.swagger.response.payment.request.embed;

import lombok.Data;

@Data
public class SecureAuthInfo {
    private Integer eci;
    private String cavv;
    private String xid;
}
