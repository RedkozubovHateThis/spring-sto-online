package io.swagger.response.report;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ExecutorResponse {

    private String fullName;
    private Double totalByNorm;
    private Double totalByPrice;
    private Double percent;
    private List<ExecutorDocumentResponse> executorDocumentResponses = new ArrayList<>();

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
        executorDocumentResponses.add( new ExecutorDocumentResponse( response ) );
    }

    public Double getTotalSum() {
        return totalByNorm + totalByPrice;
    }

    public Double getSalary() {
        return getTotalSum() * ( percent / 100 );
    }

}
