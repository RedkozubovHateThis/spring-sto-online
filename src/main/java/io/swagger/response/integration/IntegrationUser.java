package io.swagger.response.integration;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class IntegrationUser extends BaseIntegrationEntity {
    private String fullName;
    private String firstName;
    private String lastName;
    private String middleName;
    private String address;
    private String email;
    private String phone;
    private String inn;
    private String bankBic;
    private String bankName;
    private String checkingAccount;
    private String corrAccount;
    private String role;
    private String password;
}
