package io.swagger.response.payment;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class PromisedAvailableResponse {

    private Boolean isAvailable;
    private Date availableDate;
    private List<Double> availableCosts = new ArrayList<>();

}
