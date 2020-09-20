package io.swagger.response.integration;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class IntegrationClient extends IntegrationProfile {

    private String lastName;
    private String firstName;
    private String middleName;
    private Boolean clientIsCustomer;

}
