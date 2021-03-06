package io.swagger.response.info;

import lombok.Data;

import java.util.Date;

@Data
public class ServiceLeaderInfo {

    private Long totalDocuments;
    private Long totalDocumentsCreated;
    private Long totalDocumentsInWork;
    private Long totalDocumentsCompleted;

    private Double totalSum;
    private Long totalVehicles;
    private Long totalClients;

    private Double totalBalance;
    private Date adSubscriptionEndDate;
    private Boolean adSubscriptionAvailable;
    private Long operatorSubscriptionEndDate;
    private Boolean operatorSubscriptionAvailable;

    private Integer averageAdView;
    private Integer adEfficiency;

}
