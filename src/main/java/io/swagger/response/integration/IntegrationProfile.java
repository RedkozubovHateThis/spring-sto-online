package io.swagger.response.integration;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class IntegrationProfile extends BaseIntegrationEntity {

    private String name;
    private String inn;
    private String phone;
    private String email;
    private String address;

}
