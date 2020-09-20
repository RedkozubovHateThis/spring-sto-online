package io.swagger.response.integration;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class IntegrationWork extends BaseIntegrationEntity {

    private String number;
    private String name;
    private Boolean byPrice;
    private Double timeValue;
    private Double priceNorm;
    private Double price;
    private Integer count;

}
