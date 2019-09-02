package io.swagger.response;

import io.swagger.firebird.model.ServiceWork;
import lombok.Data;

@Data
public class ServiceWorkResponse {

    private Integer id;
    private String name;
    private Double price;
    private Double total;
    private Double priceNorm;
    private Integer quantity;
    private Boolean byPrice = false;

    public ServiceWorkResponse(ServiceWork serviceWork) {

        if ( serviceWork == null ) return;

        id = serviceWork.getId();
        name = serviceWork.getName();

        if ( serviceWork.getPriceNorm() != null && serviceWork.getTimeValue() != null ) {
            total = serviceWork.getPriceNorm() * serviceWork.getTimeValue();
        }
        else if ( serviceWork.getPrice() != null ) {
            total = serviceWork.getPrice();
            byPrice = true;
        }

        quantity = serviceWork.getQuantity();
        price = serviceWork.getPrice();
        priceNorm = serviceWork.getPriceNorm();

    }

}
