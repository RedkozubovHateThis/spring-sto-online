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

    public ServiceWorkTotalResponse(DocumentOut documentOut) {

        if ( documentOut == null ) return;

        Set<ServiceWork> serviceWorkSet = documentOut.getServiceWorks();

        if ( serviceWorkSet == null || serviceWorkSet.size() == 0 ) return;

        workList = serviceWorkSet.stream().map( serviceWork -> {
            if ( serviceWork.getPriceNorm() != null ) {

                int quantity = serviceWork.getQuantity() != null && serviceWork.getQuantity() > 0 ?
                        serviceWork.getQuantity() : 1;
                double timeValue = serviceWork.getTimeValue() != null && serviceWork.getTimeValue() > 0 ?
                        serviceWork.getTimeValue() : 1;

                workSum += serviceWork.getPriceNorm() * quantity * timeValue;

            }
            return new ServiceWorkResponse(serviceWork);
        } ).collect( Collectors.toList() );

    }

}
