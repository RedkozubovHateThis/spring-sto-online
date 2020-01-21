package io.swagger.response.payment;

import io.swagger.postgres.model.payment.SubscriptionType;
import lombok.Data;

@Data
public class SubscriptionTypeResponse {

    private Long id;
    private String name;
    private Boolean isFree;
    private Double cost;
    private Double documentCost;
    private Integer documentsCount;
    private Integer durationDays;
    private Boolean isInactive;

    public SubscriptionTypeResponse() {}

    public SubscriptionTypeResponse(SubscriptionType type) {
        this.id = type.getId();
        this.name = type.getName();
        this.isFree = type.getIsFree();
        this.cost = type.getCost();
        this.documentCost = type.getDocumentCost();
        this.documentsCount = type.getDocumentsCount();
        this.durationDays = type.getDurationDays();
        this.isInactive = false;
    }

}
