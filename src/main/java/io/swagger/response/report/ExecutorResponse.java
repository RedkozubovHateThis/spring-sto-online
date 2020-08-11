package io.swagger.response.report;

import io.swagger.postgres.model.security.Profile;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ExecutorResponse {

    private Long registeredById;
    private String registeredBy;
    private Double total = 0.0;
    private List<ClientRegisteredResponse> clientRegisteredResponses = new ArrayList<>();

    public ExecutorResponse() {}

    public ExecutorResponse(Profile profile) {
        this.registeredById = profile.getId();
        this.registeredBy = profile.getName();
    }

    public ExecutorResponse(String registeredBy) {
        this.registeredBy = registeredBy;
    }

    public void addRegisteredResponse(Profile client) {
        clientRegisteredResponses.add( new ClientRegisteredResponse( client ) );
    }

    public Map<String, Object> buildReportData() {
        Map<String, Object> reportData = new HashMap<>();

        reportData.put("isBold", true);
        reportData.put("fullName", registeredBy);

        return reportData;
    }

}
