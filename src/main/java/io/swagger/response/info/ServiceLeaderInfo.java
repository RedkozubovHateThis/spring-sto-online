package io.swagger.response.info;

import lombok.Data;

import java.util.Date;

@Data
public class ServiceLeaderInfo {

    private Integer documentsRemains;
    private Integer totalDocuments;

    private String subscribeName;
    private Date subscribeEndDate;

    private Double balance;
    private Boolean balanceValid;

    private String moderatorFio;
    private String serviceName;

}
