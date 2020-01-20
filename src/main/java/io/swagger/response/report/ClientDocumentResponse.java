package io.swagger.response.report;

import io.swagger.helper.DateHelper;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
public class ClientDocumentResponse {

    private String documentNumber;
    private Date documentDate;
    private Double total;

    public ClientDocumentResponse() {}

    public ClientDocumentResponse(ClientsNativeResponse response) {
        this.documentNumber = response.getFullNumber();
        this.documentDate = response.getDateStart();
        this.total = response.getTotal();
    }

    public Map<String, Object> buildReportData() {
        Map<String, Object> reportData = new HashMap<>();

        reportData.put("isBold", false);
        reportData.put("fullName", String.format("\t№ %s от %s", documentNumber, DateHelper.formatDate(documentDate)));
        reportData.put("total", total);

        return reportData;
    }

}
