package io.swagger.response.payment;

import io.swagger.postgres.model.enums.SubscriptionType;
import lombok.Data;

@Data
public class SubscriptionTypeResponse {

    private SubscriptionType type;
    private String name;
    private Boolean isFree;
    private Double cost;
    private Double documentCost;
    private Integer documentsCount;
    private Integer durationDays;
    private Boolean isInactive;

    public SubscriptionTypeResponse(SubscriptionType type) {
        this.type = type;
        this.name = type.getName();
        this.isFree = type.getFree();
        this.cost = type.getCost();
        this.documentCost = type.getDocumentCost();
        this.documentsCount = type.getDocumentsCount();
        this.durationDays = type.getDurationDays();
        this.isInactive = false;
    }

}
