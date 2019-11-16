package io.swagger.response.firebird;

import io.swagger.firebird.model.ServiceWork;
import lombok.Data;

@Data
public class ServiceWorkResponse {

    private Integer id;
    private String number;
    private String name;
    private Double price;
    private Double total;
    private Double priceNorm;
    private Integer quantity;
    private Boolean byPrice = false;

    public ServiceWorkResponse(ServiceWork serviceWork) {

        if ( serviceWork == null ) return;

        id = serviceWork.getId();
        number = serviceWork.getNumber();
        name = serviceWork.getName();
        total = serviceWork.getServiceWorkTotalCost(true);
        byPrice = serviceWork.isByPrice();
        quantity = serviceWork.getQuantity();
        price = serviceWork.getPrice();
        priceNorm = serviceWork.getPriceNorm();

    }

}
