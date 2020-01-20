package io.swagger.response.report;

import lombok.Data;

import java.util.Date;

@Data
public class ClientsNativeResponse {

    private String fullNumber;
    private Integer clientId;
    private String fullName;
    private Integer dsdId;
    private Date dateStart;
    private Double total;

    public ClientsNativeResponse(String FULLNUMBER, Integer CLIENT_ID, String FULL_NAME, Integer DSD_ID, Date DATE_START, Double TOTAL) {
        this.fullNumber = FULLNUMBER;
        this.clientId = CLIENT_ID;
        this.fullName = FULL_NAME;
        this.dsdId = DSD_ID;
        this.dateStart = DATE_START;
        this.total = TOTAL;
    }
}
