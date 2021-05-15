package io.swagger.response.integration;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class IntegrationBalanceResponse {
    private Double balance;
    private Long operatorExpiresAt;
    private Boolean operatorIsRenewable;
    private String adExpiresAt;
    private Boolean adIsRenewable;
}
