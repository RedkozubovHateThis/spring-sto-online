package io.swagger.response.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ExecutorResponse {

    private String fullName;
    private Double totalByNorm;
    private Double totalByPrice;
    private Double percent;
    private List<ExecutorDocumentResponse> executorDocumentResponses = new ArrayList<>();

    public ExecutorResponse() {}

    public ExecutorResponse(ExecutorsNativeResponse response) {
        this.fullName = response.getFullName();
        this.percent = response.getPercent();
        this.totalByNorm = response.getTotalByNorm();
        this.totalByPrice = response.getTotalByPrice();
    }

    public void increaseTotalByNorm(Double totalByNorm) {
        if ( totalByNorm == null ) return;

        this.totalByNorm += totalByNorm;
    }

    public void increaseTotalByPrice(Double totalByPrice) {
        if ( totalByPrice == null ) return;

        this.totalByPrice += totalByPrice;
    }

    public void addDocumentResponse(ExecutorsNativeResponse response) {
        executorDocumentResponses.add( new ExecutorDocumentResponse( response, this ) );
    }

    public void addDocumentResponse(ExecutorDocumentResponse response) {
        executorDocumentResponses.add( response );
    }

    public Double getTotalSum() {
        return totalByNorm + totalByPrice;
    }

    public Double getSalary() {
        return getTotalSum() * ( percent / 100 );
    }

    public Map<String, Object> buildReportData() {
        Map<String, Object> reportData = new HashMap<>();

        reportData.put("isBold", true);
        reportData.put("fullName", fullName);
        reportData.put("totalByNorm", totalByNorm);
        reportData.put("totalByPrice", totalByPrice);
        reportData.put("percent", percent);
        reportData.put("totalSum", getTotalSum());
        reportData.put("salary", getSalary());

        return reportData;
    }

}
