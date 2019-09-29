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
            addonsSum += serviceGoodsAddon.getServiceGoodsCost(true);
            return new ServiceGoodsAddonResponse(serviceGoodsAddon);
        } ).collect( Collectors.toList() );

        documentResponse.incrementSum(addonsSum);

    }

}
