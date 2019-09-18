package io.swagger.response.report;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ClientResponse {

    private Integer clientId;
    private String fullName;
    private Double total;
    private List<ClientDocumentResponse> clientDocumentResponses = new ArrayList<>();

    public ClientResponse(ClientsNativeResponse response) {
        this.clientId = response.getClientId();
        this.fullName = response.getFullName();
        this.total = response.getTotal();
    }

    public void addClientResponse(ClientsNativeResponse response) {
        clientDocumentResponses.add( new ClientDocumentResponse( response ) );
    }

    public void increaseTotal(Double total) {
        if ( total == null ) return;

        this.total += total;
    }

}
