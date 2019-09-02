package io.swagger.response;

import io.swagger.firebird.model.DocumentOut;
import io.swagger.firebird.model.ServiceWork;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ServiceWorkTotalResponse {

    private Double workSum = 0.0;
    private List<ServiceWorkResponse> workList;

    public ServiceWorkTotalResponse(DocumentOut documentOut, DocumentResponse documentResponse) {

        if ( documentOut == null ) return;

        Set<ServiceWork> serviceWorkSet = documentOut.getServiceWorks();

        if ( serviceWorkSet == null || serviceWorkSet.size() == 0 ) return;

        workList = serviceWorkSet.stream().map( serviceWork -> {
            if ( serviceWork.getPriceNorm() != null && serviceWork.getTimeValue() != null ) {

                int quantity = serviceWork.getQuantity() != null && serviceWork.getQuantity() > 0 ?
                        serviceWork.getQuantity() : 1;

                workSum += serviceWork.getPriceNorm() * quantity * serviceWork.getTimeValue();

            }
            else if ( serviceWork.getPrice() != null ) {

                int quantity = serviceWork.getQuantity() != null && serviceWork.getQuantity() > 0 ?
                        serviceWork.getQuantity() : 1;

                workSum += serviceWork.getPrice() * quantity;

            }
            return new ServiceWorkResponse(serviceWork);
        } ).collect( Collectors.toList() );

        documentResponse.incrementSum(workSum);

    }

}
