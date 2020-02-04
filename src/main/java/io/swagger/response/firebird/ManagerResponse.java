package io.swagger.response.firebird;

import io.swagger.firebird.model.Employee;
import io.swagger.firebird.model.Manager;
import io.swagger.firebird.model.OrganizationStructure;
import lombok.Data;

@Data
public class ManagerResponse {

    private Integer id;
    private String fullName;
    private String shortName;

    public ManagerResponse(Manager manager) {
        OrganizationStructure organizationStructure = manager.getOrganizationStructure();
        if ( organizationStructure == null ) return;

        Employee employee = organizationStructure.getEmployee();
        if ( employee == null ) return;

        id = manager.getId();
        fullName = employee.getFullName();
        shortName = employee.getShortName();
    }

}
