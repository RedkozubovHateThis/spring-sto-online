package io.swagger.response.report;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ClientResponse {

    private Integer clientId;
    private String fullName;
    private Double total;
    private List<ClientDocumentResponse> clientDocumentResponses = new ArrayList<>();

    public ClientResponse() {}

    public ClientResponse(ClientsNativeResponse response) {
        this.clientId = response.getClientId();
        this.fullName = response.getFullName();
        this.total = response.getTotal();
    }

    public void addClientResponse(ClientsNativeResponse response) {
        clientDocumentResponses.add( new ClientDocumentResponse( response ) );
    }

    public void addClientResponse(ClientDocumentResponse response) {
        clientDocumentResponses.add( response );
    }

    public void increaseTotal(Double total) {
        if ( total == null ) return;

        this.total += total;
    }

    public Map<String, Object> buildReportData() {
        Map<String, Object> reportData = new HashMap<>();

        reportData.put("isBold", true);
        reportData.put("fullName", fullName);
        reportData.put("total", total);

        return reportData;
    }

}
