package io.swagger.response;

import io.swagger.firebird.model.Organization;
import lombok.Data;

@Data
public class OrganizationResponse {

    private Integer id;
    private String fullName;

    public OrganizationResponse(Organization client) {
        id = client.getId();
        fullName = client.getFullName();
    }

}
