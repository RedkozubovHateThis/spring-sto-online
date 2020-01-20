package io.swagger.response.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.helper.DateHelper;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
public class ExecutorDocumentResponse {

    private String documentNumber;
    private Date documentDate;
    private Double totalByNorm;
    private Double totalByPrice;
    private Double percent;

    public ExecutorDocumentResponse() {}

    public ExecutorDocumentResponse(ExecutorsNativeResponse response, ExecutorResponse executorResponse) {
        this.documentNumber = response.getFullNumber();
        this.documentDate = response.getDateStart();
        this.totalByNorm = response.getTotalByNorm();
        this.totalByPrice = response.getTotalByPrice();
        this.percent = executorResponse.getPercent();
    }

    public Double getTotalSum() {
        return totalByNorm + totalByPrice;
    }

    public Double getSalary() {
        return getTotalSum() * ( percent / 100 );
    }

    public Map<String, Object> buildReportData() {
        Map<String, Object> reportData = new HashMap<>();

        reportData.put("isBold", false);
        reportData.put("fullName", String.format("\t№ %s от %s", documentNumber, DateHelper.formatDate(documentDate)));
        reportData.put("totalByNorm", totalByNorm);
        reportData.put("totalByPrice", totalByPrice);
        reportData.put("percent", percent);
        reportData.put("totalSum", getTotalSum());
        reportData.put("salary", getSalary());

        return reportData;
    }

}
