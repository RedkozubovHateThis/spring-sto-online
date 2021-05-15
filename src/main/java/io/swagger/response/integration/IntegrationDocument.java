package io.swagger.response.integration;

import io.swagger.postgres.model.enums.ServiceDocumentPaidStatus;
import io.swagger.postgres.model.enums.ServiceDocumentStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class IntegrationDocument extends BaseIntegrationEntity {

    private String number;
    private Double cost;
    private String status;
    private String paidStatus;
    private String startDate;
    private String endDate;
    private String masterFio;
    private String modelName;
    private String regNumber;
    private String vinNumber;
    private String vehicleIntegrationId;
    private Integer year;
    private Integer mileage;
    private String reason;

    private IntegrationClient client;
    private IntegrationProfile customer;
    private IntegrationProfile executor;

    private List<IntegrationWork> works;
    private List<IntegrationAddon> addons;

}
