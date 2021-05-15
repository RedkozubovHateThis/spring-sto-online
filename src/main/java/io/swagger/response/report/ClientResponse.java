package io.swagger.response.report;

import io.swagger.postgres.model.ServiceDocument;
import io.swagger.postgres.model.Vehicle;
import io.swagger.postgres.model.security.Profile;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ClientResponse {

    private Long clientId;
    private String fullName;
    private Double total = 0.0;
    private List<ClientDocumentResponse> clientDocumentResponses = new ArrayList<>();

    public ClientResponse() {}

    public ClientResponse(Profile profile) {
        this.clientId = profile.getId();
        this.fullName = profile.getName();
    }

    public ClientResponse(Vehicle vehicle) {
        this.clientId = vehicle.getId();
        this.fullName = vehicle.getModelName() + ": " + vehicle.getVinNumber();
    }

    public void addClientResponse(ServiceDocument serviceDocument) {
        if ( serviceDocument.getCost() != null )
            this.total += serviceDocument.getCost();
        clientDocumentResponses.add( new ClientDocumentResponse( serviceDocument ) );
    }

    public void addClientResponse(ClientDocumentResponse clientDocumentResponse) {
        if ( clientDocumentResponse.getTotal() != null )
            this.total += clientDocumentResponse.getTotal();
        clientDocumentResponses.add( clientDocumentResponse );
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
