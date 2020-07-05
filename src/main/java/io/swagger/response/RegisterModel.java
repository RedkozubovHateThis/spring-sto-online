package io.swagger.response;

import lombok.Data;

@Data
public class RegisterModel {
    private String firstName;
    private String lastName;
    private String middleName;
    private String name;
    private String address;
    private String email;
    private String phone;
    private String inn;
    private String password;
    private String rePassword;
}
