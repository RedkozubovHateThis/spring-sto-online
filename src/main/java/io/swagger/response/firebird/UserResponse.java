package io.swagger.response.firebird;

import io.swagger.firebird.model.Client;
import io.swagger.firebird.model.Employee;
import io.swagger.firebird.model.User;
import lombok.Data;

@Data
public class UserResponse {

    private Integer id;
    private String fullName;
    private String shortName;

    public UserResponse(User user) throws IllegalArgumentException {
        id = user.getId();

        Employee employee = user.getEmployee();
        if ( employee == null ) throw new IllegalArgumentException("Employee can not be null");

        fullName = employee.getFullName();
        shortName = employee.getShortName();
    }

}
