package io.swagger.response;

import io.swagger.firebird.model.Client;
import io.swagger.helper.DateHelper;
import lombok.Data;

@Data
public class ClientResponse {

    private Integer id;
    private String fullName;

    public ClientResponse(Client client) {
        id = client.getId();
        fullName = client.getFullName();
    }

}
