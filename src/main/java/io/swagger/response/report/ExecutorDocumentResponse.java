package io.swagger.response.report;

import lombok.Data;

import java.util.Date;

@Data
public class ExecutorDocumentResponse {

    private Date documentDate;
    private Double totalByNorm;
    private Double totalByPrice;

    public ExecutorDocumentResponse(ExecutorsNativeResponse response) {
        this.documentDate = response.getDateStart();
        this.totalByNorm = response.getTotalByNorm();
        this.totalByPrice = response.getTotalByPrice();
    }

    public Double getTotalSum() {
        return totalByNorm + totalByPrice;
    }

    public Double getSalary(Double percent) {
        return getTotalSum() * ( percent / 100 );
    }

}
