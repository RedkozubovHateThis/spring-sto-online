package io.swagger.response.info;

import lombok.Data;

@Data
public class ClientInfo {

    private Integer totalDocuments;
    private Integer totalDone;
    private Integer totalDraft;

    private Double totalRepairSum;
    private Integer totalVehicles;
    private Integer totalServices;

}
