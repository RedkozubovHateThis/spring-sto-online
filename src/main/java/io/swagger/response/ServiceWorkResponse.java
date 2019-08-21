package io.swagger.response;

import io.swagger.firebird.model.ServiceWork;
import lombok.Data;

@Data
public class ServiceWorkResponse {

    private String name;
    private Double price;
    private Integer quantity;

    public ServiceWorkResponse(ServiceWork serviceWork) {

        if ( serviceWork == null ) return;

        name = serviceWork.getName();

        if ( serviceWork.getPriceNorm() == null ) return;
        double timeValue = serviceWork.getTimeValue() != null && serviceWork.getTimeValue() > 0 ?
                serviceWork.getTimeValue() : 1;

        price = serviceWork.getPriceNorm() * timeValue;
        quantity = serviceWork.getQuantity();

    }

}
