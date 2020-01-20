package io.swagger.response.report;

import lombok.Data;

import java.util.Date;

@Data
public class ExecutorsNativeResponse {

    private String fullNumber;
    private String fullName;
    private Integer dsdId;
    private Date dateStart;
    private Double percent;
    private Double totalByNorm;
    private Double totalByPrice;

    public ExecutorsNativeResponse(String FULLNUMBER, String FULL_NAME, Integer DSD_ID, Date DATE_START, Double PERCENT, Double TOTAL_BY_NORM, Double TOTAL_BY_PRICE) {
        this.fullNumber = FULLNUMBER;
        this.fullName = FULL_NAME;
        this.dsdId = DSD_ID;
        this.dateStart = DATE_START;
        this.percent = PERCENT;
        this.totalByNorm = TOTAL_BY_NORM;
        this.totalByPrice = TOTAL_BY_PRICE;
    }
}
