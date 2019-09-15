package io.swagger.response.firebird;

import io.swagger.firebird.model.ServiceGoodsAddon;
import lombok.Data;

@Data
public class ServiceGoodsAddonResponse {

    private Integer id;
    private String fullName;
    private Double totalCost;
    private Double cost;
    private Integer goodsCount;

    public ServiceGoodsAddonResponse(ServiceGoodsAddon serviceGoodsAddon) {

        if ( serviceGoodsAddon == null ) return;

        id = serviceGoodsAddon.getId();
        fullName = serviceGoodsAddon.getFullName();
        cost = serviceGoodsAddon.getCost();
        totalCost = serviceGoodsAddon.getCost();
        goodsCount = serviceGoodsAddon.getGoodsCount();

        if ( serviceGoodsAddon.getDiscount() != null ) {
            totalCost -= serviceGoodsAddon.getDiscount();
        }
        if ( serviceGoodsAddon.getDiscountFix() != null ) {
            totalCost -= serviceGoodsAddon.getDiscountFix();
        }

    }

}
