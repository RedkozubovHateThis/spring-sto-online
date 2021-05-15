package io.swagger.response.integration;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class IntegrationBalanceRequest extends BaseIntegrationEntity {
    private String phone;
    private String email;
}
