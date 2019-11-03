package io.swagger.response.firebird;

import io.swagger.firebird.model.Client;
import lombok.Data;

@Data
public class ClientResponse {

    private Integer id;
    private String fullName;
    private String shortName;

    public ClientResponse(Client client) {
        id = client.getId();
        fullName = client.getFullName();
        shortName = client.getShortName();
    }

}
