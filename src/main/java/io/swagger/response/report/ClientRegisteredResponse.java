package io.swagger.response.report;

import io.swagger.helper.DateHelper;
import io.swagger.postgres.model.ServiceDocument;
import io.swagger.postgres.model.security.Profile;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
public class ClientRegisteredResponse {

    private String name;
    private Date registerDate;

    public ClientRegisteredResponse() {}

    public ClientRegisteredResponse(Profile profile) {
        this.name = profile.getName();
    }

    public Map<String, Object> buildReportData() {
        Map<String, Object> reportData = new HashMap<>();

        reportData.put("isBold", false);
        reportData.put("fullName", String.format("\t%s", name));

        return reportData;
    }

}
