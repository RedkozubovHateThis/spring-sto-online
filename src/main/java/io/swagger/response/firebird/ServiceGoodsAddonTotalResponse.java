package io.swagger.response.firebird;

import io.swagger.firebird.model.DocumentOut;
import io.swagger.firebird.model.ServiceGoodsAddon;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ServiceGoodsAddonTotalResponse {

    private Double addonsSum = 0.0;
    private List<ServiceGoodsAddonResponse> serviceGoodsAddonList;

    public ServiceGoodsAddonTotalResponse(DocumentOut documentOut, DocumentResponse documentResponse) {

        if ( documentOut == null ) return;

        Set<ServiceGoodsAddon> serviceGoodsAddonSet = documentOut.getServiceGoodsAddons();

        if ( serviceGoodsAddonSet == null || serviceGoodsAddonSet.size() == 0 ) return;

        serviceGoodsAddonList = serviceGoodsAddonSet.stream().map( serviceGoodsAddon -> {

            int goodsCount = serviceGoodsAddon.getGoodsCount() != null && serviceGoodsAddon.getGoodsCount() > 0 ?
                    serviceGoodsAddon.getGoodsCount() : 1;

            double totalCost = serviceGoodsAddon.getCost();

            if ( serviceGoodsAddon.getDiscount() != null ) {
                totalCost -= serviceGoodsAddon.getDiscount();
            }
            if ( serviceGoodsAddon.getDiscountFix() != null ) {
                totalCost -= serviceGoodsAddon.getDiscountFix();
            }

            addonsSum += totalCost * goodsCount;

            return new ServiceGoodsAddonResponse(serviceGoodsAddon);
        } ).collect( Collectors.toList() );

        documentResponse.incrementSum(addonsSum);

    }

}
