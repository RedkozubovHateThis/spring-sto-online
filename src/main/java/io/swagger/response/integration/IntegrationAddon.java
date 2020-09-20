package io.swagger.response.integration;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class IntegrationAddon extends BaseIntegrationEntity {

    private String number;
    private String name;
    private Double cost;
    private Integer count;

}
