package io.swagger.response.payment;

import lombok.Data;

import java.util.Date;

@Data
public class PromisedAvailableResponse {

    private Boolean isAvailable;
    private Date availableDate;

}
