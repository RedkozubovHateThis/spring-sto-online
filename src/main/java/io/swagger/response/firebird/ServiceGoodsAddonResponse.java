package io.swagger.response.firebird;

import io.swagger.firebird.model.ServiceGoodsAddon;
import lombok.Data;

@Data
public class ServiceGoodsAddonResponse {

    private Integer id;
    private String number;
    private String fullName;
    private Double totalCost;
    private Double cost;
    private Integer goodsCount;

    public ServiceGoodsAddonResponse(ServiceGoodsAddon serviceGoodsAddon) {

        if ( serviceGoodsAddon == null ) return;

        id = serviceGoodsAddon.getId();
        number = serviceGoodsAddon.getNumber();
        fullName = serviceGoodsAddon.getFullName();
        cost = serviceGoodsAddon.getCost();
        totalCost = serviceGoodsAddon.getServiceGoodsCost(true);
        goodsCount = serviceGoodsAddon.getGoodsCount();

    }

}
